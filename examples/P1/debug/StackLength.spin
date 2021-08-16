' Author: Jeff Martin
{{
    This code demonstrates the use of the Stack Length object.  See the Stack Length object for more information.

    The example object being tested appears under the heading "Code/Object Being Tested for Stack Usage," near the
    bottom of this source.  Hypothetically, it is the code written by a developer and given 32 longs of Stack space
    during development.

    Now that this object is done, the developer wishes to check its actual stack utilization, so he temporarily adds
    the code that appears under the heading "Temporary Code to Test Stack Usage," below, downloads with F11, opens
    the Parallax Serial Terminal and sets it to the Propeller chip's programming port at (115200 baud), resets the
    Propeller and waits for message.

    The message "Stack Usage: 9" appears and now he knows his code should reserve only 9 longs of space for Stack.
    He makes the change, deletes the "temporary stack testing code" and calls it done!
}}
CON

    _clkmode      = xtal1 + pll16x
    _xinfreq      = 5_000_000

OBJ

    Stk : "debug.stacklength"

PUB Main

    Stk.Init(@stack, 32)                                  'Initialize reserved Stack space (reserved below)
    Start(16, 500, 0)                                     'Exercise code/object under test
    waitcnt(clkfreq * 2 + cnt)                            'Wait ample time for max stack usage
    Stk.GetLength(30, 115200)                             'Transmit results serially out P30 at 115,200 baud

VAR

    long    stack[32]

PUB Start(Pin, DelayMS, Count)
{{
    Start new toggling process in a new cog.
}}

    cognew(Toggle(Pin, DelayMS, Count), @stack)

PUB Toggle(Pin, DelayMS, Count)
{{
    Toggle Pin, Count times with DelayMS milliseconds in between.

    If Count = 0, toggle Pin forever.
}}

    dira[Pin]~~                                           'Set I/O Pin to output direction
    repeat                                                'Repeat the following
        !outa[Pin]                                        '  Toggle I/O Pin
        waitcnt(clkfreq / 1000 * DelayMS + cnt)           '  Wait for DelayMS milliseconds
    while Count := --Count #> -1                          'While Count-1 is not 0 (limit minimum to -1)

