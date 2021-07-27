{{
*****************************************
* Servo32v7 Driver                   v7 *
* Author: Beau Schwabe                  *
* Copyright (c) 2009 Parallax           *
* See end of file for terms of use.     *
*****************************************


*****************************************************************
 Control up to 32-Servos      Version 7               08-18-2009
*****************************************************************
 Coded by Beau Schwabe (Parallax).
*****************************************************************

 History:
                            Version 1 -               initial concept

                            Version 2 - (03-08-2006)  Beta release

                            Version 3 - (11-04-2007)  Improved servo resolution to 1uS

                            Version 4 - (05-03-2009)  Ability to disable a servo channel
                                                      and remove channel preset requirement

                            Version 5 - (05-08-2009)  Added ramping ability

                            Version 6 - (07-18-2009)  Fixed slight timing skew in ZoneLoop and
                                                      fixed overhead timing latency in ZoneCore

                            Version 7 - (08-18-2009)  Fixed servo jitter in ramping function when
                                                      servo reached it's target position.

Theory of Operation:

Each servo requires a pulse that varies from 1mS to 2mS with a period of 20mS.
To prevent a current surge nightmare, I have broken the 20mS period into four
groups or Zones of 8 servos each with a period of 5mS. What this does, is to
ensure that at any given moment in time, a maximum of only 8 servos are receiving
a pulse.

   Zone1 Zone2 Zone3    Zone4          In Zone1, servo pins  0- 7 are active
           In Zone2, servo pins  8-15 are active
  │─5mS│─5mS│─5mS│─5mS│        In Zone3, servo pins 16-23 are active
  │──────────20mS───────────│        In Zone4, servo pins 24-31 are active
                  
    1-2mS servo pulse 

The preferred circuit of choice is to place a 4.7K resistor on each signal input
to the servo.    If long leads are used, place a 1000uF cap at the servo power
connector.    Servo's seem to be happy with a 5V supply receiving a 3.3V signal.

}}

CON
    _1uS = 1_000_000 /        1                                                 'Divisor for 1 uS

    ZonePeriod = 5_000                                                          '5mS (1/4th of typical servo period of 20mS)
    NoGlitchWindow = 3_000                                                      '3mS Glitch prevention window (set value larger than maximum servo width of 2.5mS)
                                                                                'Use at least 500uS for overhead (actual overhead about 300uS)

     LowRange = 500             '<- during Debug I changed this value to 1 to test 1us resolution
    HighRange = 2500

VAR
        long          RampFlag
        long          ZoneClocks
        long          NoGlitch
        long          ServoPinDirection
        long          ServoData[32]                                             '0-31 Current Servo Value
        long          ServoTarget[32]                                           '0-31 Desired Servo Value
        long          ServoDelay[32]                                            '0-31 Servo Ramp Delay

OBJ

    SERVO : "motor.servo.ramp"

PUB Start
    RampFlag := 0
    ZoneClocks := (clkfreq / _1uS * ZonePeriod)                                 'calculate # of clocks per ZonePeriod
    NoGlitch   := $FFFF_FFFF-(clkfreq / _1uS * NoGlitchWindow)                  'calculate # of clocks for GlitchFree servos. Problem occurs when 'cnt' value rollover is less than the servo's pulse width.
    cognew(@ServoStart,@ZoneClocks)

PUB Ramp
    SERVO.StartRamp(@ServoData)
    RampFlag := 1

PUB SetRamp(Pin, Width,Delay)|S_Width
      ServoDelay[Pin] := Delay 'Note: The resolution of Delay is about 38.75us
                               '      or 3100 clocks

      S_Width := LowRange #> Width <# HighRange                                 'limit Width value
      Pin :=    0 #> Pin <# 31                                                  'limit Pin value between 0 and 31
      ServoTarget[Pin] := ((clkfreq / _1uS * S_Width)/SERVO#CoreSpeed)*SERVO#CoreSpeed
                                                                                'calculate # of clocks for a specific Pulse Width
                                                                                'and adjust the snap value to match the value of
                                                                                'CoreSpeed. (Note: this prevents jitter when Servo
                                                                                '                  has reached it's target position)


      if S_Width==Width
         dira[Pin] := 1                                                         'set selected servo pin as an OUTPUT
      else
         dira[Pin] := 0                                                         'set selected servo pin as an INPUT only if Width out of range
      ServoPinDirection := dira                                                 'Read I/O state of ALL pins

PUB Set(Pin, Width)|S_Width                                                     'Set Servo value
      S_Width := LowRange #> Width <# HighRange                                 'limit Width value
      Pin :=    0 #> Pin <# 31                                                  'limit Pin value between 0 and 31
      if RampFlag == 0
         ServoData[Pin] := (clkfreq / _1uS * S_Width)                           'calculate # of clocks for a specific Pulse Width
      else
         ServoData[Pin] := ((clkfreq / _1uS * S_Width)/SERVO#CoreSpeed)*SERVO#CoreSpeed

      ServoTarget[Pin] := ServoData[Pin]

      if S_Width==Width
         dira[Pin] := 1                                                         'set selected servo pin as an OUTPUT
      else
         dira[Pin] := 0                                                         'set selected servo pin as an INPUT only if Width out of range
      ServoPinDirection := dira                                                 'Read I/O state of ALL pins

DAT

'*********************
'* Assembly language *
'*********************
                        org
'------------------------------------------------------------------------------------------------------------------------------------------------
ServoStart              mov     Index,                  par                     'Set Index Pointer
                        rdlong  _ZoneClocks,            Index                   'Get ZoneClock value
                        add     Index,                  #4                      'Increment Index to next Pointer
                        rdlong  _NoGlitch,              Index                   'Get NoGlitch value
                        add     Index,                  #4                      'Increment Index to next Pointer
                        mov     PinDirectionAddress,    Index                   'Set pointer for I/O direction Address
                        add     Index,                  #32                     'Increment Index to END of Zone1 Pointer
                        mov     Zone1Index,             Index                   'Set Index Pointer for Zone1
                        add     Index,                  #32                     'Increment Index to END of Zone2 Pointer
                        mov     Zone2Index,             Index                   'Set Index Pointer for Zone2
                        add     Index,                  #32                     'Increment Index to END of Zone3 Pointer
                        mov     Zone3Index,             Index                   'Set Index Pointer for Zone3
                        add     Index,                  #32                     'Increment Index to END of Zone4 Pointer
                        mov     Zone4Index,             Index                   'Set Index Pointer for Zone4
IOupdate                rdlong  dira,                   PinDirectionAddress     'Get and set I/O pin directions
'------------------------------------------------------------------------------------------------------------------------------------------------
Zone1                   mov     ZoneIndex,              Zone1Index              'Set Index Pointer for Zone1
                        call    #ResetZone
                        call    #ZoneCore
Zone2                   mov     ZoneIndex,              Zone2Index              'Set Index Pointer for Zone2
                        call    #IncrementZone
                        call    #ZoneCore
Zone3                   mov     ZoneIndex,              Zone3Index              'Set Index Pointer for Zone3
                        call    #IncrementZone
                        call    #ZoneCore
Zone4                   mov     ZoneIndex,              Zone4Index              'Set Index Pointer for Zone4
                        call    #IncrementZone
                        call    #ZoneCore
                        jmp     #IOupdate
'------------------------------------------------------------------------------------------------------------------------------------------------
ResetZone               mov     ZoneShift1,             #1
                        mov     ZoneShift2,             #2
                        mov     ZoneShift3,             #4
                        mov     ZoneShift4,             #8
                        mov     ZoneShift5,             #16
                        mov     ZoneShift6,             #32
                        mov     ZoneShift7,             #64
                        mov     ZoneShift8,             #128
ResetZone_ret           ret
'------------------------------------------------------------------------------------------------------------------------------------------------
IncrementZone           shl     ZoneShift1,             #8
                        shl     ZoneShift2,             #8
                        shl     ZoneShift3,             #8
                        shl     ZoneShift4,             #8
                        shl     ZoneShift5,             #8
                        shl     ZoneShift6,             #8
                        shl     ZoneShift7,             #8
                        shl     ZoneShift8,             #8
IncrementZone_ret       ret
'------------------------------------------------------------------------------------------------------------------------------------------------
ZoneCore                mov     ServoByte,              #0                      'Clear ServoByte
                        mov     Index,                  ZoneIndex               'Set Index Pointer for proper Zone

ZoneSync                mov     SyncPoint,              cnt                     'Create a Sync Point with the system counter
'                       mov     temp,                   _NoGlitch
'                       sub     temp,                   SyncPoint            wc
                        sub     _NoGlitch,              SyncPoint         nr,wc 'Test to make sure 'cnt' value won't rollover within Servo's pulse width
              if_C      jmp     #ZoneSync                                       'If C flag is set get a new Sync Point, otherwise we are ok.


'                       add     SyncPoint,              #220                    '<- Debug - 2us becomes 1us
'                       add     SyncPoint,              #300                    '<- Debug - 2us becomes 3us
                        add     SyncPoint,              #260                    'Add overhead offset to counter Sync point
                                                                                'midpoint from above Debug test.

                        mov     LoopCounter,            #8                      'Set Loop Counter to 8 Servos for this Zone
                        movd    LoadServos,             #ServoWidth8            'Restore/Set self-modifying code on "LoadServos" line
                        movd    ServoSync,              #ServoWidth8            'Restore/Set self-modifying code on "ServoSync" line

LoadServos              rdlong  ServoWidth8,            Index                   'Get Servo Data
                        sub     Index,                  #4                      'Decrement Index pointer to next address
                        nop
ServoSync               add     ServoWidth8,            SyncPoint               'Determine system counter location where pulse should end
                        sub     LoadServos,             d_field                 'self-modify destination pointer for "LoadServos" line
                        sub     ServoSync,              d_field                 'self-modify destination pointer for "ServoSync" line
                        djnz    LoopCounter,            #LoadServos             'Do ALL 8 servo positions for this Zone

                        mov     temp,                   _ZoneClocks             'Move _ZoneClocks into temp
                        add     temp,                   SyncPoint               'Add SyncPoint to _ZoneClocks

'----------------------------------------------Start Tight Servo code-------------------------------------------------------------
ZoneLoop                mov     tempcnt,                cnt                     '(4 - clocks) take a snapshot of current counter value
                        cmpsub  ServoWidth1,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift1              '(4 - clocks) Set ServoByte.Bit0 to "0" or "1" depending on the value of "C"
                        cmpsub  ServoWidth2,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift2              '(4 - clocks) Set ServoByte.Bit1 to "0" or "1" depending on the value of "C"
                        cmpsub  ServoWidth3,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift3              '(4 - clocks) Set ServoByte.Bit2 to "0" or "1" depending on the value of "C"
                        cmpsub  ServoWidth4,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift4              '(4 - clocks) Set ServoByte.Bit3 to "0" or "1" depending on the value of "C"
                        cmpsub  ServoWidth5,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift5              '(4 - clocks) Set ServoByte.Bit4 to "0" or "1" depending on the value of "C"
                        cmpsub  ServoWidth6,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift6              '(4 - clocks) Set ServoByte.Bit5 to "0" or "1" depending on the value of "C"
                        cmpsub  ServoWidth7,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift7              '(4 - clocks) Set ServoByte.Bit6 to "0" or "1" depending on the value of "C"
                        cmpsub  ServoWidth8,            tempcnt       nr,wc     '(4 - clocks) compare system counter to ServoWidth ; write result in C flag
                        muxc    ServoByte,              ZoneShift8              '(4 - clocks) Set ServoByte.Bit7 to "0" or "1" depending on the value of "C"
                        mov     outa,                   ServoByte               '(4 - clocks) Send ServoByte to Zone Port
                        cmp     temp,                   tempcnt       nr,wc     '(4 - clocks) Determine if cnt has exceeded width of _ZoneClocks ; write result in C flag
              if_NC     jmp     #ZoneLoop                                       '(4 - clocks) if the "C Flag" is not set stay in the current Zone
'-----------------------------------------------End Tight Servo code--------------------------------------------------------------
'                                                                        Total = 80 - clocks  @ 80MHz that's 1uS resolution
ZoneCore_ret            ret
'------------------------------------------------------------------------------------------------------------------------------------------------
d_field                 long    $0000_0200

PinDirectionAddress     long    0

counter                 long    0
Address1                long    0
Address2                long    0
Address3                long    0
temp1                   long    0
temp2                   long    0
tempcnt                 long    0

dly                     long    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
                        long    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

ServoWidth1             res     1
ServoWidth2             res     1
ServoWidth3             res     1
ServoWidth4             res     1
ServoWidth5             res     1
ServoWidth6             res     1
ServoWidth7             res     1
ServoWidth8             res     1

ZoneShift1              res     1
ZoneShift2              res     1
ZoneShift3              res     1
ZoneShift4              res     1
ZoneShift5              res     1
ZoneShift6              res     1
ZoneShift7              res     1
ZoneShift8              res     1

temp                    res     1
Index                   res     1
ZoneIndex               res     1
Zone1Index              res     1
Zone2Index              res     1
Zone3Index              res     1
Zone4Index              res     1
SyncPoint               res     1

ServoByte               res     1
LoopCounter             res     1

_ZoneClocks             res     1
_NoGlitch               res     1
_ServoPinDirection      res     1

