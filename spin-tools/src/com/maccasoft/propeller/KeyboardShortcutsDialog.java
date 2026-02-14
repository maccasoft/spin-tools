/*
 * Copyright (c) 26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class KeyboardShortcutsDialog extends Dialog {

    public KeyboardShortcutsDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Keyboard Shortcuts");
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        content.setLayout(layout);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setBackgroundMode(SWT.INHERIT_FORCE);

        applyDialogFont(content);

        addFileColumn(content);
        addToolsColumn(content);
        addEditColumn(content);

        return content;
    }

    void addFileColumn(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        content.setLayout(layout);
        content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        Font textFont = JFaceResources.getTextFont();

        Label label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setFont(textFont);
        label.setText(" File");
        label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setFont(textFont);
        label.setText("""
             Ctrl+Alt+1      New P1 Spin
             Ctrl+Alt+2      New P1 Spin Object
             Ctrl+Alt+3      New P2 Spin
             Ctrl+Alt+4      New P2 Spin Object
             Ctrl+Alt+5      New P1 C
             Ctrl+Alt+6      New P2 C
            
             Ctr+O           Open File
            
             Ctrl+W          Close Current Editor
             Shift+Ctrl+W    Close All Editors
            
             Ctrl+S          Save Current File
             Shift+Ctrl+S    Save all Files
            
             Ctrl+T          Pin as Top Object
            
             F5              Refresh File Explorer
            """);
    }

    void addToolsColumn(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        content.setLayout(layout);
        content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        Font textFont = JFaceResources.getTextFont();

        Label label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setFont(textFont);
        label.setText(" Tools");
        label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setFont(textFont);
        label.setText("""
             F8              Show Info
             F10             Upload to RAM
             F11             Upload to Flash/Eeprom
            
             Ctrl+F8         Show Info with Debug
            
             Shift+F10       Upload to RAM with Terminal
             Ctrl+F10        Upload to RAM with Debug
            
             Shift+F11       Upload to Flash/Eeprom with Terminal
             Ctrl+F11        Upload to Flash/Eeprom with Debug
            
             F7              Show Devices
             F12             Open Serial Terminal
            
             Alt+[1..9]      Run External Tool 1..9
            """);
    }

    void addEditColumn(Composite parent) {
        Font textFont = JFaceResources.getTextFont();

        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridData) label.getLayoutData()).horizontalSpan = 2;
        label.setFont(textFont);
        label.setText(" Edit");
        label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        label.setFont(textFont);
        label.setText("""
             Ctrl+Z          Undo
             Shift+Ctrl+Z    Redo
            
             Ctrl+X          Cut
             Ctrl+C          Copy
             Ctrl+V          Paste
             Ctrl+A          Select All
            
             Shift+Alt+A     Toggle Block Selection
            
             Ctrl+F          Find / Replace
             Ctrl+K          Find Next
             Shift+Ctrl+K    Find Previous
            
             Ctrl+[0..9]     Go To Bookmark 0..9
            """);

        label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        label.setFont(textFont);
        label.setText("""
             Ctrl+Tab        Next Tab
             Shift+Ctrl+Tab  Previous Tab
            
             Shift+Alt+Up    Go to Previous Error / Warning
             Shift+Alt+Down  Go to Next Error / Warning
             Shift+Alt+Left  Go to Previous Edit Location
             Shift+Alt+Right Go to Next Edit Location
            
             Ctrl+Space      Open Content Proposal
             Ins             Toggle Edit Mode
             Tab             Align to Next Column
             Shift+Tab       Align to Previous Column
            
             Shift+Ctrl+F    Format Source
             Shift+Alt+P     Make Skip Pattern
            """);
    }

}
