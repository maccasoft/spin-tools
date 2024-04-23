/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.maccasoft.propeller.Preferences.ExternalTool;
import com.maccasoft.propeller.internal.ImageRegistry;

public class ExternalToolDialog extends Dialog {

    Text name;
    Button browse;
    Text program;
    Text arguments;

    ExternalTool externalTool;

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

    public ExternalToolDialog(Shell parentShell) {
        super(parentShell);
    }

    public ExternalToolDialog(Shell parentShell, ExternalTool externalTool) {
        super(parentShell);
        this.externalTool = externalTool;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("External Tool");
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
        label.setText("Name");
        name = new Text(composite, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        name.addFocusListener(textFocusListener);

        label = new Label(composite, SWT.NONE);
        label.setText("Program");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Composite group = new Composite(composite, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        program = new Text(group, SWT.BORDER);
        program.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) program.getLayoutData()).widthHint = convertWidthInCharsToPixels(55);
        program.addFocusListener(textFocusListener);

        browse = new Button(group, SWT.PUSH);
        browse.setImage(ImageRegistry.getImageFromResources("folder-horizontal-open.png"));
        browse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dlg = new FileDialog(group.getShell(), SWT.OPEN);
                String fileName = dlg.open();
                if (fileName != null) {
                    program.setText(fileName);
                }
            }

        });

        label = new Label(composite, SWT.NONE);
        label.setText("Arguments");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        arguments = new Text(composite, SWT.MULTI | SWT.BORDER);
        arguments.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        ((GridData) arguments.getLayoutData()).heightHint = convertHeightInCharsToPixels(5);
        arguments.addFocusListener(textFocusListener);

        label = new Label(composite, SWT.NONE);
        label.setText("${file} insert the currently selected editor's file or pinned top file.");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        if (externalTool != null) {
            name.setText(externalTool.getName());
            program.setText(externalTool.getProgram());
            arguments.setText(externalTool.getArguments());
        }

        name.addModifyListener(textModifyListener);
        program.addModifyListener(textModifyListener);
        arguments.addModifyListener(textModifyListener);

        return composite;
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
        if (program.getText().trim().isEmpty()) {
            ok = false;
        }

        getButton(OK).setEnabled(ok);
    }

    @Override
    protected void okPressed() {
        if (externalTool == null) {
            externalTool = new ExternalTool(name.getText(), program.getText(), arguments.getText());
        }
        else {
            externalTool.setName(name.getText());
            externalTool.setProgram(program.getText());
            externalTool.setArguments(arguments.getText());
        }
        super.okPressed();
    }

    public ExternalTool getExternalTool() {
        return externalTool;
    }

}
