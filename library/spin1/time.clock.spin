' Original author: Jeff Martin
{{
    This object is used to configure the system clock at run-time.

    Normally, clock mode and speed is fixed and set at compile-time with clock setting
    constants such as:

          _clkmode = xtal1 + pll16x   'standard mode and
          _xinfreq = 5_000_000        'frequency (80 MHz)

    However, the Propeller allows clock mode and speed changes at run-time for flexible power
    management.  However, the CLKSET command doesn't accept the same clock setting constants
    used by _CLKMODE, above, but rather takes the specific CLK Register values representing
    the desired mode.

    This object was written for those that would rather use the same clock setting constants
    they are familiar with from the _CLKMODE constant.
}}
CON

    WMIN = 381                                              ' WAITCNT-expression overhead minimum

    RCFAST_         = %0_0_0_00_000                         ' Clock mode constants used for the
    RCSLOW_         = %0_0_0_00_001                         ' SetMode method.  Each constant name
    XINPUT_         = %0_0_1_00_010                         ' represents the expression built of
    XTAL1_          = %0_0_1_01_010                         ' similar names from clock setting
    XTAL2_          = %0_0_1_10_010                         ' constants (without trailing "_" and
    XTAL3_          = %0_0_1_11_010                         ' with "+" instead of middle "_").
    XINPUT_PLL1X    = %0_1_1_00_011                         '
    XINPUT_PLL2X    = %0_1_1_00_100                         ' For example, calling the method
    XINPUT_PLL4X    = %0_1_1_00_101                         ' SetMode(XTAL1_PLL16X) changes the
    XINPUT_PLL8X    = %0_1_1_00_110                         ' clock mode to the same state at
    XINPUT_PLL16X   = %0_1_1_00_111                         ' run-time as the CON block statement
    XTAL1_PLL1X     = %0_1_1_01_011                         ' _clkmode = xtal1 + pll16x does at
    XTAL1_PLL2X     = %0_1_1_01_100                         ' compile-time.
    XTAL1_PLL4X     = %0_1_1_01_101                         '
    XTAL1_PLL8X     = %0_1_1_01_110                         ' Calling SetMode with one of these
    XTAL1_PLL16X    = %0_1_1_01_111                         ' constants is much faster than using
    XTAL2_PLL1X     = %0_1_1_10_011                         ' the legacy SetClock method from
    XTAL2_PLL2X     = %0_1_1_10_100                         ' previous versions of this Clock
    XTAL2_PLL4X     = %0_1_1_10_101                         ' object.
    XTAL2_PLL8X     = %0_1_1_10_110                         '
    XTAL2_PLL16X    = %0_1_1_10_111                         ' The values of each are the actual
    XTAL3_PLL1X     = %0_1_1_11_011                         ' CLK Register values that determine
    XTAL3_PLL2X     = %0_1_1_11_100                         ' the System Clock mode and frequency
    XTAL3_PLL4X     = %0_1_1_11_101
    XTAL3_PLL8X     = %0_1_1_11_110
    XTAL3_PLL16X    = %0_1_1_11_111

PUB SetClock(xinfrequency, mode): newFreq
{{
    Set the system clock to mode `mode` and input frequency `xinfrequency`.

    Parameters:

    - `mode` - one of the Clock Mode constants defined in the CON block above,
      such as RCFAST_ or XTAL1_PLL16X.

    - `xinfrequency` - Frequency (in Hz) that external crystal/clock is driving
      into XIN pin.  Use 0 if no external clock source is connected.

    Returns the new clock frequency.

    NOTE: The SetMode method automatically converts the given clock setting constant
    expression to the corresponding CLK Register value (shown in the table), calculates
    and updates the System Clock Frequency value (CLKFREQ) and performs the proper
    stabilization procedure (10 ms delay), as needed, to ensure a stable clock when
    switching from a non-feedback clock source to a feedback-based clock source (like
    crystals and resonators). In addition to the required stabilization procedure noted above
    an additional delay of approximately 75 µs occurs while the hardware switches the source.
}}

    xinFreq := xinfrequency                                                     ' Update xinFreq
    oscDelay[2] := xinFreq / 100 #> WMIN                                        ' Update oscDelay for XINPUT 10 ms delay

    ifnot (clkmode & $18) and (mode & $18)                                      ' If switching from a non-feedback to a feedback-based clock source
        clkset(clkmode & $07 | mode & $78, clkfreq)                             '   first rev up oscillator and possibly PLL circuits (using current clock source RCSLOW, RCFAST, XINPUT, or XINPUT + PLLxxx)
        waitcnt(oscDelay[clkmode & $7 <# 2] * |<(clkmode & $7 - 3 #> 0) + cnt)  '   and wait 10 ms to stabilize, accounting for worst-case IRC speed (or XIN + PLL speed)

    clkset(mode, newFreq := ircFreq[mode <# 2] * |<(mode & $7 - 3 #> 0))        ' Switch to new clock mode, indicate new frequency (ideal RCFAST, ideal RCSLOW, or
                                                                                ' XINFreq * PLL multiplier) and update return value (new frequency)

DAT
    ircFreq     long    12_000_000                                              ' Ideal RCFAST frequency
                long    20_000                                                  ' Ideal RCSLOW frequency
    xinFreq     long    0                                                       ' External source (XIN) frequency (updated by .Init); MUST reside immediately after ircFreq
    oscDelay    long    20_000_000 / 100                                        ' Sys Counter offset for 10 ms oscillator startup delay based on worst-case RCFAST frequency
                long    33_000 / 100 #> WMIN                                    ' <same as above> but based on worst-case RCSLOW frequency; limited to WMIN to prevent freeze
                long    0 {xinFreq / 100 #> WMIN}                               ' <same as above> but based on external source (XIN) frequency; updated by .Init

