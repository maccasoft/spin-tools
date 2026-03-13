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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
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

import com.maccasoft.propeller.Preferences.ExternalTool;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.ContentProposalAdapter;
import com.maccasoft.propeller.internal.ImageRegistry;
import com.maccasoft.propeller.internal.TextContentAdapter;

public class ExternalToolDialog extends Dialog {

    Text name;
    Button browse;
    Text program;
    Text arguments;
    Map<String, Button> editorAction;

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
        label.setText("Name");
        name = new Text(composite, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        name.addFocusListener(textFocusListener);

        label = new Label(composite, SWT.NONE);
        label.setText("Program");
        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite programGroup = new Composite(composite, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginHeight = 0;
        programGroup.setLayout(layout);
        programGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        program = new Text(programGroup, SWT.BORDER);
        program.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) program.getLayoutData()).widthHint = convertWidthInCharsToPixels(55);
        program.addFocusListener(textFocusListener);

        browse = new Button(programGroup, SWT.PUSH);
        browse.setImage(ImageRegistry.getImageFromResources("folder-horizontal-open.png"));
        browse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dlg = new FileDialog(programGroup.getShell(), SWT.OPEN);
                String fileName = dlg.open();
                if (fileName != null) {
                    program.setText(fileName);
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

        label = new Label(composite, SWT.NONE);
        label.setText("Arguments");
        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite argumentsGroup = new Composite(composite, SWT.NONE);
        argumentsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        argumentsGroup.setLayout(layout);

        arguments = new Text(argumentsGroup, SWT.WRAP | SWT.BORDER);
        arguments.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        ((GridData) arguments.getLayoutData()).widthHint = convertWidthInCharsToPixels(75);
        ((GridData) arguments.getLayoutData()).heightHint = convertHeightInCharsToPixels(5);
        arguments.addFocusListener(textFocusListener);
        arguments.setToolTipText("Type $ or Ctrl+Space to list and insert the available variables.");

        KeyStroke keyStroke = null;
        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");
        } catch (Exception e) {
            // Do nothing
        }
        ContentProposalAdapter adapter = new ContentProposalAdapter(
            arguments,
            new TextContentAdapter(),
            (contents, position) -> new IContentProposal[] {
                new ContentProposal("${file}", "${file}", "The selected editor or pinned top object file name."),
                new ContentProposal("${file.name}", "${file.name}", "The selected editor or pinned top object file name only (without extension)."),
                new ContentProposal("${file.loc}", "${file.loc}", "The selected editor or pinned top object absolute location (directory)."),
                new ContentProposal("${serial}", "${serial}", "The selected serial port."),
                new ContentProposal("${ip}", "${ip}", "IP address of the selected network device."),
            },
            keyStroke,
            new char[] { '$' });
        adapter.setPopupSize(new Point(200, 300));
        adapter.setPropagateKeys(true);
        adapter.setAutoActivationDelay(500);
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

        label = new Label(composite, SWT.NONE);
        label.setText("Auto-save Editor");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        Composite radioGroup = new Composite(composite, SWT.NONE);
        radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        radioGroup.setLayout(layout);

        editorAction = new HashMap<>();

        Button button = new Button(radioGroup, SWT.RADIO);
        button.setText("Yes");
        button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        editorAction.put(ExternalTool.EDITOR_AUTOSAVE, button);

        button = new Button(radioGroup, SWT.RADIO);
        button.setText("No");
        button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        editorAction.put(ExternalTool.EDITOR_NONE, button);

        button = new Button(radioGroup, SWT.RADIO);
        button.setText("Ask");
        button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        editorAction.put(ExternalTool.EDITOR_WARN_UNSAVED, button);

        if (externalTool == null) {
            externalTool = new ExternalTool();
        }
        name.setText(externalTool.getName());
        program.setText(externalTool.getProgram());
        arguments.setText(externalTool.getArguments());

        button = editorAction.get(externalTool.getEditorAction());
        if (button != null) {
            button.setSelection(true);
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
        boolean ok = false;

        for (Button button : editorAction.values()) {
            if (button.getSelection()) {
                ok = true;
                break;
            }
        }

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
        externalTool.setName(name.getText());
        externalTool.setProgram(program.getText());
        externalTool.setArguments(arguments.getText());
        for (Entry<String, Button> entry : editorAction.entrySet()) {
            if (entry.getValue().getSelection()) {
                externalTool.setEditorAction(entry.getKey());
                break;
            }
        }
        super.okPressed();
    }

    public ExternalTool getExternalTool() {
        return externalTool;
    }

}
