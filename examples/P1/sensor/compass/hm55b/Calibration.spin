{{
''**********************************************
''*  HM55B Compass Calibration Add-On     V1.0 *
''*  Author: Beau Schwabe                      *
''*  Copyright (c) 2009 Parallax, Inc.         *
''*  See end of file for terms of use.         *
''**********************************************
}}
CON
{{

This program runs under the same cog as you started the compass from.  It simply adds a
set of routines that allow you to calibrate your compass by way of linear interpolation.

Compass Calibration for Linear interpolation Method:

Calibrate:

Read RAW compass values at 22.5 Deg intervals and Store these values in the DAT section below.


Calculating the correct heading from stored calibration values:

1) Read RAW compass value into RAWheading variable

2) Look where RAWheading value is within stored Calibration values.
   Determine LowCalibration value and HighCalibration value based on RAWheading
   Set HeadingIndex value from lookup table (<-- 22.5 Deg Increments indicating
   where heading should be)

3) CalibrationDelta = HighCalibration - LowCalibration

4) HeadingError = 22.5 / CalibrationDelta

5) CorrectHeading = [ ( RAWheading - LowCalibration ) * HeadingError ] + HeadingIndex

}}

VAR
long    CalibrationDelta
long    HighCalibration
long    LowCalibration
long    HeadingError
long    CorrectHeading
long    HeadingIndex

PUB Correct(RawHeading)                                 'Input value is expected to be a 13-Bit Angle
                                                        'ranging from 0 to 8191.

    DetectRange(RawHeading)                             ' Based on calibrated measurements detect
                                                        ' which 1/16th section of the compass circle
                                                        ' the compass is facing

    CalibrationDelta := HighCalibration - LowCalibration' Determine amount of difference
                                                        ' in the 1/16 compass section.

    HeadingError := (512 * 1000) / CalibrationDelta     ' Determine the amount of error or
                                                        ' scaling that needs to be done based on how
                                                        ' much should be within 1/16th of the compass
                                                        ' section vs. how much is actually within 1/16th
                                                        ' of the compass area.
                                                        '
                                                        ' Note: With a 13-bit returned compass angle,
                                                        '       1/16th of the total compass circle equates
                                                        '       to 512 points.

    CorrectHeading := (((RawHeading - LowCalibration) * HeadingError) / 1000)+ HeadingIndex     'Calculate
                                                        ' correct heading by creating a linear relationship
                                                        ' between calibrated values vs. actual values

    If CorrectHeading < 0                               ' Compensate for the 360 Deg / 0 Deg transition
       CorrectHeading := CorrectHeading + 8192

    result := CorrectHeading

PUB DetectRange(RAW_Deg)|IndexHead
    repeat IndexHead from 0 to 15
      if RAW_Deg < CompassCalibration[IndexHead]         ' Determine position from Calibration values
         If IndexHead <> 0
            LowCalibration := CompassCalibration[IndexHead-1]
            HighCalibration := CompassCalibration[IndexHead]
            HeadingIndex := (IndexHead-1) * 512
            quit
      else
         LowCalibration := CompassCalibration[15]        ' Compensate for 360 / 0 Deg transition
         HighCalibration := CompassCalibration[0] + 8192
         HeadingIndex := 7680

DAT     ''Data setup section for compass calibration values

''Compass Calibration section requires 8 longs of storage

''                          Raw Reading                Actual Direction
'
'                My Compass values, yours will vary
CompassCalibration      word    0418                     '   0.0 Deg     <---- TIP: To Calibrate Compass, run the program
                        word    1056                     '  22.5 Deg                reading the RawHeading value.  Move the
                        word    1595                     '  45.0 Deg                compass orientation to each 22.5 deg
                        word    1991                     '  67.5 Deg                position and write down or enter the
                        word    2376                     '  90.0 Deg                values in the table to the left.  Once
                        word    2783                     ' 112.5 Deg                the table is complete, the compass is
                        word    3190                     ' 135.0 Deg                now calibrated.  Re-load and run the
                        word    3652                     ' 157.5 Deg                program, this time reading the
                        word    4037                     ' 180.0 Deg                CorrectHeading value.
                        word    4345                     ' 202.5 Deg
                        word    4763                     ' 225.0 Deg
                        word    5214                     ' 247.5 Deg                Refer to page 3 of the document link below
                        word    5687                     ' 270.0 Deg                for easy compass orientation and calibration.
                        word    6215                     ' 292.5 Deg
                        word    7040                     ' 315.0 Deg
                        word    7821                     ' 337.5 Deg

'                                              http://www.parallax.com/Portals/0/Downloads/docs/prod/compshop/HM55BModDocs.pdf

