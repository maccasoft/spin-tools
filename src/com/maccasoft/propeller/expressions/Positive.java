package com.maccasoft.propeller.expressions;

public class Positive extends UnaryOperator {

    public Positive(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if (term.getNumber() instanceof Integer) {
            return term.getNumber().intValue();
        }
        return term.getNumber().doubleValue();
    }

    @Override
    public String getLexeme() {
        return "+";
    }

}
