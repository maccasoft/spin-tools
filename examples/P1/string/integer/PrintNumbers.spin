CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "com.serial.terminal"
    num  : "string.integer"

PUB Main

    term.Start(115_200)

    term.Str(num.Dec (34236))
    term.NewLine

    term.Str ( num.DecPadded (34236, 10) )
    term.NewLine

    term.Str ( num.DecZeroed (34236, 10) )
    term.NewLine

    term.Str ( num.Hex (34236, 8) )
    term.NewLine

    term.Str ( num.HexIndicated (34236, 8) )
    term.NewLine

    term.Str ( num.Bin (34256, 32))
    term.NewLine

    term.Str ( num.BinIndicated (34256, 32))
    term.NewLine

    term.Str ( num.StrToBase (string("34256"),10))
    term.NewLine
