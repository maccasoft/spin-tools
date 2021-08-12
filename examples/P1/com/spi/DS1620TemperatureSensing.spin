' Author: Beau Schwabe
{{
    Once the `spi.Start` command is called from Spin, it will remain running in its own COG.
    If the Receive or Send command are called with 'Bits' set to Zero, then the COG will shut
    down.  Another way to shut the COG down is to call the 'spi.stop' command from Spin.

    --------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------
         The DS1620 temperature sensor is used to demonStrate the spi's Receive and Send functions.

         most of the code folLows the "Stamp Works" documentation that can be found here...

         http://www.parallax.com/Portals/0/Downloads/docs/books/sw/Web-SW-v2.1.pdf

    --------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------

    Schematic:
                        Vdd
                         
                330 ┌────┴────┐
         P0 ────┤1   8   7├──NC
                    │         │
         P1 ───────┤2       6├──NC
                    │         │
         P2 ───────┤3   4   5├──NC
                    └────┬────┘
                         
                        Vss
}}

CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    spi  : "com.spi"
    term : "com.serial.terminal"
    num  : "string.integer"

CON

    WRITE_CONFIG   = $0C           '' write config register
    START_CONVERSION  = $EE           '' start conversion
    READ_TEMP   = $AA           '' read temperature

    DQ    = 0                  '' Set DS1620 Data Pin
    CLK   = 1                  '' Set DS1620 Clock Pin
    reset = 2                  '' Set DS1620 reset Pin

PUB Main | ClockDelay, ClockState, celsius, fahrenheit

    term.StartRxTx(31, 30, 0, 9600) '' Initialize serial communication to the PC through the USB connector
                                    '' To view termial data on the PC use the Parallax termial Terminal (PST) program.

    spi.Start(DQ, CLK, spi#LSBPRE, spi#LSBFIRST, 15, 1)

    High(reset)                                         '' alert the DS1620
    spi.Send(8, WRITE_CONFIG)
    spi.Send(8, %10)                                    '' configure for ; CPU / Free-run mode
    Low(reset)                                          '' release the DS1620

    waitcnt(cnt+clkfreq*10/1000)                        '' Pause for 10ms

    High(reset)                                         '' alert the DS1620
    spi.Send(8, START_CONVERSION)
    Low(reset)                                          '' release the DS1620

    repeat
        High(reset)                                     '' alert the DS1620
        spi.Send(8, READ_TEMP)
        celsius := spi.Receive(9)                       '' read the temperature
        Low(reset)                                      '' release the DS1620

        celsius := celsius << 23 ~> 23                  '' extend sign bit
        celsius *= 5                                    '' convert to tenths

        fahrenheit := celsius * 9 / 5 + 320             '' convert celsius reading to fahrenheit

        term.Str(string("DS1620 thermometer"))
        term.Char(9)
        term.Char(9)
        term.Str(num.Dec(celsius/10))
        term.Char(".")
        term.Str(num.Dec(celsius - celsius/10*10))
        term.Str(string("°C"))
        term.Char(9)
        term.Char(9)
        term.Str(num.Dec(fahrenheit/10))
        term.Char(".")
        term.Str(num.Dec(fahrenheit - fahrenheit/10*10))
        term.Str(string("°F"))
        term.Char(13)

PUB High(Pin)

    dira[Pin]~~
    outa[Pin]~~

PUB Low(Pin)

    dira[Pin]~~
    outa[Pin]~

