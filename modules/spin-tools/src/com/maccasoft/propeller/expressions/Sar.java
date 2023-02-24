package com.maccasoft.propeller.expressions;

public class Sar extends BinaryOperator {

    public Sar(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value = term1.getNumber().longValue();
        return value >> (term2.getNumber().longValue() & 0xFF);
    }

    @Override
    public String getLexeme() {
        return "sar";
    }

}
