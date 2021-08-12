CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term  : "com.serial.terminal"
    fp    : "math.float"
    fs    : "string.float"
    num   : "string.integer"

PUB Main | idx, a

    term.Start(115200)
    fp.Start

    term.Str(string("f(x) = log(x), x = (1,20)", term#NL, term#NL))
    term.Str(string("     x   log(x)", term#NL, term#NL))

    repeat idx from 1 to 20

        a := fp.Log(fp.FloatF(idx))

        term.Chars(" ",3)
        term.Str(num.DecPadded(idx, 3))
        term.Chars(" ",3)
        term.Str(fs.FloatToString(a))
        term.Char(term#NL)
