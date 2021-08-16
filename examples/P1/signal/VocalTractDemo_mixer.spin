''****************************************
''**  Vocal Tract Demo (Mixer) v1.0      *
''**  Author: Chip Gracey                *
''**  Copyright (c) 2006 Parallax, Inc.  *
''**  See end of file for terms of use.  *
''****************************************

CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000

  jj = 19   -7
  buffersize = $1000


OBJ

  v     : "signal.synth.speech"
  sm    : "signal.spatializer"
  m     : "input.mouse"
  di    : "signal.dither"


VAR

  long  stackspace[40]
  long  buffer[buffersize]
  byte  aa,ga,gp,vp,vr,f1,f2,f3,f4,na,nf,fa,ff

  word  sm_input[4] 'addresses of sample streams for each channel (0=disabled)
  word  sm_angle[4] 'angles for each channel (-111 = far left, 0 = center, +111 = far right)
  word  sm_depth[4] 'depths for each channel (0..buffersize-16)
  word  sm_knobs

PUB go | i, d, dx, dy

  v.start(@aa, -1, -1, -1)
  v.set_attenuation(0)

  repeat i from 0 to 0
    sm_input[i] := v.sample_ptr
    sm_depth[i] := $1000
    'sm_angle[i] := i << 28

  sm.start(@sm_input, @buffer, buffersize, 11, -1, 10, -1)
  'di.start(sm.sample_ptr)

  sm_knobs := %000_110_111_001

' s.start(@sources, @distances, @angles, 0,1,2,3)

  m.start(24,25)
  m.bound_limits(-127,0,0,127,4095*256,16)
  m.bound_scales(1,1,1)



  'vp := 64
  'vr := 20

  cognew(doit,@stackspace)

  dira[23..16]~~

  'return

  repeat
    d++
    dx := m.delta_x << 8
    dy := m.delta_y << 6
    repeat i from 0 to 0
      sm_angle[i] := sm_angle[i] + dx #> 0 <# $FFFF
      sm_depth[i] := sm_depth[i] - dy #> 0 <# $FFFF'(buffersize - 16)

{
      if d & $1000
        sm_angle[i] := 0
        sm_depth[i] := 0
      else
        sm_angle[i] := -1
        sm_depth[i] := buffersize -1
}


PRI doit | a, b, n

'repeat

  'gp := 10'0 +96
  'ga := 255
  'v.go(100)


  'vp := 80
  'vr := 20
v.set_pace(100)

repeat

  setformants(470,1650,2500,3500)
  gp := 80' +40+40

  ff := 170
  v.go(10)
  fa := 40
  v.go(150)
  fa := 0
  aa := 25

  v.go(100)


  ga := 70
  setformants(700,1750,2500,3500)
  v.go(70)

  go_ah

{
  repeat
    aa~
    set(result++)
    v.go(0)
    ga := 70
    v.go(||?b // 300 + 200)
    if result == 5
      result~
      a++
      if a < 0 or a > 6
        a~
    set(a)
    ga := 50
    v.go(200)
    ga := 0
    v.go(50)
    'v.go(100)
}
  setformants(700,1500,2400,3400)
  v.go(150)

  setformants(600,1440,2300,3300)
  v.go(50)

  ga := 10
  aa := 0

  ff := 250
  v.go(20)

  fa := 10
  v.go(20)

  v.go(80)

  fa := 0
  v.go(20)


  ga := 70
  aa := 15
  setformants(500,1440,2300,3300)
  v.go(20)

  setformants(550,1750,2400,3400)
  v.go(60)

  v.go(50)

  setformants(250,1700,2300,3400)
  setnasal(2000)
  na := $FF

  v.go(60)
  ga := 60
  v.go(150)

  ga := 0
  aa := 0
  v.go(80)

  na := 0
  v.go(1000)


PRI go_ah

  repeat
    aa := 20
    setformants(775,1300,2500,3500)
    gp := 130
    ga := 80'-30

    'ga := 0
    'fa := 100
    'ff := 0
    v.go(100)



PRI set(i)

    f1 := (f1s[i] + jj/2) / jj
    f2 := (f2s[i] + jj/2) / jj
    f3 := (f3s[i] + jj/2) / jj
    f4 := (f4s[i] + jj/2) / jj


PRI setformants(sf1,sf2,sf3,sf4)

    f1 := (sf1 + jj/2) / jj  <# 255
    f2 := (sf2 + jj/2) / jj  <# 255
    f3 := (sf3 + jj/2) / jj  <# 255
    f4 := (sf4 + jj/2) / jj  <# 255


PRI setnasal(f)

  nf := (f + jj/2) / jj <# 255




DAT
'       byte  aa,ga,gp,vp,vr,f1,q1,f2,q2,f3,q3,f4,q4,fn,qn,fa,ff
note    byte  0,2,4,5,7,9,11,12,11,9,7,5,4,2,0,0

s1      byte  '0,0,0,0,0,670/jj,qx1,1033/jj,qx2,2842/jj,qx3,3933/19,qx4,0,0,0,0,0,0
s2      byte  40,000,100,0,0,0/16,2300/16,3000/16,3500/16,255,2000/16,0,0,0
s3      byte  20,200,100,0,0,250/16,2300/16,3000/16,3500/16,0,0,0,0,0
s4      byte  20,200,100,0,0,250/16,2300/16,3000/16,3500/16,0,0,0,0,0
s5      byte  40,200,100,5,20,700/16,1800/16,2550/16,3500/16,0,0,0,0,0
s6      byte  00,000,100,0,0,425/16,1000/16,2400/16,3500/16,0,0,0,0,0

        '     ee   i    e    a    o    oh   foot boot r    l    uh
f1s     long  0280,0450,0550,0700,0775,0575,0425,0275,0560,0560,0700
f2s     long  2040,2060,1950,1800,1100,0900,1000,0850,1200,0820,1300
f3s     long  3040,2700,2600,2550,2500,2450,2400,2400,1500,2700,2600
f4s     long  3600,3570,3400,3400,3500,3500,3500,3500,3050,3600,3100

q1s     byte  $9A, $9A, $9A, $9A, $9A, $9A, $9A, $9A, $9A, $9A, $98
q2s     byte  $98, $98, $98, $98, $98, $98, $98, $98, $98, $98, $96
q3s     byte  $94, $94, $94, $94, $94, $94, $94, $94, $94, $94, $94
q4s     byte  $92, $92, $92, $92, $92, $92, $92, $92, $92, $92, $90




{
f1s     long  0250,0375,0550,0700,0775,0575,0425,0275
f2s     long  2300,2150,1950,1800,1100,0900,1000,0850
f3s     long  3000,2800,2600,2550,2500,2450,2400,2400
f4s     long  3800,3600,3400,3400,3500,3500,3500,3500
}

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
