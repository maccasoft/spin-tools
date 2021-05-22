package com.maccasoft.propeller.expressions;

public abstract class Function extends Expression {

    protected final Expression term;

    public abstract String getLexeme();

    public Function(Expression term) {
        this.term = term;
    }

    public Expression getTerm() {
        return term;
    }

    @Override
    public boolean isNumber() {
        return term.isNumber();
    }

    @Override
    public String toString() {
        return getLexeme() + "(" + term + ")";
    }

}
