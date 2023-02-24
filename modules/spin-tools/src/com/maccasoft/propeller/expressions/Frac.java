package com.maccasoft.propeller.expressions;

public class Frac extends BinaryOperator {

    public Frac(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value = term1.getNumber().longValue();
        return ((value << 32) / term2.getNumber().longValue()) & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "frac";
    }

}
