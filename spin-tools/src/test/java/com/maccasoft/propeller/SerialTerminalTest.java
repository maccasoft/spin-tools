/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class SerialTerminalTest {

    Display display;
    SerialTerminal instance;
    int screenStart;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        Preferences preferences = new Preferences();
        preferences.setTerminalWindow(new Rectangle(0, 0, 80, 25));

        instance = new SerialTerminal(display, preferences);
        instance.create();
        instance.shell.pack();

        instance.canvas.notifyListeners(SWT.Resize, new Event());
        while (display.readAndDispatch()) {

        }
        screenStart = instance.topRow;
    }

    @AfterEach
    void tearDown() {
        while (display.readAndDispatch()) {

        }
        instance.dispose();
        while (display.readAndDispatch()) {

        }
    }

    @AfterAll
    void terminate() {
        display.dispose();
    }

    @Test
    void testWrite() {
        Assertions.assertEquals(0, instance.cx);
        Assertions.assertEquals(instance.topRow, instance.cy);

        instance.write('X');

        Assertions.assertEquals(1, instance.cx);
        Assertions.assertEquals(screenStart, instance.cy);
        Assertions.assertEquals('X', instance.screen[instance.cy][0].character);
    }

    @Test
    void testANSICodes() {
        instance.write("\033[12;20H");

        Assertions.assertEquals(screenStart + 12 - 1, instance.cy);
        Assertions.assertEquals(20 - 1, instance.cx);
    }

    @Test
    void testANSIColor() {
        instance.write("\033[31;42mA");
        SerialTerminal.Cell cell = instance.screen[instance.cy][0];
        Assertions.assertEquals('A', cell.character);
        Assertions.assertEquals(SerialTerminal.colors[1], cell.foreground);
        Assertions.assertEquals(SerialTerminal.colors[2], cell.background);
    }

    @Test
    void testBackspace() {
        instance.backspaceClears = false;
        instance.write("ABC\010");

        Assertions.assertEquals(3 - 1, instance.cx);
        Assertions.assertEquals(screenStart, instance.cy);
        Assertions.assertEquals('A', instance.screen[instance.cy][0].character);
        Assertions.assertEquals('B', instance.screen[instance.cy][1].character);
        Assertions.assertEquals('C', instance.screen[instance.cy][2].character);
    }

    @Test
    void testBackspaceClears() {
        instance.backspaceClears = true;
        instance.write("ABC\010");

        Assertions.assertEquals(3 - 1, instance.cx);
        Assertions.assertEquals(screenStart, instance.cy);
        Assertions.assertEquals('A', instance.screen[instance.cy][0].character);
        Assertions.assertEquals('B', instance.screen[instance.cy][1].character);
        Assertions.assertEquals(' ', instance.screen[instance.cy][2].character);
    }

    @Test
    void testImplicitCRLF() {
        instance.implicitCRLF = true;
        instance.write("ABC\r");

        Assertions.assertEquals(0, instance.cx);
        Assertions.assertEquals(screenStart + 1, instance.cy);
    }

    @Test
    void testImplicitCRLFDisabled() {
        instance.implicitCRLF = false;
        instance.write("ABC\r");

        Assertions.assertEquals(0, instance.cx);
        Assertions.assertEquals(screenStart, instance.cy);
    }

    @Test
    void testImplicitCRLFSkipsLF() {
        instance.implicitCRLF = true;
        instance.write("ABC\r\n");

        Assertions.assertEquals(0, instance.cx);
        Assertions.assertEquals(screenStart + 1, instance.cy);

        instance.write("\n");

        Assertions.assertEquals(0, instance.cx);
        Assertions.assertEquals(screenStart + 2, instance.cy);
    }

}
