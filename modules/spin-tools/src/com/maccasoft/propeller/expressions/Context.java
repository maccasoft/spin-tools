package com.maccasoft.propeller.expressions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.model.Token;

public class Context {

    final Context parent;
    final boolean caseSensitive;

    Map<String, Expression> symbols = new HashMap<>();
    Map<String, Expression> caseInsensitiveSymbols = new CaseInsensitiveMap<>();

    Map<String, List<Token>> defines = new HashMap<>();
    Map<String, List<Token>> caseInsensitiveDefines = new CaseInsensitiveMap<>();

    Integer address;
    Integer objectAddress;
    Integer memoryAddress;

    public Context() {
        this(null, false);
    }

    public Context(boolean caseSensitive) {
        this(null, caseSensitive);
    }

    public Context(Context parent) {
        this(parent, parent.isCaseSensitive());
    }

    Context(Context parent, boolean caseSensitive) {
        this.parent = parent;
        this.caseSensitive = caseSensitive;
        caseInsensitiveSymbols.put("$", new ContextLiteral(this));
        caseInsensitiveSymbols.put("@$", new ObjectContextLiteral(this));
        caseInsensitiveSymbols.put("@@$", new MemoryContextLiteral(this));
    }

    public Context getParent() {
        return parent;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void addAll(Map<String, Expression> map) {
        if (caseSensitive) {
            symbols.putAll(map);
        }
        else {
            caseInsensitiveSymbols.putAll(map);
        }
    }

    public void addBuiltinSymbol(String name, Expression value) {
        if (caseInsensitiveSymbols.containsKey(name)) {
            throw new RuntimeException("symbol " + name + " already defined");
        }
        caseInsensitiveSymbols.put(name, value);
    }

    public void addSymbol(String name, Expression value) {
        if (caseInsensitiveSymbols.containsKey(name)) {
            throw new RuntimeException("symbol " + name + " already defined");
        }
        if (caseSensitive) {
            if (symbols.containsKey(name)) {
                throw new RuntimeException("symbol " + name + " already defined");
            }
            symbols.put(name, value);
        }
        else {
            caseInsensitiveSymbols.put(name, value);
        }
    }

    public void addOrUpdateSymbol(String name, Expression value) {
        if (caseSensitive) {
            symbols.put(name, value);
        }
        else {
            caseInsensitiveSymbols.put(name, value);
        }
    }

    public Expression getSymbol(String name) {
        Expression exp = getLocalSymbol(name);
        if (exp == null) {
            throw new RuntimeException("symbol " + name + " not found!");
        }
        return exp;
    }

    public Expression getLocalSymbol(String name) {
        Expression exp = caseInsensitiveSymbols.get(name);
        if (exp == null && caseSensitive) {
            exp = symbols.get(name);
        }
        if (exp == null && parent != null) {
            exp = parent.getLocalSymbol(name);
        }
        return exp;
    }

    public boolean isDefined(String identifier) {
        boolean result = caseSensitive ? defines.containsKey(identifier) : caseInsensitiveDefines.containsKey(identifier);
        if (result == false) {
            result = caseSensitive ? symbols.containsKey(identifier) : caseInsensitiveSymbols.containsKey(identifier);
        }
        if (result == false && parent != null) {
            return parent.isDefined(identifier);
        }
        return result;
    }

    public void addDefinition(String identifier, List<Token> definition) {
        if (caseSensitive) {
            defines.put(identifier, definition);
        }
        else {
            caseInsensitiveDefines.put(identifier, definition);
        }
    }

    public void addDefinition(String identifier, Expression expression) {
        addDefinition(identifier, Collections.emptyList());
        addSymbol(identifier, expression);
    }

    public List<Token> getDefinition(String identifier) {
        List<Token> result = caseSensitive ? defines.get(identifier) : caseInsensitiveDefines.get(identifier);
        if (result == null && parent != null) {
            result = parent.getDefinition(identifier);
        }
        return result;
    }

    public boolean hasSymbol(String name) {
        boolean result = caseInsensitiveSymbols.containsKey(name);
        if (result == false && caseSensitive) {
            result = symbols.containsKey(name);
        }
        if (result == false && parent != null) {
            result = parent.hasSymbol(name);
        }
        return result;
    }

    public int getInteger(String name) {
        Expression result = getSymbol(name);
        return result.getNumber().intValue();
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public boolean isAddressSet() {
        return address != null;
    }

    public int getAddress() {
        if (address == null) {
            throw new RuntimeException("address not set");
        }
        return address;
    }

    public int getObjectAddress() {
        if (objectAddress == null) {
            throw new RuntimeException("object address not set");
        }
        return objectAddress;
    }

    public void setObjectAddress(int address) {
        this.objectAddress = address;
    }

    public int getMemoryAddress() {
        if (memoryAddress == null) {
            throw new RuntimeException("memory address not set");
        }
        return memoryAddress;
    }

    public void setMemoryAddress(int address) {
        this.memoryAddress = address;
    }

}
