/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Positive;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantsContext;
import com.maccasoft.propeller.spin.Spin2Parser.ExpressionContext;

@SuppressWarnings({
    "unchecked", "rawtypes"
})
public class Spin2Compiler extends Spin2BaseVisitor implements Context {

    Map<String, Expression> constants = new HashMap<String, Expression>();

    public Spin2Compiler() {
    }

    @Override
    public Object visitConstants(ConstantsContext ctx) {
        ctx.accept(new Spin2BaseVisitor() {
            int enumValue = 0, enumIncrement = 1;

            @Override
            public Object visitConstant(ConstantContext ctx) {
                if (ctx.start != null) {
                    enumValue = Integer.parseInt(ctx.start.getText());
                    enumIncrement = 1;
                }

                if (ctx.step != null) {
                    enumIncrement = Integer.parseInt(ctx.step.getText());
                }

                if (ctx.name != null) {
                    if (ctx.exp != null) {
                        Expression expression = compileExpression(ctx.exp);
                        constants.put(ctx.name.getText().toLowerCase(), expression);
                    }
                    else {
                        constants.put(ctx.name.getText().toLowerCase(), new NumberLiteral(enumValue));
                    }

                    enumValue += ctx.multiplier != null ? enumIncrement * Integer.parseInt(ctx.multiplier.getText()) : enumIncrement;
                }

                return null;
            }

        });
        return null;
    }

    Expression compileExpression(ExpressionContext ctx) {
        if (ctx.operator != null) {
            String op = ctx.operator.getText();
            if (ctx.left == null) {
                if ("+".equals(op)) {
                    return new Positive(compileExpression(ctx.right));
                }
                else if ("-".equals(op)) {
                    return new Negative(compileExpression(ctx.right));
                }
            }
            else {
                if ("+".equals(op)) {
                    return new Add(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if ("-".equals(op)) {
                    return new Subtract(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if ("/".equals(op)) {
                    return new Divide(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if ("*".equals(op)) {
                    return new Multiply(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if ("addpins".equalsIgnoreCase(op)) {
                    return new Addpins(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if (">>".equals(op)) {
                    return new ShiftRight(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if ("<<".equals(op)) {
                    return new ShiftLeft(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if ("//".equals(op)) {
                    return new Modulo(compileExpression(ctx.left), compileExpression(ctx.right));
                }
                else if ("round".equalsIgnoreCase(op)) {
                    return new Round(compileExpression(ctx.exp));
                }
                else if ("trunc".equalsIgnoreCase(op)) {
                    return new Trunc(compileExpression(ctx.exp));
                }
            }
        }
        else if (ctx.atom() != null) {
            String s = ctx.atom().getText();
            if (s.startsWith("%%")) {
                return new NumberLiteral(Integer.parseInt(s.substring(1).replace("_", ""), 4));
            }
            else if (s.startsWith("%")) {
                return new NumberLiteral(Integer.parseInt(s.substring(1).replace("_", ""), 2));
            }
            else if (s.startsWith("$")) {
                return new NumberLiteral(Integer.parseInt(s.substring(1).replace("_", ""), 16));
            }
            else if ((s.charAt(0) >= '0' && s.charAt(0) <= '9')) {
                if (s.contains(".")) {
                    return new NumberLiteral(Double.parseDouble(s.replace("_", "")));
                }
                else {
                    return new NumberLiteral(Integer.parseInt(s.replace("_", "")));
                }
            }
            return new Identifier(s, this);
        }
        return new NumberLiteral(0);
    }

    @Override
    public Expression getSymbol(String name) {
        Expression exp = constants.get(name.toLowerCase());
        return exp;
    }

    @Override
    public boolean hasSymbol(String name) {
        boolean result = constants.containsKey(name.toLowerCase());
        return result;
    }

    @Override
    public int getAddress() {
        return 0;
    }

}
