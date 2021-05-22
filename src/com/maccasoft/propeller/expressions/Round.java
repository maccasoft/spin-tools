package com.maccasoft.propeller.expressions;

public class Round extends Function {

    public Round(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if (term.getNumber() instanceof Double) {
            return new Long(Math.round(term.getNumber().doubleValue())).longValue();
        }
        return term.getNumber();
    }

    @Override
    public String getLexeme() {
        return "round";
    }

}
