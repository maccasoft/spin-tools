/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import com.maccasoft.propeller.Preferences.LruData;
import com.maccasoft.propeller.Preferences.PackageFile;
import com.maccasoft.propeller.Preferences.SearchPreferences;
import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.ComPortList;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.SerialComPort;
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

public class SpinTools {

    public static final String APP_TITLE = "Spin Tools IDE";
    public static final String APP_VERSION = "0.52.1";

    static final File defaultSpin1Examples = new File(System.getProperty("APP_DIR"), "examples/P1").getAbsoluteFile();
    static final File defaultSpin2Examples = new File(System.getProperty("APP_DIR"), "examples/P2").getAbsoluteFile();

    Display display;
    Shell shell;
    Composite toolBarContainer;

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

    Process process;
    MenuItem runMenuItem;

    MenuItem topObjectItem;
    MenuItem blockSelectionItem;
    ToolItem topObjectToolItem;
    MenuItem topObjectTabItem;

    MenuItem consoleItem;
    ToolItem consoleToolItem;

    Menu bookmarksMenu;

    SourcePool sourcePool;
    ComPortList comPortList;

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
        "*.spin;*.spin2;*.c;*.pasm;*.p2asm",
        "*.c",
        "*.spin;*.pasm",
        "*.spin2;*.p2asm"
    };

    final IOpenListener openListener = new IOpenListener() {

        @Override
        public void open(OpenEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();

            if (selection.getFirstElement() instanceof File fileToOpen) {
                if (fileToOpen.isDirectory()) {
                    return;
                }
                String name = fileToOpen.getName().toLowerCase();
                if (name.endsWith(".spin") || name.endsWith(".pasm") || name.endsWith(".spin2") || name.endsWith(".p2asm") || name.endsWith(".c")) {
                    openOrSwitchToTab(fileToOpen);
                }
                else if (name.endsWith(".json")) {
                    FirmwarePackDialog dlg = new FirmwarePackDialog(shell, preferences);
                    dlg.open(fileToOpen);
                }
                else {
                    Program.launch(fileToOpen.getAbsolutePath());
                }
            }
            else if (selection.getFirstElement() instanceof ObjectTree objectTree) {
                File fileToOpen = objectTree.getFile();
                if (fileToOpen.isDirectory()) {
                    return;
                }
                String name = fileToOpen.getName().toLowerCase();
                if (name.endsWith(".spin") || name.endsWith(".spin2")) {
                    openOrSwitchToTab(fileToOpen);
                }
            }
            else if (selection.getFirstElement() instanceof ObjectNode node) {
                openOrSwitchToTab(node.getFileName());
            }
            else if (selection.getFirstElement() instanceof DirectiveNode.IncludeNode node) {
                openOrSwitchToTab(node.getFileName());
            }
            else if (selection.getFirstElement() instanceof VariableNode node) {
                openOrSwitchToTab(node.getType().getText());
            }
        }

    };

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_SHOW_TOOLBAR:
                    toolBarContainer.setVisible((Boolean) evt.getNewValue());
                    ((GridData) toolBarContainer.getLayoutData()).exclude = !((Boolean) evt.getNewValue()).booleanValue();
                    toolBarContainer.getParent().layout(true, true);
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
                case Preferences.PROP_WINDOW_FONT:
                    Font textFont = JFaceResources.getDefaultFont();
                    FontData fontData = textFont.getFontData()[0];
                    if (evt.getNewValue() != null) {
                        fontData = StringConverter.asFontData(evt.getNewValue().toString());
                    }
                    fontData.setStyle(SWT.NONE);
                    objectBrowser.updateFontsFrom(fontData);
                    fileBrowser.updateFontsFrom(fontData);
                    outlineViewStack.updateFontsFrom(fontData);
                    break;
            }
        }
    };

    final DisposeListener tabItemDisposeListener = new DisposeListener() {

        @Override
        public void widgetDisposed(DisposeEvent e) {
            if (shell.isDisposed() || toolBarContainer.isDisposed() || objectBrowser.isDisposed()) {
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
        this.display = shell.getDisplay();
        this.shell = shell;
        this.shell.setData(this);

        preferences = Preferences.getInstance();

        boolean dark = "dark".equals(preferences.getTheme());
        if ("win32".contentEquals(SWT.getPlatform()) && Display.isSystemDarkTheme() && preferences.getTheme() == null) {
            dark = true;
        }
        if (dark) {
            try {
                @SuppressWarnings("rawtypes")
                Class clazz = Class.forName("org.eclipse.swt.internal." + SWT.getPlatform() + ".OS");
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

        toolBarContainer = new Composite(container, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = layout.verticalSpacing = 0;
        layout.marginWidth = layout.marginHeight = 0;
        toolBarContainer.setLayout(layout);
        toolBarContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        toolBarContainer.setVisible(preferences.getShowToolbar());
        ((GridData) toolBarContainer.getLayoutData()).exclude = !preferences.getShowToolbar();

        ToolBar mainToolBar = createToolbar(toolBarContainer);
        mainToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ToolBar sideToolBar = createSideToolbar(toolBarContainer);
        sideToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        sashForm = new SashForm(container, SWT.HORIZONTAL);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        browserSashForm = new SashForm(sashForm, SWT.VERTICAL);
        browserSashForm.setVisible(preferences.getShowObjectBrowser() || preferences.getShowBrowser());

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

        if ("win32".equals(SWT.getPlatform()) || preferences.getTheme() != null) {
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

        DropTarget dropTarget = new DropTarget(tabFolder, DND.DROP_COPY | DND.DROP_MOVE);
        dropTarget.setTransfer(FileTransfer.getInstance());
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetEvent e) {
                if (e.detail == DND.DROP_NONE) {
                    e.detail = DND.DROP_LINK;
                }
            }

            @Override
            public void dragOperationChanged(DropTargetEvent e) {
                if (e.detail == DND.DROP_NONE) {
                    e.detail = DND.DROP_LINK;
                }
            }

            @Override
            public void drop(DropTargetEvent event) {
                CTabItem selection = null;

                if (event.data instanceof String[] dataArray) {
                    for (String s : dataArray) {
                        File fileToOpen = new File(s);
                        String name = fileToOpen.getName().toLowerCase();
                        if (name.endsWith(".spin") || name.endsWith(".p1asm") || name.endsWith(".spin2") || name.endsWith(".p2asm")) {
                            if (fileToOpen.exists()) {
                                EditorTab editorTab = openOrSwitchToTab(fileToOpen);
                                if (selection == null) {
                                    selection = editorTab.getTabItem();
                                }
                            }
                        }
                    }
                    if (selection != null) {
                        tabFolder.setSelection(selection);
                        tabFolder.notifyListeners(SWT.Selection, new Event());
                    }
                }
                else {
                    event.detail = DND.DROP_NONE;
                }
            }

        });

        sourcePool = new SourcePool();

        comPortList = new ComPortList(preferences);

        String portName = preferences.getPort();
        if (portName != null) {
            comPortList.setSelection(portName);
        }
        ComPort selection = comPortList.getSelection();
        if (selection != null) {
            statusLine.setPortText(selection.getName());
            statusLine.setPortToolTipText(selection.getDescription());
        }

        comPortList.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ComPort port = (ComPort) evt.getNewValue();

                SerialTerminal serialTerminal = getSerialTerminal();
                if (serialTerminal != null && serialTerminal.getSerialPort() != null) {
                    if (!port.equals(serialTerminal.getSerialPort())) {
                        ComPort oldSerialPort = serialTerminal.getSerialPort();
                        try {
                            if (oldSerialPort.isOpened()) {
                                oldSerialPort.closePort();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        serialTerminal.setSerialPort(port);
                    }
                }

                if (consoleView.getSerialPort() != null && !port.equals(consoleView.getSerialPort())) {
                    ComPort oldSerialPort = consoleView.getSerialPort();
                    try {
                        if (oldSerialPort.isOpened()) {
                            oldSerialPort.closePort();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    consoleView.setSerialPort(port);
                }

                preferences.setPort(port.getPortName());

                statusLine.setPortText(port.getName());
                statusLine.setPortToolTipText(port.getDescription());
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
                        EditorTab editorTab = (EditorTab) tabFolder.getItem(i).getData();
                        File file = editorTab.getFile();
                        if (file != null) {
                            StyledText styledText = editorTab.getEditor().getStyledText();
                            preferences.setLruData(file, styledText.getTopIndex(), styledText.getCaretOffset());

                            openTabs.add(file.getAbsolutePath());
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

        display.addListener(SWT.OpenDocument, event -> {
            File fileToOpen = new File(event.text);
            String name = fileToOpen.getName().toLowerCase();
            if (name.endsWith(".spin") || name.endsWith(".p1asm") || name.endsWith(".spin2") || name.endsWith(".p2asm")) {
                if (fileToOpen.exists()) {
                    openOrSwitchToTab(fileToOpen);
                    shell.setActive();
                }
            }
        });
    }

    void applyTheme(String id) {
        Color widgetForeground = null;
        Color widgetBackground = null;
        Color listForeground = null;
        Color listBackground = null;
        Color tabfolderBackground = null;

        if ("win32".equals(SWT.getPlatform()) && id == null) {
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
            if ("win32".equals(SWT.getPlatform())) {
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

        toolBarContainer.setBackground(widgetBackground);
        Control[] toolBars = toolBarContainer.getChildren();
        Control[] items = ((ToolBar) toolBars[0]).getChildren();
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
        ToolItem[] toolItems = tabFolderToolBar.getItems();
        for (int i = 0; i < toolItems.length; i++) {
            toolItems[i].setBackground(tabfolderBackground);
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
        openFromMenu.addMenuListener(new MenuAdapter() {

            @Override
            public void menuShown(MenuEvent e) {
                MenuItem[] items = openFromMenu.getItems();
                for (int i = 0; i < items.length; i++) {
                    items[i].dispose();
                }
                populateOpenFromMenu(openFromMenu);
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
        saveToMenu.addMenuListener(new MenuAdapter() {

            @Override
            public void menuShown(MenuEvent e) {
                MenuItem[] items = saveToMenu.getItems();
                for (int i = 0; i < items.length; i++) {
                    items[i].dispose();
                }

                populateSaveToMenu(saveToMenu);

                if (saveToMenu.getItemCount() == 0) {
                    MenuItem item = new MenuItem(saveToMenu, SWT.PUSH);
                    item.setText("None");
                    item.setEnabled(false);
                }
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

        menu.addMenuListener(new MenuAdapter() {

            List<MenuItem> list = new ArrayList<MenuItem>();

            @Override
            public void menuShown(MenuEvent e) {
                for (MenuItem item : list) {
                    item.dispose();
                }
                list.clear();
                populateLruFiles(menu, lruIndex, list);
            }

        });
    }

    void populateOpenFromMenu(Menu menu) {
        List<String> list = new ArrayList<String>();
        List<String> defaultList = Arrays.asList(new String[] {
            defaultSpin1Examples.getAbsolutePath(),
            defaultSpin2Examples.getAbsolutePath(),
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

        List<File> lru = new ArrayList<>(preferences.getLru());
        lru.sort((o1, o2) -> o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath()));
        for (File file : lru) {
            String folder = file.getParent();
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
            defaultSpin1Examples.getAbsolutePath(),
            defaultSpin1Examples.getAbsolutePath(),
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

        List<File> lru = new ArrayList<>(preferences.getLru());
        lru.sort((o1, o2) -> o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath()));
        for (File file : lru) {
            String folder = file.getParent();
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

        Iterator<File> iter = preferences.getLru().iterator();
        while (iter.hasNext()) {
            File fileToOpen = iter.next();

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
                    openOrSwitchToTab(fileToOpen);
                }
            });
            list.add(item);

            index++;
        }
    }

    ToolBar createToolbar(Composite parent) {
        ToolItem toolItem;
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
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
                    if (location.file != null) {
                        EditorTab editorTab = openOrSwitchToTab(location.file);
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
                                updateEditorSelection();
                                updateCaretPosition();
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
                    if (location.file != null) {
                        EditorTab editorTab = openOrSwitchToTab(location.file);
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
                                updateEditorSelection();
                                updateCaretPosition();
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
                    boolean forceDebug = (e.stateMask & SWT.MOD1) != 0;
                    handleShowInfo(forceDebug);
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
                        SerialTerminal newTerminal = new SerialTerminal(display, preferences);
                        newTerminal.open();
                        display.asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                newTerminal.setSerialPort(comPortList.getSelection());
                            }

                        });
                        serialTerminal = newTerminal;
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

    ToolBar createSideToolbar(Composite parent) {
        ToolItem toolItem;
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT);

        toolItem = new ToolItem(toolBar, SWT.PUSH);
        toolItem.setImage(ImageRegistry.getImageFromResources("layout-select-sidebar-right.png"));
        toolItem.setToolTipText("Toggle Outline View");
        toolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                outlineViewStack.setVisible(!outlineViewStack.getVisible());
                editorSashForm.layout();
                tabFolder.forceFocus();
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
        if (filterPath == null && !preferences.getLru().isEmpty()) {
            filterPath = preferences.getLru().get(0);
        }

        if (filterPath != null) {
            dlg.setFilterPath(filterPath.getParent());
        }

        String fileName = dlg.open();
        if (fileName != null) {
            File fileToOpen = new File(fileName);
            openOrSwitchToTab(fileToOpen);
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
            openOrSwitchToTab(fileToOpen);
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
            return openOrSwitchToTab(fileToOpen);
        }

        return null;
    }

    EditorTab openNewTab(File fileToOpen) {
        EditorTab editorTab = new EditorTab(tabFolder, fileToOpen, sourcePool);
        hookListeners(editorTab);

        tabFolder.getDisplay().asyncExec(() -> {
            try {
                tabFolder.setSelection(editorTab.getTabItem());

                editorTab.setEditorText(FileUtils.loadFromFile(fileToOpen));

                LruData lruData = preferences.getLruData(fileToOpen);
                if (lruData != null) {
                    StyledText styledText = editorTab.getEditor().getStyledText();
                    styledText.setCaretOffset(lruData.caretPosition);
                    if (lruData.topIndex != 0) {
                        styledText.setTopIndex(lruData.topIndex);
                    }
                }
                preferences.addToLru(fileToOpen);

                tabFolder.notifyListeners(SWT.Selection, new Event());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return editorTab;
    }

    void hookListeners(EditorTab editorTab) {
        OutlineView outlineView = outlineViewStack.createNew();
        outlineView.addOpenListener(openListener);

        editorTab.setOutlineView(outlineView);

        editorTab.addCaretListener(event -> updateCaretPosition());
        editorTab.addPropertyChangeListener(evt -> {
            EditorTab topEditorTab = getTargetObjectEditorTab();
            switch (evt.getPropertyName()) {
                case EditorTab.OBJECT_TREE:
                    if (evt.getSource() == topEditorTab) {
                        objectBrowser.setInput((ObjectTree) evt.getNewValue(), topEditorTab.isTopObject());
                    }
                    break;
            }
        });
        editorTab.getEditor().addSourceListener(element -> {
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

            EditorTab targetEditorTab = objectFileName != null ? openOrSwitchToTab(objectFileName) : (EditorTab) tabFolder.getSelection().getData();
            if (targetEditorTab == null) {
                return;
            }
            SourceEditor editor = targetEditorTab.getEditor();
            shell.getDisplay().asyncExec(() -> {
                editor.goToLineColumn(element.line, element.column);
                updateEditorSelection();
                updateCaretPosition();
            });

            if (sourceLocation != null) {
                backStack.push(sourceLocation);
                forwardStack.clear();
            }
        });
        editorTab.getEditor().getRuler().addListener(bookmarks -> {

            updateBookmarksMenu(bookmarks);
            if (editorTab.getFile() != null) {
                preferences.setBookmarks(editorTab.getFile(), bookmarks);
            }

        });

        editorTab.getEditor().getStyledText().setMenu(createContextMenu());
        editorTab.getTabItem().addDisposeListener(tabItemDisposeListener);
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
        hookListeners(editorTab);

        blockSelectionItem.setSelection(editorTab.isBlockSelection());
        objectBrowser.setInput(null, false);

        tabFolder.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    tabFolder.setSelection(editorTab.getTabItem());
                    tabFolder.notifyListeners(SWT.Selection, new Event());

                    outlineViewStack.setTopView(editorTab.getOutlineView());

                    editorTab.setEditorText(text);
                    editorTab.setFocus();
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
            editorTab.save();
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

        if (filterPath == null && !preferences.getLru().isEmpty()) {
            filterPath = preferences.getLru().get(0);
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
            if (parentFile == null && !preferences.getLru().isEmpty()) {
                parentFile = preferences.getLru().get(0).getParentFile();
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
                editorTab.save();
                editorTab.clearDirty();

                preferences.addToLru(fileToSave);
                preferences.setBookmarks(fileToSave, editorTab.getEditor().getRuler().getBookmarks());

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
        EditorTab editorTab = getTargetObjectEditorTab();
        if (editorTab != null) {
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

        String processor;
        if (rootFileName.toLowerCase().endsWith(".spin")) {
            processor = "P1";
        }
        else if (rootFileName.toLowerCase().endsWith(".spin2")) {
            processor = "P2";
        }
        else {
            processor = "C";
        }

        if (rootFileName.indexOf('.') != -1) {
            rootFileName = rootFileName.substring(0, rootFileName.lastIndexOf('.'));
        }

        String archiveName = String.format("%s - %s - Archive [Date %s Time %s].zip",
            rootFileName,
            processor,
            dateFormat.format(now),
            timeFormat.format(now));

        dlg.setFileName(archiveName);
        dlg.setOverwrite(true);

        File filterPath = editorTab.getFile();
        if (filterPath == null && !preferences.getLru().isEmpty()) {
            filterPath = preferences.getLru().get(0);
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

                File topFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText());
                Path topFilePath = topFile.getAbsoluteFile().getParentFile().toPath();

                for (File file : editorTab.getDependencies()) {
                    Path filePath = file.getAbsoluteFile().toPath();
                    if (filePath.startsWith(topFilePath)) {
                        archiveStream.putNextEntry(new ZipEntry(topFilePath.relativize(filePath).toString()));
                    }
                    else {
                        archiveStream.putNextEntry(new ZipEntry(file.getName()));
                    }

                    boolean found = false;
                    for (int i = 0; i < tabFolder.getItemCount(); i++) {
                        EditorTab tab = (EditorTab) tabFolder.getItem(i).getData();
                        File localFile = tab.getFile() != null ? tab.getFile() : new File(tab.getText());
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
                sb.append("Parallax Propeller " + processor + " Project Archive");
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

    EditorTab openOrSwitchToTab(File fileToOpen) {
        for (CTabItem tabItem : tabFolder.getItems()) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            File localFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText());
            if (localFile.equals(fileToOpen)) {
                tabFolder.setSelection(tabItem);
                tabFolder.notifyListeners(SWT.Selection, new Event());
                editorTab.setFocus();
                return editorTab;
            }
        }
        return openNewTab(fileToOpen);
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

        bookmarksMenu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Go to Bookmark...");
        item.setMenu(bookmarksMenu);

        for (int i = 1; i <= 9; i++) {
            final int bookmarkNumber = i - 1;
            item = new MenuItem(bookmarksMenu, SWT.PUSH);
            item.setText(String.valueOf(i) + "\tCtrl+" + String.valueOf(i));
            item.setAccelerator(SWT.MOD1 + '0' + i);
            item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    CTabItem tabItem = tabFolder.getSelection();
                    if (tabItem == null) {
                        return;
                    }
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    editorTab.getEditor().navigateToBookmark(bookmarkNumber);
                }

            });
        }

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
                handleShowInfo(false);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Show Info with Debug\tCtrl+F8");
        item.setAccelerator(SWT.MOD1 | SWT.F8);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleShowInfo(true);
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

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Firmware Packages...");
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                handleFirmwarePack();
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

        runMenuItem = new MenuItem(menu, SWT.CASCADE);
        runMenuItem.setText("Run");
        runMenuItem.setMenu(new Menu(parent.getParent(), SWT.DROP_DOWN));

        populateRunMenu();

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Serial Terminal\tF12");
        item.setAccelerator(SWT.F12);
        item.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event e) {
                SerialTerminal serialTerminal = getSerialTerminal();
                if (serialTerminal == null) {
                    SerialTerminal newTerminal = new SerialTerminal(display, preferences);
                    newTerminal.open();
                    display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            newTerminal.setSerialPort(comPortList.getSelection());
                        }

                    });
                    serialTerminal = newTerminal;
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

    void populateRunMenu() {
        Menu menu = runMenuItem.getMenu();

        MenuItem[] items = menu.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i].dispose();
        }

        int i = 0;
        for (ExternalTool tool : preferences.getExternalTools()) {
            MenuItem item = new MenuItem(menu, SWT.PUSH);
            if (i < 9) {
                item.setText(tool.getName() + "\tAlt+" + (char) ('1' + i));
                item.setAccelerator(SWT.MOD3 + '1' + i);
            }
            else {
                item.setText(tool.getName());
            }
            item.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    if (!handleRunningProcess()) {
                        return;
                    }
                    handleRunExternalTool(tool);
                }
            });
            i++;
        }

        if (i == 0) {
            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText("No configured external tools");
            item.setEnabled(false);
        }
    }

    private void handleRunExternalTool(ExternalTool tool) {
        EditorTab editorTab = getTargetObjectEditorTab();
        if (editorTab == null) {
            return;
        }

        switch (tool.getEditorAction()) {
            case ExternalTool.EDITOR_WARN_UNSAVED:
                if (editorTab.isDirty()) {
                    int style = SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
                    MessageBox messageBox = new MessageBox(shell, style);
                    messageBox.setText(APP_TITLE);
                    messageBox.setMessage("Editor contains unsaved changes.  Save before running external tool?");
                    switch (messageBox.open()) {
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
                        case SWT.NO:
                            break;
                        default:
                            return;
                    }
                }
                break;

            case ExternalTool.EDITOR_AUTOSAVE:
                if (editorTab.isDirty()) {
                    try {
                        doFileSave(editorTab);
                        if (editorTab.isDirty()) {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        File editorFile = editorTab.getFile();
        if (editorFile == null) {
            editorFile = new File(editorTab.getText()).getAbsoluteFile();
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(tool.getProgram());

        String arguments = tool.getArguments();
        if (arguments != null) {
            String file = editorFile.getName();
            String fileName = file.lastIndexOf('.') != -1 ? file.substring(0, file.lastIndexOf('.')) : file;
            String fileLoc = editorFile.getParentFile().getAbsolutePath();

            String cmdline = arguments.replace("${file}", file) //
                .replace("${file.name}", fileName) //
                .replace("${file.loc}", fileLoc);

            ComPort comPort = comPortList.getSelection();
            if (comPort instanceof SerialComPort) {
                cmdline = cmdline.replace("${serial}", comPort.getPortName());
            }
            if (comPort instanceof NetworkComPort) {
                cmdline = cmdline.replace("${ip}", ((NetworkComPort) comPort).getInetAddr().getHostAddress());
            }

            String[] args = Utils.splitArguments(cmdline);
            cmd.addAll(Arrays.asList(args));
        }

        try {
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

            runCommand(cmd, editorFile.getParentFile(), consoleView.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(shell, APP_TITLE, e.getMessage());
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

    protected void runCommand(List<String> cmd, File outDir, OutputStream stdout) throws IOException {
        ComPort serialPort = comPortList.getSelection();

        SerialTerminal serialTerminal = getSerialTerminal();
        if (serialTerminal != null) {
            serialTerminal.setSerialPort(null);
        }

        if (serialPort != null && serialPort.isOpened()) {
            serialPort.closePort();
        }

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
                        if (count > 0) {
                            stdout.write(buf, 0, count);
                        }
                    } while (count != -1);

                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                display.asyncExec(() -> {
                    if (serialTerminal != null) {
                        serialTerminal.setSerialPort(serialPort);
                    }

                    EditorTab editorTab = getTargetObjectEditorTab();
                    if (editorTab != null) {
                        File editorFile = editorTab.getFile();
                        if (editorFile != null) {
                            fileBrowser.refresh(editorFile);
                        }
                    }

                    CTabItem tabItem = tabFolder.getSelection();
                    if (tabItem != null) {
                        EditorTab currentEditorTab = (EditorTab) tabItem.getData();
                        File editorFile = editorTab.getFile();
                        if (editorFile != null) {
                            fileBrowser.refresh(editorFile);
                            currentEditorTab.checkExternalContentUpdate();
                        }
                    }
                });
            }
        };
        ioThread.start();
    }

    void createPortMenu(Menu parent) {
        final Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);
        menu.addMenuListener(new MenuAdapter() {

            @Override
            public void menuShown(MenuEvent e) {
                comPortList.fillMenu(menu);

                if (menu.getItemCount() == 0) {
                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                    item.setText("No available ports");
                    item.setEnabled(false);
                }
            }

        });

        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText("&Port");
        item.setMenu(menu);
    }

    private void handleShowInfo(boolean forceDebug) {
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
        }

        if (obj instanceof Spin1Object) {
            P1MemoryDialog dlg = new P1MemoryDialog(shell);
            dlg.setTheme(preferences.getTheme());
            dlg.setObject((Spin1Object) obj, editorTab.getObjectTree(), editorTab.isTopObject());
            dlg.open();
        }
        else if (obj instanceof Spin2Object) {
            P2MemoryDialog dlg = new P2MemoryDialog(shell);
            dlg.setTheme(preferences.getTheme());
            dlg.setObject((Spin2Object) obj, editorTab.getObjectTree(), editorTab.isTopObject());
            dlg.open();
        }
    }

    String handleBrowseFirmwarePack(String filterPath) {
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setText("Open Firmware Pack");
        dlg.setFilterNames(FirmwarePackDialog.filterNames);
        dlg.setFilterExtensions(FirmwarePackDialog.filterExtensions);
        dlg.setFilterIndex(0);

        if (preferences.getPackageLru().size() != 0) {
            PackageFile lruFile = preferences.getPackageLru().get(0);
            filterPath = lruFile.getFile().getAbsolutePath();
        }

        if (filterPath != null && !filterPath.isBlank()) {
            File file = new File(filterPath).getAbsoluteFile().getParentFile();
            if (file != null) {
                filterPath = file.getAbsolutePath();
            }
        }
        if (filterPath != null && !filterPath.isBlank()) {
            dlg.setFilterPath(filterPath);
        }

        return dlg.open();
    }

    private void handleUpload(boolean writeToFlash, boolean openTerminal, boolean forceDebug) {
        ComPort serialPort = null;
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

        SerialTerminal serialTerminal = getSerialTerminal();
        if (openTerminal) {
            if (serialTerminal == null) {
                serialTerminal = new SerialTerminal(display, preferences);
                serialTerminal.open();
            }
            serialTerminal.setFocus();
        }

        serialPort = comPortList.getSelection();

        if (serialTerminal != null) {
            serialTerminal.setSerialPort(null);
        }

        consoleView.setSerialPort(null);
        consoleView.closeLogFile();
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
        consoleView.setEnabled(false);

        if (isDebug || serialTerminal != null) {
            serialPortShared = true;
        }

        ComPort uploadPort = doUpload(obj, writeToFlash, serialPort, serialPortShared);

        if (isDebug) {
            File topObjectFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText()).getAbsoluteFile();
            consoleView.setTopObjectFile(topObjectFile);
            consoleView.setSerialPort(uploadPort != null ? uploadPort : serialPort);
            consoleView.setEnabled(true);
        }
        else if (serialTerminal != null) {
            serialTerminal.setSerialPort(uploadPort != null ? uploadPort : serialPort);
            if (openTerminal) {
                serialTerminal.setFocus();
            }
        }

        if (uploadPort != null) {
            comPortList.setSelection(uploadPort);
            preferences.setPort(uploadPort.getPortName());
            statusLine.setPortText(uploadPort.getName());
            statusLine.setPortToolTipText(uploadPort.getDescription());
            if (!uploadPort.equals(serialPort)) {
                if (serialPort != null && serialPort.isOpened()) {
                    serialPort.closePort();
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

    private ComPort doUpload(SpinObject obj, boolean writeToFlash, ComPort comPort, boolean serialPortShared) {
        Shell activeShell = Display.getDefault().getActiveShell();
        AtomicReference<ComPort> port = new AtomicReference<>(comPort);

        ProgressMonitorDialog dlg = new ProgressMonitorDialog(activeShell);

        IRunnableWithProgress thread = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                int flags;
                PropellerLoader loader;

                try {
                    String text = "Upload";
                    if (comPort != null) {
                        text += " to " + comPort.getDescription();
                    }
                    monitor.beginTask(text, IProgressMonitor.UNKNOWN);

                    if (obj instanceof Spin1Object) {
                        loader = new Propeller1Loader(comPort, preferences.getP1ResetControl(), serialPortShared) {

                            @Override
                            protected void bufferUpload(int type, byte[] binaryImage, String text) throws ComPortException {
                                monitor.setTaskName("Upload to " + comPort.getDescription());
                                super.bufferUpload(type, binaryImage, text);
                            }

                        };
                        flags = writeToFlash ? Propeller1Loader.DOWNLOAD_RUN_EEPROM : Propeller1Loader.DOWNLOAD_RUN_BINARY;
                    }
                    else {
                        loader = new Propeller2Loader(comPort, preferences.getP2ResetControl(), serialPortShared) {

                            @Override
                            protected void bufferUpload(int type, byte[] binaryImage, String text) throws ComPortException {
                                monitor.setTaskName("Upload to " + comPort.getDescription());
                                super.bufferUpload(type, binaryImage, text);
                            }

                        };
                        flags = writeToFlash ? Propeller2Loader.DOWNLOAD_RUN_FLASH : Propeller2Loader.DOWNLOAD_RUN_RAM;
                    }

                    loader.setListener(new PropellerLoaderListener() {

                        @Override
                        public void bufferUpload(int type, byte[] binaryImage, String text) {
                            monitor.subTask("Loading " + text + " to RAM");
                        }

                        @Override
                        public void verifyRam() {
                            monitor.subTask("Verifying RAM ... ");
                        }

                        @Override
                        public void eepromWrite() {
                            monitor.subTask("Writing EEPROM ... ");
                        }

                        @Override
                        public void eepromVerify() {
                            monitor.subTask("Verifying EEPROM ... ");
                        }

                    });

                    loader.setBlacklistedPorts(preferences.getBlacklistedPorts());

                    Display.getDefault().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            dlg.open();
                        }
                    });

                    byte[] image = obj.getBinary();
                    port.set(loader.upload(image, flags, preferences.isAutoDiscoverDevice()));

                } catch (Exception e) {
                    e.printStackTrace();
                    Display.getDefault().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            StringBuilder sb = new StringBuilder();
                            if (comPort != null) {
                                sb.append(comPort.getDescription());
                                sb.append(System.lineSeparator());
                                sb.append(System.lineSeparator());
                            }
                            sb.append(e.getMessage());
                            MessageDialog.openError(activeShell, APP_TITLE, sb.toString());
                        }
                    });
                }

                monitor.done();
            }

        };

        try {
            dlg.setOpenOnRun(false);
            dlg.run(true, true, thread);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return port.get();
    }

    private void handleListDevices() {
        ComPort selectedPort = null;
        ComPort currentPort = consoleView.getSerialPort();

        SerialTerminal serialTerminal = getSerialTerminal();
        if (serialTerminal != null) {
            if (serialTerminal.getSerialPort() != null) {
                currentPort = serialTerminal.getSerialPort();
            }
            serialTerminal.setSerialPort(null);
        }
        consoleView.setSerialPort(null);

        DevicesDialog dlg = new DevicesDialog(shell);
        dlg.setSelection(currentPort);
        if (dlg.open() == DevicesDialog.OK) {
            selectedPort = dlg.getSelection();
        }

        if (selectedPort == null) {
            selectedPort = currentPort;
        }

        if (selectedPort != null) {
            if (currentPort != null && currentPort != selectedPort) {
                if (currentPort.isOpened()) {
                    currentPort.closePort();
                }
            }
            if (consoleView.getVisible()) {
                consoleView.setSerialPort(selectedPort);
            }
            else if (serialTerminal != null) {
                serialTerminal.setSerialPort(selectedPort);
            }

            comPortList.setSelection(selectedPort);
            preferences.setPort(selectedPort.getPortName());
            statusLine.setPortText(selectedPort.getName());
            statusLine.setPortToolTipText(selectedPort.getDescription());
        }
    }

    private void handleFirmwarePack() {
        FirmwarePackDialog dlg = new FirmwarePackDialog(shell, preferences);
        dlg.open();
    }

    void createHelpMenu(Menu parent) {
        MenuItem item;
        Menu menu = new Menu(parent.getParent(), SWT.DROP_DOWN);

        final Menu propeller1Menu = new Menu(parent.getParent(), SWT.DROP_DOWN);
        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Propeller 1...");
        item.setMenu(propeller1Menu);

        item = new MenuItem(propeller1Menu, SWT.PUSH);
        item.setText("Documentation...");
        item.addListener(SWT.Selection, e -> Program.launch("https://www.parallax.com/download/propeller-1-documentation/"));

        item = new MenuItem(propeller1Menu, SWT.PUSH);
        item.setText("Forum...");
        item.addListener(SWT.Selection, e -> Program.launch("https://forums.parallax.com/categories/propeller-1-multicore-microcontroller"));

        final Menu propeller2Menu = new Menu(parent.getParent(), SWT.DROP_DOWN);
        item = new MenuItem(menu, SWT.CASCADE);
        item.setText("Propeller 2...");
        item.setMenu(propeller2Menu);

        item = new MenuItem(propeller2Menu, SWT.PUSH);
        item.setText("Documentation...");
        item.addListener(SWT.Selection, e -> Program.launch("https://www.parallax.com/propeller-2/documentation/"));

        item = new MenuItem(propeller2Menu, SWT.PUSH);
        item.setText("Forum...");
        item.addListener(SWT.Selection, e -> Program.launch("https://forums.parallax.com/categories/propeller-2-multicore-microcontroller"));

        new MenuItem(propeller2Menu, SWT.SEPARATOR);

        item = new MenuItem(propeller2Menu, SWT.PUSH);
        item.setText("IRQsoft Propeller 2 Docs...");
        item.addListener(SWT.Selection, e -> Program.launch("https://p2docs.github.io/"));

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("OBEX Object Exchange...");
        item.addListener(SWT.Selection, e -> Program.launch("https://obex.parallax.com/obex/"));

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Parallax Website...");
        item.addListener(SWT.Selection, e -> Program.launch("https://parallax.com"));

        item = getSystemMenuItem(SWT.ID_ABOUT);
        if (item == null) {
            new MenuItem(menu, SWT.SEPARATOR);

            item = new MenuItem(menu, SWT.PUSH);
            item.setText("About " + APP_TITLE);
        }
        item.addListener(SWT.Selection, e -> {
            AboutDialog dlg = new AboutDialog(shell);
            dlg.setTheme(preferences.getTheme());
            dlg.open();
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
                            tabFolder.notifyListeners(SWT.Selection, new Event());
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

    public void runStartup(String[] args) {
        int tabIndex = 0;
        List<File> list = new ArrayList<>();

        if (preferences.getReloadOpenTabs()) {
            String[] openTabs = preferences.getOpenTabs();
            for (int i = 0; i < openTabs.length; i++) {
                File file = new File(openTabs[i]);
                if (!list.contains(file)) {
                    list.add(file);
                }
            }

            File topObjectFile = preferences.getTopObject();
            if (topObjectFile != null) {
                list.remove(topObjectFile);
                list.add(0, topObjectFile);
            }
        }

        for (int i = 0; i < args.length; i++) {
            File file = new File(args[i]).getAbsoluteFile();
            if (FileUtils.isEditable(file) && !list.contains(file)) {
                list.add(file);
            }
            if (i == 0) {
                tabIndex = list.indexOf(file);
            }
        }

        for (File fileToOpen : list) {
            try {
                String text = FileUtils.loadFromFile(fileToOpen);

                Display.getDefault().syncExec(() -> {
                    EditorTab editorTab = new EditorTab(tabFolder, fileToOpen, sourcePool);
                    hookListeners(editorTab);

                    editorTab.setEditorText(text);

                    LruData lruData = preferences.getLruData(fileToOpen);
                    if (lruData != null) {
                        StyledText styledText = editorTab.getEditor().getStyledText();
                        styledText.setCaretOffset(lruData.caretPosition);
                        if (lruData.topIndex != 0) {
                            styledText.setTopIndex(lruData.topIndex);
                        }
                    }
                });
            } catch (Exception e) {
                Display.getDefault().syncExec(() -> {
                    MessageDialog.openError(shell, APP_TITLE, "Can't reopen top object " + fileToOpen.getAbsolutePath());
                    preferences.setTopObject(null);
                });
            }
        }

        final File selection = !list.isEmpty() ? list.get(tabIndex) : null;

        Display.getDefault().asyncExec(() -> {
            if (tabFolder.isDisposed()) {
                return;
            }
            fileBrowser.setVisiblePaths(preferences.getRoots());
            fileBrowser.setExpandedPaths(preferences.getExpandedPaths());
            if (selection != null) {
                for (CTabItem tabItem : tabFolder.getItems()) {
                    EditorTab editorTab = (EditorTab) tabItem.getData();
                    File localFile = editorTab.getFile() != null ? editorTab.getFile() : new File(editorTab.getText());
                    if (localFile.equals(selection)) {
                        tabFolder.setSelection(tabItem);
                        tabFolder.notifyListeners(SWT.Selection, new Event());
                        editorTab.setFocus();
                    }
                }
            }
            File lastPath = preferences.getLastPath();
            if (lastPath != null) {
                fileBrowser.setSelection(lastPath);
            }
        });
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
        tabFolder.notifyListeners(SWT.Selection, new Event());
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
        tabFolder.notifyListeners(SWT.Selection, new Event());
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
                case SWT.NO:
                    break;
                default:
                    return false;
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

        updateBookmarksMenu();
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

    void updateBookmarksMenu() {
        CTabItem tabItem = tabFolder.getSelection();
        if (tabItem != null) {
            EditorTab editorTab = (EditorTab) tabItem.getData();
            updateBookmarksMenu(editorTab.getEditor().getRuler().getBookmarks());
        }
    }

    void updateBookmarksMenu(Integer[] bookmarks) {
        MenuItem[] items = bookmarksMenu.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i].setEnabled(false);
        }

        if (bookmarks != null) {
            int i = 0;
            while (i < items.length && i < bookmarks.length) {
                items[i].setEnabled(bookmarks[i] != null);
                i++;
            }
        }
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
        Display.setAppName("cocoa".equals(SWT.getPlatform()) ? APP_TITLE : "maccasoft-spintoolside");
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

                    SpinTools app = new SpinTools(shell);
                    BusyIndicator.showWhile(display, () -> app.runStartup(args), true);

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

    static boolean internalErrorRunning;

    public static void openInternalError(Shell shell, String message, Throwable details) {
        if (internalErrorRunning) {
            return;
        }
        internalErrorRunning = true;
        try {
            details.printStackTrace();
            InternalErrorDialog dlg = new InternalErrorDialog(shell, APP_TITLE, null, "An unexpected error has occured.", details, MessageDialog.ERROR, new String[] {
                IDialogConstants.OK_LABEL, IDialogConstants.SHOW_DETAILS_LABEL
            }, 0);
            dlg.setDetailButton(1);
            dlg.open();
        } finally {
            internalErrorRunning = false;
        }
    }

}
