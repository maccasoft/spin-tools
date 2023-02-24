package com.maccasoft.propeller.spin2.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class BitField extends Spin2Bytecode {

    public static enum Op {
        Read, Write, Setup
    }

    Op op;
    boolean push;
    int bitfield = -1;

    public BitField(Spin2Context context, Op op, int bitfield) {
        super(context);
        this.op = op;
        this.bitfield = bitfield;
    }

    public BitField(Spin2Context context, Op op, boolean push, int bitfield) {
        super(context);
        this.op = op;
        this.push = push;
        this.bitfield = bitfield;
    }

    @Override
    public int getSize() {
        try {
            return getBytes().length;
        } catch (Exception e) {
            // Do nothing
        }
        return 5;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (bitfield == -1) {
                os.write(0xDE); // Read (pop)
            }
            else {
                if (bitfield >= 0 && bitfield <= 15) {
                    os.write(0xE0 + bitfield);
                }
                else if (bitfield >= 16 && bitfield <= 31) {
                    os.write(0xF0 + (bitfield - 16));
                }
                else {
                    os.write(0xDF);
                    os.write(Constant.wrVar(bitfield));
                }
            }

            if (op == Op.Read) {
                os.write(0x80);
            }
            else if (op == Op.Write) {
                os.write(push ? 0x82 : 0x81);
            }
        } catch (IOException e) {
            // Do nothing
        }

        return os.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BITFIELD_");

        switch (op) {
            case Read:
                sb.append("READ");
                break;
            case Write:
                sb.append("WRITE");
                break;
            case Setup:
                sb.append("SETUP");
                break;
        }

        if (bitfield == -1) {
            sb.append(" (pop)");
        }
        else {
            if (bitfield >= 0 && bitfield <= 15) {
                sb.append(" (short)");
            }
            else if (bitfield >= 16 && bitfield <= 31) {
                sb.append(" (short)");
            }
        }

        if (op == Op.Write && push) {
            sb.append(" (push)");
        }

        return sb.toString();
    }

}
