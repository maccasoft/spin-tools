/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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

    public static enum Pack {
        LONGS_1BIT(0b1, 32, 1),
        LONGS_2BIT(0b11, 16, 2),
        LONGS_4BIT(0b1111, 8, 4),
        LONGS_8BIT(0b11111111, 4, 8),
        LONGS_16BIT(0b1111111111111111, 2, 16),

        WORDS_1BIT(0b1, 16, 1),
        WORDS_2BIT(0b11, 8, 2),
        WORDS_4BIT(0b1111, 4, 4),
        WORDS_8BIT(0b11111111, 2, 8),

        BYTES_1BIT(0b1, 8, 1),
        BYTES_2BIT(0b11, 4, 2),
        BYTES_4BIT(0b1111, 2, 4);

        public final int mask;
        public final int size;
        public final int shift;

        Pack(int mask, int size, int shift) {
            this.mask = mask;
            this.size = size;
            this.shift = shift;
        }
    }

    public static enum RGBColor {
        ORANGE,
        BLUE,
        GREEN,
        CYAN,
        RED,
        MAGENTA,
        YELLOW,
        GREY
    }

    protected Display display;
    protected Shell shell;

    protected Canvas canvas;

    protected Point imageSize;
    protected Point dotSize;

    public static DebugWindow createType(String key) {
        switch (key.toUpperCase()) {
            case "LOGIC":
                return new DebugLogicWindow();
            case "SCOPE":
                return new DebugScopeWindow();
            case "SCOPE_XY":
                break;
            case "FFT":
                break;
            case "SPECTRO":
                break;
            case "PLOT":
                return new DebugPlotWindow();
            case "TERM":
                break;
            case "BITMAP":
                return new DebugBitmapWindow();
            case "MIDI":
                break;
        }
        return null;
    }

    public DebugWindow() {
        imageSize = new Point(256, 256);
        dotSize = new Point(1, 1);
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

        shell = new Shell(display);
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
        gridData.widthHint = imageSize.x * dotSize.x;
        gridData.heightHint = imageSize.y * dotSize.y;
        canvas.setLayoutData(gridData);
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

                GridData gridData = (GridData) canvas.getLayoutData();
                gridData.widthHint = imageSize.x * dotSize.x;
                gridData.heightHint = imageSize.y * dotSize.y;

                shell.pack();
                shell.redraw();
            }
        }
    }

    protected void dotsize(KeywordIterator iter) {
        if (iter.hasNextNumber()) {
            dotSize.x = iter.nextNumber();
            dotSize.y = dotSize.x;
            if (iter.hasNextNumber()) {
                dotSize.y = iter.nextNumber();
            }

            GridData gridData = (GridData) canvas.getLayoutData();
            gridData.widthHint = imageSize.x * dotSize.x;
            gridData.heightHint = imageSize.y * dotSize.y;

            shell.pack();
            shell.redraw();
        }
    }

    protected void doSaveBitmap(Image image, String name, boolean window) {
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
            loader.data = new ImageData[] {
                image.getImageData()
            };
            loader.save(fileName, format);
        }
    }

}
