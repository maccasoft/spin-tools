/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.swt.graphics.Transform;
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

    Image image;
    GC imageGc;
    FontDescriptor defaultFontDescriptor;
    Transform textTransform;

    ImageData canvasImageData;
    Image canvasImage;

    static class Sprite {

        ImageData imageData;
        PaletteData paletteData;
        byte[] alphaData;

        Map<String, Image> imageCache = new HashMap<>();

        public Sprite(int width, int height) {
            paletteData = new PaletteData(new RGB[256]);
            Arrays.fill(paletteData.colors, new RGB(0, 0, 0));

            alphaData = new byte[256];

            imageData = new ImageData(width, height, 8, paletteData, 4, new byte[width * height]);
        }

        Image getImage(int orient, int scale) {
            String key = String.format("%d-%d", orient, scale);

            Image scaledImage = imageCache.get(key);
            if (scaledImage == null) {
                scaledImage = new Image(null, transformImage(orient, scale));
                imageCache.put(key, scaledImage);
            }

            return scaledImage;
        }

        ImageData transformImage(int orient, int scale) {
            ImageData scaledImageData = new ImageData(imageData.width * scale, imageData.height * scale, 8, paletteData, 4, new byte[imageData.width * scale * imageData.height * scale]);

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
                    x = imageData.width - 1;
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
                    y = imageData.height - 1;
                    break;
            }

            int idx = 0;
            while (idx < imageData.data.length) {
                int b = imageData.data[idx++] & 0xFF;
                int alpha = alphaData[b] & 0xFF;
                switch (orient) {
                    case 0:
                        if (x >= imageData.width) {
                            x = 0;
                            if (++y >= imageData.height) {
                                y = 0;
                            }
                        }
                        setPixel(scaledImageData, x++, y, b, alpha, scale);
                        break;
                    case 1:
                        if (x < 0) {
                            x = imageData.width - 1;
                            if (++y >= imageData.height) {
                                y = 0;
                            }
                        }
                        setPixel(scaledImageData, x--, y, b, alpha, scale);
                        break;
                    case 2:
                        if (x >= imageData.width) {
                            x = 0;
                            if (--y < 0) {
                                y = imageData.height - 1;
                            }
                        }
                        setPixel(scaledImageData, x++, y, b, alpha, scale);
                        break;
                    case 3:
                        if (x < 0) {
                            x = imageData.width - 1;
                            if (--y < 0) {
                                y = imageData.height - 1;
                            }
                        }
                        setPixel(scaledImageData, x--, y, b, alpha, scale);
                        break;
                    case 4:
                        if (y >= imageData.height) {
                            y = 0;
                            if (++x >= imageData.width) {
                                x = 0;
                            }
                        }
                        setPixel(scaledImageData, x, y++, b, alpha, scale);
                        break;
                    case 5:
                        if (y < 0) {
                            y = imageData.height - 1;
                            if (++x >= imageData.width) {
                                x = 0;
                            }
                        }
                        setPixel(scaledImageData, x, y--, b, alpha, scale);
                        break;
                    case 6:
                        if (y >= imageData.height) {
                            y = 0;
                            if (--x < 0) {
                                x = imageData.width - 1;
                            }
                        }
                        setPixel(scaledImageData, x, y++, b, alpha, scale);
                        break;
                    case 7:
                        if (y < 0) {
                            y = imageData.height - 1;
                            if (--x < 0) {
                                x = imageData.width - 1;
                            }
                        }
                        setPixel(scaledImageData, x, y--, b, alpha, scale);
                        break;
                }
            }

            return scaledImageData;
        }

        void setPixel(ImageData imageData, int x, int y, int b, int alpha, int scale) {
            int py = y * scale;
            for (int cy = 0; cy < scale; cy++, py++) {
                int px = x * scale;
                for (int cx = 0; cx < scale; cx++, px++) {
                    imageData.setAlpha(px, py, alpha);
                    imageData.setPixel(px, py, b);
                }
            }
        }

        void dispose() {
            for (Map.Entry<String, Image> entry : imageCache.entrySet()) {
                entry.getValue().dispose();
            }
            imageCache.clear();
        }

    }

    public DebugPlotWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        defaultFontDescriptor = JFaceResources.getDefaultFontDescriptor();
        textTransform = new Transform(display);

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
                        dotSize.x = dotSize.y = iter.nextNumber();
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

        imageGc = new GC(image);
        imageGc.setBackground(backColor);
        imageGc.fillRectangle(0, 0, imageSize.x, imageSize.y);
        imageGc.setAntialias(SWT.ON);

        canvasImageData = image.getImageData();

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                if (pendingRedraw.getAndSet(false)) {
                    if (canvasImage != null) {
                        canvasImage.dispose();
                    }
                    canvasImage = new Image(display, canvasImageData);
                }
                paint(e.gc);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                synchronized (DebugPlotWindow.this) {
                    imageGc.dispose();
                }
                for (int i = 0; i < sprites.length; i++) {
                    if (sprites[i] != null) {
                        sprites[i].dispose();
                    }
                }
                for (int i = 0; i < layer.length; i++) {
                    if (layer[i] != null) {
                        layer[i].dispose();
                    }
                }
                if (canvasImage != null) {
                    canvasImage.dispose();
                }
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
        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.OFF);

        Point canvasSize = canvas.getSize();
        gc.drawImage(canvasImage, 0, 0, imageSize.x, imageSize.y, 0, 0, canvasSize.x, canvasSize.y);
    }

    @Override
    public void update(KeywordIterator iter) {
        String cmd;
        Color tempColor;

        synchronized (DebugPlotWindow.this) {
            imageGc.setAlpha(255);

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
                                        int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : lineSize;
                                        int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() : opacity;
                                        obox(imageGc, width, height, radiusX, radiusY, sizeOverride, opacityOverride, color);
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
                        int sizeOverride = iter.hasNextNumber() ? iter.nextNumber() : textSize;
                        int styleOverride = iter.hasNextNumber() ? iter.nextNumber() : textStyle;
                        int angleOverride = iter.hasNextNumber() ? iter.nextNumber() : textAngle;
                        if (iter.hasNextString()) {
                            text(iter.nextString(), sizeOverride, styleOverride, angleOverride, textColor);
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
                                    while (idx < sprite.imageData.data.length) {
                                        sprite.imageData.data[idx++] = (byte) iter.nextNumber();
                                    }

                                    idx = 0;
                                    while (iter.hasNextNumber() && idx < sprite.paletteData.colors.length) {
                                        int c = iter.nextNumber();
                                        sprite.alphaData[idx] = (byte) ((c >> 24) & 0xFF);
                                        sprite.paletteData.colors[idx] = new RGB((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF);
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
                            int orient = iter.hasNextNumber() ? iter.nextNumber() & 7 : 0;
                            int scale = iter.hasNextNumber() ? iter.nextNumber() & 63 : 1;
                            int opacityOverride = iter.hasNextNumber() ? iter.nextNumber() & 255 : opacity;
                            if (id >= 0 && id < sprites.length && sprites[id] != null && scale >= 1) {
                                Image image = sprites[id].getImage(orient, scale);
                                imageGc.setAlpha(opacityOverride);
                                imageGc.drawImage(image, x, y);
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
                        close();
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
        }

        if (autoUpdate) {
            update();
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

    void oval(GC gc, int width, int height, int lineSize, int opacity, Color color) {
        gc.setAlpha(opacity);
        gc.setLineWidth(lineSize);
        gc.setForeground(color);

        width -= lineSize * 2;
        height -= lineSize * 2;

        if (lineSize == 0) {
            gc.setBackground(color);
            gc.fillOval(x - width / 2, y - height / 2, width, height);
        }
        gc.drawOval(x - width / 2, y - height / 2, width, height);
    }

    void box(GC gc, int width, int height, int lineSize, int opacity, Color color) {
        gc.setAlpha(opacity);
        gc.setLineWidth(lineSize);
        gc.setForeground(color);
        if (lineSize == 0) {
            gc.setBackground(color);
            gc.fillRectangle(x - width / 2, y - height / 2, width, height);
        }
        gc.drawRectangle(x - width / 2, y - height / 2, width, height);
    }

    void obox(GC gc, int width, int height, int radiusX, int radiusY, int lineSize, int opacity, Color color) {
        gc.setAlpha(opacity);
        gc.setLineWidth(lineSize);
        gc.setForeground(color);
        if (lineSize == 0) {
            gc.setBackground(color);
            gc.fillRectangle(x - width / 2, y - height / 2, width, height);
        }
        gc.drawRoundRectangle(x - width / 2, y - height / 2, width, height, radiusX, radiusY);
    }

    void line(GC gc, int dx, int dy, int lineSize, Color color) {
        gc.setForeground(color);
        gc.setLineWidth(lineSize >> precise);

        gc.setAlpha(opacity);
        gc.drawLine(x >> precise, y >> precise, dx >> precise, dy >> precise);

        x = dx;
        y = dy;
    }

    void text(String str, int size, int style, int angle, Color color) {
        int lineWidth = 0;

        imageGc.setForeground(color);

        FontDescriptor fontDescriptor = defaultFontDescriptor;

        switch (style & 0b11) {
            case 0b00:
            case 0b01:
                break;
            case 0b10:
            case 0b11:
                fontDescriptor = fontDescriptor.withStyle(SWT.BOLD);
                lineWidth = 2;
                break;
        }
        if ((style & 0b100) != 0) {
            fontDescriptor = fontDescriptor.withStyle(SWT.ITALIC);
        }
        fontDescriptor = fontDescriptor.setHeight(size - 1);

        Font font = fontDescriptor.createFont(display);
        try {
            imageGc.setFont(font);

            textTransform.identity();
            textTransform.translate(x, y);
            textTransform.rotate(angle);
            imageGc.setTransform(textTransform);

            Point extent = imageGc.stringExtent(str);
            int tx = -extent.x; // left (default)
            switch (style & 0b00110000) { // horizontal justification
                case 0b00000000: // middle
                    tx = -extent.x / 2;
                    break;
                case 0b00100000: // right
                    tx = 0;
                    break;
            }
            int ty = -extent.y; // bottom (default)
            switch (style & 0b11000000) { // vertical justification
                case 0b00000000: // middle
                    ty = -extent.y / 2;
                    break;
                case 0b11000000: // top
                    ty = 0;
                    break;
            }

            imageGc.drawText(str, tx, ty, true);
            if ((style & 0b1000) != 0) { // underline
                imageGc.setLineWidth(lineWidth);
                imageGc.drawLine(tx, ty + extent.y, tx + extent.x, ty + extent.y);
            }
        } finally {
            font.dispose();
        }

        imageGc.setTransform(null);
    }

    protected void update() {
        if (image.isDisposed()) {
            return;
        }
        canvasImageData = image.getImageData();
        super.update();
    }

    static String[] data = new String[] {
        "size 384 384 update",
        "cartesian 1",
        "spritedef 0 16 16 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $1F $1F $18 $18 $19 $00 $00 $00 $00 $00 $00 $00 $00 $19 $1F $1F $1F $1F $1F $18 $19 $00 $00 $00 $00 $00 $00 $00 $19 $21 $1F $21 $21 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $19 $21 $21 $21 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $19 $21 $21 $19 $19 $11 $11 $11 $11 $19 $19 $19 $00 $00 $00 $19 $19 $19 $19 $19 $11 $09 $09 $19 $09 $19 $00 $00 $00 $00 $00 $19 $09 $09 $19 $19 $11 $09 $09 $19 $09 $19 $19 $19 $00 $00 $00 $19 $09 $09 $19 $19 $19 $11 $09 $09 $09 $08 $08 $08 $19 $00 $00 $19 $11 $09 $11 $19 $11 $09 $19 $11 $09 $09 $09 $09 $19 $00 $00 $00 $19 $11 $11 $11 $09 $19 $19 $19 $11 $11 $11 $19 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "spritedef 1 16 16 $00 $00 $00 $19 $19 $11 $11 $09 $09 $19 $19 $19 $19 $00 $00 $00 $00 $00 $19 $19 $19 $19 $11 $11 $11 $11 $11 $19 $00 $00 $00 $00 $00 $00 $19 $21 $1F $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $19 $21 $1F $18 $1F $19 $19 $03 $03 $03 $19 $00 $00 $00 $00 $00 $19 $21 $1F $18 $1F $19 $08 $03 $03 $03 $03 $19 $00 $00 $00 $00 $19 $21 $1F $1F $18 $19 $08 $08 $03 $03 $03 $19 $00 $00 $00 $00 $19 $19 $21 $1F $1F $1F $19 $08 $08 $08 $19 $19 $19 $00 $00 $00 $19 $19 $19 $21 $21 $21 $19 $19 $19 $19 $0F $03 $19 $00 $00 $00 $00 $19 $17 $19 $19 $19 $15 $03 $03 $0F $0F $03 $19 $00 $00 $00 $00 $19 $17 $17 $17 $15 $15 $15 $0F $0F $15 $15 $19 $00 $00 $00 $00 $19 $17 $17 $15 $15 $15 $15 $15 $15 $15 $19 $00 $00 $00 $00 $00 $19 $17 $17 $17 $17 $17 $17 $17 $17 $17 $19 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $00 $00 $19 $13 $13 $12 $12 $07 $19 $00 $00 $00 $00 $00 $00 $00 $00 $00 $19 $13 $13 $13 $12 $12 $07 $19 $00 $00 $00 $00 $00 $00 $00 $00 $19 $19 $19 $19 $19 $19 $19 $19 $00 $00 $00 $00 $00 $0000_0000 $FFF8_F8F8 $FFF8_F8F8 $FFF8_F8F8 $FFF8_D898 $FFF8_D890 $FFF8_D848 $FFF0_D848 $FFF8_C890 $FFF0_A070 $FFF8_9068 $FFF8_9838 $FFF8_9818 $FFD8_9048 $FF68_B0D8 $FF60_A8E0 $FF48_90B0 $FFF0_8868 $FFE8_9048 $FFB8_6800 $FFB0_6800 $FF50_88B8 $FF48_6890 $FF40_6090 $FFF8_3050 $FF30_3030 $FFF8_2048 $FFD8_2800 $FFF8_0808 $FF20_2020 $FFD8_0020 $FFD8_0028 $FFF8_F8F8 $FFD8_F8F8 $FFF8_D800 $FFA0_E820 $FFF0_D0B0 $FFF8_D088 $FFF8_C8C0 $FFF8_A830 $FF80_C030 $FFF8_9020 $FFD0_A000 $FFA0_7800 $FF58_9800 $FFE0_5810 $FFF8_4848 $FF98_4800 $FF30_3030 $FFF8_2820 $FFD0_0020 $FF20_2020",
        "clear",
        "set 0, 0 sprite 0 0 8",
        "set 0, 128 sprite 0 0 8",
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
