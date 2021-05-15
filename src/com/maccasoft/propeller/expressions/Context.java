package com.maccasoft.propeller.expressions;

public interface Context {

    public Expression getSymbol(String name);

    public boolean hasSymbol(String name);

    public int getAddress();

}
