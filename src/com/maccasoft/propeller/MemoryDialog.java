/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

public class MemoryDialog extends Dialog {

    public static final int BYTES_PER_ROW = 16;

    Display display;
    Canvas canvas;
    ScrollBar verticalBar;

    Font font;
    FontMetrics fontMetrics;
    Color codeBackground;
    Color variablesBackground;
    Color stackFreeBackground;

    byte[] data;

    int clkfreq;
    int pbase;
    int vbase;
    int dbase;

    public MemoryDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Info");
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

        codeBackground = ColorRegistry.getColor(255, 191, 191);
        variablesBackground = ColorRegistry.getColor(255, 248, 192);
        stackFreeBackground = ColorRegistry.getColor(191, 223, 255);

        createInfoGroup(content);
        createMemoryView(content);

        int rows = canvas.getClientArea().height / fontMetrics.getHeight();
        int selection = verticalBar.getSelection();
        verticalBar.setValues(selection, 0, data.length, rows * BYTES_PER_ROW, BYTES_PER_ROW, rows * BYTES_PER_ROW);

        return content;
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
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(30), SWT.DEFAULT));
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
        label.setText(String.format("%d longs", (data.length - vbase) / 4));
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
        if (data[4] == 0x00) {
            label.setText("RCFAST");
        }
        else if (data[4] == 0x01) {
            label.setText("RCSLOW");
        }
        else {
            String xtal = null, pll = null;
            switch (data[4] & 0b0_0_1_11_000) {
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
            switch (data[4] & 0b0_1_1_00_111) {
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
                label.setText(xtal + "+" + pll);
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
        if (data[4] == 0x00) {
            label.setText("~12 MHz");
        }
        else if (data[4] == 0x01) {
            label.setText("~20 KHz");
        }
        else {
            label.setText(String.format("%d MHz", clkfreq / 1000000));
        }

        label = new Label(group, SWT.NONE);
        label.setText("XIN Freq.");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        if ((data[4] & 0b00100000) == 0) {
            label.setText("Ignored");
        }
        else {
            switch (data[4] & 0b0_1_1_00_111) {
                case 0b0_1_1_00_011:
                    label.setText(String.format("%d MHz", clkfreq / 1000000));
                    break;
                case 0b0_1_1_00_100:
                    label.setText(String.format("%d MHz", clkfreq / 2000000));
                    break;
                case 0b0_1_1_00_101:
                    label.setText(String.format("%d MHz", clkfreq / 4000000));
                    break;
                case 0b0_1_1_00_110:
                    label.setText(String.format("%d MHz", clkfreq / 8000000));
                    break;
                case 0b0_1_1_00_111:
                    label.setText(String.format("%d MHz", clkfreq / 16000000));
                    break;
            }
        }
    }

    public void createMemoryView(Composite parent) {
        display = parent.getDisplay();

        if ("win32".equals(SWT.getPlatform())) {
            font = new Font(Display.getDefault(), "Courier New", 10, SWT.NONE);
        }
        else {
            font = new Font(Display.getDefault(), "mono", 10, SWT.NONE);
        }

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        canvas = new Canvas(container, SWT.V_SCROLL | SWT.DOUBLE_BUFFERED | SWT.NO_FOCUS) {

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

        GC gc = new GC(canvas);
        fontMetrics = gc.getFontMetrics();
        gc.dispose();

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
                if (e.keyCode == SWT.PAGE_UP) {
                    if (verticalBar.getSelection() > 0) {
                        verticalBar.setSelection(verticalBar.getSelection() - verticalBar.getPageIncrement());
                        canvas.redraw();
                    }
                }
                if (e.keyCode == SWT.ARROW_UP) {
                    if (verticalBar.getSelection() > 0) {
                        verticalBar.setSelection(verticalBar.getSelection() - verticalBar.getIncrement());
                        canvas.redraw();
                    }
                }
                if (e.keyCode == SWT.ARROW_DOWN) {
                    if ((verticalBar.getSelection() + verticalBar.getThumb()) < verticalBar.getMaximum()) {
                        verticalBar.setSelection(verticalBar.getSelection() + verticalBar.getIncrement());
                        canvas.redraw();
                    }
                }
                if (e.keyCode == SWT.PAGE_DOWN) {
                    if ((verticalBar.getSelection() + verticalBar.getThumb()) < verticalBar.getMaximum()) {
                        verticalBar.setSelection(verticalBar.getSelection() + verticalBar.getPageIncrement());
                        canvas.redraw();
                    }
                }
                if (e.keyCode == SWT.END) {
                    if ((verticalBar.getSelection() + verticalBar.getThumb()) < verticalBar.getMaximum()) {
                        verticalBar.setSelection(verticalBar.getMaximum() - verticalBar.getThumb());
                        canvas.redraw();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        verticalBar = canvas.getVerticalBar();
        verticalBar.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selection = verticalBar.getSelection();
                verticalBar.setSelection(((selection + (BYTES_PER_ROW / 2)) / BYTES_PER_ROW) * BYTES_PER_ROW);
                canvas.redraw();
            }
        });
    }

    public Control getControl() {
        return canvas;
    }

    public void setData(byte[] data) {
        this.data = data;

        clkfreq = readLong(0);
        pbase = readWord(6);
        vbase = readWord(8);
        dbase = readWord(10) - 8;
    }

    int readWord(int index) {
        return (data[index] & 0xFF) | ((data[index + 1] & 0xFF) << 8);
    }

    int readLong(int index) {
        return (data[index] & 0xFF) | ((data[index + 1] & 0xFF) << 8) | ((data[index + 2] & 0xFF) << 16) | ((data[index + 3] & 0xFF) << 24);
    }

    public static void main(String[] args) {
        final Display display = new Display();

        Realm.runWithDefault(DisplayRealm.getRealm(display), new Runnable() {

            @Override
            public void run() {
                try {
                    MemoryDialog app = new MemoryDialog(null);

                    byte[] data = new byte[32768];
                    try {
                        FileInputStream is = new FileInputStream(new File("/home/marco/Sketches/serial", "test.binary"));
                        is.read(data);
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    app.setData(data);

                    app.open();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        display.dispose();
    }

}
