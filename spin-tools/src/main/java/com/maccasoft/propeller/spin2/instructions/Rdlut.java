/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Rdlut extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S_WC_WZ_WCZ.check(arguments, effect)) {
            return new Rdlut_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * RDLUT   D,{#}S/P {WC/WZ/WCZ}
     */
    public class Rdlut_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Rdlut_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.effect = effect;
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1010101 CZI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();

            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1010101);
            value = cz.setValue(value, encodeEffect(effect));

            try {
                value = d.setValue(value, getDst(dst, false));
            } catch (CompilerException e) {
                msgs.addMessage(e);
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), dst.getData()));
            }

            try {
                if (src.isPtr()) {
                    value = i.setBoolean(value, true);
                }
                else {
                    int addr = src.getInteger();
                    if (!src.isLongLiteral()) {
                        if (src.isLiteral()) {
                            if (addr < 0 || addr > 511) {
                                throw new Exception("constants must be from 0 to 511");
                            }
                        }
                        else if (addr < 0 || addr > 0x1FF) {
                            throw new Exception("source register cannot exceed $1FF");
                        }
                    }
                    value = i.setBoolean(value, src.isLiteral());
                }
                value = s.setValue(value, src.getInteger());
            } catch (CompilerException e) {
                msgs.addMessage(e);
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), src.getData()));
            }

            if (msgs.hasChilds()) {
                throw msgs;
            }

            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

    }
}
