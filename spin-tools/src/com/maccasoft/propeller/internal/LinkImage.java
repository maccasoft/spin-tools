/*
 * Copyright (c) 2025 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class LinkImage implements Listener {

    Label label;
    String url;

    boolean pressed;

    public LinkImage(Composite parent) {
        label = new Label(parent, SWT.NONE);
        label.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));
        label.addListener(SWT.MouseDown, this);
        label.addListener(SWT.MouseUp, this);
        label.addListener(SWT.MouseMove, this);
    }

    public void setImage(Image image) {
        label.setImage(image);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.label.setToolTipText(url);
    }

    public void setLayoutData(Object layoutData) {
        label.setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return label.getLayoutData();
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.type) {
            case SWT.MouseDown:
                pressed = true;
                break;

            case SWT.MouseUp:
                if (pressed) {
                    try {
                        if (url != null) {
                            Program.launch(url);
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
                pressed = false;
                break;

            case SWT.MouseMove: {
                Rectangle bounds = ((Control) event.widget).getBounds();
                if (!bounds.contains(event.x, event.y)) {
                    if (pressed) {
                        pressed = false;
                    }
                }
                break;
            }
        }
    }

}
