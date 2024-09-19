package sg.edu.nus.comp.cs4218.impl.integration.pipe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GrepApplicationIT { // NOPMD
    private ShellImpl shell;

    private ByteArrayOutputStream outputStream;
    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";
    private static final String[] FILE_NAMES = {INPUT_FILE, OUTPUT_FILE};
    private static final String STRING1 = "Hello World";
    private static final String STRING2 = "Bye World";

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

    private void createFile(String fileName, String content) {
        Path currentPath = Paths.get(Environment.currentDirectory);
        Path filePath = currentPath.resolve(fileName);
        try {
            Files.write(filePath, content.getBytes());
        } catch (IOException e) {
            fail("Failed to create file: " + e.getMessage());
        }
    }

    // grep pipe
    // wc, sort, cat, cut, tee, grep

    @DisabledOnOs(OS.WINDOWS) // Windows and MAC gets different results - use MAC/LINUX for actual test
    @Test
    public void grep_PipeWc_PrintsNumberOfLines() {
        try {
            String input = STRING1 + System.lineSeparator() +
                    STRING2 + System.lineSeparator() +
                    STRING1 + System.lineSeparator();
            createFile(INPUT_FILE, input);

            shell.parseAndEvaluate("grep Hello input.txt | wc", outputStream);
            String expectedOutput = "\t2\t4\t24" + System.lineSeparator();
            assertEquals(expectedOutput, outputStream.toString());
        } catch (IOException | AbstractApplicationException | ShellException e) {
            fail();
        }
    }

    @Test
    public void grep_PipeSort_PrintsSortedLines() {
        try {
            String input = STRING1 + System.lineSeparator() +
                    STRING2 + System.lineSeparator() +
                    STRING1 + System.lineSeparator();
            createFile(INPUT_FILE, input);

            shell.parseAndEvaluate("grep World input.txt | sort", outputStream);

            assertEquals(STRING2 + System.lineSeparator() +
                    STRING1 + System.lineSeparator() +
                    STRING1 + System.lineSeparator(), outputStream.toString());
        } catch (IOException | AbstractApplicationException | ShellException e) {
            fail();
        }
    }

    @Test
    public void grep_PipeCat_PrintsLines() {
        try {
            String input = STRING1 + System.lineSeparator() +
                    STRING2 + System.lineSeparator() +
                    STRING1 + System.lineSeparator();
            createFile(INPUT_FILE, input);

            shell.parseAndEvaluate("grep Hello input.txt | cat", outputStream);

            assertEquals(STRING1 + System.lineSeparator() +
                    STRING1 + System.lineSeparator(), outputStream.toString());
        } catch (IOException | AbstractApplicationException | ShellException e) {
            fail();
        }
    }

    @Test
    public void grep_PipeCut_PrintsCutLines() {
        try {
            String input = STRING1 + System.lineSeparator() +
                    STRING2 + System.lineSeparator() +
                    STRING1 + System.lineSeparator();
            createFile(INPUT_FILE, input);

            shell.parseAndEvaluate("grep Hello input.txt | cut -c 1-4", outputStream);

            assertEquals("Hell" + System.lineSeparator() +
                    "Hell" + System.lineSeparator(), outputStream.toString());
        } catch (IOException | AbstractApplicationException | ShellException e) {
            fail();
        }
    }

    @Test
    public void grep_PipeTee_PrintsLinesToFile() {
        try {
            String input = STRING1 + System.lineSeparator() +
                    STRING2 + System.lineSeparator() +
                    STRING1 + System.lineSeparator();
            createFile(INPUT_FILE, input);

            shell.parseAndEvaluate("grep Hello input.txt | tee output.txt", outputStream);

            String expectedOutput = STRING1 + StringUtils.STRING_NEWLINE +
                    STRING1;

            assertEquals(expectedOutput + StringUtils.STRING_NEWLINE, outputStream.toString());
            assertEquals(expectedOutput, new String(Files.readAllBytes(Paths.get(OUTPUT_FILE))));
        } catch (IOException | AbstractApplicationException | ShellException e) {
            fail();
        }
    }

    @Test
    public void grep_PipeGrep_PrintsLines() {
        try {
            String input = STRING1 + System.lineSeparator() +
                    STRING2 + System.lineSeparator() +
                    "Hi World" + System.lineSeparator();
            createFile(INPUT_FILE, input);

            shell.parseAndEvaluate("grep H input.txt | grep i", outputStream);

            assertEquals("Hi World" + System.lineSeparator(), outputStream.toString());
        } catch (IOException | AbstractApplicationException | ShellException e) {
            fail();
        }
    }


}
