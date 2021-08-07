{{
    This object can be included as a library or run in place.
}}
CON
    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

    MAX_LINE = 40

OBJ

    term            : "com.serial.terminal"
    commandparser   : "commandparser"
    str             : "string"
    num             : "string.integer"

VAR

    byte    line[MAX_LINE]
    long    stack[40]

PUB Start | hex
{{
    Start the debug shell. This will launch the shell on the the host
    serial port, allowing you to access the Propeller from your computer.

    The debugger is launched in a separate cog.
}}

    term.Start (115200)

    commandparser.SetDescription(@data_description)

    hex := commandparser.AddCommand(@cmd_hex, @cmd_hex_desc)
    commandparser.AddOption (hex, @opt_l, @opt_l_desc, true)

    commandparser.AddPositionalArgument (hex, @pos_addr, @pos_addr_desc)

    commandparser.AddCommand(@cmd_info, @cmd_info_desc)

    commandparser.Start(@line, MAX_LINE)

    cognew(RunShell, @stack)

PRI RunShell | i

    repeat
        term.Flush
        term.Str (@data_prompt)

        term.ReadLine (@line, MAX_LINE)

        if str.IsEmpty (@line)
            next

        if (i := commandparser.Process) <> commandparser#ERROR_NONE

            Error(commandparser.ErrorString(i))
            term.Str (commandparser.Usage)

        elseif commandparser.IsCommand (@cmd_help)

            term.Str (commandparser.Usage)

        elseif commandparser.IsCommand (@cmd_hex)

            HexCommand

        elseif commandparser.IsCommand (@cmd_info)

            InfoCommand

PRI HexCommand | address, c, l
' Derived from Chip Gracey's Monitor program.

    l := 16
    if commandparser.IsSet (@opt_l)
        l := num.StrToBase (commandparser.Value(@opt_l), 10)
        if l < 0
            l := 0
        if l > 63
            l := 63

    address := num.StrToBase (commandparser.PositionalArgument, 16)

    repeat l

        term.Hex (address,4)
        term.Str(string("  "))

        repeat 16
            term.Hex (byte[address++],2)
            term.Char (" ")

        address -= 16
        term.Char (" ")

        repeat 16
            c := byte[address++]
            if not lookdown(c : $20..$80)
                c := "."
            term.Char(c)

        term.NewLine

PRI InfoCommand

    term.Str (string("chipver: "))
    term.Dec (chipver)
    term.NewLine

    term.Str (string("clkfreq: "))
    term.Dec (clkfreq)
    term.Str (string(" MHz"))
    term.NewLine

    term.Str (string("clkmode: "))
    term.Bin (clkmode, 8)
    term.NewLine

PRI Error(s)

    term.Str (string("ERROR: "))
    term.Str (s)
    term.NewLine

DAT

    data_prompt         byte    "$ ",0

    data_description    byte    "This shell enables an interface to a live Propeller for debugging.",0

    cmd_help            byte    "help",0

    cmd_hex             byte    "hex",0
    cmd_hex_desc        byte    "output contents of memory to terminal",0

    cmd_info            byte    "info",0
    cmd_info_desc       byte    "print information about the running application",0

    opt_l               byte    "-l",0
    opt_l_desc          byte    "Lines to print (default 16, limit 64)",0

    pos_addr            byte    "ADDR",0
    pos_addr_desc       byte    "The starting address of the RAM to examine.",0
