{{
*****************************************
* Servo32 Ramp Driver                v2 *
* Author: Beau Schwabe                  *
* Copyright (c) 2009 Parallax           *
* See end of file for terms of use.     *
*****************************************

*****************************************************************
 Control ramping of up to 32-Servos      Version2     08-18-2009
*****************************************************************
 Coded by Beau Schwabe (Parallax).
*****************************************************************


 History:
                           Version 1 - (05-11-2009) initial concept

                           Version 2 - (08-18-2009) updated where 'CoreSpeed' is iniitalized so that the
                                                    Servo32 object can take beter advantage of the assigned value.
}}

CON
                 CoreSpeed    =     620

                                             '' Note: It takes aproximately 3100 clocks to process all 32 Channels,
                                             ''       So the resolution is about 38.75us

                                             '' increment/decrement pulse width every 3100 clocks
                                             '' So at 2us and a full sweep 500us to 2500us (Delta of 2000us)
                                             '' the total time travel would be 38.75ms
                                             ''
                                             '' 160 = 2us @ 38.750ms
                                             '' 240 = 3us @ 25.833ms
                                             '' 320 = 4us @ 19.375ms
                                             '' 400 = 5us @ 15.500ms
                                             '' 413 = 5.1625us @ 15.012ms
                                             '' 480 = 6us @ 12.917ms
                                             '' 560 = 7us @ 11.071ms
                                             '' 620 = 7.75us @ 10ms
                                             '' 640 = 8us @ 9.6875ms


PUB StartRamp (ServoData)
    cognew(@RampStart,ServoData)

DAT

'*********************
'* Assembly language *
'*********************

'' Note: It takes aproximately 3100 clocks to process all 32 Channels,
''       So the resolution is about 38.75us

                        org
'------------------------------------------------------------------------------------------------------------------------------------------------
RampStart
                        mov     Address1,       par              'ServoData
                        mov     Address2,       Address1
                        add     Address2,       #128             'ServoTarget
                        mov     Address3,       Address2
                        add     Address3,       #128             'ServoDelay
'---------------------------------------------------------------------------------------
Ch01                    sub      dly + 00,      #1      wc
                   if_c rdlong   dly + 00,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch02                    sub      dly + 01,      #1      wc
                   if_c rdlong   dly + 01,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch03                    sub      dly + 02,      #1      wc
                   if_c rdlong   dly + 02,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch04                    sub      dly + 04,      #1      wc
                   if_c rdlong   dly + 04,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch05                    sub      dly + 05,      #1      wc
                   if_c rdlong   dly + 05,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch06                    sub      dly + 06,      #1      wc
                   if_c rdlong   dly + 06,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch07                    sub      dly + 07,      #1      wc
                   if_c rdlong   dly + 07,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch08                    sub      dly + 08,      #1      wc
                   if_c rdlong   dly + 08,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch09                    sub      dly + 09,      #1      wc
                   if_c rdlong   dly + 09,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch10                    sub      dly + 10,      #1      wc
                   if_c rdlong   dly + 10,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch11                    sub      dly + 11,      #1      wc
                   if_c rdlong   dly + 11,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch12                    sub      dly + 12,      #1      wc
                   if_c rdlong   dly + 12,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch13                    sub      dly + 13,      #1      wc
                   if_c rdlong   dly + 13,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch14                    sub      dly + 14,      #1      wc
                   if_c rdlong   dly + 14,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch15                    sub      dly + 15,      #1      wc
                   if_c rdlong   dly + 15,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch16                    sub      dly + 16,      #1      wc
                   if_c rdlong   dly + 16,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch17                    sub      dly + 17,      #1      wc
                   if_c rdlong   dly + 17,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch18                    sub      dly + 18,      #1      wc
                   if_c rdlong   dly + 18,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch19                    sub      dly + 19,      #1      wc
                   if_c rdlong   dly + 19,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch20                    sub      dly + 20,      #1      wc
                   if_c rdlong   dly + 20,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch21                    sub      dly + 21,      #1      wc
                   if_c rdlong   dly + 21,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch22                    sub      dly + 22,      #1      wc
                   if_c rdlong   dly + 22,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch23                    sub      dly + 23,      #1      wc
                   if_c rdlong   dly + 23,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch24                    sub      dly + 24,      #1      wc
                   if_c rdlong   dly + 24,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch25                    sub      dly + 25,      #1      wc
                   if_c rdlong   dly + 25,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch26                    sub      dly + 26,      #1      wc
                   if_c rdlong   dly + 26,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch27                    sub      dly + 27,      #1      wc
                   if_c rdlong   dly + 27,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch28                    sub      dly + 28,      #1      wc
                   if_c rdlong   dly + 28,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch29                    sub      dly + 29,      #1      wc
                   if_c rdlong   dly + 29,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch30                    sub      dly + 30,      #1      wc
                   if_c rdlong   dly + 30,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch31                    sub      dly + 31,      #1      wc
                   if_c rdlong   dly + 31,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
Ch32                    sub      dly + 32,      #1      wc
                   if_c rdlong   dly + 32,      Address3         'Move Delay into temp delay value
                        call     #RampCore
'---------------------------------------------------------------------------------------
                        jmp     #RampStart
'-------------------------------------------------------------------
'-------------------------------------------------------------------
RampCore
                        rdlong   temp1,         Address1         'Move ServoData into temp1
                        rdlong   temp2,         Address2         'Move ServoTarget into temp2
                  if_nc jmp      #CodeBalance
                        cmp      temp1,         temp2   wc,wz

            if_c_and_nz add      temp1,         _CoreSpeed        'Increment ServoData if ServoTarget is greater
           if_nc_and_nz sub      temp1,         _CoreSpeed        'Decrement ServoData if ServoTarget is less

OutLoop                 wrlong   temp1,         Address1         'Update ServoData value

                        add      Address1,      #4               'Increment Delay pointer
                        add      Address2,      #4               'Increment ServoData pointer
                        add      Address3,      #4
RampCore_ret            ret

CodeBalance             nop                                      'makes for equal code branch path
                        jmp     #OutLoop
'-------------------------------------------------------------------
'-------------------------------------------------------------------
time1                   long    0
time2                   long    0

_CoreSpeed              long    CoreSpeed

Address1                long    0
Address2                long    0
Address3                long    0
Address4                long    0

temp1                   long    0
temp2                   long    0

dly                     long    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
                        long    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

