''***************************************
''*  Dither v1.0                        *
''*  Author: Chip Gracey                *
''*  Copyright (c) 2006 Parallax, Inc.  *
''*  See end of file for terms of use.  *
''***************************************

con dither = 5

pub start(ptr)

  cognew(@entry, ptr)

dat

                        org

entry                   mov     ctra,ctra_
                        mov     ctrb,ctrb_
                        mov     dira,dira_


loop                    rdlong  left,par                '20 instructions per loop = 1us period @80MHz
                        mov     right,left
                        and     left,hFFFF0000
                        shl     right,#16
                        add     left,h80000000
                        add     right,h80000000

                        test    lfsr0,taps0     wc
                        rcl     lfsr0,#1
                        test    lfsr1,taps1     wc
                        rcl     lfsr1,#1

                        mov     t1,lfsr0                'update left output
                        sar     t1,#dither
                        add     t1,left
                        mov     frqa,t1

                        mov     t1,lfsr1                'update right output
                        sar     t1,#dither
                        add     t1,right
                        mov     frqb,t1

                        jmp     #loop


ctra_                   long    $18000000+11
ctrb_                   long    $18000000+10
dira_                   long    |<11 + |<10

lfsr0                   long    1
lfsr1                   long    1
taps0                   long    $A4000080
taps1                   long    $80A01000

hFFFF0000               long    $FFFF0000
h80000000               long    $80000000

left                    res     1
right                   res     1
t1                      res     1

