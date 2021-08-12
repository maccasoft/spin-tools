CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "com.serial.terminal"
    str  : "string"

PUB Main | i

    term.Start (115200)

    term.Str (str.Lower(string("BACON!!!", term#NL)))
    term.Str (str.Upper(string("bacon...", term#NL)))
