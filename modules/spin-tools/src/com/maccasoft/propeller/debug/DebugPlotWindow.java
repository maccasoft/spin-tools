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
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.debug.DebugBitmapWindow.RGBColor;

public class DebugPlotWindow extends DebugWindow {

    Image image;
    GC imageGc;

    int x;
    int y;
    Point origin;

    Color color;
    Color textColor;
    Color backColor;
    int opacity;

    ColorMode colorMode;
    int colorTune;
    Color[] lutColors;

    int textSize;
    int textStyle;
    int textAngle;

    boolean polar;
    long twoPi;
    int theta;

    boolean autoUpdate;

    public DebugPlotWindow() {
        x = y = 0;
        origin = new Point(0, 0);

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new Color[256];

        textSize = 10;
        textStyle = 0b00000001;
        textAngle = 0;

        polar = false;
        twoPi = 0x100000000L;
        theta = 0;

        autoUpdate = true;
    }

    @Override
    protected void createContents(Composite parent) {
        super.createContents(parent);

        color = new Color(255, 255, 255);
        textColor = new Color(255, 255, 255);
        backColor = new Color(0, 0, 0);
        opacity = 255;

        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
        imageGc = new GC(image);

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                e.gc.setAdvanced(true);
                e.gc.setAntialias(SWT.ON);
                e.gc.setInterpolation(SWT.OFF);
                Point canvasSize = canvas.getSize();
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
                    size(iter);
                    break;

                case "DOTSIZE":
                    dotsize(iter);
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

                case "BACKCOLOR":
                    tempColor = color(iter);
                    if (tempColor != null) {
                        backColor.dispose();
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
    }

    @Override
    protected void size(KeywordIterator iter) {
        super.size(iter);

        imageGc.dispose();
        image.dispose();
        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
        imageGc = new GC(image);
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

        for (int i = 0; i < 256 && iter.hasNext(); i++) {
            if (iter.hasNextNumber()) {
                lutColors[i] = translateColor(iter.nextNumber(), ColorMode.RGB24);
            }
            else {
                int h = 0;
                int p = 8;

                String s = iter.next().toUpperCase();
                switch (s) {
                    case "BLACK":
                        lutColors[i] = new Color(0, 0, 0);
                        break;
                    case "WHITE":
                        lutColors[i] = new Color(255, 255, 255);
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
                        lutColors[i] = translateColor((h << 5) | (p << 1), ColorMode.RGBI8X);
                        break;
                }
            }
        }
    }

    @Override
    public void update(KeywordIterator iter) {
        String cmd;
        Color tempColor;

        while (iter.hasNext()) {
            cmd = iter.next().toUpperCase();
            switch (cmd) {

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

                case "BACKCOLOR":
                    tempColor = color(iter);
                    if (tempColor != null) {
                        backColor.dispose();
                        backColor = tempColor;
                    }
                    break;

                case "BLACK":
                case "WHITE":
                case "ORANGE":
                case "BLUE":
                case "GREEN":
                case "CYAN":
                case "RED":
                case "MAGENTA":
                case "YELLOW":
                case "GREY":
                    iter.back();
                    // Fall-through
                case "COLOR":
                    tempColor = color(iter);
                    if (tempColor != null) {
                        if (iter.hasNext() && "TEXT".equalsIgnoreCase(iter.peekNext())) {
                            textColor.dispose();
                            textColor = tempColor;
                        }
                        else {
                            color.dispose();
                            color = tempColor;
                        }
                    }
                    break;

                case "OPACITY":
                    if (iter.hasNextNumber()) {
                        opacity = iter.nextNumber() & 255;
                    }
                    break;

                case "PRECISE":
                    break;

                case "LINESIZE":
                    if (iter.hasNextNumber()) {
                        iter.nextNumber();
                    }
                    break;

                case "ORIGIN":
                    if (iter.hasNextNumber()) {
                        int newX = iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            int newY = iter.nextNumber();
                            origin = new Point(newX, newY);
                        }
                    }
                    break;

                case "SET":
                    if (iter.hasNextNumber()) {
                        int newX = iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            int newY = iter.nextNumber();
                            if (polar) {
                                Point pt = polarToCartesian(newX, newY);
                                newX = pt.x;
                                newY = pt.y;
                            }
                            x = origin.x + newX;
                            y = imageSize.y - (origin.y + newY);
                        }
                    }
                    break;

                case "DOT":
                    if (iter.hasNextNumber()) { // line size
                        iter.nextNumber();
                    }
                    if (iter.hasNextNumber()) { // opacity
                        iter.nextNumber();
                    }
                    imageGc.setForeground(color);
                    imageGc.drawPoint(x, y);
                    if (autoUpdate) {
                        canvas.redraw();
                    }
                    break;

                case "LINE":
                    if (iter.hasNextNumber()) {
                        int newX = iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            int newY = iter.nextNumber();
                            if (polar) {
                                Point pt = polarToCartesian(newX, newY);
                                newX = pt.x;
                                newY = pt.y;
                            }
                            int dx = origin.x + newX;
                            int dy = imageSize.y - (origin.y + newY);
                            int size = iter.hasNextNumber() ? iter.nextNumber() : 1;
                            line(dx, dy, size, color);
                            if (autoUpdate) {
                                canvas.redraw();
                            }
                        }
                    }
                    break;

                case "CIRCLE":
                    if (iter.hasNextNumber()) {
                        int diameter = iter.nextNumber();
                        int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : 0;
                        int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() : opacity;
                        oval(diameter, diameter, sizeOverride, opacityOverride, color);
                        if (autoUpdate) {
                            canvas.redraw();
                        }
                    }
                    break;

                case "OVAL":
                    if (iter.hasNextNumber()) {
                        int width = iter.nextNumber();
                        if (iter.hasNext()) {
                            int height = iter.nextNumber();
                            int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : 0;
                            int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() : opacity;
                            oval(width, height, sizeOverride, opacityOverride, color);
                            if (autoUpdate) {
                                canvas.redraw();
                            }
                        }
                    }
                    break;

                case "BOX":
                    if (iter.hasNextNumber()) {
                        int width = iter.nextNumber();
                        if (iter.hasNext()) {
                            int height = iter.nextNumber();
                            int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : 0;
                            int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() : opacity;
                            box(width, height, sizeOverride, opacityOverride, color);
                            if (autoUpdate) {
                                canvas.redraw();
                            }
                        }
                    }
                    break;

                case "OBOX":
                    if (iter.hasNextNumber()) {
                        int width = iter.nextNumber();
                        if (iter.hasNext()) {
                            int height = iter.nextNumber();
                            if (iter.hasNext()) {
                                int radiusX = iter.nextNumber();
                                if (iter.hasNext()) {
                                    int radiusY = iter.nextNumber();
                                    int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : 0;
                                    int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() : opacity;
                                    obox(width, height, sizeOverride, radiusX, radiusY, opacityOverride, color);
                                    if (autoUpdate) {
                                        canvas.redraw();
                                    }
                                }
                            }
                        }
                    }
                    break;

                case "TEXTSIZE":
                    if (iter.hasNextNumber()) {
                        textSize = iter.nextNumber();
                    }
                    break;
                case "TEXTSTYLE":
                    if (iter.hasNextNumber()) {
                        textStyle = iter.nextNumber() & 0xFF;
                    }
                    break;
                case "TEXTANGLE":
                    if (iter.hasNextNumber()) {
                        textAngle = iter.nextNumber();
                    }
                    break;
                case "TEXT": {
                    int sizeOverride = textSize;
                    int styleOverride = textStyle;
                    if (iter.hasNextNumber()) {
                        sizeOverride = iter.nextNumber();
                    }
                    if (iter.hasNextNumber()) {
                        styleOverride = iter.nextNumber();
                    }
                    if (iter.hasNextNumber()) {
                        iter.nextNumber();
                    }
                    if (iter.hasNextString()) {
                        text(iter.nextString(), sizeOverride, styleOverride, textColor);
                        if (autoUpdate) {
                            canvas.redraw();
                        }
                    }
                    break;
                }

                case "SPRITEDEF":
                    if (iter.hasNextNumber()) {
                        int xDim = iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            int yDim = iter.nextNumber();
                            for (int y = 0; y < yDim; y++) {
                                for (int x = 0; x < xDim; x++) {
                                    iter.nextNumber();
                                }
                            }
                            for (int i = 0; i < 256; i++) {
                                iter.nextNumber();
                            }
                        }
                    }
                    break;
                case "SPRITE":
                    if (iter.hasNextNumber()) {
                        iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            iter.nextNumber();
                        }
                        if (iter.hasNextNumber()) {
                            iter.nextNumber();
                        }
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

                case "CARTESIAN":
                    polar = false;
                    if (iter.hasNextNumber()) {
                        iter.next();
                        if (iter.hasNextNumber()) {
                            iter.next();
                        }
                    }
                    break;

                case "CLEAR":
                    imageGc.setForeground(backColor);
                    imageGc.setBackground(backColor);
                    imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);
                    imageGc.drawRectangle(0, 0, imageSize.x, imageSize.y);
                    if (autoUpdate) {
                        canvas.redraw();
                    }
                    break;

                case "UPDATE":
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

    Color color(KeywordIterator iter) {
        if (iter.hasNextNumber()) {
            return translateColor(iter.nextNumber(), colorMode);
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

    Point polarToCartesian(int rhoX, int thetaY) {
        double tf = (thetaY + theta) / (double) twoPi * Math.PI * 2;
        double yf = Math.sin(tf);
        double xf = Math.cos(tf);
        return new Point((int) Math.round(xf * rhoX), (int) Math.round(yf * rhoX));
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

    void oval(int width, int height, int lineSize, int opacity, Color color) {
        imageGc.setAlpha(opacity);
        imageGc.setLineWidth(lineSize);
        imageGc.setAntialias(SWT.ON);
        imageGc.setForeground(color);

        width -= lineSize * 2;
        height -= lineSize * 2;

        if (lineSize == 0) {
            imageGc.setBackground(color);
            imageGc.fillOval(x - width / 2, y - height / 2, width, height);
        }
        imageGc.drawOval(x - width / 2, y - height / 2, width, height);
    }

    void box(int width, int height, int lineSize, int opacity, Color color) {
        imageGc.setAlpha(opacity);
        imageGc.setLineWidth(lineSize);
        imageGc.setAntialias(SWT.ON);
        imageGc.setForeground(color);
        if (lineSize == 0) {
            imageGc.setBackground(color);
            imageGc.fillRectangle(x - width / 2, y - height / 2, width, height);
        }
        imageGc.drawRectangle(x - width / 2, y - height / 2, width, height);
    }

    void obox(int width, int height, int radiusX, int radiusY, int lineSize, int opacity, Color color) {
        imageGc.setAlpha(opacity);
        imageGc.setLineWidth(lineSize);
        imageGc.setAntialias(SWT.ON);
        imageGc.setForeground(color);
        if (lineSize == 0) {
            imageGc.setBackground(color);
            imageGc.fillRectangle(x - width / 2, y - height / 2, width, height);
        }
        imageGc.drawRoundRectangle(x - width / 2, y - height / 2, width, height, radiusX, radiusY);
    }

    void line(int dx, int dy, int lineSize, Color color) {
        imageGc.setAntialias(SWT.ON);
        imageGc.setForeground(color);
        imageGc.setLineWidth(lineSize);

        imageGc.setAlpha(opacity);
        imageGc.drawLine(x, y, dx, dy);

        x = dx;
        y = dy;
    }

    void text(String str, int size, int style, Color color) {
        imageGc.setAntialias(SWT.ON);
        imageGc.setForeground(color);

        FontDescriptor fontDescriptor = JFaceResources.getDefaultFontDescriptor();

        switch (style & 0b11) {
            case 0b00:
            case 0b01:
                break;
            case 0b10:
            case 0b11:
                fontDescriptor = fontDescriptor.withStyle(SWT.BOLD);
                break;
        }
        if ((style & 0b100) != 0) {
            fontDescriptor = fontDescriptor.withStyle(SWT.ITALIC);
        }
        fontDescriptor = fontDescriptor.setHeight(size - 1);

        Font font = fontDescriptor.createFont(display);
        try {
            imageGc.setFont(font);

            Point extent = imageGc.stringExtent(str);
            int tx = x - extent.x / 2;
            switch (style & 0b11000000) {
                case 0b00100000: // right
                    tx = x - extent.x;
                    break;
                case 0b00110000: // left
                    tx = x;
                    break;
            }
            int ty = y - extent.y / 2;
            switch (style & 0b11000000) {
                case 0b10000000: // bottom
                    ty = y + extent.y;
                    break;
                case 0b11000000: // top
                    ty = y;
                    break;
            }

            imageGc.drawText(str, tx, ty, true);
            if ((style & 0b1000) != 0) { // underline
                imageGc.setLineWidth(0);
                imageGc.drawLine(tx, ty + extent.y, tx + extent.x, ty + extent.y);
            }
        } finally {
            font.dispose();
        }
    }

    static String[] data = new String[] {
        "size 600 650 backcolor white update",
        "origin 300 270 polar -64 -16",
        "clear",
        "set 330 0 cyan 3 text 30 3 'Hub RAM Interface'",
        "set 280 0 text 15 3 'Every cog can read/write 32 bits per clock'",
        "grey 12 set 103 0 line 190 0 20",
        "grey 12 set 103 8 line 190 8 20",
        "grey 12 set 103 16 line 190 16 20",
        "grey 12 set 103 24 line 190 24 20",
        "grey 12 set 103 32 line 190 32 20",
        "grey 12 set 103 40 line 190 40 20",
        "grey 12 set 103 48 line 190 48 20",
        "grey 12 set 103 56 line 190 56 20",
        "set 0 0 cyan 4 circle 151 yellow 7 circle 147 3",
        "set 24 0 white text 14 'Address LSBs'",
        "set 0 0 text 18 1 '8 Hub RAMs'",
        "set 24 32 text 14 '16K x 32' ",
        "cyan 6 set 103 0 circle 55 text 20 '0'",
        "cyan 4 set 103 0 circle 57 3",
        "orange 6 set 190 0 circle 81 text 20 'Cog0'",
        "orange 4 set 190 0 circle 83 3",
        "cyan 6 set 103 8 circle 55 text 20 '1'",
        "cyan 4 set 103 8 circle 57 3",
        "orange 6 set 190 8 circle 81 text 20 'Cog1'",
        "orange 4 set 190 8 circle 83 3",
        "cyan 6 set 103 16 circle 55 text 20 '2'",
        "cyan 4 set 103 16 circle 57 3",
        "orange 6 set 190 16 circle 81 text 20 'Cog2'",
        "orange 4 set 190 16 circle 83 3",
        "cyan 6 set 103 24 circle 55 text 20 '3'",
        "cyan 4 set 103 24 circle 57 3",
        "orange 6 set 190 24 circle 81 text 20 'Cog3'",
        "orange 4 set 190 24 circle 83 3",
        "cyan 6 set 103 32 circle 55 text 20 '4'",
        "cyan 4 set 103 32 circle 57 3",
        "orange 6 set 190 32 circle 81 text 20 'Cog4'",
        "orange 4 set 190 32 circle 83 3",
        "cyan 6 set 103 40 circle 55 text 20 '5'",
        "cyan 4 set 103 40 circle 57 3",
        "orange 6 set 190 40 circle 81 text 20 'Cog5'",
        "orange 4 set 190 40 circle 83 3",
        "cyan 6 set 103 48 circle 55 text 20 '6'",
        "cyan 4 set 103 48 circle 57 3",
        "orange 6 set 190 48 circle 81 text 20 'Cog6'",
        "orange 4 set 190 48 circle 83 3",
        "cyan 6 set 103 56 circle 55 text 20 '7'",
        "cyan 4 set 103 56 circle 57 3",
        "orange 6 set 190 56 circle 81 text 20 'Cog7'",
        "orange 4 set 190 56 circle 83 3",
        "update ",
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
                    DebugWindow window = new DebugPlotWindow();
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
