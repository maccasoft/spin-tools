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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.Preferences;

public class DebugLogicWindow extends DebugWindow {

    Image image;
    GC imageGc;

    int[] sampleData;
    int sampleDataIndex;

    int spacing;
    int rate;
    int lineSize;

    Color backColor;
    Color gridColor;

    Pack packMode;
    boolean altPack;

    Channel channelData[];

    int triggerMask;
    int triggerMatch;
    int triggerOffset;
    boolean armed;
    boolean triggered;

    int holdOff;
    int holdOffCount;

    int sampleCount;

    Font font;
    int charHeight;

    class Channel {
        int mask;
        String name;
        int bitCount;
        Color color;

        Channel(int mask, String name, int bitCount, Color color) {
            this.mask = mask;
            this.name = name;
            this.bitCount = bitCount;
            this.color = color;
        }

        public void plot(GC gc, int lineY) {
            int y;
            int x = 0;
            int index = (sampleDataIndex + triggerOffset + sampleData.length / 2) % sampleData.length;

            gc.setForeground(color);
            gc.drawText(name, 0, lineY, true);

            gc.setLineWidth(lineSize);

            y = lineY;
            if ((sampleData[index] & mask) == 0) {
                y += charHeight;
            }
            gc.drawLine(x, y, x + spacing, y);
            x += spacing;

            for (int i = 1; i < sampleData.length; i++) {
                index = (index + 1) % sampleData.length;

                int newY = lineY;
                if ((sampleData[index] & mask) == 0) {
                    newY += charHeight;
                }
                gc.drawLine(x, y, x, newY);
                gc.drawLine(x, newY, x + spacing, newY);

                x += spacing;
                y = newY;
            }
        }

    }

    public DebugLogicWindow() {
        sampleData = new int[32];
        sampleDataIndex = 0;

        spacing = 8;
        rate = 1;
        lineSize = 1;

        backColor = BLACK;
        gridColor = GRAY3;

        channelData = new Channel[0];

        triggerMask = 0;
        triggerMatch = 1;
        triggerOffset = sampleData.length / 2;
        armed = false;
        triggered = false;

        holdOff = 0;
        holdOffCount = holdOff;

        sampleCount = 0;
    }

    @Override
    protected void createContents(Composite parent) {
        super.createContents(parent);

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (Preferences.getInstance().getEditorFont() != null) {
            fontData = StringConverter.asFontData(Preferences.getInstance().getEditorFont());
        }
        fontData.setStyle(SWT.NONE);
        font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);

        resize();

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
                font.dispose();
            }

        });
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
                            triggerOffset = sampleData.length / 2;
                            resize();
                        }
                        break;

                    case "SPACING":
                        if (iter.hasNextNumber()) {
                            spacing = iter.nextNumber();
                            resize();
                        }
                        break;

                    case "RATE":
                        if (iter.hasNextNumber()) {
                            iter.nextNumber();
                        }
                        break;

                    case "LINESIZE":
                        if (iter.hasNextNumber()) {
                            lineSize = iter.nextNumber();
                        }
                        break;

                    case "TEXTSIZE":
                        if (iter.hasNextNumber()) {
                            iter.nextNumber();
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
                        packedMode(cmd, iter);
                        break;

                    case "HIDEXY":
                        break;
                }
            }
        }
    }

    void resize() {
        if (imageGc != null) {
            imageGc.dispose();
        }
        if (image != null) {
            image.dispose();
        }

        imageSize.x = sampleData.length * spacing;
        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));

        imageGc = new GC(image);
        imageGc.setAdvanced(true);
        imageGc.setAntialias(SWT.OFF);
        imageGc.setTextAntialias(SWT.ON);
        imageGc.setInterpolation(SWT.NONE);
        imageGc.setLineCap(SWT.CAP_SQUARE);
        imageGc.setFont(font);
        imageGc.setLineDash(new int[] {
            3, 3
        });

        charHeight = imageGc.stringExtent("X").y;

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = imageSize.x;
        gridData.heightHint = imageSize.y;
    }

    void channel(String name, KeywordIterator iter) {
        Color color = defaultColors[channelData.length % defaultColors.length];

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
        newArray[i] = new Channel(1 << i, name, bitCount, color);
        channelData = newArray;

        imageSize.y = 5 + (charHeight + 5) * channelData.length;
        resize();

        shell.pack();
        shell.redraw();
    }

    void packedMode(String cmd, KeywordIterator iter) {
        packMode = Pack.valueOf(cmd.toUpperCase());
        altPack = false;
        if (iter.hasNext() && "ALT".equalsIgnoreCase(iter.peekNext())) {
            altPack = true;
            iter.next();
        }
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
                case "GREY":
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
    public void update(KeywordIterator iter) {
        int pixel;
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
                            processSample(pixel & packMode.mask);
                            pixel >>= packMode.shift;
                        }
                    }
                    else {
                        processSample(pixel);
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
                        break;

                    case "CLEAR":
                        triggered = false;
                        imageGc.setForeground(backColor);
                        imageGc.setBackground(backColor);
                        imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);
                        imageGc.drawRectangle(0, 0, imageSize.x, imageSize.y);
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
        sampleData[sampleDataIndex++] = sample;
        if (sampleDataIndex >= sampleData.length) {
            sampleDataIndex = 0;
        }

        if (sampleCount < sampleData.length) {
            sampleCount++;
        }

        if (triggerMask != 0) {
            triggered = false;
            if (sampleCount >= sampleData.length) {
                sample = sampleData[(sampleDataIndex + triggerOffset - 1) % sampleData.length];
                if (armed) {
                    if (((sample ^ triggerMatch) & triggerMask) == 0) {
                        triggered = true;
                        armed = false;
                    }
                }
                else {
                    if (((sample ^ triggerMatch) & triggerMask) != 0) {
                        armed = true;
                    }
                }
                if (triggered) {
                    if (holdOffCount > 0) {
                        holdOffCount--;
                    }
                    if (holdOffCount == 0) {
                        update();
                        holdOffCount = holdOff;
                    }
                }
            }
        }
        else if (sampleCount >= sampleData.length) {
            update();
        }
    }

    void update() {
        int y;

        imageGc.setBackground(backColor);
        imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);

        int x = (sampleData.length * spacing / 2) - spacing / 2;
        if (x >= 0) {
            imageGc.setForeground(new Color(64, 64, 64));
            imageGc.setLineStyle(SWT.LINE_CUSTOM);
            imageGc.drawLine(x, 0, x, imageSize.y);
        }

        imageGc.setLineStyle(SWT.LINE_SOLID);

        y = imageSize.y - charHeight - 5;
        for (int i = 0; i < channelData.length; i++) {
            channelData[i].plot(imageGc, y);
            y -= charHeight + 5;
        }

        canvas.redraw();
    }

    static String[] data = new String[] {
        "samples 32 spacing 12  'TX' 'IN' longs_2bit",
        "trigger %10 %10", //"trigger %10 %10 22",
        "$AABF_FD55 $FFFF_FFAA",
        "$ABBF_FD55 $FFFF_FFAA",
        //"%10_10_10_10_10_11_11_11_11_11_11_01_01_01_01_01 %11_11_11_11_11_11_11_11_11_11_11_11_10_10_10_10",
        //"%10_10_10_11_10_11_11_11_11_11_11_01_01_01_01_01" // %11_11_11_11_11_11_11_11_11_11_11_11_10_10_10_10",
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
                    DebugWindow window = new DebugLogicWindow();
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
