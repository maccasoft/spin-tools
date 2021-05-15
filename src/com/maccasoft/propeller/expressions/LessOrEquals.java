package com.maccasoft.propeller.expressions;

public class LessOrEquals extends BinaryOperator {

    public LessOrEquals(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        if (term1.getNumber() instanceof Integer && term2.getNumber() instanceof Integer) {
            return term1.getNumber().intValue() <= term2.getNumber().intValue() ? -1 : 0;
        }
        return term1.getNumber().doubleValue() <= term2.getNumber().doubleValue() ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "<=";
    }

}
