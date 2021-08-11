''***************************************
''*  Propeller Loader v1.0              *
''*  Author: Chip Gracey                *
''*  Copyright (c) 2006 Parallax, Inc.  *
''*  See end of file for terms of use.  *
''***************************************

' v1.0 - 13 June 2006 - original version

''_____________________________________________________________________________
''
''This object lets a Propeller chip load up another Propeller chip in the same
''way the PC normally does.
''
''To do this, the program to be loaded into the other Propeller chip must be
''compiled using "F8" (be sure to enable "Show Hex") and then a "Save Binary
''File" must be done. This binary file must then be included so that it will be
''resident and its address can be conveyed to this object for loading.
''
''Say that the file was saved as "loadme.binary". Also, say that the Propeller
''which will be performing the load/program operation has its pins 0..2 tied to
''the other Propeller's pins RESn, P31, and P30, respectively. And we'll say
''we're working with Version 1 chips and you just want to load and execute the
''program. Your code would look something like this:
''
''
''OBJ loader : "misc.loader"
''
''DAT loadme file "loadme.binary"
''
''PUB LoadPropeller
''
''  loader.Connect(0, 1, 2, 1, loader#LoadRun, @loadme)
''
''
''This object drives the other Propeller's RESn line, so it is recommended that
''the other Propeller's BOEn pin be tied high and that its RESn pin be pulled
''to VSS with a 1M resistor to keep it on ice until showtime.
''_____________________________________________________________________________
''


CON

  #1, ErrorConnect, ErrorVersion, ErrorChecksum, ErrorProgram, ErrorVerify
  #0, Shutdown, LoadRun, ProgramShutdown, ProgramRun


VAR

  long P31, P30, LFSR, Ver, Echo


PUB Connect(PinRESn, PinP31, PinP30, Version, Command, CodePtr) : Error

  'set P31 and P30
  P31 := PinP31
  P30 := PinP30

  'RESn low
  outa[PinRESn] := 0
  dira[PinRESn] := 1

  'P31 high (our TX)
  outa[PinP31] := 1
  dira[PinP31] := 1

  'P30 input (our RX)
  dira[PinP30] := 0

  'RESn high
  outa[PinRESn] := 1

  'wait 100ms
  waitcnt(clkfreq / 10 + cnt)

  'Communicate (may abort with error code)
  if Error := \Communicate(Version, Command, CodePtr)
    dira[PinRESn] := 0

  'P31 float
  dira[PinP31] := 0


PRI Communicate(Version, Command, CodePtr) | ByteCount

  'output calibration pulses
  BitsOut(%01, 2)

  'send LFSR pattern
  LFSR := "P"
  repeat 250
    BitsOut(IterateLFSR, 1)

  'receive and verify LFSR pattern
  repeat 250
    if WaitBit(1) <> IterateLFSR
      abort ErrorConnect

  'receive chip version
  repeat 8
    Ver := WaitBit(1) << 7 + Ver >> 1

  'if version mismatch, shutdown and abort
  if Ver <> Version
    BitsOut(Shutdown, 32)
    abort ErrorVersion

  'send command
  BitsOut(Command, 32)

  'handle command details
  if Command

    'send long count
    ByteCount := byte[CodePtr][8] | byte[CodePtr][9] << 8
    BitsOut(ByteCount >> 2, 32)

    'send bytes
    repeat ByteCount
      BitsOut(byte[CodePtr++], 8)

    'allow 250ms for positive checksum response
    if WaitBit(25)
      abort ErrorChecksum

    'eeprom program command
    if Command > 1

      'allow 5s for positive program response
      if WaitBit(500)
        abort ErrorProgram

      'allow 2s for positive verify response
      if WaitBit(200)
        abort ErrorVerify


PRI IterateLFSR : Bit

  'get return bit
  Bit := LFSR & 1

  'iterate LFSR (8-bit, $B2 taps)
  LFSR := LFSR << 1 | (LFSR >> 7 ^ LFSR >> 5 ^ LFSR >> 4 ^ LFSR >> 1) & 1


PRI WaitBit(Hundredths) : Bit | PriorEcho

  repeat Hundredths

    'output 1t pulse
    BitsOut(1, 1)

    'sample bit and echo
    Bit := ina[P30]
    PriorEcho := Echo

    'output 2t pulse
    BitsOut(0, 1)

    'if echo was low, got bit
    if not PriorEcho
      return

    'wait 10ms
    waitcnt(clkfreq / 100 + cnt)

  'timeout, abort
  abort ErrorConnect


PRI BitsOut(Value, Bits)

  repeat Bits

    if Value & 1

      'output '1' (1t pulse)
      outa[P31] := 0
      Echo := ina[P30]
      outa[P31] := 1

    else

      'output '0' (2t pulse)
      outa[P31] := 0
      outa[P31] := 0
      Echo := ina[P30]
      Echo := ina[P30]
      outa[P31] := 1

    Value >>= 1

