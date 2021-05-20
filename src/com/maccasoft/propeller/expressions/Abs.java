package com.maccasoft.propeller.expressions;

public class Abs extends UnaryOperator {

    public Abs(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if (term.getNumber() instanceof Double) {
            return Math.abs(term.getNumber().doubleValue());
        }
        return Math.abs(term.getNumber().longValue());
    }

    @Override
    public String getLexeme() {
        return "round";
    }

}
