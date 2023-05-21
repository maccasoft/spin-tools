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

import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenMarker;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenMarker;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

public class SourceTokenMarkerTest {

    @Test
    public void testMethodProposals() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL);
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(0);

        List<IContentProposal> result = subject.getMethodProposals(context, "");

        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals("method", result.get(0).getLabel());
        Assertions.assertEquals("method()", result.get(0).getContent());
        Assertions.assertEquals(8, result.get(0).getCursorPosition());
    }

    @Test
    public void testMethodArgumentsProposals() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "    \n"
            + "PUB method(a, b)\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL);
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(0);

        List<IContentProposal> result = subject.getMethodProposals(context, "");

        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals("method", result.get(0).getLabel());
        Assertions.assertEquals("method(a, b)", result.get(0).getContent());
        Assertions.assertEquals(7, result.get(0).getCursorPosition());
    }

    @Test
    public void testMatchingMethodsProposals() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "    \n"
            + "PUB abcd_1234()\n"
            + "    \n"
            + "PUB abcd_5678()\n"
            + "    \n"
            + "PUB efgh_1234()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL);
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(0);

        List<IContentProposal> result = subject.getMethodProposals(context, "abcd_");

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("abcd_1234", result.get(0).getLabel());
        Assertions.assertEquals("abcd_1234()", result.get(0).getContent());
        Assertions.assertEquals(11, result.get(0).getCursorPosition());

        Assertions.assertEquals("abcd_5678", result.get(1).getLabel());
        Assertions.assertEquals("abcd_5678()", result.get(1).getContent());
        Assertions.assertEquals(11, result.get(1).getCursorPosition());
    }

    @Test
    public void testObjectProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object : \"object\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String objectText = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object": {
                        Spin2TokenStream stream = new Spin2TokenStream(objectText);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getMethodProposals(context, "");

        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals("object", result.get(0).getLabel());
        Assertions.assertEquals("object", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());
    }

    @Test
    public void testObjectMethodProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object : \"object\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String objectText = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object": {
                        Spin2TokenStream stream = new Spin2TokenStream(objectText);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getMethodProposals(context, "object.");

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("object.null", result.get(0).getLabel());
        Assertions.assertEquals("null()", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());

        Assertions.assertEquals("object.method", result.get(1).getLabel());
        Assertions.assertEquals("method()", result.get(1).getContent());
        Assertions.assertEquals(8, result.get(1).getCursorPosition());
    }

    @Test
    public void testMatchingObjectProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object : \"object\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String objectText = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object": {
                        Spin2TokenStream stream = new Spin2TokenStream(objectText);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getMethodProposals(context, "obj");

        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals("object", result.get(0).getLabel());
        Assertions.assertEquals("object", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());
    }

    @Test
    public void testMatchingObjectMethodProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object : \"object\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String objectText = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object": {
                        Spin2TokenStream stream = new Spin2TokenStream(objectText);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getMethodProposals(context, "object.me");

        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals("object.method", result.get(0).getLabel());
        Assertions.assertEquals("method()", result.get(0).getContent());
        Assertions.assertEquals(8, result.get(0).getCursorPosition());
    }

    @Test
    public void testObjectMethodAddressProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object : \"object\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String objectText = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object": {
                        Spin2TokenStream stream = new Spin2TokenStream(objectText);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getMethodProposals(context, "@object.");

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("object.null", result.get(0).getLabel());
        Assertions.assertEquals("null()", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());

        Assertions.assertEquals("object.method", result.get(1).getLabel());
        Assertions.assertEquals("method()", result.get(1).getContent());
        Assertions.assertEquals(8, result.get(1).getCursorPosition());
    }

    @Test
    public void testObjectArrayMethodProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object[2] : \"object1\"\n"
            + "    object2   : \"object2\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String object1Text = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";
        String object2Text = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method2()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object1": {
                        Spin2TokenStream stream = new Spin2TokenStream(object1Text);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                    case "object2": {
                        Spin2TokenStream stream = new Spin2TokenStream(object2Text);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getMethodProposals(context, "object[0].");

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("object.null", result.get(0).getLabel());
        Assertions.assertEquals("null()", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());

        Assertions.assertEquals("object.method", result.get(1).getLabel());
        Assertions.assertEquals("method()", result.get(1).getContent());
        Assertions.assertEquals(8, result.get(1).getCursorPosition());
    }

    @Test
    public void testObjectArrayExpressionMethodProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object[2] : \"object1\"\n"
            + "    object2   : \"object2\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String object1Text = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method()\n"
            + "    \n"
            + "";
        String object2Text = ""
            + "PUB null()\n"
            + "    \n"
            + "PUB method2()\n"
            + "    \n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object1": {
                        Spin2TokenStream stream = new Spin2TokenStream(object1Text);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                    case "object2": {
                        Spin2TokenStream stream = new Spin2TokenStream(object2Text);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getMethodProposals(context, "object[byte[a][0]].");

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("object.null", result.get(0).getLabel());
        Assertions.assertEquals("null()", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());

        Assertions.assertEquals("object.method", result.get(1).getLabel());
        Assertions.assertEquals("method()", result.get(1).getContent());
        Assertions.assertEquals(8, result.get(1).getCursorPosition());
    }

    @Test
    public void testSpin1ObjectConstantsProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object : \"object\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String objectText = ""
            + "CON\n"
            + "\n"
            + "    PIN_RX = 1\n"
            + "    PIN_TX = 2\n"
            + "";

        SourceTokenMarker subject = new Spin1TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object": {
                        Spin1TokenStream stream = new Spin1TokenStream(objectText);
                        Spin1Parser parser = new Spin1Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getConstantsProposals(context, "object#");

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("object#PIN_RX", result.get(0).getLabel());
        Assertions.assertEquals("PIN_RX", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());

        Assertions.assertEquals("object#PIN_TX", result.get(1).getLabel());
        Assertions.assertEquals("PIN_TX", result.get(1).getContent());
        Assertions.assertEquals(6, result.get(1).getCursorPosition());
    }

    @Test
    public void testSpin2ObjectConstantsProposals() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    object : \"object\"\n"
            + "\n"
            + "PUB main()\n"
            + "    \n"
            + "";
        String objectText = ""
            + "CON\n"
            + "\n"
            + "    PIN_RX = 1\n"
            + "    PIN_TX = 2\n"
            + "";

        SourceTokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL) {

            @Override
            protected Node getObjectTree(String fileName) {
                switch (fileName) {
                    case "object": {
                        Spin2TokenStream stream = new Spin2TokenStream(objectText);
                        Spin2Parser parser = new Spin2Parser(stream);
                        return parser.parse();
                    }
                }
                return null;
            }

        };
        subject.refreshTokens(text);

        Node context = subject.getRoot().getChild(1);

        List<IContentProposal> result = subject.getConstantsProposals(context, "object.");

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("object.PIN_RX", result.get(0).getLabel());
        Assertions.assertEquals("PIN_RX", result.get(0).getContent());
        Assertions.assertEquals(6, result.get(0).getCursorPosition());

        Assertions.assertEquals("object.PIN_TX", result.get(1).getLabel());
        Assertions.assertEquals("PIN_TX", result.get(1).getContent());
        Assertions.assertEquals(6, result.get(1).getCursorPosition());
    }

}
