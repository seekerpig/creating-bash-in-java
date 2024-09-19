package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class CatApplicationTest {

    private static CatApplication catApplication;
    private File tempFile1;
    private File tempFile2;
    
    @BeforeEach
    public void setUp() throws IOException {
        catApplication = new CatApplication();

        tempFile1 = File.createTempFile("testCat1", ".txt");
        Files.write(tempFile1.toPath(), List.of("Line 1", "Line 2"), StandardCharsets.UTF_8); //NOPMD - suppressed AvoidDuplicateLiterals - For readability

        tempFile2 = File.createTempFile("testCat2", ".txt");
        Files.write(tempFile2.toPath(), List.of("Line 3", "Line 4"), StandardCharsets.UTF_8); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile1.toPath());
        Files.deleteIfExists(tempFile2.toPath());
    }

    /**
     * Test case to verify that a CatException is thrown when the standard output stream is null.
     */
    @Test
    public void run_NullStandardOutput_ThrowsException() {
        String input = "Hello World"; //NOPMD - suppressed AvoidDuplicateLiterals -  For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        CatException catException = assertThrows(
                CatException.class,
                () -> catApplication.run(new String[]{}, stdin, null)
        );

        assertEquals("cat: " + ErrorConstants.ERR_NO_OSTREAM, catException.getMessage());
    }

    /**
     * Test case to verify that a CatException is thrown when invalid arguments are provided to the run method.
     */
    @Test
    public void run_InvalidArgs_ThrowsException() {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        CatException catException = assertThrows(
                CatException.class,
                () -> catApplication.run(new String[]{"-b"}, null, stdout)
        );

        assertEquals("cat: Exception Caught: illegal option -- b", catException.getMessage());
    }

    /**
     * Test case to verify the behavior of the `run` method in the `CatApplication` class
     * when the arguments include the `-n` option and a single file path.
     * The expected behavior is that the content of the file is displayed with line numbers.
     */
    @Test
    public void run_ArgsIncludeLineNumberOneFile_ContentDisplayed() {
        String[] args = {"-n", tempFile1.getAbsolutePath()};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> catApplication.run(args, null, outputStream));

        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2" + StringUtils.STRING_NEWLINE; //NOPMD - suppressed AvoidDuplicateLiterals -  For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Test case to verify that the `run` method of the `CatApplication` class correctly displays the content of multiple files
     * with line numbers when the arguments include the `-n` option.
     * <p>
     * The test creates two temporary files and passes their absolute paths along with the `-n` option as arguments to the `run` method.
     * The expected output is a string containing the content of both files, with line numbers appended to each line.
     */
    @Test
    public void run_ArgsIncludeLineNumberMultipleFiles_ContentDisplayed() {
        String[] args = {"-n", tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath()};

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> catApplication.run(args, null, stdout));

        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2" + StringUtils.STRING_NEWLINE +
                "3 Line 3" + StringUtils.STRING_NEWLINE + "4 Line 4" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, stdout.toString());
    }

    /**
     * Test case to verify the behavior of the `run` method in the `CatApplication` class
     * when provided with arguments containing the path to a single file and no line numbers.
     * The expected behavior is that the content of the file should be displayed on the standard output.
     */
    @Test
    public void run_ArgsNoLineNumberOneFile_ContentDisplayed() {
        String[] args = {tempFile1.getAbsolutePath()};

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> catApplication.run(args, null, stdout));

        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, stdout.toString());
    }

    /**
     * Test case to verify the behavior of the `run` method in the `CatApplication` class
     * when provided with multiple file paths as arguments and the `-n` option is not specified.
     * The content of the files should be displayed without line numbers.
     */
    @Test
    public void run_ArgsNoLineNumberMultipleFiles_ContentDisplayed() {
        String[] args = {tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath()};

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> catApplication.run(args, null, stdout));

        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE +
                "Line 3" + StringUtils.STRING_NEWLINE + "Line 4" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, stdout.toString());
    }

    /**
     * Test case to verify the behavior of the `run` method when the arguments include the `-n` option and the input is provided through stdin.
     * The expected behavior is that the content should be displayed with line numbers.
     */
    @Test
    public void run_ArgsIncludeLineNumberNoFilesStdin_ContentDisplayed() {
        String[] args = {"-n", "-"};

        String input = "Hello World" + StringUtils.STRING_NEWLINE + "Cat Stdin Test" + StringUtils.STRING_NEWLINE + "End"; //NOPMD - suppressed AvoidDuplicateLiterals -  For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> catApplication.run(args, stdin, stdout));

        String expectedOutput = "1 Hello World" + StringUtils.STRING_NEWLINE + "2 Cat Stdin Test" + StringUtils.STRING_NEWLINE + "3 End" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdout.toString());
    }

    /**
     * Test case to verify the behavior of the `run` method when the arguments do not include line numbers,
     * there are no files to read from, and the input is provided through stdin. The expected behavior is that
     * the content provided through stdin is displayed on the stdout.
     */
    @Test
    public void run_ArgsNoLineNumberNoFilesStdin_ContentDisplayed() {
        String[] args = {"-"};

        String input = "Hello World" + StringUtils.STRING_NEWLINE + "Cat Stdin Test" + StringUtils.STRING_NEWLINE + "End";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> catApplication.run(args, stdin, stdout));

        String expectedOutput = "Hello World" + StringUtils.STRING_NEWLINE + "Cat Stdin Test" + StringUtils.STRING_NEWLINE + "End" + StringUtils.STRING_NEWLINE;

        assertEquals(expectedOutput, stdout.toString());
    }

    /**
     * Test case to verify that the `run` method throws a `CatException` when given an invalid file argument.
     */
    @Test
    public void run_ArgsInvalidFile_ThrowsCatException() {
        String[] args = {"test.txt"};

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        assertThrows(
                CatException.class,
                () -> catApplication.run(args, null, stdout)
        );
    }

    /**
     * Test case for the `catFiles` method when given a single file and line numbers are not requested.
     * It verifies that the method returns the contents of the file as expected.
     *
     * @throws AbstractApplicationException if an error occurs during the execution of the method
     */
    @Test
    void catFiles_SingleFileNoLineNumbers_ReturnsFileContents() throws AbstractApplicationException {
        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2";
        String result = catApplication.catFiles(false, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the `catFiles` method when given a single file with line numbers option.
     * It verifies that the method returns the expected output, which is the contents of the file with line numbers.
     *
     * @throws AbstractApplicationException if an error occurs during the execution of the method
     */
    @Test
    void catFiles_SingleFileWithLineNumbers_ReturnsFileContentsWithLineNumbers() throws AbstractApplicationException {
        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2";
        String result = catApplication.catFiles(true, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFiles method when multiple files are provided and line numbers are not requested.
     * It verifies that the method returns the concatenated contents of the files without line numbers.
     *
     * @throws AbstractApplicationException if an error occurs during the execution of the catFiles method
     */
    @Test
    void catFiles_MultipleFilesNoLineNumbers_ReturnsConcatenatedFileContents() throws AbstractApplicationException {
        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE +
                "Line 3" + StringUtils.STRING_NEWLINE + "Line 4";
        String result = catApplication.catFiles(false, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFiles method in the CatApplication class.
     * Tests the behavior of concatenating multiple files with line numbers.
     *
     * @throws AbstractApplicationException if an error occurs during application execution
     */
    @Test
    void catFiles_MultipleFilesWithLineNumbers_ReturnsConcatenatedFileContentsWithLineNumbers() throws AbstractApplicationException {
        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2" + StringUtils.STRING_NEWLINE +
                "3 Line 3" + StringUtils.STRING_NEWLINE + "4 Line 4";
        String result = catApplication.catFiles(true, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the `catFiles` method when duplicate files are provided and line numbers are not requested.
     * It verifies that the method returns the duplicated contents of the files.
     *
     * @throws AbstractApplicationException if an error occurs during the execution of the `catFiles` method
     */
    @Test
    void catFiles_DuplicateFilesNoLineNumbers_ReturnsDuplicatedFileContents() throws AbstractApplicationException {
        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE +
                "Line 1" + StringUtils.STRING_NEWLINE + "Line 2";
        String result = catApplication.catFiles(false, tempFile1.getAbsolutePath(), tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the `catFiles` method when given duplicate files with line numbers.
     * It verifies that the method returns the duplicated file contents with line numbers.
     *
     * @throws AbstractApplicationException if an error occurs during the execution of the application
     */
    @Test
    void catFiles_DuplicateFilesWithLineNumbers_ReturnsDuplicatedFileContentsWithLineNumbers() throws AbstractApplicationException {
        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2" + StringUtils.STRING_NEWLINE +
                "3 Line 1" + StringUtils.STRING_NEWLINE + "4 Line 2";
        String result = catApplication.catFiles(true, tempFile1.getAbsolutePath(), tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case to verify that an exception is thrown when attempting to concatenate
     * non-existent file(s) using the `catFiles` method.
     *
     * @throws AbstractApplicationException if an error occurs during the execution of the `catFiles` method.
     */
    @Test
    void catFiles_NonExistentFile_ThrowsException() {
        assertThrows(AbstractApplicationException.class, () -> catApplication.catFiles(false, "nonexistentfile.txt"));
    }

    /**
     * Test case for the `catStdin` method when the input stream is null.
     *
     * @throws CatException if an error occurs while executing the `catStdin` method.
     */
    @Test
    public void catStdin_nullInputStream_ThrowsCatException() {
        assertThrows(CatException.class, () -> catApplication.catStdin(false, null));
    }

    /**
     * Test case for the `catStdin` method when no line numbers are requested.
     * It verifies that the method returns the content of the standard input stream.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catStdin_NoLineNumber_ReturnsStdinContent() throws Exception {
        String input = "Hello World" + StringUtils.STRING_NEWLINE + "Cat Stdin Test";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = input;
        String result = catApplication.catStdin(false, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for catStdin_WithLineNumber_ReturnsStdinContentWithLineNumbers method.
     * It tests the functionality of the catStdin method in the CatApplication class when line numbers are enabled.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catStdin_WithLineNumber_ReturnsStdinContentWithLineNumbers() throws Exception {
        String input = "Hello World" + StringUtils.STRING_NEWLINE + "Cat Stdin Test";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = "1 Hello World" + StringUtils.STRING_NEWLINE + "2 Cat Stdin Test";
        String result = catApplication.catStdin(true, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFileAndStdin method when given a single file and no line numbers.
     * It verifies that the method returns the expected output, which is the content of the file
     * concatenated with the content from the standard input.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catFileAndStdin_SingleFileNoLineNumber_ReturnsFileAndStdinContent() throws Exception {
        String input = "Stdin Content"; //NOPMD - suppressed AvoidDuplicateLiterals -  For readability (it is not scalable to have global variables for all string literals given the magnitude of tests - it only makes sense in certain contexts, for example: for keywords)
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE + input;
        String result = catApplication.catFileAndStdin(false, stdin, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFileAndStdin method in the CatApplication class.
     * Tests the behavior of the method when given a single file and stdin content, with line numbers enabled.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catFileAndStdin_SingleFileWithLineNumber_ReturnsFileAndStdinContentWithLineNumbers() throws Exception {
        String input = "Stdin Content";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2" + StringUtils.STRING_NEWLINE + "3 Stdin Content";
        String result = catApplication.catFileAndStdin(true, stdin, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFileAndStdin method in the CatApplication class.
     * Tests the behavior of the method when multiple files are provided and line numbers are not included.
     * The method should return the content of the files and the content from the standard input.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catFileAndStdin_MultipleFilesNoLineNumber_ReturnsFilesAndStdinContent() throws Exception {
        String input = "Stdin Content";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE +
                "Line 3" + StringUtils.STRING_NEWLINE + "Line 4" + StringUtils.STRING_NEWLINE +
                input;
        String result = catApplication.catFileAndStdin(false, stdin, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFileAndStdin method in the CatApplication class.
     * Tests the behavior of the method when multiple files are provided along with stdin content,
     * and line numbers are enabled.
     * <p>
     * The method should return the concatenated content of the files and the stdin content,
     * with line numbers added to each line.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catFileAndStdin_MultipleFilesWithLineNumber_ReturnsFilesAndStdinContentWithLineNumbers() throws Exception {
        String input = "Stdin Content";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2" + StringUtils.STRING_NEWLINE +
                "3 Line 3" + StringUtils.STRING_NEWLINE + "4 Line 4" + StringUtils.STRING_NEWLINE +
                "5 Stdin Content";
        String result = catApplication.catFileAndStdin(true, stdin, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFileAndStdin method in the CatApplication class.
     * Tests the behavior of the method when given duplicate file paths and the flag to display line numbers is set to true.
     * The method should return the content of the duplicated files and the content of the stdin with line numbers.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catFileAndStdin_DuplicateFilesWithLineNumber_ReturnsDuplicatedFilesAndStdinContentWithLineNumbers() throws Exception {
        String input = "Stdin Content";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = "1 Line 1" + StringUtils.STRING_NEWLINE + "2 Line 2" + StringUtils.STRING_NEWLINE +
                "3 Line 1" + StringUtils.STRING_NEWLINE + "4 Line 2" + StringUtils.STRING_NEWLINE +
                "5 Stdin Content";
        String result = catApplication.catFileAndStdin(true, stdin, tempFile1.getAbsolutePath(), tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the catFileAndStdin method in the CatApplication class.
     * Tests the scenario where duplicate files are provided as input, and line numbers are not included in the output.
     * The method should return the content of the duplicated files and the content from stdin, without line numbers.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void catFileAndStdin_DuplicateFilesNoLineNumber_ReturnsDuplicatedFilesAndStdinContentWithoutLineNumbers() throws Exception {
        String input = "Stdin Content";
        InputStream stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        String expectedOutput = "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE +
                "Line 1" + StringUtils.STRING_NEWLINE + "Line 2" + StringUtils.STRING_NEWLINE +
                input;
        String result = catApplication.catFileAndStdin(false, stdin, tempFile1.getAbsolutePath(), tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Verifies that catFileAndStdin throws an CatException when stdin is null.
     */
    @Test
    void catFileAndStdin_StdinIsNull_ThrowsCatException() {
        assertThrows(CatException.class, () -> {
            catApplication.catFileAndStdin(false, null, "file.txt");
        });
    }

    /**
     * Test case to verify the behavior of the `run` method in the `CatApplication` class
     * when the input arguments are null.
     *
     * @throws Exception If an exception occurs during the test
     */
    @Test
    void run_NullArgs_ThrowsCatException() throws Exception {
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = null;
        assertThrows(CatException.class, () -> new CatApplication().run(args, null, stdout));
    }

    /**
     * Test case to verify the behavior of the `run` method in the `CatApplication` class
     * when the standard output is null.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void run_NullStdout_ThrowsCatException() throws Exception {
        InputStream stdin = new ByteArrayInputStream("Hello World".getBytes());
        String[] args = {};
        assertThrows(CatException.class, () -> new CatApplication().run(args, stdin, null));
    }

    /**
     * Test case for the `catFileAndStdin` method when the standard input is null.
     * It verifies that an exception is thrown when attempting to run the `cat` application with a null standard input.
     */
    @Test
    public void catFileAndStdin_stdInIsNull_ThrowsException() {
        String[] fileNames = new String[]{"-", "testfile.txt"};

        assertThrows(Exception.class, () -> catApplication.run(fileNames, null, new ByteArrayOutputStream()));
    }

    /**
     * Test case for the `cat` command when reading from both a file and standard input, with line numbers enabled.
     * <p>
     * This test verifies that the `cat` command correctly reads from both a file and standard input, and outputs the
     * contents with line numbers.
     */
    @Test
    public void catFileAndStdin_stdInLineNumbers_ReadsFromInput() {
        String[] fileNames = new String[]{"-n", "-"};
        ByteArrayInputStream stdin = new ByteArrayInputStream("test".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> catApplication.run(fileNames, stdin, outputStream));
    }
}
