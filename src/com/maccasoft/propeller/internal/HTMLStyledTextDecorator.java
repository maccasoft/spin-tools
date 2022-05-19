/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class HTMLStyledTextDecorator extends ParserCallback {

    private StyledText styledText;

    int bold;
    int italic;
    int underline;
    int code;

    FontData fontData;
    Map<Integer, Font> fonts;

    private HTMLEditorKit.Parser parser;

    int currentPosition;
    int lineWidth;

    StringBuilder sb = new StringBuilder();
    List<StyleRange> styles = new ArrayList<StyleRange>();

    public HTMLStyledTextDecorator(StyledText styledText) {
        this.parser = new ParserDelegator();
        this.styledText = styledText;

        this.fonts = new HashMap<Integer, Font>();

        Font font = styledText.getFont();
        fontData = font.getFontData()[0];

        styledText.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                fonts.values().forEach((f) -> f.dispose());
            }
        });
    }

    Font getCodeFont(int style) {
        Font font = fonts.get(style);
        if (font == null) {
            if ("win32".equals(SWT.getPlatform())) {
                font = new Font(styledText.getDisplay(), "Courier New", fontData.getHeight() - 1, style);
            }
            else {
                font = new Font(styledText.getDisplay(), "mono", fontData.getHeight() - 1, style);
            }
            fonts.put(style, font);
        }
        return font;
    }

    public int getLineSize() {
        return lineWidth;
    }

    @Override
    public void handleStartTag(final Tag t, final MutableAttributeSet a, final int pos) {
        if (t == Tag.P) {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                sb.append("\n");
            }
            sb.append("\n");
        }
        else if (t == Tag.B) {
            applyStyle();
            bold++;
        }
        else if (t == Tag.I) {
            applyStyle();
            italic++;
        }
        else if (t == Tag.U) {
            applyStyle();
            underline++;
        }
        else if (t == Tag.CODE) {
            applyStyle();
            code++;
        }
    }

    @Override
    public void handleEndTag(final Tag t, final int pos) {
        if (t == Tag.P) {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                sb.append("\n");
            }
            sb.append("\n");
        }
        else if (t == Tag.B) {
            applyStyle();
            bold--;
        }
        else if (t == Tag.I) {
            applyStyle();
            italic--;
        }
        else if (t == Tag.U) {
            applyStyle();
            underline--;
        }
        else if (t == Tag.CODE) {
            applyStyle();
            code--;
        }
    }

    void applyStyle() {
        if (currentPosition == sb.length()) {
            return;
        }
        StyleRange range = new StyleRange();
        range.start = currentPosition;
        range.length = sb.length() - currentPosition;
        if (bold > 0) {
            range.fontStyle |= SWT.BOLD;
        }
        if (italic > 0) {
            range.fontStyle |= SWT.ITALIC;
        }
        range.underline = underline > 0;
        if (code > 0) {
            range.font = getCodeFont(range.fontStyle);
        }
        styles.add(range);
        currentPosition = sb.length();

        if (code > 0) {
            GC gc = new GC(styledText);
            try {
                gc.setFont(range.font);
                String s = sb.substring(range.start);
                Point p = gc.textExtent(s);
                lineWidth = Math.max(lineWidth, p.x);
                gc.dispose();
            } catch (Exception e) {
                // Do nothing
            }
        }
    }

    @Override
    public void handleError(final String errorMsg, final int pos) {

    }

    @Override
    public void handleText(final char[] data, final int pos) {
        sb.append(data);
    }

    @Override
    public void handleSimpleTag(final Tag t, final MutableAttributeSet a, final int pos) {
        if (t == Tag.BR) {
            sb.append("\n");
        }
    }

    public void setText(String text) {
        sb.setLength(0);
        styles.clear();
        lineWidth = 0;
        currentPosition = 0;
        try {
            parser.parse(new StringReader(text), this, true);
            if (currentPosition != sb.length() && (bold > 0 || italic > 0 || underline > 0 || code > 0)) {
                applyStyle();
            }
            styledText.setText(sb.toString());
            styledText.setStyleRanges(styles.toArray(new StyleRange[styles.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            styledText.setText(text);
        }
    }
}
