/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ConsoleView {

    Composite container;
    StyledText console;

    OutputStream outputStream = new OutputStream() {

        final Color errorColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
        int moreLinesToMark = 0;

        StringBuilder lineBuilder = new StringBuilder();

        @Override
        public void write(int b) throws IOException {

        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    appendLine(new String(b, off, len));
                    updateStyle();
                }
            });
        }

        void appendLine(String s) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '\r') {
                    int ofs = getLastLineOffset();
                    console.replaceTextRange(ofs, console.getCharCount() - ofs, lineBuilder.toString());
                    lineBuilder = new StringBuilder();
                }
                else if (c == '\n') {
                    if (lineBuilder.length() > 0) {
                        int ofs = getLastLineOffset();
                        console.replaceTextRange(ofs, console.getCharCount() - ofs, lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                    }
                    console.append("\n");
                }
                else {
                    lineBuilder.append(c);
                }
            }
            if (lineBuilder.length() > 0) {
                int ofs = getLastLineOffset();
                console.replaceTextRange(ofs, console.getCharCount() - ofs, lineBuilder.toString());
            }

            console.setCaretOffset(console.getCharCount());
            if (console.getLineCount() > 0) {
                console.setTopIndex(console.getLineCount() - 1);
            }
        }

        int getLastLineOffset() {
            int index = console.getLineCount() - 1;
            return console.getOffsetAtLine(index);
        }

        void updateStyle() {
            int count = console.getLineCount();
            if (count != 0) {
                count--;
            }

            for (int i = 0; i < count; i++) {
                String line = console.getLine(i);
                if (line.contains(": error :")) {
                    int start = console.getOffsetAtLine(i);
                    console.setStyleRange(new StyleRange(start, line.length(), errorColor, null));
                    moreLinesToMark += 3;
                }
                else if (line.contains("Error : ")) {
                    int start = console.getOffsetAtLine(i);
                    console.setStyleRange(new StyleRange(start, line.length(), errorColor, null));
                    moreLinesToMark += 2;
                }
                else if (moreLinesToMark > 0) {
                    int start = console.getOffsetAtLine(i);
                    console.setStyleRange(new StyleRange(start, line.length(), errorColor, null));
                    moreLinesToMark--;
                }
            }
        }

        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {

        }

    };

    OutputStream errorStream = new OutputStream() {

        final Color errorColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);

        @Override
        public void write(int b) throws IOException {

        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    int start = console.getCharCount();

                    console.append(new String(b, off, len));
                    console.setCaretOffset(console.getCharCount());
                    if (console.getLineCount() > 0) {
                        console.setTopIndex(console.getLineCount() - 1);
                    }

                    console.setStyleRange(new StyleRange(start, len, errorColor, null));
                }
            });
        }

        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {

        }

    };

    private Font consoleFont;

    public ConsoleView(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label label = new Label(container, SWT.NONE);
        label.setText("Console");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        console = new StyledText(container, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        console.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        console.setMargins(5, 5, 5, 5);
        console.setTabs(4);

        consoleFont = JFaceResources.getTextFont();
        console.setFont(consoleFont);
        console.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                consoleFont.dispose();
            }
        });
    }

    public Composite getControl() {
        return container;
    }

    public StyledText getStyledText() {
        return console;
    }

    public void clear() {
        console.setText("\r\n");
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStream getErrorStream() {
        return errorStream;
    }

}
