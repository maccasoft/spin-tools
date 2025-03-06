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

package com.maccasoft.propeller.devices;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.maccasoft.propeller.Preferences;
import com.maccasoft.propeller.Preferences.RemoteDevice;

import jssc.SerialPortList;

public class ComPortList {

    Preferences preferences;

    ComPort selection;

    PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    final SelectionAdapter selectionListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            changeSupport.firePropertyChange("selection", selection, selection = (ComPort) e.widget.getData());
        }
    };

    public ComPortList(Preferences preferences) {
        this.preferences = preferences;

        if (selection == null) {
            Iterator<String> iter = Arrays.asList(SerialPortList.getPortNames()).iterator();
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
        Set<String> blacklistedPorts = preferences.getBlacklistedPorts();

        for (String portName : Arrays.asList(SerialPortList.getPortNames())) {
            if (!blacklistedPorts.contains(portName)) {
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

    public void setSelection(String portName) {
        if (selection != null) {
            if (selection.getPortName().equalsIgnoreCase(portName)) {
                return;
            }
        }

        for (String availablePortName : Arrays.asList(SerialPortList.getPortNames())) {
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
