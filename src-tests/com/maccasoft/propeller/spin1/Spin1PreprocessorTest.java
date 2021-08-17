/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;

class Spin1PreprocessorTest {

    @Test
    void testKeepSingleMethod() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals("main", ((MethodNode) root.getChild(0)).getName().getText());
    }

    @Test
    void testKeepFirstMethod() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals("main", ((MethodNode) root.getChild(0)).getName().getText());
    }

    @Test
    void testKeepReferencedMethods() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main\n"
            + "\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Assertions.assertEquals(2, root.getChilds().size());
        Assertions.assertEquals("main", ((MethodNode) root.getChild(0)).getName().getText());
        Assertions.assertEquals("method1", ((MethodNode) root.getChild(1)).getName().getText());
    }

    @Test
    void testKeepCascadingReferencedMethods() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main\n"
            + "\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Assertions.assertEquals(3, root.getChilds().size());
        Assertions.assertEquals("main", ((MethodNode) root.getChild(0)).getName().getText());
        Assertions.assertEquals("method1", ((MethodNode) root.getChild(1)).getName().getText());
        Assertions.assertEquals("method2", ((MethodNode) root.getChild(2)).getName().getText());
    }

    @Test
    void testRemoveObjectMethods() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "");
        sources.put("text1.spin", ""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");
        Assertions.assertEquals(0, text1.getChilds().size());
    }

    @Test
    void testKeepReferencedObjectMethods() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    o.method1\n"
            + "\n"
            + "");
        sources.put("text1.spin", ""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");
        Assertions.assertEquals(1, text1.getChilds().size());
        Assertions.assertEquals("method1", ((MethodNode) text1.getChild(0)).getName().getText());
    }

    @Test
    void testKeepCascadingReferencedObjectMethods() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    o.method1\n"
            + "\n"
            + "");
        sources.put("text1.spin", ""
            + "PUB method1\n"
            + "\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");
        Assertions.assertEquals(2, text1.getChilds().size());
        Assertions.assertEquals("method1", ((MethodNode) text1.getChild(0)).getName().getText());
        Assertions.assertEquals("method2", ((MethodNode) text1.getChild(1)).getName().getText());
    }

    @Test
    void testKeepCascadingReferencedChildObjectMethods() {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    o.method1\n"
            + "\n"
            + "");
        sources.put("text1.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "    o.method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB method1\n"
            + "\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Map<String, Node> objects = parseSources(sources);

        Node root = objects.get("main.spin");
        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");
        Assertions.assertEquals(2, text1.getChilds().size());
        Assertions.assertEquals("method1", ((MethodNode) text1.getChild(1)).getName().getText());

        Node text2 = objects.get("text2.spin");
        Assertions.assertEquals(2, text1.getChilds().size());
        Assertions.assertEquals("method1", ((MethodNode) text2.getChild(0)).getName().getText());
        Assertions.assertEquals("method2", ((MethodNode) text2.getChild(1)).getName().getText());
    }

    Map<String, Node> parseSources(Map<String, String> sources) {
        Map<String, Node> objects = new HashMap<String, Node>();
        for (Entry<String, String> entry : sources.entrySet()) {
            Spin1TokenStream stream = new Spin1TokenStream(entry.getValue());
            objects.put(entry.getKey(), new Spin1Parser(stream).parse());
        }
        return objects;
    }

}
