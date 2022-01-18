/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class HTMLStyledTextParser extends ParserCallback {

    private final List<StyleRange> listOfStyles;
    private final StyledText styledText;
    private final StringBuilder outputString;
    private StyleRange currentStyleRange;
    private TagType currentTagType;
    private int currentPosition;
    private final HTMLEditorKit.Parser parser;

    private Font fixedFont;

    private enum TagType {
        B, U, I, CODE
    };

    public HTMLStyledTextParser(StyledText styledText) {
        this.parser = new ParserDelegator();
        this.styledText = styledText;
        this.listOfStyles = new ArrayList<StyleRange>();
        this.outputString = new StringBuilder();

        Font font = styledText.getFont();
        FontData fontData = font.getFontData()[0];
        if ("win32".equals(SWT.getPlatform())) {
            fixedFont = new Font(styledText.getDisplay(), "Courier New", fontData.getHeight() - 1, SWT.NONE);
        }
        else {
            fixedFont = new Font(styledText.getDisplay(), "mono", fontData.getHeight() - 1, SWT.NONE);
        }
    }

    @Override
    public void handleStartTag(final Tag t, final MutableAttributeSet a, final int pos) {
        if (t == Tag.P) {
            if (outputString.length() > 0 && outputString.charAt(outputString.length() - 1) != '\n') {
                outputString.append("\n");
            }
            outputString.append("\n");
        }
        else if (t == Tag.B) {
            currentStyleRange = new StyleRange();
            currentTagType = TagType.B;
            currentPosition = outputString.length();
        }
        else if (t == Tag.I) {
            currentStyleRange = new StyleRange();
            currentTagType = TagType.I;
            currentPosition = outputString.length();
        }
        else if (t == Tag.U) {
            currentStyleRange = new StyleRange();
            currentTagType = TagType.U;
            currentPosition = outputString.length();
        }
        else if (t == Tag.CODE) {
            currentStyleRange = new StyleRange();
            currentStyleRange.font = fixedFont;
            currentTagType = TagType.CODE;
            currentPosition = outputString.length();
        }
    }

    @Override
    public void handleEndTag(final Tag t, final int pos) {
        if (t == Tag.P) {
            if (outputString.length() > 0 && outputString.charAt(outputString.length() - 1) != '\n') {
                outputString.append("\n");
            }
            outputString.append("\n");
        }
        if (t != Tag.B && t != Tag.I && t != Tag.U && t != Tag.CODE) {
            return;
        }
        int style = SWT.NORMAL;
        boolean underline = false;
        if (t == Tag.B) {
            if (TagType.B != this.currentTagType) {
                throw new RuntimeException("Error parsing [" + this.styledText.getText() + "] : bad syntax");
            }
            style = SWT.BOLD;
        }
        else if (t == Tag.I) {
            if (TagType.I != this.currentTagType) {
                throw new RuntimeException("Error parsing [" + this.styledText.getText() + "] : bad syntax");
            }
            style = SWT.ITALIC;
        }
        else if (t == Tag.U) {
            if (TagType.U != this.currentTagType) {
                throw new RuntimeException("Error parsing [" + this.styledText.getText() + "] : bad syntax");
            }
            style = SWT.NORMAL;
        }
        else if (t == Tag.CODE) {
            if (TagType.CODE != this.currentTagType) {
                throw new RuntimeException("Error parsing [" + this.styledText.getText() + "] : bad syntax");
            }
            style = SWT.BOLD;
        }
        if (currentStyleRange != null) {
            currentStyleRange.start = currentPosition;
            currentStyleRange.length = outputString.length() - currentPosition;
            currentStyleRange.fontStyle = style;
            currentStyleRange.underline = underline;
            listOfStyles.add(this.currentStyleRange);
            currentStyleRange = null;
            currentTagType = null;
        }
    }

    @Override
    public void handleError(final String errorMsg, final int pos) {

    }

    @Override
    public void handleText(final char[] data, final int pos) {
        this.outputString.append(data);
    }

    @Override
    public void handleSimpleTag(final Tag t, final MutableAttributeSet a, final int pos) {
        if (t == Tag.BR) {
            outputString.append("\n");
        }
    }

    public void setText(String text) {
        listOfStyles.clear();
        currentStyleRange = null;
        currentTagType = null;
        currentPosition = 0;
        outputString.setLength(0);
        try {
            parser.parse(new StringReader(text), this, true);
            styledText.setText(outputString.toString());
            styledText.setStyleRanges(listOfStyles.toArray(new StyleRange[listOfStyles.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            styledText.setText(text);
        }
    }
}
