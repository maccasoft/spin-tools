'' =================================================================================================
''
''   File....... jm_io.spin
''   Purpose.... Basic IO control
''   Author..... Jon "JonnyMac" McPhalen
''               Copyright (c) 2014-2022 Jon McPhalen
''               -- see below for terms of use
''   E-mail..... jon.mcphalen@gmail.com
''   Started....
''   Updated.... 07 JAN 2022
''
'' =================================================================================================


con { fixed io pins }

  RX_PGM = 31  { I }                                            ' serial / programming
  TX_PGM = 30  { O }

  SDA_EE = 29  { I/O }                                          ' i2c / eeprom
  SCL_EE = 28  { I/O }


pub null

'' This is not a top-level object


pub start(pmask, dmask)

'' Setup pins using pins and directions masks

  outa := pmask
  dira := dmask


pub high(pin)                                                   ' like P2 pinhigh() for one pin

'' Makes pin output and high

  outa[pin] := 1
  dira[pin] := 1


pub low(pin)                                                    ' like P2 pinlow() for one pin

'' Makes pin output and low

  outa[pin] := 0
  dira[pin] := 1


pub toggle(pin)                                                 ' like P2 pintoggle() for one pin

'' Toggles pin state

  !outa[pin]
  dira[pin] := 1


pub set(pin, value)                                             ' like P2 pinwrite() for one pin

'' Makes pin output and set to value.0

  outa[pin] := value
  dira[pin] := 1


pub input(pin)                                                  ' like P2 pinfloat() + pinread() for one pin

'' Makes pin input and returns current state (0..1)

  dira[pin] := 0

  return ina[pin]


pub pulse_in(px, state)

'' Returns pulse input on state
'' -- pulse should be less thant 26s at 80MHz
'' -- px is input pin, 0..27
'' -- state is 1 for high pulse, 0 for low pulse
'' -- width returned in system ticks

  if (ctra == 0)
    dira[px] := 0                                               ' force pin to input mode
    frqa := 1
    if (state)
      ctra := (%01000 << 26) | px                               ' set ctra to POS detect on pin
      waitpne(1 << px, 1 << px, 0)                              ' wait for pin to be low
      phsa := 0                                                 ' clear accumulator
      waitpeq(1 << px, 1 << px, 0)                              ' wait for pin to go high
      waitpne(1 << px, 1 << px, 0)                              ' wait for pin to go low
    else
      ctra := (%01100 << 26) | px                               ' set ctra for NEG detect on pin
      waitpeq(1 << px, 1 << px, 0)
      phsa := 0
      waitpne(1 << px, 1 << px, 0)
      waitpeq(1 << px, 1 << px, 0)
    ctra := 0                                                   ' clear counter
    return phsa                                                 ' return pulse width in ticks

  elseif (ctrb == 0)
    dira[px] := 0
    frqb := 1
    if (state)
      ctrb := (%01000 << 26) | px
      waitpne(1 << px, 1 << px, 0)
      phsb := 0
      waitpeq(1 << px, 1 << px, 0)
      waitpne(1 << px, 1 << px, 0)
    else
      ctrb := (%01100 << 26) | px
      waitpeq(1 << px, 1 << px, 0)
      phsb := 0
      waitpne(1 << px, 1 << px, 0)
      waitpeq(1 << px, 1 << px, 0)
    ctrb := 0
    return phsb

  else
    return -1                                                   ' no counter available


pub pulse_out(px, us) | pwtix, state

'' Generate pulse on pin px for us microseconds
'' -- px is output pin, 0..27
'' -- us is width of output pulse in microseconds
'' -- uses first available counter
''    * returns -1 if not counter available
'' -- pulse output is opposite of input state

  pwtix := us * (clkfreq / 1_000_000)                           ' pulse width in ticks
  state := ina[px]                                              ' read incoming state of pin

  if (ctra == 0)                                                ' ctra available?
    if (state == 0)                                             ' low-high-low
      low(px)                                                   ' set to output
      frqa := 1
      phsa := -pwtix                                            ' set timing
      ctra := (%00100 << 26) | px                               ' start the pulse
      repeat
      until (phsa => 0)                                         ' let pulse finish

    else                                                        ' high-low-high
      high(px)
      frqa := -1
      phsa := pwtix
      ctra := (%00100 << 26) | px
      repeat
      until (phsa < 0)

    ctra := 0                                                   ' release counter
    return us

  elseif (ctrb == 0)
    if (state == 0)
      low(px)
      frqb := 1
      phsb := -pwtix
      ctrb := (%00100 << 26) | px
      repeat
      until (phsb => 0)

    else
      high(px)
      frqb := -1
      phsb := pwtix
      ctrb := (%00100 << 26) | px
      repeat
      until (phsb < 0)

    ctrb := 0
    return us

  else
    return -1                                                   ' alert user of error


pub freq_out(ctrx, px, fx)

'' Sets ctrx to frequency fx on pin px (NCO/SE mode)
'' -- ctrx is counter to use:
''    * "a", "A", 0 for ctra
''    * "b", "B", 1 for ctrb
'' -- px is output pin, 0..27
'' -- fx in 0.1Hz units (100_0 = 100Hz)
''    * 0 to stop counter that is running

  if (fx > 0)
    fx := ($4000_0000 / ((clkfreq >> 2) / fx))                  ' convert freq for NCO mode
    fx := (fx + 5) / 10                                         ' round up if needed

    case ctrx
      0, "a", "A":
        ctra := (%00100 << 26) | px                             ' configure ctra for NCO on pin
        frqa := fx                                              ' set frequency
        dira[px] := 1                                           ' make pin an output

      1, "b", "B":
        ctrb := (%00100 << 26) | px
        frqb := fx
        dira[px] := 1

  else
    case ctrx
      0, "a", "A":
        ctra := 0                                               ' disable counter
        outa[px] := 0                                           ' clear pin/driver
        dira[px] := 0

      1, "b", "B":
        ctrb := 0
        outa[px] := 0
        dira[px] := 0


con { license }

{{

  Terms of Use: MIT License

  Permission is hereby granted, free of charge, to any person obtaining a copy of this
  software and associated documentation files (the "Software"), to deal in the Software
  without restriction, including without limitation the rights to use, copy, modify,
  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
  permit persons to whom the Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be included in all copies
  or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
  PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
  CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


  Note from JonnyMac:

  If you intend to modify and re-publish, kindly take my name off of this code so that I
  don't get credit for your good work, nor get blamed for your mistakes.

}}
