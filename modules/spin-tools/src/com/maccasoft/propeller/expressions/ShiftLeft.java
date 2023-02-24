package com.maccasoft.propeller.expressions;

public class ShiftLeft extends BinaryOperator {

    public ShiftLeft(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value = term1.getNumber().longValue() & 0xFFFFFFFFL;
        return value << (term2.getNumber().longValue() & 0x1F);
    }

    @Override
    public String getLexeme() {
        return "<<";
    }

}
