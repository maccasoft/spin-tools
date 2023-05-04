package com.maccasoft.propeller.expressions;

public class GreaterThanUnsigned extends BinaryOperator {

    public GreaterThanUnsigned(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        return (term1.getNumber().longValue() & 0xFFFFFFFFL) > (term2.getNumber().longValue() & 0xFFFFFFFFL) ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "+>";
    }

}
