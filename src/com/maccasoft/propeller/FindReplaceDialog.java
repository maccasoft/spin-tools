/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class FindReplaceDialog extends Dialog {

    private Combo fFindField, fReplaceField;
    private Label fStatusLabel;

    private FindReplaceTarget fTarget;
    private boolean fNeedsInitialFindBeforeReplace;

    public FindReplaceDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(getShellStyle() ^ SWT.APPLICATION_MODAL | SWT.MODELESS);
        setBlockOnOpen(false);
    }

    public void setTarget(FindReplaceTarget target) {
        this.fTarget = target;

    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Find / Replace");
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite panel = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = true;
        panel.setLayout(layout);
        setGridData(panel, SWT.FILL, true, SWT.FILL, true);

        Composite inputPanel = createInputPanel(panel);
        setGridData(inputPanel, SWT.FILL, true, SWT.TOP, false);

        Composite buttonPanel = createButtonSection(panel);
        setGridData(buttonPanel, SWT.RIGHT, true, SWT.BOTTOM, false);

        Composite statusBar = createStatusAndCloseButton(panel);
        setGridData(statusBar, SWT.FILL, true, SWT.BOTTOM, false);

        applyDialogFont(panel);

        initFindStringFromSelection();
        fNeedsInitialFindBeforeReplace = true;

        return panel;
    }

    private Composite createInputPanel(Composite parent) {

        Composite panel = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        panel.setLayout(layout);

        Label findLabel = new Label(panel, SWT.LEFT);
        findLabel.setText("Find");
        setGridData(findLabel, SWT.LEFT, false, SWT.CENTER, false);

        fFindField = new Combo(panel, SWT.DROP_DOWN | SWT.BORDER);
        setGridData(fFindField, SWT.FILL, true, SWT.CENTER, false);
        addDecorationMargin(fFindField);

        Label fReplaceLabel = new Label(panel, SWT.LEFT);
        fReplaceLabel.setText("Replace with");
        setGridData(fReplaceLabel, SWT.LEFT, false, SWT.CENTER, false);

        // Create the replace content assist field
        fReplaceField = new Combo(panel, SWT.DROP_DOWN | SWT.BORDER);
        setGridData(fReplaceField, SWT.FILL, true, SWT.CENTER, false);
        addDecorationMargin(fReplaceField);

        return panel;
    }

    private Composite createButtonSection(Composite parent) {

        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = -1; // this is intended
        panel.setLayout(layout);

        Button fFindNextButton = makeButton(panel, "Find", 102, true, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                fStatusLabel.setText("");
                performSearch();
            }

        });
        setGridData(fFindNextButton, SWT.FILL, true, SWT.FILL, false);

        /*Button fSelectAllButton = makeButton(panel, "Select All", 106, false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

            }

        });
        setGridData(fSelectAllButton, SWT.FILL, true, SWT.FILL, false);*/

        new Label(panel, SWT.NONE); // filler
        new Label(panel, SWT.NONE);

        Button fReplaceFindButton = makeButton(panel, "Replace/Find", 103, false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                fStatusLabel.setText("");
                if (fNeedsInitialFindBeforeReplace) {
                    performSearch();
                }
                fTarget.replaceSelection(fReplaceField.getText());
                performSearch();
            }

        });
        setGridData(fReplaceFindButton, SWT.FILL, false, SWT.FILL, false);

        Button fReplaceSelectionButton = makeButton(panel, "Replace", 104, false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                fStatusLabel.setText("");
                if (fNeedsInitialFindBeforeReplace) {
                    performSearch();
                }
                fTarget.replaceSelection(fReplaceField.getText());
            }

        });
        setGridData(fReplaceSelectionButton, SWT.FILL, false, SWT.FILL, false);

        Button fReplaceAllButton = makeButton(panel, "Replace All", 105, false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                fStatusLabel.setText("");
                performReplaceAll();
            }

        });
        setGridData(fReplaceAllButton, SWT.FILL, true, SWT.FILL, false);

        return panel;
    }

    private Composite createStatusAndCloseButton(Composite parent) {

        Composite panel = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        panel.setLayout(layout);

        fStatusLabel = new Label(panel, SWT.LEFT);
        setGridData(fStatusLabel, SWT.FILL, true, SWT.CENTER, false);

        Button closeButton = createButton(panel, IDialogConstants.CANCEL_ID, "Close", false);
        setGridData(closeButton, SWT.RIGHT, false, SWT.BOTTOM, false);

        return panel;
    }

    private void setGridData(Control component, int horizontalAlignment, boolean grabExcessHorizontalSpace, int verticalAlignment, boolean grabExcessVerticalSpace) {
        GridData gd;
        if (component instanceof Button && (((Button) component).getStyle() & SWT.PUSH) != 0) {
            setButtonDimensionHint((Button) component);
            gd = (GridData) component.getLayoutData();
        }
        else {
            gd = new GridData();
            component.setLayoutData(gd);
            gd.horizontalAlignment = horizontalAlignment;
            gd.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
        }
        gd.verticalAlignment = verticalAlignment;
        gd.grabExcessVerticalSpace = grabExcessVerticalSpace;
    }

    private void setButtonDimensionHint(Button button) {
        Object gd = button.getLayoutData();
        if (gd instanceof GridData) {
            ((GridData) gd).widthHint = getButtonWidthHint(button);
            ((GridData) gd).horizontalAlignment = GridData.FILL;
        }
    }

    private int getButtonWidthHint(Button button) {
        button.setFont(JFaceResources.getDialogFont());
        PixelConverter converter = new PixelConverter(button);
        int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
    }

    private void addDecorationMargin(Control control) {
        Object layoutData = control.getLayoutData();
        if (!(layoutData instanceof GridData)) {
            return;
        }
        GridData gd = (GridData) layoutData;
        FieldDecoration dec = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
        gd.horizontalIndent = dec.getImage().getBounds().width;
    }

    private Button makeButton(Composite parent, String label, int id, boolean dfltButton, SelectionListener listener) {
        Button button = createButton(parent, id, label, dfltButton);
        button.addSelectionListener(listener);
        //storeButtonWithMnemonicInMap(button);
        return button;
    }

    private void initFindStringFromSelection() {
        String fullSelection = fTarget.getSelectionText();
        if (!fullSelection.isEmpty()) {
            fFindField.setText(fullSelection);
        }
        fFindField.setSelection(new Point(0, fFindField.getText().length()));
    }

    void performSearch() {
        Point r = fTarget.getSelection();
        int findReplacePosition = r.x;
        if (!fNeedsInitialFindBeforeReplace) {
            findReplacePosition += r.y;
        }
        fNeedsInitialFindBeforeReplace = false;
        int index = fTarget.findAndSelect(findReplacePosition, fFindField.getText(), true, true, false);
        if (index == -1) {
            index = fTarget.findAndSelect(0, fFindField.getText(), true, true, false);
            if (index == -1) {
                fStatusLabel.setText("String not found");
            }
            else {
                fStatusLabel.setText("Wrapped search");
            }
            Display.getDefault().beep();
        }
    }

    void performReplaceAll() {
        String findString = fFindField.getText();
        String replaceString = fReplaceField.getText();

        if (findString != null && !findString.isEmpty()) {
            int replaceCount = 0;
            int findReplacePosition = 0;
            int index = 0;

            while (index != -1) {
                index = fTarget.findAndSelect(findReplacePosition, findString, true, true, false);
                if (index != -1) {
                    fTarget.replaceSelection(replaceString);
                    replaceCount++;
                    findReplacePosition = index + replaceString.length();
                }
            }

            fStatusLabel.setText(replaceCount + ((replaceCount == 1) ? " match replaced" : " matches replaced"));
        }
    }

}
