{{
    This demo illustrates the use of the terminal ReadLine function for
    getting input from a command line.

    For this demo to work correctly, terminal Echo must be disabled.
}}
CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

    MAX_LINE = 64

OBJ

    term : "com.serial.terminal"

VAR

    byte line[MAX_LINE]

PUB Main

  term.Start (115200)

  repeat
    term.Str (string("> "))
    term.ReadLine (@line, MAX_LINE)
    term.Str (@line)
    term.NewLine
