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
import com.maccasoft.propeller.spin2.Spin2Object;

public class P2MemoryDialog extends Dialog {

    public static final int BYTES_PER_ROW = 16;

    Display display;

    ObjectBrowser objectTree;
    CTabFolder tabFolder;
    Canvas canvas;
    StyledText styledText;

    Font font;
    FontMetrics fontMetrics;
    Color codeBackground;
    Color variablesBackground;
    Color stackFreeBackground;

    Spin2Object object;
    ObjectTree tree;
    boolean topObject;

    byte[] data;

    int clkfreq;
    int clkmode;
    int pbase;
    int vbase;
    int dbase;

    int dbgsize;

    NumberFormat format;

    public P2MemoryDialog(Shell parentShell) {
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

        objectTree = new ObjectBrowser(container);
        objectTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData) objectTree.getLayoutData()).heightHint = objectTree.getTree().getItemHeight() * 10;
        objectTree.setInput(tree, topObject);

        Composite group = new Composite(container, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        Label label = new Label(group, SWT.NONE);
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

        if (dbgsize != 0) {
            label = new Label(group, SWT.NONE);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            label.setText("Debugger");
            label.setLayoutData(new GridData(convertWidthInCharsToPixels(30), SWT.DEFAULT));
            label = new Label(group, SWT.NONE);
            label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
            label.setText(String.format("%d bytes", dbgsize));
            label = new Label(group, SWT.BORDER);
            label.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            label.setLayoutData(new GridData(convertWidthInCharsToPixels(5), SWT.DEFAULT));
        }

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
        switch (Spin2Object.cm_ss.getValue(clkmode)) {
            case 0b00:
                sb.append("RCFAST");
                break;
            case 0b01:
                sb.append("RCSLOW");
                break;
            case 0b10:
                sb.append("XTAL" + Spin2Object.cm_cc.getValue(clkmode));
                break;
            case 0b11:
                sb.append("XTAL" + Spin2Object.cm_cc.getValue(clkmode));
                if (Spin2Object.cm_pll.isSet(clkmode)) {
                    sb.append("+PLL" + ((Spin2Object.cm_vco_mul.getValue(clkmode) + 1) / (Spin2Object.cm_xi_div.getValue(clkmode) + 1)) + "X");
                }
                break;
        }
        label.setText(sb.toString());

        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label.setText("Clock Freq.");
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        switch (Spin2Object.cm_ss.getValue(clkmode)) {
            case 0b00:
                label.setText("~" + format.format(20000000) + " Hz");
                break;
            case 0b01:
                label.setText("~" + format.format(20000) + " Hz");
                break;
            case 0b10:
            case 0b11:
                label.setText(format.format(clkfreq) + " Hz");
                break;
        }

        label = new Label(group, SWT.NONE);
        label.setText("XIN Freq.");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        switch (Spin2Object.cm_ss.getValue(clkmode)) {
            case 0b00:
            case 0b01:
                label.setText("Ignored");
                break;
            case 0b10:
                label.setText(format.format(clkfreq * (Spin2Object.cm_xi_div.getValue(clkmode) + 1)) + " Hz");
                break;
            case 0b11:
                label.setText(format.format(clkfreq / (Spin2Object.cm_vco_mul.getValue(clkmode) + 1) * (Spin2Object.cm_xi_div.getValue(clkmode) + 1)) + " Hz");
                break;
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
                        if (addr1 >= dbase + dbgsize) {
                            e.gc.setBackground(stackFreeBackground);
                        }
                        else if (addr1 >= vbase + dbgsize) {
                            e.gc.setBackground(variablesBackground);
                        }
                        else if (addr1 >= pbase + dbgsize) {
                            e.gc.setBackground(codeBackground);
                        }
                        else if (addr1 >= dbgsize) {
                            e.gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                        }
                        else {
                            e.gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
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
        int selection = ((pbase + dbgsize) / BYTES_PER_ROW) * BYTES_PER_ROW;
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

    public Spin2Object getObject() {
        return object;
    }

    public void setObject(Spin2Object object, ObjectTree tree, boolean topObject) {
        this.object = object;

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            object.generateBinary(os);

            data = new byte[512 * 1024];
            System.arraycopy(os.toByteArray(), 0, data, 0, Math.min(data.length, os.size()));

            clkfreq = object.getClkFreq();
            clkmode = object.getClkMode();

            if (object.getDebugger() != null) {
                dbgsize = object.getDebugger().getSize() + object.getDebugData().getSize();
            }

            if (object.getInterpreter() != null) {
                pbase = readLong(dbgsize + 0x30) & 0xFFFFF;
                vbase = readLong(dbgsize + 0x34) & 0xFFFFF;
                dbase = readLong(dbgsize + 0x38) & 0xFFFFF;
            }
            else {
                vbase = dbase = object.getSize();
            }

            this.tree = tree;
            this.topObject = topObject;

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
