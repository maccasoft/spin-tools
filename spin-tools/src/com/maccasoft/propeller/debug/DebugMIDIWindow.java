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

import java.util.Arrays;
import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.internal.CircularBuffer;

public class DebugMIDIWindow extends DebugWindow {

    static final int BASE_SIZE = 6;

    int size;
    int keyFirst;
    int keyLast;
    int channel;
    Color whiteColor;
    Color blackColor;

    int state;
    int note;

    int keySize;
    int border;
    int offset;

    int fontHeight;

    boolean[] black = new boolean[128];
    int[] left = new int[128];
    int[] right = new int[128];
    int[] bottom = new int[128];
    int[] numX = new int[128];
    int[] velocity = new int[128];

    Image image;

    public DebugMIDIWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        size = 4;

        keyFirst = 21;
        keyLast = 108;
        channel = 0;

        whiteColor = CYAN;
        blackColor = MAGENTA;

        state = 0;
    }

    @Override
    public void setup(KeywordIterator iter) {
        String cmd;
        Color tempColor;

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
                    if (iter.hasNextNumber()) {
                        size = iter.nextNumber();
                    }
                    break;

                case "RANGE":
                    if (iter.hasNextNumber()) {
                        keyFirst = keyLast = iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            keyLast = iter.nextNumber();
                        }
                    }
                    break;

                case "CHANNEL":
                    if (iter.hasNextNumber()) {
                        channel = iter.nextNumber();
                    }
                    break;

                case "COLOR":
                    tempColor = color(iter);
                    if (tempColor != null) {
                        whiteColor = tempColor;
                        tempColor = color(iter);
                        if (tempColor != null) {
                            blackColor = tempColor;
                        }
                    }
                    break;
            }
        }

        GC gc = new GC(canvas);
        try {
            FontMetrics fontMetrics = gc.getFontMetrics();
            fontHeight = fontMetrics.getHeight();
            int fontWidth = (int) fontMetrics.getAverageCharacterWidth();
            keySize = BASE_SIZE + size * fontWidth;
            border = keySize / ((BASE_SIZE + fontWidth) / 2);
        } finally {
            gc.dispose();
        }

        int whiteKeys = 0;
        int x = border;
        for (int i = 0, note = 0; i < 127; i++) {
            int tweak = switch (note) {
                case 0 -> 10;
                case 1 -> -2;
                case 2 -> 16;
                case 3 -> 2;
                case 4 -> 22;
                case 5 -> 9;
                case 6 -> -4;
                case 7 -> 14;
                case 8 -> 0;
                case 9 -> 18;
                case 10 -> 4;
                case 11 -> 23;
                default -> 0;
            };
            boolean bl = note == 1 || note == 3 || note == 6 || note == 8 || note == 10;
            if (bl) {
                left[i] = x - (keySize * (10 - tweak) + 16) / 32;
                right[i] = left[i] + (keySize * 20) / 32;
                bottom[i] = keySize * 4;
                numX[i] = (left[i] + right[i] + 1) / 2;
            }
            else {
                left[i] = x;
                right[i] = left[i] + keySize;
                bottom[i] = keySize * 6;
                numX[i] = x + (keySize * tweak + 16) / 32;
                x += keySize;
            }
            black[i] = bl;
            if (note++ >= 11) {
                note = 0;
            }
            if (!bl && i >= keyFirst && i <= keyLast) {
                whiteKeys++;
            }
        }
        if (black[keyFirst]) {
            offset = left[keyFirst - 1] - border;
            whiteKeys++;
        }
        else {
            offset = left[keyFirst] - border;
        }
        if (black[keyLast]) {
            whiteKeys++;
        }

        imageSize.x = border + keySize * whiteKeys + border;
        imageSize.y = border + keySize * 6 + border;
        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));

        draw();

        canvas.addPaintListener(e -> paint(e.gc));

        canvas.addDisposeListener(e -> image.dispose());

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = imageSize.x;
        gridData.heightHint = imageSize.y;
    }

    @Override
    protected void paint(GC gc) {
        try {
            gc.setAdvanced(true);
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.OFF);
            gc.drawImage(image, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Color color(KeywordIterator iter) {
        if (iter.hasNext()) {
            int h = 0;
            int p = 8;

            String s = iter.next().toUpperCase();
            switch (s) {
                case "BLACK":
                    return new Color(0, 0, 0);
                case "WHITE":
                    return new Color(255, 255, 255);
                case "GREY":
                    s = "GRAY";
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
            }

            iter.back();
        }
        return null;
    }

    Color translateColor(int p, ColorMode mode) {
        int color = mode.translateColor(p, 0);
        return new Color(display, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
    }

    @Override
    public void update(KeywordIterator iter) {
        int val;
        String cmd;

        while (iter.hasNext()) {
            cmd = iter.next();

            if (isNumber(cmd)) {
                try {
                    val = stringToNumber(cmd) & 0xFF;
                    if ((val & 0x80) != 0) {
                        state = 0;
                    }
                    switch (state) {
                        case 0:
                            if (((val & 0xF0) == 0x90) && ((val & 0x0F) == channel)) {
                                state = 1;
                            }
                            if (((val & 0xF0) == 0x80) && ((val & 0x0F) == channel)) {
                                state = 3;
                            }
                            break;
                        case 1:
                            note = val;
                            state = 2;
                            break;
                        case 2:
                            velocity[note] = val;
                            draw();
                            canvas.redraw();
                            state = 1;
                            break;
                        case 3:
                            note = val;
                            state = 4;
                            break;
                        case 4:
                            velocity[note] = val;
                            draw();
                            canvas.redraw();
                            state = 3;
                            break;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            else {
                switch (cmd.toUpperCase()) {
                    case "CLEAR":
                        Arrays.fill(velocity, 0);
                        draw();
                        canvas.redraw();
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

    void draw() {
        GC gc = new GC(image);
        Transform tr = new Transform(Display.getCurrent());
        try {
            gc.setInterpolation(SWT.NONE);
            gc.setTextAntialias(SWT.ON);

            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            gc.fillRectangle(0, 0, imageSize.x, imageSize.y);

            for (int i = keyFirst; i <= keyLast; i++) {
                if (!black[i]) {
                    drawKey(gc, tr, i, WHITE, whiteColor);
                }
            }

            for (int i = keyFirst; i <= keyLast; i++) {
                if (black[i]) {
                    drawKey(gc, tr, i, BLACK, blackColor);
                }
            }
        } finally {
            tr.dispose();
            gc.dispose();
        }
    }

    void drawKey(GC gc, Transform tr, int i, Color keyColor, Color velocityColor) {
        gc.setTransform(null);

        gc.setForeground(BLACK);
        gc.setBackground(keyColor);
        gc.fillRectangle(left[i] - offset, -1, right[i] - left[i] - 1, bottom[i] + 1);
        gc.drawRectangle(left[i] - offset, -1, right[i] - left[i] - 1, bottom[i] + 1);

        if (velocity[i] > 0) {
            int h = (bottom[i] - border) * velocity[i] / 127;
            gc.setBackground(velocityColor);
            gc.fillRectangle(left[i] - offset + 1, bottom[i] - h, right[i] - left[i] - 2, h);
        }

        tr.identity();
        tr.translate(numX[i] - offset - fontHeight / 2.0f, border + fontHeight);
        tr.rotate(-90);
        gc.setTransform(tr);

        gc.setForeground(GRAY3);
        gc.drawString(String.valueOf(i), 0, 0, true);
    }

    static String[] data = new String[] {
        "size 3 range 36 84",
        "$90 36 63",
        "$90 37 63",
        "$90 52 31",
        "$90 53 31",
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
                    DebugWindow window = new DebugMIDIWindow(new CircularBuffer(128));
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
