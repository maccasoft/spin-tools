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

CON

    Enable = 0
     Clock = 1
      Data = 2

VAR

    long    correctheading
    long    Deg, OldDeg

OBJ

    term        : "com.serial.terminal"
    compass     : "sensor.compass.hm55b"
    calibrate   : "Calibration"
    num         : "string.integer"

PUB Main | i, dx, dy, rawheading

    term.Start(38400)
    compass.start(Enable,Clock,Data)

    repeat
        term.Char(1)                                         ' Send the HOME code to the DEBUG terminal
        term.Str(string("compass Propeller Compass Demo"))   ' Display Header Text
        term.Char(term#NL)                                        ' Send the RETURN key code to the DEBUG terminal
        term.Char(term#NL)                                        ' Send the RETURN key code to the DEBUG terminal
        term.Char(term#NL)                                        ' Send the RETURN key code to the DEBUG terminal

        rawheading := compass.theta                         ' Read RAW 13-bit Angle

        correctheading := calibrate.Correct(rawheading)   ' calibrate Correct Heading

        Deg := correctheading * 45 / 1024                 ' Convert 13-Bit Angle to Deg
                                                          ' Note: This only makes it easier for us Humans to
                                                          '       read.

        term.Str(string("Correct Heading: "))              ' Display Correct Heading as a Degree
        term.Str(num.Dec(Deg))
        term.Str(string("   "))

        term.Char(term#NL)
        term.Char(term#NL)


''#########################################################
''#########################################################

''        This section for Calibration purposes only.   - See 'compass Compass Calibration.Spin'
''        You may remove or comment after calibration.

      term.Str(string("RAW Heading: "))                  ' Display RAW Heading as a 13-Bit Angle
      term.Dec(rawheading/11*11)                         ' Reduce returned Coordic value down to about 0.5 Deg
      term.Str(string("    "))                           ' resolution.  This helps to reduce LSB jitter

''#########################################################
''#########################################################


''#########################################################
''#########################################################

''        This section for DEBUG graphics only.

      if Deg<>OldDeg                                    'No need to update if position has not moved
         DrawCompassNeedle(OldDeg,(" "))
         DrawCompassNeedle(Deg,("#"))
         OldDeg := Deg

      DrawCompassCircle

''#########################################################
''#########################################################

PUB DrawCompassNeedle(_Deg,Character) | LineStep, Line, X, Y, X_Size, Y_Size, X_Center, Y_Center

    _Deg := 360 - (_Deg + 180)                          'Adjust Deg value for screen coordinate system

    X_Size := 26
    Y_Size := 13
    X_Center := 50
    Y_Center := 15

    LineStep := 5

    repeat Line from 0 to LineStep
        X := X_Center + (GetSine(Deg2Bit13(_Deg))* ((X_Size * Line)/LineStep))/ 65535
        Y := Y_Center + (GetCosine(Deg2Bit13(_Deg))* ((Y_Size * Line)/LineStep))/ 65535
        term.Position(X,Y)
        term.Char(Character)

PUB DrawCompassCircle | _Deg, X, Y, X_Size, Y_Size, X_Center, Y_Center

    X_Size := 30
    Y_Size := 15
    X_Center := 50
    Y_Center := 15

    repeat _Deg from 0 to 360 step 5
        X := X_Center + (GetSine(Deg2Bit13(_Deg))* X_Size)/ 65535
        Y := Y_Center + (GetCosine(Deg2Bit13(_Deg))* Y_Size)/ 65535
        term.Position(X,Y)
        term.Char("+")

PUB Deg2Bit13(_Deg)

    return _Deg*1024/45

PUB GetCosine(angle)

    return GetSine(angle+$0800)

PUB GetSine(angle) | C,Z

    C := (angle & $0800)/$0800                          'Get quadrant 2/4 into C
    Z := 1-(angle & $1000)/$1000                        'Get quadrant 3/4 into Z
    if C==1
       -angle                                           'if quadrant 2/4, negate offset
    angle |= ($E000 >> 1)                               'or in sin table address >> 1
    angle <<= 1                                         'shift left to get final word address
    angle := word[angle]                                'read word sample from $E000 to $F000
    if Z==0
        -angle                                           'if quadrant 3/4, negate sample

    return angle

