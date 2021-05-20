package com.maccasoft.propeller.expressions;

public class Group extends Passthrough {

    private final Expression term;

    public Group(Expression term) {
        this.term = term;
    }

    public Expression getTerm() {
        return term;
    }

    @Override
    public Expression resolve() {
        return term;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public String toString() {
        return "(" + term + ")";
    }

}
