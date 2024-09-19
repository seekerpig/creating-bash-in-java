package sg.edu.nus.comp.cs4218.impl.integration.pipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class EchoApplicationIT { // NOPMD
    private ShellImpl shell;

    private ByteArrayOutputStream outputStream;

    private static final String[] FILE_NAMES = {"input.txt", "output.txt"};

    @BeforeEach
    public void setUp() {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();

        // cleanup
        for (String fileName : FILE_NAMES) {
            File folder = new File(fileName);
            if (folder.exists()) {
                folder.delete();
            }
        }
    }

    @AfterEach
    public void tearDown() {
        Path currentPath = Paths.get(Environment.currentDirectory);
        for (String fileName : FILE_NAMES) {
            Path filePath = currentPath.resolve(fileName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                fail("Failed to delete file: " + e.getMessage());
            }
        }
    }

    /**
     * Test to check if echo and cat prints out correct output.
     */
    @Test
    public void echo_PipedWithCat_PrintsCorrectOutput() {
        String input = "Hello World"; //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable, and some test case has variations
        String expectedOutput = input + System.lineSeparator();

        try {
            shell.parseAndEvaluate("echo " + input + " | cat", outputStream);
            assertEquals(expectedOutput, outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage()); //NOPMD - suppressed AvoidDuplicateLiterals - It is more readable as a string than a global variable
        }
    }

    /**
     * Test to check if echo and cut with character flag prints out correct output.
     */
    @Test
    public void echo_PipedWithCutForCharacters_PrintsCorrectOutput() {
        try {
            shell.parseAndEvaluate("echo \"Hello World\" | cut -c 2-5", outputStream);
            assertEquals("ello" + System.lineSeparator(), outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * Test to check if echo and cut with byte flag prints out correct output.
     */
    @Test
    public void echo_PipedWithCutForBytes_PrintsCorrectOutput() {
        try {
            shell.parseAndEvaluate("echo \"Hello World\" | cut -b 2-5", outputStream);
            assertEquals("ello" + System.lineSeparator(), outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * Test to check if echo and wc prints out correct output.
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void echo_PipedWithWcForBytes_PrintsCorrectOutput() {
        try {
            shell.parseAndEvaluate("echo \"Hello World\" | wc -c", outputStream);
            assertEquals("\t12" + System.lineSeparator(), outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * Test to check if echo and sort prints out correct output.
     */
    @Test
    public void echo_PipedWithSort_PrintsCorrectOutput() {
        try {
            shell.parseAndEvaluate("echo \"Hello World\" | sort", outputStream);
            assertEquals("Hello World" + System.lineSeparator(), outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * Test to check if echo and grep prints out correct output.
     */
    @Test
    public void echo_PipedWithGrep_PrintsCorrectOutput() {
        try {
            shell.parseAndEvaluate("echo \"Hello World\" | grep \"Hello\"", outputStream);
            assertEquals("Hello World" + System.lineSeparator(), outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * Test to check if echo and paste prints out correct output.
     */
    @Test
    public void echo_PipedWithPaste_PrintsCorrectOutput() {
        try {
            shell.parseAndEvaluate("echo \"Hello World\" | paste", outputStream);
            assertEquals("Hello World" + System.lineSeparator(), outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * Test to check if echo and uniq prints out correct output.
     */
    @Test
    public void echo_PipedWithUniq_PrintsCorrectOutput() {
        try {
            shell.parseAndEvaluate("echo \"Hello World\" | uniq", outputStream);
            assertEquals("Hello World" + System.lineSeparator(), outputStream.toString());
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
