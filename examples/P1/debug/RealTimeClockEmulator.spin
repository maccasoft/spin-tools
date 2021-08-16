' Author: Beau Schwabe
CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    clock   : "debug.emulator.rtc"
    term    : "com.serial.terminal"

VAR

    long  TimeString
    byte  SS,MM,HH,AP,DD,MO,YY,LY
    byte  DateStamp[11], TimeStamp[11]

PUB Main

    term.Start(115200)                              ' Initialize termial communication to the PC

    clock.Start(@TimeString)                        ' Initiate Prop Clock

    clock.Suspend                                   ' Suspend Clock while being set

    clock.SetYear(09)                               ' 00 - 31 ... Valid from 2000 to 2031
    clock.SetMonth(03)                              ' 01 - 12 ... Month
    clock.SetDate(11)                               ' 01 - 31 ... Date

    clock.SetHour(12)                               ' 01 - 12 ... Hour
    clock.SetMin(00)                                ' 00 - 59 ... Minute
    clock.SetSec(00)                                ' 00 - 59 ... Second

    clock.SetAMPM(1)                                ' 0 = AM ; 1 = PM

    clock.Restart                                   ' Start Clock after being set

    repeat
        clock.ParseDateStamp(@DateStamp)
        clock.ParseTimeStamp(@TimeStamp)

        term.Char(1)
        term.Str(@DateStamp)
        term.Str(string("  "))
        term.Str(@TimeStamp)

