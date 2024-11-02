/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
    void testTTYWrite() {
        SerialTerminal.TTY subject = instance.new TTY();
        Assertions.assertEquals(0, instance.cx);
        Assertions.assertEquals(instance.topRow, instance.cy);

        subject.write('X');

        Assertions.assertEquals(1, instance.cx);
        Assertions.assertEquals(screenStart, instance.cy);
        Assertions.assertEquals('X', instance.screen[instance.cy][instance.cx - 1].character);
    }

    @Test
    void testANSICursorPosition() {
        SerialTerminal.ANSI subject = instance.new ANSI();
        subject.write("\033[12;20H");
        Assertions.assertEquals(screenStart + 12 - 1, instance.cy);
        Assertions.assertEquals(19, instance.cx);
    }

    @Test
    void testANSIColor() {
        SerialTerminal.ANSI subject = instance.new ANSI();
        subject.write("\033[31;42mA");
        SerialTerminal.Cell cell = instance.screen[instance.cy][instance.cx - 1];
        Assertions.assertEquals('A', cell.character);
        Assertions.assertEquals(instance.colors[1], cell.foreground);
        Assertions.assertEquals(instance.colors[2], cell.background);
    }

}
