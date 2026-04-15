#pragma target ${target}

#ifdef __P1__
#define _XINFREQ 5_000_000
#define _CLKMODE XTAL1 + PLL16X
#else
#define _CLKFREQ 160_000_000
#endif

void main()
{
    // initialization

    while (true) {
        // loop
    }
}
