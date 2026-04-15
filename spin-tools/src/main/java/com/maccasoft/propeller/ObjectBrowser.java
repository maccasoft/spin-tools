/*
 * Copyright (c) 2021-2025 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.maccasoft.propeller.internal.ImageRegistry;

public class ObjectBrowser {

    Display display;
    TreeViewer viewer;

    Font font;
    Font fontBold;
    TextStyle topObjectStyle;

    boolean topObject;
    String topObjectFolder;

    public ObjectBrowser(Composite parent) {
        display = parent.getDisplay();

        viewer = new TreeViewer(parent);
        viewer.setUseHashlookup(true);

        FontData[] fd = viewer.getControl().getFont().getFontData();
        fd[0].setStyle(SWT.BOLD);
        fontBold = new Font(display, fd[0]);
        topObjectStyle = new TextStyle(fontBold, null, null);

        viewer.setLabelProvider(new StyledCellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                ObjectTree element = (ObjectTree) cell.getElement();
                String text = element.getName();

                cell.setText(text);
                if (element.getParent() == null && topObject) {
                    StyleRange range = new StyleRange(topObjectStyle);
                    range.start = 0;
                    range.length = text.length();
                    cell.setStyleRanges(new StyleRange[] {
                        range
                    });
                }

                File file = element.getFile();
                boolean altImage = false;
                if (topObjectFolder != null) {
                    String filePath = file.getAbsolutePath();
                    if (!filePath.startsWith(topObjectFolder)) {
                        altImage = true;
                    }
                }
                Image image = ImageRegistry.getImageForFile(file.getName() + "-object", altImage);
                if (image == null) {
                    image = ImageRegistry.getImageForFile(file, false);
                }
                cell.setImage(image);
            }

        });
        viewer.setContentProvider(new ITreeContentProvider() {

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
        viewer.getTree().addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                fontBold.dispose();
            }
        });

        Preferences preferences = Preferences.getInstance();
        if (preferences.getWindowFont() != null) {
            FontData fontData = StringConverter.asFontData(preferences.getWindowFont());
            fontData.setStyle(SWT.NONE);
            updateFontsFrom(fontData);
        }

        viewer.getControl().addDisposeListener((e) -> {
            if (font != null) {
                font.dispose();
            }
            if (fontBold != null) {
                fontBold.dispose();
            }
        });
    }

    public void setLayoutData(Object data) {
        viewer.getControl().setLayoutData(data);
    }

    public Object getLayoutData() {
        return viewer.getControl().getLayoutData();
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

    public void setInput(ObjectTree input, boolean topObject) {
        viewer.getControl().setRedraw(false);
        try {
            this.topObject = topObject;
            if (input == null) {
                topObjectFolder = null;
                viewer.setInput(new ObjectTree[0]);
            }
            else {
                topObjectFolder = input.getFile().getAbsoluteFile().getParent() + File.separator;
                viewer.setInput(new ObjectTree[] {
                    input
                });
                viewer.expandAll();
            }
        } finally {
            viewer.getControl().setRedraw(true);
        }
    }

    public boolean getVisible() {
        return viewer.getControl().getVisible();
    }

    public void setVisible(boolean visible) {
        viewer.getControl().setVisible(visible);
    }

    public boolean isDisposed() {
        return viewer.getControl().isDisposed();
    }

    public Control getControl() {
        return viewer.getControl();
    }

    public Tree getTree() {
        return viewer.getTree();
    }

    public void setBackground(Color color) {
        viewer.getControl().setBackground(color);
    }

    public void setForeground(Color color) {
        viewer.getControl().setForeground(color);
    }

    public void updateFontsFrom(FontData fontData) {
        Font oldFont = font;
        Font oldFontBold = fontBold;

        Display display = viewer.getControl().getDisplay();
        font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);
        fontBold = new Font(display, fontData.getName(), fontData.getHeight(), SWT.BOLD);

        viewer.getControl().setFont(font);

        if (oldFont != null) {
            oldFont.dispose();
            oldFontBold.dispose();
        }
    }

}
