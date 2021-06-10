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

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlContentAdapter2;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class StyledTextContentAdapter implements IControlContentAdapter, IControlContentAdapter2 {

    @Override
    public String getControlContents(Control control) {
        int caretOffset = ((StyledText) control).getCaretOffset();
        int line = ((StyledText) control).getLineAtOffset(caretOffset);
        return ((StyledText) control).getLine(line);
    }

    @Override
    public void setControlContents(Control control, String text, int cursorPosition) {
        int caretOffset = ((StyledText) control).getCaretOffset();
        int line = ((StyledText) control).getLineAtOffset(caretOffset);
        int lineOffset = ((StyledText) control).getOffsetAtLine(line);

        String contents = ((StyledText) control).getLine(line);

        int position = caretOffset - lineOffset;
        int start = position;
        while (start > 0) {
            if (contents.charAt(start - 1) != '_' && !Character.isAlphabetic(contents.charAt(start - 1))) {
                break;
            }
            start--;
        }
        while (position < contents.length()) {
            if (contents.charAt(position) != '_' && !Character.isAlphabetic(contents.charAt(position))) {
                break;
            }
            position++;
        }
        if (position < contents.length() && contents.charAt(position) == '(') {
            int e = text.indexOf('(');
            if (e != -1) {
                text = text.substring(0, e + 1);
                position++;
            }
        }

        ((StyledText) control).setSelection(new Point(start + lineOffset, position + lineOffset));
        ((StyledText) control).insert(text);

        caretOffset = 0;
        while (caretOffset < text.length()) {
            if (text.charAt(caretOffset) == '(') {
                caretOffset++;
                if (caretOffset < text.length() && text.charAt(caretOffset) == ')') {
                    caretOffset++;
                }
                break;
            }
            caretOffset++;
        }

        ((StyledText) control).setCaretOffset(start + lineOffset + caretOffset);
    }

    @Override
    public void insertControlContents(Control control, String text, int cursorPosition) {
        ((StyledText) control).insert(text);
        ((StyledText) control).setCaretOffset(((StyledText) control).getCaretOffset() + text.length());
    }

    @Override
    public int getCursorPosition(Control control) {
        int caretOffset = ((StyledText) control).getCaretOffset();
        int line = ((StyledText) control).getLineAtOffset(caretOffset);
        return caretOffset - ((StyledText) control).getOffsetAtLine(line);
    }

    @Override
    public Rectangle getInsertionBounds(Control control) {
        StyledText text = (StyledText) control;
        Point caretOrigin = text.getLocationAtOffset(text.getCaretOffset());
        return new Rectangle(caretOrigin.x + text.getClientArea().x,
            caretOrigin.y + text.getClientArea().y + 3, 1, text.getLineHeight());
    }

    @Override
    public void setCursorPosition(Control control, int position) {
        ((StyledText) control).setSelection(new Point(position, position));
    }

    @Override
    public Point getSelection(Control control) {
        return ((StyledText) control).getSelection();
    }

    @Override
    public void setSelection(Control control, Point range) {
        ((StyledText) control).setSelection(range);
    }

}
