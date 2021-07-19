' Original author: Jeff Martin
{{
    This object provides basic time functions for Spin.

    Sleep methods are adjusted for method cost and freeze-protected.
}}
VAR

    long sync

CON

    WMIN = 381                                                                  ' WAITCNT-expression overhead minimum

PUB Sleep(secs)
{{
    Sleep for `secs` seconds.
}}

    waitcnt(((clkfreq * secs - 3016) #> WMIN) + cnt)

PUB MSleep(msecs)
{{
    Sleep for `msecs` milliseconds.
}}

    waitcnt(((clkfreq / 1_000 * msecs - 3932) #> WMIN) + cnt)

PUB USleep(usecs)
{{
    Sleep for `usecs` microseconds.
}}

    waitcnt(((clkfreq / 1_000_000 * usecs - 3928) #> WMIN) + cnt)

PUB SetSync
{{
    Set starting point for synchronized time delays.
    Wait for the start of the next window with `WaitForSync`.
}}

    sync := cnt

PUB WaitForSync(secs)
{{
    Wait until start of the next `secs`-long time period.

    Must call `SetSync` before calling `WaitForSync` the first time.
}}

    waitcnt(sync += ((clkfreq * secs) #> WMIN))

