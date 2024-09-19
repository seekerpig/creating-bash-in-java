package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class CommandBuilderTest {
    private ApplicationRunner appRunner;

    /**
     * Sets up the test environment before each test case, including initializing a mock ApplicationRunner.
     */
    @BeforeEach
    void setUp() {
        appRunner = mock(ApplicationRunner.class);
    }

    /**
     * Tests parsing an empty command string. Verifies that a ShellException is thrown.
     */
    @Test
    void parseCommand_EmptyCommandString_ShouldThrowShellException() {
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand("", appRunner));
    }

    /**
     * Tests parsing a command string that contains only a newline. Verifies that a ShellException is thrown.
     */
    @Test
    void parseCommand_ContainsNewLine_ShouldThrowShellException() {
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(STRING_NEWLINE, appRunner));
    }

    /**
     * Tests parsing a command string with a double pipe operator. Verifies that a ShellException is thrown.
     */
    @Test
    void parseCommand_DoublePipe_ShouldThrowShellException() {
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand("echo 'hello' || wc", appRunner));
    }

    /**
     * Tests parsing a command string with a single arrow ("<") operator. Verifies that a ShellException is thrown.
     */
    @Test
    void parseCommand_SingleArrow_ShouldThrowShellException() {
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand("<", appRunner));
    }

    /**
     * Tests parsing a command string with a pipe operator at the start. Verifies that a ShellException is thrown.
     */
    @Test
    void parseCommand_PipeOperatorAtStart_ShouldThrowShellException() {
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand("| echo", appRunner));
    }

    /**
     * Tests parsing a correct "echo" command string. Verifies that the correct CallCommand is retrieved and no exception is thrown.
     *
     * @throws ShellException If an error occurs during command parsing
     */
    @Test
    void parseCommand_CorrectEcho_ShouldRetrieveCorrectCommandAndDontThrowException() throws ShellException {
        assertDoesNotThrow(() -> CommandBuilder.parseCommand("echo hello world", appRunner));

        CallCommand finalCommand = (CallCommand) CommandBuilder.parseCommand("echo hello world", appRunner);
        assertEquals(List.of("echo", "hello", "world"), finalCommand.getArgsList()); //NOPMD - suppressed AvoidDuplicateLiterals - echo is needed for each test case, hence called repeatedly
    }

    /**
     * Tests parsing a correct "echo" command string with a pipe, leading to another command. Verifies that the correct PipeCommand is retrieved and no exception is thrown.
     *
     * @throws ShellException If an error occurs during command parsing
     */
    @Test
    void parseCommand_CorrectEchoWithPipe_ShouldRetrieveCorrectCommandAndDontThrowException() throws ShellException {
        assertDoesNotThrow(() -> CommandBuilder.parseCommand("echo testing | testing", appRunner));

        PipeCommand finalCommand = (PipeCommand) CommandBuilder.parseCommand("echo testing | grep testing", appRunner);
        List<CallCommand> commandList = finalCommand.getCallCommands();
        assertEquals(List.of("echo", "testing"), commandList.get(0).getArgsList());
        assertEquals(List.of("grep", "testing"), commandList.get(1).getArgsList());
    }

    /**
     * Tests parsing a command string with a sequence of commands separated by a semicolon. Verifies that the correct SequenceCommand is retrieved and no exception is thrown.
     *
     * @throws ShellException If an error occurs during command parsing
     */
    @Test
    void parseCommand_CorrectEchoWithSequence_ShouldRetrieveCorrectCommandAndDontThrowException() throws ShellException {
        assertDoesNotThrow(() -> CommandBuilder.parseCommand("echo testing1 | testing2", appRunner));

        SequenceCommand finalCommand = (SequenceCommand) CommandBuilder.parseCommand("echo testing1 ; echo testing2", appRunner);
        List<Command> commandList = finalCommand.getCommands();

        assertEquals(List.of("echo", "testing1"), ((CallCommand) commandList.get(0)).getArgsList());
        assertEquals(List.of("echo", "testing2"), ((CallCommand) commandList.get(1)).getArgsList());
    }

    /**
     * Tests parsing a command string with correct echo command and I/O redirection. Verifies that the correct CallCommand is retrieved and no exception is thrown.
     *
     * @throws ShellException If an error occurs during command parsing
     */
    @Test
    void parseCommand_CorrectEchoWithIORedirection_ShouldRetrieveCorrectCommandAndDontThrowException() throws ShellException {
        assertDoesNotThrow(() -> CommandBuilder.parseCommand("echo testing1 | testing2", appRunner));

        CallCommand finalCommand = (CallCommand) CommandBuilder.parseCommand("echo testing1 > testing1.txt", appRunner);

        assertEquals(List.of("echo", "testing1", ">", "testing1.txt"), finalCommand.getArgsList());
    }

    /**
     * Tests parsing a command string with unmatched quotes. Verifies that a ShellException is thrown.
     */
    @Test
    void parseCommand_WrongQuotes_ShouldThrowException() {
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand("echo \"testing1", appRunner));

    }

    /**
     * Tests parsing a command string that starts with a semicolon. Verifies that a ShellException is thrown.
     */
    @Test
    void parseCommand_StartWithSemiColon_ShouldThrowException() {
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand("; echo testing1", appRunner));

    }

    /**
     * Tests parsing a command string with a pipe as part of a sequence command. Verifies that the correct SequenceCommand is retrieved.
     *
     * @throws ShellException If an error occurs during command parsing
     */
    @Test
    void parseCommand_PipeAsPartOfSequenceCommand_ShouldRetrieveCorrectCommand() throws ShellException {
        SequenceCommand finalCommand = (SequenceCommand) CommandBuilder.parseCommand("echo testing1 > testing1.txt ; echo testing2 > testing2.txt", appRunner);
        List<Command> commandList = finalCommand.getCommands();

        assertEquals(List.of("echo", "testing1", ">", "testing1.txt"), ((CallCommand) commandList.get(0)).getArgsList());
        assertEquals(List.of("echo", "testing2", ">", "testing2.txt"), ((CallCommand) commandList.get(1)).getArgsList());

    }
}