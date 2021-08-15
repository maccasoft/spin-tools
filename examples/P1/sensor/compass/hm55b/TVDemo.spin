' Author: Beau Schwabe
{{

            ┌────┬┬────┐
      ┌────│1  6│── +5V       P0 = Enable
      │ 1K  │  ├┴──┴┤  │               P1 = Clock
  P2 ┻──│2 │ /\ │ 5│── P0        P2 = Data
            │  │/  \│  │
    VSS ──│3 └────┘ 4│── P1
            └──────────┘

}}
CON
    _clkmode = XTAL1 + PLL16X
    _xinfreq = 5_000_000

    ' Setup Constants for the TV and Graphics display

    stack           = ($3000 + $3000 + 100) >> 2               'accommodate display memory and stack
    x_tiles         = 16
    y_tiles         = 12
    paramcount      = 14
    display_base    = $5000
    bitmap_base     = $2000

    ' Setup Constants for the Compass

    Enable  = 0
    Clock   = 1
    Data    = 2

VAR     ''Setup variables related to the TV display

    long  tv_status     '0/1/2 = off/visible/invisible           read-only
    long  tv_enable     '0/? = off/on                            write-only
    long  tv_pins       '%ppmmm = pins                           write-only
    long  tv_mode       '%ccinp = chroma,interlace,ntsc/pal,swap write-only
    long  tv_screen     'pointer to screen (words)               write-only
    long  tv_colors     'pointer to colors (longs)               write-only
    long  tv_hc         'horizontal cells                        write-only
    long  tv_vc         'vertical cells                          write-only
    long  tv_hx         'horizontal cell expansion               write-only
    long  tv_vx         'vertical cell expansion                 write-only
    long  tv_ho         'horizontal offset                       write-only
    long  tv_vo         'vertical offset                         write-only
    long  tv_broadcast  'broadcast frequency (Hz)                write-only
    long  tv_auralcog   'aural fm cog                            write-only
    word  screen[x_tiles * y_tiles]
    long  colors[64]

VAR     ''Setup variables related to converting numbers to strings for display

    long  idx           ' ( 1 long ) pointer into string
    byte  nstr[64]      ' (16 longs) string
    byte  z_pad

VAR     ''Setup variables related to the compass

    long CorrectHeading
    long Deg

OBJ     ''Setup Object references that make this demo work

    tv    :     "display.tv"
    gr    :     "display.tv.graphics"
    HM55B :     "sensor.compass.hm55b"
    Calibrate : "Calibration"

PUB DEMO_Initialization | i,dx,dy

    ' start tv

    longmove(@tv_status, @tvparams, paramcount)
    tv_screen := @screen
    tv_colors := @colors
    tv.start(@tv_status)

    ' init colors

    repeat i from 0 to 63
        colors[i] := $02_9D_BB_2A
{
                      Color 3
                      ││ Color 2
                      ││ ││ Color 1
                      ││ ││ ││ Color 0
                      ││ ││ ││ ││
        colors[i] := $02_9D_BB_2A

    Examples: (See Palette Demo for other available colors)
      BB RED
      2A Dark BLUE
      02 BLACK
      5B GREEN
      9D YELLOW
      CD PINK
      FC PURPLE
      BC ORANGE
      04 GREY
      3C Light BLUE

}
    ' init tile screen
    repeat dx from 0 to tv_hc - 1
        repeat dy from 0 to tv_vc - 1
            screen[dy * tv_hc + dx] := display_base >> 6 + dy + dx * tv_vc + ((dy & $3F) << 10)

    ' start and setup graphics
    gr.start                                            '' Initialize Graphics Object
    gr.setup(16, 12, 128, 96, bitmap_base)

    'start and setup Compass

    HM55B.start(Enable,Clock,Data)                      '' Initialize Compass Object

    Compass_Demo                                        '' Start the Compass DEMO

PUB Compass_Demo|RawHeading
    repeat
      gr.clear                                          ' Clear graphics screen

      gr.colorwidth(2, 0)                               ' Set Color and Width
      gr.textmode(1,1,7,%0100)                          ' Set text mode
      gr.text(0,82,string("HM55B Propeller Compass Demo"))         ' Display Header Text

      DrawCompass                                       ' Draw the compass

      RawHeading := HM55B.theta                         ' Read RAW 13-bit Angle

      CorrectHeading := Calibrate.Correct(RawHeading)   ' Calibrate Correct Heading

      Deg := CorrectHeading * 45 / 1024                 ' Convert 13-Bit Angle to Deg
                                                        ' Note: This only makes it easier for us Humans to
                                                        '       read.
''#########################################################
''#########################################################

''        This section for Calibration purposes only.   - See 'HM55B Compass Calibration.Spin'
''        You may remove or comment after calibration.

      gr.text(-125, -80,string("RAW"))                  ' Display RAW Heading as a 13-Bit Angle
      gr.text(-125, -95, string("Heading:"))
      gr.text(-65, -95, Dec(RawHeading/11*11))          ' Reduce returned Coordic value down to about 0.5 Deg
                                                        ' resolution.  This helps to reduce LSB jitter
      gr.finish
''#########################################################
''#########################################################


      gr.text(40, -80, string("Correct"))               ' Display Correct Heading as a Degree
      gr.text(40, -95, string("Heading:"))
      gr.text(100, -95, Dec(Deg))


      gr.finish

      DrawNeedle(1,RawHeading)                          ' Display Compass Needle Raw Heading Graphic
      DrawNeedle(2,CorrectHeading)                      ' Display Compass Needle Correct Heading Graphic

      gr.copy(display_base)                             ' copy bitmap to display

PUB DrawCompass|i                                         ' Draw compass
      gr.arc(0, 0, 60, 60, 0, 23, 360, 0)               '     Great circle
      repeat i from 0 to 32                             '     Draw ticks around Great circle
        gr.arc(0, 0, 60, 60, i*256, 0, 1, 0)
        gr.arc(0, 0, 55, 55, i*256, 0, 1, 1)

      gr.textmode(1,1,7,%0000)
      gr.text(-2, 60, string("0"))                      ' Associate Human readable Degree ticks
      gr.text(42, 42, string("45"))
      gr.text(64, -8, string("90"))
      gr.text(45, -55, string("135"))
      gr.text(-9, -76, string("180"))
      gr.text(-65, -55, string("225"))
      gr.text(-83, -8, string("270"))
      gr.text(-62, 42, string("315"))

      gr.text(-2, 38, string("N"))                      ' Spell the NEWS
      gr.text(45, -8, string("E"))
      gr.text(-50, -8, string("W"))
      gr.text(-2, -55, string("S"))

PUB DrawNeedle(_Color,_Deg)
    gr.color(_Color)
    gr.arc(0, 0, 50, 50, -_Deg+2048, 0, 1, 0)            ' Draw Compass needle
    gr.arc(0, 0, 50, 50, -_Deg+6144, 0, 1, 1)
    gr.arc(0, 0, 5, 5, -_Deg, 0, 1, 0)                   ' Draw needle cross
    gr.arc(0, 0, 5, 5, -_Deg+4096, 0, 1, 1)
    gr.arc(0, 0, 40, 40, -_Deg+2048-150, 0, 1, 0)        ' Draw needle arrow
    gr.arc(0, 0, 50, 50, -_Deg+2048, 0, 1, 1)
    gr.arc(0, 0, 40, 40, -_Deg+2048+150, 0, 1, 1)

PRI decstr(value) | div

' Converts value to signed-decimal string equivalent
' -- characters written to current position of idx
' -- returns pointer to nstr

  if (value < 0)                                         ' negative value?
    -value                                               '   yes, make positive
    nstr[idx++] := "-"                                   '   and print sign indicator

  div := 1_000_000_000                                   ' initialize divisor
  z_pad~                                                 ' clear zero-pad flag

  repeat 10
    if (value => div)                                    ' printable character?
      nstr[idx++] := (value / div + "0")                 '   yes, print ASCII digit
      value //= div                                      '   update value
      z_pad~~                                            '   set zflag
    elseif z_pad or (div == 1)                           ' printing or last column?
      nstr[idx++] := "0"
    div /= 10

  return @nstr

PRI Dec(value)
    bytefill(@nstr, 0, 64)                               ' clear string to zeros
    idx~                                                 ' reset index
    return decstr(value)

PUB Decx(value, digits) | div

'' Returns pointer to zero-padded, signed-decimal string
'' -- if value is negative, field width is digits+1

    bytefill(@nstr, 0, 64)                               ' clear string to zeros
    idx~                                                 ' reset index
    digits := 1 #> digits <# 10

    if (value < 0)                                       ' negative value?
      -value                                             '   yes, make positive
      nstr[idx++] := "-"                                 '   and print sign indicator
      div := 1_000_000_000                               ' initialize divisor
    if digits < 10                                       ' less than 10 digits?
      repeat (10 - digits)                               '   yes, adjust divisor
        div /= 10

    value //= (div * 10)                                 ' truncate unused digits

    repeat digits
      nstr[idx++] := (value / div + "0")                 ' convert digit to ASCII
      value //= div                                      ' update value
      div /= 10                                          ' update divisor

    return @nstr

DAT

tvparams                long    0                        ' status
                        long    1                        ' enable
                        long    %001_0101                ' pins
                        long    %0000                    ' mode
                        long    0                        ' screen
                        long    0                        ' colors
                        long    x_tiles                  ' hc
                        long    y_tiles                  ' vc
                        long    10                       ' hx
                        long    1                        ' vx
                        long    0                        ' ho
                        long    0                        ' vo
                        long    0                        ' broadcast
                        long    0                        ' auralcog

