/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.internal.ColorRegistry;

public class OverviewRuler {

    final static int HEIGHT = 5;

    Canvas canvas;

    StyledText styledText;

    private Color errorColor;
    private Color warningColor;
    private Map<Integer, Color> highlight = new HashMap<Integer, Color>();

    public OverviewRuler(Composite parent) {
        canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.NO_FOCUS);
        canvas.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        canvas.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                if (styledText == null) {
                    return;
                }
                Rectangle rect = canvas.getClientArea();
                int lineCount = styledText.getLineCount();
                float lineStep = (float) (rect.height - HEIGHT) / lineCount;

                for (int line : highlight.keySet()) {
                    int y = (int) (line * lineStep);
                    e.gc.setBackground(highlight.get(line));
                    e.gc.fillRectangle(0, y, rect.width, HEIGHT);
                }
            }
        });
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                Rectangle rect = canvas.getClientArea();
                int lineCount = styledText.getLineCount();

                float lineStep = (float) (rect.height - HEIGHT) / lineCount;
                for (int line : highlight.keySet()) {
                    int y = (int) (line * lineStep);
                    if (e.y >= y && e.y < y + HEIGHT) {
                        goToLine(line);
                        return;
                    }
                }

                lineStep = (float) lineCount / (rect.height - HEIGHT);
                goToLine((int) (e.y * lineStep) + 1);
            }

        });
        canvas.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseHover(MouseEvent e) {
                Rectangle rect = canvas.getClientArea();
                int lineCount = styledText.getLineCount();
                float lineStep = (float) lineCount / (rect.height - HEIGHT);
                int line = (int) (e.y * lineStep) + 1;
                styledText.setToolTipText("Line: " + line);
            }
        });

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
        layoutData.widthHint = 10;
        canvas.setLayoutData(layoutData);

        errorColor = ColorRegistry.getColor(0xC0, 0x00, 0x00);
        warningColor = ColorRegistry.getColor(0xFC, 0xAF, 0x3E);
    }

    public void setStyledText(StyledText styledText) {
        this.styledText = styledText;
    }

    public void setErrorHighlight(int line) {
        highlight.put(line - 1, errorColor);
    }

    public void setWarningHighlight(int line) {
        highlight.put(line - 1, warningColor);
    }

    public void clearHighlights() {
        highlight.clear();
    }

    public void redraw() {
        canvas.redraw();
    }

    void goToLine(int line) {
        if (line >= styledText.getLineCount()) {
            return;
        }

        Rectangle rect = styledText.getClientArea();
        int topLine = styledText.getLineIndex(0);
        int bottomLine = styledText.getLineIndex(rect.height);
        int pageSize = bottomLine - topLine;
        while (line < topLine) {
            topLine -= pageSize;
            bottomLine -= pageSize;
        }
        while (line > bottomLine) {
            topLine += pageSize;
            bottomLine += pageSize;
        }

        styledText.setCaretOffset(styledText.getOffsetAtLine(line));
        styledText.setTopIndex(topLine);
    }

}
