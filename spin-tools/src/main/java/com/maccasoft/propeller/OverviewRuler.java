/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
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
    Cursor handCursor;

    Color errorColor;
    Color warningColor;
    private Map<Integer, String> errorHighlight = new TreeMap<>();
    private Map<Integer, String> warningHighlight = new TreeMap<>();

    Shell popupWindow;

    public OverviewRuler(Composite parent) {
        display = parent.getDisplay();

        handCursor = display.getSystemCursor(SWT.CURSOR_HAND);

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
                float lineStep = Math.min((float) (rect.height - HEIGHT) / lineCount, styledText.getLineHeight());
                int offset = Math.max((int) ((lineStep - HEIGHT) / 2), 0);

                for (int line : warningHighlight.keySet()) {
                    int y = (int) (line * lineStep) + offset;
                    e.gc.setBackground(warningColor);
                    e.gc.fillRectangle(0, y, rect.width, HEIGHT);
                }

                for (int line : errorHighlight.keySet()) {
                    int y = (int) (line * lineStep) + offset;
                    e.gc.setBackground(errorColor);
                    e.gc.fillRectangle(0, y, rect.width, HEIGHT);
                }
            }
        });
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                Rectangle rect = canvas.getClientArea();
                int lineCount = styledText.getLineCount();
                float lineStep = Math.min((float) (rect.height - HEIGHT) / lineCount, styledText.getLineHeight());
                int offset = Math.max((int) ((lineStep - HEIGHT) / 2), 0);

                for (int line : errorHighlight.keySet()) {
                    int y = (int) (line * lineStep) + offset;
                    if (e.y >= y && e.y <= y + HEIGHT) {
                        goToLine(line);
                        return;
                    }
                }
                for (int line : warningHighlight.keySet()) {
                    int y = (int) (line * lineStep) + offset;
                    if (e.y >= y && e.y <= y + HEIGHT) {
                        goToLine(line);
                        return;
                    }
                }

                int line = (int) (e.y / lineStep);
                if (line < lineCount) {
                    goToLine(line);
                }
            }
        });
        canvas.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (popupWindow != null) {
                    popupWindow.dispose();
                    popupWindow = null;
                    styledText.setFocus();
                }
                canvas.setCursor(isOnAnnotation(e.y) ? handCursor : null);
            }
        });
        canvas.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseExit(MouseEvent e) {
                canvas.setCursor(null);
            }

            @Override
            public void mouseHover(MouseEvent e) {
                if (popupWindow != null) {
                    return;
                }

                String message = null;
                Rectangle rect = canvas.getClientArea();
                int lineCount = styledText.getLineCount();
                float lineStep = Math.min((float) (rect.height - HEIGHT) / lineCount, styledText.getLineHeight());
                int offset = Math.max((int) ((lineStep - HEIGHT) / 2), 0);

                for (int line : errorHighlight.keySet()) {
                    int y = (int) (line * lineStep) + offset;
                    if (e.y >= y && e.y <= y + HEIGHT) {
                        message = "Line " + (line + 1) + ": " + errorHighlight.get(line);
                        break;
                    }
                }
                if (message == null) {
                    for (int line : warningHighlight.keySet()) {
                        int y = (int) (line * lineStep) + offset;
                        if (e.y >= y && e.y <= y + HEIGHT) {
                            message = "Line " + (line + 1) + ": " + warningHighlight.get(line);
                            break;
                        }
                    }
                }

                if (message == null) {
                    int line = (int) (e.y / lineStep) + 1;
                    if (line <= lineCount) {
                        message = "Line " + line;
                    }
                }

                if (message != null) {
                    popupWindow = new Shell(styledText.getShell(), SWT.ON_TOP);
                    FillLayout layout = new FillLayout();
                    layout.marginHeight = layout.marginWidth = 5;
                    popupWindow.setLayout(layout);
                    Label content = new Label(popupWindow, SWT.NONE);
                    content.setText(message);
                    popupWindow.pack();

                    Rectangle popupRect = popupWindow.getBounds();
                    popupRect.x = rect.x - popupRect.width;
                    popupRect.y = e.y - popupRect.height / 2;
                    popupWindow.setBounds(display.map(canvas, null, popupRect));

                    popupWindow.setVisible(true);
                }
            }
        });

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
        layoutData.widthHint = 12;
        canvas.setLayoutData(layoutData);

        errorColor = ColorRegistry.getColor(0xFE, 0x2D, 0x98);
        warningColor = ColorRegistry.getColor(0xF2, 0xBF, 0x57);
    }

    public void setStyledText(StyledText styledText) {
        this.styledText = styledText;
    }

    public void setErrorHighlight(int line, String message) {
        String msg = errorHighlight.get(line - 1);
        if (msg != null) {
            if (!msg.contains(System.lineSeparator())) {
                msg = "Multiple errors at this line" + System.lineSeparator() + "    - " + msg;
            }
            message = msg + System.lineSeparator() + "    - " + message;
        }
        errorHighlight.put(line - 1, message);
    }

    public void setWarningHighlight(int line, String message) {
        String msg = warningHighlight.get(line - 1);
        if (msg != null) {
            if (!msg.contains(System.lineSeparator())) {
                msg = "Multiple warnings at this line" + System.lineSeparator() + "    - " + msg;
            }
            message = msg + System.lineSeparator() + "    - " + message;
        }
        warningHighlight.put(line - 1, message);
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

    boolean isOnAnnotation(int ey) {
        Rectangle rect = canvas.getClientArea();
        int lineCount = styledText.getLineCount();
        float lineStep = Math.min((float) (rect.height - HEIGHT) / lineCount, styledText.getLineHeight());
        int offset = Math.max((int) ((lineStep - HEIGHT) / 2), 0);

        for (int line : errorHighlight.keySet()) {
            int y = (int) (line * lineStep) + offset;
            if (ey >= y && ey <= y + HEIGHT) {
                return true;
            }
        }
        for (int line : warningHighlight.keySet()) {
            int y = (int) (line * lineStep) + offset;
            if (ey >= y && ey <= y + HEIGHT) {
                return true;
            }
        }
        return false;
    }

    public void setBackground(Color color) {
        canvas.setBackground(color);
        canvas.redraw();
    }

    public void setForeground(Color color) {
        canvas.setForeground(color);
        canvas.redraw();
    }

}
