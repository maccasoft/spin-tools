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
CON

  _CLKMODE = XTAL1 + PLL16X
  _XINFREQ = 5_000_000

        CS = 0
       DIO = 1
       CLK = 2

VAR

    long vref,x,y,z,ThetaA,ThetaB,ThetaC

OBJ
    H48C  :     "sensor.accel.tri.h48c"
    VGA   :     "display.vga.text"

PUB DEMO_TEST

  'start VGA terminal
  VGA.start(16)

  'start and setup Accelerometer
  H48C.start(CS,DIO,CLK)

  VGA.out($01)
  VGA.str(string("H48C Tri-Axis Accelerometer"))

  VGA.str(string($A,4,$B,4))
  VGA.str(string("Vref ="))

  VGA.str(string($A,7,$B,6))
  VGA.str(string("X ="))
  VGA.str(string($A,7,$B,7))
  VGA.str(string("Y ="))
  VGA.str(string($A,7,$B,8))
  VGA.str(string("Z ="))

  VGA.str(string($A,1,$B,10))
  VGA.str(string("Theta A ="))
  VGA.str(string($A,1,$B,11))
  VGA.str(string("Theta B ="))
  VGA.str(string($A,1,$B,12))
  VGA.str(string("Theta C ="))

  repeat
     'vref := (H48C.vref*825)/1024   '<-- Here's how to get vref in mV

     vref := H48C.vref               '<-- Here's how to get vref in RAW


'Note: The returned value for X, Y, and Z is equal to the axis - Vref
        x := H48C.x   '<-- Here's how to get x
        y := H48C.y   '<-- Here's how to get y
        z := H48C.z   '<-- Here's how to get z

'Note: The returned value is in Deg (0-359)
'      remove the '*45)/1024' to return the 13-Bit Angle
   ThetaA := (H48C.thetaA*45)/1024   '<-- ThetaA is the angle relationship between X and Y
   ThetaB := (H48C.thetaB*45)/1024   '<-- ThetaB is the angle relationship between X and Z
   ThetaC := (H48C.thetaC*45)/1024   '<-- ThetaC is the angle relationship between Y and Z

    VGA.str(string($A,11,$B,4))
    VGA.dec(vref)
    VGA.str(string("   "))

    VGA.str(string($A,11,$B,6))
    VGA.dec(x)
    VGA.str(string("  "))
    VGA.str(string($A,11,$B,7))
    VGA.dec(y)
    VGA.str(string("  "))
    VGA.str(string($A,11,$B,8))
    VGA.dec(z)
    VGA.str(string("  "))

    VGA.str(string($A,11,$B,10))
    VGA.dec(ThetaA)
    VGA.str(string("  "))
    VGA.str(string($A,11,$B,11))
    VGA.dec(ThetaB)
    VGA.str(string("  "))
    VGA.str(string($A,11,$B,12))
    VGA.dec(ThetaC)
    VGA.str(string("  "))

DAT
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
