package com.maccasoft.propeller.expressions;

public class Equals extends BinaryOperator {

    public Equals(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        return term1.getNumber().equals(term2.getNumber()) ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "==";
    }

}
