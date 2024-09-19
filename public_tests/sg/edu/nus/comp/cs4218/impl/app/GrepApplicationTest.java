package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GrepApplicationTest {

    private static final String TEST_FILE_NAME_1 = "testFile1";
    private static final String TEST_FILE_NAME_2 = "testFile2";
    private static final String MATCH_STRING = "pattern";

    private static final String MATCH_STRING_UPPER = "PATTERN"; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String LINE_WITH_MATCH_UPPER = "line2 " + MATCH_STRING_UPPER + StringUtils.STRING_NEWLINE; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String LINE_WITH_MATCH = "line3 " + MATCH_STRING + StringUtils.STRING_NEWLINE;
    private static final String[] FILE_FOLDER_NAMES = {TEST_FILE_NAME_1, TEST_FILE_NAME_2, "test-dir"};
    private static final String GREP_FILE_FAIL_MSG = "Failed to grep from files: "; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String GREP_STDIN_FAIL_MSG = "Failed to grep from stdin: "; //NOPMD - suppressed LongVariable - Variable Name is More Readable

    private String createTestFile(String fileName, Boolean withMatchString, Boolean withUpperMatchString) { //NOPMD - suppressed LongVariable - Variable Name is More Readable
        // TODO: create file in Environment.currentDirectory

        // get correct path
        Path currPath = Paths.get(Environment.currentDirectory);
        Path filePath = currPath.resolve(fileName);

        String content = "line1" + StringUtils.STRING_NEWLINE;
        if (withMatchString) {
            content += LINE_WITH_MATCH;
        }
        if (withUpperMatchString) {
            content += LINE_WITH_MATCH_UPPER;
        }
        try {
            Files.writeString(filePath, content);
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        return fileName;
    }

    private void createTestDirectory(String dirName) {
        Path currPath = Paths.get(Environment.currentDirectory);
        Path dirPath = currPath.resolve(dirName);
        try {
            Files.createDirectory(dirPath);
        } catch (IOException e) {
            fail("Failed to create directory: " + e.getMessage());
        }
    }

    // create input stream with boolean
    private InputStream createInputStream(Boolean withMatchString, Boolean withUpperMatchString) { //NOPMD - suppressed LongVariable - Variable Name is More Readable
        String inputString = "line1" + StringUtils.STRING_NEWLINE;
        if (withMatchString) {
            inputString += LINE_WITH_MATCH;
        }
        if (withUpperMatchString) {
            inputString += LINE_WITH_MATCH_UPPER;
        }
        return new ByteArrayInputStream(inputString.getBytes());
    }

    @AfterEach
    void tearDown() {
        for (String fileFolderName : FILE_FOLDER_NAMES) {
            Path currPath = Paths.get(Environment.currentDirectory);
            Path filePath = currPath.resolve(fileFolderName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                fail("Failed to delete test file in tearDown() function: " + e.getMessage());
            }
        }
    }

    /**
     * Test case to verify that the method {@link GrepApplication #grepFromFiles(String, boolean, boolean, boolean, String)}
     * throws a {@link GrepException} when provided with a null file name.
     */
    @Test
    public void testGrepFromFiles_NullFileName_ShouldThrowException() {
        GrepApplication grepApplication = new GrepApplication();
        assertThrows(GrepException.class, () -> grepApplication.grepFromFiles(MATCH_STRING, false, false, false, null));
    }

    /**
     * Test case to verify that the method {@link GrepApplication #grepFromFiles(String, boolean, boolean, boolean, String)}
     * throws a {@link GrepException} when provided with a null pattern.
     */
    @Test
    public void testGrepFromFiles_NullPattern_ShouldThrowException() {
        GrepApplication grepApplication = new GrepApplication();
        createTestFile(TEST_FILE_NAME_1, true, true);
        assertThrows(GrepException.class, () -> grepApplication.grepFromFiles(null, false, false, false, TEST_FILE_NAME_1));
    }

    /**
     * Test case to verify that the method {@link GrepApplication #grepFromFiles(String, boolean, boolean, boolean, String)}
     * returns a single matching line when searching with a given pattern in a file.
     */
    @Test
    public void testGrepFromFiles_MatchingLines_ShouldReturnOneMatchingLine() {
        String fileName = createTestFile(TEST_FILE_NAME_1, true, true);

        GrepApplication grepApplication = new GrepApplication();
        try {
            String result = grepApplication.grepFromFiles(MATCH_STRING, false, false, false, fileName);
            assertEquals(LINE_WITH_MATCH, result);
        } catch (GrepException e) {
            fail("GrepException: " + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail("AbstractApplicationException: " + e.getMessage());
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    /**
     * Test case to verify that the method {@link GrepApplication #grepFromFiles(String, boolean, boolean, boolean, String)}
     * returns an error message when the specified file is not found.
     */
    @Test
    public void testGrepFromFiles_FileNotFound_ShouldReturnMessage() {
        GrepApplication grepApplication = new GrepApplication();
        try {
            assertEquals(TEST_FILE_NAME_1 + ": No such file or directory" + StringUtils.STRING_NEWLINE, grepApplication.grepFromFiles(MATCH_STRING, false, false, false, TEST_FILE_NAME_1));
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        }
    }

    /**
     * Test case to verify that the method {@link GrepApplication #grepFromFiles(String, boolean, boolean, boolean, String)}
     * returns an error message when the specified file is actually a directory.
     */

    @Test
    public void testGrepFromFiles_FileIsDirectory_ShouldReturnMessage() {
        GrepApplication grepApplication = new GrepApplication();
        createTestDirectory("test-dir");
        try {
            assertEquals("test-dir: Is a directory" + StringUtils.STRING_NEWLINE, grepApplication.grepFromFiles(MATCH_STRING, false, true, false, "test-dir"));
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        }
    }

    @Test
    public void testGrepFromFiles_MatchingLinesInsensitive_ShouldReturnTwoMatchingLines() {
        GrepApplication grepApplication = new GrepApplication();
        String fileName = createTestFile(TEST_FILE_NAME_1, true, true);
        try {
            String result = grepApplication.grepFromFiles(MATCH_STRING, true, false, false, fileName);
            assertEquals(LINE_WITH_MATCH + LINE_WITH_MATCH_UPPER, result);
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        }
    }

    @Test
    public void testGrepFromFiles_MatchingLinesCount_ShouldReturnCount() {
        String fileName = createTestFile(TEST_FILE_NAME_1, true, true);

        GrepApplication grepApplication = new GrepApplication();
        try {
            String result = grepApplication.grepFromFiles(MATCH_STRING, false, true, false, fileName);
            assertEquals("1" + StringUtils.STRING_NEWLINE, result);
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        }
    }

    @Test
    public void testGrepFromFiles_MatchingLinesPrefix_ShouldReturnMatchingLineWithPrefix() {
        String fileName = createTestFile(TEST_FILE_NAME_1, true, true);

        GrepApplication grepApplication = new GrepApplication();
        try {
            String result = grepApplication.grepFromFiles(MATCH_STRING, false, false, true, fileName);
            assertEquals(fileName + ":" + LINE_WITH_MATCH, result);
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        }
    }

    @Test
    public void testGrepFromFiles_NoMatchingLines_ShouldReturnEmptyString() {
        String fileName = createTestFile(TEST_FILE_NAME_1, false, true);

        GrepApplication grepApplication = new GrepApplication();
        try {
            String result = grepApplication.grepFromFiles(MATCH_STRING, false, false, false, fileName);
            assertEquals("", result);
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        }
    }

    // testGrepFromFile multiple files  get count
    @Test
    public void testGrepFromFiles_MultipleFiles_ShouldReturnCounts() {
        String fileName1 = createTestFile(TEST_FILE_NAME_1, true, true);
        String fileName2 = createTestFile(TEST_FILE_NAME_2, true, true);

        GrepApplication grepApplication = new GrepApplication();
        try {
            String result = grepApplication.grepFromFiles(MATCH_STRING, false, true, false, fileName1, fileName2);
            assertEquals(TEST_FILE_NAME_1 + ":1" + StringUtils.STRING_NEWLINE + TEST_FILE_NAME_2 + ":1" + StringUtils.STRING_NEWLINE, result);
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        }
    }

    // test grepFromStdin
    @Test
    public void testGrepFromStdin_NullPattern_ShouldThrowException() {
        GrepApplication grepApplication = new GrepApplication();
        assertThrows(GrepException.class, () -> grepApplication.grepFromStdin(null, false, false, false, System.in));
    }

    @Test
    public void testGrepFromStdin_MatchingLines_ShouldReturnMatchingLine() throws IOException {
        GrepApplication grepApplication = new GrepApplication();
        InputStream inputStream = createInputStream(true, true);
        try {
            String result = grepApplication.grepFromStdin(MATCH_STRING, false, false, false, inputStream);
            assertEquals(LINE_WITH_MATCH, result);
        } catch (GrepException e) {
            fail("GrepException: " + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail("AbstractApplicationException: " + e.getMessage());
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        } finally {
            inputStream.close();
        }
    }

    // case insensitive
    @Test
    public void testGrepFromStdin_MatchingLinesInsensitive_ShouldReturnMatchingLine() throws IOException {
        GrepApplication grepApplication = new GrepApplication();
        InputStream inputStream = createInputStream(true, true);
        try {
            String result = grepApplication.grepFromStdin(MATCH_STRING, true, false, false, inputStream);
            assertEquals(LINE_WITH_MATCH + LINE_WITH_MATCH_UPPER, result);
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } finally {
            inputStream.close();
        }
    }

    // test grepFromStdin single file  get count
    @Test
    public void testGrepFromStdin_MatchingLinesCount_ShouldReturnCount() throws IOException {
        GrepApplication grepApplication = new GrepApplication();
        InputStream inputStream = createInputStream(true, true);
        try {
            String result = grepApplication.grepFromStdin(MATCH_STRING, false, true, false, inputStream);
            assertEquals("1" + StringUtils.STRING_NEWLINE, result);
        } catch (GrepException e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } finally {
            inputStream.close();
        }
    }

    // test grepFromStdin single file  get prefix
    @Test
    public void testGrepFromStdin_MatchingLinesPrefix_ShouldReturnMatchingLineWithPrefix() throws IOException {
        GrepApplication grepApplication = new GrepApplication();
        InputStream inputStream = createInputStream(true, true);
        try {
            String result = grepApplication.grepFromStdin(MATCH_STRING, false, false, true, inputStream);
            assertEquals("(standard input):" + LINE_WITH_MATCH, result);
        } catch (GrepException e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } finally {
            inputStream.close();
        }
    }

    // test grepFromStdin single file  no match
    @Test
    public void testGrepFromStdin_NoMatchingLines_ShouldReturnEmptyString() throws IOException {
        GrepApplication grepApplication = new GrepApplication();
        InputStream inputStream = createInputStream(false, false);
        try {
            String result = grepApplication.grepFromStdin(MATCH_STRING, false, false, false, inputStream);
            assertEquals("", result);
        } catch (GrepException e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
        } finally {
            inputStream.close();
        }
    }

    // test grepFromStdin multiple files  get count
//    @Test
//    public void testGrepFromStdin_MultipleFiles_ShouldReturnCounts() {
//        GrepApplication grepApplication = new GrepApplication();
//        InputStream inputStream1 = createInputStream(true, true);
//        InputStream inputStream2 = createInputStream(true, true);
//        try {
//            String result = grepApplication.grepFromStdin(MATCH_STRING, false, true, false, inputStream1, inputStream2);
//            assertEquals("(standard input): 1" + StringUtils.STRING_NEWLINE + "(standard input): 1" + StringUtils.STRING_NEWLINE, result);
//        } catch (GrepException e) {
//            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
//        } catch (AbstractApplicationException e) {
//            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
//        } catch (Exception e) {
//            fail(GREP_STDIN_FAIL_MSG + e.getMessage());
//        }
//    }


    @Test
    public void testRun_validInputFiles_shouldCorrectOutput() throws IOException {
        // Prepare test data
        String[] args = {"-i", "-H", MATCH_STRING, TEST_FILE_NAME_1};
        String expectedOutput = TEST_FILE_NAME_1 + ":" + LINE_WITH_MATCH + TEST_FILE_NAME_1 + ":" + LINE_WITH_MATCH_UPPER;

        // Create test files
        createTestFile(TEST_FILE_NAME_1, true, true);

        // Call the method
        GrepApplication instance = new GrepApplication();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            instance.run(args, null, outputStream);
        } catch (AbstractApplicationException e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            outputStream.close();
        }

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS) // Bugged on Windows
    public void testGrepFromFileAndStdin_validInputFiles_shouldReturnNull() throws IOException {
        GrepApplication grepApplication = new GrepApplication();
        String file1 = createTestFile(TEST_FILE_NAME_1, true, true);
        String[] files = {file1, "-"};
        InputStream inputStream = createInputStream(true, true);
        String expectedOutput = TEST_FILE_NAME_1 + ":" + LINE_WITH_MATCH +
                "(standard input):" + LINE_WITH_MATCH + StringUtils.STRING_NEWLINE;
        // remove last newline
        expectedOutput = expectedOutput.substring(0, expectedOutput.length() - 1);
        try {
            String result = grepApplication.grepFromFileAndStdin(MATCH_STRING, false, false, false, inputStream, files);
            assertEquals(expectedOutput, result);
        } catch (GrepException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } catch (Exception e) {
            fail(GREP_FILE_FAIL_MSG + e.getMessage());
        } finally {
            inputStream.close();
        }
    }
}