{{
**************************************************************
* Inductive Proximity Sensor Part2 Demo                 v1.0 *
* Author: Beau Schwabe                                       *
* Copyright (c) 2008 Parallax                                *
* See end of file for terms of use.                          *
**************************************************************

Inductive Proximity Sensor Part2.Spin


Schematic:

 { Series RLC }{ Peak Detector }{ Voltage Divider }{ Sigma Delta ADC }

     C1            D1
 P6 ──┳──────────────┐
         R1             │
        │                │
         L1             │
                        │
                         │
      C2           D2    │                                  +3.3V
 P7 ──┳──────────────╋───────────────┐                  
         R2             │                R3              C4
        │               C3            ┣──────────────────╋────┳──── P0
         L2             │                R4              C5 │
                                                             └── P1
                                                                   R5

 C1,C2 - 10pF
 C3    - 0.01µF
 C4,C5 - 220pF
 D1,D2 - 1N914
 L1,L2 - Sense Coil (25 Turns 30 Gauge air-core style 14.5mm diameter form)
 R1,R2 - 100Ω
 R3,R5 - 1Meg
 R4    - 220K

 Note:
        - R1,R2 limits the current of L1,L2.  Each inductor (L1 and L2) in combination
          with C1,C2 and form a Series RLC circuit

        - D1,D2 and C1,C2 form a pseudo Peak Detector generating voltages as high as 21V Pk-Pk
          at the D1-C3 and D2-C3 junction when R1,L1,C1 (or R2,L2,C2), are at the resonant frequency
          from P6 or P7.

        - The voltage divider (R3 and R4) brings the voltage down by a factor of 6.5 so
          21V becomes 3.23V

        - R5,C4, and C5 form the Sigma Delta ADC hardware

}}
CON

  _XINFREQ = 5_000_000          'Propeller Processor Crystal value (5MHz)
  _CLKMODE = XTAL1 + PLL16X     'Set PLL value to X16 to get an 80MHz (5MHz x 16 = 80MHz) clock

  SensePin = 0                  'ADC INPUT pin
  DrivePin = 1                  'ADC OUTPUT pin
  FPin1    = 6                  'Frequency Synthesizer OUTPUT pin 1
  FPin2    = 7                  'Frequency Synthesizer OUTPUT pin 2

  StartFrequency = 8_000_000     'Start Frequency to Sweep ... 500 kHz to 128 MHz
  StopFrequency = 10_000_000    'Stop Frequency to Sweep ... 500 kHz to 128 MHz
  SweepStep = 20_000            'Sweep increment used in auto calibration

VAR
  long  ADCmax,VMax1,VMax2,Fmax1,Fmax2,Scan1,Scan2,Sample

PUB Proximity_Sensor_Demo
{{
##########################################################################################
##########################################################################################
 Auto Calibration Section:
                    Make sure that the coin or metal is centered between each coil.
##########################################################################################
##########################################################################################
}}
    cognew(@asm_ADC, @Sample)                           ' launch Sigma Delta ADC ; uses CTRA


    Fmax1 := Calibrate_Coil(FPin1)                      ' Initiate calibration for coil 1
    VMax1 := ADCmax                                     ' Maximum voltage level at peak resonance
    Fmax2 := Calibrate_Coil(FPin2)                      ' Initiate calibration for coil 2
    VMax2 := ADCmax                                     ' Maximum voltage level at peak resonance

{{
##########################################################################################
##########################################################################################
 Main DEMO Program:
                    This DEMO uses eight LED's connected on P16 to P23 to visually
                    demonstrate a differential inductive proximity sensor.
##########################################################################################
##########################################################################################
}}
    dira[16..23] ~~                                     ' Set I/O direction of LED's to output

    repeat

      Synth(FPin1, Fmax1)                               ' Set the oscillator to the resonant frequency
      waitcnt(cnt+clkfreq>>6 )                          ' Delay ; Allow ADC to settle for 1/64th of a second
      Scan1 := Sample                                   ' Get ADC Sample


      Synth(FPin2, Fmax2)                               ' Set the oscillator to the resonant frequency
      waitcnt(cnt+clkfreq>>6 )                          ' Delay ; Allow ADC to settle for 1/64th of a second
      Scan2 := Sample                                   ' Get ADC Sample

      Scan1 := ((Scan1 * 8)/VMax1)                      ' Scale ADC value to range from 0 to 8
      Scan2 := 8-((Scan2 * 8)/VMax2)                    ' Scale ADC value to range from 0 to 8, but invert output

      outa[23..16] := |< ((Scan1 + Scan2)/2)            ' Turn on 1 of 8 LED's based on differential ADC value

PUB Calibrate_Coil(Pin)|Temp, Frequency

''Calibrate Coil
    result~                                             ' Clear Result
    ADCmax~                                             ' Clear ADCmax
    repeat Frequency from StartFrequency to StopFrequency step SweepStep        'Sweep frequency

      Synth(Pin, Frequency)                             ' set oscillator ; uses CTRB
      waitcnt(cnt+clkfreq>>6 )                          ' Delay ; Allow ADC to settle for 1/64th of a second

      Temp := 0                                         ' Average five ADC Samples
      repeat 5
        Temp += Sample
      Temp /= 5

      if Temp => ADCmax                                 ' Detect 'peak' voltage value from ADC
         ADCmax := Temp                                 ' this will be the resonant frequency
         result := Frequency                            ' of the RLC circuit

'' Determine "Mode 2" mid-point voltage

    repeat Frequency from result to StopFrequency step SweepStep                'Sweep frequency

      Synth(Pin, Frequency)                             ' set oscillator ; uses CTRB
      waitcnt(cnt+clkfreq>>5 )                          ' Delay ; Allow ADC to settle
                                                        ' 1/32th of a second

      Temp := 0
      repeat 5                                          ' Average five ADC Samples
        Temp += Sample
      Temp /= 5

      if (Temp*2) =< ADCmax                             ' Check mid-point voltage level for Mode 2
         result := Frequency
         quit



PUB Synth(_Pin, Freq) | s, d, ctr, frq
{{
##########################################################################################
##########################################################################################
 Section used to Synthesize a specific frequency on an I/O pin:
##########################################################################################
##########################################################################################
}}
    Freq := Freq #> 500_000 <# 128_000_000              ' limit frequency range

    ctr := constant(%00010 << 26)                       ' ..set PLL mode
    d := >|((Freq - 1) / 1_000_000)                     ' determine PLLDIV
    s := 4 - d                                          ' determine shift
    ctr |= d << 23                                      ' set PLLDIV

    FRQB := fraction(Freq, CLKFREQ, s)                  ' Compute FRQB value
    CTRB := ctr | _Pin                                  ' set PINA to complete CTRB value
    DIRA[_Pin]~~                                        ' make pin output

PRI fraction(a, b, shift) : f
    if shift > 0                                        ' if shift, pre-shift a or b left
      a <<= shift                                       ' to maintain significant bits while
    if shift < 0                                        ' insuring proper result
      b <<= -shift
    repeat 32                                           ' perform long division of a/b
      f <<= 1
      if a => b
        a -= b
        f++
      a <<= 1
DAT
{{
##########################################################################################
##########################################################################################
 Section used to setup a Sigma Delta ADC:
##########################################################################################
##########################################################################################
}}
asm_ADC       org
              or        dira,DrivePinMask               ' make DrivePin an output

              movs      ctra,#SensePin
              movd      ctra,#DrivePin
              movi      ctra,#%01001_000                ' POS W/FEEDBACK mode for CTRA
              mov       frqa,#1

              mov       count,cnt                       ' prepare for WAITCNT loop
              add       count,cycles
:loop
              mov       phsa, #0                        ' clear PHSA
              waitcnt   count,cycles                    ' wait for next CNT value
              mov       _sample,phsa                    ' move PHSA into sample
              wrlong    _sample,par                     ' write sample back to Spin variable "sample"
              jmp       #:loop                          ' wait for next sample

cycles        long      3300                            ' VDD Voltage level (mV)
DrivePinMask  long      |< DrivePin                     ' output mask
count         long      0
_sample       long      0

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
