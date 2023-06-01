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
    void testGetKeywordControlContents() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    meth|od1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();

        Assertions.assertEquals("    method1", subject.getControlContents(control));
        Assertions.assertEquals(8, subject.getCursorPosition(control));
    }

    @Test
    void testGetObjectKeywordControlContents() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    object.met|hod1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();

        Assertions.assertEquals("    object.method1", subject.getControlContents(control));
        Assertions.assertEquals(14, subject.getCursorPosition(control));
    }

    @Test
    void testSetShorterControlContents() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    meth|od1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    short|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "short", subject.getCursorPosition(control));

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetLongerControlContents() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    meth|od1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    longmethod|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "longmethod", subject.getCursorPosition(control));

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetArgumentTemplateContents() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    meth|od1\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    method(|arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetControlContentsKeepExistingArguments() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        meth|od2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method(|arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectArray() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    obj|ect[1].start()\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "array[0]", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    array[|1].start()\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectMethod() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    object.|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    object.method(|arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectName() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    obj|ect.method(arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "object2", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    object2|.method(arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectConstant() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    a := object#|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "CONSTANT", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    a := object#CONSTANT|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectAndMethod() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    obj|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "object.method(arg0,arg1)", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    object.method(|arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectAndConstant() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    a := obj|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "object#CONSTANT", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    a := object#CONSTANT|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectArrayMethod() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    object[0].|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "method(arg0,arg1)", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    object[0].method(|arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetObjectAndArrayMethod() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "    obj|\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "object[0].method(arg0,arg1)", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "    object[0].method(|arg0,arg1)\n"
            + "    repeat\n"
            + "        method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testReplaceAtLineStart() {
        String text = ""
            + "PUB start\n"
            + "\n"
            + "obj|ect.method(arg0,arg1)\n"
            + "repeat\n"
            + "    method2(arg0)\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, "object2", subject.getCursorPosition(control));

        String expectedText = ""
            + "PUB start\n"
            + "\n"
            + "object2|.method(arg0,arg1)\n"
            + "repeat\n"
            + "    method2(arg0)\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetSpin1LocalLabel() {
        String text = ""
            + "DAT\n"
            + "\n"
            + "    jmp #:|\n"
            + "    ret\n"
            + ":local\n"
            + "    ret\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, ":local", subject.getCursorPosition(control));

        String expectedText = ""
            + "DAT\n"
            + "\n"
            + "    jmp #:local|\n"
            + "    ret\n"
            + ":local\n"
            + "    ret\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

    @Test
    void testSetSpin2LocalLabel() {
        String text = ""
            + "DAT\n"
            + "\n"
            + "    jmp #.|\n"
            + "    ret\n"
            + ":local\n"
            + "    ret\n"
            + "\n";
        int caretOffset = text.indexOf("|");

        control = new StyledText(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setCaretOffset(caretOffset);

        StyledTextContentAdapter subject = new StyledTextContentAdapter();
        subject.setControlContents(control, ".local", subject.getCursorPosition(control));

        String expectedText = ""
            + "DAT\n"
            + "\n"
            + "    jmp #.local|\n"
            + "    ret\n"
            + ":local\n"
            + "    ret\n"
            + "\n";
        int expectedCaretOffset = expectedText.indexOf("|");

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedCaretOffset, control.getCaretOffset());
    }

}
