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

public class DebugScopeWindow extends DebugWindow {

    Color backColor;
    Color gridColor;

    ColorMode colorMode;
    int colorTune;
    Color[] lutColors;

    int dotSize;
    int lineSize;
    int textSize;

    PackMode packMode;

    int samples;
    int channelIndex;
    Channel[] channelData;
    int defaultColorIndex;

    int sampleCount;

    int triggerChannel;
    int triggerArm;
    int triggerFire;
    int triggerOffset;
    boolean armed;
    boolean triggered;

    int holdOff;
    int holdOffCount;

    int rate;
    int rateCount;

    Font legendFont;
    Font channelFont;
    int charHeight;

    class Channel {
        String name;

        int min;
        int max;
        boolean auto;

        int y_size;
        int y_base;

        int legend;
        Color color;

        int[] sampleData;

        String legendMax;
        int legendMaxY;
        String legendMin;
        int legendMinY;
        int[] linePoints;

        Channel(String name, int min, int max, boolean auto, int y_size, int y_base, int legend, Color color) {
            this.name = name;

            this.min = min;
            this.max = max;
            this.auto = auto;

            this.y_size = y_size;
            this.y_base = y_base;

            this.legend = legend;
            this.color = color;

            if (legend != 0 && !auto) {
                double sy = (double) y_size / (double) (max - min);

                legendMax = String.format("%c%d", max >= 0 ? '+' : '-', Math.abs(max));
                legendMaxY = MARGIN_HEIGHT + charHeight + (imageSize.y - y_base) - (int) Math.round((max - min) * sy);

                legendMin = String.format("%c%d", min >= 0 ? '+' : '-', Math.abs(min));
                legendMinY = MARGIN_HEIGHT + charHeight + (imageSize.y - y_base);
            }

            this.sampleData = new int[samples];
            this.linePoints = new int[0];
        }

        void update() {
            double sx = (double) imageSize.x / (double) samples;
            double sy = (double) y_size / (double) (max - min);
            int firstSample = sampleData.length - sampleCount;

            if (auto) {
                min = Integer.MAX_VALUE;
                max = Integer.MIN_VALUE;
                for (int i = firstSample; i < sampleData.length; i++) {
                    int d = sampleData[i];
                    if (d < min) {
                        min = d;
                    }
                    if (d > max) {
                        max = d;
                    }
                }

                if (legend != 0) {
                    legendMax = String.format("%c%d", max >= 0 ? '+' : '-', Math.abs(max));
                    legendMaxY = MARGIN_HEIGHT + charHeight + (imageSize.y - y_base) - (int) Math.round((max - min) * sy);

                    legendMin = String.format("%c%d", min >= 0 ? '+' : '-', Math.abs(min));
                    legendMinY = MARGIN_HEIGHT + charHeight + (imageSize.y - y_base);
                }
            }

            int arraySize = (sampleData.length - firstSample) * 2;
            if (linePoints.length != arraySize) {
                linePoints = new int[arraySize];
            }

            double x = firstSample * sx;
            for (int i = firstSample, idx = 0; i < sampleData.length; i++) {
                linePoints[idx++] = MARGIN_WIDTH + (int) Math.round(x);
                linePoints[idx++] = MARGIN_HEIGHT + charHeight + (imageSize.y - y_base) - (int) Math.round((sampleData[i] - min) * sy);
                x += sx;
            }
        }

        void plot(GC gc) {
            if (legend != 0) {
                gc.setLineWidth(0);
                gc.setLineStyle(SWT.LINE_DOT);
                gc.setFont(legendFont);
                gc.setForeground(gridColor);
                gc.setBackground(backColor);

                if ((legend & 0b0001) != 0) {
                    gc.drawLine(MARGIN_WIDTH, legendMinY, imageSize.x, legendMinY);
                }
                if ((legend & 0b0010) != 0) {
                    gc.drawLine(MARGIN_WIDTH, legendMaxY, imageSize.x, legendMaxY);
                }

                if (legendMin != null && (legend & 0b0100) != 0) {
                    Point extent = gc.stringExtent(legendMin);
                    gc.fillRectangle(MARGIN_WIDTH + 1, legendMinY - extent.y / 2, 2 + extent.x + 2, extent.y);
                    gc.drawText(legendMin, MARGIN_WIDTH + 2, legendMinY - extent.y / 2, true);
                }
                if (legendMax != null && (legend & 0b1000) != 0) {
                    Point extent = gc.stringExtent(legendMax);
                    gc.fillRectangle(MARGIN_WIDTH + 1, legendMaxY - extent.y / 2, 2 + extent.x + 2, extent.y);
                    gc.drawText(legendMax, MARGIN_WIDTH + 2, legendMaxY - extent.y / 2, true);
                }
            }

            gc.setLineWidth(lineSize);
            gc.setLineStyle(SWT.LINE_SOLID);
            gc.setForeground(color);
            gc.drawPolyline(linePoints);
        }

    }

    public DebugScopeWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        backColor = new Color(0, 0, 0);
        gridColor = new Color(64, 64, 64);

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new Color[256];

        dotSize = 0;
        lineSize = 1;
        textSize = 0;

        packMode = PackMode.NONE();

        samples = 256;
        channelIndex = 0;
        channelData = new Channel[0];
        defaultColorIndex = 0;

        sampleCount = 0;

        triggerChannel = -1;
        triggerArm = -1;
        triggerFire = 0;
        armed = false;
        triggered = false;

        holdOff = samples;
        holdOffCount = holdOff;

        rate = 1;
        rateCount = 0;
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

                case "SAMPLES":
                    if (iter.hasNextNumber()) {
                        samples = iter.nextNumber();
                        holdOff = samples;
                    }
                    break;

                case "RATE":
                    rate(iter);
                    break;

                case "DOTSIZE":
                    if (iter.hasNextNumber()) {
                        dotSize = iter.nextNumber();
                    }
                    break;

                case "LINESIZE":
                    if (iter.hasNextNumber()) {
                        lineSize = iter.nextNumber() / 2;
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

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (Preferences.getInstance().getEditorFont() != null) {
            fontData = StringConverter.asFontData(Preferences.getInstance().getEditorFont());
        }
        fontData.setStyle(SWT.NONE);

        channelFont = new Font(display, fontData.getName(), textSize != 0 ? textSize : fontData.getHeight(), SWT.BOLD);
        legendFont = new Font(display, fontData.getName(), textSize != 0 ? textSize : fontData.getHeight(), SWT.NONE);

        GC gc = new GC(canvas);
        try {
            gc.setFont(channelFont);
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

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                paint(e.gc);
            }

        });

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = MARGIN_WIDTH + imageSize.x + MARGIN_WIDTH;
        gridData.heightHint = MARGIN_HEIGHT + charHeight + imageSize.y + MARGIN_HEIGHT;
    }

    void rate(KeywordIterator iter) {
        rate = -1;
        rateCount = 0;
        if (iter.hasNextNumber()) {
            rate = iter.nextNumber();
        }
    }

    @Override
    protected void paint(GC gc) {
        gc.setAdvanced(true);
        gc.setAntialias(SWT.OFF);
        gc.setTextAntialias(SWT.ON);
        gc.setInterpolation(SWT.NONE);
        gc.setLineCap(SWT.CAP_SQUARE);

        gc.setBackground(backColor);
        gc.fillRectangle(canvas.getClientArea());

        gc.setLineWidth(0);
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setForeground(gridColor);
        gc.drawRectangle(MARGIN_WIDTH - 1, MARGIN_HEIGHT + charHeight - 1, imageSize.x + 1, imageSize.y + 1);

        int x = MARGIN_WIDTH;
        int space = gc.stringExtent("A").x;

        gc.setFont(channelFont);
        for (int i = 0; i < channelData.length; i++) {
            Point extent = gc.stringExtent(channelData[i].name);
            gc.setForeground(channelData[i].color);
            gc.drawString(channelData[i].name, x, MARGIN_HEIGHT + charHeight - 1 - extent.y, true);
            x += extent.x + space;
        }

        if (triggerChannel != -1) {
            double sx = (double) imageSize.x / (double) samples;
            x = MARGIN_WIDTH + (int) Math.round(triggerOffset * sx);
            gc.setForeground(gridColor);
            gc.drawLine(x, MARGIN_HEIGHT + charHeight, x, MARGIN_HEIGHT + charHeight + imageSize.y);
        }

        for (int i = 0; i < channelData.length; i++) {
            channelData[i].plot(gc);
        }
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
            else if (isString(cmd)) {
                channel(stringStrip(cmd), iter);
            }
            else {
                switch (cmd.toUpperCase()) {
                    case "TRIGGER":
                        triggerChannel = -1;
                        triggerArm = -1;
                        triggerFire = 0;
                        triggerOffset = samples / 2;
                        if (iter.hasNextNumber()) {
                            triggerChannel = iter.nextNumber();
                        }
                        if (iter.hasNextNumber()) {
                            triggerArm = iter.nextNumber();
                        }
                        if (iter.hasNextNumber()) {
                            triggerFire = iter.nextNumber();
                        }
                        if (iter.hasNextNumber()) {
                            triggerOffset = iter.nextNumber();
                        }
                        armed = false;
                        triggered = false;
                        break;

                    case "HOLDOFF":
                        holdOff = samples;
                        if (iter.hasNextNumber()) {
                            holdOff = iter.nextNumber();
                        }
                        holdOffCount = holdOff;
                        break;

                    case "CLEAR":
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

    void channel(String name, KeywordIterator iter) {
        int min = 0, max = 0;
        boolean auto = false;

        Color color = defaultColors[defaultColorIndex++];
        if (defaultColorIndex >= defaultColors.length) {
            defaultColorIndex = 0;
        }

        if (iter.hasNext() && "AUTO".equalsIgnoreCase(iter.peekNext())) {
            auto = true;
            iter.next();
        }
        else {
            min = iter.hasNextNumber() ? iter.nextNumber() : Integer.MIN_VALUE;
            max = iter.hasNextNumber() ? iter.nextNumber() : Integer.MAX_VALUE;
        }
        int y_size = iter.hasNextNumber() ? iter.nextNumber() : imageSize.y;
        int y_base = iter.hasNextNumber() ? iter.nextNumber() : 0;
        int legend = iter.hasNextNumber() ? iter.nextNumber() : 0;

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
        newArray[i++] = new Channel(name, min, max, auto, y_size, y_base, legend, color);
        channelData = newArray;
    }

    void processSample(int sample) {
        System.arraycopy(channelData[channelIndex].sampleData, 1, channelData[channelIndex].sampleData, 0, samples - 1);
        channelData[channelIndex].sampleData[samples - 1] = sample;

        channelIndex++;
        if (channelIndex >= channelData.length) {
            if (sampleCount < samples) {
                sampleCount++;
            }

            triggered = false;

            if (triggerChannel >= 0) {
                if (sampleCount >= samples) {
                    sample = channelData[triggerChannel].sampleData[(triggerOffset - 1) % samples];
                    if (armed) {
                        if (triggerFire >= triggerArm) {
                            if (sample >= triggerFire) {
                                triggered = true;
                                armed = false;
                            }
                        }
                        else {
                            if (sample <= triggerFire) {
                                triggered = true;
                                armed = false;
                            }
                        }
                    }
                    else {
                        if (triggerFire >= triggerArm) {
                            if (sample <= triggerFire) {
                                armed = true;
                            }
                        }
                        else {
                            if (sample >= triggerFire) {
                                armed = true;
                            }
                        }
                    }
                    if (holdOffCount > 0) {
                        holdOffCount--;
                    }

                    if (triggered && holdOffCount == 0) {
                        rateCount++;
                        if (rateCount >= rate) {
                            update();
                            rateCount = 0;
                        }
                        holdOffCount = holdOff;
                    }
                }
            }
            else {
                rateCount++;
                if (rateCount >= rate) {
                    update();
                    rateCount = 0;
                }
            }

            channelIndex = 0;
        }
    }

    Color translateColor(int p, ColorMode mode) {
        int color;

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
                color = mode.translateColor(p, colorTune);
                return new Color(display, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
        }
    }

    void update() {
        for (int i = 0; i < channelData.length; i++) {
            channelData[i].update();
        }
        canvas.redraw();
    }

    static String[] data = new String[] {
        "",
        "'FreqA' -1000 1000 100 136 15 MAGENTA",
        "'FreqB' -1000 1000 100 20 15 ORANGE",
        "TRIGGER 0 HOLDOFF 2",
        "0, 0",
        "31, 63",
        "63, 127",
        "94, 189",
        "125, 251",
        "156, 312",
        "187, 372",
        "218, 430",
        "249, 486",
        "279, 541",
        "309, 593",
        "339, 643",
        "368, 690",
        "397, 735",
        "426, 776",
        "454, 815",
        "482, 850",
        "509, 881",
        "536, 910",
        "562, 934",
        "588, 955",
        "613, 972",
        "637, 985",
        "661, 994",
        "685, 999",
        "707, 1_000",
        "729, 997",
        "750, 990",
        "771, 979",
        "790, 964",
        "809, 945",
        "827, 922",
        "844, 896",
        "861, 866",
        "876, 833",
        "891, 796",
        "905, 756",
        "918, 713",
        "930, 667",
        "941, 618",
        "951, 567",
        "960, 514",
        "969, 458",
        "976, 401",
        "982, 342",
        "988, 282",
        "992, 220",
        "996, 158",
        "998, 95",
        "1_000, 32",
        "1_000, -32",
        "1_000, -95",
        "998, -158",
        "996, -220",
        "992, -282",
        "988, -342",
        "982, -401",
        "976, -458",
        "969, -514",
        "960, -567",
        "951, -618",
        "941, -667",
        "930, -713",
        "918, -756",
        "905, -796",
        "891, -833",
        "876, -866",
        "861, -896",
        "844, -922",
        "827, -945",
        "809, -964",
        "790, -979",
        "771, -990",
        "750, -997",
        "729, -1_000",
        "707, -999",
        "685, -994",
        "661, -985",
        "637, -972",
        "613, -955",
        "588, -934",
        "562, -910",
        "536, -881",
        "509, -850",
        "482, -815",
        "454, -776",
        "426, -735",
        "397, -690",
        "368, -643",
        "339, -593",
        "309, -541",
        "279, -486",
        "249, -430",
        "218, -372",
        "187, -312",
        "156, -251",
        "125, -189",
        "94, -127",
        "63, -63",
        "31, 0",
        "0, 63",
        "-31, 127",
        "-63, 189",
        "-94, 251",
        "-125, 312",
        "-156, 372",
        "-187, 430",
        "-218, 486",
        "-249, 541",
        "-279, 593",
        "-309, 643",
        "-339, 690",
        "-368, 735",
        "-397, 776",
        "-426, 815",
        "-454, 850",
        "-482, 881",
        "-509, 910",
        "-536, 934",
        "-562, 955",
        "-588, 972",
        "-613, 985",
        "-637, 994",
        "-661, 999",
        "-685, 1_000",
        "-707, 997",
        "-729, 990",
        "-750, 979",
        "-771, 964",
        "-790, 945",
        "-809, 922",
        "-827, 896",
        "-844, 866",
        "-861, 833",
        "-876, 796",
        "-891, 756",
        "-905, 713",
        "-918, 667",
        "-930, 618",
        "-941, 567",
        "-951, 514",
        "-960, 458",
        "-969, 401",
        "-976, 342",
        "-982, 282",
        "-988, 220",
        "-992, 158",
        "-996, 95",
        "-998, 32",
        "-1_000, -32",
        "-1_000, -95",
        "-1_000, -158",
        "-998, -220",
        "-996, -282",
        "-992, -342",
        "-988, -401",
        "-982, -458",
        "-976, -514",
        "-969, -567",
        "-960, -618",
        "-951, -667",
        "-941, -713",
        "-930, -756",
        "-918, -796",
        "-905, -833",
        "-891, -866",
        "-876, -896",
        "-861, -922",
        "-844, -945",
        "-827, -964",
        "-809, -979",
        "-790, -990",
        "-771, -997",
        "-750, -1_000",
        "-729, -999",
        "-707, -994",
        "-685, -985",
        "-661, -972",
        "-637, -955",
        "-613, -934",
        "-588, -910",
        "-562, -881",
        "-536, -850",
        "-509, -815",
        "-482, -776",
        "-454, -735",
        "-426, -690",
        "-397, -643",
        "-368, -593",
        "-339, -541",
        "-309, -486",
        "-279, -430",
        "-249, -372",
        "-218, -312",
        "-187, -251",
        "-156, -189",
        "-125, -127",
        "-94, -63",
        "-63, 0",
        "-31, 63",
        "0, 127",
        "31, 189",
        "63, 251",
        "94, 312",
        "125, 372",
        "156, 430",
        "187, 486",
        "218, 541",
        "249, 593",
        "279, 643",
        "309, 690",
        "339, 735",
        "368, 776",
        "397, 815",
        "426, 850",
        "454, 881",
        "482, 910",
        "509, 934",
        "536, 955",
        "562, 972",
        "588, 985",
        "613, 994",
        "637, 999",
        "661, 1_000",
        "685, 997",
        "707, 990",
        "729, 979",
        "750, 964",
        "771, 945",
        "790, 922",
        "809, 896",
        "827, 866",
        "844, 833",
        "861, 796",
        "876, 756",
        "891, 713",
        "905, 667",
        "918, 618",
        "930, 567",
        "941, 514",
        "951, 458",
        "960, 401",
        "969, 342",
        "976, 282",
        "982, 220",
        "988, 158",
        "992, 95",
        "996, 32",
        "998, -32",
        "1_000, -95",
        "1_000, -158",
        "1_000, -220",
        "998, -282",
        "996, -342",
        "992, -401",
        "988, -458",
        "982, -514",
        "976, -567",
        "969, -618",
        "960, -667",
        "951, -713",
        "941, -756",
        "930, -796",
        "918, -833",
        "905, -866",
        "891, -896",
        "876, -922",
        "861, -945",
        "844, -964",
        "827, -979",
        "809, -990",
        "790, -997",
        "771, -1_000",
        "750, -999",
        "729, -994",
        "707, -985",
        "685, -972",
        "661, -955",
        "637, -934",
        "613, -910",
        "588, -881",
        "562, -850",
        "536, -815",
        "509, -776",
        "482, -735",
        "454, -690",
        "426, -643",
        "397, -593",
        "368, -541",
        "339, -486",
        "309, -430",
        "279, -372",
        "249, -312",
        "218, -251",
        "187, -189",
        "156, -127",
        "125, -63",
        "94, 0",
        "63, 63",
        "31, 127",
        "0, 189",
        "-31, 251",
        "-63, 312",
        "-94, 372",
        "-125, 430",
        "-156, 486",
        "-187, 541",
        "-218, 593",
        "-249, 643",
        "-279, 690",
        "-309, 735",
        "-339, 776",
        "-368, 815",
        "-397, 850",
        "-426, 881",
        "-454, 910",
        "-482, 934",
        "-509, 955",
        "-536, 972",
        "-562, 985",
        "-588, 994",
        "-613, 999",
        "-637, 1_000",
        "-661, 997",
        "-685, 990",
        "-707, 979",
        "-729, 964",
        "-750, 945",
        "-771, 922",
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
                    DebugWindow window = new DebugScopeWindow(new CircularBuffer(128));
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
