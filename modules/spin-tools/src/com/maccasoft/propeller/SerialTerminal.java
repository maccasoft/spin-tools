/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Platform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortEvent;
import com.maccasoft.propeller.devices.ComPortEventListener;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.DeviceDescriptor;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.NetworkUtils;
import com.maccasoft.propeller.internal.BusyIndicator;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.PngImageTransfer;

import jssc.SerialPort;

public class SerialTerminal {

    public static final String WINDOW_TITLE = "Serial Terminal";

    public static final int CURSOR_OFF = 0x00;
    public static final int CURSOR_ON = 0x04;
    public static final int CURSOR_ULINE = 0x02;
    public static final int CURSOR_BLOCK = 0x00;
    public static final int CURSOR_FLASH = 0x01;
    public static final int CURSOR_SOLID = 0x00;

    public static final int CURSOR_DISPLAY = 0x1000;

    public static final int FRAME_TIMER = 16;

    public static final int BACKBUFFER_LINES = 500;

    Display display;
    Shell shell;

    Composite lineInputGroup;
    Combo lineInput;
    Canvas canvas;

    Combo terminalType;
    Combo baudRate;
    Button clear;
    Button reset;
    Button monitor;
    Button taqoz;

    Font font;
    int characterWidth;
    int characterHeight;

    int cx;
    int cy;
    int screenWidth;
    int screenHeight;

    Color foreground;
    Color background;
    int cursorState;

    ComPort comPort;

    int serialBaudRate;
    boolean localEcho;
    int historyIndex;

    Preferences preferences;

    static List<Integer> baudRates = Arrays.asList(new Integer[] {
        300, 600, 1200, 2400, 4800, 9600, 19200, 31250, 38400, 57600, 115200,
        230400, 250000, 460800, 921600, 1000000, 1500000, 1843200, 2000000, 3000000
    });

    Color[] colors = new Color[] {
        new Color(0, 0, 0),
        new Color(170, 0, 0),
        new Color(0, 170, 0),
        new Color(170, 170, 0),
        new Color(0, 0, 170),
        new Color(170, 0, 170),
        new Color(0, 170, 170),
        new Color(170, 170, 170),
        new Color(85, 85, 85),
        new Color(255, 0, 0),
        new Color(0, 255, 0),
        new Color(255, 255, 0),
        new Color(0, 0, 255),
        new Color(255, 0, 255),
        new Color(0, 255, 255),
        new Color(255, 255, 255),
    };

    TerminalEmulation emulation;

    static class Cell {
        char character;
        Color foreground;
        Color background;

        Cell(Color foreground, Color background) {
            this.character = ' ';
            this.foreground = foreground;
            this.background = background;
        }

        void set(char character, Color foreground, Color background) {
            this.character = character;
            this.foreground = foreground;
            this.background = background;
        }

        void set(Cell cell) {
            this.character = cell.character;
            this.foreground = cell.foreground;
            this.background = cell.background;
        }

    }

    int topRow;
    Cell[][] screen = new Cell[0][0];
    Rectangle selectionRectangle;

    int frameCounter;

    final AtomicReference<Rectangle> redrawRectangle = new AtomicReference<Rectangle>();

    final Runnable redrawRunnable = new Runnable() {

        @Override
        public void run() {
            Rectangle rect = redrawRectangle.getAndSet(null);
            if (rect != null && !canvas.isDisposed()) {
                int x = rect.x * characterWidth;
                int width = Math.min(rect.width, screenWidth) * characterWidth;
                int top = (rect.y - topRow) * characterHeight;
                int bottom = top + (rect.height * characterHeight);
                canvas.redraw(x, top, width, bottom - top, false);
                canvas.update();
            }
        }

    };

    final Runnable screenUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            if (canvas.isDisposed()) {
                return;
            }
            if (lineInputGroup.getVisible()) {
                return;
            }
            frameCounter++;
            if (frameCounter >= 15) {
                if ((cursorState & CURSOR_FLASH) != 0) {
                    cursorState ^= CURSOR_DISPLAY;
                    redraw(cx, cy, 1, 1);
                }
                frameCounter = 0;
            }
            display.timerExec(FRAME_TIMER, this);
        }
    };

    ComPortEventListener serialEventListener = new ComPortEventListener() {

        int index = 0, max = 0;
        byte[] buf = new byte[4];

        @Override
        public void serialEvent(ComPortEvent comPortEvent) {
            ComPort comPort = comPortEvent.getComPort();
            if (comPortEvent.isRXCHAR()) {
                try {
                    byte[] rx = comPort.readBytes();
                    for (int i = 0; i < rx.length; i++) {
                        byte b = rx[i];
                        if (index == 0) {
                            if ((b & 0b111_00000) == 0b110_00000) {
                                buf[index++] = b;
                                max = 2;
                            }
                            else if ((b & 0b1111_0000) == 0b1110_0000) {
                                buf[index++] = b;
                                max = 3;
                            }
                            else if ((b & 0b11111_000) == 0b11110_000) {
                                buf[index++] = b;
                                max = 4;
                            }
                            else {
                                write((char) b);
                            }
                        }
                        else {
                            buf[index++] = b;
                            if (index >= max) {
                                write(new String(buf, 0, max).charAt(0));
                                index = 0;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    abstract class TerminalEmulation {

        void write(String s) {
            for (int i = 0; i < s.length(); i++) {
                write(s.charAt(i));
            }
        }

        abstract void write(char c);
    }

    class TTY extends TerminalEmulation {

        public TTY() {
            foreground = colors[7];
            background = colors[0];
        }

        @Override
        public void write(char c) {
            redraw(cx, cy, 1, 1);

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
                    if (cy >= screen.length) {
                        scrollUp(1);
                        cy--;
                        redraw();
                    }
                    break;

                case 0x0C:
                    scrollUp(screenHeight);
                    redraw();
                    cx = 0;
                    cy = screen.length - screenHeight;
                    break;

                case 0x0D:
                    cx = 0;
                    break;

                default:
                    if (cx >= screenWidth) {
                        cx = 0;
                        cy++;
                        if (cy >= screen.length) {
                            scrollUp(1);
                            cy--;
                            redraw();
                        }
                    }
                    screen[cy][cx].set(c, foreground, background);
                    redraw(cx, cy, 1, 1);
                    cx++;
                    break;
            }

            redraw(cx, cy, 1, 1);
        }
    }

    class ANSI extends TTY {

        int state = 0;
        int idx, argc, fg = 7, bg = 0;
        int[] args = new int[16];
        char prefix;
        int savedCx, savedCy;

        @Override
        public void write(char c) {
            if (c == 0x1B) {
                argc = idx = 0;
                args[0] = 0;
                prefix = 0;
                state = 1;
                return;
            }
            switch (state) {
                case 0:
                    super.write(c);
                    break;

                case 1:
                    switch (c) {
                        case '[':
                            state = 2;
                            break;
                        case 'A':
                            if (cy > screen.length) {
                                cy--;
                            }
                            state = 0;
                            break;
                        case 'B':
                            if (cy < (screen.length - screenHeight)) {
                                cy++;
                            }
                            state = 0;
                            break;
                        case 'C':
                            if (cx < screenWidth) {
                                cy++;
                            }
                            state = 0;
                            break;
                        case 'D':
                            if (cx > 0) {
                                cx--;
                            }
                            state = 0;
                            break;
                        default:
                            super.write(c);
                            break;
                    }
                    break;

                case 2:
                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            if (idx == argc) {
                                argc++;
                                args[idx] = 0;
                            }
                            args[idx] = args[idx] * 10 + (c - '0');
                            break;
                        case ';':
                            argc++;
                            idx++;
                            args[idx] = 0;
                            break;
                        case '?':
                            prefix = c;
                            break;

                        case 'A':
                            cy = screen.length - Math.max(cy - (argc == 0 || args[0] == 0 ? 1 : args[0]), 0);
                            state = 0;
                            break;
                        case 'B':
                            cy = screen.length - Math.min(cy + (argc == 0 || args[0] == 0 ? 1 : args[0]), screenHeight - 1);
                            state = 0;
                            break;
                        case 'C':
                            cx = Math.min(cx + (argc == 0 || args[0] == 0 ? 1 : args[0]), screenWidth - 1);
                            state = 0;
                            break;
                        case 'D':
                            cx = Math.max(cx - (argc == 0 || args[0] == 0 ? 1 : args[0]), 0);
                            state = 0;
                            break;

                        case 'H':
                        case 'f':
                            redraw(cx, cy, 1, 1);
                            if (argc == 0) {
                                cx = 0;
                                cy = screen.length - screenHeight;
                            }
                            else if (argc >= 2) {
                                cy = (screen.length - screenHeight) + Math.min(args[0] > 0 ? (args[0] - 1) : 0, screenHeight - 1);
                                cx = Math.min(args[1] > 0 ? (args[1] - 1) : 0, screenWidth - 1);
                            }
                            redraw(cx, cy, 1, 1);
                            state = 0;
                            break;

                        case 'J': {
                            redraw(cx, cy, 1, 1);
                            if (argc == 0 || args[0] == 0) { // Erase from cursor until end of screen
                                for (int x = cx; x < screenWidth; x++) {
                                    screen[cy][x].set(' ', foreground, background);
                                }
                                for (int y = cy + 1; y < screen.length; y++) {
                                    for (int x = cx; x < screenWidth; x++) {
                                        screen[y][x].set(' ', foreground, background);
                                    }
                                }
                                redraw(0, cy, screenWidth, screen.length - cy);
                            }
                            else if (args[0] == 1) { // Erase from cursor to beginning of screen
                                for (int y = screen.length - screenHeight; y < cy; y++) {
                                    for (int x = 0; x < screenWidth; x++) {
                                        screen[y][x].set(' ', foreground, background);
                                    }
                                }
                                for (int x = 0; x <= cx; x++) {
                                    screen[cy][x].set(' ', foreground, background);
                                }
                                redraw(0, screen.length - screenHeight, screenWidth, cy - (screen.length - screenHeight) + 1);
                            }
                            else if (args[0] == 2) { // Erase entire screen
                                for (int y = screen.length - screenHeight; y < screen.length; y++) {
                                    for (int x = 0; x < screenWidth; x++) {
                                        screen[y][x].set(' ', foreground, background);
                                    }
                                }
                                redraw(0, screen.length - screenHeight, screenWidth, screenHeight);
                            }
                            state = 0;
                            break;
                        }

                        case 'K': {
                            cursorState &= ~CURSOR_DISPLAY;
                            frameCounter = 0;
                            redraw(cx, cy, 1, 1);
                            if (argc == 0 || args[0] == 0) { // Erase from cursor to end of line
                                for (int x = cx; x < screenWidth; x++) {
                                    screen[cy][x].set(' ', foreground, background);
                                }
                            }
                            else if (args[0] == 1) { // Erase from start of line to cursor
                                for (int x = 0; x <= cx; x++) {
                                    screen[cy][x].set(' ', foreground, background);
                                }
                            }
                            else if (args[0] == 2) { // Erase entire line
                                for (int x = 0; x < screenWidth; x++) {
                                    screen[cy][x].set(' ', foreground, background);
                                }
                            }
                            redraw(0, cy, screenWidth, 1);
                            state = 0;
                            break;
                        }

                        case 'm':
                            for (int i = 0; i < argc; i++) {
                                if (args[i] == 0) {
                                    bg = 0;
                                    fg = 7;
                                }
                                else if (args[i] == 1) {
                                    fg |= 8;
                                }
                                else if (args[i] == 2) {
                                    fg &= 7;
                                }
                                else if (args[i] == 5) {
                                    cursorState |= CURSOR_FLASH;
                                }
                                else if (args[i] == 7) {
                                    int t = fg;
                                    fg = bg;
                                    bg = t & 0x07;
                                }
                                else if (args[i] == 25) {
                                    cursorState &= ~CURSOR_FLASH;
                                }
                                else if (args[i] >= 30 && args[i] <= 37) {
                                    fg = (fg & 0x08) | (args[i] - 30);
                                }
                                else if (args[i] >= 40 && args[i] <= 47) {
                                    bg = args[i] - 40;
                                }
                                else if (args[i] >= 90 && args[i] <= 97) {
                                    fg = (args[i] - 90) | 8;
                                }
                                else if (args[i] >= 100 && args[i] <= 107) {
                                    bg = (args[i] - 100) | 8;
                                }
                            }
                            if (argc == 0) {
                                bg = 0;
                                fg = 7;
                            }
                            foreground = colors[fg];
                            background = colors[bg];
                            state = 0;
                            break;

                        case 's':
                            savedCx = cx;
                            savedCy = cy;
                            state = 0;
                            break;
                        case 'u':
                            cx = savedCx;
                            cy = savedCy;
                            state = 0;
                            break;

                        case 'h':
                            if (prefix == '?') {
                                if (args[0] == 25) {
                                    cursorState |= CURSOR_ON;
                                }
                                break;
                            }
                            break;

                        case 'l':
                            if (prefix == '?') {
                                if (args[0] == 25) {
                                    cursorState &= ~CURSOR_ON;
                                }
                                break;
                            }
                            break;

                        case 'n':
                            if (args[0] == 6) {
                                try {
                                    comPort.writeBytes(String.format("\033[%d;%dR", cy - screenHeight, cx).getBytes());
                                } catch (ComPortException e) {
                                    // Do nothing
                                }
                                break;
                            }
                            break;

                        default:
                            state = 0;
                            break;
                    }
                    break;
            }
        }

    }

    class ParallaxSerialTerminal extends TerminalEmulation {

        int state = 0;
        int cmd, p0, p1;

        public ParallaxSerialTerminal() {
            foreground = colors[7];
            background = colors[0];
        }

        @Override
        public void write(char c) {
            redraw(cx, cy, 1, 1);

            if (cmd == 2) { // PC: Position Cursor in x,y
                if (state == 0) {
                    p0 = Math.min(c, screenWidth - 1);
                    state++;
                    return;
                }
                cy = (screen.length - screenHeight) + Math.min(c, screenHeight - 1);
                cx = p0;
                cmd = 0;
                return;
            }
            else if (cmd == 14) { // PX: Position cursor in X
                cx = Math.min(c, screenWidth - 1);
                cmd = 0;
                return;
            }
            else if (cmd == 15) { // PY: Position cursor in Y
                cy = (screen.length - screenHeight) + Math.min(c, screenHeight - 1);
                cmd = 0;
                return;
            }

            switch (c) {
                case 1: // HM: HoMe cursor
                    cx = 0;
                    cy = screen.length - screenHeight;
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
                    if (cy < screen.length - (screenHeight - 1)) {
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
                        screen[cy][cx].character = ' ';
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
                    if (cy >= screen.length) {
                        scrollUp(1);
                        cy--;
                        redraw();
                    }
                    break;

                case 11: // CE: Clear To End of Line
                    for (int x = cx; x < screenWidth; x++) {
                        screen[cy][x].set(' ', foreground, background);
                    }
                    redraw(cx, cy, screenWidth - cx, 1);
                    break;

                case 12: // CB: Clear Lines Below
                    for (int y = cy; y < screen.length; y++) {
                        for (int x = 0; x < screenWidth; x++) {
                            screen[y][x].set(' ', foreground, background);
                        }
                    }
                    redraw(0, cy, screenWidth, screen.length - cy + 1);
                    break;

                case 16: // CS: Clear Screen
                    scrollUp(screenHeight);
                    cx = 0;
                    cy = screen.length - screenHeight;
                    redraw();
                    break;

                default:
                    if (cx >= screenWidth) {
                        cx = 0;
                        cy++;
                        if (cy >= screen.length) {
                            scrollUp(1);
                            cy--;
                            redraw();
                        }
                    }
                    screen[cy][cx].set(c, foreground, background);
                    redraw(cx, cy, 1, 1);
                    cx++;
                    break;
            }

            redraw(cx, cy, 1, 1);
        }
    }

    SelectionAdapter baudRateSelectionListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                serialBaudRate = baudRates.get(baudRate.getSelectionIndex());
                preferences.setTerminalBaudRate(serialBaudRate);
                comPort.setParams(serialBaudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            setFocus();
        }
    };

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_TERMINAL_FONT:
                    Font textFont = JFaceResources.getTextFont();
                    FontData fontData = textFont.getFontData()[0];
                    if (evt.getNewValue() != null) {
                        fontData = StringConverter.asFontData(evt.getNewValue().toString());
                    }
                    if (font != null) {
                        font.dispose();
                    }
                    font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);

                    GC gc = new GC(shell);
                    try {
                        gc.setFont(font);
                        Point pt = gc.textExtent("M");
                        characterWidth = pt.x;
                        characterHeight = pt.y;
                    } finally {
                        gc.dispose();
                    }

                    GridData gridData = (GridData) canvas.getLayoutData();
                    Rectangle rect = preferences.getTerminalWindow();
                    if (rect != null) {
                        gridData.widthHint = rect.width * characterWidth;
                        gridData.heightHint = rect.height * characterHeight;
                    }
                    else {
                        gridData.widthHint = 80 * characterWidth;
                        gridData.heightHint = 30 * characterHeight;
                    }

                    shell.pack();
                    redraw();
                    break;

                case Preferences.PROP_TERMINAL_LINE_INPUT:
                    lineInputGroup.setVisible(((Boolean) evt.getNewValue()).booleanValue());
                    ((GridData) lineInputGroup.getLayoutData()).exclude = !((Boolean) evt.getNewValue()).booleanValue();
                    lineInputGroup.getParent().layout(true);
                    break;

                case Preferences.PROP_TERMINAL_LOCAL_ECHO:
                    localEcho = (Boolean) evt.getNewValue();
                    break;

                case Preferences.PROP_THEME:
                    applyTheme((String) evt.getNewValue());
                    break;
            }
        }
    };

    public SerialTerminal(Display display, Preferences preferences) {
        this.display = display;
        this.preferences = preferences;

        serialBaudRate = preferences.getTerminalBaudRate();
        localEcho = preferences.getTerminalLocalEcho();

        cursorState = CURSOR_DISPLAY | CURSOR_ON | CURSOR_FLASH | CURSOR_ULINE;

        setTerminalType(preferences.getTerminalType());
    }

    public void open() {
        create();

        shell.pack();
        shell.open();

        shell.addShellListener(new ShellAdapter() {

            @Override
            public void shellActivated(ShellEvent e) {
                setFocus();
            }
        });

        display.timerExec(FRAME_TIMER, screenUpdateRunnable);
    }

    void create() {
        shell = new Shell(display);
        shell.setText(WINDOW_TITLE);
        shell.setData(this);

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (preferences.getTerminalFont() != null) {
            fontData = StringConverter.asFontData(preferences.getTerminalFont());
        }
        font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);

        GC gc = new GC(shell);
        try {
            gc.setFont(font);
            Point pt = gc.textExtent("M");
            characterWidth = pt.x;
            characterHeight = pt.y;
        } finally {
            gc.dispose();
        }

        FillLayout layout = new FillLayout();
        layout.marginWidth = layout.marginHeight = 0;
        shell.setLayout(layout);

        createContents(shell);
        applyTheme(preferences.getTheme());

        Rectangle rect = preferences.getTerminalWindow();
        if (rect != null) {
            shell.setLocation(rect.x, rect.y);
        }

        preferences.addPropertyChangeListener(preferencesChangeListener);

        shell.addListener(SWT.Traverse, new Listener() {

            @Override
            public void handleEvent(Event e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE) {
                    e.doit = false;
                }
            }
        });

        shell.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                preferences.removePropertyChangeListener(preferencesChangeListener);
                try {
                    if (comPort.isOpened()) {
                        comPort.closePort();
                    }
                } catch (Exception ex) {

                }
                font.dispose();
            }
        });
    }

    public void dispose() {
        shell.dispose();
    }

    protected void createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setBackgroundMode(SWT.INHERIT_DEFAULT);

        createLineInputGroup(container);

        canvas = new Canvas(container, SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Rectangle rect = preferences.getTerminalWindow();
        if (rect != null) {
            gridData.widthHint = rect.width * characterWidth;
            gridData.heightHint = rect.height * characterHeight;
        }
        else {
            gridData.widthHint = 100 * characterWidth;
            gridData.heightHint = 30 * characterHeight;
        }
        canvas.setLayoutData(gridData);

        createBottomControls(container);

        foreground = colors[7];
        background = colors[0];
        canvas.setBackground(background);

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                Rectangle rect = shell.getBounds();
                rect.width = screenWidth;
                rect.height = screenHeight;
                preferences.setTerminalWindow(rect);
            }

        });

        canvas.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                Point size = canvas.getSize();
                int width = (size.x - canvas.getVerticalBar().getSize().x) / characterWidth;
                int height = size.y / characterHeight;

                Cell[][] newScreen = new Cell[BACKBUFFER_LINES][width];

                int y = 0;
                while (y < newScreen.length) {
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

                screenWidth = width;
                screenHeight = height;
                screen = newScreen;

                topRow = screen.length - screenHeight;
                if (cy == 0) {
                    cy = topRow;
                }
                canvas.getVerticalBar().setValues(topRow, 0, screen.length, screenHeight, 1, screenHeight);
            }
        });

        canvas.getVerticalBar().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                cursorState &= ~CURSOR_DISPLAY;
                frameCounter = 0;
                topRow = ((ScrollBar) e.widget).getSelection();
                redraw();
            }

        });

        canvas.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.INSERT && (e.stateMask & SWT.MOD2) != 0) {
                    pasteFromClipboard();
                    return;
                }
                if (e.character != 0) {
                    try {
                        if (comPort != null && comPort.isOpened()) {
                            comPort.writeByte((byte) e.character);
                            if (localEcho) {
                                write(e.character);
                            }
                        }
                        else {
                            write(e.character);
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

                e.gc.setFont(font);

                for (int y = y1, cy = y * characterHeight; y >= y0; y--, cy -= characterHeight) {
                    for (int x = x1, cx = x * characterWidth; x >= x0; x--, cx -= characterWidth) {
                        Cell cell = screen[topRow + y][x];
                        e.gc.setForeground(cell.foreground);
                        e.gc.setBackground(cell.background);
                        e.gc.drawString(String.valueOf(cell.character), cx, cy, false);
                    }
                }

                if (!lineInputGroup.getVisible()) {
                    if ((cursorState & (CURSOR_DISPLAY | CURSOR_ON)) == (CURSOR_DISPLAY | CURSOR_ON)) {
                        int h = characterHeight;
                        if ((cursorState & CURSOR_ULINE) != 0) {
                            h = characterHeight / 4;
                        }
                        int y = ((cy - topRow) * characterHeight) + (characterHeight - h);
                        int x = cx * characterWidth;
                        e.gc.setBackground(e.gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
                        e.gc.fillRectangle(x, y, characterWidth, h);
                    }
                }

                if (selectionRectangle != null && selectionRectangle.width != 0 && selectionRectangle.height != 0) {
                    int x = selectionRectangle.x * characterWidth;
                    int y = (selectionRectangle.y - topRow) * characterHeight;
                    int width = selectionRectangle.width * characterWidth - 1;
                    int height = selectionRectangle.height * characterHeight - 1;
                    e.gc.setAlpha(128);
                    e.gc.setBackground(e.gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
                    e.gc.fillRectangle(x, y, width, height);
                    e.gc.setAlpha(255);
                    e.gc.setForeground(e.gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
                    e.gc.drawRectangle(x, y, width, height);
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (selectionRectangle != null && selectionRectangle.width != 0 && selectionRectangle.height != 0) {
                    StringBuilder text = new StringBuilder();
                    for (int y = selectionRectangle.y; y < selectionRectangle.y + selectionRectangle.height; y++) {
                        StringBuilder line = new StringBuilder();
                        for (int x = selectionRectangle.x; x < selectionRectangle.x + selectionRectangle.width; x++) {
                            line.append(screen[y][x].character);
                        }
                        if (text.length() != 0) {
                            text.append(System.lineSeparator());
                        }
                        text.append(line.toString().replaceFirst("\\s++$", ""));
                    }
                    if (text.length() == 0) {
                        text.append(" ");
                    }

                    ImageData imageData = new ImageData(selectionRectangle.width * characterWidth, selectionRectangle.height * characterHeight, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));
                    Image image = new Image(display, imageData);
                    GC gc = new GC(image);
                    try {
                        int y0 = selectionRectangle.y;
                        int y1 = selectionRectangle.y + selectionRectangle.height - 1;
                        int x0 = selectionRectangle.x;
                        int x1 = selectionRectangle.x + selectionRectangle.width - 1;

                        gc.setFont(font);
                        for (int y = y1, cy = (y - y0) * characterHeight; y >= y0; y--, cy -= characterHeight) {
                            for (int x = x1, cx = (x - x0) * characterWidth; x >= x0; x--, cx -= characterWidth) {
                                Cell cell = screen[y][x];
                                gc.setForeground(cell.foreground);
                                gc.setBackground(cell.background);
                                gc.drawString(String.valueOf(cell.character), cx, cy, false);
                            }
                        }
                    } finally {
                        gc.dispose();
                    }

                    Clipboard clipboard = new Clipboard(display);
                    try {
                        clipboard.setContents(new Object[] {
                            text.toString(),
                            image.getImageData(),
                            image.getImageData()
                        }, new Transfer[] {
                            TextTransfer.getInstance(),
                            PngImageTransfer.getInstance(),
                            ImageTransfer.getInstance()
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    clipboard.dispose();

                    image.dispose();
                }
                selectionRectangle = null;
                canvas.redraw();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                int cx = Math.min(e.x / characterWidth, screenWidth - 1);
                int cy = Math.min(topRow + (e.y / characterHeight), screen.length - 1);
                selectionRectangle = new Rectangle(cx, cy, 0, 0);
                canvas.redraw();
            }

        });

        canvas.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (selectionRectangle != null) {
                    int cx = Math.min(e.x / characterWidth, screenWidth - 1);
                    if (cx < selectionRectangle.x) {
                        return;
                    }
                    int cy = Math.min(topRow + (e.y / characterHeight), screen.length - 1);
                    if (cy < selectionRectangle.y) {
                        return;
                    }
                    selectionRectangle.width = cx - selectionRectangle.x + 1;
                    selectionRectangle.height = cy - selectionRectangle.y + 1;
                    canvas.redraw();
                }
            }
        });
    }

    void createLineInputGroup(Composite parent) {
        lineInputGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginTop = layout.marginHeight;
        layout.marginHeight = 0;
        lineInputGroup.setLayout(layout);
        lineInputGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        lineInput = new Combo(lineInputGroup, SWT.BORDER | SWT.DROP_DOWN);
        lineInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        String[] history = preferences.getTerminalHistory();
        if (history != null) {
            lineInput.setItems(history);
            historyIndex = lineInput.getItemCount();
        }

        lineInput.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int l = lineInput.getText().length();
                lineInput.setSelection(new Point(l, l));
                historyIndex = lineInput.getSelectionIndex();
            }
        });
        lineInput.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                switch (e.keyCode) {
                    case SWT.ARROW_UP:
                        if (historyIndex > 0) {
                            historyIndex--;
                            lineInput.select(historyIndex);
                            int l = lineInput.getText().length();
                            lineInput.setSelection(new Point(l, l));
                        }
                        e.doit = false;
                        break;
                    case SWT.ARROW_DOWN:
                        if (historyIndex < lineInput.getItemCount()) {
                            historyIndex++;
                        }
                        if (historyIndex < lineInput.getItemCount()) {
                            lineInput.select(historyIndex);
                            int l = lineInput.getText().length();
                            lineInput.setSelection(new Point(l, l));
                        }
                        else {
                            lineInput.setText("");
                        }
                        e.doit = false;
                        break;
                }
            }
        });
        lineInput.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.keyCode) {
                    case SWT.ARROW_UP:
                    case SWT.ARROW_DOWN:
                        e.doit = false;
                        break;
                    case SWT.CR:
                        try {
                            String text = lineInput.getText();
                            if (comPort != null && comPort.isOpened()) {
                                comPort.writeBytes(text.getBytes());
                                comPort.writeInt(0x0D);
                            }
                            if (!text.isEmpty()) {
                                int index = lineInput.indexOf(text);
                                if (index != -1) {
                                    lineInput.remove(index);
                                }
                                lineInput.add(text);
                                while (lineInput.getItemCount() > 10) {
                                    lineInput.remove(0);
                                }
                                preferences.setTerminalHistory(lineInput.getItems());
                            }
                            historyIndex = lineInput.getItemCount();
                            lineInput.setText("");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        setFocus();
                        e.doit = false;
                        break;
                }
            }
        });

        lineInputGroup.setVisible(preferences.getTerminalLineInput());
        ((GridData) lineInputGroup.getLayoutData()).exclude = !preferences.getTerminalLineInput();
    }

    void createBottomControls(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        terminalType = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        terminalType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        terminalType.setItems(new String[] {
            "TTY",
            "ANSI / VT100",
            "Parallax Serial Terminal"
        });

        baudRate = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        baudRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        String[] items = new String[baudRates.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(baudRates.get(i));
        }
        baudRate.setItems(items);

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        GC gc = new GC(container);
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        clear = new Button(container, SWT.PUSH);
        clear.setText("Clear");
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = Math.max(Dialog.convertHorizontalDLUsToPixels(fontMetrics, 50), clear.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        clear.setLayoutData(data);
        clear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    clear();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                setFocus();
            }
        });

        reset = new Button(container, SWT.PUSH);
        reset.setText("Reset");
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = Math.max(Dialog.convertHorizontalDLUsToPixels(fontMetrics, 50), reset.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        reset.setLayoutData(data);
        reset.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (comPort != null) {
                        comPort.hwreset();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                setFocus();
            }
        });

        monitor = new Button(container, SWT.PUSH);
        monitor.setText("Monitor");
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = Math.max(Dialog.convertHorizontalDLUsToPixels(fontMetrics, 50), monitor.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        monitor.setLayoutData(data);
        monitor.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    clear();
                    startMonitor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                setFocus();
            }
        });

        taqoz = new Button(container, SWT.PUSH);
        taqoz.setText("TAQOZ");
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = Math.max(Dialog.convertHorizontalDLUsToPixels(fontMetrics, 50), taqoz.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        taqoz.setLayoutData(data);
        taqoz.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    clear();
                    startTAQOZ();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                setFocus();
            }
        });

        GridLayout layout = new GridLayout(container.getChildren().length, false);
        layout.marginBottom = layout.marginHeight;
        layout.marginLeft = layout.marginRight = layout.marginWidth;
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        baudRate.select(baudRates.indexOf(serialBaudRate));
        baudRate.addSelectionListener(baudRateSelectionListener);

        if (emulation instanceof ParallaxSerialTerminal) {
            terminalType.select(2);
        }
        else if (emulation instanceof ANSI) {
            terminalType.select(1);
        }
        else {
            terminalType.select(0);
        }
        terminalType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setTerminalType(terminalType.getSelectionIndex());
                preferences.setTerminalType(terminalType.getSelectionIndex());
                setFocus();
            }
        });
    }

    void setTerminalType(int type) {
        switch (type) {
            case 1:
                if (!(emulation instanceof ANSI)) {
                    emulation = new ANSI();
                }
                break;
            case 2:
                if (!(emulation instanceof ParallaxSerialTerminal)) {
                    emulation = new ParallaxSerialTerminal();
                }
                break;
            default:
                if (!(emulation instanceof TTY)) {
                    emulation = new TTY();
                }
                break;
        }
    }

    public void setFocus() {
        if (lineInputGroup.getVisible()) {
            lineInput.setFocus();
        }
        else {
            canvas.setFocus();
        }
    }

    public void close() {
        shell.dispose();
    }

    public void write(char c) {
        if (!canvas.isDisposed()) {
            emulation.write(c);
        }
    }

    public void write(String s) {
        if (!canvas.isDisposed()) {
            for (int i = 0; i < s.length(); i++) {
                emulation.write(s.charAt(i));
            }
        }
    }

    void scrollUp(int lines) {
        int srcY = lines;
        int dstY = 0;

        while (srcY < screen.length) {
            for (int x = 0; x < screenWidth; x++) {
                screen[dstY][x].set(screen[srcY][x]);
            }
            srcY++;
            dstY++;
        }

        while (dstY < screen.length) {
            for (int x = 0; x < screenWidth; x++) {
                screen[dstY][x].set(' ', foreground, background);
            }
            dstY++;
        }
    }

    public ComPort getSerialPort() {
        return comPort;
    }

    public void setSerialPort(ComPort serialPort) {
        try {
            if (this.comPort != null && this.comPort != serialPort) {
                if (this.comPort.isOpened()) {
                    this.comPort.removeEventListener();
                }
            }

            if (serialPort != null) {
                shell.setText(WINDOW_TITLE + " on " + serialPort.getDescription());
                if (!serialPort.isOpened()) {
                    if (serialPort instanceof NetworkComPort) {
                        NetworkComPort netPort = (NetworkComPort) serialPort;
                        if (netPort.getInetAddr() == null) {
                            BusyIndicator.showWhile(display, new Runnable() {

                                @Override
                                public void run() {
                                    Collection<DeviceDescriptor> list = NetworkUtils.getAvailableDevices();
                                    for (DeviceDescriptor descr : list) {
                                        if (descr.mac_address.equals(netPort.getMacAddress())) {
                                            netPort.setInetAddr(descr.inetAddr);
                                            shell.setText(WINDOW_TITLE + " on " + serialPort.getDescription());
                                        }
                                    }
                                }
                            });
                        }
                    }
                    serialPort.openPort();
                }
                serialPort.setParams(
                    serialBaudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
                if (this.comPort != serialPort) {
                    serialPort.setEventListener(serialEventListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.comPort = serialPort;
        updateButtonsEnablement();
    }

    void updateButtonsEnablement() {
        lineInputGroup.setEnabled(comPort != null && comPort.isOpened());

        terminalType.setEnabled(comPort != null && comPort.isOpened());
        baudRate.setEnabled(comPort != null && comPort.isOpened());

        clear.setEnabled(comPort != null && comPort.isOpened());
        reset.setEnabled(comPort != null && comPort.isOpened());
        monitor.setEnabled(comPort != null && comPort.isOpened());
        taqoz.setEnabled(comPort != null && comPort.isOpened());
    }

    public int getBaudRate() {
        return serialBaudRate;
    }

    void redraw() {
        Rectangle rect = redrawRectangle.getAndUpdate(new UnaryOperator<Rectangle>() {

            @Override
            public Rectangle apply(Rectangle t) {
                if (t == null) {
                    return new Rectangle(0, topRow, screenWidth, screenHeight);
                }
                return t.union(new Rectangle(0, topRow, screenWidth, screenHeight));
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

    public void clear() {
        scrollUp(screenHeight);
        redraw();
        cx = 0;
        cy = screen.length - screenHeight;
    }

    public void startMonitor() {
        setTerminalType(1);
        terminalType.select(1);

        try {
            comPort.hwreset();
            comPort.writeBytes(new byte[] {
                '>', ' ', 0x04
            });
        } catch (Exception e) {

        }
    }

    public void startTAQOZ() {
        setTerminalType(1);
        terminalType.select(1);

        try {
            comPort.hwreset();
            comPort.writeBytes(new byte[] {
                '>', ' ', 0x1B
            });
        } catch (Exception e) {

        }
    }

    public void pasteFromClipboard() {
        Clipboard clipboard = new Clipboard(display);
        try {
            String s = (String) clipboard.getContents(TextTransfer.getInstance());
            if (s != null) {
                final byte[] b = s.replaceAll("(\r\n|\n|\r)", "\r").getBytes();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < b.length; i++) {
                                comPort.writeByte(b[i]);
                                if (localEcho) {
                                    write((char) b[i]);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        } finally {
            clipboard.dispose();
        }
    }

    public void applyTheme(String id) {
        Color widgetForeground = null;
        Color widgetBackground = null;
        Color listForeground = null;
        Color listBackground = null;

        if ("win32".equals(Platform.PLATFORM) && id == null) {
            if (Display.isSystemDarkTheme()) {
                id = "dark";
            }
        }

        if (id == null) {
            widgetBackground = ColorRegistry.getColor(ColorRegistry.WIDGET_BACKGROUND);
            widgetForeground = ColorRegistry.getColor(ColorRegistry.WIDGET_FOREGROUND);
            listBackground = ColorRegistry.getColor(ColorRegistry.LIST_BACKGROUND);
            listForeground = ColorRegistry.getColor(ColorRegistry.LIST_FOREGROUND);
        }
        else if ("dark".equals(id)) {
            widgetForeground = new Color(0xF0, 0xF0, 0xF0);
            widgetBackground = new Color(0x50, 0x55, 0x57);
            listForeground = new Color(0xA7, 0xA7, 0xA7);
            listBackground = new Color(0x2B, 0x2B, 0x2B);
        }
        else if ("light".equals(id)) {
            widgetForeground = new Color(0x00, 0x00, 0x00);
            if ("win32".equals(Platform.PLATFORM)) {
                widgetBackground = new Color(0xF0, 0xF0, 0xF0);
            }
            else {
                widgetBackground = new Color(0xFA, 0xFA, 0xFA);
            }
            listForeground = new Color(0x00, 0x00, 0x00);
            listBackground = new Color(0xFE, 0xFE, 0xFE);
        }

        canvas.getParent().setBackground(widgetBackground);

        lineInput.setBackground(listBackground);
        lineInput.setForeground(listForeground);

        terminalType.setForeground(listForeground);
        terminalType.setBackground(listBackground);
        baudRate.setBackground(listBackground);
        baudRate.setForeground(listForeground);

        clear.setForeground(widgetForeground);
        reset.setForeground(widgetForeground);
        monitor.setForeground(widgetForeground);
        taqoz.setForeground(widgetForeground);
    }

}
