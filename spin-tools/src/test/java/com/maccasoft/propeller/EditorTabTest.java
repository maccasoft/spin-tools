/*
 * Copyright (c) 26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
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
class EditorTabTest {

    Display display;
    Shell shell;
    CTabFolder tabFolder;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        shell = new Shell(display);
        tabFolder = new CTabFolder(shell, SWT.NONE);
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
    public void testSetFileUpdatesSourcePool1() {
        File file = new File("test.spin2").getAbsoluteFile();
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    repeat\n"
            + "\n"
            + "";
        SourcePool sourcePool = new SourcePool();

        EditorTab subject = new EditorTab(tabFolder, file.getName(), sourcePool);
        subject.setEditorText(text, 0, 0);

        subject.setFile(file);

        Assertions.assertEquals(text, sourcePool.getSource(file));
    }

    @Test
    public void testSetFileUpdatesSourcePool2() {
        File file = new File("test.spin2").getAbsoluteFile();
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    repeat\n"
            + "\n"
            + "";
        SourcePool sourcePool = new SourcePool();
        sourcePool.setSource(file, text);

        EditorTab subject = new EditorTab(tabFolder, file, sourcePool);
        subject.setEditorText(text, 0, 0);

        File newFile = new File("test_new.spin2").getAbsoluteFile();
        subject.setFile(newFile);

        Assertions.assertNull(sourcePool.getSource(file));
        Assertions.assertEquals(text, sourcePool.getSource(newFile));
    }

}
