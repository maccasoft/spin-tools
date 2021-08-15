''***********************************************
''*  Memsic Dual Accelerometer Simple Demo v1.0 *
''*  Author: Paul Baker                         *
''*  Copyright (c) 2007 Parallax, Inc.          *
''*  See end of file for terms of use.          *
''***********************************************
CON
  _CLKMODE = XTAL1 + PLL16X
  _XINFREQ = 5_000_000

  'Constants used by the Accelerometer Object
  Xout_pin    =  0              'Propeller pin MX2125 X out is connected to
  Yout_pin    =  1              'Propeller pin MX2125 Y out is connected to

VAR
  long XVal, YVal
OBJ
    text  : "display.tv.text"
    accel : "tiny.sensor.accel.dual.mxd2125"

PUB Setup
  text.start(12)

  'seperate cog example
  text.dec(accel.start(Xout_pin, Yout_pin))             'load a cog with accelerometer driver
  text.str(string("Seperate cog example",$0D))
  repeat 20
    text.dec(accel.x)                                   'retrieve X axis value
    text.out(" ")
    text.dec(accel.y)                                   'retrieve Y axis value
    text.out($0D)
    waitcnt(clkfreq>>1 + cnt)
  accel.stop                                            'stop the accelerometer driver cog

  'now show in same cog example
  accel.init(Xout_pin, Yout_pin)
  text.str(string("Same cog example",$0D))
  repeat 20
    accel.Get_XY(@XVal, @YVal)                           'get X and Y values by passing pointers to variables
    text.dec(XVal)
    text.out(" ")
    text.dec(YVal)
    text.out($0D)
    waitcnt(clkfreq>>1 + cnt)
  text.str(string("Demo complete"))

{{

┌──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                                   TERMS OF USE: MIT License                                                  │
├──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation    │
│files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,    │
│modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software│
│is furnished to do so, subject to the following conditions:                                                                   │
│                                                                                                                              │
│The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.│
│                                                                                                                              │
│THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE          │
│WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR         │
│COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,   │
│ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                         │
└──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
}}
