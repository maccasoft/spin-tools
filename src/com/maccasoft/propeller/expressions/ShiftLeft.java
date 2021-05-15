package com.maccasoft.propeller.expressions;

public class ShiftLeft extends BinaryOperator {

    public ShiftLeft(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        return term1.getNumber().intValue() << term2.getNumber().intValue();
    }

    @Override
    public String getLexeme() {
        return "<<";
    }

}
