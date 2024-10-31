package org.example;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class CommandLineInterpreter {

    public void help() {
        System.out.println("Available Commands:");
        System.out.println("pwd          - Prints the current directory.");
        System.out.println("cd           - Changes the current directory:");
        System.out.println("               cd (type 1) - Change to home directory.");
        System.out.println("               cd .. (type 2) - Go to previous directory.");
        System.out.println("               cd <path> (type 3) - Change to a specific path.");
        System.out.println();
        System.out.println("ls           - Lists directory contents.");
        System.out.println("ls -a        - Lists all files including hidden files.");
        System.out.println("ls -r        - Lists files in reverse order (to be implemented).");
        System.out.println("mkdir <dir>  - Creates a new directory with the specified name.");
        System.out.println("rmdir <dir>  - Removes an empty directory or verifies if non-empty:");
        System.out.println("               rmdir <dir> (type 1) - Removes all empty subdirectories.");
        System.out.println("               rmdir <dir> (type 2) - Deletes directory if empty.");
        System.out.println();
        System.out.println("touch <file> - Creates a new, empty file with the specified name.");
        System.out.println("mv <src> <dst> - Moves a file or directory to a new location.");
        System.out.println("rm <file/dir> - Removes a file or an entire directory and its contents.");
        System.out.println("cat <file>   - Displays the content of a file.");
        System.out.println("> <file>     - Redirects output to overwrite a file.");
        System.out.println(">> <file>    - Redirects output to append to a file.");
        System.out.println("| <cmd>      - Pipes the output of one command as input to another command.");
        System.out.println("help         - Displays this help message.");
        System.out.println("exit         - Exits the terminal.");
        System.out.println();
    }

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
        }
        else if (type == 2) // "cd .." - Go to previous directory
        {
            File prevDir = new File(Main.previousDirectory);
            if (prevDir.exists() && prevDir.isDirectory()) {
                String tempDirectory = Main.previousDirectory;
                Main.previousDirectory = Main.currentDirectory;
                Main.currentDirectory = tempDirectory;
            }
            else
                System.out.println("Previous directory does not exist! ...Staying in the current directory.");
        }
        else if (type == 3) // Change to a specific path
        {
            File newDir = new File(path);
            if (newDir.exists() && newDir.isDirectory()) {
                Main.previousDirectory = Main.currentDirectory;
                Main.currentDirectory = newDir.getAbsolutePath();
            } else {
                System.out.println("Invalid path: " + path + " Directory does not exist!");
            }
        }
        else {
            System.out.println("Invalid cd command usage!");
        }
    }


    // ls
    public String ls(String s){
        StringBuilder a = new StringBuilder();
        if (Objects.equals(s, "")) {
            File file = new File(System.getProperty("user.dir"));
            if (!file.exists()) {
                System.out.println("Directory does not exist!");
            } else {
                File[] files = file.listFiles();
                if (files != null) { // Check to avoid potential null pointer exception
                    Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
                }

                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory() && !f.isHidden()) {
                            //System.out.println("directory: " + f.getName());
                            a.append("directory: ").append(f.getName());
                            a.append("\n");
                        } else if (f.isFile() && !f.isHidden()) {
                            //System.out.println("     file: " + f.getName());
                            a.append("     file: ").append(f.getName());
                            a.append("\n");
                        }
                    }
                }

            }
        } else if (Objects.equals(s, "-r")) {
            return ls_r();
        } else if (Objects.equals(s, "-a")) {
            return ls_a();
        }
        return a.toString();
    }

    // ls -a
    public String ls_a(){
        File file = new File(System.getProperty("user.dir"));
        StringBuilder a = new StringBuilder();
        if (!file.exists()) {
            System.out.println("Directory does not exist!");
        } else {
            File[] files = file.listFiles();
            if (files != null) { // Check to avoid potential null pointer exception
                Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            }

            assert files != null;
            for (File f : files) {
                if (f.isDirectory()) {
                    a.append("directory: ").append(f.getName());
                    a.append("\n");
//                    System.out.println("directory: " + f.getName());
                } else if (f.isFile()) {
                    a.append("     file: ").append(f.getName());
                    a.append("\n");
//                    System.out.println("     file: " + f.getName());
                }
            }
        }
        return a.toString();
    }
    // ls -r
    public String ls_r(){
        StringBuilder a = new StringBuilder();
        File file = new File(System.getProperty("user.dir"));
        if (!file.exists()){
            System.out.println("Directory does not exist!");
        }
        else {
            File[] files = file.listFiles();
            if (files != null) { // Check to avoid potential null pointer exception
                Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            }
            assert files != null;
            for (int i = files.length - 1; i >= 0; i--) {
                if (files[i].isDirectory() && !files[i].isHidden()) {
                    a.append("directory: ").append(files[i].getName());
                    a.append("\n");
//                    System.out.println("directory: " + files[i].getName());
                } else if (files[i].isFile() && !files[i].isHidden()) {
                    a.append("     file: ").append(files[i].getName());
                    a.append("\n");
//                    System.out.println("     file: "+ files[i].getName());
                }
            }
        }
        return a.toString();
    }

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
    public void touch(String name){
        File file = new File(name);
        if (!file.exists()) {
            try{
                if (file.createNewFile()){
                    System.out.println("Created a new file: " + file.getName());
                }
                else {
                    System.out.println("Failed to create the file.");
                }
            }
            catch (IOException e){
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
        else {
            System.out.println("File already exists!");
        }
    }

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
    public void Cat(String arg1, String fileName){
        Scanner inp = new Scanner(System.in);
        String inputString = "";
        BufferedReader br;
        if (arg1.equalsIgnoreCase(">")){
            try {
                File myObj = new File(fileName);
                if (myObj.createNewFile()){
                    System.out.println("Created a new file: " + myObj.getName());
                }
                else {
                    System.out.println("file already exists!");
                }
                FileOutputStream fos = new FileOutputStream(myObj);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                System.out.println("write what you want in this file then @z to close the file");
                while (!inputString.equalsIgnoreCase("@z")){
                    inputString = inp.nextLine();
                    if (!inputString.equalsIgnoreCase("@z")){
                        bw.write(inputString);
                        bw.newLine();
                    }
                }
                bw.close();
            } catch (IOException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        } else if (arg1.equalsIgnoreCase(">>")) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
                System.out.println("write what you want in this file then @z to close the file");
                while (!inputString.equalsIgnoreCase("@z")){
                    inputString = inp.nextLine();
                    if (!inputString.equalsIgnoreCase("@z")){
                        out.print(inputString);
                    }
                }
                out.close();
            }
            catch (IOException e){
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
        else {
            try {
                br = new BufferedReader(new FileReader(arg1));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
            catch (Exception e){
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    // >
    public void redirectOutput(String commandOutput, String fileName, boolean append) {
        try (FileWriter fileWriter = new FileWriter(fileName, append)) {
            fileWriter.write(commandOutput + "\n");
            if (append)
                System.out.println("Appended to " + fileName + " successfully √");
            else
                System.out.println("Written in " + fileName + " successfully got replaced √");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }


    // | (pipe)
  public String executePipe(String command1Output, String command2) {
        String[] splitCommand = command2.trim().split("\\s+");
        Parser parser = new Parser(splitCommand);
        String cmd = parser.getCmd();

        // Handle redirection if `>` or `>>` is used
        if (cmd.equals(">") || cmd.equals(">>")) {
            String fileName = parser.getFirstArgument();
            boolean append = cmd.equals(">>");

            // Redirect output to file
            try (FileWriter writer = new FileWriter(fileName, append)) {
                writer.write(command1Output);
                return "";  // No output to pass along the pipe
            } catch (IOException e) {
                return "Error writing to file: " + e.getMessage();
            }
        }

        // Process other commands based on command name
      return "";
    }
}
