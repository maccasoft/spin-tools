/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

import com.maccasoft.propeller.internal.ImageRegistry;

public class LineNumbersRuler {

    Canvas canvas;
    GridData layoutData;

    StyledText text;

    int leftMargin;
    int rightMargin;

    private int scrollBarSelection;
    private int lineCount;

    private Color highlightForeground;
    private Set<Integer> highlight = new HashSet<>();

    private Integer[] bookmarks = new Integer[9];
    private BookmarksListener bookmarksListener;

    public static interface BookmarksListener {

        public void bookmarksChanged(Integer[] bookmarks);
    }

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

    final TextChangeListener textChangeListener = new TextChangeListener() {

        @Override
        public void textSet(TextChangedEvent event) {

        }

        @Override
        public void textChanging(TextChangingEvent event) {
            if (event.newLineCount != 0 || event.replaceLineCount != 0) {
                int startLine = text.getLineAtOffset(event.start);

                Set<Integer> newHighlight = new HashSet<>();
                for (int lineNumber : highlight) {
                    int offset = text.getOffsetAtLine(lineNumber);
                    if (event.start <= offset) {
                        if ((startLine + event.replaceLineCount) <= lineNumber) {
                            newHighlight.add(lineNumber + (event.newLineCount - event.replaceLineCount));
                        }
                    }
                }
                highlight.clear();
                highlight.addAll(newHighlight);

                for (int i = 0; i < bookmarks.length; i++) {
                    if (bookmarks[i] != null) {
                        int offset = text.getOffsetAtLine(bookmarks[i]);
                        if (event.start <= offset) {
                            if ((startLine + event.replaceLineCount) > bookmarks[i]) {
                                bookmarks[i] = null;
                            }
                            else {
                                bookmarks[i] += event.newLineCount - event.replaceLineCount;
                            }
                        }
                    }
                }
                fireBookmarksChanged(bookmarks);
                canvas.redraw();
            }
        }

        @Override
        public void textChanged(TextChangedEvent event) {

        }
    };

    public LineNumbersRuler(Composite parent) {
        canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.NO_FOCUS);
        canvas.setLayoutData(layoutData = new GridData(SWT.FILL, SWT.FILL, false, true));
        canvas.addPaintListener(paintListener);
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                int lineNumber = text.getLineIndex(e.y);
                toggleBookmarkAt(lineNumber);
                fireBookmarksChanged(bookmarks);
                canvas.redraw();
            }

        });

        leftMargin = 5;
        rightMargin = 5;

        GC gc = new GC(canvas);
        layoutData.widthHint = leftMargin + gc.stringExtent("0000").x + rightMargin;
        gc.dispose();

        scrollBarSelection = lineCount = -1;

        highlightForeground = new Color(0xF0, 0x00, 0x00);
    }

    public void setText(StyledText text) {
        this.text = text;
        this.text.addPaintListener(textPaintListener);
        this.text.getContent().addTextChangeListener(textChangeListener);
    }

    public void addListener(BookmarksListener l) {
        this.bookmarksListener = l;
    }

    public void removeListener() {
        this.bookmarksListener = null;
    }

    void fireBookmarksChanged(Integer[] bookmarks) {
        try {
            if (bookmarksListener != null) {
                bookmarksListener.bookmarksChanged(bookmarks);
            }
        } catch (Exception e) {
            // Do nothing
            e.printStackTrace();
        }
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

            for (int num = 0; num < bookmarks.length; num++) {
                if (bookmarks[num] != null && bookmarks[num] == lineNumber) {
                    Image image = ImageRegistry.getImageFromResources(String.format("notification-counter-%02d.png", num + 1));
                    gc.drawImage(image, 0, y);
                    break;
                }
            }

            lineNumber++;
        }
    }

    public void setFont(Font font) {
        canvas.setFont(font);

        GC gc = new GC(canvas);
        layoutData.widthHint = leftMargin + gc.stringExtent("0000").x + rightMargin;
        gc.dispose();

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

    public void setBookmarks(Integer[] lines) {
        for (int i = 0; i < bookmarks.length; i++) {
            bookmarks[i] = null;
        }
        if (lines != null) {
            for (int i = 0; i < bookmarks.length && i < lines.length; i++) {
                bookmarks[i] = lines[i];
            }
        }
        canvas.redraw();
    }

    public Integer[] getBookmarks() {
        return bookmarks;
    }

    public Integer getBookmark(int num) {
        return bookmarks[num];
    }

    public void toggleBookmarkAt(int line) {
        for (int i = 0; i < bookmarks.length; i++) {
            if (bookmarks[i] != null && bookmarks[i].equals(line)) {
                bookmarks[i] = null;
                canvas.redraw();
                return;
            }

        }
        for (int i = 0; i < bookmarks.length; i++) {
            if (bookmarks[i] == null) {
                bookmarks[i] = line;
                canvas.redraw();
                return;
            }
        }
    }

}
