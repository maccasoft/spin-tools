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

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

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
        viewer.getTree().addMouseMoveListener(new MouseMoveListener() {

            TreeItem lastItem;

            @Override
            public void mouseMove(MouseEvent e) {
                TreeItem item = viewer.getTree().getItem(new Point(e.x, e.y));
                if (item == lastItem) {
                    return;
                }
                if (item != null && (item.getData() instanceof ObjectTree)) {
                    viewer.getTree().setToolTipText(((ObjectTree) item.getData()).getFile().getAbsolutePath());
                }
                else {
                    viewer.getTree().setToolTipText(null);
                }
                lastItem = item;
            }

        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection.isEmpty()) {
                    return;
                }
                ObjectTree object = (ObjectTree) selection.getFirstElement();
                if (object != null) {
                    viewer.getTree().setToolTipText(object.getFile().getAbsolutePath());
                }
                else {
                    viewer.getTree().setToolTipText(null);
                }
            }

        });
        viewer.setUseHashlookup(true);
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
