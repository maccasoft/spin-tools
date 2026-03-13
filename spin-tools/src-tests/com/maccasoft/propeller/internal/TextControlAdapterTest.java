/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class TextControlAdapterTest {

    Display display;
    Shell shell;
    Text control;

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
    void testSetControlContents() {
        String text = "/opt/bin/loadp2 -p $| ${file.name}.binary";
        int caretOffset = text.indexOf("|");

        control = new Text(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setSelection(caretOffset, caretOffset);

        TextContentAdapter subject = new TextContentAdapter();
        subject.setControlContents(control, "${serial}", subject.getCursorPosition(control));

        String expectedText = "/opt/bin/loadp2 -p ${serial}| ${file.name}.binary";

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedText.indexOf("|"), control.getCaretPosition());
    }

    @Test
    void testReplaceControlContents() {
        String text = "/opt/bin/loadp2 ${file|.name}.binary";
        int caretOffset = text.indexOf("|");

        control = new Text(shell, SWT.NONE);
        control.setText(text.replace("|", ""));
        control.setSelection(caretOffset, caretOffset);

        TextContentAdapter subject = new TextContentAdapter();
        subject.setControlContents(control, "${file}", subject.getCursorPosition(control));

        String expectedText = "/opt/bin/loadp2 ${file}|.binary";

        Assertions.assertEquals(expectedText.replace("|", ""), control.getText());
        Assertions.assertEquals(expectedText.indexOf("|"), control.getCaretPosition());
    }

}
