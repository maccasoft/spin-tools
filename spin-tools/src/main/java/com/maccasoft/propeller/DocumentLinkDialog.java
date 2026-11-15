/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ImageRegistry;
import com.maccasoft.propeller.preferences.DocumentLink;

public class DocumentLinkDialog extends Dialog {

    Text name;
    Text fileUrl;
    Button browse;

    DocumentLink documentLink;

    ModifyListener textModifyListener = new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
            validate();
        }

    };

    FocusListener textFocusListener = new FocusAdapter() {

        @Override
        public void focusGained(FocusEvent e) {
            ((Text) e.widget).selectAll();
        }

    };

    public DocumentLinkDialog(Shell parentShell) {
        super(parentShell);
    }

    public DocumentLinkDialog(Shell parentShell, DocumentLink documentLink) {
        super(parentShell);
        this.documentLink = documentLink;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Help Link");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        Preferences preferences = Preferences.getInstance();
        if ("win32".equals(SWT.getPlatform()) || preferences.getTheme() != null) {
            applyTheme(parent, preferences.getTheme());
        }
        return contents;
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
            if ("win32".equals(SWT.getPlatform())) {
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

        Label label = new Label(composite, SWT.NONE);
        label.setText("Label");
        name = new Text(composite, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        name.addFocusListener(textFocusListener);

        createFileURLGroup(composite);

        if (documentLink == null) {
            documentLink = new DocumentLink();
        }
        name.setText(documentLink.getLabel());
        fileUrl.setText(documentLink.getUrl());

        name.addModifyListener(textModifyListener);
        fileUrl.addModifyListener(textModifyListener);

        return composite;
    }

    void createFileURLGroup(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText("File or URL");

        Composite group = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        fileUrl = new Text(group, SWT.BORDER);
        fileUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) fileUrl.getLayoutData()).widthHint = convertWidthInCharsToPixels(55);
        fileUrl.addFocusListener(textFocusListener);

        browse = new Button(group, SWT.PUSH);
        browse.setImage(ImageRegistry.getImageFromResources("folder-horizontal-open.png"));
        browse.setLayoutData(new GridData(convertHorizontalDLUsToPixels(16), convertHorizontalDLUsToPixels(16)));
        browse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dlg = new FileDialog(group.getShell(), SWT.OPEN);
                String fileName = dlg.open();
                if (fileName != null) {
                    fileUrl.setText(fileName);
                    if (name.getText().isBlank()) {
                        String s = new File(fileName).getName();
                        int i = s.lastIndexOf('.');
                        if (i != -1) {
                            s = s.substring(0, i);
                        }
                        name.setText(s);
                    }
                }
            }

        });
    }

    @Override
    public void create() {
        super.create();
        validate();
    }

    void validate() {
        boolean ok = true;

        if (name.getText().trim().isEmpty()) {
            ok = false;
        }
        if (fileUrl.getText().trim().isEmpty()) {
            ok = false;
        }

        getButton(OK).setEnabled(ok);
    }

    @Override
    protected void okPressed() {
        documentLink.setLabel(name.getText());
        documentLink.setUrl(fileUrl.getText());
        super.okPressed();
    }

    public DocumentLink getHelpLink() {
        return documentLink;
    }

}
