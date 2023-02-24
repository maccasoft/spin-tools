package com.maccasoft.propeller.expressions;

public class Addbits extends BinaryOperator {

    public Addbits(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        return (term1.getNumber().longValue() & 0x1F) | ((term2.getNumber().longValue() & 0x1F) << 5);
    }

    @Override
    public String getLexeme() {
        return "addbits";
    }

}
