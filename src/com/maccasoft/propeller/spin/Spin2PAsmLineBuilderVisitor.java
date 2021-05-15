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
import com.maccasoft.propeller.spin.Spin2Parser.ConditionContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataValueContext;
import com.maccasoft.propeller.spin.Spin2Parser.DirectiveContext;
import com.maccasoft.propeller.spin.Spin2Parser.EffectContext;
import com.maccasoft.propeller.spin.Spin2Parser.LabelContext;
import com.maccasoft.propeller.spin.Spin2Parser.OpcodeContext;

@SuppressWarnings({
    "rawtypes"
})
public class Spin2PAsmLineBuilderVisitor extends Spin2BaseVisitor {

    Spin2Context scope;
    String label;
    String condition;
    String mnemonic;
    List<Spin2PAsmExpression> arguments = new ArrayList<Spin2PAsmExpression>();
    String effect;

    public Spin2PAsmLineBuilderVisitor(Spin2Context scope) {
        this.scope = scope;
    }

    @Override
    public Object visitLabel(LabelContext ctx) {
        label = ctx.getText();
        return null;
    }

    @Override
    public Object visitCondition(ConditionContext ctx) {
        condition = ctx.getText();
        return null;
    }

    @Override
    public Object visitOpcode(OpcodeContext ctx) {
        mnemonic = ctx.getText();
        return null;
    }

    @Override
    public Object visitDirective(DirectiveContext ctx) {
        mnemonic = ctx.getText();
        return null;
    }

    @Override
    public Object visitArgument(ArgumentContext ctx) {
        Expression expression = Spin2Compiler.compileExpression(scope, ctx.expression());
        arguments.add(new Spin2PAsmExpression(ctx.prefix() != null ? ctx.prefix().getText() : null, expression));
        return null;
    }

    @Override
    public Object visitEffect(EffectContext ctx) {
        effect = ctx.getText();
        return null;
    }

    @Override
    public Object visitDataValue(DataValueContext ctx) {
        Expression expression = Spin2Compiler.compileExpression(scope, ctx.expression(0));
        arguments.add(new Spin2PAsmExpression(null, expression));
        return null;
    }

    public Spin2PAsmLine getLine() {
        return new Spin2PAsmLine(scope, label, condition, mnemonic, arguments, effect);
    }

}
