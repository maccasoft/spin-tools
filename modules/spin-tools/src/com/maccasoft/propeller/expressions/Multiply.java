package com.maccasoft.propeller.expressions;

public class Multiply extends BinaryOperator {

    public Multiply(Expression multiplicand, Expression multiplier) {
        super(multiplicand, multiplier);
    }

    public Expression getMultiplicand() {
        return term1;
    }

    public Expression getMultiplier() {
        return term2;
    }

    @Override
    public Number getNumber() {
        if ((term1.getNumber() instanceof Long) && (term2.getNumber() instanceof Long)) {
            return term1.getNumber().longValue() * term2.getNumber().longValue();
        }
        return term1.getNumber().doubleValue() * term2.getNumber().doubleValue();
    }

    @Override
    public String getLexeme() {
        return "*";
    }

}
