package com.maccasoft.propeller.expressions;

public class Bmask extends UnaryOperator {

    public Bmask(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return (2L << (term.getNumber().longValue() & 0x1F)) - 1L;
    }

    @Override
    public String getLexeme() {
        return "bmask";
    }

}
