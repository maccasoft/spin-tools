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
import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.Preferences;
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
            pt.x += MARGIN_WIDTH;
            pt.y += MARGIN_HEIGHT + charHeight * channelData.length + MARGIN_HEIGHT;

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

        size = 128;
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

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (Preferences.getInstance().getEditorFont() != null) {
            fontData = StringConverter.asFontData(Preferences.getInstance().getEditorFont());
        }

        font = new Font(display, fontData.getName(), textSize != 0 ? textSize : fontData.getHeight(), SWT.BOLD);

        GC gc = new GC(canvas);
        try {
            gc.setFont(font);
            charHeight = gc.stringExtent("X").y;
            for (Channel ch : channelData) {
                Point extent = gc.stringExtent(ch.name);
                if (extent.y > charHeight) {
                    charHeight = extent.y;
                }
            }
        } finally {
            gc.dispose();
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
        gridData.widthHint = MARGIN_WIDTH + imageSize.x + MARGIN_WIDTH;
        gridData.heightHint = MARGIN_HEIGHT + charHeight * channelData.length + MARGIN_HEIGHT + imageSize.y + MARGIN_HEIGHT;
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
                case "GRAY":
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
    protected void paint(GC gc) {
        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);

        gc.setBackground(backColor);
        gc.fillRectangle(canvas.getClientArea());

        gc.setLineStyle(SWT.LINE_SOLID);

        int x = MARGIN_WIDTH + (imageSize.x / 2) + (int) Math.round(0 * scale);
        int y = MARGIN_HEIGHT + charHeight * channelData.length + MARGIN_HEIGHT + (imageSize.y / 2) - (int) Math.round(0 * scale);
        gc.setForeground(gridColor);
        gc.drawLine(x, 0, x, MARGIN_HEIGHT + charHeight * channelData.length + MARGIN_HEIGHT + imageSize.y + MARGIN_HEIGHT);
        gc.drawLine(0, y, MARGIN_WIDTH + imageSize.x + MARGIN_WIDTH, y);

        gc.drawOval(MARGIN_WIDTH, MARGIN_HEIGHT + charHeight * channelData.length + MARGIN_HEIGHT, imageSize.x, imageSize.y);

        gc.setLineWidth(dotSize);
        gc.setFont(font);

        int textX = MARGIN_WIDTH;
        for (int i = 0; i < channelData.length; i++) {
            gc.setForeground(channelData[i].color);
            gc.drawText(channelData[i].name, textX, MARGIN_HEIGHT, true);
            textX += gc.stringExtent(channelData[i].name).x + 5;
            channelData[i].draw(gc);
        }
    }

    @Override
    public void update(KeywordIterator iter) {
        String cmd;

        while (iter.hasNext()) {
            cmd = iter.next();

            if (isNumber(cmd)) {
                try {
                    packMode.newPack(stringToNumber(cmd));
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
                        save(iter);
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
            tf = (Math.PI / 2 - (y + theta)) / twoPi * Math.PI * 2;
            yf = Math.sin(tf);
            xf = Math.cos(tf);
            return new Point((imageSize.x / 2) + (int) Math.round(rf * xf), (imageSize.y / 2) + (int) Math.round(rf * yf));
        }
        else if (logScale) {
            rf = (Math.log(Math.hypot(x, y) + 1) / Math.log((double) range + 1)) * (imageSize.x / 2);
            tf = Math.atan2(x, y);
            yf = Math.sin(tf);
            xf = Math.cos(tf);
            return new Point((imageSize.x / 2) + (int) Math.round(rf * xf), (imageSize.y / 2) - (int) Math.round(rf * yf));
        }

        return new Point((imageSize.x / 2) + (int) Math.round(x * scale), (imageSize.y / 2) - (int) Math.round(y * scale));
    }

    void update() {
        canvas.redraw();
    }

    static final String[] data = new String[] {
        "SIZE 80 RANGE 8 SAMPLES 0 LOGSCALE 'LogScale'",
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
                    DebugWindow window = new DebugScopeXYWindow(new CircularBuffer(128));
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
