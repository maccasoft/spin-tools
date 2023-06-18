/*
 * Copyright (c) 2018 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

public class LineNumbersRuler {

    Canvas canvas;
    GridData layoutData;
    FontMetrics fontMetrics;

    StyledText text;

    int leftMargin;
    int rightMargin;

    private int scrollBarSelection;
    private int lineCount;

    private Color highlightForeground;
    private Set<Integer> highlight = new HashSet<Integer>();

    final PaintListener paintListener = new PaintListener() {

        @Override
        public void paintControl(PaintEvent e) {
            if (text != null) {
                onPaintControl(e.gc);
            }
        }
    };

    final PaintListener textPaintListener = new PaintListener() {

        @Override
        public void paintControl(PaintEvent e) {
            ScrollBar scrollBar = text.getVerticalBar();
            if (scrollBarSelection != scrollBar.getSelection() || lineCount != text.getLineCount()) {
                canvas.redraw();
                scrollBarSelection = scrollBar.getSelection();
                lineCount = text.getLineCount();
            }
        }
    };

    public LineNumbersRuler(Composite parent) {
        canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.NO_FOCUS);
        canvas.setLayoutData(layoutData = new GridData(SWT.FILL, SWT.FILL, false, true));
        canvas.addPaintListener(paintListener);

        GC gc = new GC(canvas);
        fontMetrics = gc.getFontMetrics();
        gc.dispose();

        leftMargin = rightMargin = 5;

        layoutData.widthHint = (int) Math.round(leftMargin + fontMetrics.getAverageCharacterWidth() * 5 + rightMargin);

        scrollBarSelection = lineCount = -1;

        highlightForeground = new Color(0xF0, 0x00, 0x00);
    }

    public void setText(StyledText text) {
        this.text = text;
        this.text.addPaintListener(textPaintListener);
    }

    void onPaintControl(GC gc) {
        Color foreground = canvas.getForeground();
        Rectangle rect = canvas.getClientArea();

        gc.setClipping(0, text.getTopMargin(), rect.width, rect.height - text.getTopMargin() - text.getBottomMargin());

        int lineNumber = text.getTopIndex() - 1;
        if (lineNumber < 0) {
            lineNumber = 0;
        }
        while (lineNumber < text.getLineCount()) {
            int y = text.getLinePixel(lineNumber);
            if (y >= rect.height) {
                break;
            }
            String s = Integer.toString(lineNumber + 1);
            gc.setForeground(highlight.contains(lineNumber) ? highlightForeground : foreground);
            gc.drawString(s, rect.width - gc.stringExtent(s).x - rightMargin, y);
            lineNumber++;
        }
    }

    public void setFont(Font font) {
        canvas.setFont(font);

        GC gc = new GC(canvas);
        fontMetrics = gc.getFontMetrics();
        gc.dispose();

        layoutData.widthHint = (int) Math.round(leftMargin + fontMetrics.getAverageCharacterWidth() * 4 + rightMargin);

        canvas.redraw();
    }

    public void setVisible(boolean visible) {
        canvas.setVisible(visible);
        layoutData.exclude = !visible;
    }

    public void setHighlight(int line) {
        highlight.add(line - 1);
    }

    public void clearHighlights() {
        highlight.clear();
    }

    public void redraw() {
        canvas.redraw();
    }

    public void setBackground(Color color) {
        canvas.setBackground(color);
        canvas.redraw();
    }

    public void setForeground(Color color) {
        canvas.setForeground(color);
        canvas.redraw();
    }

    public void setHighlightForeground(Color color) {
        highlightForeground = color;
        canvas.redraw();
    }

}
