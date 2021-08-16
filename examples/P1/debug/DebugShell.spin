CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    shell : "debug.shell"

PUB Main

    shell.Start
