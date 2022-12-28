'' =================================================================================================
''
''   File....... jm_165_ez_demo.spin
''   Purpose....
''   Author..... Jon McPhalen
''               Copyright (c) 2022 Jon McPhalen
''               -- see below for terms of use
''   E-mail..... jon.mcphalen@gmail.com
''   Started....
''   Updated.... 21 NOV 2022
''
''   {$P1}
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

  RX1      = 31  { I }                                          ' serial / programming
  TX1      = 30  { O }

  SDA1     = 29  { I/O }                                        ' i2c / eeprom
  SCL1     = 28  { I/O }


con { app io pins }

  DAT_165  =  2  { I }                                          ' control lines for 74x165
  CLK_165  =  1  { O }
  LD_165   =  0  { O }


con

  #true,  ON, OFF
  #false, NO, YES

  SREGS = 2                                                     ' 1 to 4


obj

' main                                                          ' * master Spin cog
  time : "jm_time_80"                                           '   timing and delays (80MHz)
  prng : "jm_prng"                                              '   pseudo-random number generation
  io   : "jm_io"                                                '   simple io
  ins  : "jm_165_ez"                                            '   74x164 inputs
  term : "jm_fullduplexserial"                                  ' * serial IO for terminal
  ansi : "jm_ansi"                                              '   ANSI terminal control sequences

' * uses cog when loaded


dat


var


pub main | srbits, n

  setup

  wait_for_terminal(true)
  time.pause(100)

  term.str(string("74x165 Inputs Demo", 13, 13))

  repeat
    srbits := ins.read(ins#MSBFIRST, SREGS)

    term.str(string(term#HOME, 13, 13))
    repeat n from SREGS-1 to 0
      term.fxbin(srbits.byte[n], 8)
      term.tx(" ")

    time.pause(100)


pub setup

'' Setup IO and objects for application

  time.start                                                    ' setup timing & delays

  prng.seed(-cnt, -cnt ~> 2, $CAFE_BABE, cnt <- 2, cnt)         ' seed randomizer

  io.start(0, 0)                                                ' clear all pins (master cog)

  ins.start(DAT_165, CLK_165, LD_165)                           ' connect to '165(s)

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
