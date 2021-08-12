CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "com.serial.terminal"
    str  : "string"

VAR

    byte    str1[64]

PUB Main

    term.Start (115200)

    str.Copy (@str1, @magicstring)
    str.ReplaceAll (@str1, string("______"), string("donkey"))
    term.Str (@str1)

DAT

    magicstring     byte    "Mary had a little ______, little ______, little ______",0
