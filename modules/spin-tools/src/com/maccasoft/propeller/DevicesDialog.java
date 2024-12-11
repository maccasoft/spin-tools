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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.Platform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.DeviceDescriptor;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.NetworkUtils;
import com.maccasoft.propeller.devices.SerialComPort;
import com.maccasoft.propeller.internal.ColorRegistry;

import jssc.SerialPort;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

public class DevicesDialog extends Dialog {

    public static final int DISCOVER_PORT = 32420;

    public static final int DISCOVER_REPLY_TIMEOUT = 250;
    public static final int DISCOVER_ATTEMPTS = 3;

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

    TableViewer viewer;
    ComPort selection;

    public DevicesDialog(Shell parentShell) {
        super(parentShell);
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

        Preferences preferences = Preferences.getInstance();
        if ("win32".equals(Platform.PLATFORM) || preferences.getTheme() != null) {
            applyTheme(parent, preferences.getTheme());
        }

        scheduleUpdate();

        return control;
    }

    Color widgetForeground;
    Color widgetBackground;
    Color listForeground;
    Color listBackground;
    Color labelForeground;
    Color buttonBackground;

    void applyTheme(Control control, String id) {
        widgetForeground = null;
        widgetBackground = null;
        listForeground = null;
        listBackground = null;
        labelForeground = null;
        buttonBackground = null;

        if ("win32".equals(Platform.PLATFORM) && id == null) {
            if (Display.isSystemDarkTheme()) {
                id = "dark";
            }
        }

        if (id == null) {
            listBackground = ColorRegistry.getColor(ColorRegistry.LIST_BACKGROUND);
            listForeground = ColorRegistry.getColor(ColorRegistry.LIST_FOREGROUND);
            widgetBackground = ColorRegistry.getColor(ColorRegistry.WIDGET_BACKGROUND);
            widgetForeground = ColorRegistry.getColor(ColorRegistry.WIDGET_FOREGROUND);
        }
        else if ("dark".equals(id)) {
            widgetForeground = new Color(0xF0, 0xF0, 0xF0);
            widgetBackground = new Color(0x50, 0x55, 0x57);
            listForeground = new Color(0xA7, 0xA7, 0xA7);
            listBackground = new Color(0x2B, 0x2B, 0x2B);
            labelForeground = new Color(0xD7, 0xD7, 0xD7);
            buttonBackground = new Color(0x50, 0x55, 0x57);
        }
        else if ("light".equals(id)) {
            widgetForeground = new Color(0x00, 0x00, 0x00);
            if ("win32".equals(Platform.PLATFORM)) {
                widgetBackground = new Color(0xF0, 0xF0, 0xF0);
            }
            else {
                widgetBackground = new Color(0xFA, 0xFA, 0xFA);
            }
            listForeground = new Color(0x00, 0x00, 0x00);
            listBackground = new Color(0xFE, 0xFE, 0xFE);
            labelForeground = new Color(0x00, 0x00, 0x00);
            buttonBackground = new Color(0xFA, 0xFA, 0xFA);
        }

        applyTheme(control);
    }

    void applyTheme(Control control) {
        if ((control instanceof List) || (control instanceof Table)) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Button) {
            if (control != getShell().getDefaultButton()) {
                control.setForeground(widgetForeground);
                control.setBackground(buttonBackground);
            }
        }
        else if (control instanceof Text) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Spinner) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Combo) {
            control.setForeground(listForeground);
            control.setBackground(listBackground);
        }
        else if (control instanceof Label) {
            control.setForeground(widgetForeground);
        }
        else if (control instanceof Composite) {
            control.setBackground(widgetBackground);
            Control[] children = ((Composite) control).getChildren();
            for (int i = 0; i < children.length; i++) {
                applyTheme(children[i]);
            }
        }
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

                if (cell.getElement() instanceof Device) {
                    Device element = (Device) cell.getElement();
                    sb.append(element.getName());
                    sb.append(" ");

                    styles.add(new StyleRange(sb.length(), element.getPort().length(), ColorRegistry.getColor(0x80, 0x80, 0x00), null));
                    sb.append(element.getPort());
                }
                else if (cell.getElement() instanceof DeviceDescriptor) {
                    DeviceDescriptor element = (DeviceDescriptor) cell.getElement();
                    sb.append(element.name);
                    sb.append(" ");

                    String text = String.format("ip=%s, mac=%s", element.inetAddr.getHostAddress(), element.mac_address);

                    styles.add(new StyleRange(sb.length(), text.length(), ColorRegistry.getColor(0x80, 0x80, 0x00), null));
                    sb.append(text);
                }

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
        createButton(parent, IDialogConstants.CLIENT_ID, "Discover", false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        switch (buttonId) {
            case IDialogConstants.OK_ID: {
                Object selectedElement = viewer.getStructuredSelection().getFirstElement();
                String portName = getPortName(selectedElement);

                if (selection == null || !selection.getPortName().equals(portName)) {
                    if (selectedElement instanceof Device) {
                        selection = new SerialComPort(portName);
                    }
                    else if (selectedElement instanceof DeviceDescriptor) {
                        selection = new NetworkComPort((DeviceDescriptor) selectedElement);
                    }
                }
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

    String getPortName(Object o) {
        if (o instanceof Device) {
            return ((Device) o).getPort();
        }
        else if (o instanceof DeviceDescriptor) {
            return ((DeviceDescriptor) o).getPortName();
        }
        return "";
    }

    void scheduleUpdate() {
        List<Object> list = new ArrayList<>();

        viewer.getControl().setEnabled(false);

        String currentSelection;
        if (viewer.getStructuredSelection().isEmpty()) {
            currentSelection = selection != null ? selection.getPortName() : null;
        }
        else {
            currentSelection = getPortName(viewer.getStructuredSelection().getFirstElement());
        }

        Thread thread = new Thread(new Runnable() {

            byte LFSR;

            @Override
            public void run() {
                String[] portNames = SerialPortList.getPortNames();
                for (int i = 0; i < portNames.length; i++) {
                    ComPort serialPort = null;
                    if (selection != null) {
                        if (selection.getPortName().equals(portNames[i])) {
                            serialPort = selection;
                        }
                    }
                    if (serialPort == null) {
                        serialPort = new SerialComPort(portNames[i]);
                    }

                    try {
                        String version = find((SerialComPort) serialPort);
                        if (version != null) {
                            list.add(new Device(version, portNames[i]));
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }

                list.addAll(NetworkUtils.getAvailableDevices());

                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (viewer.getControl().isDisposed()) {
                            return;
                        }
                        viewer.setInput(list.toArray());
                        if (currentSelection != null) {
                            for (Object device : list) {
                                if (getPortName(device).equals(currentSelection)) {
                                    viewer.setSelection(new StructuredSelection(device));
                                    break;
                                }
                            }
                        }
                        viewer.getControl().setEnabled(true);
                        viewer.getControl().setFocus();
                        getButton(IDialogConstants.CLIENT_ID).setEnabled(true);
                    }
                });
            }

            public String find(SerialComPort comPort) throws ComPortException {
                boolean closePort = comPort != selection;

                if (!comPort.isOpened()) {
                    comPort.openPort();
                    closePort = true;
                }

                int rc = 0;
                try {
                    comPort.setParams(
                        115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                    comPort.hwreset();
                    rc = hwfind1(comPort);
                } catch (Exception e) {
                    // Do nothing
                }
                if (rc == 0) {
                    try {
                        comPort.setParams(
                            2000000,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                        comPort.hwreset();
                        rc = hwfind2(comPort);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }

                if (closePort) {
                    try {
                        comPort.closePort();
                    } catch (Exception e) {
                        // Do nothing
                    }
                }

                return getVersionText(rc);
            }

            String getVersionText(int version) {
                switch (version) {
                    case 0:
                        // Not found
                        return null;
                    case 1:
                        return "P8X32A - 8 cogs, 32KB hub, 32 I/O pins";
                    case 'A':
                        return "FPGA - 8 cogs, 512KB hub, 48 smart pins 63..56, 39..0, 80MHz";
                    case 'B':
                        return "FPGA - 4 cogs, 256KB hub, 12 smart pins 63..60/7..0, 80MHz";
                    case 'C':
                        return "1 cog, 32KB hub, 8 smart pins 63..62/5..0, 80MHz, No CORDIC";
                    case 'D':
                        return "1 cog, 128KB hub, 7 smart pins 63..62/4..0, 80MHz, No CORDIC";
                    case 'E':
                        return "FPGA - 4 cogs, 512KB hub, 18 smart pins 63..62/15..0, 80MHz";
                    case 'F':
                        return "16 cogs, 1024KB hub, 7 smart pins 63..62/33..32/2..0, 80MHz";
                    case 'G':
                        return "P2X8C4M64P Rev B/C - 8 cogs, 512KB hub, 64 smart pins";
                    default:
                        return "Unknown version " + (Character.isLetterOrDigit(version) ? "'" + (char) version + "'" : version);
                }
            }

            private void msleep(int msec) {
                try {
                    Thread.sleep(msec);
                } catch (Exception e) {

                }
            }

            protected int hwfind1(SerialComPort comPort) throws ComPortException {
                int n, ii, jj;
                byte[] buffer;

                // send the calibration pulse
                comPort.writeInt(0xF9);

                // send the magic propeller LFSR byte stream.
                LFSR = 'P';
                buffer = new byte[250];
                for (n = 0; n < 250; n++) {
                    buffer[n] = (byte) (iterate() | 0xFE);
                    //System.out.print(String.format("%02X ", ii));
                }
                comPort.writeBytes(buffer);
                //System.out.println();

                skipIncomingBytes(comPort);

                // Send 258 0xF9 for LFSR and Version ID
                // These bytes clock the LSFR bits and ID from propeller back to us.
                buffer = new byte[258];
                for (n = 0; n < 258; n++) {
                    buffer[n] = (byte) (0xF9);
                }
                comPort.writeBytes(buffer);

                // Wait at least 100ms for the first response. Allow some margin.
                // Some chips may respond < 50ms, but there's no guarantee all will.
                // If we don't get it, we can assume the propeller is not there.
                if ((ii = getBit(comPort, 110)) == -1) {
                    throw new ComPortException("Timeout waiting for first response bit");
                }
                //System.out.print(String.format("%02X ", ii));

                // wait for response so we know we have a Propeller
                for (n = 1; n < 250; n++) {
                    jj = iterate();
                    //System.out.println(String.format("%03d: %d:%d", n, ii, jj));

                    if (ii != jj) {
                        //System.err.println("Lost HW contact");
                        for (n = 0; n < 300; n++) {
                            if (comPort.readByteWithTimeout(50) == -1) {
                                break;
                            }
                        }
                        return 0;
                    }

                    int to = 0;
                    do {
                        if ((ii = getBit(comPort, 110)) != -1) {
                            //System.out.print(String.format("%02X ", ii));
                            break;
                        }
                    } while (to++ < 100);

                    if (to > 100) {
                        throw new ComPortException("Timeout waiting for response bit");
                    }
                }
                //System.out.println();

                int rc = 0;
                for (n = 0; n < 8; n++) {
                    rc >>= 1;
                    if ((ii = getBit(comPort, 110)) != -1) {
                        //System.out.print(String.format("%02X ", ii));
                        rc += (ii != 0) ? 0x80 : 0;
                    }
                }
                //System.out.println();

                return rc;
            }

            private int getBit(SerialComPort comPort, int timeout) {
                try {
                    int rx = comPort.readByteWithTimeout(timeout);
                    return rx & 1;
                } catch (ComPortException e) {

                }
                return -1;
            }

            int iterate() {
                int bit = LFSR & 1;
                LFSR = (byte) ((LFSR << 1) | (((LFSR >> 7) ^ (LFSR >> 5) ^ (LFSR >> 4) ^ (LFSR >> 1)) & 1));
                return bit;
            }

            protected int hwfind2(SerialComPort comPort) throws ComPortException {
                String result = new String();

                comPort.writeString("> \r");
                msleep(1);

                comPort.writeString("> Prop_Chk 0 0 0 0\r");

                try {
                    while (result.length() < 11 || !result.endsWith("\r\n")) {
                        byte[] b = comPort.readBytes(1, 20);
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

            protected int skipIncomingBytes(SerialComPort comPort) throws ComPortException {
                int n = 0;
                while (comPort.readByteWithTimeout(50) != -1) {
                    n++;
                }
                return n;
            }

        });
        thread.start();
    }

    public ComPort getSelection() {
        return selection;
    }

    public void setSelection(ComPort selection) {
        this.selection = selection;
    }

}
