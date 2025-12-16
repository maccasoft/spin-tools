/*
 * Copyright (c) 2021-2025 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.SafeRunnable;
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
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
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

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ContentProposalAdapter;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.internal.HTMLStyledTextDecorator;
import com.maccasoft.propeller.internal.IContentProposalListener2;
import com.maccasoft.propeller.internal.StyledTextContentAdapter;
import com.maccasoft.propeller.internal.Utils;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spinc.CTokenMarker;

public class SourceEditor {

    private static final int UNDO_LIMIT = 500;
    private static final int CURRENT_CHANGE_TIMER_EXPIRE = 500;

    Display display;
    Composite container;
    LineNumbersRuler ruler;
    StyledText styledText;
    OverviewRuler overview;

    boolean showIndentLines;
    int indentLinesSize;
    Color indentLinesForeground;

    Font font;
    Font fontBold;
    Font fontItalic;
    Font fontBoldItalic;
    Point charSize;

    int currentLine;
    Color currentLineBackground;
    boolean highlightCurrentLine;

    int hoverDocModifiers;
    int hyperlinkModifiers;

    Caret insertCaret;
    Caret overwriteCaret;
    Caret alignCaret;

    SourceTokenMarker tokenMarker;
    Map<TokenId, TextStyle> styleMap = new HashMap<TokenId, TextStyle>();
    Map<Integer, StyleRange[]> lineStylesCache = new HashMap<>();

    EditorHelp helpProvider;
    ContentProposalAdapter proposalAdapter;

    boolean ignoreUndo;
    boolean ignoreRedo;
    TextChange currentChange;
    Stack<TextChange> undoStack = new Stack<TextChange>();
    Stack<TextChange> redoStack = new Stack<TextChange>();

    boolean ignoreModify;
    ListenerList<ModifyListener> modifyListeners = new ListenerList<ModifyListener>();

    private ListenerList<SourceListener> openListeners = new ListenerList<>();

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

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_EDITOR_FONT:
                    Font textFont = JFaceResources.getTextFont();
                    FontData fontData = textFont.getFontData()[0];
                    if (evt.getNewValue() != null) {
                        fontData = StringConverter.asFontData(evt.getNewValue().toString());
                    }
                    fontData.setStyle(SWT.NONE);
                    updateFontsFrom(fontData);
                    break;
                case Preferences.PROP_SHOW_LINE_NUMBERS:
                    ruler.setVisible((Boolean) evt.getNewValue());
                    container.layout(true);
                    break;
                case Preferences.PROP_INDENT_LINES_SIZE:
                    indentLinesSize = (Integer) evt.getNewValue();
                    styledText.redraw();
                    break;
                case Preferences.PROP_SHOW_INDENT_LINES:
                    showIndentLines = (Boolean) evt.getNewValue();
                    styledText.redraw();
                    break;
                case Preferences.PROP_SHOW_SECTIONS_BACKGROUND:
                    styledText.redraw();
                    break;
                case Preferences.PROP_HIGHLIGHT_CURRENT_LINE:
                    highlightCurrentLine = (Boolean) evt.getNewValue();
                    styledText.redraw();
                    break;
                case Preferences.PROP_HOVERDOC_MOD:
                    hoverDocModifiers = getModifiers(preferences.getHoverDocModifiers());
                    break;
                case Preferences.PROP_HYPERLINK_MOD:
                    hyperlinkModifiers = getModifiers(preferences.getHyperlinkModifiers());
                    break;
                case Preferences.PROP_THEME:
                    applyTheme((String) evt.getNewValue());
                    styledText.redraw();
                    break;
            }
        }
    };

    Shell popupWindow;
    Rectangle popupMouseBounds;
    Point mousePosition;

    boolean mouseButton1;
    boolean hoverHighlight;
    NavigationTarget hoverTarget;

    Preferences preferences;

    Listener modifiersFilterListener = new Listener() {

        @Override
        public void handleEvent(Event event) {
            if (event.widget == styledText) {
                if (event.button == 1 && hoverTarget != null) {
                    event.stateMask &= ~hyperlinkModifiers;
                }
            }
        }

    };

    public SourceEditor(Composite parent) {
        display = parent.getDisplay();
        preferences = Preferences.getInstance();

        container = new Composite(parent, SWT.NO_FOCUS);
        GridLayout containerLayout = new GridLayout(3, false);
        containerLayout.horizontalSpacing = 0;
        containerLayout.marginWidth = containerLayout.marginHeight = 0;
        container.setLayout(containerLayout);

        currentLine = 0;

        ruler = new LineNumbersRuler(container);

        styledText = new StyledText(container, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL) {

            @Override
            public void invokeAction(int action) {
                switch (action) {
                    case ST.LINE_START:
                        doLineStart(false);
                        break;
                    case ST.LINE_END:
                        doLineEnd(false);
                        break;
                    case ST.SELECT_LINE_START:
                        doLineStart(true);
                        break;
                    case ST.SELECT_LINE_END:
                        doLineEnd(true);
                        break;
                    case ST.TOGGLE_OVERWRITE:
                        if (styledText.getCaret() == insertCaret) {
                            alignCaret.setLocation(styledText.getCaret().getLocation());
                            styledText.setCaret(alignCaret);
                        }
                        else if (styledText.getCaret() == alignCaret) {
                            overwriteCaret.setLocation(styledText.getCaret().getLocation());
                            styledText.setCaret(overwriteCaret);
                            super.invokeAction(action);
                        }
                        else {
                            insertCaret.setLocation(styledText.getCaret().getLocation());
                            styledText.setCaret(insertCaret);
                            super.invokeAction(action);
                        }
                        break;
                    default:
                        super.invokeAction(action);
                }
            }

        };
        styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        styledText.setMargins(5, 5, 5, 5);
        styledText.setTabs(8);

        insertCaret = new Caret(styledText, SWT.NULL);
        overwriteCaret = new Caret(styledText, SWT.NULL);
        alignCaret = new Caret(styledText, SWT.NULL);

        overview = new OverviewRuler(container);
        overview.setStyledText(styledText);

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (preferences.getEditorFont() != null) {
            fontData = StringConverter.asFontData(preferences.getEditorFont());
        }
        fontData.setStyle(SWT.NONE);
        updateFontsFrom(fontData);

        showIndentLines = preferences.getShowIndentLines();
        indentLinesSize = preferences.getIndentLinesSize();
        highlightCurrentLine = preferences.getHighlightCurrentLine();

        hyperlinkModifiers = getModifiers(preferences.getHyperlinkModifiers());
        hoverDocModifiers = getModifiers(preferences.getHoverDocModifiers());

        preferences.addPropertyChangeListener(preferencesChangeListener);

        ruler.setText(styledText);

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
                if (e.text.indexOf('\t') != -1) {
                    e.text = FileUtils.replaceTabs(e.text, styledText.getTabs());
                }

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
                int stateMask = e.stateMask;
                if ((e.keyCode & SWT.MODIFIER_MASK) != 0) {
                    stateMask |= e.keyCode & SWT.MODIFIER_MASK;
                }

                if ((e.keyCode & hoverDocModifiers) != 0 && (stateMask & hoverDocModifiers) == hoverDocModifiers) {
                    if (popupWindow == null && mousePosition != null) {
                        Event event = new Event();
                        event.x = mousePosition.x;
                        event.y = mousePosition.y;
                        event.stateMask = stateMask;
                        styledText.notifyListeners(SWT.MouseHover, event);
                    }
                }

                if ((e.keyCode & hyperlinkModifiers) != 0 && (stateMask & hyperlinkModifiers) == hyperlinkModifiers) {
                    if (!mouseButton1 && mousePosition != null) {
                        hoverHighlight = true;
                        int offset = styledText.getOffsetAtPoint(mousePosition);
                        if (offset != -1) {
                            hoverTarget = getNavigationTarget(offset);
                        }
                        Cursor cursor = hoverTarget != null ? display.getSystemCursor(SWT.CURSOR_HAND) : null;
                        if (cursor != styledText.getCursor()) {
                            styledText.setCursor(cursor);
                        }
                        styledText.redraw();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int stateMask = e.stateMask;

                if ((e.keyCode & hoverDocModifiers) != 0 && (stateMask & hoverDocModifiers) == hoverDocModifiers) {
                    if (popupWindow != null) {
                        popupWindow.dispose();
                        popupWindow = null;
                        popupMouseBounds = null;
                        styledText.setFocus();
                    }
                }

                if ((e.keyCode & hyperlinkModifiers) != 0) {
                    if (!mouseButton1) {
                        hoverHighlight = false;
                        styledText.setCursor(null);
                        styledText.redraw();
                        if (mousePosition != null) {
                            Event event = new Event();
                            event.x = mousePosition.x;
                            event.y = mousePosition.y;
                            styledText.notifyListeners(SWT.MouseHover, event);
                        }
                    }
                }
            }
        });

        styledText.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                hoverHighlight = false;
                styledText.setCursor(null);
                styledText.redraw();
            }
        });

        styledText.getVerticalBar().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                styledText.redraw();
            }
        });

        styledText.getContent().addTextChangeListener(new TextChangeListener() {

            @Override
            public void textSet(TextChangedEvent event) {
                try {
                    if (tokenMarker != null) {
                        tokenMarker.refreshTokens(styledText.getText());
                        lineStylesCache.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                styledText.redraw();
            }

            @Override
            public void textChanging(TextChangingEvent event) {
                fixupTokens(event, tokenMarker.getCompilerTokens());
                fixupTokens(event, tokenMarker.getExcludedNodes());
            }

            void fixupTokens(TextChangingEvent event, Collection<TokenMarker> c) {
                for (TokenMarker entry : c) {
                    if (event.newCharCount != 0) {
                        if (event.start < entry.start) {
                            entry.start += event.newCharCount;
                            entry.stop += event.newCharCount;
                        }
                        else if (event.start >= entry.start && event.start <= entry.stop + 1) {
                            entry.stop += event.newCharCount;
                        }
                    }
                    if (event.replaceCharCount != 0) {
                        if (event.start + event.replaceCharCount <= entry.start) {
                            entry.start -= event.replaceCharCount;
                            entry.stop -= event.replaceCharCount;
                        }
                        else if (event.start >= entry.start && event.start <= entry.stop) {
                            if (event.start + event.replaceCharCount > entry.stop) {
                                entry.stop -= entry.stop - event.start;
                            }
                            else {
                                entry.stop -= event.replaceCharCount;
                            }
                        }
                        else if (event.start < entry.start) {
                            if (event.start + event.replaceCharCount <= entry.stop) {
                                entry.stop -= (event.start + event.replaceCharCount) - entry.start;
                                entry.stop -= entry.start - event.start;
                                entry.start -= entry.start - event.start;
                            }
                            else {
                                entry.stop = entry.start;
                            }
                        }
                    }
                }
            }

            @Override
            public void textChanged(TextChangedEvent event) {
                try {
                    if (tokenMarker != null) {
                        tokenMarker.refreshTokens(styledText.getText());
                        lineStylesCache.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                styledText.redraw();
            }

        });

        styledText.addLineStyleListener(new LineStyleListener() {

            @Override
            public void lineGetStyle(LineStyleEvent event) {
                event.styles = lineStylesCache.get(event.lineOffset);

                if (event.styles == null && tokenMarker != null) {
                    try {
                        List<StyleRange> ranges = new ArrayList<StyleRange>();
                        for (TokenMarker entry : tokenMarker.getLineTokens(event.lineOffset, event.lineText)) {
                            TextStyle style = styleMap.get(entry.getId());
                            if (style != null) {
                                StyleRange range = new StyleRange(style);
                                range.start = entry.getStart();
                                range.length = entry.getStop() - range.start + 1;
                                ranges.add(range);
                            }
                        }
                        event.styles = ranges.toArray(new StyleRange[ranges.size()]);
                        lineStylesCache.put(event.lineOffset, event.styles);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        styledText.addLineBackgroundListener(new LineBackgroundListener() {

            @Override
            public void lineGetBackground(LineBackgroundEvent event) {
                if (preferences.getShowSectionsBackground()) {
                    if (!(tokenMarker instanceof CTokenMarker)) {
                        event.lineBackground = getLineBackground(tokenMarker.getRoot(), event.lineOffset);
                    }
                }
                if (highlightCurrentLine) {
                    if (styledText.getLineAtOffset(event.lineOffset) == currentLine) {
                        if (event.lineBackground != null) {
                            event.lineBackground = ColorRegistry.getDimColor(event.lineBackground, -12);
                        }
                        else {
                            event.lineBackground = currentLineBackground;
                        }
                    }
                }
            }
        });

        styledText.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    mouseButton1 = true;
                }
            }

            @Override
            public void mouseUp(MouseEvent e) {
                if (e.button == 1) {
                    mouseButton1 = false;
                }

                if (hoverTarget != null) {
                    if (popupWindow != null) {
                        popupWindow.dispose();
                        popupWindow = null;
                        popupMouseBounds = null;
                    }

                    final SourceElement element = hoverTarget;
                    display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            fireNavigateToEvent(element);
                        }

                    });
                    hoverTarget = null;

                    styledText.setCursor(null);
                    styledText.redraw();
                }
            }

        });

        styledText.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                mousePosition = new Point(e.x, e.y);

                if (popupWindow != null) {
                    if (popupMouseBounds != null && !popupMouseBounds.contains(e.x, e.y)) {
                        popupWindow.dispose();
                        popupWindow = null;
                        popupMouseBounds = null;
                        styledText.setFocus();
                    }
                    return;
                }

                if (mouseButton1) {
                    if (hoverTarget != null) {
                        hoverHighlight = false;
                        hoverTarget = null;
                        styledText.setCursor(null);
                        styledText.redraw();
                    }
                    return;
                }

                hoverHighlight = (e.stateMask & SWT.MODIFIER_MASK) == hyperlinkModifiers;
                hoverTarget = null;

                if (hoverHighlight) {
                    int offset = styledText.getOffsetAtPoint(mousePosition);
                    if (offset != -1) {
                        hoverTarget = getNavigationTarget(offset);
                    }

                    Cursor cursor = hoverTarget != null ? display.getSystemCursor(SWT.CURSOR_HAND) : null;
                    if (cursor != styledText.getCursor()) {
                        styledText.setCursor(cursor);
                    }
                    styledText.redraw();
                }
            }

        });

        styledText.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseHover(MouseEvent e) {
                if (popupWindow != null) {
                    return;
                }
                if ((e.stateMask & SWT.MODIFIER_MASK) != hoverDocModifiers) {
                    return;
                }

                int offset = styledText.getOffsetAtPoint(new Point(e.x, e.y));

                Token token = tokenMarker.getTokenAt(offset);
                if (token == null || token.type == Token.EOF) {
                    return;
                }

                popupMouseBounds = styledText.getTextBounds(token.start, token.stop);
                popupMouseBounds.x -= 5;
                popupMouseBounds.y -= 5;
                popupMouseBounds.width += 10;
                popupMouseBounds.height += 10;

                Node context = tokenMarker.getContextAtLine(styledText.getLineAtOffset(offset));
                TokenMarker marker = tokenMarker.getMarkerAtOffset(offset);
                Rectangle bounds = display.map(styledText, null, styledText.getTextBounds(token.start, token.stop));

                if (marker != null && marker.getError() != null) {
                    popupWindow = new Shell(styledText.getShell(), SWT.RESIZE | SWT.ON_TOP);
                    FillLayout layout = new FillLayout();
                    layout.marginHeight = layout.marginWidth = 5;
                    popupWindow.setLayout(layout);
                    popupWindow.setForeground(textForeground);
                    popupWindow.setBackground(textBackground);
                    popupWindow.setBackgroundMode(SWT.INHERIT_DEFAULT);

                    Label content = new Label(popupWindow, SWT.NONE);
                    content.setForeground(textForeground);
                    content.setText(marker.getError());
                    popupWindow.pack();

                    Point size = popupWindow.getSize();
                    popupWindow.setLocation(bounds.x, bounds.y - size.y - 3);

                    popupWindow.setVisible(true);
                    return;
                }

                if (token != null && helpProvider != null) {
                    String text = tokenMarker.getMethod(token.getText());
                    if (text == null && token.getText().startsWith("@")) {
                        text = tokenMarker.getMethod(token.getText().substring(1));
                    }
                    if (text == null && token.getText().startsWith(".")) {
                        int line = styledText.getLineAtOffset(offset);
                        int lineOffset = styledText.getOffsetAtLine(line);
                        String lineText = styledText.getLine(line);
                        int endIndex = token.start - lineOffset - 1;
                        if (endIndex >= 0) {
                            if (lineText.charAt(endIndex) == ']') {
                                int depth = -1;
                                while (endIndex >= 0) {
                                    if (lineText.charAt(endIndex) == ']') {
                                        depth++;
                                    }
                                    else if (lineText.charAt(endIndex) == '[') {
                                        if (depth == 0) {
                                            break;
                                        }
                                        depth--;
                                    }
                                    endIndex--;
                                }
                                endIndex--;
                            }
                            Token objectToken = tokenMarker.getTokenAt(endIndex + lineOffset);
                            if (objectToken != null) {
                                String qualifiedName = objectToken.getText() + token.getText();
                                text = tokenMarker.getMethod(qualifiedName);
                                if (text == null && qualifiedName.startsWith("@")) {
                                    text = tokenMarker.getMethod(qualifiedName.substring(1));
                                }
                            }
                        }
                    }
                    if (text == null) {
                        int line = styledText.getLineAtOffset(offset);
                        int lineOffset = styledText.getOffsetAtLine(line);
                        String lineText = styledText.getLine(line);

                        int index = token.stop - lineOffset + 1;
                        while (index < lineText.length() && Character.isWhitespace(lineText.charAt(index))) {
                            index++;
                        }
                        if (index < lineText.length() && lineText.charAt(index) != '[') {
                            text = helpProvider.getString(context != null ? context.getClass().getSimpleName() : null, token.getText().toLowerCase());
                        }
                    }
                    if (text != null && !"".equals(text)) {
                        popupWindow = new Shell(styledText.getShell(), SWT.RESIZE | SWT.ON_TOP);
                        FillLayout layout = new FillLayout();
                        layout.marginHeight = layout.marginWidth = 0;
                        popupWindow.setLayout(layout);
                        popupWindow.setLayout(layout);
                        popupWindow.setForeground(textForeground);
                        popupWindow.setBackground(textBackground);
                        popupWindow.setBackgroundMode(SWT.INHERIT_DEFAULT);

                        StyledText content = new StyledText(popupWindow, SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
                        content.setMargins(5, 5, 5, 5);
                        content.setCaret(null);
                        content.setForeground(textForeground);
                        content.setBackground(textBackground);
                        HTMLStyledTextDecorator htmlText = new HTMLStyledTextDecorator(content);
                        htmlText.setText(text);

                        popupWindow.pack();

                        bounds.y += bounds.height + 3;
                        bounds.width = Math.max(Math.max(640, bounds.width),
                            htmlText.getLineSize() +
                                content.getLeftMargin() + content.getRightMargin() +
                                layout.marginWidth * 2 +
                                popupWindow.getBorderWidth() * 2 + 5);
                        if (bounds.height < 240) {
                            bounds.height = 240;
                        }
                        popupWindow.setBounds(bounds);

                        popupWindow.setVisible(true);
                    }
                }

            }

        });

        KeyStroke keyStroke = null;
        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");
        } catch (Exception e) {
            e.printStackTrace();
        }
        proposalAdapter = new ContentProposalAdapter(
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
        proposalAdapter.setPopupSize(new Point(200, 300));
        proposalAdapter.setPropagateKeys(true);
        proposalAdapter.setAutoActivationDelay(500);
        proposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        proposalAdapter.addContentProposalListener(new IContentProposalListener2() {

            char[] activationChars;

            @Override
            public void proposalPopupOpened(ContentProposalAdapter adapter) {
                activationChars = adapter.getAutoActivationCharacters();
                adapter.setAutoActivationCharacters(null);
            }

            @Override
            public void proposalPopupClosed(ContentProposalAdapter adapter) {
                adapter.setAutoActivationCharacters(activationChars);
            }
        });

        styledText.addVerifyKeyListener(new VerifyKeyListener() {

            @Override
            public void verifyKey(VerifyEvent e) {
                try {
                    if (proposalAdapter.isProposalPopupOpen()) {
                        switch (e.keyCode) {
                            case SWT.CR:
                            case SWT.TAB:
                            case SWT.PAGE_DOWN:
                            case SWT.PAGE_UP:
                            case SWT.HOME:
                            case SWT.END:
                            case SWT.ARROW_LEFT:
                            case SWT.ARROW_RIGHT:
                                e.doit = false;
                                return;
                        }
                    }
                    else {
                        switch (e.keyCode) {
                            case SWT.CR:
                                doAutoIndent();
                                e.doit = false;
                                break;
                            case SWT.TAB:
                                if ((e.stateMask & SWT.MODIFIER_MASK) == SWT.MOD2) {
                                    doBacktab();

                                }
                                else if ((e.stateMask & SWT.MODIFIER_MASK) == 0) {
                                    doTab();
                                }
                                e.doit = false;
                                break;
                            case SWT.PAGE_DOWN:
                            case SWT.PAGE_UP:
                                styledText.redraw();
                                break;
                            case SWT.BS:
                            case SWT.DEL:
                                if (styledText.getCaret() == alignCaret) {
                                    int caretOffset = styledText.getCaretOffset();
                                    int lineNumber = styledText.getLineAtOffset(caretOffset);
                                    int lineStart = styledText.getOffsetAtLine(lineNumber);
                                    int currentColumn = caretOffset - lineStart;
                                    String text = styledText.getLine(lineNumber);
                                    int i = text.indexOf("  ", currentColumn);
                                    if (i != -1) {
                                        styledText.setCaretOffset(lineStart + i);
                                        styledText.insert(" ");
                                        styledText.setCaretOffset(caretOffset);
                                    }
                                }
                                break;
                            default:
                                if (e.character != 0 && styledText.getCaret() == alignCaret) {
                                    int caretOffset = styledText.getCaretOffset();
                                    int lineNumber = styledText.getLineAtOffset(caretOffset);
                                    int lineStart = styledText.getOffsetAtLine(lineNumber);
                                    int currentColumn = caretOffset - lineStart;
                                    String text = styledText.getLine(lineNumber);
                                    int i = text.indexOf("  ", currentColumn);
                                    if (i != -1) {
                                        styledText.setSelection(lineStart + i, lineStart + i + 1);
                                        styledText.insert("");
                                        styledText.setCaretOffset(caretOffset);
                                    }
                                }
                                break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        styledText.addPaintListener(new PaintListener() {

            int rightOffset;
            Rectangle clientArea;

            @Override
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;

                try {
                    clientArea = styledText.getClientArea();
                    clientArea.x += styledText.getRightMargin();
                    clientArea.y += styledText.getTopMargin();
                    clientArea.width -= styledText.getRightMargin() + styledText.getLeftMargin();
                    clientArea.height -= styledText.getTopMargin() + styledText.getBottomMargin();

                    rightOffset = styledText.getHorizontalPixel();

                    gc.setAdvanced(true);
                    gc.setAntialias(SWT.OFF);
                    gc.setClipping(clientArea);

                    gc.setLineWidth(indentLinesSize);
                    gc.setForeground(ColorRegistry.getColor(0xA0, 0xA0, 0xA0));

                    if (showIndentLines) {
                        Node root = tokenMarker.getRoot();
                        if (root != null) {
                            for (Node node : root.getChilds()) {
                                if (node instanceof MethodNode) {
                                    for (int i = 0; i < node.getChildCount(); i++) {
                                        Node child = node.getChild(i);
                                        if ((child instanceof StatementNode) || (child instanceof DataLineNode)) {
                                            int y2 = paintBlock(gc, child);
                                            Token token = child.getStartToken();
                                            String tokenText = token.getText().toUpperCase();
                                            if ("REPEAT".equals(tokenText)) {
                                                if (i + 1 < node.getChildCount()) {
                                                    Node nextChild = node.getChild(i + 1);
                                                    Token nextToken = nextChild.getStartToken();
                                                    String nextTokenText = nextToken.getText().toUpperCase();
                                                    if ("WHILE".equals(nextTokenText) || "UNTIL".equals(nextTokenText)) {
                                                        int x2 = (charSize.x * token.column + charSize.x) - rightOffset;
                                                        int y3 = styledText.getLinePixel(nextToken.line);
                                                        gc.drawLine(x2, y2, x2, y3);
                                                    }
                                                }
                                            }
                                            else if ("IF".equals(tokenText) || "IFNOT".equals(tokenText) || "ELSEIF".equals(tokenText) || "ELSEIFNOT".equals(tokenText) || "ELSE".equals(tokenText)) {
                                                if (i + 1 < node.getChildCount()) {
                                                    Node nextChild = node.getChild(i + 1);
                                                    Token nextToken = nextChild.getStartToken();
                                                    String nextTokenText = nextToken.getText().toUpperCase();
                                                    if ("ELSEIF".equals(nextTokenText) || "ELSEIFNOT".equals(nextTokenText) || "ELSE".equals(nextTokenText)) {
                                                        int x2 = (charSize.x * token.column + charSize.x) - rightOffset;
                                                        int y3 = styledText.getLinePixel(nextToken.line);
                                                        gc.drawLine(x2, y2, x2, y3);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    gc.setLineWidth(0);

                    for (TokenMarker entry : tokenMarker.getCompilerTokens()) {
                        if (entry.id == TokenId.WARNING) {
                            try {
                                Rectangle r = styledText.getTextBounds(entry.getStart(), entry.getStop());
                                int[] polyline = computePolyline(new Point(r.x, r.y), new Point(r.x + r.width, r.y), styledText.getLineHeight());
                                gc.setForeground(ColorRegistry.getColor(0xF6, 0xD4, 0x56));
                                gc.drawPolyline(polyline);
                            } catch (Exception ex) {
                                // Ignore
                            }
                        }
                    }

                    for (TokenMarker entry : tokenMarker.getCompilerTokens()) {
                        if (entry.id == TokenId.ERROR) {
                            try {
                                Rectangle r = styledText.getTextBounds(entry.getStart(), entry.getStop());
                                int[] polyline = computePolyline(new Point(r.x, r.y), new Point(r.x + r.width, r.y), styledText.getLineHeight());
                                gc.setForeground(ColorRegistry.getColor(0xC0, 0x00, 0x00));
                                gc.drawPolyline(polyline);
                            } catch (Exception ex) {
                                // Ignore
                            }
                        }
                    }

                    if (hoverHighlight && hoverTarget != null) {
                        Rectangle r = styledText.getTextBounds(hoverTarget.start, hoverTarget.stop);
                        gc.setForeground(styledText.getForeground());
                        gc.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            int paintBlock(GC gc, Node node) {
                Token token = node.getStartToken();

                int line0 = token.line;
                int x0 = (charSize.x * token.column + charSize.x) - rightOffset;
                int y0 = styledText.getLinePixel(token.line) + charSize.y;

                for (int i = 0; i < node.getChildCount(); i++) {
                    Node child = node.getChild(i);
                    if ((child instanceof StatementNode) || (child instanceof DataLineNode)) {
                        token = child.getStartToken();
                        String tokenText = token.getText().toUpperCase();
                        if ("WHILE".equals(tokenText) || "UNTIL".equals(tokenText)) {
                            continue;
                        }
                        if (token.line != line0) {
                            int x1 = (charSize.x * token.column) - rightOffset;
                            int y1 = styledText.getLinePixel(token.line) + charSize.y / 2 + 1;
                            gc.drawLine(x0, y0, x0, y1);
                            gc.drawLine(x0, y1, x1, y1);
                            y0 = y1;
                        }
                        int y2 = paintBlock(gc, child);
                        if ("REPEAT".equals(tokenText)) {
                            if (i + 1 < node.getChildCount()) {
                                Node nextChild = node.getChild(i + 1);
                                Token nextToken = nextChild.getStartToken();
                                String nextTokenText = nextToken.getText().toUpperCase();
                                if ("WHILE".equals(nextTokenText) || "UNTIL".equals(nextTokenText)) {
                                    int x2 = (charSize.x * token.column + charSize.x) - rightOffset;
                                    int y3 = styledText.getLinePixel(nextToken.line);
                                    gc.drawLine(x2, y2, x2, y3);
                                }
                            }
                        }
                        else if ("IF".equals(tokenText) || "IFNOT".equals(tokenText) || "ELSEIF".equals(tokenText) || "ELSEIFNOT".equals(tokenText) || "ELSE".equals(tokenText)) {
                            if (i + 1 < node.getChildCount()) {
                                Node nextChild = node.getChild(i + 1);
                                Token nextToken = nextChild.getStartToken();
                                String nextTokenText = nextToken.getText().toUpperCase();
                                if ("ELSEIF".equals(nextTokenText) || "ELSEIFNOT".equals(nextTokenText) || "ELSE".equals(nextTokenText)) {
                                    int x2 = (charSize.x * token.column + charSize.x) - rightOffset;
                                    int y3 = styledText.getLinePixel(nextToken.line);
                                    gc.drawLine(x2, y2, x2, y3);
                                    line0 = nextToken.line;
                                }
                            }
                        }
                    }
                }

                return y0;
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

        display.addFilter(SWT.MouseDown, modifiersFilterListener);

        styledText.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                display.removeFilter(SWT.MouseDown, modifiersFilterListener);
                preferences.removePropertyChangeListener(preferencesChangeListener);

                font.dispose();
                fontBold.dispose();
                fontItalic.dispose();
                fontBoldItalic.dispose();

                currentLineBackground.dispose();

                insertCaret.dispose();
                overwriteCaret.dispose();
                alignCaret.dispose();
            }
        });

        container.setTabList(new Control[] {
            styledText
        });
    }

    int getModifiers(int value) {
        int rc = 0;

        if ((value & 0x01) != 0) {
            rc |= SWT.MOD1;
        }
        if ((value & 0x02) != 0) {
            rc |= SWT.MOD2;
        }
        if ((value & 0x04) != 0) {
            rc |= SWT.MOD3;
        }
        if ((value & 0x08) != 0) {
            rc |= SWT.MOD4;
        }

        return rc;
    }

    void updateFontsFrom(FontData fontData) {
        Font oldFont = font;
        Font oldFontBold = fontBold;
        Font oldFontItalic = fontItalic;
        Font oldFontBoldItalic = fontBoldItalic;

        font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);
        fontBold = new Font(display, fontData.getName(), fontData.getHeight(), SWT.BOLD);
        fontItalic = new Font(display, fontData.getName(), fontData.getHeight(), SWT.ITALIC);
        fontBoldItalic = new Font(display, fontData.getName(), fontData.getHeight(), SWT.BOLD | SWT.ITALIC);

        applyTheme(preferences.getTheme());

        ruler.setFont(font);
        styledText.setFont(font);

        GC gc = new GC(styledText);
        gc.setFont(font);
        charSize = gc.stringExtent("A"); //$NON-NLS-1$
        gc.dispose();

        insertCaret.setSize(2, styledText.getLineHeight());
        overwriteCaret.setSize(charSize.x, styledText.getLineHeight());

        Image oldImage = alignCaret.getImage();
        Image image = new Image(display, charSize.x, styledText.getLineHeight());
        gc = new GC(image);
        try {
            gc.setBackground(new Color(0, 0, 0));
            gc.fillRectangle(0, 0, charSize.x, styledText.getLineHeight() - 2);
            gc.setBackground(new Color(255, 255, 255));
            gc.fillRectangle(0, styledText.getLineHeight() - 2, charSize.x, 2);
        } finally {
            gc.dispose();
        }
        alignCaret.setImage(image);
        if (oldImage != null) {
            oldImage.dispose();
        }

        if (oldFont != null) {
            oldFont.dispose();
            oldFontBold.dispose();
            oldFontItalic.dispose();
            oldFontBoldItalic.dispose();
        }
    }

    Color textBackground;
    Color textForeground;
    Color rulersBackground;
    Color rulersForeground;

    void applyTheme(String id) {
        textBackground = null;
        textForeground = null;
        rulersBackground = null;
        rulersForeground = null;

        styleMap.clear();

        if ("win32".equals(SWT.getPlatform()) && id == null) {
            if (Display.isSystemDarkTheme()) {
                id = "dark";
            }
        }

        if (id == null) {
            id = Display.isSystemDarkTheme() ? "dark" : "light";
            rulersBackground = ColorRegistry.getColor(ColorRegistry.WIDGET_BACKGROUND);
            rulersForeground = ColorRegistry.getColor(ColorRegistry.WIDGET_FOREGROUND);
            textBackground = ColorRegistry.getColor(ColorRegistry.LIST_BACKGROUND);
            textForeground = ColorRegistry.getColor(ColorRegistry.LIST_FOREGROUND);
        }
        else if ("dark".equals(id)) {
            textBackground = new Color(0x2A, 0x2A, 0x2A);
            textForeground = new Color(0xC7, 0xC7, 0xC7);
            rulersBackground = new Color(0x3B, 0x3B, 0x3B);
            rulersForeground = new Color(0x78, 0x92, 0x9B);
        }
        else if ("light".equals(id)) {
            textBackground = new Color(0xFE, 0xFE, 0xFE);
            textForeground = new Color(0x00, 0x00, 0x00);
            if ("win32".equals(SWT.getPlatform())) {
                rulersBackground = new Color(0xF0, 0xF0, 0xF0);
            }
            else {
                rulersBackground = new Color(0xFA, 0xFA, 0xFA);
            }
            rulersForeground = new Color(0x3B, 0x3B, 0x3B);
        }

        ruler.setBackground(rulersBackground);
        ruler.setForeground(rulersForeground);
        styledText.setBackground(textBackground);
        styledText.setForeground(textForeground);
        overview.setBackground(rulersBackground);
        overview.setForeground(rulersForeground);

        if ("dark".equals(id)) {
            currentLineBackground = new Color(display, 0x3B, 0x3B, 0x3B);

            styleMap.put(TokenId.COMMENT, new TextStyle(font, new Color(0x7E, 0x7E, 0x7E), null));
            styleMap.put(TokenId.SECTION, new TextStyle(fontBold, new Color(0x12, 0x90, 0xC3), null));

            styleMap.put(TokenId.NUMBER, new TextStyle(font, new Color(0x68, 0x97, 0xBB), null));
            styleMap.put(TokenId.STRING, new TextStyle(font, new Color(0x17, 0xC6, 0xA3), null));
            styleMap.put(TokenId.CONSTANT, new TextStyle(font, new Color(0xCC, 0x81, 0xBA), null));

            styleMap.put(TokenId.METHOD_PUB, new TextStyle(fontBold, new Color(0x12, 0x90, 0xC3), null));
            styleMap.put(TokenId.METHOD_PRI, new TextStyle(fontBoldItalic, new Color(0x12, 0x90, 0xC3), null));
            styleMap.put(TokenId.METHOD_LOCAL, new TextStyle(font, new Color(0xAF, 0xAF, 0x00), null));
            styleMap.put(TokenId.METHOD_RETURN, new TextStyle(font, new Color(0xCC, 0x6C, 0x1D), null));

            styleMap.put(TokenId.TYPE, new TextStyle(fontBold, new Color(0x12, 0x90, 0xC3), null));
            styleMap.put(TokenId.KEYWORD, new TextStyle(fontBold, new Color(0x00, 0xA0, 0x00), null));
            styleMap.put(TokenId.FUNCTION, new TextStyle(fontBold, new Color(0x00, 0xA0, 0x00), null));
            styleMap.put(TokenId.OBJECT, new TextStyle(font, new Color(0x66, 0xE1, 0xF8), null));
            styleMap.put(TokenId.VARIABLE, new TextStyle(font, new Color(0x66, 0xE1, 0xF8), null));

            styleMap.put(TokenId.DIRECTIVE, new TextStyle(font, new Color(0x20, 0x80, 0x20), null));

            //styleMap.put(TokenId.PASM_LABEL, new TextStyle(fontBold, null, null));
            styleMap.put(TokenId.PASM_LOCAL_LABEL, new TextStyle(fontItalic, null, null));
            styleMap.put(TokenId.PASM_CONDITION, new TextStyle(fontBold, null, null));
            styleMap.put(TokenId.PASM_INSTRUCTION, new TextStyle(fontBold, new Color(0xCC, 0x6C, 0x1D), null));
            styleMap.put(TokenId.PASM_MODIFIER, new TextStyle(fontBold, null, null));
        }
        else if ("light".equals(id)) {
            currentLineBackground = new Color(display, 0xE8, 0xF2, 0xFE);

            styleMap.put(TokenId.CON, new TextStyle(null, null, ColorRegistry.getColor(0xFF, 0xF8, 0xC0)));
            styleMap.put(TokenId.CON_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xFD, 0xF3, 0xA8)));
            styleMap.put(TokenId.VAR, new TextStyle(null, null, ColorRegistry.getColor(0xFF, 0xDF, 0xBF)));
            styleMap.put(TokenId.VAR_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xFD, 0xD2, 0xA7)));
            styleMap.put(TokenId.OBJ, new TextStyle(null, null, ColorRegistry.getColor(0xFF, 0xBF, 0xBF)));
            styleMap.put(TokenId.OBJ_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xFD, 0xA7, 0xA7)));
            styleMap.put(TokenId.PUB, new TextStyle(null, null, ColorRegistry.getColor(0xBF, 0xDF, 0xFF)));
            styleMap.put(TokenId.PUB_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xA7, 0xD2, 0xFD)));
            styleMap.put(TokenId.PRI, new TextStyle(null, null, ColorRegistry.getColor(0xBF, 0xF8, 0xFF)));
            styleMap.put(TokenId.PRI_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xA7, 0xF3, 0xFD)));
            styleMap.put(TokenId.DAT, new TextStyle(null, null, ColorRegistry.getColor(0xBF, 0xFF, 0xC8)));
            styleMap.put(TokenId.DAT_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xA7, 0xFD, 0xB3)));

            styleMap.put(TokenId.COMMENT, new TextStyle(font, new Color(0x7E, 0x7E, 0x7E), null));
            styleMap.put(TokenId.SECTION, new TextStyle(fontBold, new Color(0x00, 0x00, 0xA0), null));

            styleMap.put(TokenId.NUMBER, new TextStyle(font, new Color(0x00, 0x66, 0x99), null));
            styleMap.put(TokenId.STRING, new TextStyle(font, new Color(0x7E, 0x00, 0x7E), null));
            styleMap.put(TokenId.CONSTANT, new TextStyle(font, new Color(0x7E, 0x00, 0x7E), null));

            styleMap.put(TokenId.METHOD_PUB, new TextStyle(fontBold, new Color(0x00, 0x00, 0xA0), null));
            styleMap.put(TokenId.METHOD_PRI, new TextStyle(fontBoldItalic, new Color(0x00, 0x00, 0xA0), null));
            styleMap.put(TokenId.METHOD_LOCAL, new TextStyle(font, new Color(0x80, 0x80, 0x00), null));
            styleMap.put(TokenId.METHOD_RETURN, new TextStyle(font, new Color(0x90, 0x00, 0x00), null));

            styleMap.put(TokenId.TYPE, new TextStyle(fontBold, null, null));
            styleMap.put(TokenId.KEYWORD, new TextStyle(fontBold, new Color(0x00, 0x80, 0x00), null));
            styleMap.put(TokenId.FUNCTION, new TextStyle(fontBold, null, null));
            styleMap.put(TokenId.OBJECT, new TextStyle(font, new Color(0x00, 0x00, 0xC0), null));
            styleMap.put(TokenId.VARIABLE, new TextStyle(font, new Color(0x00, 0x00, 0xC0), null));

            styleMap.put(TokenId.DIRECTIVE, new TextStyle(font, new Color(0x00, 0x80, 0x00), null));

            //styleMap.put(TokenId.PASM_LABEL, new TextStyle(fontBold, null, null));
            styleMap.put(TokenId.PASM_LOCAL_LABEL, new TextStyle(fontItalic, null, null));
            styleMap.put(TokenId.PASM_CONDITION, new TextStyle(fontBold, null, null));
            styleMap.put(TokenId.PASM_INSTRUCTION, new TextStyle(fontBold, new Color(0x80, 0x00, 0x00), null));
            styleMap.put(TokenId.PASM_MODIFIER, new TextStyle(fontBold, null, null));
        }

        lineStylesCache.clear();
    }

    public void setTokenMarker(SourceTokenMarker tokenMarker) {
        this.tokenMarker = tokenMarker;
        this.tokenMarker.refreshTokens(styledText.getText());
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

    public LineNumbersRuler getRuler() {
        return ruler;
    }

    public StyledText getStyledText() {
        return styledText;
    }

    public void dispose() {
        container.dispose();
    }

    public boolean isDisposed() {
        return container.isDisposed();
    }

    public void setText(String text) {
        styledText.setRedraw(false);
        styledText.removeCaretListener(caretListener);
        try {
            ignoreModify = true;
            currentLine = 0;
            styledText.setText(text);
            undoStack.clear();
            redoStack.clear();

            ignoreModify = false;
            styledText.redraw();

        } finally {
            styledText.setRedraw(true);
            styledText.addCaretListener(caretListener);
        }
    }

    public String getText() {
        String text = styledText.getText();
        String result = text.replaceAll("[ \\t]+(\r\n|\n|\r)", "$1");
        if (!result.equals(text)) {
            int offset = styledText.getCaretOffset();
            int line = styledText.getLineAtOffset(offset);
            int column = offset - styledText.getOffsetAtLine(line);
            int topPixel = styledText.getTopPixel();

            styledText.setRedraw(false);
            ignoreModify = true;
            try {
                styledText.setText(result);
                styledText.setTopPixel(topPixel);

                tokenMarker.refreshTokens(result);

                String ls = styledText.getLine(line);
                styledText.setCaretOffset(styledText.getOffsetAtLine(line) + Math.min(column, ls.length()));
            } finally {
                styledText.setRedraw(true);
                styledText.redraw();
                ignoreModify = false;
            }
        }
        return result;
    }

    public void setBookmarks(Integer[] lines) {
        ruler.setBookmarks(lines);
    }

    public Integer[] getBookmarks() {
        return ruler.getBookmarks();
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
        int currentColumn = caretOffset - lineStart;

        String lineText = styledText.getLine(lineNumber);
        String leftText = lineText.substring(0, currentColumn);
        String rightText = lineText.substring(currentColumn);

        StringBuilder sb = new StringBuilder();
        sb.append(leftText);
        sb.append(styledText.getLineDelimiter());

        int indentColumn = 0;
        while (indentColumn < leftText.length() && Character.isWhitespace(leftText.charAt(indentColumn))) {
            indentColumn++;
        }
        String indent = StringUtils.repeat(' ', indentColumn);
        sb.append(indent);

        if (indentColumn < leftText.length()) {
            if (startsWithBlock(leftText.trim())) {
                boolean tabstopMatch = false;

                indentColumn++;

                Node node = tokenMarker.getSectionAtLine(lineNumber);
                if (node != null) {
                    int[] tabStops = Preferences.getInstance().getTabStops(node.getClass());
                    if (tabStops != null) {
                        for (int i = 0; i < tabStops.length; i++) {
                            if (tabStops[i] >= indentColumn) {
                                indentColumn = tabStops[i];
                                tabstopMatch = true;
                                break;
                            }
                        }
                    }
                }

                if (!tabstopMatch) {
                    int defaultTabStop = styledText.getTabs();
                    while ((indentColumn % defaultTabStop) != 0) {
                        indentColumn++;
                    }
                }

                sb.append(StringUtils.repeat(' ', indentColumn - indent.length()));
            }
        }

        caretOffset = lineStart + sb.length();

        if (tokenMarker instanceof CTokenMarker) {
            if (leftText.stripTrailing().endsWith("{")) {
                String text = styledText.getText();

                int nested = 0;

                int index = 0;
                while (index < text.length()) {
                    if (text.charAt(index) == '{') {
                        nested++;
                    }
                    else if (text.charAt(index) == '}') {
                        nested--;
                    }
                    index++;
                }
                if (nested != 0) {
                    sb.append(styledText.getLineDelimiter());
                    sb.append(indent);
                    sb.append("}");
                }
            }
        }

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
        String s = text.strip();

        if (s.equals("IF") || s.equals("ELSE") || s.equals("CASE")) {
            return true;
        }
        if (tokenMarker instanceof CTokenMarker) {
            if (text.endsWith(";")) {
                return false;
            }
            if (s.equals("FOR") || s.equals("DO") || s.equals("WHILE") || s.equals("SWITCH")) {
                return true;
            }
            if (text.endsWith("{") || text.endsWith(":")) {
                return true;
            }
        }
        else {
            if (s.equals("CON") || s.equals("VAR") || s.equals("OBJ") || s.equals("PUB") || s.equals("PRI")) {
                return true;
            }
            if (s.equals("REPEAT") || s.equals("IFNOT") || s.equals("ELSEIF") || s.equals("ELSEIFNOT")) {
                return true;
            }
        }

        return false;
    }

    void doTab() {
        String lineDelimiter = styledText.getLineDelimiter();

        Point editorSelection = styledText.getSelection();
        if (editorSelection.x != editorSelection.y) {
            int lineNumber = styledText.getLineAtOffset(editorSelection.x);
            int lineStart = styledText.getOffsetAtLine(lineNumber);
            String text = styledText.getText(editorSelection.x, editorSelection.y - 1);

            int currentColumn = Integer.MAX_VALUE;

            int index = 0;
            do {
                int eol = text.indexOf(lineDelimiter, index);

                if (eol != -1 && (eol - index) > lineDelimiter.length()) {
                    int count = index == 0 ? editorSelection.x - styledText.getOffsetAtLine(lineNumber) : 0;
                    while (index < text.length() && text.charAt(index) == ' ') {
                        count++;
                        index++;
                    }
                    currentColumn = Math.min(currentColumn, count);
                }

                index = eol;
                if (index != -1) {
                    index += lineDelimiter.length();
                }
            } while (index != -1 && index < text.length());

            boolean tabstopMatch = false;
            int nextTabColumn = currentColumn + 1;

            int[] tabStops = null;
            Node node = tokenMarker.getSectionAtLine(lineNumber);
            if (node != null) {
                tabStops = getBlockTabStops(node);
            }
            if (tabStops != null) {
                for (int i = 0; i < tabStops.length; i++) {
                    if (tabStops[i] >= nextTabColumn) {
                        nextTabColumn = tabStops[i];
                        tabstopMatch = true;
                        break;
                    }
                }
            }

            if (!tabstopMatch) {
                int defaultTabStop = 4;
                while ((nextTabColumn % defaultTabStop) != 0) {
                    nextTabColumn++;
                }
            }

            String spaces = (nextTabColumn - currentColumn) > 0 ? " ".repeat(nextTabColumn - currentColumn) : "";
            Point textSelection = new Point(editorSelection.x, editorSelection.y);

            index = 0;
            do {
                int eol = text.indexOf(lineDelimiter, index);
                if (eol == -1) {
                    eol = text.length();
                }

                if ((eol - index) > lineDelimiter.length()) {
                    int position = index;
                    while (position < text.length() && text.charAt(position) == ' ') {
                        position++;
                    }
                    text = text.substring(0, position) + spaces + text.substring(position);

                    if (index == 0 && editorSelection.x != lineStart) {
                        textSelection.x += spaces.length();
                    }
                    textSelection.y += spaces.length();

                    eol = text.indexOf(lineDelimiter, index);
                }

                index = eol;
                if (index != -1) {
                    index += lineDelimiter.length();
                }

            } while (index != -1 && index < text.length());

            styledText.setRedraw(false);
            try {
                styledText.replaceTextRange(editorSelection.x, editorSelection.y - editorSelection.x, text);
                styledText.setSelection(textSelection.x, textSelection.y);
                styledText.showSelection();
            } finally {
                styledText.setRedraw(true);
            }
        }
        else {
            int caretOffset = styledText.getCaretOffset();
            int lineNumber = styledText.getLineAtOffset(caretOffset);
            int lineStart = styledText.getOffsetAtLine(lineNumber);
            int currentColumn = caretOffset - lineStart;

            boolean tabstopMatch = false;
            int nextTabColumn = currentColumn + 1;

            Node node = tokenMarker.getSectionAtLine(lineNumber);
            if (node != null) {
                int[] tabStops = getBlockTabStops(node);
                if (tabStops != null) {
                    for (int i = 0; i < tabStops.length; i++) {
                        if (tabStops[i] >= nextTabColumn) {
                            nextTabColumn = tabStops[i];
                            tabstopMatch = true;
                            break;
                        }
                    }
                }
            }

            if (!tabstopMatch) {
                int defaultTabStop = 4;
                while ((nextTabColumn % defaultTabStop) != 0) {
                    nextTabColumn++;
                }
            }

            String text = styledText.getLine(lineNumber);

            if (styledText.getCaret() != insertCaret) {
                while (currentColumn < nextTabColumn && currentColumn < text.length() && text.charAt(currentColumn) == ' ') {
                    currentColumn++;
                }
            }

            StringBuilder sb = new StringBuilder();

            int start = caretOffset - lineStart;
            while (currentColumn < nextTabColumn) {
                sb.append(" ");
                if (styledText.getCaret() == alignCaret && start < text.length()) {
                    int i = text.indexOf("  ", start);
                    if (i != -1) {
                        styledText.setSelection(lineStart + i, lineStart + i + 1);
                        styledText.insert("");
                        text = text.substring(0, i) + text.substring(i + 1);
                    }
                }
                currentColumn++;
            }

            styledText.setRedraw(false);
            try {
                if (sb.length() != 0) {
                    styledText.setSelection(caretOffset, caretOffset);
                    styledText.insert(sb.toString());
                }
                styledText.setCaretOffset(lineStart + nextTabColumn);
                styledText.showSelection();
            } finally {
                styledText.setRedraw(true);
            }
        }
    }

    void doBacktab() {
        String lineDelimiter = styledText.getLineDelimiter();

        Point editorSelection = styledText.getSelection();
        if (editorSelection.x != editorSelection.y) {
            int lineNumber = styledText.getLineAtOffset(editorSelection.x);
            int lineStart = styledText.getOffsetAtLine(lineNumber);
            String text = styledText.getText(lineStart, editorSelection.y - 1);

            int currentColumn = Integer.MAX_VALUE;

            int index = 0;
            do {
                int eol = text.indexOf(lineDelimiter, index);
                if (eol == -1) {
                    eol = text.length();
                }

                if ((eol - index) > lineDelimiter.length()) {
                    int count = index == 0 ? editorSelection.x - styledText.getOffsetAtLine(lineNumber) : 0;
                    while (index < text.length() && text.charAt(index) == ' ') {
                        count++;
                        index++;
                    }
                    currentColumn = Math.min(currentColumn, count);
                }

                index = eol;
                if (index != -1) {
                    index += lineDelimiter.length();
                }
            } while (index != -1 && index < text.length());

            boolean tabstopMatch = false;
            int previousTabColumn = currentColumn - 1;

            int[] tabStops = null;
            Node node = tokenMarker.getSectionAtLine(lineNumber);
            if (node != null) {
                tabStops = getBlockTabStops(node);
            }
            if (tabStops != null) {
                for (int i = tabStops.length - 1; i >= 0; i--) {
                    if (previousTabColumn >= tabStops[i]) {
                        previousTabColumn = tabStops[i];
                        tabstopMatch = true;
                        break;
                    }
                }
                if (!tabstopMatch && tabStops.length > 0) {
                    previousTabColumn = 0;
                    tabstopMatch = true;
                }
            }

            if (!tabstopMatch) {
                int defaultTabStop = 4;
                previousTabColumn = currentColumn - 1;
                while ((previousTabColumn % defaultTabStop) != 0) {
                    previousTabColumn--;
                }
            }

            int spaces = currentColumn - previousTabColumn;
            if (spaces < 0) {
                spaces = 0;
            }
            Point textSelection = new Point(editorSelection.x, editorSelection.y);

            index = 0;
            do {
                int count = 0;
                while (index < text.length() && text.charAt(index) == ' ') {
                    count++;
                    index++;
                }
                if (count >= spaces) {
                    text = text.substring(0, index - spaces) + text.substring(index);
                    if ((lineStart + index) == editorSelection.x && editorSelection.x != lineStart) {
                        textSelection.x -= spaces;
                    }
                    textSelection.y -= spaces;
                    index -= spaces;
                }

                index = text.indexOf(lineDelimiter, index);
                if (index != -1) {
                    index += lineDelimiter.length();
                }

            } while (index != -1 && index < text.length());

            styledText.setRedraw(false);
            try {
                styledText.replaceTextRange(lineStart, editorSelection.y - lineStart, text);
                styledText.setSelection(textSelection.x, textSelection.y);
                styledText.showSelection();
            } finally {
                styledText.setRedraw(true);
            }
        }
        else {
            int caretOffset = styledText.getCaretOffset();
            int lineNumber = styledText.getLineAtOffset(caretOffset);
            int lineStart = styledText.getOffsetAtLine(lineNumber);
            String lineText = styledText.getLine(lineNumber);

            int currentColumn = caretOffset - lineStart;
            if (currentColumn == 0) {
                return;
            }
            int start = currentColumn;

            boolean tabstopMatch = false;
            int previousTabColumn = currentColumn - 1;

            Node node = tokenMarker.getSectionAtLine(lineNumber);
            if (node != null) {
                int[] tabStops = getBlockTabStops(node);
                if (tabStops != null) {
                    for (int i = tabStops.length - 1; i >= 0; i--) {
                        if (previousTabColumn >= tabStops[i]) {
                            previousTabColumn = tabStops[i];
                            tabstopMatch = true;
                            break;
                        }
                    }
                    if (!tabstopMatch && tabStops.length > 0) {
                        previousTabColumn = 0;
                        tabstopMatch = true;
                    }
                }
            }

            if (!tabstopMatch) {
                int defaultTabStop = 4;
                previousTabColumn = currentColumn - 1;
                while ((previousTabColumn % defaultTabStop) != 0) {
                    previousTabColumn--;
                }
            }

            if (styledText.getCaret() != insertCaret) {
                String line = styledText.getLine(lineNumber);
                while (currentColumn > 0 && currentColumn > previousTabColumn && line.charAt(currentColumn) == ' ') {
                    currentColumn--;
                    caretOffset--;
                }
            }

            while (currentColumn > 0 && currentColumn > previousTabColumn) {
                if (!Character.isWhitespace(lineText.charAt(currentColumn - 1))) {
                    break;
                }
                currentColumn--;
            }

            String text = styledText.getLine(lineNumber);

            styledText.setRedraw(false);
            try {
                if ((lineStart + currentColumn) < caretOffset) {
                    if (styledText.getCaret() == alignCaret) {
                        int i = text.indexOf("  ", start);
                        if (i == -1) {
                            i = text.indexOf(" '", start);
                            if (i == -1) {
                                i = text.indexOf(" {", start);
                            }
                        }
                        if (i != -1) {
                            styledText.setSelection(lineStart + i, lineStart + i);
                            styledText.insert(" ".repeat(caretOffset - (lineStart + currentColumn)));
                        }
                    }
                    styledText.setSelection(lineStart + currentColumn, caretOffset);
                    styledText.insert("");
                }
                styledText.setCaretOffset(lineStart + currentColumn);
                styledText.showSelection();
            } finally {
                styledText.setRedraw(true);
            }
        }
    }

    int[] getBlockTabStops(Node node) {
        return preferences.getTabStops(node.getClass());
    }

    public IContentProposal[] computeProposals(String contents, int position) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        int lineIndex = styledText.getLineAtOffset(styledText.getCaretOffset());
        Node node = tokenMarker.getContextAtLine(lineIndex);

        String filterText = getFilterText(node, contents, position);

        if (position == 0) {
            proposals.addAll(helpProvider.fillProposals("Root", filterText));
            if (tokenMarker instanceof CTokenMarker) {
                if (node == null) {
                    proposals.addAll(helpProvider.fillSourceProposals(filterText));
                }
            }
        }

        if (node instanceof DirectiveNode.IncludeNode) {
            proposals.addAll(helpProvider.fillSourceProposals(filterText));
        }
        else if (node instanceof ObjectNode) {
            proposals.addAll(helpProvider.fillSourceProposals(filterText));
        }
        else if (node instanceof VariablesNode) {
            proposals.addAll(tokenMarker.getTypeProposals(node, filterText));
        }
        else if (node instanceof VariableNode) {
            VariableNode line = (VariableNode) node;
            position = styledText.getCaretOffset();
            if (line.type != null && position >= line.type.start && position <= line.type.stop + 1) {
                proposals.addAll(helpProvider.fillSourceProposals(filterText));
            }
        }
        else if (node instanceof DataLineNode) {
            DataLineNode line = (DataLineNode) node;
            position = styledText.getCaretOffset();

            if (node.getStartToken().line != lineIndex || (line.condition == null && line.instruction == null)) {
                proposals.addAll(helpProvider.fillProposals("Condition", filterText));
                proposals.addAll(helpProvider.fillProposals("Instruction", filterText));
            }
            else if (line.condition != null && position >= line.condition.start && position <= line.condition.stop + 1) {
                proposals.addAll(helpProvider.fillProposals("Condition", filterText));
            }
            else if (line.condition != null && line.instruction == null && position > line.condition.stop) {
                proposals.addAll(helpProvider.fillProposals("Instruction", filterText));
            }
            else if (line.instruction != null && position >= line.instruction.start && position <= line.instruction.stop + 1) {
                proposals.addAll(helpProvider.fillProposals("Instruction", filterText));
            }
            else if (line.instruction != null && line.condition == null && position < line.instruction.start) {
                proposals.addAll(helpProvider.fillProposals("Condition", filterText));
            }
            else if (line.instruction != null && position > line.instruction.stop + 1) {
                if (node.getParent() instanceof StatementNode || node.getParent() instanceof MethodNode) {
                    proposals.addAll(tokenMarker.getInlinePAsmProposals(node, filterText));
                    proposals.addAll(tokenMarker.getConstantsProposals(node, filterText));
                }
                else {
                    proposals.addAll(tokenMarker.getPAsmProposals(node, filterText));
                }
                proposals.addAll(helpProvider.fillProposals(node.getClass().getSimpleName(), filterText));
            }
        }
        else if (node instanceof MethodNode) {
            boolean found = false;
            MethodNode methodNode = (MethodNode) node;
            for (MethodNode.ParameterNode child : methodNode.getParameters()) {
                if (child.type != null && position >= child.type.start && position <= child.type.stop + 1) {
                    proposals.addAll(tokenMarker.getTypeProposals(node, filterText));
                    found = true;
                }
                if (child.identifier != null && position >= child.identifier.start && position <= child.identifier.stop + 1) {
                    found = true;
                }
            }
            for (MethodNode.LocalVariableNode child : methodNode.getLocalVariables()) {
                if (child.type != null && position >= child.type.start && position <= child.type.stop + 1) {
                    proposals.addAll(tokenMarker.getTypeProposals(node, filterText));
                    found = true;
                }
                if (child.identifier != null && position >= child.identifier.start && position <= child.identifier.stop + 1) {
                    found = true;
                }
            }
            for (MethodNode.ReturnNode child : methodNode.getReturnVariables()) {
                if (child.type != null && position >= child.type.start && position <= child.type.stop + 1) {
                    proposals.addAll(tokenMarker.getTypeProposals(node, filterText));
                    found = true;
                }
                if (child.identifier != null && position >= child.identifier.start && position <= child.identifier.stop + 1) {
                    found = true;
                }
            }
            if (!found) {
                proposals.addAll(tokenMarker.getTypeProposals(node, filterText));
            }
        }
        else if (node != null) {
            if ((node instanceof StatementNode) || (node instanceof MethodNode) || (node instanceof FunctionNode)) {
                proposals.addAll(tokenMarker.getMethodProposals(node, filterText));
                proposals.addAll(tokenMarker.getPAsmLabelProposals(node, filterText));
            }
            proposals.addAll(tokenMarker.getConstantsProposals(node, filterText));
            proposals.addAll(helpProvider.fillProposals(node.getClass().getSimpleName(), filterText));
        }

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

    public void goToLineColumn(int line, int column) {
        if (line >= styledText.getLineCount()) {
            return;
        }

        Rectangle rect = styledText.getClientArea();
        int topLine = styledText.getLineIndex(0);
        int bottomLine = styledText.getLineIndex(rect.height);
        int halfPageSize = (bottomLine - topLine - 1) / 2;

        if (line < topLine || line > bottomLine) {
            styledText.setTopIndex((line - halfPageSize) >= 0 ? line - halfPageSize : 0);
        }

        styledText.setCaretOffset(styledText.getOffsetAtLine(line) + column);
    }

    String getFilterText(Node context, String contents, int position) {
        char ch = 0;
        int start = position;

        while (start > 0) {
            if (context instanceof DataLineNode) {
                if (contents.charAt(start - 1) == '#') {
                    break;
                }
            }
            ch = contents.charAt(start - 1);
            if (!Character.isLetterOrDigit(ch) && ch != '.' && ch != ':' && ch != '#' && ch != '_') {
                break;
            }
            start--;
        }

        String name = contents.substring(start, position);

        if (name.startsWith(".") && start > 0 && contents.charAt(start - 1) == ']') {
            start -= 2;
            int depth = 0;
            while (start > 0) {
                ch = contents.charAt(start);
                if (ch == ']') {
                    depth++;
                }
                if (ch == '[') {
                    if (depth == 0) {
                        position = start;
                        break;
                    }
                    depth--;
                }
                start--;
            }

            while (start > 0) {
                if (context instanceof DataLineNode) {
                    if (contents.charAt(start - 1) == '#') {
                        break;
                    }
                }
                ch = contents.charAt(start - 1);
                if (!Character.isLetterOrDigit(ch) && ch != '.' && ch != ':' && ch != '#' && ch != '_') {
                    break;
                }
                start--;
            }

            name = contents.substring(start, position) + name;
        }

        return name;
    }

    public void setCompilerMessages(List<CompilerException> messages) {
        tokenMarker.refreshCompilerTokens(messages);
        lineStylesCache.clear();

        ruler.clearHighlights();
        overview.clearHighlights();
        for (CompilerException msg : messages) {
            if (msg.type == CompilerException.ERROR) {
                ruler.setHighlight(msg.line);
                overview.setErrorHighlight(msg.line, msg.getMessage());
            }
            else if (msg.type == CompilerException.WARNING) {
                overview.setWarningHighlight(msg.line, msg.getMessage());
            }
        }
    }

    public void redraw() {
        styledText.redraw();
        ruler.redraw();
        overview.redraw();
    }

    public Color getLineBackground(Node root, int lineOffset) {
        if (tokenMarker != null) {
            TokenId id = tokenMarker.getLineBackgroundId(root, lineOffset);
            if (id != null) {
                TextStyle style = styleMap.get(id);
                if (style != null) {
                    return style.background;
                }
            }
        }
        return null;
    }

    public void addSourceListener(SourceListener listener) {
        openListeners.add(listener);
    }

    public void removeSourceListener(SourceListener listener) {
        openListeners.remove(listener);
    }

    public void fireNavigateToEvent(final SourceElement element) {
        for (SourceListener l : openListeners) {
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.navigateTo(element);
                }
            });
        }
    }

    public void format(Formatter formatter) {
        int topPixel = styledText.getTopPixel();
        int caretOffset = styledText.getCaretOffset();
        int caretLine = styledText.getLineAtOffset(caretOffset);
        int caretColumn = caretOffset - styledText.getOffsetAtLine(caretLine);

        styledText.setRedraw(false);
        styledText.removeCaretListener(caretListener);
        try {
            ignoreModify = true;

            String text = styledText.getSelectionText();
            if (!text.isEmpty()) {
                text = formatter.format(text);
                Point selection = styledText.getSelection();
                styledText.replaceTextRange(selection.x, selection.y - selection.x + 1, text);
            }
            else {
                text = formatter.format(styledText.getText());
                styledText.setText(text);
            }

            if (caretLine > styledText.getLineCount()) {
                caretLine = styledText.getLineCount() - 1;
            }

            styledText.setTopPixel(topPixel);
            styledText.setCaretOffset(styledText.getOffsetAtLine(caretLine) + caretColumn);

            ignoreModify = false;
            styledText.redraw();

            styledText.notifyListeners(SWT.Modify, new Event());

        } finally {
            styledText.setRedraw(true);
            styledText.addCaretListener(caretListener);
        }
    }

    public Node getNodeAtOffset(int offset) {
        Node result = null;

        Node root = tokenMarker.getRoot();
        for (Node node : root.getChilds()) {
            if (offset < node.getStartIndex()) {
                break;
            }
            result = getNodeAtOffset(node, offset, node);
        }

        return result;
    }

    private Node getNodeAtOffset(Node root, int offset, Node result) {
        for (Node node : root.getChilds()) {
            if (offset < node.getStartIndex()) {
                break;
            }
            if (node instanceof ObjectNode) {
                result = node;
            }
            else {
                result = getNodeAtOffset(node, offset, node);
            }
        }
        return result;
    }

    public static class NavigationTarget extends SourceElement {
        int start;
        int stop;

        public NavigationTarget(int line, int column) {
            super(null, line, column);
        }

        public NavigationTarget(Token token, Token target) {
            this(token, null, target);
        }

        public NavigationTarget(Token token, Node object) {
            this(token, object, null);
        }

        public NavigationTarget(Token token, Node object, Token target) {
            super(object, target != null ? target.line : 0, target != null ? target.column : 0);
            this.start = token.start;
            if (token.getText().startsWith("@@")) {
                this.start += 2;
            }
            if (token.getText().startsWith("@")) {
                this.start += 1;
            }
            this.stop = token.stop;
        }

    }

    NavigationTarget getNavigationTarget(int offset) {
        Token token = tokenMarker.getTokenAt(offset);
        if (token == null || token.type == Token.EOF) {
            return null;
        }

        String itemName = token.getText();
        if (itemName.startsWith("@@")) {
            itemName = itemName.substring(2);
        }
        if (itemName.startsWith("@")) {
            itemName = itemName.substring(1);
        }

        Node context = getNodeAtOffset(offset);

        if (context instanceof ObjectNode) {
            ObjectNode obj = (ObjectNode) context;
            if (obj.count != null && obj.count.contains(token)) {
                context = obj.count;
            }
            if (obj.file == token) {
                return new NavigationTarget(token, context);
            }
            for (ObjectNode.ParameterNode node : obj.parameters) {
                if (node.expression.contains(token)) {
                    context = node.expression;
                    break;
                }
            }
        }
        else if (context instanceof DirectiveNode.IncludeNode) {
            if (((DirectiveNode.IncludeNode) context).getFile() == token) {
                return new NavigationTarget(token, context);
            }
        }
        else if (context instanceof VariableNode) {
            VariableNode obj = (VariableNode) context;
            if (obj.getType() == token) {
                if ("LONG".equalsIgnoreCase(token.getText()) || "WORD".equalsIgnoreCase(token.getText()) || "BYTE".equalsIgnoreCase(token.getText())) {
                    return null;
                }
                if ("int".equalsIgnoreCase(token.getText()) || "float".equalsIgnoreCase(token.getText())) {
                    return null;
                }
                return new NavigationTarget(token, context);
            }
        }
        else if (context instanceof DataLineNode.ParameterNode) {
            Node lineNode = context.getParent();
            Node parent = lineNode.getParent();

            if (itemName.startsWith(tokenMarker.localLabelPrefix)) {
                int index = parent.getChilds().indexOf(lineNode);
                while (index > 0) {
                    if (parent.getChild(index) instanceof DataLineNode) {
                        DataLineNode obj = (DataLineNode) parent.getChild(index);
                        if (obj.instruction != null && "NAMESP".equalsIgnoreCase(obj.instruction.getText())) {
                            break;
                        }
                        if (obj.label != null) {
                            if (!obj.label.getText().startsWith(tokenMarker.localLabelPrefix)) {
                                break;
                            }
                            if (obj.label.equals(itemName, tokenMarker.isCaseSensitive())) {
                                return new NavigationTarget(token, obj.label);
                            }
                        }
                    }
                    index--;
                }
                index = parent.getChilds().indexOf(lineNode) + 1;
                while (index < parent.getChildCount()) {
                    if (parent.getChild(index) instanceof DataLineNode) {
                        DataLineNode obj = (DataLineNode) parent.getChild(index);
                        if (obj.instruction != null && "NAMESP".equalsIgnoreCase(obj.instruction.getText())) {
                            break;
                        }
                        if (obj.label != null) {
                            if (!obj.label.getText().startsWith(tokenMarker.localLabelPrefix)) {
                                break;
                            }
                            if (obj.label.equals(itemName, tokenMarker.isCaseSensitive())) {
                                return new NavigationTarget(token, obj.label);
                            }
                        }
                    }
                    index++;
                }
            }
            else if (itemName.indexOf('.') == -1) {
                int index = parent.getChilds().indexOf(lineNode);
                while (index > 0) {
                    if (parent.getChild(index) instanceof DataLineNode) {
                        DataLineNode obj = (DataLineNode) parent.getChild(index);
                        if (obj.instruction != null && "NAMESP".equalsIgnoreCase(obj.instruction.getText())) {
                            break;
                        }
                        if (obj.label != null) {
                            if (!obj.label.getText().startsWith(tokenMarker.localLabelPrefix)) {
                                if (obj.label.equals(itemName, tokenMarker.isCaseSensitive())) {
                                    return new NavigationTarget(token, obj.label);
                                }
                            }
                        }
                    }
                    index--;
                }
                index = parent.getChilds().indexOf(lineNode) + 1;
                while (index < parent.getChildCount()) {
                    if (parent.getChild(index) instanceof DataLineNode) {
                        DataLineNode obj = (DataLineNode) parent.getChild(index);
                        if (obj.instruction != null && "NAMESP".equalsIgnoreCase(obj.instruction.getText())) {
                            break;
                        }
                        if (obj.label != null) {
                            if (!obj.label.getText().startsWith(tokenMarker.localLabelPrefix)) {
                                if (obj.label.equals(itemName, tokenMarker.isCaseSensitive())) {
                                    return new NavigationTarget(token, obj.label);
                                }
                            }
                        }
                    }
                    index++;
                }
            }

            String namespace = "";

            for (Node node : tokenMarker.getRoot().getChilds()) {
                if (node instanceof DataNode) {
                    for (Node child : node.getChilds()) {
                        if (!(child instanceof DataLineNode)) {
                            continue;
                        }
                        DataLineNode obj = (DataLineNode) child;
                        if (obj.instruction != null && "NAMESP".equalsIgnoreCase(obj.instruction.getText())) {
                            namespace = obj.parameters.size() != 0 ? obj.parameters.get(0).getText() + "." : "";
                        }
                        if (obj.label != null) {
                            String qualifiedName = namespace + obj.label.getText();
                            if (tokenMarker.isCaseSensitive() ? qualifiedName.equals(itemName) : qualifiedName.equalsIgnoreCase(itemName)) {
                                return new NavigationTarget(token, obj.label);
                            }
                        }
                    }
                }
            }

            if ((parent instanceof StatementNode) || (parent instanceof MethodNode)) {
                for (int index = 0; index < parent.getChildCount(); index++) {
                    if (parent.getChild(index) instanceof DataLineNode) {
                        DataLineNode obj = (DataLineNode) parent.getChild(index);
                        if (obj.label != null) {
                            if (obj.label.equals(itemName, tokenMarker.isCaseSensitive())) {
                                return new NavigationTarget(token, obj.label);
                            }
                        }
                    }
                }
                while (!(parent instanceof MethodNode)) {
                    parent = parent.getParent();
                }
                MethodNode node = (MethodNode) parent;
                for (MethodNode.ParameterNode obj : node.getParameters()) {
                    if (obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                        return new NavigationTarget(token, obj.identifier);
                    }
                }
                for (MethodNode.LocalVariableNode obj : node.getLocalVariables()) {
                    if (obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                        return new NavigationTarget(token, obj.identifier);
                    }
                }
                for (MethodNode.ReturnNode obj : node.getReturnVariables()) {
                    if (obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                        return new NavigationTarget(token, obj.identifier);
                    }
                }
                return null;
            }
        }

        int objstart = -1;
        int objstop = -1;
        String objectName = null;

        int dot = itemName.indexOf('.');
        if (dot == -1) {
            dot = itemName.indexOf('#');
        }
        if (dot == 0) {
            int line = styledText.getLineAtOffset(offset);
            int lineOffset = styledText.getOffsetAtLine(line);
            String lineText = styledText.getLine(line);
            int endIndex = token.start - lineOffset - 1;
            if (endIndex > 0) {
                if (lineText.charAt(endIndex) == ']') {
                    int depth = -1;
                    while (endIndex >= 0) {
                        if (lineText.charAt(endIndex) == ']') {
                            depth++;
                        }
                        else if (lineText.charAt(endIndex) == '[') {
                            if (depth == 0) {
                                break;
                            }
                            depth--;
                        }
                        endIndex--;
                    }
                    endIndex--;
                }
                if (endIndex >= 0) {
                    Token objectToken = tokenMarker.getTokenAt(endIndex + lineOffset);
                    if (objectToken != null) {
                        objstart = objectToken.start;
                        objstop = objectToken.stop;
                        objectName = objectToken.getText();
                        if (objectName.startsWith("@")) {
                            objectName = objectName.substring(1);
                            objstart++;
                        }
                    }
                }
                itemName = itemName.substring(1);
            }
        }
        else if (dot != -1) {
            objstart = token.start;
            objstop = token.start + dot - 1;
            objectName = itemName.substring(0, dot);
            itemName = itemName.substring(dot + 1);
        }

        if (objectName == null && (context instanceof StatementNode)) {
            Node parent = context.getParent();
            while (parent != null && !(parent instanceof MethodNode)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                MethodNode node = (MethodNode) parent;
                for (MethodNode.ParameterNode obj : node.getParameters()) {
                    if (obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                        return new NavigationTarget(token, obj.identifier);
                    }
                }
                for (MethodNode.LocalVariableNode obj : node.getLocalVariables()) {
                    if (obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                        return new NavigationTarget(token, obj.identifier);
                    }
                }
                for (MethodNode.ReturnNode obj : node.getReturnVariables()) {
                    if (obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                        return new NavigationTarget(token, obj.identifier);
                    }
                }
            }
        }

        if ((context instanceof StatementNode) || (context instanceof ExpressionNode) || (context instanceof DataLineNode.ParameterNode)) {
            Node root = tokenMarker.getRoot();
            if (objectName != null) {
                for (Node node : root.getChilds()) {
                    if (node instanceof ObjectsNode) {
                        for (Node child : node.getChilds()) {
                            if (!(child instanceof ObjectNode)) {
                                continue;
                            }
                            ObjectNode obj = (ObjectNode) child;
                            if (objectName != null) {
                                if (offset >= objstart && offset <= objstop) {
                                    if (obj.name.equals(objectName, tokenMarker.isCaseSensitive())) {
                                        Token highlight =
                                            new Token(token.getStream(), token.start, token.line, token.column, token.type, token.getText().substring(0, dot));
                                        return new NavigationTarget(highlight, obj.name);
                                    }
                                }
                                else if (obj.name.equals(objectName, tokenMarker.isCaseSensitive())) {
                                    String fileName = obj.getFileName();
                                    Node objectRoot = tokenMarker.getObjectTree(fileName);
                                    if (objectRoot != null) {
                                        for (Node objectNode : objectRoot.getChilds()) {
                                            if (objectNode instanceof ConstantsNode) {
                                                for (Node objectChildNode : objectNode.getChilds()) {
                                                    if (objectChildNode instanceof ConstantNode) {
                                                        ConstantNode constant = (ConstantNode) objectChildNode;
                                                        if (constant.identifier != null && constant.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                                                            Token highlight =
                                                                new Token(token.getStream(), token.start + dot + 1, token.line, token.column + dot + 1, token.type, token.getText().substring(dot + 1));
                                                            return new NavigationTarget(highlight, obj, constant.identifier);
                                                        }
                                                    }
                                                }
                                            }
                                            else if (objectNode instanceof MethodNode) {
                                                MethodNode method = (MethodNode) objectNode;
                                                if (method.name.equals(itemName, tokenMarker.isCaseSensitive())) {
                                                    Token highlight =
                                                        new Token(token.getStream(), token.start + dot + 1, token.line, token.column + dot + 1, token.type, token.getText().substring(dot + 1));
                                                    return new NavigationTarget(highlight, obj, method.name);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                for (Node node : root.getChilds()) {
                    NavigationTarget target = getNavigationTarget(node, token, itemName);
                    if (target != null) {
                        return target;
                    }
                }
            }
        }

        return null;
    }

    NavigationTarget getNavigationTarget(Node node, Token token, String itemName) {

        if (node instanceof ObjectsNode) {
            for (Node child : node.getChilds()) {
                if (!(child instanceof ObjectNode)) {
                    continue;
                }
                ObjectNode obj = (ObjectNode) child;
                if (obj.name.equals(itemName, tokenMarker.isCaseSensitive())) {
                    return new NavigationTarget(token, obj.name);
                }
            }
        }
        else if (node instanceof ConstantsNode) {
            for (Node child : node.getChilds()) {
                if (!(child instanceof ConstantNode)) {
                    continue;
                }
                ConstantNode obj = (ConstantNode) child;
                if (obj.identifier != null && obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                    return new NavigationTarget(token, obj.identifier);
                }
            }
        }
        else if (node instanceof MethodNode) {
            MethodNode method = (MethodNode) node;
            if (method.name.equals(itemName, tokenMarker.isCaseSensitive())) {
                return new NavigationTarget(token, method.name);
            }
        }
        else if (node instanceof VariablesNode) {
            for (Node child : node.getChilds()) {
                if (child instanceof VariableNode) {
                    VariableNode obj = (VariableNode) child;
                    if (obj.identifier != null && obj.identifier.equals(itemName, tokenMarker.isCaseSensitive())) {
                        return new NavigationTarget(token, obj.identifier);
                    }
                }
                else {
                    NavigationTarget target = getNavigationTarget(child, token, itemName);
                    if (target != null) {
                        return target;
                    }
                }
            }
        }
        else if (node instanceof DataNode) {
            String namespace = "";
            for (Node child : node.getChilds()) {
                if (!(child instanceof DataLineNode)) {
                    continue;
                }
                DataLineNode obj = (DataLineNode) child;
                if (obj.instruction != null && "NAMESP".equalsIgnoreCase(obj.instruction.getText())) {
                    namespace = obj.parameters.size() != 0 ? obj.parameters.get(0).getText() + "." : "";
                }
                if (obj.label != null) {
                    String qualifiedName = namespace + obj.label.getText();
                    if (tokenMarker.isCaseSensitive() ? qualifiedName.equals(itemName) : qualifiedName.equalsIgnoreCase(itemName)) {
                        return new NavigationTarget(token, obj.label);
                    }
                }
            }
        }

        return null;
    }

    public void setBackground(Color color) {
        styledText.setBackground(color);
    }

    public void setForeground(Color color) {
        styledText.setForeground(color);
    }

    public void setCurrentLineBackground(Color color) {
        currentLineBackground = color;
        styledText.redraw();
    }

    public void setIndentLinesForeground(Color color) {
        indentLinesForeground = color;
        styledText.redraw();
    }

    public void makeSkipPattern() {
        String text = styledText.getSelectionText();
        String pattern = Utils.makeSkipPattern(text);

        if (pattern.length() == 0) {
            MessageDialog.openWarning(styledText.getShell(), SpinTools.APP_TITLE, "No pattern detected");
            return;
        }

        Clipboard clipboard = new Clipboard(display);
        try {
            clipboard.setContents(new Object[] {
                pattern,
            }, new Transfer[] {
                TextTransfer.getInstance(),
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        clipboard.dispose();

        MessageDialog.openInformation(styledText.getShell(), SpinTools.APP_TITLE, "Skip pattern copied to clipboard:" + styledText.getLineDelimiter() + styledText.getLineDelimiter() + pattern);
    }

    public void navigateToBookmark(int num) {
        Integer line = ruler.getBookmark(num);
        if (line != null) {
            NavigationTarget target = new NavigationTarget(line, 0);
            fireNavigateToEvent(target);
        }
    }

}
