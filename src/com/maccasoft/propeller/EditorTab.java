/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Formatter;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenMarker;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Formatter;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenMarker;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

public class EditorTab implements FindReplaceTarget {

    SourcePool sourcePool;

    File file;
    SourceEditor editor;
    CTabItem tabItem;

    Font busyFont;

    SourceTokenMarker tokenMarker;

    String tabItemText;
    boolean dirty;

    AtomicBoolean threadRunning = new AtomicBoolean(false);
    AtomicBoolean pendingCompile = new AtomicBoolean(false);

    Set<String> dependencies = new HashSet<String>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    SpinObject object;
    String objectTree;

    Preferences preferences;

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_SPIN1_LIBRARY_PATH:
                    if (!tabItemText.toLowerCase().endsWith(".spin2")) {
                        scheduleCompile();
                    }
                    break;
                case Preferences.PROP_SPIN2_LIBRARY_PATH:
                    if (tabItemText.toLowerCase().endsWith(".spin2")) {
                        scheduleCompile();
                    }
                    break;
            }
        }
    };

    final PropertyChangeListener sourcePoolChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(SourcePool.PROP_DEBUG_ENABLED)) {
                if (tabItemText.toLowerCase().endsWith(".spin2")) {
                    scheduleCompile();
                }
                return;
            }
            File localFile = file != null ? file : new File(tabItemText);
            if (evt.getPropertyName().equals(localFile.getAbsolutePath())) {
                if (evt.getNewValue() != null) {
                    scheduleCompile();
                }
                return;
            }
            if (dependencies.contains(evt.getPropertyName())) {
                scheduleCompile();
                return;
            }
        }

    };

    class EditorTabSourceProvider extends Compiler.SourceProvider {

        File[] searchPaths;

        public EditorTabSourceProvider(File[] searchPaths) {
            this.searchPaths = searchPaths;
        }

        @Override
        public Node getParsedSource(String name) {
            File localFile = file != null ? new File(file.getParentFile(), name) : new File(name);

            Node node = sourcePool.getParsedSource(localFile.getAbsolutePath());
            if (node == null) {
                for (int i = 0; i < searchPaths.length; i++) {
                    localFile = new File(searchPaths[i], name);
                    if ((node = sourcePool.getParsedSource(localFile.getAbsolutePath())) != null) {
                        break;
                    }
                }
            }

            if (node != null) {
                dependencies.add(localFile.getAbsolutePath());
            }

            return node;
        }

        @Override
        public String getSource(String name) {
            File localFile = file != null ? new File(file.getParentFile(), name) : new File(name);
            if (localFile.exists()) {
                try {
                    dependencies.add(localFile.getAbsolutePath());
                    return FileUtils.loadFromFile(localFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            for (int i = 0; i < searchPaths.length; i++) {
                localFile = new File(searchPaths[i], name);
                if (localFile.exists()) {
                    try {
                        dependencies.add(localFile.getAbsolutePath());
                        return FileUtils.loadFromFile(localFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            return null;
        }

        @Override
        public byte[] getResource(String name) {
            File localFile = new File(name);
            if (!localFile.exists()) {
                localFile = file != null ? new File(file.getParentFile(), name) : new File(name);
            }
            if (localFile.exists()) {
                try {
                    dependencies.add(localFile.getAbsolutePath());
                    return FileUtils.loadBinaryFromFile(localFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            for (int i = 0; i < searchPaths.length; i++) {
                localFile = new File(searchPaths[i], name);
                if (localFile.exists()) {
                    try {
                        dependencies.add(localFile.getAbsolutePath());
                        return FileUtils.loadBinaryFromFile(localFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            return null;
        }
    }

    class Spin1TokenMarkerAdatper extends Spin1TokenMarker {

        @Override
        public void refreshTokens(String text) {
            super.refreshTokens(text);

            File localFile = file != null ? new File(file.getParentFile(), tabItemText) : new File(tabItemText);
            sourcePool.setParsedSource(localFile.getAbsolutePath(), getRoot());
        }

        @Override
        protected Node getObjectTree(String fileName) {
            File localFile = file != null ? new File(file.getParentFile(), fileName + ".spin") : new File(fileName + ".spin");

            Node node = sourcePool.getParsedSource(localFile.getAbsolutePath());
            if (node == null && localFile.exists()) {
                try {
                    Spin1TokenStream stream = new Spin1TokenStream(FileUtils.loadFromFile(localFile));
                    Spin1Parser subject = new Spin1Parser(stream);
                    node = subject.parse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (node == null) {
                File[] paths = preferences.getSpin1LibraryPath();
                for (int i = 0; i < paths.length; i++) {
                    localFile = new File(paths[i], fileName + ".spin");
                    if ((node = sourcePool.getParsedSource(localFile.getAbsolutePath())) != null) {
                        break;
                    }
                    if (localFile.exists()) {
                        try {
                            Spin1TokenStream stream = new Spin1TokenStream(FileUtils.loadFromFile(localFile));
                            Spin1Parser subject = new Spin1Parser(stream);
                            node = subject.parse();
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return node;
        }

    }

    class Spin2TokenMarkerAdatper extends Spin2TokenMarker {

        @Override
        public void refreshTokens(String text) {
            super.refreshTokens(text);

            File localFile = file != null ? new File(file.getParentFile(), tabItemText) : new File(tabItemText);
            sourcePool.setParsedSource(localFile.getAbsolutePath(), getRoot());
        }

        @Override
        protected Node getObjectTree(String fileName) {
            File localFile = file != null ? new File(file.getParentFile(), fileName + ".spin2") : new File(fileName + ".spin2");

            Node node = sourcePool.getParsedSource(localFile.getAbsolutePath());
            if (node == null && localFile.exists()) {
                try {
                    Spin2TokenStream stream = new Spin2TokenStream(FileUtils.loadFromFile(localFile));
                    Spin2Parser subject = new Spin2Parser(stream);
                    node = subject.parse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (node == null) {
                File[] searchPaths = preferences.getSpin2LibraryPath();
                for (int i = 0; i < searchPaths.length; i++) {
                    localFile = new File(searchPaths[i], fileName + ".spin2");
                    if ((node = sourcePool.getParsedSource(localFile.getAbsolutePath())) != null) {
                        break;
                    }
                    if (localFile.exists()) {
                        try {
                            Spin2TokenStream stream = new Spin2TokenStream(FileUtils.loadFromFile(localFile));
                            Spin2Parser subject = new Spin2Parser(stream);
                            node = subject.parse();
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return node;
        }

    }

    final Runnable compilerRunnable = new Runnable() {

        @Override
        public void run() {
            if (editor.getControl().isDisposed()) {
                return;
            }
            if (!threadRunning.getAndSet(true)) {
                pendingCompile.set(false);

                File localFile = file != null ? file : new File(tabItemText);
                Node root = sourcePool.getParsedSource(localFile.getAbsolutePath());

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        dependencies.clear();

                        Compiler compiler = tabItemText.toLowerCase().endsWith(".spin2") ? new Spin2Compiler() : new Spin1Compiler();
                        compiler.setRemoveUnusedMethods(true);
                        if (compiler instanceof Spin2Compiler) {
                            compiler.setDebugEnabled(sourcePool.isDebugEnabled());
                            compiler.addSourceProvider(new EditorTabSourceProvider(preferences.getSpin2LibraryPath()));
                        }
                        if (compiler instanceof Spin1Compiler) {
                            compiler.addSourceProvider(new EditorTabSourceProvider(preferences.getSpin1LibraryPath()));
                        }

                        try {
                            object = compiler.compile(tabItemText, root);
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
                                    if (editor == null || editor.getStyledText().isDisposed()) {
                                        return;
                                    }
                                    editor.setCompilerMessages(list);
                                    editor.redraw();
                                    tabItem.setFont(null);
                                    updateTabItemText();
                                }
                            });
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

    public EditorTab(CTabFolder folder, String name, SourcePool sourcePool) {
        this.tabItemText = name;
        this.sourcePool = sourcePool;
        this.preferences = Preferences.getInstance();

        tabItem = new CTabItem(folder, SWT.NONE);
        tabItem.setShowClose(true);
        tabItem.setText(tabItemText);
        tabItem.setData(this);

        FontData[] fontData = tabItem.getFont().getFontData();
        busyFont = new Font(tabItem.getDisplay(), fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC);

        editor = new SourceEditor(folder);

        if (tabItemText.toLowerCase().endsWith(".spin2")) {
            editor.setTokenMarker(tokenMarker = new Spin2TokenMarkerAdatper());
            editor.setHelpProvider(new EditorHelp("Spin2Instructions.xml", new File(""), ".spin2"));
        }
        else {
            editor.setTokenMarker(tokenMarker = new Spin1TokenMarkerAdatper());
            editor.setHelpProvider(new EditorHelp("Spin1Instructions.xml", new File(""), ".spin"));
        }

        editor.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!dirty) {
                    dirty = true;
                    updateTabItemText();
                }
            }
        });

        sourcePool.addPropertyChangeListener(sourcePoolChangeListener);
        preferences.addPropertyChangeListener(preferencesChangeListener);

        tabItem.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                sourcePool.removePropertyChangeListener(sourcePoolChangeListener);
                File localFile = file != null ? new File(file.getParentFile(), tabItemText) : new File(tabItemText);
                sourcePool.removeParsedSource(localFile.getAbsolutePath());
                busyFont.dispose();
            }

        });

        tabItem.setControl(editor.getControl());
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
        File localFile = file != null ? new File(file.getParentFile(), tabItemText) : new File(tabItemText);
        sourcePool.removeParsedSource(localFile.getAbsolutePath());

        this.file = file;

        if (file.getName().toLowerCase().endsWith(".spin2")) {
            editor.setTokenMarker(tokenMarker = new Spin2TokenMarkerAdatper());
            editor.setHelpProvider(new EditorHelp("Spin2Instructions.xml", file.getParentFile(), ".spin2"));
        }
        else {
            editor.setTokenMarker(tokenMarker = new Spin1TokenMarkerAdatper());
            editor.setHelpProvider(new EditorHelp("Spin1Instructions.xml", file.getParentFile(), ".spin"));
        }

        tabItem.setToolTipText(file != null ? file.getAbsolutePath() : "");
    }

    public void setFocus() {
        editor.getControl().setFocus();
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

    public void cut() {
        editor.cut();
    }

    public void copy() {
        editor.copy();
    }

    public void paste() {
        editor.paste();
    }

    public void selectAll() {
        editor.selectAll();
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

    public void addOpenListener(IOpenListener listener) {
        editor.addOpenListener(listener);
        editor.getOutline().addOpenListener(listener);
    }

    public void removeOpenListener(IOpenListener listener) {
        editor.removeOpenListener(listener);
        editor.getOutline().removeOpenListener(listener);
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

    void scheduleCompile() {
        Display.getDefault().timerExec(500, compilerRunnable);
        tabItem.setFont(busyFont);
    }

    public void formatSource() {
        Formatter formatter = tabItemText.toLowerCase().endsWith(".spin2") ? new Spin2Formatter() : new Spin1Formatter();

        //formatter.setKeepBlankLines(true);
        formatter.setPAsmColumns(4, 16, 24, 44, 52);
        //formatter.setInlinePAsmColumns(8, 16, 24, 44, 52);
        //formatter.setAdjustPAsmColumns(true);
        //formatter.setIsolateLargeLabels(true);

        editor.format(formatter);
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public String getObjectTree() {
        return objectTree;
    }

}
