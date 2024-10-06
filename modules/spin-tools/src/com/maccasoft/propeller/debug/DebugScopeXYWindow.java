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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;

public class DebugScopeXYWindow extends DebugWindow {

    Image image;
    GC imageGc;

    Color backColor;
    Color gridColor;

    ColorMode colorMode;
    int colorTune;
    Color[] lutColors;

    int lineSize;
    int textSize;

    PackMode packMode;

    int size;
    int range;
    boolean logScale;

    boolean polar;
    long twoPi;
    int theta;

    int samples;
    int sampleCount;
    int sampleIndex;

    int rate;
    int rateCount;

    Font font;
    int charHeight;

    int channelIndex;
    Channel[] channelData;

    double scale;
    int sampleX;
    int sampleY;

    class Channel {
        String name;
        Color color;

        Channel(String name, Color color) {
            this.name = name;
            this.color = color;
        }

    }

    public DebugScopeXYWindow() {
        backColor = new Color(0, 0, 0);
        gridColor = new Color(64, 64, 64);

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new Color[256];

        lineSize = 1;
        textSize = 0;

        packMode = PackMode.NONE();

        range = 0x7FFFFFFF;
        logScale = false;

        polar = false;
        twoPi = 0x100000000L;
        theta = 0;

        samples = 256;
        sampleCount = 0;
        sampleIndex = 0;

        rate = 1;
        rateCount = 0;

        channelIndex = 0;
        channelData = new Channel[0];
    }

    @Override
    public void setup(KeywordIterator iter) {
        String cmd;

        while (iter.hasNext()) {
            cmd = iter.next();
            if (isString(cmd)) {
                channel(stringStrip(cmd), iter);
            }
            else {
                switch (cmd.toUpperCase()) {
                    case "TITLE":
                        title(iter);
                        break;

                    case "POS":
                        pos(iter);
                        break;

                    case "SIZE":
                        if (iter.hasNextNumber()) {
                            size = iter.nextNumber();
                        }
                        break;

                    case "RANGE":
                        if (iter.hasNextNumber()) {
                            range = iter.nextNumber();
                        }
                        break;

                    case "SAMPLES":
                        if (iter.hasNextNumber()) {
                            samples = iter.nextNumber();
                        }
                        break;

                    case "RATE":
                        rate = 1;
                        rateCount = 0;
                        if (iter.hasNextNumber()) {
                            rate = iter.nextNumber();
                        }
                        break;

                    case "DOTSIZE":
                        if (iter.hasNextNumber()) {
                            dotSize.x = iter.nextNumber();
                            dotSize.y = dotSize.x;
                        }
                        break;

                    case "TEXTSIZE":
                        if (iter.hasNextNumber()) {
                            textSize = iter.nextNumber();
                        }
                        break;

                    case "COLOR":
                        if (iter.hasNextNumber()) {
                            iter.nextNumber();
                            if (iter.hasNextNumber()) {
                                iter.nextNumber();
                            }
                        }
                        break;

                    case "POLAR":
                        polar = true;
                        twoPi = 0x100000000L;
                        theta = 0;
                        if (iter.hasNextNumber()) {
                            twoPi = iter.nextNumber();
                            if (twoPi == -1) {
                                twoPi = -0x100000000L;
                            }
                            else if (twoPi == 0) {
                                twoPi = 0x100000000L;
                            }
                            if (iter.hasNextNumber()) {
                                theta = iter.nextNumber();
                            }
                        }
                        break;

                    case "LOGSCALE":
                        logScale = true;
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
                        packMode = packedMode(cmd, iter);
                        break;

                    case "HIDEXY":
                        break;
                }
            }
        }

        scale = (double) size / (double) range;

        imageSize.x = imageSize.y = (size * 2) + 1;

        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
        imageGc = new GC(image);

        imageGc.setAdvanced(true);
        imageGc.setAdvanced(true);
        imageGc.setAntialias(SWT.OFF);
        imageGc.setTextAntialias(SWT.ON);
        imageGc.setInterpolation(SWT.NONE);
        imageGc.setLineCap(SWT.CAP_SQUARE);
        imageGc.setLineWidth(lineSize);
        imageGc.setFont(font);

        update();

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                Point canvasSize = canvas.getSize();

                e.gc.setAdvanced(true);
                e.gc.setAntialias(SWT.ON);
                e.gc.setInterpolation(SWT.HIGH);

                e.gc.drawImage(image, 0, 0, imageSize.x, imageSize.y, 0, 0, canvasSize.x, canvasSize.y);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                imageGc.dispose();
                image.dispose();
            }

        });

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = imageSize.x * dotSize.x;
        gridData.heightHint = imageSize.y * dotSize.y;

        shell.pack();
        shell.redraw();
    }

    void channel(String name, KeywordIterator iter) {
        Color color = defaultColors[channelData.length % defaultColors.length];

        if (iter.hasNext()) {
            int h = 0;
            int p = 8;

            String s = iter.next().toUpperCase();
            switch (s) {
                case "BLACK":
                    color = new Color(0, 0, 0);
                    break;
                case "WHITE":
                    color = new Color(255, 255, 255);
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
                default:
                    iter.back();
                    break;
            }
        }

        int i = 0;
        Channel[] newArray = new Channel[channelData.length + 1];
        while (i < channelData.length) {
            newArray[i] = channelData[i++];
        }
        newArray[i] = new Channel(name, color);
        channelData = newArray;
    }

    Color translateColor(int p, ColorMode mode) {
        int color = mode.translateColor(p, 6);
        return new Color(display, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
    }

    @Override
    public void update(KeywordIterator iter) {
        int sample;
        String cmd;

        while (iter.hasNext()) {
            cmd = iter.next();

            if (isNumber(cmd)) {
                try {
                    sample = stringToNumber(cmd);
                    packMode.newPack(sample);
                    for (int i = 0; i < packMode.size; i++) {
                        processSample(packMode.unpack());
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            else {
                switch (cmd.toUpperCase()) {
                    case "CLEAR":
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
                            if (isString(key)) {
                                doSaveBitmap(image, stringStrip(key), window);
                            }
                        }
                        break;

                    case "CLOSE":
                        shell.dispose();
                        break;
                }
            }
        }
    }

    void processSample(int sample) {
        if (channelIndex == 0) {
            sampleX = sample;
        }
        else {
            sampleY = sample;
        }
        if (++channelIndex >= 2) {
            Point pt = translatePoint(sampleX, sampleY);
            imageGc.setForeground(new Color(0, 250, 0));
            imageGc.drawPoint(pt.x, pt.y);

            if (++rateCount >= rate) {
                canvas.redraw();
                rateCount = 0;
            }

            channelIndex = 0;
        }
    }

    Point translatePoint(int x, int y) {
        double rf, tf, xf, yf;

        if (polar) {
            if (logScale) {
                if (x != 0) {
                    rf = (Math.log(x) / Math.log(range)) * (imageSize.x / 2);
                }
                else {
                    rf = 0;
                }
            }
            else {
                rf = x * scale;
            }
            tf = Math.PI / 2 - (y + theta) / twoPi * Math.PI * 2;
            yf = Math.sin(tf);
            xf = Math.cos(tf);
            return new Point((int) Math.round(xf * rf), (int) Math.round(yf * rf));
        }
        else if (logScale) {
            rf = (Math.log(Math.hypot(sampleX, sampleY) + 1) / Math.log((double) range + 1)) * (imageSize.x / 2);
            tf = Math.atan2(sampleX, sampleY);
            yf = Math.sin(tf);
            xf = Math.cos(tf);
            return new Point((imageSize.x / 2) + (int) Math.round(xf * rf), (imageSize.y / 2) - (int) Math.round(yf * rf));
        }

        return new Point((imageSize.x / 2) + (int) Math.round(sampleX * scale), (imageSize.y / 2) - (int) Math.round(sampleY * scale));
    }

    void update() {
        imageGc.setBackground(backColor);
        imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);

        imageGc.setLineStyle(SWT.LINE_SOLID);

        int x = (imageSize.x / 2) + (int) Math.round(0 * scale);
        int y = (imageSize.y / 2) - (int) Math.round(0 * scale);
        imageGc.setForeground(gridColor);
        imageGc.drawLine(x, 0, x, imageSize.y - 1);
        imageGc.drawLine(0, y, imageSize.x - 1, y);

        imageGc.drawOval(0, 0, imageSize.x - 1, imageSize.y - 1);

        canvas.redraw();
    }

    static final String[] data = new String[] {
        "SIZE 80 RANGE 8 SAMPLES 0 'Normal'",
        "-8, -8",
        "-8, -7",
        "-8, -6",
        "-8, -5",
        "-8, -4",
        "-8, -3",
        "-8, -2",
        "-8, -1",
        "-8, 0",
        "-8, 1",
        "-8, 2",
        "-8, 3",
        "-8, 4",
        "-8, 5",
        "-8, 6",
        "-8, 7",
        "-8, 8",
        "-7, -8",
        "-7, -7",
        "-7, -6",
        "-7, -5",
        "-7, -4",
        "-7, -3",
        "-7, -2",
        "-7, -1",
        "-7, 0",
        "-7, 1",
        "-7, 2",
        "-7, 3",
        "-7, 4",
        "-7, 5",
        "-7, 6",
        "-7, 7",
        "-7, 8",
        "-6, -8",
        "-6, -7",
        "-6, -6",
        "-6, -5",
        "-6, -4",
        "-6, -3",
        "-6, -2",
        "-6, -1",
        "-6, 0",
        "-6, 1",
        "-6, 2",
        "-6, 3",
        "-6, 4",
        "-6, 5",
        "-6, 6",
        "-6, 7",
        "-6, 8",
        "-5, -8",
        "-5, -7",
        "-5, -6",
        "-5, -5",
        "-5, -4",
        "-5, -3",
        "-5, -2",
        "-5, -1",
        "-5, 0",
        "-5, 1",
        "-5, 2",
        "-5, 3",
        "-5, 4",
        "-5, 5",
        "-5, 6",
        "-5, 7",
        "-5, 8",
        "-4, -8",
        "-4, -7",
        "-4, -6",
        "-4, -5",
        "-4, -4",
        "-4, -3",
        "-4, -2",
        "-4, -1",
        "-4, 0",
        "-4, 1",
        "-4, 2",
        "-4, 3",
        "-4, 4",
        "-4, 5",
        "-4, 6",
        "-4, 7",
        "-4, 8",
        "-3, -8",
        "-3, -7",
        "-3, -6",
        "-3, -5",
        "-3, -4",
        "-3, -3",
        "-3, -2",
        "-3, -1",
        "-3, 0",
        "-3, 1",
        "-3, 2",
        "-3, 3",
        "-3, 4",
        "-3, 5",
        "-3, 6",
        "-3, 7",
        "-3, 8",
        "-2, -8",
        "-2, -7",
        "-2, -6",
        "-2, -5",
        "-2, -4",
        "-2, -3",
        "-2, -2",
        "-2, -1",
        "-2, 0",
        "-2, 1",
        "-2, 2",
        "-2, 3",
        "-2, 4",
        "-2, 5",
        "-2, 6",
        "-2, 7",
        "-2, 8",
        "-1, -8",
        "-1, -7",
        "-1, -6",
        "-1, -5",
        "-1, -4",
        "-1, -3",
        "-1, -2",
        "-1, -1",
        "-1, 0",
        "-1, 1",
        "-1, 2",
        "-1, 3",
        "-1, 4",
        "-1, 5",
        "-1, 6",
        "-1, 7",
        "-1, 8",
        "0, -8",
        "0, -7",
        "0, -6",
        "0, -5",
        "0, -4",
        "0, -3",
        "0, -2",
        "0, -1",
        "0, 0",
        "0, 1",
        "0, 2",
        "0, 3",
        "0, 4",
        "0, 5",
        "0, 6",
        "0, 7",
        "0, 8",
        "1, -8",
        "1, -7",
        "1, -6",
        "1, -5",
        "1, -4",
        "1, -3",
        "1, -2",
        "1, -1",
        "1, 0",
        "1, 1",
        "1, 2",
        "1, 3",
        "1, 4",
        "1, 5",
        "1, 6",
        "1, 7",
        "1, 8",
        "2, -8",
        "2, -7",
        "2, -6",
        "2, -5",
        "2, -4",
        "2, -3",
        "2, -2",
        "2, -1",
        "2, 0",
        "2, 1",
        "2, 2",
        "2, 3",
        "2, 4",
        "2, 5",
        "2, 6",
        "2, 7",
        "2, 8",
        "3, -8",
        "3, -7",
        "3, -6",
        "3, -5",
        "3, -4",
        "3, -3",
        "3, -2",
        "3, -1",
        "3, 0",
        "3, 1",
        "3, 2",
        "3, 3",
        "3, 4",
        "3, 5",
        "3, 6",
        "3, 7",
        "3, 8",
        "4, -8",
        "4, -7",
        "4, -6",
        "4, -5",
        "4, -4",
        "4, -3",
        "4, -2",
        "4, -1",
        "4, 0",
        "4, 1",
        "4, 2",
        "4, 3",
        "4, 4",
        "4, 5",
        "4, 6",
        "4, 7",
        "4, 8",
        "5, -8",
        "5, -7",
        "5, -6",
        "5, -5",
        "5, -4",
        "5, -3",
        "5, -2",
        "5, -1",
        "5, 0",
        "5, 1",
        "5, 2",
        "5, 3",
        "5, 4",
        "5, 5",
        "5, 6",
        "5, 7",
        "5, 8",
        "6, -8",
        "6, -7",
        "6, -6",
        "6, -5",
        "6, -4",
        "6, -3",
        "6, -2",
        "6, -1",
        "6, 0",
        "6, 1",
        "6, 2",
        "6, 3",
        "6, 4",
        "6, 5",
        "6, 6",
        "6, 7",
        "6, 8",
        "7, -8",
        "7, -7",
        "7, -6",
        "7, -5",
        "7, -4",
        "7, -3",
        "7, -2",
        "7, -1",
        "7, 0",
        "7, 1",
        "7, 2",
        "7, 3",
        "7, 4",
        "7, 5",
        "7, 6",
        "7, 7",
        "7, 8",
        "8, -8",
        "8, -7",
        "8, -6",
        "8, -5",
        "8, -4",
        "8, -3",
        "8, -2",
        "8, -1",
        "8, 0",
        "8, 1",
        "8, 2",
        "8, 3",
        "8, 4",
        "8, 5",
        "8, 6",
        "8, 7",
        "8, 8"
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
                    DebugWindow window = new DebugScopeXYWindow();
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
