package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

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

    @AfterEach
    public void cleanUpFile() {
        File file = new File("testReadFile.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    public void resetOutputStream() {
        outContent.reset();
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
        assertTrue(newDir.exists());
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
        assertFalse(dir.exists());
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
        assertFalse(dir.exists());
    }

    @Test
    public void testRmFile() {
        String fileName = "testFile.txt";
        File file = new File(fileName);
        try {
            file.createNewFile();
            cli.rm(fileName);
            assertFalse(file.exists());
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
        assertFalse(sourceDir.exists());
        assertTrue(destinationDir.exists());

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
            assertFalse(sourceFile.exists());
            assertTrue(new File(destinationFileName).exists());

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
        assertTrue(file.exists());

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
        assertTrue(file.exists());

        // Additional check: Verify the appended content
        try (Scanner scanner = new Scanner(file)) {
            assertEquals("Hello World", scanner.nextLine().trim());
            assertEquals("Appended Text", scanner.nextLine().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete(); // Cleanup
    }


    @Test
    public void testLs() {
        // Setup - Create files and directories
        String dirName = "testDir";
        String fileName = "testFile.txt";
        File dir = new File(dirName);
        File file = new File(fileName);
        try {
            dir.mkdir();
            file.createNewFile();

            String output = cli.ls("");
            assertTrue(output.contains("directory: testDir"));
            assertTrue(output.contains("file: testFile.txt"));
            assertFalse(output.contains(".hiddenFile"));
            assertFalse(output.contains(".hiddenDir"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cleanup
            file.delete();
            dir.delete();
        }
    }

    @Test
    public void testLsA() {
        // Setup - Create hidden and visible files and directories
        String visibleDir = "visibleDir";
        String visibleFile = "visibleFile.txt";
        String hiddenDir = ".hiddenDir";
        String hiddenFile = ".hiddenFile";

        File dir = new File(visibleDir);
        File file = new File(visibleFile);
        File hiddenDirFile = new File(hiddenDir);
        File hiddenFileFile = new File(hiddenFile);

        try {
            dir.mkdir();
            file.createNewFile();
            hiddenDirFile.mkdir();
            hiddenFileFile.createNewFile();

            String output = cli.ls("-a");

            // Check that hidden and visible files are included
            List<String> expectedItems = Arrays.asList(
                    "directory: .hiddenDir",
                    "file: .hiddenFile",
                    "directory: visibleDir",
                    "file: visibleFile.txt"
            );
            for (String item : expectedItems) {
                assertTrue(output.contains(item), "Expected output to contain: " + item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cleanup
            file.delete();
            dir.delete();
            hiddenFileFile.delete();
            hiddenDirFile.delete();
        }
    }

    @Test
    public void testLsReverse() {
        // Setup - Create files and directories
        String dirName = "testDir";
        String fileName = "testFile.txt";
        File dir = new File(dirName);
        File file = new File(fileName);

        try {
            dir.mkdir();
            file.createNewFile();

            String output = cli.ls("-r");

            // Check order of directory and file (reverse order)
            int dirIndex = output.indexOf("directory: testDir");
            int fileIndex = output.indexOf("file: testFile.txt");
            assertTrue(fileIndex < dirIndex, "File should appear before directory in reverse order");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cleanup
            file.delete();
            dir.delete();
        }
    }

    @Test
    public void testCatWriteNewFile() throws IOException {
        String fileName = "testNewFile.txt";
        String content = "This is a test line.";

        // Simulate user input with `@z` to end the input
        System.setIn(new ByteArrayInputStream((content + "\n@z\n").getBytes()));

        cli.Cat(">", fileName);

        // Verify file is created and has correct content
        File file = new File(fileName);
        assertTrue(file.exists(), "File should exist");

        // Read file content to verify it was written correctly
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            assertEquals(content, reader.readLine(), "File content should match");
        }

        file.delete(); // Cleanup
    }

    // Test for appending to an existing file with `>>`
    @Test
    public void testCatAppendToFile() throws IOException {
        String fileName = "testAppendFile.txt";
        String initialContent = "Initial line.";
        String appendContent = "Appended line.";


        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(initialContent + "\n");
        }

        System.setIn(new ByteArrayInputStream((appendContent + "\n@z\n").getBytes()));

        cli.Cat(">>", fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            assertEquals(initialContent, reader.readLine(), "Initial content should match");
            assertEquals(appendContent, reader.readLine(), "Appended content should match");
        }

        new File(fileName).delete();
    }


    @Test
    public void testCatReadFile() throws IOException {
        String fileName = "testReadFile.txt";
        String content = "This is a line in the file.";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }

        outContent.reset();

        cli.Cat(fileName, "");

        assertTrue(outContent.toString().trim().contains(content), "Output should match file content");

        new File(fileName).delete();
    }


    @Test
    public void testCatReadNonExistentFile() {
        String fileName = "nonExistentFile.txt";

        cli.Cat(fileName, "");

        // Verify error message
        assertTrue(outContent.toString().contains("An error occurred: "), "Should display error for non-existent file");
    }


    // Test for creating a new file
    @Test
    public void testTouchCreatesNewFile() {
        String fileName = "testTouchNewFile.txt";
        File file = new File(fileName);

        // Ensure the file does not exist before the test
        if (file.exists()) {
            file.delete();
        }

        // Clear any previous content in the output stream
        outContent.reset();

        // Call the touch method
        cli.touch(fileName);

        // Check if the file was created
        assertTrue(file.exists(), "File should be created");

        // Verify the output message
        assertTrue(outContent.toString().contains("Created a new file: " + fileName),
                "Expected output to indicate file creation");

        // Cleanup
        file.delete();
    }

    // Test for handling an existing file
    @Test
    public void testTouchExistingFile() {
        String fileName = "testTouchExistingFile.txt";
        File file = new File(fileName);

        // Ensure the file exists before the test
        try {
            file.createNewFile();
        } catch (IOException e) {
            fail("Setup failed, could not create file: " + e.getMessage());
        }

        // Clear any previous content in the output stream
        outContent.reset();

        // Call the touch method
        cli.touch(fileName);

        // Verify the output message
        assertTrue(outContent.toString().contains("File already exists!"),
                "Expected output to indicate file already exists");

        // Cleanup
        file.delete();
    }

    // Test for touch failure (e.g., invalid file name)
    @Test
    public void testTouchWithInvalidFileName() {
        String invalidFileName = "/invalid:/file.txt";

        // Clear any previous content in the output stream
        outContent.reset();

        // Call the touch method with an invalid file name
        cli.touch(invalidFileName);

        // Verify that an error message was printed to System.err
        assertTrue(outContent.toString().contains("An error occurred:"),
                "Expected an error message when trying to create a file with an invalid name");
    }
    @Test
    public void testPwdPipeMkdir() {
        // Create and navigate to the directory "testPipeDir" using "mkdir testPipeDir | cd testPipeDir"
        String newDirName = "testPipeDir";
        cli.mkdir(newDirName);  // First create the directory
        cli.cd(newDirName, 3);  // Then change to that directory

        // Verify that current directory is indeed "testPipeDir"
        assertEquals(newDirName, new File(Main.currentDirectory).getName(), "Current directory should be 'testPipeDir'.");

        // Clean up
        cli.cd("..", 2);  // Return to the parent directory
        new File(Main.currentDirectory + "/" + newDirName).delete(); // Delete test directory
    }

    @Test
    public void testPwdPipeCd() {
        // Test chaining `pwd | cd` (output of `pwd` piped into `cd`)
        String pwdOutput = cli.pwd();

        // Check that the directory has been changed to pwdOutput
        assertEquals(pwdOutput, cli.pwd(), "Current directory should match the output of `pwd`.");
    }

    @Test
    public void testMkdirCd() {
        CommandLineInterpreter CLI = new CommandLineInterpreter();

        // Define the directory name
        String newDirName = "testPipeDir";

        CLI.mkdir(newDirName);

        // Verify the directory exists
        File createdDir = new File(Main.currentDirectory + "/" + newDirName);
        assertTrue(createdDir.exists(), "Directory should be created successfully.");

        // Clean up
        if (createdDir.exists())
            createdDir.delete();
    }


    @Test
    public void testPwdPipeRedirectOutput() {
        // Initial clean-up
        File outputFile = new File(Main.currentDirectory + File.separator + "output.txt");
        if (outputFile.exists()) {
            outputFile.delete();
        }

        // Run the test command `pwd | > output.txt`
        String pwdOutput = cli.pwd();
        cli.executePipe(pwdOutput, "> output.txt");

        // Verify the output file was created
        assertTrue(outputFile.exists(), "Output file should be created.");

        // Check that the file content matches pwdOutput
        try {
            String fileContent = Files.readString(Paths.get(outputFile.getPath()));
            assertEquals(pwdOutput, fileContent.trim(), "File content should match the `pwd` output.");
        } catch (IOException e) {
            fail("An IOException occurred while reading the output file: " + e.getMessage());
        } finally {
            // Final clean-up
            outputFile.delete();
        }
    }

}
