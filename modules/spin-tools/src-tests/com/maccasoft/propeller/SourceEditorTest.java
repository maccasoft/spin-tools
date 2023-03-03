/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

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
public class SourceEditorTest {

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
    public void testGetFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("met", subject.getFilterText(null, "    method", 7));
    }

    @Test
    public void testGetIbjectFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("obj", subject.getFilterText(null, "    object.method", 7));
        Assertions.assertEquals("object.", subject.getFilterText(null, "    object.method", 11));
        Assertions.assertEquals("object.met", subject.getFilterText(null, "    object.method", 14));
    }

    @Test
    public void testGetObjectArrayFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("obj", subject.getFilterText(null, "    object[0].method", 7));
        Assertions.assertEquals("object.", subject.getFilterText(null, "    object[0].method", 14));
        Assertions.assertEquals("object.met", subject.getFilterText(null, "    object[0].method", 17));
    }

    @Test
    public void testGetObjectArrayExpressionFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("obj", subject.getFilterText(null, "    object[byte[a][0]].method", 7));
        Assertions.assertEquals("object.", subject.getFilterText(null, "    object[byte[a][0]].method", 23));
        Assertions.assertEquals("object.met", subject.getFilterText(null, "    object[byte[a][0]].method", 26));
    }

}
