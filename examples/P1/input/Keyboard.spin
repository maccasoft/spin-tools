' Original Author: Chip Gracey
CON

    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "display.tv.terminal"
    kb   : "input.keyboard"

PUB start

  term.start(12)
  term.str(string("Keyboard Demo...",13))

  kb.start(26, 27)

  repeat
    term.hex(kb.getkey,3)
    term.out(" ")

