' Authors: Jon Williams, Jeff Martin
CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

    LCD_PIN   = 0
    LCD_BAUD  = 19_200
    LCD_LINES = 4

OBJ

    lcd : "display.lcd.serial"
    num : "string.integer"

PUB Main | idx

    if lcd.Start(LCD_PIN, LCD_BAUD, LCD_LINES)              ' start lcd

        lcd.SetCursor(0)                                    ' cursor off
        lcd.EnableBacklight(true)                           ' backlight on (if available)
        lcd.Custom(0, @bullet)                              ' create custom character 0
        lcd.Clear
        lcd.Str(string("LCD DEBUG", 13))
        lcd.Char(0)                                         ' display custom bullet character
        lcd.Str(string(" Dec", 13))
        lcd.Char(0)
        lcd.Str(string(" Hex", 13))
        lcd.Char(0)
        lcd.Str(string(" Bin"))

        repeat
            repeat idx from 0 to 255
                UpdateLCD(idx)
                waitcnt(clkfreq / 5 + cnt)                  ' pad with 1/5 sec

            repeat idx from -255 to 0
                UpdateLCD(idx)
                waitcnt(clkfreq / 5 + cnt)

PRI UpdateLCD(value)

    lcd.Position(12, 1)
    lcd.Str(num.DecPadded(value, 8))
    lcd.Position(11, 2)
    lcd.Str(num.HexIndicated(value, 8))
    lcd.Position(7, 3)
    lcd.Str(num.BinIndicated(value, 12))

DAT

bullet
byte    $00, $04, $0E, $1F, $0E, $04, $00, $00

