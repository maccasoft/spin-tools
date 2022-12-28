'' =================================================================================================
''
''   File....... jm_i2c_devices.spin
''   Purpose.... I2C bus scanner; shows connected devices in matrix format
''   Author..... Jon "JonnyMac" McPhalen
''               Copyright (c) 2018-2022 Jon McPhalen
''               -- see below for terms of use
''   E-mail..... jon.mcphalen@gmail.com
''   Started....
''   Updated.... 30 DEC 2020
''
'' =================================================================================================


con { timing }

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000                                          ' use 5MHz crystal

  CLK_FREQ = (_clkmode >> 6) * _xinfreq                         ' system freq as a constant
  MS_001   = CLK_FREQ / 1_000                                   ' ticks in 1ms
  US_001   = CLK_FREQ / 1_000_000                               ' ticks in 1us


con { terminal }

  BR_TERM  = 115_200                                            ' terminal baud rate

  #0, T_PST, T_ANSI                                             ' terminal types

  T_TYPE = T_PST


con { fixed io pins }

  PGM_RX   = 31  { I }                                          ' serial / programming
  PGM_TX   = 30  { O }

  EE_SDA   = 29  { I/O }                                        ' i2c / eeprom
  EE_SCL   = 28  { I/O }


con { app io pins }

  SDAX     = 29  { I/O }                                        ' test I2C bus
  SCLX     = 28  { I/O } 


con

  #true,  ON, OFF                                               ' digital control
  #false, NO, YES

  TRIES = 1                                                     ' should not be needed!


obj

' main                                                          ' * master Spin cog
  time : "jm_time_80"                                           '   timing and delays (80MHz)
  io   : "jm_io_basic"                                          '   essential io
  i2c  : "jm_i2c"                                               '   i2c IO
  term : "jm_fullduplexserial"                                  ' * serial IO for terminal
  ansi : "jm_ansi"                                              '   ANSI terminal control sequences

' * uses cog when loaded


dat

  Banner1       byte    "P1 I2C Devices", 0
  Banner2       byte    "-- dddd_aaa_x (8-bit) format", 0

  Header        byte    "    00 02 04 06 08 0A 0C 0E", 13
                byte    "    -- -- -- -- -- -- -- --", 13
                byte    0


var

  long  found[32]                                               ' list of found devices


pub main | count, type, addr, devid, ok, x

  setup

  wait_for_terminal(true)

  if (T_TYPE == T_PST)
    term.fstr3(string("%s\r%s\r\r%s"), @Banner1, @Banner2, @Header)
  else
    term.fstr3(string("%s%s%s\r"), ansi.fgnd(ansi#BR_CYAN), @Banner1, ansi.normal)
    term.fstr2(string("%s\r\r%s"), @Banner2, @Header)

  count := 0

  repeat type from %0001 to %1110                               ' %1111 not valid
    term.fxhex(type << 4, 2)
    term.txn(" ", 2)
    repeat addr from %000 to %111
      devid := (type << 4) | (addr << 1)                        ' build device id
      if (i2c.present(devid))                                   ' ping device
        if (T_TYPE == T_ANSI)
          term.str(ansi.bold)
          term.str(ansi.fgnd(ansi#BR_GREEN))
        term.fxhex(devid, 2)                                    ' show 8-bit id as hex
        if (T_TYPE == T_ANSI)
          term.str(ansi.normal)
        term.tx(" ")
        found[count++] := devid                                 ' add to found list
      else
        term.str(string(".. "))
      time.pause(1)
    term.tx(13)

  term.fstr1(string("\rDevices: %d\r"), count)

  ' show found devices details

  if (count > 0)
    repeat x from 0 to count-1
      devid := found[x]
      type := (devid >> 4) & %1111
      addr := (devid >> 1) & %111
      term.fstr3(string("-- $%.2x \%%.4b_%.3b_0  "), devid, type, addr)
      term.fstr1(string("(7-bit: 0x%.2x)\r"), devid >> 1)

  repeat
    waitcnt(0)


pub setup

'' Setup IO and objects for application

  time.start                                                    ' setup timing & delays

  io.start(0, 0)                                                ' clear all pins (master cog)

  i2c.setupx(SCLX, SDAX)                                        ' connect to I2C bus

  term.tstart(BR_TERM)                                          ' start serial for terminal *


pub wait_for_terminal(clear)

'' Wait for terminal to be open and key pressed

  term.rxflush
  term.rx
  if (clear)
    clear_screen


pub clear_screen

  if (T_TYPE == T_PST)
    term.tx(term#CLS)
  else
    term.str(ansi.hide_cursor)
    term.str(ansi.home)
    term.str(ansi.cls)


con { license }

{{

  Terms of Use: MIT License

  Permission is hereby granted, free of charge, to any person obtaining a copy of this
  software and associated documentation files (the "Software"), to deal in the Software
  without restriction, including without limitation the rights to use, copy, modify,
  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
  permit persons to whom the Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be included in all copies
  or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
  PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
  CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

}}