/*
 * Copyright (c) 26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import java.util.regex.Matcher;

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
class HTMLStyledTextDecoratorTest {

    Display display;
    Shell shell;
    StyledText styledText;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
        shell = new Shell(display);
    }

    @BeforeEach
    void setUp() {
        styledText = new StyledText(shell, SWT.NONE);
    }

    @AfterEach
    void tearDown() {
        while (display.readAndDispatch()) {

        }
        styledText.dispose();
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
    void testRegularExpressions1() {
        Matcher m = HTMLStyledTextDecorator.tagPattern.matcher("<p>");
        Assertions.assertTrue(m.find());
        Assertions.assertEquals(2, m.groupCount());
        Assertions.assertEquals("<p>", m.group(0));
        Assertions.assertEquals("p", m.group(1));
    }

    @Test
    void testRegularExpressions2() {
        Matcher m = HTMLStyledTextDecorator.tagPattern.matcher("</p>");
        Assertions.assertTrue(m.find());
        Assertions.assertEquals(2, m.groupCount());
        Assertions.assertEquals("</p>", m.group(0));
        Assertions.assertEquals("/p", m.group(1));
    }

    @Test
    void testRegularExpressions3() {
        Matcher m = HTMLStyledTextDecorator.tagPattern.matcher("<br />");
        Assertions.assertTrue(m.find());
        Assertions.assertEquals(2, m.groupCount());
        Assertions.assertEquals("<br />", m.group(0));
        Assertions.assertEquals("br", m.group(1));
    }

    @Test
    void testRegularExpressions4() {
        Matcher m = HTMLStyledTextDecorator.attrValueAll.matcher("style=\"width:100%;\"");
        Assertions.assertTrue(m.find());
        Assertions.assertEquals(7, m.groupCount());
        Assertions.assertEquals("style=\"width:100%;\"", m.group(0));
        Assertions.assertEquals("style", m.group(1));
        Assertions.assertEquals("=\"width:100%;\"", m.group(2));
        Assertions.assertEquals("\"width:100%;\"", m.group(3));
        Assertions.assertEquals("width:100%;", m.group(4));
    }

    @Test
    void testRegularExpressions5() {
        Matcher m = HTMLStyledTextDecorator.attrValueAll.matcher("class=\"header bold\"");
        Assertions.assertTrue(m.find());
        Assertions.assertEquals(7, m.groupCount());
        Assertions.assertEquals("class=\"header bold\"", m.group(0));
        Assertions.assertEquals("class", m.group(1));
        Assertions.assertEquals("=\"header bold\"", m.group(2));
        Assertions.assertEquals("\"header bold\"", m.group(3));
        Assertions.assertEquals("header bold", m.group(4));
    }

    @Test
    void testFullDocument() {
        HTMLStyledTextDecorator subject = new HTMLStyledTextDecorator(styledText);
        subject.setText(
            "<div class=\"header\">pub main()</div>" +
                "<p>Method description</p>" +
                "<p class=\"subtitle\">Parameters</p>" +
                "<div><span class\"params\">pin<span> - Pin number.</div>" +
                "<div><span class\"params\">delay<span> - Delay (ms.).</div>" +
                "<p class=\"subtitle\">Returns</p>" +
                "<div><span class\"returns\">cog_id<span> - Running cog number.</div>" +
                "<p>Notes.</p>"
        );

        Assertions.assertEquals("""
            pub main()
            
            Method description
            
            Parameters
            
            pin- Pin number.
            delay- Delay (ms.).
            
            Returns
            
            cog_id- Running cog number.
            
            Notes.
            
            """, styledText.getText());
    }

}
