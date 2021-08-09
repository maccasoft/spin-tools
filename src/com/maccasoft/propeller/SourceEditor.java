/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.EditorTokenMarker.TokenId;
import com.maccasoft.propeller.EditorTokenMarker.TokenMarker;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ContentProposalAdapter;
import com.maccasoft.propeller.internal.HTMLStyledTextParser;
import com.maccasoft.propeller.internal.StyledTextContentAdapter;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;

public class SourceEditor {

    private static final int UNDO_LIMIT = 500;
    private static final int CURRENT_CHANGE_TIMER_EXPIRE = 500;

    Display display;
    Composite container;
    LineNumbersRuler ruler;
    StyledText styledText;

    Font font;
    Font fontBold;
    Font fontItalic;
    Font fontBoldItalic;

    int currentLine;
    Color currentLineBackground;

    Caret insertCaret;
    Caret overwriteCaret;
    boolean modified;

    EditorTokenMarker tokenMarker;
    Map<TokenId, TextStyle> styleMap = new HashMap<TokenId, TextStyle>();

    SpinEditorBackgroundDecorator backgroundDecorator;
    EditorHelp helpProvider;

    boolean ignoreUndo;
    boolean ignoreRedo;
    TextChange currentChange;
    Stack<TextChange> undoStack = new Stack<TextChange>();
    Stack<TextChange> redoStack = new Stack<TextChange>();

    boolean ignoreModify;
    ListenerList<ModifyListener> modifyListeners = new ListenerList<ModifyListener>();

    class TextChange {

        int caretOffset;
        int start;
        int length;
        String replacedText;
        long timeStamp;

        TextChange(int start, int length, String replacedText, int caretOffset) {
            this.start = start;
            this.length = length;
            this.replacedText = replacedText;
            this.timeStamp = System.currentTimeMillis();
            this.caretOffset = caretOffset;
        }

        void append(int length, String replacedText) {
            this.length += length;
            this.replacedText += replacedText;
            this.timeStamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return (System.currentTimeMillis() - timeStamp) >= CURRENT_CHANGE_TIMER_EXPIRE;
        }
    }

    private final CaretListener caretListener = new CaretListener() {

        @Override
        public void caretMoved(CaretEvent event) {
            int line = styledText.getLineAtOffset(event.caretOffset);
            if (line != currentLine) {
                Rectangle r = styledText.getClientArea();
                if (currentLine != -1) {
                    styledText.redraw(0, styledText.getLinePixel(currentLine), r.width, styledText.getLineHeight(), false);
                }
                currentLine = line;
                styledText.redraw(0, styledText.getLinePixel(currentLine), r.width, styledText.getLineHeight(), false);
            }
        }
    };

    final Runnable refreshViewRunnable = new Runnable() {

        @Override
        public void run() {
            if (styledText == null || styledText.isDisposed()) {
                return;
            }
            styledText.redraw();
        }
    };

    Shell popupWindow;
    Rectangle popupMouseBounds;

    public SourceEditor(Composite parent) {
        display = parent.getDisplay();

        container = new Composite(parent, SWT.NO_FOCUS);
        GridLayout containerLayout = new GridLayout(2, false);
        containerLayout.horizontalSpacing = 1;
        containerLayout.marginWidth = containerLayout.marginHeight = 0;
        container.setLayout(containerLayout);

        if ("win32".equals(SWT.getPlatform())) {
            font = new Font(display, "Courier New", 10, SWT.NONE);
            fontBold = new Font(display, "Courier New", 10, SWT.BOLD);
            fontItalic = new Font(display, "Courier New", 10, SWT.ITALIC);
            fontBoldItalic = new Font(display, "Courier New", 10, SWT.BOLD | SWT.ITALIC);
        }
        else {
            font = new Font(display, "mono", 10, SWT.NONE);
            fontBold = new Font(display, "mono", 10, SWT.BOLD);
            fontItalic = new Font(display, "mono", 10, SWT.ITALIC);
            fontBoldItalic = new Font(display, "mono", 10, SWT.BOLD | SWT.ITALIC);
        }

        currentLine = 0;
        currentLineBackground = new Color(display, 0xE8, 0xF2, 0xFE);

        styleMap.put(TokenId.COMMENT, new TextStyle(font, ColorRegistry.getColor(0x7E, 0x7E, 0x7E), null));
        styleMap.put(TokenId.SECTION, new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0xA0), null));

        styleMap.put(TokenId.NUMBER, new TextStyle(font, ColorRegistry.getColor(0x00, 0x66, 0x99), null));
        styleMap.put(TokenId.STRING, new TextStyle(font, ColorRegistry.getColor(0x7E, 0x00, 0x7E), null));
        styleMap.put(TokenId.CONSTANT, new TextStyle(font, ColorRegistry.getColor(0x7E, 0x00, 0x7E), null));

        styleMap.put(TokenId.METHOD_PUB, new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0xA0), null));
        styleMap.put(TokenId.METHOD_PRI, new TextStyle(fontBoldItalic, ColorRegistry.getColor(0x00, 0x00, 0xA0), null));
        styleMap.put(TokenId.METHOD_LOCAL, new TextStyle(font, ColorRegistry.getColor(0x80, 0x80, 0x00), null));
        styleMap.put(TokenId.METHOD_RETURN, new TextStyle(font, ColorRegistry.getColor(0x90, 0x00, 0x00), null));

        styleMap.put(TokenId.TYPE, new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0x00), null));
        styleMap.put(TokenId.KEYWORD, new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x80, 0x00), null));
        styleMap.put(TokenId.FUNCTION, new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0x00), null));

        styleMap.put(TokenId.PASM_LOCAL_LABEL, new TextStyle(fontItalic, ColorRegistry.getColor(0x00, 0x00, 0x00), null));
        styleMap.put(TokenId.PASM_CONDITION, new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0x00), null));
        styleMap.put(TokenId.PASM_INSTRUCTION, new TextStyle(fontBold, ColorRegistry.getColor(0x80, 0x00, 0x00), null));
        styleMap.put(TokenId.PASM_MODIFIER, new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0x00), null));

        ruler = new LineNumbersRuler(container);
        ruler.setFont(font);

        styledText = new StyledText(container, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL) {

            @Override
            public void invokeAction(int action) {
                switch (action) {
                    case ST.LINE_START:
                        SourceEditor.this.doLineStart(false);
                        break;
                    case ST.LINE_END:
                        SourceEditor.this.doLineEnd(false);
                        break;
                    case ST.SELECT_LINE_START:
                        SourceEditor.this.doLineStart(true);
                        break;
                    case ST.SELECT_LINE_END:
                        SourceEditor.this.doLineEnd(true);
                        break;
                    case ST.PASTE:
                        SourceEditor.this.paste();
                        break;
                    default:
                        super.invokeAction(action);
                }
            }

        };
        styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        styledText.setMargins(5, 5, 5, 5);
        styledText.setTabs(8);
        styledText.setFont(font);

        ruler.setText(styledText);

        insertCaret = new Caret(styledText, SWT.NULL);
        insertCaret.setSize(2, styledText.getLineHeight());
        insertCaret.setFont(font);

        GC gc = new GC(styledText);
        Point charSize = gc.stringExtent("A"); //$NON-NLS-1$
        gc.dispose();

        overwriteCaret = new Caret(styledText, SWT.NULL);
        overwriteCaret.setSize(charSize.x, styledText.getLineHeight());
        overwriteCaret.setFont(styledText.getFont());

        styledText.setCaret(insertCaret);
        styledText.addCaretListener(caretListener);

        styledText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!ignoreModify) {
                    Object[] l = modifyListeners.getListeners();
                    for (int i = 0; i < l.length; i++) {
                        ((ModifyListener) l[i]).modifyText(e);
                    }
                }
            }
        });

        styledText.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                String replacedText = styledText.getTextRange(e.start, e.end - e.start);
                if (!ignoreUndo) {
                    if (currentChange == null || currentChange.isExpired()) {
                        undoStack.push(currentChange = new TextChange(e.start, e.text.length(), replacedText, styledText.getCaretOffset()));
                        if (undoStack.size() > UNDO_LIMIT) {
                            undoStack.remove(0);
                        }
                    }
                    else {
                        if (e.start != currentChange.start + currentChange.length) {
                            undoStack.push(currentChange = new TextChange(e.start, e.text.length(), replacedText, styledText.getCaretOffset()));
                            if (undoStack.size() > UNDO_LIMIT) {
                                undoStack.remove(0);
                            }
                        }
                        else {
                            currentChange.append(e.text.length(), replacedText);
                        }
                    }
                }
                else if (!ignoreRedo) {
                    redoStack.push(new TextChange(e.start, e.text.length(), replacedText, styledText.getCaretOffset()));
                    if (redoStack.size() > UNDO_LIMIT) {
                        redoStack.remove(0);
                    }
                }
            }
        });

        styledText.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.character != SWT.TAB) {
                    return;
                }
                e.doit = false;
                if ((e.stateMask & SWT.CTRL) != 0) {
                    Event event = new Event();
                    event.character = e.character;
                    event.stateMask = e.stateMask;
                    event.detail = e.detail;
                    e.display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            if (!styledText.isDisposed()) {
                                Control control = styledText.getParent();
                                while (!(control instanceof CTabFolder) && control.getParent() != null) {
                                    control = control.getParent();
                                }
                                control.notifyListeners(SWT.Traverse, event);
                            }
                        }
                    });
                }
            }
        });
        styledText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.INSERT && e.stateMask == 0) {
                    styledText.setCaret(styledText.getCaret() == insertCaret ? overwriteCaret : insertCaret);
                }
            }
        });
        styledText.addVerifyKeyListener(new VerifyKeyListener() {

            @Override
            public void verifyKey(VerifyEvent e) {
                if (e.keyCode == SWT.CR) {
                    doAutoIndent();
                    e.doit = false;
                }
                else if (e.keyCode == SWT.TAB) {
                    e.doit = false;
                    if ((e.stateMask & SWT.CTRL) != 0) {
                        return;
                    }
                    if ((e.stateMask & SWT.SHIFT) != 0) {
                        doBacktab();

                    }
                    else {
                        doTab();
                    }
                }
            }
        });

        styledText.getContent().addTextChangeListener(new TextChangeListener() {

            @Override
            public void textSet(TextChangedEvent event) {

            }

            @Override
            public void textChanging(TextChangingEvent event) {
                modified = true;
            }

            @Override
            public void textChanged(TextChangedEvent event) {

            }
        });

        styledText.addLineStyleListener(new LineStyleListener() {

            @Override
            public void lineGetStyle(LineStyleEvent event) {

                if (tokenMarker != null) {
                    if (modified) {
                        try {
                            tokenMarker.refreshTokens(styledText.getText());
                            display.timerExec(500, refreshViewRunnable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        modified = false;
                    }

                    List<StyleRange> ranges = new ArrayList<StyleRange>();

                    try {
                        for (TokenMarker entry : tokenMarker.getLineTokens(event.lineOffset, event.lineText)) {
                            TextStyle style = styleMap.get(entry.getId());
                            if (style != null) {
                                StyleRange range = new StyleRange(style);
                                range.start = entry.getStart();
                                range.length = entry.getStop() - range.start + 1;
                                ranges.add(range);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    event.styles = ranges.toArray(new StyleRange[ranges.size()]);
                }
            }
        });

        backgroundDecorator = new SpinEditorBackgroundDecorator();
        styledText.addLineBackgroundListener(new LineBackgroundListener() {

            @Override
            public void lineGetBackground(LineBackgroundEvent event) {
                event.lineBackground = backgroundDecorator.getLineBackground(tokenMarker.getRoot(), event.lineOffset);
                if (styledText.getLineAtOffset(event.lineOffset) == currentLine) {
                    if (event.lineBackground != null) {
                        event.lineBackground = ColorRegistry.getDimColor(event.lineBackground, -6);
                    }
                    else {
                        event.lineBackground = currentLineBackground;
                    }
                }
            }
        });

        styledText.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (popupWindow == null) {
                    return;
                }
                if (popupMouseBounds != null && !popupMouseBounds.contains(e.x, e.y)) {
                    popupWindow.dispose();
                    popupWindow = null;
                    popupMouseBounds = null;
                }
            }
        });

        styledText.addMouseTrackListener(new MouseTrackListener() {

            Token token;

            @Override
            public void mouseHover(MouseEvent e) {
                if (popupWindow != null) {
                    return;
                }

                int offset = styledText.getOffsetAtPoint(new Point(e.x, e.y));

                token = tokenMarker.getTokenAt(offset);
                if (token == null) {
                    return;
                }

                popupMouseBounds = styledText.getTextBounds(token.start, token.stop);
                popupMouseBounds.x -= 5;
                popupMouseBounds.y -= 5;
                popupMouseBounds.width += 10;
                popupMouseBounds.height += 10;

                Node context = tokenMarker.getContextAt(offset);
                TokenMarker marker = tokenMarker.getMarkerAtOffset(offset);
                Rectangle bounds = display.map(styledText, null, styledText.getTextBounds(token.start, token.stop));

                if (marker != null && marker.getError() != null) {
                    popupWindow = new Shell(styledText.getShell(), SWT.NO_FOCUS | SWT.ON_TOP);
                    FillLayout layout = new FillLayout();
                    layout.marginHeight = layout.marginWidth = 5;
                    popupWindow.setLayout(layout);
                    Label content = new Label(popupWindow, SWT.NONE);
                    content.setText(marker.getError());
                    popupWindow.pack();

                    Point size = popupWindow.getSize();
                    popupWindow.setLocation(bounds.x, bounds.y - size.y - 3);

                    popupWindow.open();
                    return;
                }

                if (token != null) {
                    String text = helpProvider.getString(context != null ? context.getClass().getSimpleName() : null, token.getText().toLowerCase());
                    if (text == null) {
                        text = tokenMarker.getMethod(token.getText());
                    }
                    if (text != null) {
                        popupWindow = new Shell(styledText.getShell(), SWT.NO_FOCUS | SWT.ON_TOP);
                        FillLayout layout = new FillLayout();
                        layout.marginHeight = layout.marginWidth = 5;
                        popupWindow.setLayout(layout);

                        StyledText content = new StyledText(popupWindow, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
                        content.setCaret(null);
                        new HTMLStyledTextParser(content).setText(text);

                        popupWindow.pack();

                        bounds.y += bounds.height + 3;
                        if (bounds.width < 640) {
                            bounds.width = 640;
                        }
                        if (bounds.height < 240) {
                            bounds.height = 240;
                        }

                        popupWindow.setBounds(bounds);
                        popupWindow.open();
                    }
                }

            }

            @Override
            public void mouseExit(MouseEvent e) {

            }

            @Override
            public void mouseEnter(MouseEvent e) {

            }
        });

        KeyStroke keyStroke = null;
        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentProposalAdapter adapter = new ContentProposalAdapter(
            styledText,
            new StyledTextContentAdapter(),
            new IContentProposalProvider() {

                @Override
                public IContentProposal[] getProposals(String contents, int position) {
                    return computeProposals(contents, position);
                }

            },
            keyStroke,
            new char[] {
                '.', '#', '@'
            });
        adapter.setPopupSize(new Point(200, 300));
        adapter.setPropagateKeys(true);
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

        styledText.addVerifyKeyListener(new VerifyKeyListener() {

            @Override
            public void verifyKey(VerifyEvent e) {
                try {
                    KeyStroke k = KeyStroke.getInstance("Enter");
                    if (k.getNaturalKey() == e.keyCode && adapter.isProposalPopupOpen()) {
                        e.doit = false;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        styledText.addListener(SWT.Paint, new Listener() {

            Rectangle clientArea;

            @Override
            public void handleEvent(Event event) {
                GC gc = event.gc;
                try {
                    clientArea = styledText.getClientArea();
                    clientArea.y += styledText.getTopMargin();
                    clientArea.height -= styledText.getTopMargin() + styledText.getBottomMargin();

                    gc.setAdvanced(true);
                    gc.setAntialias(SWT.OFF);
                    gc.setLineWidth(1);
                    gc.setClipping(clientArea);

                    gc.setForeground(ColorRegistry.getColor(0xA0, 0xA0, 0xA0));

                    Node root = tokenMarker.getRoot();
                    if (root != null) {
                        for (Node child : root.getChilds()) {
                            if (child instanceof MethodNode) {
                                paintBlock(gc, child, 0, -1);
                            }
                        }
                    }

                    for (TokenMarker entry : tokenMarker.getCompilerTokens()) {
                        Rectangle r = styledText.getTextBounds(entry.getStart(), entry.getStop());
                        int[] polyline = computePolyline(new Point(r.x, r.y), new Point(r.x + r.width, r.y), styledText.getLineHeight());
                        if (entry.id == TokenId.ERROR) {
                            gc.setForeground(ColorRegistry.getColor(0xC0, 0x00, 0x00));
                        }
                        else {
                            gc.setForeground(ColorRegistry.getColor(0xFC, 0xAF, 0x3E));
                        }
                        gc.drawPolyline(polyline);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            void paintBlock(GC gc, Node node, int level, int yref) {
                List<Node> childs = node.getChilds();

                Rectangle r = styledText.getTextBounds(node.getStartToken().start, node.getStartToken().start);
                int x0 = r.x + r.width / 2;
                int y0 = r.y + r.height;

                if (y0 >= clientArea.height || childs.size() == 0) {
                    return;
                }

                Rectangle r1 = styledText.getTextBounds(childs.get(childs.size() - 1).getStartToken().start, childs.get(childs.size() - 1).getStartToken().start);
                if (r1.y < clientArea.y) {
                    return;
                }

                for (Node child : childs) {
                    if (!(child instanceof StatementNode) && !(child instanceof DataLineNode)) {
                        continue;
                    }
                    int y1 = -1;
                    if (level > 0) {
                        r = styledText.getTextBounds(child.getStartToken().start, child.getStartToken().start);
                        y1 = r.y + r.height / 2;
                        if (y1 != y0 && y1 >= 0 && y1 != yref) {
                            int x1 = r.x - r.width / 2;
                            gc.drawLine(x0, y0, x0, y1);
                            gc.drawLine(x0, y1, x1, y1);
                            y0 = y1;
                        }
                        if (y0 >= clientArea.height) {
                            break;
                        }
                    }
                    if (child instanceof StatementNode) {
                        paintBlock(gc, child, level + 1, y1);
                    }
                }
            }

            private int[] computePolyline(Point left, Point right, int height) {

                final int WIDTH = 4; // must be even
                final int HEIGHT = 2; // can be any number

                int peeks = (right.x - left.x) / WIDTH;

                int leftX = left.x;

                // compute (number of point) * 2
                int length = ((2 * peeks) + 1) * 2;
                if (length < 0) {
                    return new int[0];
                }

                int[] coordinates = new int[length];

                // cache peeks' y-coordinates
                int bottom = left.y + height - 1;
                int top = bottom - HEIGHT;

                // populate array with peek coordinates
                for (int i = 0; i < peeks; i++) {
                    int index = 4 * i;
                    coordinates[index] = leftX + (WIDTH * i);
                    coordinates[index + 1] = bottom;
                    coordinates[index + 2] = coordinates[index] + WIDTH / 2;
                    coordinates[index + 3] = top;
                }

                // the last down flank is missing
                coordinates[length - 2] = left.x + (WIDTH * peeks);
                coordinates[length - 1] = bottom;

                return coordinates;
            }

        });

        styledText.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                font.dispose();
                fontBold.dispose();
                fontItalic.dispose();
                fontBoldItalic.dispose();
                currentLineBackground.dispose();
                for (TextStyle style : styleMap.values()) {
                    if (style.foreground != null) {
                        style.foreground.dispose();
                    }
                    if (style.background != null) {
                        style.background.dispose();
                    }
                }
                insertCaret.dispose();
                overwriteCaret.dispose();
            }
        });
    }

    public void setTokenMarker(EditorTokenMarker tokenMarker) {
        this.tokenMarker = tokenMarker;
        modified = true;
        styledText.redraw();
    }

    public void setHelpProvider(EditorHelp helpProvider) {
        this.helpProvider = helpProvider;
    }

    public void addModifyListener(ModifyListener l) {
        modifyListeners.add(l);
    }

    public void removeModifyListener(ModifyListener l) {
        modifyListeners.remove(l);
    }

    public Control getControl() {
        return container;
    }

    public StyledText getStyledText() {
        return styledText;
    }

    public void dispose() {
        container.dispose();
    }

    public void setText(String text) {
        styledText.setRedraw(false);
        styledText.removeCaretListener(caretListener);
        try {
            text = text.replaceAll("[ \\t]+(\r\n|\n|\r)", "$1");
            text = replaceTabs(text, styledText.getTabs());

            ignoreModify = true;
            currentLine = 0;
            styledText.setText(text);
            undoStack.clear();
            redoStack.clear();

            modified = true;
            ignoreModify = false;

        } finally {
            styledText.setRedraw(true);
            styledText.addCaretListener(caretListener);
        }
    }

    String replaceTabs(String text, int tabs) {
        final StringBuilder spaces = new StringBuilder(tabs);
        for (int i = 0; i < tabs; i++) {
            spaces.append(' ');
        }

        int i = 0;
        while ((i = text.indexOf('\t', i)) != -1) {
            int s = i;
            while (s > 0) {
                s--;
                if (text.charAt(s) == '\r' || text.charAt(s) == '\n') {
                    s++;
                    break;
                }
            }
            int n = ((i - s) % tabs);
            text = text.substring(0, i) + spaces.substring(0, tabs - n) + text.substring(i + 1);
        }

        return text;
    }

    public String getText() {
        String text = styledText.getText();
        String result = text.replaceAll("[ \\t]+(\r\n|\n|\r)", "$1");
        if (!result.equals(text)) {
            int offset = styledText.getCaretOffset();
            int line = styledText.getLineAtOffset(offset);
            int column = offset - styledText.getOffsetAtLine(line);
            int topindex = styledText.getTopIndex();

            styledText.setRedraw(false);
            ignoreModify = true;
            try {
                styledText.setText(result);
                styledText.setTopIndex(topindex);

                String ls = styledText.getLine(line);
                styledText.setCaretOffset(styledText.getOffsetAtLine(line) + Math.min(column, ls.length()));
            } finally {
                styledText.setRedraw(true);
                ignoreModify = false;
            }
        }
        return result;
    }

    public void undo() {
        if (undoStack.empty()) {
            return;
        }

        TextChange change = undoStack.pop();

        ignoreUndo = true;
        try {
            styledText.setRedraw(false);
            styledText.replaceTextRange(change.start, change.length, change.replacedText);
            styledText.setCaretOffset(change.caretOffset);
            styledText.setSelection(change.caretOffset, change.caretOffset);
            if (redoStack.size() != 0) {
                redoStack.peek().caretOffset = change.caretOffset;
            }
        } finally {
            styledText.setRedraw(true);
            ignoreUndo = false;
        }
    }

    public void redo() {
        if (redoStack.empty()) {
            return;
        }

        TextChange change = redoStack.pop();

        ignoreRedo = true;
        try {
            styledText.setRedraw(false);
            styledText.replaceTextRange(change.start, change.length, change.replacedText);
            styledText.setCaretOffset(change.caretOffset);
            styledText.setSelection(change.caretOffset, change.caretOffset);
        } finally {
            styledText.setRedraw(true);
            ignoreRedo = false;
        }
    }

    public void cut() {
        styledText.cut();
    }

    public void copy() {
        styledText.copy();
    }

    public void paste() {
        styledText.paste();
    }

    public void selectAll() {
        styledText.selectAll();
    }

    void doLineStart(boolean doSelect) {
        int offset = styledText.getCaretOffset();
        int lineNumber = styledText.getLineAtOffset(offset);
        int lineOffset = offset - styledText.getOffsetAtLine(lineNumber);
        String line = styledText.getLine(lineNumber);
        int nonBlankOffset = 0;
        while (nonBlankOffset < line.length() && (line.charAt(nonBlankOffset) == ' ' || line.charAt(nonBlankOffset) == '\t')) {
            nonBlankOffset++;
        }
        if (lineOffset == nonBlankOffset) {
            lineOffset = 0;
        }
        else {
            lineOffset = nonBlankOffset;
        }

        styledText.setRedraw(false);
        try {
            int newOffset = lineOffset + styledText.getOffsetAtLine(lineNumber);
            if (doSelect) {
                Point selection = styledText.getSelection();
                if (offset == selection.x) {
                    styledText.setSelection(selection.y, newOffset);
                }
                else if (offset == selection.y) {
                    styledText.setSelection(selection.x, newOffset);
                }
                else {
                    styledText.setSelection(offset, newOffset);
                }
            }
            styledText.setCaretOffset(newOffset);
            styledText.setHorizontalIndex(0);
            styledText.showSelection();
        } finally {
            styledText.setRedraw(true);
        }
    }

    void doLineEnd(boolean doSelect) {
        int offset = styledText.getCaretOffset();
        int lineNumber = styledText.getLineAtOffset(offset);
        int lineOffset = offset - styledText.getOffsetAtLine(lineNumber);
        String line = styledText.getLine(lineNumber);
        int nonBlankOffset = line.length();
        while (nonBlankOffset > 0 && (line.charAt(nonBlankOffset - 1) == ' ' || line.charAt(nonBlankOffset - 1) == '\t')) {
            nonBlankOffset--;
        }
        if (lineOffset == nonBlankOffset) {
            lineOffset = line.length();
        }
        else {
            lineOffset = nonBlankOffset;
        }

        styledText.setRedraw(false);
        try {
            int newOffset = lineOffset + styledText.getOffsetAtLine(lineNumber);
            if (doSelect) {
                Point selection = styledText.getSelection();
                if (offset == selection.x) {
                    styledText.setSelection(selection.y, newOffset);
                }
                else if (offset == selection.y) {
                    styledText.setSelection(selection.x, newOffset);
                }
                else {
                    styledText.setSelection(offset, newOffset);
                }
            }
            styledText.setCaretOffset(newOffset);
            styledText.showSelection();
        } finally {
            styledText.setRedraw(true);
        }
    }

    void doAutoIndent() {
        int caretOffset = styledText.getCaretOffset();
        int lineNumber = styledText.getLineAtOffset(caretOffset);
        int lineStart = styledText.getOffsetAtLine(lineNumber);
        String lineText = styledText.getLine(lineNumber);
        int currentColumn = caretOffset - lineStart;

        String leftText = lineText.substring(0, currentColumn);
        String rightText = lineText.substring(currentColumn);

        StringBuilder sb = new StringBuilder();
        sb.append(leftText);
        sb.append(styledText.getLineDelimiter());

        int indentColumn = 0;
        while (indentColumn < leftText.length() && Character.isWhitespace(leftText.charAt(indentColumn))) {
            indentColumn++;
        }
        if (indentColumn < leftText.length()) {
            String trimmedText = leftText.substring(indentColumn).toUpperCase();
            if (startsWithBlock(trimmedText)) {
                boolean tabstopMatch = false;
                int[] tabStops = Preferences.getInstance().getTabStops();

                indentColumn++;
                for (int i = 0; i < tabStops.length; i++) {
                    if (tabStops[i] >= indentColumn) {
                        indentColumn = tabStops[i];
                        tabstopMatch = true;
                        break;
                    }
                }

                if (!tabstopMatch) {
                    int defaultTabStop = styledText.getTabs();
                    while ((indentColumn % defaultTabStop) != 0) {
                        indentColumn++;
                    }
                }
            }
        }
        for (int i = 0; i < indentColumn; i++) {
            sb.append(" ");
        }

        caretOffset = lineStart + sb.length();
        sb.append(rightText);

        styledText.setRedraw(false);
        try {
            styledText.replaceTextRange(lineStart, lineText.length(), sb.toString());
            styledText.setCaretOffset(caretOffset);
        } finally {
            styledText.setRedraw(true);
        }
    }

    boolean startsWithBlock(String text) {
        if (text.startsWith("CON") || text.startsWith("VAR") || text.startsWith("OBJ") || text.startsWith("PUB") || text.startsWith("PRI")) {
            return true;
        }
        if (text.startsWith("IF") || text.startsWith("ELSE") || text.startsWith("REPEAT") || text.startsWith("CASE")) {
            return true;
        }
        return false;
    }

    void doTab() {
        int caretOffset = styledText.getCaretOffset();
        int lineNumber = styledText.getLineAtOffset(caretOffset);
        int lineStart = styledText.getOffsetAtLine(lineNumber);
        int currentColumn = caretOffset - lineStart;

        String lineText = styledText.getLine(lineNumber);
        if (lineText.substring(0, currentColumn).trim().equals("")) {
            if (currentColumn < lineText.length() && Character.isWhitespace(lineText.charAt(currentColumn))) {
                do {
                    currentColumn++;
                } while (currentColumn < lineText.length() && Character.isWhitespace(lineText.charAt(currentColumn)));
                styledText.setCaretOffset(lineStart + currentColumn);
                return;
            }
        }

        boolean tabstopMatch = false;
        int[] tabStops = Preferences.getInstance().getTabStops();

        int nextTabColumn = currentColumn + 1;
        for (int i = 0; i < tabStops.length; i++) {
            if (tabStops[i] >= nextTabColumn) {
                nextTabColumn = tabStops[i];
                tabstopMatch = true;
                break;
            }
        }

        if (!tabstopMatch) {
            int defaultTabStop = styledText.getTabs();
            while ((nextTabColumn % defaultTabStop) != 0) {
                nextTabColumn++;
            }
        }

        StringBuilder sb = new StringBuilder();
        while (currentColumn < nextTabColumn) {
            sb.append(" ");
            currentColumn++;
        }

        styledText.setRedraw(false);
        try {
            styledText.insert(sb.toString());
            styledText.setCaretOffset(lineStart + nextTabColumn);
            styledText.showSelection();
        } finally {
            styledText.setRedraw(true);
        }
    }

    void doBacktab() {
        int caretOffset = styledText.getCaretOffset();
        int lineNumber = styledText.getLineAtOffset(caretOffset);
        int lineStart = styledText.getOffsetAtLine(lineNumber);
        String lineText = styledText.getLine(lineNumber);

        int currentColumn = caretOffset - lineStart;
        if (currentColumn == 0) {
            return;
        }

        int[] tabStops = Preferences.getInstance().getTabStops();
        int previousTabColumn = 0;
        for (int i = 0; i < tabStops.length; i++) {
            if (tabStops[i] >= currentColumn) {
                break;
            }
            previousTabColumn = tabStops[i];
        }

        while (currentColumn > 0 && currentColumn > previousTabColumn) {
            if (!Character.isWhitespace(lineText.charAt(currentColumn - 1))) {
                break;
            }
            currentColumn--;
        }

        styledText.setRedraw(false);
        try {
            styledText.setSelection(lineStart + currentColumn, caretOffset);
            styledText.insert("");
            styledText.showSelection();
        } finally {
            styledText.setRedraw(true);
        }
    }

    public IContentProposal[] computeProposals(String contents, int position) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        Node node = tokenMarker.getContextAt(styledText.getCaretOffset());

        int start = position;
        while (start > 0) {
            if (node instanceof DataLineNode) {
                if (contents.charAt(start - 1) == '#') {
                    break;
                }
            }
            if (contents.charAt(start - 1) != '.' && contents.charAt(start - 1) != '#' && contents.charAt(start - 1) != '_' && !Character.isAlphabetic(contents.charAt(start - 1))) {
                break;
            }
            start--;
        }

        String token = contents.substring(start, position).toUpperCase();

        if (position == 0) {
            proposals.addAll(helpProvider.fillProposals("Root", token));
        }
        else {
            if (node instanceof ObjectNode) {
                proposals.addAll(helpProvider.fillSourceProposals());
            }
            else if (node instanceof DataLineNode) {
                DataLineNode line = (DataLineNode) node;
                position = styledText.getCaretOffset();

                if (line.condition != null) {
                    if (position >= line.condition.getStartIndex() && position <= line.condition.getStopIndex() + 1) {
                        proposals.addAll(helpProvider.fillProposals("Condition", token));
                    }
                }
                if (line.instruction != null) {
                    if (position >= line.instruction.getStartIndex() && position <= line.instruction.getStopIndex() + 1) {
                        proposals.addAll(helpProvider.fillProposals("Instruction", token));
                    }
                    if (position > line.instruction.getStopIndex() + 1) {
                        proposals.addAll(helpProvider.fillProposals(line.instruction.getText().toUpperCase(), token));
                        if (node.getParent() instanceof StatementNode || node.getParent() instanceof MethodNode) {
                            proposals.addAll(tokenMarker.getMethodProposals(node.getParent(), token));
                        }
                        else {
                            proposals.addAll(tokenMarker.getPAsmProposals(token));
                        }
                    }
                }

                if (line.condition == null && line.instruction == null) {
                    proposals.addAll(helpProvider.fillProposals("Condition", token));
                    proposals.addAll(helpProvider.fillProposals("Instruction", token));
                }
                else if (line.condition != null && line.instruction == null) {
                    if (position > line.condition.getStopIndex() + 1) {
                        proposals.addAll(helpProvider.fillProposals("Instruction", token));
                    }
                }
                else if (line.condition == null && line.instruction != null) {
                    if (position < line.instruction.getStartIndex()) {
                        proposals.addAll(helpProvider.fillProposals("Condition", token));
                    }
                }
            }
            else if (node != null) {
                if (node instanceof StatementNode) {
                    proposals.addAll(tokenMarker.getMethodProposals(node, token));
                }
                proposals.addAll(helpProvider.fillProposals(node.getClass().getSimpleName(), token));
            }
        }

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

    public void gotToLineColumn(int line, int column) {
        if (line >= styledText.getLineCount()) {
            return;
        }
        int offset = styledText.getOffsetAtLine(line);
        styledText.setCaretOffset(offset + column);
        styledText.setTopIndex(line > 10 ? line - 10 : 1);
    }

    public void setCompilerMessages(List<CompilerMessage> messages) {
        tokenMarker.refreshCompilerTokens(messages);

        ruler.clearHighlights();
        for (CompilerMessage msg : messages) {
            if (msg.type == CompilerMessage.ERROR) {
                ruler.setHighlight(msg.line);
            }
        }
    }

    public void redraw() {
        styledText.redraw();
        ruler.redraw();
    }

}
