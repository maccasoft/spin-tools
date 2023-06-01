/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class OutlineView {

    TreeViewer viewer;

    static class DefinesNode extends Node {

        public DefinesNode(Token token) {
            tokens.add(new Token(0, "#" + token.getText()));
        }

    }

    static class DefinitionNode extends Node {

        String text;

        public DefinitionNode(Node parent, Token identifier, String text) {
            super(parent);
            tokens.add(identifier);
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }

    }

    final ITreeContentProvider contentProvider = new ITreeContentProvider() {

        @Override
        public Object[] getElements(Object inputElement) {
            List<Object> list = new ArrayList<Object>();

            DefinesNode defines = null;
            DefinesNode includes = null;

            for (Node node : ((Node) inputElement).getChilds()) {
                if (node instanceof DirectiveNode.IncludeNode) {
                    DirectiveNode.IncludeNode directive = (DirectiveNode.IncludeNode) node;
                    if (directive.getFile() != null) {
                        if (includes == null) {
                            includes = new DefinesNode(directive.getDirective());
                        }
                        String text = directive.getFile().getText().substring(1, directive.getFile().getText().length() - 1);
                        DefinitionNode definition = new DefinitionNode(includes, directive.getFile(), text);
                        definition.setData("__skip__", node.getData("__skip__"));
                    }
                }
                else if (node instanceof DirectiveNode.DefineNode) {
                    DirectiveNode.DefineNode directive = (DirectiveNode.DefineNode) node;
                    if (directive.getIdentifier() != null) {
                        if (defines == null) {
                            defines = new DefinesNode(directive.getDirective());
                        }
                        DefinitionNode definition = new DefinitionNode(defines, directive.getIdentifier(), directive.getIdentifier().getText());
                        definition.setData("__skip__", node.getData("__skip__"));
                    }
                }
                else if (!(node instanceof DirectiveNode)) {
                    list.add(node);
                    if (node instanceof VariableNode) {
                        list.addAll(node.getChilds());
                    }
                }
            }

            if (defines != null) {
                list.add(0, defines);
            }
            if (includes != null) {
                list.add(0, includes);
            }

            return list.toArray(new Object[list.size()]);
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            List<Object> list = new ArrayList<Object>();

            if (hasChildren(parentElement)) {
                for (Node child : ((Node) parentElement).getChilds()) {
                    if (child instanceof ConstantNode) {
                        if (((ConstantNode) child).identifier != null) {
                            list.add(child);
                        }
                    }
                    else if (child instanceof DataLineNode) {
                        if (((DataLineNode) child).label != null) {
                            if (!((DataLineNode) child).label.getText().startsWith(".")) {
                                list.add(child);
                            }
                        }
                    }
                    else if (!(child instanceof DirectiveNode)) {
                        list.add(child);
                    }
                }
            }

            return list.toArray(new Object[list.size()]);
        }

        @Override
        public Object getParent(Object element) {
            return ((Node) element).getParent();
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof DefinesNode) {
                return true;
            }
            return (element instanceof ConstantsNode) || (element instanceof VariablesNode) || (element instanceof ObjectsNode) || (element instanceof DataNode);
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
                Object element = cell.getElement();
                if (element instanceof ConstantsNode || element instanceof VariablesNode || element instanceof ObjectsNode || element instanceof DataNode) {
                    decorateSectionStart((Node) element, cell);
                }
                else if (element instanceof DefinesNode) {
                    String text = ((Node) element).getStartToken().getText();
                    if (skipNode((Node) element)) {
                        appendText(text, commentStyle);
                    }
                    else {
                        appendText(text, sectionStyle);
                    }
                }
                else if (element instanceof DefinitionNode) {
                    if (skipNode((Node) element)) {
                        StyleRange range = new StyleRange(commentStyle);
                        range.start = sb.length();
                        sb.append(((Node) element).getText());
                        range.length = sb.length() - range.start;
                        styles.add(range);
                    }
                    else {
                        sb.append(((Node) element).getText());
                    }
                }
                else if (element instanceof VariableNode) {
                    VariableNode node = (VariableNode) element;
                    if (skipNode(node)) {
                        StyleRange range = new StyleRange(commentStyle);
                        range.start = sb.length();
                        if (node.identifier != null) {
                            if (sb.length() != 0) {
                                sb.append(" ");
                            }
                            sb.append(node.identifier.getText());
                        }
                        if (node.type != null) {
                            sb.append(" : ");
                            sb.append(node.type.getText());
                        }
                        else if (node.getParent() instanceof VariableNode) {
                            Token type = ((VariableNode) node.getParent()).type;
                            if (type != null) {
                                sb.append(" : ");
                                sb.append(type.getText());
                            }
                        }
                        range.length = sb.length() - range.start;
                        styles.add(range);
                    }
                    else {
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
                        else if (node.getParent() instanceof VariableNode) {
                            Token type = ((VariableNode) node.getParent()).type;
                            if (type != null) {
                                sb.append(" : ");
                                appendText(type.getText(), methodReturnStyle);
                            }
                        }
                    }
                }
                else if (element instanceof ObjectNode) {
                    ObjectNode node = (ObjectNode) element;
                    if (node.name != null) {
                        if (skipNode(node)) {
                            StyleRange range = new StyleRange(commentStyle);
                            range.start = sb.length();
                            sb.append(node.name.getText());
                            sb.append(" : ");
                            if (node.file != null) {
                                sb.append(node.getFileName());
                            }
                            range.length = sb.length() - range.start;
                            styles.add(range);
                        }
                        else {
                            sb.append(node.name.getText());
                            sb.append(" : ");
                            if (node.file != null) {
                                appendText(node.getFileName(), stringStyle);
                            }
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
                    if (skipNode((DataLineNode) element)) {
                        StyleRange range = new StyleRange(commentStyle);
                        range.start = sb.length();
                        sb.append(((DataLineNode) element).label.getText());
                        range.length = sb.length() - range.start;
                        styles.add(range);
                    }
                    else {
                        sb.append(((DataLineNode) element).label.getText());
                    }
                }
                else {
                    if (skipNode((Node) element)) {
                        StyleRange range = new StyleRange(commentStyle);
                        range.start = sb.length();
                        sb.append(((Node) element).getStartToken().getText());
                        range.length = sb.length() - range.start;
                        styles.add(range);
                    }
                    else {
                        sb.append(((Node) element).getStartToken().getText());
                    }
                }

                cell.setText(sb.toString());
                cell.setStyleRanges(styles.toArray(new StyleRange[0]));
            } catch (Exception e) {
                // Do nothing
            }

            super.update(cell);
        }

        void decorateMethod(MethodNode node, ViewerCell cell) {
            if (skipNode(node)) {
                StyleRange range = new StyleRange(commentStyle);
                range.start = sb.length();

                if (node.getType() != null) {
                    sb.append(node.getType().getText().toUpperCase());
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
                        sb.append(child.getText());
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
                            sb.append(child.getText());
                            first = false;
                        }
                    }
                }
                range.length = sb.length() - range.start;
                styles.add(range);
            }
            else {
                if (node.getType() != null) {
                    appendText(node.getType().getText().toUpperCase(), sectionStyle);
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
        }

        void decorateSectionStart(Node node, ViewerCell cell) {
            String text = "";
            if (node.getStartToken() != null) {
                text = node.getStartToken().getText().toUpperCase();
            }
            appendText(text, sectionStyle);

            if (node.getTokenCount() > 1) {
                text = node.getToken(1).getText();
                while (text.startsWith("'")) {
                    text = text.substring(1);
                }
                text = text.trim();
                if (!text.isEmpty()) {
                    sb.append(" ");
                    appendText(text, commentStyle);
                }
            }
        }

        void decorateFunction(FunctionNode node, ViewerCell cell) {
            if (node.getIdentifier() != null) {
                if (skipNode(node)) {
                    StyleRange range = new StyleRange(commentStyle);
                    range.start = sb.length();

                    if (sb.length() != 0) {
                        sb.append(" ");
                    }
                    sb.append(node.getIdentifier().getText());

                    sb.append("(");
                    boolean first = true;
                    for (FunctionNode.ParameterNode child : node.getParameters()) {
                        if (!first) {
                            sb.append(", ");
                        }
                        if (child.type != null) {
                            sb.append(child.type.getText());
                        }
                        else {
                            sb.append(child.identifier.getText());
                        }
                        first = false;
                    }
                    sb.append(")");

                    if (node.getType() != null) {
                        sb.append(" : ");
                        sb.append(node.getType().getText());
                    }

                    range.length = sb.length() - range.start;
                    styles.add(range);
                }
                else {
                    if (sb.length() != 0) {
                        sb.append(" ");
                    }
                    appendText(node.getIdentifier().getText(), sectionStyle);

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
        }

        void appendText(String text, TextStyle style) {
            StyleRange range = new StyleRange(style);
            range.start = sb.length();
            range.length = text.length();

            sb.append(text);
            styles.add(range);
        }

        boolean skipNode(Node node) {
            Boolean skip = (Boolean) node.getData("__skip__");
            return skip != null && skip.booleanValue();
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
    TextStyle sectionStyle;
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
        viewer.addTreeListener(new ITreeViewerListener() {

            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                if (event.getElement() instanceof Node) {
                    ((Node) event.getElement()).setData("__treeExpanded__", true);
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                if (event.getElement() instanceof Node) {
                    ((Node) event.getElement()).setData("__treeExpanded__", false);
                }
            }

        });

        FontData[] fd = viewer.getControl().getFont().getFontData();
        fd[0].setStyle(SWT.BOLD);
        fontBold = new Font(viewer.getControl().getDisplay(), fd[0]);

        commentStyle = new TextStyle(null, ColorRegistry.getColor(0x7E, 0x7E, 0x7E), null);
        sectionStyle = new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0x00), null);
        methodLocalStyle = new TextStyle(null, ColorRegistry.getColor(0x80, 0x80, 0x00), null);
        methodReturnStyle = new TextStyle(null, ColorRegistry.getColor(0x90, 0x00, 0x00), null);
        typeStyle = new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0x00), null);
        stringStyle = new TextStyle(null, ColorRegistry.getColor(0x7E, 0x00, 0x7E), null);
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
            for (Node child : node.getChilds()) {
                Boolean expanded = (Boolean) child.getData("__treeExpanded__");
                if (expanded != null) {
                    viewer.setExpandedState(child, expanded.booleanValue());
                }
            }
        } finally {
            viewer.getTree().setRedraw(true);
        }
    }

    public void setSelection(IStructuredSelection selection) {
        Object node = selection.getFirstElement();
        if (node instanceof DirectiveNode.IncludeNode) {
            DirectiveNode.IncludeNode directive = (DirectiveNode.IncludeNode) node;
            if (directive.getFile() != null) {
                DefinesNode includes = new DefinesNode(directive.getDirective());
                String text = directive.getFile().getText().substring(1, directive.getFile().getText().length() - 1);
                selection = new StructuredSelection(new DefinitionNode(includes, directive.getFile(), text));
            }
        }
        else if (node instanceof DirectiveNode.DefineNode) {
            DirectiveNode.DefineNode directive = (DirectiveNode.DefineNode) node;
            if (directive.getIdentifier() != null) {
                DefinesNode defines = new DefinesNode(directive.getDirective());
                selection = new StructuredSelection(new DefinitionNode(defines, directive.getIdentifier(), directive.getIdentifier().getText()));
            }
        }
        viewer.setSelection(selection, true);
    }

    public Control getControl() {
        return viewer.getControl();
    }

    public TreeViewer getViewer() {
        return viewer;
    }

    String getPath(Object element) {
        StringBuilder sb = new StringBuilder();

        if (element instanceof TreePath) {
            TreePath path = (TreePath) element;
            for (int i = 0; i < path.getSegmentCount(); i++) {
                sb.append("/");
                sb.append(getPathText(path.getSegment(i)));
            }
        }
        else {
            Node node = (Node) element;
            if (node != null) {
                do {
                    sb.insert(0, getPathText(node));
                    sb.insert(0, "/");
                    node = node.getParent();
                } while (node != null && node.getParent() != null);
            }
        }

        return sb.toString();
    }

    String getPathText(Object element) {
        Node node = (Node) element;

        if (node instanceof DirectiveNode.IncludeNode) {
            DirectiveNode.IncludeNode directive = (DirectiveNode.IncludeNode) node;
            if (directive.getFile() != null) {
                return directive.getFile().getText();
            }
        }
        if (node instanceof DirectiveNode.DefineNode) {
            DirectiveNode.DefineNode directive = (DirectiveNode.DefineNode) node;
            if (directive.getIdentifier() != null) {
                return directive.getIdentifier().getText();
            }
        }

        String text = node.getTokenCount() != 0 ? node.getStartToken().getText() : "";

        if (node instanceof VariableNode) {
            if (((VariableNode) node).identifier != null) {
                text = ((VariableNode) node).identifier.getText();
            }
        }
        else if (node instanceof FunctionNode) {
            if (((FunctionNode) node).identifier != null) {
                text += " " + ((FunctionNode) node).identifier.getText();
            }
        }
        else if (node instanceof MethodNode) {
            if (((MethodNode) node).name != null) {
                text += " " + ((MethodNode) node).name.getText();
            }
        }
        else if (!(node instanceof DefinitionNode)) {
            if (node.getParent() != null && node.getParent().getParent() == null) {
                text += String.valueOf(node.getParent().getChilds().indexOf(node));
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

}
