' Author:  Chris Savage & Jeff Martin
CON

    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

    PING_Pin = 0                                           ' I/O Pin For PING)))
    term_Pin = 1                                           ' I/O Pin For term
    term_Baud = 19_200                                     ' term Baud Rate
    term_Lines = 4                                         ' Parallax 4X20 Serial term (#27979)

VAR

    long  range

OBJ

    term : "display.lcd.serial"
    ping : "sensor.ping"
    num  : "string.integer"

PUB Start

    term.Start(term_Pin, term_Baud, term_Lines)            ' Initialize term Object
    term.SetCursor(0)                                      ' Turn Off Cursor
    term.EnableBacklight(true)                             ' Turn On Backlight
    term.Clear
    term.Str(string("PING))) Demo", 13, 13, "Inches      -", 13, "Centimeters -"))

    repeat
        term.Position(15, 2)
        range := ping.Inches(PING_Pin)
        term.Str(num.Dec(range))
        term.Str(string(".0 "))
        term.Position(14, 3)
        range := ping.Millimeters(PING_Pin)
        term.Str(num.DecPadded(range / 10, 3))
        term.Char(".")
        term.Str(num.DecPadded(range // 10, 1))
        waitcnt(clkfreq / 10 + cnt)

