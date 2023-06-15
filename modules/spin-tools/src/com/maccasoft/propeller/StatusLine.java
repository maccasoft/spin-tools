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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class StatusLine implements IProgressMonitor {

    Composite container;
    Label messageLabel;
    Label caretPositionLabel;
    Label portLabel;
    ProgressIndicator progressBar;

    public StatusLine(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setData(this);
        container.setBackgroundMode(SWT.INHERIT_FORCE);

        GC gc = new GC(parent);
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        messageLabel = new Label(container, SWT.NONE);
        messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        addSeparator();

        caretPositionLabel = new Label(container, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData.widthHint = Dialog.convertWidthInCharsToPixels(fontMetrics, 20);
        caretPositionLabel.setLayoutData(gridData);

        addSeparator();

        portLabel = new Label(container, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData.widthHint = Dialog.convertWidthInCharsToPixels(fontMetrics, 20);
        portLabel.setLayoutData(gridData);

        addSeparator();

        progressBar = new ProgressIndicator(container, SWT.HORIZONTAL);
        gridData = new GridData(GridData.GRAB_VERTICAL);
        gridData.widthHint = 200;
        progressBar.setLayoutData(gridData);

        container.setLayout(new GridLayout(container.getChildren().length, false));
    }

    public Object getLayoutData() {
        return container.getLayoutData();
    }

    public void setLayoutData(Object layoutData) {
        container.setLayoutData(layoutData);
    }

    void addSeparator() {
        Label label = new Label(container, SWT.SEPARATOR);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, false, true);
        layoutData.heightHint = 24;
        label.setLayoutData(layoutData);
    }

    public void setText(String text) {
        messageLabel.setText(text);
    }

    public void setText(String text, Color color) {
        messageLabel.setForeground(color);
        messageLabel.setText(text);
    }

    public void setCaretPositionText(String text) {
        caretPositionLabel.setText(text);
    }

    public void setPort(String text) {
        portLabel.setText(text);
    }

    public IProgressMonitor getProgressMonitor() {
        return this;
    }

    @Override
    public void beginTask(String name, final int totalWork) {
        final boolean animated = (totalWork == UNKNOWN || totalWork == 0);
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                if (progressBar == null || progressBar.isDisposed()) {
                    return;
                }

                if (!progressBar.getVisible()) {
                    progressBar.setVisible(true);
                    container.layout();
                }
                if (!animated) {
                    progressBar.beginTask(totalWork);
                }
                else {
                    progressBar.beginAnimatedTask();
                }
            }
        });
    }

    @Override
    public void done() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                if (progressBar == null || progressBar.isDisposed()) {
                    return;
                }
                progressBar.sendRemainingWork();
                progressBar.done();
            }
        });
    }

    @Override
    public void subTask(String name) {

    }

    @Override
    public void worked(int work) {
        internalWorked(work);
    }

    @Override
    public void internalWorked(final double work) {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                if (progressBar == null || progressBar.isDisposed()) {
                    return;
                }
                progressBar.worked(work);
            }
        });
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void setCanceled(boolean value) {

    }

    @Override
    public void setTaskName(String name) {

    }

    public void setBackground(Color color) {
        container.setBackground(color);
    }

    public void setForeground(Color color) {
        container.setForeground(color);
        Control[] children = container.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].setForeground(color);
        }
    }

}
