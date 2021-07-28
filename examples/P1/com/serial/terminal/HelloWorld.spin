CON

    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term  : "com.serial.terminal"

PUB Main

    term.Start (115200)
    term.Str (string("Hello World!"))
