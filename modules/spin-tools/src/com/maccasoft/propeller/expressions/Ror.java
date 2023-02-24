package com.maccasoft.propeller.expressions;

public class Ror extends BinaryOperator {

    public Ror(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value1 = term1.getNumber().longValue();
        long value2 = term1.getNumber().longValue();
        long result = (value1 >> value2) | (value1 << (32 - value2));
        return result & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "ror";
    }

}
