package com.maccasoft.propeller.expressions;

public class LogicalAnd extends BinaryOperator {

    public LogicalAnd(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value1 = term1.getNumber().longValue();
        long value2 = term2.getNumber().longValue();
        return (value1 != 0 && value2 != 0) ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "and";
    }

}
