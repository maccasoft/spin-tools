'' =================================================================================================
''
''   File....... jm_nstr.spin
''   Purpose.... Convert numbers to strings
''   Authors.... Jon McPhalen
''               -- Copyright (c) 2020-2022 Jon McPhalen
''               -- see below for terms of use
''   E-mail..... jon.mcphalen@gmail.com
''   Started....
''   Updated.... 24 JUN 2021
''
'' =================================================================================================


con

  NBUF_SIZE = 48
  PBUF_SIZE = 128


var

' byte  nbuf[NBUF_SIZE]                                         ' number conversions
' byte  pbuf[PBUF_SIZE]                                         ' padded strings


dat

  nbuf          byte    0[NBUF_SIZE]
  pbuf          byte    0[PBUF_SIZE]


pub null

'' This is not an application


pub fmt_number(value, radix, digits, width, pad) : p_str        ' *** changed 23 JUN 2021 ***

'' Return pointer to string of value converted to number in padded field
'' -- value is converted using radix
'' -- radix is chararacter indicating type                      ' *** used to be only number ***
'' -- digits is max number of digits to use
'' -- width is width of final fields (max)
'' -- pad is character used to pad final field (if needed)

  case radix
    "d", "D", 10 : p_str := padstr(dec(value, digits),      width, pad)
'   "u", "U", 11 : p_str := padstr(usdec(value, digits),    width, pad)
    "f", "F", 12 : p_str := padstr(dpdec(value, digits),    width, pad)
    "b", "B", 02 : p_str := padstr(itoa(value,  2, digits), width, pad)
    "q", "Q", 04 : p_str := padstr(itoa(value,  4, digits), width, pad)
    "o", "O", 08 : p_str := padstr(itoa(value,  8, digits), width, pad)
    "x", "X", 16 : p_str := padstr(itoa(value, 16, digits), width, pad)
    other        : p_str := string("?")


pub dec(value, digits) : p_str | sign, len

'' Convert decimal value to string
'' -- digits is 0 (auto size) to 10

  bytefill(@nbuf, 0, NBUF_SIZE)                                 ' clear buffer
  p_str := @nbuf + (NBUF_SIZE-2)                                ' move to end
  longfill(@sign, 0, 2)                                         ' clear locals

  if (value < 0)                                                ' negative?
    if (value == negx)                                          ' negx is special
      sign := 2
      value := posx
    else
      sign := 1
      value := -value

  digits := 0 #> digits <# 10                                   ' constrain to long

  repeat
    byte[--p_str] := "0" + (value // 10)                        ' get digit, convert to ASCII
    value /= 10                                                 ' remove that digit
    if (digits)                                                 ' length limited?
      if (++len == digits)                                      '  check size
        quit
    else
      if (value == 0)                                           ' done?
        quit

  if (sign)                                                     ' negative?
    byte[--p_str] := "-"                                        '  add sign indicator
    if (sign == 2)
      nbuf[NBUF_SIZE-2] := "8"                                  '  fix negx

  bytemove(@nbuf, p_str, strsize(p_str)+1)                      ' align with nbuf[0]

  p_str := @nbuf


pub dpdec(value, dp) : p_str | len, scratch[5]

'' Convert value to string with decimal point
'' -- dp is digits after decimal point
'' -- returns pointer to updated fp string
'' -- modifies original string
'' -- return pointer to converted string

  p_str := dec(value, 0)                                        ' convert to string

  if (dp > 0)
    len := strsize(p_str)                                       ' digits
    longfill(@scratch, 0, 5)                                    ' clear scratch buffer

    if (value < 0)                                              ' ignore "-" if present
      ++p_str
      --len

    if (len < (dp+1))                                           ' insert 0s?
      bytemove(@scratch, p_str, len)                            ' move digits to scratch buffer
      bytefill(p_str, "0", dp+2-len)                            ' pad string with 0s
      bytemove(p_str+dp+2-len, @scratch, len+1)                 ' move digits back
      byte[p_str+1] := "."                                      ' insert dpoint
    else
      bytemove(@scratch, p_str+len-dp, dp)                      ' move decimal part to buffer
      byte[p_str+len-dp] := "."                                 ' insert dpoint
      bytemove(p_str+len-dp+1, @scratch, dp+1)                  ' move decimal part back

    if (value < 0)                                              ' fix pointer for negative #s
      --p_str


pub itoa(value, radix, digits) : p_str | shift, mask, len, d

'' Convert to value power-of-2 string
'' -- radix --> 2: binary, 4: quarternary, 8: octal, 16: hexadecimal
'' -- digits is 0 (auto size) to limit for long using radix

  bytefill(@nbuf, 0, NBUF_SIZE)                                 ' clear buffer
  p_str := @nbuf + (NBUF_SIZE-2)                                ' move to end

  case radix                                                    ' fix digits
    02    : digits := 0 #> digits <# 32
    04    : digits := 0 #> digits <# 16
    08    : digits := 0 #> digits <# 11
    10    : digits := 0 #> digits <# 10
    16    : digits := 0 #> digits <#  8
    other : return p_str

  shift := >|radix - 1                                          ' bits per digit
  mask := ((2 << shift) - 1) >> 1                               ' bit mask for digit
  len := 0                                                      ' length of string

  repeat
    d := value & mask                                           ' get right-most digit
    if (d < 10)                                                 ' convert to ASCII
      byte[--p_str] := "0" + d
    else
      byte[--p_str] := "A" + (d - 10)
    value >>= shift                                             ' remove digit
    if (digits)                                                 ' length limited?
      if (++len == digits)                                      '  check size
        quit
    else
      if (value == 0)                                           ' done?
        quit

  bytemove(@nbuf, p_str, strsize(p_str)+1)                      ' align with nbuf[0]

  p_str := @nbuf


pub padstr(p_str, width, padchar) : p_pad | len

'' Pad string with padchar character
'' -- positive width uses left pad, negative field width uses right pad
''    * width = 99 centers string in field
'' -- truncate if string len > width
'' -- input string is not modified
'' -- returns pointer to padded string

  bytefill(@pbuf, 0, PBUF_SIZE)                                 ' clear padded buffer
  len := strsize(p_str)                                         ' get length of input
  width := -PBUF_SIZE+1 #> width <# PBUF_SIZE-1                 ' constrain to buffer size

  if (width > 0)                                                ' right-justify in padded field
    if (width > len)
      bytefill(@pbuf, padchar, width-len)
      bytemove(@pbuf+width-len, p_str, len)
      p_pad := @pbuf
    else
      bytemove(@pbuf, p_str+len-width, width)                   ' truncate to right-most characters
      p_pad := @pbuf

  elseif (width < 0)                                            ' left-justify in padded field
    width := -width
    if (width > len)
      bytemove(@pbuf, p_str, len)
      bytefill(@pbuf+len, padchar, width-len)
      p_pad := @pbuf

    else
      bytemove(@pbuf, p_str, width)                             ' truncate to leftmost characters
      p_pad := @pbuf

  else
    p_pad := p_str                                              ' 0 width -- return original


con { license }

{{

  Terms of Use: MIT License

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.

}}
