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
        if (term1.getNumber() instanceof Integer && term2.getNumber() instanceof Integer) {
            return term1.getNumber().intValue() * term2.getNumber().intValue();
        }
        return term1.getNumber().floatValue() * term2.getNumber().floatValue();
    }

    @Override
    public String getLexeme() {
        return "*";
    }

}
