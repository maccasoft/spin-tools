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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class StyledTextContentAdapterTest {

    Display display;
    Shell shell;
    StyledText control;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
        shell = new Shell(display);
    }

    @AfterEach
    void tearDown() {
        while (display.readAndDispatch()) {

        }
        control.dispose();
        while (display.readAndDispatch()) {

        }
    }

    @AfterAll
    void terminate() {
        while (display.readAndDispatch()) {

        }
        shell.dispose();
        while (display.readAndDispatch()) {

        }
        display.dispose();
    }

    @Test
    void testGetControlContents() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(19);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();

        Assertions.assertEquals("    method1", subject.getControlContents(control));
        Assertions.assertEquals(8, subject.getCursorPosition(control));
    }

    @Test
    void testSetShorterControlContents() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(19);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "short", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "PUB start\n"
            + "\n"
            + "    short\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n", control.getText());
        Assertions.assertEquals(20, control.getCaretOffset());
    }

    @Test
    void testSetLongerControlContents() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(19);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "longmethod", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "PUB start\n"
            + "\n"
            + "    longmethod\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n", control.getText());
        Assertions.assertEquals(25, control.getCaretOffset());
    }

    @Test
    void testSetArgumentTemplateContents() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(19);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "PUB start\n"
            + "\n"
            + "    method(arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n", control.getText());
        Assertions.assertEquals(22, control.getCaretOffset());
    }

    @Test
    void testSetControlContentsKeepExistingArguments() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(46);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method(arg0)\n"
            + "\n", control.getText());
        Assertions.assertEquals(49, control.getCaretOffset());
    }

    @Test
    void testSetObjectArray() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    object[1].start()\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(18);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "array[0]", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "PUB start\n"
            + "\n"
            + "    array[1].start()\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n", control.getText());
        Assertions.assertEquals(21, control.getCaretOffset());
    }

    @Test
    void testSetObjectMethod() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    object.\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(22);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "PUB start\n"
            + "\n"
            + "    object.method(arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n", control.getText());
        Assertions.assertEquals(29, control.getCaretOffset());
    }

    @Test
    void testSetObjectArrayMethod() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "PUB start\n"
            + "\n"
            + "    object[0].\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n");
        control.setCaretOffset(25);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "PUB start\n"
            + "\n"
            + "    object[0].method(arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n", control.getText());
        Assertions.assertEquals(32, control.getCaretOffset());
    }

    @Test
    void testSetSpin1LocalLabel() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "DAT\n"
            + "\n"
            + "    jmp #:\n"
            + "    ret\n"
            + ":local\n"
            + "    ret\n"
            + "\n");
        control.setCaretOffset(15);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, ":local", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "DAT\n"
            + "\n"
            + "    jmp #:local\n"
            + "    ret\n"
            + ":local\n"
            + "    ret\n"
            + "\n", control.getText());
        Assertions.assertEquals(20, control.getCaretOffset());
    }

    @Test
    void testSetSpin2LocalLabel() {
        control = new StyledText(shell, SWT.NONE);
        control.setText(""
            + "DAT\n"
            + "\n"
            + "    jmp #.\n"
            + "    ret\n"
            + ".local\n"
            + "    ret\n"
            + "\n");
        control.setCaretOffset(15);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, ".local", subject.getCursorPosition(control));

        Assertions.assertEquals(""
            + "DAT\n"
            + "\n"
            + "    jmp #.local\n"
            + "    ret\n"
            + ".local\n"
            + "    ret\n"
            + "\n", control.getText());
        Assertions.assertEquals(20, control.getCaretOffset());
    }

}
