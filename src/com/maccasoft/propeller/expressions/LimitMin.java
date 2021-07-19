package com.maccasoft.propeller.expressions;

public class LimitMin extends BinaryOperator {

    public LimitMin(Expression multiplicand, Expression multiplier) {
        super(multiplicand, multiplier);
    }

    @Override
    public Number getNumber() {
        if ((term1.getNumber() instanceof Long) && (term2.getNumber() instanceof Long)) {
            if (term1.getNumber().longValue() > term2.getNumber().longValue()) {
                return term1.getNumber();
            }
            else {
                return term2.getNumber();
            }
        }
        if (term1.getNumber().doubleValue() > term2.getNumber().doubleValue()) {
            return term1.getNumber();
        }
        else {
            return term2.getNumber();
        }
    }

    @Override
    public String getLexeme() {
        return "#>";
    }

}
