package com.maccasoft.propeller.expressions;

public class Modulo extends BinaryOperator {

    public Modulo(Expression dividend, Expression divisor) {
        super(dividend, divisor);
    }

    public Expression getDividend() {
        return term1;
    }

    public Expression getDivisor() {
        return term2;
    }

    @Override
    public Number getNumber() {
        Number divisor = term2.getNumber();
        if (term1.getNumber() instanceof Integer && divisor instanceof Integer) {
            if (divisor.intValue() == 0) {
                throw new EvaluationException("Division by zero.");
            }
            return term1.getNumber().intValue() % divisor.intValue();
        }
        if (divisor.doubleValue() == 0) {
            throw new EvaluationException("Division by zero.");
        }
        return term1.getNumber().doubleValue() % divisor.doubleValue();
    }

    @Override
    public String getLexeme() {
        return "%";
    }

}
