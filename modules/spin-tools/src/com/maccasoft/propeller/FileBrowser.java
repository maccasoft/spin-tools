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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.internal.ImageRegistry;

public class FileBrowser {

    Display display;
    TreeViewer viewer;

    File[] visiblePaths;
    Set<File> visibleParents;
    Set<String> visibleExtensions;
    Set<String> hiddenExtensions;

    public static class Model {

        public Model() {
        }

        public File[] getElements() {
            return File.listRoots();
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
            if (visibleParents.size() == 0) {
                return new File[0];
            }

            File[] input = ((Model) inputElement).getElements();

            // On non-Windows systems, skip root file
            if (!"win32".equals(SWT.getPlatform()) && input.length == 1) {
                return input[0].listFiles(visibleFoldersFilter);
            }

            List<File> elements = new ArrayList<>();
            for (int i = 0; i < input.length; i++) {
                if (visibleFoldersFilter.accept(input[i])) {
                    elements.add(input[i]);
                }
            }
            return elements.toArray(new File[elements.size()]);
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            return ((File) parentElement).listFiles(visibleExtensionsFilter);
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

    final FileFilter visibleFoldersFilter = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                if (visibleParents.contains(pathname)) {
                    return true;
                }
                if (visiblePaths != null) {
                    while ((pathname = pathname.getParentFile()) != null) {
                        for (int i = 0; i < visiblePaths.length; i++) {
                            if (pathname.equals(visiblePaths[i])) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

    };

    final FileFilter visibleExtensionsFilter = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            if (name.startsWith(".")) {
                return false;
            }

            if (pathname.isDirectory()) {
                if (visibleParents.contains(pathname)) {
                    return true;
                }
                if (visiblePaths != null) {
                    while ((pathname = pathname.getParentFile()) != null) {
                        for (int i = 0; i < visiblePaths.length; i++) {
                            if (pathname.equals(visiblePaths[i])) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            if (visiblePaths != null && visiblePaths.length != 0) {
                boolean result = false;
                while ((pathname = pathname.getParentFile()) != null && result == false) {
                    for (int i = 0; i < visiblePaths.length; i++) {
                        if (pathname.equals(visiblePaths[i])) {
                            result = true;
                            break;
                        }
                    }
                }
                if (result == false) {
                    return false;
                }
            }

            int i = name.lastIndexOf('.');
            if (i != -1) {
                String ext = name.substring(i).toLowerCase();
                if (hiddenExtensions.contains(ext)) {
                    return false;
                }
                return visibleExtensions.size() == 0 || visibleExtensions.contains(ext);
            }

            return true;
        }

    };

    public FileBrowser(Composite parent) {
        display = parent.getDisplay();

        visibleParents = new HashSet<>();

        visibleExtensions = new HashSet<>();

        hiddenExtensions = new HashSet<>();
        hiddenExtensions.add(".bin");
        hiddenExtensions.add(".binary");

        viewer = new TreeViewer(parent);
        viewer.setLabelProvider(new StyledCellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                File element = (File) cell.getElement();

                String name = element.getName();
                if (name == null || name.length() == 0) {
                    name = element.getPath();
                }
                cell.setText(name);

                cell.setImage(ImageRegistry.getImageForFile(element));
            }

        });
        viewer.setComparator(new FileComparator());
        viewer.setContentProvider(new FileTreeContentProvider());
        viewer.setUseHashlookup(true);
        viewer.setInput(new Model());

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
    }

    public void setVisiblePaths(File[] paths) {
        this.visiblePaths = paths;

        visibleParents.clear();
        for (int i = 0; i < paths.length; i++) {
            File f = paths[i];
            while (f != null) {
                visibleParents.add(f.getAbsoluteFile());
                f = f.getParentFile();
            }
        }

        viewer.getControl().setRedraw(false);
        try {
            viewer.refresh();
        } finally {
            viewer.getControl().setRedraw(true);
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
        viewer.getControl().setRedraw(false);
        try {
            viewer.setSelection(new StructuredSelection(file), true);

            while (viewer.getSelection().isEmpty()) {
                if (!file.isDirectory()) {
                    if ((file = file.getParentFile().getAbsoluteFile()) == null) {
                        return;
                    }
                }
                viewer.setSelection(new StructuredSelection(file), true);
            }
        } finally {
            viewer.getControl().setRedraw(true);
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

    public boolean getVisible() {
        return viewer.getControl().getVisible();
    }

    public void setVisible(boolean visible) {
        viewer.getControl().setVisible(visible);
    }

    public void setVisibleExtensions(String[] extensions) {
        visibleExtensions.clear();
        for (int i = 0; i < extensions.length; i++) {
            visibleExtensions.add(extensions[i].toLowerCase());
        }
    }

    public void setHiddenExtensions(String[] extensions) {
        hiddenExtensions.clear();
        for (int i = 0; i < extensions.length; i++) {
            hiddenExtensions.add(extensions[i].toLowerCase());
        }
    }

    public void setExpandedPaths(File[] paths) {
        viewer.setExpandedElements((Object[]) paths);
    }

    public File[] getExpandedPaths() {
        Object[] elements = viewer.getExpandedElements();
        File[] result = new File[elements.length];
        System.arraycopy(elements, 0, result, 0, elements.length);
        return result;
    }

    public void setBackground(Color color) {
        viewer.getControl().setBackground(color);
    }

    public void setForeground(Color color) {
        viewer.getControl().setForeground(color);
    }

}
