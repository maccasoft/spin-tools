package com.maccasoft.propeller.expressions;

public class IfElse extends Passthrough {

    private final Expression condition;
    private final Expression trueTerm;
    private final Expression falseTerm;

    public IfElse(Expression condition, Expression trueTerm, Expression falseTerm) {
        this.condition = condition;
        this.trueTerm = trueTerm;
        this.falseTerm = falseTerm;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getTrueTerm() {
        return trueTerm;
    }

    public Expression getFalseTerm() {
        return falseTerm;
    }

    public boolean isTrue() {
        return condition.getNumber().intValue() != 0;
    }

    @Override
    public Expression resolve() {
        return isTrue() ? trueTerm : falseTerm;
    }

    @Override
    public boolean isNumber() {
        return trueTerm.isNumber() && falseTerm.isNumber() || super.isNumber();
    }

    @Override
    public boolean isString() {
        return trueTerm.isString() && falseTerm.isString() || super.isString();
    }

    @Override
    public String toString() {
        return "" + condition + " ? " + trueTerm + " : " + falseTerm;
    }

}
