''***************************************
''*  Memsic 2125 Accelerometer DEMO     *
''*  Author: Beau Schwabe               *
''*  Copyright (c) 2009 Parallax, Inc.  *
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

  MMx = 0         'Memsic2125 X channel
  MMy = 1         'Memsic2125 Y channel

{
         ┌──────────┐
Tout ──│1  6│── VDD
         │  ┌────┐  │
Yout ──│2 │ /\ │ 5│── Xout
         │  │/  \│  │
 VSS ──│3 └────┘ 4│── VSS
         └──────────┘

}

VAR

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

  tv            : "display.tv"
  gr            : "display.tv.graphics"
  MM2125        : "sensor.accel.dual.memsic2125"

PUB start | i, dx, dy, clk_scale,d,e,f,fdeg,Offset,Bar,dx1,dy1,dx2,dy2,cordlength,size

  'start tv
  longmove(@tv_status, @tvparams, paramcount)
  tv_screen := @screen
  tv_colors := @colors
  tv.start(@tv_status)

  'init colors
  repeat i from 0 to 63
    colors[i] := $9D_07_1C_02

  'init tile screen
  repeat dx from 0 to tv_hc - 1
    repeat dy from 0 to tv_vc - 1
      screen[dy * tv_hc + dx] := display_base >> 6 + dy + dx * tv_vc + ((dy & $3F) << 10)

  'start and setup graphics
  gr.start
  gr.setup(16, 12, 128, 96, bitmap_base)



'-------------------------------------------------------------------------------------------+

  MM2125.start(MMx, MMy)      '' Initialize Mx2125
  waitcnt(clkfreq/10 + cnt)   'wait for things to settle
  MM2125.setlevel             'assume at startup that the memsic2125 is level
                                'Note: This line is important for determining a deg

  clk_scale := clkfreq / 500_000                      'set clk_scale based on system clock

  gr.textmode(1, 1, 6, %%22)

  size := 95
  repeat

    'clear bitmap
    gr.clear

    d := MM2125.theta         'Get raw 32-bit deg
    d := d >> 19              'Convert 32-bit angle into a 13-Bit angle

    f := 180- MM2125.MxTilt   'Get xTilt Deg
    fdeg := MM2125.MxTilt     'preserve Deg value
    f := (f *1024)/45         'Convert Deg to 13-Bit Angle

    e := 180- MM2125.MyTilt   'Get yTilt Deg
    e := (e *1024)/45         'Convert Deg to 13-Bit Angle





    gr.color(2)
'-----------------------------------------------------------------------------------------
    gr.arc(0,0,size,size,0,256,33,2)                    ''Draw Great Circle
'-----------------------------------------------------------------------------------------
    Offset := -fdeg                                     ''Draw Horizon and Ticks
    repeat i from -180+Offset to 180+Offset
      if (i-Offset) // 5 == 0
         if i => -size and i =< size
            dx := (sin(-e+4096)*i)/65535
            dy := (cos(-e+4096)*i)/65535

            if i == Offset                              'Draw moving Horizon
               cordlength := ^^((size*size)-(fdeg*fdeg))
               dx1 := dx+ (sin(-e+2048)*cordlength)/65535
               dy1 := dy+ (cos(-e+2048)*cordlength)/65535
               dx2 := dx+ (sin(-e-2048)*cordlength)/65535
               dy2 := dy+ (cos(-e-2048)*cordlength)/65535
               gr.plot(dx1,dy1)
               gr.line(dx2,dy2)

               gr.text(dx,dy,string("0 "))
            else                                        'Draw Horizon Ticks...
               If (i-Offset) // 5 == 0                  '...small every 5 Deg
                  Bar := 3
               If (i-Offset) // 45 == 0                 '...large every 45 deg...
                  Bar := 10
                  if i-Offset == -180
                     gr.text(dx,dy,string("-180 "))     '...with text
                  if i-Offset == -135
                     gr.text(dx,dy,string("-135 "))
                  if i-Offset == -90
                     gr.text(dx,dy,string("-90 "))
                  if i-Offset == -45
                     gr.text(dx,dy,string("-45 "))
                  if i-Offset == 45
                     gr.text(dx,dy,string("45 "))
                  if i-Offset == 90
                     gr.text(dx,dy,string("90 "))
                  if i-Offset == 135
                     gr.text(dx,dy,string("135 "))
                  if i-Offset == 180
                     gr.text(dx,dy,string("180 "))

               dx1 := dx+ (sin(-e+2048)*Bar)/65535
               dy1 := dy+ (cos(-e+2048)*Bar)/65535
               dx2 := dx+ (sin(-e-2048)*Bar)/65535
               dy2 := dy+ (cos(-e-2048)*Bar)/65535
               gr.plot(dx1,dy1)
               gr.line(dx2,dy2)

            dx := (sin(-e+4096){*i})/65535              'Draw fixed Horizon
            dy := (cos(-e+4096){*i})/65535
            dx1 := dx+ (sin(-e+2048)* size)/65535
            dy1 := dy+ (cos(-e+2048)* size)/65535
            dx2 := dx+ (sin(-e-2048)* size)/65535
            dy2 := dy+ (cos(-e-2048)* size)/65535
            gr.color(1)
            gr.plot(dx1,dy1)
            gr.line(dx2,dy2)
            gr.color(2)

'-----------------------------------------------------------------------------------------

    gr.colorwidth(3,1)
    repeat i from 0 to 8192 step 1024                   ''Draw Rotational Ticks
      gr.arc(0,0,((size * 70)/90),((size * 70)/90),i-d,0,1,0)                       'Draw Ticks in motion
      gr.arc(0,0,((size * 65)/90),((size * 65)/90),i-d,0,1,1)
    gr.width(0)
    dx1 := 8+(sin(d+2048)*((size * 50)/90))/65535                     'Draw reference '0' Deg in motion
    dy1 := 8+(cos(d+2048)*((size * 50)/90))/65535
    gr.text(dx1,dy1,string("0"))

    gr.color(1)
    repeat i from 0 to 8192 step 128                    'Draw Rotational Ticks Text
      if (i/8)//128 == 0
         dx1 := 8+(sin(-i+2048)*((size * 65)/90))/65535
         dy1 := 8+(cos(-i+2048)*((size * 65)/90))/65535
         if i == 0
            gr.text(dx1,dy1,string("0"))
         if i == 1024
            gr.text(dx1,dy1,string("45"))
         if i == 2048
            gr.text(dx1,dy1,string("90"))
         if i == 3072
            gr.text(dx1,dy1,string("135"))
         if i == 4096
            gr.text(dx1,dy1,string("180"))
         if i == 5120
            gr.text(dx1,dy1,string("225"))
         if i == 6144
            gr.text(dx1,dy1,string("270"))
         if i == 7168
            gr.text(dx1,dy1,string("315"))

         gr.arc(0,0,((size * 75)/90),((size * 75)/90),i,0,1,0)                      'Draw fixed Rotational Ticks
      else
         gr.arc(0,0,((size * 85)/90),((size * 85)/90),i,0,1,0)
      gr.arc(0,0,size,size,i,0,1,1)
    gr.color(2)


    'copy bitmap to display
    gr.copy(display_base)


pub cos(angle) : x

'' Get cosine of angle (0-8191)

  x := sin(angle + $800)

pub sin(angle) : y

'' Get sine of angle (0-8191)

  y := angle << 1 & $FFE
  if angle & $800
    y := word[$F000 - y]
  else
    y := word[$E000 + y]
  if angle & $1000
    -y

DAT

tvparams                long    0               'status
                        long    1               'enable

                        long    %001_0101       'pins   New Demo Board
'                        long    %011_0000       'pins   Old Demo Board

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
