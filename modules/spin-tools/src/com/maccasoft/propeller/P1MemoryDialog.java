/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.spin1.Spin1Object;

public class P1MemoryDialog extends Dialog {

    public static final int BYTES_PER_ROW = 16;

    Display display;

    CTabFolder tabFolder;
    Canvas canvas;
    StyledText styledText;

    Font font;
    FontMetrics fontMetrics;
    Color codeBackground;
    Color variablesBackground;
    Color stackFreeBackground;

    Spin1Object object;
    byte[] data;

    int clkfreq;
    int clkmode;
    int pbase;
    int vbase;
    int dbase;

    NumberFormat format;

    public P1MemoryDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Program Informations");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.marginWidth = layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        content.setLayout(layout);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        content.setBackgroundMode(SWT.INHERIT_FORCE);

        applyDialogFont(content);

        display = parent.getDisplay();

        font = JFaceResources.getTextFont();

        GC gc = new GC(content);
        gc.setFont(font);
        fontMetrics = gc.getFontMetrics();
        gc.dispose();

        format = NumberFormat.getInstance();
        format.setGroupingUsed(true);

        codeBackground = ColorRegistry.getColor(255, 191, 191);
        variablesBackground = ColorRegistry.getColor(255, 248, 192);
        stackFreeBackground = ColorRegistry.getColor(191, 223, 255);

        createInfoGroup(content);

        tabFolder = new CTabFolder(content, SWT.BORDER);
        tabFolder.setMaximizeVisible(false);
        tabFolder.setMinimizeVisible(false);
        tabFolder.setTabHeight(24);

        CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("Memory");
        tabItem.setControl(createMemoryView(tabFolder));

        tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("Listing");
        tabItem.setControl(createListingView(tabFolder));

        tabFolder.setSelection(0);
        tabFolder.getSelection().getControl().setFocus();

        tabFolder.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.character != SWT.TAB || (e.stateMask & SWT.CTRL) == 0) {
                    return;
                }

                int index = tabFolder.getSelectionIndex();
                if ((e.stateMask & SWT.SHIFT) != 0) {
                    index--;
                    if (index < 0) {
                        index = tabFolder.getItemCount() - 1;
                    }
                }
                else {
                    index++;
                    if (index >= tabFolder.getItemCount()) {
                        index = 0;
                    }
                }
                tabFolder.setSelection(index);
                tabFolder.getItem(index).getControl().setFocus();

                e.doit = false;
            }
        });

        return content;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CLIENT_ID + 1, "Save Binary", false);
        createButton(parent, IDialogConstants.CLIENT_ID + 2, "Save Listing", false);
        super.createButtonsForButtonBar(parent);
    }

    public void createInfoGroup(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite group = new Composite(container, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        label = new Label(group, SWT.NONE);
        label.setText("$0000");
        label = new Label(group, SWT.CENTER);
        label.setText("HUB RAM Usage");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setText("$7FFF");

        label = new Label(group, SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 3;
        gridData.heightHint = convertVerticalDLUsToPixels(16);
        gridData.widthHint = convertHorizontalDLUsToPixels(100);
        label.setLayoutData(gridData);
        label.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(Event e) {
                Rectangle bounds = ((Control) e.widget).getBounds();

                int codePixels = (int) (bounds.width * (vbase - pbase) / 32768.0);
                int variablesPixels = (int) (bounds.width * (dbase - vbase) / 32768.0);

                e.gc.setBackground(codeBackground);
                e.gc.fillRectangle(0, 0, codePixels, bounds.height);
                e.gc.setBackground(variablesBackground);
                e.gc.fillRectangle(codePixels, 0, variablesPixels, bounds.height);
                e.gc.setBackground(stackFreeBackground);
                e.gc.fillRectangle(codePixels + variablesPixels, 0, bounds.width - (codePixels + variablesPixels), bounds.height);
            }

        });

        group = new Composite(container, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Code / Data");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        label.setText(String.format("%d longs", (vbase - pbase) / 4));
        label = new Label(group, SWT.BORDER);
        label.setBackground(codeBackground);
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(5), SWT.DEFAULT));

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Variables");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        label.setText(String.format("%d longs", (dbase - vbase) / 4));
        label = new Label(group, SWT.BORDER);
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(5), SWT.DEFAULT));
        label.setBackground(variablesBackground);

        label = new Label(group, SWT.NONE);
        label.setText("Stack / Free");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setText(String.format("%d longs", (data.length - dbase) / 4));
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        label = new Label(group, SWT.BORDER);
        label.setBackground(stackFreeBackground);
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(5), SWT.DEFAULT));

        label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        group = new Composite(container, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(30), SWT.DEFAULT));
        label.setText("Clock Mode");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        if (clkmode == 0x00) {
            label.setText("RCFAST");
        }
        else if (clkmode == 0x01) {
            label.setText("RCSLOW");
        }
        else {
            String xtal = null, pll = null;
            switch (clkmode & 0b0_0_1_11_000) {
                case 0b0_0_1_01_000:
                    xtal = "XTAL1";
                    break;
                case 0b0_0_1_10_000:
                    xtal = "XTAL2";
                    break;
                case 0b0_0_1_11_000:
                    xtal = "XTAL3";
                    break;
            }
            switch (clkmode & 0b0_1_1_00_111) {
                case 0b0_1_1_00_011:
                    pll = "PLL1X";
                    break;
                case 0b0_1_1_00_100:
                    pll = "PLL2X";
                    break;
                case 0b0_1_1_00_101:
                    pll = "PLL4X";
                    break;
                case 0b0_1_1_00_110:
                    pll = "PLL8X";
                    break;
                case 0b0_1_1_00_111:
                    pll = "PLL16X";
                    break;
            }
            if (xtal != null && pll != null) {
                label.setText(xtal + " + " + pll);
            }
            else if (xtal != null) {
                label.setText(xtal);
            }
            else if (pll != null) {
                label.setText(pll);
            }
        }

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Clock Freq.");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        if (clkmode == 0x00) {
            label.setText("~ " + format.format(12000000) + " Hz");
        }
        else if (clkmode == 0x01) {
            label.setText("~ " + format.format(20000) + " Hz");
        }
        else {
            label.setText(format.format(clkfreq) + " Hz");
        }

        label = new Label(group, SWT.NONE);
        label.setText("XIN Freq.");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        if ((clkmode & 0b00100000) == 0) {
            label.setText("Ignored");
        }
        else {
            switch (clkmode & 0b0_1_1_00_111) {
                case 0b0_1_1_00_011:
                    label.setText(format.format(clkfreq) + " Hz");
                    break;
                case 0b0_1_1_00_100:
                    label.setText(format.format(clkfreq / 2) + " Hz");
                    break;
                case 0b0_1_1_00_101:
                    label.setText(format.format(clkfreq / 4) + " Hz");
                    break;
                case 0b0_1_1_00_110:
                    label.setText(format.format(clkfreq / 8) + " Hz");
                    break;
                case 0b0_1_1_00_111:
                    label.setText(format.format(clkfreq / 16) + " Hz");
                    break;
            }
        }
    }

    Control createMemoryView(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        canvas = new Canvas(container, SWT.V_SCROLL | SWT.DOUBLE_BUFFERED) {

            @Override
            public Point computeSize(int wHint, int hHint, boolean changed) {
                int halfWidth = (int) (fontMetrics.getAverageCharacterWidth() / 2);
                int width = (int) (fontMetrics.getAverageCharacterWidth() * (4 + 1 + 3 * BYTES_PER_ROW + BYTES_PER_ROW) + halfWidth);
                return super.computeSize(width, fontMetrics.getHeight() * 32, changed);
            }

        };
        canvas.setFont(font);
        canvas.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        //canvas.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));

        ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selection = verticalBar.getSelection();
                verticalBar.setSelection(((selection + (BYTES_PER_ROW / 2)) / BYTES_PER_ROW) * BYTES_PER_ROW);
                canvas.redraw();
            }
        });

        canvas.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(Event e) {
                if (data == null) {
                    return;
                }

                int byteWidth = (int) (fontMetrics.getAverageCharacterWidth() * 3);
                int characterWidth = (int) fontMetrics.getAverageCharacterWidth();
                int halfWidth = (int) (fontMetrics.getAverageCharacterWidth() / 2);

                Rectangle rect = canvas.getClientArea();

                int y = 0;
                int addr = canvas.getVerticalBar().getSelection();
                while (addr < data.length && y < rect.height) {
                    e.gc.drawString(String.format("%04X", addr), 0, y, true);

                    int x1 = 5 * characterWidth - halfWidth;
                    int x2 = x1 + BYTES_PER_ROW * byteWidth + halfWidth;

                    int addr1 = addr;
                    for (int i = 0; i < BYTES_PER_ROW && addr1 < data.length; i++) {
                        if (addr1 >= dbase) {
                            e.gc.setBackground(stackFreeBackground);
                        }
                        else if (addr1 >= vbase) {
                            e.gc.setBackground(variablesBackground);
                        }
                        else if (addr1 >= pbase) {
                            e.gc.setBackground(codeBackground);
                        }
                        e.gc.fillRectangle(x1, y, byteWidth, fontMetrics.getHeight());
                        e.gc.fillRectangle(x2, y, characterWidth, fontMetrics.getHeight());

                        x1 += byteWidth;
                        x2 += characterWidth;
                        addr1++;
                    }

                    x1 = 5 * characterWidth;
                    x2 = x1 + BYTES_PER_ROW * byteWidth;

                    for (int i = 0; i < BYTES_PER_ROW && addr < data.length; i++) {
                        e.gc.drawString(String.format("%02X", data[addr] & 0xFF), x1, y, true);
                        if (data[addr] >= 0x20 && data[addr] <= 0x7F) {
                            e.gc.drawString(String.format("%c", data[addr] & 0xFF), x2, y, true);
                        }
                        else {
                            e.gc.drawString(".", x2, y, true);
                        }

                        x1 += byteWidth;
                        x2 += characterWidth;
                        addr++;
                    }

                    y += fontMetrics.getHeight();
                }
            }

        });

        canvas.addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                if (data == null) {
                    return;
                }
                int rows = (((Canvas) e.widget).getClientArea().height) / fontMetrics.getHeight();
                int selection = verticalBar.getSelection();
                verticalBar.setValues(selection, 0, data.length, rows * BYTES_PER_ROW, BYTES_PER_ROW, rows * BYTES_PER_ROW);
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });
        canvas.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.HOME) {
                    if (verticalBar.getSelection() > 0) {
                        verticalBar.setSelection(0);
                        canvas.redraw();
                    }
                }
                else if (e.keyCode == SWT.PAGE_UP) {
                    if (verticalBar.getSelection() > 0) {
                        verticalBar.setSelection(verticalBar.getSelection() - verticalBar.getPageIncrement());
                        canvas.redraw();
                    }
                }
                else if (e.keyCode == SWT.ARROW_UP) {
                    if (verticalBar.getSelection() > 0) {
                        verticalBar.setSelection(verticalBar.getSelection() - verticalBar.getIncrement());
                        canvas.redraw();
                    }
                }
                else if (e.keyCode == SWT.ARROW_DOWN) {
                    if ((verticalBar.getSelection() + verticalBar.getThumb()) < verticalBar.getMaximum()) {
                        verticalBar.setSelection(verticalBar.getSelection() + verticalBar.getIncrement());
                        canvas.redraw();
                    }
                }
                else if (e.keyCode == SWT.PAGE_DOWN) {
                    if ((verticalBar.getSelection() + verticalBar.getThumb()) < verticalBar.getMaximum()) {
                        verticalBar.setSelection(verticalBar.getSelection() + verticalBar.getPageIncrement());
                        canvas.redraw();
                    }
                }
                else if (e.keyCode == SWT.END) {
                    if ((verticalBar.getSelection() + verticalBar.getThumb()) < verticalBar.getMaximum()) {
                        verticalBar.setSelection(verticalBar.getMaximum() - verticalBar.getThumb());
                        canvas.redraw();
                    }
                }
                else if (e.keyCode == SWT.ESC) {
                    handleShellCloseEvent();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        canvas.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                e.doit = false;
                if ((e.stateMask & SWT.CTRL) != 0) {
                    Event event = new Event();
                    event.character = e.character;
                    event.stateMask = e.stateMask;
                    event.detail = e.detail;
                    e.display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            if (!canvas.isDisposed()) {
                                Control control = canvas.getParent();
                                while (!(control instanceof CTabFolder) && control.getParent() != null) {
                                    control = control.getParent();
                                }
                                control.notifyListeners(SWT.Traverse, event);
                            }
                        }
                    });
                }
            }
        });

        int rows = canvas.getClientArea().height / fontMetrics.getHeight();
        int selection = (pbase / BYTES_PER_ROW) * BYTES_PER_ROW;
        verticalBar.setValues(selection, 0, data.length, rows * BYTES_PER_ROW, BYTES_PER_ROW, rows * BYTES_PER_ROW);

        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                verticalBar.setSelection(selection);
                canvas.redraw();
            }

        });

        return container;
    }

    Control createListingView(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = fontMetrics.getHeight() * 30;
        gridData.widthHint = (int) (fontMetrics.getAverageCharacterWidth() * 40);

        styledText = new StyledText(container, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        styledText.setLayoutData(gridData);
        styledText.setMargins(5, 5, 5, 5);
        styledText.setTabs(8);
        styledText.setFont(font);
        styledText.setEditable(false);

        styledText.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.ESC) {
                    handleShellCloseEvent();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        styledText.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                e.doit = false;
                if ((e.stateMask & SWT.CTRL) != 0) {
                    Event event = new Event();
                    event.character = e.character;
                    event.stateMask = e.stateMask;
                    event.detail = e.detail;
                    e.display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            if (!styledText.isDisposed()) {
                                Control control = styledText.getParent();
                                while (!(control instanceof CTabFolder) && control.getParent() != null) {
                                    control = control.getParent();
                                }
                                control.notifyListeners(SWT.Traverse, event);
                            }
                        }
                    });
                }
            }
        });

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    object.generateListing(new PrintStream(os));
                    display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            if (!styledText.isDisposed()) {
                                styledText.setText(os.toString());
                            }
                        }
                    });
                } catch (Exception e) {
                    // Do nothing
                }
            }
        });
        thread.start();

        return container;
    }

    public Spin1Object getObject() {
        return object;
    }

    public void setObject(Spin1Object object) {
        this.object = object;

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            object.generateBinary(os);

            data = new byte[32 * 1024];
            System.arraycopy(os.toByteArray(), 0, data, 0, Math.min(data.length, os.size()));

            byte sum = 0;
            for (int i = 0; i < data.length; i++) {
                sum += data[i];
            }
            data[5] = (byte) (0x14 - sum);

            clkfreq = object.getClkFreq();
            clkmode = object.getClkMode();

            pbase = readWord(6);
            vbase = readWord(8);
            dbase = readWord(10) - 8;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int readWord(int index) {
        return (data[index] & 0xFF) | ((data[index + 1] & 0xFF) << 8);
    }

    int readLong(int index) {
        return (data[index] & 0xFF) | ((data[index + 1] & 0xFF) << 8) | ((data[index + 2] & 0xFF) << 16) | ((data[index + 3] & 0xFF) << 24);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CLIENT_ID + 1) {
            doSaveBinary();
            return;
        }
        if (buttonId == IDialogConstants.CLIENT_ID + 2) {
            doSaveListing();
            return;
        }
        super.buttonPressed(buttonId);
    }

    protected void doSaveBinary() {

    }

    protected void doSaveListing() {

    }

}
