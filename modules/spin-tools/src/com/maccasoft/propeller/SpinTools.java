/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Platform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.maccasoft.propeller.Preferences.ExternalTool;
import com.maccasoft.propeller.Preferences.SearchPreferences;
import com.maccasoft.propeller.internal.BusyIndicator;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.internal.ImageRegistry;
import com.maccasoft.propeller.internal.InternalErrorDialog;
import com.maccasoft.propeller.internal.Utils;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spinc.CTokenMarker;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SpinTools {

    public static final String APP_TITLE = "Spin Tools IDE";
    public static final String APP_VERSION = "0.39.0";

    static final File defaultSpin1Examples = new File(System.getProperty("APP_DIR"), "examples/P1").getAbsoluteFile();
    static final File defaultSpin2Examples = new File(System.getProperty("APP_DIR"), "examples/P2").getAbsoluteFile();

    Shell shell;
    ToolBar toolBar;

    SashForm sashForm;
    SashForm browserSashForm;
    SashForm editorSashForm;
    SashForm centralSashForm;

    ObjectBrowser objectBrowser;
    FileBrowser fileBrowser;
    CTabFolder tabFolder;
    ToolBar tabFolderToolBar;
    OutlineViewStack outlineViewStack;
    ConsoleView consoleView;

    StatusLine statusLine;

    Menu runMenu;
    Process process;

    MenuItem topObjectItem;
    MenuItem blockSelectionItem;
    ToolItem topObjectToolItem;
    MenuItem topObjectTabItem;

    MenuItem consoleItem;
    ToolItem consoleToolItem;

    SourcePool sourcePool;
    SerialPortList serialPortList;

    Preferences preferences;

    Stack<SourceLocation> backStack = new Stack<SourceLocation>();
    Stack<SourceLocation> forwardStack = new Stack<SourceLocation>();

    FindReplaceDialog findReplaceDialog;

    public static final String[] filterNames = new String[] {
        "All Source Files",
        "C Files",
        "Spin1 Files",
        "Spin2 Files"
    };
    public static final String[] filterExtensions = new String[] {
        "*.spin;*.spin2;*.c",
        "*.c",
        "*.spin",
        "*.spin2"
    };

    final CaretListener caretListener = new CaretListener() {

        @Override
        public void caretMoved(CaretEvent event) {
            updateCaretPosition();
        }

    };

    final IOpenListener openListener = new IOpenListener() {

        @Override
        public void open(OpenEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();

            if (selection.getFirstElement() instanceof File) {
                File fileToOpen = (File) selection.getFirstElement();
                if (fileToOpen.isDirectory()) {
                    return;
                }
                String name = fileToOpen.getName().toLowerCase();
                if (name.endsWith(".spin") || name.endsWith(".spin2") || name.endsWith(".c")) {
                    EditorTab editorTab = findFileEditorTab(fileToOpen);
                    if (editorTab == null) {
                        openNewTab(fileToOpen);
                    }
                }
                else {
                    Program.launch(fileToOpen.getAbsolutePath());
                }
            }
            else if (selection.getFirstElement() instanceof ObjectTree) {
                File fileToOpen = ((ObjectTree) selection.getFirstElement()).getFile();
                if (fileToOpen.isDirectory()) {
                    return;
                }
                String name = fileToOpen.getName().toLowerCase();
                if (name.endsWith(".spin") || name.endsWith(".spin2")) {
                    EditorTab editorTab = findFileEditorTab(fileToOpen);
                    if (editorTab == null) {
                        openNewTab(fileToOpen);
                    }
                }
            }
            else if (selection.getFirstElement() instanceof ObjectNode) {
                if (openOrSwitchToTab(((ObjectNode) selection.getFirstElement()).getFileName()) == null) {
                    return;
                }
            }
            else if (selection.getFirstElement() instanceof DirectiveNode.IncludeNode) {
                if (openOrSwitchToTab(((DirectiveNode.IncludeNode) selection.getFirstElement()).getFileName()) == null) {
                    return;
                }
            }
            else if (selection.getFirstElement() instanceof VariableNode) {
                if (openOrSwitchToTab(((VariableNode) selection.getFirstElement()).getType().getText()) == null) {
                    return;
                }
            }
        }

    };

    final SourceListener sourceListener = new SourceListener() {

        @Override
        public void navigateTo(SourceElement element) {
            SourceLocation sourceLocation = getCurrentSourceLocation();

            String objectFileName = null;
            if (element.object instanceof ObjectNode) {
                objectFileName = ((ObjectNode) element.object).getFileName();
            }
            if (element.object instanceof DirectiveNode.IncludeNode) {
                objectFileName = ((DirectiveNode.IncludeNode) element.object).getFileName();
            }
            if (element.object instanceof VariableNode) {
                objectFileName = ((VariableNode) element.object).getType().getText();
            }

            EditorTab editorTab = objectFileName != null ? openOrSwitchToTab(objectFileName) : (EditorTab) tabFolder.getSelection().getData();
            if (editorTab == null) {
                return;
            }
            SourceEditor editor = editorTab.getEditor();
            shell.getDisplay().asyncExec(new Runnable() {

                @Override
                public void run() {
                    editor.goToLineColumn(element.line, element.column);
                }

            });

            if (sourceLocation != null) {
                backStack.push(sourceLocation);
                forwardStack.clear();
            }
        }

    };

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_SHOW_TOOLBAR:
                    toolBar.setVisible((Boolean) evt.getNewValue());
                    ((GridData) toolBar.getLayoutData()).exclude = !((Boolean) evt.getNewValue()).booleanValue();
                    toolBar.getParent().layout(true, true);
                    break;
                case Preferences.PROP_SHOW_OBJECT_BROWSER:
                    objectBrowser.setVisible((Boolean) evt.getNewValue());
                    browserSashForm.setVisible(objectBrowser.getVisible() || fileBrowser.getVisible());
                    sashForm.layout(true, true);
                    break;
                case Preferences.PROP_SHOW_BROWSER:
                    fileBrowser.setVisible((Boolean) evt.getNewValue());
                    if (fileBrowser.getVisible()) {
                        fileBrowser.refresh();
                    }
                    browserSashForm.setVisible(objectBrowser.getVisible() || fileBrowser.getVisible());
                    sashForm.layout(true, true);
                    break;
                case Preferences.PROP_SHOW_EDITOR_OUTLINE:
                    outlineViewStack.setVisible((Boolean) evt.getNewValue());
                    sashForm.layout(true, true);
                    break;
                case Preferences.PROP_ROOTS:
                    fileBrowser.setVisiblePaths((File[]) evt.getNewValue());
                    break;
                case Preferences.PROP_THEME:
                    applyTheme((String) evt.getNewValue());
                    break;
                case Preferences.PROP_EXTERNAL_TOOLS:
                    populateRunMenu();
                    break;
            }
        }
    };

    final PropertyChangeListener editorChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            EditorTab topEditorTab = getTargetObjectEditorTab();
            switch (evt.getPropertyName()) {
                case EditorTab.OBJECT_TREE:
                    if (evt.getSource() == topEditorTab) {
                        objectBrowser.setInput((ObjectTree) evt.getNewValue(), topEditorTab.isTopObject());
                    }
                    break;
            }
        }
    };

    final DisposeListener tabItemDisposeListener = new DisposeListener() {

        @Override
        public void widgetDisposed(DisposeEvent e) {
            if (shell.isDisposed() || toolBar.isDisposed() || objectBrowser.isDisposed()) {
                return;
            }
            EditorTab editorTab = (EditorTab) e.widget.getData();
            OutlineView outlineView = editorTab.getOutlineView();
            EditorTab targetEditorTab = getTargetObjectEditorTab();
            if (targetEditorTab == null || targetEditorTab == editorTab) {
                objectBrowser.setInput(null, false);
                topObjectItem.setSelection(false);
                topObjectToolItem.setSelection(false);
                topObjectTabItem.setSelection(false);
            }
            if (outlineViewStack.getTopView() == outlineView) {
                outlineViewStack.setTopView(null);
            }
            outlineView.dispose();
        }

    };

    public SpinTools(Shell shell) {
        this.shell = shell;
        this.shell.setData(this);

        preferences = Preferences.getInstance();

        boolean dark = "dark".equals(preferences.getTheme());
        if ("win32".contentEquals(Platform.PLATFORM) && Display.isSystemDarkTheme() && preferences.getTheme() == null) {
            dark = true;
        }
        if (dark) {
            try {
                @SuppressWarnings("rawtypes")
                Class clazz = Class.forName("org.eclipse.swt.internal." + Platform.PLATFORM + ".OS");
                @SuppressWarnings("unchecked")
                Method method = clazz.getMethod("setTheme", boolean.class);
                method.invoke(null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Rectangle bounds = preferences.getWindowBounds();
        if (bounds != null) {
            shell.setBounds(bounds);
        }

        Menu menu = new Menu(shell, SWT.BAR);
        createFileMenu(menu);
        createEditMenu(menu);
        createToolsMenu(menu);
        createHelpMenu(menu);
        shell.setMenuBar(menu);

        Composite container = new Composite(shell, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = layout.verticalSpacing = 0;
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        toolBar = createToolbar(container);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        toolBar.setVisible(preferences.getShowToolbar());
        ((GridData) toolBar.getLayoutData()).exclude = !preferences.getShowToolbar();

        sashForm = new SashForm(container, SWT.HORIZONTAL);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        browserSashForm = new SashForm(sashForm, SWT.VERTICAL);

        centralSashForm = new SashForm(sashForm, SWT.VERTICAL);

        editorSashForm = new SashForm(centralSashForm, SWT.HORIZONTAL);

        objectBrowser = new ObjectBrowser(browserSashForm);
        objectBrowser.setVisible(preferences.getShowObjectBrowser());

        fileBrowser = new FileBrowser(browserSashForm);
        fileBrowser.setVisible(preferences.getShowBrowser());

        tabFolder = new CTabFolder(editorSashForm, SWT.BORDER | SWT.FLAT);
        tabFolder.setMaximizeVisible(false);
        tabFolder.setMinimizeVisible(false);

        createTabFolderMenu();

        outlineViewStack = new OutlineViewStack(editorSashForm, SWT.BORDER);
        outlineViewStack.setVisible(preferences.getShowEditorOutline());

        consoleView = new ConsoleView(centralSashForm);
        consoleView.setVisible(false);

        statusLine = new StatusLine(container);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
        layoutData.heightHint = 24;
        statusLine.setLayoutData(layoutData);

        if ("win32".equals(Platform.PLATFORM) || preferences.getTheme() != null) {
            applyTheme(preferences.getTheme());
        }

        int[] weights = preferences.getWeights("sashForm");
        sashForm.setWeights(weights != null ? weights : new int[] {
            200, 800
        });
        weights = preferences.getWeights("browserSashForm");
        browserSashForm.setWeights(weights != null ? weights : new int[] {
            200, 800
        });
        weights = preferences.getWeights("centralSashForm");
        centralSashForm.setWeights(weights != null ? weights : new int[] {
            800, 200
        });
        weights = preferences.getWeights("editorSashForm");
        editorSashForm.setWeights(weights != null ? weights : new int[] {
            750, 250
        });

        objectBrowser.addOpenListener(openListener);

        fileBrowser.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection().isEmpty()) {
                    preferences.setLastPath(null);
                    return;
                }
                File file = (File) ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (!file.isDirectory()) {
                    file = file.getParentFile();
                }
                if (file != null) {
                    preferences.setLastPath(file);
                }
            }
        });
        fileBrowser.addOpenListener(openListener);

        tabFolder.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent event) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem != null) {
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.setFocus();
                }
            }
        });
        tabFolder.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.character != SWT.TAB || (e.stateMask & SWT.MODIFIER_MASK) == 0) {
                    return;
                }
                if ((e.stateMask & SWT.MOD2) == 0) {
                    handleNextTab();
                }
                else {
                    handlePreviousTab();
                }
                e.doit = false;
            }
        });
        tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {

            @Override
            public void close(CTabFolderEvent event) {
                EditorTab editorTab = (EditorTab) event.item.getData();
                event.doit = canCloseEditorTab(editorTab);
            }

        });
        tabFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateEditorSelection();
                updateCaretPosition();
            }

        });
        tabFolder.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                CTabItem tabItem = tabFolder.getItem(new Point(e.x, e.y));
                if (tabItem != null && e.button == 2) {
                    closeEditor(tabItem);
                }
            }

        });

        sourcePool = new SourcePool();

        serialPortList = new SerialPortList();

        String port = preferences.getPort();
        if (port != null) {
            serialPortList.setSelection(port);
            statusLine.setPort(port);
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
                statusLine.setPort(port);
            }

        });

        preferences.addPropertyChangeListener(preferencesChangeListener);

        shell.addListener(SWT.Close, new Listener() {

            @Override
            public void handleEvent(Event event) {
                event.doit = handleRunningProcess();
                if (event.doit) {
                    event.doit = handleUnsavedContent();
                }
            }
        });
        shell.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                try {
                    preferences.setWindowBounds(shell.getBounds());
                    preferences.setWeights("sashForm", sashForm.getWeights());
                    preferences.setWeights("browserSashForm", browserSashForm.getWeights());
                    preferences.setWeights("editorSashForm", editorSashForm.getWeights());

                    List<String> openTabs = new ArrayList<String>();
                    for (int i = 0; i < tabFolder.getItemCount(); i++) {
                        EditorTab tab = (EditorTab) tabFolder.getItem(i).getData();
                        if (tab.getFile() != null) {
                            openTabs.add(tab.getFile().getAbsolutePath());
                        }
                    }
                    preferences.setOpenTabs(openTabs.toArray(new String[openTabs.size()]));

                    preferences.setExpandedPaths(fileBrowser.getExpandedPaths());

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

        BusyIndicator.showWhile(tabFolder.getDisplay(), new Runnable() {

            @Override
            public void run() {
                File topObjectFile = preferences.getTopObject();
                if (topObjectFile != null) {
                    try {
                        String text = FileUtils.loadFromFile(topObjectFile);
                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                EditorTab editorTab = new EditorTab(tabFolder, topObjectFile, sourcePool);

                                OutlineView outlineView = outlineViewStack.createNew();
                                outlineView.addOpenListener(openListener);
                                editorTab.setOutlineView(outlineView);

                                editorTab.setEditorText(text);
                                editorTab.addCaretListener(caretListener);
                                editorTab.addPropertyChangeListener(editorChangeListener);
                                editorTab.getEditor().addSourceListener(sourceListener);
                                editorTab.getEditor().getStyledText().setMenu(createContextMenu());
                                editorTab.getTabItem().addDisposeListener(tabItemDisposeListener);
                            }

                        });
                    } catch (Exception e) {
                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                MessageDialog.openError(shell, APP_TITLE, "Can't reopen top object " + topObjectFile.getAbsolutePath());
                                preferences.setTopObject(null);
                            }
                        });
                    }
                }

                final String[] openTabs = preferences.getOpenTabs();
                if (openTabs != null && preferences.getReloadOpenTabs()) {
                    for (int i = 0; i < openTabs.length; i++) {
                        File fileToOpen = new File(openTabs[i]);
                        if (!fileToOpen.exists() || fileToOpen.isDirectory()) {
                            continue;
                        }
                        if (topObjectFile != null && topObjectFile.equals(fileToOpen)) {
                            continue;
                        }
                        try {
                            String text = FileUtils.loadFromFile(fileToOpen);
                            Display.getDefault().syncExec(new Runnable() {

                                @Override
                                public void run() {
                                    EditorTab editorTab = new EditorTab(tabFolder, fileToOpen, sourcePool);

                                    OutlineView outlineView = outlineViewStack.createNew();
                                    outlineView.addOpenListener(openListener);
                                    editorTab.setOutlineView(outlineView);

                                    editorTab.setEditorText(text);
                                    editorTab.addCaretListener(caretListener);
                                    editorTab.addPropertyChangeListener(editorChangeListener);
                                    editorTab.getEditor().addSourceListener(sourceListener);
                                    editorTab.getEditor().getStyledText().setMenu(createContextMenu());
                                    editorTab.getTabItem().addDisposeListener(tabItemDisposeListener);
                                }

                            });
                        } catch (Exception e) {
                            Display.getDefault().syncExec(new Runnable() {

                                @Override
                                public void run() {
                                    MessageDialog.openError(shell, APP_TITLE, "Can't reopen " + fileToOpen.getAbsolutePath());
                                    preferences.setTopObject(null);
                                }
                            });
                        }
                    }
                }

                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (tabFolder.isDisposed()) {
                            return;
                        }
                        fileBrowser.setVisiblePaths(preferences.getRoots());
                        fileBrowser.setExpandedPaths(preferences.getExpandedPaths());
                        if (tabFolder.getItemCount() != 0) {
                            tabFolder.setSelection(0);
                            updateEditorSelection();
                            updateCaretPosition();
                        }
                        File lastPath = preferences.getLastPath();
                        if (lastPath != null) {
                            fileBrowser.setSelection(lastPath);
                        }
                    }

                });
            }
        }, true);
    }

    void applyTheme(String id) {
        Color widgetForeground = null;
        Color widgetBackground = null;
        Color listForeground = null;
        Color listBackground = null;
        Color tabfolderBackground = null;

        if ("win32".equals(Platform.PLATFORM) && id == null) {
            if (Display.isSystemDarkTheme()) {
                id = "dark";
            }
        }

        if (id == null) {
            listBackground = ColorRegistry.getColor(ColorRegistry.LIST_BACKGROUND);
            listForeground = ColorRegistry.getColor(ColorRegistry.LIST_FOREGROUND);
            widgetBackground = ColorRegistry.getColor(ColorRegistry.WIDGET_BACKGROUND);
            widgetForeground = ColorRegistry.getColor(ColorRegistry.WIDGET_FOREGROUND);
            tabfolderBackground = widgetBackground;
        }
        else if ("dark".equals(id)) {
            widgetForeground = new Color(0xF0, 0xF0, 0xF0);
            widgetBackground = new Color(0x50, 0x55, 0x57);
            listForeground = new Color(0xA7, 0xA7, 0xA7);
            listBackground = new Color(0x2B, 0x2B, 0x2B);
            tabfolderBackground = new Color(0x43, 0x44, 0x47);
        }
        else if ("light".equals(id)) {
            widgetForeground = new Color(0x00, 0x00, 0x00);
            if ("win32".equals(Platform.PLATFORM)) {
                widgetBackground = new Color(0xF0, 0xF0, 0xF0);
            }
            else {
                widgetBackground = new Color(0xFA, 0xFA, 0xFA);
            }
            listForeground = new Color(0x00, 0x00, 0x00);
            listBackground = new Color(0xFE, 0xFE, 0xFE);
            tabfolderBackground = widgetBackground;
        }

        shell.setBackground(widgetBackground);

        toolBar.setBackground(widgetBackground);
        ToolItem[] items = toolBar.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i].setBackground(widgetBackground);
        }

        sashForm.setBackground(widgetBackground);
        browserSashForm.setBackground(widgetBackground);
        editorSashForm.setBackground(widgetBackground);
        centralSashForm.setBackground(widgetBackground);

        objectBrowser.setBackground(listBackground);
        objectBrowser.setForeground(listForeground);

        fileBrowser.setBackground(listBackground);
        fileBrowser.setForeground(listForeground);

        tabFolder.setForeground(widgetForeground);
        tabFolder.setBackground(tabfolderBackground);
        tabFolder.setSelectionForeground(widgetForeground);
        tabFolder.setSelectionBackground(listBackground);

        tabFolderToolBar.setBackground(tabfolderBackground);
        items = tabFolderToolBar.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i].setBackground(tabfolderBackground);
        }

        outlineViewStack.setBackground(listBackground);
        outlineViewStack.setForeground(listForeground);

        consoleView.setBackground(listBackground);
        consoleView.setForeground(widgetForeground);

        statusLine.setForeground(widgetForeground);
        statusLine.setBackground(widgetBackground);
    }

    void createFileMenu(Menu parent) {
        Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("&File");
        item.setMenu(menu);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New\tCtrl+N");
        item.setAccelerator(SWT.MOD1 + 'N');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileNew();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New (From P1/Spin template)\tCtrl+Alt+1");
        item.setAccelerator(SWT.MOD1 + SWT.MOD3 + '1');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                String name = getUniqueName("Untitled", ".spin");
                File templateFile = preferences.getSpin1Template();
                if (templateFile != null) {
                    try {
                        String text = FileUtils.loadFromFile(templateFile);
                        openNewTab(name, text);
                    } catch (Exception e1) {
                        openInternalError(shell, "Error opening template file " + templateFile, e1);
                    }
                }
                else {
                    openNewTab(name, getResourceAsString("template.spin"));
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New (From P1/C template)\tCtrl+Alt+2");
        item.setAccelerator(SWT.MOD1 + SWT.MOD3 + '2');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                String name = getUniqueName("Untitled", ".c");
                openNewTab(name, getResourceAsString("template1.c"));
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New (From P2/Spin template)\tCtrl+Alt+3");
        item.setAccelerator(SWT.MOD1 + SWT.MOD3 + '3');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                String name = getUniqueName("Untitled", ".spin2");
                File templateFile = preferences.getSpin2Template();
                if (templateFile != null) {
                    try {
                        String text = FileUtils.loadFromFile(templateFile);
                        openNewTab(name, text);
                    } catch (Exception e1) {
                        openInternalError(shell, "Error opening template file " + templateFile, e1);
                    }
                }
                else {
                    openNewTab(name, getResourceAsString("template.spin2"));
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("New (From P2/C template)\tCtrl+Alt+4");
        item.setAccelerator(SWT.MOD1 + SWT.MOD3 + '4');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                String name = getUniqueName("Untitled", ".c");
                openNewTab(name, getResourceAsString("template2.c"));
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Open...");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileOpen();
            }
        });

        final Menu openFromMenu = new Menu(parent.getParent(), SWT.DROP_DOWN);
        openFromMenu.addMenuListener(new MenuListener() {

            @Override
            public void menuShown(MenuEvent e) {
                MenuItem[] item = openFromMenu.getItems();
                for (int i = 0; i < item.length; i++) {
                    item[i].dispose();
                }
                populateOpenFromMenu(openFromMenu);
            }

            @Override
            public void menuHidden(MenuEvent e) {
            }
        });
        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Open From...");
        item.setMenu(openFromMenu);

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close Editor\tCtrl+W");
        item.setAccelerator(SWT.MOD1 + 'W');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                closeCurrentEditor();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close All Editors\tShift+Ctrl+W");
        item.setAccelerator(SWT.MOD2 + SWT.MOD1 + 'W');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                e.display.asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (closeCurrentEditor()) {
                            e.display.asyncExec(this);
                        }
                        else {
                            tabFolder.forceFocus();
                        }
                    }

                });
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Save\tCtrl+S");
        item.setAccelerator(SWT.MOD1 + 'S');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileSave();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Save As...");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileSaveAs();
            }
        });

        final Menu saveToMenu = new Menu(parent.getParent(), SWT.DROP_DOWN);
        saveToMenu.addMenuListener(new MenuListener() {

            @Override
            public void menuShown(MenuEvent e) {
                MenuItem[] item = saveToMenu.getItems();
                for (int i = 0; i < item.length; i++) {
                    item[i].dispose();
                }
                populateSaveToMenu(saveToMenu);
            }

            @Override
            public void menuHidden(MenuEvent e) {

            }

        });
        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Save To...");
        item.setMenu(saveToMenu);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Save All\tShift+Ctrl+S");
        item.setAccelerator(SWT.MOD2 + SWT.MOD1 + 'S');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileSaveAll();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        topObjectItem = new MenuItem(menu, SWT.CHECK);
        topObjectItem.setText("Pin as Top Object\tCtrl+T");
        topObjectItem.setAccelerator(SWT.MOD1 + 'T');
        topObjectItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleSetTopObject();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Archive Project...");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleArchiveProject();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Refresh\tF5");
        item.setAccelerator(SWT.F5);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                if (fileBrowser.getVisible()) {
                    fileBrowser.refresh();
                }
            }
        });

        final int lruIndex = menu.getItemCount();

        item = getSystemMenuItem(SWT.ID_QUIT);
        if (item == null) {
            new MenuItem(menu, SWT.SEPARATOR);

            item = new MenuItem(menu, SWT.PUSH);
            item.setText("Exit");
        }
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                shell.dispose();
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

    void populateOpenFromMenu(Menu menu) {
        List<String> list = new ArrayList<String>();
        List<String> defaultList = Arrays.asList(new String[] {
            new File(System.getProperty("APP_DIR"), "examples/P1").getAbsolutePath(),
            new File(System.getProperty("APP_DIR"), "examples/P2").getAbsolutePath(),
            Preferences.defaultSpin1LibraryPath.getAbsolutePath(),
            Preferences.defaultSpin2LibraryPath.getAbsolutePath()
        });

        for (String folder : defaultList) {
            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText(folder);
            item.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    handleFileOpenFrom(folder);
                }
            });
        }

        boolean addSeparator = true;

        List<File> libraryPath = new ArrayList<>();
        libraryPath.addAll(Arrays.asList(preferences.getSpin1LibraryPath()));
        libraryPath.addAll(Arrays.asList(preferences.getSpin2LibraryPath()));
        Collections.sort(libraryPath, (o1, o2) -> o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath()));
        for (File file : libraryPath) {
            String folder = file.getAbsolutePath();
            if (!list.contains(folder) && !defaultList.contains(folder)) {
                if (addSeparator) {
                    new MenuItem(menu, SWT.SEPARATOR);
                    addSeparator = false;
                }
                MenuItem item = new MenuItem(menu, SWT.PUSH);
                item.setText(folder);
                item.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        handleFileOpenFrom(folder);
                    }
                });
                list.add(folder);
            }
        }

        if (!Arrays.equals(preferences.getRoots(), Preferences.defaultVisiblePaths)) {
            addSeparator = true;

            List<File> visibleFolders = Arrays.asList(preferences.getRoots());
            Collections.sort(visibleFolders, (o1, o2) -> o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath()));
            for (File file : visibleFolders) {
                String folder = file.getAbsolutePath();
                if (!list.contains(folder) && !defaultList.contains(folder)) {
                    if (addSeparator) {
                        new MenuItem(menu, SWT.SEPARATOR);
                        addSeparator = false;
                    }
                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                    item.setText(folder);
                    item.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            handleFileOpenFrom(folder);
                        }

                    });
                    list.add(folder);
                }
            }
        }

        addSeparator = true;

        List<String> lru = new ArrayList<String>(preferences.getLru());
        Collections.sort(lru, (o1, o2) -> o1.compareToIgnoreCase(o2));
        for (String file : lru) {
            String folder = new File(file).getParent();
            if (!list.contains(folder) && !defaultList.contains(folder)) {
                if (addSeparator) {
                    new MenuItem(menu, SWT.SEPARATOR);
                    addSeparator = false;
                }
                MenuItem item = new MenuItem(menu, SWT.PUSH);
                item.setText(folder);
                item.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        handleFileOpenFrom(folder);
                    }
                });
                list.add(folder);
            }
        }
    }

    void populateSaveToMenu(Menu menu) {
        List<String> list = new ArrayList<String>();
        List<String> defaultList = Arrays.asList(new String[] {
            new File(System.getProperty("APP_DIR"), "examples/P1").getAbsolutePath(),
            new File(System.getProperty("APP_DIR"), "examples/P2").getAbsolutePath(),
            Preferences.defaultSpin1LibraryPath.getAbsolutePath(),
            Preferences.defaultSpin2LibraryPath.getAbsolutePath()
        });

        boolean addSeparator = false;

        List<File> libraryPath = new ArrayList<>();
        libraryPath.addAll(Arrays.asList(preferences.getSpin1LibraryPath()));
        libraryPath.addAll(Arrays.asList(preferences.getSpin2LibraryPath()));
        Collections.sort(libraryPath, (o1, o2) -> o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath()));
        for (File file : libraryPath) {
            String folder = file.getAbsolutePath();
            if (!list.contains(folder) && !defaultList.contains(folder)) {
                if (addSeparator) {
                    new MenuItem(menu, SWT.SEPARATOR);
                    addSeparator = false;
                }
                MenuItem item = new MenuItem(menu, SWT.PUSH);
                item.setText(folder);
                item.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        handleFileSaveTo(folder);
                    }

                });
                list.add(folder);
            }
        }

        if (!Arrays.equals(preferences.getRoots(), Preferences.defaultVisiblePaths)) {
            addSeparator = true;

            List<File> visibleFolders = Arrays.asList(preferences.getRoots());
            Collections.sort(visibleFolders, (o1, o2) -> o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath()));
            for (File file : visibleFolders) {
                String folder = file.getAbsolutePath();
                if (!list.contains(folder) && !defaultList.contains(folder)) {
                    if (addSeparator) {
                        new MenuItem(menu, SWT.SEPARATOR);
                        addSeparator = false;
                    }
                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                    item.setText(folder);
                    item.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            handleFileSaveTo(folder);
                        }

                    });
                    list.add(folder);
                }
            }
        }

        addSeparator = true;

        List<String> lru = new ArrayList<String>(preferences.getLru());
        Collections.sort(lru, (o1, o2) -> o1.compareToIgnoreCase(o2));
        for (String file : lru) {
            String folder = new File(file).getParent();
            if (!list.contains(folder) && !defaultList.contains(folder)) {
                if (addSeparator) {
                    new MenuItem(menu, SWT.SEPARATOR);
                    addSeparator = false;
                }
                MenuItem item = new MenuItem(menu, SWT.PUSH);
                item.setText(folder);
                item.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        handleFileSaveTo(folder);
                    }

                });
                list.add(folder);
            }
        }
    }

    private void handleFileSaveTo(String filterPath) {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            doFileSaveAs(editorTab, new File(filterPath, editorTab.getText()));
        }
    }

    void populateLruFiles(Menu menu, int itemIndex, List<MenuItem> list) {
        int index = 0;

        Iterator<String> iter = preferences.getLru().iterator();
        while (iter.hasNext()) {
            final File fileToOpen = new File(iter.next());

            if (index == 0) {
                list.add(new MenuItem(menu, SWT.SEPARATOR, itemIndex++));
            }

            MenuItem item = new MenuItem(menu, SWT.PUSH, itemIndex++);
            item.setText(String.format("%d %s", index + 1, fileToOpen.getName()));
            item.setToolTipText(fileToOpen.getAbsolutePath());
            item.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    if (!fileToOpen.exists()) {
                        MessageDialog.openError(shell, APP_TITLE, "File " + fileToOpen + " not found");
                        preferences.getLru().remove(fileToOpen.toString());

                        File parentFile = fileToOpen.getParentFile();
                        while (parentFile != null) {
                            if (parentFile.exists()) {
                                break;
                            }
                            parentFile = parentFile.getParentFile();
                        }
                        handleFileOpenFrom(parentFile != null ? parentFile.getAbsolutePath() : "");
                        return;
                    }
                    EditorTab editorTab = findFileEditorTab(fileToOpen);
                    if (editorTab == null) {
                        editorTab = openNewTab(fileToOpen);
                    }
                    tabFolder.setSelection(editorTab.getTabItem());
                }
            });
            list.add(item);

            index++;
        }
    }

    ToolBar createToolbar(Composite parent) {
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT);

        ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("blue-document-number-1.png"));
        toolItem.setToolTipText("New from P1/Spin template");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String name = getUniqueName("Untitled", ".spin");
                File templateFile = preferences.getSpin1Template();
                if (templateFile != null) {
                    try {
                        String text = FileUtils.loadFromFile(templateFile);
                        openNewTab(name, text);
                    } catch (Exception e1) {
                        openInternalError(shell, "Error opening template file " + templateFile, e1);
                    }
                }
                else {
                    openNewTab(name, getResourceAsString("template.spin"));
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("blue-document-number-2.png"));
        toolItem.setToolTipText("New from P2/Spin template");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String name = getUniqueName("Untitled", ".spin2");
                File templateFile = preferences.getSpin2Template();
                if (templateFile != null) {
                    try {
                        String text = FileUtils.loadFromFile(templateFile);
                        openNewTab(name, text);
                    } catch (Exception e1) {
                        openInternalError(shell, "Error opening template file " + templateFile, e1);
                    }
                }
                else {
                    openNewTab(name, getResourceAsString("template.spin2"));
                }
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("folder-horizontal-open.png"));
        toolItem.setToolTipText("Open");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    handleFileOpen();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("disk-black.png"));
        toolItem.setToolTipText("Save");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    handleFileSave();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("disks-black.png"));
        toolItem.setToolTipText("Save All");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    handleFileSaveAll();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("scissors.png"));
        toolItem.setToolTipText("Cut");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).cut();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("document-copy.png"));
        toolItem.setToolTipText("Copy");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).copy();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("clipboard-paste.png"));
        toolItem.setToolTipText("Paste");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).paste();
                }
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("arrow-curve-180.png"));
        toolItem.setToolTipText("Undo");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem != null) {
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.undo();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("arrow-curve-000-left.png"));
        toolItem.setToolTipText("Redo");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem != null) {
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.redo();
                }
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("flashlight.png"));
        toolItem.setToolTipText("Find / Replace...");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem == null) {
                    return;
                }

                if (findReplaceDialog != null && !findReplaceDialog.isDisposed()) {
                    findReplaceDialog.getShell().setFocus();
                    return;
                }

                findReplaceDialog = new FindReplaceDialog(shell);
                findReplaceDialog.setTheme(preferences.getTheme());
                findReplaceDialog.setTarget((EditorTab) tabItem.getData());
                findReplaceDialog.open();
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("document-text-arrow-270-small.png"));
        toolItem.setToolTipText("Next Annotation");
        toolItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    if (tabItem != null) {
                        EditorTab editorTab = (EditorTab) tabItem.getData();
                        editorTab.goToNextAnnotation();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("document-text-arrow-090-small.png"));
        toolItem.setToolTipText("Previous Annotation");
        toolItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    CTabItem tabItem = tabFolder.getSelection();
                    if (tabItem != null) {
                        EditorTab editorTab = (EditorTab) tabItem.getData();
                        editorTab.goToPreviousAnnotation();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("arrow-180.png"));
        toolItem.setToolTipText("Previous edit location");
        toolItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    if (backStack.isEmpty()) {
                        return;
                    }

                    SourceLocation currentLocation = getCurrentSourceLocation();

                    SourceLocation location = backStack.pop();
                    EditorTab editorTab = findFileEditorTab(location.file);
                    if (editorTab == null && location.file != null) {
                        editorTab = openNewTab(location.file);
                    }
                    if (editorTab != null) {
                        if (currentLocation != null) {
                            forwardStack.push(currentLocation);
                        }
                        SourceEditor editor = editorTab.getEditor();
                        shell.getDisplay().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                StyledText styledText = editor.getStyledText();
                                styledText.setCaretOffset(location.offset);
                                styledText.setTopPixel(location.topPixel);
                            }

                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("arrow.png"));
        toolItem.setToolTipText("Next edit location");
        toolItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    if (forwardStack.isEmpty()) {
                        return;
                    }

                    SourceLocation currentLocation = getCurrentSourceLocation();

                    SourceLocation location = forwardStack.pop();
                    EditorTab editorTab = findFileEditorTab(location.file);
                    if (editorTab == null && location.file != null) {
                        editorTab = openNewTab(location.file);
                    }
                    if (editorTab != null) {
                        if (currentLocation != null) {
                            backStack.push(currentLocation);
                        }
                        SourceEditor editor = editorTab.getEditor();
                        shell.getDisplay().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                StyledText styledText = editor.getStyledText();
                                styledText.setCaretOffset(location.offset);
                                styledText.setTopPixel(location.topPixel);
                            }

                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        topObjectToolItem = new ToolItem(toolBar, SWT.CHECK);
        topObjectToolItem.setImage(ImageRegistry.getImageFromResources("document-code-pin.png"));
        topObjectToolItem.setToolTipText("Pin as Top Object");
        topObjectToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleSetTopObject();
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("information-frame.png"));
        toolItem.setToolTipText("Show Info");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    handleShowInfo();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("bug.png"));
        toolItem.setToolTipText("Upload to RAM with Debug");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean openTerminal = (e.stateMask & SWT.MOD2) != 0;
                handleUpload(false, openTerminal, true);
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("control.png"));
        toolItem.setToolTipText("Upload to RAM");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean openTerminal = (e.stateMask & SWT.MOD2) != 0;
                handleUpload(false, openTerminal, false);
            }
        });

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("processor.png"));
        toolItem.setToolTipText("Upload to Flash");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean openTerminal = (e.stateMask & SWT.MOD2) != 0;
                handleUpload(true, openTerminal, false);
            }
        });

        new ToolItem(toolBar, SWT.SEPARATOR);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("terminal.png"));
        toolItem.setToolTipText("Terminal");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    SerialTerminal serialTerminal = getSerialTerminal();
                    if (serialTerminal == null) {
                        serialTerminal = new SerialTerminal();
                        serialTerminal.open();
                        serialTerminal.setSerialPort(new SerialPort(serialPortList.getSelection()));
                    }
                    serialTerminal.setFocus();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        consoleToolItem = new ToolItem(toolBar, SWT.CHECK);
        consoleToolItem.setImage(ImageRegistry.getImageFromResources("application-text.png"));
        consoleToolItem.setToolTipText("Toggle Console");
        consoleToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                consoleView.setVisible(consoleToolItem.getSelection());
                consoleItem.setSelection(consoleToolItem.getSelection());
                centralSashForm.layout();
            }
        });

        return toolBar;
    }

    private void handleFileNew() {
        String suffix = ".spin";

        if (tabFolder.getSelection() != null) {
            EditorTab currentTab = (EditorTab) tabFolder.getSelection().getData();
            String tabName = currentTab.getText();
            suffix = tabName.substring(tabName.lastIndexOf('.'));
        }

        String name = getUniqueName("Untitled", suffix);
        openNewTab(name, "");
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
        dlg.setText("Open Source File");
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);
        dlg.setFilterIndex(0);

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
            EditorTab editorTab = findFileEditorTab(fileToOpen);
            if (editorTab == null) {
                editorTab = openNewTab(new File(fileName));
            }
            tabFolder.setSelection(editorTab.getTabItem());
        }
    }

    private void handleFileOpenFrom(String filterPath) {
        FileDialog dlg = new FileDialog(shell, SWT.OPEN);
        dlg.setText("Open Source File");
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);
        dlg.setFilterIndex(0);

        if (filterPath != null) {
            dlg.setFilterPath(filterPath);
        }

        String fileName = dlg.open();
        if (fileName != null) {
            File fileToOpen = new File(fileName);
            EditorTab editorTab = findFileEditorTab(fileToOpen);
            if (editorTab == null) {
                editorTab = openNewTab(new File(fileName));
            }
            tabFolder.setSelection(editorTab.getTabItem());
        }
    }

    EditorTab openOrSwitchToTab(String name) {
        EditorTab editorTab = null;

        EditorTab currentTab = (EditorTab) tabFolder.getSelection().getData();
        String tabName = currentTab.getText();
        String suffix = tabName.substring(tabName.lastIndexOf('.'));

        if (".spin".equals(suffix)) {
            editorTab = openOrSwitchToTab(name + suffix, preferences.getSpin1LibraryPath());
            if (editorTab == null) {
                editorTab = openOrSwitchToTab(name, preferences.getSpin1LibraryPath());
            }
        }
        else if (".spin2".equals(suffix)) {
            editorTab = openOrSwitchToTab(name + suffix, preferences.getSpin2LibraryPath());
            if (editorTab == null) {
                editorTab = openOrSwitchToTab(name, preferences.getSpin2LibraryPath());
            }
        }
        else if (".c".equals(suffix)) {
            CTokenMarker tokenMarker = (CTokenMarker) currentTab.getTokenMarker();
            if (tokenMarker.isP1()) {
                editorTab = openOrSwitchToTab(name + suffix, preferences.getSpin1LibraryPath());
                if (editorTab == null) {
                    editorTab = openOrSwitchToTab(name + ".spin", preferences.getSpin1LibraryPath());
                }
                if (editorTab == null) {
                    editorTab = openOrSwitchToTab(name, preferences.getSpin1LibraryPath());
                }
            }
            else {
                editorTab = openOrSwitchToTab(name + suffix, preferences.getSpin2LibraryPath());
                if (editorTab == null) {
                    editorTab = openOrSwitchToTab(name + ".spin2", preferences.getSpin2LibraryPath());
                }
                if (editorTab == null) {
                    editorTab = openOrSwitchToTab(name, preferences.getSpin2LibraryPath());
                }
            }
        }

        return editorTab;
    }

    EditorTab openOrSwitchToTab(String name, File[] searchPaths) {
        File parent = new File("");

        if (tabFolder.getSelection() != null) {
            EditorTab currentTab = (EditorTab) tabFolder.getSelection().getData();
            if (currentTab.getFile() != null) {
                parent = currentTab.getFile().getParentFile();
            }
        }

        File fileToOpen = new File(parent, name);
        if (!fileToOpen.exists() || fileToOpen.isDirectory()) {
            for (int i = 0; i < searchPaths.length; i++) {
                fileToOpen = new File(searchPaths[i], name);
                if (fileToOpen.exists() && !fileToOpen.isDirectory()) {
                    break;
                }
            }
        }
        if (!fileToOpen.exists() || fileToOpen.isDirectory()) {
            fileToOpen = new File(parent, name);
        }

        if (fileToOpen.exists() && !fileToOpen.isDirectory()) {
            EditorTab editorTab = findFileEditorTab(fileToOpen);
            if (editorTab == null) {
                editorTab = openNewTab(fileToOpen);
            }
            return editorTab;
        }

        return null;
    }

    EditorTab openNewTab(File fileToOpen) {
        EditorTab editorTab = new EditorTab(tabFolder, fileToOpen, sourcePool);

        OutlineView outlineView = outlineViewStack.createNew();
        outlineView.addOpenListener(openListener);
        editorTab.setOutlineView(outlineView);

        preferences.addToLru(fileToOpen);

        editorTab.addCaretListener(caretListener);
        editorTab.addPropertyChangeListener(editorChangeListener);
        editorTab.getEditor().addSourceListener(sourceListener);
        editorTab.getEditor().getStyledText().setMenu(createContextMenu());
        editorTab.getTabItem().addDisposeListener(tabItemDisposeListener);

        tabFolder.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    tabFolder.setSelection(tabFolder.getItemCount() - 1);
                    editorTab.setEditorText(FileUtils.loadFromFile(fileToOpen));
                    updateEditorSelection();
                    updateCaretPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        return editorTab;
    }

    Menu createContextMenu() {
        Menu menu = new Menu(shell, SWT.POP_UP);

        MenuItem item = new MenuItem(menu, SWT.CASCADE);
        item.setText("New");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileNew();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Save");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileSave();
            }
        });

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Save All");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFileSaveAll();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                closeCurrentEditor();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close All");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                e.display.asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (tabFolder.getItemCount() > 0) {
                            if (closeEditor(tabFolder.getItem(0))) {
                                e.display.asyncExec(this);
                            }
                        }
                    }

                });
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close All Others");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem currentTabItem = tabFolder.getSelection();
                e.display.asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        CTabItem tabItem = null;

                        if (tabFolder.getItem(0) != currentTabItem) {
                            tabItem = tabFolder.getItem(0);
                        }
                        else if (tabFolder.getItemCount() > 1) {
                            tabItem = tabFolder.getItem(1);
                        }

                        if (tabItem != null) {
                            if (closeEditor(tabItem)) {
                                e.display.asyncExec(this);
                            }
                        }
                    }

                });
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Cut");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).cut();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).copy();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Paste");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).paste();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Select All");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).selectAll();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Find / Replace...");
        item.setAccelerator(SWT.MOD1 + 'F');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem == null) {
                    return;
                }

                if (findReplaceDialog != null && !findReplaceDialog.isDisposed()) {
                    findReplaceDialog.getShell().setFocus();
                    return;
                }

                findReplaceDialog = new FindReplaceDialog(shell);
                findReplaceDialog.setTheme(preferences.getTheme());
                findReplaceDialog.setTarget((EditorTab) tabItem.getData());
                findReplaceDialog.open();
            }

        });

        return menu;
    }

    EditorTab openNewTab(String name, String text) {
        EditorTab editorTab = new EditorTab(tabFolder, name, sourcePool);

        OutlineView outlineView = outlineViewStack.createNew();
        outlineView.addOpenListener(openListener);
        editorTab.setOutlineView(outlineView);

        editorTab.addCaretListener(caretListener);
        editorTab.addPropertyChangeListener(editorChangeListener);
        editorTab.getEditor().addSourceListener(sourceListener);
        editorTab.getEditor().getStyledText().setMenu(createContextMenu());
        editorTab.getTabItem().addDisposeListener(tabItemDisposeListener);

        blockSelectionItem.setSelection(editorTab.isBlockSelection());
        objectBrowser.setInput(null, false);

        tabFolder.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    tabFolder.setSelection(tabFolder.getItemCount() - 1);
                    outlineViewStack.setTopView(editorTab.getOutlineView());
                    editorTab.setEditorText(text);
                    editorTab.setFocus();
                    updateCaretPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        return editorTab;
    }

    private void handleFileSave() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            doFileSave(editorTab);
        }
    }

    private void doFileSave(EditorTab editorTab) {
        File fileToSave = editorTab.getFile();
        if (fileToSave == null) {
            doFileSaveAs(editorTab, editorTab.getFile());
            return;
        }
        else {
            File parentFile = fileToSave.getParentFile();
            if (parentFile != null) {
                if (parentFile.equals(Preferences.defaultSpin1LibraryPath) || parentFile.equals(Preferences.defaultSpin2LibraryPath)) {
                    doFileSaveAs(editorTab, editorTab.getFile());
                    return;
                }
                if (parentFile.equals(defaultSpin1Examples) || parentFile.equals(defaultSpin2Examples)) {
                    doFileSaveAs(editorTab, editorTab.getFile());
                    return;
                }
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
            writer.write(editorTab.getEditorText());
            writer.close();
            editorTab.clearDirty();
        } catch (Exception e) {
            openInternalError(shell, "Unexpected error saving file", e);
        }
    }

    private void handleFileSaveAs() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            doFileSaveAs(editorTab, editorTab.getFile());
        }
    }

    private void doFileSaveAs(EditorTab editorTab, File filterPath) {
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setText("Save Source File");
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);
        dlg.setFilterIndex(0);

        dlg.setFileName(editorTab.getText());
        dlg.setOverwrite(true);

        if (filterPath == null && preferences.getLru().size() != 0) {
            filterPath = new File(preferences.getLru().get(0));
        }
        if (filterPath != null) {
            File parentFile = filterPath.getParentFile();
            if (parentFile != null) {
                if (parentFile.equals(Preferences.defaultSpin1LibraryPath) || parentFile.equals(Preferences.defaultSpin2LibraryPath)) {
                    parentFile = null;
                }
                else if (parentFile.equals(defaultSpin1Examples) || parentFile.equals(defaultSpin2Examples)) {
                    parentFile = null;
                }
            }
            if (parentFile == null && preferences.getLru().size() != 0) {
                parentFile = new File(preferences.getLru().get(0)).getParentFile();
                if (parentFile != null) {
                    if (parentFile.equals(Preferences.defaultSpin1LibraryPath) || parentFile.equals(Preferences.defaultSpin2LibraryPath)) {
                        parentFile = null;
                    }
                    else if (parentFile.equals(defaultSpin1Examples) || parentFile.equals(defaultSpin2Examples)) {
                        parentFile = null;
                    }
                }
            }
            if (parentFile != null) {
                dlg.setFilterPath(parentFile.getAbsolutePath());
            }
        }

        String fileName = dlg.open();
        if (fileName != null) {
            File fileToSave = new File(fileName);
            try {
                editorTab.setFile(fileToSave);
                editorTab.setText(fileToSave.getName());

                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
                writer.write(editorTab.getEditorText());
                writer.close();

                editorTab.clearDirty();
                preferences.addToLru(fileToSave);

                fileBrowser.refresh(fileToSave.getParentFile());
            } catch (Exception e) {
                openInternalError(shell, "Unexpected error saving file", e);
            }
        }
    }

    private void handleFileSaveAll() {
        for (int i = 0; i < tabFolder.getItemCount(); i++) {
            CTabItem tabItem = tabFolder.getItem(i);
            EditorTab editorTab = (EditorTab) tabItem.getData();
            if (editorTab.isDirty()) {
                doFileSave(editorTab);
            }
        }
    }

    private void handleArchiveProject() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            doArchiveProject(editorTab);
        }
    }

    private void doArchiveProject(EditorTab editorTab) {
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setText("Archive Project");
        String[] filterNames = new String[] {
            "Archive Files"
        };
        String[] filterExtensions = new String[] {
            "*.zip"
        };
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm", Locale.US);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, LLLL d, yyyy 'at' HH:mm", Locale.US);

        String rootFileName = editorTab.getText();
        String archiveName = String.format("%s - Archive [Date %s Time %s].zip",
            rootFileName.substring(0, rootFileName.lastIndexOf('.')),
            dateFormat.format(now),
            timeFormat.format(now));

        dlg.setFileName(archiveName);
        dlg.setOverwrite(true);

        File filterPath = editorTab.getFile();
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
                ZipOutputStream archiveStream = new ZipOutputStream(new FileOutputStream(fileToSave));
                archiveStream.putNextEntry(new ZipEntry(editorTab.getText()));
                archiveStream.write(editorTab.getEditorText().getBytes());

                File topFile = editorTab.getFile() != null ? new File(editorTab.getFile().getParentFile(), editorTab.getText()) : new File(editorTab.getText());
                Path topFileParent = topFile.getParentFile().toPath();

                for (File file : editorTab.getDependencies()) {
                    Path filePath = file.toPath();
                    if (filePath.startsWith(topFileParent)) {
                        archiveStream.putNextEntry(new ZipEntry(topFileParent.relativize(filePath).toString()));
                    }
                    else {
                        archiveStream.putNextEntry(new ZipEntry(file.getName()));
                    }

                    boolean found = false;
                    for (int i = 0; i < tabFolder.getItemCount(); i++) {
                        EditorTab tab = (EditorTab) tabFolder.getItem(i).getData();
                        File localFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText());
                        if (localFile.equals(file)) {
                            archiveStream.write(tab.getEditorText().getBytes());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        archiveStream.write(FileUtils.loadBinaryFromFile(file));
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append("----------------------------------");
                sb.append(System.lineSeparator());
                sb.append("Parallax Propeller Project Archive");
                sb.append(System.lineSeparator());
                sb.append("----------------------------------");
                sb.append(System.lineSeparator());

                sb.append(System.lineSeparator());

                sb.append(" Project : ");
                sb.append(editorTab.getText());
                sb.append(System.lineSeparator());
                sb.append("Archived : ");
                sb.append(dateTimeFormat.format(now));
                sb.append(System.lineSeparator());
                sb.append("    Tool : ");
                sb.append(APP_TITLE);
                sb.append(" version ");
                sb.append(APP_VERSION);
                sb.append(System.lineSeparator());

                sb.append(System.lineSeparator());
                sb.append(editorTab.getObjectTree());

                archiveStream.putNextEntry(new ZipEntry("_README_.txt"));
                archiveStream.write(sb.toString().getBytes());

                archiveStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    EditorTab findFileEditorTab(File file) {
        for (int i = 0; i < tabFolder.getItemCount(); i++) {
            EditorTab editorTab = (EditorTab) tabFolder.getItem(i).getData();

            File localFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText());
            if (localFile.equals(file)) {
                tabFolder.setSelection(i);
                editorTab.setFocus();
                updateCaretPosition();
                return editorTab;
            }
        }
        return null;
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
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem != null) {
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.undo();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Redo\tShift+Ctrl+Z");
        item.setAccelerator(SWT.MOD2 + SWT.MOD1 + 'Z');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem != null) {
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.redo();
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
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).cut();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy\tCtrl+C");
        item.setAccelerator(SWT.MOD1 + 'C');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).copy();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Paste\tCtrl+V");
        item.setAccelerator(SWT.MOD1 + 'V');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).paste();
                }
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Select All\tCtrl+A");
        item.setAccelerator(SWT.MOD1 + 'A');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Control focusControl = shell.getDisplay().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).selectAll();
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        blockSelectionItem = new MenuItem(menu, SWT.CHECK);
        blockSelectionItem.setText("Block Selection\tShift+Alt+A");
        blockSelectionItem.setAccelerator(SWT.MOD2 + SWT.MOD3 + 'A');
        blockSelectionItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                EditorTab editorTab = (EditorTab) tabItem.getData();
                editorTab.setBlockSelection(((MenuItem) e.widget).getSelection());
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Find / Replace...\tCtrl+F");
        item.setAccelerator(SWT.MOD1 + 'F');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem == null) {
                    return;
                }

                if (findReplaceDialog != null && !findReplaceDialog.isDisposed()) {
                    findReplaceDialog.getShell().setFocus();
                    return;
                }

                findReplaceDialog = new FindReplaceDialog(shell);
                findReplaceDialog.setTheme(preferences.getTheme());
                findReplaceDialog.setTarget((EditorTab) tabItem.getData());
                findReplaceDialog.open();
            }

        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Find Next\tCtrl+K");
        item.setAccelerator(SWT.MOD1 + 'K');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem == null) {
                    return;
                }
                EditorTab editorTab = (EditorTab) tabItem.getData();
                if (findReplaceDialog == null || findReplaceDialog.getFindString() == null) {
                    if (findReplaceDialog != null && !findReplaceDialog.isDisposed()) {
                        findReplaceDialog.getShell().setFocus();
                        return;
                    }

                    findReplaceDialog = new FindReplaceDialog(shell);
                    findReplaceDialog.setTheme(preferences.getTheme());
                    findReplaceDialog.setTarget((EditorTab) tabItem.getData());
                    findReplaceDialog.open();
                    return;
                }
                SearchPreferences prefs = preferences.getSearchPreferences();
                editorTab.searchNext(findReplaceDialog.getFindString(), prefs.caseSensitiveSearch, prefs.wrapSearch, prefs.wholeWordSearch, prefs.regexSearch);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Find Previous\tShift+Ctrl+K");
        item.setAccelerator(SWT.MOD2 + SWT.MOD1 + 'K');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem == null) {
                    return;
                }
                EditorTab editorTab = (EditorTab) tabItem.getData();
                if (findReplaceDialog == null || findReplaceDialog.getFindString() == null) {
                    if (findReplaceDialog != null && !findReplaceDialog.isDisposed()) {
                        findReplaceDialog.getShell().setFocus();
                        return;
                    }

                    findReplaceDialog = new FindReplaceDialog(shell);
                    findReplaceDialog.setTheme(preferences.getTheme());
                    findReplaceDialog.setTarget((EditorTab) tabItem.getData());
                    findReplaceDialog.open();
                    return;
                }
                SearchPreferences prefs = preferences.getSearchPreferences();
                editorTab.searchPrevious(findReplaceDialog.getFindString(), prefs.caseSensitiveSearch, prefs.wrapSearch, prefs.wholeWordSearch, prefs.regexSearch);
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Format source\tShift+Ctrl+F");
        item.setAccelerator(SWT.MOD2 + SWT.MOD1 + 'F');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem == null) {
                    return;
                }
                EditorTab editorTab = (EditorTab) tabItem.getData();
                editorTab.formatSource();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Make skip pattern\tShift+Alt+P");
        item.setAccelerator(SWT.MOD2 + SWT.MOD3 + 'P');
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                CTabItem tabItem = tabFolder.getSelection();
                if (tabItem == null) {
                    return;
                }
                EditorTab editorTab = (EditorTab) tabItem.getData();
                editorTab.getEditor().makeSkipPattern();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Next Tab\tCtrl+Tab");
        item.setAccelerator(SWT.MOD1 + SWT.TAB);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleNextTab();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Previous Tab\tShift+Ctrl+Tab");
        item.setAccelerator(SWT.MOD2 + SWT.MOD1 + SWT.TAB);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handlePreviousTab();
            }
        });

        item = getSystemMenuItem(SWT.ID_PREFERENCES);
        if (item == null) {
            new MenuItem(menu, SWT.SEPARATOR);

            item = new MenuItem(menu, SWT.PUSH);
            item.setText("Preferences");
        }
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                PreferencesDialog dlg = new PreferencesDialog(shell, preferences);
                dlg.open();
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
        item.setText("Show Info\tF8");
        item.setAccelerator(SWT.F8);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleShowInfo();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to RAM\tF10");
        item.setAccelerator(SWT.F10);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleUpload(false, false, false);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to RAM with Terminal\tShift+F10");
        item.setAccelerator(SWT.MOD2 | SWT.F10);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleUpload(false, true, false);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to RAM with Debug\tCtrl+F10");
        item.setAccelerator(SWT.MOD1 | SWT.F10);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleUpload(false, false, true);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to Flash\tF11");
        item.setAccelerator(SWT.F11);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleUpload(true, false, false);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to Flash with Terminal\tShift+F11");
        item.setAccelerator(SWT.MOD2 | SWT.F11);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleUpload(true, true, false);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Upload to Flash with Debug\tCtrl+F11");
        item.setAccelerator(SWT.MOD1 | SWT.F11);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleUpload(true, false, true);
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        consoleItem = new MenuItem(menu, SWT.CHECK);
        consoleItem.setText("Toggle Console");
        consoleItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                consoleView.setVisible(consoleItem.getSelection());
                consoleToolItem.setSelection(consoleItem.getSelection());
                centralSashForm.layout();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        createRunMenu(menu);

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Serial Terminal\tF12");
        item.setAccelerator(SWT.F12);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                SerialTerminal serialTerminal = getSerialTerminal();
                if (serialTerminal == null) {
                    serialTerminal = new SerialTerminal();
                    serialTerminal.open();
                    serialTerminal.setSerialPort(new SerialPort(serialPortList.getSelection()));
                }
                serialTerminal.setFocus();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Show Devices\tF7");
        item.setAccelerator(SWT.F7);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleListDevices();
            }
        });

        createPortMenu(menu);

        return menu;
    }

    void createRunMenu(Menu parent) {
        runMenu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        populateRunMenu();

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("Run");
        item.setMenu(runMenu);
    }

    void populateRunMenu() {
        MenuItem[] items = runMenu.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i].dispose();
        }

        ExternalTool[] tools = preferences.getExternalTools();
        for (int i = 0; i < tools.length; i++) {
            String name = tools[i].getName();
            String program = tools[i].getProgram();
            String arguments = tools[i].getArguments();

            MenuItem item = new MenuItem(runMenu, SWT.PUSH);
            if (i < 9) {
                item.setText(name + "\tAlt+" + (char) ('1' + i));
                item.setAccelerator(SWT.MOD3 + '1' + i);
            }
            else {
                item.setText(name);
            }
            item.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    if (!handleRunningProcess()) {
                        return;
                    }

                    consoleView.clear();
                    if (!consoleView.getVisible()) {
                        consoleView.setVisible(true);
                        consoleItem.setSelection(true);
                        consoleToolItem.setSelection(true);
                        centralSashForm.layout();
                    }
                    consoleView.setSerialPort(null);
                    consoleView.closeLogFile();
                    consoleView.setEnabled(true);

                    EditorTab editorTab = getTargetObjectEditorTab();
                    if (editorTab == null) {
                        return;
                    }

                    if (editorTab.isDirty()) {
                        int style = SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
                        MessageBox messageBox = new MessageBox(shell, style);
                        messageBox.setText(APP_TITLE);
                        messageBox.setMessage("Editor contains unsaved changes.  Save before running external tool?");
                        switch (messageBox.open()) {
                            case SWT.CANCEL:
                                return;
                            case SWT.YES:
                                try {
                                    doFileSave(editorTab);
                                    if (editorTab.isDirty()) {
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }

                    List<String> cmd = new ArrayList<>();
                    cmd.add(program);

                    if (arguments != null) {
                        String file = editorTab.getFile().getName();
                        String fileName = file.lastIndexOf('.') != -1 ? file.substring(0, file.lastIndexOf('.')) : file;
                        String fileLoc = editorTab.getFile().getParentFile().getAbsolutePath();

                        String cmdline = arguments.replace("${file}", file).replace("${file.name}", fileName).replace("${file.loc}", fileLoc).replace("${serial}", serialPortList.getSelection());

                        String[] args = Utils.splitArguments(cmdline);
                        cmd.addAll(Arrays.asList(args));
                    }

                    try {
                        runCommand(cmd, editorTab.getFile().getParentFile(), consoleView.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private boolean handleRunningProcess() {
        if (process != null && process.isAlive()) {
            int style = SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO;
            MessageBox messageBox = new MessageBox(shell, style);
            messageBox.setText(APP_TITLE);
            messageBox.setMessage("An external tool is still running.  Terminate?");
            if (messageBox.open() != SWT.YES) {
                return false;
            }
            try {
                process.destroyForcibly();
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                // Do nothing
            }
            if (process.isAlive()) {
                MessageDialog.openError(shell, APP_TITLE, "Can't terminate process " + process.pid());
                return false;
            }
        }
        return true;
    }

    protected void runCommand(List<String> cmd, File outDir, OutputStream stdout) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        builder.directory(outDir);

        StringBuilder sb = new StringBuilder();
        for (String s : cmd) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(s);
        }
        sb.append("\n");
        stdout.write(sb.toString().getBytes());

        process = builder.start();

        Thread ioThread = new Thread() {

            int count;
            byte[] buf = new byte[4096];

            @Override
            public void run() {
                try {
                    InputStream out = process.getInputStream();

                    do {
                        count = out.read(buf);
                        for (int i = 0; i < count; i++) {
                            stdout.write(buf[i]);
                        }
                    } while (count != -1);

                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ioThread.start();
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

    private void handleShowInfo() {
        EditorTab editorTab = getTargetObjectEditorTab();
        if (editorTab == null) {
            return;
        }
        editorTab.waitCompile();
        if (editorTab.hasErrors()) {
            MessageDialog.openError(shell, APP_TITLE, "Program has errors.");
            editorTab.goToFirstError();
            return;
        }

        SpinObject object = editorTab.getObject();
        if (object == null) {
            return;
        }

        if (object instanceof Spin1Object) {
            P1MemoryDialog dlg = new P1MemoryDialog(shell);
            dlg.setTheme(preferences.getTheme());
            dlg.setObject((Spin1Object) object, editorTab.getObjectTree(), editorTab.isTopObject());
            dlg.open();
        }
        else if (object instanceof Spin2Object) {
            P2MemoryDialog dlg = new P2MemoryDialog(shell);
            dlg.setTheme(preferences.getTheme());
            dlg.setObject((Spin2Object) object, editorTab.getObjectTree(), editorTab.isTopObject());
            dlg.open();
        }
    }

    private void handleUpload(boolean writeToFlash, boolean openTerminal, boolean forceDebug) {
        SerialPort serialPort = null;
        boolean serialPortShared = false;

        EditorTab editorTab = getTargetObjectEditorTab();
        if (editorTab == null) {
            return;
        }
        editorTab.waitCompile();
        if (editorTab.hasErrors()) {
            MessageDialog.openError(shell, APP_TITLE, "Program has errors.");
            editorTab.goToFirstError();
            return;
        }

        SpinObject obj = editorTab.getObject();
        if (obj == null) {
            return;
        }
        if (obj instanceof Spin1Object) {
            forceDebug = false;
        }

        boolean isDebug = (obj instanceof Spin2Object) && ((Spin2Object) obj).getDebugger() != null;
        if (isDebug != forceDebug) {
            editorTab.runCompile(forceDebug);
            if (editorTab.hasErrors()) {
                MessageDialog.openError(shell, APP_TITLE, "Program has errors.");
                editorTab.goToFirstError();
                return;
            }
            obj = editorTab.getObject();
            isDebug = forceDebug;
        }

        if (obj instanceof Spin2Object) {
            ((Spin2Object) obj).setClockSetter(preferences.getSpin2ClockSetter());
            ((Spin2Object) obj).setCompress(preferences.getSpin2Compress());
        }

        if (isDebug) {
            consoleView.clear();
            if (!consoleView.getVisible()) {
                consoleView.setVisible(true);
                consoleItem.setSelection(true);
                consoleToolItem.setSelection(true);
                centralSashForm.layout();
            }
            consoleView.setSerialBaudRate(((Spin2Object) obj).getDebugBaud());
        }
        consoleView.setEnabled(isDebug);

        SerialTerminal serialTerminal = getSerialTerminal();
        if (openTerminal) {
            if (serialTerminal == null) {
                serialTerminal = new SerialTerminal();
                serialTerminal.open();
            }
            serialTerminal.setFocus();
        }

        serialPort = consoleView.getSerialPort();
        if (serialPort == null && serialTerminal != null) {
            serialPort = serialTerminal.getSerialPort();
        }
        if (serialPort == null) {
            serialPort = new SerialPort(serialPortList.getSelection());
        }

        consoleView.setSerialPort(null);
        consoleView.closeLogFile();

        if (serialTerminal != null) {
            serialTerminal.setSerialPort(null);
        }

        if (isDebug || serialTerminal != null) {
            serialPortShared = true;
        }

        SerialPort uploadPort = doUpload(obj, writeToFlash, serialPort, serialPortShared);

        if (isDebug) {
            File debugFolder = editorTab.getFile() != null ? editorTab.getFile().getParentFile() : new File("").getAbsoluteFile();
            String fileName = editorTab.getText();
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
            consoleView.setLogFile(debugFolder, String.format("%s.%s.log", fileName, dateFormat.format(date)));
            consoleView.setSerialPort(uploadPort != null ? uploadPort : serialPort);
        }
        else if (serialTerminal != null) {
            serialTerminal.setSerialPort(uploadPort != null ? uploadPort : serialPort);
        }

        if (uploadPort != null) {
            String port = uploadPort.getPortName();
            serialPortList.setSelection(port);
            preferences.setPort(port);
            statusLine.setPort(port);
            if (!serialPort.getPortName().equals(port)) {
                try {
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    // Do nothing
                }
            }
        }
    }

    EditorTab getTargetObjectEditorTab() {
        CTabItem[] items = tabFolder.getItems();
        for (int i = 0; i < items.length; i++) {
            EditorTab editorTab = (EditorTab) items[i].getData();
            if (editorTab.isTopObject()) {
                return editorTab;
            }
        }
        CTabItem tabItem = tabFolder.getSelection();
        return tabItem != null ? (EditorTab) tabItem.getData() : null;
    }

    private SerialPort doUpload(SpinObject obj, boolean writeToFlash, SerialPort serialPort, boolean serialPortShared) {
        IRunnableWithProgress thread;
        Shell activeShell = Display.getDefault().getActiveShell();
        AtomicReference<SerialPort> port = new AtomicReference<>(serialPort);

        ProgressMonitorDialog dlg = new ProgressMonitorDialog(activeShell);

        if (obj instanceof Spin1Object) {
            thread = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Upload", IProgressMonitor.UNKNOWN);

                    try {
                        Propeller1Loader loader = new Propeller1Loader(serialPort, serialPortShared) {

                            @Override
                            protected void bufferUpload(int type, byte[] binaryImage, String text) throws SerialPortException, IOException {
                                monitor.setTaskName("Loading " + text + " to RAM");
                                super.bufferUpload(type, binaryImage, text);
                            }

                            @Override
                            protected void verifyRam() throws SerialPortException, IOException {
                                monitor.setTaskName("Verifying RAM ... ");
                                super.verifyRam();
                            }

                            @Override
                            protected void eepromWrite() throws SerialPortException, IOException {
                                monitor.setTaskName("Writing EEPROM ... ");
                                super.eepromWrite();
                            }

                            @Override
                            protected void eepromVerify() throws SerialPortException, IOException {
                                monitor.setTaskName("Verifying EEPROM ... ");
                                super.eepromVerify();
                            }

                        };

                        SerialPort detectedPort = loader.detect();
                        if (detectedPort == null) {
                            throw new RuntimeException("Propeller 1 not found");
                        }
                        port.set(detectedPort);

                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                dlg.open();
                            }
                        });

                        byte[] image = obj.getBinary();
                        loader.upload(image, writeToFlash ? Propeller1Loader.DOWNLOAD_RUN_EEPROM : Propeller1Loader.DOWNLOAD_RUN_BINARY);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                String msg = e.getMessage();
                                if (e instanceof SerialPortException) {
                                    SerialPortException serialException = (SerialPortException) e;
                                    SerialPort port = serialException.getPort();
                                    msg = port.getPortName() + " " + serialException.getExceptionType();
                                }
                                MessageDialog.openError(activeShell, APP_TITLE, "Error uploading program:\r\n" + msg);
                            }
                        });
                    }

                    monitor.done();
                }

            };
        }
        else {
            thread = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Upload", IProgressMonitor.UNKNOWN);

                    try {
                        Propeller2Loader loader = new Propeller2Loader(serialPort, serialPortShared) {

                            @Override
                            protected void bufferUpload(int type, byte[] binaryImage, String text) throws SerialPortException, IOException {
                                monitor.setTaskName("Loading " + text + " to RAM");
                                super.bufferUpload(type, binaryImage, text);
                            }

                            @Override
                            protected void verifyRam() throws SerialPortException, IOException {
                                monitor.setTaskName("Verifying RAM ... ");
                                super.verifyRam();
                            }

                            @Override
                            protected void flashWrite() throws SerialPortException, IOException {
                                monitor.setTaskName("Writing to flash ... ");
                                super.flashWrite();
                            }

                        };

                        SerialPort detectedPort = loader.detect();
                        if (detectedPort == null) {
                            throw new RuntimeException("Propeller 2 not found");
                        }
                        port.set(detectedPort);

                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                dlg.open();
                            }
                        });

                        byte[] image = obj.getBinary();
                        loader.upload(image, writeToFlash ? Propeller2Loader.DOWNLOAD_RUN_FLASH : Propeller2Loader.DOWNLOAD_RUN_RAM);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                String msg = e.getMessage();
                                if (e instanceof SerialPortException) {
                                    SerialPortException serialException = (SerialPortException) e;
                                    SerialPort port = serialException.getPort();
                                    msg = port.getPortName() + " " + serialException.getExceptionType();
                                }
                                MessageDialog.openError(activeShell, APP_TITLE, "Error uploading program:\r\n" + msg);
                            }
                        });
                    }

                    monitor.done();
                }

            };
        }

        try {
            dlg.setOpenOnRun(false);
            dlg.run(true, true, thread);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return port.get();
    }

    private void handleListDevices() {
        SerialPort terminalPort = null;
        SerialPort devicePort = null;

        SerialTerminal serialTerminal = getSerialTerminal();
        if (serialTerminal != null) {
            terminalPort = serialTerminal.getSerialPort();
            serialTerminal.setSerialPort(null);
        }

        DevicesDialog dlg = new DevicesDialog(shell);
        dlg.setSelection(serialPortList.getSelection());
        dlg.setTerminalPort(terminalPort);
        if (dlg.open() == DevicesDialog.OK) {
            String port = dlg.getSelection();
            if (terminalPort != null && terminalPort.getPortName().equals(port)) {
                devicePort = terminalPort;
            }
            else {
                devicePort = new SerialPort(port);
            }
            serialPortList.setSelection(port);
            preferences.setPort(port);
            statusLine.setPort(port);
        }

        if (serialTerminal != null) {
            serialTerminal.setSerialPort(devicePort != null ? devicePort : terminalPort);
        }

        if (terminalPort != null && devicePort != null && !devicePort.getPortName().equals(terminalPort.getPortName())) {
            try {
                terminalPort.closePort();
            } catch (SerialPortException e) {
                // Do nothing
            }
        }
    }

    void createHelpMenu(Menu parent) {
        Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        MenuItem item = getSystemMenuItem(SWT.ID_ABOUT);
        if (item == null) {
            item = new MenuItem(menu, SWT.PUSH);
            item.setText("About " + APP_TITLE);
        }
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                AboutDialog dlg = new AboutDialog(shell);
                dlg.setTheme(preferences.getTheme());
                dlg.open();
            }
        });

        if (menu.getItemCount() != 0) {
            item = new MenuItem(parent, SWT.CASCADE);
            item.setText("&Help");
            item.setMenu(menu);
        }
    }

    void createTabFolderMenu() {
        tabFolderToolBar = new ToolBar(tabFolder, SWT.FLAT);

        Menu menu = new Menu(tabFolderToolBar);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Next Tab\tCtrl+Tab");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleNextTab();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Previous Tab\tShift+Ctrl+Tab");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handlePreviousTab();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        topObjectTabItem = new MenuItem(menu, SWT.CHECK);
        topObjectTabItem.setText("Pin as Top Object\tCtrl+T");
        topObjectTabItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleSetTopObject();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close Editor\tCtrl+W");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                closeCurrentEditor();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close All Editors\tShift+Ctrl+W");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                e.display.asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (closeCurrentEditor()) {
                            e.display.asyncExec(this);
                        }
                    }

                });
            }
        });

        final int entriesTokeep = menu.getItemCount();

        final ToolItem menuToolItem = new ToolItem(tabFolderToolBar, SWT.PUSH);
        menuToolItem.setImage(ImageRegistry.getImageFromResources("vertical-dots.png"));
        menuToolItem.addListener(SWT.Selection, new Listener() {

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
                    menuItem.setText(((EditorTab) tabItem.getData()).getText());
                    menuItem.setSelection(tabFolder.getSelection() == tabItem);
                    menuItem.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(Event e) {
                            tabFolder.setSelection(tabItem);
                            tabItem.getControl().setFocus();
                        }
                    });
                }

                Rectangle rect = menuToolItem.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = tabFolderToolBar.toDisplay(pt);
                menu.setLocation(pt.x, pt.y);
                menu.setVisible(true);
            }
        });

        tabFolder.setTopRight(tabFolderToolBar);
    }

    private void handleNextTab() {
        if (tabFolder.getItemCount() <= 1) {
            return;
        }

        int index = tabFolder.getSelectionIndex() + 1;
        if (index >= tabFolder.getItemCount()) {
            index = 0;
        }
        tabFolder.setSelection(index);
        updateEditorSelection();
        updateCaretPosition();
    }

    private void handlePreviousTab() {
        if (tabFolder.getItemCount() <= 1) {
            return;
        }

        int index = tabFolder.getSelectionIndex() - 1;
        if (index < 0) {
            index = tabFolder.getItemCount() - 1;
        }
        tabFolder.setSelection(index);
        updateEditorSelection();
        updateCaretPosition();
    }

    private void handleSetTopObject() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            editorTab.toggleTopObject();
            objectBrowser.setInput(editorTab.getObjectTree(), editorTab.isTopObject());
            topObjectItem.setSelection(editorTab.isTopObject());
            topObjectTabItem.setSelection(editorTab.isTopObject());
            topObjectToolItem.setSelection(editorTab.isTopObject());
        }
        else {
            topObjectItem.setSelection(false);
            topObjectTabItem.setSelection(false);
            topObjectToolItem.setSelection(false);
        }
    }

    boolean closeCurrentEditor() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            return closeEditor(tabItem);
        }
        return false;
    }

    boolean closeEditor(CTabItem tabItem) {
        EditorTab editorTab = (EditorTab) tabItem.getData();
        if (canCloseEditorTab(editorTab)) {
            tabItem.dispose();
            return true;
        }
        return false;
    }

    boolean canCloseEditorTab(EditorTab editorTab) {
        if (editorTab.isDirty()) {
            int style = SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
            MessageBox messageBox = new MessageBox(shell, style);
            messageBox.setText(APP_TITLE);
            messageBox.setMessage("Editor contains unsaved changes.  Save before close?");
            switch (messageBox.open()) {
                case SWT.CANCEL:
                    return false;
                case SWT.YES:
                    try {
                        doFileSave(editorTab);
                        if (editorTab.isDirty()) {
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    break;
            }
        }
        if (editorTab.isTopObject()) {
            int style = SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO;
            MessageBox messageBox = new MessageBox(shell, style);
            messageBox.setText(APP_TITLE);
            messageBox.setMessage("Closing this tab will disable top object compile.  Close anyway?");
            switch (messageBox.open()) {
                case SWT.YES:
                    preferences.setTopObject(null);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private boolean handleUnsavedContent() {
        boolean dirty = false;

        CTabItem[] tabItems = tabFolder.getItems();
        for (int i = 0; i < tabItems.length; i++) {
            EditorTab editorTab = (EditorTab) tabItems[i].getData();
            if (editorTab.isDirty()) {
                dirty = true;
                break;
            }
        }

        if (dirty) {
            int style = SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
            MessageBox messageBox = new MessageBox(shell, style);
            messageBox.setText(APP_TITLE);
            messageBox.setMessage("Editor contains unsaved changes.  Save before exit?");
            int rc = messageBox.open();
            switch (rc) {
                case SWT.YES:
                    for (int i = 0; i < tabItems.length; i++) {
                        EditorTab editorTab = (EditorTab) tabItems[i].getData();
                        if (editorTab.isDirty()) {
                            doFileSave(editorTab);
                            if (editorTab.isDirty()) {
                                return false;
                            }
                        }
                    }
                    return true;
                case SWT.NO:
                    break;
                default:
                    return false;
            }
        }

        return true;
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

    void updateEditorSelection() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();

            if (getTargetObjectEditorTab() == editorTab) {
                objectBrowser.setInput(editorTab.getObjectTree(), editorTab.isTopObject());
            }
            outlineViewStack.setTopView(editorTab.getOutlineView());
            if (findReplaceDialog != null) {
                findReplaceDialog.setTarget(editorTab);
            }
            topObjectItem.setSelection(editorTab.isTopObject());
            topObjectTabItem.setSelection(editorTab.isTopObject());
            topObjectToolItem.setSelection(editorTab.isTopObject());
            blockSelectionItem.setSelection(editorTab.isBlockSelection());

            editorTab.setFocus();
        }
        else {
            if (getTargetObjectEditorTab() == null) {
                objectBrowser.setInput(null, false);
            }
            outlineViewStack.setTopView(null);
            if (findReplaceDialog != null) {
                findReplaceDialog.setTarget(null);
            }
            topObjectItem.setSelection(false);
            topObjectTabItem.setSelection(false);
            topObjectToolItem.setSelection(false);
            blockSelectionItem.setSelection(false);

            tabFolder.forceFocus();
        }
        updateCaretPosition();
    }

    void updateCaretPosition() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            statusLine.setCaretPositionText("");
            return;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();
        StyledText styledText = editorTab.getEditor().getStyledText();
        int offset = styledText.getCaretOffset();
        int y = styledText.getLineAtOffset(offset);
        int x = offset - styledText.getOffsetAtLine(y);
        statusLine.setCaretPositionText(String.format("%d : %d : %d", y + 1, x + 1, offset));
    }

    SourceLocation getCurrentSourceLocation() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem == null) {
            return null;
        }
        EditorTab editorTab = (EditorTab) tabItem.getData();
        StyledText styledText = editorTab.getEditor().getStyledText();
        int offset = styledText.getCaretOffset();
        int topPixel = styledText.getTopPixel();
        File localFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText());
        return new SourceLocation(localFile, offset, topPixel);
    }

    private MenuItem getSystemMenuItem(int id) {
        Menu menu = Display.getDefault().getSystemMenu();
        if (menu != null) {
            MenuItem[] item = menu.getItems();
            for (int i = 0; i < item.length; i++) {
                if (item[i].getID() == id) {
                    return item[i];
                }
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

        ColorRegistry.initSystemDefaults();

        display.setErrorHandler(new Consumer<Error>() {

            @Override
            public void accept(Error e) {
                openInternalError(display.getActiveShell(), "An unexpected error has occured.", e);
            }

        });
        display.setRuntimeExceptionHandler(new Consumer<RuntimeException>() {

            @Override
            public void accept(RuntimeException e) {
                openInternalError(display.getActiveShell(), "An unexpected error has occured.", e);
            }

        });

        Realm.runWithDefault(DisplayRealm.getRealm(display), new Runnable() {

            @Override
            public void run() {
                try {
                    Shell shell = new Shell(display);
                    shell.setText(APP_TITLE);
                    shell.setImages(new Image[] {
                        ImageRegistry.getImageFromResources("app64.png"),
                        ImageRegistry.getImageFromResources("app48.png"),
                        ImageRegistry.getImageFromResources("app32.png"),
                        ImageRegistry.getImageFromResources("app16.png"),
                    });

                    Rectangle screen = display.getClientArea();

                    Rectangle rect = new Rectangle(0, 0, (int) (screen.width * 0.8), (int) (screen.height * 0.85));
                    rect.x = (screen.width - rect.width) / 2;
                    rect.y = (screen.height - rect.height) / 2;
                    if (rect.y < 0) {
                        rect.height += rect.y * 2;
                        rect.y = 0;
                    }

                    shell.setLocation(rect.x, rect.y);
                    shell.setSize(rect.width, rect.height);

                    FillLayout layout = new FillLayout();
                    layout.marginWidth = layout.marginHeight = 4;
                    shell.setLayout(layout);

                    new SpinTools(shell);

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

    public static void openInternalError(Shell shell, String message, Throwable details) {
        details.printStackTrace();
        InternalErrorDialog dlg = new InternalErrorDialog(shell, APP_TITLE, null, "An unexpected error has occured.", details, MessageDialog.ERROR, new String[] {
            IDialogConstants.OK_LABEL, IDialogConstants.SHOW_DETAILS_LABEL
        }, 0);
        dlg.setDetailButton(1);
        dlg.open();
    }

}
