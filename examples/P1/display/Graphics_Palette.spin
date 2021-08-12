''***************************************
''*  Graphics Palette                   *
''*  Author: Chip Gracey                *
''*  Copyright (c) 2006 Parallax, Inc.  *
''*  See end of file for terms of use.  *
''***************************************

CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000
  _free = ($3000 + $3000) >> 2          'accomodate bitmap buffers
  _stack = $100                         'insure sufficient stack

  x_tiles = 16
  y_tiles = 12

  paramcount = 14
  bitmap_base = $2000
  display_base = $5000

VAR

  long  mousex, mousey

  long  tv_status     '0/1/2 = off/visible/invisible           read-only
  long  tv_enable     '0/? = off/on                            write-only
  long  tv_pins       '%ppmmm = pins                           write-only
  long  tv_mode       '%ccinp = chroma,interlace,ntsc/pal,swap write-only
  long  tv_screen     'pointer to screen (words)               write-only
  long  tv_colors     'pointer to colors (longs)               write-only
  long  tv_hc         'horizontal cells                        write-only
  long  tv_vc         'vertical cells                          write-only
  long  tv_hx         'horizontal cell expansion               write-only
  long  tv_vx         'vertical cell expansion                 write-only
  long  tv_ho         'horizontal offset                       write-only
  long  tv_vo         'vertical offset                         write-only
  long  tv_broadcast  'broadcast frequency (Hz)                write-only
  long  tv_auralcog   'aural fm cog                            write-only

  word  screen[x_tiles * y_tiles]
  long  colors[64]

OBJ

  tv    : "display.tv"
  gr    : "display.tv.graphics"
  mouse : "input.mouse"


PUB start | x, y, i, c, k

  'start tv
  longmove(@tv_status, @tvparams, paramcount)
  tv_screen := @screen
  tv_colors := @colors
  tv.start(@tv_status)

  'init colors
  repeat i from $00 to $0F
    case i
      5..10 : c := $01000000 * (i - 5) + $02020507
      other  : c := $07020504
    colors[i] := c
  repeat i from $10 to $1F
    colors[i] := $10100000 * (i & $F) + $0B0A0507
  repeat i from $20 to $2F
    colors[i] := $10100000 * (i & $F) + $0D0C0507
  repeat i from $30 to $3F
    colors[i] := $10100000 * (i & $F) + $080E0507

  'init tile screen
  repeat x from 0 to tv_hc - 1
    repeat y from 0 to tv_vc - 1
      case y
        0, 2 : i := $30 + x
        3..4 : i := $20 + x
        5..6 : i := $10 + x
        8    : i := x
        other:  i := 0
      screen[x + y * tv_hc] := i << 10 + display_base >> 6 + x * tv_vc + y

  'start and setup graphics
  gr.start
  gr.setup(16, 12, 0, 0, bitmap_base)

  'start mouse
  mouse.start(24, 25)
  mousex := 128
  mousey := 35

  repeat

    'clear bitmap
    gr.clear

    'draw color samples
    gr.width(29)
    'draw saturated samples
    gr.color(3)
    repeat x from 0 to 15
      gr.plot(x << 4 + 7, 183)
    'draw gradient samples
    repeat y from 2 to 6
      gr.color(y & 1 | 2)
      repeat x from 0 to 15
        gr.plot(x << 4 + 7, 183 - y << 4)
    'draw monochrome samples
    gr.color(3)
    repeat x from 5 to 10
      gr.plot(x << 4 + 7, 55)

    'draw mouse pointer
    gr.width(0)
    mousex := mousex + mouse.delta_x #> 0 <# 251
    mousey := mousey + mouse.delta_y #> 4 <# 191
    gr.pix(mousex, mousey, 0, @pixdef)

    'check mouse position
    c~~
    if mousey => 176
      c := (mousex & $F0 + $8)
    elseif mousey => 80 and mousey < 160
      c := (mousex & $F0 + $E - (159 - mousey) >> 4)
    elseif mousey => 48 and mousey < 64 and mousex => 80 and mousex < 176
      c := (mousex - 80) >> 4 + 2

    'show appropriate message
    if c => 0
      gr.colorwidth(3, 10)
      gr.textmode(3, 3, 8, %0101)
      hexstring[0] := hex(c >> 4)
      hexstring[1] := hex(c & $F)
      gr.text(128, 17, @colorstring)
      colors.byte[2] := c
      gr.colorwidth(2, 6)
      gr.text(128, 17, @colorstring)
    else
      gr.colorwidth(3, 0)
      gr.textmode(2, 2, 6, %0101)
      gr.text(128, 20, string("Point mouse to color"))

    'copy bitmap to display
    gr.copy(display_base)

    'animate mouse pointer color
    c := k++ & 3 + 4
    i := -3
    repeat 64
      colors.byte[i += 4] := c


PRI hex(value) : chr

  return lookupz(value : "0".."9", "A".."F")

DAT

tvparams                long    0               'status
                        long    1               'enable
                        long    %001_0101       'pins
                        long    %0000           'mode
                        long    0               'screen
                        long    0               'colors
                        long    x_tiles         'hc
                        long    y_tiles         'vc
                        long    10              'hx
                        long    1               'vx
                        long    0               'ho
                        long    0               'vo
                        long    60_000_000      'broadcast
                        long    0               'auralcog


pixdef                  word                    'arrow pointer
                        byte    1,5,0,4
                        word    %%11110000
                        word    %%11100000
                        word    %%11110000
                        word    %%10111000
                        word    %%00011000

colorstring             byte    "COLOR "
hexstring               byte    "00",0

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
