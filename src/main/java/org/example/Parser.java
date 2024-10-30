package org.example;

public class Parser {
    private final String cmd;
    private final String[] arguments;

    public Parser(String[] commands) {
        cmd = commands[0];
        arguments = commands;
    }


    public String getCmd() {
        // arguments[0] = commands[0]
        return cmd;
    }

    public String getFirstArgument() {
        if(arguments.length < 2)
            return "";
        else
            return arguments[1];
    }

    public String getSecondArgument() {
        if(arguments.length < 3)
            return "";
        else
            return arguments[2];
    }
}
