/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.io.File;

import org.apache.commons.lang3.BitField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.internal.CircularBuffer;

public abstract class DebugWindow {

    public static final Color RED = new Color(0xFF, 0x00, 0x00);
    public static final Color LIME = new Color(0x00, 0xFF, 0x00);
    public static final Color BLUE = new Color(0x3F, 0x3F, 0xFF);
    public static final Color YELLOW = new Color(0xFF, 0xFF, 0x00);
    public static final Color MAGENTA = new Color(0xFF, 0x00, 0xFF);
    public static final Color CYAN = new Color(0x00, 0xFF, 0xFF);
    public static final Color ORANGE = new Color(0xFF, 0x7F, 0x00);
    public static final Color OLIVE = new Color(0x7F, 0x7F, 0x00);
    public static final Color WHITE = new Color(0xFF, 0xFF, 0xFF);
    public static final Color BLACK = new Color(0x00, 0x00, 0x00);
    public static final Color GRAY = new Color(0x40, 0x40, 0x40);
    public static final Color GRAY2 = new Color(0x80, 0x80, 0x80);
    public static final Color GRAY3 = new Color(0xD0, 0xD0, 0xD0);

    public static final Color[] defaultColors = {
        LIME, RED, CYAN, YELLOW, MAGENTA, BLUE, ORANGE, OLIVE
    };

    public static enum RGBColor {
        ORANGE,
        BLUE,
        GREEN,
        CYAN,
        RED,
        MAGENTA,
        YELLOW,
        GRAY,
    }

    public static final int MARGIN_WIDTH = 6;
    public static final int MARGIN_HEIGHT = 6;

    protected Display display;
    protected Shell shell;

    protected Canvas canvas;

    protected Point imageSize;

    protected final CircularBuffer transmitBuffer;

    Point origin;
    int xDirection;
    int yDirection;

    File currentDirectory;

    int keyPress;
    int mousePack;
    int mousePixel;

    public static final BitField mouseX = new BitField(0b0000000000000000001111111111111);
    public static final BitField mouseY = new BitField(0b0000011111111111110000000000000);
    public static final BitField mouseWheel = new BitField(0b0001100000000000000000000000000);
    public static final BitField mouseLeft = new BitField(0b0010000000000000000000000000000);
    public static final BitField mouseMiddle = new BitField(0b0100000000000000000000000000000);
    public static final BitField mouseRight = new BitField(0b1000000000000000000000000000000);

    public static DebugWindow createType(String key, CircularBuffer transmitBuffer) {
        switch (key.toUpperCase()) {
            case "LOGIC":
                return new DebugLogicWindow(transmitBuffer);
            case "SCOPE":
                return new DebugScopeWindow(transmitBuffer);
            case "SCOPE_XY":
                return new DebugScopeXYWindow(transmitBuffer);
            case "FFT":
                return new DebugFFTWindow(transmitBuffer);
            case "SPECTRO":
                return new DebugSpectroWindow(transmitBuffer);
            case "PLOT":
                return new DebugPlotWindow(transmitBuffer);
            case "TERM":
                return new DebugTermWindow(transmitBuffer);
            case "BITMAP":
                return new DebugBitmapWindow(transmitBuffer);
            case "MIDI":
                return new DebugMIDIWindow(transmitBuffer);
        }
        return null;
    }

    public DebugWindow(CircularBuffer transmitBuffer) {
        this.transmitBuffer = transmitBuffer;

        imageSize = new Point(256, 256);
        origin = new Point(0, 0);

        xDirection = 0;
        yDirection = 0;
    }

    public File getCurrentDirectory() {
        return currentDirectory != null ? currentDirectory : new File("");
    }

    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public void open() {
        if (shell == null) {
            create();
        }

        shell.pack();
        shell.open();
    }

    public void create() {
        display = Display.getDefault();

        shell = new Shell(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);
        shell.setData(this);

        FillLayout layout = new FillLayout();
        layout.marginWidth = layout.marginHeight = 0;
        shell.setLayout(layout);

        createContents(shell);
    }

    protected void createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setBackgroundMode(SWT.INHERIT_DEFAULT);

        //createLineInputGroup(container);

        canvas = new Canvas(container, SWT.DOUBLE_BUFFERED);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = imageSize.x;
        gridData.heightHint = imageSize.y;
        canvas.setLayoutData(gridData);

        canvas.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.keyCode) {
                    case SWT.ARROW_LEFT:
                        keyPress = 1;
                        break;
                    case SWT.ARROW_RIGHT:
                        keyPress = 2;
                        break;
                    case SWT.ARROW_UP:
                        keyPress = 3;
                        break;
                    case SWT.ARROW_DOWN:
                        keyPress = 4;
                        break;
                    case SWT.HOME:
                        keyPress = 5;
                        break;
                    case SWT.END:
                        keyPress = 6;
                        break;
                    case SWT.INSERT:
                        keyPress = 10;
                        break;
                    case SWT.PAGE_UP:
                        keyPress = 11;
                        break;
                    case SWT.PAGE_DOWN:
                        keyPress = 12;
                        break;
                    default:
                        if (e.character == SWT.DEL) {
                            keyPress = 7;
                        }
                        else if (e.character < 127) {
                            keyPress = e.character & 0xFF;
                        }
                        break;
                }
            }

        });

        canvas.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                mousePack = mouseX.setValue(mousePack, xDirection == 0 ? origin.x + e.x : imageSize.x - (origin.x + e.x));
                mousePack = mouseY.setValue(mousePack, yDirection == 0 ? imageSize.y - (origin.y + e.y) : origin.y + e.y);
            }

        });
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    mousePack = mouseLeft.setValue(mousePack, 1);
                }
                if (e.button == 2) {
                    mousePack = mouseMiddle.setValue(mousePack, 1);
                }
                if (e.button == 3) {
                    mousePack = mouseRight.setValue(mousePack, 1);
                }
            }

            @Override
            public void mouseUp(MouseEvent e) {
                if (e.button == 1) {
                    mousePack = mouseLeft.setValue(mousePack, 0);
                }
                if (e.button == 2) {
                    mousePack = mouseMiddle.setValue(mousePack, 0);
                }
                if (e.button == 3) {
                    mousePack = mouseRight.setValue(mousePack, 0);
                }
            }
        });

        canvas.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseScrolled(MouseEvent e) {
                mousePack = mouseWheel.setValue(mousePack, e.count);
            }
        });
    }

    protected void paint(GC gc) {

    }

    protected void sendKeyPress() {
        transmitBuffer.writeLong(keyPress);
        synchronized (transmitBuffer) {
            transmitBuffer.notify();
        }
        keyPress = 0;
    }

    protected void sendMouse() {
        transmitBuffer.writeLong(mousePack);
        transmitBuffer.writeLong(mousePixel);
        synchronized (transmitBuffer) {
            transmitBuffer.notify();
        }
        mousePack = mouseWheel.setValue(mousePack, 0);
    }

    public void setText(String text) {
        shell.setText(text);
    }

    public void setup(KeywordIterator iter) {

    }

    public void update(KeywordIterator iter) {

    }

    protected boolean isString(String s) {
        return s.startsWith("'") && s.endsWith("'");
    }

    protected String stringStrip(String s) {
        return s.substring(1, s.length() - 1);
    }

    protected boolean isNumber(String s) {
        char ch = s.charAt(0);
        return (ch == '$' || ch == '%' || ch == '-' || ch == '+' || Character.isDigit(ch));
    }

    protected int stringToNumber(String s) {
        if (s.startsWith("$")) {
            return (int) Long.parseLong(s.substring(1).replace("_", ""), 16);
        }
        if (s.startsWith("%%")) {
            return (int) Long.parseLong(s.substring(2).replace("_", ""), 4);
        }
        if (s.startsWith("%")) {
            return (int) Long.parseLong(s.substring(1).replace("_", ""), 2);
        }
        return (int) Long.parseLong(s.replace("_", ""), 10);
    }

    public void addDisposeListener(DisposeListener l) {
        shell.addDisposeListener(l);
    }

    public void removeDisposeListener(DisposeListener l) {
        shell.removeDisposeListener(l);
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    public void dispose() {
        shell.dispose();
    }

    protected void title(KeywordIterator iter) {
        if (iter.hasNext()) {
            String value = iter.next();
            if (value.startsWith("'")) {
                value = value.substring(1);
            }
            if (value.endsWith("'")) {
                value = value.substring(0, value.length() - 1);
            }
            shell.setText(value);
        }
    }

    protected void pos(KeywordIterator iter) {
        if (iter.hasNextNumber()) {
            int px = iter.nextNumber();
            if (iter.hasNextNumber()) {
                int py = iter.nextNumber();
                shell.setLocation(px, py);
            }
        }
    }

    protected void size(KeywordIterator iter) {
        if (iter.hasNextNumber()) {
            int width = iter.nextNumber();
            if (iter.hasNextNumber()) {
                imageSize.x = width;
                imageSize.y = iter.nextNumber();
            }
        }
    }

    protected PackMode packedMode(String cmd, KeywordIterator iter) {
        boolean alt = false;
        boolean signx = false;

        if (iter.hasNext() && "ALT".equalsIgnoreCase(iter.peekNext())) {
            alt = true;
            iter.next();
        }

        if (iter.hasNext() && "SIGNX".equalsIgnoreCase(iter.peekNext())) {
            signx = true;
            iter.next();
        }

        switch (cmd.toUpperCase()) {
            case "LONGS_1BIT":
                return PackMode.LONGS_1BIT(alt, signx);
            case "LONGS_2BIT":
                return PackMode.LONGS_2BIT(alt, signx);
            case "LONGS_4BIT":
                return PackMode.LONGS_4BIT(alt, signx);
            case "LONGS_8BIT":
                return PackMode.LONGS_8BIT(alt, signx);
            case "LONGS_16BIT":
                return PackMode.LONGS_16BIT(alt, signx);
            case "WORDS_1BIT":
                return PackMode.WORDS_1BIT(alt, signx);
            case "WORDS_2BIT":
                return PackMode.WORDS_2BIT(alt, signx);
            case "WORDS_4BIT":
                return PackMode.WORDS_4BIT(alt, signx);
            case "WORDS_8BIT":
                return PackMode.WORDS_8BIT(alt, signx);
            case "BYTES_1BIT":
                return PackMode.BYTES_1BIT(alt, signx);
            case "BYTES_2BIT":
                return PackMode.BYTES_2BIT(alt, signx);
            case "BYTES_4BIT":
                return PackMode.BYTES_4BIT(alt, signx);
        }

        return PackMode.NONE();
    }

    protected void save(KeywordIterator iter) {
        boolean window = false;

        if (iter.hasNext()) {
            String key = iter.next();
            if (key.equalsIgnoreCase("WINDOW")) {
                window = true;
                if (!iter.hasNext()) {
                    return;
                }
                key = iter.next();
            }
            if (isString(key)) {
                doSaveBitmap(stringStrip(key), window);
            }
        }
    }

    void doSaveBitmap(String name, boolean window) {
        String fileName = new File(name).getAbsoluteFile().getName();

        int format = SWT.IMAGE_BMP;
        if (fileName.toLowerCase().endsWith(".png")) {
            format = SWT.IMAGE_PNG;
        }
        else if (fileName.toLowerCase().endsWith(".jpg")) {
            format = SWT.IMAGE_JPEG;
        }
        else if (!fileName.toLowerCase().endsWith(".bmp")) {
            fileName = fileName + ".bmp";
        }

        ImageLoader loader = new ImageLoader();
        if (window) {
            GC gc = new GC(shell);
            try {
                Point size = shell.getSize();
                Image windowImage = new Image(display, size.x, size.y);
                try {
                    gc.copyArea(windowImage, 0, 0);
                    loader.data = new ImageData[] {
                        windowImage.getImageData()
                    };
                    loader.save(fileName, format);
                } finally {
                    windowImage.dispose();
                }
            } finally {
                gc.dispose();
            }
        }
        else {
            Point size = canvas.getSize();
            Image image = new Image(display, new ImageData(size.x, size.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
            try {
                GC gc = new GC(image);
                try {
                    paint(gc);
                } finally {
                    gc.dispose();
                }
                loader.data = new ImageData[] {
                    image.getImageData()
                };
                loader.save(fileName, format);
            } finally {
                image.dispose();
            }
        }
    }

}
