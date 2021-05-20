package com.maccasoft.propeller.expressions;

public class LessOrEquals extends BinaryOperator {

    public LessOrEquals(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        if (term1.getNumber() instanceof Long && term2.getNumber() instanceof Long) {
            return term1.getNumber().longValue() <= term2.getNumber().longValue() ? -1 : 0;
        }
        return term1.getNumber().doubleValue() <= term2.getNumber().doubleValue() ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "<=";
    }

}
