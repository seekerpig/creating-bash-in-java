package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Integration test class for testing globbing functionalities in the ShellImpl.
 * This class tests the ability of the shell to interpret and execute commands
 * with globbing patterns, ensuring that file and directory names matching these
 * patterns are correctly identified and processed. The tests cover various commands
 * including 'echo', 'rm', 'wc', 'mv', 'paste', 'grep', and 'ls', as well as scenarios
 * involving quoting and IO redirection.
 */
public class GlobbingIT { //NOPMD
    private ShellImpl shell;
    private OutputStream outputStream;

    private final String initialEnvironment = Environment.currentDirectory; //NOPMD - suppressed LongVariable - Variable Name is More Readable

    private static final String TESTFOLDERNAME = "testResources";
    private Path subFolderPath;

    /**
     * Initializes resources before each test.
     * Sets up the shell and an output stream to capture the command outputs.
     */
    @BeforeEach
    void setUp() throws IOException {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
        Path currPath = Paths.get(initialEnvironment);
        subFolderPath = currPath.resolve(TESTFOLDERNAME);
        Files.createDirectories(subFolderPath);
    }

    /**
     * Cleans up resources after each test.
     * Specifically, it deletes the test directory 'testResources' created during the tests.
     *
     * @throws IOException if an error occurs during directory deletion.
     */
    @AfterEach
    void tearDown() throws IOException {
        Path subFolderPath = Paths.get(initialEnvironment).resolve(TESTFOLDERNAME);
        Path subFolderPath2 = subFolderPath.resolve("test");
        deleteDirectoryRecursively(subFolderPath);
        deleteDirectoryRecursively(subFolderPath2);
        Environment.currentDirectory = initialEnvironment;
    }

    /**
     * Deletes the given directory recursively.
     *
     * @param path the path to the directory to be deleted
     * @throws IOException if an I/O error occurs
     */
    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    /**
     * Tests globbing with the echo command to ensure it outputs files found in the directory.
     * This verifies that the shell correctly interprets globbing patterns to match and list files.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withEchoCommand_shouldOutputFoundFilesInDirectory() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt"; //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has variations
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content"); //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has variations

        String fileName2 = "testFile2.txt"; //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has variations
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content"); //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has variations

        String fileName3 = "testFile3"; //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has slight variations
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "testfile3 content"); //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has variations

        shell.parseAndEvaluate("cd testResources; echo *", outputStream);
        assertEquals("testFile1.txt testFile2.txt testFile3" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests the deletion of files that do not exist using globbing patterns with the rm command.
     * This test expects an exception to be thrown, verifying that error handling works as expected.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_DeleteFileThatDoesNotExist_shouldThrowException() throws IOException, AbstractApplicationException, ShellException {
        assertThrows(Exception.class, () -> shell.parseAndEvaluate("rm *.abc", outputStream));
    }

    /**
     * Tests invalid usage of globbing with the cd command, expecting an exception.
     * This test verifies that inappropriate globbing patterns result in errors as expected.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_InvalidUseOfGlobbingInCD_shouldThrowException() throws IOException, AbstractApplicationException, ShellException {
        assertThrows(Exception.class, () -> shell.parseAndEvaluate("cd *", outputStream));

    }

    /**
     * Tests the word count (wc) command with multiple files using globbing patterns.
     * Verifies that wc produces correct word counts for all valid files matched by the glob.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_wcMultipleFiles_shouldOutputWordCountsForAllValidFiles() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "1");

        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "12");

        String fileName3 = "testFile3";
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "123");

        shell.parseAndEvaluate("cd testResources; wc *.txt", outputStream);
        String line1 = "\t0\t1\t1 testFile1.txt";
        String line2 = "\t0\t1\t2 testFile2.txt";
        String line3 = "\t0\t2\t3 total";
        assertEquals(line1 + StringUtils.STRING_NEWLINE +
                line2 + StringUtils.STRING_NEWLINE +
                line3 + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests globbing with the echo command using a double asterisk, which should output files found in the directory.
     * This test verifies that the shell handles globbing patterns with double asterisks correctly.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withEchoCommandDoubleAsterisk_shouldOutputFoundFilesInDirectory() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content");

        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content");

        String fileName3 = "testFile3";
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "testfile3 content");

        shell.parseAndEvaluate("cd testResources; echo **", outputStream);
        assertEquals("testFile1.txt testFile2.txt testFile3" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests globbing with the echo command with quoting, ensuring that files matching the pattern are correctly output.
     * This test demonstrates the shell's ability to correctly interpret quoted strings containing globbing patterns.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withEchoCommandDoubleAsteriskAndQuoting_shouldOutputFoundFilesInDirectory() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content");

        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content");

        String fileName3 = "testFile3";
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "testfile3 content");

        shell.parseAndEvaluate("cd testResources; echo \"`echo *.txt`\"", outputStream);
        assertEquals("testFile1.txt testFile2.txt" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests the rm command with globbing to ensure only specified files are removed.
     * Verifies the shell's file deletion capabilities when using globbing patterns to specify files.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withRmCommand_shouldOutputFoundFilesNotRemoved() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content");

        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content");

        String fileName3 = "testFile3";
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "testfile3 content");

        shell.parseAndEvaluate("cd testResources; rm *.txt; ls", outputStream);
        assertEquals("testFile3" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests the mv command with globbing, checking if specified files are moved correctly.
     * This test ensures that the shell can handle file moving operations using globbing patterns to identify files.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withMvCommand_shouldOutputFoundFilesMoved() throws IOException, AbstractApplicationException, ShellException {
        Path subFolderPath2 = subFolderPath.resolve("test");
        Files.createDirectories(subFolderPath2);

        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content");

        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content");

        String fileName3 = "testFile3";
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "testfile3 content");

        shell.parseAndEvaluate("cd testResources; mv *.txt test; ls", outputStream);
        assertEquals("test" + StringUtils.STRING_NEWLINE + "testFile3" + StringUtils.STRING_NEWLINE, outputStream.toString());

        outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate("cd test; ls", outputStream);
        assertEquals("testFile1.txt" + StringUtils.STRING_NEWLINE + "testFile2.txt" + StringUtils.STRING_NEWLINE, outputStream.toString());

    }


    /**
     * Tests IO redirection with quoting and globbing, ensuring correct output in the redirected file.
     * This test checks the shell's capability to handle IO redirection in conjunction with globbing and quoting.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withIORedirectionAndQuoting_shouldOutputSearchResult() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 special content");

        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content");

        String fileName3 = "testFile3.txt";
        Path pathToFile3 = subFolderPath.resolve(fileName3);

        shell.parseAndEvaluate("cd testResources; paste `ls test*` > testFile3.txt", outputStream);
        assertEquals("testfile1 special content\ttestfile2 content" + StringUtils.STRING_NEWLINE, Files.readString(pathToFile3));
    }

    /**
     * Tests the ls command with a txt extension globbing pattern, ensuring all matching files are output.
     * This test confirms that the shell can filter and list files based on globbing patterns with specific extensions.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withLsCommandWithTxtExtension_shouldOutputAllFoundFiles() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content");

        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content");

        String fileName3 = "testFile3";
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "testfile3 content");

        shell.parseAndEvaluate("cd testResources; ls *.txt", outputStream);
        assertEquals("testFile1.txt" + StringUtils.STRING_NEWLINE + "testFile2.txt" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests the ls command with a prefix globbing pattern, verifying that all matching files are listed.
     * This test ensures the shell's ability to use globbing patterns for filtering files based on name prefixes.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws AbstractApplicationException if an application error occurs.
     * @throws ShellException               if a shell processing error occurs.
     */
    @Test
    void globbing_withLsCommandWithPrefix_shouldOutputAllFoundFiles() throws IOException, AbstractApplicationException, ShellException {
        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content");

        String fileName2 = "notTestFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        Files.writeString(pathToFile2, "testfile2 content");

        String fileName3 = "testFile3";
        Path pathToFile3 = subFolderPath.resolve(fileName3);
        Files.writeString(pathToFile3, "testfile3 content");

        shell.parseAndEvaluate("cd testResources; ls test*", outputStream);
        assertEquals("testFile1.txt" + StringUtils.STRING_NEWLINE + "testFile3" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

}
