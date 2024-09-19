package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * This class contains test cases for the SortApplication class.
 * It tests the functionality of sorting input from standard input and returning
 * the sorted output.
 * The test cases cover different scenarios such as sorting with various flags
 * and different input types.
 */
public class SortApplicationTest {

    private static final String LINE_SEPARATOR = StringUtils.STRING_NEWLINE;

    private static final String MIXED_INPUT_1 = "apple" + LINE_SEPARATOR + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "Banana" + LINE_SEPARATOR + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "123" + LINE_SEPARATOR + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "23" + LINE_SEPARATOR +
            "!@#" + LINE_SEPARATOR + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "Cherry" + LINE_SEPARATOR + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "grape" + LINE_SEPARATOR + //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here
            "ORANGE" + LINE_SEPARATOR; //NOPMD - suppressed AvoidDuplicateLiterals - These strings are already part of a global variable, it wouldn't make sense from swe standpoint to further have global variables for all strings here

    private SortApplication sortApp;
    private InputStream stdin;
    private OutputStream stdout;

    private File tempFile1;
    private File tempFile2;
    private File tempFile3;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        sortApp = new SortApplication();

        stdout = new ByteArrayOutputStream();

        tempFile1 = File.createTempFile("testSort1", ".txt");
        List<String> lines1 = Arrays.asList("!@#", "123", "23", "Banana", "ORANGE", "apple", "grape");
        Files.write(tempFile1.toPath(), lines1, StandardCharsets.UTF_8);

        tempFile2 = File.createTempFile("testSort2", ".txt");
        List<String> lines2 = Arrays.asList("*&^%", "1", "10", "Cherry", "ZEBRA", "$$$"); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        Files.write(tempFile2.toPath(), lines2, StandardCharsets.UTF_8);

        tempFile3 = File.createTempFile("testSort3", ".txt");
        List<String> lines3 = Arrays.asList("999", "42"); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        Files.write(tempFile3.toPath(), lines3, StandardCharsets.UTF_8);
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (stdin != null) {
            stdin.close();
        }
        stdout.close();
        Files.delete(tempFile1.toPath());
        Files.delete(tempFile2.toPath());
        Files.delete(tempFile3.toPath());
    }

    /**
     * Test case for the method sortFromStdin_SimpleInput_ReturnsSortedOutput.
     * It tests the functionality of sorting input from standard input and returning
     * the sorted output.
     * The input consists of three strings: "banana", "apple", and "cherry".
     * The expected output is "apple\nbanana\ncherry".
     * The method sets up the input stream with the given input, calls the
     * sortFromStdin method,
     * and compares the result with the expected output using the assertEquals
     * method.
     *
     * @throws SortException if there is an error during sorting.
     */
    @Test
    public void sortFromStdin_SimpleInput_ReturnsSortedOutput() throws SortException {
        String simpleInput = "banana" + System.lineSeparator() + "apple" + System.lineSeparator() + "cherry";
        stdin = new ByteArrayInputStream(simpleInput.getBytes());
        String expected = "apple" + System.lineSeparator() + "banana" + System.lineSeparator() + "cherry";

        String result = sortApp.sortFromStdin(false, false, false, stdin);
        assertEquals(expected, result);
    }

    /**
     * Test case for the sortFromStdin method when given a null input stream.
     * Expects a SortException to be thrown.
     *
     * @throws SortException if an error occurs during sorting.
     */
    @Test()
    public void sortFromStdin_NullInputStream_ThrowsException() throws SortException {
        assertThrows(SortException.class, () -> sortApp.sortFromStdin(false, false, false, null));
    }

    /**
     * Test case for the sortFromFiles method when given no files.
     * Expects a SortException to be thrown.
     *
     * @throws SortException if an error occurs during sorting.
     */
    @Test()
    public void sortFromFiles_NoFiles_ThrowsException() throws SortException {
        assertThrows(SortException.class, () -> sortApp.sortFromFiles(false, false, false, null));
    }

    /**
     * Test case for the sortFromStdin method when no flags are provided.
     * It verifies that the method returns the expected output when sorting input
     * from stdin without any flags.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_NoFlags_ReturnsDefaultSortedOutput() throws Exception {
        String expectedOutput = "!@#" + LINE_SEPARATOR + "123" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "Banana"
                + LINE_SEPARATOR + "Cherry" + LINE_SEPARATOR + "ORANGE" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR
                + "grape";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(false, false, false, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromStdin method when only the first word is a number.
     * It verifies that the method returns the output sorted numerically.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_OnlyIsFirstWordNumber_ReturnsNumericallySortedOutput() throws Exception {
        String expectedOutput = "!@#" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "123" + LINE_SEPARATOR + "Banana"
                + LINE_SEPARATOR + "Cherry" + LINE_SEPARATOR + "ORANGE" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR
                + "grape";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(true, false, false, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromStdin method when only the reverse flag is set.
     * It verifies that the method returns the reverse sorted output when provided
     * with input from stdin.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_OnlyIsReverseOrder_ReturnsReverseSortedOutput() throws Exception {
        String expectedOutput = "grape" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR + "ORANGE" + LINE_SEPARATOR
                + "Cherry" + LINE_SEPARATOR + "Banana" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "123" + LINE_SEPARATOR
                + "!@#";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(false, true, false, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromStdin method when only case independence is
     * enabled.
     * It verifies that the method returns the expected case-insensitive sorted
     * output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_OnlyIsCaseIndependent_ReturnsCaseInsensitiveSortedOutput() throws Exception {
        String expectedOutput = "!@#" + LINE_SEPARATOR + "123" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "apple"
                + LINE_SEPARATOR + "Banana" + LINE_SEPARATOR + "Cherry" + LINE_SEPARATOR + "grape" + LINE_SEPARATOR
                + "ORANGE";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(false, false, true, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromStdin method when the first word is a number and
     * the sorting order is reverse.
     * It verifies that the method returns the expected numerically reverse sorted
     * output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_IsFirstWordNumberAndIsReverseOrder_ReturnsNumericallyReverseSortedOutput()
            throws Exception {
        String expectedOutput = "grape" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR + "ORANGE" + LINE_SEPARATOR
                + "Cherry" + LINE_SEPARATOR + "Banana" + LINE_SEPARATOR + "123" + LINE_SEPARATOR + "23" + LINE_SEPARATOR
                + "!@#";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(true, true, false, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for sorting input from standard input when the first word is a
     * number and the sorting is case-insensitive and numerically case-insensitive.
     * It verifies that the method returns the expected sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_IsFirstWordNumberAndIsCaseIndependent_ReturnsNumericallyCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = "!@#" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "123" + LINE_SEPARATOR + "apple"
                + LINE_SEPARATOR + "Banana" + LINE_SEPARATOR + "Cherry" + LINE_SEPARATOR + "grape" + LINE_SEPARATOR
                + "ORANGE";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(true, false, true, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromStdin method when the input is in reverse order and
     * is case independent.
     * It verifies that the method returns the expected output, which is the reverse
     * case-insensitive sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_IsReverseOrderAndIsCaseIndependent_ReturnsReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = "ORANGE" + LINE_SEPARATOR + "grape" + LINE_SEPARATOR + "Cherry" + LINE_SEPARATOR
                + "Banana" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "123" + LINE_SEPARATOR
                + "!@#";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(false, true, true, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromStdin method when all flags are set to true.
     * It verifies that the method returns the expected output, which is the input
     * sorted in reverse order,
     * considering numerical values, and ignoring case sensitivity.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromStdin_AllFlagsTrue_ReturnsNumericallyReverseCaseInsensitiveSortedOutput() throws Exception {
        String expectedOutput = "ORANGE" + LINE_SEPARATOR + "grape" + LINE_SEPARATOR + "Cherry" + LINE_SEPARATOR
                + "Banana" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR + "123" + LINE_SEPARATOR + "23" + LINE_SEPARATOR
                + "!@#";
        stdin = new ByteArrayInputStream(MIXED_INPUT_1.getBytes());
        String result = sortApp.sortFromStdin(true, true, true, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when sorting a single file without any
     * flags.
     * It verifies that the method returns the expected output after sorting the
     * file.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SingleFileNoFlags_ReturnsDefaultSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("!@#", "123", "23", "Banana", "ORANGE", "apple", "grape"));
        String result = sortApp.sortFromFiles(false, false, false, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case to verify the behavior of the `sortFromFiles` method when sorting
     * from two different files without any flags.
     * It checks if the method returns the merged and sorted output as expected.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesNoFlags_ReturnsMergedSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "$$$", "*&^%", "1", "10", "123", "23",
                "Banana", "Cherry", "ORANGE", "ZEBRA", "apple", "grape"));
        String result = sortApp.sortFromFiles(false, false, false, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for sorting from files when the same file is provided twice without
     * any flags.
     * It verifies that the method returns the duplicated sorted output as expected.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SameFileTwiceNoFlags_ReturnsDuplicatedSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "!@#", "123", "123", "23", "23",
                "Banana", "Banana", "ORANGE", "ORANGE", "apple", "apple", "grape", "grape"));
        String result = sortApp.sortFromFiles(false, false, false, tempFile1.getAbsolutePath(),
                tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case to verify the behavior of the `sortFromFiles` method when the first
     * word in the file is a number.
     * It checks if the method returns the output sorted numerically.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SingleFileIsFirstWordNumber_ReturnsNumericallySortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("!@#", "23", "123", "Banana", "ORANGE", "apple", "grape"));
        String result = sortApp.sortFromFiles(true, false, false, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case to verify the behavior of the `sortFromFiles` method when sorting
     * from two different files
     * where the first word is a number. It should return the merged output of the
     * files, sorted numerically.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesIsFirstWordNumber_ReturnsMergedNumericallySortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "$$$", "*&^%", "1", "10", "23", "123",
                "Banana", "Cherry", "ORANGE", "ZEBRA", "apple", "grape"));
        String result = sortApp.sortFromFiles(true, false, false, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the method sortFromFiles when the same file is provided twice
     * and the first word is a number.
     * It verifies that the method returns the duplicated input file content sorted
     * in numerical order.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SameFileTwiceIsFirstWordNumber_ReturnsDuplicatedNumericallySortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("$$$", "$$$", "*&^%", "*&^%", "1", "1", "10",
                "10", "Cherry", "Cherry", "ZEBRA", "ZEBRA"));
        String result = sortApp.sortFromFiles(true, false, false, tempFile2.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when a single file is in reverse
     * order.
     * It verifies that the method returns the reverse sorted output as expected.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SingleFileIsReverseOrder_ReturnsReverseSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("grape", "apple", "ORANGE", "Banana", "23", "123", "!@#"));
        String result = sortApp.sortFromFiles(false, true, false, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case to verify the behavior of the `sortFromFiles` method when sorting
     * two different files in reverse order.
     * It checks if the method returns the merged and reverse sorted output as
     * expected.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesIsReverseOrder_ReturnsMergedReverseSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("grape", "apple", "ZEBRA", "ORANGE", "Cherry",
                "Banana", "23", "123", "10", "1", "*&^%", "$$$", "!@#"));
        String result = sortApp.sortFromFiles(false, true, false, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for sorting from files where the same file is provided twice and
     * the content is in reverse order.
     * It verifies that the method returns the duplicated reverse sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SameFileTwiceIsReverseOrder_ReturnsDuplicatedReverseSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("grape", "grape", "apple", "apple", "ORANGE",
                "ORANGE", "Banana", "Banana", "23", "23", "123", "123", "!@#", "!@#"));
        String result = sortApp.sortFromFiles(false, true, false, tempFile1.getAbsolutePath(),
                tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the method sortFromFiles when sorting a single file in a
     * case-independent manner.
     * It verifies that the method returns the expected case-insensitive sorted
     * output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SingleFileIsCaseIndependent_ReturnsCaseInsensitiveSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("!@#", "123", "23", "apple", "Banana", "grape", "ORANGE"));
        String result = sortApp.sortFromFiles(false, false, true, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case to verify the behavior of the `sortFromFiles` method when sorting
     * from two different files with case independence.
     * It checks if the method returns the merged output of the two files, sorted in
     * a case-insensitive manner.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesIsCaseIndependent_ReturnsMergedCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "$$$", "*&^%", "1", "10", "123", "23",
                "apple", "Banana", "Cherry", "grape", "ORANGE", "ZEBRA"));
        String result = sortApp.sortFromFiles(false, false, true, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the method sortFromFiles when the same file is provided twice.
     * The test checks if the method returns the duplicated case-insensitive sorted
     * output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SameFileTwiceIsCaseIndependent_ReturnsDuplicatedCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("$$$", "$$$", "*&^%", "*&^%", "1", "1", "10",
                "10", "Cherry", "Cherry", "ZEBRA", "ZEBRA"));
        String result = sortApp.sortFromFiles(false, false, true, tempFile2.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when all flags are set to true.
     * It verifies that the method returns the expected output, which is the
     * numerically reverse case-insensitive sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SingleFileAllFlagsTrue_ReturnsNumericallyReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("ORANGE", "grape", "Banana", "apple", "123", "23", "!@#"));
        String result = sortApp.sortFromFiles(true, true, true, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when sorting two different files with
     * all flags set to true.
     * It verifies that the method returns the merged output of the files, sorted
     * numerically in reverse order and case-insensitive.
     * The expected output is a string containing the sorted lines in the following
     * order: "ZEBRA", "ORANGE", "grape", "Cherry", "Banana", "apple", "123", "23",
     * "10", "1", "*&^%", "$$$", "!@#".
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesAllFlagsTrue_ReturnsMergedNumericallyReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("ZEBRA", "ORANGE", "grape", "Cherry",
                "Banana", "apple", "123", "23", "10", "1", "*&^%", "$$$", "!@#"));
        String result = sortApp.sortFromFiles(true, true, true, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when the same file is provided twice
     * and all flags are set to true.
     * It verifies that the method returns the duplicated, numerically reverse,
     * case-insensitive sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SameFileTwiceAllFlagsTrue_ReturnsDuplicatedNumericallyReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("ORANGE", "ORANGE", "grape", "grape",
                "Banana", "Banana", "apple", "apple", "123", "123", "23", "23", "!@#", "!@#"));
        String result = sortApp.sortFromFiles(true, true, true, tempFile1.getAbsolutePath(),
                tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the method sortFromFiles when the singleFileFirst and lastFlags
     * are set to true.
     * It verifies that the method returns the expected output, which is the
     * numerically case-insensitive sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SingleFileFirstAndLastFlagsTrue_ReturnsNumericallyCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("!@#", "23", "123", "apple", "Banana", "grape", "ORANGE"));
        String result = sortApp.sortFromFiles(true, false, true, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when two different files are provided
     * and the first and last flags are set to true.
     * It verifies that the method returns the merged, numerically and
     * case-insensitively sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesFirstAndLastFlagsTrue_ReturnsMergedNumericallyCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "$$$", "*&^%", "1", "10", "23", "123",
                "apple", "Banana", "Cherry", "grape", "ORANGE", "ZEBRA"));
        String result = sortApp.sortFromFiles(true, false, true, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for sorting from files when the same file is provided twice and the
     * first and last flags are set to true.
     * It verifies that the method returns the duplicated, numerically and
     * case-insensitively sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SameFileTwiceFirstAndLastFlagsTrue_ReturnsDuplicatedNumericallyCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("$$$", "$$$", "*&^%", "*&^%", "1", "1", "10",
                "10", "Cherry", "Cherry", "ZEBRA", "ZEBRA"));
        String result = sortApp.sortFromFiles(true, false, true, tempFile2.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the method sortFromFiles when the last two flags are set to
     * true.
     * It verifies that the method returns the reverse case-insensitive sorted
     * output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_LastTwoFlagsTrue_ReturnsReverseCaseInsensitiveSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("ORANGE", "grape", "Banana", "apple", "23", "123", "!@#"));
        String result = sortApp.sortFromFiles(false, true, true, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when sorting from two different files
     * with the last two flags set to true.
     * It verifies that the method returns the merged, reverse, case-insensitive
     * sorted output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesLastTwoFlagsTrue_ReturnsMergedReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("ZEBRA", "ORANGE", "grape", "Cherry",
                "Banana", "apple", "23", "123", "10", "1", "*&^%", "$$$", "!@#"));
        String result = sortApp.sortFromFiles(false, true, true, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when the same file is provided twice
     * as input,
     * with the last two flags set to true. It verifies that the method returns the
     * expected
     * output, which is the duplicated, reverse, case-insensitive sorted content of
     * the file.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SameFileTwiceLastTwoFlagsTrue_ReturnsDuplicatedReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("ORANGE", "ORANGE", "grape", "grape",
                "Banana", "Banana", "apple", "apple", "23", "23", "123", "123", "!@#", "!@#"));
        String result = sortApp.sortFromFiles(false, true, true, tempFile1.getAbsolutePath(),
                tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the method sortFromFiles when the first two flags are set to
     * true.
     * It verifies that the method returns the expected output, which is the
     * numerically reverse sorted content of a single file.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_SingleFileFirstTwoFlagsTrue_ReturnsNumericallyReverseSortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR,
                Arrays.asList("grape", "apple", "ORANGE", "Banana", "123", "23", "!@#"));
        String result = sortApp.sortFromFiles(true, true, false, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when two different files are provided
     * and the first two flags are set to true.
     * It verifies that the method returns the merged output of the files, sorted
     * numerically in reverse order.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    public void sortFromFiles_TwoDifferentFilesFirstTwoFlagsTrue_ReturnsMergedNumericallyReverseSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("grape", "apple", "ZEBRA", "ORANGE", "Cherry",
                "Banana", "123", "23", "10", "1", "*&^%", "$$$", "!@#"));
        String result = sortApp.sortFromFiles(true, true, false, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when the same file is provided twice
     * and the first two flags are set to true.
     * It verifies that the method returns the duplicated content of the file,
     * sorted in reverse numerical order.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    public void sortFromFiles_SameFileTwiceFirstTwoFlagsTrue_ReturnsDuplicatedNumericallyReverseSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("grape", "grape", "apple", "apple", "ORANGE",
                "ORANGE", "Banana", "Banana", "123", "123", "23", "23", "!@#", "!@#"));
        String result = sortApp.sortFromFiles(true, true, false, tempFile1.getAbsolutePath(),
                tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the sortFromFiles method when all flags are set to true.
     * It verifies that the method returns the merged, numerically sorted, reverse
     * case-insensitive output from three input files.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_ThreeFilesAllFlagsTrue_ReturnsMergedNumericallyReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("ZEBRA", "ORANGE", "grape", "Cherry",
                "Banana", "apple", "999", "123", "42", "23", "10", "1", "*&^%", "$$$", "!@#"));
        String result = sortApp.sortFromFiles(true, true, true, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath(), tempFile3.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case to verify the behavior of the `sortFromFiles` method when sorting
     * three files in reverse order and case-insensitive manner.
     * It checks if the method returns the merged output of the three files, sorted
     * in reverse order and case-insensitive manner.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_ThreeFilesReverseCaseInsensitive_ReturnsMergedReverseCaseInsensitiveSortedOutput()
            throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "$$$", "*&^%", "1", "10", "23", "42",
                "123", "999", "apple", "Banana", "Cherry", "grape", "ORANGE", "ZEBRA"));
        String result = sortApp.sortFromFiles(true, false, true, tempFile1.getAbsolutePath(),
                tempFile2.getAbsolutePath(), tempFile3.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for sorting three identical files numerically.
     * It verifies that the method returns the expected output, which is the content
     * of the three files
     * combined and sorted in numerical order.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void sortFromFiles_ThreeSameFilesNumericalSorting_ReturnsTripledNumericallySortedOutput() throws Exception {
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("42", "42", "42", "999", "999", "999"));
        String result = sortApp.sortFromFiles(true, false, false, tempFile3.getAbsolutePath(),
                tempFile3.getAbsolutePath(), tempFile3.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for running the SortApplication's `run` method with no options and
     * input from stdin.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void run_SortStdinNoOptions_GivenInputFromStdin() throws Exception {
        String input = MIXED_INPUT_1;
        stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        sortApp.run(new String[] {}, stdin, stdout);
        stdout.flush();
        String expectedOutput = "!@#" + LINE_SEPARATOR + "123" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "Banana"
                + LINE_SEPARATOR + "Cherry" + LINE_SEPARATOR + "ORANGE" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR
                + "grape" + LINE_SEPARATOR;
        String actualOutput = stdout.toString();
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test case for running the SortApplication's `run` method with the "-r" flag,
     * using input from stdin in reverse order.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void run_SortStdinReverseOrder_GivenInputFromStdin() throws Exception {
        String input = MIXED_INPUT_1;
        stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        sortApp.run(new String[] { "-r" }, stdin, stdout);
        stdout.flush();
        String expectedOutput = "grape" + LINE_SEPARATOR + "apple" + LINE_SEPARATOR + "ORANGE" + LINE_SEPARATOR
                + "Cherry" + LINE_SEPARATOR + "Banana" + LINE_SEPARATOR + "23" + LINE_SEPARATOR + "123" + LINE_SEPARATOR
                + "!@#" + LINE_SEPARATOR;
        String actualOutput = stdout.toString();
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test case for running the SortApplication's `run` method with file names and
     * no options.
     * It verifies that the expected output matches the actual output after running
     * the method.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void run_SortFilesNoOptions_GivenFileNames() throws Exception {
        sortApp.run(new String[] { tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath() }, null, stdout);
        stdout.flush();
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "$$$", "*&^%", "1", "10", "123", "23",
                "Banana", "Cherry", "ORANGE", "ZEBRA", "apple", "grape", ""));
        String actualOutput = stdout.toString();
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test case for the `run` method in the `SortApplication` class.
     * It tests the sorting of files with the numeric option given file names.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void run_SortFilesWithNumericOption_GivenFileNames() throws Exception {
        sortApp.run(new String[] { "-n", tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath() }, null, stdout);
        stdout.flush();
        String expectedOutput = String.join(LINE_SEPARATOR, Arrays.asList("!@#", "$$$", "*&^%", "1", "10", "23", "123",
                "Banana", "Cherry", "ORANGE", "ZEBRA", "apple", "grape", ""));
        String actualOutput = stdout.toString();
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test case to verify that the `run` method of the SortApplication class throws
     * a SortException
     * when the stdout parameter is null.
     */
    @Test
    public void run_SortFilesWithCheckForStdoutNull_ThrowsSortException() {
        assertThrows(SortException.class, () -> sortApp.run(new String[] {}, null, null));
    }

    /**
     * Test case to verify that running the SortApplication with an invalid flag
     * throws a SortException.
     * The method sets up the necessary input arguments and input stream, and then
     * asserts that a SortException is thrown.
     * It also checks that the exception message contains the expected error
     * message.
     */
    @Test
    public void run_InvalidFlag_ThrowsSortException() {
        String[] args = { "-x" };
        stdin = new ByteArrayInputStream("Some input".getBytes(StandardCharsets.UTF_8));
        Exception exception = assertThrows(SortException.class, () -> sortApp.run(args, stdin, stdout));
        String expectedMessage = "sort: illegal option -- x";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
