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

import org.eclipse.jface.viewers.TreePath;
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

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spinc.CParser;

@TestInstance(Lifecycle.PER_CLASS)
class OutlineViewTest {

    Display display;
    Shell shell;
    OutlineView view;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        shell = new Shell(display);
        view = new OutlineView(shell);
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
    void testVarComparer() {
        String text = ""
            + "VAR\n"
            + "  long a, b\n"
            + "  word c\n"
            + "";

        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        Node varNode = root.getChild(0);
        Assertions.assertEquals("/VAR0", view.getPath(varNode));

        Node varChildNode = varNode.getChild(0);
        Assertions.assertEquals("/VAR0/0", view.getPath(varChildNode));
        Assertions.assertEquals("/VAR0/0/A", view.getPath(varChildNode.getChild(0)));
        Assertions.assertEquals("/VAR0/0/B", view.getPath(varChildNode.getChild(1)));

        varChildNode = varNode.getChild(1);
        Assertions.assertEquals("/VAR0/1", view.getPath(varChildNode));
        Assertions.assertEquals("/VAR0/1/C", view.getPath(varChildNode.getChild(0)));
    }

    @Test
    void testPubComparer() {
        String text = ""
            + "PUB main\n"
            + "'\n"
            + "";

        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        Assertions.assertEquals("/PUB MAIN", view.getPath(root.getChild(0)));
    }

    @Test
    void testPubTreepathComparer() {
        String text = ""
            + "PUB main\n"
            + "'\n"
            + "";

        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });

        Assertions.assertEquals("/PUB MAIN", view.getPath(path0));
    }

    @Test
    void testDatComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver\n"
            + "";

        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        Assertions.assertEquals("/DAT0", view.getPath(root.getChild(0)));
        Assertions.assertEquals("/DAT0/DRIVER", view.getPath(root.getChild(0).getChild(1)));
    }

    @Test
    void testMultipleDatComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver1\n"
            + "'\n"
            + "DAT         org     $000\n"
            + "'\n"
            + "driver2\n"
            + "";

        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        Assertions.assertEquals("/DAT0", view.getPath(root.getChild(0)));
        Assertions.assertEquals("/DAT0/DRIVER1", view.getPath(root.getChild(0).getChild(1)));
        Assertions.assertEquals("/DAT1", view.getPath(root.getChild(1)));
        Assertions.assertEquals("/DAT1/DRIVER2", view.getPath(root.getChild(1).getChild(1)));
    }

    @Test
    void testDatTreepathComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver\n"
            + "";

        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });
        TreePath path1 = new TreePath(new Object[] {
            root.getChild(0), root.getChild(0).getChild(1)
        });

        Assertions.assertEquals("/DAT0", view.getPath(path0));
        Assertions.assertEquals("/DAT0/DRIVER", view.getPath(path1));
    }

    @Test
    void testIncludeComparer() {
        String text = ""
            + "#include <object>\n"
            + "";

        CParser parser = new CParser(text);
        Node root = parser.parse();

        Assertions.assertEquals("/<OBJECT>", view.getPath(root.getChild(0)));
    }

    @Test
    void testFunctionComparer() {
        String text = ""
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CParser parser = new CParser(text);
        Node root = parser.parse();

        Assertions.assertEquals("/VOID MAIN", view.getPath(root.getChild(0)));
    }

    @Test
    void testFunctionTreepathComparer() {
        String text = ""
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CParser parser = new CParser(text);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });

        Assertions.assertEquals("/VOID MAIN", view.getPath(path0));
    }

}
