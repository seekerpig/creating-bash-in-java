package impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.app.SortApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SortBugsTest {
    private static final String LINE_SEPARATOR = StringUtils.STRING_NEWLINE;
    String directoryPath = "./sortTestFiles";

    private SortApplication sortApp;
    private InputStream stdin;
    private OutputStream stdout;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        sortApp = new SortApplication();
        stdout = new ByteArrayOutputStream();
        
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (stdin != null) {
            stdin.close();
        }
        stdout.close();
    }

    @Test
    public void sortFromFiles_OneFileIsFirstWordNumber_ReturnsSortedOutput()
            throws Exception {
        File fileFromDirectory = Paths.get(directoryPath, "input1.txt").toFile();
        File expectedOutputFile = Paths.get(directoryPath, "expected1.txt").toFile();

        String result = sortApp.sortFromFiles(true, false, false, fileFromDirectory.getAbsolutePath());

        BufferedReader reader = new BufferedReader(new FileReader(expectedOutputFile));
        StringBuilder expectedOutputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            expectedOutputBuilder.append(line);
            expectedOutputBuilder.append(System.lineSeparator());
        }
        reader.close();
        
        if (expectedOutputBuilder.length() > 0) {
            expectedOutputBuilder.setLength(expectedOutputBuilder.length() - System.lineSeparator().length());
        }
        
        String expectedOutput = expectedOutputBuilder.toString();
        
        assertEquals(expectedOutput, result);
    }

    @Test
    public void sortFromFiles_OneFileAllFlagsTrue_ReturnsSortedOutput()
            throws Exception {
        File fileFromDirectory = Paths.get(directoryPath, "input2.txt").toFile();
        File expectedOutputFile = Paths.get(directoryPath, "expected2.txt").toFile();

        String result = sortApp.sortFromFiles(true, true, true, fileFromDirectory.getAbsolutePath());

        BufferedReader reader = new BufferedReader(new FileReader(expectedOutputFile));
        StringBuilder expectedOutputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            expectedOutputBuilder.append(line);
            expectedOutputBuilder.append(System.lineSeparator());
        }
        reader.close();

        if (expectedOutputBuilder.length() > 0) {
            expectedOutputBuilder.setLength(expectedOutputBuilder.length() - System.lineSeparator().length());
        }

        String expectedOutput = expectedOutputBuilder.toString();

        assertEquals(expectedOutput, result);
    }
}
