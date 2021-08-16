' Original authors: Chip Gracey, Beau Schwabe
CON

  _CLKMODE = XTAL1 + PLL16X
  _XINFREQ = 5_000_000

  Pin  = 27
  Frequency = 440                                       ' DC to 128MHz

OBJ
  Freq : "signal.synth"

PUB CTR_Demo

    Freq.Synth("A",Pin, Frequency)                      'Synth({Counter"A" or Counter"B"},Pin, Freq)

    repeat                                              'loop forever to keep cog alive
