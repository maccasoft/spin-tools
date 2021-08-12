/*
 * Copyright (c) 2016-18 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

public class SerialTerminal {

    public static final int FRAME_TIMER = 16;

    Window window;
    Composite container;
    Canvas canvas;

    Combo terminalType;
    Combo baudRate;
    Button dtr;
    Button dsr;
    Button rts;
    Button cts;

    Font font;
    FontMetrics fontMetrics;

    PaletteData paletteData;
    Image image;
    Rectangle imageBounds;
    AtomicBoolean needsRedraw;

    int cx;
    int cy;

    boolean cursorState;
    Color foreground;
    Color background;

    SerialPort serialPort;
    int serialBaudRate;

    static List<Integer> baudRates = Arrays.asList(new Integer[] {
        300, 600, 1200, 2400, 4800, 9600, 19200, 31250, 38400, 57600, 115200,
        230400, 250000, 460800, 9216000, 1000000, 1500000, 2000000, 3000000
    });

    TerminalEmulation emulation;

    final Runnable screenUpdateRunnable = new Runnable() {

        int counter;

        @Override
        public void run() {
            if (canvas.isDisposed()) {
                return;
            }
            counter++;
            if (counter >= 15) {
                cursorState = !cursorState;
                needsRedraw.set(true);
                counter = 0;
            }
            if (needsRedraw.getAndSet(false)) {
                canvas.redraw();
            }
            Display.getDefault().timerExec(FRAME_TIMER, this);
        }
    };

    SerialPortEventListener serialEventListener = new SerialPortEventListener() {

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            if (serialPortEvent.getEventType() == SerialPortEvent.RXCHAR) {
                try {
                    final byte[] rx = serialPort.readBytes();
                    if (rx != null) {
                        drawString(new String(rx));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    if (serialPortEvent.isCTS()) {
                        cts.setSelection((serialPortEvent.getEventValue() & SerialPortEvent.CTS) != 0);
                    }
                    if (serialPortEvent.isDSR()) {
                        dsr.setSelection((serialPortEvent.getEventValue() & SerialPortEvent.DSR) != 0);
                    }
                }

            });
        }
    };

    interface TerminalEmulation {

        public void drawChar(GC gc, char c);
    }

    public class TTY implements TerminalEmulation {

        int p0;

        @Override
        public void drawChar(GC gc, char c) {
            switch (c) {
                case 0x08:
                    if (cx >= fontMetrics.getAverageCharacterWidth()) {
                        cx -= fontMetrics.getAverageCharacterWidth();
                    }
                    else {
                        cx = 0;
                    }
                    break;
                case 0x09:
                    p0 = (int) (cx / fontMetrics.getAverageCharacterWidth());
                    p0 = ((p0 / 8) + 1) * 8;
                    if (p0 > (imageBounds.width / fontMetrics.getAverageCharacterWidth())) {
                        p0 = 0;
                    }
                    cx = (int) (p0 * fontMetrics.getAverageCharacterWidth());
                    break;
                case 0x0A:
                    cy += fontMetrics.getHeight();
                    if (cy >= imageBounds.height) {
                        gc.copyArea(0, fontMetrics.getHeight(), imageBounds.width, imageBounds.height - fontMetrics.getHeight(), 0, 0);
                        gc.fillRectangle(0, imageBounds.height - fontMetrics.getHeight(), imageBounds.width, fontMetrics.getHeight());
                        cy -= fontMetrics.getHeight();
                    }
                    break;
                case 0x0C:
                    cx = cy = 0;
                    gc.fillRectangle(imageBounds);
                    break;
                case 0x0D:
                    cx = 0;
                    break;
                default:
                    if (c >= ' ' && c <= 0x7F) {
                        if (cx >= imageBounds.width) {
                            cx = 0;
                            cy += fontMetrics.getHeight();
                            if (cy >= imageBounds.height) {
                                gc.copyArea(0, fontMetrics.getHeight(), imageBounds.width, imageBounds.height - fontMetrics.getHeight(), 0, 0);
                                gc.fillRectangle(0, imageBounds.height - fontMetrics.getHeight(), imageBounds.width, fontMetrics.getHeight());
                                cy -= fontMetrics.getHeight();
                            }
                        }

                        gc.drawString(String.valueOf(c), cx, cy);

                        cx += gc.getAdvanceWidth(c);
                    }
                    break;
            }
        }
    }

    class ParallaxSerialTerminal implements TerminalEmulation {

        int state = 0;
        int cmd, p0, p1;

        @Override
        public void drawChar(GC gc, char c) {
            if (cmd == 2) { // PC: Position Cursor in x,y
                if (state == 0) {
                    p0 = (int) Math.min(c, imageBounds.width / fontMetrics.getAverageCharacterWidth());
                    state++;
                    return;
                }
                p1 = Math.min(c, imageBounds.height / fontMetrics.getHeight());
                cx = (int) (p0 * fontMetrics.getAverageCharacterWidth());
                cy = p1 * fontMetrics.getHeight();
                cmd = 0;
                return;
            }
            else if (cmd == 14) { // PX: Position cursor in X
                cx = (int) (Math.min(c, imageBounds.width / fontMetrics.getAverageCharacterWidth()) * fontMetrics.getAverageCharacterWidth());
                cmd = 0;
                return;
            }
            else if (cmd == 15) { // PY: Position cursor in Y
                cy = Math.min(c, imageBounds.height / fontMetrics.getHeight()) * fontMetrics.getHeight();
                cmd = 0;
                return;
            }
            switch (c) {
                case 1: // HM: HoMe cursor
                    cx = cy = 0;
                    break;

                case 2: // PC: Position Cursor in x,y
                case 14: // PX: Position cursor in X
                case 15: // PY: Position cursor in Y
                    cmd = c;
                    state = 0;
                    break;

                case 3: // ML: Move cursor Left
                    if (cx >= fontMetrics.getAverageCharacterWidth()) {
                        cx -= fontMetrics.getAverageCharacterWidth();
                    }
                    else {
                        cx = 0;
                    }
                    break;

                case 4: // MR: Move cursor Right
                    if ((cx + fontMetrics.getAverageCharacterWidth()) < imageBounds.width) {
                        cx += fontMetrics.getAverageCharacterWidth();
                    }
                    break;

                case 5: // MU: Move cursor Up
                    if (cy >= fontMetrics.getHeight()) {
                        cy -= fontMetrics.getHeight();
                    }
                    else {
                        cy = 0;
                    }
                    break;

                case 6: // MR: Move cursor Down
                    if ((cy + fontMetrics.getHeight()) < imageBounds.height) {
                        cy += fontMetrics.getHeight();
                    }
                    break;

                case 7: // BP: BeeP speaker
                    // TODO BeeP speaker
                    break;

                case 8: // BS: BackSpace
                    if (cx >= fontMetrics.getAverageCharacterWidth()) {
                        cx -= fontMetrics.getAverageCharacterWidth();
                    }
                    else {
                        cx = 0;
                    }
                    break;

                case 9: // TB: TaB
                    p0 = (int) (cx / fontMetrics.getAverageCharacterWidth());
                    p0 = ((p0 / 8) + 1) * 8;
                    if (p0 > (imageBounds.width / fontMetrics.getAverageCharacterWidth())) {
                        p0 = 0;
                    }
                    cx = (int) (p0 * fontMetrics.getAverageCharacterWidth());
                    break;

                case 10: // LF: Line Feed
                case 13: // NL: New Line
                    cx = 0;
                    cy += fontMetrics.getHeight();
                    if (cy >= imageBounds.height) {
                        gc.copyArea(0, fontMetrics.getHeight(), imageBounds.width, imageBounds.height - fontMetrics.getHeight(), 0, 0);
                        gc.fillRectangle(0, imageBounds.height - fontMetrics.getHeight(), imageBounds.width, fontMetrics.getHeight());
                        cy -= fontMetrics.getHeight();
                    }
                    break;

                case 16: // CS: Clear Screen
                    cx = cy = 0;
                    gc.fillRectangle(imageBounds);
                    break;

                default:
                    if (c >= ' ' && c <= 0x7F) {
                        if (cx >= imageBounds.width) {
                            cx = 0;
                            cy += fontMetrics.getHeight();
                            if (cy >= imageBounds.height) {
                                gc.copyArea(0, fontMetrics.getHeight(), imageBounds.width, imageBounds.height - fontMetrics.getHeight(), 0, 0);
                                gc.fillRectangle(0, imageBounds.height - fontMetrics.getHeight(), imageBounds.width, fontMetrics.getHeight());
                                cy -= fontMetrics.getHeight();
                            }
                        }

                        gc.drawString(String.valueOf(c), cx, cy);

                        cx += gc.getAdvanceWidth(c);
                    }
                    break;
            }
        }
    }

    public SerialTerminal(SerialPort serialPort) {
        this.needsRedraw = new AtomicBoolean();

        window = new Window((Shell) null) {

            @Override
            protected void configureShell(Shell newShell) {
                super.configureShell(newShell);

                Rectangle screen = newShell.getDisplay().getClientArea();
                Rectangle rect = new Rectangle(0, 0, 640, 480);
                rect.x = (screen.width - rect.width) / 2;
                rect.y = (screen.height - rect.height) / 2;
                if (rect.y < 0) {
                    rect.height += rect.y * 2;
                    rect.y = 0;
                }

                newShell.setLocation(rect.x, rect.y);
                newShell.setSize(rect.width, rect.height);

                FillLayout layout = new FillLayout();
                layout.marginWidth = layout.marginHeight = 0;
                newShell.setLayout(layout);

                newShell.addListener(SWT.Traverse, new Listener() {

                    @Override
                    public void handleEvent(Event e) {
                        if (e.detail == SWT.TRAVERSE_ESCAPE) {
                            e.doit = false;
                        }
                    }
                });
            }

            @Override
            protected Control createContents(Composite parent) {
                return SerialTerminal.this.createContents(parent);
            }
        };

        foreground = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
        background = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

        this.serialPort = serialPort;
        this.serialBaudRate = 115200;
        this.emulation = new ParallaxSerialTerminal();
    }

    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        canvas = new Canvas(container, SWT.DOUBLE_BUFFERED);
        canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBottomControls(container);

        if ("win32".equals(SWT.getPlatform())) {
            font = new Font(parent.getDisplay(), "Courier New", 10, SWT.NONE);
        }
        else {
            font = new Font(parent.getDisplay(), "mono", 10, SWT.NONE);
        }
        GC gc = new GC(canvas);
        try {
            gc.setFont(font);
            fontMetrics = gc.getFontMetrics();
        } finally {
            gc.dispose();
        }

        paletteData = new PaletteData(new RGB[] {
            new RGB(0, 0, 0),
            new RGB(0, 0, 170),
            new RGB(170, 0, 0),
            new RGB(170, 0, 170),
            new RGB(0, 170, 0),
            new RGB(0, 170, 170),
            new RGB(170, 170, 0),
            new RGB(170, 170, 170),
            new RGB(85, 85, 85),
            new RGB(0, 0, 255),
            new RGB(255, 0, 0),
            new RGB(255, 0, 255),
            new RGB(0, 255, 0),
            new RGB(0, 255, 255),
            new RGB(255, 255, 0),
            new RGB(255, 255, 255),
        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                try {
                    serialPort.removeEventListener();
                    if (serialPort.isOpened()) {
                        serialPort.closePort();
                    }
                } catch (Exception ex) {

                }
                font.dispose();
            }
        });

        hookListeners();

        return container;
    }

    void hookListeners() {
        canvas.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                Point size = canvas.getSize();

                int bytesPerLine = (((size.x * 8 + 7) / 8) + (4 - 1)) / 4 * 4;
                Image newImage = new Image(canvas.getDisplay(), new ImageData(size.x, size.y, 8, paletteData, 4, new byte[bytesPerLine * size.y]));
                Rectangle newImageBounds = newImage.getBounds();

                GC gc = new GC(newImage);
                try {
                    gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
                    gc.fillRectangle(newImageBounds);
                    if (image != null && !image.isDisposed()) {
                        gc.drawImage(image, 0, 0);
                    }
                } finally {
                    gc.dispose();
                }

                if (image != null) {
                    image.dispose();
                }
                image = newImage;
                imageBounds = newImageBounds;
                imageBounds.width -= (imageBounds.width % fontMetrics.getAverageCharacterWidth());
                imageBounds.height -= (imageBounds.height % fontMetrics.getHeight());
            }
        });

        canvas.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character != 0) {
                    try {
                        if (serialPort.isOpened()) {
                            serialPort.writeByte((byte) e.character);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                if (image == null || image.isDisposed()) {
                    return;
                }
                e.gc.drawImage(image, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
                if (cursorState) {
                    e.gc.setBackground(e.gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
                    e.gc.fillRectangle(cx, cy, (int) fontMetrics.getAverageCharacterWidth(), fontMetrics.getHeight());
                }
            }
        });

        window.getShell().setText("Serial Monitor on " + serialPort.getPortName());

        try {
            if (!serialPort.isOpened()) {
                serialPort.openPort();
                serialPort.setDTR(true);
                serialPort.setRTS(true);
            }
            serialPort.setParams(
                serialBaudRate,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
            serialPort.addEventListener(serialEventListener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Display.getDefault().timerExec(FRAME_TIMER, screenUpdateRunnable);
    }

    void createBottomControls(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        terminalType = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        terminalType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        terminalType.setItems(new String[] {
            "TTY", "Parallax Serial Terminal"
        });

        baudRate = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        baudRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        String[] items = new String[baudRates.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(baudRates.get(i));
        }
        baudRate.setItems(items);

        dtr = new Button(container, SWT.CHECK);
        dtr.setText("DTR");
        dsr = new Button(container, SWT.RADIO);
        dsr.setText("DSR");
        rts = new Button(container, SWT.CHECK);
        rts.setText("RTS");
        cts = new Button(container, SWT.RADIO);
        cts.setText("CTS");

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        GridLayout layout = new GridLayout(container.getChildren().length, false);
        layout.marginBottom = layout.marginHeight;
        layout.marginLeft = layout.marginRight = layout.marginWidth;
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        baudRate.select(baudRates.indexOf(serialBaudRate));
        baudRate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    serialBaudRate = baudRates.get(baudRate.getSelectionIndex());
                    serialPort.setParams(
                        serialBaudRate,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                canvas.setFocus();
            }
        });

        if (emulation instanceof ParallaxSerialTerminal) {
            terminalType.select(1);
        }
        else {
            terminalType.select(0);
        }
        terminalType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                switch (terminalType.getSelectionIndex()) {
                    case 0:
                        emulation = new TTY();
                        break;
                    case 1:
                        emulation = new ParallaxSerialTerminal();
                        break;
                }
                canvas.setFocus();
            }
        });
    }

    public Control getControl() {
        return canvas;
    }

    public void open() {
        window.open();
        window.getShell().setData(this);
    }

    public void close() {
        window.close();
    }

    public void drawString(String s) {
        GC gc = new GC(image);
        try {
            gc.setFont(font);
            gc.setForeground(foreground);
            gc.setBackground(background);
            for (char c : s.toCharArray()) {
                emulation.drawChar(gc, c);
            }
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    if (!needsRedraw.getAndSet(true)) {
                        canvas.redraw();
                    }
                }

            });
        } finally {
            gc.dispose();
        }
    }

    public void drawChar(char c) {
        GC gc = new GC(image);
        try {
            gc.setFont(font);
            gc.setForeground(foreground);
            gc.setBackground(background);
            emulation.drawChar(gc, c);
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    if (!needsRedraw.getAndSet(true)) {
                        canvas.redraw();
                    }
                }

            });
        } finally {
            gc.dispose();
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        try {
            if (this.serialPort != null) {
                if (this.serialPort.isOpened()) {
                    this.serialPort.removeEventListener();
                }
            }
            this.serialPort = serialPort;
            if (this.serialPort != null) {
                if (!this.serialPort.isOpened()) {
                    this.serialPort.openPort();
                    serialPort.setDTR(true);
                    serialPort.setRTS(true);
                }
                this.serialPort.addEventListener(serialEventListener);
                this.serialPort.setParams(
                    serialBaudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
                window.getShell().setText("Serial Monitor on " + this.serialPort.getPortName());
            }
            terminalType.setEnabled(serialPort != null);
            baudRate.setEnabled(serialPort != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
