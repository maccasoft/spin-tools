package com.maccasoft.propeller.expressions;

public class Compare extends BinaryOperator {

    public Compare(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        if (term1.getNumber() instanceof Long && term2.getNumber() instanceof Long) {
            if (term1.getNumber().longValue() < term2.getNumber().longValue()) {
                return -1;
            }
            if (term1.getNumber().longValue() > term2.getNumber().longValue()) {
                return 1;
            }
        }
        else {
            if (term1.getNumber().doubleValue() < term2.getNumber().doubleValue()) {
                return -1;
            }
            if (term1.getNumber().doubleValue() > term2.getNumber().doubleValue()) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String getLexeme() {
        return "<=>";
    }

}
