''***************************************
''*  Microphone-to-Headphones v1.0      *
''*  Author: Chip Gracey                *
''*  Copyright (c) 2006 Parallax, Inc.  *
''*  See end of file for terms of use.  *
''***************************************

' This program uses the Propeller Demo Board, Rev C
' The microphone is digitized and the samples are played on the headphones.

CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000


' At 80MHz the ADC/DAC sample resolutions and rates are as follows:
'
' sample   sample
' bits       rate
' ----------------
' 5       2.5 MHz
' 6      1.25 MHz
' 7       625 KHz
' 8       313 KHz
' 9       156 KHz
' 10       78 KHz
' 11       39 KHz
' 12     19.5 KHz
' 13     9.77 KHz
' 14     4.88 KHz

  bits = 11               'try different values from table here


PUB go

  cognew(@asm_entry, 0)   'launch assembly program into a COG


DAT

'
'
' Assembly program
'
              org

asm_entry     mov       dira,asm_dira                   'make pins 8 (ADC) and 0 (DAC) outputs

              movs      ctra,#8                         'POS W/FEEDBACK mode for CTRA
              movd      ctra,#9
              movi      ctra,#%01001_000
              mov       frqa,#1

              movs      ctrb,#10                        'DUTY DIFFERENTIAL mode for CTRB
              movd      ctrb,#11
              movi      ctrb,#%00111_000

              mov       asm_cnt,cnt                     'prepare for WAITCNT loop
              add       asm_cnt,asm_cycles


:loop         waitcnt   asm_cnt,asm_cycles              'wait for next CNT value (timing is determinant after WAITCNT)

              mov       asm_sample,phsa                 'capture PHSA and get difference
              sub       asm_sample,asm_old
              add       asm_old,asm_sample

              shl       asm_sample,#32-bits             'justify sample and output to FRQB
              mov       frqb,asm_sample

              jmp       #:loop                          'wait for next sample period
'
'
' Data
'
asm_cycles    long      |< bits - 1                     'sample time
asm_dira      long      $00000E00                       'output mask

asm_cnt       res       1
asm_old       res       1
asm_sample    res       1

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