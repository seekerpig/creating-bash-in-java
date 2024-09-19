package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

/**
 * This class contains unit tests for the PipeCommand class.
 * It tests the functionality of executing commands in a pipe.
 */
@ExtendWith(MockitoExtension.class)
public class PipeCommandTest {


    private ApplicationRunner appRunner;


    private ArgumentResolver argumentResolver;
    private static final String FOLDERNAME = "testResources";

    @BeforeEach
    void setUp() {
        appRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
    }

    @AfterEach
    void tearDown() throws IOException {
        Path subFolderPath = Paths.get(Environment.currentDirectory).resolve(FOLDERNAME);
        deleteDirectoryRecursively(subFolderPath);
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


    private String generateRandomContent(int length) {
        int leftLimit = 97;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Test case for executing a single command in a pipe.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void evaluate_OneCommand_Successful() throws Exception {
        List<CallCommand> callCommands = List
                .of(new CallCommand(Arrays.asList("echo", "hello"), appRunner, argumentResolver)); //NOPMD - suppressed AvoidDuplicateLiterals - Using echo as a direct string is more readable
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        pipeCommand.evaluate(stdin, stdout);

        assertEquals("hello" + StringUtils.STRING_NEWLINE, stdout.toString());
    }

    /**
     * Test case for executing multiple commands in a pipe.
     * It creates a list of CallCommand objects representing the commands to be
     * executed.
     * <p>
     * An empty input stream is used as the standard input, and a
     * ByteArrayOutputStream is used as the standard output.
     * The PipeCommand is evaluated, and the output is compared with the expected
     * output "hello world\n".
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void evaluate_MultipleCommands_Written() throws Exception {
        List<CallCommand> callCommands = Arrays.asList(
                new CallCommand(Arrays.asList("echo", "hello world"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("grep", "hello"), appRunner, argumentResolver)); //NOPMD - suppressed AvoidDuplicateLiterals - Using a string instead of a variable is a lot more readable
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        pipeCommand.evaluate(stdin, stdout);

        assertEquals("hello world" + StringUtils.STRING_NEWLINE, stdout.toString());
    }

    /**
     * Test case to verify the exception handling behavior of the PipeCommand class.
     * It tests whether the PipeCommand correctly throws a ShellException when an
     * invalid command is encountered.
     */
    @Test
    public void evaluate_InvalidCommand_ThrowsException() {
        List<CallCommand> callCommands = Arrays.asList(
                new CallCommand(Arrays.asList("echo", "hello"), appRunner, argumentResolver),
                new CallCommand(List.of("invalidCommand"), appRunner, argumentResolver));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        assertThrows(ShellException.class, () -> pipeCommand.evaluate(stdin, stdout));
    }

    /**
     * Test case for evaluating the stream handling functionality of the PipeCommand
     * class.
     * It tests the evaluation of a pipeline of two CallCommands: an "echo" command
     * followed by a "grep" command.
     * The expected behavior is that the output of the "echo" command, containing
     * the word "stream", is passed as input to the "grep" command,
     * and the output of the "grep" command is written to the stdout stream.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    public void evaluate_TwoCommands_WrittenToStream() throws Exception {
        List<CallCommand> callCommands = Arrays.asList(
                new CallCommand(Arrays.asList("echo", "stream test"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("grep", "stream"), appRunner, argumentResolver));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        pipeCommand.evaluate(stdin, stdout);

        assertEquals("stream test" + StringUtils.STRING_NEWLINE, stdout.toString());
    }

    /**
     * Test case to verify the behavior of the PipeCommand when the command list is
     * empty.
     * It expects the evaluate method to throw a ShellException.
     */
    @Test
    public void evaluate_EmptyList_ThrowsException() {
        List<CallCommand> callCommands = new ArrayList<>();
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        assertThrows(ShellException.class, () -> pipeCommand.evaluate(stdin, stdout),
                "Expected evaluate to throw ShellException, but it did not");
    }

    /**
     * Validates the successful evaluation of commands reading from and writing to a file, and piping between commands.
     * A file 'testFile1.txt' with content 'hello abc' is created in a 'testResources' subdirectory. The test performs
     * a 'cat' command to read this file, followed by a 'grep' command to filter the content. The output is then verified
     * against the expected result. This test demonstrates the application's capability to handle file operations and
     * command pipelining.
     *
     * @throws Exception if file operations, command execution, or assertions fail.
     */
    @Test
    public void evaluate_ReadFromFileAndPipe_SuccessfulEvaluation() throws Exception {
        Path currPath = Paths.get(Environment.currentDirectory);
        Path subFolderPath = currPath.resolve(FOLDERNAME);
        Files.createDirectories(subFolderPath);

        String fileName = "testFile1.txt"; //NOPMD - suppressed AvoidDuplicateLiterals -  Using a string in each test instead of a global variable is a lot more readable
        Path pathToFile = subFolderPath.resolve(fileName);

        String content = "hello abc";
        Files.writeString(pathToFile, content);


        List<CallCommand> callCommands = Arrays.asList(
                new CallCommand(Arrays.asList("cat", FOLDERNAME + File.separator + "testFile1.txt"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("grep", "hello"), appRunner, argumentResolver));
        PipeCommand pipeCommand = new PipeCommand(callCommands);

        InputStream fileInputStream = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        pipeCommand.evaluate(fileInputStream, stdout);

        assertEquals(content + StringUtils.STRING_NEWLINE, stdout.toString());
    }

    /**
     * Tests successful input redirection and piping with commands 'sort' and 'grep'. A file named 'testFile1.txt' is
     * created in the 'testResources' directory with specific content. The 'sort' command sorts its content in reverse
     * order, and the 'grep' command filters lines containing 'test'. The final output is captured and validated to
     * ensure correct functionality of input redirection and command piping in processing file content.
     *
     * @throws Exception for issues with file operations, command execution, or if the output assertion fails.
     */
    @Test
    public void evaluate_InputRedirectionWithPipe_WrittenToStreamSuccessfully() throws Exception {
        Path currPath = Paths.get(Environment.currentDirectory);
        Path subFolderPath = currPath.resolve(FOLDERNAME);
        Files.createDirectories(subFolderPath);


        String fileName = "testFile1.txt";

        Path pathToFile = subFolderPath.resolve(fileName);

        String content = "aello abc" + StringUtils.STRING_NEWLINE + "testing fff"; //NOPMD - suppressed AvoidDuplicateLiterals - Using a string in each test instead of a global variable is a lot more readable
        Files.writeString(pathToFile, content);
        List<CallCommand> callCommands = List.of(
                new CallCommand(Arrays.asList("sort", "-r", "<", FOLDERNAME + File.separator + "testFile1.txt"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("grep", "test"), appRunner, argumentResolver)
        );
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        pipeCommand.evaluate(stdin, stdout);

        assertEquals("testing fff" + StringUtils.STRING_NEWLINE, stdout.toString());
    }

    /**
     * Tests successful output redirection and piping involving 'sort' and 'grep' commands. This method creates
     * 'testFile1.txt' in a 'testResources' directory, writes predefined content, and then executes a sequence of
     * commands where the 'sort' command sorts the content in reverse order, followed by a 'grep' command that
     * filters lines containing 'test'. The output of 'grep' is redirected to 'testFile2.txt'. The test verifies
     * the output file contains the expected result, demonstrating the application's handling of output redirection
     * and command piping to process and store results in a file.
     *
     * @throws Exception for issues with file operations, command execution, or if content validation fails.
     */

    @Test
    public void evaluate_OutputRedirectionWithPipe_WrittenToFileSuccessfully() throws Exception {
        Path currPath = Paths.get(Environment.currentDirectory);
        Path subFolderPath = currPath.resolve(FOLDERNAME);
        Files.createDirectories(subFolderPath);


        String fileName = "testFile1.txt";


        Path pathToFile = subFolderPath.resolve(fileName);

        String content = "aello abc" + StringUtils.STRING_NEWLINE + "testing fff";
        Files.writeString(pathToFile, content);

        List<CallCommand> callCommands = List.of(
                new CallCommand(Arrays.asList("sort", "-r", "<", FOLDERNAME + File.separator + "testFile1.txt"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("grep", "test", ">", FOLDERNAME + File.separator + "testFile2.txt"), appRunner, argumentResolver)
        );
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        pipeCommand.evaluate(stdin, stdout);
        String fileName2 = "testFile2.txt";
        Path pathToFile2 = subFolderPath.resolve(fileName2);
        assertEquals("testing fff" + StringUtils.STRING_NEWLINE, Files.readString(pathToFile2));
    }

    /**
     * Test evaluates behavior of PipeCommand with non-existing files. It constructs a PipeCommand
     * using a CallCommand with 'sort' command on a non-existing file. The method checks if executing
     * evaluate on PipeCommand with no actual files throws the expected Exception. This scenario simulates
     * the error handling capability of PipeCommand when dealing with file-based commands that fail due to
     * file not found issues.
     * <p>
     * This test ensures that the system robustly handles errors and directs appropriate error messages or exceptions
     * to stderr, maintaining system stability in face of invalid input scenarios.
     *
     * @throws Exception to indicate any exceptions thrown during test execution are part of the expected outcomes,
     *                   specifically targeting the handling of non-existent file operations within command executions.
     */
    @Test
    public void evaluate_NoFiles_StdErrNotEmpty() throws Exception {
        List<CallCommand> callCommands = List.of(
                new CallCommand(Arrays.asList("sort", "non_existing_file.txt"), appRunner, argumentResolver));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        assertThrows(Exception.class, () -> pipeCommand.evaluate(stdin, stdout));
    }

    /**
     * Test case to verify the successful execution of a command chain.
     * The command chain consists of three commands: echo, grep, and wc.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void evaluate_multipleChains_NoError() throws Exception {
        List<CallCommand> callCommands = Arrays.asList(
                new CallCommand(Arrays.asList("echo", "chaining commands"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("grep", "commands"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("wc", "-w"), appRunner, argumentResolver));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        pipeCommand.evaluate(stdin, stdout);

        String formatChar = "\t2";
        assertEquals(formatChar + StringUtils.STRING_NEWLINE, stdout.toString());
    }

    /**
     * Test case to verify the behavior of a pipe command with an intermediate
     * command failure.
     * It creates a list of CallCommand objects representing the commands in the
     * pipe.
     * <p>
     * An empty input stream and a ByteArrayOutputStream for output are used.
     */
    @Test
    public void evaluate_IntermediateError_ThrowsException() {
        List<CallCommand> callCommands = Arrays.asList(
                new CallCommand(Arrays.asList("echo", "initial command"), appRunner, argumentResolver),
                new CallCommand(List.of("invalidCommand"), appRunner, argumentResolver),
                new CallCommand(Arrays.asList("wc", "-w"), appRunner, argumentResolver));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        assertThrows(ShellException.class, () -> {
            pipeCommand.evaluate(stdin, stdout);
        }, "Expected ShellException to be thrown due to invalid command");
    }
}