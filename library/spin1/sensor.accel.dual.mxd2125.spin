''******************************************
''*  Memsic Dual Accelerometer Driver v1.0 *
''*  Author: Paul Baker                    *
''*  Copyright (c) 2007 Parallax, Inc.     *
''*  See end of file for terms of use.     *
''******************************************

VAR
long cog, XVal, YVal                  'variable which contains the cog identifier and Raw sensor data

PUB stop
    '' Stop driver - frees a cog
    if cog
       cogstop(cog)

PUB start(Xin,Yin): okay
    ctramode := %1000 << 26 + Xin     'construct value for counter A mode (POS accumulator)
    ctrbmode := %1000 << 26 + Yin     'construct value for counter B mode (POS accumulator)
    pinwaitm := |< Xin + |< Yin       'construct pin mask for waiting

    '' Start driver - starts a cog
    '' returns false if no cog available

    okay := cog := cognew(@MXD2125, @XVal)

PUB x
  return XVal                                   'return current X axis pulse width

PUB y
  return YVal                                   'return current Y axis pulse width

DAT
MXD2125       org
              'set shared memory locations
              mov Xarg, par                     'store location to write X value
              mov Yarg, par                     'store location to write Y value
              add Yarg, #4                      'adjust location of Y value
              'establish counter A as pulse width counter
              mov frqa, #1                      'add 1 to counter A total each cycle pin is high
              mov ctra, ctramode
              'establish counter B as pulse width counter
              mov frqb, #1                      'add 1 to counter B total each cycle pin is high
              mov ctrb, ctrbmode
:loop         waitpeq zero, pinwaitm            'wait until both channels have completed thier cycle
              mov temp, phsa                    'copy X axis value to hub
              wrlong temp, Xarg
              mov temp, phsb                    'copy Y axis value to hub
              wrlong temp, Yarg
              mov phsa, zero                    'reset counter values
              mov phsb, zero
              waitpeq pinwaitm, pinwaitm        'wait until both channels start thier cycle
              jmp #:loop                        'do it again

zero          LONG 0                            'value zero
temp          LONG 0                            'temporary loaction to store counter value
Xarg          LONG 0                            'address to store X axis pulse width
Yarg          LONG 0                            'address to store X axis pulse width
ctramode      LONG 0                            'counter A mode value
ctrbmode      LONG 0                            'counter B mode value
pinwaitm      LONG 0                            'pin mask used in waitpeq instruction

