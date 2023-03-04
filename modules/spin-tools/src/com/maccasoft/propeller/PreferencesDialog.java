/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

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

    Button showBrowser;
    PathList roots;
    Button showObjectBrowser;

    Text editorFont;
    Spinner editorFontSize;
    Button editorFontBrowse;
    Button showLineNumbers;
    Button showIndentLines;
    Button showEditorOutline;
    TabStops conTabStops;
    TabStops varTabStops;
    TabStops objTabStops;
    TabStops pubTabStops;
    TabStops datTabStops;
    Button showSectionsBackground;

    PathList spin1Paths;
    Button spin1CaseSensitive;

    PathList spin2Paths;
    Button spin2CaseSensitive;
    Button spin2ClockSetter;

    Text terminalFont;
    Spinner terminalFontSize;
    Button terminalFontBrowse;
    Button terminalLineInput;
    Button terminalLocalEcho;

    Preferences preferences;
    FontData defaultFont;
    Font fontBold;

    boolean oldShowObjectBrowser;
    boolean oldShowBrowser;
    String oldEditorFont;
    boolean oldShowLineNumbers;
    boolean oldShowIndentLines;
    boolean oldShowSectionsBackground;
    boolean oldShowEditorOutline;
    boolean oldSpin1CaseSensitive;
    boolean oldSpin2CaseSensitive;
    String oldTerminalFont;
    boolean oldTerminalLineInput;
    boolean oldTerminalLocalEcho;

    static int lastPage;

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
            gridData.heightHint = convertHeightInCharsToPixels(12) + list.getBorderWidth() * 2;
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

    public PreferencesDialog(Shell parentShell) {
        super(parentShell);
        preferences = Preferences.getInstance();
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
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
        gridData.heightHint = convertHeightInCharsToPixels(20);
        pages.setLayoutData(gridData);

        stack = new Composite(composite, SWT.NONE);
        stackLayout = new StackLayout();
        stackLayout.marginHeight = stackLayout.marginWidth = 0;
        stack.setLayout(stackLayout);
        stack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createGeneralPage(stack);
        createEditorPage(stack);
        createSpin1CompilerPage(stack);
        createSpin2CompilerPage(stack);
        createTerminalPage(stack);

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

        oldShowObjectBrowser = preferences.getShowObjectBrowser();
        oldShowBrowser = preferences.getShowBrowser();
        oldEditorFont = preferences.getEditorFont();
        oldShowLineNumbers = preferences.getShowLineNumbers();
        oldShowIndentLines = preferences.getShowIndentLines();
        oldShowEditorOutline = preferences.getShowEditorOutline();
        oldShowSectionsBackground = preferences.getShowSectionsBackground();
        oldSpin1CaseSensitive = preferences.getSpin1CaseSensitiveSymbols();
        oldSpin2CaseSensitive = preferences.getSpin2CaseSensitiveSymbols();
        oldTerminalFont = preferences.getTerminalFont();
        oldTerminalLineInput = preferences.getTerminalLineInput();
        oldTerminalLocalEcho = preferences.getTerminalLocalEcho();

        return composite;
    }

    void createGeneralPage(Composite parent) {
        Composite composite = createPage(parent, "General");

        showObjectBrowser = new Button(composite, SWT.CHECK);
        showObjectBrowser.setText("Show object browser");
        showObjectBrowser.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        showObjectBrowser.setSelection(preferences.getShowObjectBrowser());
        showObjectBrowser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowObjectBrowser(showObjectBrowser.getSelection());
            }

        });

        showBrowser = new Button(composite, SWT.CHECK);
        showBrowser.setText("Show file browser");
        showBrowser.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        showBrowser.setSelection(preferences.getShowBrowser());
        showBrowser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowBrowser(showBrowser.getSelection());
            }

        });

        Label label = new Label(composite, SWT.NONE);
        label.setText("File browser visible paths");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        roots = new PathList(composite, false);
        roots.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        if (preferences.getRoots() != null) {
            java.util.List<String> items = Arrays.asList(preferences.getRoots());
            if (items != null) {
                Collections.sort(items);
                roots.setItems(items.toArray(new String[items.size()]));
            }
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

        spin1CaseSensitive = new Button(composite, SWT.CHECK);
        spin1CaseSensitive.setText("Case sensitive symbols");
        spin1CaseSensitive.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
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

        spin2CaseSensitive = new Button(composite, SWT.CHECK);
        spin2CaseSensitive.setText("Case sensitive symbols");
        spin2CaseSensitive.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        spin2CaseSensitive.setSelection(preferences.getSpin2CaseSensitiveSymbols());
        spin2CaseSensitive.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setSpin2CaseSensitiveSymbols(((Button) e.widget).getSelection());
            }

        });

        spin2ClockSetter = new Button(composite, SWT.CHECK);
        spin2ClockSetter.setText("Use clock setter for PASM-only code");
        spin2ClockSetter.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        spin2ClockSetter.setSelection(preferences.getSpin2ClockSetter());
        spin2ClockSetter.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setSpin2ClockSetter(((Button) e.widget).getSelection());
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

        new Label(composite, SWT.NONE);

        showIndentLines = new Button(composite, SWT.CHECK);
        showIndentLines.setText("Show indentation lines");
        showIndentLines.setSelection(preferences.getShowIndentLines());
        showIndentLines.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setShowIndentLines(showIndentLines.getSelection());
            }
        });

        new Label(composite, SWT.NONE);

        showEditorOutline = new Button(composite, SWT.CHECK);
        showEditorOutline.setText("Show editor outline");
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

    @Override
    protected void cancelPressed() {
        preferences.setShowObjectBrowser(oldShowObjectBrowser);
        preferences.setShowBrowser(oldShowBrowser);

        preferences.setEditorFont(oldEditorFont);
        preferences.setShowLineNumbers(oldShowLineNumbers);
        preferences.setShowIndentLines(oldShowIndentLines);
        preferences.setShowEditorOutline(oldShowEditorOutline);
        preferences.setShowSectionsBackground(oldShowSectionsBackground);

        preferences.setSpin1CaseSensitiveSymbols(oldSpin1CaseSensitive);
        preferences.setSpin2CaseSensitiveSymbols(oldSpin2CaseSensitive);

        preferences.setTerminalFont(oldTerminalFont);
        preferences.setTerminalLineInput(oldTerminalLineInput);
        preferences.setTerminalLocalEcho(oldTerminalLocalEcho);

        super.cancelPressed();
    }

    @Override
    protected void okPressed() {
        preferences.setRoots(roots.getItems());

        preferences.setTabStops(ConstantsNode.class, conTabStops.getSelection());
        preferences.setTabStops(VariablesNode.class, varTabStops.getSelection());
        preferences.setTabStops(ObjectsNode.class, objTabStops.getSelection());
        preferences.setTabStops(MethodNode.class, pubTabStops.getSelection());
        preferences.setTabStops(DataNode.class, datTabStops.getSelection());

        preferences.setSpin1LibraryPath(spin1Paths.getFileItems());
        preferences.setSpin2LibraryPath(spin2Paths.getFileItems());

        super.okPressed();
    }
}
