package com.maccasoft.propeller.expressions;

public class EvaluationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EvaluationException() {
        this(null);
    }

    public EvaluationException(String message) {
        super(message);
    }

}
