' UNIX-style command line parsing for Spin
{{
    This object defines a rich command line parser for Spin terminals.

    Some key features:

    -   Automatically generates command line help.
    -   Supports options and one positional argument.
    -   Supports multiple commands.
    -   Uses only the `string` library so you can plug it into any output driver you want.

    `commandparser` will generate your list of commands for you on demand.

        $ help
        This shell enables an interface to a live Propeller for debugging.
        commands:

            hex         output contents of memory to terminal
            info        print information about the running application
            help        show help

    It will also generate help text for individual commands.

        $ help hex
        Usage: hex [OPTIONS]... ADDR

        options:

            -l VAL      Lines to print (default 16, limit 64)

        positional argument:

            ADDR        The starting address of the RAM to examine.

    It generates specific errors when incorrect commands are typed.

        $ hex -l
        ERROR: Option missing parameter

    ## Usage

    As with any thing else, we can define a string in place or in a DAT block elsewhere.
    In this case, however, it is a good idea to define them in a DAT block because we will
    refer to the strings by name frequently, and this way it is less wasteful.

    ### Setting up the parser

    So let's start by writing out all our descriptions of the commands we want to use.

        DAT
            data_description    byte    "This shell enables an interface to a live"
                                byte    "Propeller for debugging.",0

            cmd_help            byte    "help",0

            cmd_hex             byte    "hex",0
            cmd_hex_desc        byte    "output hex to terminal",0

            cmd_info            byte    "info",0
            cmd_info_desc       byte    "print information about the running application",0

            opt_b               byte    "-b",0
            opt_b_desc          byte    "Enable some feature",0

            opt_c               byte    "-c",0
            opt_c_desc          byte    "And cool",0

            opt_d               byte    "-d",0
            opt_d_desc          byte    "Turn this on!",0

            pos_file            byte    "FILE",0
            pos_file_desc       byte    "The file on which to operate",0

    #### Setting the description

    The description gives people a general overview of what this interface is for. It is the
    first thing displayed when `Usage` is called.

        commandparser.SetDescription(@data_description)

    #### Adding a command

    Let's add our first command, `hex`. We store the return value from `AddCommand` in a variable
    because we will need it to add options to it.

        hex := commandparser.AddCommand(@cmd_hex, @cmd_hex_desc)

    Now let's add the options.

        commandparser.AddOption (hex, @opt_b, @opt_b_desc, true)
        commandparser.AddOption (hex, @opt_c, @opt_c_desc, true)
        commandparser.AddOption (hex, @opt_d, @opt_d_desc, false)

    Now let's add a positional argument.

        commandparser.AddPositionalArgument (hex, @pos_file, @pos_file_desc)

    #### Adding another command

    Now that we've added `hex`, let's add a second command called `info`.

        commandparser.AddCommand(@cmd_info, @cmd_info_desc)

    By default, this library allows a maximum of 5 commands, 5 options, and 10 arguments.
    This can be easily change by updating the constants.

    #### Starting up

    The only thing left to do before we can start parsing arguments is to start the parser.
    This performs the remaining setup.

        commandparser.Start(@line, MAX_LINE)

    @line is a pointer to a string that will contain the workspace for the command line parser.
    It needs to be long enough to fit all of the arguments, which will depend on your needs.

    Here is a good example definition:

        CON
            MAX_LINE = 40
        VAR
            byte    line[MAX_LINE]

    ### Processing commands

    Processing arguments is straightforward. It is as simple as setting up some boilerplate code.

    The first step is running `Process`, and checking to see if it returned any errors. If so,
    we can get more information about what happened with `ErrorString` and remind the user how
    to use the command line with `Usage`. The printout from `Usage` is context-sensitive and changes
    depending on what the user was last doing.

        if (i := commandparser.Process) <> commandparser#ERROR_NONE
            term.Str (commandparser.ErrorString(i))
            term.Str (commandparser.Usage)

    We can also explicitly print the help information if a command is received.

        elseif commandparser.IsCommand (@cmd_help)
            term.Str (commandparser.Usage)

    Here is where we start processing commands

        elseif commandparser.IsCommand (@cmd_hex)
            HexCommand

        elseif commandparser.IsCommand (@cmd_info)
            InfoCommand

    An `else` is not be needed as an error would have been received before it had a chance to get here.

    ### Processing options and positional arguments

    Once the command is known, we can start processing the arguments for individual commands with the
    following functions: `IsSet`, `Value`, and `PositionalArgument`.

    -   `IsSet` returns true if an option was set, otherwise false.
    -   `Value` returns a pointer to the string containing the value of the option if available, or null.
    -   `PositionalArgument` returns a pointer to the string containing the positional argument, or null
        if not available.

    And that's all. The parser is ready to use.
}}
CON

    MAX_ARGUMENTS   = 10
    MAX_COMMANDS    = 5
    MAX_OPTIONS     = 5
    MAX_ERRORS      = 5

    MAX_USAGE       = (MAX_COMMANDS+10)*40

    #0, ERROR_NONE, ERROR_MAX_ARGS, ERROR_INVALID_ARGS, ERROR_INVALID_COMMAND, ERROR_MISSING_PARAMETER

OBJ

    str : "string"

VAR

    byte    max_line
    word    ptr_str
    word    ptr_description

    byte    argc
    word    argv[MAX_ARGUMENTS]

    byte    cmdc
    word    command_name[MAX_COMMANDS]
    word    command_description[MAX_COMMANDS]
    word    command_positional_argument_name[MAX_COMMANDS]
    word    command_positional_argument_description[MAX_COMMANDS]

    byte    currentcommand

    byte    optc
    byte    option_cmd[MAX_OPTIONS]
    word    option_name[MAX_OPTIONS]
    word    option_description[MAX_OPTIONS]
    byte    option_hasvalue[MAX_OPTIONS]
    word    option_value[MAX_OPTIONS]

    byte    posc
    word    positional_argument

    word    error_string[MAX_ERRORS]
    byte    data_usage[MAX_USAGE]

PUB Start(s, size)
{{
    Initialize the command line parser. You will need provide a string and
    corresponding size of which you want the parser to be able to handle.
}}

    ptr_str := s
    max_line := size

    error_string[ERROR_MAX_ARGS]            := string("Max arguments")
    error_string[ERROR_INVALID_ARGS]        := string("Invalid arguments")
    error_string[ERROR_INVALID_COMMAND]     := string("Invalid command")
    error_string[ERROR_MISSING_PARAMETER]   := string("Option missing parameter")

    AddCommand (string("help"), string("show help"))

PUB Process | i
{{
    Process all command line tokens inside of the string.

    This command should be run once every time ths argument string changes.
}}

    wordfill(@option_value, 0, MAX_OPTIONS)

    argc := 0
    posc := 0
    currentcommand := -1
    argv[argc] := str.Tokenize (ptr_str)

    currentcommand := MatchCommand(argv[0])

    if (currentcommand == 255)
        return ERROR_INVALID_COMMAND

    argv[++argc] := str.Tokenize (0)

    repeat while argv[argc]

        if argc => MAX_ARGUMENTS
            return ERROR_MAX_ARGS

        if (i := MatchOption(argv[argc])) > -1

            if option_hasvalue[i]
                argv[++argc] := str.Tokenize (0)

                ifnot argv[argc]
                    return ERROR_MISSING_PARAMETER
                    next

                if argc => MAX_ARGUMENTS
                    return ERROR_MAX_ARGS

                if IsOption(argc)
                    return ERROR_INVALID_ARGS

                option_value[i] := argv[argc]

        else

            ifnot IsOption(argc)
                positional_argument[posc++] := argv[argc]
            else
                return ERROR_INVALID_ARGS

        argv[++argc] := str.Tokenize (0)

PUB AddCommand(name, description)
{{
    Add a new command to the parser, with the name `name` and
    a brief description called `description`.

    This parser supports multiple commands, with different options
    and positional arguments for each command.
}}

    result := cmdc
    if cmdc < MAX_COMMANDS
        if MatchCommand(name) <> -1
            return -1
        command_name[cmdc] := name
        command_description[cmdc] := description
        cmdc++

PUB AddOption(cmd, name, description, hasvalue)
{{
    Add an option to an existing command.

    -   `name` - the name of the new option. Must start with `-`.
        For example, `-b` or `--new`.

    -   `description` - a description of the new option for the
        help printer. No text wrapping is performed so try to keep
        descriptions short.

    -   `hasvalue` - if true, the command line parser will look for
        a parameter following this option in the list of arguments.
        For example, `--digit 23423`, `-b hello`.

    This command line does not support quotation marks, so parameters
    should be single word.

    This function does nothing if the command is not
    already defined.
}}

    result := optc
    if optc < MAX_OPTIONS
        if MatchOption(name) <> -1
            return -1
        option_cmd[optc] := cmd
        option_name[optc] := name
        option_description[optc] := description
        option_hasvalue[optc] := hasvalue
        optc++

PUB AddPositionalArgument(cmd, name, description)
{{
    Add a positional argument to the command line parser.

    These are arguments that are not associated with any option.
    For example, if you had the following list of arguments:

        hex -b 115200 0x3234 -d on

    The positional argument would be 0x3234.

    This command line parser supports exactly one positional
    argument per command.
}}

    if cmd => 0 and cmd < MAX_COMMANDS
        command_positional_argument_name[cmd] := name
        command_positional_argument_description[cmd] := description

PUB PositionalArgument
{{
    Return a pointer to the string containing the positional
    argument.
}}

    return positional_argument

PUB IsCommand(cmd)
{{
    Return true if the command passed to the parser is equal
    to `cmd`, otherwise false.
}}

    return str.Match(argv[0], cmd)

PUB IsOption(n)
{{
    Return true if the argument at position `n` in the string
    is an option, otherwise false.
}}

    return (byte[argv[n]][0] == "-")

PUB IsSet(option) | i, j
{{
    Return true if the option `option` was passed on the
    command line, otherwise false.
}}

    i := MatchOption(option)
    if option_hasvalue[i]
        return (option_value[i] <> 0)
    else
        j := 0
        repeat while j < argc
            if str.Match(argv[j++], option)
                return true

PUB Value(option) | i
{{
    Return a pointer to the string containing the value of
    the option `option`.
}}

    if (i := MatchOption(option)) > -1
        return option_value[i]

PUB SetDescription(s)
{{
    Set a brief description for the command line parser and
    what it does.
}}

    ptr_description := s

PUB ErrorString(err)
{{
    Return a string describing the error that was thrown when
    `Process` was called.
}}

    return error_string[err]

PUB Usage | i
{{
    Print a formatted help of the current command line parser.

    `commandparser` automatically adds a `help` function to the
    list of commands when a parser is created.

    `Usage` does one of two things depending on when it is called.

    -   If an invalid command has been passed to the parser, or `help`
        is called without a positional argument, a list of commands
        is printed.

    -   If a valid command is passed but its parameters are invalid,
        or `help` passed with the name of a valid command, usage
        for that command is printed.
}}

    str.Clear (@data_usage)

    if (currentcommand == 255)
        BuildHelp
    elseif IsCommand(string("help"))
        i := MatchCommand(PositionalArgument)
        if i > -1
            BuildCommandHelp(i)
        else
            BuildHelp
    else
        BuildCommandHelp(currentcommand)

    str.Append(@data_usage, string(10))

    return @data_usage

PRI BuildHelp | i

    str.Append(@data_usage, ptr_description)
    str.Append(@data_usage, string(10,"commands:",10,10))

    i := 0
    repeat while i < cmdc
        AddLine(command_name[i], command_description[i], 0)
        i++

PRI BuildCommandHelp(cmd) | i, c

    str.Append(@data_usage, string("Usage: "))
    str.Append(@data_usage, command_name[cmd])

    c:= 0
    repeat i from 0 to optc - 1
        if option_cmd[i] == cmd
            c++

    if c > 0
        str.Append(@data_usage, string(" [OPTIONS]..."))

    if command_positional_argument_name[cmd] <> 0
        str.Append(@data_usage, string(" "))
        str.Append(@data_usage, command_positional_argument_name[cmd])
        str.Append(@data_usage, string(10))

    if c > 0
        str.Append(@data_usage, string(10,"options:",10,10))

        i := 0
        repeat while i < optc
            if (option_cmd[i] == cmd)
                AddLine(option_name[i], option_description[i], option_hasvalue[i])
            i++

    if command_positional_argument_name[cmd] <> 0
        str.Append(@data_usage, string(10,"positional argument:",10,10))
        AddLine(command_positional_argument_name[cmd], command_positional_argument_description[cmd], 0)

PRI AddLine(name, description, hasvalue)

    str.Append(@data_usage, string("    "))
    str.Append(@data_usage, name)

    if hasvalue
        str.Append(@data_usage, string(" VAL"))
        bytefill  (@data_usage + strsize(@data_usage), " ", 12 - strsize(name) - 4)
    else
        bytefill  (@data_usage + strsize(@data_usage), " ", 12 - strsize(name))

    str.Append(@data_usage, description)
    str.Append(@data_usage, string(10))

PRI MatchCommand(s) | j

    j := 0
    repeat while j < cmdc

        if str.Match(s, command_name[j])
            return j
        j++

    return -1

PRI MatchOption(s) | j

    j := 0
    repeat while j < optc
        if currentcommand == option_cmd[j] and str.Match(s, option_name[j])
            return j
        j++

    return -1
