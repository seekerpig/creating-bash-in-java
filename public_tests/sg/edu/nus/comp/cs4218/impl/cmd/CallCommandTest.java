package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CallCommandTest {
    private CallCommand command;
    private ApplicationRunner appRunner;
    private List<String> argsList;
    private ArgumentResolver argumentResolver;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * Sets up the testing environment before each test. This includes initializing the ApplicationRunner,
     * ArgumentResolver, the input stream, and the output stream.
     */
    @BeforeEach
    void setUp() {
        appRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        inputStream = System.in;
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Tests the behavior of the CallCommand's evaluate method when the arguments list is null.
     * Verifies that a ShellException is thrown to indicate that the command cannot be executed without arguments.
     */
    @Test
    void evaluate_nullArgsList_shouldThrowShellException() {
        CallCommand command = new CallCommand(null, appRunner, argumentResolver);
        assertThrows(ShellException.class, () -> command.evaluate(inputStream, outputStream));
    }

    /**
     * Tests the behavior of the CallCommand's evaluate method when the arguments list is empty.
     * Verifies that a ShellException is thrown to indicate that the command cannot be executed with an empty arguments list.
     */
    @Test
    void evaluate_argsListEmpty_shouldThrowShellException() {
        argsList = new ArrayList<String>();
        CallCommand command = new CallCommand(argsList, appRunner, argumentResolver);
        assertThrows(ShellException.class, () -> command.evaluate(inputStream, outputStream));
    }

    /**
     * Tests the getArgsList method when a test arguments list is provided. Verifies that the method returns the same test arguments list.
     */
    @Test
    void getArgsList_givenTestArgsList_shouldReturnTestArgsList() {
        argsList = Arrays.asList("hello", "world");
        command = new CallCommand(argsList, appRunner, argumentResolver);
        assertSame(argsList, command.getArgsList());
    }

    /**
     * Tests the getArgsList method when the arguments list is null. Verifies that the method returns null.
     */
    @Test
    void getArgsList_givenNullArgsList_shouldReturnNull() {
        command = new CallCommand(null, appRunner, argumentResolver);
        assertNull(command.getArgsList());
    }

    /**
     * Tests the evaluate method with a correct command. Verifies that the command produces the correct output.
     *
     * @throws FileNotFoundException        if the file to be written to or read from does not exist
     * @throws AbstractApplicationException if an application-specific error occurs
     * @throws ShellException               if a shell-specific error occurs
     */
    @Test
    void evaluate_givenCorrectCommand_shouldObtainCorrectOutput() throws FileNotFoundException, AbstractApplicationException, ShellException {
        argsList = Arrays.asList("echo", "hello world");
        command = new CallCommand(argsList, appRunner, argumentResolver);
        command.evaluate(inputStream, outputStream);
        assertEquals("hello world" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests the terminate method to verify that calling it does not throw any exceptions.
     */
    @Test
    void terminate_called_ShouldNotThrowException() {
        CallCommand command = new CallCommand(null, appRunner, argumentResolver);
        assertDoesNotThrow(() -> command.terminate());
    }
}