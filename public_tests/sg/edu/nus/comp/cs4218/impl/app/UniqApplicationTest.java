package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

/**
 * This class contains unit tests for the UniqApplication class.
 * It tests various scenarios of the UniqApplication's functionality.
 */
public class UniqApplicationTest {
    private UniqApplication uniqApp;

    private File tempFile1;
    private File tempFile2;
    private File tempFile3;

    String inputContent = "Hello World" + StringUtils.STRING_NEWLINE + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "Hello World" + StringUtils.STRING_NEWLINE +
            "Alice" + StringUtils.STRING_NEWLINE + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "Alice" + StringUtils.STRING_NEWLINE +
            "Bob" + StringUtils.STRING_NEWLINE + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "Alice" + StringUtils.STRING_NEWLINE +
            "Bob";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        uniqApp = new UniqApplication();

        tempFile1 = File.createTempFile("testTee1", ".txt");
        Files.write(tempFile1.toPath(), List.of("Hello World", "Hello World", "Alice", "Alice", "Bob", "Alice", "Bob"),
                StandardCharsets.UTF_8);

        tempFile2 = File.createTempFile("testTee2", ".txt");

        tempFile3 = File.createTempFile("testTee3", ".txt");
        Files.write(tempFile3.toPath(),
                List.of("Line 5", "Line 5", "Line 5", "Line 6", "Line 6", "Line 6", "Line 6", "Line 7", "Line 7"), //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
                StandardCharsets.UTF_8);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile1.toPath());
        Files.deleteIfExists(tempFile2.toPath());
    }

    /**
     * Test case to verify the behavior of the run method when given the prefix
     * argument and input/output files.
     * It checks if the run method inserts lines with the specified prefix into the
     * output file.
     *
     * @throws AbstractApplicationException If an error occurs during application
     *                                      execution.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void run_WithPrefixArgAndIOFiles_InsertsLinesWithPrefix() throws AbstractApplicationException, IOException {
        String[] args = { "-c", tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        uniqApp.run(args, stdin, stdout);

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Alice", "1 Bob", "1 Alice", "1 Bob"); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords) //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case to verify the behavior of the run method when the "-c" prefix
     * argument is provided and an input file is used.
     * The method should insert lines with the specified prefix to the standard
     * output.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     */
    @Test
    public void run_WithPrefixArgAndInputFile_InsertsLinesWithPrefixToStdout() throws AbstractApplicationException {
        String[] args = { "-c", tempFile1.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        uniqApp.run(args, stdin, stdout);

        String stdOutput = stdout.toString();

        String expectedOutput = "2 Hello World" + StringUtils.STRING_NEWLINE +
                "2 Alice" + StringUtils.STRING_NEWLINE +
                "1 Bob" + StringUtils.STRING_NEWLINE +
                "1 Alice" + StringUtils.STRING_NEWLINE +
                "1 Bob" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdOutput);
    }

    /**
     * Test case for the run_WithPrefixArgAndNo_ReadsStdinAndWritesToStdout method.
     * This test verifies that the UniqApplication's run method correctly reads from
     * stdin and writes to stdout
     * when the "-c" prefix argument is provided.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void run_WithPrefixArgAndNo_ReadsStdinAndWritesToStdout() throws AbstractApplicationException, IOException {
        String[] args = { "-c" };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        uniqApp.run(args, stdin, stdout);

        String stdOutput = stdout.toString();

        String expectedOutput = "2 Hello World" + StringUtils.STRING_NEWLINE +
                "2 Alice" + StringUtils.STRING_NEWLINE +
                "1 Bob" + StringUtils.STRING_NEWLINE +
                "1 Alice" + StringUtils.STRING_NEWLINE +
                "1 Bob" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdOutput);
    }

    /**
     * Test case to verify that the `run` method of the `UniqApplication` class
     * correctly inserts duplicate lines into a file when the `-d` flag is provided.
     *
     * @throws AbstractApplicationException If an error occurs while running the
     *                                      application.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void run_WithOnlyDuplicateAndIOFiles_InsertsDuplicatesToFile()
            throws AbstractApplicationException, IOException {
        String[] args = { "-d", tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList("Hello World", "Alice");

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case to verify that the run method of UniqApplication inserts duplicates
     * to stdout
     * when the "-d" flag is provided and an input file is specified.
     *
     * @throws AbstractApplicationException if an error occurs while running the
     *                                      application
     */
    @Test
    public void run_WithOnlyDuplicateAndInputFile_InsertsDuplicatesToStdout() throws AbstractApplicationException {
        String[] args = { "-d", tempFile1.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        String stdOutput = stdout.toString();

        String expectedOutput = "Hello World" + StringUtils.STRING_NEWLINE +
                "Alice" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdOutput);
    }

    /**
     * Test case for the scenario where the `run` method is called with the "-d"
     * flag and no input files.
     * It verifies that the `uniqApp` reads from standard input and writes to
     * standard output correctly.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     */
    @Test
    public void run_WithOnlyDuplicateAndNoFiles_ReadsStdinAndWritesToStdout() throws AbstractApplicationException {
        String[] args = { "-d" };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        String stdOutput = stdout.toString();

        String expectedOutput = "Hello World" + StringUtils.STRING_NEWLINE +
                "Alice" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdOutput);
    }

    /**
     * Test case to verify the behavior of the `run` method in the `UniqApplication`
     * class
     * when all input lines are duplicates and the output is written to a file.
     *
     * It verifies that the `run` method correctly inserts all duplicate lines into
     * the specified file.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void run_WithAllDuplicateAndIOFiles_InsertsAllDuplicatesToFile()
            throws AbstractApplicationException, IOException {
        String[] args = { "-D", tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList("Hello World", "Hello World", "Alice", "Alice");

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case to verify that the run method of UniqApplication inserts all
     * duplicate lines from the input file to the standard output.
     * It sets up the necessary arguments, input stream, and output stream for the
     * test.
     * The expected output is compared with the actual output to ensure correctness.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the UniqApplication
     */
    @Test
    public void run_WithAllDuplicateAndInputFile_InsertsAllDuplicatesToStdout() throws AbstractApplicationException {
        String[] args = { "-D", tempFile1.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        String stdOutput = stdout.toString();

        String expectedOutput = "Hello World" + StringUtils.STRING_NEWLINE +
                "Hello World" + StringUtils.STRING_NEWLINE +
                "Alice" + StringUtils.STRING_NEWLINE +
                "Alice" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdOutput);
    }

    /**
     * Test case for the run_WithAllDuplicateAndNoFiles_ReadsStdinAndWritesToStdout
     * method.
     * It tests the behavior of the UniqApplication when all input lines are
     * duplicates and no files are provided.
     * The method reads from stdin and writes the unique lines to stdout.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the UniqApplication
     */
    @Test
    public void run_WithAllDuplicateAndNoFiles_ReadsStdinAndWritesToStdout() throws AbstractApplicationException {
        String[] args = { "-D" };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        String stdOutput = stdout.toString();

        String expectedOutput = "Hello World" + StringUtils.STRING_NEWLINE +
                "Hello World" + StringUtils.STRING_NEWLINE +
                "Alice" + StringUtils.STRING_NEWLINE +
                "Alice" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdOutput);
    }

    /**
     * Test case for the run method in UniqApplication class when there are no
     * arguments and input/output files are used.
     * The test verifies that adjacent duplicate lines are removed from the output
     * file.
     *
     * @throws AbstractApplicationException If an error occurs during application
     *                                      execution.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void run_WithNoArgsAndIOFiles_AdjLinesRemoved() throws AbstractApplicationException, IOException {
        String[] args = { tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList("Hello World", "Alice", "Bob", "Alice", "Bob");

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case to verify the behavior of the `run` method in the `UniqApplication`
     * class
     * when there are no arguments and multiple repeated adjacent lines are removed.
     *
     * @throws AbstractApplicationException If an error occurs while running the
     *                                      application.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void run_WithNoArgsMultipleRepeats_AdjLinesRemoved() throws AbstractApplicationException, IOException {
        String[] args = { tempFile3.getAbsolutePath(), tempFile2.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList("Line 5", "Line 6", "Line 7");

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case for the run_WithPrefixMultipleRepeats_InsertLinesWithPrefix method.
     * It tests the functionality of inserting lines with a prefix when there are
     * multiple repeats.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void run_WithPrefixMultipleRepeats_InsertLinesWithPrefix() throws AbstractApplicationException, IOException {
        String[] args = { "-c", tempFile3.getAbsolutePath(), tempFile2.getAbsolutePath() };

        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();

        uniqApp.run(args, stdin, stdout);

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList("3 Line 5", "4 Line 6", "2 Line 7");

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case for the uniqFromFile method with count=true, repeated=true,
     * allRepeated=true.
     * This test verifies that duplicate lines are merged to their first occurrence
     * in the output file.
     *
     * @throws AbstractApplicationException If an error occurs during application
     *                                      execution.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void uniqFromFile_CountTrueRepeatedTrueAllRepeatedTrue_DuplicateLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(true, false, true, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Hello World", "2 Alice", "2 Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * Test case for the uniqFromFile method with the following parameters:
     * - count: true
     * - repeated: true
     * - allRepeated: false
     * - inputFile1: path of the first input file
     * - inputFile2: path of the second input file
     *
     * This test verifies that when the uniqFromFile method is called with the
     * specified parameters,
     * duplicate lines are merged to their first occurrence, and the resulting
     * content matches the expected content.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void uniqFromFile_CountTrueRepeatedTrueAllRepeatedFalse_DuplicateLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(true, true, false, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * Test case for the uniqFromFile method with the following parameters:
     * - count: true
     * - repeated: false
     * - allRepeated: true
     * - files: tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath()
     *
     * This test verifies that the uniqFromFile method correctly reads the content
     * from the specified files,
     * removes repeated lines, and prefixes each line with its count. The expected
     * content is compared with
     * the actual content read from the file.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the uniq application
     * @throws IOException                  if an I/O error occurs while reading the
     *                                      file
     */
    @Test
    public void uniqFromFile_CountTrueRepeatedFalseAllRepeatedTrue_AllLinesWithCountPrefix()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(true, false, true, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Hello World", "2 Alice", "2 Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * Test case for the uniqFromFile method with count=true, repeated=false,
     * allRepeated=false.
     * This test verifies that when count is set to true, repeated is set to false,
     * and allRepeated is set to false,
     * all lines in the input files are merged to their first occurrence in the
     * output file.
     *
     * @throws AbstractApplicationException If an error occurs during the execution
     *                                      of the application.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void uniqFromFile_CountTrueRepeatedFalseAllRepeatedFalse_AllLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(true, false, false, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Alice", "1 Bob", "1 Alice", "1 Bob", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * Test case for the uniqFromFile method with the following parameters:
     * - count: false
     * - repeated: true
     * - allRepeated: true
     * - file paths: tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath()
     *
     * This test verifies that the uniqFromFile method correctly reads the contents
     * of the specified files,
     * removes duplicate lines, and prints each unique line once with a count prefix
     * if it is repeated.
     * The expected content after applying the uniqFromFile method is ["Hello
     * World", "Hello World", "Alice", "Alice"].
     * The assertLinesMatch method is used to compare the expected content with the
     * actual content read from the file.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the uniqFromFile method
     * @throws IOException                  if an I/O error occurs while reading the
     *                                      file
     */
    @Test
    public void uniqFromFile_CountFalseRepeatedTrueAllRepeatedTrue_DuplicateLinesPrintedOnceWithCountPrefix()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(false, true, true, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Hello World", "Alice", "Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * This test case verifies that when the uniqFromFile method is called with the
     * given parameters,
     * duplicate lines are printed once in the output file.
     *
     * @throws AbstractApplicationException If an exception occurs during
     *                                      application execution
     * @throws IOException                  If an I/O error occurs
     */
    @Test
    public void uniqFromFile_CountFalseRepeatedTrueAllRepeatedFalse_DuplicateLinesPrintedOnce()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(false, true, false, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * Test case for the uniqFromFile method with count set to false, repeated set
     * to false,
     * allRepeated set to true, and lines with count prefix.
     *
     * @throws AbstractApplicationException If an error occurs during application
     *                                      execution.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void uniqFromFile_CountFalseRepeatedFalseAllRepeatedTrue_AllLinesWithCountPrefix()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(false, false, true, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Hello World", "Alice", "Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * Test case for the uniqFromFile method with count=false, repeated=false,
     * allRepeated=false.
     * This test verifies that all lines are merged to their first occurrence in the
     * output file.
     *
     * @throws AbstractApplicationException If an error occurs during application
     *                                      execution.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void uniqFromFile_CountFalseRepeatedFalseAllRepeatedFalse_AllLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        String result = uniqApp.uniqFromFile(false, false, false, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Alice", "Bob", "Alice", "Bob", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    };

    /**
     * Test case for the uniqFromStdin method with count=true, repeated=true,
     * allRepeated=true.
     * This test verifies that duplicate lines are merged to their first occurrence.
     *
     * @throws AbstractApplicationException If an error occurs during application
     *                                      execution.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void uniqFromStdin_CountTrueRepeatedTrueAllRepeatedTrue_DuplicateLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(true, true, true, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Hello World", "2 Alice", "2 Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }

    /**
     * The test verifies that duplicate lines are merged to their first occurrence.
     * The method reads input from the inputStream, applies the uniq operation with
     * the specified parameters,
     * and writes the result to the outputFile. Then, it reads the content of the
     * outputFile and compares it
     * with the expectedContent. The test passes if the lines match.
     *
     * @throws AbstractApplicationException if an error occurs during the
     *                                      application execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void uniqFromStdin_CountTrueRepeatedTrueAllRepeatedFalse_DuplicateLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(true, true, false, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }

    /**
     * This test case verifies that the uniqFromStdin method correctly processes the
     * input from stdin
     * and writes the output to the specified file. It checks that all lines are
     * included in the output
     * with a count prefix, and that repeated lines are not included.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void uniqFromStdin_CountTrueRepeatedFalseAllRepeatedTrue_AllLinesWithCountPrefix()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(true, false, true, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Hello World", "2 Alice", "2 Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }

    /**
     * The test verifies that when the uniqFromStdin method is called with the given
     * parameters,
     * all lines from the input stream are merged to their first occurrence, and the
     * merged lines
     * are written to the output file.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the application
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void uniqFromStdin_CountTrueRepeatedFalseAllRepeatedFalse_AllLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(true, false, false, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("2 Hello World", "2 Alice", "1 Bob", "1 Alice", "1 Bob", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }

    /**
     * This test case verifies that when the uniqFromStdin method is called with the
     * specified parameters,
     * duplicate lines are printed once with a count prefix. The method reads the
     * input from the inputStream,
     * performs the uniq operation, and writes the result to the outputFile. The
     * file content is then read
     * and compared with the expectedContent to ensure that the uniq operation is
     * performed correctly.
     *
     * Expected behavior:
     * - Duplicate lines are printed once with a count prefix.
     * - The file content matches the expectedContent.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the uniq application
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void uniqFromStdin_CountFalseRepeatedTrueAllRepeatedTrue_DuplicateLinesPrintedOnceWithCountPrefix()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(false, true, true, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Hello World", "Alice", "Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }

    /**
     * This test verifies that when uniqFromStdin is called with the given
     * parameters,
     * the method correctly reads from the input stream, removes duplicate lines,
     * and writes
     * the unique lines to the output file.
     *
     * Expected behavior:
     * - Duplicate lines are removed from the inputContent.
     * - The unique lines are written to the output file.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void uniqFromStdin_CountFalseRepeatedTrueAllRepeatedFalse_DuplicateLinesPrintedOnce()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(false, true, false, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }

    /**
     * Test case for the uniqFromStdin method with count set to false, repeated set
     * to false,
     * allRepeated set to true, and all lines with count prefix.
     *
     * @throws AbstractApplicationException if an error occurs during application
     *                                      execution
     * @throws IOException                  if an I/O error occurs
     */
    @Test
    public void uniqFromStdin_CountFalseRepeatedFalseAllRepeatedTrue_AllLinesWithCountPrefix()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(false, false, true, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Hello World", "Alice", "Alice", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }

    /**
     * Test case for the `uniqFromStdin` method with `count` set to false,
     * `repeated` set to false,
     * and `allRepeated` set to false. This test verifies that all lines are merged
     * to their first occurrence
     * when reading from standard input and writing to a file.
     *
     * @throws AbstractApplicationException If an error occurs during application
     *                                      execution.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void uniqFromStdin_CountFalseRepeatedFalseAllRepeatedFalse_AllLinesMergedToFirstOccurrence()
            throws AbstractApplicationException, IOException {
        InputStream stream = new ByteArrayInputStream(inputContent.getBytes());
        String result = uniqApp.uniqFromStdin(false, false, false, stream, tempFile2.getAbsolutePath());

        List<String> expectedContent = Arrays.asList("Hello World", "Alice", "Bob", "Alice", "Bob", "");
        String joinedContent = String.join(StringUtils.STRING_NEWLINE, expectedContent);

        assertEquals(joinedContent, result);
    }
}