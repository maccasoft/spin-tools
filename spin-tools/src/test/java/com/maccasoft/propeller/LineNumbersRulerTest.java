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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class LineNumbersRulerTest {

    Display display;
    Shell shell;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        shell = new Shell(display);
    }

    @AfterEach
    void tearDown() {
        while (display.readAndDispatch()) {

        }
        shell.dispose();
        while (display.readAndDispatch()) {

        }
    }

    @AfterAll
    void terminate() {
        display.dispose();
    }

    @Test
    public void testInsertLineUpdatesBookmark() {
        StyledText styledText = new StyledText(shell, SWT.NONE);

        LineNumbersRuler ruler = new LineNumbersRuler(shell);
        ruler.setText(styledText);
        ruler.setBookmarks(new Integer[] {
            2, null, 6
        });

        styledText.setText(""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "PRI func(b) : r\n"
            + "\n"
            + "    return b * 4\n"
            + "\n"
            + "");

        styledText.setCaretOffset(styledText.getOffsetAtLine(3));
        styledText.insert("    a := func(a)\n");

        Assertions.assertEquals(""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "    a := func(a)\n"
            + "\n"
            + "PRI func(b) : r\n"
            + "\n"
            + "    return b * 4\n"
            + "\n"
            + "", styledText.getText());
        Assertions.assertArrayEquals(new Integer[] { 2, null, 7, null, null, null, null, null, null }, ruler.getBookmarks());
    }

    @Test
    public void testRemoveLineUpdatesBookmark() {
        StyledText styledText = new StyledText(shell, SWT.NONE);

        LineNumbersRuler ruler = new LineNumbersRuler(shell);
        ruler.setText(styledText);
        ruler.setBookmarks(new Integer[] {
            2, null, 6
        });

        styledText.setText(""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "PRI func(b) : r\n"
            + "\n"
            + "    return b * 4\n"
            + "\n"
            + "");

        styledText.replaceTextRange(styledText.getOffsetAtLine(1), 1, "");

        Assertions.assertEquals(""
            + "PUB main | a\n"
            + "    a := 1\n"
            + "\n"
            + "PRI func(b) : r\n"
            + "\n"
            + "    return b * 4\n"
            + "\n"
            + "", styledText.getText());
        Assertions.assertArrayEquals(new Integer[] { 1, null, 5, null, null, null, null, null, null }, ruler.getBookmarks());
    }

    @Test
    public void testRemoveLineRangeDeleteBookmark() {
        StyledText styledText = new StyledText(shell, SWT.NONE);

        LineNumbersRuler ruler = new LineNumbersRuler(shell);
        ruler.setText(styledText);
        ruler.setBookmarks(new Integer[] {
            2, null, 6
        });

        styledText.setText(""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "PRI func(b) : r\n"
            + "\n"
            + "    return b * 4\n"
            + "\n"
            + "");

        int startOffset = styledText.getOffsetAtLine(1);
        int endOffset = styledText.getOffsetAtLine(3);
        styledText.replaceTextRange(startOffset, endOffset - startOffset, "");

        Assertions.assertEquals(""
            + "PUB main | a\n"
            + "\n"
            + "PRI func(b) : r\n"
            + "\n"
            + "    return b * 4\n"
            + "\n"
            + "", styledText.getText());
        Assertions.assertArrayEquals(new Integer[] { null, null, 4, null, null, null, null, null, null }, ruler.getBookmarks());
    }

}
