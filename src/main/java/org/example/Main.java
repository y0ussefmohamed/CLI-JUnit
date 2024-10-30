package org.example;

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
            CommandLineInterpreter CLI = new CommandLineInterpreter();

            CLI.pwd(); System.out.print(":-$ "); String command = inp.nextLine();

            Parser parser = new Parser(command.split("\\s+"));
            String cmd = parser.getCmd();

            // Explain :
            // mkdir joe     .....
            // cmd   arg[1]  arg[2]

            if(cmd.equalsIgnoreCase("pwd"))
            {
                if(Objects.equals(parser.getFirstArgument(), ""))  {
                    CLI.pwd();
                    System.out.println();
                }
                else
                    System.out.println("pwd doesn't take any arguments !");
            }
            else  if(cmd.equalsIgnoreCase("mkdir"))
            {
                if(Objects.equals(parser.getSecondArgument(), ""))
                    CLI.mkdir(parser.getFirstArgument());
                else
                    System.out.println("mkdir doesn't take a second argument !");
            }
            else if(cmd.equalsIgnoreCase("rmdir"))
            {
                if(Objects.equals(parser.getSecondArgument(), ""))
                    if(Objects.equals(parser.getFirstArgument(), "*"))
                        CLI.rmdir(homeDirectory, 1);
                    else
                        CLI.rmdir(parser.getFirstArgument(), 2);
                else
                    System.out.println("mkdir doesn't take a second argument !");
            }
            else  if(cmd.equalsIgnoreCase("cd"))
            {
                if(Objects.equals(parser.getSecondArgument(), "")) {
                    if(Objects.equals(parser.getFirstArgument(), ""))
                        CLI.cd("", 1);
                    else if(Objects.equals(parser.getFirstArgument(), ".."))
                        CLI.cd("", 2);
                    else
                        CLI.cd(parser.getFirstArgument(), 3);
                }
                else
                    System.out.println("cd doesn't take a second argument !");
            }
            else  if(cmd.equalsIgnoreCase("rm"))
            {
                if(Objects.equals(parser.getSecondArgument(), ""))
                   CLI.rm(parser.getFirstArgument());
                else
                    System.out.println("cd doesn't take a second argument !");
            }
            else  if(cmd.equalsIgnoreCase("mv")) {
                CLI.mv(parser.getFirstArgument(), parser.getSecondArgument());
            }


            if(parser.getFirstArgument().equalsIgnoreCase(">"))
                CLI.redirectOutput(currentDirectory, parser.getSecondArgument(), false);
            else if(parser.getFirstArgument().equalsIgnoreCase(">>")) {
                // TODO();
            }
            else if(parser.getFirstArgument().equalsIgnoreCase("|")) {
                // TODO();
            }

            if(Objects.equals(command, "exit"))
                break;
        }
    }
}