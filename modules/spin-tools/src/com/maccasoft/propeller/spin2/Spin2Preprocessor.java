
package com.maccasoft.propeller.spin2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectTree;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;

public class Spin2Preprocessor {

    class MethodReference {
        int count;
        MethodNode node;
        List<MethodNode> references = new ArrayList<MethodNode>();

        public MethodReference(MethodNode node) {
            this.node = node;
        }

    }

    class ObjectTreeVisitor extends NodeVisitor {

        File file;
        ObjectTreeVisitor parent;
        ObjectTree objectTree;

        public ObjectTreeVisitor(File file, ObjectTree objectTree) {
            this.file = file;
            this.objectTree = objectTree;
        }

        public ObjectTreeVisitor(ObjectTreeVisitor parent, File file, ListOrderedMap<String, Node> list) {
            this.parent = parent;
            this.file = file;
            this.objectTree = new ObjectTree(file, file.getName());

            this.parent.objectTree.add(objectTree);
        }

        @Override
        public boolean visitMethod(MethodNode node) {
            referencedMethods.put(node, new MethodReference(node));
            return false;
        }

        @Override
        public void visitObject(ObjectNode node) {
            if (node.name == null || node.file == null) {
                return;
            }

            String objectFileName = node.file.getText().substring(1, node.file.getText().length() - 1);
            File objectFile = getFile(objectFileName);
            if (objectFile == null) {
                objectFile = getFile(objectFileName + ".spin2");
            }
            if (objectFile == null) {
                objectFile = new File(objectFileName + ".spin2");
            }

            ObjectTreeVisitor p = parent;
            while (p != null) {
                if (p.file.equals(objectFile)) {
                    throw new CompilerException(file.getName(), "\"" + objectFile.getName() + "\" illegal circular reference", node.file);
                }
                p = p.parent;
            }

            Node objectRoot = objects.get(objectFile.getName());
            if (objectRoot == null) {
                objectRoot = getParsedObject(objectFile.getName());
            }
            if (objectRoot == null) {
                return;
            }

            objects.remove(objectFile.getName());
            objects.put(0, objectFile.getName(), objectRoot);

            objectRoot.accept(new ObjectTreeVisitor(this, objectFile, objects));
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.instruction != null && node.parameters.size() != 0) {
                if ("FILE".equalsIgnoreCase(node.instruction.getText()) || "INCLUDE".equalsIgnoreCase(node.instruction.getText())) {
                    for (Node parameterNode : node.parameters) {
                        String fileName = parameterNode.getText().substring(1, parameterNode.getText().length() - 1);
                        File file = getFile(fileName);
                        if (file == null) {
                            file = new File(fileName);
                        }
                        objectTree.add(new ObjectTree(file, fileName));
                    }
                }
            }
        }

    }

    ListOrderedMap<String, Node> objects;
    Map<MethodNode, MethodReference> referencedMethods;

    ObjectTree objectTree;

    public Spin2Preprocessor() {

    }

    public void process(File rootFile, Node root) {
        objects = ListOrderedMap.listOrderedMap(new HashMap<>());
        referencedMethods = new HashMap<>();
        objectTree = new ObjectTree(rootFile, rootFile.getName());

        root.accept(new ObjectTreeVisitor(rootFile, objectTree));

        countMethodReferences(true, root);
        for (Node node : objects.values()) {
            countMethodReferences(false, node);
        }
    }

    protected File getFile(String name) {
        return null;
    }

    protected Node getParsedObject(String fileName) {
        return null;
    }

    void countMethodReferences(boolean keepFirst, Node root) {
        Set<String> objectNames = new HashSet<>();
        Map<String, MethodNode> symbols = new HashMap<String, MethodNode>();

        root.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode node) {
                if (node.file == null) {
                    return;
                }
                String fileName = node.file.getText().substring(1, node.file.getText().length() - 1);
                Node objectRoot = objects.get(fileName);
                if (objectRoot == null) {
                    objectRoot = objects.get(fileName + ".spin2");
                }
                if (objectRoot != null) {
                    String prefix = node.name.getText() + ".";
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public boolean visitMethod(MethodNode node) {
                            if (node.getName() != null) {
                                symbols.put(prefix + node.getName().getText(), node);
                                symbols.put("@" + prefix + node.getName().getText(), node);
                            }
                            return false;
                        }

                    });
                }
                objectNames.add(node.name.getText());
                objectNames.add("@" + node.name.getText());
            }

            @Override
            public boolean visitMethod(MethodNode node) {
                if (node.getName() != null) {
                    symbols.put(node.getName().getText(), node);
                    symbols.put("@" + node.getName().getText(), node);
                }
                return false;
            }

        });

        root.accept(new NodeVisitor() {

            boolean first = keepFirst;

            @Override
            public boolean visitMethod(MethodNode node) {
                MethodReference parent = referencedMethods.get(node);
                if (first) {
                    parent.count++;
                    first = false;
                }
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        markTokens(parent, child);
                    }
                }
                return false;
            }

            void markTokens(MethodReference parent, Node node) {
                Iterator<Token> iter = node.getTokens().iterator();
                while (iter.hasNext()) {
                    Token token = iter.next();

                    MethodNode methodNode = symbols.get(token.getText());
                    if (methodNode == null && objectNames.contains(token.getText())) {
                        String prefix = token.getText();
                        if (iter.hasNext()) {
                            token = iter.next();
                            if ("[".equals(token.getText())) {
                                markIndexTokens(iter, parent);
                                if (iter.hasNext()) {
                                    token = iter.next();
                                }
                            }
                            if (token.getText().startsWith(".")) {
                                methodNode = symbols.get(prefix + token.getText());
                            }
                        }
                    }
                    if (methodNode != null && methodNode != parent.node) {
                        MethodReference ref = referencedMethods.get(methodNode);
                        ref.count++;
                        parent.references.add(methodNode);
                    }
                }
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        markTokens(parent, child);
                    }
                }
            }

            void markIndexTokens(Iterator<Token> iter, MethodReference parent) {
                while (iter.hasNext()) {
                    Token token = iter.next();
                    if ("]".equals(token.getText())) {
                        break;
                    }

                    MethodNode methodNode = symbols.get(token.getText());
                    if (methodNode == null && objectNames.contains(token.getText())) {
                        String prefix = token.getText();
                        if (iter.hasNext()) {
                            token = iter.next();
                            if ("[".equals(token.getText())) {
                                markIndexTokens(iter, parent);
                            }
                            if (token.getText().startsWith(".")) {
                                methodNode = symbols.get(prefix + token.getText());
                            }
                        }
                    }
                    if (methodNode != null && methodNode != parent.node) {
                        MethodReference ref = referencedMethods.get(methodNode);
                        ref.count++;
                        parent.references.add(methodNode);
                    }
                }
            }

        });
    }

    public void removeUnusedMethods() {
        boolean repeat;

        do {
            repeat = false;
            Iterator<Entry<MethodNode, MethodReference>> iter = referencedMethods.entrySet().iterator();
            while (iter.hasNext()) {
                MethodReference ref = iter.next().getValue();
                if (ref.count == 0) {
                    for (MethodNode child : ref.references) {
                        MethodReference childRef = referencedMethods.get(child);
                        childRef.count--;
                    }
                    iter.remove();
                    repeat = true;
                }
            }
        } while (repeat);
    }

    public boolean isReferenced(Node node) {
        return referencedMethods.containsKey(node);
    }

    public ListOrderedMap<String, Node> getObjects() {
        return objects;
    }

    public ObjectTree getObjectTree() {
        return objectTree;
    }

}
