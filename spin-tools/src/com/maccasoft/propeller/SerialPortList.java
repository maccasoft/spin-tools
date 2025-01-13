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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.maccasoft.propeller.Preferences.RemoteDevice;
import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.SerialComPort;

import jssc.SerialNativeInterface;

public class SerialPortList {

    Preferences preferences;

    ComPort selection;

    PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static SerialNativeInterface serialInterface;
    private static final Pattern PORTNAMES_REGEXP;
    private static final String PORTNAMES_PATH;

    static {
        serialInterface = new SerialNativeInterface();
        switch (SerialNativeInterface.getOsType()) {
            case SerialNativeInterface.OS_LINUX: {
                PORTNAMES_REGEXP = Pattern.compile("(ttyS|ttyUSB|ttyACM|ttyAMA|rfcomm|ttyO)[0-9]{1,3}");
                PORTNAMES_PATH = "/dev/";
                break;
            }
            case SerialNativeInterface.OS_SOLARIS: {
                PORTNAMES_REGEXP = Pattern.compile("[0-9]*|[a-z]*");
                PORTNAMES_PATH = "/dev/term/";
                break;
            }
            case SerialNativeInterface.OS_MAC_OS_X: {
                PORTNAMES_REGEXP = Pattern.compile("(tty|cu)\\..*");
                PORTNAMES_PATH = "/dev/";
                break;
            }
            case SerialNativeInterface.OS_WINDOWS: {
                PORTNAMES_REGEXP = Pattern.compile("");
                PORTNAMES_PATH = "";
                break;
            }
            default: {
                PORTNAMES_REGEXP = null;
                PORTNAMES_PATH = null;
                break;
            }
        }
    }

    private static final Comparator<String> PORTNAMES_COMPARATOR = new Comparator<String>() {

        @Override
        public int compare(String valueA, String valueB) {

            if (valueA.equalsIgnoreCase(valueB)) {
                return valueA.compareTo(valueB);
            }

            int minLength = Math.min(valueA.length(), valueB.length());

            int shiftA = 0;
            int shiftB = 0;

            for (int i = 0; i < minLength; i++) {
                char charA = valueA.charAt(i - shiftA);
                char charB = valueB.charAt(i - shiftB);
                if (charA != charB) {
                    if (Character.isDigit(charA) && Character.isDigit(charB)) {
                        int[] resultsA = getNumberAndLastIndex(valueA, i - shiftA);
                        int[] resultsB = getNumberAndLastIndex(valueB, i - shiftB);

                        if (resultsA[0] != resultsB[0]) {
                            return resultsA[0] - resultsB[0];
                        }

                        if (valueA.length() < valueB.length()) {
                            i = resultsA[1];
                            shiftB = resultsA[1] - resultsB[1];
                        }
                        else {
                            i = resultsB[1];
                            shiftA = resultsB[1] - resultsA[1];
                        }
                    }
                    else {
                        if (Character.toLowerCase(charA) - Character.toLowerCase(charB) != 0) {
                            return Character.toLowerCase(charA) - Character.toLowerCase(charB);
                        }
                    }
                }
            }
            return valueA.compareToIgnoreCase(valueB);
        }

        /**
         * Evaluate port <b>index/number</b> from <b>startIndex</b> to the number end. For example:
         * for port name <b>serial-123-FF</b> you should invoke this method with <b>startIndex = 7</b>
         *
         * @return If port <b>index/number</b> correctly evaluated it value will be returned<br>
         * <b>returnArray[0] = index/number</b><br>
         * <b>returnArray[1] = stopIndex</b><br>
         *
         * If incorrect:<br>
         * <b>returnArray[0] = -1</b><br>
         * <b>returnArray[1] = startIndex</b><br>
         *
         * For this name <b>serial-123-FF</b> result is:
         * <b>returnArray[0] = 123</b><br>
         * <b>returnArray[1] = 10</b><br>
         */
        private int[] getNumberAndLastIndex(String str, int startIndex) {
            String numberValue = "";
            int[] returnValues = {
                -1, startIndex
            };
            for (int i = startIndex; i < str.length(); i++) {
                returnValues[1] = i;
                char c = str.charAt(i);
                if (Character.isDigit(c)) {
                    numberValue += c;
                }
                else {
                    break;
                }
            }
            try {
                returnValues[0] = Integer.valueOf(numberValue);
            } catch (Exception ex) {
                //Do nothing
            }
            return returnValues;
        }
    };

    final SelectionAdapter selectionListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            changeSupport.firePropertyChange("selection", selection, selection = (ComPort) e.widget.getData());
        }
    };

    public SerialPortList(Preferences preferences) {
        this.preferences = preferences;

        if (selection == null) {
            Iterator<String> iter = getAvailablePorts().iterator();
            if (iter.hasNext()) {
                selection = new SerialComPort(iter.next());
            }
        }

        if (selection == null) {
            RemoteDevice[] devices = preferences.getRemoteDevices();
            if (devices.length != 0) {
                selection = new NetworkComPort(devices[0].getName(), devices[0].getIp(), devices[0].getMac(), devices[0].getResetPin());
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }

    public void fillMenu(Menu menu) {
        MenuItem[] items = menu.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i].dispose();
        }

        fillSerialPorts(menu);
        fillNetworkPorts(menu);
    }

    void fillSerialPorts(Menu menu) {
        ComPort comPort;

        for (String portName : getAvailablePorts()) {
            if (selection != null && selection.getPortName().equalsIgnoreCase(portName)) {
                comPort = selection;
            }
            else {
                comPort = new SerialComPort(portName);
            }

            MenuItem item = new MenuItem(menu, SWT.CHECK);
            item.setText(comPort.getDescription());
            item.setSelection(comPort == selection);
            item.setData(comPort);
            item.addSelectionListener(selectionListener);
        }
    }

    void fillNetworkPorts(Menu menu) {
        ComPort comPort;

        RemoteDevice[] devices = preferences.getRemoteDevices();
        if (devices != null && devices.length != 0) {
            if (menu.getItemCount() != 0) {
                new MenuItem(menu, SWT.SEPARATOR);
            }
            for (RemoteDevice descr : devices) {
                if (selection != null && selection.getPortName().equalsIgnoreCase(descr.getPortName())) {
                    comPort = selection;
                }
                else {
                    comPort = new NetworkComPort(descr.getName(), descr.getIp(), descr.getMac(), descr.getResetPin());
                }

                MenuItem item = new MenuItem(menu, SWT.CHECK);
                item.setText(comPort.getDescription());
                item.setSelection(comPort == selection);
                item.setData(comPort);
                item.addSelectionListener(selectionListener);
            }
        }
    }

    Collection<String> getAvailablePorts() {
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_WINDOWS) {
            return getWindowsPortNames(PORTNAMES_REGEXP, PORTNAMES_COMPARATOR);
        }
        return getUnixBasedPortNames(PORTNAMES_PATH, PORTNAMES_REGEXP, PORTNAMES_COMPARATOR);
    }

    private Collection<String> getWindowsPortNames(Pattern pattern, Comparator<String> comparator) {
        TreeSet<String> ports = new TreeSet<String>(comparator);

        String[] portNames = serialInterface.getSerialPortNames();
        if (portNames != null) {
            for (String portName : portNames) {
                if (pattern.matcher(portName).find()) {
                    ports.add(portName);
                }
            }
        }

        return ports;
    }

    private Collection<String> getUnixBasedPortNames(String searchPath, Pattern pattern, Comparator<String> comparator) {
        TreeSet<String> portsTree = new TreeSet<String>(comparator);

        searchPath = (searchPath.equals("") ? searchPath : (searchPath.endsWith("/") ? searchPath : searchPath + "/"));

        File dir = new File(searchPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length > 0) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (!file.isDirectory() && !file.isFile() && pattern.matcher(fileName).find()) {
                        String portName = searchPath + fileName;
                        // For linux ttyS0..31 serial ports check existence by opening each of them
                        if (fileName.startsWith("ttyS")) {
                            long portHandle = serialInterface.openPort(portName, false);//Open port without TIOCEXCL
                            if (portHandle < 0 && portHandle != SerialNativeInterface.ERR_PORT_BUSY) {
                                continue;
                            }
                            else if (portHandle != SerialNativeInterface.ERR_PORT_BUSY) {
                                serialInterface.closePort(portHandle);
                            }
                        }
                        portsTree.add(portName);
                    }
                }
            }
        }

        return portsTree;
    }

    public void setSelection(String portName) {
        if (selection != null) {
            if (selection.getPortName().equalsIgnoreCase(portName)) {
                return;
            }
        }

        for (String availablePortName : getAvailablePorts()) {
            if (availablePortName.equalsIgnoreCase(portName)) {
                selection = new SerialComPort(portName);
                return;
            }
        }

        RemoteDevice[] devices = preferences.getRemoteDevices();
        for (int i = 0; i < devices.length; i++) {
            if (devices[i].getPortName().equalsIgnoreCase(portName)) {
                selection = new NetworkComPort(devices[i].getName(), devices[i].getIp(), devices[i].getMac(), devices[i].getResetPin());
                return;
            }
        }

        selection = null;
    }

    public ComPort getSelection() {
        return selection;
    }

    public void setSelection(ComPort selection) {
        this.selection = selection;

    }

}
