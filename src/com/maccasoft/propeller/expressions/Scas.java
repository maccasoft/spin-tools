package com.maccasoft.propeller.expressions;

public class Scas extends BinaryOperator {

    public Scas(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value = (term1.getNumber().longValue() * term2.getNumber().longValue());
        return (value >> 30) & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "scas";
    }

}
