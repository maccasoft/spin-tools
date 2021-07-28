CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ
    ser : "com.serial"

PUB Main

    ser.Start(115200)

    ser.Char("H")
    ser.Char("e")
    ser.Char("l")
    ser.Char("l")
    ser.Char("o")
    ser.Char(" ")
    ser.Char("w")
    ser.Char("o")
    ser.Char("r")
    ser.Char("l")
    ser.Char("d")
    ser.Char("!")
    ser.Char(10)
