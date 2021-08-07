{{
*****************************************
* Memsic 2125 Driver v1.2               *
* Author: Beau Schwabe                  *
* Copyright (c) 2006 - 2009 Parallax    *
* See end of file for terms of use.     *
*****************************************


History:

Version 1.0 - (07-31-2006) original release
Version 1.1 - (08-17-2008) modified code to return RAW x and y values
Version 1.2 - (12-18-2009) Added X and Y Tilt values

}}
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

  long  cog

  long  offset
  long  scale

  long  calflag                 '5 contiguous longs
  long  _ro
  long  _theta
  long  _xraw
  long  _yraw

PUB start(xpin, ypin) : okay

'' Start driver - starts a cog
'' returns false if no cog available
''
''   xpin  = x input signal
''   ypin  = y input signal
''
    stop
    offset := 90 * (clkfreq / 200)                        'offset value for Tilt conversion
    scale  := clkfreq / 800                               'scale value for Tilt conversion
    ctra_value := $6800_0000 + xpin
    ctrb_value := $6800_0000 + ypin
    mask_value := |<xpin + |<ypin
    okay := cog := cognew(@entry, @calflag) + 1

PUB stop
 '' Stop driver - frees a cog
    if cog
       cogstop(cog~ -  1)
    longfill(@calflag, 0, 3)

PUB setlevel
    calflag := 1

PUB ro : acceleration
    return _ro

PUB theta : angle
    return _theta

PUB Mx
    return _xraw

PUB My
    return _yraw

PUB MxTilt
    return (_xraw*90-offset)/scale

PUB MyTilt
    return (_yraw*90-offset)/scale

DAT

'****************************************
'* Assembly language Memsic 2125 driver *
'****************************************

                        org
'
'
' Entry
'
entry                   mov     ctra,ctra_value         'Setup both counters to simultaniously
                        mov     ctrb,ctrb_value         'read the X-axis and Y-axis from the accelerometer

                        mov     frqa,#1
                        mov     frqb,#1

:loop                   mov     phsa,#0                 'Reset phase A and phase B on each counter
                        mov     phsb,#0

                        waitpeq mask_value,mask_value   'Wait until both the X-axis and Y-axis pins go HIGH
                        waitpeq zero,mask_value         'Wait until both the X-axis and Y-axis pins go LOW

                        mov     rawx,phsa               'move raw phase A and raw phase B values into their
                        mov     rawy,phsb               'coresponding variables

                        rdlong  t1,par          wz      'check calibration flag
        if_nz           mov     levelx,rawx             'If the calibration flag is set, initialize
        if_nz           mov     levely,rawy             'offset variables to compensate level tilt error.
        if_nz           wrlong  zero,par                'reset calibration flag to zero

                        mov     cx,rawx                 'get final x,y and apply level offset
                        sub     cx,levelx
                        mov     cy,rawy
                        sub     cy,levely

                        call    #cordic                 'convert to polar

                        mov     t1,par                  'write result
                        add     t1,#4
                        wrlong  cx,t1
                        add     t1,#4
                        wrlong  ca,t1
                        add     t1,#4
                        wrlong  rawx,t1
                        add     t1,#4
                        wrlong  rawy,t1

                        jmp     #:loop

' Perform CORDIC cartesian-to-polar conversion

cordic                  abs     cx,cx           wc
        if_c            neg     cy,cy
                        mov     ca,#0
                        rcr     ca,#1

                        movs    :lookup,#table
                        mov     t1,#0
                        mov     t2,#20

:loop                   mov     dx,cy           wc
                        sar     dx,t1
                        mov     dy,cx
                        sar     dy,t1
                        sumc    cx,dx
                        sumnc   cy,dy
:lookup                 sumc    ca,table

                        add     :lookup,#1
                        add     t1,#1
                        djnz    t2,#:loop

cordic_ret              ret


table                   long    $20000000
                        long    $12E4051E
                        long    $09FB385B
                        long    $051111D4
                        long    $028B0D43
                        long    $0145D7E1
                        long    $00A2F61E
                        long    $00517C55
                        long    $0028BE53
                        long    $00145F2F
                        long    $000A2F98
                        long    $000517CC
                        long    $00028BE6
                        long    $000145F3
                        long    $0000A2FA
                        long    $0000517D
                        long    $000028BE
                        long    $0000145F
                        long    $00000A30
                        long    $00000518

' Initialized data

ctra_value              long    0
ctrb_value              long    0
mask_value              long    0
zero                    long    0
h80000000               long    $80000000


' Uninitialized data

t1                      res     1
t2                      res     1

rawx                    res     1
rawy                    res     1

levelx                  res     1
levely                  res     1

dx                      res     1
dy                      res     1
cx                      res     1
cy                      res     1
ca                      res     1

