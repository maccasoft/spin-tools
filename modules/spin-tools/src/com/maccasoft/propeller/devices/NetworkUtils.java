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

package com.maccasoft.propeller.devices;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkUtils {

    public static final int HTTP_PORT = 80;
    public static final int TELNET_PORT = 23;
    public static final int DISCOVER_PORT = 32420;

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int RESPONSE_TIMEOUT = 3000;
    public static final int DISCOVER_REPLY_TIMEOUT = 250;
    public static final int DISCOVER_ATTEMPTS = 3;

    public static Collection<DeviceDescriptor> getAvailableDevices() {
        Map<String, DeviceDescriptor> map = new HashMap<>();

        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface nif = nets.nextElement();
                if (nif.isUp() && !nif.isLoopback()) {
                    for (InterfaceAddress addr : nif.getInterfaceAddresses()) {
                        InetAddress inetAddr = addr.getBroadcast();
                        if (inetAddr != null) {
                            DeviceDescriptor result = probe(inetAddr, true);
                            if (result != null) {
                                map.put(result.mac_address, result);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<DeviceDescriptor> list = new ArrayList<>(map.values());
        Collections.sort(list, new Comparator<DeviceDescriptor>() {

            @Override
            public int compare(DeviceDescriptor o1, DeviceDescriptor o2) {
                return o1.name.compareTo(o2.name);
            }

        });

        return map.values();
    }

    public static DeviceDescriptor probe(InetAddress inetAddr) throws IOException {
        return probe(inetAddr, false);
    }

    static DeviceDescriptor probe(InetAddress inetAddr, boolean broadcast) throws IOException {
        DeviceDescriptor found = null;
        byte[] buffer = new byte[2048];

        DatagramSocket socket = new DatagramSocket(DISCOVER_PORT);
        if (broadcast) {
            socket.setBroadcast(true);
        }
        socket.setSoTimeout(DISCOVER_REPLY_TIMEOUT);

        for (int i = 0; i < DISCOVER_ATTEMPTS && found == null; i++) {
            socket.send(new DatagramPacket(new byte[] {
                0x00, 0x00, 0x00, 0x00
            }, 4, inetAddr, DISCOVER_PORT));

            try {
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                do {
                    socket.receive(response);
                    if (response.getLength() > 0) {
                        byte[] data = response.getData();
                        if (data[0] != 0x00) {
                            String json = new String(data, 0, response.getLength());
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                found = mapper.readValue(json, DeviceDescriptor.class);
                                if (found != null) {
                                    found.inetAddr = response.getAddress();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                } while (found == null);
            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        socket.close();

        return found;
    }

}
