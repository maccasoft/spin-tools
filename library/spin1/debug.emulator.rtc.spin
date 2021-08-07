{{
************************************************
* Propeller RTC Emulator                  v1.0 *
* Author: Beau Schwabe                         *
* Copyright (c) 2009 Parallax                  *
* See end of file for terms of use.            *
************************************************
}}
VAR
long    Timer,Temp,ClockFlag,_TimeAddress
byte    MonthDays,SS,MM,HH,AP,DD,MO,YY,LY
byte    APswitch,DateTimeStamp[11]
long    Stack[100]

PUB Start(TimeAddress)
    _TimeAddress := TimeAddress
    cognew(Run(TimeAddress),@Stack)

PUB SetSec(_SS)
    SS := _SS

PUB SetMin(_MM)
    MM := _MM

PUB SetHour(_HH)
    HH := _HH

PUB SetAMPM(_AP)
    AP := _AP

PUB SetDate(_DD)
    DD := _DD

PUB SetMonth(_MO)
    MO := _MO

PUB SetYear(_YY)
    YY := _YY

PUB Suspend
    ClockFlag := 1              '' Suspend Clock
    repeat while ClockFlag == 1 '' Clock responds with a 2 when suspend received
    ParseTime(_TimeAddress)     '' Unpack current time variable values from 'long'

PUB Restart
    UnParseTime(_TimeAddress)   '' Pack current time variable values into 'long'
    ClockFlag := 0              '' Restart Clock

PUB Run(TimeAddress)

'' TimeAddress variable allocation:
'' Leap   Year    Month   Date   AM/PM  Hours   Minutes   Seconds
'' (0-1) (00-31)  (1-12) (1-31)  (0-1) (1-12)  (00-59)   (00-59)
''   0____00000____0000___00000____0____0000____000000____000000

APswitch := 1
Timer := cnt
repeat
  waitcnt(Timer += clkfreq)     '' 1 Second Synchronized Delay

  if ClockFlag <> 0             '' Check for request to suspend clock?
     ClockFlag := 2             '' respond by acknowledging request
     repeat while ClockFlag <> 0  '' Wait for the OK to resume clock
     Timer := cnt

  ParseTime(TimeAddress)

  If YY>>2<<2 == YY             '' Detect Leap Year
     LY := 1
  else
     LY := 0

  MonthDays := 28               '' Decode number of days in each month
  If MO <> 2
     MonthDays += 2
     if MO & %0001 <> (MO & %1000)/%1000
        MonthDays += 1
  else
     MonthDays += LY

  SS += 1                       '' Increment Time Calendar

  if SS == 60                   '' Seconds LOGIC
     SS := 0
     MM += 1

  if MM == 60                   '' Minutes LOGIC
     MM := 0
     HH += 1

  if HH == 13                   '' Hours LOGIC
     HH := 1

  if HH == 11                   '' AM/PM LOGIC
     APswitch := 0
  if HH < 11
     APswitch := 1
  if HH == 12
     if APswitch == 0
        APswitch := 1
        AP := 1 - AP
        if AP == 0
           DD += 1


  if DD == MonthDays + 1        '' Days LOGIC
     DD := 1
     MO += 1

  if MO == 13                   '' Months LOGIC
     MO := 1
     YY += 1

  if YY == 33                   '' Years LOGIC
     YY := 32

  UnParseTime(TimeAddress)      '' Pack current time variable values into 'long'

PUB UnParseTime(TimeAddress)
    result := LY<<31 | YY<<26 | MO<< 22 | DD<<17 | AP<<16 | HH<<12 | MM<<6 | SS
    longmove(TimeAddress,@result,1)

PUB ParseTime(TimeAddress)
    longmove(@Temp,TimeAddress,1) 'Parse Data
    SS := Temp & %111111
    Temp := Temp >> 6
    MM := Temp & %111111
    Temp := Temp >> 6
    HH := Temp & %1111
    Temp := Temp >> 4
    AP := Temp & %1
    Temp := Temp >> 1
    DD := Temp & %11111
    Temp := Temp >> 5
    MO := Temp & %1111
    Temp := Temp >> 4
    YY := Temp & %11111
    Temp := Temp >> 5
    LY := Temp & %1

PUB ParseDateStamp(DataAddress)
    DateTimeStamp[0] := "2"                            ''<-Year
    DateTimeStamp[1] := "0"                             '<-Year
    DateTimeStamp[2] := $30 + YY/10                     '<-Year
    DateTimeStamp[3] := $30 + YY-(YY/10)*10             '<-Year
    DateTimeStamp[4] := "/"
    DateTimeStamp[5] := $30 + MO/10                    ''<-Month
    DateTimeStamp[6] := $30 + MO-(MO/10)*10             '<-Month
    DateTimeStamp[7] := "/"
    DateTimeStamp[8] := $30 + DD/10                    ''<-Day
    DateTimeStamp[9] := $30 + DD-(DD/10)*10             '<-Day
    DateTimeStamp[10] := 0      ' String Terminator ALWAYS Zero
    bytemove(DataAddress,@DateTimeStamp,11)

PUB ParseTimeStamp(DataAddress)
    DateTimeStamp[0] := $30 + HH/10                    ''<-Hour
    DateTimeStamp[1] := $30 + HH-(HH/10)*10             '<-Hour
    DateTimeStamp[2] := ":"
    DateTimeStamp[3] := $30 + MM/10                    ''<-Minute
    DateTimeStamp[4] := $30 + MM-(MM/10)*10             '<-Minute
    DateTimeStamp[5] := ":"
    DateTimeStamp[6] := $30 + SS/10                    ''<-Second
    DateTimeStamp[7] := $30 + SS-(SS/10)*10             '<-Second
    if AP < 1
       DateTimeStamp[8] := "a"                         ''<-Set am
    else
       DateTimeStamp[8] := "p"                         ''<-Set pm
    DateTimeStamp[9] := "m"
    DateTimeStamp[10] := 0      ' String Terminator ALWAYS Zero
    bytemove(DataAddress,@DateTimeStamp,11)

