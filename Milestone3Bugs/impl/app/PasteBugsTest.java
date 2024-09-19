package impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PasteBugsTest {
    @TempDir
    Path tempDir;
    private ByteArrayOutputStream outputStream;
    private ShellImpl shell;

    Path fileA;
    Path fileB;

    @BeforeEach
    public void setUp() throws IOException {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
        // Setup A.txt
        fileA = tempDir.resolve("A.txt");
        Files.write(fileA, Arrays.asList("A", "B", "C", "D"));

        // Setup B.txt
        fileB = tempDir.resolve("B.txt");
        Files.write(fileB, Arrays.asList("1", "2", "3", "4"));
    }

    @Test
    public void testPasteCommandWithFiles() throws Exception {
        String command = String.format("paste - %s - < %s", fileA.toString(), fileB.toString());
        String expectedOutput = String.format("1\tA\t2%s3\tB\t4%s\tC\t%s\tD%s",
                System.lineSeparator(), System.lineSeparator(), System.lineSeparator(), System.lineSeparator());


        shell.parseAndEvaluate(command, outputStream);

        assertEquals(expectedOutput, outputStream.toString());
    }
}
