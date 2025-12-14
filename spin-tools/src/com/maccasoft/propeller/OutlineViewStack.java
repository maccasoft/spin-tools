/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.maccasoft.propeller.internal.ColorRegistry;

public class OutlineViewStack {

    Composite container;
    StackLayout layout;
    Color foreground;
    Color background;

    Map<Control, OutlineView> map = new HashMap<>();

    final DisposeListener disposeListener = new DisposeListener() {

        @Override
        public void widgetDisposed(DisposeEvent e) {
            if (e.widget == layout.topControl) {
                layout.topControl = null;
            }
            map.remove(e.widget);
        }

    };

    public OutlineViewStack(Composite parent, int style) {
        layout = new StackLayout();

        background = ColorRegistry.getColor(ColorRegistry.LIST_BACKGROUND);
        foreground = ColorRegistry.getColor(ColorRegistry.LIST_FOREGROUND);

        container = new Composite(parent, style);
        container.setLayout(layout);
        container.setBackground(background);
        container.setForeground(foreground);
    }

    public OutlineView createNew() {
        OutlineView view = new OutlineView(container);
        view.setForeground(foreground);
        view.setBackground(background);
        view.getControl().addDisposeListener(disposeListener);
        map.put(view.getControl(), view);
        return view;
    }

    public OutlineView getTopView() {
        if (layout.topControl == null) {
            return null;
        }
        return map.get(layout.topControl);
    }

    public void setTopView(OutlineView view) {
        layout.topControl = view != null ? view.getControl() : null;
        container.layout();
    }

    public boolean getVisible() {
        return container.getVisible();
    }

    public void setVisible(boolean visible) {
        container.setVisible(visible);
    }

    public void setBackground(Color color) {
        background = color;
        for (OutlineView view : map.values()) {
            view.setBackground(color);
        }
        container.setBackground(color);
    }

    public void setForeground(Color color) {
        foreground = color;
        for (OutlineView view : map.values()) {
            view.setForeground(color);
        }
        container.setForeground(color);
    }

    public void updateFontsFrom(FontData fontData) {
        for (OutlineView view : map.values()) {
            view.updateFontsFrom(fontData);
        }
    }

}
