''***********************************************
''*  TSL230 Light to Frequency Simple Demo 1.0  *
''*  Author: Paul Baker                         *
''*  Copyright (c) 2007 Parallax, Inc.          *
''*  See end of file for terms of use.          *
''***********************************************
{{
                    ┌──────────┐
          cbase ──│1 o      8│──┳──┐
                    │          │   │  
        cbase+1 ──│2        7│──┘
                    │    []    │
                ┌──│3        6│──── pin
                │   │          │   
                ┣──│4        5│──┘ 3.3V
                   └──────────┘

}}
CON
  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000

  pin = 0                                  'pin connected to tsl230 output
  cbase = 1                                'pin connected to S0 (S1 connected to cbase + 1)
  scale = %10                              'scale value for tsl230 (%00=off,%01=x1,%10=x10,%11=x100)
  ctrmode = $28000000                      'mode value for counter to operate as a frequency counter

OBJ
  term : "display.tv.text"

PUB go | old
  dira := %11 << cbase                     'set scale pins to output
  outa := scale << cbase                   'set scale value
  term.start(12)                           'start terminal
  frqa := 1                                'set counter to increment by one
  ctra := ctrmode + pin                    'start counter
  repeat
    waitcnt(80_000_000 / 10 + cnt)         'wait for 100ms
    term.dec(phsa)                         'output counter value
    term.out($0D)                          'line feed
    phsa := 0                              'reset counter value

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
