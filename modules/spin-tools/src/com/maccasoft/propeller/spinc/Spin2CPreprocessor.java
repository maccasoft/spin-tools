/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectTree;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.Token;

public class Spin2CPreprocessor {

    class ObjectTreeVisitor extends NodeVisitor {

        File file;
        ObjectTreeVisitor parent;
        ObjectTree objectTree;

        public ObjectTreeVisitor(File file, ObjectTree objectTree) {
            this.file = file;
            this.objectTree = objectTree;
        }

        public ObjectTreeVisitor(ObjectTreeVisitor parent, File file, ListOrderedMap<File, Node> list) {
            this.parent = parent;
            this.file = file;
            this.objectTree = new ObjectTree(file, file.getName());

            this.parent.objectTree.add(objectTree);
        }

        @Override
        public void visitDirective(DirectiveNode node) {
            if (node instanceof DirectiveNode.IncludeNode) {
                DirectiveNode.IncludeNode include = (DirectiveNode.IncludeNode) node;
                if (include.getFile() != null) {
                    addObject(include.getFile());
                }
            }
        }

        @Override
        public void visitObject(ObjectNode node) {
            if (node.name == null || node.file == null) {
                return;
            }
            addObject(node.file);
        }

        void addObject(Token fileToken) {
            String objectFileName = fileToken.getText().substring(1, fileToken.getText().length() - 1);

            File objectFile = compiler.getFile(objectFileName);
            if (objectFile == null) {
                objectFile = compiler.getFile(objectFileName + ".spin2");
            }
            if (objectFile == null) {
                objectFile = new File(objectFileName + ".spin2");
            }

            ObjectTreeVisitor p = parent;
            while (p != null) {
                if (p.file.equals(objectFile)) {
                    throw new CompilerException(file.getName(), "\"" + objectFile.getName() + "\" illegal circular reference", fileToken);
                }
                p = p.parent;
            }

            Node objectRoot = objects.get(objectFile);
            if (objectRoot == null) {
                objectRoot = compiler.getParsedObject(objectFile.getName());
            }
            if (objectRoot == null) {
                return;
            }

            objects.remove(objectFile);
            objects.put(0, objectFile, objectRoot);

            objectRoot.accept(new ObjectTreeVisitor(this, objectFile, objects));
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.instruction != null && node.parameters.size() != 0) {
                if ("FILE".equalsIgnoreCase(node.instruction.getText()) || "INCLUDE".equalsIgnoreCase(node.instruction.getText())) {
                    for (Node parameterNode : node.parameters) {
                        String fileName = parameterNode.getText().substring(1, parameterNode.getText().length() - 1);
                        File file = compiler.getFile(fileName);
                        if (file == null) {
                            file = new File(fileName);
                        }
                        objectTree.add(new ObjectTree(file, fileName));
                    }
                }
            }
        }

    }

    Spin2CCompiler compiler;
    ListOrderedMap<File, Node> objects;
    ObjectTree objectTree;

    public Spin2CPreprocessor(Spin2CCompiler compiler) {
        this.compiler = compiler;
    }

    public void process(File rootFile, Node root) {
        objects = ListOrderedMap.listOrderedMap(new HashMap<>());
        objectTree = new ObjectTree(rootFile, rootFile.getName());

        root.accept(new ObjectTreeVisitor(rootFile, objectTree));
    }

    public ListOrderedMap<File, Node> getObjects() {
        return objects;
    }

    public ObjectTree getObjectTree() {
        return objectTree;
    }

}