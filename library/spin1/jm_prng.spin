'' =================================================================================================
''
''   File....... jm_prng.spin
''   Purpose.... Better psuedo random number generator
''   Author..... Michael Rychlik
''               -- reformatted by Jon "JonnyMac" McPhalen
''               -- see below for terms of use
''   E-mail.....
''   Started.... 31 NOV 2011
''   Updated.... 05 MAY 2012
''
'' =================================================================================================

{{

   /* Implementation of a 32-bit KISS generator which uses no multiply instructions */

   static unsigned int x=123456789,y=234567891,z=345678912,w=456789123,c=0;
   unsigned int JKISS32()
   {
     int t;
     y ^= (y<<5);
     y ^= (y>>7);
     y ^= (y<<22);
     t = z+w+c;
     z = w;
     c = t < 0;
     w = t&2147483647;
     x += 1411392427;
     return x + y + w;
   }

}}

var

  long  x
  long  y
  long  z
  long  w
  long  c


pub null

'' This is not a top-level object


pub start

  x := 123456789
  y := 234567891
  z := 345678912
  w := 456789123
  c := 0


pub seed(xx, yy, zz, ww, cc)

  if (xx <> 0)
    x := xx

  if (yy <> 0)
    y := yy

  if (zz <> 0)
    z := zz

  if (ww <> 0)
    w := 0

  c := cc & 1


pub random | t

'' Returns new random #

  y ^= (y <<  5)
  y ^= (y >>  7)
  y ^= (y << 22)
  t := z + w + c
  z := w
  c := (t < 0) & 1
  w := t & 2147483647
  x += 1411392427

  return (x + y + w)


pub randomize(lo, hi)

'' Return a random number between lo and hi (inclusive)

  return  (random & $7FFF_FFFF) // ((hi - lo) + 1) + lo


con { license }

{{

  Terms of Use: MIT License

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.

}}
