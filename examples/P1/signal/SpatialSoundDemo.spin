{{
****************************************
**  Spatial Sound Demo v1.0            *
**  Author: Chip Gracey                *
**  Copyright (c) 2006 Parallax, Inc.  *
**  See end of file for terms of use.  *
****************************************

Spatial Sound Demo - works on Propeller Demo Board

This program demonstrates the StereoSpatializer object. It uses VocalTract to
provide a sound source which gets fed into a single input of the spatializer.
A mouse is used to control the angle and depth settings for that single input.
Stereo sound is output to the headphone jack. Note that the spatializer can
process three more inputs than this demo shows.

}}
CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000

  buffer_size = $1000


OBJ

  v : "signal.synth.speech"
  s : "signal.spatializer"
  m : "input.mouse"


VAR

  byte  aa,ga,gp,vp,vr,f1,f2,f3,f4,na,nf,fa,ff          'vocal tract parameters

  word  input[4]                                        'stereo spatializer parameters
  word  angle[4]
  word  depth[4]
  word  knobs

  long  buffer[buffer_size]                             'stereo spatializer delay buffer


PUB start

  'start vocal tract and make "uh" sound
  v.start(@aa, -1, -1, -1)
  gp := 88
  f1 := constant(670 / 19)
  f2 := constant(1160 / 19)
  f3 := constant(2600 / 19)
  f4 := constant(3100 / 19)
  v.go(0)
  ga := 50
  v.go(0)

  'start stereo spatializer and set knobs
  s.start(@input, @buffer, buffer_size, 11, -1, 10, -1)
  knobs := %000_101_111_001

  'connect vocal tract output to spatializer input #0
  input[0] := v.sample_ptr

  'start mouse
  m.start(24,25)
  m.bound_limits(0, 0, 0, 255, constant(buffer_size >> 4 - 1), 0)
  m.bound_scales(1, -1, 1)

  'update input #0's angle and depth with mouse coordinates
  repeat
    angle[0] := m.bound_x << 8
    depth[0] := m.bound_y << 8

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
