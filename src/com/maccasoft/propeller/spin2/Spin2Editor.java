/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.databinding.swt.DisplayRealm;
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

import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ContentProposalAdapter;
import com.maccasoft.propeller.internal.HTMLStyledTextParser;
import com.maccasoft.propeller.internal.StyledTextContentAdapter;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.EditorTokenMarker.TokenId;
import com.maccasoft.propeller.spin2.EditorTokenMarker.TokenMarker;

public class Spin2Editor {

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

    Spin2EditorBackgroundDecorator backgroundDecorator;

    boolean ignoreUndo;
    boolean ignoreRedo;
    TextChange currentChange;
    Stack<TextChange> undoStack = new Stack<TextChange>();
    Stack<TextChange> redoStack = new Stack<TextChange>();

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

    final Runnable refreshMarkersRunnable = new Runnable() {

        @Override
        public void run() {
            if (styledText == null || styledText.isDisposed()) {
                return;
            }
            try {
                tokenMarker.refreshTokens(styledText.getText());
                styledText.redraw();
            } catch (Exception e) {
                e.printStackTrace();
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

    Shell window;

    public Spin2Editor(Composite parent) {
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

        TextStyle warningStyle = new TextStyle();
        warningStyle.underline = true;
        warningStyle.underlineColor = ColorRegistry.getColor(0xFC, 0xAF, 0x3E);
        warningStyle.underlineStyle = SWT.UNDERLINE_DOUBLE;
        styleMap.put(TokenId.WARNING, warningStyle);

        TextStyle errorStyle = new TextStyle();
        errorStyle.underline = true;
        errorStyle.underlineColor = ColorRegistry.getColor(0xC0, 0x00, 0x00);
        errorStyle.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
        styleMap.put(TokenId.ERROR, errorStyle);

        ruler = new LineNumbersRuler(container);
        ruler.setFont(font);

        styledText = new StyledText(container, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
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
                //e.display.timerExec(500, refreshMarkersRunnable);
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
                if (e.keyCode == SWT.TAB) {
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
                List<StyleRange> ranges = new ArrayList<StyleRange>();

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
                }

                event.styles = ranges.toArray(new StyleRange[ranges.size()]);
            }
        });

        backgroundDecorator = new Spin2EditorBackgroundDecorator();
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
                if (window != null) {
                    window.dispose();
                    window = null;
                }
            }
        });

        styledText.addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseHover(MouseEvent e) {
                if (window != null) {
                    window.dispose();
                }

                int offset = styledText.getOffsetAtPoint(new Point(e.x, e.y));

                Token token = tokenMarker.getTokenAt(offset);
                if (token == null) {
                    return;
                }
                Rectangle bounds = display.map(styledText, null, styledText.getTextBounds(token.start, token.stop));

                Node context = tokenMarker.getContextAt(offset);
                TokenMarker marker = tokenMarker.getMarkerAtOffset(offset);

                if (marker != null && marker.getError() != null) {
                    window = new Shell(styledText.getShell(), SWT.NO_FOCUS | SWT.ON_TOP);
                    FillLayout layout = new FillLayout();
                    layout.marginHeight = layout.marginWidth = 5;
                    window.setLayout(layout);
                    Label content = new Label(window, SWT.NONE);
                    content.setText(marker.getError());
                    window.pack();

                    Point size = window.getSize();
                    window.setLocation(bounds.x, bounds.y - size.y - 3);

                    window.open();
                    display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            styledText.setFocus();
                        }
                    });
                    return;
                }

                if (token != null) {
                    String text = Spin2InstructionHelp.getString(context != null ? context.getClass().getSimpleName() : null, token.getText().toLowerCase());
                    if (text == null) {
                        text = tokenMarker.getMethod(token.getText());
                    }
                    if (text != null) {
                        window = new Shell(styledText.getShell(), SWT.NO_FOCUS | SWT.ON_TOP);
                        FillLayout layout = new FillLayout();
                        layout.marginHeight = layout.marginWidth = 5;
                        window.setLayout(layout);

                        StyledText content = new StyledText(window, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
                        content.setCaret(null);
                        new HTMLStyledTextParser(content).setText(text);

                        window.pack();

                        bounds.y += bounds.height + 3;
                        if (bounds.width < 640) {
                            bounds.width = 640;
                        }
                        if (bounds.height < 240) {
                            bounds.height = 240;
                        }

                        window.setBounds(bounds);

                        window.open();
                        display.asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                styledText.setFocus();
                            }
                        });
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
            null);
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
            public void handleEvent(Event e) {
                try {
                    clientArea = styledText.getClientArea();
                    clientArea.y += styledText.getTopMargin();
                    clientArea.height -= styledText.getBottomMargin();

                    e.gc.setClipping(clientArea);
                    e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_DARK_GRAY));

                    Node root = tokenMarker.getRoot();
                    if (root != null) {
                        for (Node child : root.getChilds()) {
                            if (child instanceof MethodNode) {
                                paintBlock(e.gc, child, 0, -1);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            void paintBlock(GC gc, Node node, int level, int yref) {
                Rectangle r = styledText.getTextBounds(node.getStartToken().start, node.getStartToken().start);
                int x0 = r.x + r.width / 2;
                int y0 = r.y + r.height;

                if (y0 >= clientArea.height) {
                    return;
                }

                for (Node child : node.getChilds()) {
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

    public Control getControl() {
        return container;
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

            currentLine = 0;
            styledText.setText(text);
            undoStack.clear();
            redoStack.clear();

            modified = true;

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
            try {
                styledText.setText(result);
                styledText.setTopIndex(topindex);

                String ls = styledText.getLine(line);
                styledText.setCaretOffset(styledText.getOffsetAtLine(line) + Math.min(column, ls.length()));
            } finally {
                styledText.setRedraw(true);
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

    void doTab() {
        int caretOffset = styledText.getCaretOffset();
        int lineNumber = styledText.getLineAtOffset(caretOffset);
        int lineStart = styledText.getOffsetAtLine(lineNumber);

        int currentColumn = caretOffset - lineStart;

        int nextTabColumn = 0;
        while (nextTabColumn <= currentColumn) {
            nextTabColumn += styledText.getTabs();
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

        int previousTabColumn = 0;
        while (previousTabColumn + styledText.getTabs() < currentColumn) {
            previousTabColumn += styledText.getTabs();
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

        int start = position;
        while (start > 0) {
            if (contents.charAt(start - 1) != '_' && !Character.isAlphabetic(contents.charAt(start - 1))) {
                break;
            }
            start--;
        }

        String token = contents.substring(start, position).toUpperCase();

        if (position == 0) {
            proposals.addAll(Spin2InstructionHelp.fillProposals("Root", token));
        }
        else {
            Node node = tokenMarker.getContextAt(styledText.getCaretOffset());
            if (node instanceof DataLineNode) {
                DataLineNode line = (DataLineNode) node;
                position = styledText.getCaretOffset();

                if (line.condition != null) {
                    if (position >= line.condition.getStartIndex() && position <= line.condition.getStopIndex() + 1) {
                        proposals.addAll(Spin2InstructionHelp.fillProposals("Condition", token));
                    }
                }
                if (line.instruction != null) {
                    if (position >= line.instruction.getStartIndex() && position <= line.instruction.getStopIndex() + 1) {
                        proposals.addAll(Spin2InstructionHelp.fillProposals("Instruction", token));
                    }
                    if (position > line.instruction.getStopIndex() + 1) {
                        proposals.addAll(Spin2InstructionHelp.fillProposals(line.instruction.getText().toUpperCase(), token));
                        if (node.getParent() instanceof StatementNode || node.getParent() instanceof MethodNode) {
                            proposals.addAll(tokenMarker.getMethodProposals(node.getParent(), token));
                        }
                        else {
                            proposals.addAll(tokenMarker.getPAsmProposals(token));
                        }
                    }
                }

                if (line.condition == null && line.instruction == null) {
                    proposals.addAll(Spin2InstructionHelp.fillProposals("Condition", token));
                    proposals.addAll(Spin2InstructionHelp.fillProposals("Instruction", token));
                }
                else if (line.condition != null && line.instruction == null) {
                    if (position > line.condition.getStopIndex() + 1) {
                        proposals.addAll(Spin2InstructionHelp.fillProposals("Instruction", token));
                    }
                }
                else if (line.condition == null && line.instruction != null) {
                    if (position < line.instruction.getStartIndex()) {
                        proposals.addAll(Spin2InstructionHelp.fillProposals("Condition", token));
                    }
                }
            }
            else if (node != null) {
                if (node instanceof StatementNode) {
                    proposals.addAll(tokenMarker.getMethodProposals(node, token));
                }
                proposals.addAll(Spin2InstructionHelp.fillProposals(node.getClass().getSimpleName(), token));
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

    public static void main(String[] args) {
        final Display display = new Display();

        display.setErrorHandler(new Consumer<Error>() {

            @Override
            public void accept(Error t) {
                t.printStackTrace();
            }

        });
        display.setRuntimeExceptionHandler(new Consumer<RuntimeException>() {

            @Override
            public void accept(RuntimeException t) {
                t.printStackTrace();
            }

        });

        Realm.runWithDefault(DisplayRealm.getRealm(display), new Runnable() {

            @Override
            public void run() {
                try {
                    Shell shell = new Shell(display);
                    shell.setText("Spin2 Editor");

                    Rectangle screen = display.getClientArea();

                    Rectangle rect = new Rectangle(0, 0, 800, 800);
                    rect.x = (screen.width - rect.width) / 2;
                    rect.y = (screen.height - rect.height) / 2;
                    if (rect.y < 0) {
                        rect.height += rect.y * 2;
                        rect.y = 0;
                    }

                    shell.setLocation(rect.x, rect.y);
                    shell.setSize(rect.width, rect.height);

                    FillLayout layout = new FillLayout();
                    layout.marginWidth = layout.marginHeight = 5;
                    shell.setLayout(layout);

                    new Spin2Editor(shell);

                    shell.open();

                    while (display.getShells().length != 0) {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        display.dispose();
    }

}