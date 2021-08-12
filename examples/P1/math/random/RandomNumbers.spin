{{
    This program demonstrates how the `math.random` object
    can be used to generate unique random numbers on each power-up.
}}
CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term    : "com.serial.terminal"
    random  : "math.random"
    time    : "time"

PUB start | i

  random.Start
  term.Start(115200)

  repeat
      term.Hex(random.Random, 8)
      term.NewLine
      time.MSleep (50)
