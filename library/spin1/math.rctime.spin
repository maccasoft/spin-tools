' Original author: Beau Schwabe
OBJ

    time : "time"
    io   : "io"

VAR

    long cogon, cog
    long RCStack[16]
    long rctemp
    long mode

PUB Start(pin, state, rcvalueaddr)

'' Start CalculateRCTime - starts a cog
'' returns false if no cog available

  stop
  cogon := (cog := cognew(CalculateRCTime(pin, state, rcvalueaddr),@RCStack)) > 0
  mode := 1

PUB stop

'' Stop CalculateRCTime - frees a cog

  if cogon~
    cogstop(cog)

PUB CalculateRCTime(pin,state,rcvalueaddr)
    repeat

           io.Set (pin, state)                  'make I/O an output in the state you wish to measure... and then charge cap
           io.Output (pin)
           time.MSleep(1)                       'pause for 1mS to charge cap
           io.Input (pin)                       'make I/O an input
           rctemp := cnt                        'grab clock tick counter value
           waitpeq(1 - state, |< pin, 0)        'wait until pin goes into the opposite state you wish to measure; state: 1=discharge 0=charge
           rctemp := cnt - rctemp               'see how many clock cycles passed until desired state changed
           rctemp := rctemp - 1600              'offset adjustment (entry and exit clock cycles Note: this can vary slightly with code changes)
           rctemp := rctemp >> 4                'scale result (divide by 16) <<-number of clock cycles per itteration loop
           long [rcvalueaddr] := rctemp         'Write rctemp to RCValue

           if mode == 0                         'Check for forground (0) or background (1) mode of operation; forground = no seperate cog / background = seperate running cog
              quit
