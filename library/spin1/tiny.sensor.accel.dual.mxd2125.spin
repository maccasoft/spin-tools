''*************************************************
''*  Memsic Dual Accelerometer Simple Driver v1.0 *
''*  Author: Paul Baker                           *
''*  Copyright (c) 2007 Parallax, Inc.            *
''*  See end of file for terms of use.            *
''*************************************************
{
This object implements two different versions of the MXD2125 driver, the first runs the driver in a seperate cog.
The second runs the driver in the cog which calls the method. Only one version of the driver should be used at a time,
and methods of the other driver version should never be called.
}

VAR
long cog, XVal, YVal                  'variable which contains the cog identifier and Raw sensor data
long ctramode, ctrbmode, pinwaitm
long stack[50]

{**************************************************************
* The following methods are for the dedicated cog version of
* the driver. The driver is started by calling the method start
* with the pins connected to the X axis and Y axis as arguments.
* Readings are taken by calling methods x and y.
***************************************************************}
PUB stop
    '' Stop driver - frees a cog
    if cog
       cogstop(cog)

PUB start(Xin, Yin): okay
  ctramode := %1000 << 26 + Xin     'construct value for counter A mode (POS accumulator)
  ctrbmode := %1000 << 26 + Yin     'construct value for counter B mode (POS accumulator)
  pinwaitm := |< Xin + |< Yin       'construct pin mask for waiting

  '' Start driver - starts a cog
  '' returns false if no cog available

  okay := cog := cognew(accel_driver, @stack)

PUB x
  if cog
    return XVal                                   'return current X axis pulse width

PUB y
  if cog
    return YVal                                   'return current Y axis pulse width

PRI accel_driver
  frqa := 1                                       'setup counters to increment by one
  frqb := 1
  ctra := ctramode                                'start counters
  ctrb := ctrbmode
  repeat
    waitpeq(0,pinwaitm,0)                         'wait until both channels are off
    XVal := phsa                                  'record counter values
    YVal := phsb
    phsa := 0                                     'reset count
    phsb := 0
    waitpeq(pinwaitm,pinwaitm,0)                  'wait until both channels turn on

{**************************************************************
* The following methods are for the execute on current cog
* version of the driver. The driver is started by calling the init
* method with the pins connected to the X axis and Y axis as arguments.
* Reading is taken by passing pointers to variables in Get_XY
***************************************************************}
PUB init(Xin, Yin)
  cog := false                                    'set cog variable to failure in case stop is accidentally called
  frqa := 1                                       'setup counters to increment by one
  frqb := 1
  ctramode := %1000 << 26 + Xin                   'construct value for counter A mode (POS accumulator)
  ctrbmode := %1000 << 26 + Yin                   'construct value for counter B mode (POS accumulator)

PUB Get_XY(pX,pY)
  ctra := ctramode                                'start up both counters
  ctrb := ctrbmode
  waitpeq(0,pinwaitm,0)                           'wait for both channels to turn off
  phsa := 0                                       'reset counter values
  phsb := 0
  waitpeq(pinwaitm,pinwaitm,0)                    'wait for beginning of measurement
  waitpeq(0,pinwaitm,0)                           'wait for end of measurement
  LONG[pX] := phsa                                'record count values into pointers
  LONG[pY] := phsb
  ctra := 0                                       'turn off counters
  ctrb := 0

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