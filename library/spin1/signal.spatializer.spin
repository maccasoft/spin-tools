{{
┌───────────────────────────────────────────┬────────────────┬───────────────────────────────────┬─────────────────┐
│ Stereo Spatializer v1.0                   │ by Chip Gracey │ Copyright (c) 2006 Parallax, Inc. │ 26 October 2006 │
├───────────────────────────────────────────┴────────────────┴───────────────────────────────────┴─────────────────┤
│                                                                                                                  │
│ This object spatializes up to four monaural audio sample streams into a time-conscious stereo sound field with   │
│ angle and depth controls for each channel. It requires one cog and at least 80 MHz.                              │
│                                                                                                                  │
│ The stereo spatializer is controlled via 13 single-word parameters which must reside in the parent object:       │
│                                                                                                                  │
│ VAR word input[4]    'pointers to longs which receive sample streams for each channel (0 = disable)              │
│ VAR word angle[4]    'angles for each channel (0..32768..65535 = left..center..right)                            │
│ VAR word depth[4]    'depths for each channel (0..65535 = near..far)                                             │
│ VAR word knobs       'bit fields for controlling depth decay, echoes, and dither                                 │
│                                                                                                                  │
│ The 'input' words are used to point the spatializer to longs which are receiving 32-bit monaural sample streams  │
│ in real-time (preferably at a matching 20KHz sample rate). Any time an input is set to 0, that channel will be   │
│ disabled.                                                                                                        │
│                                                                                                                  │
│ The 'angle' words determine where, from left to right, the corresponding inputs will be placed within the sound  │
│ field. This is achieved by using the sine of the angle (as ±90 degrees) to compute a complimentary 0..3dB decay  │
│ and ±350µs-maximum delay for each ear. For example, when the angle is all the way left, the left channel         │
│ receives a -0dB signal with a -350µs delay, while the right channel receives a -3dB signal with a +350µs delay.  │
│ When the angle is exactly center, both channels receive a -1.5dB signal with a 0µs delay. The spatializer        │
│ maintains its own internal angles which 'chase' the angle words at a rate of 1.76 degrees per millisecond to     │
│ avoid audible discontinuities in the sample delay buffer.                                                        │
│                                                                                                                  │
│ The 'depth' words determine how far away the corresponding inputs will be placed into the sound field. This is   │
│ achieved by logarithmically decaying the input samples by the depth and then summing them into the delay buffer  │
│ using depth / 16 (limited to buffer size - 16) as a whole-sample offset.  This creates a time delay before a     │
│ sample will be heard, but more importantly allows for doppler shifts to occur when the depths are changed. Note  │
│ that the spatializer's 20KHz input/output sample rate has a period of 50µs, which is far too coarse for a        │
│ natural-sounding delay step. The unit of depth delay is 1/16 of a sample period, or 3.125µs. This provides good  │
│ depth resolution which fills a word, but is still inadequate for a natural-sounding delay step. Internally,      │
│ delay is tracked in base units of 1/256 of a sample period, or 195ns ── a time in which sounds travels only      │
│ 67µm! To realize this resolution, input samples are apportioned into two parts and then summed into adjacent     │
│ samples in the delay buffer. The spatializer maintains its internal high-resolution depths by 'chasing' the      │
│ depth words using a difference-driven, dampened acceleration algorithm. This insures that pitch changes          │
│ resulting from doppler shifts occur very smoothly and continuously in response to the depth words being modified │
│ over time.                                                                                                       │
│                                                                                                                  │
│ The 'knobs' word contains four 3-bit fields which are arranged as follows: %NNN_XXX_PPP_DDD                      │
│                                                                                                                  │
│            ┌───────┬────────┬────────────┬───────────────┬────────────────────────────┐                          │
│            │ knobs │  %NNN  │    %XXX    │     %PPP      │            %DDD            │                          │
│            ├───────┼────────┼────────────┼───────────────┼────────────────────────────┤                          │
│            │ value │ dither │ cross echo │ parallel echo │      depth decay rate      │                          │
│            ├───────┼────────┼────────────┼───────────────┼────────────────────────────┤                          │
│            │  000  │ -24dB  │   -24dB    │     -24dB     │ -3dB per 32768 depth units │                          │
│            │  001  │ -27dB  │   -21dB    │     -21dB     │ -3dB per 16384 depth units │                          │
│            │  010  │ -30dB  │   -18dB    │     -18dB     │ -3dB per 8192 depth units  │                          │
│            │  011  │ -33dB  │   -15dB    │     -15dB     │ -3dB per 4096 depth units  │                          │
│            │  100  │ -36dB  │   -12dB    │     -12dB     │ -3dB per 2048 depth units  │                          │
│            │  101  │ -39dB  │   -9dB     │     -9dB      │ -3dB per 1024 depth units  │                          │
│            │  110  │ -42dB  │   -6dB     │     -6dB      │ -3dB per 512 depth units   │                          │
│            │  111  │ -45dB  │   -3dB     │     -3dB      │ -3dB per 256 depth units   │                          │
│            └───────┴────────┴────────────┴───────────────┴────────────────────────────┘                          │
│                                                                                                                  │
│ When starting the spatializer, you must give it a buffer of longs to be used as a stereo sample delay. The size  │
│ of this buffer must range from 16 to 4096 longs. With the minimal 16 longs, you'll have only enough buffer for   │
│ the ear-to-ear delays needed for directional cuing. Your depth delay will be stuck at 0. To be able to generate  │
│ depth delays, doppler shifts, and significant echoes, you will need to increase the buffer size. For every extra │
│ 18 longs, you'll get ~1 foot (0.9ms) of depth delay. For every extra 58 longs, you'll get ~1 meter (2.9ms) of    │
│ depth delay. The maximum extra 4080 longs (4096 total) will yield a depth delay of ~230 feet or ~70 meters,      │
│ which is 204ms. Regardless of your buffer size, you can always specify depths up to 65535 to achieve depth       │
│ decay.  However, for depth delay purposes, the maximum usable depth value will be (buffer size - 16) * 16.       │
│                                                                                                                  │
│ The spatializer generates stereo audio samples at a continuous rate of 20KHz. Samples may be output to pins via  │
│ delta-modulation for either RC filtering or direct transducer driving. In this case, 4x-oversampled dither is    │
│ used to eliminate both quantization noise from delta-modulation and pico-second jitter noise from on-chip        │
│ crosstalk between nearby pins. Normally, these two noise sources generate distracting buzzes, whines, and hash,  │
│ but dithering removes them in exchange for lower-level white noise which does not draw your attention. The       │
│ dither level can be adjusted through the 'knobs' word. You can turn it way down way to hear what almost no       │
│ dither sounds like. Aside from outputting to pins, sample pairs are always streamed into a special long so that  │
│ other objects can access them in real-time.                                                                      │
│                                                                                                                  │
└──────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

}}
VAR

  long  cog

  long  args[3], samples                                '4 longs    ...must be
  long  dira_, dirb_, ctra_, ctrb_, cnt_                '5 longs    ...contiguous


PUB start(settings_ptr, buffer_ptr, buffer_size, lpos_pin, lneg_pin, rpos_pin, rneg_pin) : okay | i

'' Start stereo spatializer driver - starts a cog
'' returns false if no cog available
''
''   settings_ptr = pointer to settings (13 words)
''     buffer_ptr = pointer to buffer (16 to 4096 longs)
''    buffer_size = number of longs in buffer
''       lpos_pin = left positive delta-modulation pin (-1 to disable)
''       lneg_pin = left negative delta-modulation pin (lpos_pin must also be enabled, -1 to disable)
''       rpos_pin = right positive delta-modulation pin (-1 to disable)
''       rneg_pin = right negative delta-modulation pin (rpos_pin must also be enabled, -1 to disable)

  'Reset driver
  stop

  'Remember arguments
  longmove(@args, @settings_ptr, 3)

  'If delta-modulation pin(s) enabled, ready output(s) and ready ctra/ctrb for duty mode
  repeat i from 0 to 2 step 2
    if lpos_pin[i] > -1
      dira_[lpos_pin[i] >> 5 & 1] |= |< lpos_pin[i]
      ctra_[i >> 1] := $18000000 + lpos_pin[i] & $3F
      if lneg_pin[i] > -1
        dira_[lneg_pin[i] >> 5 & 1] |= |< lneg_pin[i]
        ctra_[i >> 1] += $04000000 + (lneg_pin[i] & $3F) << 9

  'Ready 20KHz sample period
  cnt_ := clkfreq / 20_000

  'Launch spatializer cog
  return cog := cognew(@entry, @samples) + 1


PUB stop

'' Stop stereo spatializer driver - frees a cog

  'If already running, stop spatializer cog
  if cog
    cogstop(cog~ -  1)

  'Reset variables
  longfill(@dira_, 0, 4)


PUB sample_ptr : ptr

'' Returns the address of the long which receives the stereo audio samples in real-time
'' (two signed 16-bit values updated at 20KHz - left in top word, right in bottom word)

  return @samples


DAT

' ┌──────────────────┐
' │  Initialization  │
' └──────────────────┘

entry                   org

:zero                   mov     reserves,#0             'zero all reserved data
                        add     :zero,h00000200
                        djnz    clear_cnt,#:zero

                        mov     t1,par                  'get settings pointers
                        sub     t1,#3*4
                        rdlong  input_ptr,t1
                        mov     angle_ptr,input_ptr
                        add     angle_ptr,#4*2
                        mov     depth_ptr,angle_ptr
                        add     depth_ptr,#4*2
                        mov     knobs_ptr,depth_ptr
                        add     knobs_ptr,#4*2

                        add     t1,#1*4                 'get buffer base
                        rdlong  buff_base,t1

                        add     t1,#1*4                 'get buffer size
                        rdlong  buff_size,t1
                        shl     buff_size,#2

                        mov     buff_limit,buff_size    'compute buffer limit
                        sub     buff_limit,#4

                        mov     buff_clamp,buff_size    'compute buffer clamp
                        sub     buff_clamp,#16*4
                        shl     buff_clamp,#8-2

                        add     t1,#2*4                 'get dira/dirb/ctra/ctrb
                        mov     t2,#4
:regs                   rdlong  dira,t1
                        add     t1,#1*4
                        add     :regs,h00000200
                        djnz    t2,#:regs

                        rdlong  cnt_ticks,t1            'get cnt ticks

                        mov     cnt_value,cnt           'prepare for initial waitcnt
                        add     cnt_value,cnt_ticks


' ┌────────────────────┐
' │  Spatializer Loop  │
' └────────────────────┘

' Wait for next sample period, then get knob settings

loop                    waitcnt cnt_value,cnt_ticks     'wait for sample period

                        mov     inputs,#4               'ready to spatialize 4 inputs

                        rdword  t1,knobs_ptr            'get knob settings

                        mov     decay,t1                'get depth decay rate
                        and     decay,#7

                        shr     t1,#3                   'get parallel echo level
                        neg     pecho,t1
                        and     pecho,#7        wz
        if_z            mov     pecho,#8

                        shr     t1,#3                   'get cross echo level
                        neg     xecho,t1
                        and     xecho,#7        wz
        if_z            mov     xecho,#8

                        shr     t1,#3                   'get dither level
                        mov     dither,t1
                        and     dither,#7
                        add     dither,#8

' Spatialize an input channel - first update dithered outputs

:input                  mov     t1,lfsr0                'update left duty output
                        sar     t1,dither
                        add     t1,left
                        mov     frqa,t1

                        mov     t1,lfsr1                'update right duty output
                        sar     t1,dither
                        add     t1,right
                        mov     frqb,t1

' Get input sample and iterate dither generators

                        rdword  lx,input_ptr    wz      'get input pointer, check if 0

                        test    lfsr0,taps0     wc      'iterate left dither source
                        rcl     lfsr0,#1

        if_nz           rdlong  lx,lx                   'if pointer not 0, get sample, else use 0

                        test    lfsr1,taps1     wc      'iterate right dither source
                        rcl     lfsr1,#1

' Get sample into log form

                        abs     lx,lx           wc      '** sample is signed value
                        muxc    sign,#1                 'absolutize value and store sign

                        test    lx,hFFFF0000    wz      'msb-justify value and track whole exponent
        if_z            shl     lx,#16
                        muxnz   :whole,#$10

                        test    lx,hFF000000    wz
        if_z            shl     lx,#8
                        muxnz   :whole,#$08

                        test    lx,hF0000000    wz
        if_z            shl     lx,#4
                        muxnz   :whole,#$04

                        test    lx,hC0000000    wz
        if_z            shl     lx,#2
                        muxnz   :whole,#$02

                        test    lx,h80000000    wz
        if_z            shl     lx,#1
                        muxnz   :whole,#$01

                        shl     lx,#1                   'lookup fractional exponent
                        movs    lx,#$C
                        rol     lx,#12
                        rdword  lx,lx

                        shl     lx,#16                  'insert whole exponent
:whole                  or      lx,#%00000
                        ror     lx,#5                   '** sample is now in log form with isolated sign

' Process angle

:angle0                 mov     angle,angles            'get current angle
                        rdword  t1,angle_ptr            'get target angle
                        sub     t1,angle        wc      'get target-current difference
                        abs     t1,t1                   'absolutize difference
                        max     t1,#$20                 'limit difference
                        sumc    angle,t1                'add limited difference to current angle
:angle1                 mov     angles,angle            'store current angle

                        shr     angle,#16-12            'make 12-bit angle within quadrants 1|4
                        sub     angle,h00000800 wc      'subtract π/2, get quadrant 4 into c
                        negc    angle,angle             'if quadrant 4, negate table offset
                        or      angle,h00007000         'insert sine table base address >> 1
                        shl     angle,#1                'shift left to get final word address
                        rdword  angle,angle             'read sine word from table
                        negc    angle,angle             'if quadrant 4, negate word (max ± $FFFF)

' Process depth

:depth0                 mov     depth,depths            'get current depth
                        rdword  t1,depth_ptr            'get target depth
                        shl     t1,#15                  'shift up target for scaling space
                        sub     t1,depth                'get target-current difference
                        sar     t1,#19                  'scale difference
:veloc0                 mov     veloc,velocs            'get velocity
                        add     veloc,t1                'add scaled difference to velocity
                        add     depth,veloc             'add velocity to current depth
                        mins    depth,#0                'insure current depth doesn't go negative
:depth1                 mov     depths,depth            'store current depth
                        mov     t1,veloc                'dampen velocity
                        sar     t1,#8
                        sub     veloc,t1
:veloc1                 mov     velocs,veloc            'store velocity

                        shr     depth,#19-16            'reduce fractional depth to 16 bits

                        mov     t1,depth                'attenuate sample by depth and decay rate
                        shl     t1,decay                '(logarithmic curve approximates 1/d²)
                        sub     lx,t1           wc
        if_c            mov     lx,#0                   'clamp underflow

                        shr     depth,#16-8             'reduce fractional depth to 8 bits
                        max     depth,buff_clamp        'confine depth to buffer space

' Compute left and right samples with unique ear attenuations

                        mov     rx,lx                   'samples initially identical

                        shl     angle,#10               'shift up ear sine

                        mov     t1,h04000000            'attenuate left sample by left ear sine
                        add     t1,angle
                        sub     lx,t1           wc
        if_c            mov     lx,#0                   'clamp underflow

                        mov     t1,h04000000            'attenuate right sample by right ear sine
                        sub     t1,angle
                        sub     rx,t1           wc
        if_c            mov     rx,#0                   'clamp underflow

' Sum samples into left and right buffer channels at unique ear offsets

                        mov     channels,#2             'ready for left and right channels

                        mov     t1,angle                'multiply ear sine by 7/8 to get <700µs
                        sar     t1,#3                   '...ear-to-ear delay (<14 samples @20KHz)
                        sub     angle,t1
                        sar     angle,#10+5             'get +/- <7.0 into signed 3.8 format

                        mov     t1,angle                'get left ear sine (lx holds left ear sample)

:channel                add     t1,depth                'get ear sine + depth (with 8 fractional bits)

                        xor     buff_base,#2            'toggle left/right buffer channel

                        mov     t2,t1                   'compute buffer offset for sample summing
                        sar     t2,#8                   'get ear sine + depth whole offset
                        add     t2,#8                   'add center-of-head offset
                        shl     t2,#2                   'convert to long offset
                        add     t2,buff_ptr             'add buffer pointer
                        cmpsub  t2,buff_size            'insure buffer wrap
                        cmp     t2,buff_limit   wz      'remember if last location
                        add     t2,buff_base            'add buffer base with left/right channel

                        rol     lx,#5                   '** sample is in log form with isolated sign
                        movs    :shr,lx                 'get whole log
                        xor     :shr,#$1F               'not whole log
                        movs    lx,#$D                  'use fractional log to lookup antilog
                        rol     lx,#12
                        rdword  lx,lx
                        shl     lx,#15                  'msb-justify antilog with leading 1
                        or      lx,h80000000
:shr                    shr     lx,#0                   'shift antilog down by not whole log
                        test    sign,#1         wc      'restore sign
                        negc    lx,lx                   '** sample is now signed value

                        mov     t3,lx                   'get whole sample

                        sar     lx,#8                   'compute fractional sample for precise
                        shl     lx,#8-1                 '...inter-sample summation
                        and     t1,#$FF                 '(uses 8 fractional bits of ear sine + depth)
                        shr     t1,#1           wc
        if_c            add     t1,lx
                        sar     t1,#1           wc
        if_c            add     t1,lx
                        sar     t1,#1           wc
        if_c            add     t1,lx
                        sar     t1,#1           wc
        if_c            add     t1,lx
                        sar     t1,#1           wc
        if_c            add     t1,lx
                        sar     t1,#1           wc
        if_c            add     t1,lx
                        sar     t1,#1           wc
        if_c            add     t1,lx
                        sar     t1,#1           wc
        if_c            add     t1,lx

                        sub     t3,t1                   'get whole-minus-fractional sample

                        rdword  lx,t2                   'sum whole-minus-fractional sample into +0
                        shr     t3,#16
                        add     lx,t3
                        wrword  lx,t2

        if_nz           add     t2,#4                   'increment buffer offset
        if_z            mov     t2,buff_base            'insure buffer wrap

                        rdword  lx,t2                   'sum fractional sample into +1
                        shr     t1,#16
                        add     lx,t1
                        wrword  lx,t2

                        mov     lx,rx                   'get right ear sample
                        neg     t1,angle                'get right ear sine

                        djnz    channels,#:channel      'loop once for right channel

' Another input channel?

                        add     input_ptr,#2            'increment pointers
                        add     angle_ptr,#2
                        add     depth_ptr,#2
                        add     :angle0,#1
                        add     :angle1,h00000200
                        add     :depth0,#1
                        add     :depth1,h00000200
                        add     :veloc0,#1
                        add     :veloc1,h00000200

                        djnz    inputs,#:input          'another input channel?

                        sub     input_ptr,#4*2          'done, reset pointers
                        sub     depth_ptr,#4*2
                        sub     angle_ptr,#4*2
                        sub     :angle0,#4
                        sub     :angle1,h00000800
                        sub     :depth0,#4
                        sub     :depth1,h00000800
                        sub     :veloc0,#4
                        sub     :veloc1,h00000800

' Read output samples from buffer and write echoes back

                        mov     t1,buff_ptr             'read sample pair from buffer
                        add     t1,buff_base
                        rdlong  left,t1

                        add     buff_ptr,#4             'advance buffer pointer
                        cmpsub  buff_ptr,buff_size      'insure buffer wrap

                        wrlong  left,par                'update sample pair in main memory

                        mov     right,left              'unpack left and right samples
                        shl     right,#16
                        and     left,hFFFF0000

                        mov     lx,left                 'compute parallel echoes
                        sar     lx,pecho
                        mov     rx,right
                        sar     rx,pecho

                        mov     t2,right                'compute cross echoes
                        sar     t2,xecho
                        add     lx,t2
                        mov     t2,left
                        sar     t2,xecho
                        add     rx,t2

                        add     left,h80000000          'convert samples to duty cycles
                        add     right,h80000000

                        and     lx,hFFFF0000            'write echoes back to buffer
                        shr     rx,#16
                        or      lx,rx
                        wrlong  lx,t1

' Loop for next sample period

                        jmp     #loop


' ┌────────────────┐
' │  Defined Data  │
' └────────────────┘

hFFFF0000               long    $FFFF0000               'miscellaneous constants greater than 9 bits
hFF000000               long    $FF000000
hF0000000               long    $F0000000
hC0000000               long    $C0000000
h80000000               long    $80000000
h10000000               long    $10000000
h04000000               long    $04000000
h00007000               long    $00007000
h00000800               long    $00000800
h00000200               long    $00000200

lfsr0                   long    1                       'linear feedback shift registers for dither noise
taps0                   long    $A4000080
lfsr1                   long    1
taps1                   long    $80A01000

clear_cnt               long    $1F0 - reserves         'number of reserved registers to clear on startup


' ┌──────────────────────────────────────────────────┐
' │  Undefined Data (zeroed by initialization code)  │
' └──────────────────────────────────────────────────┘

reserves

cnt_value               res     1                       'reserved registers that get cleared on startup
cnt_ticks               res     1

buff_base               res     1
buff_size               res     1
buff_limit              res     1
buff_clamp              res     1
buff_ptr                res     1

input_ptr               res     1
angle_ptr               res     1
depth_ptr               res     1
knobs_ptr               res     1

decay                   res     1
pecho                   res     1
xecho                   res     1
dither                  res     1

sign                    res     1
inputs                  res     1
channels                res     1

angle                   res     1
depth                   res     1
veloc                   res     1

angles                  res     4
depths                  res     4
velocs                  res     4

t1                      res     1
t2                      res     1
t3                      res     1

lx                      res     1
rx                      res     1

left                    res     1
right                   res     1

