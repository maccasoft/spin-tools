' Original authors: Chip Gracey, Beau Schwabe

PUB Synth(CTR_AB, Pin, Freq) | s, d, ctr, frq

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

    if CTR_AB == "A"
       CTRA := ctr                        'set CTRA
       FRQA := frq                        'set FRQA
       DIRA[Pin]~~                        'make pin output

    if CTR_AB == "B"
       CTRB := ctr                        'set CTRB
       FRQB := frq                        'set FRQB
       DIRA[Pin]~~                        'make pin output

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

