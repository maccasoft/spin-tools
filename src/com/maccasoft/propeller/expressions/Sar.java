package com.maccasoft.propeller.expressions;

public class Sar extends BinaryOperator {

    public Sar(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long result = term1.getNumber().longValue() >> (term2.getNumber().longValue() & 0xFF);
        return result & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "sar";
    }

}
