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
        if ((term1.getNumber() instanceof Long) && (term2.getNumber() instanceof Long)) {
            return term1.getNumber().longValue() + term2.getNumber().longValue();
        }
        return term1.getNumber().doubleValue() + term2.getNumber().doubleValue();
    }

    @Override
    public String getLexeme() {
        return "+";
    }

}
