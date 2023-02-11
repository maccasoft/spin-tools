package com.maccasoft.propeller.expressions;

public class Nan extends Function {

    public Nan(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return Double.isNaN(term.getNumber().doubleValue()) ? -1L : 0L;
    }

    @Override
    public String getLexeme() {
        return "nan";
    }

}
