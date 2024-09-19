package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import sg.edu.nus.comp.cs4218.exception.PasteException;

public class PasteApplicationTest {
    private static File tempFile1;
    private static File tempFile2;
    private static File tempFile3;
    private static final File DIRECTORY = new File("pasteTestDirectory");
    private static final File NONEXISTENT = new File("paste_nonexistent.txt");
    private static final File FILE_EMPTY = new File("paste_empty.txt");
    private static final File FILE_1 = new File("paste_1.txt");
    private static final String TEXT_FILE_1 = String.join(System.lineSeparator(), "A", "B", "C", "D", "E");
    private static final File FILE_2 = new File("paste_2.txt");
    private static final String TEXT_FILE_2 = String.join(System.lineSeparator(), "1", "2", "3", "4", "5");
    private static final File FILE_3 = new File("paste_3.txt");
    private static final String TEXT_FILE_3 = String.join(System.lineSeparator(), "a", "b", "c", "d", "e");
    private static final File FILE_4 = new File("paste_4.txt");
    private static final String TEXT_FILE_4 = String.join(System.lineSeparator(), "!", "@", "#", "$");
    private static final File FILE_5 = new File("paste_5.txt");
    private static final String TEXT_FILE_5 = String.join(System.lineSeparator(), "-", "_", "=");
    private static final String ERR_IS_DIR = String.format("paste: %s: Is a directory", DIRECTORY);
    private static final String ERR_NO_SUCH_FILE = String.format("paste: %s: No such file or directory", NONEXISTENT);
    private static final String TEMP = "temp-paste";
    private static final String DIR = "dir";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String TEST_LINE_1_1 = "Test line 1.1";
    private static final String TEST_LINE_1_2 = "Test line 1.2";
    private static final String TEST_LINE_1_3 = "Test line 1.3";
    private static final String TEST_LINE = "Test line 1" + LINE_SEPARATOR +
            "Test line 2" + LINE_SEPARATOR +
            "Test line 3";
    private static final String EXPECTED_TEXT = "Test line 1\tTest line 2\tTest line 3";
    private static final String NON_EXISTENT_FILE = "nonexistent_file.txt";
    private static final String EXCEPTION_MSG = "Expected PasteException to be thrown";
    private static final String TEXT_1 = TEST_LINE_1_1 + LINE_SEPARATOR +
            TEST_LINE_1_2 + LINE_SEPARATOR +
            TEST_LINE_1_3;
    private static final String TEXT_2 = "Test line 2.1" + LINE_SEPARATOR +
            "Test line 2.2";
    private static final String ABCDE_TAB = "A\tB\tC\tD\tE";
    public static final String STRING_NEWLINE = System.lineSeparator();
    public static final char CHAR_TAB = '\t';
    private static final Deque<Path> FILES = new ArrayDeque<>();
    private static Path tempPath;
    private static Path dirPath;
    private static PasteApplication pasteApplication;

    private void assertEqualsReplacingNewlines(String expected, String actual) {
        assertEquals(expected.replaceAll("\r\n", "\n"), actual.replaceAll("\r\n", "\n"));
    }

    public static void writeToFileWithText(File file, String text) throws IOException {
        FileWriter writer = new FileWriter(file); //NOPMD

        if (text == null || text.isBlank()) {
            writer.close();
            return;
        }

        writer.write(text);
        writer.close();
    }

    @BeforeEach
    void setUp() {
        pasteApplication = new PasteApplication();
    }

    @BeforeAll
    static void createTemp() throws IOException {
        String currentDirectory = System.getProperty("user.dir");

        tempPath = Paths.get(currentDirectory, TEMP);
        Files.createDirectory(tempPath);

        dirPath = Paths.get(currentDirectory, TEMP, DIR);
        Files.createDirectory(dirPath);

        writeToFileWithText(FILE_EMPTY, null);
        writeToFileWithText(FILE_1, TEXT_FILE_1);
        writeToFileWithText(FILE_2, TEXT_FILE_2);
        writeToFileWithText(FILE_3, TEXT_FILE_3);
        writeToFileWithText(FILE_4, TEXT_FILE_4);
        writeToFileWithText(FILE_5, TEXT_FILE_5);

        DIRECTORY.mkdirs();

        tempFile1 = File.createTempFile("testPaste1", ".txt");
        Files.write(tempFile1.toPath(), Arrays.asList("A", "B", "C", "D"), StandardCharsets.UTF_8);

        tempFile2 = File.createTempFile("testPaste2", ".txt");
        Files.write(tempFile2.toPath(), Arrays.asList("1", "2", "3", "4"), StandardCharsets.UTF_8);

        tempFile3 = File.createTempFile("testPaste3", ".txt");
        Files.write(tempFile3.toPath(), Arrays.asList("1 2", "3 4", "5 6", "7 8"), StandardCharsets.UTF_8);
    }

    @AfterAll
    static void deleteFiles() throws IOException {
        for (Path file : FILES) {
            Files.deleteIfExists(file);
        }
        Files.delete(dirPath);
        Files.delete(tempPath);

        FILE_EMPTY.delete();
        FILE_1.delete();
        FILE_2.delete();
        FILE_3.delete();
        FILE_4.delete();
        FILE_5.delete();

        DIRECTORY.delete();

        Files.deleteIfExists(tempFile1.toPath());
        Files.deleteIfExists(tempFile2.toPath());
    }

    private void createFile(String name, String text) throws IOException {
        Path path = tempPath.resolve(name);
        Files.createFile(path);
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
        FILES.push(path);
    }

    private String[] toArgs(String flag, String... files) {
        List<String> args = new ArrayList<>();
        if (!flag.isEmpty()) {
            args.add("-" + flag);
        }
        for (String file : files) {
            if ("-".equals(file)) {
                args.add(file);
            } else {
                args.add(Paths.get(TEMP, file).toString());
            }
        }
        return args.toArray(new String[0]);
    }

    /**
     * Verifies that running the paste command with a null standard output stream throws a PasteException.
     */
    @Test
    void run_SingleStdinNullStdout_ThrowsException() {
        InputStream inputStream = new ByteArrayInputStream(TEST_LINE.getBytes(StandardCharsets.UTF_8));
        assertThrows(PasteException.class, () -> pasteApplication.run(toArgs(""), inputStream, null));
    }

    /**
     * Tests that executing the paste command with null standard input and no files specified, without any flags, results in a PasteException.
     */
    @Test
    void run_NullStdinNullFilesNoFlag_ThrowsException() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertThrows(PasteException.class, () -> pasteApplication.run(toArgs(""), null, output));
    }

    /**
     * Checks that running the paste command with a flag, null standard input, and no files leads to a PasteException.
     */
    @Test
    void run_NullStdinNullFilesFlag_ThrowsException() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertThrows(PasteException.class, () -> pasteApplication.run(toArgs("n"), null, output));
    }

    //mergeStdin cases

    /**
     * Ensures the paste command correctly displays contents from standard input without any flags.
     */
    @Test
    void run_SingleStdinNoFlag_DisplaysStdinContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(TEST_LINE.getBytes(StandardCharsets.UTF_8));
        pasteApplication.run(toArgs(""), inputStream, output);
        assertEquals((TEST_LINE + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Verifies that the paste command with the serial flag ('s') correctly processes and displays standard input in a non-parallel manner.
     */
    @Test
    void run_SingleStdinFlag_DisplaysNonParallelStdinContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(TEST_LINE.getBytes(StandardCharsets.UTF_8));
        pasteApplication.run(toArgs("s"), inputStream, output);
        assertEquals((EXPECTED_TEXT + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Confirms that the paste command displays standard input contents correctly when using "-" as a file argument without any flags.
     */
    @Test
    void run_SingleStdinDashNoFlag_DisplaysStdinContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(TEST_LINE.getBytes(StandardCharsets.UTF_8));
        pasteApplication.run(toArgs("", "-"), inputStream, output);
        assertEquals((TEST_LINE + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Tests that the paste command with the serial flag ('s') and "-" as a file argument properly displays standard input in serial format.
     */
    @Test
    void run_SingleStdinDashFlag_DisplaysNonParallelStdinContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(TEST_LINE.getBytes(StandardCharsets.UTF_8));
        pasteApplication.run(toArgs("s", "-"), inputStream, output);
        assertEquals((EXPECTED_TEXT + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Ensures that the paste command outputs an empty string when provided with an empty standard input and no flags.
     */
    @Test
    void run_SingleEmptyStdinNoFlag_DisplaysEmpty() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String text = "";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        pasteApplication.run(toArgs(""), inputStream, output);
        assertEquals(text, output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Checks that the paste command with the serial flag ('s') outputs an empty string when given an empty standard input.
     */
    @Test
    void run_SingleEmptyStdinFlag_DisplaysEmpty() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String text = "";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        pasteApplication.run(toArgs("s"), inputStream, output);
        assertEquals(text, output.toString(StandardCharsets.UTF_8));
    }

    //mergeFiles cases

    /**
     * Verifies that attempting to run the paste command on a nonexistent file without flags results in a PasteException.
     */
    @Test
    void run_NonexistentFileNoFlag_ThrowsException() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertThrows(PasteException.class, () -> {
            pasteApplication.run(toArgs("", NON_EXISTENT_FILE), System.in, output);
        }, EXCEPTION_MSG);
    }

    /**
     * Tests that running the paste command on a directory without flags throws a PasteException, rather than displaying content.
     */
    @Test
    void run_DirectoryNoFlag_DisplaysEmpty() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertEquals("", output.toString(StandardCharsets.UTF_8));
        assertThrows(PasteException.class, () -> {
            pasteApplication.run(toArgs("", DIR), System.in, output);
        }, EXCEPTION_MSG);
    }

    /**
     * Confirms that running the paste command on a single file without flags correctly displays the file's contents.
     */
    @Test
    void run_SingleFileNoFlag_DisplaysFileContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName = "fileA.txt";
        String text = TEST_LINE;
        createFile(fileName, text);
        pasteApplication.run(toArgs("", fileName), System.in, output);
        assertEquals((text + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Tests that the paste command with the serial flag ('s') on a single file properly displays its contents in non-parallel format.
     */
    @Test
    void run_SingleFileFlag_DisplaysNonParallelFileContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName = "fileB.txt";
        createFile(fileName, TEST_LINE);
        pasteApplication.run(toArgs("s", fileName), System.in, output);
        assertEquals((EXPECTED_TEXT + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Ensures that running the paste command on an empty file without flags results in no output.
     */
    @Test
    void run_SingleEmptyFileNoFlag_DisplaysEmpty() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName = "fileC.txt";
        String text = "";
        createFile(fileName, text);
        pasteApplication.run(toArgs("", fileName), System.in, output);
        assertEquals(text, output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Verifies that running the paste command with an unknown flag on a single file throws a PasteException.
     */
    @Test
    void run_SingleFileUnknownFlag_Throws() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName = "fileE.txt";
        createFile(fileName, TEST_LINE);
        assertThrows(PasteException.class, () -> pasteApplication.run(toArgs("a", fileName), System.in, output));
    }

    /**
     * Tests that running the paste command on multiple files without flags correctly merges and displays their contents in parallel.
     */
    @Test
    void run_MultipleFilesNoFlag_DisplaysMergedFileContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName1 = "fileF.txt";
        String fileName2 = "fileG.txt";
        String expectedText = "Test line 1.1\tTest line 2.1" + LINE_SEPARATOR +
                "Test line 1.2\tTest line 2.2" + LINE_SEPARATOR +
                TEST_LINE_1_3;
        createFile(fileName1, TEXT_1);
        createFile(fileName2, TEXT_2);
        pasteApplication.run(toArgs("", fileName1, fileName2), System.in, output);
        assertEquals((expectedText + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Confirms that the paste command with the serial flag ('s') on multiple files displays their contents in a serial, non-parallel manner.
     */
    @Test
    void run_MultipleFilesFlag_DisplaysNonParallelMergedFileContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName1 = "fileH.txt";
        String fileName2 = "fileI.txt";
        String expectedText = "Test line 1.1\tTest line 1.2\tTest line 1.3" + LINE_SEPARATOR +
                "Test line 2.1\tTest line 2.2";
        createFile(fileName1, TEXT_1);
        createFile(fileName2, TEXT_2);
        pasteApplication.run(toArgs("s", fileName1, fileName2), System.in, output);
        assertEquals((expectedText + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Ensures that running the paste command on multiple empty files without flags results in an empty output.
     */
    @Test
    void run_MultipleEmptyFilesNoFlag_DisplaysEmpty() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName1 = "fileJ.txt";
        String fileName2 = "fileK.txt";
        String text = "";
        createFile(fileName1, text);
        createFile(fileName2, text);
        pasteApplication.run(toArgs("", fileName1, fileName2), System.in, output);
        assertEquals(text, output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Verifies that running the paste command with the serial flag ('s') on multiple empty files produces an empty output.
     */
    @Test
    void run_MultipleEmptyFilesFlag_DisplaysEmpty() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName1 = "fileL.txt";
        String fileName2 = "fileM.txt";
        String text = "";
        createFile(fileName1, text);
        createFile(fileName2, text);
        pasteApplication.run(toArgs("s", fileName1, fileName2), System.in, output);
        assertEquals(text, output.toString(StandardCharsets.UTF_8));
    }

    //mergeFilesAndStdin cases

    /**
     * Checks that running the paste command with standard input and a nonexistent file without flags throws a PasteException.
     */
    @Test
    void run_SingleStdinNonexistentFileNoFlag_ThrowsException() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String stdinText = TEST_LINE_1_1 + LINE_SEPARATOR +
                TEST_LINE_1_2 + LINE_SEPARATOR +
                TEST_LINE_1_3;
        InputStream inputStream = new ByteArrayInputStream(stdinText.getBytes(StandardCharsets.UTF_8));

        assertThrows(PasteException.class, () -> {
            pasteApplication.run(toArgs("", NON_EXISTENT_FILE), inputStream, output);
        }, EXCEPTION_MSG);
    }

    /**
     * Ensures that running the paste command with standard input and a directory as an argument throws a PasteException.
     */
    @Test
    void run_SingleStdinDirectoryNoFlag_ThrowsPasteException() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String stdinText = TEST_LINE_1_1 + LINE_SEPARATOR +
                TEST_LINE_1_2 + LINE_SEPARATOR +
                TEST_LINE_1_3;
        InputStream inputStream = new ByteArrayInputStream(stdinText.getBytes(StandardCharsets.UTF_8));

        assertThrows(PasteException.class, () -> {
            pasteApplication.run(toArgs("", DIR, "-"), inputStream, output);
        }, EXCEPTION_MSG);
    }

    /**
     * Tests that running the paste command with standard input ("-") and a single file without flags correctly merges and displays their contents.
     */
    @Test
    void run_SingleStdinDashSingleFileNoFlag_DisplaysMergedStdinFileContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String stdinText = TEST_LINE_1_1 + LINE_SEPARATOR +
                TEST_LINE_1_2 + LINE_SEPARATOR +
                TEST_LINE_1_3;
        InputStream inputStream = new ByteArrayInputStream(stdinText.getBytes(StandardCharsets.UTF_8));
        String fileName = "fileN.txt";
        String fileText = "Test line 2.1" + LINE_SEPARATOR +
                "Test line 2.2";
        createFile(fileName, fileText);
        String expectedText = "Test line 1.1\tTest line 2.1" + LINE_SEPARATOR +
                "Test line 1.2\tTest line 2.2" + LINE_SEPARATOR +
                TEST_LINE_1_3;
        pasteApplication.run(toArgs("", "-", fileName), inputStream, output);
        assertEquals((expectedText + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Confirms that running the paste command with a single file followed by standard input ("-") without flags properly merges and displays their contents in sequence.
     */
    @Test
    void run_SingleFileSingleStdinDashNoFlag_DisplaysNonParallelMergedFileStdinContents() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileText = TEST_LINE_1_1 + LINE_SEPARATOR +
                TEST_LINE_1_2 + LINE_SEPARATOR +
                TEST_LINE_1_3;
        String fileName = "fileO.txt";
        createFile(fileName, fileText);
        String stdinText = "Test line 2.1" + LINE_SEPARATOR +
                "Test line 2.2";
        InputStream inputStream = new ByteArrayInputStream(stdinText.getBytes(StandardCharsets.UTF_8));
        String expectedText = "Test line 1.1\tTest line 2.1" + LINE_SEPARATOR +
                "Test line 1.2\tTest line 2.2" + LINE_SEPARATOR +
                TEST_LINE_1_3;
        pasteApplication.run(toArgs("", fileName, "-"), inputStream, output);
        assertEquals((expectedText + STRING_NEWLINE), output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Verifies that attempting to merge content from a non-existent file using the mergeFile method throws a PasteException.
     */
    @Test
    void mergeFile_FileNotFound_ThrowsException() throws PasteException {
        assertThrows(PasteException.class, () -> pasteApplication.mergeFile(true, NONEXISTENT.toString()));
    }

    /**
     * Tests that the mergeFile method throws a PasteException when attempting to merge from a directory instead of a file.
     */
    @Test
    void mergeFile_FileIsDirectory_ThrowsException() throws PasteException {
        assertThrows(PasteException.class, () -> pasteApplication.mergeFile(true, DIRECTORY.toString()));
    }

    /**
     * Ensures that providing a null InputStream to mergeFileAndStdin results in a PasteException.
     */
    @Test
    void mergeFileAndStdin_NullInputStream_ThrowsException() {
        assertThrows(PasteException.class, () -> pasteApplication.mergeFileAndStdin(true, null));
    }

    /**
     * Tests that mergeFileAndStdin throws a PasteException when attempting to merge content from a non-existent file.
     */
    @Test
    void mergeFileAndStdin_NonexistentFile_ThrowsException() {
        // Prepare stdin content
        String stdinContent = "s\nt\nd\ni\nn";
        InputStream stdin = new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8));

        // Non-existent file name

        // Assert that the method throws a PasteException for the non-existent file
        assertThrows(PasteException.class, () -> {
            pasteApplication.mergeFileAndStdin(false, stdin, NON_EXISTENT_FILE);
        }, "Expected mergeFileAndStdin to throw PasteException when a file does not exist.");
    }

    /**
     * Confirms that mergeFileAndStdin throws a PasteException when provided with a null OutputStream.
     */
    @Test
    void mergeFileAndStdin_NullOutputStream_ThrowsException() {
        assertThrows(PasteException.class, () -> pasteApplication.mergeFileAndStdin(true, System.in, null));
    }

    /**
     * Tests that mergeFileAndStdin throws a PasteException when attempting to merge with a null filename argument.
     */
    @Test
    void mergeFileAndStdin_NullFilename_ThrowsException() {
        assertThrows(PasteException.class, () -> pasteApplication.mergeFileAndStdin(true, System.in, null));
    }

    /**
     * Verifies that mergeFileAndStdin correctly merges content from one file and standard input into a single string.
     */
    @Test
    void mergeFileAndStdin_OneFileOneStdin_MergesBoth() throws PasteException {
        String stdinContent = "s\nt\nd\ni\nn";
        InputStream stdin = new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8));
        String expected = "A\ts\nB\tt\nC\td\nD\ti\nE\tn";
        String result = pasteApplication.mergeFileAndStdin(false, stdin, FILE_1.toString(), "-");
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Tests that mergeFileAndStdin correctly merges content from one file and standard input serially when isSerial is set to true.
     */
    @Test
    void mergeFileAndStdin_OneFileOneStdinIsSerialTrue_MergesSerially() throws PasteException {
        String stdinContent = "s\nt\nd\ni\nn";
        InputStream stdin = new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8));
        String expected = "A\tB\tC\tD\tE\ns\tt\td\ti\tn";
        String result = pasteApplication.mergeFileAndStdin(true, stdin, FILE_1.toString(), "-");
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Validates that mergeFileAndStdin can successfully merge content from two files and standard input into a single output.
     */
    @Test
    void mergeFileAndStdin_TwoFilesOneStdin_MergesBoth() throws PasteException {
        String stdinContent = String.join(System.lineSeparator(),
                "s", "t", "d", "i", "n");
        InputStream stdin = new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8));
        String expected = String.join(System.lineSeparator(),
                "A\t1\ts", "B\t2\tt", "C\t3\td", "D\t4\ti", "E\t5\tn");
        String result = pasteApplication.mergeFileAndStdin(false, stdin, FILE_1.toString(), FILE_2.toString(), "-");
        assertEquals(expected, result);
    }

    /**
     * Tests that mergeFileAndStdin serially merges content from two files and standard input with isSerial set to true.
     */
    @Test
    void mergeFileAndStdin_TwoFilesOneStdinIsSerialTrue_MergesSerially() throws PasteException {
        String stdinContent = String.join(System.lineSeparator(),
                "s", "t", "d", "i", "n");
        InputStream stdin = new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8));
        String expected = String.join(System.lineSeparator(),
                ABCDE_TAB, "1\t2\t3\t4\t5", "s\tt\td\ti\tn");
        String result = pasteApplication.mergeFileAndStdin(true, stdin, FILE_1.toString(), FILE_2.toString(), "-");
        assertEquals(expected, result);
    }

    /**
     * Checks the functionality of mergeFileAndStdin for serially merging content from three files and standard input when isSerial is true.
     */
    @Test
    void mergeFileAndStdin_ThreeFilesOneStdinIsSerialTrue_MergesAll() throws PasteException {
        String stdinContent = String.join(System.lineSeparator(),
                "s", "t", "d", "i", "n");
        InputStream stdin = new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8));
        String expected = String.join(System.lineSeparator(),
                ABCDE_TAB, "1\t2\t3\t4\t5", "s\tt\td\ti\tn", "!\t@\t#\t$");
        String result = pasteApplication.mergeFileAndStdin(true, stdin, FILE_1.toString(), FILE_2.toString(), "-", FILE_4.toString());
        assertEquals(expected, result);
    }

    /**
     * Confirms mergeFileAndStdin can merge content from two files of different sizes with standard input, displaying aligned rows.
     */
    @Test
    void mergeFileAndStdin_TwoFilesDifferentSizeOneStdin_MergesBoth() throws PasteException {
        String stdinContent = String.join(System.lineSeparator(),
                "s", "t", "d", "i", "n");
        InputStream stdin = new ByteArrayInputStream(stdinContent.getBytes(StandardCharsets.UTF_8));
        String expected = String.join(System.lineSeparator(),
                "A\t!\ts", "B\t@\tt", "C\t#\td", "D\t$\ti", "E\t\tn");
        String result = pasteApplication.mergeFileAndStdin(false, stdin, FILE_1.toString(), FILE_4.toString(), "-");
        assertEquals(expected, result);
    }

    /**
     * Ensures that calling mergeStdin with a null InputStream throws a PasteException.
     */
    @Test
    void mergeStdin_NullStream_ThrowsException() {
        assertThrows(PasteException.class, () -> pasteApplication.mergeStdin(true, null));
    }

    /**
     * Verifies that mergeStdin without the serial flag returns the input stream's content as is.
     */
    @Test
    void mergeStdin_NoSerial_ReturnsItself() throws PasteException {
        InputStream stream = new ByteArrayInputStream(TEXT_FILE_1.getBytes());
        String result = pasteApplication.mergeStdin(false, stream);
        assertEquals(TEXT_FILE_1, result);
    }

    /**
     * Tests that mergeStdin with the serial flag replaces newlines with tabs in the input stream's content.
     */
    @Test
    void mergeStdin_Serial_ReturnsNewlinesReplacedByTabs() throws PasteException {
        InputStream stream = new ByteArrayInputStream(TEXT_FILE_1.getBytes());
        String result = pasteApplication.mergeStdin(true, stream);
        assertEquals(TEXT_FILE_1.replaceAll(STRING_NEWLINE, String.valueOf(CHAR_TAB)), result);
    }

    /**
     * Asserts that attempting to merge content from a file with a null filename parameter throws a PasteException.
     */
    @Test
    void mergeFile_NullFilename_ThrowsException() {
        assertThrows(PasteException.class, () -> pasteApplication.mergeFile(true, null));
    }

    /**
     * Tests that mergeFile throws a PasteException when attempting to merge from a nonexistent file.
     */
    @Test
    void mergeFile_NonexistentFile_ThrowsException() {
        assertThrows(PasteException.class, () -> {
            pasteApplication.mergeFile(false, NON_EXISTENT_FILE);
        }, "Expected mergeFile to throw PasteException when file does not exist.");
    }

    /**
     * Validates that merging content from one file without the serial flag correctly returns the file's content unchanged.
     */
    @Test
    void mergeFile_NoSerialOneFile_ReturnsItself() throws PasteException {
        String result = pasteApplication.mergeFile(false, FILE_1.toString());
        assertEqualsReplacingNewlines(TEXT_FILE_1, result);
    }

    /**
     * Checks that merging content from two files without the serial flag results in their contents being interleaved.
     */
    @Test
    void mergeFile_NoSerialTwoFiles_ReturnsInterleaving() throws PasteException {
        String expected = "A\t1\nB\t2\nC\t3\nD\t4\nE\t5";
        String result = pasteApplication.mergeFile(false, FILE_1.toString(), FILE_2.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Verifies that merging content from two files of different sizes without the serial flag interleaves their contents, handling size discrepancies.
     */
    @Test
    void mergeFile_NoSerialTwoFilesDiffSize_ReturnsInterleaving() throws PasteException {
        String expected = "A\t!\nB\t@\nC\t#\nD\t$\nE";
        String result = pasteApplication.mergeFile(false, FILE_1.toString(), FILE_4.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Tests that merging content from three files without the serial flag results in their contents being correctly interleaved.
     */
    @Test
    void mergeFile_NoSerialThreeFiles_ReturnsInterleaving() throws PasteException {
        String expected = "A\t1\ta" + System.lineSeparator() +
                "B\t2\tb" + System.lineSeparator() +
                "C\t3\tc" + System.lineSeparator() +
                "D\t4\td" + System.lineSeparator() +
                "E\t5\te";
        String result = pasteApplication.mergeFile(false, FILE_1.toString(), FILE_2.toString(), FILE_3.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Verifies merging content from three files of different sizes without the serial flag correctly interleaves their contents, accounting for varying lengths.
     */
    @Test
    void mergeFile_NoSerialThreeFilesDiffSize_ReturnsInterleaving() throws PasteException {
        String expected = "A\t!\t-" + System.lineSeparator() +
                "B\t@\t_" + System.lineSeparator() +
                "C\t#\t=" + System.lineSeparator() +
                "D\t$\t" + System.lineSeparator() +
                "E";
        String result = pasteApplication.mergeFile(false, FILE_1.toString(), FILE_4.toString(), FILE_5.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Confirms that merging content from two files and one null reference without the serial flag results in the non-null files' contents being interleaved as expected.
     */
    @Test
    void mergeFile_NoSerialTwoFilesOneNull_ReturnsInterleaving() throws PasteException {
        String expected = "A\t1\nB\t2\nC\t3\nD\t4\nE\t5";
        String result = pasteApplication.mergeFile(false, FILE_1.toString(), FILE_2.toString(), null);
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Tests that merging content with a null reference placed before two files without the serial flag still interleaves the contents of the non-null files correctly.
     */
    @Test
    void mergeFile_NoSerialTwoFilesOneNullDiffOrder_ReturnsInterleaving() throws PasteException {
        String expected = "A\t1\nB\t2\nC\t3\nD\t4\nE\t5";
        String result = pasteApplication.mergeFile(false, null, FILE_1.toString(), FILE_2.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Verifies that attempting to merge content from one file and one null reference without the serial flag results in the original file's content unchanged.
     */
    @Test
    void mergeFile_NoSerialOneFileOneNull_ReturnsException() throws PasteException {
        String result = pasteApplication.mergeFile(false, FILE_1.toString(), null);
        assertEqualsReplacingNewlines(TEXT_FILE_1, result);
    }

    /**
     * Confirms that merging content from a single file with the serial flag results in the file's content being returned as a single line, with elements separated by tabs.
     */
    @Test
    void mergeFile_SerialOneFile_ReturnsItself() throws PasteException {
        String expected = ABCDE_TAB;
        String result = pasteApplication.mergeFile(true, FILE_1.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Tests that merging content from two files with the serial flag results in their contents being output in parallel, each on its own line.
     */
    @Test
    void mergeFile_SerialTwoFiles_ReturnsParallel() throws PasteException {
        String expected = "A\tB\tC\tD\tE\n1\t2\t3\t4\t5";
        String result = pasteApplication.mergeFile(true, FILE_1.toString(), FILE_2.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Verifies that merging content from two files of different sizes with the serial flag outputs their contents in parallel lines, respecting the size difference.
     */
    @Test
    void mergeFile_SerialTwoFilesDiffSize_ReturnsParallel() throws PasteException {
        String expected = "A\tB\tC\tD\tE\n!\t@\t#\t$";
        String result = pasteApplication.mergeFile(true, FILE_1.toString(), FILE_4.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Confirms that merging content from three files with the serial flag correctly outputs their contents in parallel, each set on its respective line.
     */
    @Test
    void mergeFile_SerialThreeFiles_ReturnsParallel() throws PasteException {
        String expected = "A\tB\tC\tD\tE\n1\t2\t3\t4\t5\na\tb\tc\td\te";
        String result = pasteApplication.mergeFile(true, FILE_1.toString(), FILE_2.toString(), FILE_3.toString());
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Tests that merging content serially from one file and one null reference results in only the content from the existing file being returned.
     */
    @Test
    void mergeFile_SerialOneFileOneNull_Returns1File() throws PasteException {
        String expected = ABCDE_TAB;
        String result = pasteApplication.mergeFile(true, FILE_1.toString(), null);
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Verifies that merging content serially from two files and one null reference outputs the contents of the non-null files in parallel lines.
     */
    @Test
    void mergeFile_SerialTwoFilesOneNull_ReturnsFilesConcatenated() throws PasteException {
        String expected = "A\tB\tC\tD\tE\n1\t2\t3\t4\t5";
        String result = pasteApplication.mergeFile(true, FILE_1.toString(), FILE_2.toString(), null);
        assertEqualsReplacingNewlines(expected, result);
    }

    /**
     * Test case for the mergeFile method when merging a single file in serial mode.
     * It verifies that the expected output is equal to the result obtained from
     * merging the file.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    void mergeFile_SerialSingleFile_OutputAsExpected() throws Exception {
        String expectedOutput = String.join("\t", "A", "B", "C", "D");
        String result = pasteApplication.mergeFile(true, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for merging multiple files in serial order.
     * It verifies that the merged output matches the expected output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void mergeFile_SerialMultipleFiles_OutputConcatenatedByLines() throws Exception {
        String expectedOutput = String.join("\t", "A", "B", "C", "D") + System.lineSeparator() +
                String.join("\t", "1", "2", "3", "4");
        String result = pasteApplication.mergeFile(true, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for merging a single file in parallel mode.
     * It verifies that the output of the merge operation matches the expected
     * output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void mergeFile_ParallelSingleFile_OutputAsExpected() throws Exception {
        String expectedOutput = String.join(System.lineSeparator(), "A", "B", "C", "D");
        String result = pasteApplication.mergeFile(false, tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for merging multiple files in parallel.
     * It verifies that the output of the merge operation matches the expected
     * output.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void mergeFile_ParallelMultipleFiles_OutputTabSeparated() throws Exception {
        String expectedOutput = "A\t1" + System.lineSeparator() + "B\t2" + System.lineSeparator() +
                "C\t3" + System.lineSeparator() + "D\t4";
        String result = pasteApplication.mergeFile(false, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the mergeStdin_Serial method.
     * It tests the functionality of merging the standard input in a serial manner.
     * The method takes the standard input as a ByteArrayInputStream and merges it
     * into a single string.
     * The expected output is the same as the input string, with each line separated
     * by the system line separator.
     * The method returns the merged string.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    void mergeStdin_Serial_InputEchoedBack() throws Exception {
        String input = String.join(System.lineSeparator(), "A", "B", "C", "D");
        InputStream stdin = new ByteArrayInputStream(input.getBytes());
        String expectedOutput = String.join("\t", "A", "B", "C", "D");
        String result = pasteApplication.mergeStdin(true, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for the mergeStdin_Parallel method.
     * This method tests the functionality of merging the standard input in parallel
     * mode.
     * It creates a string input with lines "A", "B", "C", "D" and converts it into
     * an InputStream.
     * The expected output is the same as the input string.
     * The method calls the mergeStdin method with parallel mode set to false and
     * compares the result with the expected output.
     * If the result matches the expected output, the test case passes.
     *
     * @throws Exception if an error occurs during the test case execution
     */
    @Test
    void mergeStdin_Parallel_InputEchoedBack() throws Exception {
        String input = String.join(System.lineSeparator(), "A", "B", "C", "D");
        InputStream stdin = new ByteArrayInputStream(input.getBytes());
        String expectedOutput = String.join(System.lineSeparator(), "A", "B", "C", "D");
        String result = pasteApplication.mergeStdin(false, stdin);
        assertEquals(expectedOutput, result);
    }

    @Test
    void mergeStdin_SerialMultiNumbers_InputEchoedBack() throws Exception {
        String input = String.join(System.lineSeparator(), "A 1", "B 2", "C 3", "D 4");
        InputStream stdin = new ByteArrayInputStream(input.getBytes());
        String expectedOutput = String.join("\t", "A 1", "B 2", "C 3", "D 4");
        String result = pasteApplication.mergeStdin(true, stdin);
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for merging a file and stdin in serial mode.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void mergeFileAndStdin_Serial_InputAndFileContentMerged() throws Exception {
        String input = String.join(System.lineSeparator(), "1", "2", "3", "4");
        InputStream stdin = new ByteArrayInputStream(input.getBytes());
        String expectedOutput = String.join("\t", "1", "2", "3", "4") + System.lineSeparator() +
                String.join("\t", "A", "B", "C", "D");
        String result = pasteApplication.mergeFileAndStdin(true, stdin, "-", tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }

    /**
     * Test case for merging the contents of a file and standard input in parallel.
     * It creates a test input string, sets up a ByteArrayInputStream with the input
     * string,
     * and defines the expected output string. Then, it calls the mergeFileAndStdin
     * method
     * with the parallel flag set to false, the input stream, and the absolute path
     * of the
     * temporary file. Finally, it asserts that the result of the method call
     * matches the
     * expected output string.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    void mergeFileAndStdin_Parallel_MergeSuccessfully() throws Exception {
        String input = String.join(System.lineSeparator(), "1", "2", "3", "4");
        InputStream stdin = new ByteArrayInputStream(input.getBytes());
        String expectedOutput = "1\tA" + System.lineSeparator() + "2\tB" + System.lineSeparator() +
                "3\tC" + System.lineSeparator() + "4\tD";
        String result = pasteApplication.mergeFileAndStdin(false, stdin, "-", tempFile1.getAbsolutePath());
        assertEquals(expectedOutput, result);
    }
}

