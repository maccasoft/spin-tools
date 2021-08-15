{{
**************************************************************
* Inductive Proximity Sensor Part1 Demo                 v1.0 *
* Author: Beau Schwabe                                       *
* Copyright (c) 2008 Parallax                                *
* See end of file for terms of use.                          *
**************************************************************

Inductive Proximity Sensor Part1.Spin


Schematic:

 { Series RLC }{ Peak Detector }{ Voltage Divider }{ Sigma Delta ADC }

     C1               D1                                  +3.3V
 P7 ──┳──────────────┳───────────────┐                  
         R1             │                R2              C3
        │               C2            ┣──────────────────╋────┳──── P0
         L1             │                R3              C4 │
                                                             └── P1
                                                                  R4

 C1    - 10pF
 C2    - 0.01µF
 C3,C4 - 220pF
 D1    - 1N914
 L1    - Sense Coil (25 Turns 30 Gauge air-core style 14.5mm diameter form)
 R1    - 100Ω
 R2,R4 - 1Meg
 R3    - 220K

 Note:
        - R1 limits the current of the L1 and C1 and forms a Series RLC circuit

        - D1 and C2 form a pseudo Peak Detector generating voltages as high as 21V Pk-Pk
          at the D1-C2 junction when R1,L1, and C1 are at the resonant frequency from P7.

        - The voltage divider (R2 and R3) brings the voltage down by a factor of 6.5 so
          21V becomes 3.23V

        - R4,C3, and C4 form the Sigma Delta ADC hardware

}}
CON

  _XINFREQ = 5_000_000          'Propeller Processor Crystal value (5MHz)
  _CLKMODE = XTAL1 + PLL16X     'Set PLL value to X16 to get an 80MHz (5MHz x 16 = 80MHz) clock

  SensePin = 0                  'ADC INPUT pin
  DrivePin = 1                  'ADC OUTPUT pin
  FPin     = 7                  'Frequency Synthesizer OUTPUT pin

  StartFrequency = 8_00_000     'Start Frequency to Sweep ... 500 kHz to 128 MHz
  StopFrequency = 10_000_000    'Stop Frequency to Sweep ... 500 kHz to 128 MHz
  SweepStep = 25_000            'Sweep increment used in auto calibration

VAR
  long                Frequency,FMax,ADCmax,Temp,Scan1,Scan2,Blink,n,Sample

PUB Proximity_Sensor_Demo
{{
##########################################################################################
##########################################################################################
 Auto Calibration Section:
                    Make sure that coil is absent of any metal, or that the metal object
                    approaching the coil is at it's furthest point from the coil.
##########################################################################################
##########################################################################################
}}
    cognew(@asm_ADC, @Sample)                           'launch Sigma Delta ADC ; uses CTRA

    FMax~                                               'Clear Fmax
    ADCmax~                                             'Clear ADCmax
    repeat Frequency from StartFrequency to StopFrequency step SweepStep        'Sweep frequency

      Synth(FPin, Frequency)                            'set oscillator ; uses CTRB
      waitcnt(cnt+clkfreq>>10)                          'Delay ; Allow ADC to settle
                                                        'approx 1/1000th of a second

      if Sample > ADCmax                                'Detect 'peak' voltage value from ADC
         ADCmax := Sample                               'this will be the resonant frequency
         FMax := Frequency                              'of the RLC

{{
##########################################################################################
##########################################################################################
 Main DEMO Program:
                    This DEMO uses eight LED's connected on P16 to P23 to visually
                    demonstrate an inductive proximity sensor.
##########################################################################################
##########################################################################################
}}
    Synth(FPin, FMax)                                   'set the oscillator to the resonate
                                                        'frequency of the RLC circuit ; uses CTRB

    dira[16..23] ~~                                     'Set I/O direction of LED's to output

    repeat                                              'Main Loop

        Temp := Sample                                  'Read current ADC value

        Scan1 := Temp / 412                             'Set COARSE LED scale as Scan1:
                                                        '  Maximum ADC value is 3300 (set below)
                                                        '....so 3300 / 8 Leds = 412

        outa[16..23] := |< Scan1                        'Turn on 1 of 8 LED's based on ADC value

        Scan2 := (Temp - (Scan1 * 412))/51              'Set FINE LED scale as Scan2:

        n++                                             ''Used for blinking LED ; increment n
        if n > 2500                                     'if n > 2500 then clear n, and toggle
           n := 0                                       'Blink variable
           Blink := 1 - Blink

        outa[23..16] |= (|< Scan2)* Blink               'Turn on 1 of 8 LED's based on ADC value


{{
Note:
- The COARSE indicator is a single solid LED in the array that moves from left to right as a metal
  object is brought closer in proximity to the sense coil.

- The FINE indicator blinks a single LED in the array and moves it from right to left, repeating the
  sequence for each COARSE indicator position, as the metal object is brought closer in proximity to
  the sense coil.
}}
PUB Synth(_Pin, Freq) | s, d, ctr, frq
{{
##########################################################################################
##########################################################################################
 Section used to Synthesize a specific frequency on an I/O pin:
##########################################################################################
##########################################################################################
}}
    Freq := Freq #> 500_000 <# 128_000_000              'limit frequency range

    ctr := constant(%00010 << 26)                       '..set PLL mode
    d := >|((Freq - 1) / 1_000_000)                     'determine PLLDIV
    s := 4 - d                                          'determine shift
    ctr |= d << 23                                      'set PLLDIV

    FRQB := fraction(Freq, CLKFREQ, s)                  'Compute FRQB value
    CTRB := ctr | _Pin                                  'set PINA to complete CTRB value
    DIRA[_Pin]~~                                        'make pin output

PRI fraction(a, b, shift) : f
    if shift > 0                                        'if shift, pre-shift a or b left
      a <<= shift                                       'to maintain significant bits while
    if shift < 0                                        'insuring proper result
      b <<= -shift
    repeat 32                                           'perform long division of a/b
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
              or        dira,DrivePinMask               'make DrivePin an output

              movs      ctra,#SensePin
              movd      ctra,#DrivePin
              movi      ctra,#%01001_000                'POS W/FEEDBACK mode for CTRA
              mov       frqa,#1

              mov       count,cnt                       'prepare for WAITCNT loop
              add       count,cycles
:loop
              mov       phsa, #0                        'clear PHSA
              waitcnt   count,cycles                    'wait for next CNT value
              mov       _sample,phsa                    'move PHSA into sample
              wrlong    _sample,par                     'write sample back to Spin variable "sample"
              jmp       #:loop                          'wait for next sample

cycles        long      3300                            'VDD Voltage level (mV)
DrivePinMask  long      |< DrivePin                     'output mask
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
