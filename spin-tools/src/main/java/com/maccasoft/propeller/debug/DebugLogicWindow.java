/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.util.Arrays;
import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

public class DebugLogicWindow extends DebugWindow {

    public static final int VERTICAL_SPACING = 6;

    int[] sampleData;

    int spacing;
    int lineSize;
    int textSize;

    Color backColor;
    Color gridColor;

    PackMode packMode;

    int defaultColorIndex;
    Channel channelData[];

    int triggerMask;
    int triggerMatch;
    int triggerOffset;
    boolean armed;
    boolean triggered;

    int rate;
    int rateCount;

    int holdOff;
    int holdOffCount;

    int sampleCount;
    int sampleIndex;

    Font font;
    int textWidth;
    int charHeight;

    class Channel {
        int mask;
        String name;
        Color color;

        int[] linePoints;

        Channel(int mask, String name, Color color) {
            this.mask = mask;
            this.name = name;
            this.color = color;
            this.linePoints = new int[0];
        }

        void update(int lineY) {
            int y;
            int x = MARGIN_WIDTH * 2 + textWidth + (sampleData.length - sampleCount) * spacing;
            int ptr = sampleIndex - sampleCount;
            if (ptr < 0) {
                ptr += sampleData.length;
            }
            int idx = 0;

            int arraySize = sampleCount * 4;
            if (linePoints.length != arraySize) {
                linePoints = new int[arraySize];
            }

            for (int i = 0; i < sampleCount; i++) {
                y = (sampleData[ptr] & mask) == 0 ? lineY + charHeight : lineY;
                linePoints[idx++] = x;
                linePoints[idx++] = y;
                x += spacing;
                linePoints[idx++] = x;
                linePoints[idx++] = y;
                ptr = (ptr + 1) % sampleData.length;
            }
        }

        void plot(GC gc) {
            gc.setLineStyle(SWT.LINE_SOLID);
            gc.setForeground(color);
            gc.setLineWidth(lineSize);
            gc.drawPolyline(linePoints);
        }

    }

    public DebugLogicWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        sampleData = new int[32];

        packMode = PackMode.NONE();

        spacing = 8;
        lineSize = 1;
        textSize = 0;

        backColor = new Color(0, 0, 0);
        gridColor = new Color(64, 64, 64);

        defaultColorIndex = 0;
        channelData = new Channel[0];

        triggerMask = 0;
        triggerMatch = 1;
        triggerOffset = sampleData.length / 2;
        armed = false;
        triggered = false;

        rate = 1;
        rateCount = 0;

        holdOff = sampleData.length;
        holdOffCount = holdOff;

        sampleCount = 0;
        sampleIndex = 0;
    }

    @Override
    public void setup(KeywordIterator iter) {
        String cmd;
        Color tempColor;

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

                    case "SAMPLES":
                        if (iter.hasNextNumber()) {
                            sampleData = new int[iter.nextNumber()];
                        }
                        break;

                    case "SPACING":
                        if (iter.hasNextNumber()) {
                            spacing = iter.nextNumber();
                        }
                        break;

                    case "RATE":
                        if (iter.hasNextNumber()) {
                            rate = iter.nextNumber();
                            rateCount = 0;
                        }
                        break;

                    case "LINESIZE":
                        if (iter.hasNextNumber()) {
                            lineSize = iter.nextNumber();
                        }
                        break;

                    case "TEXTSIZE":
                        if (iter.hasNextNumber()) {
                            textSize = iter.nextNumber();
                        }
                        break;

                    case "COLOR":
                        tempColor = color(iter);
                        if (tempColor != null) {
                            backColor.dispose();
                            backColor = tempColor;
                        }
                        tempColor = color(iter);
                        if (tempColor != null) {
                            gridColor.dispose();
                            gridColor = tempColor;
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

                    case "HIDEXY": // TODO
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

        triggerMask = 0;
        triggerMatch = 1;
        triggerOffset = sampleData.length / 2;

        holdOff = sampleData.length;
        holdOffCount = holdOff;

        GC gc = new GC(canvas);
        try {
            gc.setFont(font);
            charHeight = gc.stringExtent("X").y;

            textWidth = 0;
            for (Channel ch : channelData) {
                Point extent = gc.stringExtent(ch.name);
                if (extent.x > textWidth) {
                    textWidth = extent.x;
                }
            }
        } finally {
            gc.dispose();
        }

        imageSize.x = sampleData.length * spacing;
        imageSize.y = (charHeight * channelData.length) + (VERTICAL_SPACING * (channelData.length - 1));

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                pendingRedraw.set(false);
                paint(e.gc);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                font.dispose();
            }

        });

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = MARGIN_WIDTH * 2 + textWidth + imageSize.x + MARGIN_WIDTH;
        gridData.heightHint = MARGIN_HEIGHT + imageSize.y + MARGIN_HEIGHT;
    }

    void channel(String name, KeywordIterator iter) {
        Color color = defaultColors[defaultColorIndex++];
        if (defaultColorIndex >= defaultColors.length) {
            defaultColorIndex = 0;
        }

        int bitCount = iter.hasNextNumber() ? iter.nextNumber() : 1;

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

        if (bitCount == 1) {
            add(new Channel(1 << channelData.length, name, color));
        }
        else {
            int i = 0;
            add(new Channel(1 << channelData.length, String.format("%s %d", name, i), color));
            i++;
            while (i < bitCount) {
                add(new Channel(1 << channelData.length, String.format("%d", i), color));
                i++;
            }
        }
    }

    void add(Channel channel) {
        int i = 0;
        Channel[] newArray = new Channel[channelData.length + 1];
        while (i < channelData.length) {
            newArray[i] = channelData[i++];
        }
        newArray[i] = channel;
        channelData = newArray;
    }

    Color color(KeywordIterator iter) {
        if (iter.hasNextNumber()) {
            return translateColor(iter.nextNumber(), ColorMode.RGB24);
        }
        else if (iter.hasNext()) {
            int h = 0;
            int p = 8;

            String s = iter.next().toUpperCase();
            switch (s) {
                case "BLACK":
                    return new Color(0, 0, 0);
                case "WHITE":
                    return new Color(255, 255, 255);
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
                    return translateColor((h << 5) | (p << 1), ColorMode.RGBI8X);
                default:
                    iter.back();
                    break;
            }
        }
        return null;
    }

    Color translateColor(int p, ColorMode mode) {
        int color = mode.translateColor(p, 6);
        return new Color(display, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
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
        gc.drawRectangle(MARGIN_WIDTH * 2 + textWidth - 1, MARGIN_HEIGHT - 1, imageSize.x + 1, imageSize.y + 1);

        int y = MARGIN_HEIGHT + charHeight;

        gc.setFont(font);
        for (int i = channelData.length - 1; i >= 0; i--) {
            Point extent = gc.stringExtent(channelData[i].name);
            gc.setForeground(channelData[i].color);
            gc.drawString(channelData[i].name, MARGIN_WIDTH + textWidth - extent.x, y - extent.y - 1, true);
            y += charHeight + VERTICAL_SPACING;
        }

        gc.setLineStyle(SWT.LINE_DOT);

        if (triggerOffset != 0) {
            int x = (sampleData.length * spacing / 2) - spacing / 2;
            gc.setForeground(new Color(64, 64, 64));
            gc.drawLine(x, 0, x, imageSize.y);
        }

        y = MARGIN_HEIGHT;
        for (int i = channelData.length - 1; i >= 0; i--) {
            gc.setLineStyle(SWT.LINE_DOT);
            gc.setForeground(gridColor);
            gc.drawLine(MARGIN_WIDTH + textWidth, y, MARGIN_WIDTH + textWidth + imageSize.x, y);

            channelData[i].plot(gc);

            y += charHeight + VERTICAL_SPACING - 1;
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
                    case "TRIGGER":
                        armed = false;
                        triggerMask = 0;
                        triggerMatch = 1;
                        triggerOffset = sampleData.length / 2;
                        if (iter.hasNextNumber()) {
                            triggerMask = iter.nextNumber();
                        }
                        if (iter.hasNextNumber()) {
                            triggerMatch = iter.nextNumber();
                        }
                        if (iter.hasNextNumber()) {
                            triggerOffset = iter.nextNumber();
                        }
                        break;

                    case "HOLDOFF":
                        holdOff = sampleData.length;
                        if (iter.hasNextNumber()) {
                            holdOff = iter.nextNumber();
                        }
                        holdOffCount = holdOff;
                        break;

                    case "CLEAR":
                        Arrays.fill(sampleData, 0);
                        triggered = false;
                        sampleCount = 0;
                        update();
                        break;

                    case "SAVE":
                        save(iter);
                        break;

                    case "CLOSE":
                        close();
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
        sampleData[sampleIndex] = sample;

        if (sampleCount < sampleData.length) {
            sampleCount++;
        }
        sampleIndex = (sampleIndex + 1) % sampleData.length;

        triggered = false;

        if (triggerMask != 0) {
            if (sampleCount >= sampleData.length) {
                int offs = sampleIndex - triggerOffset;
                if (offs < 0) {
                    offs += sampleData.length;
                }
                int triggerSample = sampleData[offs];
                if (armed) {
                    if (((triggerSample ^ triggerMatch) & triggerMask) == 0) {
                        triggered = true;
                        armed = false;
                    }
                }
                else {
                    if (((triggerSample ^ triggerMatch) & triggerMask) != 0) {
                        armed = true;
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
    }

    @Override
    protected void update() {
        int y = MARGIN_HEIGHT;
        for (int i = channelData.length - 1; i >= 0; i--) {
            channelData[i].update(y);
            y += charHeight + VERTICAL_SPACING - 1;
        }
        super.update();
    }

    static String[] data = new String[] {
        "samples 32 spacing 12  'TX' 'IN' longs_2bit",
        "trigger %10 %10 22",
        "$AAAF_FD55 $FFFF_FFEA",
        "$AAEF_FD55 $FFFF_FFEA",
        "$ABAF_FD55 $FFFF_FFEA",
        "$ABEF_FD55 $FFFF_FFEA",
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
                    DebugWindow window = new DebugLogicWindow(new CircularBuffer(128));
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
                    return;
                }
            }
        });

        display.dispose();
    }

}
