' Original author: Jeff Martin
{{
    Demonstration of various handy features of the Parallax Serial Terminal (object and software).  The Parallax Serial
    Terminal software is included with the Propeller Tool installer (v1.2.6 or newer) and provides a simple serial-based
    interface to the Propeller chip.  Typically this is done over the programming connection but may use other I/O pins
    if desired.

    # Usage

    -   Run the Parallax Serial Terminal (included with the Propeller Tool) and set it to the connected Propeller
        chip's COM Port with a baud rate of 115200.
    -   In the Propeller Tool, press the F10 (or F11) key to compile and load the code.
    -   Immediately click the Parallax Serial Terminal's Enable button.  Do not wait until the program is finished
        downloading.
}}

CON

    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

    ColPos = 8

OBJ

    term : "com.serial.terminal"

PUB Main | value, base, width, offset

  term.Start(115_200)                                                            'Set Parallax Serial Terminal to 115200 baud

  '-------- Demo 1 --------
  term.Str(@DemoHeader)                                                          'Print header; uses string in DAT section.
  term.Chars("-", strsize(@DemoHeader))                                          'Use Chars method to output hyphens "-"
  term.Str(string(term#NL, term#NL, "*** Number Feedback Example ***"))

  repeat
    term.Chars(term#NL, 3)                                                        'Output multiple new lines
    term.Str(string("Enter a decimal value: "))                                  'Prompt user to enter a number; uses immediate string.
    value := term.DecIn                                                          'Get number (in decimal).
    term.Str(string(term#NL, "Your value is..."))                                 'Announce output
    term.Str(string(term#NL, " (Decimal):"))                                      'In decimal
    term.PositionX(16)                                                           'Move cursor to column 16
    term.Dec(value)
    term.Str(string(term#NL, " (Hexadecimal):", term#PX, 16))                      'In hexadecimal.  We used PX control code to
    term.Hex(value, 8)                                                           '  move cursor (alternative to PositionX method).
    term.Str(string(term#NL, " (Binary):"))                                       'In binary.
    term.MoveRight(6)                                                            'Used MoveRight to move cursor (alternative
    term.Bin(value, 32)                                                          '  to features used above).
    term.Str(string(term#NL, term#NL, "Try again? (Y/N):"))                        'Prompt to repeat
    value := term.CharIn
  while (value == "Y") or (value == "y")                                        'Loop back if desired


  '-------- Demo 2 --------
  repeat
    term.Clear                                                                   'Clear screen
    term.Str(@DemoHeader)                                                        'Print header.
    term.Chars("-", strsize(@DemoHeader))                                        'Use Chars method to output hyphens "-"
    term.Str(string(term#NL, term#NL, "*** Pseudo-Random Number Example ***"))
    term.Chars(term#NL, 2)                                                        'Output multiple new lines
    term.Str(string("Enter 'seed' value: "))                                     'Prompt for seed value
    value := term.DecIn
    term.Str(string(term#NL, "Display decimal, hexadecimal, or binary? (D/H/B)")) 'Prompt for base size
    base := term.CharIn
    term.Str(@RandomHeader)                                                      'Output table header
    term.Dec(value)
    base := lookdownz(base & %11011111: "B", "H", "D") <# 2                     'Convert base to number (B=0, H=1, else = 2)
    width := lookupz(base: 32, 8, 11)                                           'Calculate field width
    offset := ColPos + 4 + width                                                'Calculate column offset
    term.Chars(term#NL, 2)                                                        'New lines
    term.PositionX(ColPos)                                                       'Position and display first column heading
    term.Str(@Forward)
    term.PositionX(offset)                                                       'Position and display second column heading
    term.Str(@Backward)
    term.NewLine                                                                 'Draw underlines
    term.PositionX(ColPos)
    term.Chars("-", width)
    term.PositionX(offset)
    term.Chars("-", width)
    term.NewLine

    'Pseudo-Random Number (Forward)
    repeat 10
      waitcnt(clkfreq / 6 + cnt)                                                'Wait 1/6 second
      term.PositionX(ColPos)                                                     'Position to first column
      ?value                                                                    'Generate random number forward
      case base                                                                 'Output in binary, hexadecimal, or decimal
        0: term.Bin(value, width) {binary}
        1: term.Hex(value, width) {hex}
        2: term.Dec(value)        {decimal}
      term.MoveDown(1)                                                           'Move to next line

    'Pseudo-Random Number (Backward)
    repeat 10
      waitcnt(clkfreq / 6 + cnt)                                                'Wait 1/6 second
      term.MoveUp(1)                                                             'Move to previous line
      term.PositionX(offset)                                                     'Position to second column
      case base                                                                 'Output in binary, hexadecimal, or decimal
        0: term.Bin(value, width) {binary}
        1: term.Hex(value, width) {hex}
        2: term.Dec(value)        {decimal}
      value?                                                                    'Generate random number backward

    term.Position(0, 23)                                                         'Position below table
    term.Str(string("Try again? (Y/N):"))                                        'Prompt to repeat
    value := term.CharIn
  while (value == "Y") or (value == "y")                                        'Loop back if desired

  term.Clear
  term.Str(string("Thanks for playing."))

DAT

DemoHeader    byte "Parallax Serial Terminal Demonstration", term#NL, 0
RandomHeader  byte term#NL, term#NL, "Pseudo-Random Numbers Generated by Seed Value ", 0
Forward       byte "Forward", 0
Backward      byte "Backward", 0
