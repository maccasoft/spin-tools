CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    term  : "com.serial.terminal"
    ss    : "string.type"

PUB Main

    term.Start (115200)

    TestString(string("BACON"))
    TestString(string("bacon"))
    TestString(string("34545"))
    TestString(string("345aaaa"))
    TestString(string("       "))

PUB TestString(stringptr)

    term.Str    (string("        String: "))
    term.Str    (stringptr)
    term.Str    (string(term#NL))

    term.Str    (string("----------------------", term#NL))
    PrintOutcome(string("  Alphanumeric"),ss.IsAlphaNumeric   (stringptr))
    PrintOutcome(string("         Alpha"),ss.IsAlpha          (stringptr))
    PrintOutcome(string("         Digit"),ss.IsDigit          (stringptr))
    PrintOutcome(string("         Lower"),ss.IsLower          (stringptr))
    PrintOutcome(string("         Upper"),ss.IsUpper          (stringptr))
    PrintOutcome(string("         Space"),ss.IsSpace          (stringptr))
    term.Str    (string("----------------------"))

    term.Str    (string(term#NL, term#NL))

PUB PrintOutcome(stringptr, outcome)

    term.Str (stringptr)
    term.Str (string(": "))

    if outcome
        term.Str (string("true"))
    else
        term.Str (string("false"))

    term.NewLine

