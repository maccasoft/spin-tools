''***************************************
''*  Graphics Demo                      *
''*  Author: Chip Gracey                *
''*  Copyright (c) 2005 Parallax, Inc.  *
''*  See end of file for terms of use.  *
''***************************************


CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000
  _stack = ($3000 + $3000 + 100) >> 2   'accomodate display memory and stack

  x_tiles = 16
  y_tiles = 12

  paramcount = 14
  bitmap_base = $2000
  display_base = $5000

  lines = 5
  thickness = 2


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

  byte  x[lines]
  byte  y[lines]
  byte  xs[lines]
  byte  ys[lines]


OBJ

  tv    : "display.tv"
  gr    : "display.tv.graphics"
  mouse : "input.mouse"


PUB start | i, j, k, kk, dx, dy, pp, pq, rr, numx, numchr

  'start tv
  longmove(@tv_status, @tvparams, paramcount)
  tv_screen := @screen
  tv_colors := @colors
  tv.start(@tv_status)

  'init colors
  repeat i from 0 to 63
    colors[i] := $00001010 * (i+4) & $F + $2B060C02

  'init tile screen
  repeat dx from 0 to tv_hc - 1
    repeat dy from 0 to tv_vc - 1
      screen[dy * tv_hc + dx] := display_base >> 6 + dy + dx * tv_vc + ((dy & $3F) << 10)

  'init bouncing lines
  i := 1001
  j := 123123
  k := 8776434
  repeat i from 0 to lines - 1
    x[i] := ?j // 64
    y[i] := k? // 48
    repeat until xs[i] := k? ~> 29
    repeat until ys[i] := ?j ~> 29

  'start and setup graphics
  gr.start
  gr.setup(16, 12, 128, 96, bitmap_base)

  'start mouse
  mouse.start(24, 25)

  repeat

    'clear bitmap
    gr.clear

    'draw spinning triangles
    gr.colorwidth(3,0)
    repeat i from 1 to 8
      gr.vec(0, 0, (k & $7F) << 3 + i << 5, k << 6 + i << 8, @vecdef)

    'draw expanding mouse crosshairs
    gr.colorwidth(2,k>>2)
    mousex := mousex + mouse.delta_x #> -128 <# 127
    mousey := mousey + mouse.delta_y #> -96 <# 95
    gr.pix(mousex, mousey, k>>4 & $7, @pixdef)

    'if left mouse button pressed, throw snowballs
    if mouse.button(0)
      gr.width(pq & $F)
      gr.color(2)
      pp := (pq & $F)*(pq & $F) + 5
      pq++
      gr.arc(mousex, mousey, pp, pp>>1, -k * 200, $200, 8, 0)
    else
      pq~

    'if right mouse button pressed, pause
    repeat while mouse.button(1)

    'draw expanding pixel halo
    gr.colorwidth(1,k)
    gr.arc(0,0,80,30,-k<<5,$2000/9,9,0)

    'step bouncing lines
    repeat i from 0 to lines - 1
      if || ~x[i] > 60
        -xs[i]
      if || ~y[i] > 40
        -ys[i]
      x[i] += xs[i]
      y[i] += ys[i]

    'draw bouncing lines
    gr.colorwidth(1,thickness)
    gr.plot(~x[0], ~y[0])
    repeat i from 1 to lines - 1
      gr.line(~x[i],~y[i])
    gr.line(~x[0], ~y[0])

    'draw spinning stars and revolving crosshairs and dogs
    gr.colorwidth(2,0)
    repeat i from 0 to 7
      gr.vecarc(80,50,30,30,-(i<<10+k<<6),$40,-(k<<7),@vecdef2)
      gr.pixarc(-80,-40,30,30,i<<10+k<<6,0,@pixdef2)
      gr.pixarc(-80,-40,20,20,-(i<<10+k<<6),0,@pixdef)

    'draw small box with text
    gr.colorwidth(1,14)
    gr.box(60,-80,60,16)
    gr.textmode(1,1,6,5)
    gr.colorwidth(2,0)
    gr.text(90,-72,@pchip)

    'draw incrementing digit
    if not ++numx & 7
      numchr++
    if numchr < "0" or numchr > "9"
      numchr := "0"
    gr.textmode(8,8,6,5)
    gr.colorwidth(1,8)
    gr.text(-90,50,@numchr)

    'copy bitmap to display
    gr.copy(display_base)

    'increment counter that makes everything change
    k++


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
                        long    0               'broadcast
                        long    0               'auralcog

vecdef                  word    $4000+$2000/3*0         'triangle
                        word    50
                        word    $8000+$2000/3*1+1
                        word    50
                        word    $8000+$2000/3*2-1
                        word    50
                        word    $8000+$2000/3*0
                        word    50
                        word    0

vecdef2                 word    $4000+$2000/12*0        'star
                        word    50
                        word    $8000+$2000/12*1
                        word    20
                        word    $8000+$2000/12*2
                        word    50
                        word    $8000+$2000/12*3
                        word    20
                        word    $8000+$2000/12*4
                        word    50
                        word    $8000+$2000/12*5
                        word    20
                        word    $8000+$2000/12*6
                        word    50
                        word    $8000+$2000/12*7
                        word    20
                        word    $8000+$2000/12*8
                        word    50
                        word    $8000+$2000/12*9
                        word    20
                        word    $8000+$2000/12*10
                        word    50
                        word    $8000+$2000/12*11
                        word    20
                        word    $8000+$2000/12*0
                        word    50
                        word    0

pixdef                  word                            'crosshair
                        byte    2,7,3,3
                        word    %%00333000,%%00000000
                        word    %%03020300,%%00000000
                        word    %%30020030,%%00000000
                        word    %%32222230,%%00000000
                        word    %%30020030,%%02000000
                        word    %%03020300,%%22200000
                        word    %%00333000,%%02000000

pixdef2                 word                            'dog
                        byte    1,4,0,3
                        word    %%20000022
                        word    %%02222222
                        word    %%02222200
                        word    %%02000200

pchip                   byte    "Propeller",0           'text

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
