/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.Platform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.maccasoft.propeller.Preferences.ExternalTool;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ImageRegistry;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.VariablesNode;

public class PreferencesDialog extends Dialog {

    List pages;
    Composite stack;
    StackLayout stackLayout;

    Combo theme;
    Button showBrowser;
    PathList roots;
    Button showToolbar;
    Button showObjectBrowser;

    Text editorFont;
    Spinner editorFontSize;
    Button editorFontBrowse;
    Button showLineNumbers;
    Button showIndentLines;
    Spinner indentLinesSize;
    Button showEditorOutline;
    TabStops conTabStops;
    TabStops varTabStops;
    TabStops objTabStops;
    TabStops pubTabStops;
    TabStops datTabStops;
    Button showSectionsBackground;

    PathList spin1Paths;
    Button spin1CaseSensitive;
    FileSelector spin1Template;

    PathList spin2Paths;
    Button spin2CaseSensitive;
    Button spin2ClockSetter;
    Button spin2Compress;
    FileSelector spin2Template;

    Text terminalFont;
    Spinner terminalFontSize;
    Button terminalFontBrowse;
    Button terminalLineInput;
    Button terminalLocalEcho;

    Text consoleFont;
    Spinner consoleFontSize;
    Button consoleFontBrowse;
    Spinner consoleMaxLines;
    Button consoleWriteLogFile;
    Button consoleResetDeviceOnClose;

    ToolsList externalTools;

    Preferences preferences;
    FontData defaultFont;
    Font fontBold;

    Color widgetForeground;
    Color widgetBackground;
    Color listForeground;
    Color listBackground;
    Color labelForeground;

    String oldTheme;
    boolean oldShowToolbar;
    boolean oldShowObjectBrowser;
    boolean oldShowBrowser;
    String oldEditorFont;
    boolean oldShowLineNumbers;
    boolean oldShowIndentLines;
    int oldIndentLinesSize;
    boolean oldShowSectionsBackground;
    boolean oldShowEditorOutline;
    boolean oldSpin1CaseSensitive;
    boolean oldSpin2CaseSensitive;
    boolean oldSpin2ClockSetter;
    boolean oldSpin2Compress;
    String oldTerminalFont;
    boolean oldTerminalLineInput;
    boolean oldTerminalLocalEcho;
    String oldConsoleFont;

    static int lastPage;

    class FileSelector {

        Composite group;
        Text text;
        Button browse;

        int filterIndex;

        public FileSelector(Composite parent) {
            group = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout(2, false);
            layout.marginWidth = layout.marginHeight = 0;
            group.setLayout(layout);

            text = new Text(group, SWT.BORDER);
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.widthHint = convertWidthInCharsToPixels(50);
            text.setLayoutData(gridData);

            browse = new Button(group, SWT.PUSH);
            browse.setImage(ImageRegistry.getImageFromResources("folder-horizontal-open.png"));
            browse.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    FileDialog dlg = new FileDialog(group.getShell(), SWT.OPEN);
                    dlg.setText("Open Source File");
                    dlg.setFilterNames(SpinTools.filterNames);
                    dlg.setFilterExtensions(SpinTools.filterExtensions);
                    dlg.setFilterIndex(filterIndex);
                    dlg.setFilterPath(new File(text.getText()).getParent());
                    String fileName = dlg.open();
                    if (fileName != null) {
                        text.setText(fileName);
                    }
                }

            });

            filterIndex = 0;
        }

        public void setFilterIndex(int index) {
            this.filterIndex = index;
        }

        public void setLayoutData(Object layoutData) {
            group.setLayoutData(layoutData);
        }

        public Object getLayoutData() {
            return group.getLayoutData();
        }

        public File getSelection() {
            String text = this.text.getText().trim();
            return !text.isEmpty() ? new File(text) : null;
        }

        public void setSelection(File text) {
            this.text.setText(text != null ? text.getPath() : "");
        }

    }

    class PathList {

        Composite group;

        List list;
        Button add;
        Button remove;
        Button moveUp;
        Button moveDown;

        public PathList(Composite parent, boolean allowMove) {
            group = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout(2, false);
            layout.marginWidth = layout.marginHeight = 0;
            group.setLayout(layout);

            list = new List(group, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.widthHint = convertWidthInCharsToPixels(50);
            gridData.heightHint = convertHeightInCharsToPixels(10) + list.getBorderWidth() * 2;
            list.setLayoutData(gridData);
            list.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateButtons();
                }
            });

            Composite container = new Composite(group, SWT.NONE);
            layout = new GridLayout(1, true);
            layout.marginWidth = layout.marginHeight = 0;
            container.setLayout(layout);
            container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

            add = new Button(container, SWT.PUSH);
            add.setImage(ImageRegistry.getImageFromResources("add.png"));
            add.setToolTipText("Add");
            add.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    DirectoryDialog dlg = new DirectoryDialog(parent.getShell());

                    int index = list.getSelectionIndex();
                    if (index != -1) {
                        dlg.setFilterPath(list.getItem(index));
                    }

                    String s = dlg.open();
                    if (s != null) {
                        list.add(s);
                        updateButtons();
                    }
                }

            });

            remove = new Button(container, SWT.PUSH);
            remove.setImage(ImageRegistry.getImageFromResources("delete.png"));
            remove.setToolTipText("Remove");
            remove.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    list.remove(list.getSelectionIndex());
                    updateButtons();
                }

            });
            remove.setEnabled(false);

            if (allowMove) {
                moveUp = new Button(container, SWT.PUSH);
                moveUp.setImage(ImageRegistry.getImageFromResources("arrow_up.png"));
                moveUp.setToolTipText("Up");
                moveUp.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        int index = list.getSelectionIndex();
                        ArrayList<String> items = new ArrayList<String>(Arrays.asList(list.getItems()));
                        String s = items.get(index);
                        items.remove(index);
                        items.add(index - 1, s);
                        list.setItems(items.toArray(new String[items.size()]));
                        list.setSelection(index - 1);
                        list.setFocus();
                        updateButtons();
                    }

                });
                moveUp.setEnabled(false);

                moveDown = new Button(container, SWT.PUSH);
                moveDown.setImage(ImageRegistry.getImageFromResources("arrow_down.png"));
                moveDown.setToolTipText("Down");
                moveDown.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        int index = list.getSelectionIndex();
                        ArrayList<String> items = new ArrayList<String>(Arrays.asList(list.getItems()));
                        String s = items.get(index);
                        items.add(index + 2, s);
                        items.remove(index);
                        list.setItems(items.toArray(new String[items.size()]));
                        list.setSelection(index + 1);
                        list.setFocus();
                        updateButtons();
                    }

                });
                moveDown.setEnabled(false);
            }
        }

        void updateButtons() {
            int index = list.getSelectionIndex();
            remove.setEnabled(index != -1);
            if (moveUp != null) {
                moveUp.setEnabled(index != -1 && index > 0);
            }
            if (moveDown != null) {
                moveDown.setEnabled(index != -1 && index < (list.getItemCount() - 1));
            }
        }

        public void setLayoutData(Object layoutData) {
            group.setLayoutData(layoutData);
        }

        public Object getLayoutData() {
            return group.getLayoutData();
        }

        public void setItems(String[] items) {
            if (moveUp != null && moveDown != null) {
                java.util.List<String> l = Arrays.asList(items);
                Collections.sort(l);
                items = l.toArray(new String[l.size()]);
            }
            list.setItems(items);
        }

        public String[] getItems() {
            return list.getItems();
        }

        public void setFileItems(File[] fileItems) {
            String[] items = new String[fileItems.length];
            for (int i = 0; i < items.length; i++) {
                items[i] = fileItems[i].getAbsolutePath();
            }
            list.setItems(items);
        }

        public File[] getFileItems() {
            String[] items = list.getItems();
            File[] fileItems = new File[items.length];
            for (int i = 0; i < fileItems.length; i++) {
                fileItems[i] = new File(items[i]);
            }
            return fileItems;
        }

    }

    PreferencesDialog(Shell parentShell, Preferences preferences) {
        super(parentShell);
        this.preferences = preferences;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Preferences");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if ("win32".equals(Platform.PLATFORM) || preferences.getTheme() != null) {
            applyTheme(parent, preferences.getTheme());
        }
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
        applyDialogFont(composite);

        Font textFont = JFaceResources.getTextFont();
        defaultFont = textFont.getFontData()[0];
        defaultFont.setStyle(SWT.NONE);

        FontData[] fontData = composite.getFont().getFontData();
        fontBold = new Font(composite.getDisplay(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
        composite.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                fontBold.dispose();
            }
        });

        pages = new List(composite, SWT.SIMPLE | SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true);
        gridData.widthHint = convertWidthInCharsToPixels(20);
        gridData.heightHint = convertHeightInCharsToPixels(10);
        pages.setLayoutData(gridData);

        stack = new Composite(composite, SWT.NONE);
        stackLayout = new StackLayout();
        stackLayout.marginHeight = stackLayout.marginWidth = 0;
        stack.setLayout(stackLayout);
        stack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createGeneralPage(stack);
        createConsolePage(stack);
        createEditorPage(stack);
        createSpin1CompilerPage(stack);
        createSpin2CompilerPage(stack);
        createTerminalPage(stack);
        createToolsPage(stack);

        stackLayout.topControl = stack.getChildren()[lastPage];

        pages.select(lastPage);
        pages.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                lastPage = pages.getSelectionIndex();
                stackLayout.topControl = stack.getChildren()[lastPage];
                stack.layout();
            }
        });

        oldTheme = preferences.getTheme();
        oldShowToolbar = preferences.getShowToolbar();
        oldShowObjectBrowser = preferences.getShowObjectBrowser();
        oldShowBrowser = preferences.getShowBrowser();
        oldEditorFont = preferences.getEditorFont();
        oldShowLineNumbers = preferences.getShowLineNumbers();
        oldShowIndentLines = preferences.getShowIndentLines();
        oldIndentLinesSize = preferences.getIndentLinesSize();
        oldShowEditorOutline = preferences.getShowEditorOutline();
        oldShowSectionsBackground = preferences.getShowSectionsBackground();
        oldSpin1CaseSensitive = preferences.getSpin1CaseSensitiveSymbols();
        oldSpin2CaseSensitive = preferences.getSpin2CaseSensitiveSymbols();
        oldSpin2ClockSetter = preferences.getSpin2ClockSetter();
        oldSpin2Compress = preferences.getSpin2Compress();
        oldTerminalFont = preferences.getTerminalFont();
        oldTerminalLineInput = preferences.getTerminalLineInput();
        oldTerminalLocalEcho = preferences.getTerminalLocalEcho();
        oldConsoleFont = preferences.getConsoleFont();

        return composite;
    }

    void createGeneralPage(Composite parent) {
        Composite composite = createPage(parent, "General");

        Label label = new Label(composite, SWT.NONE);
        label.setText("Theme");
        theme = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        theme.setItems("System", "Dark", "Light");
        if ("dark".equals(preferences.getTheme())) {
            theme.select(1);
        }
        else if ("light".equals(preferences.getTheme())) {
            theme.select(2);
        }
        else {
            theme.select(0);
        }
        theme.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                switch (theme.getSelectionIndex()) {
                    case 0:
                        preferences.setTheme(null);
                        break;
                    case 1:
                        preferences.setTheme("dark");
                        break;
                    case 2:
                        preferences.setTheme("light");
                        break;
                }
                applyTheme(getContents(), preferences.getTheme());
            }

        });

        label = new Label(composite, SWT.NONE);
        showToolbar = new Button(composite, SWT.CHECK);
        showToolbar.setText("Show toolbar");
        showToolbar.setSelection(preferences.getShowToolbar());
        showToolbar.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowToolbar(showToolbar.getSelection());
            }

        });

        label = new Label(composite, SWT.NONE);
        showObjectBrowser = new Button(composite, SWT.CHECK);
        showObjectBrowser.setText("Show object browser");
        showObjectBrowser.setSelection(preferences.getShowObjectBrowser());
        showObjectBrowser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowObjectBrowser(showObjectBrowser.getSelection());
            }

        });

        label = new Label(composite, SWT.NONE);
        showBrowser = new Button(composite, SWT.CHECK);
        showBrowser.setText("Show file browser");
        showBrowser.setSelection(preferences.getShowBrowser());
        showBrowser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowBrowser(showBrowser.getSelection());
            }

        });

        label = new Label(composite, SWT.NONE);
        label.setText("File browser visible paths");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        roots = new PathList(composite, false);
        roots.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        if (preferences.getRoots() != null) {
            java.util.List<String> list = new ArrayList<>(); // Arrays.asList(preferences.getRoots());
            File[] elements = preferences.getRoots();
            for (int i = 0; i < elements.length; i++) {
                list.add(elements[i].getAbsolutePath());
            }
            Collections.sort(list);
            roots.setItems(list.toArray(new String[list.size()]));
        }
    }

    void createSpin1CompilerPage(Composite parent) {
        Composite composite = createPage(parent, "Spin1");

        Label label = new Label(composite, SWT.NONE);
        label.setText("Library paths");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        spin1Paths = new PathList(composite, true);
        spin1Paths.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        File[] items = preferences.getSpin1LibraryPath();
        if (items != null) {
            spin1Paths.setFileItems(items);
        }

        label = new Label(composite, SWT.NONE);
        label.setText("Template");
        spin1Template = new FileSelector(composite);
        spin1Template.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        spin1Template.setFilterIndex(2);
        spin1Template.setSelection(preferences.getSpin1Template());

        new Label(composite, SWT.NONE);
        spin1CaseSensitive = new Button(composite, SWT.CHECK);
        spin1CaseSensitive.setText("Case sensitive symbols");
        spin1CaseSensitive.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        spin1CaseSensitive.setSelection(preferences.getSpin1CaseSensitiveSymbols());
        spin1CaseSensitive.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setSpin1CaseSensitiveSymbols(((Button) e.widget).getSelection());
            }

        });
    }

    void createSpin2CompilerPage(Composite parent) {
        Composite composite = createPage(parent, "Spin2");

        Label label = new Label(composite, SWT.NONE);
        label.setText("Library paths");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        spin2Paths = new PathList(composite, true);
        spin2Paths.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        File[] items = preferences.getSpin2LibraryPath();
        if (items != null) {
            spin2Paths.setFileItems(items);
        }

        label = new Label(composite, SWT.NONE);
        label.setText("Template");
        spin2Template = new FileSelector(composite);
        spin2Template.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        spin2Template.setFilterIndex(3);
        spin2Template.setSelection(preferences.getSpin2Template());

        new Label(composite, SWT.NONE);
        spin2CaseSensitive = new Button(composite, SWT.CHECK);
        spin2CaseSensitive.setText("Case sensitive symbols");
        spin2CaseSensitive.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        spin2CaseSensitive.setSelection(preferences.getSpin2CaseSensitiveSymbols());
        spin2CaseSensitive.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setSpin2CaseSensitiveSymbols(((Button) e.widget).getSelection());
            }

        });

        new Label(composite, SWT.NONE);
        spin2ClockSetter = new Button(composite, SWT.CHECK);
        spin2ClockSetter.setText("Use clock setter for PASM-only code");
        spin2ClockSetter.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        spin2ClockSetter.setSelection(preferences.getSpin2ClockSetter());
        spin2ClockSetter.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setSpin2ClockSetter(((Button) e.widget).getSelection());
            }

        });

        new Label(composite, SWT.NONE);
        spin2Compress = new Button(composite, SWT.CHECK);
        spin2Compress.setText("Compress binary");
        spin2Compress.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        spin2Compress.setSelection(preferences.getSpin2Compress());
        spin2Compress.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setSpin2Compress(((Button) e.widget).getSelection());
            }

        });
    }

    class TabStops {

        Text text;

        TabStops(Composite parent) {
            text = new Text(parent, SWT.BORDER);
            text.addFocusListener(new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent e) {
                    int[] value = getSelection();
                    setSelection(value);
                }
            });
        }

        void setLayoutData(Object data) {
            text.setLayoutData(data);
        }

        Object getLayoutData() {
            return text.getLayoutData();
        }

        void setSelection(int[] value) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < value.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(value[i]);
            }
            text.setText(sb.toString());
        }

        int[] getSelection() {
            java.util.List<Integer> l = new ArrayList<>();

            String[] s = text.getText().split("[, ]");
            for (int i = 0; i < s.length; i++) {
                try {
                    l.add(Integer.valueOf(s[i]));
                } catch (Exception e) {

                }
            }

            int[] result = new int[l.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = l.get(i);
            }

            return result;
        }
    }

    void createEditorPage(Composite parent) {
        Composite composite = createPage(parent, "Editor");

        Label label = new Label(composite, SWT.NONE);
        label.setText("Font");

        Composite container = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        editorFont = new Text(container, SWT.BORDER);
        editorFont.setLayoutData(new GridData(convertWidthInCharsToPixels(35), SWT.DEFAULT));
        editorFontSize = new Spinner(container, SWT.NONE);
        editorFontSize.setValues(1, 1, 72, 0, 1, 1);
        editorFontSize.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String s = StringConverter.asString(new FontData(editorFont.getText(), editorFontSize.getSelection(), SWT.NONE));
                if (s.equals(StringConverter.asString(defaultFont))) {
                    s = null;
                }
                preferences.setEditorFont(s);
            }
        });

        editorFontBrowse = new Button(container, SWT.PUSH);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = editorFontBrowse.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        editorFontBrowse.setLayoutData(data);

        editorFontBrowse.setText("Select");
        editorFontBrowse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FontDialog dlg = new FontDialog(getShell());
                dlg.setText("Editor font");
                dlg.setFontList(new FontData[] {
                    new FontData(editorFont.getText(), editorFontSize.getSelection(), SWT.NONE)
                });
                FontData result = dlg.open();
                if (result != null) {
                    result.setStyle(SWT.NONE);

                    editorFont.setText(result.getName());
                    editorFontSize.setSelection(result.getHeight());

                    String s = StringConverter.asString(result);
                    if (s.equals(StringConverter.asString(defaultFont))) {
                        s = null;
                    }
                    preferences.setEditorFont(s);
                }
            }
        });
        FontData fontData = defaultFont;
        String s = preferences.getEditorFont();
        if (s != null) {
            fontData = StringConverter.asFontData(s);
        }
        editorFont.setText(fontData.getName());
        editorFontSize.setSelection(fontData.getHeight());

        new Label(composite, SWT.NONE);

        showLineNumbers = new Button(composite, SWT.CHECK);
        showLineNumbers.setText("Show line numbers");
        showLineNumbers.setSelection(preferences.getShowLineNumbers());
        showLineNumbers.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowLineNumbers(showLineNumbers.getSelection());
            }
        });

        createIndentLinesGroup(composite);

        new Label(composite, SWT.NONE);

        showEditorOutline = new Button(composite, SWT.CHECK);
        showEditorOutline.setText("Show outline");
        showEditorOutline.setSelection(preferences.getShowEditorOutline());
        showEditorOutline.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowEditorOutline(showEditorOutline.getSelection());
            }
        });

        new Label(composite, SWT.NONE);

        showSectionsBackground = new Button(composite, SWT.CHECK);
        showSectionsBackground.setText("Show sections background");
        showSectionsBackground.setSelection(preferences.getShowSectionsBackground());
        showSectionsBackground.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowSectionsBackground(showSectionsBackground.getSelection());
            }
        });

        Group group = new Group(composite, SWT.NONE);
        group.setText("Tab stops");
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        label = new Label(group, SWT.NONE);
        label.setText("CON Block");
        conTabStops = new TabStops(group);
        conTabStops.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) conTabStops.getLayoutData()).widthHint = convertWidthInCharsToPixels(35);
        conTabStops.setSelection(preferences.getTabStops(ConstantsNode.class));

        label = new Label(group, SWT.NONE);
        label.setText("VAR Block");
        varTabStops = new TabStops(group);
        varTabStops.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) varTabStops.getLayoutData()).widthHint = convertWidthInCharsToPixels(35);
        varTabStops.setSelection(preferences.getTabStops(VariablesNode.class));

        label = new Label(group, SWT.NONE);
        label.setText("OBJ Block");
        objTabStops = new TabStops(group);
        objTabStops.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) objTabStops.getLayoutData()).widthHint = convertWidthInCharsToPixels(35);
        objTabStops.setSelection(preferences.getTabStops(ObjectsNode.class));

        label = new Label(group, SWT.NONE);
        label.setText("PUB / PRI Block");
        pubTabStops = new TabStops(group);
        pubTabStops.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) pubTabStops.getLayoutData()).widthHint = convertWidthInCharsToPixels(35);
        pubTabStops.setSelection(preferences.getTabStops(MethodNode.class));

        label = new Label(group, SWT.NONE);
        label.setText("DAT Block");
        datTabStops = new TabStops(group);
        datTabStops.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) datTabStops.getLayoutData()).widthHint = convertWidthInCharsToPixels(35);
        datTabStops.setSelection(preferences.getTabStops(DataNode.class));
    }

    private void createIndentLinesGroup(Composite parent) {
        new Label(parent, SWT.NONE);

        showIndentLines = new Button(parent, SWT.CHECK);
        showIndentLines.setText("Show indentation lines");
        showIndentLines.setSelection(preferences.getShowIndentLines());

        new Label(parent, SWT.NONE);

        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.horizontalIndent = convertHorizontalDLUsToPixels(16);
        container.setLayoutData(gridData);

        Label label = new Label(container, SWT.NONE);
        label.setText("Line size");

        indentLinesSize = new Spinner(container, SWT.NONE);
        indentLinesSize.setValues(preferences.getIndentLinesSize(), 0, 16, 0, 1, 1);
        indentLinesSize.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setIndentLinesSize(indentLinesSize.getSelection());
            }
        });

        showIndentLines.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = showIndentLines.getSelection();
                label.setEnabled(selection);
                indentLinesSize.setEnabled(selection);
                preferences.setShowIndentLines(selection);
            }
        });

        boolean selection = showIndentLines.getSelection();
        label.setEnabled(selection);
        indentLinesSize.setEnabled(selection);
        preferences.setShowIndentLines(selection);
    }

    void createTerminalPage(Composite parent) {
        Composite composite = createPage(parent, "Terminal");

        Label label = new Label(composite, SWT.NONE);
        label.setText("Font");

        Composite container = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        terminalFont = new Text(container, SWT.BORDER);
        terminalFont.setLayoutData(new GridData(convertWidthInCharsToPixels(35), SWT.DEFAULT));
        terminalFontSize = new Spinner(container, SWT.NONE);
        terminalFontSize.setValues(1, 1, 72, 0, 1, 1);
        terminalFontSize.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String s = StringConverter.asString(new FontData(terminalFont.getText(), terminalFontSize.getSelection(), SWT.NONE));
                if (s.equals(StringConverter.asString(defaultFont))) {
                    s = null;
                }
                preferences.setTerminalFont(s);
            }
        });

        terminalFontBrowse = new Button(container, SWT.PUSH);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = terminalFontBrowse.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        terminalFontBrowse.setLayoutData(data);

        terminalFontBrowse.setText("Select");
        terminalFontBrowse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FontDialog dlg = new FontDialog(getShell());
                dlg.setText("Terminal font");
                dlg.setFontList(new FontData[] {
                    new FontData(terminalFont.getText(), terminalFontSize.getSelection(), SWT.NONE)
                });
                FontData result = dlg.open();
                if (result != null) {
                    result.setStyle(SWT.NONE);

                    terminalFont.setText(result.getName());
                    terminalFontSize.setSelection(result.getHeight());

                    String s = StringConverter.asString(result);
                    if (s.equals(StringConverter.asString(defaultFont))) {
                        s = null;
                    }
                    preferences.setTerminalFont(s);
                }
            }
        });
        FontData fontData = defaultFont;
        String s = preferences.getTerminalFont();
        if (s != null) {
            fontData = StringConverter.asFontData(s);
        }
        terminalFont.setText(fontData.getName());
        terminalFontSize.setSelection(fontData.getHeight());

        new Label(composite, SWT.NONE);

        terminalLineInput = new Button(composite, SWT.CHECK);
        terminalLineInput.setText("Line input");
        terminalLineInput.setSelection(preferences.getTerminalLineInput());
        terminalLineInput.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setTerminalLineInput(((Button) e.widget).getSelection());
            }
        });

        new Label(composite, SWT.NONE);

        terminalLocalEcho = new Button(composite, SWT.CHECK);
        terminalLocalEcho.setText("Local echo");
        terminalLocalEcho.setSelection(preferences.getTerminalLocalEcho());
        terminalLocalEcho.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setTerminalLocalEcho(((Button) e.widget).getSelection());
            }
        });
    }

    void createConsolePage(Composite parent) {
        Composite composite = createPage(parent, "Console");

        Label label = new Label(composite, SWT.NONE);
        label.setText("Font");

        Composite container = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        consoleFont = new Text(container, SWT.BORDER);
        consoleFont.setLayoutData(new GridData(convertWidthInCharsToPixels(35), SWT.DEFAULT));
        consoleFontSize = new Spinner(container, SWT.NONE);
        consoleFontSize.setValues(1, 1, 72, 0, 1, 1);
        consoleFontSize.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String s = StringConverter.asString(new FontData(consoleFont.getText(), consoleFontSize.getSelection(), SWT.NONE));
                if (s.equals(StringConverter.asString(defaultFont))) {
                    s = null;
                }
                preferences.setConsoleFont(s);
            }
        });

        consoleFontBrowse = new Button(container, SWT.PUSH);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = consoleFontBrowse.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        consoleFontBrowse.setLayoutData(data);

        consoleFontBrowse.setText("Select");
        consoleFontBrowse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FontDialog dlg = new FontDialog(getShell());
                dlg.setText("Console font");
                dlg.setFontList(new FontData[] {
                    new FontData(consoleFont.getText(), consoleFontSize.getSelection(), SWT.NONE)
                });
                FontData result = dlg.open();
                if (result != null) {
                    result.setStyle(SWT.NONE);

                    consoleFont.setText(result.getName());
                    consoleFontSize.setSelection(result.getHeight());

                    String s = StringConverter.asString(result);
                    if (s.equals(StringConverter.asString(defaultFont))) {
                        s = null;
                    }
                    preferences.setConsoleFont(s);
                }
            }
        });
        FontData fontData = defaultFont;
        String s = preferences.getConsoleFont();
        if (s != null) {
            fontData = StringConverter.asFontData(s);
        }
        consoleFont.setText(fontData.getName());
        consoleFontSize.setSelection(fontData.getHeight());

        label = new Label(composite, SWT.NONE);
        label.setText("Max. Lines");
        consoleMaxLines = new Spinner(composite, SWT.NONE);
        consoleMaxLines.setValues(preferences.getConsoleMaxLines(), 1, 999999, 0, 1, 10);

        new Label(composite, SWT.NONE);
        consoleWriteLogFile = new Button(composite, SWT.CHECK);
        consoleWriteLogFile.setText("Write to Log File");
        consoleWriteLogFile.setSelection(preferences.getConsoleWriteLogFile());

        new Label(composite, SWT.NONE);
        consoleResetDeviceOnClose = new Button(composite, SWT.CHECK);
        consoleResetDeviceOnClose.setText("Reset Device on Close");
        consoleResetDeviceOnClose.setSelection(preferences.getConsoleResetDeviceOnClose());
    }

    class ToolsList {

        Composite group;

        ListViewer viewer;
        Button add;
        Button edit;
        Button remove;

        java.util.List<ExternalTool> elements = new ArrayList<>();

        public ToolsList(Composite parent) {
            group = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout(2, false);
            layout.marginWidth = layout.marginHeight = 0;
            group.setLayout(layout);

            viewer = new ListViewer(group, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
            viewer.setContentProvider(new ArrayContentProvider());
            viewer.setLabelProvider(new LabelProvider());
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.widthHint = convertWidthInCharsToPixels(50);
            gridData.heightHint = convertHeightInCharsToPixels(10) + viewer.getList().getBorderWidth() * 2;
            viewer.getControl().setLayoutData(gridData);
            viewer.addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    updateButtons();
                }
            });
            viewer.setInput(elements);

            Composite container = new Composite(group, SWT.NONE);
            layout = new GridLayout(1, true);
            layout.marginWidth = layout.marginHeight = 0;
            container.setLayout(layout);
            container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

            add = new Button(container, SWT.PUSH);
            add.setImage(ImageRegistry.getImageFromResources("add.png"));
            add.setToolTipText("Add");
            add.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    ExternalToolDialog dlg = new ExternalToolDialog(parent.getShell());
                    if (dlg.open() == ExternalToolDialog.OK) {
                        elements.add(dlg.getExternalTool());
                        viewer.refresh();
                        updateButtons();
                    }
                }

            });

            edit = new Button(container, SWT.PUSH);
            edit.setImage(ImageRegistry.getImageFromResources("pencil.png"));
            edit.setToolTipText("Edit");
            edit.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    ExternalTool selection = (ExternalTool) viewer.getStructuredSelection().getFirstElement();
                    ExternalToolDialog dlg = new ExternalToolDialog(parent.getShell(), selection);
                    if (dlg.open() == ExternalToolDialog.OK) {
                        viewer.refresh();
                    }
                }

            });
            edit.setEnabled(false);

            remove = new Button(container, SWT.PUSH);
            remove.setImage(ImageRegistry.getImageFromResources("delete.png"));
            remove.setToolTipText("Remove");
            remove.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    elements.remove(viewer.getStructuredSelection().getFirstElement());
                    viewer.refresh();
                    updateButtons();
                }

            });
            remove.setEnabled(false);
        }

        void updateButtons() {
            boolean emptySelection = viewer.getStructuredSelection().isEmpty();
            remove.setEnabled(!emptySelection);
            edit.setEnabled(!emptySelection);
        }

        public void setLayoutData(Object layoutData) {
            group.setLayoutData(layoutData);
        }

        public Object getLayoutData() {
            return group.getLayoutData();
        }

        public void setItems(ExternalTool[] items) {
            elements.clear();
            for (int i = 0; i < items.length; i++) {
                elements.add(new ExternalTool(items[i].name, items[i].program, items[i].arguments));
            }
            viewer.refresh();
        }

        public ExternalTool[] getItems() {
            return elements.toArray(new ExternalTool[elements.size()]);
        }

    }

    void createToolsPage(Composite parent) {
        Composite composite = createPage(parent, "Tools");

        Label label = new Label(composite, SWT.NONE);
        label.setText("External Tools");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        externalTools = new ToolsList(composite);
        externalTools.setItems(preferences.getExternalTools());
        externalTools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    }

    Composite createPage(Composite parent, String text) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(composite, SWT.NONE);
        label.setText(text);
        label.setFont(fontBold);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, layout.numColumns, 1));

        label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, layout.numColumns, 1));

        pages.add(text);

        return composite;
    }

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
        if (control instanceof List) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Button) {
            if (control != getShell().getDefaultButton()) {
                control.setForeground(widgetForeground);
                control.setBackground(buttonBackground);
            }
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

    @Override
    protected void cancelPressed() {
        preferences.setTheme(oldTheme);
        preferences.setShowToolbar(oldShowToolbar);
        preferences.setShowObjectBrowser(oldShowObjectBrowser);
        preferences.setShowBrowser(oldShowBrowser);

        preferences.setEditorFont(oldEditorFont);
        preferences.setShowLineNumbers(oldShowLineNumbers);
        preferences.setShowIndentLines(oldShowIndentLines);
        preferences.setIndentLinesSize(oldIndentLinesSize);
        preferences.setShowEditorOutline(oldShowEditorOutline);
        preferences.setShowSectionsBackground(oldShowSectionsBackground);

        preferences.setSpin1CaseSensitiveSymbols(oldSpin1CaseSensitive);

        preferences.setSpin2CaseSensitiveSymbols(oldSpin2CaseSensitive);
        preferences.setSpin2ClockSetter(oldSpin2ClockSetter);
        preferences.setSpin2Compress(oldSpin2Compress);

        preferences.setTerminalFont(oldTerminalFont);
        preferences.setTerminalLineInput(oldTerminalLineInput);
        preferences.setTerminalLocalEcho(oldTerminalLocalEcho);

        preferences.setConsoleFont(oldConsoleFont);

        super.cancelPressed();
    }

    @Override
    protected void okPressed() {
        String[] items = roots.getItems();
        File[] elements = new File[items.length];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new File(items[i]);
        }
        preferences.setRoots(elements);

        preferences.setTabStops(ConstantsNode.class, conTabStops.getSelection());
        preferences.setTabStops(VariablesNode.class, varTabStops.getSelection());
        preferences.setTabStops(ObjectsNode.class, objTabStops.getSelection());
        preferences.setTabStops(MethodNode.class, pubTabStops.getSelection());
        preferences.setTabStops(DataNode.class, datTabStops.getSelection());

        preferences.setSpin1LibraryPath(spin1Paths.getFileItems());
        preferences.setSpin1Template(spin1Template.getSelection());

        preferences.setSpin2LibraryPath(spin2Paths.getFileItems());
        preferences.setSpin2Template(spin2Template.getSelection());

        preferences.setConsoleMaxLines(consoleMaxLines.getSelection());
        preferences.setConsoleWriteLogFile(consoleWriteLogFile.getSelection());
        preferences.setConsoleResetDeviceOnClose(consoleResetDeviceOnClose.getSelection());

        preferences.setExternalTools(externalTools.getItems());

        if (!Objects.equals(oldTheme, preferences.getTheme())) {
            MessageDialog.openWarning(getShell(), SpinTools.APP_TITLE, "Close and reopen the application for the theme changes to take full effect.");
        }

        super.okPressed();
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

                    PreferencesDialog dialog = new PreferencesDialog(null, new Preferences());
                    dialog.open();

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        display.dispose();
    }

}
