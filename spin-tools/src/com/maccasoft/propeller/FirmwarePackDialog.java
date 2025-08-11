/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Platform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.maccasoft.propeller.Preferences.PackageFile;
import com.maccasoft.propeller.internal.BusyIndicator;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ImageRegistry;

public class FirmwarePackDialog {

    public static final String WINDOW_TITLE = "Firmware Package";

    private static final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
    private static final int VERTICAL_DIALOG_UNITS_PER_CHAR = 8;

    public static final String[] filterNames = new String[] {
        "Firmware Packs",
    };
    public static final String[] filterExtensions = new String[] {
        "*.json",
    };

    static final String BUNDLE_PREFIX_NAME = "propeller-firmware-loader";
    static final String FIRMWARE_ENTRY_NAME = "propeller-firmware-loader/firmware.json";

    Shell parentShell;
    Display display;
    Shell shell;
    FontMetrics fontMetrics;

    ComboViewer packFile;
    Button browseFile;

    TableViewer viewer;
    Button add;
    Button edit;
    Button remove;
    Button moveUp;
    Button moveDown;

    Button enableLocal;
    Button enableNetwork;
    Button[] enableBundle;
    Button exportBundleButton;

    Button saveButton;
    Button saveAsButton;

    Preferences preferences;

    String appDir;
    FirmwarePack pack;

    PackageFile lruFile;
    Object selection;
    boolean dirty;

    final ISelectionChangedListener packFileSelectionListener = new ISelectionChangedListener() {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            if (!handleUnsavedContent()) {
                try {
                    packFile.removeSelectionChangedListener(packFileSelectionListener);
                    if (lruFile != null) {
                        packFile.setSelection(new StructuredSelection(lruFile), true);
                    }
                } finally {
                    packFile.addSelectionChangedListener(packFileSelectionListener);
                }
                return;
            }
            lruFile = (PackageFile) event.getStructuredSelection().getFirstElement();
            if (lruFile != null) {
                File fileToLoad = lruFile.getFile();
                if (!fileToLoad.exists()) {
                    MessageDialog.openError(shell, shell.getText(), "File " + fileToLoad + " not found");
                    preferences.getPackageLru().remove(lruFile);
                    packFile.refresh();

                    fileToLoad = handleBrowseFirmwarePack(fileToLoad.getAbsoluteFile().getParent());
                    if (fileToLoad == null) {
                        return;
                    }
                }
                loadFromFile(fileToLoad);
            }
        }

    };

    public FirmwarePackDialog(Shell parentShell, Preferences preferences) {
        if (parentShell == null) {
            parentShell = new Shell(Display.getDefault());

        }
        this.parentShell = parentShell;
        this.display = parentShell.getDisplay();
        this.preferences = preferences;

        this.pack = new FirmwarePack();

        this.appDir = System.getenv("APP_DIR");
        if (this.appDir == null) {
            this.appDir = new File("").getAbsolutePath();
        }
    }

    public void open() {
        open(null);
    }

    public void open(File file) {
        if (shell == null) {
            create();
        }

        if (file != null) {
            loadFromFile(file);
        }

        shell.pack();

        Rectangle parentRect = parentShell.getBounds();
        Rectangle rect = shell.getBounds();
        rect.x = parentRect.x + (parentRect.width - rect.width) / 2;
        rect.y = parentRect.y + (parentRect.height - rect.height) / 2;
        shell.setLocation(rect.x, rect.y);

        shell.open();

        shell.addListener(SWT.Close, new Listener() {

            @Override
            public void handleEvent(Event event) {
                event.doit = handleUnsavedContent();
            }
        });

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        if (!display.isDisposed()) {
            display.update();
        }
    }

    void create() {
        shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText(WINDOW_TITLE);
        shell.setData(this);

        FillLayout layout = new FillLayout();
        layout.marginWidth = layout.marginHeight = 0;
        shell.setLayout(layout);

        GC gc = new GC(shell);
        try {
            fontMetrics = gc.getFontMetrics();
        } finally {
            gc.dispose();
        }

        createContents(shell);
    }

    protected void createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = convertVerticalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        container.setLayout(layout);
        container.setBackgroundMode(SWT.INHERIT_DEFAULT);

        createFileSelectionGroup(container);
        createFirmwareListGroup(container);
        createSettingsGroup(container);
        createBottomControls(container);

        if ("win32".equals(Platform.PLATFORM) || preferences.getTheme() != null) {
            applyTheme(parent, preferences.getTheme());
        }

        updateControls();
    }

    void createFileSelectionGroup(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        GridData containerGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        container.setLayoutData(containerGridData);

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        label.setText("Package File");

        Composite group = new Composite(container, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        GridData groupLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
        group.setLayoutData(groupLayoutData);

        packFile = new ComboViewer(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        packFile.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        packFile.setContentProvider(new IStructuredContentProvider() {

            @Override
            @SuppressWarnings({
                "rawtypes", "unchecked"
            })
            public Object[] getElements(Object inputElement) {
                List l = new ArrayList((Collection) inputElement);
                if (lruFile != null && !l.contains(lruFile)) {
                    l.add(0, lruFile);
                }
                return l.toArray();
            }

        });
        packFile.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                return ((PackageFile) element).getFile().toString();
            }

        });
        packFile.setInput(preferences.getPackageLru());
        packFile.addSelectionChangedListener(packFileSelectionListener);

        browseFile = new Button(group, SWT.PUSH | SWT.FLAT);
        browseFile.setImage(ImageRegistry.getImageFromResources("folder-horizontal-open.png"));
        browseFile.setToolTipText("Browse files");
        browseFile.setLayoutData(new GridData(convertHorizontalDLUsToPixels(18), convertHorizontalDLUsToPixels(18)));
        browseFile.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                PackageFile lruFile = (PackageFile) packFile.getStructuredSelection().getFirstElement();
                File fileToLoad = handleBrowseFirmwarePack(lruFile != null ? lruFile.getFile().getAbsolutePath() : "");
                if (fileToLoad != null) {
                    loadFromFile(fileToLoad);
                    dirty = false;
                    updateControls();
                }
            }

        });
    }

    public void loadFromFile(File file) {
        lruFile = new PackageFile(file);

        if (file.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                pack = mapper.readValue(file, FirmwarePack.class);

                viewer.setInput(pack.getFirmwareList());
                enableLocal.setSelection(pack.isEnableLocal());
                enableNetwork.setSelection(pack.isEnableNetwork());

            } catch (Exception e) {
                // Do nothing
                e.printStackTrace();
            }

            for (PackageFile pf : preferences.getPackageLru()) {
                if (pf.getFile().equals(file)) {
                    for (int i = 0; i < enableBundle.length; i++) {
                        String id = (String) enableBundle[i].getData("id");
                        enableBundle[i].setSelection(pf.getBundleEnabled(id));
                    }
                    lruFile = pf;
                    break;
                }
            }

            preferences.addToPackageLru(lruFile);
        }

        try {
            packFile.removeSelectionChangedListener(packFileSelectionListener);
            packFile.refresh();
            if (lruFile != null) {
                packFile.setSelection(new StructuredSelection(lruFile), true);
            }
        } finally {
            packFile.addSelectionChangedListener(packFileSelectionListener);
        }

        dirty = false;
        updateControls();
    }

    public void addFirmare(Firmware firmware) {
        pack.getFirmwareList().add(0, firmware);

        viewer.refresh();
        viewer.setSelection(new StructuredSelection(firmware));

        dirty = true;
        updateControls();
    }

    File handleBrowseFirmwarePack(String filterPath) {
        FileDialog dlg = new FileDialog(shell, SWT.OPEN);
        dlg.setText("Open Firmware Pack");
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);
        dlg.setFilterIndex(0);

        if (filterPath != null && !filterPath.isBlank()) {
            File file = new File(filterPath).getAbsoluteFile().getParentFile();
            if (file != null) {
                filterPath = file.getAbsolutePath();
            }
        }
        if (filterPath == null || filterPath.isBlank()) {
            filterPath = appDir;
        }
        if (filterPath != null && !filterPath.isBlank()) {
            dlg.setFilterPath(filterPath);
        }

        String fileName = dlg.open();
        return fileName != null ? new File(fileName).getAbsoluteFile() : null;
    }

    void createFirmwareListGroup(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        GridData containerGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        container.setLayoutData(containerGridData);

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
        label.setText("Firmware List");

        viewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
        viewer.getTable().setHeaderVisible(true);

        TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        viewerColumn.getColumn().setText("Description");
        viewerColumn.getColumn().setWidth(convertWidthInCharsToPixels(80));
        viewerColumn.setLabelProvider(new StyledCellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                Firmware firmware = (Firmware) cell.getElement();
                cell.setText(firmware.getDescription());
            }

        });
        viewerColumn.setEditingSupport(new EditingSupport(viewer) {

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(viewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                return element == selection;
            }

            @Override
            protected Object getValue(Object element) {
                Firmware firmware = (Firmware) element;
                return firmware.getDescription();
            }

            @Override
            protected void setValue(Object element, Object value) {
                Firmware firmware = (Firmware) element;
                String description = value != null ? value.toString() : "";
                if (!description.equals(firmware.getDescription())) {
                    dirty = true;
                }
                firmware.setDescription(description);
                viewer.update(element, null);
                updateControls();
            }

        });

        viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        viewerColumn.getColumn().setText("Type");
        viewerColumn.getColumn().setWidth(convertWidthInCharsToPixels(10));
        viewerColumn.setLabelProvider(new StyledCellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                Firmware firmware = (Firmware) cell.getElement();
                cell.setText(firmware.getBinaryVersion() == 1 ? "P1" : "P2");
            }

        });

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(pack.getFirmwareList());

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selection = event.getStructuredSelection().getFirstElement();
                updateControls();
            }

        });

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = viewer.getTable().getItemHeight() * 10;
        viewer.getControl().setLayoutData(gridData);

        Composite group = new Composite(container, SWT.NONE);
        layout = new GridLayout(1, true);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        add = createPageButton(group, ImageRegistry.getImageFromResources("add.png"), "Add");
        add.addSelectionListener(new SelectionAdapter() {

            public static final String[] filterNames = new String[] {
                "Firmware Files",
            };
            public static final String[] filterExtensions = new String[] {
                "*.binary;*.bin",
            };

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dlg = new FileDialog(shell, SWT.OPEN);
                dlg.setFilterNames(filterNames);
                dlg.setFilterExtensions(filterExtensions);
                dlg.setFilterIndex(0);

                String filterPath = browseFile.getText();
                if (!filterPath.isBlank()) {
                    File file = new File(filterPath).getAbsoluteFile().getParentFile();
                    if (file != null) {
                        filterPath = file.getAbsolutePath();
                    }
                }
                if (filterPath == null) {
                    filterPath = appDir;
                }
                if (filterPath != null) {
                    dlg.setFilterPath(filterPath);
                }

                String fileName = dlg.open();
                if (fileName != null) {
                    try {
                        File file = new File(fileName);
                        Firmware firmware = Firmware.fromFile(file);
                        firmware.setDescription(file.getName() + " - " + firmware.getDescription());
                        addFirmare(firmware);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });

        remove = createPageButton(group, ImageRegistry.getImageFromResources("delete.png"), "Remove");
        remove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = viewer.getStructuredSelection();
                if (!selection.isEmpty()) {
                    pack.removeFirmware((Firmware) selection.getFirstElement());
                    viewer.refresh(true, true);
                    dirty = true;
                    updateControls();
                }
            }

        });

        moveUp = createPageButton(group, ImageRegistry.getImageFromResources("arrow_up.png"), "Move Up");
        moveUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                List<Firmware> list = pack.getFirmwareList();
                IStructuredSelection selection = viewer.getStructuredSelection();
                if (!selection.isEmpty()) {
                    int index = list.indexOf(selection.getFirstElement());
                    if (index != -1 && index > 0) {
                        list.remove(index);
                        list.add(index - 1, (Firmware) selection.getFirstElement());
                        viewer.refresh(true, true);
                        dirty = true;
                        updateControls();
                    }
                }
            }

        });

        moveDown = createPageButton(group, ImageRegistry.getImageFromResources("arrow_down.png"), "Move Down");
        moveDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                List<Firmware> list = pack.getFirmwareList();
                IStructuredSelection selection = viewer.getStructuredSelection();
                if (!selection.isEmpty()) {
                    int index = list.indexOf(selection.getFirstElement());
                    if (index != -1 && index < list.size() - 1) {
                        list.add(index + 2, (Firmware) selection.getFirstElement());
                        list.remove(index);
                        viewer.refresh(true, true);
                        dirty = true;
                        updateControls();
                    }
                }
            }

        });
    }

    void createSettingsGroup(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        container.setLayout(layout);
        GridData containerGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        container.setLayoutData(containerGridData);

        Label label = new Label(container, SWT.NONE);
        label.setText("Discover settings");
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        Composite group = new Composite(container, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        enableLocal = createRadioOrCheckButton(group, "Local", SWT.CHECK);
        enableLocal.setSelection(pack.isEnableLocal());
        enableLocal.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                pack.setEnableLocal(((Button) event.widget).getSelection());
                dirty = true;
                updateControls();
            }

        });
        enableNetwork = createRadioOrCheckButton(group, "Network", SWT.CHECK);
        enableNetwork.setSelection(pack.isEnableNetwork());
        enableNetwork.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                pack.setEnableNetwork(((Button) event.widget).getSelection());
                dirty = true;
                updateControls();
            }

        });

        label = new Label(container, SWT.NONE);
        label.setText("Loader bundles");
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        group = new Composite(container, SWT.NONE);
        layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        int i = 0;
        enableBundle = new Button[5];

        SelectionAdapter listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (lruFile != null) {
                    String id = (String) e.widget.getData("id");
                    lruFile.setBundleEnabled(id, ((Button) e.widget).getSelection());
                    updateControls();
                }
            }

        };

        enableBundle[i] = createRadioOrCheckButton(group, "Linux (x86_64)", SWT.CHECK);
        enableBundle[i].setData("id", "linux-x86_64");
        enableBundle[i].setData("suffix", "-linux-x86_64.tar.gz");
        enableBundle[i].setEnabled(bundleExists((String) enableBundle[i].getData("suffix")));
        enableBundle[i].addSelectionListener(listener);
        i++;

        enableBundle[i] = createRadioOrCheckButton(group, "Linux (aarch64)", SWT.CHECK);
        enableBundle[i].setData("id", "linux-aarch64");
        enableBundle[i].setData("suffix", "-linux-aarch64.tar.gz");
        enableBundle[i].setEnabled(bundleExists((String) enableBundle[i].getData("suffix")));
        enableBundle[i].addSelectionListener(listener);
        i++;

        enableBundle[i] = createRadioOrCheckButton(group, "Windows (x86_64)", SWT.CHECK);
        enableBundle[i].setData("id", "windows-x86_64");
        enableBundle[i].setData("suffix", "-windows-x86_64.zip");
        enableBundle[i].setEnabled(bundleExists((String) enableBundle[i].getData("suffix")));
        enableBundle[i].addSelectionListener(listener);
        i++;

        enableBundle[i] = createRadioOrCheckButton(group, "MacOS (x86_64)", SWT.CHECK);
        enableBundle[i].setData("id", "macos-x86_64");
        enableBundle[i].setData("suffix", "-macos-x86_64.tar.gz");
        enableBundle[i].setEnabled(bundleExists((String) enableBundle[i].getData("suffix")));
        enableBundle[i].addSelectionListener(listener);
        i++;

        enableBundle[i] = createRadioOrCheckButton(group, "MacOS (aarch64)", SWT.CHECK);
        enableBundle[i].setData("id", "macos-aarch64");
        enableBundle[i].setData("suffix", "-macos-aarch64.tar.gz");
        enableBundle[i].setEnabled(bundleExists((String) enableBundle[i].getData("suffix")));
        enableBundle[i].addSelectionListener(listener);
        i++;

        exportBundleButton = createButton(container, "Export");
        exportBundleButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                BusyIndicator.showWhile(display, new Runnable() {

                    @Override
                    public void run() {
                        doSaveBundle();
                    }

                });
            }
        });
    }

    boolean bundleExists(String suffix) {
        File bundlesPath = new File(appDir, "loaders");
        File[] files = bundlesPath.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(BUNDLE_PREFIX_NAME) && name.endsWith(suffix);
            }

        });
        return files != null && files.length != 0;
    }

    Button createPageButton(Composite parent, Image image, String toolTipText) {
        Button button = new Button(parent, SWT.PUSH);
        button.setImage(image);
        button.setToolTipText(toolTipText);
        button.setLayoutData(new GridData(convertHorizontalDLUsToPixels(18), convertHorizontalDLUsToPixels(18)));
        return button;
    }

    void createBottomControls(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button button = createButton(container, "New");
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!handleUnsavedContent()) {
                    return;
                }

                packFile.setSelection(StructuredSelection.EMPTY);
                lruFile = null;

                pack = new FirmwarePack();
                viewer.setInput(pack.getFirmwareList());
                enableLocal.setSelection(pack.isEnableLocal());
                enableNetwork.setSelection(pack.isEnableNetwork());

                dirty = false;
                updateControls();
            }
        });

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        saveAsButton = createButton(container, "Save As...");
        saveAsButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                doSaveAs();
            }
        });

        saveButton = createButton(container, "Save");
        saveButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                doSave();
            }
        });

        GridLayout layout = new GridLayout(container.getChildren().length, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
    }

    Button createButton(Composite parent, String text) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(text);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = Math.max(Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.BUTTON_WIDTH), button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        return button;
    }

    protected boolean handleUnsavedContent() {
        if (dirty) {
            int style = SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
            MessageBox messageBox = new MessageBox(shell, style);
            messageBox.setText(shell.getText());
            messageBox.setMessage("Package contains unsaved changes.  Save before closing?");
            switch (messageBox.open()) {
                case SWT.YES:
                    try {
                        doSave();
                        if (dirty) {
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case SWT.NO:
                    return true;
                default:
                    return false;
            }
        }
        return true;
    }

    protected void doSave() {
        File fileToSave = null;

        if (lruFile != null) {
            fileToSave = lruFile.getFile();
        }
        if (fileToSave == null) {
            fileToSave = getFileToWrite(true);
        }
        if (fileToSave == null) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            mapper.setSerializationInclusion(Include.NON_DEFAULT);
            mapper.writeValue(fileToSave, pack);

            if (lruFile == null) {
                lruFile = new PackageFile(fileToSave);
            }
            else if (!fileToSave.equals(lruFile.getFile())) {
                lruFile = new PackageFile(fileToSave, lruFile.getBundles());
            }
            preferences.addToPackageLru(lruFile);

            try {
                packFile.removeSelectionChangedListener(packFileSelectionListener);
                packFile.refresh();
                packFile.setSelection(new StructuredSelection(lruFile), true);
            } finally {
                packFile.addSelectionChangedListener(packFileSelectionListener);
            }

            dirty = false;
            updateControls();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doSaveAs() {
        File fileToSave = getFileToWrite(true);
        if (fileToSave == null) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            mapper.setSerializationInclusion(Include.NON_DEFAULT);
            mapper.writeValue(fileToSave, pack);

            if (lruFile == null) {
                lruFile = new PackageFile(fileToSave);
            }
            else if (!fileToSave.equals(lruFile.getFile())) {
                lruFile = new PackageFile(fileToSave, lruFile.getBundles());
            }
            preferences.addToPackageLru(lruFile);

            try {
                packFile.removeSelectionChangedListener(packFileSelectionListener);
                packFile.refresh();
                packFile.setSelection(new StructuredSelection(lruFile), true);
            } finally {
                packFile.addSelectionChangedListener(packFileSelectionListener);
            }

            dirty = false;
            updateControls();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doSaveBundle() {
        File packageFile = lruFile.getFile();
        File outDir = packageFile.getParentFile().getAbsoluteFile();

        for (int i = 0; i < enableBundle.length; i++) {
            if (enableBundle[i].getSelection()) {
                try {
                    exportBundle((String) enableBundle[i].getData("suffix"), outDir, pack);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    File getFileToWrite(boolean overwrite) {
        String filterPath = null;

        FileDialog dlg = new FileDialog(shell, SWT.OPEN | SWT.SAVE);
        dlg.setText("Open Firmware File");
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);
        dlg.setFilterIndex(0);
        dlg.setOverwrite(overwrite);

        if (lruFile != null) {
            filterPath = lruFile.getFile().getParentFile().getAbsolutePath();
            dlg.setFileName(lruFile.getFile().getName());
        }
        if (filterPath == null) {
            filterPath = appDir;
        }
        if (filterPath != null) {
            dlg.setFilterPath(filterPath);
        }

        String fileName = dlg.open();
        if (fileName != null) {
            return new File(fileName);
        }

        return null;
    }

    void updateControls() {
        int index = -1;

        IStructuredSelection selection = viewer.getStructuredSelection();
        if (!selection.isEmpty()) {
            index = pack.getFirmwareList().indexOf(selection.getFirstElement());
        }

        remove.setEnabled(index != -1);
        if (moveUp != null) {
            moveUp.setEnabled(index != -1 && index > 0);
        }
        if (moveDown != null) {
            moveDown.setEnabled(index != -1 && index < (pack.getFirmwareList().size() - 1));
        }

        exportBundleButton.setEnabled(pack.getFirmwareList().size() != 0 && lruFile != null);

        saveButton.setEnabled(pack.getFirmwareList().size() != 0 && lruFile != null);
        saveAsButton.setEnabled(pack.getFirmwareList().size() != 0 && lruFile != null);
    }

    protected int convertHorizontalDLUsToPixels(int dlus) {
        if (fontMetrics == null) {
            return 0;
        }
        return (int) ((fontMetrics.getAverageCharacterWidth() * dlus + HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2) / HORIZONTAL_DIALOG_UNIT_PER_CHAR);
    }

    protected int convertVerticalDLUsToPixels(int dlus) {
        if (fontMetrics == null) {
            return 0;
        }
        return (fontMetrics.getHeight() * dlus + VERTICAL_DIALOG_UNITS_PER_CHAR / 2) / VERTICAL_DIALOG_UNITS_PER_CHAR;
    }

    protected int convertWidthInCharsToPixels(int chars) {
        if (fontMetrics == null) {
            return 0;
        }
        return (int) (fontMetrics.getAverageCharacterWidth() * chars);
    }

    protected int convertHeightInCharsToPixels(int chars) {
        if (fontMetrics == null) {
            return 0;
        }
        return fontMetrics.getHeight() * chars;
    }

    Color widgetForeground;
    Color widgetBackground;
    Color listForeground;
    Color listBackground;
    Color labelForeground;
    Color buttonBackground;

    void applyTheme(Control control, String id) {
        widgetForeground = null;
        widgetBackground = null;
        listForeground = null;
        listBackground = null;
        labelForeground = null;
        buttonBackground = null;

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
        }
        else if ("dark".equals(id)) {
            widgetForeground = new Color(0xF0, 0xF0, 0xF0);
            widgetBackground = new Color(0x50, 0x55, 0x57);
            listForeground = new Color(0xA7, 0xA7, 0xA7);
            listBackground = new Color(0x2B, 0x2B, 0x2B);
            labelForeground = new Color(0xD7, 0xD7, 0xD7);
            buttonBackground = new Color(0x50, 0x55, 0x57);
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
            labelForeground = new Color(0x00, 0x00, 0x00);
            buttonBackground = new Color(0xFA, 0xFA, 0xFA);
        }

        applyTheme(control);
    }

    void applyTheme(Control control) {
        if (control instanceof List || control instanceof Table || control instanceof Tree) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
            if (control instanceof Table) {
                ((Table) control).setHeaderForeground(widgetForeground);
                ((Table) control).setHeaderBackground(widgetBackground);
            }
        }
        else if (control instanceof Button) {
            control.setBackground(buttonBackground);
        }
        else if (control instanceof Text) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Spinner) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Combo) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Label) {
            control.setForeground(widgetForeground);
        }
        else if (control instanceof Composite) {
            control.setBackground(widgetBackground);
            Control[] children = ((Composite) control).getChildren();
            for (int i = 0; i < children.length; i++) {
                applyTheme(children[i]);
            }
        }
    }

    void exportBundle(String suffix, File outDir, FirmwarePack pack) throws StreamWriteException, DatabindException, IOException {
        byte[] data = getBinary(pack);

        File bundlesPath = new File(appDir, "loaders");
        File[] files = bundlesPath.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(BUNDLE_PREFIX_NAME) && name.endsWith(suffix);
            }

        });
        if (files.length == 0) {
            return;
        }
        File inFile = files[0];

        File outFile = new File(outDir, inFile.getName());
        if (outFile.exists()) {
            outFile.delete();
        }

        if (inFile.getName().endsWith(".zip")) {
            exportZipBundle(inFile, outFile, data);
        }
        else {
            exportTarBundle(inFile, outFile, data);
        }
    }

    void exportTarBundle(File inFile, File outFile, byte[] data) throws StreamWriteException, DatabindException, IOException {
        if (outFile.exists()) {
            outFile.delete();
        }

        TarArchiveInputStream is = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(inFile)));
        TarArchiveOutputStream os = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(outFile)));
        os.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

        is.forEach((action) -> {
            os.putArchiveEntry(action);
            IOUtils.copy(is, os);
            os.closeArchiveEntry();
        });

        TarArchiveEntry entry = new TarArchiveEntry(FIRMWARE_ENTRY_NAME);
        entry.setModTime(new Date());
        entry.setSize(data.length);

        os.putArchiveEntry(entry);
        os.write(data);
        os.closeArchiveEntry();

        os.close();
        is.close();
    }

    void exportZipBundle(File inFile, File outFile, byte[] data) throws StreamWriteException, DatabindException, IOException {
        if (outFile.exists()) {
            outFile.delete();
        }

        ZipArchiveInputStream is = new ZipArchiveInputStream(new FileInputStream(inFile));
        ZipArchiveOutputStream os = new ZipArchiveOutputStream(new FileOutputStream(outFile));

        is.forEach((action) -> {
            os.putArchiveEntry(action);
            IOUtils.copy(is, os);
            os.closeArchiveEntry();
        });

        ZipArchiveEntry entry = new ZipArchiveEntry(FIRMWARE_ENTRY_NAME);
        entry.setLastModifiedTime(FileTime.from(Instant.now()));
        entry.setSize(data.length);

        os.putArchiveEntry(entry);
        os.write(data);
        os.closeArchiveEntry();

        os.close();
        is.close();
    }

    byte[] getBinary(FirmwarePack pack) throws StreamWriteException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mapper.writeValue(os, pack);
        os.close();

        return os.toByteArray();
    }

    Button createRadioOrCheckButton(Composite parent, String text, int style) {
        Button button = new Button(parent, style);
        button.setText(text);

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        String theme = System.getProperty("org.eclipse.swt.internal.gtk.theme");
        if (theme != null && theme.startsWith("Breeze")) {
            //gridData.horizontalIndent = -4;
            gridData.widthHint = button.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 8;
        }
        button.setLayoutData(gridData);

        return button;
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
                    Preferences preferences = new Preferences();
                    //preferences.addToPackageLru(new File("/home/marco/workspace/spin-tools-ide/spin-tools", "firmware.json"));
                    //preferences.addToPackageLru(new File("/home/marco/workspace/spin-tools-ide/spin-tools", "firmware-2025.01.25.json"));
                    //preferences.addToPackageLru(new File("/home/marco/workspace/propeller-firmware-loader", "firmware-pack.json"));

                    FirmwarePackDialog serialTerminal = new FirmwarePackDialog(null, preferences);
                    serialTerminal.open();

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        display.dispose();
    }

}
