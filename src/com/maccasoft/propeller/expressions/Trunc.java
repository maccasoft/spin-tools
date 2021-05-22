package com.maccasoft.propeller.expressions;

public class Trunc extends Function {

    public Trunc(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if (term.getNumber() instanceof Double) {
            return new Double(Math.floor(term.getNumber().doubleValue())).longValue();
        }
        return term.getNumber();
    }

    @Override
    public String getLexeme() {
        return "trunc";
    }

}
