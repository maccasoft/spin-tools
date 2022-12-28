'' =================================================================================================
''
''   File....... jm_165_ez.spin
''   Purpose.... Basic 74x165 driver
''   Author..... Jon "JonnyMac" McPhalen
''               Copyright (C) 2013-22 Jon McPhalen
''               -- see below for terms of use
''   E-mail..... jon.mcphalen@gmail.com
''   Started....
''   Updated.... 21 NOV 2022
''
'' =================================================================================================

{

   When using cascaded devices, the chip farthest from the Propeller will provide
   byte 0 of the input data:


           9┌──────────┐10  9┌──────────┐10  9┌──────────┐10  9┌──────────┐10
   din ────┤  value.3 ├─────┤  value.2 ├─────┤  value.1 ├─────┤  value.0 ├─ GND
            └─┬┬┬┬┬┬┬┬─┘     └─┬┬┬┬┬┬┬┬─┘     └─┬┬┬┬┬┬┬┬─┘     └─┬┬┬┬┬┬┬┬─┘


           9┌──────────┐10  9┌──────────┐10  9┌──────────┐10
   din ────┤  value.2 ├─────┤  value.1 ├─────┤  value.0 ├─ GND
            └─┬┬┬┬┬┬┬┬─┘     └─┬┬┬┬┬┬┬┬─┘     └─┬┬┬┬┬┬┬┬─┘


           9┌──────────┐10  9┌──────────┐10
   din ────┤  value.1 ├─────┤  value.0 ├─ GND
            └─┬┬┬┬┬┬┬┬─┘     └─┬┬┬┬┬┬┬┬─┘


           9┌──────────┐10
   din ────┤  value.0 ├─ GND
            └─┬┬┬┬┬┬┬┬─┘

   Normal input mode is MSBFIRST.

}


con { fixed io pins }

  RX1      = 31  { I }                                          ' serial / programming
  TX1      = 30  { O }

  SDA1     = 29  { I/O }                                        ' i2c / eeprom
  SCL1     = 28  { I/O }


con

  #0, LSBFIRST, MSBFIRST


var

  long  din                                                     ' data pin  (input)
  long  clk                                                     ' clock pin (output)
  long  load                                                    ' load pin  (output)


pub null

  ' This is not a top-level object


pub start(dpin, cpin, lpin)

'' Assigns and configures 74x165 pins
'' -- dpin connects to 74x165.Qh    (1st device)
'' -- cpin connects to 74x165.CLK   (all devices)
'' -- lpin connects to 74x165.SH/LD (all devices)

  longmove(@din, @dpin, 3)                                      ' copy pins

  dira[din] := 0                                                ' make data an input

  outa[clk] := 0                                                ' make clock an output and low
  dira[clk] := 1

  outa[load] := 1                                               ' make latch an output and high
  dira[load] := 1


pub read(mode, nbytes) : in165 | bits

'' Input 1 to 4 bytes from 74x165 chain
'' -- mode is 0 for LSBFIRST, 1 for MSBFIRST (standard)
'' -- nbytes is number of bytes/shift-registers to read (1 to 4)

  bits  := (1 #> nbytes <# 4) << 3                              ' convert bytes to bits (x8)

  outa[load] := 0                                               ' blip Shift/Load line
  outa[load] := 1

  repeat bits
    in165 := (in165 << 1) | ina[din]                            ' get bit
    outa[clk] := 1                                              ' clock in next
    outa[clk] := 0

  if (mode == LSBFIRST)                                         ' LSBFIRST result?
    in165 ><= bits                                              '  reverse bits


con

{{

  Terms of Use: MIT License

  Permission is hereby granted, free of charge, to any person obtaining a copy of this
  software and associated documentation files (the "Software"), to deal in the Software
  without restriction, includtag without limitation the rights to use, copy, modify,
  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
  permit persons to whom the Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be included in all copies
  or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
  INCLUdtaG BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
  PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
  CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

}}
