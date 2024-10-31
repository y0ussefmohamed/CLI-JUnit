package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static String currentDirectory = System.getProperty("user.dir");
    public static String homeDirectory = System.getProperty("user.dir");
    public static String previousDirectory = homeDirectory;

    public static void main(String[] args) throws IOException {
        Scanner inp = new Scanner(System.in);

        System.out.println();
        System.out.println("Command Line Interpreter is now working: ");
        while (true) {
            System.out.print(currentDirectory + " :-$ ");
            String commandLine = inp.nextLine();
            String[] pipedCommands = commandLine.split("\\|");
            String output = "";

            for (int i = 0; i < pipedCommands.length; i++) {
                CommandLineInterpreter CLI = new CommandLineInterpreter();
                CLI.pwd();
                String command = pipedCommands[i].trim();
                String[] splittedCommand = command.split("\\s+");
                Parser parser = new Parser(splittedCommand);
                String cmd = parser.getCmd();

                if (cmd.equalsIgnoreCase("pwd")) {
                    output = CLI.pwd();
                }

                else if (cmd.equalsIgnoreCase("cat")){
                    CLI.Cat(parser.getFirstArgument(), parser.getSecondArgument());
                    output = "";
                }

                else if(parser.getFirstArgument().equalsIgnoreCase(">") || parser.getSecondArgument().equalsIgnoreCase(">")) {
                    if (cmd.equalsIgnoreCase("ls")){
                        if (Objects.equals(parser.getFirstArgument(), "-r")) {
                            CLI.redirectOutput(CLI.ls("-r"), parser.getThirdArgument(), false);
                        } else if (Objects.equals(parser.getFirstArgument(), "-a")) {
                            CLI.redirectOutput(CLI.ls("-a"), parser.getThirdArgument(), false);;
                        }
                        else if (Objects.equals(parser.getFirstArgument(), ">")) {
                            CLI.redirectOutput(CLI.ls(""), parser.getSecondArgument(), false);
                        }
                    }
                    output = "";
                }

                else if(parser.getFirstArgument().equalsIgnoreCase(">>") || parser.getSecondArgument().equalsIgnoreCase(">>")) {
                    if (cmd.equalsIgnoreCase("ls")){
                        if (Objects.equals(parser.getFirstArgument(), "-r")) {
                            CLI.redirectOutput(CLI.ls("-r"), parser.getThirdArgument(), true);
                        } else if (Objects.equals(parser.getFirstArgument(), "-a")) {
                            CLI.redirectOutput(CLI.ls("-a"), parser.getThirdArgument(), true);;
                        }
                        else if (Objects.equals(parser.getFirstArgument(), ">>")) {
                            CLI.redirectOutput(CLI.ls(""), parser.getSecondArgument(), true);
                        }
                    }
                    output = "";
                }

                else if (cmd.equalsIgnoreCase("mkdir")) {
                    CLI.mkdir(parser.getFirstArgument());
                    output = "";
                }
                else if (cmd.equalsIgnoreCase("cd")) {
                    if (Objects.equals(parser.getFirstArgument(), ""))
                        CLI.cd("", 1);
                    else if (Objects.equals(parser.getFirstArgument(), ".."))
                        CLI.cd("", 2);
                    else
                        CLI.cd(parser.getFirstArgument(), 3);
                    output = "";
                }
                else if (cmd.equalsIgnoreCase("rmdir")) {
                    if (Objects.equals(parser.getSecondArgument(), "")) {
                        if (Objects.equals(parser.getFirstArgument(), "*"))
                            CLI.rmdir(homeDirectory, 1);
                        else
                            CLI.rmdir(parser.getFirstArgument(), 2);
                    } else {
                        System.out.println("mkdir doesn't take a second argument!");
                    }
                }
                else if (cmd.equalsIgnoreCase("mv")) {
                    CLI.mv(parser.getFirstArgument(), parser.getSecondArgument());
                }
                else if (cmd.equalsIgnoreCase("rm")) {
                    if (Objects.equals(parser.getSecondArgument(), ""))
                        CLI.rm(parser.getFirstArgument());
                    else
                        System.out.println("cd doesn't take a second argument!");
                }

                else if (cmd.equalsIgnoreCase("help")) {
                    CLI.help();
                }

                else if (cmd.equalsIgnoreCase("ls")){
                    if (Objects.equals(parser.getSecondArgument(), "")) {
                        if (Objects.equals(parser.getFirstArgument(), "-r")) {
                            System.out.println(CLI.ls("-r"));
                        } else if (Objects.equals(parser.getFirstArgument(), "-a")) {
                            System.out.println(CLI.ls("-a"));
                        }
                        else if (Objects.equals(parser.getFirstArgument(), "")) {
                            System.out.println(CLI.ls(""));
                        }
                    }
                    else {
                        System.out.println("ls doesn't take any arguments !");
                    }
                    output = "";
                }


                else if (cmd.equalsIgnoreCase("touch")){
                    if (Objects.equals(parser.getSecondArgument(), "")) {
                        CLI.touch(parser.getFirstArgument());
                    }
                    else {
                        System.out.println("touch doesn't take a second argument !");
                    }
                    output = "";
                }

                else if (cmd.equalsIgnoreCase("exit")) {
                    return;
                }
                else {
                    System.out.println("Unknown command: " + cmd);
                }

                // If this is not the last command, pass output to the next command in the pipe
                if (i < pipedCommands.length - 1) {
                    output = CLI.executePipe(output, pipedCommands[i + 1].trim());
                } else if (!output.isEmpty()) {
                    System.out.println(output);
                }

                if (Objects.equals(command, "exit"))
                    break;
            }
        }
    }
}
