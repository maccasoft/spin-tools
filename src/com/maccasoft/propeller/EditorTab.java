/*
 * Copyright (c) 2016 Marco Maccaferri and others.
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.EditorTokenMarker.TokenMarker;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenMarker;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenMarker;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

public class EditorTab {

    File file;
    SourceEditor editor;
    CTabItem tabItem;

    Font busyFont;

    EditorTokenMarker tokenMarker;

    String tabItemText;
    boolean dirty;

    AtomicBoolean threadRunning = new AtomicBoolean(false);
    AtomicBoolean pendingCompile = new AtomicBoolean(false);

    boolean errors;
    List<CompilerMessage> messages = new ArrayList<CompilerMessage>();
    Object object;

    final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

        }
    };

    final PropertyChangeListener settingsChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

        }
    };

    class Spin1TokenMarkerAdatper extends Spin1TokenMarker {

        @Override
        protected Node getObjectTree(String fileName) {
            String fileType = tabItemText.substring(tabItemText.lastIndexOf('.'));
            AtomicReference<Node> result = new AtomicReference<Node>();
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    File libraryPath = new File(Preferences.getInstance().getSpin1LibraryPath()).getAbsoluteFile();
                    Node node = getNodeRootFromTab(fileName + fileType, libraryPath);
                    result.set(node);
                }
            });
            Node root = result.get();
            if (root == null) {
                File fileParent = file != null ? file.getParentFile() : null;
                File file = new File(fileParent, fileName + fileType);
                if (!file.exists()) {
                    file = new File(Preferences.getInstance().getSpin1LibraryPath(), fileName + fileType);
                }
                if (file.exists()) {
                    try {
                        Spin1TokenStream stream = new Spin1TokenStream(loadFromFile(file));
                        Spin1Parser subject = new Spin1Parser(stream);
                        root = subject.parse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return root;
        }

    }

    class Spin1CompilerAdapter extends Spin1Compiler {

        @Override
        protected Node getParsedObject(String fileName) {
            AtomicReference<Node> result = new AtomicReference<Node>();
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    File libraryPath = new File(Preferences.getInstance().getSpin1LibraryPath()).getAbsoluteFile();
                    Node node = getNodeRootFromTab(fileName, libraryPath);
                    result.set(node);
                }
            });
            Node root = result.get();
            if (root == null) {
                File fileParent = file != null ? file.getParentFile() : null;
                File file = new File(fileParent, fileName);
                if (!file.exists()) {
                    file = new File(Preferences.getInstance().getSpin1LibraryPath(), fileName);
                }
                if (file.exists()) {
                    try {
                        Spin1TokenStream stream = new Spin1TokenStream(loadFromFile(file));
                        Spin1Parser subject = new Spin1Parser(stream);
                        root = subject.parse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return root;
        }

    }

    final Runnable spin1CompilerRunnable = new Runnable() {

        @Override
        public void run() {
            if (editor.getControl().isDisposed()) {
                return;
            }
            if (!threadRunning.getAndSet(true)) {
                pendingCompile.set(false);

                Node root = tokenMarker.getRoot();
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Spin1Compiler compiler = new Spin1CompilerAdapter();
                        try {
                            object = compiler.compile(tabItemText, root);
                            errors = compiler.hasErrors();
                        } catch (Exception e) {
                            errors = true;
                            e.printStackTrace();
                        }

                        if (!pendingCompile.get()) {
                            messages.clear();
                            messages.addAll(compiler.getMessages());

                            List<CompilerMessage> list = new ArrayList<CompilerMessage>();
                            for (CompilerMessage msg : messages) {
                                if (tabItemText.equals(msg.fileName)) {
                                    list.add(msg);
                                }
                            }
                            editor.setCompilerMessages(list);

                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    if (editor == null || editor.getStyledText().isDisposed()) {
                                        return;
                                    }
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
                Display.getDefault().timerExec(250, spin1CompilerRunnable);
            }
        }

    };

    class Spin2TokenMarkerAdatper extends Spin2TokenMarker {

        @Override
        protected Node getObjectTree(String fileName) {
            String fileType = tabItemText.substring(tabItemText.lastIndexOf('.'));
            AtomicReference<Node> result = new AtomicReference<Node>();
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    File libraryPath = new File(Preferences.getInstance().getSpin2LibraryPath()).getAbsoluteFile();
                    Node node = getNodeRootFromTab(fileName + fileType, libraryPath);
                    result.set(node);
                }
            });
            Node root = result.get();
            if (root == null) {
                File fileParent = file != null ? file.getParentFile() : null;
                File file = new File(fileParent, fileName + fileType);
                if (!file.exists()) {
                    file = new File(Preferences.getInstance().getSpin2LibraryPath(), fileName + fileType);
                }
                if (file.exists()) {
                    try {
                        Spin2TokenStream stream = new Spin2TokenStream(loadFromFile(file));
                        Spin2Parser subject = new Spin2Parser(stream);
                        root = subject.parse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return root;
        }

    }

    class Spin2CompilerAdapter extends Spin2Compiler {

        @Override
        protected Node getParsedObject(String fileName) {
            AtomicReference<Node> result = new AtomicReference<Node>();
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    File libraryPath = new File(Preferences.getInstance().getSpin2LibraryPath()).getAbsoluteFile();
                    Node node = getNodeRootFromTab(fileName, libraryPath);
                    result.set(node);
                }
            });
            Node root = result.get();
            if (root == null) {
                File fileParent = file != null ? file.getParentFile() : null;
                File file = new File(fileParent, fileName);
                if (!file.exists()) {
                    file = new File(Preferences.getInstance().getSpin2LibraryPath(), fileName);
                }
                if (file.exists()) {
                    try {
                        Spin2TokenStream stream = new Spin2TokenStream(loadFromFile(file));
                        Spin2Parser subject = new Spin2Parser(stream);
                        root = subject.parse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return root;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            File fileParent = file != null ? file.getParentFile() : null;

            try {
                File fileToLoad = new File(fileParent, fileName);
                if (!fileToLoad.exists()) {
                    fileToLoad = new File(Preferences.getInstance().getSpin2LibraryPath(), fileName);
                }
                InputStream is = new FileInputStream(fileToLoad);
                try {
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return b;
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    final Runnable spin2CompilerRunnable = new Runnable() {

        @Override
        public void run() {
            if (editor.getControl().isDisposed()) {
                return;
            }
            if (!threadRunning.getAndSet(true)) {
                pendingCompile.set(false);

                Node root = tokenMarker.getRoot();
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Spin2Compiler compiler = new Spin2CompilerAdapter();
                        try {
                            object = compiler.compile(tabItemText, root);
                            errors = compiler.hasErrors();
                        } catch (Exception e) {
                            errors = true;
                            e.printStackTrace();
                        }

                        if (!pendingCompile.get()) {
                            messages.clear();
                            messages.addAll(compiler.getMessages());

                            List<CompilerMessage> list = new ArrayList<CompilerMessage>();
                            for (CompilerMessage msg : messages) {
                                if (tabItemText.equals(msg.fileName)) {
                                    list.add(msg);
                                }
                            }
                            editor.setCompilerMessages(list);

                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    if (editor == null || editor.getStyledText().isDisposed()) {
                                        return;
                                    }
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
                Display.getDefault().timerExec(250, spin2CompilerRunnable);
            }
        }

    };

    public EditorTab(CTabFolder folder, String name) {
        tabItem = new CTabItem(folder, SWT.NONE);
        tabItem.setShowClose(true);
        tabItem.setText(tabItemText = name);
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
                if (tabItemText.toLowerCase().endsWith(".spin2")) {
                    Display.getDefault().timerExec(500, spin2CompilerRunnable);
                }
                else {
                    Display.getDefault().timerExec(500, spin1CompilerRunnable);
                }
                tabItem.setFont(busyFont);
            }
        });

        tabItem.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
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
        this.file = file;
        if (file.getName().toLowerCase().endsWith(".spin2")) {
            editor.setTokenMarker(tokenMarker = new Spin2TokenMarkerAdatper());
            editor.setHelpProvider(new EditorHelp("Spin2Instructions.xml", file.getParentFile(), ".spin2"));
        }
        else {
            editor.setTokenMarker(tokenMarker = new Spin1TokenMarkerAdatper());
            editor.setHelpProvider(new EditorHelp("Spin1Instructions.xml", file.getParentFile(), ".spin"));
        }
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
        if (tabItemText.toLowerCase().endsWith(".spin2")) {
            Display.getDefault().timerExec(1000, spin2CompilerRunnable);
        }
        else {
            Display.getDefault().timerExec(1000, spin1CompilerRunnable);
        }
        tabItem.setFont(busyFont);
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

    public List<CompilerMessage> getMessages() {
        return messages;
    }

    public Object getObject() {
        return object;
    }

    public void goToFirstError() {
        Iterator<TokenMarker> iter = tokenMarker.getCompilerTokens().iterator();
        if (iter.hasNext()) {
            TokenMarker marker = iter.next();
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            editor.gotToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
        }
    }

    public void goToNextError() {
        int offset = editor.getStyledText().getCaretOffset();
        int line = editor.getStyledText().getLineAtOffset(offset);

        Iterator<TokenMarker> iter = tokenMarker.getCompilerTokens().iterator();
        while (iter.hasNext()) {
            TokenMarker marker = iter.next();
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            if (markerLine > line) {
                editor.gotToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
                return;
            }
        }

        iter = tokenMarker.getCompilerTokens().iterator();
        if (iter.hasNext()) {
            TokenMarker marker = iter.next();
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            editor.gotToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
        }

        Display.getDefault().beep();
    }

    public void goToPreviousError() {
        int offset = editor.getStyledText().getCaretOffset();
        int line = editor.getStyledText().getLineAtOffset(offset);

        Iterator<TokenMarker> iter = tokenMarker.getCompilerTokens().descendingIterator();
        while (iter.hasNext()) {
            TokenMarker marker = iter.next();
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            if (markerLine < line) {
                editor.gotToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
                return;
            }
        }

        iter = tokenMarker.getCompilerTokens().descendingIterator();
        if (iter.hasNext()) {
            TokenMarker marker = iter.next();
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            editor.gotToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
        }

        Display.getDefault().beep();
    }

    protected Node getNodeRootFromTab(String fileName, File libraryPath) {
        File fileParent = file != null ? file.getParentFile().getAbsoluteFile() : null;

        CTabFolder tabFolder = tabItem.getParent();
        for (int i = 0; i < tabFolder.getItemCount(); i++) {
            EditorTab editorTab = (EditorTab) tabFolder.getItem(i).getData();
            File tabParent = editorTab.file != null ? editorTab.file.getParentFile().getAbsoluteFile() : new File("").getAbsoluteFile();
            if (fileParent == null || tabParent.equals(fileParent) || tabParent.equals(libraryPath)) {
                if (fileName.equals(editorTab.tabItemText)) {
                    return editorTab.tokenMarker.getRoot();
                }
            }
        }

        return null;
    }

    String loadFromFile(File file) throws Exception {
        String line;
        StringBuilder sb = new StringBuilder();

        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
        }

        return sb.toString();
    }

    public SourceEditor getEditor() {
        return editor;
    }

}
