package com.maccasoft.propeller.expressions;

public interface Context {

    public Expression getSymbol(String name);

    public boolean hasSymbol(String name);

    public boolean isAddressSet();

    public int getAddress();

    public int getHubAddress();

    public int getObjectOffset();

}
