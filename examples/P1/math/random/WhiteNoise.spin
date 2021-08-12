' Original author: Chip Gracey
{{
    This program demonstrates how the RealRandom object
    can generate a random number on each power-up. It also
    conveys continuous random numbers to the headphones
    for listening -- be warned, it is loud whitenoise.

    It uses the Propeller Demo Board, or any equivalent
    TV and audio circuits on a raw Propeller.
}}

CON

    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

OBJ

    random  : "math.random"
    io : "io"

CON

    AUDIO_L = 27

PUB start | i

  random.Start

  i := random.RandomAddress

  io.Output (AUDIO_L)

  repeat
    io.Set (AUDIO_L, long[i])
