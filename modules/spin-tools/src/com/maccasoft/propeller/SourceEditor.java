/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ContentProposalAdapter;
import com.maccasoft.propeller.internal.HTMLStyledTextDecorator;
import com.maccasoft.propeller.internal.IContentProposalListener2;
import com.maccasoft.propeller.internal.StyledTextContentAdapter;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class SourceEditor {

    private static final int UNDO_LIMIT = 500;
    private static final int CURRENT_CHANGE_TIMER_EXPIRE = 500;

    Display display;
    SashForm sashForm;
    Composite container;
    LineNumbersRuler ruler;
    OutlineView outline;
    StyledText styledText;
    OverviewRuler overview;

    boolean showIndentLines;
    int indentLinesSize;

    Font font;
    Font fontBold;
    Font fontItalic;
    Font fontBoldItalic;
    Point charSize;

    int currentLine;
    Color currentLineBackground;

    Caret insertCaret;
    Caret overwriteCaret;
    boolean modified;

    int[] sectionCount = new int[6];

    SourceTokenMarker tokenMarker;
    Map<TokenId, TextStyle> styleMap = new HashMap<TokenId, TextStyle>();

    EditorHelp helpProvider;

    boolean ignoreUndo;
    boolean ignoreRedo;
    TextChange currentChange;
    Stack<TextChange> undoStack = new Stack<TextChange>();
    Stack<TextChange> redoStack = new Stack<TextChange>();

    boolean ignoreModify;
    ListenerList<ModifyListener> modifyListeners = new ListenerList<ModifyListener>();

    private ListenerList<IOpenListener> openListeners = new ListenerList<>();

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
            display.timerExec(250, outlineSyncRunnable);
        }
    };

    final Runnable outlineSyncRunnable = new Runnable() {

        @Override
        public void run() {
            if (styledText.isDisposed()) {
                return;
            }
            int caretOffset = styledText.getCaretOffset();
            int line = styledText.getLineAtOffset(caretOffset);
            Node selection = getCaretNode(caretOffset, line);
            outline.removeSelectionChangedListener(outlineSelectionListener);
            try {
                outline.setSelection(selection != null ? new StructuredSelection(selection) : StructuredSelection.EMPTY);
            } finally {
                outline.addSelectionChangedListener(outlineSelectionListener);
            }
        }

        Node getCaretNode(int offset, int line) {
            Node selection = null;

            Node root = tokenMarker.getRoot();
            if (root != null) {
                for (Node node : root.getChilds()) {
                    if (node.getStartToken() == null) {
                        continue;
                    }
                    int start = node.getStartToken().start - node.getStartToken().column;
                    if (node instanceof MethodNode) {
                        if (offset < start) {
                            return selection;
                        }
                        selection = node;
                    }
                    else if (node instanceof DataNode) {
                        if (offset < start) {
                            return selection;
                        }
                        selection = node;
                        for (Node child : node.getChilds()) {
                            Token token = child.getStartToken();
                            if (token != null) {
                                start = token.start - token.column;
                                if (offset < start) {
                                    return selection;
                                }
                                DataLineNode dataLine = (DataLineNode) child;
                                if (dataLine.label != null && !dataLine.label.getText().startsWith(".")) {
                                    selection = child;
                                }
                            }
                        }
                    }
                    else {
                        if (offset < start) {
                            return selection;
                        }
                        selection = node;

                        int childLine = -1;
                        Node childSelection = selection;
                        for (Node child : node.getChilds()) {
                            Token token = child.getStartToken();
                            if (token != null) {
                                start = token.start;
                                if (token.line != childLine) {
                                    start -= token.column;
                                    childLine = token.line;
                                }
                                if (offset < start) {
                                    return childSelection;
                                }
                                childSelection = child;
                            }
                        }

                        if (line == childSelection.getStartToken().line) {
                            return childSelection;
                        }
                    }
                }
            }

            return selection;
        }

    };

    final ISelectionChangedListener outlineSelectionListener = new ISelectionChangedListener() {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            Node node = (Node) event.getStructuredSelection().getFirstElement();
            if (node != null) {
                int offset = node.getStartIndex();
                int line = styledText.getLineAtOffset(offset);
                int column = offset - styledText.getOffsetAtLine(line);
                SourceElement element = new SourceElement(null, line, column);
                display.asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(element)));
                    }

                });
                styledText.setFocus();
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
                case Preferences.PROP_SHOW_INDENT_LINES:
                    showIndentLines = (Boolean) evt.getNewValue();
                    styledText.redraw();
                    break;
                case Preferences.PROP_SHOW_EDITOR_OUTLINE:
                    outline.setVisible((Boolean) evt.getNewValue());
                    sashForm.layout(true);
                    break;
                case Preferences.PROP_SHOW_SECTIONS_BACKGROUND:
                    styledText.redraw();
                    break;
            }
        }
    };

    Shell popupWindow;
    Rectangle popupMouseBounds;

    boolean hoverHighlight;
    Token hoverHighlightToken;
    Token hoverToken;
    String hoverItemName;

    Preferences preferences;

    public SourceEditor(Composite parent) {
        display = parent.getDisplay();
        preferences = Preferences.getInstance();

        sashForm = new SashForm(parent, SWT.HORIZONTAL);

        container = new Composite(sashForm, SWT.NO_FOCUS);
        GridLayout containerLayout = new GridLayout(3, false);
        containerLayout.horizontalSpacing = 1;
        containerLayout.marginWidth = containerLayout.marginHeight = 0;
        container.setLayout(containerLayout);

        currentLine = 0;
        currentLineBackground = new Color(display, 0xE8, 0xF2, 0xFE);

        ruler = new LineNumbersRuler(container);

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
                    case ST.TOGGLE_OVERWRITE:
                        if (styledText.getCaret() == insertCaret) {
                            overwriteCaret.setLocation(styledText.getCaret().getLocation());
                            styledText.setCaret(overwriteCaret);
                        }
                        else {
                            insertCaret.setLocation(styledText.getCaret().getLocation());
                            styledText.setCaret(insertCaret);
                        }
                        super.invokeAction(action);
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

        overview = new OverviewRuler(container);
        overview.setStyledText(styledText);

        outline = new OutlineView(sashForm);
        outline.addSelectionChangedListener(outlineSelectionListener);
        outline.setVisible(preferences.getShowEditorOutline());

        sashForm.setWeights(new int[] {
            8000, 2000
        });

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (preferences.getEditorFont() != null) {
            fontData = StringConverter.asFontData(preferences.getEditorFont());
        }
        fontData.setStyle(SWT.NONE);
        updateFontsFrom(fontData);

        showIndentLines = preferences.getShowIndentLines();
        indentLinesSize = preferences.getIndentLinesSize();

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
                if (e.keyCode == SWT.CTRL) {
                    if (hoverToken != null) {
                        styledText.setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
                    }
                    hoverHighlight = true;
                    styledText.redraw();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CTRL) {
                    hoverHighlight = false;
                    styledText.setCursor(null);
                    styledText.redraw();
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
                            outline.setInput(tokenMarker.getRoot());
                            //display.timerExec(500, refreshViewRunnable);
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

        styledText.addLineBackgroundListener(new LineBackgroundListener() {

            @Override
            public void lineGetBackground(LineBackgroundEvent event) {
                if (preferences.getShowSectionsBackground()) {
                    event.lineBackground = getLineBackground(tokenMarker.getRoot(), event.lineOffset);
                }
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

        styledText.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (hoverToken == null) {
                    return;
                }
                int offset = styledText.getOffsetAtPoint(new Point(e.x, e.y));
                if (offset == -1) {
                    return;
                }
                Token token = tokenMarker.getTokenAt(offset);
                if (token == null || token.type == Token.EOF) {
                    return;
                }

                String itemName = token.getText();
                if (itemName.startsWith("@")) {
                    itemName = itemName.substring(1);
                }
                int dot = itemName.indexOf('.');

                int objstart = -1;
                int objstop = -1;
                String objectName = null;

                if (dot == 0) {
                    int line = styledText.getLineAtOffset(offset);
                    int lineOffset = styledText.getOffsetAtLine(line);
                    String lineText = styledText.getLine(line);
                    int endIndex = token.start - lineOffset - 1;
                    if (lineText.charAt(endIndex) == ']') {
                        while (endIndex >= 0 && lineText.charAt(endIndex) != '[') {
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
                else if (dot != -1) {
                    objstart = token.start;
                    objstop = token.start + dot - 1;
                    objectName = itemName.substring(0, dot);
                    itemName = itemName.substring(dot + 1);
                }

                Node context = tokenMarker.getContextAtLine(styledText.getLineAtOffset(offset));

                if (context instanceof ObjectNode) {
                    ObjectNode obj = (ObjectNode) context;
                    if (obj.file == token) {
                        display.asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(obj)));
                            }

                        });
                        hoverToken = hoverHighlightToken = null;
                        hoverHighlight = false;
                        styledText.setCursor(null);
                        styledText.redraw();
                    }
                }
                else if (context instanceof DataLineNode) {
                    Node root = tokenMarker.getRoot();
                    for (Node node : root.getChilds()) {
                        if (node instanceof DataNode) {
                            for (Node child : node.getChilds()) {
                                DataLineNode obj = (DataLineNode) child;
                                if (obj.label != null && obj.label.getText().equals(itemName)) {
                                    SourceElement element = new SourceElement(null, obj.label.line, obj.label.column);
                                    display.asyncExec(new Runnable() {

                                        @Override
                                        public void run() {
                                            fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(element)));
                                        }

                                    });
                                    break;
                                }
                            }
                        }
                    }
                }
                else if (context instanceof StatementNode) {
                    Node root = tokenMarker.getRoot();
                    if (objectName != null) {
                        for (Node node : root.getChilds()) {
                            if (node instanceof ObjectsNode) {
                                if (offset >= objstart && offset <= objstop) {
                                    if (openLinkedObject(node, objectName)) {
                                        break;
                                    }
                                }
                                else if (openLinkedObjectMethod(node, objectName, itemName)) {
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        for (Node node : root.getChilds()) {
                            if (node instanceof ObjectsNode) {
                                if (openLinkedObject(node, itemName)) {
                                    break;
                                }
                            }
                            else if (node instanceof MethodNode) {
                                MethodNode method = (MethodNode) node;
                                if (method.name.getText().equals(itemName)) {
                                    SourceElement element = new SourceElement(null, method.name.line, method.name.column);
                                    display.asyncExec(new Runnable() {

                                        @Override
                                        public void run() {
                                            fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(element)));
                                        }

                                    });
                                    break;
                                }
                            }
                            else if (node instanceof VariablesNode) {
                                for (Node child : node.getChilds()) {
                                    VariableNode obj = (VariableNode) child;
                                    if (obj.identifier != null && obj.identifier.getText().equals(itemName)) {
                                        SourceElement element = new SourceElement(null, obj.identifier.line, obj.identifier.column);
                                        display.asyncExec(new Runnable() {

                                            @Override
                                            public void run() {
                                                fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(element)));
                                            }

                                        });
                                        break;
                                    }
                                }
                            }
                            else if (node instanceof DataNode) {
                                for (Node child : node.getChilds()) {
                                    DataLineNode obj = (DataLineNode) child;
                                    if (obj.label != null && obj.label.getText().equals(itemName)) {
                                        SourceElement element = new SourceElement(null, obj.label.line, obj.label.column);
                                        display.asyncExec(new Runnable() {

                                            @Override
                                            public void run() {
                                                fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(element)));
                                            }

                                        });
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                hoverToken = hoverHighlightToken = null;
                styledText.setCursor(null);
                styledText.redraw();
            }

            boolean openLinkedObject(Node node, String hoverText) {
                for (Node child : node.getChilds()) {
                    if (child instanceof ObjectNode) {
                        ObjectNode obj = (ObjectNode) child;
                        if (obj.name.getText().equals(hoverText)) {
                            SourceElement element = new SourceElement(null, obj.name.line, obj.name.column);
                            display.asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(element)));
                                }

                            });
                            return true;
                        }
                    }
                    else if (openLinkedObject(child, hoverText)) {
                        return true;
                    }
                }
                return false;
            }

            boolean openLinkedObjectMethod(Node node, String objname, String hoverText) {
                for (Node child : node.getChilds()) {
                    if (child instanceof ObjectNode) {
                        ObjectNode obj = (ObjectNode) child;
                        if (obj.name.getText().equals(objname)) {

                            String fileName = obj.file.getText().substring(1, obj.file.getText().length() - 1);
                            Node objectRoot = tokenMarker.getObjectTree(fileName);
                            if (objectRoot != null) {
                                for (Node objectNode : objectRoot.getChilds()) {
                                    if (objectNode instanceof MethodNode) {
                                        MethodNode method = (MethodNode) objectNode;
                                        if (method.name.getText().equals(hoverText)) {
                                            SourceElement element = new SourceElement(obj, method.name.line, method.name.column);
                                            display.asyncExec(new Runnable() {

                                                @Override
                                                public void run() {
                                                    fireOpen(new OpenEvent(outline.getViewer(), new StructuredSelection(element)));
                                                }

                                            });
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (openLinkedObjectMethod(child, objname, hoverText)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void mouseDown(MouseEvent e) {

            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {

            }
        });

        styledText.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (popupWindow != null) {
                    if (popupMouseBounds != null && !popupMouseBounds.contains(e.x, e.y)) {
                        popupWindow.dispose();
                        popupWindow = null;
                        popupMouseBounds = null;
                        styledText.setFocus();
                    }
                    return;
                }

                hoverHighlight = (e.stateMask & SWT.CTRL) != 0;
                hoverToken = hoverHighlightToken = null;

                if (hoverHighlight) {
                    int offset = styledText.getOffsetAtPoint(new Point(e.x, e.y));
                    Node context = offset != -1 ? tokenMarker.getContextAtLine(styledText.getLineAtOffset(offset)) : null;
                    if (context instanceof ObjectNode) {
                        if (((ObjectNode) context).file == tokenMarker.getTokenAt(offset)) {
                            hoverToken = ((ObjectNode) context).file;
                            hoverHighlightToken = hoverToken;
                        }
                    }
                    else if (context instanceof DataLineNode) {
                        DataLineNode node = (DataLineNode) context;
                        Token token = tokenMarker.getTokenAt(offset);
                        if (token != node.label && token != node.condition && token != node.instruction && token.type == 0) {
                            hoverToken = tokenMarker.getTokenAt(offset);
                            hoverHighlightToken = hoverToken;
                        }
                    }
                    else if (context instanceof StatementNode) {
                        Token token = tokenMarker.getTokenAt(offset);
                        if (token != null) {
                            int objstart = -1;
                            int objstop = -1;
                            int namestart = token.start;
                            int namestop = token.stop;
                            int dot = token.getText().indexOf('.');

                            if (dot == -1) {
                                if (token.getText().charAt(0) == '@') {
                                    namestart++;
                                }
                            }
                            else if (dot == 0) {
                                namestart++;
                            }
                            else {
                                objstart = token.start;
                                if (token.getText().charAt(0) == '@') {
                                    objstart++;
                                }
                                objstop = token.start + dot - 1;
                                namestart = token.start + dot + 1;
                            }

                            Set<TokenMarker> markers = tokenMarker.getLineTokens(token.start, token.getText());
                            for (TokenMarker entry : markers) {
                                if (offset >= entry.getStart() && offset <= entry.getStop()) {
                                    if (entry.getId() == TokenId.METHOD_PUB || entry.getId() == TokenId.METHOD_PRI) {
                                        hoverToken = token;
                                        hoverHighlightToken = new Token(token.getStream(), namestart);
                                        hoverHighlightToken.stop = namestop;
                                        break;
                                    }
                                    else if (entry.getId() == TokenId.OBJECT) {
                                        hoverToken = token;
                                        if (objstart != -1) {
                                            hoverHighlightToken = new Token(token.getStream(), objstart);
                                            hoverHighlightToken.stop = objstop;
                                        }
                                        else {
                                            hoverHighlightToken = token;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Cursor cursor = hoverHighlightToken != null ? display.getSystemCursor(SWT.CURSOR_HAND) : null;
                    if (cursor != styledText.getCursor()) {
                        styledText.setCursor(cursor);
                    }
                    styledText.redraw();
                }
            }

        });

        styledText.addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseHover(MouseEvent e) {
                if (popupWindow != null || hoverHighlight) {
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
                    Label content = new Label(popupWindow, SWT.NONE);
                    content.setText(marker.getError());
                    popupWindow.pack();

                    Point size = popupWindow.getSize();
                    popupWindow.setLocation(bounds.x, bounds.y - size.y - 3);

                    popupWindow.setVisible(true);
                    return;
                }

                if (token != null) {
                    String text = helpProvider.getString(context != null ? context.getClass().getSimpleName() : null, token.getText().toLowerCase());
                    if (text == null) {
                        text = tokenMarker.getMethod(token.getText());
                        if (text == null && token.getText().startsWith("@")) {
                            text = tokenMarker.getMethod(token.getText().substring(1));
                        }
                        if (text == null && token.getText().startsWith(".")) {
                            int line = styledText.getLineAtOffset(offset);
                            int lineOffset = styledText.getOffsetAtLine(line);
                            String lineText = styledText.getLine(line);
                            int endIndex = token.start - lineOffset - 1;
                            if (lineText.charAt(endIndex) == ']') {
                                while (endIndex >= 0 && lineText.charAt(endIndex) != '[') {
                                    endIndex--;
                                }
                                endIndex--;
                            }
                            if (endIndex >= 0) {
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
                    }
                    if (text != null && !"".equals(text)) {
                        popupWindow = new Shell(styledText.getShell(), SWT.RESIZE | SWT.ON_TOP);
                        FillLayout layout = new FillLayout();
                        layout.marginHeight = layout.marginWidth = 0;
                        popupWindow.setLayout(layout);

                        StyledText content = new StyledText(popupWindow, SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
                        content.setMargins(5, 5, 5, 5);
                        content.setCaret(null);
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
        adapter.addContentProposalListener(new IContentProposalListener2() {

            @Override
            public void proposalPopupOpened(ContentProposalAdapter adapter) {
                adapter.setAutoActivationCharacters(null);
            }

            @Override
            public void proposalPopupClosed(ContentProposalAdapter adapter) {
                adapter.setAutoActivationCharacters(new char[] {
                    '.', '#', '@'
                });
            }
        });

        styledText.addVerifyKeyListener(new VerifyKeyListener() {

            @Override
            public void verifyKey(VerifyEvent e) {
                if (adapter.isProposalPopupOpen()) {
                    switch (e.keyCode) {
                        case SWT.CR:
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
                try {
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
                                            if ("REPEAT".equalsIgnoreCase(token.getText())) {
                                                if (i + 1 < node.getChildCount()) {
                                                    Node nextChild = node.getChild(i + 1);
                                                    Token nextToken = nextChild.getStartToken();
                                                    if ("WHILE".equalsIgnoreCase(nextToken.getText()) || "UNTIL".equalsIgnoreCase(nextToken.getText())) {
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

                    gc.setLineWidth(1);

                    for (TokenMarker entry : tokenMarker.getCompilerTokens()) {
                        try {
                            Rectangle r = styledText.getTextBounds(entry.getStart(), entry.getStop());
                            int[] polyline = computePolyline(new Point(r.x, r.y), new Point(r.x + r.width, r.y), styledText.getLineHeight());
                            if (entry.id == TokenId.ERROR) {
                                gc.setForeground(ColorRegistry.getColor(0xC0, 0x00, 0x00));
                            }
                            else {
                                gc.setForeground(ColorRegistry.getColor(0xF6, 0xD4, 0x56));
                            }
                            gc.drawPolyline(polyline);
                        } catch (Exception ex) {
                            // Ignore
                        }
                    }

                    if (hoverHighlight && hoverHighlightToken != null) {
                        int start = hoverHighlightToken.start;
                        if (hoverHighlightToken.getText().startsWith("@@")) {
                            start += 2;
                        }
                        else if (hoverHighlightToken.getText().startsWith("@")) {
                            start += 1;
                        }
                        Rectangle r = styledText.getTextBounds(start, hoverHighlightToken.stop);
                        gc.setForeground(ColorRegistry.getColor(0x00, 0x00, 0x00));
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
                int y1 = y0;

                for (int i = 0; i < node.getChildCount(); i++) {
                    Node child = node.getChild(i);
                    if ((child instanceof StatementNode) || (child instanceof DataLineNode)) {
                        token = child.getStartToken();
                        if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                            continue;
                        }
                        y1 = styledText.getLinePixel(token.line) + charSize.y / 2 + 1;
                        if (token.line != line0) {
                            int x1 = (charSize.x * token.column) - rightOffset;
                            gc.drawLine(x0, y1, x1, y1);
                        }
                        int y2 = paintBlock(gc, child);
                        if ("REPEAT".equalsIgnoreCase(token.getText())) {
                            if (i + 1 < node.getChildCount()) {
                                Node nextChild = node.getChild(i + 1);
                                Token nextToken = nextChild.getStartToken();
                                if ("WHILE".equalsIgnoreCase(nextToken.getText()) || "UNTIL".equalsIgnoreCase(nextToken.getText())) {
                                    int x2 = (charSize.x * token.column + charSize.x) - rightOffset;
                                    int y3 = styledText.getLinePixel(nextToken.line);
                                    gc.drawLine(x2, y2, x2, y3);
                                }
                            }
                        }
                    }
                }

                if (y1 > y0) {
                    gc.drawLine(x0, y0, x0, y1);
                }

                return y1;
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
                preferences.removePropertyChangeListener(preferencesChangeListener);
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

    void updateFontsFrom(FontData fontData) {
        Font oldFont = font;
        Font oldFontBold = fontBold;
        Font oldFontItalic = fontItalic;
        Font oldFontBoldItalic = fontBoldItalic;

        font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);
        fontBold = new Font(display, fontData.getName(), fontData.getHeight(), SWT.BOLD);
        fontItalic = new Font(display, fontData.getName(), fontData.getHeight(), SWT.ITALIC);
        fontBoldItalic = new Font(display, fontData.getName(), fontData.getHeight(), SWT.BOLD | SWT.ITALIC);

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

        ruler.setFont(font);
        styledText.setFont(font);

        GC gc = new GC(styledText);
        gc.setFont(font);
        charSize = gc.stringExtent("A"); //$NON-NLS-1$
        gc.dispose();

        insertCaret.setSize(2, styledText.getLineHeight());
        overwriteCaret.setSize(charSize.x, styledText.getLineHeight());

        if (oldFont != null) {
            oldFont.dispose();
            oldFontBold.dispose();
            oldFontItalic.dispose();
            oldFontBoldItalic.dispose();
        }
    }

    public void setTokenMarker(SourceTokenMarker tokenMarker) {
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
        return sashForm;
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
            styledText.redraw();

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
                Node node = tokenMarker.getContextAtLine(lineNumber);
                while (node.getParent() != null && node.getParent().getParent() != null) {
                    node = node.getParent();
                }

                boolean tabstopMatch = false;
                int[] tabStops = Preferences.getInstance().getTabStops(node.getClass());

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

        Node node = tokenMarker.getContextAtLine(lineNumber);
        while (node.getParent() != null && node.getParent().getParent() != null) {
            node = node.getParent();
        }

        boolean tabstopMatch = false;
        int[] tabStops = Preferences.getInstance().getTabStops(node.getClass());

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

        Node node = tokenMarker.getContextAtLine(lineNumber);
        while (node.getParent() != null && node.getParent().getParent() != null) {
            node = node.getParent();
        }

        int[] tabStops = Preferences.getInstance().getTabStops(node.getClass());
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

        int lineIndex = styledText.getLineAtOffset(styledText.getCaretOffset());
        Node node = tokenMarker.getContextAtLine(lineIndex);

        int start = position;
        while (start > 0) {
            if (node instanceof DataLineNode) {
                if (contents.charAt(start - 1) == '#') {
                    break;
                }
            }
            char ch = contents.charAt(start - 1);
            if (!Character.isLetterOrDigit(ch) && ch != '.' && ch != ':' && ch != '#' && ch != '_') {
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
                proposals.addAll(helpProvider.fillSourceProposals(token));
            }
            else if (node instanceof DataLineNode) {
                DataLineNode line = (DataLineNode) node;
                position = styledText.getCaretOffset();

                if (node.getStartToken().line != lineIndex || (line.condition == null && line.instruction == null)) {
                    proposals.addAll(helpProvider.fillProposals("Condition", token));
                    proposals.addAll(helpProvider.fillProposals("Instruction", token));
                }
                else if (line.condition != null && position >= line.condition.start && position <= line.condition.stop + 1) {
                    proposals.addAll(helpProvider.fillProposals("Condition", token));
                }
                else if (line.condition != null && line.instruction == null && position > line.condition.stop) {
                    proposals.addAll(helpProvider.fillProposals("Instruction", token));
                }
                else if (line.instruction != null && position >= line.instruction.start && position <= line.instruction.stop + 1) {
                    proposals.addAll(helpProvider.fillProposals("Instruction", token));
                }
                else if (line.instruction != null && line.condition == null && position < line.instruction.start) {
                    proposals.addAll(helpProvider.fillProposals("Condition", token));
                }
                else if (line.instruction != null && position > line.instruction.stop + 1) {
                    proposals.addAll(helpProvider.fillProposals(line.instruction.getText().toUpperCase(), token));
                    if (node.getParent() instanceof StatementNode || node.getParent() instanceof MethodNode) {
                        proposals.addAll(tokenMarker.getMethodProposals(node.getParent(), token));
                    }
                    else {
                        proposals.addAll(tokenMarker.getPAsmProposals(node, token));
                    }
                }
            }
            else if (node != null) {
                if ((node instanceof StatementNode) || (node instanceof MethodNode)) {
                    proposals.addAll(tokenMarker.getMethodProposals(node, token));
                }
                else {
                    proposals.addAll(tokenMarker.getConstantsProposals(node, token));
                }
                proposals.addAll(helpProvider.fillProposals(node.getClass().getSimpleName(), token));
            }
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
        int pageSize = bottomLine - topLine;
        while (line < topLine) {
            topLine -= pageSize;
            bottomLine -= pageSize;
        }
        while (line > bottomLine) {
            topLine += pageSize;
            bottomLine += pageSize;
        }

        styledText.setCaretOffset(styledText.getOffsetAtLine(line) + column);
        styledText.setTopIndex(topLine);
    }

    public void setCompilerMessages(List<CompilerException> messages) {
        tokenMarker.refreshCompilerTokens(messages);

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
        Color color = getSectionBackground(null);

        if (root != null) {
            for (Node child : root.getChilds()) {
                if (lineOffset < child.getStartIndex()) {
                    break;
                }
                color = getSectionBackground(child);
            }
        }

        return color;
    }

    Color getSectionBackground(Node node) {
        Color result = null;

        if (node == null) {
            sectionCount[0] = sectionCount[1] = sectionCount[2] = sectionCount[3] = sectionCount[4] = sectionCount[5] = 0;
        }

        if (node instanceof VariablesNode) {
            result = getColor(255, 223, 191, sectionCount[1] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[1] = sectionCount[1] == 0 ? 1 : 0;
            }
        }
        else if (node instanceof ObjectsNode) {
            result = getColor(255, 191, 191, sectionCount[2] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[2] = sectionCount[2] == 0 ? 1 : 0;
            }
        }
        else if (node instanceof MethodNode) {
            if (((MethodNode) node).isPublic()) {
                result = getColor(191, 223, 255, sectionCount[3] == 0 ? 0 : -6);
                if (node != null) {
                    sectionCount[3] = sectionCount[3] == 0 ? 1 : 0;
                }
            }
            else {
                result = getColor(191, 248, 255, sectionCount[4] == 0 ? 0 : -6);
                if (node != null) {
                    sectionCount[4] = sectionCount[4] == 0 ? 1 : 0;
                }
            }
        }
        else if (node instanceof DataNode) {
            result = getColor(191, 255, 200, sectionCount[5] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[5] = sectionCount[5] == 0 ? 1 : 0;
            }
        }
        else {
            result = getColor(255, 248, 192, sectionCount[0] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[0] = sectionCount[0] == 0 ? 1 : 0;
            }
        }

        return result;
    }

    Color getColor(int r, int g, int b, int percent) {
        r += (int) (r / 100.0 * percent);
        g += (int) (g / 100.0 * percent);
        b += (int) (b / 100.0 * percent);
        return ColorRegistry.getColor(r, g, b);
    }

    public OutlineView getOutline() {
        return outline;
    }

    public void addOpenListener(IOpenListener listener) {
        openListeners.add(listener);
    }

    public void removeOpenListener(IOpenListener listener) {
        openListeners.remove(listener);
    }

    protected void fireOpen(final OpenEvent event) {
        for (IOpenListener l : openListeners) {
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.open(event);
                }
            });
        }
    }

    public void format(Formatter formatter) {
        int topPixel = styledText.getTopPixel();
        int caretOffset = styledText.getCaretOffset();
        int caretLine = styledText.getLineAtOffset(caretOffset);
        int caretColumn = caretOffset - styledText.getOffsetAtLine(caretLine);

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

        modified = true;
        styledText.redraw();
    }

}
