package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;



public class Main {
    // System.getProperty
    public static String currentDirectory = ("/Users/youssefmo/Downloads/CLI-JUnit");
    public static String homeDirectory = ("/Users/youssefmo/Downloads/CLI-JUnit");
    public static String previousDirectory = homeDirectory;
    public static void main(String[] args) throws IOException {
        Scanner inp = new Scanner(System.in); // To take fast input

        System.out.println();
        System.out.println("Command Line Interpreter is now working: ");
        while(true) {
            System.out.print(currentDirectory + " :-$ "); String commandLine = inp.nextLine();
            String[] pipedCommands = commandLine.split("\\|");
            String output = "";

            for (int i = 0; i < pipedCommands.length; i++) {
                CommandLineInterpreter CLI = new CommandLineInterpreter();
                CLI.pwd();
                String command = pipedCommands[i].trim();
                String[] splittedCommand = command.split("\\s+");
                Parser parser = new Parser(splittedCommand);
                String cmd = parser.getCmd();

                switch (cmd.toLowerCase()) {
                    case "pwd":
                        output = CLI.pwd();
                        break;
                   /* case "ls":
                        output = CLI.ls();
                        break;
                    case "ls -a":
                        output = CLI.lsAll();
                        break;
                    case "ls -r":
                        output = CLI.lsReverse();
                        break;*/
                    case "mkdir":
                        CLI.mkdir(parser.getFirstArgument());
                        output = "";
                        break;
                    case "cd": {
                        if(Objects.equals(parser.getFirstArgument(), ""))
                            CLI.cd("", 1);
                        else if(Objects.equals(parser.getFirstArgument(), ".."))
                            CLI.cd("", 2);
                        else
                            CLI.cd(parser.getFirstArgument(), 3);

                        output = "";
                        break;
                    }
                    case "rmdir": {
                        if(Objects.equals(parser.getSecondArgument(), ""))
                            if(Objects.equals(parser.getFirstArgument(), "*"))
                                CLI.rmdir(homeDirectory, 1);
                            else
                                CLI.rmdir(parser.getFirstArgument(), 2);
                        else
                            System.out.println("mkdir doesn't take a second argument !");
                        break;
                    }
                    case "mv": {
                        CLI.mv(parser.getFirstArgument(), parser.getSecondArgument());
                        break;
                    }
                    case "rm": {
                        if (Objects.equals(parser.getSecondArgument(), ""))
                            CLI.rm(parser.getFirstArgument());
                        else
                            System.out.println("cd doesn't take a second argument !");
                        break;
                    }
                   /* case "cat":
                        output = CLI.cat(parser.getFirstArgument());
                        break;
                    case "touch":
                        CLI.touch(parser.getFirstArgument());
                        output = "";
                        break;*/
                    case ">":
                        CLI.redirectOutput(output, parser.getFirstArgument(), false);
                        output = "";
                        break;
                    default:
                        System.out.println("Unknown command: " + cmd);
                }

                // If this is not the last command, pass output to the next command in the pipe
                if (i < pipedCommands.length - 1) {
                    output = CLI.executePipe(output, pipedCommands[i + 1].trim());
                } else if (!output.isEmpty()) {
                    System.out.println(output);
                }



                if(Objects.equals(command, "exit"))
                    break;
            }
        }
    }
}