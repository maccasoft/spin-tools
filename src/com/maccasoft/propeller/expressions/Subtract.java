package com.maccasoft.propeller.expressions;

public class Subtract extends BinaryOperator {

    public Subtract(Expression minuend, Expression subtrahend) {
        super(minuend, subtrahend);
    }

    public Expression getMinuend() {
        return term1;
    }

    public Expression getSubtrahend() {
        return term2;
    }

    @Override
    public Number getNumber() {
        if (term1.getNumber() instanceof Integer && term2.getNumber() instanceof Integer) {
            return term1.getNumber().intValue() - term2.getNumber().intValue();
        }
        return term1.getNumber().floatValue() - term2.getNumber().floatValue();
    }

    @Override
    public String getLexeme() {
        return "-";
    }

}
