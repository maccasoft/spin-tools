CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000

  MMx = 0
  MMy = 1

OBJ

    term    : "com.serial.terminal"
    num     : "string.integer"
    MM2125  : "sensor.accel.dual.memsic2125"

PUB Main | a, b, c, d, e, f, clk_scale

    term.Start(115200)                                  ' Initialize serial communication to the PC
    MM2125.start(MMx, MMy)                              ' Initialize Mx2125
    waitcnt(clkfreq/10 + cnt)                           ' wait for things to settle
    MM2125.setlevel                                     ' assume at startup that the memsic2125 is level
                                                        ' Note: This line is important for determining a deg

    clk_scale := clkfreq / 500_000                      ' set clk_scale based on system clock


    repeat
      a := MM2125.Mx                                    ' get RAW x value
      b := MM2125.My                                    ' get RAW y value

      c := MM2125.ro                                    ' Get raw value for acceleration
      c := c / clk_scale                                ' convert raw acceleration value to mg's

      d := MM2125.theta                                 ' Get raw 32-bit deg
      d := d >> 24                                      ' scale 32-bit value to an 8-bit Binary Radian
      d := (d * 45)/32                                  ' Convert Binary radians into Degrees

      e := MM2125.MxTilt

      f := MM2125.MyTilt

      term.Str(num.Dec(a))                              ' Display RAW x value
      term.Char(9)
      term.Str(num.Dec(b))                              ' Display RAW y value
      term.Char(9)
      term.Char(9)
      term.Str(num.Dec(c))                              ' Display Acceleration value in mg's
      term.Char(9)
      term.Str(num.Dec(d))                              ' Display Deg
      term.Char(9)
      term.Str(num.Dec(e))                              ' Display X Tilt
      term.Char(9)
      term.Str(num.Dec(f))                              ' Display X Tilt

      term.Char(13)

{{
Note: At rest, normal RAW x and y values should be at about 400_000 if the Propeller is running at 80MHz.

Since the frequency of the MM2125 is about 100Hz this means that the Period is 10ms... At rest this is a
50% duty cycle, the signal that we are measuring is only HIGH for 5ms.  At 80MHz (12.5ns) this equates to
a value of 400_000 representing a 5ms pulse width.
}}
