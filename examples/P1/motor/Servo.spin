{{
*****************************************
* Servo32_v7 Demo                    v7 *
* Author: Beau Schwabe                  *
* Copyright (c) 2009 Parallax           *
* See end of file for terms of use.     *
*****************************************
}}

CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000                                'Note Clock Speed for your setup!!

    ServoCh1 = 0                                        'Select DEMO servo

VAR

OBJ
  SERVO : "motor.servo"

PUB Servo32_DEMO | temp

    SERVO.Start                 'Start Servo handler
    SERVO.Ramp  '<-OPTIONAL     'Start Background Ramping

                                'Note: Ramping requires another COG
                                '      If ramping is not started, then
                                '      'SetRamp' commands within the
                                '      program are ignored
                                '
                                'Note: At ANY time, the 'Set' command overides
                                '      the servo position.  To 'Ramp' from the
                                '      current position to the next position,
                                '      you must use the 'SetRamp' command

'         'Set(Pin, Width)
    SERVO.Set(ServoCh1,1500)                  'Move Servo to Center

          'SetRamp(Pin, Width,Delay)<-- 100 = 1 sec 6000 = 1 min
    SERVO.SetRamp(ServoCh1,2000,200)          'Pan Servo

    repeat 1500000                            'Do nothing here just to wait for
                                              'background ramping to complete

    SERVO.SetRamp(ServoCh1,1000,50)           'Pan Servo

    repeat 800000                             'Do nothing here just to wait for
                                              'background ramping to complete


    SERVO.Set(ServoCh1,1500)                  'Force Servo to Center

{{
    Note: It is no longer necessary to preset the servo before starting the Servo32 object, but for backwards compatibility
          the old method will still work just fine.

          This change was made with a slight modification to the Servo32 object which allows you to disable a servo Channel
          after it has been started.

          To disable a servo channel simply specify the servo width that has a value outside of the allowed range.  The default
          for this range is set between 500us and 2500us



}}

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
