/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.debug.DebugPAsmWindow;
import com.maccasoft.propeller.debug.DebugWindow;
import com.maccasoft.propeller.debug.KeywordIterator;
import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortEvent;
import com.maccasoft.propeller.devices.ComPortEventListener;
import com.maccasoft.propeller.internal.CircularBuffer;

import jssc.SerialPort;

public class ConsoleView {

    Display display;
    StyledText console;

    ComPort serialPort;
    int serialBaudRate;

    Preferences preferences;

    Font font;
    Color foreground;
    Color disabledForeground;
    int maxLines;
    boolean writeLogFile;
    boolean enabled;

    CircularBuffer receiveBuffer;
    CircularBuffer transmitBuffer;

    StringBuilder pendingText;
    File logFile;
    PrintStream os;

    Map<String, DebugWindow> map = new HashMap<>();
    DebugPAsmWindow[] debugger = new DebugPAsmWindow[8];

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

    ComPortEventListener serialEventListener = new ComPortEventListener() {

        @Override
        public void serialEvent(ComPortEvent event) {
            if (event.isRXCHAR()) {
                try {
                    byte[] rx = serialPort.readBytes();
                    if (rx != null) {
                        if (os != null) {
                            os.write(rx, 0, rx.length);
                        }
                        receiveBuffer.write(rx);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    OutputStream consoleOutputStream = new OutputStream() {

        @Override
        public void write(int b) throws IOException {
            if (os != null) {
                os.write(b);
            }
            receiveBuffer.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (os != null) {
                os.write(b, 0, b.length);
            }
            receiveBuffer.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (os != null) {
                os.write(b, off, len);
            }
            receiveBuffer.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {

        }

    };

    boolean consoleThreadRun;

    Runnable receiveThread = new Runnable() {

        int utf_buf_index = 0, utf_ch_count = 0;
        byte[] utf_buf = new byte[4];

        StringBuilder lineBuilder = new StringBuilder(128);

        @Override
        public void run() {
            int count, b = 0;
            int cogn = -1;
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            while (consoleThreadRun) {
                try {
                    count = receiveBuffer.available();
                    while (count > 0) {
                        b = receiveBuffer.read();
                        count--;

                        if (b < 8) {
                            cogn = b;
                            if ((b = receiveBuffer.read()) == 0) {
                                count--;
                                if ((b = receiveBuffer.read()) == 0) {
                                    count--;
                                    if ((b = receiveBuffer.read()) == 0) {
                                        count--;
                                        break;
                                    }
                                }
                            }
                        }

                        os.write(b);
                    }
                    if (os.size() > 0) {
                        byte[] rx = os.toByteArray();
                        display.asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                if (console.isDisposed()) {
                                    return;
                                }
                                for (int i = 0; i < rx.length; i++) {
                                    writeByte(rx[i]);
                                }
                            }

                        });
                        os.reset();
                    }
                    if (cogn != -1) {
                        DebugPAsmWindow window = debugger[cogn];
                        if (window != null) {
                            window.breakPoint(receiveBuffer, transmitBuffer);
                            window.update();
                        }
                        else {
                            DebugPAsmWindow newWindow = new DebugPAsmWindow(cogn);
                            display.syncExec(new Runnable() {

                                @Override
                                public void run() {
                                    newWindow.create();
                                    newWindow.open();
                                    newWindow.breakPoint(receiveBuffer, transmitBuffer);
                                    newWindow.update();
                                }

                            });
                            debugger[cogn] = newWindow;
                        }
                        cogn = -1;
                    }
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // Do nothing
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        void writeByte(byte b) {
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
                        String text = lineBuilder.toString();

                        if (text.startsWith("`")) {
                            handleDebugWindowCommand(text);
                        }

                        if (pendingText.length() == 0) {
                            display.asyncExec(textUpdateRunnable);
                        }
                        pendingText.append(text);
                        pendingText.append(System.lineSeparator());

                        lineBuilder.setLength(0);
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

    };

    Runnable transmitThread = new Runnable() {

        @Override
        public void run() {
            int count, b;

            while (consoleThreadRun) {
                try {
                    count = transmitBuffer.available();
                    while (count > 0 && consoleThreadRun) {
                        b = transmitBuffer.read();
                        if (serialPort.isOpened()) {
                            while (!serialPort.writeInt(b) && consoleThreadRun) {
                                Thread.sleep(1);
                            }
                        }
                        count--;
                    }
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // Do nothing
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    Runnable textUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            if (console.isDisposed()) {
                return;
            }
            console.append(pendingText.toString());
            if (console.getLineCount() > maxLines) {
                int length = console.getOffsetAtLine(console.getLineCount() - maxLines);
                console.replaceTextRange(0, length, "");
            }
            console.invokeAction(ST.TEXT_END);
            pendingText.setLength(0);
        }

    };

    public ConsoleView(Composite parent) {
        display = parent.getDisplay();
        preferences = Preferences.getInstance();

        receiveBuffer = new CircularBuffer(1 * 1024 * 1024);
        transmitBuffer = new CircularBuffer(4096);
        pendingText = new StringBuilder();

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

        console.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                e.text = e.text.replaceAll("\u001B\\[[;\\d]*m", "");
            }

        });

        console.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent event) {
                consoleThreadRun = false;

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

                for (int i = 0; i < debugger.length; i++) {
                    if (debugger[i] != null) {
                        debugger[i].dispose();
                    }
                }

                for (DebugWindow window : new ArrayList<>(map.values())) {
                    window.dispose();
                }
                map.clear();

                font.dispose();
            }
        });

        consoleThreadRun = true;

        new Thread(receiveThread, "ConsoleView Receiver").start();
        new Thread(transmitThread, "ConsoleView Transmitter").start();
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
                serialPort.hwreset();
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
        receiveBuffer.flush();
        pendingText = new StringBuilder();

        for (int i = 0; i < debugger.length; i++) {
            if (debugger[i] != null) {
                debugger[i].dispose();
                debugger[i] = null;
            }
        }
        for (DebugWindow window : new ArrayList<>(map.values())) {
            window.dispose();
        }
        map.clear();

        console.setText("");
    }

    public ComPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(ComPort serialPort) {
        try {
            if (this.serialPort != null && this.serialPort != serialPort) {
                if (this.serialPort.isOpened()) {
                    this.serialPort.removeEventListener();
                }
            }

            if (serialPort != null) {
                if (this.serialPort != serialPort) {
                    serialPort.setEventListener(serialEventListener);
                }
                serialPort.setParams(
                    serialBaudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
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

    void handleDebugWindowCommand(String text) {
        DebugWindow window = null;

        KeywordIterator iter = new KeywordIterator(text);
        if (iter.hasNext()) {
            String key = iter.next();
            window = DebugWindow.createType(key, transmitBuffer);
            if (window != null) {
                if (iter.hasNext()) {
                    String id = iter.next();

                    window.create();
                    window.setText(id);
                    window.addDisposeListener(new DisposeListener() {

                        @Override
                        public void widgetDisposed(DisposeEvent e) {
                            map.remove(id);
                        }

                    });
                    window.setup(iter);
                    window.open();

                    map.put(id, window);
                }
            }
            else {
                List<DebugWindow> list = new ArrayList<>();
                window = map.get(key);
                if (window != null) {
                    list.add(window);
                    while (iter.hasNext()) {
                        window = map.get(iter.peekNext());
                        if (window == null) {
                            break;
                        }
                        list.add(window);
                        iter.next();
                    }
                    int index = iter.getIndex();
                    for (DebugWindow w : list) {
                        w.update(iter);
                        iter.setIndex(index);
                    }
                }
            }
        }
    }

}
