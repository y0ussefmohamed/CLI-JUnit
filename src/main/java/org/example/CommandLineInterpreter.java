package org.example;

import org.example.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommandLineInterpreter {
    // pwd
    public String pwd() {
        return Main.currentDirectory;
    }

    // cd
    public void cd(String path, int type) {
        if (type == 1)  // Change currentDir to home (no arguments)
        {
            Main.previousDirectory = Main.currentDirectory;
            Main.currentDirectory = Main.homeDirectory;
        } else if (type == 2) // "cd .." - Go to previous directory
        {
            String tempDirectory = Main.previousDirectory;
            Main.previousDirectory = Main.currentDirectory;
            Main.currentDirectory = tempDirectory;
        } else if (type == 3) // Change to a specific path
        {
            File newDir = new File(path);
            if (newDir.exists() && newDir.isDirectory()) {
                Main.previousDirectory = Main.currentDirectory;
                Main.currentDirectory = newDir.getAbsolutePath();
            }
            else
                System.out.println("Invalid path: " + path + " Directory does not exist!");

        } else
            System.out.println("Invalid cd command usage!");

    }

    // ls

    // ls -a

    // ls -r

    // mkdir
    public void mkdir(String newDirectoryName) {
        if(newDirectoryName.isEmpty())
            System.out.println("mkdir must be called with a new directory name!");
        else {
            File newDirectory = new File(newDirectoryName);
            if(newDirectory.exists())
                System.out.println("Directory name has already been used!");
            else {
                newDirectory.mkdir();
                System.out.println(newDirectoryName + " directory has been created successfully √");
            }
        }
    }
    // rmdir
    public void rmdir(String directoryNameToRemove, int type) {

        if (directoryNameToRemove.isEmpty()) {
            System.out.println("rmdir must be called with a directory name!");
        } else {
            File directoryToRemove = new File(directoryNameToRemove);

            if (directoryToRemove.exists()) {
                if (type == 1) {
                    removeEmptyDirectories(directoryToRemove);
                }
                else if(type == 2)
                {
                    if (directoryToRemove.delete()) // deletion occurs and returns true
                        System.out.println(directoryNameToRemove + " has been removed √");
                    else
                        System.out.println("This directory is not empty!");
                }
            }
            else
                System.out.println("Directory does not exist!");

        }
    }

    private void removeEmptyDirectories(File directory) {
        File[] subFiles = directory.listFiles();
        if (subFiles != null) {
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    removeEmptyDirectories(file);
                }
            }
        }
        if (directory.delete()) {
            System.out.println(directory.getPath() + " was empty and has been removed.");
        }
    }


    // touch

    // mv
    public void mv(String sourcePath, String destinationPath) {
        File source = new File(sourcePath);
        File destination = new File(destinationPath);

        if (!source.exists()) {
            System.out.println("Source directory does not exist.");
            return;
        }

        // If the destination is a directory, move the source into this directory
        if (destination.isDirectory()) {
            destination = new File(destinationPath, source.getName());
        }

        if (source.renameTo(destination)) {
            System.out.println("Moved successfully from " + sourcePath + " to " + destinationPath + " √");
        } else {
            System.out.println("Failed to move the Directory.");
        }
    }

    // rm
    public void rm(String directoryNameToRemove) {
        File directoryToRemove = new File(directoryNameToRemove);

        if (!directoryToRemove.exists()) {
            System.out.println("Directory does not exist !");
            return;
        }

        if (directoryToRemove.isDirectory()) {
            deleteEntireDirectory(directoryToRemove);
            System.out.println("Directory " + directoryNameToRemove + " and all its contents have been removed √");
        }
        else
        {
            if (directoryToRemove.delete())
                System.out.println("File " + directoryNameToRemove + " has been removed √");
            else
                System.out.println("Failed to delete the file.");
        }
    }

    private void deleteEntireDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if (file.isDirectory()) {
                    deleteEntireDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    // cat

    // >
    public void redirectOutput(String commandOutput, String fileName, boolean append) {
        try (FileWriter fileWriter = new FileWriter(fileName, append)) {
            fileWriter.write(commandOutput + "\n");
            if (append) {
                System.out.println("Appended to " + fileName + " successfully √");
            } else {
                System.out.println("Written in " + fileName + " successfully got replaced √");
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // >>


    // | (pipe)
    public String executePipe(String command1Output, String command2) {
        String[] splitCommand = command2.split("\\s+");
        Parser parser = new Parser(splitCommand);
        String cmd = parser.getCmd();

        // Process command2 using command1's output as input if needed
        switch (cmd.toLowerCase()) {
          /*  case "cat":
                return cat(parser.getFirstArgument());
            case "ls":
                return ls();
            case "ls -a":
                return lsAll();
            case "ls -r":
                return lsReverse();
            // Add other command cases here as needed*/
            default:
                return "Unknown command for pipe: " + cmd;
        }
    }
}