CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "com.serial.terminal"
    str  : "string"

VAR

    byte    str1[32]

PUB Main

    term.Start (115200)

    ' Create a string with the string() command

    term.Str (string("String!"))
    term.NewLine

    ' Create a string in a DAT block and use the address.

    term.Str (@magicstring)
    term.NewLine

    ' Get the size of a string with strsize()

    term.Dec (strsize(@magicstring))
    term.NewLine

DAT

    magicstring     byte    "another string!",0
