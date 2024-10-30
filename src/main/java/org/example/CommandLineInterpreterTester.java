package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandLineInterpreterTester {

    private CommandLineInterpreter cli;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        cli = new CommandLineInterpreter();
        System.setOut(new PrintStream(outContent));
        Main.currentDirectory = System.getProperty("user.dir"); // set current directory to project root
        Main.homeDirectory = Main.currentDirectory; // setting home directory to current
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    // pwd
    @Test
    public void testPwd() {
        assertEquals(Main.currentDirectory, cli.pwd());
    }

    // cd
    @Test
    public void testCdHome() {
        cli.cd("", 1);
        assertEquals(Main.homeDirectory, Main.currentDirectory);
    }

    @Test
    public void testCdPreviousDirectory() {
        String initialDir = Main.currentDirectory;
        Main.previousDirectory = "/tmp"; // Example previous directory
        cli.cd("", 2);
        assertEquals("/tmp", Main.currentDirectory);
        assertEquals(initialDir, Main.previousDirectory);
    }

    @Test
    public void testCdSpecificPath() {
        String path = System.getProperty("user.dir"); // Set to current directory
        cli.cd(path, 3);
        assertEquals(path, Main.currentDirectory);
    }

    @Test
    public void testCdInvalidPath() {
        String invalidPath = "invalid/path";
        cli.cd(invalidPath, 3);
        assertEquals("Invalid path: invalid/path Directory does not exist!", outContent.toString().trim());
    }

    // mkdir
    @Test
    public void testMkdirNewDirectory() {
        String newDirName = "testDir";
        cli.mkdir(newDirName);
        File newDir = new File(newDirName);
        assertEquals(newDir.exists(), true);
        newDir.delete(); // Cleanup
    }

    @Test
    public void testMkdirExistingDirectory() {
        String newDirName = "testDir";
        File newDir = new File(newDirName);
        newDir.mkdir();
        cli.mkdir(newDirName);
        assertEquals("Directory name has already been used!", outContent.toString().trim());
        newDir.delete(); // Cleanup
    }

    @Test
    public void testMkdirEmptyDirectoryName() {
        cli.mkdir("");
        assertEquals("mkdir must be called with a new directory name!", outContent.toString().trim());
    }

    // rmdir
    @Test
    public void testRmdirEmptyDirectory() {
        String dirName = "emptyDir";
        File dir = new File(dirName);
        dir.mkdir();
        cli.rmdir(dirName, 1);
        assertEquals(false, dir.exists());
    }

    @Test
    public void testRmdirNonEmptyDirectory() {
        String dirName = "nonEmptyDir";
        File dir = new File(dirName);
        dir.mkdir();
        try {
            // Create a file inside the directory to make it non-empty
            new File(dir, "file.txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cli.rmdir(dirName, 2);
        assertEquals("This directory is not empty!", outContent.toString().trim());

        // Cleanup
        new File(dir, "file.txt").delete();
        dir.delete();
    }


    @Test
    public void testRmdirInvalidDirectory() {
        cli.rmdir("invalidDir", 1);
        assertEquals("Directory does not exist!", outContent.toString().trim());
    }

    // rm
    @Test
    public void testRmDirectory() {
        String dirName = "testRmDir";
        File dir = new File(dirName);
        dir.mkdir();
        cli.rm(dirName);
        assertEquals(false, dir.exists());
    }

    @Test
    public void testRmFile() {
        String fileName = "testFile.txt";
        File file = new File(fileName);
        try {
            file.createNewFile();
            cli.rm(fileName);
            assertEquals(false, file.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRmInvalidFile() {
        String invalidFileName = "invalidFile.txt";
        cli.rm(invalidFileName);
        assertEquals("Directory does not exist !", outContent.toString().trim());
    }

    // mv
    @Test
    public void testMvDirectory() {
        String sourceDirName = "sourceDir";
        String destinationDirName = "destinationDir";
        File sourceDir = new File(sourceDirName);
        File destinationDir = new File(destinationDirName);

        sourceDir.mkdir();
        cli.mv(sourceDirName, destinationDirName);
        assertEquals(false, sourceDir.exists());
        assertEquals(true, destinationDir.exists());

        destinationDir.delete(); // Cleanup
    }

    @Test
    public void testMvFile() {
        String sourceFileName = "sourceFile.txt";
        String destinationFileName = "destinationFile.txt";
        File sourceFile = new File(sourceFileName);

        try {
            sourceFile.createNewFile();
            cli.mv(sourceFileName, destinationFileName);
            assertEquals(false, sourceFile.exists());
            assertEquals(true, new File(destinationFileName).exists());

            new File(destinationFileName).delete(); // Cleanup
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMvInvalidSource() {
        String invalidSource = "invalidSource";
        String destination = "destinationDir";
        cli.mv(invalidSource, destination);
        assertEquals("Source directory does not exist.", outContent.toString().trim());
    }

    // redirectOutput (>)
    @Test
    public void testRedirectOutputNewFile() {
        String fileName = "testOutput.txt";
        cli.redirectOutput("Hello World", fileName, false);  // Overwrite mode
        File file = new File(fileName);
        assertEquals(true, file.exists());

        // Additional check: Verify the content matches
        try (Scanner scanner = new Scanner(file)) {
            assertEquals("Hello World", scanner.nextLine().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        file.delete(); // Cleanup
    }

    @Test
    public void testRedirectOutputAppendToFile() {
        String fileName = "testOutput.txt";
        cli.redirectOutput("Hello World", fileName, false);  // First write
        cli.redirectOutput("Appended Text", fileName, true); // Append

        File file = new File(fileName);
        assertEquals(true, file.exists());

        // Additional check: Verify the appended content
        try (Scanner scanner = new Scanner(file)) {
            assertEquals("Hello World", scanner.nextLine().trim());
            assertEquals("Appended Text", scanner.nextLine().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        file.delete(); // Cleanup
    }
}
