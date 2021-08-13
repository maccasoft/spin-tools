''***************************************
''*  VGA Tile Driver Demo 2 v1.0        *
''*  Author: Chip Gracey                *
''*  Copyright (c) 2006 Parallax, Inc.  *
''*  See end of file for terms of use.  *
''***************************************

' 22 November 2006
'
' This program demonstrates the VGA_1280x1024_Tile_Driver_With_Cursor.
'
' It is set up to use the VGA port on the Propeller Demo Board.

CON

  _clkmode = xtal1 + pll16x
  _xinfreq = 5_000_000

  cols = 80
  rows = 64
  tiles = cols * rows

  spacetile = $220


OBJ

  vga     : "display.vga.tile.1280x1024"
  mouse   : "input.mouse"


VAR

  long  col, row, color
  long  boxcolor, boxptr

  long  array[tiles/2]
  long  cursor[1+32]

  long  cursor_x, cursor_y, cursor_col, cursor_def


PUB start | i, j, k

  'start mouse and set bound parameters
  mouse.start(24, 25)
  mouse.bound_limits(0, 0, 0, 1279, 1023, 0)
  mouse.bound_scales(1, 1, 0)
  mouse.bound_preset(640, 512, 0)

  'start vga tile driver
  vga.start(16, @array, @vgacolors, @cursor_x, 0, 0)

  'fill screen with text
  print($100)
  print_string(@text)

  box(30, 1, 0, string("Compile"))
  box(40, 1, 1, string("Execute"))
  box(50, 1, 2, string("Quit"))

  'keep updating screen
  repeat
    'set mouse cursor according to area
    if cursor_y => 800
      cursor_def := 1
      cursor_col := $FC
    elseif cursor_y => 400
      cursor_def := 0
      cursor_col := $F0
    else
      cursor_def := @propeller
      cursor_col := $FC

    'update mouse position
    cursor_x := mouse.bound_x
    cursor_y := mouse.bound_y



PRI box(left, top, clr, str) | width, height, x, y, i

' Draw a box

  boxcolor := $10 + clr
  color := clr + 5

  width := strsize(str)
  height := 2

  boxptr := top * cols + left
  boxchr($0)
  repeat i from 1 to width
    boxchr($C)
  boxchr($8)
  repeat i from 1 to height
    boxptr := (top + i) * cols + left
    boxchr($A)
    boxptr += width
    boxchr($B)
  boxptr := (top + height + 1) * cols + left
  boxchr($1)
  repeat i from 1 to width
    boxchr($D)
  boxchr($9)

  col := left + 1
  row := top + 1
  print_string(str)


PRI boxchr(c): i

  array.word[boxptr++] := $200 + c + boxcolor << 10


PRI print_string(ptr)

  repeat while byte[ptr]
    print(byte[ptr++])


PRI print(c) | i, k

'' Print a character
''
''       $0D = new line
''  $20..$FF = character
''      $100 = clear screen
''      $101 = home
''      $108 = backspace
''$110..$11F = select color

  case c
    $0D:                'return?
      newline

    $20..$FF:           'character?
      k := color << 1 + c & 1
      i := $200 + (c & $FE) + k << 10
      array.word[row * cols + col] := i
      array.word[(row + 1) * cols + col] := i | 1
      if ++col == cols
        newline

    $100:               'clear screen?
      wordfill(@array, spacetile, tiles)
      col := row := 0

    $101:               'home?
      col := row := 0

    $108:               'backspace?
      if col
        col--

    $110..$11F:         'select color?
      color := c & $F


PRI newline | i

  col := 0
  if (row += 2) == rows
    row -= 2
    'scroll lines
    repeat i from 0 to rows-3
      wordmove(@array.word[i*cols], @array.word[(i+2)*cols], cols)
    'clear new line
    wordfill(@array.word[(rows-2)*cols], spacetile, cols<<1)


DAT

propeller long

  long %00000000_00000000_00000000_00000000
  long %01111110_00000000_00000011_11111000
  long %11111111_11110001_10111111_11111110
  long %11111111_11111111_11111111_11111111
  long %01111111_11111101_10001111_11111111
  long %00011111_11000001_10000000_01111110
  long %00000000_00000011_11000000_00000000
  long %00000000_00000011_11000000_00000000
  long %00000000_00000001_10000000_00000000
  long %00000000_00001111_11110000_00000000
  long %00000000_01111001_10011110_00000000
  long %00000001_11110011_11001111_10000000
  long %00000011_11100011_11000111_11000000
  long %00000111_11000111_11100011_11100000
  long %00001111_10000111_11100001_11110000
  long %00011111_10000111_11100001_11111000
  long %00011111_00000111_11100000_11111000
  long %00111111_00001111_11110000_11111100
  long %00111110_00001111_11110000_01111100
  long %00111110_00001111_11110000_01111100
  long %01111110_00001111_11110000_01111110
  long %01111100_00001111_11110000_00111110
  long %01111100_00011111_11111000_00111110
  long %01111111_11111111_11111111_11111110
  long %01111111_11110000_00001111_11111110
  long %01111000_00000000_00000000_00011110
  long %01100000_00000000_00000000_00000110
  long %00111100_00000000_00000000_00111100
  long %00001111_11110000_00001111_11110000
  long %00000011_11111111_11111111_11000000
  long %00000000_00011111_11111000_00000000
  long %00000000_00000000_00000000_00000000
  byte 15,15

vgacolors long

  long $3C043C04       'lt grey on dk grey
  long $3C3C0404
  long $C000C000       'red
  long $C0C00000
  long $30003000       'green
  long $30300000
  long $0C000C00       'blue
  long $0C0C0000
  long $FC00FC00       'white
  long $FCFC0000
  long $FF80FF80       'red/white
  long $FFFF8080
  long $FF20FF20       'green/white
  long $FFFF2020
  long $FF28FF28       'cyan/white
  long $FFFF2828
  long $C0408080       'redbox
  long $3010F020       'greenbox
  long $3C142828       'cyanbox
  long $FC54A8A8       'greybox
  long $3C14FF28       'cyanbox+underscore
  long $F030C050       'graphics colors
  long $881430FC
  long $8008FCA4

text file "lincoln.txt"
     byte 0

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
