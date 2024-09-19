package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ShellImplTest {
    private OutputStream outputStream;
    Shell shell;

    /**
     * Sets up the test environment before each test case, including initializing a new Shell implementation and a ByteArrayOutputStream
     * to capture the output stream.
     */
    @BeforeEach
    void setUp() {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Tests parsing and evaluating a valid input command using the shell. Verifies that no exception is thrown during the process.
     */
    @Test
    void parseAndEvaluate_validInput_shouldNotThrowException() {
        assertDoesNotThrow(() -> shell.parseAndEvaluate("ls", outputStream));
    }

    /**
     * Tests parsing and evaluating an invalid input command using the shell. Verifies that an exception is thrown to indicate the error.
     */
    @Test
    void parseAndEvaluate_invalidInput_shouldThrowException() {
        assertThrows(Exception.class, () -> shell.parseAndEvaluate("", outputStream));
    }
}