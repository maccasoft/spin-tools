package com.maccasoft.propeller.expressions;

public class Sca extends BinaryOperator {

    public Sca(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value = (term1.getNumber().longValue() * term2.getNumber().longValue());
        return (value >> 32) & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "frac";
    }

}
