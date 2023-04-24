/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.internal.ColorRegistry;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

public class DevicesDialog extends Dialog {

    static class Device {

        String name;
        String port;

        public Device(String name, String port) {
            this.name = name;
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public String getPort() {
            return port;
        }

        @Override
        public String toString() {
            return name + " on " + port;
        }

    }

    SerialPort terminalPort;

    TableViewer viewer;
    String selection;

    public DevicesDialog(Shell parentShell, SerialPort terminalPort) {
        super(parentShell);
        this.terminalPort = terminalPort;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Devices");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);

        getButton(IDialogConstants.OK_ID).setEnabled(false);
        getButton(IDialogConstants.CLIENT_ID).setEnabled(false);

        scheduleUpdate();

        return control;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = convertVerticalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        content.setLayout(layout);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));

        applyDialogFont(content);

        viewer = new TableViewer(content);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new StyledCellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                StringBuilder sb = new StringBuilder();
                List<StyleRange> styles = new ArrayList<StyleRange>();

                Device element = (Device) cell.getElement();
                sb.append(element.getName());
                sb.append(" ");

                styles.add(new StyleRange(sb.length(), element.getPort().length(), ColorRegistry.getColor(0x80, 0x80, 0x00), null));
                sb.append(element.getPort());

                cell.setText(sb.toString());
                cell.setStyleRanges(styles.toArray(new StyleRange[styles.size()]));
            }

        });
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = convertWidthInCharsToPixels(82);
        gridData.heightHint = viewer.getTable().getItemHeight() * 10;
        viewer.getControl().setLayoutData(gridData);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = event.getStructuredSelection();
                getButton(IDialogConstants.OK_ID).setEnabled(!selection.isEmpty());
            }

        });

        return content;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        createButton(parent, IDialogConstants.CLIENT_ID, "Detect", false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        switch (buttonId) {
            case IDialogConstants.OK_ID: {
                IStructuredSelection selection = viewer.getStructuredSelection();
                DevicesDialog.this.selection = ((Device) selection.getFirstElement()).getPort();
                break;
            }
            case IDialogConstants.CLIENT_ID:
                getButton(IDialogConstants.OK_ID).setEnabled(false);
                getButton(IDialogConstants.CLIENT_ID).setEnabled(false);
                scheduleUpdate();
                break;
        }
        super.buttonPressed(buttonId);
    }

    void scheduleUpdate() {
        List<Device> list = new ArrayList<>();

        viewer.getControl().setEnabled(false);

        Thread thread = new Thread(new Runnable() {

            byte LFSR;

            @Override
            public void run() {
                String[] portNames = SerialPortList.getPortNames();
                for (int i = 0; i < portNames.length; i++) {
                    SerialPort serialPort;
                    if (terminalPort != null && terminalPort.getPortName().equals(portNames[i])) {
                        serialPort = terminalPort;
                    }
                    else {
                        serialPort = new SerialPort(portNames[i]);
                    }
                    try {
                        String version = find(serialPort);
                        if (version != null) {
                            list.add(new Device(version, portNames[i]));
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                    if (serialPort != terminalPort) {
                        try {
                            serialPort.closePort();
                        } catch (Exception e) {
                            // Do nothing
                        }
                    }
                }
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (viewer.getControl().isDisposed()) {
                            return;
                        }
                        viewer.setInput(list.toArray(new Device[list.size()]));
                        viewer.getControl().setEnabled(true);
                        getButton(IDialogConstants.CLIENT_ID).setEnabled(true);
                    }
                });
            }

            public String find(SerialPort serialPort) throws Exception {
                String version = null;

                if (!serialPort.isOpened()) {
                    serialPort.openPort();
                }

                int rc = 0;
                try {
                    serialPort.setParams(
                        115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE,
                        false,
                        false);
                    hwreset(serialPort);
                    rc = hwfind1(serialPort);
                } catch (Exception e) {
                    // Do nothing
                }
                if (rc == 0) {
                    try {
                        serialPort.setParams(
                            2000000,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE,
                            false,
                            false);
                        hwreset(serialPort);
                        rc = hwfind2(serialPort);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
                switch (rc) {
                    case 0:
                        // Not found
                        break;
                    case 1:
                        version = "P8X32A - 8 cogs, 32KB hub, 32 I/O pins";
                        break;
                    case 'A':
                        version = "FPGA - 8 cogs, 512KB hub, 48 smart pins 63..56, 39..0, 80MHz";
                        break;
                    case 'B':
                        version = "FPGA - 4 cogs, 256KB hub, 12 smart pins 63..60/7..0, 80MHz";
                        break;
                    case 'C':
                        version = "1 cog, 32KB hub, 8 smart pins 63..62/5..0, 80MHz, No CORDIC";
                        break;
                    case 'D':
                        version = "1 cog, 128KB hub, 7 smart pins 63..62/4..0, 80MHz, No CORDIC";
                        break;
                    case 'E':
                        version = "FPGA - 4 cogs, 512KB hub, 18 smart pins 63..62/15..0, 80MHz";
                        break;
                    case 'F':
                        version = "16 cogs, 1024KB hub, 7 smart pins 63..62/33..32/2..0, 80MHz";
                        break;
                    case 'G':
                        version = "P2X8C4M64P Rev B/C - 8 cogs, 512KB hub, 64 smart pins";
                        break;
                    default:
                        version = "Unknown version " + (Character.isLetterOrDigit(rc) ? "'" + (char) rc + "'" : rc);
                        break;
                }

                return version;
            }

            void hwreset(SerialPort serialPort) throws SerialPortException {
                serialPort.setDTR(true);
                msleep(25);
                serialPort.setDTR(false);
                msleep(25);
                skipIncomingBytes(serialPort);
                serialPort.purgePort(SerialPort.PURGE_TXABORT |
                    SerialPort.PURGE_RXABORT |
                    SerialPort.PURGE_TXCLEAR |
                    SerialPort.PURGE_RXCLEAR);
            }

            private void msleep(int msec) {
                try {
                    Thread.sleep(msec);
                } catch (Exception e) {

                }
            }

            protected int hwfind1(SerialPort serialPort) throws SerialPortException, IOException {
                int n, ii, jj;
                byte[] buffer;

                // send the calibration pulse
                serialPort.writeInt(0xF9);

                // send the magic propeller LFSR byte stream.
                LFSR = 'P';
                buffer = new byte[250];
                for (n = 0; n < 250; n++) {
                    buffer[n] = (byte) (iterate() | 0xFE);
                    //System.out.print(String.format("%02X ", ii));
                }
                serialPort.writeBytes(buffer);
                //System.out.println();

                skipIncomingBytes(serialPort);

                // Send 258 0xF9 for LFSR and Version ID
                // These bytes clock the LSFR bits and ID from propeller back to us.
                buffer = new byte[258];
                for (n = 0; n < 258; n++) {
                    buffer[n] = (byte) (0xF9);
                }
                serialPort.writeBytes(buffer);

                // Wait at least 100ms for the first response. Allow some margin.
                // Some chips may respond < 50ms, but there's no guarantee all will.
                // If we don't get it, we can assume the propeller is not there.
                if ((ii = getBit(serialPort, 110)) == -1) {
                    throw new IOException("Timeout waiting for first response bit");
                }
                //System.out.print(String.format("%02X ", ii));

                // wait for response so we know we have a Propeller
                for (n = 1; n < 250; n++) {
                    jj = iterate();
                    //System.out.println(String.format("%03d: %d:%d", n, ii, jj));

                    if (ii != jj) {
                        //System.err.println("Lost HW contact");
                        for (n = 0; n < 300; n++) {
                            if (readByteWithTimeout(serialPort, 50) == -1) {
                                break;
                            }
                        }
                        return 0;
                    }

                    int to = 0;
                    do {
                        if ((ii = getBit(serialPort, 110)) != -1) {
                            //System.out.print(String.format("%02X ", ii));
                            break;
                        }
                    } while (to++ < 100);

                    if (to > 100) {
                        throw new IOException("Timeout waiting for response bit");
                    }
                }
                //System.out.println();

                int rc = 0;
                for (n = 0; n < 8; n++) {
                    rc >>= 1;
                    if ((ii = getBit(serialPort, 110)) != -1) {
                        //System.out.print(String.format("%02X ", ii));
                        rc += (ii != 0) ? 0x80 : 0;
                    }
                }
                //System.out.println();

                return rc;
            }

            private int getBit(SerialPort serialPort, int timeout) throws SerialPortException {
                int[] rx;
                try {
                    rx = serialPort.readIntArray(1, timeout);
                    return rx[0] & 1;
                } catch (SerialPortTimeoutException e) {

                }
                return -1;
            }

            int iterate() {
                int bit = LFSR & 1;
                LFSR = (byte) ((LFSR << 1) | (((LFSR >> 7) ^ (LFSR >> 5) ^ (LFSR >> 4) ^ (LFSR >> 1)) & 1));
                return bit;
            }

            protected int hwfind2(SerialPort serialPort) throws SerialPortException, IOException {
                String result = new String();

                serialPort.writeString("> \r");
                msleep(1);

                serialPort.writeString("> Prop_Chk 0 0 0 0\r");

                try {
                    while (result.length() < 11 || !result.endsWith("\r\n")) {
                        byte[] b = serialPort.readBytes(1, 20);
                        if (b != null && b.length == 1) {
                            result += new String(b);
                        }
                    }
                    if (result.startsWith("\r\nProp_Ver ")) {
                        return result.charAt(11);
                    }
                } catch (SerialPortTimeoutException e) {
                    return 0;
                }

                return 0;
            }

            protected int skipIncomingBytes(SerialPort serialPort) throws SerialPortException {
                int n = 0;
                while (readByteWithTimeout(serialPort, 50) != -1) {
                    n++;
                }
                return n;
            }

            private int readByteWithTimeout(SerialPort serialPort, int timeout) throws SerialPortException {
                int[] rx;
                try {
                    rx = serialPort.readIntArray(1, timeout);
                    return rx[0];
                } catch (SerialPortTimeoutException e) {

                }
                return -1;
            }

        });
        thread.start();
    }

    public String getSelection() {
        return selection;
    }

}
