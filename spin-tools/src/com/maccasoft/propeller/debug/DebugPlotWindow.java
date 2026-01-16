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

import java.io.File;
import java.util.Arrays;
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
import org.eclipse.swt.graphics.ImageLoader;
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

    Point dotSize;

    Color color;
    Color textColor;
    Color backColor;
    int opacity;

    ColorMode colorMode;
    int colorTune;
    Color[] lutColors;

    int precise;
    int lineSize;

    int textSize;
    int textStyle;
    int textAngle;

    boolean polar;
    long twoPi;
    int theta;

    Sprite[] sprites;
    Image[] layer;

    boolean autoUpdate;

    Image canvasImage;

    Image image;

    class Sprite {

        int width;
        int height;
        RGB[] palette;
        byte[] pixels;
        byte[] alpha;

        public Sprite(int width, int height) {
            this.width = width;
            this.height = height;
            this.palette = new RGB[256];
            this.pixels = new byte[width * height];
            this.alpha = new byte[256];

            Arrays.fill(palette, new RGB(0, 0, 0));
        }

    }

    public DebugPlotWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        x = y = 0;
        origin = new Point(0, 0);

        dotSize = new Point(1, 1);

        color = new Color(255, 255, 255);
        textColor = new Color(255, 255, 255);
        backColor = new Color(0, 0, 0);
        opacity = 255;

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new Color[256];

        precise = 0;
        lineSize = 1;

        textSize = 10;
        textStyle = 0b00000001;
        textAngle = 0;

        polar = false;
        twoPi = 0x100000000L;
        theta = 0;

        sprites = new Sprite[256];
        layer = new Image[8];

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
                    if (iter.hasNextNumber()) {
                        dotSize.x = iter.nextNumber();
                        dotSize.y = dotSize.x;
                        if (iter.hasNextNumber()) {
                            dotSize.y = iter.nextNumber();
                        }
                    }
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

        GC imageGc = new GC(image);
        try {
            imageGc.setBackground(backColor);
            imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);
        } finally {
            imageGc.dispose();
        }

        canvasImage = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));

        GC canvasImageGc = new GC(canvasImage);
        try {
            canvasImageGc.setBackground(backColor);
            canvasImageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);
        } finally {
            canvasImageGc.dispose();
        }

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                paint(e.gc);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                for (int i = 0; i < layer.length; i++) {
                    if (layer[i] != null) {
                        layer[i].dispose();
                    }
                }
                canvasImage.dispose();
                imageGc.dispose();
                image.dispose();
            }

        });

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = imageSize.x * dotSize.x;
        gridData.heightHint = imageSize.y * dotSize.y;
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
                case "GRAY":
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
                    case "GRAY":
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
    protected void paint(GC gc) {
        Point canvasSize = canvas.getSize();

        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.OFF);

        gc.drawImage(canvasImage, 0, 0, imageSize.x, imageSize.y, 0, 0, canvasSize.x, canvasSize.y);
    }

    @Override
    public void update(KeywordIterator iter) {
        String cmd;
        Color tempColor;

        GC imageGc = new GC(image);
        try {
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
                    case "GRAY":
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
                        precise ^= 8;
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
                                x = xDirection == 0 ? (origin.x << precise) + newX : (imageSize.x << precise) - ((origin.x << precise) + newX);
                                y = yDirection == 0 ? (imageSize.y << precise) - ((origin.y << precise) + newY) : (origin.y << precise) + newY;
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
                        imageGc.fillOval((x - sizeOverride / 2) >> precise, (y - sizeOverride / 2) >> precise, sizeOverride >> precise, sizeOverride >> precise);
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
                                int dx = xDirection == 0 ? (origin.x << precise) + newX : (imageSize.x << precise) - ((origin.x << precise) + newX);
                                int dy = yDirection == 0 ? (imageSize.y << precise) - ((origin.y << precise) + newY) : (origin.y << precise) + newY;
                                int size = iter.hasNextNumber() ? iter.nextNumber() : lineSize;
                                line(imageGc, dx, dy, size, color);
                            }
                        }
                        break;

                    case "CIRCLE":
                        if (iter.hasNextNumber()) {
                            int diameter = iter.nextNumber();
                            int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : 0;
                            int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() : opacity;
                            oval(imageGc, diameter, diameter, sizeOverride, opacityOverride, color);
                        }
                        break;

                    case "OVAL":
                        if (iter.hasNextNumber()) {
                            int width = iter.nextNumber();
                            if (iter.hasNext()) {
                                int height = iter.nextNumber();
                                int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : 0;
                                int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() : opacity;
                                oval(imageGc, width, height, sizeOverride, opacityOverride, color);
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
                                box(imageGc, width, height, sizeOverride, opacityOverride, color);
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
                                        obox(imageGc, width, height, sizeOverride, radiusX, radiusY, opacityOverride, color);
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
                            text(imageGc, iter.nextString(), sizeOverride, styleOverride, textColor);
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
                                    Sprite sprite = new Sprite(xDim, yDim);

                                    int idx = 0;
                                    while (idx < sprite.pixels.length) {
                                        sprite.pixels[idx++] = (byte) iter.nextNumber();
                                    }

                                    idx = 0;
                                    while (iter.hasNextNumber() && idx < sprite.palette.length) {
                                        int c = iter.nextNumber();
                                        sprite.alpha[idx] = (byte) ((c >> 24) & 0xFF);
                                        sprite.palette[idx] = new RGB((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF);
                                        idx++;
                                    }

                                    if (id >= 0 && id < sprites.length) {
                                        sprites[id] = sprite;
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

                    case "LAYER":
                        if (iter.hasNextNumber()) {
                            int id = iter.nextNumber() - 1;
                            if (iter.hasNextString()) {
                                String fileName = iter.nextString();
                                File file = new File(getCurrentDirectory(), fileName);
                                if (file.exists() && id >= 0 && id < layer.length) {
                                    try {
                                        ImageData[] imageData = new ImageLoader().load(file.getAbsolutePath());
                                        if (imageData != null && imageData.length != 0) {
                                            if (layer[id] != null) {
                                                layer[id].dispose();
                                                layer[id] = null;
                                            }
                                            layer[id] = new Image(display, imageData[0]);
                                        }
                                    } catch (Exception e) {
                                        // Do nothing
                                    }
                                }
                            }
                        }
                        break;

                    case "CROP":
                        if (iter.hasNextNumber()) {
                            int id = iter.nextNumber() - 1;
                            if (iter.hasNext() && "AUTO".equalsIgnoreCase(iter.peekNext())) {
                                iter.next();
                                if (iter.hasNextNumber()) {
                                    int x = iter.nextNumber();
                                    if (iter.hasNextNumber()) {
                                        int y = iter.nextNumber();
                                        if (id >= 0 && id < layer.length && layer[id] != null) {
                                            Rectangle rect = layer[id].getBounds();
                                            while (y < imageSize.y) {
                                                int x1 = x;
                                                while (x1 < imageSize.x) {
                                                    imageGc.setAlpha(255);
                                                    imageGc.drawImage(layer[id], x1, y);
                                                    x1 += rect.width;
                                                }
                                                y += rect.height;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            if (iter.hasNextNumber()) {
                                int x0 = iter.nextNumber();
                                if (iter.hasNextNumber()) {
                                    int y0 = iter.nextNumber();
                                    if (iter.hasNextNumber()) {
                                        int width = iter.nextNumber();
                                        if (iter.hasNextNumber()) {
                                            int height = iter.nextNumber();

                                            int x1 = iter.hasNextNumber() ? iter.nextNumber() : x0;
                                            int y1 = iter.hasNextNumber() ? iter.nextNumber() : y0;

                                            if (id >= 0 && id < layer.length && layer[id] != null) {
                                                imageGc.setAlpha(255);
                                                imageGc.drawImage(layer[id], x0, y0, width, height, x1, y1, width, height);
                                            }
                                        }
                                    }
                                    else {
                                        if (id >= 0 && id < layer.length && layer[id] != null) {
                                            imageGc.setAlpha(255);
                                            imageGc.drawImage(layer[id], x0, y0);
                                        }
                                    }
                                }
                                break;
                            }
                            if (layer[id] != null) {
                                imageGc.setAlpha(255);
                                imageGc.drawImage(layer[id], 0, 0);
                            }
                        }
                        break;
                }
            }
        } finally {
            imageGc.dispose();
        }

        if (autoUpdate) {
            update();
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
            int b = sprite.pixels[idx++] & 0xFF;
            int alpha = sprite.alpha[b] & 0xFF;
            switch (orient) {
                case 0:
                    if (x >= imageData.width) {
                        x = 0;
                        if (++y >= imageData.height) {
                            y = 0;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
                    imageData.setPixel(x++, y, b);
                    break;
                case 1:
                    if (x < 0) {
                        x = imageData.width - 1;
                        if (++y >= imageData.height) {
                            y = 0;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
                    imageData.setPixel(x--, y, b);
                    break;
                case 2:
                    if (x >= imageData.width) {
                        x = 0;
                        if (--y < 0) {
                            y = imageData.height - 1;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
                    imageData.setPixel(x++, y, b);
                    break;
                case 3:
                    if (x < 0) {
                        x = imageData.width - 1;
                        if (--y < 0) {
                            y = imageData.height - 1;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
                    imageData.setPixel(x--, y, b);
                    break;
                case 4:
                    if (y >= imageData.height) {
                        y = 0;
                        if (++x >= imageData.width) {
                            x = 0;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
                    imageData.setPixel(x, y++, b);
                    break;
                case 5:
                    if (y < 0) {
                        y = imageData.height - 1;
                        if (++x >= imageData.width) {
                            x = 0;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
                    imageData.setPixel(x, y--, b);
                    break;
                case 6:
                    if (y >= imageData.height) {
                        y = 0;
                        if (--x < 0) {
                            x = imageData.width - 1;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
                    imageData.setPixel(x, y++, b);
                    break;
                case 7:
                    if (y < 0) {
                        y = imageData.height - 1;
                        if (--x < 0) {
                            x = imageData.width - 1;
                        }
                    }
                    imageData.setAlpha(x, y, alpha);
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

    void oval(GC imageGc, int width, int height, int lineSize, int opacity, Color color) {
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

    void box(GC imageGc, int width, int height, int lineSize, int opacity, Color color) {
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

    void obox(GC imageGc, int width, int height, int radiusX, int radiusY, int lineSize, int opacity, Color color) {
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

    void line(GC imageGc, int dx, int dy, int lineSize, Color color) {
        imageGc.setAntialias(SWT.ON);
        imageGc.setForeground(color);
        imageGc.setLineWidth(lineSize >> precise);

        imageGc.setAlpha(opacity);
        imageGc.drawLine(x >> precise, y >> precise, dx >> precise, dy >> precise);

        x = dx;
        y = dy;
    }

    void text(GC imageGc, String str, int size, int style, Color color) {
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
            int tx = x - extent.x; // left (default)
            switch (style & 0b00110000) { // horizontal justification
                case 0b00000000: // middle
                    tx = x - extent.x / 2;
                    break;
                case 0b00100000: // right
                    tx = x;
                    break;
            }
            int ty = y - extent.y; // bottom (default)
            switch (style & 0b11000000) { // vertical justification
                case 0b00000000: // middle
                    ty = y - extent.y / 2;
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
        GC canvasImageGc = new GC(canvasImage);
        try {
            canvasImageGc.drawImage(image, 0, 0);
        } finally {
            canvasImageGc.dispose();
        }
        canvas.redraw();
    }

    static String[] data = new String[] {
        "title 'Test' size 640 480 hidexy update",
        "clear",
        "set 320 0 line 320 480",
        "set 0 240 line 640 240",
        "set 320 240 yellow text 14 %0000_0011 'CENTERED'",
        "set 160 240 yellow text 14 %1100_0001 'TOP'",
        "set 480 240 yellow text 14 %1000_0010 'BOTTOM'",
        "set 320 120 yellow text 14 %0011_0000 'LEFT'",
        "set 320 360 yellow text 14 %0010_0000 'RIGHT'",
        "update",
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
