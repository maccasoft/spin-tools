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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.internal.ColorRegistry;

public class OverviewRuler {

    final static int HEIGHT = 5;

    Display display;
    Canvas canvas;

    StyledText styledText;

    private Color errorColor;
    private Color errorBackgroundColor;
    private Color warningColor;
    private Color warningBackgroundColor;
    private Set<Integer> errorHighlight = new HashSet<Integer>();
    private Set<Integer> warningHighlight = new HashSet<Integer>();

    Shell popupWindow;

    public OverviewRuler(Composite parent) {
        display = parent.getDisplay();

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

                for (int line : warningHighlight) {
                    int y = (int) (line * lineStep);
                    e.gc.setForeground(warningColor);
                    e.gc.setBackground(warningBackgroundColor);
                    e.gc.fillRectangle(0, y, rect.width, HEIGHT);
                    e.gc.drawRectangle(0, y, rect.width - 1, HEIGHT);
                }

                for (int line : errorHighlight) {
                    int y = (int) (line * lineStep);
                    e.gc.setForeground(errorColor);
                    e.gc.setBackground(errorBackgroundColor);
                    e.gc.fillRectangle(0, y, rect.width, HEIGHT);
                    e.gc.drawRectangle(0, y, rect.width - 1, HEIGHT);
                }
            }
        });
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                Rectangle rect = canvas.getClientArea();
                int lineCount = styledText.getLineCount();

                float lineStep = (float) (rect.height - HEIGHT) / lineCount;
                for (int line : errorHighlight) {
                    int y = (int) (line * lineStep);
                    if (e.y >= y && e.y < y + HEIGHT) {
                        goToLine(line);
                        return;
                    }
                }
                for (int line : warningHighlight) {
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
        canvas.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (popupWindow != null) {
                    popupWindow.dispose();
                    popupWindow = null;
                }
            }
        });
        canvas.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseHover(MouseEvent e) {
                if (popupWindow != null) {
                    return;
                }

                Rectangle rect = canvas.getClientArea();
                int lineCount = styledText.getLineCount();
                float lineStep = (float) lineCount / (rect.height - HEIGHT);
                int line = (int) (e.y * lineStep) + 1;

                popupWindow = new Shell(styledText.getShell(), SWT.NO_FOCUS | SWT.ON_TOP);
                FillLayout layout = new FillLayout();
                layout.marginHeight = layout.marginWidth = 5;
                popupWindow.setLayout(layout);
                Label content = new Label(popupWindow, SWT.NONE);
                content.setText("Line: " + line);
                popupWindow.pack();

                Rectangle popupRect = popupWindow.getBounds();
                popupRect.x = rect.x - popupRect.width;
                popupRect.y = e.y - popupRect.height / 2;
                popupWindow.setBounds(display.map(canvas, null, popupRect));

                popupWindow.open();
                canvas.setFocus();
            }
        });

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
        layoutData.widthHint = 12;
        canvas.setLayoutData(layoutData);

        errorColor = ColorRegistry.getColor(0xFE, 0x2D, 0x98);
        errorBackgroundColor = ColorRegistry.getColor(0xF9, 0xBA, 0xD9);
        warningColor = ColorRegistry.getColor(0xF6, 0xD4, 0x56);
        warningBackgroundColor = ColorRegistry.getColor(0xFC, 0xF1, 0xCB);
    }

    public void setStyledText(StyledText styledText) {
        this.styledText = styledText;
    }

    public void setErrorHighlight(int line) {
        errorHighlight.add(line - 1);
    }

    public void setWarningHighlight(int line) {
        warningHighlight.add(line - 1);
    }

    public void clearHighlights() {
        errorHighlight.clear();
        warningHighlight.clear();
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
