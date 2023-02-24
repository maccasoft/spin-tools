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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1PreprocessorTest {

    @Test
    void testSingleMethod() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
    }

    @Test
    void testFirstMethodReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testMethodReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testCascadingMethodReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testMultipleMethodReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(2, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(2, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testUnreferencedMethodReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testUnusedObjectMethodsReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testObjectMethodsReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testObjectIndexMethodsReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    o[0].method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testCascadingObjectMethodsReferenceCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testKeepSingleMethod() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
    }

    @Test
    void testKeepFirstMethod() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testKeepReferencedMethods() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testKeepCascadingReferencedMethods() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(root.getChild(1)));
        Assertions.assertTrue(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testRemoveUnreferencedCascadingMethods() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testRemoveObjectMethods() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testKeepReferencedObjectMethods() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(text1.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testKeepCascadingReferencedObjectMethods() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(text1.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testDontKeepUnreferencedCascadingObjectMethods() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        objects.put("text1.spin", parseSource(""
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + ""));

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Node text1 = objects.get("text1.spin");

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testSelfReferencingMethodCount() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testRemoveSelfReferencingMethod() {
        Map<String, Node> objects = new HashMap<String, Node>();

        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin1Preprocessor subject = new Spin1Preprocessor(root, objects);
        subject.collectReferencedMethods();
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    Node parseSource(String text) {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        return new Spin1Parser(stream).parse();
    }
}
