package com.maccasoft.propeller.expressions;

public class Add extends BinaryOperator {

    public Add(Expression augend, Expression addend) {
        super(augend, addend);
    }

    public Expression getAugend() {
        return term1;
    }

    public Expression getAddend() {
        return term2;
    }

    @Override
    public Number getNumber() {
        if (!(term1.getNumber() instanceof Double) && !(term2.getNumber() instanceof Double)) {
            return term1.getNumber().intValue() + term2.getNumber().intValue();
        }
        return term1.getNumber().floatValue() + term2.getNumber().floatValue();
    }

    @Override
    public String getLexeme() {
        return "+";
    }

}
