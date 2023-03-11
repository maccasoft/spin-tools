/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin2PreprocessorTest {

    @Test
    void testSingleMethod() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
    }

    @Test
    void testFirstMethodReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testMethodReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testCascadingMethodReferenceCount() {
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

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testMultipleMethodReferenceCount() {
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

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(2, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(2, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testUnreferencedMethodReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testUnusedObjectMethodsReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testObjectMethodsReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testObjectIndexMethodsReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    o[0].method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testCascadingObjectMethodsReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testKeepSingleMethod() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
    }

    @Test
    void testKeepFirstMethod() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testKeepReferencedMethods() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testKeepCascadingReferencedMethods() {
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

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(root.getChild(1)));
        Assertions.assertTrue(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testRemoveUnreferencedCascadingMethods() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testRemoveObjectMethods() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testKeepReferencedObjectMethods() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(text1.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testKeepCascadingReferencedObjectMethods() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(text1.getChild(0)));
        Assertions.assertTrue(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testDontKeepUnreferencedCascadingObjectMethods() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(text1.getChild(1)));
    }

    @Test
    void testSelfReferencingMethodCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testRemoveSelfReferencingMethod() {
        Node root = parseSource(""
            + "PUB main\n"
            + "\n"
            + "PUB method1\n"
            + "    method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertTrue(subject.isReferenced(root.getChild(0)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(1)));
        Assertions.assertFalse(subject.isReferenced(root.getChild(2)));
    }

    @Test
    void testPrivateMethodReferenceCount() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PRI method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(2, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testReferencedMethods() {
        Node root = parseSource(""
            + "PUB main\n"
            + "    method1\n"
            + "    method2\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PRI method2\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(2, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testPointerMethodReferenceCount() {
        Node root = parseSource(""
            + "PUB main() | a\n"
            + "    a := @method1\n"
            + "    method2()\n"
            + "\n"
            + "PUB method1()\n"
            + "\n"
            + "PRI method2()\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor();
        subject.process(new File("root"), root);
        subject.removeUnusedMethods();

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(2, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(1)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(2)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(root.getChild(2)).references.size());
    }

    @Test
    void testObjectMethodsPointerReferenceCount() {
        Node root = parseSource(""
            + "PUB main() | a\n"
            + "    a := @o.method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1()\n"
            + "\n"
            + "PUB method2()\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    @Test
    void testIndexedObjectMethodsPointerReferenceCount() {
        Node root = parseSource(""
            + "PUB main() | a\n"
            + "    a := @o[1].method1\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "\n"
            + "");
        Node text1 = parseSource(""
            + "PUB method1()\n"
            + "\n"
            + "PUB method2()\n"
            + "\n"
            + "");

        Spin2Preprocessor subject = new Spin2Preprocessor() {

            @Override
            protected Node getParsedObject(String fileName) {
                if ("text1.spin2".equals(fileName)) {
                    return text1;
                }
                return null;
            }

        };
        subject.process(new File("root"), root);

        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).count);
        Assertions.assertEquals(1, subject.referencedMethods.get(root.getChild(0)).references.size());

        Assertions.assertEquals(1, subject.referencedMethods.get(text1.getChild(0)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(0)).references.size());

        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).count);
        Assertions.assertEquals(0, subject.referencedMethods.get(text1.getChild(1)).references.size());
    }

    Node parseSource(String text) {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        return new Spin2Parser(stream).parse();
    }

}
