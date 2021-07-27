/*
 * Copyright (c) 2016-17 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class StatusLine extends Composite implements IProgressMonitor {

    Label messageLabel;
    Label caretPositionLabel;
    Label portLabel;
    ProgressIndicator progressBar;

    public StatusLine(Composite parent) {
        super(parent, SWT.NONE);

        GC gc = new GC(parent);
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        messageLabel = new Label(this, SWT.NONE);
        messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        addSeparator();

        caretPositionLabel = new Label(this, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        layoutData.widthHint = Dialog.convertWidthInCharsToPixels(fontMetrics, 20);
        caretPositionLabel.setLayoutData(layoutData);

        addSeparator();

        portLabel = new Label(this, SWT.NONE);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        layoutData.widthHint = Dialog.convertWidthInCharsToPixels(fontMetrics, 20);
        portLabel.setLayoutData(layoutData);

        addSeparator();

        progressBar = new ProgressIndicator(this, SWT.HORIZONTAL);
        layoutData = new GridData(GridData.GRAB_VERTICAL);
        layoutData.widthHint = 200;
        progressBar.setLayoutData(layoutData);

        setLayout(new GridLayout(getChildren().length, false));
    }

    void addSeparator() {
        Label label = new Label(this, SWT.SEPARATOR);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, false, true);
        layoutData.heightHint = 24;
        label.setLayoutData(layoutData);
    }

    public void setText(String text) {
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
                    layout();
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

}
