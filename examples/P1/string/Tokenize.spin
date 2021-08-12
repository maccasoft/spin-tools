CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "com.serial.terminal"
    str  : "string"

VAR

    word    tokenptr

PUB Main

    term.Start (115200)

    tokenptr := str.Tokenize (@magicstring)

    repeat while tokenptr
        term.Str (tokenptr)
        term.NewLine
        tokenptr := str.Tokenize (0)

DAT

magicstring     byte    "this string needs to be tokenized!",0
