{{
***************************************
*        Ping))) Object V1.2          *
* Author:  Chris Savage & Jeff Martin *
* Copyright (c) 2006 Parallax, Inc.   *
* See end of file for terms of use.   *
* Started: 05-08-2006                 *
***************************************

Interface to Ping))) sensor and measure its ultrasonic travel time.  Measurements can be in units of time or distance.
Each method requires one parameter, Pin, that is the I/O pin that is connected to the Ping)))'s signal line.

  ┌───────────────────┐
  │┌───┐         ┌───┐│    Connection To Propeller
  ││ ‣ │ PING))) │ ‣ ││    Remember PING))) Requires
  │└───┘         └───┘│    +5V Power Supply
  │    GND +5V SIG    │
  └─────┬───┬───┬─────┘
        │  │    3.3K
          └┘   └ Pin

--------------------------REVISION HISTORY--------------------------
 v1.2 - Updated 06/13/2011 to change SIG resistor from 1K to 3.3K
 v1.1 - Updated 03/20/2007 to change SIG resistor from 10K to 1K

}}

CON

  TO_IN = 73_746                                                                ' Inches
  TO_CM = 29_034                                                                ' Centimeters


PUB Ticks(Pin) : Microseconds | cnt1, cnt2
''Return Ping)))'s one-way ultrasonic travel time in microseconds

  outa[Pin]~                                                                    ' Clear I/O Pin
  dira[Pin]~~                                                                   ' Make Pin Output
  outa[Pin]~~                                                                   ' Set I/O Pin
  outa[Pin]~                                                                    ' Clear I/O Pin (> 2 µs pulse)
  dira[Pin]~                                                                    ' Make I/O Pin Input
  waitpne(0, |< Pin, 0)                                                         ' Wait For Pin To Go HIGH
  cnt1 := cnt                                                                   ' Store Current Counter Value
  waitpeq(0, |< Pin, 0)                                                         ' Wait For Pin To Go LOW
  cnt2 := cnt                                                                   ' Store New Counter Value
  Microseconds := (||(cnt1 - cnt2) / (clkfreq / 1_000_000)) >> 1                ' Return Time in µs


PUB Inches(Pin) : Distance
''Measure object distance in inches

  Distance := Ticks(Pin) * 1_000 / TO_IN                                        ' Distance In Inches


PUB Centimeters(Pin) : Distance
''Measure object distance in centimeters

  Distance := Millimeters(Pin) / 10                                             ' Distance In Centimeters


PUB Millimeters(Pin) : Distance
''Measure object distance in millimeters

  Distance := Ticks(Pin) * 10_000 / TO_CM                                       ' Distance In Millimeters

