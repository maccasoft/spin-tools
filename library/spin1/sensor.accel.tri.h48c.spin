{{
*********************************************
* H48C Tri-Axis Accelerometer VGA_DEMO V1.1 *
* Author: Beau Schwabe                      *
* Copyright (c) 2008 Parallax               *
* See end of file for terms of use.         *
*********************************************

Revision History:

Version 1.0 - (Sept. 2006) - Initial release with a TV mode 3D-graphics cube
Version 1.1 - (March 2008) - 3D-graphics cube removed
                           - Basic VGA mode display used instead of TV
                           - Added 600nS padding delay around Clock rise and fall times
 }}
{

     220Ω  ┌──────────┐
  P2 ──│1 ‣‣••6│── +5V       P0 = CS
     220Ω  │  ┌°───┐  │ 220Ω          P1 = DIO
  P1 ──│2 │ /\ │ 5│── P0      P2 = CLK
           │  └────┘  │ 220Ω
   VSS ──│3  4│── Zero-G
           └──────────┘

Note1: Zero-G output not used in this demo

Note2: orientation

         Z   Y    
         │  /    /   °/  reference mark on H48C Chip, not white dot on 6-Pin module
         │ /    /    /
         │/     o   white reference mark on 6-Pin module indicating Pin #1
          ──── X

       ThetaA - Angle relation between X and Y
       ThetaB - Angle relation between X and Z
       ThetaC - Angle relation between Z and Y



Note3: The H48C should be powered with a 5V supply.  It has an internal regulator
       that regulates the voltage down to 3.3V where Vref is set to 1/2 of the 3.3V
       In this object, the axis is already compensated with regard to Vref. Because
       of this, the formulas are slightly different (simplified) compared to what is
       stated in the online documentation.

G = ( axis / 4095 ) x ( 3.3 / 0.3663 )

        or

G = axis x 0.0022

        or

G = axis / 455


An expected return value from each axis would range between ±1365.

i.e.
 ±455 would represent ±1g
 ±910 would represent ±2g
±1365 would represent ±3g

}
VAR
long    cog,CS,DIO,CLK,H48C_vref,H48C_x,H48C_y,H48C_z,H48C_thetaA,H48C_thetaB,H48C_thetaC

PUB stop
'' Stop driver - frees a cog
    if cog
       cogstop(cog~ -  1)

PUB start(CS_,DIO_,CLK_): okay
    CS  := CS_
    DIO := DIO_
    CLK := CLK_

'' Start driver - starts a cog
'' returns false if no cog available

    okay := cog := cognew(@H48C, @CS)

PUB vref
    return H48C_vref
PUB x
    return H48C_x
PUB y
    return H48C_y
PUB z
    return H48C_z
PUB thetaA
    return H48C_thetaA
PUB thetaB
    return H48C_thetaB
PUB thetaC
    return H48C_thetaC
DAT
H48C          org

              mov       t1,                     par                             'Setup CS pin mask
              rdlong    t2,                     t1
              mov       CSPin_mask,             #1
              shl       CSPin_mask,             t2

              add       t1,                     #4                              'Setup DIO pin mask
              rdlong    t2,                     t1
              mov       DIOPin_mask,            #1
              shl       DIOPin_mask,            t2

              add       t1,                     #4                              'Setup CLK pin mask
              rdlong    t2,                     t1
              mov       CLKPin_mask,            #1
              shl       CLKPin_mask,            t2

              add       t1,                     #4                              'Get variable Adress location for Vref
              mov       H48C_vref_,             t1

              add       t1,                     #4                              'Get variable Adress locations for X,Y,Z  g's
              mov       H48C_x_,                t1
              add       t1,                     #4
              mov       H48C_y_,                t1
              add       t1,                     #4
              mov       H48C_z_,                t1

              add       t1,                     #4                              'Get variable Adress locations for X,Y,Z Angle's
              mov       H48C_thetaA_,           t1
              add       t1,                     #4
              mov       H48C_thetaB_,           t1
              add       t1,                     #4
              mov       H48C_thetaC_,           t1

              or        outa,                   CSPin_mask                      'Pre-Set CS pin HIGH
              or        dira,                   CSPin_mask                      'Set CS pin as an OUTPUT

              mov       t3,                     VoltRef                         'Get vRef value
              call      #DataIO
              mov       vref_,                  t3

              mov       t3,                     Xselect                         'Get X value
              call      #DataIO
              mov       x_,                     t3
              subs      x_,                     vref_

              mov       t3,                     Yselect                         'Get Y value
              call      #DataIO
              mov       y_,                     t3
              subs      y_,                     vref_

              mov       t3,                     Zselect                         'Get Z value
              call      #DataIO
              mov       z_,                     t3
              subs      z_,                     vref_

              mov       cx,                     x_                              'Get theta A from X&Y
              mov       cy,                     y_
              call      #cordic
              mov       thetaA_,                ca

              mov       cx,                     x_                              'Get theta B from X&Z
              mov       cy,                     z_
              call      #cordic
              mov       thetaB_,                ca

              mov       cx,                     z_                              'Get theta C from Z&Y
              mov       cy,                     y_
              call      #cordic
              mov       thetaC_,                ca

              mov       t1,                     H48C_vref_                      'Write Vref data back
              wrlong    vref_,                  t1
              mov       t1,                     H48C_x_                         'Write x data back
              wrlong    x_,                     t1
              mov       t1,                     H48C_y_                         'Write y data back
              wrlong    y_,                     t1
              mov       t1,                     H48C_z_                         'Write z data back
              wrlong    z_,                     t1
              mov       t1,                     H48C_thetaA_                    'Write theta A back
              wrlong    thetaA_,                t1
              mov       t1,                     H48C_thetaB_                    'Write theta B back
              wrlong    thetaB_,                t1
              mov       t1,                     H48C_thetaC_                    'Write theta C back
              wrlong    thetaC_,                t1

              jmp       #H48C
'------------------------------------------------------------------------------------------------------------------------------
DataIO                                                                          'Select DAC register and read data
              andn      outa,                   CSPin_mask                      '     Make CS pin LOW         (Select the device)
              mov       t4,                     #5                              '          Set Num of Bits
              call      #SHIFTOUT                                               '          Select DAC register
              mov       t4,                     #13                             '          Set Num of Bits
              call      #SHIFTIN                                                '          Read DAC register data
              or        outa,                   CSPin_mask                      '     Make CS pin HIGH       (Deselect the device)
              and       t3,                     DataMask
DataIO_ret    ret
'------------------------------------------------------------------------------------------------------------------------------
SHIFTOUT                                                                        'SHIFTOUT Entry
              andn      outa,                   DIOPin_mask                     'Pre-Set Data pin LOW
              or        dira,                   DIOPin_mask                     'Set Data pin as an OUTPUT

              andn      outa,                   CLKPin_mask                     'Pre-Set Clock pin LOW
              or        dira,                   CLKPin_mask                     'Set Clock pin as an OUTPUT
MSBFIRST_                                                                       '     Send Data MSBFIRST
              mov       t5,                     #1                             '          Create MSB mask     ;     load t5 with "1"
              shl       t5,                     t4                              '          Shift "1" N number of bits to the left.
              shr       t5,                     #1                              '          Shifting the number of bits left actually puts
                                                                                '          us one more place to the left than we want. To
                                                                                '          compensate we'll shift one position right.
MSB_Sout      test      t3,                     t5      wc                      '          Test MSB of DataValue
              muxc      outa,                   DIOPin_mask                     '          Set DataBit HIGH or LOW
              shr       t5,                     #1                              '          Prepare for next DataBit
              call      #Clock                                                  '          Send clock pulse
              djnz      t4,                     #MSB_Sout                       '          Decrement t4 ; jump if not Zero
              andn      outa,                   DIOPin_mask                     '          Force DataBit LOW
SHIFTOUT_ret  ret
'------------------------------------------------------------------------------------------------------------------------------
SHIFTIN                                                                         'SHIFTIN Entry
              andn      dira,                   DIOPin_mask                     'Set Data pin as an INPUT

              andn      outa,                   CLKPin_mask                     'Pre-Set Clock pin LOW
              or        dira,                   CLKPin_mask                     'Set Clock pin as an OUTPUT
MSBPOST_                                                                        '     Receive Data MSBPOST
MSBPOST_Sin   call      #Clock                                                  '          Send clock pulse
              test      DIOPin_mask,            ina     wc                      '          Read Data Bit into 'C' flag

              rcl       t3,                     #1                              '          rotate "C" flag into return value
              djnz      t4,                     #MSBPOST_Sin                    '          Decrement t4 ; jump if not Zero
SHIFTIN_ret   ret
'------------------------------------------------------------------------------------------------------------------------------
Clock         Call      #Delay600nS
              or        outa,                   CLKPin_mask                     'Set ClockPin HIGH
              Call      #Delay600nS
              andn      outa,                   CLKPin_mask                     'Set ClockPin LOW
              Call      #Delay600nS
Clock_ret     ret

Delay600nS    'Wait a total of 48 Clocks including the initial CALL entry command
              mov       t2,     cnt             'CALL entry plus this line takes 8 Clocks
              add       t2,     #32             '4 Clocks
              waitcnt   t2,     #0              '5+(27) Clocks
Delay600nS_ret ret                              '4 Clocks

'------------------------------------------------------------------------------------------------------------------------------
' Perform CORDIC cartesian-to-polar conversion

'Input = cx(x) and cy(x)
'Output = cx(ro) and ca(theta)

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

              shr       ca,                     #19

cordic_ret    ret

table         long    $20000000, $12E4051E, $09FB385B, $051111D4, $028B0D43
              long    $0145D7E1, $00A2F61E, $00517C55, $0028BE53, $00145F2F
              long    $000A2F98, $000517CC, $00028BE6, $000145F3, $0000A2FA
              long    $0000517D, $000028BE, $0000145F, $00000A30, $00000518

'------------------------------------------------------------------------------------------------------------------------------
' Initialized data

                 '     ┌───── Start Bit
                 '     │┌──── Single/Differential Bit
                 '     ││┌┳┳─ Channel Select
                 '     
Xselect       long    %11000    'DAC Control Code
Yselect       long    %11001    'DAC Control Code
Zselect       long    %11010    'DAC Control Code
VoltRef       long    %11011    'DAC Control Code

DataMask      long    $1FFF     '13-Bit data mask

' Uninitialized data
x_            long    0
y_            long    0
z_            long    0
thetaA_       long    0
thetaB_       long    0
thetaC_       long    0
vref_         long    0

t1            res     1         'temp
t2            res     1         'temp
t3            res     1         'temp
t4            res     1         'temp
t5            res     1         'temp

CSPin_mask    res     1         'IO pin mask
DIOPin_mask   res     1         'IO pin mask
CLKPin_mask   res     1         'IO pin mask

H48C_vref_    res     1         'variable address location                      Arg3

H48C_x_       res     1         'variable address location                      Arg4
H48C_y_       res     1         'variable address location                      Arg5
H48C_z_       res     1         'variable address location                      Arg6

H48C_thetaA_  res     1         'variable address location                      Arg7
H48C_thetaB_  res     1         'variable address location                      Arg8
H48C_thetaC_  res     1         'variable address location                      Arg9

dx            res     1         'cordic temp variable
dy            res     1         'cordic temp variable
cx            res     1         'cordic temp variable
cy            res     1         'cordic temp variable
ca            res     1         'cordic temp variable

