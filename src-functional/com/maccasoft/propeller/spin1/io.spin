{{
    Basic object for setting Propeller pins.

    > Because I can't remember how to use registers when I need them.
}}
PUB Output(pin)

    dira[pin]~~

PUB Input(pin)

    dira[pin]~

PUB High(pin)

    outa[pin]~~

PUB Low(pin)

    outa[pin]~

PUB Toggle(pin)

    ~outa[pin]

PUB Set(pin, enabled)

    outa[pin] := enabled

