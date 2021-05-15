package com.maccasoft.propeller.expressions;

public class Round extends UnaryOperator {

    public Round(Expression term) {
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