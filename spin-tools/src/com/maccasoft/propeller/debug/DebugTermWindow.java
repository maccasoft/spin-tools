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

import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.internal.CircularBuffer;

public class DebugTermWindow extends DebugWindow {

    int x;
    int y;

    int columns;
    int rows;
    Color backColor;

    int textSize;
    int textColor;
    Color[] textForeground;
    Color[] textBackground;

    Font textFont;
    int fontWidth;
    int fontHeight;

    boolean autoUpdate;

    Image image;
    Color currentTextColor;
    Color currentTextBackground;

    int prevCh = 0;

    public DebugTermWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        x = y = 0;
        origin = new Point(0, 0);

        columns = 40;
        rows = 20;
        backColor = BLACK;

        textSize = 8;
        textColor = 0;
        textForeground = new Color[] {
            ORANGE,
            BLACK,
            LIME,
            BLACK
        };
        textBackground = new Color[] {
            BLACK,
            ORANGE,
            BLACK,
            LIME
        };

        autoUpdate = true;
    }

    @Override
    public void setup(KeywordIterator iter) {
        int i;
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
                        columns = iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            rows = iter.nextNumber();
                        }
                    }
                    break;

                case "TEXTSIZE":
                    if (iter.hasNextNumber()) {
                        textSize = iter.nextNumber();
                    }
                    break;

                case "COLOR":
                    i = 0;
                    while (i < 4) {
                        if ((tempColor = color(iter)) == null) {
                            break;
                        }
                        textForeground[i] = tempColor;
                        if ((tempColor = color(iter)) == null) {
                            break;
                        }
                        textBackground[i] = tempColor;
                        i++;
                    }
                    break;

                case "BACKCOLOR":
                    tempColor = color(iter);
                    if (tempColor != null) {
                        backColor = tempColor;
                    }
                    break;

                case "UPDATE":
                    autoUpdate = false;
                    break;

                case "HIDEXY":
                    break;
            }
        }

        Font defaultFont = JFaceResources.getTextFont();
        FontData fontData = defaultFont.getFontData()[0];

        textFont = new Font(display, fontData.getName(), textSize, 0);

        GC gc = new GC(canvas);
        try {
            gc.setFont(textFont);
            FontMetrics fontMetrics = gc.getFontMetrics();
            fontWidth = (int) fontMetrics.getAverageCharacterWidth();
            fontHeight = fontMetrics.getHeight();
        } finally {
            gc.dispose();
        }

        imageSize = new Point(columns * fontWidth, rows * fontHeight);

        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));

        currentTextColor = textForeground[textColor];
        currentTextBackground = textBackground[textColor];

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                paint(e.gc);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                image.dispose();

                textFont.dispose();
            }

        });

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = imageSize.x;
        gridData.heightHint = imageSize.y;

        shell.pack();
        shell.redraw();
    }

    @Override
    protected void paint(GC gc) {
        Point canvasSize = canvas.getSize();

        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.OFF);

        gc.drawImage(image, 0, 0, imageSize.x, imageSize.y, 0, 0, canvasSize.x, canvasSize.y);
    }

    @Override
    public void update(KeywordIterator iter) {
        int i;
        String cmd;
        Color tempColor;

        GC gc = new GC(image);
        try {
            gc.setInterpolation(SWT.NONE);
            gc.setTextAntialias(SWT.ON);

            gc.setFont(textFont);
            gc.setForeground(currentTextColor);
            gc.setBackground(currentTextBackground);

            while (iter.hasNext()) {
                tempColor = color(iter);
                if (tempColor != null) {
                    gc.setForeground(currentTextColor = tempColor);

                    tempColor = color(iter);
                    if (tempColor != null) {
                        gc.setBackground(currentTextBackground = tempColor);
                    }

                    if (!iter.hasNext()) {
                        break;
                    }
                }

                cmd = iter.next();

                if (isString(cmd)) {
                    String s = stringStrip(cmd);
                    for (i = 0; i < s.length(); i++) {
                        drawChar(gc, s.charAt(i));
                    }
                }
                else if (isNumber(cmd)) {
                    int c = stringToNumber(cmd);
                    switch (c) {
                        case 0:
                            gc.setBackground(backColor);
                            gc.fillRectangle(0, 0, imageSize.x, imageSize.y);
                            gc.setBackground(textBackground[textColor]);
                            x = y = 0;
                            break;

                        case 1:
                            x = y = 0;
                            break;

                        case 2:
                            if (iter.hasNextNumber()) {
                                x = iter.nextNumber();
                            }
                            break;
                        case 3:
                            if (iter.hasNextNumber()) {
                                y = iter.nextNumber();
                            }
                            break;

                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            textColor = c - 4;
                            gc.setForeground(currentTextColor = textForeground[textColor]);
                            gc.setBackground(currentTextBackground = textBackground[textColor]);
                            break;

                        case 8:
                            if (x > 0) {
                                x--;
                            }
                            break;

                        case 9:
                            x = (x + 8) & ~3;
                            if (x >= columns) {
                                x = 0;
                                y++;
                                if (y >= rows) {
                                    y = 0;
                                }
                            }
                            break;

                        case 10:
                        case 13:
                            x = 0;
                            y++;
                            if (y >= rows) {
                                y = 0;
                            }
                            prevCh = c;
                            break;

                        default:
                            if (c >= ' ' && c <= 255) {
                                drawChar(gc, c);
                            }
                            break;
                    }
                }
                else {
                    switch (cmd.toUpperCase()) {
                        case "COLOR":
                            i = 0;
                            while (i < 4) {
                                if ((tempColor = color(iter)) == null) {
                                    break;
                                }
                                textForeground[i] = tempColor;
                                if ((tempColor = color(iter)) == null) {
                                    break;
                                }
                                textBackground[i] = tempColor;
                                i++;
                            }
                            break;

                        case "BACKCOLOR":
                            tempColor = color(iter);
                            if (tempColor != null) {
                                gc.setBackground(currentTextBackground = tempColor);
                            }
                            break;

                        case "SCROLL": {
                            int x1 = 0, y1 = 0, x2 = columns - 1, y2 = rows - 1, direction = 0;
                            int units = 1;
                            Color color = backColor;

                            if (iter.hasNextNumber()) {
                                x1 = iter.nextNumber();
                            }
                            if (iter.hasNextNumber()) {
                                y1 = iter.nextNumber();
                            }
                            if (iter.hasNextNumber()) {
                                x2 = iter.nextNumber();
                            }
                            if (iter.hasNextNumber()) {
                                y2 = iter.nextNumber();
                            }
                            if (iter.hasNext()) {
                                String s = iter.next();
                                if (isNumber(s)) {
                                    direction = stringToNumber(s);
                                }
                                else {
                                    switch (s.toUpperCase()) {
                                        case "UP":
                                            break;
                                        case "DOWN":
                                            direction = 1;
                                            break;
                                        case "LEFT":
                                            direction = 2;
                                            break;
                                        case "RIGHT":
                                            direction = 3;
                                        default:
                                            iter.back();
                                            break;
                                    }
                                }
                            }
                            if (iter.hasNextNumber()) {
                                units = iter.nextNumber();
                            }
                            if ((tempColor = color(iter)) != null) {
                                color = tempColor;
                            }

                            try {
                                for (i = 0; i < units; i++) {
                                    scroll(gc, x1, y1, (x2 - x1) + 1, (y2 - y1) + 1, direction, color);
                                }
                            } catch (Exception e) {
                                // Do nothing
                            }
                            break;
                        }

                        case "CLEAR":
                            gc.setBackground(backColor);
                            gc.fillRectangle(0, 0, imageSize.x, imageSize.y);
                            gc.setBackground(currentTextBackground);
                            x = y = 0;
                            break;

                        case "UPDATE":
                            update();
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
        } finally {
            gc.dispose();
        }

        if (autoUpdate) {
            update();
        }
    }

    void drawChar(GC gc, int c) {
        gc.drawText(String.valueOf((char) c), x * fontWidth, y * fontHeight, false);

        x++;
        if (x >= columns) {
            x = 0;
            y++;
            if (y >= rows) {
                y = 0;
            }
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

    void update() {
        canvas.redraw();
    }

    void scroll(GC gc, int x, int y, int width, int height, int direction, Color backColor) {
        Image img = null;

        gc.setBackground(backColor);
        try {
            switch (direction) {
                case 0: // Up
                    img = new Image(display, width * fontWidth, (height - 1) * fontHeight);
                    gc.copyArea(img, x * fontWidth, (y + 1) * fontHeight);
                    gc.drawImage(img, x * fontWidth, y * fontHeight);
                    gc.fillRectangle(x * fontWidth, (y + height - 1) * fontHeight, width * fontWidth, 1 * fontHeight);
                    break;
                case 1: // Down
                    img = new Image(display, width * fontWidth, (height - 1) * fontHeight);
                    gc.copyArea(img, x * fontWidth, y * fontHeight);
                    gc.drawImage(img, x * fontWidth, (y + 1) * fontHeight);
                    gc.fillRectangle(x * fontWidth, y * fontHeight, width * fontWidth, 1 * fontHeight);
                    break;
                case 2: // Left
                    img = new Image(display, (width - 1) * fontWidth, height * fontHeight);
                    gc.copyArea(img, (x + 1) * fontWidth, y * fontHeight);
                    gc.drawImage(img, x * fontWidth, y * fontHeight);
                    gc.fillRectangle((x + width - 1) * fontWidth, y * fontHeight, 1 * fontWidth, height * fontHeight);
                    break;
                case 3: // Right
                    img = new Image(display, (width - 1) * fontWidth, height * fontHeight);
                    gc.copyArea(img, x * fontWidth, y * fontHeight);
                    gc.drawImage(img, (x + 1) * fontWidth, y * fontHeight);
                    gc.fillRectangle(x * fontWidth, y * fontHeight, 1 * fontWidth, height * fontHeight);
                    break;
            }
        } finally {
            if (img != null) {
                img.dispose();
            }
        }
    }

    static String[] data = new String[] {
        "title '8086' size 80 25 color cyan black green black black green white red backcolor black textsize 12 pos 10 10 hidexy",
        "'AX: $0000'",
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
                    DebugWindow window = new DebugTermWindow(new CircularBuffer(128));
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
