/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.maccasoft.propeller.Propeller2Loader;
import com.maccasoft.propeller.SpinObject.DataObject;

public class Spin2Debugger {

    public static List<DataObject> decodeDebugData(byte[] data) {
        boolean asmMode = false;
        List<DataObject> l = new ArrayList<DataObject>();

        int i = 0, e;
        while (i < data.length) {
            if (data[i] == Spin2Debug.DBC_DONE) {
                l.add(new DataObject(new byte[] {
                    data[i]
                }, "DONE"));
                i++;
            }
            else if ((data[i] & 0xF0) == 0) {
                switch (data[i]) {
                    case Spin2Debug.DBC_ASMMODE:
                        l.add(new DataObject(new byte[] {
                            data[i]
                        }, "ASMMODE"));
                        asmMode = true;
                        i++;
                        break;
                    case Spin2Debug.DBC_IF:
                        e = i + 1;
                        if (asmMode) {
                            if (data[i] == 0b01000000) {
                                e += 5;
                            }
                            else {
                                e += 2;
                            }
                        }
                        l.add(new DataObject(Arrays.copyOfRange(data, i, e), "IF"));
                        i = e;
                        break;
                    case Spin2Debug.DBC_IFNOT:
                        e = i + 1;
                        if (asmMode) {
                            if (data[i] == 0b01000000) {
                                e += 5;
                            }
                            else {
                                e += 2;
                            }
                        }
                        l.add(new DataObject(Arrays.copyOfRange(data, i, e), "IFNOT"));
                        i = e;
                        break;
                    case Spin2Debug.DBC_COGN:
                        l.add(new DataObject(new byte[] {
                            data[i]
                        }, "COGN"));
                        i++;
                        break;
                    case Spin2Debug.DBC_CHAR:
                        e = i + 1;
                        if (asmMode) {
                            if (data[i] == 0b01000000) {
                                e += 5;
                            }
                            else {
                                e += 2;
                            }
                        }
                        l.add(new DataObject(Arrays.copyOfRange(data, i, e), "CHAR"));
                        i = e;
                        break;
                    case Spin2Debug.DBC_STRING: {
                        e = i + 1;
                        StringBuilder sb = new StringBuilder();
                        sb.append("STRING (");
                        while (e < data.length) {
                            if (data[e] == '\0') {
                                e++;
                                break;
                            }
                            sb.append(String.format("%c", (data[e] >= ' ' && data[e] <= 0x7F) ? data[e] : '.'));
                            e++;
                        }
                        sb.append(")");
                        l.add(new DataObject(Arrays.copyOfRange(data, i, e), sb.toString()));
                        i = e;
                        break;
                    }
                    case Spin2Debug.DBC_DELAY:
                        e = i + 1;
                        if (asmMode) {
                            if (data[i] == 0b01000000) {
                                e += 5;
                            }
                            else {
                                e += 2;
                            }
                        }
                        l.add(new DataObject(Arrays.copyOfRange(data, i, e), "DLY"));
                        i = e;
                        break;
                    default:
                        l.add(new DataObject(Arrays.copyOfRange(data, i, data.length)));
                        i = data.length;
                        break;
                }
            }
            else {
                StringBuilder sb = new StringBuilder();
                if ((data[i] & Spin2Debug.DBC_TYPE_STR) == Spin2Debug.DBC_TYPE_STR) {
                    if ((data[i] & Spin2Debug.DBC_FLAG_ARRAY) == Spin2Debug.DBC_FLAG_ARRAY) {
                        sb.append("LSTR");
                    }
                    else {
                        sb.append("ZSTR");
                    }
                }
                else {
                    if ((data[i] & Spin2Debug.DBC_TYPE_DEC) == Spin2Debug.DBC_TYPE_DEC) {
                        sb.append((data[i] & Spin2Debug.DBC_FLAG_SIGNED) != 0 ? "SDEC" : "UDEC");
                    }
                    else if ((data[i] & Spin2Debug.DBC_TYPE_HEX) == Spin2Debug.DBC_TYPE_HEX) {
                        sb.append((data[i] & Spin2Debug.DBC_FLAG_SIGNED) != 0 ? "SHEX" : "UHEX");
                    }
                    else if ((data[i] & Spin2Debug.DBC_TYPE_BIN) == Spin2Debug.DBC_TYPE_BIN) {
                        sb.append((data[i] & Spin2Debug.DBC_FLAG_SIGNED) != 0 ? "SBIN" : "UBIN");
                    }

                    if ((data[i] & 0x0C) == Spin2Debug.DBC_SIZE_BYTE) {
                        sb.append("_BYTE");
                    }
                    else if ((data[i] & 0x0C) == Spin2Debug.DBC_SIZE_WORD) {
                        sb.append("_WORD");
                    }
                    else if ((data[i] & 0x0C) == Spin2Debug.DBC_SIZE_LONG) {
                        sb.append("_LONG");
                    }

                    if ((data[i] & Spin2Debug.DBC_FLAG_ARRAY) != 0) {
                        sb.append("_ARRAY");
                    }
                }
                e = i + 1;

                if ((data[i] & Spin2Debug.DBC_FLAG_NOEXPR) == 0) {
                    sb.append("(");
                    while (e < data.length) {
                        if (data[e] == '\0') {
                            e++;
                            break;
                        }
                        sb.append(String.format("%c", (data[e] >= ' ' && data[e] <= 0x7F) ? data[e] : '.'));
                        e++;
                    }
                    sb.append(")");
                }

                if (asmMode) {
                    if (data[e] == 0b01000000) {
                        e += 5;
                    }
                    else {
                        e += 2;
                    }
                    if ((data[i] & Spin2Debug.DBC_FLAG_ARRAY) != 0) {
                        if (data[e] == 0b01000000) {
                            e += 5;
                        }
                        else {
                            e += 2;
                        }
                    }
                }

                l.add(new DataObject(Arrays.copyOfRange(data, i, e), sb.toString()));
                i = e;
            }
        }

        return l;
    }

    byte[] code = new byte[0];

    public Spin2Debugger() {
        InputStream is = getClass().getResourceAsStream("Spin2_debugger.binary");
        try {
            code = new byte[is.available()];
            is.read(code);
            writeLong(0x0148, Propeller2Loader.UPLOAD_BAUD_RATE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTxPin(int txPin) {
        writeLong(0x0140, txPin);
    }

    public void setRxPin(int rxPin) {
        writeLong(0x0144, rxPin);
    }

    public void setBaud(int baud) {
        writeLong(0x0148, baud);
    }

    public void setClkFreq(int freq) {
        writeLong(0xD4, freq);
    }

    public void setClkMode1(int mode) {
        writeLong(0xD8, mode);
    }

    public void setClkMode2(int mode) {
        writeLong(0xDC, mode);
    }

    public void setDelay(int delay) {
        writeLong(0xE0, delay);
    }

    public void setAppSize(int size) {
        writeLong(0xE4, size);
    }

    public void setHubset(int set) {
        writeLong(0xE8, set);
    }

    public int getSize() {
        return code.length;
    }

    void writeLong(int index, int value) {
        code[index] = (byte) value;
        code[index + 1] = (byte) (value >> 8);
        code[index + 2] = (byte) (value >> 16);
        code[index + 3] = (byte) (value >> 24);
    }

    int readLong(int index) {
        return (code[index] & 0xFF) | ((code[index + 1] & 0xFF) << 8) | ((code[index + 2] & 0xFF) << 16) | ((code[index + 3] & 0xFF) << 24);
    }

    public byte[] getCode() {
        return code;
    }

}
