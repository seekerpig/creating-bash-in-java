package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SequenceIT { //NOPMD
    private ShellImpl shell;
    private OutputStream outputStream;
    private final String initialEnvironment = Environment.currentDirectory; //NOPMD - suppressed LongVariable - Variable Name is More Readable

    /**
     * Initializes resources before each test.
     * Sets up the shell and an output stream to capture the command outputs.
     */
    @BeforeEach
    void setUp() {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Cleans up resources after each test.
     * Specifically, it deletes the test directory 'testResources' created during the tests.
     *
     * @throws IOException if an error occurs during directory deletion.
     */
    @AfterEach
    void tearDown() throws IOException {
        Path subFolderPath = Paths.get(initialEnvironment).resolve("testResources");
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

    /**
     * Test to verify that two valid commands separated by a semicolon are parsed, evaluated,
     * and executed sequentially, producing the expected output.
     */
    @Test
    public void sequence_twoValidCommands_ShouldRunAndOutputSequentially() throws FileNotFoundException, AbstractApplicationException, ShellException {
        shell.parseAndEvaluate("echo hello; echo world", outputStream);

        assertEquals("hello" + StringUtils.STRING_NEWLINE + "world" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Test to confirm that three valid commands separated by semicolons are parsed, evaluated,
     * and executed sequentially, yielding the correct concatenated output.
     */
    @Test
    public void sequence_threeValidCommands_ShouldRunAndOutputSequentially() throws FileNotFoundException, AbstractApplicationException, ShellException {
        shell.parseAndEvaluate("echo hello; echo world; echo thirdline", outputStream);

        assertEquals("hello" + StringUtils.STRING_NEWLINE + "world" + StringUtils.STRING_NEWLINE + "thirdline" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Test to ensure that when two valid commands with a dependency are executed sequentially,
     * the first command's effect is correctly reflected in the second command's output.
     */
    @Test
    public void sequence_twoValidCommandsWithDependency_FirstCommandShouldAffectSecondCommand() throws IOException, AbstractApplicationException, ShellException {

        Path currPath = Paths.get(initialEnvironment);
        Path subFolderPath = currPath.resolve("testResources");
        Files.createDirectories(subFolderPath);


        String fileName = "testFile1.txt";
        Path pathToFile = subFolderPath.resolve(fileName);
        Files.writeString(pathToFile, "testfile1 content");

        shell.parseAndEvaluate("cd testResources; ls", outputStream);

        assertEquals("testFile1.txt" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Test to verify that when the second command in a sequence is invalid, the shell outputs
     * an error message for the invalid command while still executing and outputting the result of the valid command.
     */
    @Test
    public void sequence_secondCommandInvalid_ShouldHaveExceptionMessageInOutput() throws IOException, AbstractApplicationException, ShellException {
        shell.parseAndEvaluate("invalidcommand; echo helloworld", outputStream);
        assertEquals("shell: invalidcommand: Invalid app" + StringUtils.STRING_NEWLINE +
                "helloworld" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Test to ensure that when the first command in a sequence is invalid, the shell outputs an error message for the invalid command
     * and proceeds to execute the next valid command, displaying its output correctly.
     */
    @Test
    public void sequence_firstCommandInvalid_ShouldHaveExceptionMessageInOutput() throws IOException, AbstractApplicationException, ShellException {
        shell.parseAndEvaluate("echo helloworld; invalidcommand", outputStream);
        assertEquals("helloworld" + StringUtils.STRING_NEWLINE +
                "shell: invalidcommand: Invalid app" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Test to confirm that executing a single command followed by a semicolon without a subsequent command throws the appropriate exception,
     * indicating that the command sequence is improperly formatted.
     */
    @Test
    public void sequence_singleCommandWithSemicolon_ShouldThrowExceptionAsThereIsNoSecondCommand() throws IOException, AbstractApplicationException, ShellException {
        assertThrows(ShellException.class, () -> shell.parseAndEvaluate("echo helloworld;", outputStream));
    }

}
