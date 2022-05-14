'' =================================================================================================
''
''   File....... jm_ansi.spin
''   Purpose.... ANSI terminal control sequences
''               * all methods return pointer to string
''   Author..... Jon "JonnyMac" McPhalen
''               Copyright (c) 2020-2022 Jon McPhalen
''               -- see below for terms of use
''   E-mail..... jon.mcphalen@gmail.com
''   Started....
''   Updated.... 13 JAN 2021
''
'' =================================================================================================


con

  ESC = 27

   #0,  BLACK,    RED,    GREEN,    YELLOW,    BLUE,    MAGENTA,    CYAN,    WHITE
   #8,  BR_BLACK, BR_RED, BR_GREEN, BR_YELLOW, BR_BLUE, BR_MAGENTA, BR_CYAN, BR_WHITE

  #(-1),  DEFAULT


dat

  Command       byte      ESC, "[", 0[24]                       ' empty command with build space


pub null

'' This is not a top level object


pub csi

  return string(ESC, "[")


con { PST control code replacements }


pub home

'' Move cursor to column 0, line 0

  return string(ESC, "[0;0H")


pub crsr_x(x) : p_str

'' Move cursor to column x

  p_str := @Command[2]                                          ' build ESC[xG
  p_str := dec(p_str, x, "G")

  byte[p_str] := 0                                              ' terminate command

  return @Command


pub crsr_y(y) : p_str

'' Move cursor to line y

  p_str := @Command[2]                                          ' build ESC[yd
  p_str := dec(p_str, y, "d")

  byte[p_str] := 0                                              ' terminate command

  return @Command


pub crsr_xy(x, y) : p_str

'' Move cursor to column x, line y

  p_str := @Command[2]                                          ' build ESC[y;xH
  p_str := dec(p_str, y+1, ";")
  p_str := dec(p_str, x+1, "H")

  byte[p_str] := 0                                              ' terminate command

  return @Command


pub crsr_up(n) : p_str

'' Move cursor up n lines

  p_str := dec(@Command[2], n, "A")                             ' build ESC[nA

  byte[p_str] := 0                                              ' terminate command

  return @Command


pub crsr_dn(n) : p_str

'' Move cursor down n lines

  p_str := dec(@Command[2], n, "B")                             ' build ESC[nB

  byte[p_str] := 0                                              ' terminate command

  return @Command


pub crsr_rt(n) : p_str

'' Move cursor right n columns

  p_str := dec(@Command[2], n, "C")                             ' build ESC[nC

  byte[p_str] := 0                                              ' terminate command

  return @Command


pub crsr_lf(n) : p_str

'' Move cursor left n columns

  p_str := dec(@Command[2], n, "D")                             ' build ESC[nD

  byte[p_str] := 0                                              ' terminate command

  return @Command


pub clr_eol

'' Clear from cursor to end of line

  return string(ESC, "[0K")


pub clr_dn

'' Clear from cursor down

  return string(ESC, "[0J")


pub cls

'' Clear entire screen

  return string(ESC, "[2J")


con { ANSI controls }


pub hide_cursor

'' Disable visible cursor

  return string(ESC, "[?25l")


pub show_cursor

'' Enable visible cursor

  return string(ESC, "[?25h")


pub normal

'' Reset all attributes

  return string(ESC, "[0m")


pub bold : p_str

'' Turn bold mode on

  return string(ESC, "[1m")


pub dim

'' Turn low intensity mode on

  return string(ESC, "[2m")


pub underline

'' Turn underline mode on

  return string(ESC, "[4m")


pub blink                                                       ' not widely supported

'' Turn blinking mode on

  return string(ESC, "[5m")


pub reverse

'' Turn reverse video on

  return string(ESC, "[7m")


pub hide                                                        ' not widely supported

'' Turn invisible text mode on

  return string(ESC, "[8m")


pub fgnd(color)

'' Set foreground color for text

  case color
    BLACK      : return string(ESC, "[30m")
    RED        : return string(ESC, "[31m")
    GREEN      : return string(ESC, "[32m")
    YELLOW     : return string(ESC, "[33m")
    BLUE       : return string(ESC, "[34m")
    MAGENTA    : return string(ESC, "[35m")
    CYAN       : return string(ESC, "[36m")
    WHITE      : return string(ESC, "[37m")
    BR_BLACK   : return string(ESC, "[90m")
    BR_RED     : return string(ESC, "[91m")
    BR_GREEN   : return string(ESC, "[92m")
    BR_YELLOW  : return string(ESC, "[93m")
    BR_BLUE    : return string(ESC, "[94m")
    BR_MAGENTA : return string(ESC, "[95m")
    BR_CYAN    : return string(ESC, "[96m")
    BR_WHITE   : return string(ESC, "[97m")
    other      : return string(ESC, "[39m")


pub bgnd(color)

'' Set background color for text

  case color
    BLACK      : return string(ESC, "[40m")
    RED        : return string(ESC, "[41m")
    GREEN      : return string(ESC, "[42m")
    YELLOW     : return string(ESC, "[43m")
    BLUE       : return string(ESC, "[44m")
    MAGENTA    : return string(ESC, "[45m")
    CYAN       : return string(ESC, "[46m")
    WHITE      : return string(ESC, "[47m")
    BR_BLACK   : return string(ESC, "[100m")
    BR_RED     : return string(ESC, "[101m")
    BR_GREEN   : return string(ESC, "[102m")
    BR_YELLOW  : return string(ESC, "[103m")
    BR_BLUE    : return string(ESC, "[104m")
    BR_MAGENTA : return string(ESC, "[105m")
    BR_CYAN    : return string(ESC, "[106m")
    BR_WHITE   : return string(ESC, "[107m")
    other      : return string(ESC, "[49m")


con { helpers }


pri dec(p_str, value, sep)

'' Converts value to decimal in p_str
'' -- for up to 3-digits values
'' -- sep is separator/terminator after value
''    * use -1 for no seperator/terminator
'' -- returns updated p_str

  if (value > 99)
    byte[p_str++] := "0" + (value / 100)
    value //= 100
    result := true                                              ' must include 10s digit

  if ((value > 9) or (result))
    byte[p_str++] := "0" + (value / 10)
    value //= 10

  byte[p_str++] := "0" + (value // 10)

  if (sep >= 0)
    byte[p_str++] := sep

  return p_str


con { license }

{{

  Terms of Use: MIT License

  Permission is hereby granted, free of charge, to any person obtaining a copy of this
  software and associated documentation files (the "Software"), to deal in the Software
  without restriction, including without limitation the rights to use, copy, modify,
  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
  permit persons to whom the Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be included in all copies
  or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
  PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
  CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

}}
