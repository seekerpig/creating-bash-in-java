package impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GrepBugsTest {

    Shell shell;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() throws Exception {
        // create a file named grep.txt at current directory
        Path currPath = Paths.get(Environment.currentDirectory);
        Path grepPath = currPath.resolve("grep.txt");
        Files.write(grepPath, Arrays.asList("line1", "line2", "line3"));

        shell = new ShellImpl();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // delete the file grep.txt
        Path currPath = Paths.get(Environment.currentDirectory);
        Path grepPath = currPath.resolve("grep.txt");
        Files.delete(grepPath);
    }

    @Test
    public void testGrepEmptyPattern() throws Exception {
        String command = "grep \"\" grep.txt";
        String expectedOutput = "line1" + System.lineSeparator() + "line2" + System.lineSeparator() + "line3" + System.lineSeparator();
        shell.parseAndEvaluate(command, outputStream);
        assertEquals(expectedOutput, outputStream.toString());
    }
}
