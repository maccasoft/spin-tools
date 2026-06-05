/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Resource;

import com.maccasoft.propeller.Preferences;

public class HTMLStyledTextDecorator {

    static final Pattern tagPattern = Pattern.compile("<(\\S*)(.*?)>");
    static final Pattern attrValueAll = Pattern.compile("([\\w:\\-]+)(\\s*=\\s*(\"(.*?)\"|'(.*?)'|([^ ]*))|(\\s+|\\z))");

    static class Attributes {

        boolean keepFormat;

        String fontName;
        Integer fontHeight;
        Integer fontStyle;
        Boolean underline;

        Color foreground;

        int spacesBefore;

        public Attributes() {

        }

        public Attributes(Attributes parent) {
            this.keepFormat = parent.keepFormat;

            this.fontName = parent.fontName;
            this.fontHeight = parent.fontHeight;
            this.fontStyle = parent.fontStyle;

            this.foreground = parent.foreground;
        }

    }

    StyledText styledText;

    FontData fontData;
    FontData fixedFontData;
    Map<String, Font> fonts;

    List<Attributes> attributes;

    int currentPosition;
    int lineWidth;

    StringBuilder sb = new StringBuilder();
    List<StyleRange> styles = new ArrayList<>();

    public HTMLStyledTextDecorator(StyledText styledText) {
        this.styledText = styledText;
        this.styledText.setWordWrap(true);

        this.attributes = new ArrayList<>();
        this.fonts = new HashMap<>();
        this.fontData = styledText.getFont().getFontData()[0];

        Preferences preferences = Preferences.getInstance();
        if (preferences.getEditorFont() != null) {
            this.fixedFontData = StringConverter.asFontData(preferences.getEditorFont());
        }
        else {
            this.fixedFontData = JFaceResources.getTextFont().getFontData()[0];
        }

        styledText.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                fonts.values().forEach(Resource::dispose);
            }
        });
    }

    public void setText(String htmlText) {
        sb.setLength(0);
        styles.clear();

        lineWidth = 0;
        currentPosition = 0;

        try {
            char previousChar = ' ';
            boolean keepFormat = false;

            int index = 0;
            while (index < htmlText.length()) {
                char ch = htmlText.charAt(index);
                switch (ch) {
                    case '&':
                        index = parseMnemonic(index, htmlText);
                        previousChar = ' ';
                        break;
                    case '<':
                        applyStyle();
                        index = parseTag(index, htmlText);
                        previousChar = ' ';
                        keepFormat = !attributes.isEmpty() && attributes.getLast().keepFormat;
                        break;
                    default:
                        if (!keepFormat && Character.isWhitespace(ch)) {
                            if (Character.isWhitespace(previousChar)) {
                                break;
                            }
                            ch = ' ';
                        }
                        sb.append(ch);
                        previousChar = ch;
                        break;
                }
                index++;
            }

            applyStyle();

            styledText.setText(sb.toString());
            styledText.setStyleRanges(styles.toArray(new StyleRange[0]));
        } catch (Exception e) {
            e.printStackTrace();
            styledText.setText(htmlText);
        }
    }

    int parseMnemonic(int index, String htmlText) {
        int startIndex = index++;

        while (index < htmlText.length()) {
            char ch = htmlText.charAt(index);
            if (ch == ';') {
                break;
            }
            index++;
        }

        String tag = htmlText.substring(startIndex, index + 1);
        switch (tag) {
            case "&amp;":
                sb.append('&');
                break;
            case "&lt;":
                sb.append('<');
                break;
            case "&gt;":
                sb.append('>');
                break;
            case "&nbsp;":
                sb.append(' ');
                break;
            default:
                sb.append(tag);
                break;
        }

        return index;
    }

    int parseTag(int index, String htmlText) {
        int startIndex = index++;

        while (index < htmlText.length()) {
            char ch = htmlText.charAt(index);
            if (ch == '>') {
                break;
            }
            index++;
        }

        String tag = htmlText.substring(startIndex, index + 1);

        if (!tag.startsWith("</") && !tag.endsWith("/>")) {
            Attributes attrs = attributes.isEmpty() ? new Attributes() : new Attributes(attributes.getLast());

            int start = tag.indexOf(' ');
            int end = tag.lastIndexOf("/>");
            if (end == -1) {
                end = tag.lastIndexOf(">");
            }
            if (start != -1 && end != -1) {
                Matcher attrMatcher = attrValueAll.matcher(tag.substring(start + 1, end).trim());
                while (attrMatcher.find()) {
                    String key = attrMatcher.group(1);
                    String value = attrMatcher.group(4);
                    if ("class".equals(key)) {
                        if (value.contains("header")) {
                            if (!attrs.keepFormat) {
                                while (index + 1 < htmlText.length()) {
                                    if (!Character.isWhitespace(htmlText.charAt(index + 1))) {
                                        break;
                                    }
                                    index++;
                                }
                            }
                            attrs.keepFormat = true;
                            attrs.fontName = fixedFontData.getName();
                            attrs.fontHeight = fixedFontData.getHeight() + 1;
                            if (attrs.fontStyle == null) {
                                attrs.fontStyle = SWT.NORMAL;
                            }
                            attrs.fontStyle |= SWT.BOLD;
                        }
                        if (value.contains("code")) {
                            if (!attrs.keepFormat) {
                                while (index + 1 < htmlText.length()) {
                                    if (!Character.isWhitespace(htmlText.charAt(index + 1))) {
                                        break;
                                    }
                                    index++;
                                }
                            }
                            attrs.keepFormat = true;
                            attrs.fontName = fixedFontData.getName();
                            attrs.fontHeight = fixedFontData.getHeight();
                            attrs.spacesBefore += 1;
                        }
                        if (value.contains("subtitle")) {
                            if (attrs.fontStyle == null) {
                                attrs.fontStyle = SWT.NORMAL;
                            }
                            attrs.fontStyle |= SWT.BOLD;
                        }
                        if (value.contains("param") || value.contains("returns")) {
                            if (attrs.fontStyle == null) {
                                attrs.fontStyle = SWT.NORMAL;
                            }
                            attrs.fontStyle |= SWT.BOLD;
                            attrs.spacesBefore += 1;
                        }
                        if (value.contains("skip")) {
                            attrs.foreground = new Color(0xC0, 0xC0, 0xC0);
                        }
                    }
                }
            }

            if (tag.equals("<b>")) {
                if (attrs.fontStyle == null) {
                    attrs.fontStyle = SWT.NORMAL;
                }
                attrs.fontStyle |= SWT.BOLD;
            }
            if (tag.startsWith("<pre")) {
                attrs.keepFormat = true;
                attrs.fontName = fixedFontData.getName();
                attrs.fontHeight = fixedFontData.getHeight();
            }

            attributes.add(attrs);
        }

        boolean hasEol = false;
        boolean hasDoubleEol = false;

        if (!sb.isEmpty()) {
            int lastIndex = sb.length() - 1;
            hasEol = sb.charAt(lastIndex) == '\r' || sb.charAt(lastIndex) == '\n';
            if (hasEol && lastIndex > 0) {
                if (sb.charAt(lastIndex) == '\n' && sb.charAt(lastIndex - 1) == '\r') {
                    lastIndex--;
                }
                if (lastIndex > 0) {
                    hasDoubleEol = sb.charAt(lastIndex - 1) == '\r' || sb.charAt(lastIndex - 1) == '\n';
                }
            }
        }

        //sb.append(tag);
        if (tag.startsWith("</")) {
            if (!attributes.isEmpty()) {
                attributes.removeLast();
            }
            if (!sb.isEmpty()) {
                if (tag.startsWith("</div")) {
                    if (!hasEol) {
                        sb.append(System.lineSeparator());
                    }
                }
                else if (tag.startsWith("</p")) {
                    if (!hasEol) {
                        sb.append(System.lineSeparator()).append(System.lineSeparator());
                    }
                    else if (!hasDoubleEol) {
                        sb.append(System.lineSeparator());
                    }
                }
            }
        }
        else if (tag.startsWith("<p")) {
            if (!sb.isEmpty()) {
                if (!hasEol) {
                    sb.append(System.lineSeparator()).append(System.lineSeparator());
                }
                else if (!hasDoubleEol) {
                    sb.append(System.lineSeparator());
                }
            }
        }
        else if (tag.startsWith("<br")) {
            sb.append(System.lineSeparator());
        }

        return index;
    }

    Font getFont(String swtFontName, int swtHeight, int swtStyle) {
        String key = swtFontName + "-" + swtHeight;
        if ((swtStyle & SWT.BOLD) != 0) {
            key += "-bold";
        }
        if ((swtStyle & SWT.ITALIC) != 0) {
            key += "-italic";
        }

        Font font = fonts.get(key);
        if (font == null) {
            font = new Font(styledText.getDisplay(), swtFontName, swtHeight, swtStyle);
            fonts.put(key, font);
        }
        return font;
    }

    void applyStyle() {
        StyleRange range = new StyleRange();
        range.start = currentPosition;
        range.length = sb.length() - currentPosition;

        if (!attributes.isEmpty()) {
            Attributes attrs = attributes.getLast();
            range.font = getFont(
                attrs.fontName != null ? attrs.fontName : fontData.getName(),
                attrs.fontHeight != null ? attrs.fontHeight : fontData.getHeight(),
                attrs.fontStyle != null ? attrs.fontStyle : SWT.NORMAL);
            range.foreground = attrs.foreground;
            if (attrs.underline != null) {
                range.underline = attrs.underline;
            }
        }

        styles.add(range);
        currentPosition = sb.length();

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
