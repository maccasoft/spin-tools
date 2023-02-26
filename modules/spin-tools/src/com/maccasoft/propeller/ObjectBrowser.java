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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.internal.ImageRegistry;

public class ObjectBrowser {

    Display display;
    TreeViewer viewer;

    public ObjectBrowser(Composite parent) {
        display = parent.getDisplay();

        viewer = new TreeViewer(parent);
        viewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                return ((ObjectTree) element).getName();
            }

            @Override
            public Image getImage(Object element) {
                return ImageRegistry.getImageForFile(((ObjectTree) element).getFile());
            }

        });
        viewer.setContentProvider(new ITreeContentProvider() {

            @Override
            public void dispose() {

            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            }

            @Override
            public Object[] getElements(Object inputElement) {
                return (ObjectTree[]) inputElement;
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                return ((ObjectTree) parentElement).getChilds();
            }

            @Override
            public Object getParent(Object element) {
                return ((ObjectTree) element).getParent();
            }

            @Override
            public boolean hasChildren(Object element) {
                return ((ObjectTree) element).hasChildren();
            }

        });
        viewer.setUseHashlookup(true);

        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection.isEmpty()) {
                    return;
                }
                // TODO
            }

        });
    }

    public void addSelectionChangedListener(ISelectionChangedListener l) {
        viewer.addSelectionChangedListener(l);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener l) {
        viewer.removeSelectionChangedListener(l);
    }

    public void addOpenListener(IOpenListener l) {
        viewer.addOpenListener(l);
    }

    public void removeOpenListener(IOpenListener l) {
        viewer.removeOpenListener(l);
    }

    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public void setInput(ObjectTree input) {
        if (input == null) {
            viewer.setInput(new ObjectTree[0]);
        }
        else {
            viewer.setInput(new ObjectTree[] {
                input
            });
            viewer.expandAll();
        }
    }

    public boolean getVisible() {
        return viewer.getControl().getVisible();
    }

    public void setVisible(boolean visible) {
        viewer.getControl().setVisible(visible);
    }

}
