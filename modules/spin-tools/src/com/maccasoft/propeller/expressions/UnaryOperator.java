package com.maccasoft.propeller.expressions;

public abstract class UnaryOperator extends Expression {

    protected final Expression term;

    public abstract String getLexeme();

    public UnaryOperator(Expression term) {
        this.term = term;
    }

    public Expression getTerm() {
        return term;
    }

    @Override
    public boolean isConstant() {
        return term.isConstant();
    }

    @Override
    public boolean isNumber() {
        return term.isNumber();
    }

    @Override
    public String toString() {
        return getLexeme() + term;
    }

}
