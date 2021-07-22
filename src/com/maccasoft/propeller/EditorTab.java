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
import java.io.FileReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenMarker;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Object;
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

    class Spin1CompilerAdapter extends Spin1Compiler {

        @Override
        protected Spin1Object getObject(String fileName) {
            AtomicReference<Node> result = new AtomicReference<Node>();
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    Node node = getNodeRootFromTab(fileName);
                    result.set(node);
                }
            });
            Node root = result.get();
            if (root == null) {
                File fileParent = file != null ? file.getParentFile() : null;
                String fileType = tabItemText.substring(tabItemText.lastIndexOf('.'));
                File file = new File(fileParent, fileName + fileType);
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
            if (root != null) {
                Spin1CompilerAdapter c = new Spin1CompilerAdapter();
                return c.compileObject(root);
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
                            object = compiler.compile(root);
                            errors = compiler.hasErrors();
                        } catch (Exception e) {
                            errors = true;
                            e.printStackTrace();
                        }

                        if (!pendingCompile.get()) {
                            editor.setCompilerMessages(compiler.getMessages());
                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    if (editor == null || editor.getStyledText().isDisposed()) {
                                        return;
                                    }
                                    editor.redraw();
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

    class Spin2CompilerAdapter extends Spin2Compiler {

        @Override
        protected Spin2Object getObject(String fileName) {
            AtomicReference<Node> result = new AtomicReference<Node>();
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    Node node = getNodeRootFromTab(fileName);
                    result.set(node);
                }
            });
            Node root = result.get();
            if (root == null) {
                File fileParent = file != null ? file.getParentFile() : null;
                String fileType = tabItemText.substring(tabItemText.lastIndexOf('.'));
                File file = new File(fileParent, fileName + fileType);
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
            if (root != null) {
                Spin2CompilerAdapter c = new Spin2CompilerAdapter();
                return c.compileObject(root);
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
                            object = compiler.compile(root);
                            errors = compiler.hasErrors();
                        } catch (Exception e) {
                            errors = true;
                            e.printStackTrace();
                        }

                        if (!pendingCompile.get()) {
                            editor.setCompilerMessages(compiler.getMessages());
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
            tokenMarker = new Spin2TokenMarker();
        }
        else {
            tokenMarker = new Spin1TokenMarker();
        }
        editor.setTokenMarker(tokenMarker);

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
            tokenMarker = new Spin2TokenMarker();
        }
        else {
            tokenMarker = new Spin1TokenMarker();
        }
        editor.setTokenMarker(tokenMarker);
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

    public Object getObject() {
        return object;
    }

    public void goToNextError() {
        int offset = editor.getStyledText().getCaretOffset();
        int line = editor.getStyledText().getLineAtOffset(offset);
        for (TokenMarker marker : tokenMarker.getCompilerTokens()) {
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            if (markerLine > line) {
                editor.gotToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
                return;
            }
        }
        Display.getDefault().beep();
    }

    public void goToPreviousError() {
        int offset = editor.getStyledText().getCaretOffset();
        int line = editor.getStyledText().getLineAtOffset(offset);
        for (TokenMarker marker : tokenMarker.getCompilerTokens()) {
            int markerLine = editor.getStyledText().getLineAtOffset(marker.start);
            if (markerLine < line) {
                editor.gotToLineColumn(markerLine, marker.start - editor.getStyledText().getOffsetAtLine(markerLine));
                return;
            }
        }
        Display.getDefault().beep();
    }

    protected Node getNodeRootFromTab(String fileName) {
        File fileParent = file != null ? file.getParentFile() : null;
        String fileType = tabItemText.substring(tabItemText.lastIndexOf('.'));

        CTabFolder tabFolder = tabItem.getParent();
        for (int i = 0; i < tabFolder.getItemCount(); i++) {
            EditorTab editorTab = (EditorTab) tabFolder.getItem(i).getData();
            File tabParent = editorTab.file != null ? editorTab.file.getParentFile() : new File("");
            if (fileParent == null || tabParent.equals(fileParent)) {
                int extIndex = editorTab.tabItemText.lastIndexOf('.');
                String tabType = editorTab.tabItemText.substring(extIndex);
                if (tabType.equals(fileType) && fileName.equals(editorTab.tabItemText.substring(0, extIndex))) {
                    return editorTab.tokenMarker.getRoot();
                }
            }
        }

        return null;
    }

}
