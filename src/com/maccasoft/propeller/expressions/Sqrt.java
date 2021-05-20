package com.maccasoft.propeller.expressions;

public class Sqrt extends UnaryOperator {

    public Sqrt(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if (term.getNumber() instanceof Double) {
            return new Long(Math.round(term.getNumber().doubleValue())).intValue();
        }
        return term.getNumber();
    }

    @Override
    public String getLexeme() {
        return "round";
    }

}
