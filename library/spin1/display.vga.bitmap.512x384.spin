''********************************************
''*  VGA 512x384 2-Color Bitmap Driver v1.0  *
''*  Author: Chip Gracey                     *
''*  Copyright (c) 2006 Parallax, Inc.       *
''*  See end of file for terms of use.       *
''********************************************
''
'' This object generates a 512x384 pixel bitmap, signaled as 1024x768 VGA.
'' Each pixel is one bit, so the entire bitmap requires 512 x 384 / 32 longs,
'' or 6,144 longs (24KB). Color words comprised of two byte fields provide
'' unique colors for every 32x32 pixel group. These color words require 512/32
'' * 384/32 words, or 192 words. Pixel memory and color memory are arranged
'' left-to-right then top-to-bottom.
''
'' A sync indicator signals each time the screen is drawn (you may ignore).
''
'' You must provide buffers for the colors, pixels, and sync. Once started,
'' all interfacing is done via memory. To this object, all buffers are read-
'' only, with the exception of the sync indicator which gets written with a
'' non-0 value. You may freely write all buffers to affect screen appearance.
''

CON

' 512x384 settings - signals as 1024 x 768 @ 67Hz

  hp = 512      'horizontal pixels
  vp = 384      'vertical pixels
  hf = 8        'horizontal front porch pixels
  hs = 48       'horizontal sync pixels
  hb = 88       'horizontal back porch pixels
  vf = 1        'vertical front porch lines
  vs = 3        'vertical sync lines
  vb = 28       'vertical back porch lines
  hn = 1        'horizontal normal sync state (0|1)
  vn = 1        'vertical normal sync state (0|1)
  pr = 35       'pixel rate in MHz at 80MHz system clock (5MHz granularity)

' Tiles

  xtiles = hp / 32
  ytiles = vp / 32

' H/V inactive states

  hv_inactive = (hn << 1 + vn) * $0101


VAR long cog

PUB start(BasePin, ColorPtr, PixelPtr, SyncPtr) : okay | i, j

'' Start VGA driver - starts a COG
'' returns false if no COG available
''
''     BasePin = VGA starting pin (0, 8, 16, 24, etc.)
''
''    ColorPtr = Pointer to 192 words which define the "0" and "1" colors for
''               each 32x32 pixel group. The lower byte of each word contains
''               the "0" bit RGB data while the upper byte of each word contains
''               the "1" bit RGB data for the associated group. The RGB
''               data in each byte is arranged as %RRGGBB00 (4 levels each).
''
''               color word example: %%0020_3300 = "0" = gold, "1" = blue
''
''    PixelPtr = Pointer to 6,144 longs containing pixels that make up the 512 x
''               384 pixel bitmap. Longs' LSBs appear left on the screen, while
''               MSBs appear right. The longs are arranged in sequence from left-
''               to-right, then top-to-bottom.
''
''     SyncPtr = Pointer to long which gets written with non-0 upon each screen
''               refresh. May be used to time writes/scrolls, so that chopiness
''               can be avoided. You must clear it each time if you want to see
''               it re-trigger.

  'if driver is already running, stop it
  stop

  'implant pin settings and pointers, then launch COG
  reg_vcfg := $200000FF + (BasePin & %111000) << 6
  i := $FF << (BasePin & %011000)
  j := BasePin & %100000 == 0
  reg_dira := i & j
  reg_dirb := i & !j
  longmove(@color_base, @ColorPtr, 2)
  if (cog := cognew(@init, SyncPtr) + 1)
    return true


PUB stop | i

'' Stop VGA driver - frees a COG

  if cog
    cogstop(cog~ - 1)


DAT

'***********************************************
'* Assembly language VGA 2-color bitmap driver *
'***********************************************

                        org                             'set origin to $000 for start of program

' Initialization code - init I/O

init                    mov     dira,reg_dira           'set pin directions
                        mov     dirb,reg_dirb

                        movi    ctra,#%00001_101        'enable PLL in ctra (VCO runs at 4x)
                        movi    frqa,#(pr / 5) << 3     'set pixel rate

                        mov     vcfg,reg_vcfg           'set video configuration

' Main loop, display field and do invisible sync lines

field                   mov     color_ptr,color_base    'reset color pointer
                        mov     pixel_ptr,pixel_base    'reset pixel pointer
                        mov     y,#ytiles               'set y tiles
:ytile                  mov     yl,#32                  'set y lines per tile
:yline                  mov     yx,#2                   'set y expansion
:yexpand                mov     x,#xtiles               'set x tiles
                        mov     vscl,vscl_pixel         'set pixel vscl

:xtile                  rdword  color,color_ptr         'get color word
                        and     color,colormask         'clear h/v bits
                        or      color,hv                'set h/v inactive states
                        rdlong  pixel,pixel_ptr         'get pixel long
                        waitvid color,pixel             'pass colors and pixels to video
                        add     color_ptr,#2            'point to next color word
                        add     pixel_ptr,#4            'point to next pixel long
                        djnz    x,#:xtile               'another x tile?

                        sub     color_ptr,#xtiles * 2   'repoint to first colors in same line
                        sub     pixel_ptr,#xtiles * 4   'repoint to first pixels in same line

                        mov     x,#1                    'do horizontal sync
                        call    #hsync

                        djnz    yx,#:yexpand            'y expand?

                        add     pixel_ptr,#xtiles * 4   'point to first pixels in next line
                        djnz    yl,#:yline              'another y line in same tile?

                        add     color_ptr,#xtiles * 2   'point to first colors in next tile
                        djnz    y,#:ytile               'another y tile?


                        wrlong   colormask,par          'visible done, write non-0 to sync

                        mov     x,#vf                   'do vertical front porch lines
                        call    #blank
                        mov     x,#vs                   'do vertical sync lines
                        call    #vsync
                        mov     x,#vb                   'do vertical back porch lines
                        call    #vsync

                        jmp     #field                  'field done, loop


' Subroutine - do blank lines

vsync                   xor     hvsync,#$101            'flip vertical sync bits

blank                   mov     vscl,hvis               'do blank pixels
                        waitvid hvsync,#0
hsync                   mov     vscl,#hf                'do horizontal front porch pixels
                        waitvid hvsync,#0
                        mov     vscl,#hs                'do horizontal sync pixels
                        waitvid hvsync,#1
                        mov     vscl,#hb                'do horizontal back porch pixels
                        waitvid hvsync,#0
                        djnz    x,#blank                'another line?
hsync_ret
blank_ret
vsync_ret               ret


' Data

reg_dira                long    0                       'set at runtime
reg_dirb                long    0                       'set at runtime
reg_vcfg                long    0                       'set at runtime

color_base              long    0                       'set at runtime (2 contiguous longs)
pixel_base              long    0                       'set at runtime

vscl_pixel              long    1 << 12 + 32            '1 pixel per clock and 32 pixels per set
colormask               long    $FCFC                   'mask to isolate R,G,B bits from H,V
hvis                    long    hp                      'visible pixels per scan line
hv                      long    hv_inactive             '-H,-V states
hvsync                  long    hv_inactive ^ $200      '+/-H,-V states


' Uninitialized data

color_ptr               res     1
pixel_ptr               res     1
color                   res     1
pixel                   res     1
x                       res     1
y                       res     1
yl                      res     1
yx                      res     1

