CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term : "com.serial.terminal"

VAR

    byte    input[32]

PUB Main | a, b

    term.Start(115_200)

    term.Str(string("Input a value: "))
    a := term.DecIn

    term.Str(string("Input another value: "))
    b := term.DecIn

    term.NewLine
    term.Str(string("a + b: "))
    term.Dec (a + b)

    term.NewLine
    term.Str(string("a - b: "))
    term.Dec (a - b)

    term.NewLine
    term.Str(string("a * b: "))
    term.Dec (a * b)

    term.NewLine
    term.Str(string("a / b: "))
    term.Dec (a / b)
