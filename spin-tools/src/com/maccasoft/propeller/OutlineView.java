/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TypeDefinitionNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class OutlineView {

    TreeViewer viewer;

    class Includes extends Node {

        public Includes() {
            super(Collections.singleton(new Token(0, "include directives")));
        }

    }

    final ITreeContentProvider contentProvider = new ITreeContentProvider() {

        @Override
        public Object[] getElements(Object inputElement) {
            List<Object> list = new ArrayList<Object>();

            Includes includes = new Includes();

            ((Node) inputElement).accept(new NodeVisitor() {

                @Override
                public void visitDirective(DirectiveNode node) {
                    if (node instanceof DirectiveNode.IncludeNode) {
                        includes.addChild(node);
                    }
                    if (node instanceof DirectiveNode.DefineNode) {
                        list.add(node);
                    }
                }

                @Override
                public boolean visitConstants(ConstantsNode node) {
                    if (node.getTokenCount() == 0) {
                        node.accept(new NodeVisitor() {

                            @Override
                            public void visitTypeDefinition(TypeDefinitionNode node) {
                                if (node.getIdentifier() != null) {
                                    list.add(node);
                                }
                            }

                            @Override
                            public void visitConstant(ConstantNode node) {
                                if (node.getIdentifier() != null) {
                                    list.add(node);
                                }
                            }

                        });
                    }
                    else if (!node.isExclude()) {
                        list.add(node);
                    }
                    return true;
                }

                @Override
                public boolean visitVariables(VariablesNode node) {
                    list.add(node);
                    return false;
                }

                @Override
                public boolean visitObjects(ObjectsNode node) {
                    list.add(node);
                    return true;
                }

                @Override
                public boolean visitMethod(MethodNode node) {
                    if (node.getName() != null) {
                        list.add(node);
                    }
                    return true;
                }

                @Override
                public boolean visitFunction(FunctionNode node) {
                    list.add(node);
                    return true;
                }

                @Override
                public boolean visitData(DataNode node) {
                    list.add(node);
                    return true;
                }

            });

            if (includes.getChildCount() != 0) {
                list.add(0, includes);
            }

            return list.toArray(new Object[list.size()]);
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            List<Object> list = new ArrayList<Object>();

            if (parentElement instanceof Includes) {
                list.addAll(((Includes) parentElement).getChilds());
                return list.toArray(new Object[list.size()]);
            }

            ((Node) parentElement).accept(new NodeVisitor() {

                @Override
                public void visitConstant(ConstantNode node) {
                    if (node.identifier != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitTypeDefinition(TypeDefinitionNode node) {
                    if (node.identifier != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitVariable(VariableNode node) {
                    if (node.identifier != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitObject(ObjectNode node) {
                    if (node.name != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitDataLine(DataLineNode node) {
                    if (node.label != null && !node.label.getText().startsWith(".") && !node.label.getText().startsWith(":")) {
                        list.add(node);
                    }
                }

            });

            return list.toArray(new Object[list.size()]);
        }

        @Override
        public Object getParent(Object element) {
            return ((Node) element).getParent();
        }

        @Override
        public boolean hasChildren(Object element) {
            return (element instanceof ConstantsNode) || (element instanceof VariablesNode) || (element instanceof ObjectsNode) || (element instanceof DataNode) || (element instanceof Includes);
        }

    };

    final OwnerDrawLabelProvider labelProvider = new StyledCellLabelProvider() {

        StringBuilder sb;
        List<StyleRange> styles;

        @Override
        public void update(ViewerCell cell) {
            sb = new StringBuilder();
            styles = new ArrayList<StyleRange>();

            try {
                Node element = (Node) cell.getElement();
                if (element instanceof ConstantsNode || element instanceof VariablesNode || element instanceof ObjectsNode || element instanceof DataNode) {
                    decorateBlockStart(element, cell);
                }
                else if (element instanceof Includes) {
                    Includes node = (Includes) element;
                    appendText(node.getStartToken().getText(), blockStyle);
                }
                else if (element instanceof DirectiveNode.IncludeNode) {
                    DirectiveNode.IncludeNode node = (DirectiveNode.IncludeNode) element;
                    if (!node.isExclude()) {
                        appendText(node.getFile().getText(), stringStyle);
                    }
                }
                else if (element instanceof DirectiveNode.DefineNode) {
                    DirectiveNode.DefineNode node = (DirectiveNode.DefineNode) element;
                    appendText("# ", blockStyle);
                    if (node.getIdentifier() != null) {
                        sb.append(node.getIdentifier().getText());
                    }
                }
                else if (element instanceof TypeDefinitionNode) {
                    TypeDefinitionNode node = (TypeDefinitionNode) element;
                    sb.append(node.getIdentifier().getText());
                }
                else if (element instanceof VariableNode) {
                    VariableNode node = (VariableNode) element;
                    if (node.identifier != null) {
                        if (sb.length() != 0) {
                            sb.append(" ");
                        }
                        sb.append(node.identifier.getText());
                    }
                    if (node.type != null) {
                        sb.append(" : ");
                        appendText(node.type.getText(), methodReturnStyle);
                    }
                    else if (node.getParent() instanceof VariablesNode) {
                        VariablesNode parent = (VariablesNode) node.getParent();
                        int index = 0;
                        while (!(parent.getChild(index) instanceof VariableNode)) {
                            index++;
                        }
                        Token type = ((VariableNode) parent.getChild(index)).type;
                        if (type != null) {
                            sb.append(" : ");
                            appendText(type.getText(), methodReturnStyle);
                        }
                    }
                }
                else if (element instanceof ObjectNode) {
                    ObjectNode node = (ObjectNode) element;
                    if (node.name != null) {
                        sb.append(node.name.getText());
                        sb.append(" : ");
                        if (node.file != null) {
                            appendText(node.getFileName(), stringStyle);
                        }
                    }
                }
                else if (element instanceof MethodNode) {
                    decorateMethod((MethodNode) element, cell);
                }
                else if (element instanceof FunctionNode) {
                    decorateFunction((FunctionNode) element, cell);
                }
                else if (element instanceof DataLineNode) {
                    DataLineNode node = (DataLineNode) element;
                    sb.append(node.label.getText());
                }
                else {
                    Node node = element;
                    sb.append(node.getStartToken().getText());
                }

                cell.setText(sb.toString());
                if (element.isExclude()) {
                    StyleRange range = new StyleRange(commentStyle);
                    range.start = 0;
                    range.length = sb.length();
                    cell.setStyleRanges(new StyleRange[] {
                        range
                    });
                }
                else {
                    cell.setStyleRanges(styles.toArray(new StyleRange[0]));
                }
            } catch (Exception e) {
                // Do nothing
            }

            super.update(cell);
        }

        void decorateMethod(MethodNode node, ViewerCell cell) {
            if (node.getType() != null) {
                appendText(node.getType().getText().toUpperCase(), blockStyle);
            }
            if (node.getName() != null) {
                if (sb.length() != 0) {
                    sb.append(" ");
                }
                sb.append(node.getName().getText());

                sb.append("(");
                boolean first = true;
                for (Node child : node.getParameters()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    appendText(child.getText(), methodLocalStyle);
                    first = false;
                }
                sb.append(")");

                if (node.getReturnVariables().size() != 0) {
                    sb.append(" : ");
                    first = true;
                    for (Node child : node.getReturnVariables()) {
                        if (!first) {
                            sb.append(", ");
                        }
                        appendText(child.getText(), methodReturnStyle);
                        first = false;
                    }
                }
            }
        }

        void decorateBlockStart(Node node, ViewerCell cell) {
            String text = "";
            if (node.getStartToken() != null) {
                text = node.getStartToken().getText().toUpperCase();
            }
            appendText(text, blockStyle);

            if (node instanceof DataNode) {
                String name = ((DataNode) node).getName();
                if (name != null) {
                    sb.append(" ");
                    sb.append(name);
                }
            }

            String comment = node.getDescription();
            if (comment != null) {
                while (comment.startsWith("'")) {
                    comment = comment.substring(1);
                }
                comment = comment.trim();
                if (!comment.isEmpty()) {
                    sb.append(" ");
                    appendText(comment, commentStyle);
                }
            }
        }

        void decorateFunction(FunctionNode node, ViewerCell cell) {
            if (node.getIdentifier() != null) {
                if (sb.length() != 0) {
                    sb.append(" ");
                }
                appendText(node.getIdentifier().getText(), blockStyle);

                sb.append("(");
                boolean first = true;
                for (FunctionNode.ParameterNode child : node.getParameters()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    if (child.type != null) {
                        appendText(child.type.getText(), methodLocalStyle);
                    }
                    else {
                        appendText(child.identifier.getText(), methodLocalStyle);
                    }
                    first = false;
                }
                sb.append(")");

                if (node.getType() != null) {
                    sb.append(" : ");
                    appendText(node.getType().getText(), methodReturnStyle);
                }
            }
        }

        void appendText(String text, TextStyle style) {
            StyleRange range = new StyleRange(style);
            range.start = sb.length();
            range.length = text.length();

            sb.append(text);
            styles.add(range);
        }

    };

    final IElementComparer elementComparer = new IElementComparer() {

        @Override
        public int hashCode(Object element) {
            return getPath(element).hashCode();
        }

        @Override
        public boolean equals(Object a, Object b) {
            return getPath(a).equals(getPath(b));
        }

    };

    TextStyle commentStyle;
    TextStyle blockStyle;
    TextStyle methodLocalStyle;
    TextStyle methodReturnStyle;
    TextStyle typeStyle;
    TextStyle stringStyle;
    Font fontBold;

    public OutlineView(Composite parent) {
        viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);
        viewer.setComparer(elementComparer);

        FontData[] fd = viewer.getControl().getFont().getFontData();
        fd[0].setStyle(SWT.BOLD);
        fontBold = new Font(viewer.getControl().getDisplay(), fd[0]);

        commentStyle = new TextStyle(null, new Color(0x7E, 0x7E, 0x7E), null);
        blockStyle = new TextStyle(fontBold, null, null);
        methodLocalStyle = new TextStyle(null, new Color(0x80, 0x80, 0x00), null);
        methodReturnStyle = new TextStyle(null, new Color(0x90, 0x00, 0x00), null);
        typeStyle = new TextStyle(fontBold, null, null);
        stringStyle = new TextStyle(null, new Color(0x7E, 0x00, 0x7E), null);
    }

    public void setLayoutData(Object layoutData) {
        viewer.getControl().setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return viewer.getControl().getLayoutData();
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        viewer.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        viewer.removeSelectionChangedListener(listener);
    }

    public void addOpenListener(IOpenListener listener) {
        viewer.addOpenListener(listener);
    }

    public void removeOpenListener(IOpenListener listener) {
        viewer.addOpenListener(listener);
    }

    public Node getInput() {
        return (Node) viewer.getInput();
    }

    public void setInput(Node node) {
        viewer.getTree().setRedraw(false);
        try {
            Object[] expandedElements = viewer.getExpandedElements();
            viewer.setInput(node);
            viewer.setExpandedElements(expandedElements);
        } finally {
            viewer.getTree().setRedraw(true);
        }
    }

    public void setSelection(IStructuredSelection selection) {
        viewer.setSelection(selection, true);
    }

    public Control getControl() {
        return viewer.getControl();
    }

    public TreeViewer getViewer() {
        return viewer;
    }

    String getPath(Object element) {

        if (element instanceof TreePath) {
            element = ((TreePath) element).getLastSegment();
        }
        return ((Node) element).getPath();
    }

    String getPathText(Object element) {
        Node node = (Node) element;

        if (node instanceof DirectiveNode.IncludeNode) {
            DirectiveNode.IncludeNode directive = (DirectiveNode.IncludeNode) node;
            if (directive.getFile() != null) {
                return directive.getFile().getText().toUpperCase();
            }
        }
        if (node instanceof DirectiveNode.DefineNode) {
            DirectiveNode.DefineNode directive = (DirectiveNode.DefineNode) node;
            if (directive.getIdentifier() != null) {
                return directive.getIdentifier().getText().toUpperCase();
            }
        }

        if (node instanceof TypeDefinitionNode) {
            if (((TypeDefinitionNode) node).getIdentifier() != null) {
                return ((TypeDefinitionNode) node).getIdentifier().getText().toUpperCase();
            }
        }

        String text = node.getTokenCount() != 0 ? node.getStartToken().getText().toUpperCase() : "";

        if (node instanceof VariableNode) {
            if (((VariableNode) node).identifier != null) {
                text = ((VariableNode) node).identifier.getText().toUpperCase();
            }
        }
        else if (node instanceof FunctionNode) {
            if (((FunctionNode) node).identifier != null) {
                text += " " + ((FunctionNode) node).identifier.getText().toUpperCase();
            }
        }
        else if (node instanceof MethodNode) {
            if (((MethodNode) node).name != null) {
                text += " " + ((MethodNode) node).name.getText().toUpperCase();
            }
        }

        return text;
    }

    public void setVisible(boolean visible) {
        viewer.getControl().setVisible(visible);
    }

    public boolean getVisible() {
        return viewer.getControl().getVisible();
    }

    public void dispose() {
        viewer.getControl().dispose();
    }

    public void refresh() {
        viewer.refresh();
    }

    public void setBackground(Color color) {
        viewer.getControl().setBackground(color);
    }

    public void setForeground(Color color) {
        viewer.getControl().setForeground(color);
    }

}
