{{
    Calculate a table of frequency from their corresponding notes.

        f(x) = f0 * (a)^n where f0 = 440, n = note, a = (2)^(1/12)
}}
CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term  : "com.serial.terminal"
    fp    : "math.float"
    fs    : "string.float"
    num   : "string.integer"

PUB Main | idx, f0, fn, a, n

    term.Start(115200)
    fp.Start

    f0 := fp.FloatF (440)
    a  := fp.Pow (fp.FloatF(2), fp.DivF (fp.FloatF(1), fp.FloatF (12)))

    term.Str(string("f(x) = f0*(2^(1/12))^n, x = (0, 60)", term#NL, term#NL))
    term.Str(string("     x   f(x)", term#NL, term#NL))

    repeat idx from 0 to 60

        n := fp.FloatF (idx)
        fn := fp.MulF (f0, fp.Pow (a, n))

        term.Chars(" ",3)
        term.Str(num.DecPadded(idx, 3))
        term.Chars(" ",3)
        term.Str(fs.FloatToString(fn))
        term.Char(term#NL)
