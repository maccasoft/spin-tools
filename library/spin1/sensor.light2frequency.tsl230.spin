''*******************************************
''*  TSL230 Light to Frequency Driver v1.0  *
''*  Author: Paul Baker                     *
''*  Copyright (c) 2007 Parallax, Inc.      *
''*  See end of file for terms of use.      *
''*******************************************
{{********************************************************
   Taos TSL230 light to frequency sensor v1.0 driver
   with manual and auto scaling capabilities
                    ┌──────────┐
    ctrlpinbase ──│1 o      8│──┳──┐
                    │          │   │  
  ctrlpinbase+1 ──│2        7│──┘
                    │    []    │
                ┌──│3        6│──── inpin
                │   │          │   
                ┣──│4        5│──┘ 3.3V
                   └──────────┘
*********************************************************}}
CON
        _clkmode = xtal1 + pll16x
        _xinfreq = 5_000_000

        ctrmode = $28000000

VAR
  long freq, cog
  long scale, cbase, sps, auto

PUB Stop
    '' Stop driver - frees a cog
    if cog
       cogstop(cog~ -  1)

{{********************************************************
  Start method to initialize driver, arguments:
  inpin - pin number output of tsl230 is connected
  ctrlpinbase - pin number connected to S0
                S1 connected to ctrlpinbase + 1
  samplefreq  - number of measurements per second
                higher values reduce significant digits
  autoscale - boolean indicates if autoscaling is used
              with autoscale on, output range is between
              0 and ~160,000,000
              with autoscale off, output range is between
              0 and ~1,600,000
*********************************************************}}
PUB Start(inpin, ctrlpinbase, samplefreq, autoscale): okay
  scale := %11                                          'set inital scale to maximum
  cbase := ctrlpinbase                                  'copy parameters
  sps := samplefreq
  auto := autoscale

  dira := %11 << cbase                                  'set control pins to output
  outa := %11 << cbase                                  'set scale

  ctra_ := ctrmode + inpin                              'compute counter mode
  cntadd := 80_000_000 / samplefreq                     'compute wait period

  cog := okay := cognew(@entry, @freq)                  'start driver

PUB GetSample : val
  val := freq * sps * lookup(scale: 100, 10, 1)         'compute scaled frequency
  if auto                                               'autoscaling code
    if val > 1_000_000                                  'if output exceeds 1 MHz, decrease gain
      scale := --scale #> 1
    elseif val < 10_000                                 'if output less than 10 kHz, increase gain
      scale := ++scale <# 3
    outa := scale << cbase

{{********************************************************
  SetScale manually sets the gain, works in both autoscale
   and manual modes, though autoscale will readjust the
   scale if set too low or high
*********************************************************}}
PUB SetScale(range)
  scale := 1 #> range <# 3                              'limit argument range to 1,2 or 3
  outa := scale << cbase                                'set scale

DAT
{{********************************************************
 Assembly driver for tsl230
*********************************************************}}
        org

entry   mov     ctra, ctra_             'setup counter to count positive edges
        mov     frqa, #1                'increment for each edge seen
        mov     cnt_, cnt               'initialize waitperiod
        add     cnt_, cntadd

:loop   waitcnt cnt_, cntadd            'wait for next sampling period
        mov     new, phsa               'record new count
        mov     temp, new               'make second copy
        sub     new, old                'compute cycles since last
        mov     old, temp               'record a new old count

        wrlong  new, par                'write number of cycles since last period to hub memory
        jmp     #:loop

ctra_   long    0
cntadd  long    0

cnt_    res     1
new     res     1
old     res     1
temp    res     1

