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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

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
import org.eclipse.swt.graphics.GC;
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
import jssc.SerialPortException;

public class SerialTerminal {

    public static final String WINDOW_TITLE = "Serial Terminal";
    public static final int FRAME_TIMER = 16;

    Display display;
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
    int characterWidth;
    int characterHeight;

    PaletteData paletteData;

    int cx;
    int cy;
    int screenWidth;
    int screenHeight;

    Color foreground;
    Color background;
    boolean cursorState;

    SerialPort serialPort;
    int serialBaudRate;

    static List<Integer> baudRates = Arrays.asList(new Integer[] {
        300, 600, 1200, 2400, 4800, 9600, 19200, 31250, 38400, 57600, 115200,
        230400, 250000, 460800, 9216000, 1000000, 1500000, 2000000, 3000000
    });

    TerminalEmulation emulation;

    class Cell {
        char character;
        Color foreground;
        Color background;

        public Cell(Color foreground, Color background) {
            this.character = ' ';
            this.foreground = foreground;
            this.background = background;
        }

    }

    Cell[][] screen = new Cell[0][0];

    final AtomicReference<Rectangle> redrawRectangle = new AtomicReference<Rectangle>();

    final Runnable redrawRunnable = new Runnable() {

        @Override
        public void run() {
            Rectangle rect = redrawRectangle.getAndSet(null);
            if (rect != null) {
                canvas.redraw(rect.x, rect.y, rect.width, rect.height, false);
            }
        }

    };

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
                counter = 0;
                redraw(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);
            }
            Display.getDefault().timerExec(FRAME_TIMER, this);
        }
    };

    SerialPortEventListener serialEventListener = new SerialPortEventListener() {

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            switch (serialPortEvent.getEventType()) {
                case SerialPortEvent.RXCHAR:
                    try {
                        final byte[] rx = serialPort.readBytes();
                        if (rx != null) {
                            drawString(new String(rx));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SerialPortEvent.DSR:
                    display.syncExec(new Runnable() {

                        @Override
                        public void run() {
                            dsr.setSelection((serialPortEvent.getEventValue() & SerialPortEvent.DSR) != 0);
                        }

                    });
                    break;
                case SerialPortEvent.CTS:
                    display.syncExec(new Runnable() {

                        @Override
                        public void run() {
                            cts.setSelection((serialPortEvent.getEventValue() & SerialPortEvent.CTS) != 0);
                        }

                    });
                    break;
            }
        }
    };

    interface TerminalEmulation {

        public void drawChar(char c);
    }

    public class TTY implements TerminalEmulation {

        int p0;

        @Override
        public void drawChar(char c) {
            redraw(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);

            switch (c) {
                case 7:
                    display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            display.beep();
                        }
                    });
                    break;

                case 0x08:
                    if (cx > 0) {
                        cx--;
                    }
                    break;

                case 0x09:
                    cx = ((cx / 8) + 1) * 8;
                    if (cx > screenWidth) {
                        cx = 0;
                    }
                    break;

                case 0x0A:
                    cy++;
                    if (cy >= screenHeight) {
                        int y;
                        for (y = 1; y < screenHeight; y++) {
                            for (int x = 0; x < screenWidth; x++) {
                                screen[y - 1][x] = screen[y][x];
                            }
                        }
                        for (int x = 0; x < screenWidth; x++) {
                            screen[y - 1][x] = new Cell(foreground, background);
                        }
                        cy--;
                        redraw();
                    }
                    break;

                case 0x0C:
                    for (int y = 0; y < screenHeight; y++) {
                        for (int x = 0; x < screenWidth; x++) {
                            screen[y][x].foreground = foreground;
                            screen[y][x].background = background;
                            screen[y][x].character = ' ';
                        }
                    }
                    cx = cy = 0;
                    redraw();
                    break;

                case 0x0D:
                    cx = 0;
                    break;

                default:
                    if (cx >= screenWidth) {
                        cx = 0;
                        cy++;
                        if (cy >= screenHeight) {
                            int y;
                            for (y = 1; y < screenHeight; y++) {
                                for (int x = 0; x < screenWidth; x++) {
                                    screen[y - 1][x] = screen[y][x];
                                }
                            }
                            for (int x = 0; x < screenWidth; x++) {
                                screen[y - 1][x] = new Cell(foreground, background);
                            }
                            cy--;
                            redraw();
                        }
                        else {
                            redraw(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);
                        }
                    }
                    screen[cy][cx].character = c;
                    cx++;
                    break;
            }

            redraw(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);
        }
    }

    class ParallaxSerialTerminal implements TerminalEmulation {

        int state = 0;
        int cmd, p0, p1;

        @Override
        public void drawChar(char c) {
            redraw(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);

            if (cmd == 2) { // PC: Position Cursor in x,y
                if (state == 0) {
                    p0 = Math.min(c, screenWidth);
                    state++;
                    return;
                }
                cy = Math.min(c, screenHeight);
                cx = p0;
                cmd = 0;
                return;
            }
            else if (cmd == 14) { // PX: Position cursor in X
                cx = Math.min(c, screenWidth);
                cmd = 0;
                return;
            }
            else if (cmd == 15) { // PY: Position cursor in Y
                cy = Math.min(c, screenHeight);
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
                    if (cx > 0) {
                        cx--;
                    }
                    break;

                case 4: // MR: Move cursor Right
                    if (cx < screenWidth - 1) {
                        cx++;
                    }
                    break;

                case 5: // MU: Move cursor Up
                    if (cy > 0) {
                        cy--;
                    }
                    break;

                case 6: // MR: Move cursor Down
                    if (cy < screenHeight - 1) {
                        cy++;
                    }
                    break;

                case 7: // BP: BeeP speaker
                    display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            display.beep();
                        }
                    });
                    break;

                case 8: // BS: BackSpace
                    if (cx > 0) {
                        cx--;
                    }
                    break;

                case 9: // TB: TaB
                    cx = ((cx / 8) + 1) * 8;
                    if (cx > screenWidth) {
                        cx = 0;
                    }
                    break;

                case 10: // LF: Line Feed
                case 13: // NL: New Line
                    cx = 0;
                    cy++;
                    if (cy >= screenHeight) {
                        int y;
                        for (y = 1; y < screenHeight; y++) {
                            for (int x = 0; x < screenWidth; x++) {
                                screen[y - 1][x] = screen[y][x];
                            }
                        }
                        for (int x = 0; x < screenWidth; x++) {
                            screen[y - 1][x] = new Cell(foreground, background);
                        }
                        cy--;
                        redraw();
                    }
                    break;

                case 16: // CS: Clear Screen
                    for (int y = 0; y < screenHeight; y++) {
                        for (int x = 0; x < screenWidth; x++) {
                            screen[y][x].foreground = foreground;
                            screen[y][x].background = background;
                            screen[y][x].character = ' ';
                        }
                    }
                    cx = cy = 0;
                    redraw();
                    break;

                default:
                    if (cx >= screenWidth) {
                        cx = 0;
                        cy++;
                        if (cy >= screenHeight) {
                            int y;
                            for (y = 1; y < screenHeight; y++) {
                                for (int x = 0; x < screenWidth; x++) {
                                    screen[y - 1][x] = screen[y][x];
                                }
                            }
                            for (int x = 0; x < screenWidth; x++) {
                                screen[y - 1][x] = new Cell(foreground, background);
                            }
                            cy--;
                            redraw();
                        }
                        else {
                            redraw(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);
                        }
                    }
                    screen[cy][cx].character = c;
                    cx++;
                    break;
            }

            redraw(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);
        }
    }

    public SerialTerminal(SerialPort serialPort) {

        display = Display.getDefault();

        window = new Window((Shell) null) {

            @Override
            protected void configureShell(Shell newShell) {
                super.configureShell(newShell);

                newShell.setText(WINDOW_TITLE);

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

        foreground = display.getSystemColor(SWT.COLOR_WHITE);
        background = display.getSystemColor(SWT.COLOR_BLACK);

        this.serialPort = serialPort;
        this.serialBaudRate = 115200;
        this.emulation = new TTY();
    }

    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        if ("win32".equals(SWT.getPlatform())) {
            font = new Font(parent.getDisplay(), "Courier New", 10, SWT.NONE);
        }
        else {
            font = new Font(parent.getDisplay(), "mono", 10, SWT.NONE);
        }

        canvas = new Canvas(container, SWT.DOUBLE_BUFFERED);
        canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        canvas.setFont(font);

        GC gc = new GC(canvas);
        try {
            Point pt = gc.textExtent("M");
            characterWidth = pt.x;
            characterHeight = pt.y;
        } finally {
            gc.dispose();
        }

        createBottomControls(container);

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

        foreground = new Color(display, paletteData.getRGB(7));
        background = new Color(display, paletteData.getRGB(0));
        canvas.setBackground(background);

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
                int width = size.x / characterWidth;
                int height = size.y / characterHeight;

                Cell[][] newScreen = new Cell[height][width];

                int y = 0;
                while (y < Math.min(screenHeight, height)) {
                    int x = 0;
                    while (x < Math.min(screenWidth, width)) {
                        newScreen[y][x] = screen[y][x];
                        x++;
                    }
                    while (x < width) {
                        newScreen[y][x] = new Cell(foreground, background);
                        x++;
                    }
                    y++;
                }
                while (y < height) {
                    for (int x = 0; x < width; x++) {
                        newScreen[y][x] = new Cell(foreground, background);
                    }
                    y++;
                }

                screenWidth = width;
                screenHeight = height;
                screen = newScreen;
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
                int y0 = e.y / characterHeight;
                int y1 = Math.min((e.y + e.height) / characterHeight, screenHeight - 1);
                int x0 = e.x / characterWidth;
                int x1 = Math.min((e.x + e.width) / characterWidth, screenWidth - 1);

                for (int y = y1, cy = y * characterHeight; y >= y0; y--, cy -= characterHeight) {
                    for (int x = x1, cx = x * characterWidth; x >= x0; x--, cx -= characterWidth) {
                        Cell cell = screen[y][x];
                        e.gc.setForeground(cell.foreground);
                        e.gc.setBackground(cell.background);
                        e.gc.drawString(String.valueOf(cell.character), cx, cy, false);
                    }
                }
                if (cursorState) {
                    e.gc.setBackground(e.gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
                    e.gc.fillRectangle(Math.min(cx, screenWidth - 1) * characterWidth, cy * characterHeight, characterWidth, characterHeight);
                }
            }
        });

        window.getShell().setText(WINDOW_TITLE + " on " + serialPort.getPortName());

        try {
            if (!serialPort.isOpened()) {
                serialPort.openPort();
            }
            serialPort.setParams(
                serialBaudRate,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE,
                false,
                false);
            serialPort.addEventListener(serialEventListener);
            dsr.setSelection(serialPort.isDSR());
            cts.setSelection(serialPort.isCTS());
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
        dtr.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    serialPort.setDTR(dtr.getSelection());
                } catch (SerialPortException e1) {
                    e1.printStackTrace();
                }
                canvas.setFocus();
            }
        });
        dsr = new Button(container, SWT.RADIO);
        dsr.setText("DSR");
        rts = new Button(container, SWT.CHECK);
        rts.setText("RTS");
        rts.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    serialPort.setRTS(rts.getSelection());
                } catch (SerialPortException e1) {
                    e1.printStackTrace();
                }
                canvas.setFocus();
            }
        });
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
        for (char c : s.toCharArray()) {
            emulation.drawChar(c);
        }
    }

    public void drawChar(char c) {
        emulation.drawChar(c);
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
                }
                this.serialPort.addEventListener(serialEventListener);
                this.serialPort.setParams(
                    serialBaudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE,
                    false,
                    false);
                window.getShell().setText(WINDOW_TITLE + " on " + this.serialPort.getPortName());
            }
            terminalType.setEnabled(serialPort != null);
            baudRate.setEnabled(serialPort != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void redraw() {
        Rectangle rect = redrawRectangle.getAndUpdate(new UnaryOperator<Rectangle>() {

            @Override
            public Rectangle apply(Rectangle t) {
                if (t == null) {
                    return new Rectangle(0, 0, screenWidth * characterWidth, screenHeight * characterHeight);
                }
                return t.union(new Rectangle(0, 0, screenWidth * characterWidth, screenHeight * characterHeight));
            }
        });
        if (rect == null) {
            display.asyncExec(redrawRunnable);
        }
    }

    void redraw(int x, int y, int width, int height) {
        Rectangle rect = redrawRectangle.getAndUpdate(new UnaryOperator<Rectangle>() {

            @Override
            public Rectangle apply(Rectangle t) {
                if (t == null) {
                    return new Rectangle(x, y, width, height);
                }
                return t.union(new Rectangle(x, y, width, height));
            }
        });
        if (rect == null) {
            display.asyncExec(redrawRunnable);
        }
    }
}
