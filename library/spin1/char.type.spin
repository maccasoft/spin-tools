{{
    This object contains functions to test individual character types.
}}
PUB IsAlphaNumeric(c)
{{
    Check if character is alphanumeric.
}}

    return lookdown(c: "0".."9", "a".."z", "A".."Z")

PUB IsAlpha(c)
{{
    Check if character is alphabetic.
}}

    return lookdown(c: "a".."z", "A".."Z")

PUB IsDigit(c)
{{
    Check if character is decimal.
}}

    return lookdown(c: "0".."9")

PUB IsLower(c)
{{
    Check if character is lowercase.
}}

    return lookdown(c: "a".."z")

PUB IsUpper(c)
{{
    Check if character is uppercase.
}}

    return lookdown(c: "A".."Z")

PUB IsSpace(c)
{{
    Check if character is whitespace.
}}

    return lookdown(c: " ", 9, 10, 13)

PUB Upper(c)
{{
    Convert character to uppercase.
}}

    if IsLower(c)
        return c - 32
    else
        return c

PUB Lower(c)
{{
    Convert character to lowercase.
}}

    if IsUpper(c)
        return c + 32
    else
        return c
