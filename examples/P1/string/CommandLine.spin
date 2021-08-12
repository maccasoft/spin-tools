CON

    _clkmode = xtal1 + pll16x
    _xinfreq = 5_000_000

    MAX_LINE = 40
    MAX_COMMANDS = 10

OBJ

    term    : "com.serial.terminal"
    str     : "string"

VAR

    long    argc
    word    argv[MAX_COMMANDS]          ' max 10 arguments

    byte    line[MAX_LINE]
    byte    prompt[70]
    byte    directory[64]

PUB Main

    term.Start (115200)

    SetDir(string("~"))

    term.Str (@data_signon)

    repeat
        term.Flush
        term.Str (@prompt)

        term.ReadLine (@line, MAX_LINE)

        if Process(@line)
            term.Str (@data_usage)
            next

PUB Process(s)

    argc := 0
    argv[argc] := str.Tokenize (s)
    repeat while argv[argc]
        argv[++argc] := str.Tokenize (0)

    if argc < 1
        return

    if Match(argv[0], string("ls"))
        ListStuff

    elseif Match(argv[0], string("cd"))
        ChangeDir

    elseif Match(argv[0], string("pwd"))
        PrintWorkingDirectory

    elseif Match(argv[0], string("bizz"))
        Bizz

    elseifnot str.Compare (argv[0], string("help"), false)
        return true

    else
        term.Str (string("Bad command or file name!",10))


PUB PrintWorkingDirectory

    term.Str (@directory)
    term.NewLine

PUB ListStuff | i

    if Match(@directory, string("~/another"))
        term.Str (@data_dir2)
    else
        term.Str (@data_dir1)

PUB ChangeDir | i

    if Match(@directory, string("~"))

        if Match(argv[1], string("another")) or Match(argv[1], string("another/"))
            SetDir(string("~/another"))
        else
            term.Str (string("Not a directory!",10))

    elseif Match(@directory, string("~/another"))

        if Match(argv[1], string(".."))
            SetDir(string("~"))
        else
            term.Str (string("Not a directory!",10))

    elseif str.IsEmpty (argv[1]) or Match(argv[1], string("~"))

        SetDir(string("~"))
        term.Str (string("Not a directory!",10))

PRI SetDir(d)

    str.Copy (@directory, d)
    SetPrompt

PUB Bizz | ran

    ran := cnt

    term.Str (string("RUNNING BIZZ BANG 4.0 in",10,"3...",10))

    repeat 200000

    term.Str (string("2...",10))

    repeat 200000

    term.Str (string("1...",10))

    repeat 200000

    repeat 1000
        term.Char (((ran? & $FF)//64)+32)
        repeat 100

PUB SetPrompt

    str.Copy (@prompt, string("user@propeller:"))
    str.Append (@prompt, @directory)
    str.Append (@prompt, string("$ "))

PRI Match(s1, s2)

    return (str.Compare (s1, s2, true) == 0)

DAT

data_usage
byte    "Commands:",10
byte    "   ls      list files",10
byte    "   pwd     print working directory",10
byte    "   cd      change directory",10
byte    "   bizz    frobnicate the bar library",10
byte    10,0

data_dir1
byte    "another/",10
byte    "coolmusic.mp3",10
byte    "file1.txt",10
byte    "file2.txt",10
byte    0

data_dir2
byte    "..",10
byte    "morestuff.txt",10
byte    0

data_signon
byte    "Type 'help' for commands",10
byte    0
