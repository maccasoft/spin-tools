{{
''****************************************
''*  HM55B Compass Module DEMO      V1.2 *
''*  Author: Beau Schwabe                *
''*  Copyright (c) 2008 Parallax, Inc.   *
''*  See end of file for terms of use.   *
''****************************************

            ┌────┬┬────┐
      ┌────│1  6│── +5V       P0 = Enable
      │ 1K  │  ├┴──┴┤  │               P1 = Clock
  P2 ┻──│2 │ /\ │ 5│── P0        P2 = Data
            │  │/  \│  │
    VSS ──│3 └────┘ 4│── P1
            └──────────┘

}}
VAR
long    cog,Enable,Clock,Data,HM55B_x,HM55B_y,HM55B_theta

PUB stop
'' Stop driver - frees a cog
    if cog
       cogstop(cog~ -  1)

PUB start(EnablePin,ClockPin,DataPin): okay
'' Start driver - starts a cog
'' returns false if no cog available
    Enable := EnablePin
    Clock := ClockPin
    Data := DataPin
    okay := cog := cognew(@HM55B, @Enable)

PUB x
    return HM55B_x

PUB y
    return HM55B_y

PUB theta
    return HM55B_theta

DAT
HM55B         org

              mov       t1,                     par
              rdlong    t2,                     t1
              mov       EnablePin_mask,         #1
              shl       EnablePin_mask,         t2

              add       t1,                     #4
              rdlong    t2,                     t1
              mov       ClockPin_mask,          #1
              shl       ClockPin_mask,          t2

              add       t1,                     #4
              rdlong    t2,                     t1
              mov       DataPin_mask,           #1
              shl       DataPin_mask,           t2

              add       t1,                     #4
              mov       HM55B_x_,               t1

              add       t1,                     #4
              mov       HM55B_y_,               t1

              add       t1,                     #4
              mov       HM55B_theta_,           t1

              or        outa,                   EnablePin_mask                  'Pre-Set Enable pin HIGH
              or        dira,                   EnablePin_mask                  'Set Enable pin as an OUTPUT

              call      #ClockTheEnable

              mov       t4,                     #4                              'Reset Compass
              mov       t3,                     Reset
              call      #SHIFTOUT

              call      #ClockTheEnable

              mov       t4,                     #4                              'Start Compass Measurement
              mov       t3,                     Measure
              call      #SHIFTOUT

Status        call      #ClockTheEnable

              mov       t4,                     #4                              'Get Status
              mov       t3,                     Report
              call      #SHIFTOUT

              mov       t4,                     #4
              call      #SHIFTIN

              and       t3,                     #$0F

              sub       Report,                 t3    wz,nr                     'Exit loop when status is ready
    if_nz     jmp      #Status

              mov       t4,                     #11                             'Read Compass x value
              call      #SHIFTIN
              and       t3,                     DataMask
              mov       x_,                     t3

              mov       t4,                     #11                             'Read Compass y value
              call      #SHIFTIN
              and       t3,                     DataMask
              mov       y_,                     t3

              call #ClockTheEnable

              test      x_,                     TestMask   wz
    if_nz     or        x_,                     NegMask

              test      y_,                     TestMask   wz
    if_nz     or        y_,                     NegMask

              neg       y_,                     y_


              mov       cx,                     x_
              mov       cy,                     y_
              call      #cordic
              shr       ca,                     #19
              mov       theta_,                 ca

              mov       t1,                     HM55B_x_                        '     Write x data back
              wrlong    x_,                     t1

              mov       t1,                     HM55B_y_                        '     Write y data back
              wrlong    y_,                     t1

              mov       t1,                     HM55B_theta_                    '     Write theta data back
              wrlong    theta_,                 t1

              jmp       #HM55B
'------------------------------------------------------------------------------------------------------------------------------
SHIFTOUT                                                                        'SHIFTOUT Entry
              andn      outa,                   DataPin_mask                    'Pre-Set Data pin LOW
              or        dira,                   DataPin_mask                    'Set Data pin as an OUTPUT

              andn      outa,                   ClockPin_mask                   'Pre-Set Clock pin LOW
              or        dira,                   ClockPin_mask                   'Set Clock pin as an OUTPUT
MSBFIRST_                                                                       '     Send Data MSBFIRST
              mov       t5,                     #%1                             '          Create MSB mask     ;     load t5 with "1"
              shl       t5,                     t4                              '          Shift "1" N number of bits to the left.
              shr       t5,                     #1                              '          Shifting the number of bits left actually puts
                                                                                '          us one more place to the left than we want. To
                                                                                '          compensate we'll shift one position right.
MSB_Sout      test      t3,                     t5      wc                      '          Test MSB of DataValue
              muxc      outa,                   DataPin_mask                    '          Set DataBit HIGH or LOW
              shr       t5,                     #1                              '          Prepare for next DataBit
              call      #Clk                                                      '          Send clock pulse
              djnz      t4,                     #MSB_Sout                       '          Decrement t4 ; jump if not Zero
              andn      outa,                   DataPin_mask                    '          Force DataBit LOW
SHIFTOUT_ret  ret
'------------------------------------------------------------------------------------------------------------------------------
SHIFTIN                                                                         'SHIFTIN Entry
              andn      dira,                   DataPin_mask                    'Set Data pin as an INPUT

              andn      outa,                   ClockPin_mask                   'Pre-Set Clock pin LOW
              or        dira,                   ClockPin_mask                   'Set Clock pin as an OUTPUT
MSBPOST_                                                                        '     Receive Data MSBPOST
MSBPOST_Sin   call      #Clk                                                      '          Send clock pulse
              test      DataPin_mask,           ina     wc                      '          Read Data Bit into 'C' flag
              rcl       t3,                     #1                              '          rotate "C" flag into return value
              djnz      t4,                     #MSBPOST_Sin                    '          Decrement t4 ; jump if not Zero
SHIFTIN_ret   ret
'------------------------------------------------------------------------------------------------------------------------------
Clk           or        outa,                   ClockPin_mask                   '          Set ClockPin HIGH
              andn      outa,                   ClockPin_mask                   '          Set ClockPin LOW
Clk_ret       ret
'------------------------------------------------------------------------------------------------------------------------------
ClockTheEnable
              or        outa,                   EnablePin_mask                  '          Set EnablePin HIGH
              andn      outa,                   EnablePin_mask                  '          Set EnablePin LOW
ClockTheEnable_ret      ret
'------------------------------------------------------------------------------------------------------------------------------
' Perform CORDIC cartesian-to-polar conversion

cordic        abs       cx,cx           wc
        if_c  neg       cy,cy
              mov       ca,#0
              rcr       ca,#1

              movs      :lookup,#table
              mov       t1,#0
              mov       t2,#20

:loop         mov       dx,cy           wc
              sar       dx,t1
              mov       dy,cx
              sar       dy,t1
              sumc      cx,dx
              sumnc     cy,dy
:lookup       sumc      ca,table

              add       :lookup,#1
              add       t1,#1
              djnz      t2,#:loop
cordic_ret    ret

table         long    $20000000
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
'------------------------------------------------------------------------------------------------------------------------------
' Initialized data

Reset         long    %0000
Measure       long    %1000
Report        long    %1100

DataMask      long    %00000000_00000000_00000111_11111111

TestMask      long    %00000000_00000000_00000010_00000000
NegMask       long    %11111111_11111111_11111100_00000000

x_            long    0
y_            long    0
theta_        long    0

' Uninitialized data

t1            res     1
t2            res     1
t3            res     1
t4            res     1
t5            res     1

EnablePin_mask res    1
ClockPin_mask res     1
DataPin_mask  res     1
HM55B_x_      res     1
HM55B_y_      res     1
HM55B_theta_  res     1

dx            res     1
dy            res     1
cx            res     1
cy            res     1
ca            res     1

