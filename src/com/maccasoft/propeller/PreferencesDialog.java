/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.maccasoft.propeller.internal.ImageRegistry;

public class PreferencesDialog extends Dialog {

    List pages;
    Composite stack;
    StackLayout stackLayout;

    List roots;
    Button rootAdd;
    Button rootRemove;
    Button rootMoveUp;
    Button rootMoveDown;

    Text editorFont;
    Spinner editorFontSize;
    Button editorFontBrowse;
    Button showLineNumbers;
    Button showIndentLines;

    Text terminalFont;
    Spinner terminalFontSize;
    Button terminalFontBrowse;

    Preferences preferences;
    FontData defaultFont;
    Font fontBold;

    String oldEditorFont;
    boolean oldShowLineNumbers;
    boolean oldShowIndentLines;
    String oldTerminalFont;

    static int lastPage;

    public PreferencesDialog(Shell parentShell) {
        super(parentShell);
        preferences = Preferences.getInstance();
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

        oldEditorFont = preferences.getEditorFont();
        oldShowLineNumbers = preferences.getShowLineNumbers();
        oldShowIndentLines = preferences.getShowIndentLines();
        oldTerminalFont = preferences.getTerminalFont();

        return composite;
    }

    void createGeneralPage(Composite parent) {
        Composite composite = createPage(parent, "General");

        Composite group = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        Label label = new Label(group, SWT.NONE);
        label.setText("File browser root paths");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        roots = new List(group, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = convertWidthInCharsToPixels(50);
        gridData.heightHint = convertHeightInCharsToPixels(5) + roots.getBorderWidth() * 2;
        roots.setLayoutData(gridData);
        roots.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateRootDirectoryButtons();
            }
        });

        Composite container = new Composite(group, SWT.NONE);
        layout = new GridLayout(1, true);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        rootAdd = new Button(container, SWT.PUSH);
        rootAdd.setImage(ImageRegistry.getImageFromResources("add.png"));
        rootAdd.setToolTipText("Add");
        rootAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dlg = new DirectoryDialog(getShell());

                int index = roots.getSelectionIndex();
                if (index != -1) {
                    dlg.setFilterPath(roots.getItem(index));
                }

                String s = dlg.open();
                if (s != null) {
                    roots.add(s);
                    updateRootDirectoryButtons();
                }
            }

        });

        rootRemove = new Button(container, SWT.PUSH);
        rootRemove.setImage(ImageRegistry.getImageFromResources("delete.png"));
        rootRemove.setToolTipText("Remove");
        rootRemove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                roots.remove(roots.getSelectionIndex());
                updateRootDirectoryButtons();
            }

        });
        rootRemove.setEnabled(false);

        rootMoveUp = new Button(container, SWT.PUSH);
        rootMoveUp.setImage(ImageRegistry.getImageFromResources("arrow_up.png"));
        rootMoveUp.setToolTipText("Up");
        rootMoveUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = roots.getSelectionIndex();
                ArrayList<String> items = new ArrayList<String>(Arrays.asList(roots.getItems()));
                String s = items.get(index);
                items.remove(index);
                items.add(index - 1, s);
                roots.setItems(items.toArray(new String[items.size()]));
                roots.setSelection(index - 1);
                roots.setFocus();
                updateRootDirectoryButtons();
            }

        });
        rootMoveUp.setEnabled(false);

        rootMoveDown = new Button(container, SWT.PUSH);
        rootMoveDown.setImage(ImageRegistry.getImageFromResources("arrow_down.png"));
        rootMoveDown.setToolTipText("Down");
        rootMoveDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = roots.getSelectionIndex();
                ArrayList<String> items = new ArrayList<String>(Arrays.asList(roots.getItems()));
                String s = items.get(index);
                items.add(index + 2, s);
                items.remove(index);
                roots.setItems(items.toArray(new String[items.size()]));
                roots.setSelection(index + 1);
                roots.setFocus();
                updateRootDirectoryButtons();
            }

        });
        rootMoveDown.setEnabled(false);

        String[] items = preferences.getRoots();
        if (items != null) {
            roots.setItems(items);
        }
    }

    void updateRootDirectoryButtons() {
        int index = roots.getSelectionIndex();
        rootRemove.setEnabled(index != -1);
        rootMoveUp.setEnabled(index != -1 && index > 0);
        rootMoveDown.setEnabled(index != -1 && index < (roots.getItemCount() - 1));
    }

    void createAssemblerPage(Composite parent) {
        Composite composite = createPage(parent, "Assembler");

        Composite group = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
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
        preferences.setEditorFont(oldEditorFont);
        preferences.setShowLineNumbers(oldShowLineNumbers);
        preferences.setShowIndentLines(oldShowIndentLines);
        preferences.setTerminalFont(oldTerminalFont);
        super.cancelPressed();
    }

    @Override
    protected void okPressed() {
        preferences.setRoots(roots.getItems());
        super.okPressed();
    }
}
