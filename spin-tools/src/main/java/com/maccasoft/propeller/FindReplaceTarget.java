/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class FindReplaceTarget {

    StyledText styledText;

    public FindReplaceTarget(StyledText styledText) {
        this.styledText = styledText;
    }

    public int findAndSelect(int findPosition, String findString, boolean searchForward, boolean caseSensitive, boolean wholeWord, boolean regexSearch) {
        int patternFlags = 0;
        String text = styledText.getText();

        if (!regexSearch) {
            findString = asRegPattern(findString);
            if (wholeWord) {
                findString = "\\b" + findString + "\\b";
            }
        }

        if (!caseSensitive) {
            patternFlags |= Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        }

        Pattern pattern = Pattern.compile(findString, patternFlags);
        Matcher matcher = pattern.matcher(text);

        if (searchForward) {
            if (findPosition == -1) {
                findPosition = 0;
            }
            if (matcher.find(findPosition)) {
                styledText.setSelectionRange(matcher.start(), matcher.group().length());
                revealCaret();
                return matcher.start();
            }
        }
        else {
            if (findPosition == -1) {
                findPosition = text.length();
            }
            boolean found = matcher.find(0);
            int index = -1;
            while (found && matcher.start() + matcher.group().length() <= findPosition) {
                index = matcher.start();
                found = matcher.find(index + 1);
            }
            if (index > -1) {
                if (matcher.find(index)) {
                    styledText.setSelectionRange(matcher.start(), matcher.group().length());
                    revealCaret();
                }
                return index;
            }
        }

        return -1;
    }

    public Point getSelection() {
        return styledText.getSelectionRange();
    }

    public String getSelectionText() {
        return styledText.getSelectionText();
    }

    public void replaceSelection(String text) {
        Point selection = styledText.getSelectionRange();
        if (selection.y != 0) {
            styledText.replaceTextRange(selection.x, selection.y, text);
        }
    }

    void revealCaret() {
        Rectangle rect = styledText.getClientArea();
        int offset = styledText.getCaretOffset();
        int topLine = styledText.getLineIndex(0);
        int bottomLine = styledText.getLineIndex(rect.height);
        int pageSize = bottomLine - topLine;
        int lineCount = styledText.getLineCount();

        while (offset < styledText.getOffsetAtLine(topLine)) {
            if (topLine - pageSize < 0) {
                topLine = 0;
                bottomLine = Math.min(pageSize, lineCount - 1);
                break;
            }
            topLine -= pageSize;
            bottomLine -= pageSize;
        }

        while (offset > styledText.getOffsetAtLine(bottomLine)) {
            if (bottomLine + pageSize >= lineCount) {
                topLine = lineCount - pageSize;
                if (topLine < 0) {
                    topLine = 0;
                }
                break;
            }
            topLine += pageSize;
            bottomLine += pageSize;
        }

        if (styledText.getLineIndex(0) != topLine) {
            styledText.setTopIndex(topLine);
        }
    }

    private String asRegPattern(String string) {
        StringBuilder out = new StringBuilder(string.length());
        boolean quoting = false;

        for (int i = 0, length = string.length(); i < length; i++) {
            char ch = string.charAt(i);
            if (ch == '\\') {
                if (quoting) {
                    out.append("\\E");
                    quoting = false;
                }
                out.append("\\\\");
                continue;
            }
            if (!quoting) {
                out.append("\\Q");
                quoting = true;
            }
            out.append(ch);
        }
        if (quoting) {
            out.append("\\E");
        }

        return out.toString();
    }

}
