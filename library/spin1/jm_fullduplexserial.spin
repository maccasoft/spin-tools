'' =================================================================================================
''
''   File....... jm_fullduplexserial.spin
''   Purpose.... Buffered serial communications with numeric formatting
''   Authors.... Chip Gracey, Jeff Martin, Daniel Harris
''               -- reformatted, code additions, and updates to PASM driver by Jon McPhalen
''               -- see below for terms of use
''   E-mail..... jon.mcphalen@gmail.com
''   Started....
''   Updated.... 22 JAN 2022
''
'' =================================================================================================

{{

   Revisions:
     22 JAN 2022 - Changed f__() methods to fx__() to match P2 version

     24 JUN 2021 - Trimmed code needed for formatted output

     23 FEB 2020 - Fixed and improved formatted outputs
                   * decimal and string fields can be padded on right or left

     15 FEB 2020 - Added output via format control strings

     16 JAN 2020 - Added octal formatting

     19 NOV 2017 - added .available() method

     07 OCT 2014 - Reformatted by Jon McPhalen
                 - updated buffer size
                 - includes rjdec() method for right-justified decimal values

     01 MAY 2011 - additional comments added

     07 MAY 2009 - Fixed bug in dec method causing largest negative value
                    (-2,147,483,648) to be output as -0

     01 MAR 2006 - Initial release


   Connections:

             4.7K
     rxpin ───── TTL level RX line (5.0v tolerant with >3.9K+)
     txpin ─────── TTL level TX line (3.3v)

     For open-drain/source connections use a 4.7K-10K pull-up (open-drain) or
     pull-down (open-source) resistor on RX and TX line(s)

}}


con { fixed io pins }

  PGM_RX = 31  { I }                                            ' serial / programming
  PGM_TX = 30  { O }

  EE_SDA = 29  { I/O }                                          ' i2c / eeprom
  EE_SCL = 28  { I/O }


con { buffer setting }

  BUF_SIZE  = 128                                               ' 16, 32, 64, 128, 256, or 512
  BUF_MASK  = BUF_SIZE - 1


con { pst formatting }

' Control code for Parallax Serial Terminal

  HOME    =  1
  CRSR_XY =  2         ' next two bytes in stream are x and y positions values
  CRSR_LF =  3
  CRSR_RT =  4
  CRSR_UP =  5
  CRSR_DN =  6
  BELL    =  7
  BKSP    =  8
  TAB     =  9
  LF      = 10
  CLR_EOL = 11         ' clear to end of line
  CLR_DN  = 12         ' clear everything after cursor
  F_FEED  = 12
  CR      = 13
  CRSR_X  = 14         ' next byte in stream is x position value
  CRSR_Y  = 15         ' next byte in stream is y position value
  CLS     = 16


obj

  nstr : "jm_nstr"                                              ' formatting routines


var { object globals }

  long  cog                                                     ' cog (+1) running uart code

  ' 9 longs, MUST be contiguous

  long  rxhead
  long  rxtail
  long  txhead
  long  txtail
  long  rxpin
  long  txpin
  long  rxtxmode
  long  bitticks
  long  bufpntr

  byte  rxbuffer[BUF_SIZE]                                      ' transmit and receive buffers
  byte  txbuffer[BUF_SIZE]


pub null

'' This is not a top-level object


pub tstart(baud)

'' Start FDS with default pins/mode for terminal (e.g., PST)

  return start(PGM_RX, PGM_TX, %0000, baud)


pub start(rxp, txp, mode, baud)

'' Start serial driver (uses a cog, returns 1 to 8 if successful)
'' -- rxp.... recieve pin (0..31)
'' -- txp.... transmit pin (0..31)
'' -- mode... %xxx1 = invert rx
''            %xx1x = invert tx
''            %x1xx = open-drain/open-source tx
''            %1xxx = ignore tx echo on rx (for half-duplex on one pin)

  stop                                                          ' stop if running

  longfill(@rxhead, 0, 4)                                       ' clear buffer indexes
  longmove(@rxpin, @rxp, 3)                                     ' copy pins and mode
  bitticks := clkfreq / baud                                    ' system ticks per bit
  bufpntr := @rxbuffer                                          ' hub address of rxbuffer

  cog := cognew(@fdsuart, @rxhead) + 1                          ' start the fds uart cog

  return cog                                                    ' return 1..8 if started, 0 if not


pub stop

'' Stop serial driver
'' -- frees a cog if driver was running

  if (cog)                                                      ' cog active?
    cogstop(cog - 1)                                            '  yes, shut it down
    cog := 0                                                    '  and mark stopped


pub rxflush

'' Flush receive buffer

  repeat
  while (rxcheck => 0)


pub available : count

'' Returns # of bytes waiting in rx buffer

  if (rxtail <> rxhead)                                         ' if byte(s) available
    count := rxhead - rxtail                                    ' get count
    if (count < 0)
      count += BUF_SIZE                                         ' fix for wrap around


pub rxcheck : b

'' Check if byte received (never waits)
'' -- returns -1 if no byte received, $00..$FF if byte

  if (rxtail <> rxhead)                                         ' if byte(s) in buffer
    b := rxbuffer[rxtail]                                       ' get next available
    rxtail := ++rxtail & BUF_MASK                               ' increment pointer, wrap if needed
  else
    b := -1                                                     ' no byte ready


pub rx : b

'' Receive byte (will block if no byte available)
'' -- returns $00..$FF

  repeat
  while ((b := rxcheck) < 0)


pub rxtime(ms) : b | mstix, t

'' Wait ms milliseconds for a byte to be received
'' -- returns -1 if no byte received, $00..$FF if byte

  mstix := clkfreq / 1000

  t := cnt
  repeat
  until ((b := rxcheck) => 0) or (((cnt-t) / mstix) => ms)


pub rxticks(n) : b | t

'' Waits n clock ticks for a byte to be received
'' -- returns -1 if no byte received

  t := cnt
  repeat
  until ((b := rxcheck) => 0) or ((cnt-t) => n)


pub tx(b)

'' Emit byte b
'' -- may wait for room in buffer

  repeat
  until (txtail <> (txhead + 1) & BUF_MASK)                     ' wait for room in buffer

  txbuffer[txhead] := b                                         ' move byte to buffer
  txhead := ++txhead & BUF_MASK                                 ' increment buffer index, wrap if needed

  if (rxtxmode & %1000)                                         ' if half-duplex on same pin
    rx                                                          '  pull tx'd byte from rx buffer


pub txn(b, n)

'' Emit byte n times

  repeat n
    tx(b)


pub txflush

'' Wait for transmit buffer to empty, then wait for final byte to transmit

  repeat
  until (txtail == txhead)

  repeat 11                                                     ' start + 8 + 2
    waitcnt(bitticks + cnt)                                     ' time enough for final byte


pub str(p_str)

'' Emit a string
'' -- p_str in pointer to 0-terminated string

  repeat strsize(p_str)                                         ' loop through string
    tx(byte[p_str++])                                           ' tx char, advance pointer


pub substr(p_str, len) | b

'' Emit len characters of string at p_str
'' -- aborts if end of string detected

  repeat len
    b := byte[p_str++]                                          ' get a byte from string
    if (b > 0)                                                  ' if not at end
      tx(b)                                                     '   send it
    else
      quit


pub padstr(p_str, width, pad)

'' Emit p_str as padded field of width characters
'' -- pad is character to use to fill out field
'' -- positive width causes right alignment
'' -- negative width causes left alignment

  str(nstr.padstr(p_str, width, pad))


con { formatted strings }

{{
    Escape sequences

      \\          backslash char
      \%          percent char
      \q          double quote
      \b          backspace
      \t          tab (horizontal tab)
      \n          new line (vertical tab)
      \r          carriage return
      \nnn        arbitrary ASCII value (nnn is decimal)

    Formatted arguments

      %w.pf       print argument as decimal width decimal point
      %[w[.p]]d   print argument as decimal
      %[w[.p]]x   print argument as hex
      %[w[.p]]o   print argument as octal
      %[w[.p]]q   print argument as quarternary
      %[w[.p]]b   print argument as binary
      %[w]s       print argument as string
      %[w]c       print argument as character (

                  -- w is field width
                     * positive w causes right alignment in field
                     * negative w causes left align ment in field
                  -- %ws aligns s in field (may truncate)
                  -- %wc prints w copies of c
                  -- p is precision characters
                     * number of characters to use, aligned in field
                       -- for %w.pf, p is number of digits after dpoint

}}


pub fstr0(p_str)

'' Emit string with formatting characters.

  format(p_str, 0)


pub fstr1(p_str, arg1)

'' Emit string with formatting characters and one argument.

  format(p_str, @arg1)


pub fstr2(p_str, arg1, arg2)

'' Emit string with formatting characters and two arguments.

  format(p_str, @arg1)


pub fstr3(p_str, arg1, arg2, arg3)

'' Emit string with formatting characters and three arguments.

  format(p_str, @arg1)


pub fstr4(p_str, arg1, arg2, arg3, arg4)

'' Emit string with formatting characters and four arguments.

  format(p_str, @arg1)


pub fstr5(p_str, arg1, arg2, arg3, arg4, arg5)

'' Emit string with formatting characters and five arguments.

  format(p_str, @arg1)


pub fstr6(p_str, arg1, arg2, arg3, arg4, arg5, arg6)

'' Emit string with formatting characters and six arguments.

  format(p_str, @arg1)


pub format(p_str, p_args) | idx, c, asc, field, digits

'' Emit formatted string with escape sequences and embedded values
'' -- p_str is a pointer to the format control string
'' -- p_args is pointer to array of longs that hold field values
''    * field values can be numbers, characters, or pointers to strings

  idx := 0                                                     ' value index

  repeat
    c := byte[p_str++]
    if (c == 0)
      return

    elseif (c == "\")
      c := byte[p_str++]
      if (c == "\")
        tx("\")
      elseif (c == "%")
        tx("%")
      elseif (c == "q")
        tx(34)
      elseif (c == "b")
        tx(BKSP)
      elseif (c == "t")
        tx(TAB)
      elseif (c == "n")
        tx(LF)
      elseif (c == "r")
        tx(CR)
      elseif ((c => "0") and (c =< "9"))
        --p_str
        p_str := get_nargs(p_str, @asc, @digits)
        if ((asc => 0) and (asc =< 255))
          tx(asc)

    elseif (c == "%")
      p_str := get_nargs(p_str, @field, @digits)
      c := byte[p_str++]
      if (lookdown(c : "dfbqoxDFBQOX"))
        str(nstr.fmt_number(long[p_args][idx++], c, digits, field, " "))
      elseif (c == "s")
        str(nstr.padstr(long[p_args][idx++], field, " "))
      elseif (c == "c")
        txn(long[p_args][idx++], 1 #> ||field)

    else
      tx(c)


pri get_nargs(p_str, p_narg1, p_narg2) | val1, val2, c, sign

'' Parse one or two numbers from string in n, -n, n.n, or -n.n format
'' -- dpoint separates values
'' -- only first # may be negative
'' -- returns pointer to 1st char after value(s)

  longfill(@val1, 0, 2)                                         ' pre-set locals
  sign := 1

  c := byte[p_str]                                              ' check for negative on first value
  if (c == "-")
    sign := -1
    ++p_str

  repeat                                                        ' get first value
    c := byte[p_str++]
    if ((c => "0") and (c =< "9"))
      val1 *= 10
      val1 += c - "0"
    else
      quit

  if (c == ".")                                                 ' if dpoint
    repeat                                                      '  get second value
      c := byte[p_str++]
      if ((c => "0") and (c =< "9"))
        val2 *= 10
        val2 += c - "0"
      else
        quit

  long[p_narg1] := val1 * sign                                  ' update caller vars
  long[p_narg2] := val2

  return --p_str                                                ' back up to non-digit


pub fmt_number(value, base, digits, width, pad)

'' Emit value converted to number in padded field
'' -- value is converted using base as radix
''    * 99 for decimal with digits after decimal point
'' -- digits is max number of digits to use
'' -- width is width of final field (max)
'' -- pad is character that fills out field

  str(nstr.fmt_number(value, base, digits, width, pad))


pub dec(value)

'' Emit value as decimal

  str(nstr.dec(value, 0))


pub fxdec(value, digits)

'' Emit value as decimal using fixed # of digits
'' -- may add leading zeros

  str(nstr.dec(value, digits))


pub jdec(value, digits, width, pad)

'' Emit value as decimal using fixed # of digits
'' -- aligned in padded field (negative width to left-align)
'' -- digits is max number of digits to use
'' -- width is width of final field (max)
'' -- pad is character that fills out field

  str(nstr.fmt_number(value, "d", digits, width, pad))


pub dpdec(value, dp)

'' Emit value as decimal with decimal point
'' -- dp is number of digits after decimal point

  str(nstr.dpdec(value, dp))


pub jdpdec(value, dp, width, pad)

'' Emit value as decimal with decimal point
'' -- aligned in padded field (negative width to left-align)
'' -- dp is number of digits after decimal point
'' -- width is width of final field (max)
'' -- pad is character that fills out field

  str(nstr.fmt_number(value, "f", dp, width, pad))


pub hex(value)

'' Emit value as hexadecimal

  str(nstr.itoa(value, 16, 0))


pub fxhex(value, digits)

'' Emit value as hexadecimal using fixed # of digits

  str(nstr.itoa(value, 16, digits))


pub jhex(value, digits, width, pad)

'' Emit value as quarternary using fixed # of digits
'' -- aligned inside field
'' -- pad fills out field

  str(nstr.fmt_number(value, "x", digits, width, pad))


pub oct(value)

'' Emit value as octal

  str(nstr.itoa(value, 8, 0))


pub foct(value, digits)

'' Emit value as octal using fixed # of digits

  str(nstr.itoa(value, 8, digits))


pub joct(value, digits, width, pad)

'' Emit value as octal using fixed # of digits
'' -- aligned inside field
'' -- pad fills out field

  str(nstr.fmt_number(value, "o", digits, width, pad))


pub qrt(value)

'' Emit value as quarternary

  str(nstr.itoa(value, 4, 0))


pub fxqrt(value, digits)

'' Emit value as quarternary using fixed # of digits

  str(nstr.itoa(value, 4, digits))


pub jqrt(value, digits, width, pad)

'' Emit value as quarternary using fixed # of digits
'' -- aligned inside field
'' -- pad fills out field

  str(nstr.fmt_number(value, "q", digits, width, pad))


pub bin(value)

'' Emit value as binary

  str(nstr.itoa(value, 2, 0))


pub fxbin(value, digits)

'' Emit value as binary using fixed # of digits

  str(nstr.itoa(value, 2, digits))


pub jbin(value, digits, width, pad)

'' Emit value as binary using fixed # of digits
'' -- aligned inside field
'' -- pad fills out field

  str(nstr.fmt_number(value, "b", digits, width, pad))


dat { pasm driver }

                        org     0

fdsuart                 mov     t1, par                         ' get structure address
                        add     t1, #16                         ' skip past heads and tails (4 longs)

                        rdlong  t2, t1                          ' get rxpin
                        mov     rxmask, #1                      ' convert to mask
                        shl     rxmask, t2

                        add     t1, #4                          ' get txpin
                        rdlong  t2, t1
                        mov     txmask, #1                      ' convert to mask
                        shl     txmask, t2

                        add     t1, #4                          ' get rxtxmode
                        rdlong  iomode, t1

                        add     t1, #4                          ' get bitticks
                        rdlong  bittix, t1

                        add     t1, #4                          ' get bufpntr
                        rdlong  rxhub, t1
                        mov     txhub, rxhub
                        add     txhub, #BUF_SIZE

                        test    iomode, #%0100          wz      ' init tx pin according to mode
                        test    iomode, #%0010          wc
        if_z_ne_c       or      outa, txmask
        if_z            or      dira, txmask

                        mov     txcode, #transmit               ' initialize ping-pong multitasking


' -----------------
'  Receive Process
' -----------------
'
receive                 jmpret  rxcode, txcode                  ' run a chunk of transmit code, then return

                        test    iomode, #%0001          wz      ' wait for start bit on rx pin
                        test    rxmask, ina             wc
        if_z_eq_c       jmp     #receive

                        mov     rxbits, #9                      ' ready to receive byte
                        mov     rxcnt, bittix
                        shr     rxcnt, #1
                        add     rxcnt, cnt

:bit                    add     rxcnt, bittix                   ' ready next bit period

:wait                   jmpret  rxcode, txcode                  ' run a chuck of transmit code, then return

                        mov     t1, rxcnt                       ' check if bit receive period done
                        sub     t1, cnt
                        cmps    t1, #0                  wc
        if_nc           jmp     #:wait

                        test    rxmask, ina             wc      ' receive bit on rx pin
                        rcr     rxdata, #1
                        djnz    rxbits, #:bit

                        shr     rxdata, #32-9                   ' justify and trim received byte
                        and     rxdata, #$FF
                        test    iomode, #%0001          wz      ' if rx inverted, invert byte
        if_nz           xor     rxdata, #$FF

                        rdlong  t2, par                         ' save received byte and inc head
                        add     t2, rxhub
                        wrbyte  rxdata, t2
                        sub     t2, rxhub
                        add     t2, #1
                        and     t2, #BUF_MASK                   ' wrap buffer index back to 0
                        wrlong  t2, par

                        jmp     #receive                        ' byte done, receive next byte


' ------------------
'  Transmit Process
' ------------------
'
transmit                jmpret  txcode, rxcode                  ' run a chunk of receive code, then return

                        mov     t1, par                         ' check for head <> tail
                        add     t1, #8
                        rdlong  t2, t1                          ' t1 = @txhead
                        add     t1, #4
                        rdlong  t3, t1                          ' t1 = @txtail
                        cmp     t2, t3                  wz      ' equal?
        if_z            jmp     #transmit                       ' if yes, check again

                        add     t3, txhub                       ' get byte and inc tail
                        rdbyte  txdata, t3
                        sub     t3, txhub
                        add     t3, #1
                        and     t3, #BUF_MASK                   ' wrap buffer index back to 0
                        wrlong  t3, t1

                        or      txdata, STOP_BITS               ' add stop bit(s)
                        shl     txdata, #1                      ' add start bit
                        mov     txbits, #11                     ' bits = start + 8 + 2 stop
                        mov     txcnt, cnt

:bit                    test    iomode, #%0100          wz      ' output bit on tx pin according to mode
                        test    iomode, #%0010          wc
        if_z_and_c      xor     txdata, #1
                        shr     txdata, #1              wc
        if_z            muxc    outa, txmask
        if_nz           muxnc   dira, txmask
                        add     txcnt, bittix                   ' ready next cnt

:wait                   jmpret  txcode, rxcode                  ' run a chunk of receive code, then return

                        mov     t1, txcnt                       ' check if bit transmit period done
                        sub     t1, cnt
                        cmps    t1, #0                  wc
        if_nc           jmp     #:wait

                        djnz    txbits, #:bit                   ' another bit to transmit?

                        jmp     #transmit                       ' byte done, transmit next byte

' --------------------------------------------------------------------------------------------------

STOP_BITS               long    $FFFF_FF00

iomode                  res     1                               ' mode bits
bittix                  res     1                               ' ticks per bit

rxmask                  res     1                               ' mask for rx pin
rxhub                   res     1                               ' hub address of rxbuffer                             '
rxdata                  res     1                               ' received byte
rxbits                  res     1                               ' bit counter for rx
rxcnt                   res     1                               ' bit timer for rx
rxcode                  res     1                               ' cog pointer for rx process

txmask                  res     1                               ' mask for tx pin
txhub                   res     1                               ' hub address of txbuffer
txdata                  res     1                               ' byte to transmit
txbits                  res     1                               ' bit counter for tx
txcnt                   res     1                               ' bit timer for tx
txcode                  res     1                               ' cog pointer for tx process

t1                      res     1                               ' working registers
t2                      res     1
t3                      res     1

                        fit     496


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
