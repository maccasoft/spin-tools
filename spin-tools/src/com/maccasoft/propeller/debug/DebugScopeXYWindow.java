/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
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

import com.maccasoft.propeller.internal.CircularBuffer;

public class DebugScopeXYWindow extends DebugWindow {

    Color backColor;
    Color gridColor;

    ColorMode colorMode;
    int colorTune;
    Color[] lutColors;

    int dotSize;
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

    boolean xy;
    int sample0;

    class Channel {
        String name;
        Color color;
        List<Point> points = new ArrayList<>();

        Channel(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        void add(Point pt) {
            points.add(pt);
            if (samples != 0) {
                while (points.size() > samples) {
                    points.remove(0);
                }
            }
        }

        void draw(GC gc) {
            gc.setBackground(color);
            for (Point pt : points) {
                gc.fillOval(pt.x - dotSize / 2, pt.y - dotSize / 2, dotSize + 1, dotSize + 1);
            }
        }

    }

    public DebugScopeXYWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        backColor = new Color(0, 0, 0);
        gridColor = new Color(64, 64, 64);

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new Color[256];

        dotSize = 6 / 2;
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
                            dotSize = iter.nextNumber() / 2;
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
        origin = new Point(imageSize.x / 2, imageSize.y / 2);

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                paint(e.gc);
            }

        });

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = imageSize.x;
        gridData.heightHint = imageSize.y;

        shell.pack();
        shell.redraw();
    }

    void paint(GC gc) {
        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);

        gc.setBackground(backColor);
        gc.fillRectangle(0, 0, imageSize.x, imageSize.y);

        gc.setLineStyle(SWT.LINE_SOLID);

        int x = (imageSize.x / 2) + (int) Math.round(0 * scale);
        int y = (imageSize.y / 2) - (int) Math.round(0 * scale);
        gc.setForeground(gridColor);
        gc.drawLine(x, 0, x, imageSize.y - 1);
        gc.drawLine(0, y, imageSize.x - 1, y);

        gc.drawOval(0, 0, imageSize.x - 1, imageSize.y - 1);

        int textX = 5;
        gc.setLineWidth(dotSize);
        for (int i = 0; i < channelData.length; i++) {
            gc.setForeground(channelData[i].color);
            gc.drawText(channelData[i].name, textX, 5, true);
            textX += gc.stringExtent(channelData[i].name).x + 5;
            channelData[i].draw(gc);
        }
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
                        for (int i = 0; i < channelData.length; i++) {
                            channelData[i].points.clear();
                        }
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
                                Image image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
                                GC gc = new GC(image);
                                try {
                                    paint(gc);
                                } finally {
                                    gc.dispose();
                                }
                                doSaveBitmap(image, stringStrip(key), window);
                            }
                        }
                        break;

                    case "CLOSE":
                        shell.dispose();
                        break;

                    case "PC_KEY":
                        sendKeyPress();
                        break;

                    case "PC_MOUSE":
                        sendMouse();
                        break;
                }
            }
        }
    }

    void processSample(int sample) {
        if (xy) {
            Point pt = translatePoint(sample0, sample);
            channelData[channelIndex].add(pt);

            channelIndex++;
            if (channelIndex >= channelData.length) {
                if (++rateCount >= rate) {
                    canvas.redraw();
                    rateCount = 0;
                }
                channelIndex = 0;
            }
        }
        else {
            sample0 = sample;
        }
        xy = !xy;
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
            rf = (Math.log(Math.hypot(x, y) + 1) / Math.log((double) range + 1)) * (imageSize.x / 2);
            tf = Math.atan2(x, y);
            yf = Math.sin(tf);
            xf = Math.cos(tf);
            return new Point((imageSize.x / 2) + (int) Math.round(xf * rf), (imageSize.y / 2) - (int) Math.round(yf * rf));
        }

        return new Point((imageSize.x / 2) + (int) Math.round(x * scale), (imageSize.y / 2) - (int) Math.round(y * scale));
    }

    void update() {
        canvas.redraw();
    }

}
