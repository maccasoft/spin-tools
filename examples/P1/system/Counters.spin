' Author: Chip Gracey
{{
    # CTRA and CTRB

    Each COG has two counter modules. They are named CTRA and CTRB.

    Each CTR module can control or monitor up to two I/O pins and perform
    conditional 32-bit accumulation of its FRQ register into its PHS register on
    every clock cycle.

    Each CTR module also has its own phase-locked loop (PLL) which can be used to
    synthesize frequencies up to 128 MHz.

    With a little setup or oversight from the COG, a CTR can be used for:

    -   frequency synthesis
    -   frequency measurement
    -   pulse counting
    -   pulse measurement
    -   multi-pin state measurement
    -   pulse-width modulation
    -   duty-cycle measurement
    -   digital-to-analog conversion
    -   analog-to-digital conversion
    -   ...and probably many other useful things

    For some of these operations, the COG can "set and forget" a CTR. For others,
    it may use WAITCNT to time-align CTR reads and writes within a loop, creating
    the effect of a more complex state machine.

    Note that for a COG clock frequency of 80 MHz, the CTR update period is a mere
    12.5ns. This high speed, combined with 32-bit precision, allows for very
    dynamic signal generation and measurement.

    The overarching CTR design goal was to create a simple and flexible subsystem
    which could perform some repetitive task on every clock cycle, thereby freeing
    the COG to perform some computationally richer super-task. While the CTRs have
    only 32 basic operating modes, there is no limit to how they might be used
    dynamically through software. Integral to this concept is the use of the
    WAITPEQ, WAITPNE, and WAITCNT instructions, which can event-align or time-align
    a COG with its CTRs.

    Each CTR has three registers:

    ### CTRA / CTRB - Control register

    The CTR register selects the CTR's operating mode. As soon as this register is
    written, the new operating mode goes into effect. Writing a zero to CTR will
    immediately disable the CTR, stopping all pin output and PHS accumulation.


    ### FRQA / FRQB - Frequency register

    FRQ holds the value that will be accumulated into the PHS register. For some
    applications, FRQ may be written once, and then ignored. For others, it may be
    rapidly modulated.


    ### PHSA / PHSB - Phase register

    The PHS register is the oddest of all COG registers. Not only can it be written
    and read via COG instructions, but it also functions as a free-running
    accumulator, summing the FRQ register into itself on potentially every clock
    cycle. Any instruction writing to PHS will override any accumulation for that
    clock cycle. PHS can only be read through the source operand (same as PAR, CNT,
    INA, and INB). Beware that doing a read-modify-write instruction on PHS, like
    "ADD PHSA,#1", will cause the last-written value to be used as the destination
    operand input, rather than the current accumulation.


            CTRA / CTRB registers

           ┌────┬─────────┬────────┬────────┬───────┬──────┬──────┐
      Bits │ 31 │ 30..26  │ 25..23 │ 22..15 │ 14..9 │ 8..6 │ 5..0 │
           ├────┼─────────┼────────┼────────┼───────┼──────┼──────┤
      Name │ ── │ CTRMODE │ PLLDIV │ ────── │ BPIN  │ ──── │ APIN │
           └────┴─────────┴────────┴────────┴───────┴──────┴──────┘

            CTRMODE selects one of 32 operating modes for the CTR
              - conveniently written (along with PLLDIV) using the MOVI instruction

            PLLDIV selects a PLL output tap
              - may be ignored if not used

            BPIN selects a pin to be the secondary I/O
              - may be ignored if not used
              - %0xxxxx = Port A, %1xxxxx = Port B (future)
              - conveniently written using the MOVD instruction

            APIN selects a pin to be the primary I/O
              - may be ignored if not used
              - %0xxxxx = Port A, %1xxxxx = Port B (future)
              - conveniently written using the MOVS instruction


                                             Accumulate   APIN        BPIN
     CTRMODE   Description                   FRQ to PHS   output*     output*
    ┌────────┬─────────────────────────────┬────────────┬────────────┬────────────┐
    │ %00000 │ Counter disabled (off)      │ 0 (never)  │ 0 (none)   │ 0 (none)   │
    ├────────┼─────────────────────────────┼────────────┼────────────┼────────────┤
    │ %00001 │ PLL internal (video mode)   │ 1 (always) │ 0          │ 0          │
    │ %00010 │ PLL single-ended            │ 1          │ PLL        │ 0          │
    │ %00011 │ PLL differential            │ 1          │ PLL        │ !PLL       │
    ├────────┼─────────────────────────────┼────────────┼────────────┼────────────┤
    │ %00100 │ NCO/PWM single-ended        │ 1          │ PHS[31]    │ 0          │
    │ %00101 │ NCO/PWM differential        │ 1          │ PHS[31]    │ !PHS[31]   │
    ├────────┼─────────────────────────────┼────────────┼────────────┼────────────┤
    │ %00110 │ DUTY single-ended           │ 1          │ PHS-Carry  │ 0          │
    │ %00111 │ DUTY differential           │ 1          │ PHS-Carry  │ !PHS-Carry │
    ├────────┼─────────────────────────────┼────────────┼────────────┼────────────┤
    │ %01000 │ POS detector                │  A¹        │ 0          │ 0          │
    │ %01001 │ POS detector w/feedback     │  A¹        │ 0          │ !A¹        │
    │ %01010 │ POSEDGE detector            │  A¹ & !A²  │ 0          │ 0          │
    │ %01011 │ POSEDGE detector w/feedback │  A¹ & !A²  │ 0          │ !A¹        │
    ├────────┼─────────────────────────────┼────────────┼────────────┼────────────┤
    │ %01100 │ NEG detector                │ !A¹        │ 0          │ 0          │
    │ %01101 │ NEG detector w/feedback     │ !A¹        │ 0          │ !A¹        │
    │ %01110 │ NEGEDGE detector            │ !A¹ &  A²  │ 0          │ 0          │
    │ %01111 │ NEGEDGE detector w/feedback │ !A¹ &  A²  │ 0          │ !A¹        │
    ├────────┼─────────────────────────────┼────────────┼────────────┼────────────┤
    │ %10000 │ LOGIC never                 │ 0          │ 0          │ 0          │
    │ %10001 │ LOGIC !A & !B               │ !A¹ & !B¹  │ 0          │ 0          │
    │ %10010 │ LOGIC A & !B                │  A¹ & !B¹  │ 0          │ 0          │
    │ %10011 │ LOGIC !B                    │       !B¹  │ 0          │ 0          │
    │ %10100 │ LOGIC !A & B                │ !A¹ &  B¹  │ 0          │ 0          │
    │ %10101 │ LOGIC !A                    │ !A¹        │ 0          │ 0          │
    │ %10110 │ LOGIC A <> B                │  A¹ <> B¹  │ 0          │ 0          │
    │ %10111 │ LOGIC !A | !B               │ !A¹ | !B¹  │ 0          │ 0          │
    │ %11000 │ LOGIC A & B                 │  A¹ &  B¹  │ 0          │ 0          │
    │ %11001 │ LOGIC A == B                │  A¹ == B¹  │ 0          │ 0          │
    │ %11010 │ LOGIC A                     │  A¹        │ 0          │ 0          │
    │ %11011 │ LOGIC A | !B                │  A¹ | !B¹  │ 0          │ 0          │
    │ %11100 │ LOGIC B                     │        B¹  │ 0          │ 0          │
    │ %11101 │ LOGIC !A | B                │ !A¹ |  B¹  │ 0          │ 0          │
    │ %11110 │ LOGIC A | B                 │  A¹ |  B¹  │ 0          │ 0          │
    │ %11111 │ LOGIC always                │ 1          │ 0          │ 0          │
    └────────┴─────────────────────────────┴────────────┴────────────┴────────────┘
     * must set corresponding DIR bit to affect pin

      A¹ = APIN input delayed by 1 clock
      A² = APIN input delayed by 2 clocks
      B¹ = BPIN input delayed by 1 clock


    ┌────────┬────────────┐
    │ PLLDIV │ Output     │     The PLL modes (%00001..%00011) cause FRQ-to-PHS
    ├────────┼────────────┤     accumulation to occur every clock cycle. This
    │  %000  │ VCO ÷ 128  │     creates a numerically-controlled oscillator (NCO)
    ├────────┼────────────┤     in PHS[31], which feeds the CTR PLL's reference
    │  %001  │ VCO ÷ 64   │     input. The PLL will multiply this frequency by 16
    ├────────┼────────────┤     using its voltage-controlled oscillator (VCO).
    │  %010  │ VCO ÷ 32   │
    ├────────┼────────────┤     For stable operation, it is recommended that the
    │  %011  │ VCO ÷ 16   │     VCO frequency be kept within 64 MHz to 128 MHz.
    ├────────┼────────────┤     This translates to an NCO frequency of 4 MHz to
    │  %100  │ VCO ÷ 8    │     8 MHz.
    ├────────┼────────────┤
    │  %101  │ VCO ÷ 4    │     The PLLDIV field of the CTR register selects
    ├────────┼────────────┤     which power-of-two division of the VCO frequency
    │  %110  │ VCO ÷ 2    │     will be used as the final PLL output. This
    ├────────┼────────────┤     affords a PLL range of 500 KHz to 128 MHz.
    │  %111  │ VCO ÷ 1    │
    └────────┴────────────┘

}}

CON

  _CLKMODE = XTAL1 + PLL16X
  _XINFREQ = 5_000_000

  AUDIO_L = 0
  AUDIO_R = 1

OBJ

  term : "com.serial.terminal"

VAR

  long  ctr, frq

PUB Main
{{
    Synthesize frequencies on pin 0 and pin 1
    also show FRQ values on tv
}}

    term.Start(115200)                     'start tv terminal

    SynthFreq(AUDIO_L, 1_000_000)            'determine ctr and frq for pin 0
    CTRA := ctr                        'set CTRA
    FRQA := frq                        'set FRQA
    DIRA[AUDIO_L]~~                          'make pin output

    term.Str(string("CTRA = "))        'show CTRA value
    term.Hex(ctr,8)
    term.Str(string("  FRQA = "))      'show FRQA value
    term.Hex(frq,8)

    SynthFreq(AUDIO_R, 1_000_001)            'determine ctr and frq for pin1
    CTRB := ctr                        'set CTRB
    FRQB := frq                        'set FRQB
    DIRA[AUDIO_R]~~                          'make pin output

    term.Str(string(13, "CTRB = "))    'show CTRB value
    term.Hex(ctr,8)
    term.Str(string("  FRQB = "))      'show FRQB value
    term.Hex(frq,8)
    term.Str (string(term#NL, "Playing tone on pins "))
    term.Dec (AUDIO_L)
    term.Str (string(" and "))
    term.Dec (AUDIO_R)

PRI SynthFreq(Pin, Freq) | s, d

'' Determine CTR settings for synthesis of 0..128 MHz in 1 Hz steps
''
'' in:    Pin = pin to output frequency on
''        Freq = actual Hz to synthesize
''
'' out:   ctr and frq hold CTRA/CTRB and FRQA/FRQB values
''
''   Uses NCO mode %00100 for 0..499_999 Hz
''   Uses PLL mode %00010 for 500_000..128_000_000 Hz
''

  Freq := Freq #> 0 <# 128_000_000     'limit frequency range

  if Freq < 500_000                    'if 0 to 499_999 Hz,
    ctr := constant(%00100 << 26)      '..set NCO mode
    s := 1                             '..shift = 1

  else                                 'if 500_000 to 128_000_000 Hz,
    ctr := constant(%00010 << 26)      '..set PLL mode
    d := >|((Freq - 1) / 1_000_000)    'determine PLLDIV
    s := 4 - d                         'determine shift
    ctr |= d << 23                     'set PLLDIV

  frq := fraction(Freq, CLKFREQ, s)    'Compute FRQA/FRQB value
  ctr |= Pin                           'set PINA to complete CTRA/CTRB value


PRI fraction(a, b, shift) : f

  if shift > 0                         'if shift, pre-shift a or b left
    a <<= shift                        'to maintain significant bits while
  if shift < 0                         'insuring proper result
    b <<= -shift

  repeat 32                            'perform long division of a/b
    f <<= 1
    if a => b
      a -= b
      f++
    a <<= 1

