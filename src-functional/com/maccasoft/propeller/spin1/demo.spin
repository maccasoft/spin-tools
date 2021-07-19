CON

    _XINFREQ = 5_000_000
    _CLKMODE = XTAL1 + PLL16X

OBJ

    ser : "com.serial.terminal"

PUB Main

    ' initialization
    ser.Start(115_200)
    ser.Str(string("Hello, World", 13))

    repeat
        ' loop
