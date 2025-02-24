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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class NetworkComPort extends ComPort {

    public static final int TELNET_PORT = 23;

    public static final int MAX_DATA_SIZE = 1024;

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int RESPONSE_TIMEOUT = 3000;

    String name;
    InetAddress inetAddr;
    String mac_address;
    String resetPin;

    HttpClient client;

    Socket socket;
    OutputStream os;
    InputStream is;

    AtomicReference<EventThread> eventThread = new AtomicReference<>();

    class EventThread extends Thread {

        final ComPortEventListener eventListener;
        private boolean threadTerminated = false;

        AtomicBoolean eventNotified = new AtomicBoolean();

        public EventThread(ComPortEventListener eventListener) {
            this.eventListener = eventListener;
        }

        @Override
        public void run() {

            while (!threadTerminated) {
                try {
                    try {
                        if (socket != null && socket.isConnected() && !eventNotified.get()) {
                            if (is.available() > 0) {
                                eventNotified.set(true);
                                eventListener.serialEvent(new ComPortEvent(NetworkComPort.this, true, false, false));
                            }
                        }
                    } catch (IOException e) {
                        // Do nothing
                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }

        public void clearEventNotified() {
            eventNotified.set(false);
        }

        public void terminateThread() {
            threadTerminated = true;
        }
    }

    public NetworkComPort(DeviceDescriptor descriptor) {
        this.name = descriptor.name;
        this.inetAddr = descriptor.inetAddr;
        this.mac_address = descriptor.mac_address;
        this.resetPin = descriptor.reset_pin;
    }

    public NetworkComPort(InetAddress inetAddr) {
        this.name = "";
        this.inetAddr = inetAddr;
    }

    public NetworkComPort(String name, String inetAddr, String mac_address, String resetPin) {
        this.name = name;
        try {
            if (inetAddr != null && !inetAddr.isBlank()) {
                this.inetAddr = InetAddress.getByName(inetAddr);
            }
        } catch (UnknownHostException e) {
            // Do nothing
        }
        this.mac_address = mac_address;
        this.resetPin = resetPin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" (");
        if (inetAddr != null) {
            sb.append("ip=");
            sb.append(inetAddr.getHostAddress());
        }
        if (mac_address != null && !mac_address.isBlank()) {
            if (inetAddr != null) {
                sb.append(", ");
            }
            sb.append("mac=");
            sb.append(mac_address);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String getPortName() {
        if (mac_address != null && !mac_address.isBlank()) {
            return mac_address;
        }
        if (inetAddr != null) {
            return inetAddr.getHostAddress();
        }
        return "";
    }

    public InetAddress getInetAddr() {
        return inetAddr;
    }

    public void setInetAddr(InetAddress inetAddr) {
        this.inetAddr = inetAddr;
    }

    public String getMacAddress() {
        return mac_address;
    }

    public void setMacAddress(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getResetPin() {
        return resetPin;
    }

    public void setResetPin(String resetPin) {
        this.resetPin = resetPin;
    }

    @Override
    public boolean isOpened() {
        if (socket == null) {
            return false;
        }
        return !socket.isClosed();
    }

    @Override
    public boolean openPort() throws ComPortException {
        try {
            client = HttpClient.newBuilder() //
                .version(Version.HTTP_1_1) //
                .connectTimeout(Duration.ofMillis(CONNECT_TIMEOUT)) //
                .build();

            socket = new Socket(inetAddr, TELNET_PORT);
            os = socket.getOutputStream();
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean setParams(int baudRate, int dataBits, int stopBits, int parity) throws ComPortException {
        try {
            Builder builder = HttpRequest.newBuilder(new URI("http://" + inetAddr.getHostAddress() + "/wx/setting?name=baud-rate&value=" + baudRate));
            HttpRequest httpRequest = builder.POST(BodyPublishers.noBody()).build();
            client.send(httpRequest, BodyHandlers.ofString());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void closePort() {
        try {
            removeEventListener();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;
    }

    @Override
    public void hwreset(int delay) { /* delay ignored */
        try {
            StringBuilder sb = new StringBuilder(128);
            sb.append("http://");
            sb.append(inetAddr.getHostAddress());
            sb.append("/propeller/reset");
            if (resetPin != null && !resetPin.isBlank()) {
                sb.append("?reset-pin=" + resetPin);
            }
            Builder builder = HttpRequest.newBuilder(new URI(sb.toString()));
            HttpRequest httpRequest = builder.POST(BodyPublishers.noBody()).build();
            client.send(httpRequest, BodyHandlers.ofString());

            Thread.sleep(delay);

            if (is.available() > 0) {
                is.readAllBytes();
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            // Do nothing
            e.printStackTrace();
        }
    }

    @Override
    public int readByteWithTimeout(int timeout) throws ComPortException {
        while (timeout > 0) {
            try {
                int len = is.available();
                if (len > 0) {
                    return is.read();
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                // Do nothing
            }
            timeout--;
        }
        return -1;
    }

    @Override
    public boolean writeInt(int singleInt) throws ComPortException {
        try {
            os.write(singleInt);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean writeByte(byte singleByte) throws ComPortException {
        try {
            os.write(singleByte);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean writeBytes(byte[] buffer) throws ComPortException {
        try {
            os.write(buffer);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public byte[] readBytes() throws ComPortException {
        try {
            int len = is.available();
            if (len == 0) {
                return null;
            }
            byte[] rx = new byte[len];
            is.read(rx);
            eventThread.getAndUpdate(new UnaryOperator<NetworkComPort.EventThread>() {

                @Override
                public EventThread apply(EventThread t) {
                    if (t != null) {
                        t.clearEventNotified();
                    }
                    return t;
                }
            });
            return rx;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
    }

    @Override
    public void setEventListener(ComPortEventListener listener) throws ComPortException {
        eventThread.getAndUpdate(new UnaryOperator<NetworkComPort.EventThread>() {

            @Override
            public EventThread apply(EventThread t) {
                if (t != null) {
                    t.terminateThread();
                }
                t = new EventThread(listener);
                t.start();
                return t;
            }
        });
    }

    @Override
    public void removeEventListener() throws ComPortException {
        eventThread.getAndUpdate(new UnaryOperator<NetworkComPort.EventThread>() {

            @Override
            public EventThread apply(EventThread t) {
                if (t != null) {
                    t.terminateThread();
                }
                return null;
            }
        });
    }

    @Override
    public void setRTS(boolean enable) throws ComPortException {
        try {
            Builder builder = HttpRequest.newBuilder(new URI("http://" + inetAddr.getHostAddress() + "/wx/setting?name=pin-gpio13&value=" + (!enable ? 1 : 0)));
            HttpRequest httpRequest = builder.POST(BodyPublishers.noBody()).build();
            client.send(httpRequest, BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
    }

    @Override
    public void setDTR(boolean enable) throws ComPortException {
        try {
            Builder builder = HttpRequest.newBuilder(new URI("http://" + inetAddr.getHostAddress() + "/wx/setting?name=pin-gpio12&value=" + (!enable ? 1 : 0)));
            HttpRequest httpRequest = builder.POST(BodyPublishers.noBody()).build();
            client.send(httpRequest, BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isCTS() throws ComPortException {
        return false;
    }

    @Override
    public boolean isDSR() throws ComPortException {
        return false;
    }

    public int readLong(int timeout) throws ComPortException, InterruptedException {
        byte[] rx = new byte[4];
        try {
            if (is.available() < 4) {
                long now = System.currentTimeMillis();
                do {
                    if ((System.currentTimeMillis() - now) > timeout) {
                        throw new InterruptedException();
                    }
                    Thread.sleep(1);
                } while (is.available() < 4);
            }
            is.read(rx);
            return (rx[0] & 0xFF) | ((rx[1] & 0xFF) << 8) | ((rx[2] & 0xFF) << 16) | ((rx[3] & 0xFF) << 24);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
    }

    public boolean writeBytes(byte[] buffer, int ofs, int len) throws ComPortException {
        try {
            os.write(buffer, ofs, len);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ComPortException(e.getMessage(), e);
        }
        return true;
    }

    public HttpClient getHttpClient() {
        return client;
    }

    @Override
    public boolean writeString(String string) throws ComPortException {
        return writeBytes(string.getBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NetworkComPort)) {
            return false;
        }
        NetworkComPort other = (NetworkComPort) o;
        return mac_address.equals(other.mac_address);
    }

}
