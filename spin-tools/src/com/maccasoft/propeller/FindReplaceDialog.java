/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.maccasoft.propeller.Preferences.Bounds;
import com.maccasoft.propeller.Preferences.SearchPreferences;
import com.maccasoft.propeller.internal.ColorRegistry;

public class FindReplaceDialog extends Dialog {

    Combo findCombo;
    Combo replaceCombo;

    Button searchFromTop;
    Button searchFromCursor;
    Button searchForward;
    Button searchBackward;
    Button searchAll;
    Button selectedLines;
    Button caseSensitiveSearch;
    Button wrapSearch;
    Button wholeWord;
    Button regularExpression;
    Label statusLabel;

    Color widgetForeground;
    Color widgetBackground;
    Color listForeground;
    Color listBackground;
    Color labelForeground;
    Color buttonBackground;

    Preferences preferences;
    SearchPreferences searchPreferences;
    FindReplaceTarget target;
    String findString;
    Set<FindReplaceTarget> searchedTargets;

    SelectionAdapter selectionAdapter = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (searchPreferences == null) {
                return;
            }
            searchPreferences.findHistory = findCombo.getItems();
            searchPreferences.replaceHistory = replaceCombo.getItems();
            searchPreferences.searchFromTop = searchFromTop.getSelection();
            searchPreferences.forwardSearch = searchForward.getSelection();
            searchPreferences.caseSensitiveSearch = caseSensitiveSearch.getSelection();
            searchPreferences.wrapSearch = wrapSearch.getSelection();
            searchPreferences.wholeWordSearch = wholeWord.getSelection();
            searchPreferences.regexSearch = regularExpression.getSelection();
            searchFromTop.setText(searchPreferences.forwardSearch ? "Top" : "Bottom");
        }

    };

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_THEME:
                    applyTheme(getShell(), (String) evt.getNewValue());
                    break;
            }
        }
    };

    public FindReplaceDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(getShellStyle() ^ SWT.APPLICATION_MODAL | SWT.MODELESS);
        setBlockOnOpen(false);
        searchedTargets = new HashSet<>();
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
        this.searchPreferences = preferences.getSearchPreferences();
    }

    public void setTarget(FindReplaceTarget target) {
        this.target = target;
        if (searchFromCursor != null && !searchFromCursor.isDisposed()) {
            boolean searched = searchedTargets.contains(target);
            searchFromTop.setSelection(!searched && searchPreferences.searchFromTop);
            searchFromCursor.setSelection(searched || !searchPreferences.searchFromTop);
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Find / Replace");
        newShell.addDisposeListener(e -> {
            Rectangle rect = getShell().getBounds();
            searchPreferences.window = new Bounds(rect.x, rect.y, rect.width, rect.height);
            if (preferences != null) {
                preferences.removePropertyChangeListener(preferencesChangeListener);
            }
            searchedTargets.clear();
        });
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected Rectangle getConstrainedShellBounds(Rectangle preferredSize) {
        Rectangle rect = super.getConstrainedShellBounds(preferredSize);

        if (searchPreferences != null && searchPreferences.window != null) {
            rect.x = searchPreferences.window.x;
            rect.y = searchPreferences.window.y;
        }

        return rect;
    }

    @Override
    public void create() {
        super.create();

        initControlsFromPreferences();
        if (target != null) {
            initFindStringFromSelection();
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite content = new Composite(parent, SWT.NULL);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        content.setBackgroundMode(SWT.INHERIT_DEFAULT);

        createInputGroup(content);
        createConfigGroup(content);
        createButtonSection(content);
        createStatusAndCloseButton(content);

        applyDialogFont(content);

        return content;
    }

    void createInputGroup(Composite parent) {
        Composite group = new Composite(parent, SWT.NULL);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label label = new Label(group, SWT.LEFT);
        label.setText("Find");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        findCombo = new Combo(group, SWT.DROP_DOWN | SWT.BORDER);
        findCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(group, SWT.LEFT);
        label.setText("Replace with");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        replaceCombo = new Combo(group, SWT.DROP_DOWN | SWT.BORDER);
        replaceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    void createConfigGroup(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(3, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        createOriginGroup(container);
        createDirectionGroup(container);
        createScopeGroup(container);
        createOptionsGroup(container);
    }

    void createOriginGroup(Composite parent) {
        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        group.setText("Origin");
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        searchFromTop = new Button(group, SWT.RADIO | SWT.LEFT);
        searchFromTop.setText("Bottom");
        searchFromTop.addSelectionListener(selectionAdapter);

        searchFromCursor = new Button(group, SWT.RADIO | SWT.LEFT);
        searchFromCursor.setText("Cursor");

        searchFromTop.setSelection(searchPreferences.searchFromTop);
        searchFromCursor.setSelection(!searchPreferences.searchFromTop);
        searchFromCursor.addSelectionListener(selectionAdapter);
    }

    void createDirectionGroup(Composite parent) {
        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        group.setText("Direction");
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        searchForward = new Button(group, SWT.RADIO | SWT.LEFT);
        searchForward.setText("Forward");
        searchForward.addSelectionListener(selectionAdapter);

        searchBackward = new Button(group, SWT.RADIO | SWT.LEFT);
        searchBackward.setText("Backward");
        searchBackward.addSelectionListener(selectionAdapter);
    }

    void createScopeGroup(Composite parent) {
        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        group.setText("Scope");
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        searchAll = new Button(group, SWT.RADIO | SWT.LEFT);
        searchAll.setText("All");
        searchAll.setSelection(true);

        selectedLines = new Button(group, SWT.RADIO | SWT.LEFT);
        selectedLines.setText("Selected lines");
        selectedLines.setSelection(false);
        selectedLines.setEnabled(false);
    }

    void createOptionsGroup(Composite parent) {
        Group group = new Group(parent, SWT.SHADOW_NONE);
        group.setText("Options");
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

        caseSensitiveSearch = new Button(group, SWT.CHECK | SWT.LEFT);
        caseSensitiveSearch.setText("Case sensitive");
        caseSensitiveSearch.addSelectionListener(selectionAdapter);

        wrapSearch = new Button(group, SWT.CHECK | SWT.LEFT);
        wrapSearch.setText("Wrap search");
        wrapSearch.addSelectionListener(selectionAdapter);

        wholeWord = new Button(group, SWT.CHECK | SWT.LEFT);
        wholeWord.setText("Whole word");
        wholeWord.addSelectionListener(selectionAdapter);

        regularExpression = new Button(group, SWT.CHECK | SWT.LEFT);
        regularExpression.setText("Regular expressions");
        regularExpression.addSelectionListener(selectionAdapter);
    }

    void createButtonSection(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(3, true));
        container.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));

        makeButton(container, "Find", true, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (target == null) {
                    return;
                }
                statusLabel.setText("");
                performSearch();
                updateHistory(findCombo);
            }

        });

        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);

        makeButton(container, "Replace/Find", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (target == null) {
                    return;
                }

                statusLabel.setText("");

                if (target.getSelection().y != 0) {
                    String replaceString = replaceCombo.getText();
                    target.replaceSelection(replaceString);
                }
                performSearch();

                updateHistory(findCombo);
                updateHistory(replaceCombo);
            }

        });

        makeButton(container, "Replace", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (target == null) {
                    return;
                }

                statusLabel.setText("");

                if (target.getSelection().y != 0) {
                    String replaceString = replaceCombo.getText();
                    target.replaceSelection(replaceString);
                }

                updateHistory(findCombo);
                updateHistory(replaceCombo);
            }

        });

        makeButton(container, "Replace All", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (target == null) {
                    return;
                }
                statusLabel.setText("");
                performReplaceAll();
                updateHistory(findCombo);
                updateHistory(replaceCombo);
            }

        });
    }

    private Button makeButton(Composite parent, String label, boolean defaultButton, SelectionListener listener) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(label);
        button.addSelectionListener(listener);
        button.setFont(JFaceResources.getDialogFont());

        PixelConverter converter = new PixelConverter(button);
        int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(gridData);

        if (defaultButton) {
            Shell shell = parent.getShell();
            if (shell != null) {
                shell.setDefaultButton(button);
            }
        }

        return button;
    }

    void createStatusAndCloseButton(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        statusLabel = new Label(container, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button button = new Button(container, SWT.PUSH);
        button.setText(IDialogConstants.CLOSE_LABEL);
        button.setFont(JFaceResources.getDialogFont());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }

        });

        PixelConverter converter = new PixelConverter(button);
        int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);

        GridData gridData = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false);
        gridData.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(gridData);
    }

    void updateHistory(Combo combo) {
        String findString = combo.getText();
        List<String> history = new ArrayList<>(Arrays.asList(combo.getItems()));

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

    private void initControlsFromPreferences() {
        if (searchPreferences != null) {
            if (searchPreferences.findHistory != null && searchPreferences.findHistory.length != 0) {
                findCombo.setItems(searchPreferences.findHistory);
            }
            if (searchPreferences.replaceHistory != null) {
                replaceCombo.setItems(searchPreferences.replaceHistory);
            }

            searchFromTop.setText(searchPreferences.forwardSearch ? "Top" : "Bottom");
            searchFromTop.setSelection(searchPreferences.searchFromTop);
            searchFromCursor.setSelection(!searchPreferences.searchFromTop);

            searchForward.setSelection(searchPreferences.forwardSearch);
            searchBackward.setSelection(!searchPreferences.forwardSearch);

            caseSensitiveSearch.setSelection(searchPreferences.caseSensitiveSearch);
            wrapSearch.setSelection(searchPreferences.wrapSearch);
            wholeWord.setSelection(searchPreferences.wholeWordSearch);
            regularExpression.setSelection(searchPreferences.regexSearch);
        }
        if (preferences != null) {
            if ("win32".equals(SWT.getPlatform()) || preferences.getTheme() != null) {
                applyTheme(getShell());
            }
            preferences.addPropertyChangeListener(preferencesChangeListener);
        }
    }

    private void initFindStringFromSelection() {
        String fullSelection = target.getSelectionText();
        if (!fullSelection.isEmpty()) {
            findCombo.setText(fullSelection);
        }
        else if (findCombo.getItemCount() != 0) {
            findCombo.select(0);
        }
        findCombo.setSelection(new Point(0, findCombo.getText().length()));
    }

    public String getFindString() {
        return findString;
    }

    public boolean isDisposed() {
        Shell shell = getShell();
        return shell == null || shell.isDisposed();
    }

    void performSearch() {
        boolean fromTop = searchFromTop.getSelection();
        boolean forward = searchForward.getSelection();
        boolean caseSensitive = caseSensitiveSearch.getSelection();
        boolean wrap = wrapSearch.getSelection();
        boolean wholeWordSearch = wholeWord.getSelection();
        boolean regexSearch = regularExpression.getSelection();

        findString = findCombo.getText();

        Point r = target.getSelection();
        int findReplacePosition = fromTop ? 0 : r.x;
        if (forward) {
            findReplacePosition += r.y;
        }

        int index;
        if (forward) {
            index = target.findAndSelect(findReplacePosition, findString, true, caseSensitive, wholeWordSearch, regexSearch);
            if (index == -1) {
                getShell().getDisplay().beep();
                if (wrap) {
                    statusLabel.setText("Wrapped search");
                    index = target.findAndSelect(-1, findString, true, caseSensitive, wholeWordSearch, regexSearch);
                }
            }
        }
        else {
            // backward
            index = target.findAndSelect(findReplacePosition - 1, findString, false, caseSensitive, wholeWordSearch, regexSearch);
            if (index == -1) {
                getShell().getDisplay().beep();
                if (wrap) {
                    statusLabel.setText("Wrapped search");
                    index = target.findAndSelect(-1, findString, false, caseSensitive, wholeWordSearch, regexSearch);
                }
            }
        }

        if (index == -1) {
            statusLabel.setText("String not found");
        }

        searchFromTop.setSelection(false);
        searchFromCursor.setSelection(true);
        searchedTargets.add(target);
    }

    void performReplaceAll() {
        boolean caseSensitive = caseSensitiveSearch.getSelection();
        boolean wholeWordSearch = wholeWord.getSelection();
        boolean regexSearch = regularExpression.getSelection();

        findString = findCombo.getText();
        String replaceString = replaceCombo.getText();

        if (findString != null && !findString.isEmpty()) {
            int replaceCount = 0;
            int findReplacePosition = 0;
            int index = 0;

            while (index != -1) {
                index = target.findAndSelect(findReplacePosition, findString, true, caseSensitive, wholeWordSearch, regexSearch);
                if (index != -1) {
                    target.replaceSelection(replaceString);
                    replaceCount++;
                    findReplacePosition = index + replaceString.length();
                }
            }

            statusLabel.setText(replaceCount + ((replaceCount == 1) ? " match replaced" : " matches replaced"));
        }
    }

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
        else if (control instanceof Group group) {
            control.setBackground(widgetBackground);
            control.setForeground(widgetForeground);
            Control[] children = group.getChildren();
            for (int i = 0; i < children.length; i++) {
                applyTheme(children[i]);
            }
        }
        else if (control instanceof Composite composite) {
            control.setBackground(widgetBackground);
            Control[] children = composite.getChildren();
            for (int i = 0; i < children.length; i++) {
                applyTheme(children[i]);
            }
        }
    }

    public static void main(String[] args) {
        final Display display = new Display();

        display.setErrorHandler(t -> t.printStackTrace());
        display.setRuntimeExceptionHandler(t -> t.printStackTrace());

        Realm.runWithDefault(DisplayRealm.getRealm(display), () -> {
            try {
                Shell shell = new Shell(display);

                FindReplaceDialog dlg = new FindReplaceDialog(shell);
                dlg.setPreferences(new Preferences());
                dlg.setBlockOnOpen(true);
                dlg.open();

                shell.dispose();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        display.dispose();
    }

}
