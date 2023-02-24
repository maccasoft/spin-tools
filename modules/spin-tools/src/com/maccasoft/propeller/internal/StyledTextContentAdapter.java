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
        StyledText styledText = (StyledText) control;
        int caretOffset = styledText.getCaretOffset();
        int line = styledText.getLineAtOffset(caretOffset);
        return styledText.getLine(line);
    }

    @Override
    public void setControlContents(Control control, String text, int cursorPosition) {
        StyledText styledText = (StyledText) control;
        int caretOffset = styledText.getCaretOffset();
        int line = styledText.getLineAtOffset(caretOffset);
        int lineOffset = styledText.getOffsetAtLine(line);
        int e;

        String contents = styledText.getLine(line);

        int position = caretOffset - lineOffset;
        int start = position;
        while (start > 0) {
            if (!isIdentifierPart(contents.charAt(start - 1))) {
                break;
            }
            start--;
        }
        while (position < contents.length()) {
            if (!isIdentifierPart(contents.charAt(position))) {
                break;
            }
            position++;
        }

        if (position < contents.length() && contents.charAt(position) != '(') {
            e = position + 1;
            while (e < contents.length() && contents.charAt(e) == ' ') {
                e++;
            }
            if (e < contents.length() && contents.charAt(e) == '(') {
                position = e;
            }
        }

        if (position < contents.length() && contents.charAt(position) == '(') {
            if ((e = text.indexOf('(')) != -1) {
                text = text.substring(0, e + 1);
                position++;
            }
        }

        if (position + lineOffset > start + lineOffset) {
            contents = styledText.getText(start + lineOffset, position + lineOffset);
            if (!contents.startsWith(".") && (e = contents.indexOf('.')) != -1) {
                start += e + 1;
            }
        }

        styledText.setSelection(new Point(start + lineOffset, position + lineOffset));
        styledText.insert(text);

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

        styledText.setCaretOffset(start + lineOffset + caretOffset);
    }

    boolean isIdentifierPart(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '.' || ch == ':';
    }

    @Override
    public void insertControlContents(Control control, String text, int cursorPosition) {
        StyledText styledText = (StyledText) control;
        styledText.insert(text);
        styledText.setCaretOffset(styledText.getCaretOffset() + text.length());
    }

    @Override
    public int getCursorPosition(Control control) {
        StyledText styledText = (StyledText) control;
        int caretOffset = styledText.getCaretOffset();
        int line = styledText.getLineAtOffset(caretOffset);
        return caretOffset - styledText.getOffsetAtLine(line);
    }

    @Override
    public Rectangle getInsertionBounds(Control control) {
        StyledText text = (StyledText) control;
        Point caretOrigin = text.getLocationAtOffset(text.getCaretOffset());
        return new Rectangle(caretOrigin.x + text.getClientArea().x, caretOrigin.y + text.getClientArea().y + 3, 1, text.getLineHeight());
    }

    @Override
    public void setCursorPosition(Control control, int position) {
        StyledText styledText = (StyledText) control;
        styledText.setSelection(new Point(position, position));
    }

    @Override
    public Point getSelection(Control control) {
        StyledText styledText = (StyledText) control;
        return styledText.getSelection();
    }

    @Override
    public void setSelection(Control control, Point range) {
        StyledText styledText = (StyledText) control;
        styledText.setSelection(range);
    }

}
