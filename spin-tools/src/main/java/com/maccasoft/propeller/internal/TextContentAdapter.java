/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlContentAdapter2;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class TextContentAdapter implements IControlContentAdapter, IControlContentAdapter2 {

    @Override
    public String getControlContents(Control control) {
        return ((Text) control).getText();
    }

    @Override
    public void setControlContents(Control control, String text, int cursorPosition) {
        Text textControl = (Text) control;
        int caretOffset = textControl.getCaretPosition();
        String contents = textControl.getText();

        int start = caretOffset;
        while (start > 0) {
            char ch = contents.charAt(start - 1);
            if (ch == '$') {
                start--;
                break;
            }
            if (ch == '{') {
                start--;
                if (start > 0 && contents.charAt(start - 1) == '$') {
                    start--;
                    break;
                }
            }
            if (!isVariablePart(ch)) {
                break;
            }
            start--;
        }

        int end = caretOffset;
        while (end < contents.length()) {
            char ch = contents.charAt(end);
            if (ch == '}') {
                end++;
                break;
            }
            if (!isVariablePart(ch)) {
                break;
            }
            end++;
        }

        textControl.setSelection(new Point(start, end));
        textControl.insert(text);
        textControl.setSelection(start + text.length(), start + text.length());
    }

    boolean isVariablePart(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '.';
    }

    @Override
    public void insertControlContents(Control control, String text, int cursorPosition) {
        Text textControl = (Text) control;
        Point selection = textControl.getSelection();
        textControl.insert(text);
        if (cursorPosition < text.length()) {
            textControl.setSelection(selection.x + cursorPosition, selection.x + cursorPosition);
        }
    }

    @Override
    public int getCursorPosition(Control control) {
        return ((Text) control).getCaretPosition();
    }

    @Override
    public Rectangle getInsertionBounds(Control control) {
        Text textControl = (Text) control;
        Point caretOrigin = textControl.getCaretLocation();
        return new Rectangle(caretOrigin.x + textControl.getClientArea().x, caretOrigin.y + textControl.getClientArea().y + 3, 1, textControl.getLineHeight());
    }

    @Override
    public void setCursorPosition(Control control, int position) {
        ((Text) control).setSelection(new Point(position, position));
    }

    @Override
    public Point getSelection(Control control) {
        return ((Text) control).getSelection();
    }

    @Override
    public void setSelection(Control control, Point range) {
        ((Text) control).setSelection(range);
    }

}
