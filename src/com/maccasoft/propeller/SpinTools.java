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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.maccasoft.propeller.internal.ImageRegistry;
import com.maccasoft.propeller.internal.TempDirectory;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Interpreter;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SpinTools {

    public static final String APP_TITLE = "Spin Tools";
    public static final String APP_VERSION = "0.0.1";

    Shell shell;
    CTabFolder tabFolder;
    SerialPortList serialPortList;

    Preferences preferences;

    public SpinTools(Shell shell) {
        this.shell = shell;
        this.shell.setData(this);

        Menu menu = new Menu(shell, SWT.BAR);
        createFileMenu(menu);
        createEditMenu(menu);
        //createSketchMenu(menu);
        createToolsMenu(menu);
        createHelpMenu(menu);
        shell.setMenuBar(menu);

        Composite container = new Composite(shell, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        tabFolder = new CTabFolder(container, SWT.BORDER);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tabFolder.setMaximizeVisible(false);
        tabFolder.setMinimizeVisible(false);
        tabFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.item != null && e.item.getData() != null) {
                    ((CTabItem) e.item).getControl().setFocus();
                }
            }
        });
        createTabFolderMenu();

        tabFolder.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.character != SWT.TAB || (e.stateMask & SWT.MODIFIER_MASK) == 0) {
                    return;
                }
                int index = tabFolder.getSelectionIndex();
                if ((e.stateMask & SWT.SHIFT) != 0) {
                    index--;
                    if (index < 0) {
                        index = tabFolder.getItemCount() - 1;
                    }
                }
                else {
                    index++;
                    if (index >= tabFolder.getItemCount()) {
                        index = 0;
                    }
                }
                tabFolder.setSelection(index);

                EditorTab tab = (EditorTab) tabFolder.getItem(index).getData();
                tab.setFocus();

                e.doit = false;
            }
        });

        preferences = Preferences.getInstance();

        serialPortList = new SerialPortList();

        String port = preferences.getPort();
        if (port != null) {
            serialPortList.setSelection(port);
        }

        serialPortList.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String port = (String) evt.getNewValue();
                SerialTerminal serialTerminal = getSerialTerminal();
                if (serialTerminal != null) {
                    if (!port.equals(serialTerminal.getSerialPort().getPortName())) {
                        SerialPort oldSerialPort = serialTerminal.getSerialPort();
                        try {
                            if (oldSerialPort.isOpened()) {
                                oldSerialPort.closePort();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        serialTerminal.setSerialPort(new SerialPort(port));
                    }
                }
                preferences.setPort(port);
            }

        });

        shell.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                try {
                    SerialTerminal serialTerminal = getSerialTerminal();
                    if (serialTerminal != null) {
                        serialTerminal.close();
                    }
                    preferences.save();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    void createFileMenu(Menu parent) {
        Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("&File");
        item.setMenu(menu);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New\tCtrl+N");
        item.setAccelerator(SWT.CTRL + 'N');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleFileNew();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New (From P1 template)\tCtrl+Alt+1");
        item.setAccelerator(SWT.CTRL + SWT.ALT + '1');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleFileNewSpin1();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New (From P2 template)\tCtrl+Alt+1");
        item.setAccelerator(SWT.CTRL + SWT.ALT + '2');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleFileNewSpin2();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Open...");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleFileOpen();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Save\tCtrl+S");
        item.setAccelerator(SWT.MOD1 + 'S');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleFileSave();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Save As...");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleFileSaveAs();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Preferences");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        final int lruIndex = menu.getItemCount();

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Exit");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    shell.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        menu.addMenuListener(new MenuListener() {

            List<MenuItem> list = new ArrayList<MenuItem>();

            @Override
            public void menuShown(MenuEvent e) {
                for (MenuItem item : list) {
                    item.dispose();
                }
                list.clear();
                populateLruFiles(menu, lruIndex, list);
            }

            @Override
            public void menuHidden(MenuEvent e) {
            }
        });
    }

    void populateLruFiles(Menu menu, int itemIndex, List<MenuItem> list) {
        int index = 0;

        Iterator<String> iter = Preferences.getInstance().getLru().iterator();
        while (iter.hasNext()) {
            final File fileToOpen = new File(iter.next());
            MenuItem item = new MenuItem(menu, SWT.PUSH, itemIndex++);
            item.setText(String.format("%d %s", index + 1, fileToOpen.getName()));
            item.setToolTipText(fileToOpen.getAbsolutePath());
            item.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    try {
                        EditorTab editorTab = new EditorTab(tabFolder, fileToOpen.getName());
                        try {
                            editorTab.setEditorText(loadFromFile(fileToOpen));
                            editorTab.setFile(fileToOpen);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tabFolder.setSelection(tabFolder.getItemCount() - 1);
                        editorTab.setFocus();
                        preferences.addToLru(fileToOpen);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            list.add(item);
            index++;
        }

        if (index > 0) {
            list.add(new MenuItem(menu, SWT.SEPARATOR, itemIndex));
        }
    }

    private void handleFileNew() {
        String suffix = ".spin";

        if (tabFolder.getSelection() != null) {
            EditorTab currentTab = (EditorTab) tabFolder.getSelection().getData();
            String tabName = currentTab.getText();
            suffix = tabName.substring(tabName.lastIndexOf('.'));
        }

        String name = getUniqueName("Untitled", suffix);
        EditorTab editorTab = new EditorTab(tabFolder, name);
        tabFolder.setSelection(tabFolder.getItemCount() - 1);
        editorTab.setFocus();
    }

    private void handleFileNewSpin1() {
        String name = getUniqueName("Untitled", ".spin");
        EditorTab editorTab = new EditorTab(tabFolder, name);
        editorTab.setEditorText(getResourceAsString("template.spin"));
        tabFolder.setSelection(tabFolder.getItemCount() - 1);
        editorTab.setFocus();
    }

    private void handleFileNewSpin2() {
        String name = getUniqueName("Untitled", ".spin2");
        EditorTab editorTab = new EditorTab(tabFolder, name);
        editorTab.setEditorText(getResourceAsString("template.spin2"));
        tabFolder.setSelection(tabFolder.getItemCount() - 1);
        editorTab.setFocus();
    }

    String getUniqueName(String prefix, String suffix) {
        int count = 0;
        String name = prefix + suffix;

        int index = 0;
        while (index < tabFolder.getItemCount()) {
            CTabItem tabItem = tabFolder.getItem(index);
            EditorTab editorTab = (EditorTab) tabItem.getData();
            if (editorTab.getText().equalsIgnoreCase(name)) {
                name = prefix + String.valueOf(++count) + suffix;
                index = -1;
            }
            index++;
        }

        return name;
    }

    String getResourceAsString(String name) {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void handleFileOpen() {
        FileDialog dlg = new FileDialog(shell, SWT.OPEN);
        dlg.setText("Open Spin File");
        String[] filterNames = new String[] {
            "Spin Files"
        };
        String[] filterExtensions = new String[] {
            "*.spin;*.spin2"
        };
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);

        File filterPath = null;

        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            filterPath = editorTab.getFile();
        }
        if (filterPath == null && preferences.getLru().size() != 0) {
            filterPath = new File(preferences.getLru().get(0));
        }

        if (filterPath != null) {
            dlg.setFilterPath(filterPath.getParent());
        }

        String fileName = dlg.open();
        if (fileName != null) {
            File fileToOpen = new File(fileName);

            EditorTab editorTab = new EditorTab(tabFolder, fileToOpen.getName());
            try {
                editorTab.setEditorText(loadFromFile(fileToOpen));
                editorTab.setFile(fileToOpen);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tabFolder.setSelection(tabFolder.getItemCount() - 1);
            editorTab.setFocus();
            preferences.addToLru(fileToOpen);
        }
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

    private void handleFileSave() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            File fileToSave = editorTab.getFile();
            if (fileToSave == null) {
                handleFileSaveAs();
                return;
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
                writer.write(editorTab.getEditorText());
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleFileSaveAs() {
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setText("Save Spin File");
        String[] filterNames = new String[] {
            "Spin Files"
        };
        String[] filterExtensions = new String[] {
            "*.spin;*.spin2"
        };
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);

        File filterPath = null;

        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            dlg.setFileName(editorTab.getText());

            filterPath = editorTab.getFile();
            if (filterPath == null && preferences.getLru().size() != 0) {
                filterPath = new File(preferences.getLru().get(0));
            }
            if (filterPath != null) {
                dlg.setFilterPath(filterPath.getParent());
            }

            String fileName = dlg.open();
            if (fileName != null) {
                File fileToSave = new File(fileName);
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
                    writer.write(editorTab.getEditorText());
                    writer.close();
                    editorTab.setFile(fileToSave);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Menu createEditMenu(Menu parent) {
        Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("&Edit");
        item.setMenu(menu);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Undo\tCtrl+Z");
        item.setAccelerator(SWT.MOD1 + 'Z');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.undo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Redo\tCtrl+Shift+Z");
        item.setAccelerator(SWT.MOD1 + SWT.MOD2 + 'Z');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.redo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Cut\tCtrl+X");
        item.setAccelerator(SWT.MOD1 + 'X');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.cut();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy\tCtrl+C");
        item.setAccelerator(SWT.MOD1 + 'C');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.copy();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Paste\tCtrl+V");
        item.setAccelerator(SWT.MOD1 + 'V');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.paste();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Select All\tCtrl+A");
        item.setAccelerator(SWT.MOD1 + 'A');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.selectAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Find / Replace...\tCtrl+F");
        item.setAccelerator(SWT.MOD1 + 'F');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Find Next\tCtrl+K");
        item.setAccelerator(SWT.MOD1 + 'K');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        return menu;
    }

    Menu createToolsMenu(Menu parent) {
        Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("&Tools");
        item.setMenu(menu);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Compile\tF8");
        item.setAccelerator(SWT.F8);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleCompile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to RAM\tF9");
        item.setAccelerator(SWT.F9);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    handleCompileAndUpload();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to Flash\tF10");
        item.setAccelerator(SWT.F10);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Serial Terminal\tCtrl+T");
        item.setAccelerator(SWT.MOD1 + 'T');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    SerialTerminal serialTerminal = getSerialTerminal();
                    if (serialTerminal == null) {
                        serialTerminal = new SerialTerminal(new SerialPort(serialPortList.getSelection()));
                        serialTerminal.open();
                    }
                    serialTerminal.getControl().setFocus();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        createPortMenu(menu);

        return menu;
    }

    void createPortMenu(Menu parent) {
        final Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);
        menu.addMenuListener(new MenuListener() {

            @Override
            public void menuShown(MenuEvent e) {
                serialPortList.fillMenu(menu);
            }

            @Override
            public void menuHidden(MenuEvent e) {

            }
        });

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("&Port");
        item.setMenu(menu);
    }

    private void handleCompile() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            return;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();
        if (editorTab.getText().toLowerCase().endsWith(".spin2")) {
            handleInternalCompile();
        }
        else {
            handleOpenSpinCompile();
        }
    }

    private void handleInternalCompile() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            return;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();

        String text = editorTab.getEditorText();
        Spin2TokenStream stream = new Spin2TokenStream(text);

        IRunnableWithProgress thread = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Compile and Upload", IProgressMonitor.UNKNOWN);

                try {
                    Spin2Parser parser = new Spin2Parser(stream);
                    Node root = parser.parse();

                    Spin2Compiler compiler = new Spin2Compiler();
                    Spin2Object obj = compiler.compile(root);

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    if (compiler.hasSpinMethods()) {
                        Spin2Interpreter interpreter = new Spin2Interpreter();
                        interpreter.setVBase(interpreter.getPBase() + obj.getSize());
                        interpreter.setDBase(interpreter.getPBase() + obj.getSize() + compiler.getVarSize());
                        os.write(interpreter.getCode());
                    }
                    obj.generateBinary(os);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                monitor.done();
            }

        };

        ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
        try {
            dlg.run(true, true, thread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOpenSpinCompile() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            return;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();
        File editorFile = editorTab.getFile();

        File file = new File(TempDirectory.location(), editorTab.getText());
        File binaryFile = new File(TempDirectory.location(), editorTab.getText() + ".binary");
        try {
            file.getParentFile().mkdirs();

            String source = editorTab.getEditorText();

            Writer os = new OutputStreamWriter(new FileOutputStream(file));
            os.write(source);
            os.close();

            Spin1TokenStream stream = new Spin1TokenStream(source);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            root.accept(new NodeVisitor() {

                @Override
                public void visitObject(ObjectNode node) {
                    String name = node.file.getText();
                    if (name.startsWith("\"")) {
                        name = name.substring(1);
                    }
                    if (name.endsWith("\"")) {
                        name = name.substring(0, name.length() - 1);
                    }
                    exportObjectFile(name + ".spin");
                }

            });

        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        //console.clear();

        IRunnableWithProgress thread = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Compile and Upload", IProgressMonitor.UNKNOWN);

                try {
                    String toolchainPath = "/home/marco/workspace/spin-tools/build/linux/";

                    List<String> cmd = new ArrayList<String>();
                    cmd.add(toolchainPath + "openspin");
                    cmd.add("-b");
                    cmd.add("-u");
                    cmd.add("-L.");
                    if (editorFile != null) {
                        cmd.add("-L" + editorFile.getParent());
                    }
                    cmd.add("-o" + binaryFile.getName());
                    cmd.add(file.getName());

                    runCommand(cmd, TempDirectory.location());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                monitor.done();
            }

        };

        ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
        try {
            dlg.run(true, true, thread);

            if (binaryFile.exists()) {
                byte[] data = new byte[32768];
                InputStream is = new FileInputStream(binaryFile);
                is.read(data);
                is.close();

                MemoryDialog dlg2 = new MemoryDialog(shell);
                dlg2.setData(data);
                dlg2.open();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void exportObjectFile(String name) {
        CTabItem[] tabItem = tabFolder.getItems();
        for (int i = 0; i < tabItem.length; i++) {
            EditorTab editorTab = (EditorTab) tabItem[i].getData();
            if (name.equals(editorTab.getText())) {
                File file = new File(TempDirectory.location(), editorTab.getText());
                try {
                    Writer os = new OutputStreamWriter(new FileOutputStream(file));
                    os.write(editorTab.getEditorText());
                    os.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    int runCommand(List<String> cmd, File outDir) throws IOException, InterruptedException {
        PrintStream out = System.out;
        PrintStream err = System.err;

        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.directory(outDir);

        StringBuilder sb = new StringBuilder();
        for (String s : cmd) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(s);
        }
        out.println(sb.toString());

        Process p = builder.start();
        Thread ioStream = new Thread() {
            int outLength, errLength;
            byte[] buffer = new byte[1024];

            @Override
            public void run() {
                try {
                    InputStream outIs = p.getInputStream();
                    InputStream errIs = p.getErrorStream();
                    do {
                        while ((outLength = outIs.read(buffer)) > 0) {
                            out.write(buffer, 0, outLength);
                        }
                        while ((errLength = errIs.read(buffer)) > 0) {
                            err.write(buffer, 0, errLength);
                        }
                    } while (outLength != -1 || errLength != -1);
                    outIs.close();
                    errIs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ioStream.start();

        return p.waitFor();
    }

    private void handleCompileAndUpload() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            return;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();
        if (editorTab.getText().toLowerCase().endsWith(".spin2")) {
            handleInternalCompileAndUpload();
        }
        else {
            handleOpenSpinCompileAndUpload();
        }
    }

    private void handleInternalCompileAndUpload() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            return;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();

        SerialTerminal serialTerminal = getSerialTerminal();

        String text = editorTab.getEditorText();
        Spin2TokenStream stream = new Spin2TokenStream(text);

        IRunnableWithProgress thread = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Compile and Upload", IProgressMonitor.UNKNOWN);

                try {
                    Spin2Parser parser = new Spin2Parser(stream);
                    Node root = parser.parse();

                    Spin2Compiler compiler = new Spin2Compiler();
                    Spin2Object obj = compiler.compile(root);

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    if (compiler.hasSpinMethods()) {
                        Spin2Interpreter interpreter = new Spin2Interpreter();
                        interpreter.setVBase(interpreter.getPBase() + obj.getSize());
                        interpreter.setDBase(interpreter.getPBase() + obj.getSize() + compiler.getVarSize());
                        os.write(interpreter.getCode());
                    }
                    obj.generateBinary(os);

                    SerialPort serialPort = null;
                    boolean shared = false;

                    if (serialTerminal != null) {
                        SerialPort terminalPort = serialTerminal.getSerialPort();
                        if (terminalPort.getPortName().equals(serialPortList.getSelection())) {
                            Display.getDefault().syncExec(new Runnable() {

                                @Override
                                public void run() {
                                    serialTerminal.setSerialPort(null);
                                }
                            });
                            serialPort = terminalPort;
                            shared = true;
                        }
                    }
                    if (serialPort == null) {
                        serialPort = new SerialPort(serialPortList.getSelection());
                    }

                    Propeller2Loader loader = new Propeller2Loader(serialPort, shared) {

                        @Override
                        protected void bufferUpload(int type, byte[] binaryImage, String text) throws SerialPortException, IOException {
                            monitor.setTaskName("Loading " + text + " to RAM");
                            super.bufferUpload(type, binaryImage, text);
                        }

                        @Override
                        protected void notifyProgress(int sent, int total) {
                            if (sent == total) {
                                monitor.subTask(String.format("%d bytes sent", total));
                            }
                            else {
                                monitor.subTask(String.format("%d bytes remaining", total - sent));
                            }
                        }

                        @Override
                        protected void verifyRam() throws SerialPortException, IOException {
                            monitor.setTaskName("Verifying RAM ... ");
                            super.verifyRam();
                        }

                    };
                    loader.upload(os.toByteArray(), 0);

                    if (shared) {
                        SerialPort terminalPort = serialPort;
                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                if (serialTerminal != null) {
                                    serialTerminal.setSerialPort(terminalPort);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                monitor.done();
            }

        };

        ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
        try {
            dlg.run(true, true, thread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOpenSpinCompileAndUpload() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            return;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();
        File editorFile = editorTab.getFile();

        File file = new File(TempDirectory.location(), editorFile.getName());
        File binaryFile = new File(TempDirectory.location(), editorFile.getName() + ".binary");
        try {
            file.getParentFile().mkdirs();

            String source = editorTab.getEditorText();

            Writer os = new OutputStreamWriter(new FileOutputStream(file));
            os.write(source);
            os.close();

            Spin1TokenStream stream = new Spin1TokenStream(source);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            root.accept(new NodeVisitor() {

                @Override
                public void visitObject(ObjectNode node) {
                    String name = node.file.getText();
                    if (name.startsWith("\"")) {
                        name = name.substring(1);
                    }
                    if (name.endsWith("\"")) {
                        name = name.substring(0, name.length() - 1);
                    }
                    exportObjectFile(name + ".spin");
                }

            });

        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        //console.clear();

        IRunnableWithProgress thread = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Compile and Upload", IProgressMonitor.UNKNOWN);

                try {
                    String toolchainPath = "/home/marco/workspace/spin-tools/build/linux/";

                    List<String> cmd = new ArrayList<String>();
                    cmd.add(toolchainPath + "openspin");
                    cmd.add("-b");
                    cmd.add("-u");
                    cmd.add("-L.");
                    cmd.add("-L" + editorFile.getParent());
                    cmd.add("-o" + binaryFile.getName());
                    cmd.add(file.getName());

                    runCommand(cmd, TempDirectory.location());

                    PropellerLoader loader = new PropellerLoader(serialPortList.getSelection()) {

                        @Override
                        protected void bufferUpload(int type, byte[] binaryImage, String text) throws SerialPortException, IOException {
                            monitor.setTaskName("Loading " + text + " to RAM");
                            super.bufferUpload(type, binaryImage, text);
                        }

                        @Override
                        protected void notifyProgress(int sent, int total) {
                            if (sent == total) {
                                monitor.subTask(String.format("%d bytes sent", total));
                            }
                            else {
                                monitor.subTask(String.format("%d bytes remaining", total - sent));
                            }
                        }

                        @Override
                        protected void verifyRam() throws SerialPortException, IOException {
                            monitor.setTaskName("Verifying RAM ... ");
                            super.verifyRam();
                        }

                    };

                    if (binaryFile.exists()) {
                        loader.upload(binaryFile, 0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                monitor.done();
            }

        };

        ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
        try {
            dlg.run(true, true, thread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createHelpMenu(Menu parent) {
        Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("&Help");
        item.setMenu(menu);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("About " + APP_TITLE);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                AboutDialog dlg = new AboutDialog(shell);
                dlg.open();
            }
        });
    }

    void createTabFolderMenu() {
        final ToolBar toolBar = new ToolBar(tabFolder, SWT.FLAT);

        final Menu menu = new Menu(toolBar);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Next Tab\tCtrl+Tab");
        item.setAccelerator(SWT.MOD1 + SWT.TAB);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Previous Tab\tCtrl+Shift+Tab");
        item.setAccelerator(SWT.MOD1 + SWT.MOD2 + SWT.TAB);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        final int entriesTokeep = menu.getItemCount();

        final ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("vertical-dots.png"));
        toolItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                while (menu.getItemCount() > entriesTokeep) {
                    menu.getItem(menu.getItemCount() - 1).dispose();
                }
                if (tabFolder.getItemCount() != 0) {
                    new MenuItem(menu, SWT.SEPARATOR);
                }
                for (CTabItem tabItem : tabFolder.getItems()) {
                    MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
                    menuItem.setText(tabItem.getText());
                    menuItem.setSelection(tabFolder.getSelection() == tabItem);
                    menuItem.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(Event e) {
                            tabFolder.setSelection(tabItem);
                            tabItem.getControl().setFocus();
                        }
                    });
                }

                Rectangle rect = toolItem.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = toolBar.toDisplay(pt);
                menu.setLocation(pt.x, pt.y);
                menu.setVisible(true);
            }
        });

        tabFolder.setTopRight(toolBar);
    }

    SerialTerminal getSerialTerminal() {
        Shell[] shells = Display.getDefault().getShells();
        for (int i = 0; i < shells.length; i++) {
            if (shells[i].getData() instanceof SerialTerminal) {
                return (SerialTerminal) shells[i].getData();
            }
        }
        return null;
    }

    static {
        Display.setAppName(APP_TITLE);
        Display.setAppVersion(APP_VERSION);
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
                    shell.setText("Spin Tools");

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

                    new SpinTools(shell);

                    shell.open();

                    while (display.getShells().length != 0) {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    }

                    TempDirectory.clean();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        display.dispose();
    }

}
