''***************************************
''*  Microphone-to-VGA v1.0             *
''*  Author: Chip Gracey                *
''*  Copyright (c) 2006 Parallax, Inc.  *
''*  See end of file for terms of use.  *
''***************************************

' This program uses the Propeller Demo Board, Rev C
'
' The microphone is digitized and the samples are displayed on a VGA monitor, just like
' an oscilloscope with triggering.
'
' This program is sloppy and not ready for prime time. I just wanted to share it now.

CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000

  tiles    = vga#xtiles * vga#ytiles
  tiles32  = tiles * 32


OBJ

  vga : "display.vga.bitmap.512x384"


VAR

  long  sync, pixels[tiles32]
  word  colors[tiles], ypos[512]


PUB start | i

  'start vga
  vga.start(16, @colors, @pixels, @sync)

  'init colors to cyan on black
  repeat i from 0 to tiles - 1
    colors[i] := $3C00

  'fill top line so that it gets erased by COG
  longfill(@pixels, $FFFFFFFF, vga#xtiles)

  'implant pointers and launch assembly program into COG
  asm_pixels := @pixels
  asm_ypos := @ypos
  cognew(@asm_entry, 0)



CON

' At 80MHz the ADC sample resolutions and rates are as follows:
'
' sample   sample
' bits       rate
' ----------------
' 9       156 KHz
' 10       78 KHz
' 11       39 KHz
' 12     19.5 KHz
' 13     9.77 KHz
' 14     4.88 KHz

  bits = 12                     'try different values from table here
  attenuation = 2               'try 0-4

  averaging = 13                '2-power-n samples to compute average with


DAT

'
'
' Assembly program
'
              org

asm_entry     mov       dira,asm_dira                   'make pin 8 (ADC) output

              movs      ctra,#8                         'POS W/FEEDBACK mode for CTRA
              movd      ctra,#9
              movi      ctra,#%01001_000
              mov       frqa,#1

              mov       xpos,#0

              mov       asm_cnt,cnt                     'prepare for WAITCNT loop
              add       asm_cnt,asm_cycles


:loop         waitcnt   asm_cnt,asm_cycles              'wait for next CNT value (timing is determinant after WAITCNT)

              mov       asm_sample,phsa                 'capture PHSA and get difference
              sub       asm_sample,asm_old
              add       asm_old,asm_sample


              add       average,asm_sample              'compute average periodically so that
              djnz      average_cnt,#:avgsame           'we can 0-justify samples
              mov       average_cnt,average_load
              shr       average,#averaging
              mov       asm_justify,average
              mov       average,#0                      'reset average for next averaging
:avgsame

              max       peak_min,asm_sample             'track min and max peaks for triggering
              min       peak_max,asm_sample
              djnz      peak_cnt,#:pksame
              mov       peak_cnt,peak_load
              mov       x,peak_max                      'compute min+12.5% and max-12.5%
              sub       x,peak_min
              shr       x,#3
              mov       trig_min,peak_min
              add       trig_min,x
              mov       trig_max,peak_max
              sub       trig_max,x
              mov       peak_min,bignum                 'reset peak detectors
              mov       peak_max,#0
:pksame

              cmp       mode,#0                 wz      'wait for negative trigger threshold
if_z          cmp       asm_sample,trig_min     wc
if_z_and_c    mov       mode,#1
if_z          jmp       #:loop

              cmp       mode,#1                 wz      'wait for positive trigger threshold
if_z          cmp       asm_sample,trig_max     wc
if_z_and_nc   mov       mode,#2
if_z          jmp       #:loop


              sub       asm_sample,asm_justify          'justify sample to bitmap center y
              sar       asm_sample,#attenuation         'this # controls attenuation (0=none)
              add       asm_sample,#384 / 2
              mins      asm_sample,#0
              maxs      asm_sample,#384 - 1

              mov       x,xpos                          'xor old pixel off
              shl       x,#1
              add       x,asm_ypos
              rdword    y,x                             'get old pixel-y
              wrword    asm_sample,x                    'save new pixel-y
              mov       x,xpos
              call      #plot

              mov       x,xpos                          'xor new pixel on
              mov       y,asm_sample
              call      #plot

              add       xpos,#1                         'increment x position and mask
              and       xpos,#$1FF              wz
if_z          mov       mode,#0                         'if rollover, reset mode for trigger

              jmp       #:loop                          'wait for next sample period
'
'
' Plot
'
plot          mov       asm_mask,#1                     'compute pixel mask
              shl       asm_mask,x
              shl       y,#6                            'compute pixel address
              add       y,asm_pixels
              shr       x,#5
              shl       x,#2
              add       y,x
              rdlong    asm_data,y                      'xor pixel
              xor       asm_data,asm_mask
              wrlong    asm_data,y

plot_ret      ret
'
'
' Data
'
asm_cycles    long      |< bits - 1                     'sample time
asm_dira      long      $00000200                       'output mask
asm_pixels    long      0                               'pixel base (set at runtime)
asm_ypos      long      0                               'y positions (set at runtime)
average_cnt   long      1
peak_cnt      long      1
peak_load     long      512
mode          long      0
bignum        long      $FFFFFFFF
average_load  long      |< averaging

asm_justify   res       1
trig_min      res       1
trig_max      res       1
average       res       1
asm_cnt       res       1
asm_old       res       1
asm_sample    res       1
asm_mask      res       1
asm_data      res       1
xpos          res       1
x             res       1
y             res       1
peak_min      res       1
peak_max      res       1

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
