/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.maccasoft.propeller.internal.ColorRegistry;

public class SerialTerminalPreferencesDialog extends Dialog {

    Text terminalFont;
    Spinner terminalFontSize;
    Button terminalFontBrowse;

    Preferences preferences;
    FontData defaultFont;
    FontData defaultTextFont;
    Font fontBold;

    Color widgetForeground;
    Color widgetBackground;
    Color listForeground;
    Color listBackground;
    Color labelForeground;

    String oldTerminalFont;
    boolean oldTerminalLineInput;
    boolean oldTerminalLocalEcho;
    int oldTerminalCursor;
    boolean oldTerminalBackspaceClears;
    boolean oldTerminalImplicitCRLF;
    Point oldTerminalSize;

    SerialTerminalPreferencesDialog(Shell parentShell, Preferences preferences) {
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
        if ("win32".equals(SWT.getPlatform()) || preferences.getTheme() != null) {
            applyTheme(parent, preferences.getTheme());
        }
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
        applyDialogFont(composite);

        defaultFont = JFaceResources.getDefaultFont().getFontData()[0];
        defaultFont.setStyle(SWT.NONE);
        defaultTextFont = JFaceResources.getTextFont().getFontData()[0];
        defaultTextFont.setStyle(SWT.NONE);

        FontData[] fontData = composite.getFont().getFontData();
        fontBold = new Font(composite.getDisplay(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
        composite.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                fontBold.dispose();
            }
        });

        createTerminalPage(composite);

        oldTerminalFont = preferences.getTerminalFont();
        oldTerminalLineInput = preferences.getTerminalLineInput();
        oldTerminalLocalEcho = preferences.getTerminalLocalEcho();
        oldTerminalCursor = preferences.getTerminalCursor();
        oldTerminalBackspaceClears = preferences.getTerminalBackspaceClears();
        oldTerminalImplicitCRLF = preferences.getTerminalImplicitCRLF();
        oldTerminalSize = preferences.getTerminalSize();

        return composite;
    }

    void createTerminalPage(Composite parent) {
        Composite composite = createPage(parent);

        Label label = new Label(composite, SWT.NONE);
        label.setText("Font");

        Composite container = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        terminalFont = new Text(container, SWT.BORDER);
        terminalFont.setLayoutData(GridDataFactory.swtDefaults() //
            .align(SWT.FILL, SWT.CENTER) //
            .grab(true, false) //
            .hint(convertWidthInCharsToPixels(35), SWT.DEFAULT) //
            .create());
        terminalFontSize = new Spinner(container, SWT.NONE);
        terminalFontSize.setValues(1, 1, 72, 0, 1, 1);
        terminalFontSize.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String s = StringConverter.asString(new FontData(terminalFont.getText(), terminalFontSize.getSelection(), SWT.NONE));
                if (s.equals(StringConverter.asString(defaultTextFont))) {
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
                    if (s.equals(StringConverter.asString(defaultTextFont))) {
                        s = null;
                    }
                    preferences.setTerminalFont(s);
                }
            }
        });
        FontData fontData = defaultTextFont;
        String s = preferences.getTerminalFont();
        if (s != null) {
            fontData = StringConverter.asFontData(s);
        }
        terminalFont.setText(fontData.getName());
        terminalFontSize.setSelection(fontData.getHeight());

        label = new Label(composite, SWT.NONE);
        label.setText("Size");

        Composite group = new Composite(composite, SWT.NONE);
        layout = new GridLayout(4, false);
        layout.marginHeight = layout.marginWidth = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        Point size = preferences.getTerminalSize();

        Spinner terminalWidth = new Spinner(group, SWT.NONE);
        terminalWidth.setValues(size.x, 80, 999, 0, 1, 1);
        label = new Label(group, SWT.NONE);
        label.setText("columns");

        Spinner terminalHeight = new Spinner(group, SWT.NONE);
        terminalHeight.setValues(size.y, 10, 99, 0, 1, 1);
        label = new Label(group, SWT.NONE);
        label.setText("rows");

        SelectionAdapter sizeSelectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setTerminalSize(terminalWidth.getSelection(), terminalHeight.getSelection());
            }

        };
        terminalWidth.addSelectionListener(sizeSelectionListener);
        terminalHeight.addSelectionListener(sizeSelectionListener);

        label = new Label(composite, SWT.NONE);
        label.setText("Cursor");

        group = new Composite(composite, SWT.NONE);
        layout = new GridLayout(4, false);
        layout.marginHeight = layout.marginWidth = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        int cursorState = preferences.getTerminalCursor();

        Button button = new Button(group, SWT.RADIO);
        button.setText("None");
        button.setSelection((cursorState & Preferences.CURSOR_ON) == 0);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int cursorState = preferences.getTerminalCursor() & Preferences.CURSOR_FLASH;
                cursorState |= Preferences.CURSOR_OFF;
                preferences.setTerminalCursor(cursorState);
            }

        });

        button = new Button(group, SWT.RADIO);
        button.setText("Underline");
        button.setSelection((cursorState & (Preferences.CURSOR_ON | Preferences.CURSOR_ULINE)) == (Preferences.CURSOR_ON | Preferences.CURSOR_ULINE));
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int cursorState = preferences.getTerminalCursor() & Preferences.CURSOR_FLASH;
                cursorState |= Preferences.CURSOR_ON | Preferences.CURSOR_ULINE;
                preferences.setTerminalCursor(cursorState);
            }

        });

        button = new Button(group, SWT.RADIO);
        button.setText("Block");
        button.setSelection((cursorState & (Preferences.CURSOR_ON | Preferences.CURSOR_ULINE)) == Preferences.CURSOR_ON);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int cursorState = preferences.getTerminalCursor() & Preferences.CURSOR_FLASH;
                cursorState |= Preferences.CURSOR_ON | Preferences.CURSOR_BLOCK;
                preferences.setTerminalCursor(cursorState);
            }

        });

        button = new Button(group, SWT.CHECK);
        button.setText("Blinking");
        button.setSelection(preferences.getTerminalLocalEcho());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int cursorState = preferences.getTerminalCursor();
                if (((Button) e.widget).getSelection()) {
                    cursorState |= Preferences.CURSOR_FLASH;
                }
                else {
                    cursorState &= ~Preferences.CURSOR_FLASH;
                }
                preferences.setTerminalCursor(cursorState);
            }

        });
        button.setSelection((cursorState & Preferences.CURSOR_FLASH) != 0);

        new Label(composite, SWT.NONE);

        group = new Composite(composite, SWT.NONE);
        layout = new GridLayout(1, false);
        layout.marginHeight = layout.marginWidth = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        button = new Button(group, SWT.CHECK);
        button.setText("Line input");
        button.setSelection(preferences.getTerminalLineInput());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setTerminalLineInput(((Button) e.widget).getSelection());
            }
        });

        button = new Button(group, SWT.CHECK);
        button.setText("Local echo");
        button.setSelection(preferences.getTerminalLocalEcho());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setTerminalLocalEcho(((Button) e.widget).getSelection());
            }
        });

        button = new Button(group, SWT.CHECK);
        button.setText("Implicit CR/LF");
        button.setSelection(preferences.getTerminalImplicitCRLF());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setTerminalImplicitCRLF(((Button) e.widget).getSelection());
            }

        });

        button = new Button(group, SWT.CHECK);
        button.setText("Backspace Clears");
        button.setSelection(preferences.getTerminalBackspaceClears());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.setTerminalBackspaceClears(((Button) e.widget).getSelection());
            }

        });
    }

    Composite createPage(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

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
    protected void cancelPressed() {
        preferences.setTerminalFont(oldTerminalFont);
        preferences.setTerminalLineInput(oldTerminalLineInput);
        preferences.setTerminalLocalEcho(oldTerminalLocalEcho);
        preferences.setTerminalCursor(oldTerminalCursor);
        preferences.setTerminalBackspaceClears(oldTerminalBackspaceClears);
        preferences.setTerminalImplicitCRLF(oldTerminalImplicitCRLF);
        preferences.setTerminalSize(oldTerminalSize.x, oldTerminalSize.y);
        super.cancelPressed();
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

                    SerialTerminalPreferencesDialog dialog = new SerialTerminalPreferencesDialog(null, new Preferences());
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
