CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "com.serial.terminal"
    str  : "string"

VAR

    byte    str1[20]
    byte    strtemp[20]

PUB Main

    term.Start (115200)

    term.Str ( str.Left (@str1, @dinosaur, 4))
    term.NewLine

    term.Str ( str.Mid (@str1, @dinosaur, 5, 9))
    term.NewLine

    term.Str ( str.Right (@str1, @dinosaur, 6))
    term.NewLine

    str.Copy (@str1, str.Left (@strtemp, @dinosaur, 2))
    str.Append (@str1, str.Right (@strtemp, @dinosaur, 6))
    term.Str (@str1)
    term.NewLine

DAT

dinosaur    byte    "dass pineapplesaurus",0
