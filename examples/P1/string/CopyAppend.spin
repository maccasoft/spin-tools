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

    str.Copy  (@str1, string("BACON"))
    term.Str  (@str1)
    term.NewLine

    str.Append(@str1, string(" AND"))
    term.Str  (@str1)
    term.NewLine

    str.Append(@str1, string(" CHICKEN"))
    term.Str  (@str1)
    term.NewLine

    str.Append(@str1, string(" IS"))
    term.Str  (@str1)
    term.NewLine

    str.Append(@str1, string(" GOOD"))
    term.Str  (@str1)
