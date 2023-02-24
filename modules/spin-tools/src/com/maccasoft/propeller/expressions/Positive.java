package com.maccasoft.propeller.expressions;

public class Positive extends UnaryOperator {

    public Positive(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if (term.getNumber() instanceof Long) {
            return term.getNumber().longValue();
        }
        return term.getNumber().doubleValue();
    }

    @Override
    public String getLexeme() {
        return "+";
    }

}
