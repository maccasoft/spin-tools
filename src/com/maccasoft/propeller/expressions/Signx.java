package com.maccasoft.propeller.expressions;

public class Signx extends BinaryOperator {

    public Signx(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long cl = ~term2.getNumber().longValue() & 0x3F;
        long value = term1.getNumber().longValue();
        value = value << cl;
        return value >> cl;
    }

    @Override
    public String getLexeme() {
        return "signx";
    }

}
