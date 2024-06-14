/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

public class ConsoleView {

    Display display;
    StyledText console;

    SerialPort serialPort;
    int serialBaudRate;
    PrintStream os;

    Preferences preferences;

    Font font;
    Color foreground;
    Color disabledForeground;
    int maxLines;
    boolean writeLogFile;
    boolean enabled;

    File logFile;
    StringBuilder lineBuilder = new StringBuilder();

    private int utf_buf_index = 0, utf_ch_count = 0;
    private byte[] utf_buf = new byte[4];

    final PropertyChangeListener preferencesChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case Preferences.PROP_CONSOLE_FONT:
                    Font textFont = JFaceResources.getTextFont();
                    FontData fontData = textFont.getFontData()[0];
                    if (evt.getNewValue() != null) {
                        fontData = StringConverter.asFontData(evt.getNewValue().toString());
                    }

                    Font newFont = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);
                    console.setFont(newFont);
                    console.redraw();

                    if (font != null) {
                        font.dispose();
                    }
                    font = newFont;
                    break;
                case Preferences.PROP_CONSOLE_MAX_LINES:
                    maxLines = ((Integer) evt.getNewValue()).intValue();
                    break;
                case Preferences.PROP_CONSOLE_WRITE_LOG_FILE:
                    writeLogFile = ((Boolean) evt.getNewValue()).booleanValue();
                    if (!writeLogFile && os != null) {
                        if (os != null) {
                            os.close();
                        }
                        os = null;
                    }
                    else if (writeLogFile && os == null) {
                        try {
                            os = new PrintStream(logFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    SerialPortEventListener serialEventListener = new SerialPortEventListener() {

        @Override
        public void serialEvent(SerialPortEvent event) {
            switch (event.getEventType()) {
                case SerialPort.MASK_RXCHAR:
                    try {
                        byte[] rx = serialPort.readBytes();
                        for (int i = 0; i < rx.length; i++) {
                            write(rx[i]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        private void write(byte b) {
            if (utf_buf_index == 0) {
                if ((b & 0b111_00000) == 0b110_00000) {
                    utf_buf[utf_buf_index++] = b;
                    utf_ch_count = 2;
                }
                else if ((b & 0b1111_0000) == 0b1110_0000) {
                    utf_buf[utf_buf_index++] = b;
                    utf_ch_count = 3;
                }
                else if ((b & 0b11111_000) == 0b11110_000) {
                    utf_buf[utf_buf_index++] = b;
                    utf_ch_count = 4;
                }
                else if (b == '\n' || b == '\r') {
                    if (lineBuilder.length() != 0) {
                        append(lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                    }
                }
                else {
                    lineBuilder.append((char) b);
                }
            }
            else {
                utf_buf[utf_buf_index++] = b;
                if (utf_buf_index >= utf_ch_count) {
                    lineBuilder.append(new String(utf_buf, 0, utf_ch_count));
                    utf_buf_index = 0;
                }
            }
        }

        private void append(String text) {
            if (os != null) {
                os.println(text);
                os.flush();
            }
            if (console.isDisposed()) {
                return;
            }
            display.asyncExec(new Runnable() {

                @Override
                public void run() {
                    if (console.isDisposed()) {
                        return;
                    }
                    console.append(text);
                    console.append(System.lineSeparator());
                    while (console.getLineCount() > maxLines) {
                        int length = console.getOffsetAtLine(console.getLineCount() - maxLines);
                        console.replaceTextRange(0, length, "");
                    }
                    console.invokeAction(ST.TEXT_END);
                }
            });
        }

    };

    OutputStream consoleOutputStream = new OutputStream() {

        @Override
        public void write(int b) throws IOException {
            if (utf_buf_index == 0) {
                if ((b & 0b111_00000) == 0b110_00000) {
                    utf_buf[utf_buf_index++] = (byte) b;
                    utf_ch_count = 2;
                }
                else if ((b & 0b1111_0000) == 0b1110_0000) {
                    utf_buf[utf_buf_index++] = (byte) b;
                    utf_ch_count = 3;
                }
                else if ((b & 0b11111_000) == 0b11110_000) {
                    utf_buf[utf_buf_index++] = (byte) b;
                    utf_ch_count = 4;
                }
                else if (b == '\n' || b == '\r') {
                    if (lineBuilder.length() != 0) {
                        append(lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                    }
                }
                else {
                    lineBuilder.append((char) b);
                }
            }
            else {
                utf_buf[utf_buf_index++] = (byte) b;
                if (utf_buf_index >= utf_ch_count) {
                    lineBuilder.append(new String(utf_buf, 0, utf_ch_count));
                    utf_buf_index = 0;
                }
            }
        }

        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {
            if (lineBuilder.length() != 0) {
                append(lineBuilder.toString());
                lineBuilder = new StringBuilder();
            }
        }

        private void append(String text) {
            if (console.isDisposed()) {
                return;
            }
            display.asyncExec(new Runnable() {

                @Override
                public void run() {
                    if (console.isDisposed()) {
                        return;
                    }
                    console.append(text);
                    console.append(System.lineSeparator());
                    while (console.getLineCount() > maxLines) {
                        int length = console.getOffsetAtLine(console.getLineCount() - maxLines);
                        console.replaceTextRange(0, length, "");
                    }
                    console.invokeAction(ST.TEXT_END);
                }
            });
        }

    };

    public ConsoleView(Composite parent) {
        display = parent.getDisplay();
        preferences = Preferences.getInstance();

        enabled = false;

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (Preferences.getInstance().getConsoleFont() != null) {
            fontData = StringConverter.asFontData(Preferences.getInstance().getTerminalFont());
        }
        font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);

        disabledForeground = display.getSystemColor(SWT.COLOR_WIDGET_DISABLED_FOREGROUND);

        console = new StyledText(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        console.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        console.setMargins(5, 5, 5, 5);
        console.setTabs(4);
        console.setFont(font);
        console.setForeground(disabledForeground);

        console.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent event) {
                if (event.character != 0) {
                    try {
                        if (serialPort != null && serialPort.isOpened()) {
                            serialPort.writeByte((byte) event.character);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        maxLines = preferences.getConsoleMaxLines();
        writeLogFile = preferences.getConsoleWriteLogFile();

        preferences.addPropertyChangeListener(preferencesChangeListener);

        console.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent event) {
                preferences.removePropertyChangeListener(preferencesChangeListener);
                if (os != null) {
                    os.close();
                }
                if (serialPort != null) {
                    try {
                        serialPort.removeEventListener();
                        if (serialPort.isOpened()) {
                            serialPort.closePort();
                        }
                    } catch (Exception e) {

                    }
                }
                font.dispose();
            }
        });
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        console.setForeground(enabled ? foreground : disabledForeground);
    }

    public boolean getVisible() {
        return console.getVisible();
    }

    public void setVisible(boolean visible) {
        if (enabled && console.getVisible() && !visible && preferences.getConsoleResetDeviceOnClose()) {
            if (serialPort != null && serialPort.isOpened()) {
                try {
                    serialPort.setDTR(true);
                    serialPort.setRTS(true);
                    Thread.sleep(25);
                    serialPort.setDTR(false);
                    serialPort.setRTS(false);
                    Thread.sleep(25);
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }
        console.setVisible(visible);
    }

    public void setFocus() {
        console.setFocus();
    }

    public Composite getControl() {
        return console;
    }

    public StyledText getStyledText() {
        return console;
    }

    public void clear() {
        console.setText("");
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        try {
            if (this.serialPort != null && this.serialPort != serialPort) {
                if (this.serialPort.isOpened()) {
                    this.serialPort.removeEventListener();
                }
            }

            lineBuilder = new StringBuilder();
            if (serialPort != null) {
                if (this.serialPort != serialPort) {
                    serialPort.addEventListener(serialEventListener);
                }
                serialPort.setParams(
                    serialBaudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE,
                    false,
                    false);
            }

            this.serialPort = serialPort;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSerialBaudRate(int serialBaudRate) {
        this.serialBaudRate = serialBaudRate;
    }

    public void setLogFile(File location, String fileName) {
        try {
            if (os != null) {
                os.close();
                os = null;
            }
            logFile = new File(location, fileName);
            if (writeLogFile) {
                os = new PrintStream(logFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLogFile() {
        try {
            if (os != null) {
                os.close();
            }
            os = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OutputStream getOutputStream() {
        return consoleOutputStream;
    }

    public void setBackground(Color color) {
        console.setBackground(color);
    }

    public void setForeground(Color color) {
        foreground = color;
        if (enabled) {
            console.setForeground(color);
        }
    }

    public void setDisabledForeground(Color color) {
        disabledForeground = color;
        if (!enabled) {
            console.setForeground(color);
        }
    }

}
