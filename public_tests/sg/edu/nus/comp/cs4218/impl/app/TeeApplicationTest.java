package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the TeeApplication class.
 */
class TeeApplicationTest {

    private TeeApplication teeApp;
    private ByteArrayOutputStream stdoutMock;

    private File tempFile1;
    private File tempFile2;
    public final static String TEST_LINE_1 = "Test line 1";
    public final static String TEST_LINE_2 = "Test line 2";
    public final static String TEST_LINE_3 = "Test line 3";
    public final static String LINE_1 = "Line 1";
    public final static String LINE_2 = "Line 2";


    String inputContent = TEST_LINE_1 + System.lineSeparator() + TEST_LINE_2 + System.lineSeparator() +
            TEST_LINE_3;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        teeApp = new TeeApplication();
        stdoutMock = new ByteArrayOutputStream();

        tempFile1 = File.createTempFile("testTee1", ".txt");
        Files.write(tempFile1.toPath(), List.of(LINE_1, LINE_2), StandardCharsets.UTF_8);

        tempFile2 = File.createTempFile("testTee2", ".txt");
        Files.write(tempFile2.toPath(), List.of("Line 3", "Line 4"), StandardCharsets.UTF_8);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile1.toPath());
        Files.deleteIfExists(tempFile2.toPath());
    }

    /**
     * This test verifies that the run method of the TeeApplication class
     * throws a TeeException when the stdout parameter is null.
     */
    @Test
    public void run_stdoutNull_throwsException() throws IOException {
        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = null; //NOPMD - suppressed CloseResource - stdout is null
        String[] args = {"-a", "testing.txt"};

        assertThrows(TeeException.class, () -> teeApp.run(args, stdin, stdout));
        stdin.close();
    }

    /**
     * This method tests the run method of the TeeApplication class and verifies
     * that it throws a TeeException
     * when the stdin parameter is null.
     */
    @Test
    public void run_stdinNull_throwsException() throws IOException {
        InputStream stdin = null; //NOPMD - suppressed CloseResource - stdin is null
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = {"-a", "testing.txt"};

        assertThrows(TeeException.class, () -> teeApp.run(args, stdin, stdout));
    }

    /**
     * Test case to verify that a TeeException is thrown when invalid arguments are
     * provided to the run method.
     */
    @Test
    public void run_InvalidArgs_ThrowsTeeException() {
        assertThrows(TeeException.class, () -> {
            teeApp.run(new String[]{"-invalid"}, new ByteArrayInputStream("test".getBytes()),
                    new ByteArrayOutputStream());
        });
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the run method of the
     *                      TeeApplication class.
     */
    @Test
    public void run_NoFiles_ContentToStdout() throws TeeException {
        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = {"-a"};

        teeApp.run(args, stdin, stdout);

        assertEquals(inputContent + System.lineSeparator(), stdout.toString());
    }

    @Test
    public void runWithoutAppend_NoFiles_ContentToStdout() throws TeeException {
        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = {};

        teeApp.run(args, stdin, stdout);

        assertEquals(inputContent + System.lineSeparator(), stdout.toString());
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the run method of the
     *                      TeeApplication class.
     */
    @Test
    public void run_WithFlagOneFile_ContentAppendedToFile() throws TeeException, IOException {
        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = {"-a", tempFile1.getAbsolutePath()};

        teeApp.run(args, stdin, stdout);

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList(LINE_1, LINE_2, TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the run method of the
     *                      TeeApplication class.
     */
    @Test
    public void run_WithoutFlagOneFile_ContentReplacedInFile() throws TeeException, IOException {
        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = {tempFile1.getAbsolutePath()};

        PrintStream originalOut = System.out; //NOPMD - suppressed CloseResource - It's being closed later
        PrintStream printStream = new PrintStream(stdout); //NOPMD - suppressed CloseResource - It's being closed later
        System.setOut(printStream);
        teeApp.run(args, stdin, stdout);
        System.setOut(originalOut);
        printStream.close();

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList(TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent);
        assertEquals(inputContent + System.lineSeparator(), stdout.toString());
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the run method of the
     *                      TeeApplication class.
     */
    @Test
    public void run_WithFlagTwoFiles_ContentToBothFiles() throws IOException, TeeException {
        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = {"-a", tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath()};
        PrintStream originalOut = System.out; //NOPMD - suppressed CloseResource - It's being closed later
        PrintStream printStream = new PrintStream(stdout); //NOPMD - suppressed CloseResource - It's being closed later
        System.setOut(printStream);
        teeApp.run(args, stdin, stdout);
        System.setOut(originalOut);
        printStream.close();

        List<String> fileContent1 = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));
        List<String> fileContent2 = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent1 = Arrays.asList(LINE_1, LINE_2, TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);
        List<String> expectedContent2 = Arrays.asList("Line 3", "Line 4", TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent1, fileContent1);
        assertLinesMatch(expectedContent2, fileContent2);

        assertEquals(inputContent + System.lineSeparator(), stdout.toString());
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the run method of the
     *                      TeeApplication class.
     */
    @Test
    public void run_WithoutFlagTwoFiles_ContentToBothFiles() throws IOException, TeeException {
        InputStream stdin = new ByteArrayInputStream(inputContent.getBytes());
        OutputStream stdout = new ByteArrayOutputStream();
        String[] args = {tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath()};

        PrintStream originalOut = System.out; //NOPMD - suppressed CloseResource - It's being closed later
        PrintStream printStream = new PrintStream(stdout); //NOPMD - suppressed CloseResource - It's being closed later
        System.setOut(printStream);
        teeApp.run(args, stdin, stdout);
        System.setOut(originalOut);
        printStream.close();

        List<String> fileContent1 = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));
        List<String> fileContent2 = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList(TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent1);
        assertLinesMatch(expectedContent, fileContent2);

        assertEquals(inputContent + System.lineSeparator(), stdout.toString());
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the teeFromStdin method of the
     *                      TeeApplication class.
     * @throws IOException  When an error occurs in reading I/O
     */
    @Test
    public void teeFromStdin_IsAppendTrueOneFile_ContentAppendedToFile() throws TeeException, IOException {
        InputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());

        System.setOut(new PrintStream(stdoutMock));
        teeApp.teeFromStdin(true, inputStream, tempFile1.getAbsolutePath());
        System.setOut(System.out);

        String stdOutput = stdoutMock.toString();

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList(LINE_1, LINE_2, TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the teeFromStdin method of the
     *                      TeeApplication class.
     * @throws IOException  When an error occurs in reading I/O
     */
    @Test
    public void teeFromStdin_IsAppendTrueTwoDifferentFiles_ContentAppendedToBothFiles()
            throws TeeException, IOException {
        InputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());

        System.setOut(new PrintStream(stdoutMock));
        teeApp.teeFromStdin(true, inputStream, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        System.setOut(System.out);

        List<String> fileContent1 = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));
        List<String> fileContent2 = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        String stdOutput = stdoutMock.toString();

        List<String> expectedContent1 = Arrays.asList(LINE_1, LINE_2, TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);
        List<String> expectedContent2 = Arrays.asList("Line 3", "Line 4", TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent1, fileContent1);
        assertLinesMatch(expectedContent2, fileContent2);
    }

    /**
     * Test case to verify that content from the standard input stream is written to
     * the standard output stream
     * when no files are specified in the arguments.
     *
     * @throws TeeException When an error occurs in the teeFromStdin method of the
     *                      TeeApplication class.
     * @throws IOException  When an error occurs in reading I/O
     */
    @Test
    public void teeFromStdin_IsAppendTrueTwoSameFiles_ContentAppendedToBothFiles() throws TeeException, IOException {
        InputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());

        System.setOut(new PrintStream(stdoutMock));
        teeApp.teeFromStdin(true, inputStream, tempFile1.getAbsolutePath(), tempFile1.getAbsolutePath());
        System.setOut(System.out);

        List<String> fileContent1 = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));

        String stdOutput = stdoutMock.toString();

        List<String> expectedContent = Arrays.asList(LINE_1, LINE_2, TEST_LINE_1, TEST_LINE_2, TEST_LINE_3,
                TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent1);
    }

    /**
     * Test case to verify that content read from the standard input stream is
     * written to the specified file,
     * and the same content is also printed to the standard output stream when
     * append mode is false
     * and only one file is specified, and the content is overwritten in the file.
     */
    @Test
    public void teeFromStdin_IsAppendFalseOneFile_ContentOverwrittenInFile() throws TeeException, IOException {
        InputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());

        System.setOut(new PrintStream(stdoutMock));
        teeApp.teeFromStdin(false, inputStream, tempFile1.getAbsolutePath());
        System.setOut(System.out);

        String stdOutput = stdoutMock.toString();

        List<String> fileContent = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));

        List<String> expectedContent = Arrays.asList(TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent);
    }

    /**
     * Test case to verify the behavior of the `teeFromStdin` method when `isAppend`
     * is set to false,
     * and two different files are provided as output destinations. This test
     * ensures that the content
     * is overwritten in both files and the standard output matches the input
     * content.
     *
     * @throws TeeException If an error occurs while executing the `teeFromStdin`
     *                      method.
     * @throws IOException  If an I/O error occurs while reading the file content.
     */
    @Test
    public void teeFromStdin_IsAppendFalseTwoDifferentFiles_ContentOverwrittenInBothFiles()
            throws TeeException, IOException {
        InputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());

        System.setOut(new PrintStream(stdoutMock));
        teeApp.teeFromStdin(false, inputStream, tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath());
        System.setOut(System.out);

        List<String> fileContent1 = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));
        List<String> fileContent2 = Files.readAllLines(Paths.get(tempFile2.getAbsolutePath()));

        String stdOutput = stdoutMock.toString();

        List<String> expectedContent = Arrays.asList(TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent1);
        assertLinesMatch(expectedContent, fileContent2);
    }

    /**
     * Test case for the `teeFromStdin` method when `isAppend` is false and two same
     * files are provided.
     * The content should be overwritten in both files.
     *
     * @throws TeeException If an error occurs while executing the `teeFromStdin`
     *                      method.
     * @throws IOException  If an I/O error occurs.
     */
    @Test
    public void teeFromStdin_IsAppendFalseTwoSameFiles_ContentOverwrittenInBothFiles()
            throws TeeException, IOException {
        InputStream inputStream = new ByteArrayInputStream(inputContent.getBytes());

        System.setOut(new PrintStream(stdoutMock));
        teeApp.teeFromStdin(false, inputStream, tempFile1.getAbsolutePath(), tempFile1.getAbsolutePath());
        System.setOut(System.out);

        List<String> fileContent1 = Files.readAllLines(Paths.get(tempFile1.getAbsolutePath()));

        String stdOutput = stdoutMock.toString();

        List<String> expectedContent = Arrays.asList(TEST_LINE_1, TEST_LINE_2, TEST_LINE_3);

        assertLinesMatch(expectedContent, fileContent1);
    }

    /**
     * Test case to verify that a TeeException is thrown when the input stream is
     * null.
     */
    @Test
    public void teeFromStdin_NullInputStream_ExceptionThrown() {
        assertThrows(TeeException.class, () -> {
            teeApp.teeFromStdin(true, null, tempFile1.getAbsolutePath());
        });
    }
}