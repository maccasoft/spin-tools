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

import com.maccasoft.propeller.OutlineView.DefinesNode;
import com.maccasoft.propeller.OutlineView.DefinitionNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;
import com.maccasoft.propeller.spinc.CParser;
import com.maccasoft.propeller.spinc.CTokenStream;

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

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Node varNode = root.getChild(0);
        Assertions.assertEquals("/VAR0", view.getPath(varNode));

        Node varChildNode = varNode.getChild(0);
        Assertions.assertEquals("/VAR0", view.getPath(varChildNode));
        Assertions.assertEquals("/VAR0/a", view.getPath(varChildNode.getChild(0)));
        Assertions.assertEquals("/VAR0/b", view.getPath(varChildNode.getChild(1)));

        varChildNode = varNode.getChild(1);
        Assertions.assertEquals("/VAR0", view.getPath(varChildNode));
        Assertions.assertEquals("/VAR0/c", view.getPath(varChildNode.getChild(0)));
    }

    @Test
    void testPubComparer() {
        String text = ""
            + "PUB main\n"
            + "'\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/PUB main", view.getPath(root.getChild(0)));
    }

    @Test
    void testPubTreepathComparer() {
        String text = ""
            + "PUB main\n"
            + "'\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });

        Assertions.assertEquals("/PUB main", view.getPath(path0));
    }

    @Test
    void testDatComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/DAT0", view.getPath(root.getChild(0)));
        Assertions.assertEquals("/DAT0/driver", view.getPath(root.getChild(0).getChild(1)));
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

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/DAT0", view.getPath(root.getChild(0)));
        Assertions.assertEquals("/DAT0/driver1", view.getPath(root.getChild(0).getChild(1)));
        Assertions.assertEquals("/DAT1", view.getPath(root.getChild(1)));
        Assertions.assertEquals("/DAT1/driver2", view.getPath(root.getChild(1).getChild(1)));
    }

    @Test
    void testDatTreepathComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });
        TreePath path1 = new TreePath(new Object[] {
            root.getChild(0), root.getChild(0).getChild(1)
        });

        Assertions.assertEquals("/DAT0", view.getPath(path0));
        Assertions.assertEquals("/DAT0/driver", view.getPath(path1));
    }

    @Test
    void testIncludeComparer() {
        String text = ""
            + "#include <object>\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/<object>", view.getPath(root.getChild(0)));
    }

    @Test
    void testhelperNodesComparer() {
        DefinesNode defines = new DefinesNode(new Token(Token.KEYWORD, "include"));
        new DefinitionNode(defines, new Token(Token.KEYWORD, "<object>"), "object");

        Assertions.assertEquals("/<object>", view.getPath(defines.getChild(0)));
    }

    @Test
    void testFunctionComparer() {
        String text = ""
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/void main", view.getPath(root.getChild(0)));
    }

    @Test
    void testFunctionTreepathComparer() {
        String text = ""
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });

        Assertions.assertEquals("/void main", view.getPath(path0));
    }

}
