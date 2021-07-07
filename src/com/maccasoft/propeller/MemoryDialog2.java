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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

import org.apache.commons.lang3.BitField;
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
import org.eclipse.swt.graphics.FontData;
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
import com.maccasoft.propeller.spin2.Spin2Object;

public class MemoryDialog2 extends Dialog {

    public static final int BYTES_PER_ROW = 16;

    static BitField cm_pll = new BitField(0b0000_000_1_000000_0000000000_0000_00_00);
    static BitField cm_xi_div = new BitField(0b0000_000_0_111111_0000000000_0000_00_00);
    static BitField cm_vco_mul = new BitField(0b0000_000_0_000000_1111111111_0000_00_00);
    static BitField cm_vco_div = new BitField(0b0000_000_0_000000_0000000000_1111_00_00);
    static BitField cm_cc = new BitField(0b0000_000_0_000000_0000000000_0000_11_00);
    static BitField cm_ss = new BitField(0b0000_000_0_000000_0000000000_0000_00_11);

    Display display;
    Canvas canvas;
    ScrollBar verticalBar;

    Font font;
    FontMetrics fontMetrics;
    Color codeBackground;
    Color variablesBackground;
    Color stackFreeBackground;

    Spin2Object object;
    byte[] data;

    int clkfreq;
    int clkmode;
    int pbase;
    int vbase;
    int dbase;

    NumberFormat format;

    public MemoryDialog2(Shell parentShell) {
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

        format = NumberFormat.getInstance();
        format.setGroupingUsed(true);

        codeBackground = ColorRegistry.getColor(255, 191, 191);
        variablesBackground = ColorRegistry.getColor(255, 248, 192);
        stackFreeBackground = ColorRegistry.getColor(191, 223, 255);

        createInfoGroup(content);
        createMemoryView(content);

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
        label.setText("$00000");
        label = new Label(group, SWT.CENTER);
        label.setText("HUB RAM Usage");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setText(String.format("$%X", data.length - 1));

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

                int interpreterPixels = (int) (bounds.width * pbase / (double) data.length);
                int codePixels = (int) (bounds.width * (vbase - pbase) / (double) data.length);
                int variablesPixels = (int) (bounds.width * (dbase - vbase) / (double) data.length);

                int x = 0;
                e.gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                e.gc.fillRectangle(x, 0, interpreterPixels, bounds.height);
                x += interpreterPixels;
                e.gc.setBackground(codeBackground);
                e.gc.fillRectangle(x, 0, codePixels, bounds.height);
                x += codePixels;
                e.gc.setBackground(variablesBackground);
                e.gc.fillRectangle(x, 0, variablesPixels, bounds.height);
                x += variablesPixels;
                e.gc.setBackground(stackFreeBackground);
                e.gc.fillRectangle(x, 0, bounds.width - x, bounds.height);
            }

        });

        group = new Composite(container, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Interpreter");
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(30), SWT.DEFAULT));
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        label.setText(String.format("%d bytes", pbase));
        label = new Label(group, SWT.BORDER);
        label.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(5), SWT.DEFAULT));

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Code / Data");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        label.setText(String.format("%d bytes", vbase - pbase));
        label = new Label(group, SWT.BORDER);
        label.setBackground(codeBackground);
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(5), SWT.DEFAULT));

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Variables");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        label.setText(String.format("%d bytes", dbase - vbase));
        label = new Label(group, SWT.BORDER);
        label.setLayoutData(new GridData(convertWidthInCharsToPixels(5), SWT.DEFAULT));
        label.setBackground(variablesBackground);

        label = new Label(group, SWT.NONE);
        label.setText("Stack / Free");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setText(String.format("%d bytes", data.length - dbase));
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

        StringBuilder sb = new StringBuilder();
        switch (cm_ss.getValue(clkmode)) {
            case 0b00:
                sb.append("RCFAST");
                break;
            case 0b01:
                sb.append("RCSLOW");
                break;
            case 0b10:
                sb.append("XIN");
                break;
            case 0b11:
                sb.append("XTAL" + (cm_cc.getValue(clkmode) + 1));
                if (cm_pll.getValue(clkmode) == 1) {
                    sb.append("+PLL" + (cm_xi_div.getValue(clkmode) + 1) + "X");
                }
                break;
        }
        label.setText(sb.toString());

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Clock Freq.");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        label.setText(format.format(clkfreq) + " Hz");

        label = new Label(group, SWT.NONE);
        label.setText("XIN Freq.");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        int value = clkfreq / (cm_vco_mul.getValue(clkmode) + 1) * (cm_xi_div.getValue(clkmode) + 1);
        label.setText(format.format(value) + " Hz");
    }

    public void createMemoryView(Composite parent) {
        Font dialogFont = parent.getFont();
        FontData[] fontData = dialogFont.getFontData();
        if ("win32".equals(SWT.getPlatform())) {
            font = new Font(Display.getDefault(), "Courier New", fontData[0].getHeight(), SWT.NONE);
        }
        else {
            font = new Font(Display.getDefault(), "mono", fontData[0].getHeight(), SWT.NONE);
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

    public void setObject(Spin2Object object) {
        this.object = object;

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            object.generateBinary(os);

            data = new byte[512 * 1024];
            System.arraycopy(os.toByteArray(), 0, data, 0, Math.min(data.length, os.size()));

            clkfreq = object.getClkFreq();
            clkmode = object.getClkMode();

            if (object.getInterpreter() != null) {
                pbase = readLong(0x30);
                vbase = readLong(0x34);
                dbase = readLong(0x38);
            }
            else {
                pbase = 0;
                vbase = dbase = object.getSize();
            }
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

}
