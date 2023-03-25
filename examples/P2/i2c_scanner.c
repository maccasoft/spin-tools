/*
 * I2C bus scanner; reports devices connected to defined bus pins
 * Based on jm_i2c_scanner.spin2 by Jon "JonnyMac" McPhalen
 */
#define _CLKFREQ 160_000_000

#include "jm_i2c"
#include "jm_serial"

#define SDA       57  // { IO }  ' i2c (optional)
#define SCL       56  // { IO }

#define TRIES     1

#define S_LIST    0
#define S_MATRIX  1

#define STYLE     S_MATRIX

jm_i2c i2c;
jm_serial term;

void main()
{
    i2c.setup(SCL, SDA, 100, PU_1K5);
    term.startx(RX1, TX1, 115_200);

#if STYLE == S_LIST
    i2c_list();
#else
    i2c_matrix();
#endif

    for(;;)
        waitct(0);
}

/*
   Display connected I2C devices in list format
 */
void i2c_list()
{
    int count = 0, ok;

    term.str(string(CLS, "P2 I2C Scanner", CR, CR));
    term.str(string(" #  7-bit  8-bit  DDDD AAA X", CR,
                    "--  -----  -----  ---- --- -", CR));

    for (int type = %0001; type <= %1110; type++) {
        for (int addr = %000; addr <= %111; addr++) {
            int devid = (type << 4) | (addr << 1);
            for (int i = 0; i < 10; i++) {
                if (ok = i2c.present(devid)) {
                    break;
                }
                else
                    waitus(100);
            }
            if (ok) {
                term.fstr3(string("%2d   $%2x    $%2x   "), ++count, devid >> 1, devid);
                term.fstr3(string("%.4b %.3b %1b\r"), devid.[7..4], devid.[3..1], devid.[0]);
                waitms(3);
            }
        }
    }
}

/*
   Display connected I2C devices in matrix format
 */
void i2c_matrix()
{
    int count = 0;

    term.str(string(CLS, "P2 I2C Devices", CR));
    term.str(string("-- dddd_aaa_0 (8-bit) format", CR, CR));

    for (int type = %0001; type <= %1110; type++) {
        term.fxhex(type << 4, 2);
        term.tx(" ");
        for (int addr = %000; addr <= %111; addr++) {
            int devid = (type << 4) | (addr << 1);
            if (i2c.present(devid)) {
                term.fxhex(devid, 2);
                term.tx(" ");
                count++;
            }
            else {
                term.str(string(".. "));
            }
        }
        waitms(1);
        term.tx(13);
    }

    term.fstr1(string("\rDevices: %d\r"), count);
}
