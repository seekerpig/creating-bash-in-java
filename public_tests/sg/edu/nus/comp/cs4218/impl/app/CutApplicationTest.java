package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class contains unit tests for the `CutApplication` class.
 * It tests various scenarios of the 'run', `cutFromStdin` and `cutFromFiles`
 * methods
 * in the `CutApplication` class.
 */
class CutApplicationTest {

    private CutApplication cutApp;
    private ByteArrayOutputStream outputStream;
    private File tempFile1;
    private File tempFile2;

    @BeforeEach
    void setUp() throws IOException {
        cutApp = new CutApplication();
        outputStream = new ByteArrayOutputStream();

        tempFile1 = File.createTempFile("testCut1", ".txt");
        Files.write(tempFile1.toPath(), List.of("abcdef", "fghijk"), StandardCharsets.UTF_8);

        tempFile2 = File.createTempFile("testCut2", ".txt");
        Files.write(tempFile2.toPath(), List.of("123あいう", "789えおか"), StandardCharsets.UTF_8);
    }

    @AfterEach
    void tearDown() throws Exception {
        outputStream.close();
        Files.deleteIfExists(tempFile1.toPath());
        Files.deleteIfExists(tempFile2.toPath());
    }

    private InputStream createInputStream(String data) {
        return new ByteArrayInputStream(data.getBytes());
    }

    /**
     * Test case for the `cutFromStdin` method with the parameters `false`, `true`,
     * and comma-separated bytes.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    void cutFromStdin_FalseTrue_CommaSeparatedBytes() throws Exception {
        InputStream stdin = createInputStream("baあz"); //NOPMD - suppressed CloseResource - stdin is being closed below, maybe pmd is not able to detect this
        String result = cutApp.cutFromStdin(false, true, Arrays.asList(new int[] { 1, 1 }, new int[] { 6, 6 }), stdin);
        assertEquals("bz" + StringUtils.STRING_NEWLINE, result);
        stdin.close();
    }

    /**
     * Test case for the `cutFromStdin` method when `false` is passed for the
     * `isDelimiter` parameter,
     * `true` is passed for the `isRange` parameter, and a range of bytes is
     * specified.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromStdin_FalseTrue_RangeOfBytes() throws Exception {
        InputStream stdin = createInputStream("Today is Tuesday."); //NOPMD - suppressed AvoidDuplicateLiterals -  For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        String result = cutApp.cutFromStdin(false, true, Arrays.asList(new int[] { 1, 4 }), stdin);
        assertEquals("Toda" + StringUtils.STRING_NEWLINE, result);
    }

    /**
     * Test case for the `cutFromStdin` method when `false` is passed as the
     * `isDelimiter` parameter,
     * `true` is passed as the `isField` parameter, and a single byte is specified.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromStdin_FalseTrue_SingleByte() throws Exception {
        InputStream stdin = createInputStream("baz"); //NOPMD - suppressed AvoidDuplicateLiterals -  For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        String result = cutApp.cutFromStdin(false, true, Arrays.asList(new int[] { 2, 2 }), stdin);
        assertEquals("a" + StringUtils.STRING_NEWLINE, result);
    }

    /**
     * Test case for the cutFromStdin method with true for the first parameter
     * (isStdin) and false for the second parameter (isCharacterSeparated).
     * This test case checks if the cutFromStdin method correctly cuts the input
     * from the standard input stream using comma-separated character positions.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    void cutFromStdin_TrueFalse_CommaSeparatedCharacters() throws Exception {
        InputStream stdin = createInputStream("Today is Tuesday."); //NOPMD - suppressed CloseResource - stdin is being closed below, maybe pmd is not able to detect this
        String result = cutApp.cutFromStdin(true, false, Arrays.asList(new int[] { 1, 1 }, new int[] { 8, 8 }), stdin);
        assertEquals("Ts" + StringUtils.STRING_NEWLINE, result);
        stdin.close();
    }

    /**
     * Test case for the `cutFromStdin` method with the parameters `true`, `false`,
     * and a range of characters.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromStdin_TrueFalse_RangeOfCharacters() throws Exception {
        InputStream stdin = createInputStream("あToday is Tuesday."); //NOPMD - suppressed CloseResource - stdin is being closed below, maybe pmd is not able to detect this
        String result = cutApp.cutFromStdin(true, false, Arrays.asList(new int[] { 1, 7 }), stdin);
        assertEquals("あToday " + StringUtils.STRING_NEWLINE, result);
        stdin.close();
    }

    /**
     * Test case for the cutFromStdin method when the parameters are set to true and
     * false, and the cut positions are single characters.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromStdin_TrueFalse_SingleCharacter() throws Exception {
        InputStream stdin = createInputStream("bあaz"); //NOPMD - suppressed CloseResource - stdin is being closed below, maybe pmd is not able to detect this
        String result = cutApp.cutFromStdin(true, false, Arrays.asList(new int[] { 2, 2 }), stdin);
        assertEquals("あ" + StringUtils.STRING_NEWLINE, result);
        stdin.close();
    }

    /**
     * Test case for the cutFromStdin method when the parameters `isStdin` is true
     * and `isFile` is false,
     * and the input contains multiple lines.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromStdin_TrueFalse_MultipleLines() throws Exception {
        String input = "Today is Tuesday." + StringUtils.STRING_NEWLINE + "Tomorrow is Wednesday.";
        InputStream stdin = createInputStream(input); //NOPMD - suppressed CloseResource - stdin is being closed below, maybe pmd is not able to detect this
        String result = cutApp.cutFromStdin(true, false, Arrays.asList(new int[] { 1, 6 }), stdin);
        assertEquals("Today " + StringUtils.STRING_NEWLINE + "Tomorr" + StringUtils.STRING_NEWLINE, result);
        stdin.close();
    }

    /**
     * Test case for the cutFromStdin method when the arguments are set to false and
     * true,
     * and the input contains multiple lines.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromStdin_FalseTrue_MultipleLines() throws Exception {
        String input = "foo" + StringUtils.STRING_NEWLINE + "あbar" + StringUtils.STRING_NEWLINE + "baz";
        InputStream stdin = createInputStream(input); //NOPMD - suppressed CloseResource -stdin is being closed below, maybe pmd is not able to detect this
        String result = cutApp.cutFromStdin(false, true, Arrays.asList(new int[] { 5, 6 }), stdin);
        assertEquals("" + StringUtils.STRING_NEWLINE + "ar" + StringUtils.STRING_NEWLINE + "" + StringUtils.STRING_NEWLINE, result);
        stdin.close();
    }

    /**
     * This test case verifies that the cutFromFiles method correctly cuts the
     * specified range of characters
     * from each line of the input file and returns the expected result.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    void cutFromFiles_TrueFalseCommaSeparatedCharacters_SingleFile() throws Exception {
        String result = cutApp.cutFromFiles(true, false, Arrays.asList(new int[] { 1, 1 }, new int[] { 3, 3 }),
                tempFile1.getAbsolutePath());
        assertEquals("ac" + StringUtils.STRING_NEWLINE + "fh" + StringUtils.STRING_NEWLINE, result);
    }

    /**
     * Test case for the `cutFromFiles` method when `true` is passed for the
     * `isCharSeparated` parameter,
     * `false` is passed for the `isFieldSeparated` parameter, and comma-separated
     * characters are used.
     * This test case verifies the behavior of the `cutFromFiles` method when
     * multiple files are provided.
     *
     * @throws Exception if an error occurs during the test case execution
     */
    @Test
    void cutFromFiles_TrueFalseCommaSeparatedCharacters_MultipleFiles() throws Exception {
        String result = cutApp.cutFromFiles(true, false, Arrays.asList(new int[] { 1, 1 }, new int[] { 3, 3 }),
                tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(
                "ac" + StringUtils.STRING_NEWLINE + "fh" + StringUtils.STRING_NEWLINE + "13" + StringUtils.STRING_NEWLINE + "79" + StringUtils.STRING_NEWLINE,
                result);
    }

    /**
     * Tests cutFromFiles method with character mode enabled, no delimiter
     * preservation, cutting characters 1 to 3, on a single file.
     *
     * @throws Exception if the test encounters an error.
     */
    @Test
    void cutFromFiles_TrueFalseRangeOfCharacters_SingleFile() throws Exception {
        String result = cutApp.cutFromFiles(true, false, Arrays.asList(new int[] { 1, 3 }),
                tempFile1.getAbsolutePath());
        assertEquals("abc" + StringUtils.STRING_NEWLINE + "fgh" + StringUtils.STRING_NEWLINE, result);
    }

    /**
     * Tests cutFromFiles method to individually cut a specified character range
     * from each line of multiple files without preserving delimiters.
     *
     * @throws Exception if the test encounters an error.
     */
    @Test
    void cutFromFiles_TrueFalseRangeOfCharacters_MultipleFiles() throws Exception {
        String result = cutApp.cutFromFiles(true, false, Arrays.asList(new int[] { 1, 3 }), tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals("abc" + StringUtils.STRING_NEWLINE + "fgh" + StringUtils.STRING_NEWLINE + "123" + StringUtils.STRING_NEWLINE
                + "789" + StringUtils.STRING_NEWLINE, result);
    }

    /**
     * Tests cutFromFiles method on a single file using byte mode, comma separation,
     * and specific byte ranges (`{1, 1}` and `{6, 6}`).
     *
     * @throws Exception if the test encounters an error.
     */
    @Test
    void cutFromFiles_FalseTrueCommaSeparatedBytes_SingleFile() throws Exception {
        String result = cutApp.cutFromFiles(false, true, Arrays.asList(new int[] { 1, 1 }, new int[] { 6, 6 }),
                tempFile1.getAbsolutePath());
        assertEquals("af" + StringUtils.STRING_NEWLINE + "fk" + StringUtils.STRING_NEWLINE, result); // Adjust expected result based on actual byte
        // handling
    }

    /**
     * Tests cutFromFiles method in byte mode with specified byte ranges [1, 1] and
     * [6, 6] across multiple files, without using delimiters.
     *
     * @throws Exception if the test encounters an error.
     */
    @Test
    void cutFromFiles_FalseTrueCommaSeparatedBytes_MultipleFiles() throws Exception {
        String result = cutApp.cutFromFiles(false, true, Arrays.asList(new int[] { 1, 1 }, new int[] { 6, 6 }),
                tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(
                "af" + StringUtils.STRING_NEWLINE + "fk" + StringUtils.STRING_NEWLINE + "1�" + StringUtils.STRING_NEWLINE + "7�" + StringUtils.STRING_NEWLINE,
                result); // Adjust expected result based on actual byte handling
    }

    /**
     * This test case verifies that the `cutFromFiles` method correctly cuts the
     * specified range of bytes from the given file.
     * The expected result is the substring of the file content that corresponds to
     * the specified range of bytes.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromFiles_FalseTrueRangeOfBytes_SingleFile() throws Exception {
        String result = cutApp.cutFromFiles(false, true, Arrays.asList(new int[] { 1, 9 }),
                tempFile2.getAbsolutePath());
        assertEquals("123あい" + StringUtils.STRING_NEWLINE + "789えお" + StringUtils.STRING_NEWLINE, result);
    }

    /**
     * This test case verifies that the `cutFromFiles` method correctly cuts the
     * specified range of bytes from multiple files.
     * The expected result is a string containing the extracted bytes from the
     * files, separated by the system's line separator.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void cutFromFiles_FalseTrueRangeOfBytes_MultipleFiles() throws Exception {
        String result = cutApp.cutFromFiles(false, true, Arrays.asList(new int[] { 1, 9 }), tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals("abcdef" + StringUtils.STRING_NEWLINE + "fghijk" + StringUtils.STRING_NEWLINE + "123あい"
                + StringUtils.STRING_NEWLINE + "789えお" + StringUtils.STRING_NEWLINE, result); // Adjust based on actual byte handling
    }

    /**
     * Test case for the cutFromStdin method when the parameters are set to true and
     * false,
     * and an invalid range is provided. This test verifies that a CutException is
     * thrown.
     *
     * @throws CutException if an error occurs during the cut operation
     */
    @Test
    void cutFromStdin_TrueFalseInvalidRange_ThrowsException() throws Exception {
        InputStream stdin = createInputStream("baz"); //NOPMD - suppressed CloseResource - stdin is being closed below, maybe pmd is not able to detect this
        assertThrows(CutException.class,
                () -> cutApp.cutFromStdin(true, false, Arrays.asList(new int[] { 1, 0 }), stdin));
        stdin.close();
    }

    /**
     * Test case for the cutFromStdin method when the parameters are set to true and
     * false,
     * and another invalid range is provided. This test verifies that a CutException
     * is thrown.
     *
     * @throws CutException if an error occurs during the cut operation
     */
    @Test
    void cutFromStdin_TrueFalseAnotherInvalidRange_ThrowsException() throws Exception {
        InputStream stdin = createInputStream("baz"); //NOPMD - suppressed CloseResource - stdin is being closed below, maybe pmd is not able to detect this
        assertThrows(CutException.class,
                () -> cutApp.cutFromStdin(true, false, Arrays.asList(new int[] { 0, 9 }), stdin));
        stdin.close();
    }

    /**
     * Test case to verify that the `run` method of the `CutApplication` class
     * correctly cuts the input from stdin when the file is specified as "-".
     * It sets up the necessary input stream and output stream, runs the `run`
     * method with the specified arguments, and compares the expected output with
     * the actual output.
     * The expected output is "Toda" when the input string is "Today is Tuesday."
     * and the cut range is "1-4".
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the `run` method
     */
    @Test
    public void run_ReadFromStdinWhenFileIsDash_CutsCorrectly() throws AbstractApplicationException {
        String[] args = { "-c", "1-4", "-" };
        String inputString = "Today is Tuesday.";
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        cutApp.run(args, stdin, stdout);

        String expectedOutput = "Toda" + StringUtils.STRING_NEWLINE;
        String actualOutput = stdout.toString(StandardCharsets.UTF_8);
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test case to verify that the `run` method of the CutApplication class
     * correctly cuts the input file
     * based on the specified character range.
     *
     * @throws AbstractApplicationException If an error occurs while executing the
     *                                      cut command.
     * @throws IOException                  If an I/O error occurs.
     */
    @Test
    public void run_ValidCutByCharacterCommand_CutsCorrectly() throws AbstractApplicationException, IOException {
        String[] args = { "-c", "1-3", tempFile1.getAbsolutePath() }; //NOPMD - suppressed AvoidDuplicateLiterals -  For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        cutApp.run(args, System.in, outputStream);
        String expectedOutput = "abc" + StringUtils.STRING_NEWLINE + "fgh" + StringUtils.STRING_NEWLINE;
        String actualOutput = outputStream.toString(StandardCharsets.UTF_8.name());
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test case to verify that the `run` method of the `CutApplication` class
     * correctly cuts the input file
     * based on the specified byte range.
     *
     * @throws AbstractApplicationException If an error occurs while running the
     *                                      application.
     */
    @Test
    public void run_ValidCutByByteCommand_CutsCorrectly() throws AbstractApplicationException {
        String[] args = { "-b", "1-3", tempFile2.getAbsolutePath() };
        ByteArrayInputStream stdin = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        cutApp.run(args, stdin, outputStream);
        String expectedOutput = "123" + StringUtils.STRING_NEWLINE + "789" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Test case to verify that a CutException is thrown when an invalid flag is
     * provided to the run method of CutApplication.
     * The method constructs a string array with an invalid flag ("-x"), creates a
     * ByteArrayInputStream with an empty string as input,
     * and asserts that a CutException is thrown with an error message containing
     * the constant ERR_INVALID_FLAG defined in ErrorConstants.
     */
    @Test
    public void run_InvalidFlag_ThrowsCutException() {
        String[] args = { "-x" };
        ByteArrayInputStream stdin = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        Exception exception = assertThrows(CutException.class, () -> cutApp.run(args, stdin, outputStream));
        assertTrue(exception.getMessage().contains(ErrorConstants.ERR_INVALID_FLAG));
    }

    /** 
     * Tests that run method throws a CutException for invalid flag combinations (both -c and -b are present).
     */
    @Test
    public void run_InvalidFlagCombination_ThrowsCutException() {
        String[] args = { "-c", "-b", "1-3" };
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayInputStream stdin = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
        Exception exception = assertThrows(CutException.class, () -> cutApp.run(args, stdin, stdout));
        assertTrue(exception.getMessage().contains(ErrorConstants.ERR_INVALID_FLAG));
    }

    /**
     * Tests that run method throws a CutException for invalid flag combinations (both -c and -b are absent).
     */
    @Test
    public void run_AnotherInvalidFlagCombination_ThrowsCutException() {
        String[] args = { "1-3" };
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayInputStream stdin = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
        Exception exception = assertThrows(CutException.class, () -> cutApp.run(args, stdin, stdout));
        assertTrue(exception.getMessage().contains(ErrorConstants.ERR_INVALID_FLAG));
    }

    /**
     * Test case to verify that a `CutException` is thrown when `null` arguments are
     * passed to the `run` method.
     *
     * This test case creates a `ByteArrayInputStream` with an empty string as
     * input, and then calls the `run` method of the `cutApp` object with `null`
     * arguments for `args` and `stdin`, and the `outputStream` as the output
     * stream. It asserts that a `CutException` is thrown.
     */
    @Test
    public void run_NullArgs_ThrowsCutException() {
        ByteArrayInputStream stdin = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        assertThrows(CutException.class, () -> cutApp.run(null, stdin, outputStream));
    }

    /**
     * Test case to verify that the `run` method throws a `CutException` when the
     * standard input is null.
     * It creates a `CutApplication` instance and calls the `run` method with the
     * arguments "-c 1-3", null standard input, and an output stream.
     * It asserts that a `CutException` is thrown.
     */
    @Test
    public void run_NullStdin_ThrowsCutException() {
        String[] args = { "-c", "1-3" };
        assertThrows(CutException.class, () -> cutApp.run(args, null, outputStream));
    }

    /**
     * Test case to verify that a CutException is thrown when the stdout is null.
     * It creates a new instance of CutApplication and calls the run method with the
     * specified arguments and null stdout.
     * The expected behavior is that a CutException is thrown.
     */
    @Test
    public void run_NullStdout_ThrowsCutException() {
        String[] args = { "-c", "1-3" };
        ByteArrayInputStream stdin = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        assertThrows(CutException.class, () -> cutApp.run(args, stdin, null));
    }
}
