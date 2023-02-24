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

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.Preferences.Bounds;
import com.maccasoft.propeller.Preferences.SearchPreferences;

public class FindReplaceDialog extends Dialog {

    private Combo fFindField, fReplaceField;
    private Button fForwardRadioButton, fGlobalRadioButton, fSelectedRangeRadioButton;
    private Button fCaseCheckBox, fWrapCheckBox, fWholeWordCheckBox, fIncrementalCheckBox;
    private Button fIsRegExCheckBox;
    private Label fStatusLabel;

    private FindReplaceTarget fTarget;
    private boolean fNeedsInitialFindBeforeReplace;

    SearchPreferences preferences;
    String findString;

    public FindReplaceDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(getShellStyle() ^ SWT.APPLICATION_MODAL | SWT.MODELESS);
        setBlockOnOpen(false);

        preferences = Preferences.getInstance().getSearchPreferences();
    }

    public void setTarget(FindReplaceTarget target) {
        this.fTarget = target;

    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Find / Replace");
        newShell.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                Rectangle rect = getShell().getBounds();
                preferences.window = new Bounds(rect.x, rect.y, rect.width, rect.height);
            }
        });
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected Rectangle getConstrainedShellBounds(Rectangle preferredSize) {
        Rectangle rect = super.getConstrainedShellBounds(preferredSize);

        SearchPreferences prefs = Preferences.getInstance().getSearchPreferences();
        if (prefs.window != null) {
            rect.x = prefs.window.x;
            rect.y = prefs.window.y;
        }

        return rect;
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

        Composite configPanel = createConfigPanel(panel);
        setGridData(configPanel, SWT.FILL, true, SWT.TOP, true);

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
        fFindField.setItems(preferences.findHistory.toArray(new String[0]));
        setGridData(fFindField, SWT.FILL, true, SWT.CENTER, false);
        addDecorationMargin(fFindField);

        Label fReplaceLabel = new Label(panel, SWT.LEFT);
        fReplaceLabel.setText("Replace with");
        setGridData(fReplaceLabel, SWT.LEFT, false, SWT.CENTER, false);

        // Create the replace content assist field
        fReplaceField = new Combo(panel, SWT.DROP_DOWN | SWT.BORDER);
        fReplaceField.setItems(preferences.replaceHistory.toArray(new String[0]));
        setGridData(fReplaceField, SWT.FILL, true, SWT.CENTER, false);
        addDecorationMargin(fReplaceField);

        return panel;
    }

    private Composite createConfigPanel(Composite parent) {

        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        panel.setLayout(layout);

        Composite directionGroup = createDirectionGroup(panel);
        setGridData(directionGroup, SWT.FILL, true, SWT.FILL, false);

        Composite scopeGroup = createScopeGroup(panel);
        setGridData(scopeGroup, SWT.FILL, true, SWT.FILL, false);

        Composite optionsGroup = createOptionsGroup(panel);
        setGridData(optionsGroup, SWT.FILL, true, SWT.FILL, true);
        ((GridData) optionsGroup.getLayoutData()).horizontalSpan = 2;

        return panel;
    }

    private Composite createDirectionGroup(Composite parent) {

        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        panel.setLayout(layout);

        Group group = new Group(panel, SWT.SHADOW_ETCHED_IN);
        group.setText("Direction");
        GridLayout groupLayout = new GridLayout();
        group.setLayout(groupLayout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        SelectionListener selectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.forwardSearch = fForwardRadioButton.getSelection();
            }

        };

        fForwardRadioButton = new Button(group, SWT.RADIO | SWT.LEFT);
        fForwardRadioButton.setText("Forward");
        setGridData(fForwardRadioButton, SWT.LEFT, false, SWT.CENTER, false);
        fForwardRadioButton.addSelectionListener(selectionListener);

        Button backwardRadioButton = new Button(group, SWT.RADIO | SWT.LEFT);
        backwardRadioButton.setText("Backward");
        setGridData(backwardRadioButton, SWT.LEFT, false, SWT.CENTER, false);
        backwardRadioButton.addSelectionListener(selectionListener);

        fForwardRadioButton.setSelection(preferences.forwardSearch);
        backwardRadioButton.setSelection(!preferences.forwardSearch);

        return panel;
    }

    private Composite createScopeGroup(Composite parent) {

        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        panel.setLayout(layout);

        Group group = new Group(panel, SWT.SHADOW_ETCHED_IN);
        group.setText("Scope");
        GridLayout groupLayout = new GridLayout();
        group.setLayout(groupLayout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        fGlobalRadioButton = new Button(group, SWT.RADIO | SWT.LEFT);
        fGlobalRadioButton.setText("All");
        setGridData(fGlobalRadioButton, SWT.LEFT, false, SWT.CENTER, false);
        fGlobalRadioButton.setSelection(true);
        fGlobalRadioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

            }

        });

        fSelectedRangeRadioButton = new Button(group, SWT.RADIO | SWT.LEFT);
        fSelectedRangeRadioButton.setText("Selected lines");
        setGridData(fSelectedRangeRadioButton, SWT.LEFT, false, SWT.CENTER, false);
        fSelectedRangeRadioButton.setSelection(false);
        fSelectedRangeRadioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

            }

        });
        fSelectedRangeRadioButton.setEnabled(false);

        return panel;
    }

    private Composite createOptionsGroup(Composite parent) {

        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        panel.setLayout(layout);

        Group group = new Group(panel, SWT.SHADOW_NONE);
        group.setText("Options");
        GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 2;
        groupLayout.makeColumnsEqualWidth = true;
        group.setLayout(groupLayout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        SelectionListener selectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.caseSensitiveSearch = fCaseCheckBox.getSelection();
                preferences.wrapSearch = fWrapCheckBox.getSelection();
            }

        };

        fCaseCheckBox = new Button(group, SWT.CHECK | SWT.LEFT);
        fCaseCheckBox.setText("Case sensitive");
        setGridData(fCaseCheckBox, SWT.LEFT, false, SWT.CENTER, false);
        fCaseCheckBox.setSelection(preferences.caseSensitiveSearch);
        fCaseCheckBox.addSelectionListener(selectionListener);
        //storeButtonWithMnemonicInMap(fCaseCheckBox);

        fWrapCheckBox = new Button(group, SWT.CHECK | SWT.LEFT);
        fWrapCheckBox.setText("Wrap search");
        setGridData(fWrapCheckBox, SWT.LEFT, false, SWT.CENTER, false);
        fWrapCheckBox.setSelection(preferences.wrapSearch);
        fWrapCheckBox.addSelectionListener(selectionListener);
        //storeButtonWithMnemonicInMap(fWrapCheckBox);

        fWholeWordCheckBox = new Button(group, SWT.CHECK | SWT.LEFT);
        fWholeWordCheckBox.setText("Whole word");
        setGridData(fWholeWordCheckBox, SWT.LEFT, false, SWT.CENTER, false);
        fWholeWordCheckBox.setSelection(preferences.wholeWordSearch);
        fWholeWordCheckBox.addSelectionListener(selectionListener);
        //storeButtonWithMnemonicInMap(fWholeWordCheckBox);

        fIncrementalCheckBox = new Button(group, SWT.CHECK | SWT.LEFT);
        fIncrementalCheckBox.setText("Incremental");
        setGridData(fIncrementalCheckBox, SWT.LEFT, false, SWT.CENTER, false);
        fIncrementalCheckBox.setSelection(false);
        fIncrementalCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

            }

        });
        fIncrementalCheckBox.setEnabled(false);
        //storeButtonWithMnemonicInMap(fIncrementalCheckBox);

        fIsRegExCheckBox = new Button(group, SWT.CHECK | SWT.LEFT);
        fIsRegExCheckBox.setText("Regular expressions");
        setGridData(fIsRegExCheckBox, SWT.LEFT, false, SWT.CENTER, false);
        ((GridData) fIsRegExCheckBox.getLayoutData()).horizontalSpan = 2;
        fIsRegExCheckBox.setSelection(preferences.regexSearch);
        fIsRegExCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.regexSearch = fIsRegExCheckBox.getSelection();
            }

        });
        //storeButtonWithMnemonicInMap(fIsRegExCheckBox);
        fWholeWordCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.wholeWordSearch = fWholeWordCheckBox.getSelection();
            }

        });
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
                if (fTarget == null) {
                    return;
                }
                fStatusLabel.setText("");
                performSearch();
                updateHistory(fFindField, preferences.findHistory);
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
                if (fTarget == null) {
                    return;
                }

                fStatusLabel.setText("");
                if (fNeedsInitialFindBeforeReplace) {
                    performSearch();
                }

                String replaceString = fReplaceField.getText();
                if (fTarget.getSelection().y != 0) {
                    fTarget.replaceSelection(replaceString);
                    performSearch();
                }

                updateHistory(fFindField, preferences.findHistory);
                updateHistory(fReplaceField, preferences.replaceHistory);
            }

        });
        setGridData(fReplaceFindButton, SWT.FILL, false, SWT.FILL, false);

        Button fReplaceSelectionButton = makeButton(panel, "Replace", 104, false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (fTarget == null) {
                    return;
                }

                fStatusLabel.setText("");
                if (fNeedsInitialFindBeforeReplace) {
                    performSearch();
                }

                String replaceString = fReplaceField.getText();
                if (fTarget.getSelection().y != 0) {
                    fTarget.replaceSelection(replaceString);
                }

                updateHistory(fFindField, preferences.findHistory);
                updateHistory(fReplaceField, preferences.replaceHistory);
            }

        });
        setGridData(fReplaceSelectionButton, SWT.FILL, false, SWT.FILL, false);

        Button fReplaceAllButton = makeButton(panel, "Replace All", 105, false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (fTarget == null) {
                    return;
                }
                fStatusLabel.setText("");
                performReplaceAll();
                updateHistory(fFindField, preferences.findHistory);
                updateHistory(fReplaceField, preferences.replaceHistory);
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
        boolean forwardSearch = fForwardRadioButton.getSelection();
        boolean caseSensitiveSearch = fCaseCheckBox.getSelection();
        boolean wrapSearch = fWrapCheckBox.getSelection();
        boolean wholeWordSearch = fWholeWordCheckBox.getSelection();
        boolean regexSearch = fIsRegExCheckBox.getSelection();

        findString = fFindField.getText();

        Point r = fTarget.getSelection();
        int findReplacePosition = r.x;
        if (forwardSearch && !fNeedsInitialFindBeforeReplace || !forwardSearch && fNeedsInitialFindBeforeReplace) {
            findReplacePosition += r.y;
        }
        fNeedsInitialFindBeforeReplace = false;

        int index;
        if (forwardSearch) {
            index = fTarget.findAndSelect(findReplacePosition, findString, true, caseSensitiveSearch, wholeWordSearch, regexSearch);
            if (index == -1) {
                getShell().getDisplay().beep();
                if (wrapSearch) {
                    fStatusLabel.setText("Wrapped search");
                    index = fTarget.findAndSelect(-1, findString, true, caseSensitiveSearch, wholeWordSearch, regexSearch);
                }
            }
        }
        else {
            // backward
            index = findReplacePosition == 0 ? -1 : fTarget.findAndSelect(findReplacePosition - 1, findString, false, caseSensitiveSearch, wholeWordSearch, regexSearch);
            if (index == -1) {
                getShell().getDisplay().beep();
                if (wrapSearch) {
                    fStatusLabel.setText("Wrapped search");
                    index = fTarget.findAndSelect(-1, findString, false, caseSensitiveSearch, wholeWordSearch, regexSearch);
                }
            }
        }

        if (index == -1) {
            fStatusLabel.setText("String not found");
        }
    }

    void performReplaceAll() {
        boolean caseSensitiveSearch = fCaseCheckBox.getSelection();
        boolean wholeWordSearch = fWholeWordCheckBox.getSelection();
        boolean regexSearch = fIsRegExCheckBox.getSelection();

        findString = fFindField.getText();
        String replaceString = fReplaceField.getText();

        if (findString != null && !findString.isEmpty()) {
            int replaceCount = 0;
            int findReplacePosition = 0;
            int index = 0;

            while (index != -1) {
                index = fTarget.findAndSelect(findReplacePosition, findString, true, caseSensitiveSearch, wholeWordSearch, regexSearch);
                if (index != -1) {
                    fTarget.replaceSelection(replaceString);
                    replaceCount++;
                    findReplacePosition = index + replaceString.length();
                }
            }

            fStatusLabel.setText(replaceCount + ((replaceCount == 1) ? " match replaced" : " matches replaced"));
        }
    }

    void updateHistory(Combo combo, List<String> history) {
        String findString = combo.getText();
        int index = history.indexOf(findString);
        if (index != 0) {
            if (index != -1) {
                history.remove(index);
            }
            history.add(0, findString);
            while (history.size() > 15) {
                history.remove(history.size() - 1);
            }
            Point selection = combo.getSelection();
            combo.setItems(history.toArray(new String[0]));
            combo.setText(findString);
            combo.setSelection(selection);
        }
    }

    public boolean isDisposed() {
        Shell shell = getShell();
        return shell == null || shell.isDisposed();
    }

    public String getFindString() {
        return findString;
    }

}
