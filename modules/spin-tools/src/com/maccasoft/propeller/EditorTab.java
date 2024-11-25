/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Formatter;
import com.maccasoft.propeller.spin1.Spin1TokenMarker;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Formatter;
import com.maccasoft.propeller.spin2.Spin2TokenMarker;
import com.maccasoft.propeller.spin2.Spin2TokenStream;
import com.maccasoft.propeller.spinc.CTokenMarker;
import com.maccasoft.propeller.spinc.Spin1CCompiler;
import com.maccasoft.propeller.spinc.Spin2CCompiler;

public class EditorTab implements FindReplaceTarget {

    public static final String OBJECT_TREE = "objectTree";

    SourcePool sourcePool;

    File file;
    SourceEditor editor;
    OutlineView outlineView;
    CTabItem tabItem;

    Font busyFont;
    Font boldFont;
    Font boldBusyFont;

    SourceTokenMarker tokenMarker;

    String tabItemText;
    boolean dirty;

    AtomicBoolean threadRunning = new AtomicBoolean(false);
    AtomicBoolean pendingCompile = new AtomicBoolean(false);

    Set<File> dependencies = new HashSet<>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    SpinObject object;
    ObjectTree objectTree;

    Preferences preferences;

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_SPIN1_CASE_SENSITIVE_SYMBOLS:
                    tokenMarker.setCaseSensitive((Boolean) evt.getNewValue());
                case Preferences.PROP_SPIN1_LIBRARY_PATH:
                    if (!tabItemText.toLowerCase().endsWith(".spin2")) {
                        scheduleCompile();
                    }
                    break;
                case Preferences.PROP_SPIN2_CASE_SENSITIVE_SYMBOLS:
                    tokenMarker.setCaseSensitive((Boolean) evt.getNewValue());
                case Preferences.PROP_SPIN2_LIBRARY_PATH:
                case Preferences.PROP_SPIN2_COMPRESS:
                    if (tabItemText.toLowerCase().endsWith(".spin2") || tabItemText.toLowerCase().endsWith(".c")) {
                        scheduleCompile();
                    }
                    break;
                case Preferences.PROP_TOP_OBJECT: {
                    File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
                    tabItem.setFont(localFile.equals(evt.getNewValue()) ? boldFont : null);
                    break;
                }
            }
        }
    };

    final PropertyChangeListener sourcePoolChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            File localFile = file != null ? file : new File(tabItemText);
            if (evt.getPropertyName().equals(localFile.getAbsolutePath())) {
                if (evt.getNewValue() != null) {
                    scheduleCompile();
                }
                return;
            }
            if (dependencies.contains(new File(evt.getPropertyName()))) {
                scheduleCompile();
                return;
            }
        }

    };

    final ISelectionChangedListener outlineSelectionChangeListener = new ISelectionChangedListener() {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            Node node = (Node) event.getStructuredSelection().getFirstElement();
            if (node != null) {
                int offset = node.getStartIndex();
                if (offset == -1) {
                    offset = 0;
                }
                int line = editor.getStyledText().getLineAtOffset(offset);
                int column = offset - editor.getStyledText().getOffsetAtLine(line);
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        editor.fireNavigateToEvent(new SourceElement(null, line, column));
                    }

                });
                setFocus();
            }
        }
    };

    final ITreeViewerListener outlineTreeListener = new ITreeViewerListener() {

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            setFocus();
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            setFocus();
        }

    };

    final Runnable outlineUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            if (outlineView.getControl().isDisposed()) {
                return;
            }
            StyledText styledText = editor.getStyledText();
            int offset = styledText.getCaretOffset();
            int y = styledText.getLineAtOffset(offset);
            Node root = tokenMarker.getRoot();
            if (root != null) {
                Node selection = getCaretNode(root, offset, y);
                if (selection != null) {
                    outlineView.removeSelectionChangedListener(outlineSelectionChangeListener);
                    outlineView.getViewer().removeTreeListener(outlineTreeListener);
                    try {
                        outlineView.setSelection(new StructuredSelection(selection));
                    } finally {
                        outlineView.addSelectionChangedListener(outlineSelectionChangeListener);
                        outlineView.getViewer().addTreeListener(outlineTreeListener);
                    }
                }
            }
        }

        Node getCaretNode(Node root, int offset, int line) {
            Node selection = null;

            for (Node node : root.getChilds()) {
                if (node.getStartToken() == null) {
                    continue;
                }
                int start = node.getStartToken().start - node.getStartToken().column;
                if ((node instanceof MethodNode) || (node instanceof FunctionNode)) {
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
                            if (child instanceof DataLineNode) {
                                DataLineNode dataLine = (DataLineNode) child;
                                if (dataLine.label != null && !dataLine.label.getText().startsWith(".")) {
                                    selection = child;
                                }
                            }
                        }
                    }
                }
                else if (node instanceof VariablesNode) {
                    if (offset < start) {
                        return selection;
                    }
                    selection = node;

                    int childLine = -1;
                    Node childSelection = selection;
                    for (Node childNode : node.getChilds()) {
                        if (childNode instanceof VariableNode) {
                            Token token = childNode.getStartToken();
                            if (token != null) {
                                start = token.start;
                                if (token.line != childLine) {
                                    start -= token.column;
                                    childLine = token.line;
                                }
                                if (offset < start) {
                                    return childSelection;
                                }
                                childSelection = childNode;
                            }
                        }
                        else {
                            for (Node child : childNode.getChilds()) {
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
                        }
                    }

                    if (line == childSelection.getStartToken().line) {
                        return childSelection;
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

            return selection;
        }

    };

    class EditorTabSourceProvider extends SourceProvider {

        File[] searchPaths;
        List<File> collectedSearchPaths;

        public EditorTabSourceProvider(File[] searchPaths) {
            this.searchPaths = searchPaths;
            this.collectedSearchPaths = new ArrayList<>();
        }

        @Override
        public Node getParsedSource(File file) {
            Node root = sourcePool.getParsedSource(file);
            if (root != null) {
                return root;
            }
            return super.getParsedSource(file);
        }

        @Override
        public File getFile(String name) {
            File localFile = file != null ? new File(file.getParentFile(), name) : new File(name);
            if (localFile.exists()) {
                File parent = localFile.getParentFile();
                if (!collectedSearchPaths.contains(parent)) {
                    collectedSearchPaths.add(parent);
                }
                dependencies.add(localFile);
                return localFile;
            }

            for (File file : collectedSearchPaths) {
                localFile = new File(file, name);
                if (localFile.exists()) {
                    File parent = localFile.getParentFile();
                    if (!collectedSearchPaths.contains(parent)) {
                        collectedSearchPaths.add(parent);
                    }
                    dependencies.add(localFile);
                    return localFile;
                }
            }

            for (int i = 0; i < searchPaths.length; i++) {
                localFile = new File(searchPaths[i], name);
                if (localFile.exists()) {
                    dependencies.add(localFile);
                    return localFile;
                }
            }

            return null;
        }

        EditorTab getTabForFile(File file) {
            CTabItem[] tabItems = tabItem.getParent().getItems();
            for (int i = 0; i < tabItems.length; i++) {
                if (tabItems[i] == tabItem) {
                    continue;
                }
                EditorTab editorTab = (EditorTab) tabItems[i].getData();
                File editorTabFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText()).getAbsoluteFile();
                if (editorTabFile.equals(file)) {
                    return editorTab;
                }
            }
            return null;
        }

    }

    class Spin1TokenMarkerAdatper extends Spin1TokenMarker {

        public Spin1TokenMarkerAdatper() {
            super(new EditorTabSourceProvider(preferences.getSpin1LibraryPath()));
        }

        @Override
        public void refreshTokens(String text) {
            super.refreshTokens(text);

            File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
            sourcePool.setParsedSource(localFile, getRoot());
        }

        @Override
        protected Node getObjectTree(String fileName) {
            Node node = super.getObjectTree(fileName + ".spin");
            if (node == null) {
                node = super.getObjectTree(fileName + ".c");
            }
            if (node == null) {
                node = super.getObjectTree(fileName);
            }
            return node;
        }

    }

    class Spin2TokenMarkerAdatper extends Spin2TokenMarker {

        public Spin2TokenMarkerAdatper() {
            super(new EditorTabSourceProvider(preferences.getSpin2LibraryPath()));
        }

        @Override
        public void refreshTokens(String text) {
            super.refreshTokens(text);

            File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
            sourcePool.setParsedSource(localFile, getRoot());
        }

        @Override
        protected Node getObjectTree(String fileName) {
            Node node = super.getObjectTree(fileName + ".spin2");
            if (node == null) {
                node = super.getObjectTree(fileName + ".c");
            }
            if (node == null) {
                node = super.getObjectTree(fileName);
            }
            return node;
        }

    }

    class CTokenMarkerAdatper extends CTokenMarker {

        public CTokenMarkerAdatper() {
            super(new EditorTabSourceProvider(preferences.getSpin2LibraryPath()));
        }

        @Override
        public void refreshTokens(String text) {
            super.refreshTokens(text);

            File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
            sourcePool.setParsedSource(localFile, getRoot());
        }

        @Override
        protected void setP1(boolean p1) {
            if (p1) {
                this.sourceProvider = new EditorTabSourceProvider(preferences.getSpin1LibraryPath());
                editor.setHelpProvider(new EditorHelp("Spin1CInstructions.xml", new File(""), ".spin"));
            }
            else {
                this.sourceProvider = new EditorTabSourceProvider(preferences.getSpin2LibraryPath());
                editor.setHelpProvider(new EditorHelp("Spin2CInstructions.xml", new File(""), ".spin2"));
            }
            super.setP1(p1);
        }

        @Override
        protected Node getObjectTree(String fileName) {
            Node node = super.getObjectTree(fileName + ".c");
            if (node == null && isP1()) {
                node = super.getObjectTree(fileName + ".spin");
            }
            if (node == null && isP2()) {
                node = super.getObjectTree(fileName + ".spin2");
            }
            if (node == null) {
                node = super.getObjectTree(fileName);
            }
            return node;
        }

    }

    boolean debug;

    final Runnable compilerRunnable = new Runnable() {

        @Override
        public void run() {
            if (editor.getControl().isDisposed()) {
                return;
            }
            if (!threadRunning.getAndSet(true)) {
                pendingCompile.set(false);

                File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
                Node root = sourcePool.getParsedSource(localFile);
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (outlineView != null && !outlineView.getControl().isDisposed() && outlineView.getInput() == null) {
                            outlineView.setInput(root);
                        }
                    }
                });

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        dependencies.clear();

                        Compiler compiler = null;
                        if (tabItemText.toLowerCase().endsWith(".spin")) {
                            compiler = new Spin1Compiler(preferences.getSpin1CaseSensitiveSymbols(), true);
                            compiler.setSourceProvider(new EditorTabSourceProvider(preferences.getSpin1LibraryPath()));

                            for (Entry<String, String> entry : preferences.getSpin1Defines().entrySet()) {
                                Token token;
                                TokenStream stream = new Spin1TokenStream(entry.getValue());
                                List<Token> list = new ArrayList<>();
                                while ((token = stream.nextToken()).type != Token.EOF) {
                                    list.add(token);
                                }
                                compiler.addDefine(entry.getKey(), list);
                            }
                        }
                        else if (tabItemText.toLowerCase().endsWith(".spin2")) {
                            compiler = new Spin2Compiler(preferences.getSpin2CaseSensitiveSymbols());
                            compiler.setSourceProvider(new EditorTabSourceProvider(preferences.getSpin2LibraryPath()));

                            for (Entry<String, String> entry : preferences.getSpin2Defines().entrySet()) {
                                Token token;
                                TokenStream stream = new Spin2TokenStream(entry.getValue());
                                List<Token> list = new ArrayList<>();
                                while ((token = stream.nextToken()).type != Token.EOF) {
                                    list.add(token);
                                }
                                compiler.addDefine(entry.getKey(), list);
                            }
                        }
                        else if (tabItemText.toLowerCase().endsWith(".c")) {
                            for (Node node : root.getChilds()) {
                                if (node instanceof DirectiveNode) {
                                    int index = 1;
                                    if (index < node.getTokenCount()) {
                                        if ("pragma".equals(node.getToken(index).getText())) {
                                            index++;
                                            if (index < node.getTokenCount()) {
                                                if ("target".equals(node.getToken(index).getText())) {
                                                    index++;
                                                    if (index < node.getTokenCount()) {
                                                        if ("P1".equals(node.getToken(index).getText())) {
                                                            compiler = new Spin1CCompiler(preferences.getSpin1CaseSensitiveSymbols());
                                                            compiler.setSourceProvider(new EditorTabSourceProvider(preferences.getSpin1LibraryPath()));
                                                        }
                                                        else if ("P2".equals(node.getToken(index).getText())) {
                                                            compiler = new Spin2CCompiler(preferences.getSpin2CaseSensitiveSymbols());
                                                            compiler.setSourceProvider(new EditorTabSourceProvider(preferences.getSpin2LibraryPath()));
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (compiler == null) {
                                compiler = new Spin2CCompiler();
                                compiler.setSourceProvider(new EditorTabSourceProvider(preferences.getSpin2LibraryPath()));
                            }
                        }
                        if (compiler != null) {
                            compiler.setRemoveUnusedMethods(true);
                            compiler.setDebugEnabled(debug);

                            if (compiler instanceof Spin2Compiler) {
                                ((Spin2Compiler) compiler).setCompress(preferences.getSpin2Compress());
                            }

                            try {
                                object = compiler.compile(localFile, root);
                                objectTree = compiler.getObjectTree();
                                errors = compiler.hasErrors();

                            } catch (Exception e) {
                                errors = true;
                                e.printStackTrace();
                            }

                            if (!pendingCompile.get()) {
                                messages.clear();
                                messages.addAll(compiler.getMessages());

                                List<CompilerException> list = new ArrayList<CompilerException>();
                                for (CompilerException msg : messages) {
                                    if (tabItemText.equals(msg.fileName)) {
                                        list.add(msg);
                                    }
                                }

                                Display.getDefault().asyncExec(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (editor == null || editor.isDisposed() || tabItem.isDisposed()) {
                                            return;
                                        }
                                        changeSupport.firePropertyChange(OBJECT_TREE, null, objectTree);
                                        editor.setCompilerMessages(list);
                                        editor.redraw();

                                        if (outlineView != null && !outlineView.getControl().isDisposed()) {
                                            outlineView.setInput(root);
                                        }

                                        tabItem.setFont(localFile.equals(preferences.getTopObject()) ? boldFont : null);
                                        updateTabItemText();
                                    }
                                });
                            }
                        }

                        threadRunning.set(false);
                    }

                });
                thread.start();
            }
            else {
                pendingCompile.set(true);
                Display.getDefault().timerExec(250, this);
            }
        }

    };

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public EditorTab(CTabFolder folder, String name, SourcePool sourcePool) {
        this(folder, null, name, sourcePool);
    }

    public EditorTab(CTabFolder folder, File file, SourcePool sourcePool) {
        this(folder, file, file.getName(), sourcePool);
    }

    private EditorTab(CTabFolder folder, File file, String name, SourcePool sourcePool) {
        this.file = file;
        this.tabItemText = name;
        this.sourcePool = sourcePool;
        this.preferences = Preferences.getInstance();

        tabItem = new CTabItem(folder, SWT.NONE);
        tabItem.setShowClose(true);
        tabItem.setText(tabItemText);
        if (file != null) {
            tabItem.setToolTipText(file.getAbsolutePath());
        }
        tabItem.setData(this);

        FontData[] fontData = tabItem.getFont().getFontData();
        busyFont = new Font(tabItem.getDisplay(), fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC);
        boldFont = new Font(tabItem.getDisplay(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
        boldBusyFont = new Font(tabItem.getDisplay(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD | SWT.ITALIC);

        editor = new SourceEditor(folder);

        File localFile = this.file != null ? this.file : new File(tabItemText).getAbsoluteFile();
        if (localFile.equals(preferences.getTopObject())) {
            tabItem.setFont(boldFont);
        }

        if (tabItemText.toLowerCase().endsWith(".spin")) {
            tokenMarker = new Spin1TokenMarkerAdatper();
            tokenMarker.setCaseSensitive(preferences.getSpin1CaseSensitiveSymbols());
            editor.setHelpProvider(new EditorHelp("Spin1Instructions.xml", localFile.getParentFile(), ".spin"));
        }
        else if (tabItemText.toLowerCase().endsWith(".spin2")) {
            tokenMarker = new Spin2TokenMarkerAdatper();
            tokenMarker.setCaseSensitive(preferences.getSpin2CaseSensitiveSymbols());
            editor.setHelpProvider(new EditorHelp("Spin2Instructions.xml", localFile.getParentFile(), ".spin2"));
        }
        else if (tabItemText.toLowerCase().endsWith(".c")) {
            tokenMarker = new CTokenMarkerAdatper();
            tokenMarker.setCaseSensitive(true);
            editor.setHelpProvider(new EditorHelp("Spin2CInstructions.xml", localFile.getParentFile(), ".spin2"));
        }
        editor.setTokenMarker(tokenMarker);

        editor.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!dirty) {
                    dirty = true;
                    updateTabItemText();
                }
            }
        });
        editor.getStyledText().addCaretListener(new CaretListener() {

            @Override
            public void caretMoved(CaretEvent event) {
                Display.getDefault().timerExec(250, outlineUpdateRunnable);
            }

        });

        sourcePool.addPropertyChangeListener(sourcePoolChangeListener);
        preferences.addPropertyChangeListener(preferencesChangeListener);

        tabItem.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                Display.getDefault().timerExec(-1, outlineUpdateRunnable);

                preferences.removePropertyChangeListener(preferencesChangeListener);
                sourcePool.removePropertyChangeListener(sourcePoolChangeListener);

                File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
                sourcePool.removeParsedSource(localFile);

                editor.dispose();

                busyFont.dispose();
                boldFont.dispose();
                boldBusyFont.dispose();
            }

        });

        tabItem.setControl(editor.getControl());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public void addCaretListener(CaretListener listener) {
        editor.getStyledText().addCaretListener(listener);
    }

    public void removeCaretListener(CaretListener listener) {
        editor.getStyledText().removeCaretListener(listener);
    }

    public void addDisposeListener(DisposeListener listener) {
        editor.getControl().addDisposeListener(listener);
    }

    public void removeDisposeListener(DisposeListener listener) {
        editor.getControl().removeDisposeListener(listener);
    }

    public OutlineView getOutlineView() {
        return outlineView;
    }

    public void setOutlineView(OutlineView outlineView) {
        if (this.outlineView != null) {
            this.outlineView.removeSelectionChangedListener(outlineSelectionChangeListener);
            this.outlineView.getViewer().removeTreeListener(outlineTreeListener);
        }
        this.outlineView = outlineView;
        if (this.outlineView != null) {
            this.outlineView.addSelectionChangedListener(outlineSelectionChangeListener);
            this.outlineView.getViewer().addTreeListener(outlineTreeListener);
        }
    }

    void updateTabItemText() {
        if (dirty) {
            tabItem.setText("*" + tabItemText);
        }
        else {
            tabItem.setText(tabItemText);
        }
        tabItem.setSelectionForeground(errors ? Display.getDefault().getSystemColor(SWT.COLOR_RED) : null);
        tabItem.setForeground(errors ? Display.getDefault().getSystemColor(SWT.COLOR_RED) : null);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        File localFile = this.file != null ? this.file : new File(tabItemText).getAbsoluteFile();
        sourcePool.removeParsedSource(localFile);

        if (file != null) {
            tabItem.setToolTipText(file.getAbsolutePath());
        }
        this.file = file;

        if (tabItemText.toLowerCase().endsWith(".spin")) {
            tokenMarker = new Spin1TokenMarkerAdatper();
            tokenMarker.setCaseSensitive(preferences.getSpin1CaseSensitiveSymbols());
            editor.setHelpProvider(new EditorHelp("Spin1Instructions.xml", localFile.getParentFile(), ".spin"));
        }
        else if (tabItemText.toLowerCase().endsWith(".spin2")) {
            tokenMarker = new Spin2TokenMarkerAdatper();
            tokenMarker.setCaseSensitive(preferences.getSpin2CaseSensitiveSymbols());
            editor.setHelpProvider(new EditorHelp("Spin2Instructions.xml", localFile.getParentFile(), ".spin2"));
        }
        else if (tabItemText.toLowerCase().endsWith(".c")) {
            tokenMarker = new CTokenMarkerAdatper();
            tokenMarker.setCaseSensitive(true);
            editor.setHelpProvider(new EditorHelp("Spin2CInstructions.xml", localFile.getParentFile(), ".spin2"));
        }
        editor.setTokenMarker(tokenMarker);

        if (file != null && file.equals(preferences.getTopObject())) {
            tabItem.setFont(boldFont);
        }
        tabItem.setToolTipText(file != null ? file.getAbsolutePath() : "");
    }

    public void setFocus() {
        if (!editor.getControl().isDisposed()) {
            editor.getControl().setFocus();
        }
    }

    public void setText(String text) {
        tabItemText = text;
        updateTabItemText();
    }

    public String getText() {
        return tabItemText;
    }

    public void dispose() {
        editor.getControl().dispose();
        tabItem.dispose();
    }

    public CTabItem getTabItem() {
        return tabItem;
    }

    public void undo() {
        editor.undo();
    }

    public void redo() {
        editor.redo();
    }

    public void setEditorText(String text) {
        editor.setText(text);
    }

    public String getEditorText() {
        return editor.getText();
    }

    public void clearDirty() {
        if (dirty) {
            dirty = false;
            updateTabItemText();
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerException> getMessages() {
        return messages;
    }

    public SpinObject getObject() {
        return object;
    }

    public void goToFirstError() {
        Iterator<TokenMarker> iter = tokenMarker.getCompilerTokens().iterator();
        while (iter.hasNext()) {
            TokenMarker marker = iter.next();
            if (marker.id == TokenId.ERROR) {
                int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
                editor.goToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
                break;
            }
        }
    }

    public void goToNextAnnotation() {
        TokenMarker marker = getNextMarker(TokenId.ERROR);
        if (marker == null) {
            marker = getNextMarker(null);
        }
        if (marker != null) {
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            editor.goToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
        }
        else {
            Display.getDefault().beep();
        }
    }

    TokenMarker getNextMarker(TokenId id) {
        int offset = editor.getStyledText().getCaretOffset();

        Iterator<TokenMarker> iter = tokenMarker.getCompilerTokens().iterator();
        while (iter.hasNext()) {
            TokenMarker marker = iter.next();
            if ((id == null || marker.id == id) && marker.start > offset) {
                return marker;
            }
        }

        iter = tokenMarker.getCompilerTokens().iterator();
        while (iter.hasNext()) {
            TokenMarker marker = iter.next();
            if (id == null || marker.id == id) {
                return marker;
            }
        }

        return null;
    }

    public void goToPreviousAnnotation() {
        TokenMarker marker = getPreviousMarker(TokenId.ERROR);
        if (marker == null) {
            marker = getPreviousMarker(null);
        }
        if (marker != null) {
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            editor.goToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
        }
        else {
            Display.getDefault().beep();
        }
    }

    TokenMarker getPreviousMarker(TokenId id) {
        int offset = editor.getStyledText().getCaretOffset();

        Iterator<TokenMarker> iter = tokenMarker.getCompilerTokens().descendingIterator();
        while (iter.hasNext()) {
            TokenMarker marker = iter.next();
            if ((id == null || marker.id == id) && marker.start < offset) {
                return marker;
            }
        }

        iter = tokenMarker.getCompilerTokens().descendingIterator();
        while (iter.hasNext()) {
            TokenMarker marker = iter.next();
            if (id == null || marker.id == id) {
                return marker;
            }
        }

        return null;
    }

    public SourceEditor getEditor() {
        return editor;
    }

    @Override
    public int findAndSelect(int widgetOffset, String findString, boolean searchForward, boolean caseSensitive, boolean wholeWord, boolean regExSearch) {
        int patternFlags = 0;
        StyledText styledText = editor.getStyledText();
        String text = styledText.getText();

        if (!regExSearch) {
            findString = asRegPattern(findString);
            if (wholeWord) {
                findString = "\\b" + findString + "\\b";
            }
        }

        if (!caseSensitive) {
            patternFlags |= Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        }

        Pattern pattern = Pattern.compile(findString, patternFlags);
        Matcher matcher = pattern.matcher(text);

        if (searchForward) {
            if (widgetOffset == -1) {
                widgetOffset = 0;
            }
            if (matcher.find(widgetOffset)) {
                styledText.setSelectionRange(matcher.start(), matcher.group().length());
                revealCaret(styledText);
                return matcher.start();
            }
        }
        else {
            if (widgetOffset == -1) {
                widgetOffset = text.length();
            }
            boolean found = matcher.find(0);
            int index = -1;
            while (found && matcher.start() + matcher.group().length() <= widgetOffset) {
                index = matcher.start();
                found = matcher.find(index + 1);
            }
            if (index > -1) {
                matcher.find(index);
                styledText.setSelectionRange(matcher.start(), matcher.group().length());
                revealCaret(styledText);
                return index;
            }

        }

        return -1;
    }

    @Override
    public Point getSelection() {
        StyledText styledText = editor.getStyledText();
        return styledText.getSelectionRange();
    }

    @Override
    public String getSelectionText() {
        StyledText styledText = editor.getStyledText();
        return styledText.getSelectionText();
    }

    @Override
    public void replaceSelection(String text) {
        StyledText styledText = editor.getStyledText();
        Point selection = styledText.getSelectionRange();
        if (selection.y != 0) {
            styledText.replaceTextRange(selection.x, selection.y, text);
        }
    }

    void revealCaret(StyledText styledText) {
        Rectangle rect = styledText.getClientArea();
        int offset = styledText.getCaretOffset();
        int topLine = styledText.getLineIndex(0);
        int bottomLine = styledText.getLineIndex(rect.height);
        int pageSize = bottomLine - topLine;
        while (offset < styledText.getOffsetAtLine(topLine)) {
            topLine -= pageSize;
            bottomLine -= pageSize;
        }
        while (offset > styledText.getOffsetAtLine(bottomLine)) {
            topLine += pageSize;
            bottomLine += pageSize;
        }

        if (styledText.getLineIndex(0) != topLine) {
            styledText.setTopIndex(topLine);
        }
    }

    private String asRegPattern(String string) {
        StringBuilder out = new StringBuilder(string.length());
        boolean quoting = false;

        for (int i = 0, length = string.length(); i < length; i++) {
            char ch = string.charAt(i);
            if (ch == '\\') {
                if (quoting) {
                    out.append("\\E");
                    quoting = false;
                }
                out.append("\\\\");
                continue;
            }
            if (!quoting) {
                out.append("\\Q");
                quoting = true;
            }
            out.append(ch);
        }
        if (quoting) {
            out.append("\\E");
        }

        return out.toString();
    }

    void searchNext(String findString, boolean caseSensitiveSearch, boolean wrapSearch, boolean wholeWordSearch, boolean regexSearch) {
        Point r = getSelection();
        int findReplacePosition = r.x + r.y;

        int index = findAndSelect(findReplacePosition, findString, true, caseSensitiveSearch, wholeWordSearch, regexSearch);
        if (index == -1) {
            editor.getControl().getDisplay().beep();
            if (wrapSearch) {
                index = findAndSelect(-1, findString, true, caseSensitiveSearch, wholeWordSearch, regexSearch);
            }
        }
    }

    void searchPrevious(String findString, boolean caseSensitiveSearch, boolean wrapSearch, boolean wholeWordSearch, boolean regexSearch) {
        Point r = getSelection();
        int findReplacePosition = r.x + r.y;

        int index = findReplacePosition == 0 ? -1 : findAndSelect(findReplacePosition - 1, findString, false, caseSensitiveSearch, wholeWordSearch, regexSearch);
        if (index == -1) {
            editor.getControl().getDisplay().beep();
            if (wrapSearch) {
                index = findAndSelect(-1, findString, false, caseSensitiveSearch, wholeWordSearch, regexSearch);
            }
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    void scheduleCompile() {
        pendingCompile.set(true);
        object = null;

        File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
        tabItem.setFont(localFile.equals(preferences.getTopObject()) ? boldBusyFont : busyFont);

        Display.getDefault().timerExec(500, compilerRunnable);
    }

    void runCompile(boolean debug) {
        this.debug = debug;
        try {
            pendingCompile.set(true);
            object = null;
            compilerRunnable.run();
            waitCompile();
        } finally {
            this.debug = false;
        }
    }

    public void formatSource() {
        Formatter formatter = tabItemText.toLowerCase().endsWith(".spin2") ? new Spin2Formatter() : new Spin1Formatter();

        //formatter.setKeepBlankLines(true);
        formatter.setSectionTabStop("con", preferences.getTabStops(ConstantsNode.class));
        formatter.setSectionTabStop("var", preferences.getTabStops(VariablesNode.class));
        formatter.setSectionTabStop("obj", preferences.getTabStops(ObjectsNode.class));
        formatter.setSectionTabStop("pub", preferences.getTabStops(MethodNode.class));
        formatter.setSectionTabStop("dat", preferences.getTabStops(DataNode.class));
        //formatter.setPAsmColumns(4, 16, 24, 44, 52);
        //formatter.setInlinePAsmColumns(8, 16, 24, 44, 52);
        //formatter.setAdjustPAsmColumns(true);
        //formatter.setIsolateLargeLabels(true);

        editor.format(formatter);
    }

    public Set<File> getDependencies() {
        return dependencies;
    }

    public ObjectTree getObjectTree() {
        return objectTree;
    }

    public void waitCompile() {
        AtomicBoolean stop = new AtomicBoolean();
        Thread waitThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (threadRunning.get() || pendingCompile.get()) {
                        if (stop.get()) {
                            break;
                        }
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }

        });
        waitThread.start();
        try {
            waitThread.join(5000);
        } catch (Exception e) {
            // Do nothing
        }
        stop.set(true);
    }

    public boolean isBlockSelection() {
        return editor.getStyledText().getBlockSelection();
    }

    public void setBlockSelection(boolean blockSelection) {
        editor.getStyledText().setBlockSelection(blockSelection);
    }

    public SourceTokenMarker getTokenMarker() {
        return tokenMarker;
    }

    public void toggleTopObject() {
        File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
        preferences.setTopObject(localFile.equals(preferences.getTopObject()) ? null : localFile);
    }

    public boolean isTopObject() {
        File localFile = file != null ? file : new File(tabItemText).getAbsoluteFile();
        return localFile.equals(preferences.getTopObject());
    }

}
