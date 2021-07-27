{{
    This object contains functions to test string types.
}}
OBJ

    cc : "char.type"

PUB IsAlphaNumeric(str)
{{
    Check if string is alphanumeric.
}}

    repeat strsize(str)
        if not cc.IsAlphaNumeric (byte[str++])
            return false

    return true

PUB IsAlpha(str)
{{
    Check if character is alphabetic.
}}

    repeat strsize(str)
        if not cc.IsAlpha (byte[str++])
            return false

    return true

PUB IsDigit(str)
{{
    Check if character is decimal.
}}

    repeat strsize(str)
        if not cc.IsDigit (byte[str++])
            return false

    return true

PUB IsLower(str)
{{
    Check if character is lowercase.
}}

    repeat strsize(str)
        if not cc.IsLower (byte[str++])
            return false

    return true

PUB IsUpper(str)
{{
    Check if character is uppercase.
}}

    repeat strsize(str)
        if not cc.IsUpper (byte[str++])
            return false

    return true

PUB IsSpace(str)
{{
    Check if character is whitespace.
}}

    repeat strsize(str)
        if not cc.IsSpace (byte[str++])
            return false

    return true
