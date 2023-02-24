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
        if ((term1.getNumber() instanceof Long) && (term2.getNumber() instanceof Long)) {
            return term1.getNumber().longValue() - term2.getNumber().longValue();
        }
        return term1.getNumber().doubleValue() - term2.getNumber().doubleValue();
    }

    @Override
    public String getLexeme() {
        return "-";
    }

}
