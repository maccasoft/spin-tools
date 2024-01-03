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
import org.eclipse.swt.widgets.Canvas;
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

import com.maccasoft.propeller.SerialTerminal.Cell;

@TestInstance(Lifecycle.PER_CLASS)
class SerialTerminalTest {

    Display display;
    SerialTerminal instance;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        instance = new SerialTerminal();
        instance.display = display;
        instance.shell = new Shell(display);
        instance.screenWidth = 80;
        instance.screenHeight = 25;
        instance.screen = new Cell[instance.screenHeight][instance.screenWidth];
        instance.canvas = new Canvas(instance.shell, SWT.NONE);
        for (int y = 0; y < 25; y++) {
            for (int x = 0; x < 80; x++) {
                instance.screen[y][x] = instance.new Cell(null, null);
            }
        }
    }

    @AfterEach
    void tearDown() {
        while (display.readAndDispatch()) {

        }
        instance.shell.dispose();
        while (display.readAndDispatch()) {

        }
    }

    @AfterAll
    void terminate() {
        display.dispose();
    }

    @Test
    void testANSINoArguments() {
        SerialTerminal.ANSI subject = instance.new ANSI();
        subject.write((char) 0x1B);
        subject.write('[');
        subject.write('X');
        Assertions.assertEquals(0, subject.argc);
    }

    @Test
    void testANSISingleArgument() {
        SerialTerminal.ANSI subject = instance.new ANSI();
        subject.write((char) 0x1B);
        subject.write('[');
        subject.write('1');
        subject.write('2');
        subject.write('X');
        Assertions.assertEquals(1, subject.argc);
        Assertions.assertEquals(12, subject.args[0]);
    }

    @Test
    void testANSIMultipleArguments() {
        SerialTerminal.ANSI subject = instance.new ANSI();
        subject.write((char) 0x1B);
        subject.write('[');
        subject.write('1');
        subject.write('2');
        subject.write(';');
        subject.write('3');
        subject.write('4');
        subject.write('X');
        Assertions.assertEquals(2, subject.argc);
        Assertions.assertEquals(12, subject.args[0]);
        Assertions.assertEquals(34, subject.args[1]);
    }

    @Test
    void testANSICursorPosition() {
        SerialTerminal.ANSI subject = instance.new ANSI();
        subject.write((char) 0x1B);
        subject.write('[');
        subject.write('1');
        subject.write('2');
        subject.write(';');
        subject.write('2');
        subject.write('H');
        Assertions.assertEquals(12 - 1, instance.cy);
        Assertions.assertEquals(2 - 1, instance.cx);
    }

    @Test
    void testANSIColor() {
        SerialTerminal.ANSI subject = instance.new ANSI();
        subject.write((char) 0x1B);
        subject.write('[');
        subject.write('3');
        subject.write('1');
        subject.write(';');
        subject.write('4');
        subject.write('2');
        subject.write('m');
        subject.write('A');
        Assertions.assertEquals('A', instance.screen[0][0].character);
        Assertions.assertEquals(subject.colors[1], instance.screen[0][0].foreground.getRGB());
        Assertions.assertEquals(subject.colors[2], instance.screen[0][0].background.getRGB());
    }

}
