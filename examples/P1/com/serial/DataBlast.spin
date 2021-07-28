CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ
    ser : "com.serial"

PUB Main | ran

    ser.Start(115_200)

    ran := cnt

    repeat
        ser.Char(32 + (ran? & $3F))
        repeat 10000