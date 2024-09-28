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

import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class DebugBitmapWindow extends DebugWindow {

    enum Pack {
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

    enum RGBColor {
        ORANGE,
        BLUE,
        GREEN,
        CYAN,
        RED,
        MAGENTA,
        YELLOW,
        GREY
    }

    ImageData imageData;
    Image image;

    int x;
    int y;
    int traceMode;

    ColorMode colorMode;
    int colorTune;
    int[] lutColors;

    Pack packMode;
    boolean altPack;

    int rate;
    int rateCount;
    boolean autoUpdate;

    public DebugBitmapWindow() {
        x = y = 0;
        traceMode = 0;

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new int[256];

        packMode = null;
        altPack = false;

        rate = -1;
        rateCount = 0;
        autoUpdate = true;
    }

    @Override
    protected void createContents(Composite parent) {
        super.createContents(parent);

        imageData = new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
        image = new Image(display, imageData);

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                e.gc.setAdvanced(true);
                e.gc.setAntialias(SWT.ON);
                e.gc.setInterpolation(SWT.OFF);
                if (image.isDisposed()) {
                    image = new Image(e.display, imageData);
                }
                Point canvasSize = canvas.getSize();
                e.gc.drawImage(image, 0, 0, imageSize.x, imageSize.y, 0, 0, canvasSize.x, canvasSize.y);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                image.dispose();
            }

        });
    }

    @Override
    public void setup(KeywordIterator iter) {
        String cmd;

        while (iter.hasNext()) {
            cmd = iter.next().toUpperCase();
            switch (cmd) {
                case "TITLE":
                    title(iter);
                    break;

                case "POS":
                    pos(iter);
                    break;

                case "SIZE":
                    size(iter);
                    break;

                case "DOTSIZE":
                    dotsize(iter);
                    break;

                case "SPARSE":
                    if (iter.hasNext()) {
                        iter.next();
                    }
                    break;

                case "LUT1":
                case "LUT2":
                case "LUT4":
                case "LUT8":
                case "LUMA8":
                case "LUMA8W":
                case "LUMA8X":
                case "HSV8":
                case "HSV8W":
                case "HSV8X":
                case "RGBI8":
                case "RGBI8W":
                case "RGBI8X":
                case "HSV16":
                case "HSV16W":
                case "HSV16X":
                case "RGB8":
                case "RGB16":
                case "RGB24":
                    colorMode(cmd, iter);
                    break;

                case "LUTCOLORS":
                    lutColors(iter);
                    break;

                case "TRACE":
                    trace(iter);
                    break;

                case "RATE":
                    rate(iter);
                    break;

                case "LONGS_1BIT":
                case "LONGS_2BIT":
                case "LONGS_4BIT":
                case "LONGS_8BIT":
                case "LONGS_16BIT":
                case "WORDS_1BIT":
                case "WORDS_2BIT":
                case "WORDS_4BIT":
                case "WORDS_8BIT":
                case "BYTES_1BIT":
                case "BYTES_2BIT":
                case "BYTES_4BIT":
                    packedMode(cmd, iter);
                    break;

                case "UPDATE":
                    autoUpdate = false;
                    break;

                case "HIDEXY":
                    break;
            }
        }
    }

    @Override
    protected void size(KeywordIterator iter) {
        super.size(iter);

        image.dispose();
        imageData = new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
    }

    void trace(KeywordIterator iter) {
        if (iter.hasNextNumber()) {
            traceMode = iter.nextNumber() & 15;
            switch (traceMode & 7) {
                case 0:
                case 2:
                case 4:
                case 5:
                    x = 0;
                    break;
                default:
                    x = imageSize.x - 1;
                    break;
            }
            switch (traceMode & 7) {
                case 0:
                case 1:
                case 4:
                case 6:
                    y = 0;
                    break;
                default:
                    y = imageSize.y - 1;
                    break;
            }
        }
    }

    void rate(KeywordIterator iter) {
        rate = -1;
        rateCount = 0;
        autoUpdate = true;
        if (iter.hasNextNumber()) {
            rate = iter.nextNumber();
        }
    }

    void packedMode(String cmd, KeywordIterator iter) {
        packMode = Pack.valueOf(cmd.toUpperCase());
        altPack = false;
        if (iter.hasNext() && "ALT".equalsIgnoreCase(iter.peekNext())) {
            altPack = true;
            iter.next();
        }
    }

    @Override
    public void update(KeywordIterator iter) {
        int pixel, color;
        String cmd;

        while (iter.hasNext()) {
            cmd = iter.next();

            if (isNumber(cmd)) {
                try {
                    pixel = stringToNumber(cmd);
                    if (packMode != null) {
                        if (altPack) {
                            if (packMode.shift <= 1) {
                                pixel = ((pixel >> 1) & 0x55555555) | ((pixel << 1) & 0xAAAAAAAA);
                            }
                            if (packMode.shift <= 2) {
                                pixel = ((pixel >> 2) & 0x33333333) | ((pixel << 2) & 0xCCCCCCCC);
                            }
                            if (packMode.shift <= 4) {
                                pixel = ((pixel >> 4) & 0x0F0F0F0F) | ((pixel << 4) & 0xF0F0F0F0);
                            }
                        }
                        for (int i = 0; i < packMode.size; i++) {
                            color = translateColor(pixel & packMode.mask, colorMode);
                            imageData.setPixel(x, y, color);
                            stepTrace();
                            pixel >>= packMode.shift;
                        }
                    }
                    else {
                        color = translateColor(pixel, colorMode);
                        imageData.setPixel(x, y, color);
                        stepTrace();
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            else {
                switch (cmd.toUpperCase()) {
                    case "LUT1":
                    case "LUT2":
                    case "LUT4":
                    case "LUT8":
                    case "LUMA8":
                    case "LUMA8W":
                    case "LUMA8X":
                    case "HSV8":
                    case "HSV8W":
                    case "HSV8X":
                    case "RGBI8":
                    case "RGBI8W":
                    case "RGBI8X":
                    case "HSV16":
                    case "HSV16W":
                    case "HSV16X":
                    case "RGB8":
                    case "RGB16":
                    case "RGB24":
                        colorMode(cmd, iter);
                        break;

                    case "LUTCOLORS":
                        lutColors(iter);
                        break;

                    case "TRACE":
                        trace(iter);
                        break;

                    case "RATE":
                        rate(iter);
                        break;

                    case "SET":
                        if (iter.hasNextNumber()) {
                            int newX = iter.nextNumber();
                            if (iter.hasNextNumber()) {
                                x = newX;
                                y = iter.nextNumber();
                            }
                        }
                        traceMode &= 7;
                        break;

                    case "SCROLL":
                        if (iter.hasNextNumber()) {
                            iter.nextNumber();
                            if (iter.hasNextNumber()) {
                                iter.nextNumber();
                            }
                        }
                        break;

                    case "CLEAR":
                        color = translateColor(0, colorMode);
                        for (int y = 0; y < imageSize.y; y++) {
                            for (int x = 0; x < imageSize.x; x++) {
                                imageData.setPixel(x, y, color);
                            }
                        }
                        image.dispose();
                        canvas.redraw();
                        break;

                    case "UPDATE":
                        image.dispose();
                        canvas.redraw();
                        break;

                    case "SAVE":
                        if (iter.hasNext()) {
                            boolean window = false;

                            String key = iter.next();
                            if (key.equalsIgnoreCase("WINDOW")) {
                                window = true;
                                if (!iter.hasNext()) {
                                    break;
                                }
                                key = iter.next();
                            }

                            doSaveBitmap(image, key, window);
                        }
                        break;

                    case "CLOSE":
                        shell.dispose();
                        break;
                }
            }
        }
    }

    void colorMode(String cmd, KeywordIterator iter) {
        colorMode = ColorMode.valueOf(cmd.toUpperCase());
        if (iter.hasNext()) {
            String key = iter.peekNext().toUpperCase();
            switch (key) {
                case "ORANGE":
                case "BLUE":
                case "GREEN":
                case "CYAN":
                case "RED":
                case "MAGENTA":
                case "YELLOW":
                case "GREY":
                    colorTune = RGBColor.valueOf(key).ordinal();
                    iter.next();
                    break;
            }
        }
    }

    void lutColors(KeywordIterator iter) {
        int color;

        for (int i = 0; i < 256 && iter.hasNext(); i++) {
            color = 0x000000;
            if (iter.hasNextNumber()) {
                color = translateColor(iter.nextNumber(), ColorMode.RGB24);
            }
            else {
                int h = 0;
                int p = 8;

                String s = iter.next().toUpperCase();
                switch (s) {
                    case "BLACK":
                        color = 0x000000;
                        break;
                    case "WHITE":
                        color = 0xFFFFFF;
                        break;
                    case "ORANGE":
                    case "BLUE":
                    case "GREEN":
                    case "CYAN":
                    case "RED":
                    case "MAGENTA":
                    case "YELLOW":
                    case "GREY":
                        h = RGBColor.valueOf(s).ordinal();
                        if (iter.hasNextNumber()) {
                            p = iter.nextNumber();
                        }
                        color = translateColor((h << 5) | (p << 1), ColorMode.RGBI8X);
                        break;
                }
            }
            lutColors[i] = color;
        }
    }

    int translateColor(int p, ColorMode mode) {
        switch (mode) {
            case LUT1:
                return lutColors[p & 0b1];
            case LUT2:
                return lutColors[p & 0b11];
            case LUT4:
                return lutColors[p & 0b1111];
            case LUT8:
                return lutColors[p & 0b11111111];
            default:
                return mode.translateColor(p, colorTune);
        }
    }

    void stepTrace() {
        switch (traceMode) {
            case 0:
            case 0 | 8:
                if (++x >= imageData.width) {
                    x = 0;
                    if ((traceMode & 8) != 0) {
                        scrollDown();
                    }
                    else if (++y >= imageData.height) {
                        y = 0;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
            case 1:
            case 1 | 8:
                if (--x < 0) {
                    x = imageData.width - 1;
                    if ((traceMode & 8) != 0) {
                        scrollDown();
                    }
                    else if (++y >= imageData.height) {
                        y = 0;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
            case 2:
            case 2 | 8:
                if (++x >= imageData.width) {
                    x = 0;
                    if ((traceMode & 8) != 0) {
                        scrollUp();
                    }
                    else if (--y < 0) {
                        y = imageData.height - 1;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
            case 3:
            case 3 | 8:
                if (--x < 0) {
                    x = imageData.width - 1;
                    if ((traceMode & 8) != 0) {
                        scrollUp();
                    }
                    else if (--y < 0) {
                        y = imageData.height - 1;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
            case 4:
            case 4 | 8:
                if (++y >= imageData.height) {
                    y = 0;
                    if ((traceMode & 8) != 0) {
                        scrollRight();
                    }
                    else if (++x >= imageData.width) {
                        x = 0;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
            case 5:
            case 5 | 8:
                if (--y < 0) {
                    y = imageData.height - 1;
                    if ((traceMode & 8) != 0) {
                        scrollRight();
                    }
                    else if (++x >= imageData.width) {
                        x = 0;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
            case 6:
            case 6 | 8:
                if (++y >= imageData.height) {
                    y = 0;
                    if ((traceMode & 8) != 0) {
                        scrollLeft();
                    }
                    else if (--x < 0) {
                        x = imageData.width - 1;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
            case 7 | 8:
                if (--y < 0) {
                    y = imageData.height - 1;
                    if ((traceMode & 8) != 0) {
                        scrollRight();
                    }
                    else if (--x < 0) {
                        x = imageData.width - 1;
                    }
                    if (autoUpdate && rate == -1) {
                        image.dispose();
                        canvas.redraw();
                    }
                }
                break;
        }

        if (autoUpdate && rate != -1) {
            if (rateCount++ >= rate) {
                image.dispose();
                canvas.redraw();
                rateCount = 0;
            }
        }
    }

    void scrollLeft() {
        Image target = new Image(display, imageData);

        GC gc = new GC(target);
        try {
            Image temp = new Image(display, imageData);
            gc.drawImage(temp, 1, 0, imageData.width - 1, imageData.height, 0, 0, imageData.width - 1, imageData.height);
            gc.drawImage(temp, 0, 0, 1, imageData.height, imageData.width - 1, 0, 1, imageData.height);
            temp.dispose();
        } finally {
            gc.dispose();
        }

        imageData = target.getImageData();
    }

    void scrollRight() {
        Image target = new Image(display, imageData);

        GC gc = new GC(target);
        try {
            Image temp = new Image(display, imageData);
            gc.drawImage(temp, 0, 0, imageData.width - 1, imageData.height, 1, 0, imageData.width - 1, imageData.height);
            gc.drawImage(temp, imageData.width - 1, 0, 1, imageData.height, 0, 0, 1, imageData.height);
            temp.dispose();
        } finally {
            gc.dispose();
        }

        imageData = target.getImageData();
    }

    void scrollDown() {
        Image target = new Image(display, imageData);

        GC gc = new GC(target);
        try {
            Image temp = new Image(display, imageData);
            gc.drawImage(temp, 0, 0, imageData.width, imageData.height - 1, 0, 1, imageData.width, imageData.height - 1);
            gc.drawImage(temp, 0, imageData.height - 1, imageData.width, 1, 0, 0, imageData.width, 1);
            temp.dispose();
        } finally {
            gc.dispose();
        }

        imageData = target.getImageData();
    }

    void scrollUp() {
        Image target = new Image(display, imageData);

        GC gc = new GC(target);
        try {
            Image temp = new Image(display, imageData);
            gc.drawImage(temp, 0, 1, imageData.width, imageData.height - 1, 0, 0, imageData.width, imageData.height - 1);
            gc.drawImage(temp, 0, 0, imageData.width, 1, 0, imageData.height - 1, imageData.width, 1);
            temp.dispose();
        } finally {
            gc.dispose();
        }

        imageData = target.getImageData();
    }

    static String[] data = new String[] {
        "SIZE 32 16 DOTSIZE 8 LUT2 LONGS_2BIT",
        "TRACE 14 LUTCOLORS WHITE RED BLUE YELLOW 6",
        "$FFFF_FFFC",
        "$444_AAA8",
        "$444_8888",
        "$444_AAA8",
        "$444_A228",
        "$444_AAA8",
        "$444_8888",
        "$444_AAA8",
        "$444_A228",
        "$444_AAA8",
        "$444_8888",
        "$444_AAA8",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$444_4444",
        "$0",
        "$0",
        "$0",
        "$0",
        "$0",
    };

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
                    DebugWindow window = new DebugBitmapWindow();
                    window.create();
                    window.setup(new KeywordIterator(data[0]));
                    window.open();

                    display.asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            for (int i = 1; i < data.length; i++) {
                                window.update(new KeywordIterator(data[i]));
                            }
                        }

                    });

                    while (display.getShells().length != 0) {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        display.dispose();
    }

}
