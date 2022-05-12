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
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;

import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.VariablesNode;

public class OutlineView {

    TreeViewer viewer;

    final ITreeContentProvider contentProvider = new ITreeContentProvider() {

        @Override
        public Object[] getElements(Object inputElement) {
            List<Object> list = new ArrayList<Object>();

            for (Node node : ((Node) inputElement).getChilds()) {
                list.add(node);
            }

            return list.toArray(new Object[list.size()]);
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            List<Object> list = new ArrayList<Object>();

            if (hasChildren(parentElement)) {
                for (Node child : ((Node) parentElement).getChilds()) {
                    if (child instanceof DataLineNode) {
                        if (((DataLineNode) child).label != null) {
                            if (!((DataLineNode) child).label.getText().startsWith(".")) {
                                list.add(child);
                            }
                        }
                    }
                    else {
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
                else if (element instanceof ObjectNode) {
                    ObjectNode node = (ObjectNode) element;
                    sb.append(node.getText());
                }
                else if (element instanceof MethodNode) {
                    decorateMethod((MethodNode) element, cell);
                }
                else if (element instanceof DataLineNode) {
                    sb.append(((DataLineNode) element).label.getText());
                }
                else {
                    sb.append(((Node) element).getStartToken().getText());
                }

                cell.setText(sb.toString());
                cell.setStyleRanges(styles.toArray(new StyleRange[0]));
            } catch (Exception e) {
                // Do nothing
            }

            super.update(cell);
        }

        void decorateMethod(MethodNode node, ViewerCell cell) {
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

        void decorateSectionStart(Node node, ViewerCell cell) {
            String text = node.getStartToken().getText().toUpperCase();
            appendText(text, sectionStyle);

            if (node.getDocument().size() != 0) {
                sb.append(" ");

                text = node.getDocument().get(0).getText();
                while (text.startsWith("'")) {
                    text = text.substring(1);
                }
                appendText(text.trim(), commentStyle);
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
    TextStyle sectionStyle;
    TextStyle methodLocalStyle;
    TextStyle methodReturnStyle;
    Font fontBold;

    public OutlineView(Composite parent) {
        viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);
        viewer.setComparer(elementComparer);

        FontData[] fd = viewer.getControl().getFont().getFontData();
        fd[0].setStyle(SWT.BOLD);
        fontBold = new Font(viewer.getControl().getDisplay(), fd[0]);

        commentStyle = new TextStyle(null, ColorRegistry.getColor(0x7E, 0x7E, 0x7E), null);
        sectionStyle = new TextStyle(fontBold, ColorRegistry.getColor(0x00, 0x00, 0x00), null);
        methodLocalStyle = new TextStyle(null, ColorRegistry.getColor(0x80, 0x80, 0x00), null);
        methodReturnStyle = new TextStyle(null, ColorRegistry.getColor(0x90, 0x00, 0x00), null);
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
        String text = node.getTokenCount() != 0 ? node.getStartToken().getText() : "";
        if (node instanceof MethodNode && ((MethodNode) node).name != null) {
            text += " " + ((MethodNode) node).name.getText();
        }

        if (!(node instanceof MethodNode)) {
            if (node.getParent() != null && node.getParent().getParent() == null) {
                text += String.valueOf(node.getParent().getChilds().indexOf(node));
            }
        }
        return text;
    }

}
