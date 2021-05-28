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

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin.Spin2Parser.ArgumentContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantExpressionContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataLineContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataValueContext;
import com.maccasoft.propeller.spin.Spin2Parser.LabelContext;
import com.maccasoft.propeller.spin.Spin2Parser.OpcodeContext;

@SuppressWarnings({
    "rawtypes"
})
public class Spin2PAsmLineBuilderVisitor extends Spin2ParserBaseVisitor {

    Spin2Context scope;
    String label;
    String condition;
    String mnemonic;
    List<Spin2PAsmExpression> arguments = new ArrayList<Spin2PAsmExpression>();
    String modifier;

    public Spin2PAsmLineBuilderVisitor(Spin2Context scope, DataLineContext ctx) {
        this.scope = scope;
        if (ctx.condition != null) {
            this.condition = ctx.condition.getText();
        }
        if (ctx.directive != null) {
            this.mnemonic = ctx.directive.getText();
        }
        if (ctx.modifier != null) {
            this.modifier = ctx.modifier.getText();
        }
    }

    @Override
    public Object visitLabel(LabelContext ctx) {
        label = ctx.getText();
        return null;
    }

    @Override
    public Object visitOpcode(OpcodeContext ctx) {
        mnemonic = ctx.getText();
        return null;
    }

    @Override
    public Object visitArgument(ArgumentContext ctx) {
        Expression expression = Spin2Compiler.buildExpression(scope, ctx.constantExpression());
        arguments.add(new Spin2PAsmExpression(ctx.prefix != null ? ctx.prefix.getText() : null, expression, null));
        return null;
    }

    @Override
    public Object visitConstantExpression(ConstantExpressionContext ctx) {
        Expression expression = Spin2Compiler.buildExpression(scope, ctx);
        arguments.add(new Spin2PAsmExpression(null, expression, null));
        return null;
    }

    @Override
    public Object visitDataValue(DataValueContext ctx) {
        List<ConstantExpressionContext> list = ctx.constantExpression();
        Expression expression = Spin2Compiler.buildExpression(scope, list.get(0));
        Expression count = list.size() > 1 ? Spin2Compiler.buildExpression(scope, list.get(1)) : null;
        arguments.add(new Spin2PAsmExpression(null, expression, count));
        return null;
    }

    public Spin2PAsmLine getLine() {
        return new Spin2PAsmLine(scope, label, condition, mnemonic, arguments, modifier);
    }

}
