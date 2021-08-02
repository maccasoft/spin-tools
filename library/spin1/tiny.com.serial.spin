''*******************************************************************
''*  Simple Asynchronous Serial Driver v1.3                         *
''*  Authors: Chip Gracey, Phil Pilgrim, Jon Williams, Jeff Martin  *
''*  Copyright (c) 2006 Parallax, Inc.                              *
''*  See end of file for terms of use.                              *
''*******************************************************************
''
'' Performs asynchronous serial input/output at low baud rates (~19.2K or lower) using high-level code
'' in a blocking fashion (ie: single-cog (serial-process) rather than multi-cog (parallel-process)).
''
'' To perform asynchronous serial communication as a parallel process, use the FullDuplexSerial object instead.
''
''
'' v1.3 - May 7, 2009    - Updated by Jeff Martin to fix rx method bug, noted by Mike Green and others, where uninitialized
''                         variable would mangle received byte.
'' v1.2 - March 26, 2008 - Updated by Jeff Martin to conform to Propeller object initialization standards and compress by 11 longs.
'' v1.1 - April 29, 2006 - Updated by Jon Williams for consistency.
''
''
'' The init method MUST be called before the first use of this object.
'' Optionally call finalize after final use to release transmit pin.
''
'' Tested to 19.2 kbaud with clkfreq of 80 MHz (5 MHz crystal, 16x PLL)


VAR

  long  sin, sout, inverted, bitTime, rxOkay, txOkay


PUB init(rxPin, txPin, baud): Okay
{{Call this method before first use of object to initialize pins and baud rate.

  • For true mode (start bit = 0), use positive baud value.     Ex: serial.init(0, 1, 9600)
    For inverted mode (start bit = 1), use negative baud value. Ex: serial.init(0, 1, -9600)
  • Specify -1 for "unused" rxPin or txPin if only one-way communication desired.
  • Specify same value for rxPin and txPin for bi-directional communication on that pin and connect a pull-up/pull-down resistor
    to that pin (depending on true/inverted mode) since pin will set it to hi-z (input) at the end of transmission to avoid
    electrical conflicts.  See "Same-Pin (Bi-Directional)" examples, below.

  EXAMPLES:

    Standard Two-Pin Bi-Directional True/Inverted Modes                Standard One-Pin Uni-Directional True/Inverted Mode
                Ex: serial.init(0, 1, ±9600)                      Ex: serial.init(0, -1, ±9600)  -or-  serial.init(-1, 0, ±9600)
         ┌────────────┐               ┌──────────┐                          ┌────────────┐               ┌──────────┐
         │Propeller P0├─────────────┤I/O Device│                          │Propeller P0├───────────────┤I/O Device│
         │          P1├─────────────┤          │                          └────────────┘               └──────────┘
         └────────────┘               └──────────┘



            Same-Pin (Bi-Directional) True Mode                              Same-Pin (Bi-Directional) Inverted Mode
                Ex: serial.init(0, 0, 9600)                                       Ex: serial.init(0, 0, -9600)
                                                                           ┌────────────┐               ┌──────────┐
                              │                                             │Propeller P0├─────┳─────┤I/O Device│
                               4.7 kΩ                                      └────────────┘       │       └──────────┘
         ┌────────────┐       │       ┌──────────┐                                                4.7 kΩ
         │Propeller P0├─────┻─────┤I/O Device│                                               │
         └────────────┘               └──────────┘                                               
}}

  finalize                                              ' clean-up if restart

  rxOkay := rxPin > -1                                  ' receiving?
  txOkay := txPin > -1                                  ' transmitting?

  sin := rxPin & $1F                                    ' set rx pin
  sout := txPin & $1F                                   ' set tx pin

  inverted := baud < 0                                  ' set inverted flag
  bitTime := clkfreq / ||baud                           ' calculate serial bit time

  return rxOkay | txOkay


PUB finalize
{{Call this method after final use of object to release transmit pin.}}

  if txOkay                                             ' if tx enabled
    dira[sout]~                                         '   float tx pin
  rxOkay := txOkay := false


PUB rx: rxByte | t
{{ Receive a byte; blocks caller until byte received. }}

  if rxOkay
    dira[sin]~                                          ' make rx pin an input
    waitpeq(inverted & |< sin, |< sin, 0)               ' wait for start bit
    t := cnt + bitTime >> 1                             ' sync + 1/2 bit
    repeat 8
      waitcnt(t += bitTime)                             ' wait for middle of bit
      rxByte := ina[sin] << 7 | rxByte >> 1             ' sample bit
    waitcnt(t + bitTime)                                ' allow for stop bit

    rxByte := (rxByte ^ inverted) & $FF                 ' adjust for mode and strip off high bits


PUB tx(txByte) | t
{{ Transmit a byte; blocks caller until byte transmitted. }}

  if txOkay
    outa[sout] := !inverted                             ' set idle state
    dira[sout]~~                                        ' make tx pin an output
    txByte := ((txByte | $100) << 2) ^ inverted         ' add stop bit, set mode
    t := cnt                                            ' sync
    repeat 10                                           ' start + eight data bits + stop
      waitcnt(t += bitTime)                             ' wait bit time
      outa[sout] := (txByte >>= 1) & 1                  ' output bit (true mode)

    if sout == sin
      dira[sout]~                                       ' release to pull-up/pull-down


PUB str(strAddr)
{{ Transmit z-string at strAddr; blocks caller until string transmitted. }}

  if txOkay
    repeat strsize(strAddr)                             ' for each character in string
      tx(byte[strAddr++])                               '   write the character

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
