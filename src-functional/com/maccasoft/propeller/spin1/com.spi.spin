' Author: Beau Schwabe
CON

    #0,MSBPRE,LSBPRE,MSBPOST,LSBPOST                    '' Used for SHIFTIN routines

'' MSBPRE   - Most Significant Bit first ; data is valid before the clock
'' LSBPRE   - Least Significant Bit first ; data is valid before the clock
'' MSBPOST  - Most Significant Bit first ; data is valid after the clock
'' LSBPOST  - Least Significant Bit first ; data is valid after the clock

    #4,LSBFIRST,MSBFIRST                                '' Used for SHIFTOUT routines

'' LSBFIRST - Least Significant Bit first ; data is valid after the clock
'' MSBFIRST - Most Significant Bit first ; data is valid after the clock

    #1,_SHIFTOUT,_SHIFTIN                               '' Used for operation Mode

VAR

    long    cog
    long    command
    byte    _datapin
    byte    _clockpin
    byte    _inputmode
    byte    _outputmode

PUB Start(datapin, clockpin, inputmode, outputmode, delay, state) : okay
{{
    Start SPI Engine - Starts a cog

    returns false if no cog available

         delay := 15            ''Clock delay
                                ''  = 300ns + (N-1) * 50ns

                                '' Example1:
                                ''     A delay of 5 would be 500ns
                                ''     300ns + 4 * 50ns = 500ns

                                '' Example2:
                                ''     A delay of 15 would be 1us
                                ''     300ns + 14 * 50ns = 1000ns = 1us

         state := 1             '' 0 - Start Clock LOW
                                '' 1 - Start Clock HIGH
}}

    Stop

    _datapin    := datapin
    _clockpin   := clockpin
    _outputmode := outputmode
    _inputmode  := inputmode

    clockdelay := delay
    clockstate := state

    okay := cog := cognew(@loop, @command) + 1

PUB Send(bits, value)

    ShiftOut(_datapin, _clockpin, _outputmode, bits, value)

PUB Receive(bits)

    ShiftIn(_datapin, _clockpin, _inputmode, bits)

PUB Stop
{{
    Stop SPI Engine - frees a cog
}}

    if cog
       cogStop(cog~ - 1)
    command~

PRI ShiftOut(dpin, cpin, mode, bits, value)                     ''If SHIFTOUT is called with 'Bits' set to Zero, then the COG will shut
                                                                ''down.  Another way to shut the COG down is to call 'Stop' from Spin.
    SetCommand(_SHIFTOUT, @dpin)

PRI ShiftIn(dpin, cpin, mode, bits) | value, flag                  ''If SHIFTIN is called with 'Bits' set to Zero, then the COG will shut
                                                                ''down.  Another way to shut the COG down is to call 'Stop' from Spin.
    flag := 1
    SetCommand(_SHIFTIN, @dpin)
    repeat until flag == 0

    return value

PRI SetCommand(cmd, argptr)

    command := cmd << 16 + argptr                       ''write command and pointer
    repeat while command                                ''wait for command to be cleared, signifying receipt

DAT
              org

loop          rdlong  t1,par          wz                ''wait for command
        if_z  jmp     #loop
              movd    :arg,#arg0                        ''get 5 arguments ; arg0 to arg4
              mov     t2,t1                             ''    │
              mov     t3,#5                             ''───┘
:arg          rdlong  arg0,t2
              add     :arg,d0
              add     t2,#4
              djnz    t3,#:arg
              mov     address,t1                        ''preserve address location for passing
                                                        ''variables back to Spin language.
              wrlong  zero,par                          ''zero command to signify command received
              ror     t1,#16+2                          ''lookup command address
              add     t1,#jumps
              movs    :table,t1
              rol     t1,#2
              shl     t1,#3
:table        mov     t2,0
              shr     t2,t1
              and     t2,#$FF
              jmp     t2                                ''jump to command
jumps         byte    0                                 ''0
              byte    SHIFTOUT_                         ''1
              byte    SHIFTIN_                          ''2
              byte    NotUsed_                          ''3
NotUsed_      jmp     #loop
'################################################################################################################
'tested OK
SHIFTOUT_                                               ''SHIFTOUT Entry
              mov     t4,             arg3      wz      ''     Load number of data bits
    if_z      jmp     #Done                             ''     '0' number of Bits = Done
              mov     t1,             #1        wz      ''     Configure DataPin
              shl     t1,             arg0
              muxz    outa,           t1                ''          PreSet DataPin LOW
              muxnz   dira,           t1                ''          Set DataPin to an OUTPUT
              mov     t2,             #1        wz      ''     Configure ClockPin
              shl     t2,             arg1              ''          Set Mask
              test    clockstate,     #1        wc      ''          Determine Starting state
    if_nc     muxz    outa,           t2                ''          PreSet ClockPin LOW
    if_c      muxnz   outa,           t2                ''          PreSet ClockPin HIGH
              muxnz   dira,           t2                ''          Set ClockPin to an OUTPUT
              sub     _LSBFIRST,      arg2    wz,nr     ''     Detect LSBFIRST mode for SHIFTOUT
    if_z      jmp     #LSBFIRST_
              sub     _MSBFIRST,      arg2    wz,nr     ''     Detect MSBFIRST mode for SHIFTOUT
    if_z      jmp     #MSBFIRST_
              jmp     #loop                             ''     Go wait for next command
'------------------------------------------------------------------------------------------------------------------------------

SHIFTIN_                                                ''SHIFTIN Entry
              mov     t4,             arg3      wz      ''     Load number of data bits
    if_z      jmp     #Done                             ''     '0' number of Bits = Done
              mov     t1,             #1        wz      ''     Configure DataPin
              shl     t1,             arg0
              muxz    dira,           t1                ''          Set DataPin to an INPUT
              mov     t2,             #1        wz      ''     Configure ClockPin
              shl     t2,             arg1              ''          Set Mask
              test    clockstate,     #1        wc      ''          Determine Starting state
    if_nc     muxz    outa,           t2                ''          PreSet ClockPin LOW
    if_c      muxnz   outa,           t2                ''          PreSet ClockPin HIGH
              muxnz   dira,           t2                ''          Set ClockPin to an OUTPUT
              sub     _MSBPRE,        arg2    wz,nr     ''     Detect MSBPRE mode for SHIFTIN
    if_z      jmp     #MSBPRE_
              sub     _LSBPRE,        arg2    wz,nr     ''     Detect LSBPRE mode for SHIFTIN
    if_z      jmp     #LSBPRE_
              sub     _MSBPOST,       arg2    wz,nr     ''     Detect MSBPOST mode for SHIFTIN
    if_z      jmp     #MSBPOST_
              sub     _LSBPOST,       arg2    wz,nr     ''     Detect LSBPOST mode for SHIFTIN
    if_z      jmp     #LSBPOST_
              jmp     #loop                             ''     Go wait for next command

'------------------------------------------------------------------------------------------------------------------------------
MSBPRE_                                                 ''     Receive Data MSBPRE
MSBPRE_Sin    test    t1,             ina     wc        ''          Read Data Bit into 'C' flag
              rcl     t3,             #1                ''          rotate "C" flag into return value
              call    #PreClock                         ''          Send clock pulse
              djnz    t4,             #MSBPRE_Sin       ''          Decrement t4 ; jump if not Zero
              jmp     #Update_SHIFTIN                   ''     Pass received data to SHIFTIN receive variable
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
LSBPRE_                                                 ''     Receive Data LSBPRE
              add     t4,             #1
LSBPRE_Sin    test    t1,             ina       wc      ''          Read Data Bit into 'C' flag
              rcr     t3,             #1                ''          rotate "C" flag into return value
              call    #PreClock                         ''          Send clock pulse
              djnz    t4,             #LSBPRE_Sin       ''     Decrement t4 ; jump if not Zero
              mov     t4,             #32               ''     For LSB shift data right 32 - #Bits when done
              sub     t4,             arg3
              shr     t3,             t4
              jmp     #Update_SHIFTIN                   ''     Pass received data to SHIFTIN receive variable
'------------------------------------------------------------------------------------------------------------------------------
MSBPOST_                                                ''     Receive Data MSBPOST
MSBPOST_Sin   call    #PostClock                        ''          Send clock pulse
              test    t1,             ina     wc        ''          Read Data Bit into 'C' flag
              rcl     t3,             #1                ''          rotate "C" flag into return value
              djnz    t4,             #MSBPOST_Sin      ''          Decrement t4 ; jump if not Zero
              jmp     #Update_SHIFTIN                   ''     Pass received data to SHIFTIN receive variable
'------------------------------------------------------------------------------------------------------------------------------
LSBPOST_                                                ''     Receive Data LSBPOST
              add     t4,             #1
LSBPOST_Sin   call    #PostClock                        ''          Send clock pulse
              test    t1,             ina       wc      ''          Read Data Bit into 'C' flag
              rcr     t3,             #1                ''          rotate "C" flag into return value
              djnz    t4,             #LSBPOST_Sin      ''          Decrement t4 ; jump if not Zero
              mov     t4,             #32               ''     For LSB shift data right 32 - #Bits when done
              sub     t4,             arg3
              shr     t3,             t4
              jmp     #Update_SHIFTIN                   ''     Pass received data to SHIFTIN receive variable
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
LSBFIRST_                                               ''     Send Data LSBFIRST
              mov     t3,             arg4              ''          Load t3 with DataValue
LSB_Sout      test    t3,             #1      wc        ''          Test LSB of DataValue
              muxc    outa,           t1                ''          Set DataBit HIGH or LOW
              shr     t3,             #1                ''          Prepare for next DataBit
              call    #PostClock                        ''          Send clock pulse
              djnz    t4,             #LSB_Sout         ''          Decrement t4 ; jump if not Zero
              mov     t3,             #0      wz        ''          Force DataBit LOW
              muxnz   outa,           t1
              jmp     #loop                             ''     Go wait for next command
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
MSBFIRST_                                               ''     Send Data MSBFIRST
              mov     t3,             arg4              ''          Load t3 with DataValue
              mov     t5,             #%1               ''          Create MSB mask     ;     load t5 with "1"
              shl     t5,             arg3              ''          Shift "1" N number of bits to the left.
              shr     t5,             #1                ''          Shifting the number of bits left actually puts
                                                        ''          us one more place to the left than we want. To
                                                        ''          compensate we'll shift one position right.
MSB_Sout      test    t3,             t5      wc        ''          Test MSB of DataValue
              muxc    outa,           t1                ''          Set DataBit HIGH or LOW
              shr     t5,             #1                ''          Prepare for next DataBit
              call    #PostClock                        ''          Send clock pulse
              djnz    t4,             #MSB_Sout         ''          Decrement t4 ; jump if not Zero
              mov     t3,             #0      wz        ''          Force DataBit LOW
              muxnz   outa,           t1

              jmp     #loop                             ''     Go wait for next command
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
Update_SHIFTIN
              mov     t1,             address           ''     Write data back to Arg4
              add     t1,             #16               ''          Arg0 = #0 ; Arg1 = #4 ; Arg2 = #8 ; Arg3 = #12 ; Arg4 = #16
              wrlong  t3,             t1
              add     t1,             #4                ''          Point t1 to Flag ... Arg4 + #4
              wrlong  zero,           t1                ''          Clear Flag ... indicates SHIFTIN data is ready
              jmp     #loop                             ''     Go wait for next command
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
PreClock
              mov     t2,             #0      nr        ''     Clock Pin
              test    t2,             ina     wz        ''          Read ClockPin
              muxz    outa,           t2                ''          Set ClockPin to opposite  of read value
              call    #ClkDly
              muxnz   outa,           t2                ''          Restore ClockPin to original read value
              call    #ClkDly
PreClock_ret  ret                                       ''          return
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
PostClock
              mov     t2,             #0      nr        ''     Clock Pin
              test    t2,             ina     wz        ''          Read ClockPin
              call    #ClkDly
              muxz    outa,           t2                ''          Set ClockPin to opposite  of read value
              call    #ClkDly
              muxnz   outa,           t2                ''          Restore ClockPin to original read value
PostClock_ret ret                                       ''          return
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
ClkDly
              mov       t6,     clockdelay
ClkPause      djnz      t6,     #ClkPause
ClkDly_ret    ret
'------------------------------------------------------------------------------------------------------------------------------
'tested OK
Done                                                    ''     Shut COG down
              mov     t2,             #0                ''          Preset temp variable to Zero
              mov     t1,             par               ''          Read the address of the first perimeter
              add     t1,             #4                ''          Add offset for the second perimeter ; The 'Flag' variable
              wrlong  t2,             t1                ''          Reset the 'Flag' variable to Zero
              CogID   t1                                ''          Read CogID
              COGSTOP t1                                ''          Stop this Cog!
'------------------------------------------------------------------------------------------------------------------------------
{
########################### Assembly variables ###########################
}
zero                    long    0                       ''constants
d0                      long    $200

_MSBPRE                 long    $0                      ''          Applies to SHIFTIN
_LSBPRE                 long    $1                      ''          Applies to SHIFTIN
_MSBPOST                long    $2                      ''          Applies to SHIFTIN
_LSBPOST                long    $3                      ''          Applies to SHIFTIN
_LSBFIRST               long    $4                      ''          Applies to SHIFTOUT
_MSBFIRST               long    $5                      ''          Applies to SHIFTOUT

clockdelay              long    0
clockstate              long    0

                                                        ''temp variables
t1                      long    0                       ''     Used for DataPin mask     and     COG shutdown
t2                      long    0                       ''     Used for CLockPin mask    and     COG shutdown
t3                      long    0                       ''     Used to hold DataValue SHIFTIN/SHIFTOUT
t4                      long    0                       ''     Used to hold # of Bits
t5                      long    0                       ''     Used for temporary data mask
t6                      long    0                       ''     Used for Clock delay
address                 long    0                       ''     Used to hold return address of first Argument passed

arg0                    long    0                       ''arguments passed to/from high-level Spin
arg1                    long    0
arg2                    long    0
arg3                    long    0
arg4                    long    0

