package com.maccasoft.propeller.expressions;

public class Decod extends UnaryOperator {

    public Decod(Expression term) {
        super(term);
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number getNumber() {
        return 1L << (term.getNumber().longValue() & 0x1F);
    }

    @Override
    public String getLexeme() {
        return "|<";
    }

}
