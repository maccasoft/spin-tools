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

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.internal.ImageRegistry;

public class FileBrowser {

    Display display;
    TreeViewer viewer;

    String[] visiblePaths;
    Set<String> visibleParents;
    Set<String> visibleExtensions;

    class FileLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            return ((File) element).getName();
        }

        @Override
        public Image getImage(Object element) {
            if (((File) element).isDirectory()) {
                return ImageRegistry.getImageFromResources("folder.png");
            }
            return ImageRegistry.getImageFromResources("document-code.png");
        }

    }

    class FileTreeContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {

        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

        @Override
        public Object[] getElements(Object inputElement) {
            return (File[]) inputElement;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            File[] childs = ((File) parentElement).listFiles(visibleExtensionsFilter);
            return childs;
        }

        @Override
        public Object getParent(Object element) {
            return ((File) element).getParentFile();
        }

        @Override
        public boolean hasChildren(Object element) {
            return ((File) element).isDirectory();
        }

    }

    class FileComparator extends ViewerComparator {

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (((File) e1).isDirectory() && !((File) e2).isDirectory()) {
                return -1;
            }
            if (!((File) e1).isDirectory() && ((File) e2).isDirectory()) {
                return 1;
            }
            return ((File) e1).getName().compareToIgnoreCase(((File) e2).getName());
        }

    }

    final FileFilter visibleExtensionsFilter = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            if (name.startsWith(".")) {
                return false;
            }
            if (pathname.isDirectory()) {
                if (visibleParents.contains(pathname.getAbsolutePath())) {
                    return true;
                }
                if (visiblePaths != null) {
                    while ((pathname = pathname.getParentFile()) != null) {
                        for (int i = 0; i < visiblePaths.length; i++) {
                            if (pathname.getAbsolutePath().equals(visiblePaths[i])) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
            return visibleExtensions.size() == 0 || accept(name);
        }

        boolean accept(String name) {
            int i = name.lastIndexOf('.');
            if (i != -1) {
                return visibleExtensions.contains(name.substring(i).toLowerCase());
            }
            return false;
        }

    };

    public FileBrowser(Composite parent) {
        display = parent.getDisplay();

        viewer = new TreeViewer(parent);
        viewer.setLabelProvider(new FileLabelProvider());
        viewer.setComparator(new FileComparator());
        viewer.setContentProvider(new FileTreeContentProvider());
        viewer.setUseHashlookup(true);

        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection.isEmpty()) {
                    return;
                }
                File file = (File) selection.getFirstElement();
                if (file.isDirectory()) {
                    viewer.setExpandedState(file, !viewer.getExpandedState(file));
                }
            }

        });
        viewer.addTreeListener(new ITreeViewerListener() {

            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                final Object element = event.getElement();
                display.asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (viewer == null || viewer.getControl().isDisposed()) {
                            return;
                        }
                        viewer.refresh(element);
                    }
                });
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {

            }

        });

        visibleExtensions = new HashSet<String>();
        visibleExtensions.add(".spin");
        visibleExtensions.add(".spin2");
    }

    public void setInput(File[] input) {
        File currentSelection = getSelection();

        viewer.setInput(input);

        if (currentSelection != null) {
            setSelection(currentSelection);
        }
    }

    public void setRoots(String[] roots) {
        this.visiblePaths = roots;
        this.visibleParents = new HashSet<String>();

        for (int i = 0; i < roots.length; i++) {
            File f = new File(roots[i]);
            while (f != null) {
                this.visibleParents.add(f.getAbsolutePath());
                f = f.getParentFile();
            }
        }

        File currentSelection = getSelection();

        viewer.refresh();

        if (currentSelection != null) {
            setSelection(currentSelection);
        }
    }

    public File getSelection() {
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        if (selection.isEmpty()) {
            return null;
        }
        return (File) selection.getFirstElement();
    }

    public void setSelection(File file) {
        viewer.setSelection(new StructuredSelection(file), true);

        while (viewer.getSelection().isEmpty()) {
            file = file.getAbsoluteFile().getParentFile();
            if (file == null) {
                return;
            }
            viewer.setSelection(new StructuredSelection(file), true);
        }

        if (file.isDirectory()) {
            viewer.setExpandedState(file, true);
        }
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

    public void refresh(File file) {
        if (file == null) {
            return;
        }
        viewer.refresh(file.getParentFile());
    }

    public void refresh() {
        viewer.refresh();
    }

    public void setVisible(boolean visible) {
        viewer.getControl().setVisible(visible);
    }

}
