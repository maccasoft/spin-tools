package com.maccasoft.propeller.expressions;

public class NotEquals extends BinaryOperator {

    public NotEquals(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        return term1.getNumber().equals(term2.getNumber()) ? 0 : -1;
    }

    @Override
    public String getLexeme() {
        return "!=";
    }

}
