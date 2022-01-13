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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class OutlineView {

    TreeViewer viewer;

    class OutlineContentProvider implements ITreeContentProvider {

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

    }

    class OutlineLabelProvider implements ILabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public String getText(Object element) {
            StringBuilder sb = new StringBuilder();

            if (element instanceof VariableNode) {
                VariableNode node = (VariableNode) element;
                if (node.getIdentifier() != null) {
                    sb.append(node.getIdentifier().getText());
                    if (node.getSize() != null) {
                        sb.append("[");
                        sb.append(node.getSize().getText());
                        sb.append("]");
                    }
                }
                if (node.getType() != null) {
                    sb.append(": ");
                    sb.append(node.getType().getText().toLowerCase());
                }
            }
            else if (element instanceof ObjectNode) {
                ObjectNode node = (ObjectNode) element;
                sb.append(node.getText());
            }
            else if (element instanceof MethodNode) {
                MethodNode node = (MethodNode) element;
                if (node.getType() != null) {
                    sb.append(node.getType().getText().toUpperCase());
                }
                if (node.getName() != null) {
                    if (sb.length() != 0) {
                        sb.append(" ");
                    }
                    sb.append(node.getName().getText());
                    sb.append("(");
                    for (Node child : node.getParameters()) {
                        sb.append(child.getText());
                    }
                    sb.append(")");
                    if (node.getReturnVariables().size() != 0) {
                        sb.append(" :");
                        for (Node child : node.getReturnVariables()) {
                            sb.append(" ");
                            sb.append(child.getText());
                        }
                    }
                }
            }
            else if (element instanceof DataLineNode) {
                sb.append(((DataLineNode) element).label.getText());
            }
            else {
                sb.append(((Node) element).getStartToken().getText().toUpperCase());
            }

            return sb.toString();
        }

    }

    public OutlineView(Composite parent) {
        viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new OutlineContentProvider());
        viewer.setLabelProvider(new OutlineLabelProvider());
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

    public void setInput(Node node) {
        viewer.setInput(node);
    }

    public void setSelection(IStructuredSelection selection) {
        viewer.setSelection(selection, true);
    }
}
