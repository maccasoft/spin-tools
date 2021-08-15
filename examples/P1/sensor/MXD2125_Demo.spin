''******************************************
''*  Memsic Dual Accelerometer Demo   v1.0 *
''*  Author: Paul Baker                    *
''*  Copyright (c) 2007 Parallax, Inc.     *
''*  See end of file for terms of use.     *
''******************************************
CON
  _CLKMODE = XTAL1 + PLL16X
  _XINFREQ = 5_000_000

  _stack = ($3000 + $3000 + 100) >> 2                   'accommodate display memory and stack

  'Constants used by the 3D code
  D           = 500             'Distance and Perspective
  SD          = 400

  CX          =  0              'Screen Center X
  CY          =  0              'Screen Center Y

  MaxPoints   = 40

  'Constants used by the Accelerometer Object
  Xout_pin    =  0              'Propeller pin MX2125 X out is connected to
  Yout_pin    =  1              'Propeller pin MX2125 Y out is connected to

  'Constants used by the Television Object
  x_tiles = 16
  y_tiles = 12

  paramcount = 14
  bitmap_base = $2000
  display_base = $5000


VAR
    'Variables used by the Television Object
    long              tv_status                         '0/1/2 = off/visible/invisible                  read-only
    long              tv_enable                         '0/? = off/on                                   write-only
    long              tv_pins                           '%ppmmm = pins                                  write-only
    long              tv_mode                           '%ccinp = chroma,interlace,ntsc/pal,swap        write-only
    long              tv_screen                         'pointer to screen (words)                      write-only
    long              tv_colors                         'pointer to colors (longs)                      write-only
    long              tv_hc                             'horizontal cells                               write-only
    long              tv_vc                             'vertical cells                                 write-only
    long              tv_hx                             'horizontal cell expansion                      write-only
    long              tv_vx                             'vertical cell expansion                        write-only
    long              tv_ho                             'horizontal offset                              write-only
    long              tv_vo                             'vertical offset                                write-only
    long              tv_broadcast                      'broadcast frequency (Hz)                       write-only
    long              tv_auralcog                       'aural fm cog                                   write-only

    word              screen[x_tiles * y_tiles]
    long              colors[64]

    'Variables used by the 3D code
    long              R1,R2,R3                          'rotation angles
    long              S1,S2,S3                          'sine of rotation angles
    long              C1,C2,C3                          'cosine of rotation angles
    long              PointX,PointY,PointZ              'current display point
    long              ScnPntX[MaxPoints],ScnPntY[MaxPoints],ScnPntZ[MaxPoints],SX[MaxPoints],SY[MaxPoints]
    long              TEMPX,TEMPY,TEMPZ
    long              Points
    long              Lines

    'Variables used to convert raw sensor data into degree tilt
    long              offset, scale

OBJ
    tv    :       "display.tv"                                  'located in default Library
    gr    :       "display.tv.graphics"                            'located in default Library
    accel :       "sensor.accel.dual.mxd2125"

PUB Setup
  offset := 90 * (clkfreq / 200)                        'offset value for sensor data conversion
  scale  := clkfreq / 800                               'scale value for sensor data conversion

  accel.start(Xout_pin,Yout_pin)                        'load a cog with accelerometer driver           '

  Start_TV_and_graphics                                 'load TV and graphics cogs

  repeat               'main program loop
    gr.clear                                            'clear bitmap

    gr.colorwidth(2, 0)                                 'Set Color and Width
    gr.textmode(1,1,7,%0100)                            'Set text mode
    gr.text(0,82,string("Dual Axis Accelerometer Demo"))'Display Header Text

    'set angles of plane
    R1 := 0                                      'Rotation angle between X and Y axis (not used with 2 axis sensor)
    R2 := (accel.x*90-offset)/scale * -1         'Rotation angle between X and Z axis (-1 used to reflect display's X axis)
    R3 := (accel.y*90-offset)/scale - 90         'Rotation angle between Y and Z axis (-90 used to rotate display's Y axis)

    TranslatePoints                                     'Perform rotational translation and 3D->2D projection
    DrawPolygons                                        'Draw 2D projected wireframe

    gr.copy(display_base)                               'copy bitmap to display

PRI Start_TV_and_graphics | i,dx,dy
  'start tv
  longmove(@tv_status, @tvparams, paramcount)           'copy initial values for TV object into variable space
  tv_screen := @screen                                  'initalize pointers
  tv_colors := @colors
  tv.start(@tv_status)                                  'load cog with TV driver

  'init colors
  repeat i from 0 to 63                                 'initialize color mapping for display tiles
    colors[i] := $00001010 * (5+4) & $F + $2B060C02

  'init tile screen
  repeat dx from 0 to tv_hc - 1                         'initialize tile pointers to region of memory containing data
    repeat dy from 0 to tv_vc - 1
      screen[dy * tv_hc + dx] := display_base >> 6 + dy + dx * tv_vc + ((dy & $3F) << 10)

  'start and setup graphics
  gr.start                                              'load cog with graphics driver
  gr.setup(16, 12, 128, 96, bitmap_base)                'initialize graphics driver

PRI cos(angle) : x

'' Get cosine of angle (0-8191)

  x := sin(angle + $800)

PRI sin(angle) : y

'' Get sine of angle (0-8191)

  y := angle << 1 & $FFE
  if angle & $800
    y := word[$F000 - y]
  else
    y := word[$E000 + y]
  if angle & $1000
    -y

{*******************************************************************************
* Following code is a stripped down version of Beau Schwabe's 3D graphics demo *
* availible at http://forums.parallax.com/forums/default.aspx?f=25&m=144641    *
********************************************************************************}

PRI TranslatePoints|i

    Points := PLANE[0]                                  'Retrieve # of points from DataBase
    Lines  := PLANE[1]                                  'Retrieve # of lines from DataBase

    S1 := sin((R1*1024)/45)                             'Convert DEG (0-360) to 13-Bit Sine and Cosine value
    S2 := sin((R2*1024)/45)
    S3 := sin((R3*1024)/45)
    C1 := cos((R1*1024)/45)
    C2 := cos((R2*1024)/45)
    C3 := cos((R3*1024)/45)

    repeat i from 1 to Points                          'Rotate Points
      PointX := ~PLANE[((i-1)*3)+2]                     'Retrieve X coordinate from DataBase
      PointY := ~PLANE[((i-1)*3)+3]                     'Retrieve Y coordinate from DataBase
      PointZ := ~PLANE[((i-1)*3)+4]                     'Retrieve Z coordinate from DataBase

      TEMPX := (PointX * C2 - PointZ * S2) / 65535      'Rotate points around the y axis.
      TEMPZ := (PointX * S2 + PointZ * C2) / 65535

      ScnPntZ[i] := (TEMPZ * C1 - PointY * S1) / 65535  'Rotate points around the x axis.
      TEMPY := (TEMPZ * S1 + PointY * C1) / 65535

      ScnPntX[i] := (TEMPX * C3 + TEMPY * S3)/ 65535    'Rotate points around the z axis.
      ScnPntY[i] := (TEMPY * C3 - TEMPX * S3)/ 65535

      TEMPZ := ScnPntZ[i] - SD                          'CONVERT 3D TO 2D

      SX[i] := ScnPntX[i] * D / TEMPZ + CX              'Note: If TEMPZ < 0 points are off screen
      SY[i] := ScnPntY[i] * D / TEMPZ + CY

PRI  DrawPolygons|i,coord1,coord2,color
     repeat i from 1 to Lines
       coord1 := PLANE[((Points * 3)+2)+((i-1)*3)]
       coord2 := PLANE[((Points * 3)+3)+((i-1)*3)]
       color  := PLANE[((Points * 3)+4)+((i-1)*3)]

       gr.color(color)                                  'set color
       gr.plot(SX[coord1],SY[coord1])                   'draw point
       gr.line(SX[coord2],SY[coord2])                   'draw line


DAT
                    'Data representation example for a 3D object

                        'number of points , number of lines
PLANE                   byte    8, 7                   'Indicate number of points and lines; Bytes = 2 + (points + lines) * 3
                        'Location of points (x, y, z)   values for x, y, and z must be limited to ±127
                        '      x
                        '      │   y
                        '      │   │   z
                        '            
                        'planar points
                        byte   0,  50, -50         '── point 1
                        byte   0, -50, -50         '── point 2
                        byte   0,  50,  50         '── point 3
                        byte   0, -50,  50         '── point 4
                        'normal vector points
                        byte   0,   0,   0         '── point 5
                        byte -50,   0,   0         '── point 6
                        byte -30,  20,   0         '── point 7
                        byte -30, -20,   0         '── point 8

                        'points listed make lines from point A to point B in color.
                        '      point A
                        '      │   point B
                        '      │   │   color
                        '            
                        'planar lines
                        byte   1,  2,  2           '── line 1
                        byte   1,  3,  2           '── line 2
                        byte   2,  4,  2           '── line 3
                        byte   3,  4,  3           '── line 4
                        'normal vector lines
                        byte   5,  6,  1           '── line 5
                        byte   6,  7,  1           '── line 6
                        byte   6,  8,  1           '── line 7


tvparams                long    0               'status
                        long    1               'enable
                        long    %001_0101       'pins demo Board
                        long    %0000           'mode
                        long    0               'screen
                        long    0               'colors
                        long    x_tiles         'hc
                        long    y_tiles         'vc
                        long    10              'hx
                        long    1               'vx
                        long    0               'ho
                        long    0               'vo
                        long    60_000_000      '_xinfreq<<4 'broadcast
                        long    0               'auralcog                                            CON

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
