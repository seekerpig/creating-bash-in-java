package sg.edu.nus.comp.cs4218.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class CommandSubstitutionIT { // NOPMD
    private ShellImpl shell;
    private ByteArrayOutputStream outputStream;
    private static String wcsingleline = "wc_test_single_line.txt";
    private static String testFileName = "testFile.txt";
    private final String initialEnvironment = Environment.currentDirectory; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private Path subFolderPath;

    private static BufferedWriter writer;

    static {
        try {
            writer = new BufferedWriter(new FileWriter(wcsingleline));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
        subFolderPath = Paths.get(initialEnvironment).resolve("testDirectory2");
        Files.createDirectories(subFolderPath);

        Path pathToFile = subFolderPath.resolve(testFileName);
        Files.writeString(pathToFile, "This is a test file."); //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has variations
    }

    /**
     * Cleans up resources after each test.
     * Specifically, it deletes the test directory 'testResources' created during the tests.
     *
     * @throws IOException if an error occurs during directory deletion.
     */
    @AfterEach
    void tearDown() throws IOException {
        deleteDirectoryRecursively(subFolderPath);
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

    @Test
    public void echo_WithCommandSubstitutionEcho_ShouldReturnEcho() throws Exception {
        String command = "echo `echo hello world`";
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail("Failed to run command: " + e.getMessage());
        }
        String expected = "hello world" + System.lineSeparator();
        String actual = outputStream.toString();
        assertEquals(expected, actual);
    }

    /**
     * Test for command substitution using echo and ls
     */
    @Test
    public void echo_WithCommandSubstitutionLs_ShouldReturnEcho() throws Exception {
        String command = "cd testDirectory2; echo `ls`";
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail("Failed to run command: " + e.getMessage());
        }
        String expected = testFileName + System.lineSeparator();
        String actual = outputStream.toString();
        assertEquals(expected, actual);
    }
}
