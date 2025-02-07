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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.internal.CircularBuffer;

public class DebugPlotWindow extends DebugWindow {

    int x;
    int y;

    Color color;
    Color textColor;
    Color backColor;
    int opacity;

    ColorMode colorMode;
    int colorTune;
    Color[] lutColors;

    int lineSize;

    int textSize;
    int textStyle;
    int textAngle;

    boolean polar;
    long twoPi;
    int theta;

    Sprite[] sprites;

    boolean autoUpdate;

    Image canvasImage;
    GC canvasImageGc;

    Image image;
    GC imageGc;

    class Sprite {

        int width;
        int height;
        RGB[] palette;
        byte[] pixels;

        public Sprite(int width, int height, RGB[] palette, byte[] pixels) {
            this.width = width;
            this.height = height;
            this.palette = palette;
            this.pixels = pixels;
        }

    }

    public DebugPlotWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        x = y = 0;
        origin = new Point(0, 0);

        color = new Color(255, 255, 255);
        textColor = new Color(255, 255, 255);
        backColor = new Color(0, 0, 0);
        opacity = 255;

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new Color[256];

        lineSize = 1;

        textSize = 10;
        textStyle = 0b00000001;
        textAngle = 0;

        polar = false;
        twoPi = 0x100000000L;
        theta = 0;

        sprites = new Sprite[256];

        autoUpdate = true;
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

        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));

        imageGc = new GC(image);
        imageGc.setInterpolation(SWT.NONE);
        imageGc.setBackground(backColor);
        imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);

        canvasImage = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));

        canvasImageGc = new GC(canvasImage);
        canvasImageGc.setBackground(backColor);
        canvasImageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                e.gc.setAdvanced(true);
                e.gc.setAntialias(SWT.ON);
                e.gc.setInterpolation(SWT.OFF);
                Point canvasSize = canvas.getSize();
                e.gc.drawImage(canvasImage, 0, 0, imageSize.x, imageSize.y, 0, 0, canvasSize.x, canvasSize.y);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                canvasImage.dispose();
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
                        imageGc.setAlpha(opacity);
                    }
                    break;

                case "PRECISE":
                    break;

                case "LINESIZE":
                    if (iter.hasNextNumber()) {
                        lineSize = iter.nextNumber();
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
                            x = xDirection == 0 ? origin.x + newX : imageSize.x - (origin.x + newX);
                            y = yDirection == 0 ? imageSize.y - (origin.y + newY) : origin.y + newY;
                        }
                    }
                    break;

                case "DOT": {
                    int sizeOverride = lineSize;
                    int opacityOverride = opacity;
                    if (iter.hasNextNumber()) { // line size
                        sizeOverride = iter.nextNumber();
                    }
                    if (iter.hasNextNumber()) { // opacity
                        opacityOverride = iter.nextNumber() & 255;
                    }
                    imageGc.setBackground(color);
                    imageGc.setAlpha(opacityOverride);
                    imageGc.fillOval(x - sizeOverride / 2, y - sizeOverride / 2, sizeOverride, sizeOverride);
                    if (autoUpdate) {
                        update();
                    }
                    break;
                }

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
                            int dx = xDirection == 0 ? origin.x + newX : imageSize.x - (origin.x + newX);
                            int dy = yDirection == 0 ? imageSize.y - (origin.y + newY) : origin.y + newY;
                            int size = iter.hasNextNumber() ? iter.nextNumber() : lineSize;
                            line(dx, dy, size, color);
                            if (autoUpdate) {
                                update();
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
                            update();
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
                                update();
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
                                update();
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
                                        update();
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
                            update();
                        }
                    }
                    break;
                }

                case "SPRITEDEF":
                    if (iter.hasNextNumber()) {
                        int id = iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            int xDim = iter.nextNumber();
                            if (iter.hasNextNumber()) {
                                int yDim = iter.nextNumber();

                                RGB[] palette = new RGB[256];
                                byte[] pixels = new byte[xDim * yDim];

                                int idx = 0;
                                while (idx < pixels.length) {
                                    pixels[idx++] = (byte) iter.nextNumber();
                                }

                                idx = 0;
                                while (iter.hasNextNumber() && idx < palette.length) {
                                    int c = iter.nextNumber();
                                    palette[idx++] = new RGB((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF);
                                }

                                if (id >= 0 && id < sprites.length) {
                                    sprites[id] = new Sprite(xDim, yDim, palette, pixels);
                                }
                            }
                        }
                    }
                    break;
                case "SPRITE":
                    if (iter.hasNextNumber()) {
                        int id = iter.nextNumber();
                        int orient = 0;
                        int scale = 1;
                        int opacityOverride = opacity;
                        if (iter.hasNextNumber()) {
                            orient = iter.nextNumber() & 7; // orient
                        }
                        if (iter.hasNextNumber()) {
                            scale = iter.nextNumber() & 63; // scale
                        }
                        if (iter.hasNextNumber()) {
                            opacityOverride = iter.nextNumber() & 255;
                        }
                        if (id >= 0 && id < sprites.length && sprites[id] != null && scale >= 1) {
                            Image image = transformImage(sprites[id], orient);
                            try {
                                Rectangle bounds = image.getBounds();
                                imageGc.setAlpha(opacityOverride);
                                imageGc.drawImage(image, 0, 0, bounds.width, bounds.height, x, y, bounds.width * scale, bounds.height * scale);
                            } finally {
                                image.dispose();
                            }
                            if (autoUpdate) {
                                update();
                            }
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
                    yDirection = xDirection = 0;
                    if (iter.hasNextNumber()) {
                        yDirection = iter.nextNumber() == 0 ? 0 : 1;
                        if (iter.hasNextNumber()) {
                            xDirection = iter.nextNumber() == 0 ? 0 : 1;
                        }
                    }
                    break;

                case "CLEAR":
                    imageGc.setBackground(backColor);
                    imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);
                    if (autoUpdate) {
                        update();
                    }
                    break;

                case "UPDATE":
                    update();
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

                case "PC_KEY":
                    sendKeyPress();
                    break;

                case "PC_MOUSE":
                    sendMouse();
                    break;
            }
        }
    }

    Image transformImage(Sprite sprite, int orient) {
        ImageData imageData = new ImageData(sprite.width, sprite.height, 8, new PaletteData(sprite.palette), 4, new byte[sprite.pixels.length]);

        int x = 0;
        int y = 0;

        switch (orient & 7) {
            case 0:
            case 2:
            case 4:
            case 5:
                x = 0;
                break;
            default:
                x = sprite.width - 1;
                break;
        }
        switch (orient & 7) {
            case 0:
            case 1:
            case 4:
            case 6:
                y = 0;
                break;
            default:
                y = sprite.height - 1;
                break;
        }

        int idx = 0;
        while (idx < sprite.pixels.length) {
            int b = sprite.pixels[idx++];
            switch (orient) {
                case 0:
                    if (x >= imageData.width) {
                        x = 0;
                        if (++y >= imageData.height) {
                            y = 0;
                        }
                    }
                    imageData.setPixel(x++, y, b);
                    break;
                case 1:
                    if (x < 0) {
                        x = imageData.width - 1;
                        if (++y >= imageData.height) {
                            y = 0;
                        }
                    }
                    imageData.setPixel(x--, y, b);
                    break;
                case 2:
                    if (x >= imageData.width) {
                        x = 0;
                        if (--y < 0) {
                            y = imageData.height - 1;
                        }
                    }
                    imageData.setPixel(x++, y, b);
                    break;
                case 3:
                    if (x < 0) {
                        x = imageData.width - 1;
                        if (--y < 0) {
                            y = imageData.height - 1;
                        }
                    }
                    imageData.setPixel(x--, y, b);
                    break;
                case 4:
                    if (y >= imageData.height) {
                        y = 0;
                        if (++x >= imageData.width) {
                            x = 0;
                        }
                    }
                    imageData.setPixel(x, y++, b);
                    break;
                case 5:
                    if (y < 0) {
                        y = imageData.height - 1;
                        if (++x >= imageData.width) {
                            x = 0;
                        }
                    }
                    imageData.setPixel(x, y--, b);
                    break;
                case 6:
                    if (y >= imageData.height) {
                        y = 0;
                        if (--x < 0) {
                            x = imageData.width - 1;
                        }
                    }
                    imageData.setPixel(x, y++, b);
                    break;
                case 7:
                    if (y < 0) {
                        y = imageData.height - 1;
                        if (--x < 0) {
                            x = imageData.width - 1;
                        }
                    }
                    imageData.setPixel(x, y--, b);
                    break;
            }
        }

        return new Image(display, imageData);
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

    void update() {
        canvasImageGc.drawImage(image, 0, 0);
        canvas.redraw();
    }

    static String[] data = new String[] {
        "plot myplot size 384 384 update",
        "myplot cartesian 1",
        "myplot spritedef 0 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $1F $1F $18 $18 $19 $00 $00 $00 $00 $00 $00 $00 $00 $19 $1F $1F $1F $1F $1F $18 $19 $00 $00 $00 $00 $00 $00 $00 $19 $21 $1F $21 $21 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $19 $21 $21 $21 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $19 $21 $21 $19 $19 $11 $11 $11 $11 $19 $19 $19 $00 $00 $00 $19 $19 $19 $19 $19 $11 $09 $09 $19 $09 $19 $00 $00 $00 $00 $00 $19 $09 $09 $19 $19 $11 $09 $09 $19 $09 $19 $19 $19 $00 $00 $00 $19 $09 $09 $19 $19 $19 $11 $09 $09 $09 $08 $08 $08 $19 $00 $00 $19 $11 $09 $11 $19 $11 $09 $19 $11 $09 $09 $09 $09 $19 $00 $00 $00 $19 $11 $11 $11 $09 $19 $19 $19 $11 $11 $11 $19 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 1 16 16 $00 $00 $00 $19 $19 $11 $11 $09 $09 $19 $19 $19 $19 $00 $00 $00 $00 $00 $19 $19 $19 $19 $11 $11 $11 $11 $11 $19 $00 $00 $00 $00 $00 $00 $19 $21 $1F $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $19 $21 $1F $18 $1F $19 $19 $03 $03 $03 $19 $00 $00 $00 $00 $00 $19 $21 $1F $18 $1F $19 $08 $03 $03 $03 $03 $19 $00 $00 $00 $00 $19 $21 $1F $1F $18 $19 $08 $08 $03 $03 $03 $19 $00 $00 $00 $00 $19 $19 $21 $1F $1F $1F $19 $08 $08 $08 $19 $19 $19 $00 $00 $00 $19 $19 $19 $21 $21 $21 $19 $19 $19 $19 $0F $03 $19 $00 $00 $00 $00 $19 $17 $19 $19 $19 $15 $03 $03 $0F $0F $03 $19 $00 $00 $00 $00 $19 $17 $17 $17 $15 $15 $15 $0F $0F $15 $15 $19 $00 $00 $00 $00 $19 $17 $17 $15 $15 $15 $15 $15 $15 $15 $19 $00 $00 $00 $00 $00 $19 $17 $17 $17 $17 $17 $17 $17 $17 $17 $19 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $13 $13 $12 $12 $07 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $13 $13 $13 $12 $12 $07 $19 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 2 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1B $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1B $1B $1B $00 $00 $00 $1B $00 $00 $00 $00 $00 $00 $00 $00 $1B $1B $0B $1B $1B $00 $1B $00 $00 $00 $00 $00 $00 $00 $00 $00 $1B $0B $0B $0B $1B $00 $00 $1B $00 $00 $00 $00 $00 $00 $00 $00 $1B $0B $02 $0B $1B $1B $1B $1B $00 $00 $00 $00 $00 $00 $00 $00 $1B $1B $0B $02 $0B $0B $1B $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1B $1B $0B $0B $1B $1B $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1B $1B $1B $1B $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 3 16 16 $00 $00 $00 $19 $19 $11 $11 $09 $09 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $11 $11 $11 $11 $19 $00 $00 $00 $00 $00 $00 $00 $19 $19 $1F $1F $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $19 $1F $18 $18 $1F $1F $19 $21 $19 $00 $00 $00 $00 $00 $00 $19 $19 $21 $1F $1F $18 $18 $1F $19 $19 $19 $00 $00 $00 $00 $19 $17 $15 $19 $21 $21 $21 $1F $1F $19 $03 $03 $19 $00 $00 $00 $19 $17 $15 $15 $19 $19 $21 $21 $19 $08 $03 $03 $03 $19 $00 $00 $19 $17 $17 $15 $15 $15 $19 $19 $17 $08 $08 $03 $03 $19 $00 $00 $19 $19 $17 $17 $17 $15 $0F $03 $03 $19 $08 $08 $19 $19 $00 $19 $13 $13 $19 $17 $15 $15 $15 $15 $15 $17 $19 $19 $13 $19 $00 $19 $13 $19 $17 $15 $15 $15 $17 $17 $17 $19 $13 $13 $13 $19 $00 $13 $12 $19 $17 $17 $17 $17 $19 $19 $19 $19 $12 $13 $13 $19 $00 $13 $12 $19 $17 $19 $19 $19 $00 $00 $00 $19 $12 $13 $19 $19 $00 $13 $12 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $00 $00 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 4 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1C $1C $1C $1C $1C $1C $1C $00 $00 $00 $00 $00 $00 $00 $00 $1C $0C $0C $0C $1C $0C $1C $1C $1C $00 $00 $1C $1C $00 $00 $1C $0C $0C $0C $0C $0C $0C $0C $1C $1C $1C $00 $00 $1C $00 $1C $0C $0C $0C $04 $04 $04 $04 $0C $0C $1C $1C $00 $00 $1C $00 $1C $0C $0C $04 $04 $04 $04 $04 $04 $0C $1C $1C $00 $00 $1C $1C $0C $0C $04 $04 $04 $01 $01 $01 $04 $0C $1C $1C $1C $1C $1C $1C $1C $0C $04 $04 $01 $01 $01 $01 $04 $04 $0C $1C $1C $00 $1C $00 $1C $0C $04 $04 $01 $01 $01 $01 $04 $04 $0C $1C $1C $1C $1C $00 $1C $1C $04 $04 $04 $01 $01 $04 $04 $04 $1C $1C $1C $1C $1C $00 $1C $1C $0C $04 $04 $04 $04 $04 $04 $0C $1C $0C $0C $1C $1C $00 $00 $1C $1C $0C $04 $04 $04 $04 $04 $04 $04 $0C $1C $1C $00 $00 $00 $00 $1C $1C $1C $0C $04 $0C $04 $0C $1C $1C $1C $00 $00 $00 $00 $00 $00 $1C $1C $1C $1C $0C $1C $1C $1C $1C $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1C $1C $1C $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 5 16 16 $00 $00 $00 $19 $19 $11 $11 $11 $11 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $19 $17 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $19 $17 $19 $1F $1F $18 $18 $19 $03 $03 $03 $03 $19 $00 $00 $00 $19 $19 $21 $1F $1F $1F $18 $19 $08 $03 $03 $03 $19 $00 $00 $19 $17 $19 $21 $21 $1F $1F $1F $19 $08 $08 $08 $19 $00 $00 $00 $19 $17 $17 $19 $21 $21 $21 $21 $19 $19 $19 $19 $00 $00 $00 $00 $19 $17 $17 $17 $19 $19 $19 $19 $19 $19 $19 $15 $19 $00 $00 $00 $19 $17 $17 $17 $17 $17 $15 $03 $03 $0F $0F $03 $19 $00 $00 $00 $00 $19 $17 $17 $17 $15 $15 $03 $03 $0F $0F $03 $19 $00 $00 $00 $00 $19 $17 $17 $17 $15 $15 $15 $0F $0F $15 $19 $19 $00 $00 $00 $00 $19 $17 $17 $15 $15 $15 $15 $15 $15 $15 $19 $00 $00 $00 $00 $00 $19 $17 $17 $17 $17 $17 $17 $17 $17 $17 $19 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $13 $13 $12 $12 $07 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $13 $13 $13 $12 $12 $07 $19 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 6 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1D $1D $1D $1D $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $1D $1D $1E $1A $1A $1A $1D $00 $00 $00 $00 $00 $00 $00 $00 $1D $20 $1E $1E $1E $1A $1A $1D $00 $00 $00 $00 $00 $00 $00 $1D $20 $1E $1E $1E $1D $1D $1D $1D $1D $1D $00 $00 $00 $00 $1D $20 $20 $20 $1D $1D $1D $1D $1D $1D $1D $1D $1D $00 $00 $00 $1D $20 $20 $1D $1D $0A $0A $0A $0A $0A $1D $1D $00 $00 $00 $1D $1D $1D $1D $1D $0A $0A $0A $1D $0A $1D $1D $00 $00 $00 $00 $1D $0A $0A $1D $1D $0A $0A $0A $1D $0A $1D $1D $1D $00 $00 $00 $1D $0A $0A $1D $1D $1D $0A $0A $0A $0A $0A $05 $05 $1D $00 $00 $1D $0A $0A $0A $1D $0A $0A $1D $0A $0A $0A $0A $0A $1D $00 $00 $00 $1D $0A $0A $0A $0A $1D $1D $1D $1D $0A $0A $1D $1D $1D $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 7 16 16 $00 $00 $1D $1D $10 $1D $0A $0A $0A $0A $0A $1D $00 $00 $00 $00 $00 $1D $20 $1E $1D $10 $1D $1D $1D $1D $1D $00 $00 $00 $00 $00 $00 $1D $1E $1E $1A $1D $1D $1D $20 $1A $1D $1D $00 $1D $00 $00 $1D $20 $1E $1A $1D $00 $00 $00 $1D $20 $1A $1D $1D $00 $1D $00 $1D $20 $1E $1D $05 $00 $00 $00 $00 $1D $20 $1E $1D $05 $1D $00 $1D $20 $20 $1D $05 $05 $00 $00 $00 $1D $1D $1D $1D $1D $00 $00 $1D $1D $20 $20 $1D $05 $05 $05 $1D $00 $0E $0E $00 $1D $00 $00 $00 $1D $1D $1D $16 $1D $1D $1D $00 $00 $0E $0E $00 $1D $00 $00 $00 $1D $16 $16 $16 $10 $10 $0E $10 $0E $0E $0E $10 $1D $00 $00 $00 $00 $1D $16 $10 $10 $0E $10 $16 $10 $10 $10 $10 $1D $1D $00 $00 $1D $14 $1D $16 $10 $10 $16 $1D $16 $16 $10 $1D $06 $0D $1D $00 $1D $14 $1D $16 $16 $16 $1D $00 $1D $16 $1D $06 $0D $14 $1D $00 $1D $14 $0D $1D $1D $1D $00 $00 $00 $1D $0D $0D $14 $1D $00 $00 $00 $1D $14 $0D $0D $14 $1D $00 $00 $1D $14 $14 $1D $00 $00 $00 $00 $00 $1D $1D $1D $1D $1D $00 $00 $00 $1D $1D $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 8 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $18 $18 $18 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $19 $21 $1F $1F $1F $18 $18 $19 $00 $00 $00 $00 $00 $00 $00 $19 $21 $1F $1F $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $19 $21 $21 $21 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $19 $21 $19 $19 $11 $11 $11 $11 $11 $11 $19 $19 $00 $00 $00 $19 $19 $19 $19 $11 $09 $09 $19 $09 $19 $09 $19 $00 $00 $00 $00 $19 $09 $19 $19 $11 $09 $09 $19 $09 $19 $09 $19 $00 $00 $00 $19 $09 $09 $19 $19 $19 $11 $09 $09 $09 $08 $08 $08 $19 $00 $00 $19 $11 $09 $11 $19 $11 $09 $19 $11 $11 $09 $09 $09 $19 $00 $00 $00 $19 $11 $11 $11 $09 $19 $19 $19 $19 $11 $11 $19 $19 $19 $00 $00 $00 $19 $19 $11 $11 $09 $09 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $19 $19 $19 $11 $11 $11 $11 $11 $19 $00 $00 $00 $00 $00 $00 $19 $1F $1F $1F $19 $19 $19 $19 $19 $19 $00 $19 $19 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 9 16 16 $00 $19 $21 $1F $18 $18 $1F $19 $19 $1F $18 $19 $19 $08 $03 $19 $19 $21 $1F $18 $19 $19 $19 $15 $19 $21 $1F $18 $19 $19 $03 $19 $19 $21 $21 $19 $03 $03 $03 $19 $15 $19 $21 $1F $1F $19 $08 $19 $19 $21 $19 $08 $03 $03 $03 $03 $19 $0F $19 $19 $19 $19 $19 $00 $00 $19 $19 $08 $08 $03 $03 $03 $19 $03 $03 $0F $0F $19 $19 $00 $00 $00 $19 $19 $08 $08 $08 $19 $15 $03 $03 $0F $19 $07 $12 $19 $00 $19 $13 $13 $19 $19 $19 $15 $15 $15 $15 $19 $07 $12 $13 $19 $19 $19 $13 $19 $17 $15 $15 $15 $17 $17 $17 $19 $12 $13 $13 $19 $19 $13 $12 $19 $17 $17 $17 $17 $19 $19 $19 $19 $12 $13 $13 $19 $19 $13 $12 $19 $17 $19 $19 $19 $00 $00 $00 $19 $12 $13 $19 $19 $19 $13 $12 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $00 $00 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 10 16 16 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $00 $19 $19 $19 $00 $00 $00 $00 $00 $00 $19 $19 $18 $18 $18 $19 $19 $03 $03 $19 $19 $00 $00 $00 $00 $19 $21 $1F $1F $1F $18 $18 $19 $08 $03 $03 $19 $00 $00 $00 $19 $21 $1F $1F $19 $19 $19 $19 $19 $19 $08 $08 $19 $00 $00 $19 $21 $21 $21 $19 $19 $19 $19 $19 $19 $19 $19 $08 $19 $00 $00 $19 $21 $19 $19 $11 $11 $11 $11 $11 $11 $19 $19 $19 $19 $00 $19 $19 $19 $19 $11 $09 $09 $19 $09 $19 $09 $19 $1F $21 $19 $00 $19 $09 $19 $19 $11 $09 $09 $19 $09 $19 $09 $19 $1F $21 $19 $19 $09 $09 $19 $19 $19 $11 $09 $09 $09 $08 $08 $08 $19 $21 $19 $19 $11 $09 $11 $19 $11 $09 $19 $11 $11 $09 $09 $09 $19 $19 $00 $00 $19 $11 $11 $11 $09 $19 $19 $19 $19 $11 $11 $19 $19 $19 $00 $00 $00 $19 $19 $11 $11 $09 $09 $19 $19 $19 $19 $19 $1F $19 $00 $00 $00 $00 $19 $19 $19 $11 $11 $11 $11 $11 $19 $1F $19 $00 $00 $00 $00 $19 $1F $1F $1F $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $19 $21 $1F $18 $18 $1F $19 $19 $1F $18 $19 $19 $00 $00 $00 $19 $21 $1F $18 $19 $19 $19 $15 $19 $21 $1F $18 $19 $19 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 11 16 16 $19 $21 $21 $19 $03 $03 $03 $19 $15 $19 $21 $1F $1F $19 $00 $00 $19 $21 $19 $08 $03 $03 $03 $03 $19 $0F $19 $19 $19 $19 $19 $00 $00 $19 $19 $08 $08 $03 $03 $03 $19 $03 $03 $0F $0F $19 $19 $00 $00 $00 $19 $19 $08 $08 $08 $19 $15 $03 $03 $0F $19 $07 $12 $19 $00 $19 $13 $13 $19 $19 $19 $15 $15 $15 $15 $19 $07 $12 $13 $19 $19 $19 $13 $19 $17 $15 $15 $15 $17 $17 $17 $19 $12 $13 $13 $19 $19 $13 $12 $19 $17 $17 $17 $17 $19 $19 $19 $19 $12 $13 $13 $19 $19 $13 $12 $19 $17 $19 $19 $19 $00 $00 $00 $19 $12 $13 $19 $19 $19 $13 $12 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $00 $00 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 12 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $1F $18 $18 $18 $18 $1F $19 $00 $00 $00 $00 $00 $00 $00 $19 $1F $19 $19 $19 $19 $19 $19 $1F $19 $00 $00 $00 $00 $00 $19 $19 $19 $1F $1F $1F $1F $1F $1F $19 $19 $19 $00 $00 $00 $19 $19 $1F $1F $18 $18 $18 $18 $18 $18 $1F $1F $19 $19 $00 $19 $09 $19 $1F $18 $18 $1F $1F $1F $1F $18 $18 $1F $19 $09 $19 $19 $11 $19 $21 $1F $1F $1F $1F $1F $1F $1F $1F $21 $19 $11 $19 $00 $19 $19 $21 $1F $1F $1F $1F $1F $1F $1F $1F $21 $19 $19 $00 $19 $1F $19 $19 $21 $1F $1F $1F $1F $1F $1F $21 $19 $19 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 13 16 16 $19 $1F $18 $19 $21 $21 $21 $21 $21 $21 $21 $21 $19 $1F $19 $00 $19 $1F $18 $19 $19 $21 $21 $21 $21 $21 $21 $19 $19 $18 $1F $19 $19 $21 $1F $18 $19 $19 $19 $19 $19 $19 $19 $19 $18 $1F $21 $19 $00 $19 $21 $1F $19 $19 $19 $19 $19 $19 $19 $1F $1F $1F $21 $19 $00 $19 $21 $21 $19 $17 $19 $1F $19 $15 $19 $1F $1F $21 $21 $19 $00 $00 $19 $21 $19 $17 $19 $1F $19 $15 $19 $21 $21 $21 $19 $00 $00 $00 $00 $19 $17 $19 $21 $21 $21 $19 $15 $19 $21 $19 $15 $19 $00 $00 $19 $17 $15 $19 $19 $19 $19 $19 $15 $17 $19 $17 $15 $15 $00 $00 $19 $17 $15 $0F $0F $15 $15 $15 $15 $15 $17 $17 $17 $17 $00 $00 $19 $17 $15 $15 $15 $19 $17 $15 $15 $17 $17 $17 $17 $19 $00 $00 $19 $17 $17 $15 $15 $19 $19 $17 $17 $17 $17 $17 $19 $00 $00 $00 $00 $19 $17 $17 $17 $17 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $19 $17 $17 $17 $17 $19 $13 $13 $12 $12 $12 $19 $00 $00 $00 $19 $12 $19 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $19 $12 $12 $13 $13 $13 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 14 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $1F $1F $1F $18 $18 $19 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 15 16 16 $00 $00 $00 $00 $19 $1F $1F $1F $1F $18 $1F $18 $18 $19 $00 $00 $00 $00 $00 $19 $21 $1F $1F $21 $1F $18 $19 $1F $1F $19 $00 $00 $00 $00 $00 $19 $21 $21 $1F $19 $21 $1F $19 $1F $21 $19 $00 $00 $00 $00 $19 $21 $21 $21 $21 $21 $19 $19 $19 $21 $19 $19 $19 $00 $00 $00 $19 $21 $21 $21 $21 $19 $03 $03 $03 $19 $19 $19 $19 $00 $00 $00 $19 $21 $21 $21 $19 $08 $03 $03 $03 $03 $19 $19 $00 $00 $00 $00 $19 $21 $21 $19 $19 $08 $08 $03 $03 $03 $19 $08 $19 $00 $00 $00 $00 $19 $19 $11 $08 $19 $08 $08 $08 $19 $11 $09 $19 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $19 $1F $18 $1F $19 $1F $1F $1F $1F $19 $00 $00 $00 $00 $00 $19 $21 $1F $1F $18 $1F $1F $1F $1F $21 $19 $00 $00 $00 $00 $00 $19 $21 $21 $1F $1F $1F $1F $21 $21 $19 $17 $19 $00 $00 $00 $00 $19 $19 $21 $21 $21 $21 $21 $19 $19 $15 $15 $19 $00 $00 $00 $00 $19 $17 $19 $19 $19 $19 $19 $17 $15 $15 $19 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $19 $13 $13 $13 $13 $12 $12 $07 $19 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 16 16 16 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $30 $30 $2A $22 $22 $22 $22 $22 $22 $22 $22 $22 $2A $2B $30 $30 $30 $2A $22 $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2A $22 $2A $20 $20 $20 $20 $20 $20 $20 $2B $2A $2B $2B $30 $30 $2A $22 $20 $20 $20 $20 $20 $20 $20 $20 $20 $2B $2B $2B $30 $30 $2A $22 $20 $20 $20 $30 $30 $30 $20 $20 $20 $30 $2B $2B $30 $30 $2A $22 $2A $30 $30 $30 $2B $2B $20 $20 $20 $30 $2B $2B $30 $30 $2A $22 $2A $2B $2B $20 $20 $20 $20 $20 $30 $30 $2B $2B $30 $30 $2A $22 $2A $2A $2A $20 $20 $20 $30 $30 $30 $2B $2B $2B $30 $30 $2A $22 $2A $2A $2A $2A $30 $30 $30 $2B $2B $2A $2B $2B $30 $30 $2A $22 $2A $2A $2A $20 $20 $20 $2B $2B $2A $2A $2B $2B $30 $30 $2A $2A $2A $2A $2A $20 $20 $20 $30 $2B $2A $2A $2B $2B $30 $30 $2A $2B $2A $2A $2A $2A $30 $30 $30 $2B $2A $2B $2B $2B $30 $30 $2A $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $30 $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 17 16 16 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $30 $30 $2A $22 $22 $22 $22 $22 $22 $22 $22 $22 $2A $2A $30 $30 $30 $2A $22 $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $30 $30 $2A $22 $2A $2A $2A $2A $2A $20 $20 $20 $20 $20 $20 $20 $30 $30 $2A $22 $2A $2A $2A $2A $20 $20 $20 $20 $20 $20 $20 $20 $30 $30 $2A $22 $2A $2A $2A $2A $20 $20 $20 $30 $30 $30 $20 $20 $30 $30 $2A $22 $2A $2A $2A $2A $2A $30 $30 $30 $2B $2B $20 $20 $30 $30 $2A $22 $2A $2A $2A $2A $2A $2B $2B $20 $20 $20 $20 $20 $30 $30 $2A $22 $2A $2A $2A $2A $2A $2A $2A $20 $20 $20 $30 $30 $30 $30 $2A $22 $2A $2A $2A $2A $2A $2A $2A $2A $30 $30 $30 $2B $30 $30 $2A $22 $2A $2A $2A $2A $2A $2A $2A $20 $20 $20 $2B $2B $30 $30 $2A $2A $2A $2A $2A $2A $2A $2A $2A $20 $20 $20 $30 $2B $30 $30 $2A $2B $2A $2A $2A $2A $2A $2A $2A $2A $30 $30 $30 $2B $30 $30 $2A $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $30 $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 18 16 16 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $30 $30 $2A $22 $22 $22 $22 $22 $22 $22 $22 $22 $2A $2B $30 $30 $30 $2A $22 $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2A $20 $20 $2A $2A $2A $2A $2A $2A $2A $2A $20 $20 $20 $30 $30 $2A $20 $20 $20 $2B $2A $2A $2A $2A $2A $20 $20 $20 $20 $30 $30 $2A $20 $20 $20 $30 $2B $2A $2A $2A $2A $20 $20 $20 $30 $30 $30 $2A $20 $20 $20 $30 $2B $2A $2A $2A $2A $2A $30 $30 $30 $30 $30 $2A $20 $20 $30 $30 $2B $2A $2A $2A $2A $2A $2B $2B $20 $30 $30 $2A $30 $30 $30 $2B $2A $2A $2A $2A $2A $2A $2A $2B $20 $30 $30 $2A $30 $2B $2B $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2A $2B $2B $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $20 $30 $30 $2A $30 $2B $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $20 $30 $30 $2A $30 $2B $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $2B $30 $30 $2A $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $30 $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 19 16 16 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $30 $30 $2A $22 $22 $22 $22 $22 $22 $22 $22 $22 $22 $2B $30 $30 $30 $2A $22 $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2A $20 $20 $20 $20 $20 $20 $2A $2A $2A $2A $2A $2B $2B $30 $30 $2A $20 $20 $20 $20 $20 $20 $20 $2B $2A $2A $2A $2B $2B $30 $30 $2A $20 $30 $30 $20 $20 $20 $20 $30 $2B $2A $2A $2B $2B $30 $30 $2A $30 $30 $2B $20 $20 $20 $20 $30 $2B $2A $2A $2B $2B $30 $30 $2A $20 $20 $20 $20 $20 $20 $30 $30 $2B $2A $2A $2B $2B $30 $30 $2A $20 $20 $20 $20 $30 $30 $30 $2B $2A $2A $2A $2B $2B $30 $30 $2A $2B $2B $30 $30 $30 $2B $2B $2A $2A $2A $2A $2B $2B $30 $30 $2A $20 $20 $20 $2B $2B $2B $2A $2A $2A $2A $2A $2B $2B $30 $30 $2A $20 $20 $20 $30 $2B $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2A $22 $30 $30 $30 $2B $2A $2A $2A $2A $2A $2B $2B $2B $30 $30 $2A $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $30 $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 20 16 16 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $30 $30 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $30 $2B $2A $20 $20 $20 $22 $22 $22 $22 $22 $2A $2A $2B $2B $30 $30 $2B $20 $20 $22 $22 $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $20 $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $22 $2A $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $30 $30 $2B $2B $2A $2A $2A $2A $2A $2A $2A $2A $2A $2B $2B $2B $30 $30 $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $30 $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $2B $30 $30 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020 $00 $30 $30 $30 $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $21 $21 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $21 $22 $22 $22 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $21 $22 $21 $21 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $22 $22 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $22 $2A $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $22 $2A $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $22 $2A $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $22 $2A $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $22 $2A $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $22 $2A $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $30 $21 $22 $20 $2A $2A $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $30 $21 $22 $2B $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $21 $22 $22 $22 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $21 $21 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $30 $30 $30 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 22 16 16 $00 $00 $00 $00 $00 $00 $00 $30 $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $22 $20 $20 $22 $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $20 $2B $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $22 $2B $2B $22 $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $20 $22 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $2A $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $30 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $22 $30 $22 $2A $30 $22 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $22 $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $2A $2A $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $30 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 24 16 16 $00 $00 $00 $00 $00 $30 $30 $30 $30 $30 $30 $00 $00 $00 $00 $00 $00 $00 $00 $30 $30 $20 $20 $32 $32 $20 $20 $30 $30 $00 $00 $00 $00 $00 $30 $32 $20 $20 $20 $31 $31 $20 $20 $20 $32 $30 $00 $00 $00 $30 $32 $31 $20 $20 $31 $31 $31 $31 $20 $20 $31 $32 $30 $00 $00 $30 $20 $31 $31 $31 $29 $29 $29 $29 $31 $31 $31 $20 $30 $00 $30 $20 $20 $20 $31 $29 $20 $20 $20 $20 $29 $31 $20 $20 $20 $30 $30 $20 $20 $20 $31 $20 $20 $20 $20 $20 $20 $31 $20 $20 $20 $30 $30 $20 $20 $20 $31 $20 $20 $20 $20 $20 $20 $31 $20 $20 $20 $30 $30 $20 $20 $31 $31 $20 $20 $20 $20 $20 $20 $31 $31 $20 $20 $30 $30 $32 $31 $32 $32 $32 $20 $20 $20 $20 $32 $32 $32 $31 $32 $30 $30 $32 $32 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $32 $32 $30 $00 $30 $30 $30 $25 $25 $30 $25 $25 $30 $25 $25 $30 $30 $30 $00 $00 $00 $30 $25 $20 $20 $30 $20 $20 $30 $20 $20 $25 $30 $00 $00 $00 $00 $30 $25 $20 $20 $20 $20 $20 $20 $20 $20 $25 $30 $00 $00 $00 $00 $00 $30 $25 $25 $25 $25 $25 $25 $25 $25 $30 $00 $00 $00 $00 $00 $00 $00 $30 $30 $30 $30 $30 $30 $30 $30 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020 $20 $20 $20 $20 $20 $20 $28 $20 $20 $20 $30 $30 $20 $20 $28 $28 $20 $20 $20 $20 $20 $20 $28 $28 $20 $20 $30 $30 $2C $28 $2C $2C $2C $20 $20 $20 $20 $2C $2C $2C $28 $2C $30 $30 $2C $2C $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $2C $2C $30 $00 $30 $30 $30 $25 $25 $30 $25 $25 $30 $25 $25 $30 $30 $30 $00 $00 $00 $30 $25 $20 $20 $30 $20 $20 $30 $20 $20 $25 $30 $00 $00 $00 $00 $30 $25 $20 $20 $20 $20 $20 $20 $20 $20 $25 $30 $00 $00 $00 $00 $00 $30 $25 $25 $25 $25 $25 $25 $25 $25 $30 $00 $00 $00 $00 $00 $00 $00 $30 $30 $30 $30 $30 $30 $30 $30 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 26 16 16 $00 $00 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $00 $00 $00 $30 $30 $30 $0E $0E $0E $0E $0E $0E $0E $0E $30 $30 $30 $00 $30 $30 $0E $0E $0E $27 $27 $27 $27 $27 $27 $0E $0E $0E $30 $30 $30 $0E $0E $27 $27 $27 $30 $20 $20 $30 $27 $27 $27 $0E $0E $30 $30 $0E $0E $27 $27 $27 $30 $27 $27 $30 $27 $27 $27 $0E $0E $30 $30 $0E $0E $0E $0E $27 $27 $27 $27 $27 $27 $0E $0E $0E $0E $30 $30 $30 $0E $0E $0E $0E $0E $0E $0E $0E $0E $0E $0E $0E $30 $30 $00 $30 $30 $30 $0E $0E $0E $0E $0E $0E $0E $0E $30 $30 $30 $00 $00 $00 $00 $30 $30 $30 $30 $30 $30 $30 $30 $30 $30 $00 $00 $00 $00 $30 $30 $00 $00 $00 $30 $2C $2C $30 $00 $00 $00 $30 $30 $00 $30 $23 $23 $30 $30 $00 $30 $28 $28 $30 $00 $30 $30 $23 $23 $30 $30 $23 $2C $23 $23 $30 $30 $28 $28 $30 $30 $23 $23 $2C $23 $30 $30 $28 $23 $2C $23 $23 $30 $28 $28 $30 $23 $23 $2C $23 $28 $30 $00 $30 $28 $28 $2C $23 $30 $28 $28 $30 $23 $2C $28 $28 $30 $00 $00 $00 $30 $30 $28 $2C $28 $28 $28 $28 $2C $28 $30 $30 $00 $00 $00 $00 $00 $00 $30 $30 $30 $30 $30 $30 $30 $30 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020 $00 $00 $00 $00 $30 $22 $22 $22 $22 $30 $00 $00 $00 $00 $00 $30 $30 $30 $30 $30 $30 $22 $22 $22 $22 $30 $30 $30 $30 $30 $30 $30 $2A $22 $22 $22 $22 $22 $22 $22 $22 $22 $22 $22 $22 $2A $30 $00 $30 $2A $22 $22 $20 $30 $22 $22 $30 $20 $22 $22 $2A $30 $00 $00 $00 $30 $2A $22 $20 $30 $20 $20 $30 $20 $22 $2A $30 $00 $00 $00 $00 $00 $30 $22 $22 $30 $22 $22 $30 $22 $22 $30 $00 $00 $00 $00 $00 $00 $30 $22 $22 $22 $22 $22 $22 $22 $22 $30 $00 $00 $00 $00 $00 $30 $22 $22 $22 $22 $22 $22 $22 $22 $22 $22 $30 $00 $00 $00 $00 $30 $22 $22 $22 $22 $2A $2A $22 $22 $22 $22 $30 $00 $00 $00 $30 $22 $22 $22 $2A $2A $30 $30 $2A $2A $22 $22 $22 $30 $00 $00 $30 $22 $2A $2A $30 $30 $00 $00 $30 $30 $2A $2A $22 $30 $00 $30 $2A $2A $30 $30 $00 $00 $00 $00 $00 $00 $30 $30 $2A $2A $30 $30 $30 $30 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $30 $30 $30 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 28 16 16 $00 $00 $00 $00 $00 $00 $2D $2D $2D $2D $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $2D $2D $2D $2D $2D $2D $00 $00 $00 $00 $00 $00 $00 $00 $00 $2D $2D $2D $2D $2D $2D $2D $2D $00 $00 $00 $00 $00 $00 $00 $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $00 $00 $00 $00 $00 $2D $33 $33 $2D $2D $2D $2D $2D $2D $33 $33 $2D $00 $00 $00 $2D $2D $2D $24 $33 $2D $2D $2D $2D $33 $24 $2D $2D $2D $00 $00 $2D $2D $2D $24 $33 $33 $33 $33 $33 $33 $24 $2D $2D $2D $00 $2D $2D $2D $2D $24 $33 $24 $2D $2D $24 $33 $24 $2D $2D $2D $2D $2D $2D $2D $2D $24 $24 $24 $2D $2D $24 $24 $24 $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $00 $2D $2D $2D $2D $24 $24 $24 $24 $24 $24 $2D $2D $2D $2D $00 $00 $00 $00 $00 $24 $24 $24 $24 $24 $24 $24 $24 $00 $00 $00 $00 $00 $00 $00 $00 $24 $24 $24 $24 $24 $24 $24 $24 $33 $33 $00 $00 $00 $00 $00 $33 $33 $24 $24 $24 $24 $24 $33 $33 $33 $33 $33 $00 $00 $00 $00 $33 $33 $33 $24 $24 $24 $33 $33 $33 $33 $33 $33 $00 $00 $00 $00 $00 $33 $33 $33 $00 $00 $33 $33 $33 $33 $33 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $2D $2D $2D $2D $00 $00 $00 $00 $00 $00 $00 $00 $00 $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $00 $00 $00 $00 $2D $2D $33 $33 $33 $2D $2D $2D $2D $33 $33 $33 $2D $2D $00 $2D $2D $24 $24 $24 $24 $33 $33 $33 $33 $24 $24 $24 $24 $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $2D $00 $00 $00 $24 $24 $24 $24 $24 $24 $24 $24 $24 $24 $00 $00 $00 $00 $00 $00 $00 $24 $24 $24 $24 $24 $24 $24 $24 $00 $00 $00 $00 $00 $33 $33 $33 $33 $33 $00 $00 $00 $00 $33 $33 $33 $33 $33 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot spritedef 30 16 16 $26 $26 $26 $26 $26 $26 $26 $26 $26 $26 $26 $26 $26 $26 $26 $26 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $2F $2F $2F $14 $2F $2F $2F $2F $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $14 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $2F $2F $2F $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $2F $14 $2F $14 $2F $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $2F $2F $2F $14 $2F $2F $2F $00 $00 $00 $00 $00 $00 $00 $00 $2F $2F $2F $14 $2F $2F $2F $14 $00 $00 $00 $00 $00 $00 $00 $00 $2F $2F $14 $2F $2F $2F $14 $2F $00 $00 $00 $00 $00 $00 $00 $00 $2F $14 $2F $14 $2F $14 $2F $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $2F $2F $2F $14 $2F $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $2F $14 $2F $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "myplot clear",
        "myplot set 0 0 sprite 1 0 4 255",
        "myplot set 64 0 sprite 1 1 4 255",
        "myplot set 128 0 sprite 1 2 4 255",
        "myplot set 192 0 sprite 1 3 4 255",
        "myplot set 0 64 sprite 1 4 4 255",
        "myplot set 64 64 sprite 1 5 4 255",
        "myplot set 128 64 sprite 1 6 4 255",
        "myplot set 192 64 sprite 1 7 4 255",
        "myplot update",
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
                    DebugWindow window = new DebugPlotWindow(new CircularBuffer(128));
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
