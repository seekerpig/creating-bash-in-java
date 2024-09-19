package sg.edu.nus.comp.cs4218.impl.integration.pipe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class TeeApplicationIT { // NOPMD
    private ShellImpl shell;
    private ByteArrayOutputStream outputStream;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String TEST_DIR = Paths.get(TEMP_DIR, "teeIntegrationTest").toString();
    private static final String FIRST_FILE = "first.txt";
    private static final String SECOND_FILE = "second.txt";
    private static final String INITIAL_1 = "Initial1";
    private static final String INITIAL_2 = "Initial2";
    private static final String APPENDED = "Appended";
    private static final String CONTENT = "content.txt";
    private static final String INITIAL_CONTENT = "InitialContent";
    private static final String APPENDED_CONTENT = "AppendedContent";

    private static final File TEST_DIRECTORY = new File(TEST_DIR);

    @BeforeEach
    void setUp() throws IOException {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
        if (!TEST_DIRECTORY.exists()) {
            boolean isCreated = TEST_DIRECTORY.mkdir();
            if (!isCreated) {
                throw new IOException("Failed to create test directory");
            }
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test directory and its content
        File[] files = TEST_DIRECTORY.listFiles();
        if (files != null) {
            for (File file : files) {
                Files.deleteIfExists(file.toPath());
            }
        }
        Files.deleteIfExists(TEST_DIRECTORY.toPath());
    }

    /**
     * Confirms that appending content to two files with `tee -a` is followed by correct parallel merging using `paste`.
     */
    @Test
    void teeAppendThenPaste_TwoFiles_AppendedContentMergedCorrectly() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();

        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(firstFile)) {
            fileWriter.write(INITIAL_1 + lineSeparator);
        }
        try (FileWriter fileWriter = new FileWriter(secondFile)) {
            fileWriter.write(INITIAL_2 + lineSeparator);
        }

        String teeCommand = String.format("echo \"%s\" | tee -a %s %s | paste %s %s", APPENDED, firstFile, secondFile, firstFile, secondFile);
        shell.parseAndEvaluate(teeCommand, outputStream);
        String expectedOutput = "Initial1\tInitial2" + lineSeparator + "Appended\tAppended" + lineSeparator;
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Tests overwriting content in two files with `tee`, then verifies `paste` correctly merges the new content from both files.
     */
    @Test
    void teeOverwriteThenPaste_TwoFiles_RewriteContentMergedCorrectly() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();

        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(firstFile)) {
            fileWriter.write(INITIAL_1 + lineSeparator);
        }
        try (FileWriter fileWriter = new FileWriter(secondFile)) {
            fileWriter.write(INITIAL_2 + lineSeparator);
        }

        String teeCommand = String.format("echo \"%s\" | tee %s %s | paste %s %s", APPENDED, firstFile, secondFile, firstFile, secondFile);
        shell.parseAndEvaluate(teeCommand, outputStream);
        String expectedOutput = "Appended\tAppended" + lineSeparator;
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Tests overwriting content in a file using `tee`, followed by merging this new content from the file and stdin with `paste`.
     */
    @Test
    void teeOverwriteThenPaste_StdinAndFile_MergedContentDisplayedCorrectly() throws Exception {
        String targetFile = Paths.get(TEST_DIR, CONTENT).toString();
        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(INITIAL_CONTENT + lineSeparator);
        }

        String teePasteCommand = String.format("echo \"%s\" | tee %s | paste %s -", APPENDED_CONTENT, targetFile, targetFile);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate(teePasteCommand, outputStream);

        String expectedOutput = String.format("%s\t%s", APPENDED_CONTENT, APPENDED_CONTENT);
        assertEquals(expectedOutput + lineSeparator, outputStream.toString());
    }

    /**
     * Tests appending content with `tee -a` to a file, then using `paste` to merge this content in parallel from the file and stdin.
     */
    @Test
    void teeAppendThenPaste_StdinAndFile_MergedContentDisplayedCorrectly() throws Exception {
        String targetFile = Paths.get(TEST_DIR, CONTENT).toString();
        String initialContent = INITIAL_CONTENT;
        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(initialContent + lineSeparator);
        }

        String teePasteCommand = String.format("echo \"%s\" | tee -a %s | paste %s -", APPENDED_CONTENT, targetFile, targetFile);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate(teePasteCommand, outputStream);

        String expectedOutput = String.format("%s\t%s%s%s", initialContent, APPENDED_CONTENT, lineSeparator, APPENDED_CONTENT);
        assertEquals(expectedOutput + lineSeparator, outputStream.toString());
    }

    /**
     * Validates appending content to two files with `tee -a`, then checks if `paste -s` correctly merges the updated content.
     */
    @Test
    void teeAppendThenPasteSerial_TwoFiles_AppendedContentMergedCorrectly() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();

        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(firstFile)) {
            fileWriter.write(INITIAL_1 + lineSeparator);
        }
        try (FileWriter fileWriter = new FileWriter(secondFile)) {
            fileWriter.write(INITIAL_2 + lineSeparator);
        }

        String teeCommand = String.format("echo \"%s\" | tee -a %s %s | paste -s %s %s", APPENDED, firstFile, secondFile, firstFile, secondFile);
        shell.parseAndEvaluate(teeCommand, outputStream);
        String expectedOutput = "Initial1\tAppended" + lineSeparator + "Initial2\tAppended" + lineSeparator;
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Tests simultaneously overwriting two files with `tee`, then merging their contents serially with `paste -s`.
     */
    @Test
    void teeOverwriteThenPasteSerial_TwoFiles_RewriteContentMergedCorrectly() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();

        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(firstFile)) {
            fileWriter.write(INITIAL_1 + lineSeparator);
        }
        try (FileWriter fileWriter = new FileWriter(secondFile)) {
            fileWriter.write(INITIAL_2 + lineSeparator);
        }

        String teeCommand = String.format("echo \"%s\" | tee %s %s | paste -s %s %s", APPENDED, firstFile, secondFile, firstFile, secondFile);
        shell.parseAndEvaluate(teeCommand, outputStream);
        String expectedOutput = APPENDED + System.lineSeparator() + APPENDED + lineSeparator;
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Tests overwriting a file with `tee`, then serially merging the new content with `paste -s`, verifying the outcome.
     */
    @Test
    void teeOverwriteThenPasteSerial_StdinAndFile_MergedContentDisplayedCorrectly() throws Exception {
        String targetFile = Paths.get(TEST_DIR, CONTENT).toString();
        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(INITIAL_CONTENT + lineSeparator);
        }

        String appendedText = APPENDED_CONTENT;

        String teePasteCommand = String.format("echo \"%s\" | tee %s | paste -s %s -", appendedText, targetFile, targetFile);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate(teePasteCommand, outputStream);

        String expectedOutput = String.format("%s%s%s", appendedText, lineSeparator, appendedText);
        assertEquals(expectedOutput + lineSeparator, outputStream.toString());
    }

    /**
     * Tests appending content to a file with `tee -a`, then merging it serially with `paste -s`, ensuring correct output.
     */
    @Test
    void teeAppendThenPasteSerial_StdinAndFile_MergedContentDisplayedCorrectly() throws Exception {
        String targetFile = Paths.get(TEST_DIR, CONTENT).toString();
        String initialContent = INITIAL_CONTENT;
        String lineSeparator = System.lineSeparator();
        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(initialContent + lineSeparator);
        }

        String appendedText = APPENDED_CONTENT;

        String teePasteCommand = String.format("echo \"%s\" | tee -a %s | paste -s %s -", appendedText, targetFile, targetFile);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate(teePasteCommand, outputStream);

        String expectedOutput = String.format("%s\t%s%s%s", initialContent, appendedText, lineSeparator, appendedText);
        assertEquals(expectedOutput + lineSeparator, outputStream.toString());
    }

    /**
     * Test for tee followed by paste with invalid file
     */
    @Test
    void teeCommand_WithInvalidFilePath_ThrowsException() {
        String invalidFilePath = Paths.get(TEST_DIR, "nonexistent", "file.txt").toString();
        String inputText = "Sample text";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String teeCommand = String.format("echo \"%s\" | tee %s | paste -", inputText, invalidFilePath);

        Exception exception = assertThrows(TeeException.class, () -> {
            shell.parseAndEvaluate(teeCommand, outputStream);
        });

        String expectedMessage = "tee: tee: Failed to read from stdin or write to file: " + invalidFilePath;
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    /**
     * Test for tee followed by paste with valid option
     */
    @Test
    void pasteCommand_WithNonExistentFile_ThrowsException() {
        String nonExistentFile = Paths.get(TEST_DIR, "thisFileDoesNotExist.txt").toString();
        String targetFile = Paths.get(TEST_DIR, CONTENT).toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String inputText = "Sample text";

        String pasteCommand = String.format("echo \"%s\" | tee %s | paste %s", inputText, targetFile, nonExistentFile);

        Exception exception = assertThrows(PasteException.class, () -> {
            shell.parseAndEvaluate(pasteCommand, outputStream);
        });

        assertTrue(exception.getMessage().contains("Error reading file"));
    }

    @Test
    void teeOverwriteAndCutContent_FromFileAndStdin_CorrectlyExtracted() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "cutContent.txt").toString();
        String inputContent = "1234567890\nabcdefghij";
        String expectedCutContent = "2468" + System.lineSeparator() + "bdfh" + System.lineSeparator(); //NOPMD - suppressed LongVariable - It's for readability

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String teeAndCutCommand = String.format("echo \"%s\" | tee %s | cut -c 2,4,6,8", inputContent, targetFile);

        shell.parseAndEvaluate(teeAndCutCommand, outputStream);

        String actualOutput = outputStream.toString();
        assertEquals(expectedCutContent, actualOutput);
    }

    @Test
    void teeAppendAndCutContent_FromAppendedFile_CorrectlyExtracted() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "appendAndCut.txt").toString();
        String initialContent = "FirstLine" + System.lineSeparator() + "SecondLine";
        String appendedContent = "1234567890\nabcdefghij\n";
        String expectedCutContent = "13579" + System.lineSeparator() + "acegi"; //NOPMD - suppressed LongVariable - It's for readability

        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(initialContent);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String teeAppendAndCutCommand = String.format("echo \"%s\" | tee -a %s | cut -c 1,3,5,7,9", appendedContent, targetFile); //NOPMD - suppressed LongVariable - It's for readability

        shell.parseAndEvaluate(teeAppendAndCutCommand, outputStream);

        String actualOutput = outputStream.toString();
        assertEquals(expectedCutContent, actualOutput.trim());
    }

    /**
     * Test for tee followed by cut with valid option of -b
     */
    @Test
    void teeOverwriteAndCutBytes_FromFile_CorrectlyExtracted() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "overwriteCutBytes.txt").toString();
        String inputContent = "12345ABCDEF\n67890GHIJKL";
        String expectedContent = "24A" +
                System.lineSeparator() + "79G";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String teeCutCommand = String.format("echo \"%s\" | tee %s | cut -b 2,4,6", inputContent, targetFile);

        shell.parseAndEvaluate(teeCutCommand, outputStream);

        String actualOutput = outputStream.toString();
        assertEquals(expectedContent + System.lineSeparator(), actualOutput);
    }

    /**
     * Test for tee followed by cut expected to throw exception
     */
    @Test
    void teeEchoAndCut_WithInvalidCutList_ReportsError() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "temp.txt").toString();
        String content = "This is a test.";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String command = String.format("echo \"%s\" | tee %s | cut -b", content, targetFile);

        Exception exception = assertThrows(CutException.class, () -> {
            shell.parseAndEvaluate(command, outputStream);
        });
    }

    /**
     * Test for tee followed by grep with valid option
     */
    @Test
    void teeFollowedByGrep_CaseInsensitiveMatching_DisplaysMatches() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "output1.txt").toString();
        String inputContent = "Case\ncaSE\nother\n";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String command = String.format("echo \"%s\" | tee %s | grep -i case", inputContent, targetFile);

        shell.parseAndEvaluate(command, outputStream);

        String expectedOutput = "Case" + System.lineSeparator() + "caSE";
        assertEquals(expectedOutput + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test for tee followed by grep -c with valid option
     */
    @Test
    void teeFollowedByGrep_CountMatchingLines_DisplaysCount() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "output2.txt").toString();
        String inputContent = "Match\nmatch\nother\n";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String command = String.format("echo \"%s\" | tee %s | grep -c match", inputContent, targetFile);

        shell.parseAndEvaluate(command, outputStream);

        String expectedOutput = "1";
        assertEquals(expectedOutput + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test for tee followed by grep -i -c with valid option
     */
    @Test
    void teeFollowedByGrep_CaseInsensitiveAndCount_DisplaysCount() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "output2.txt").toString();
        String inputContent = "Match\nmatch\nother\n";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String command = String.format("echo \"%s\" | tee %s | grep -i -c match", inputContent, targetFile);

        shell.parseAndEvaluate(command, outputStream);

        String expectedOutput = "2";
        assertEquals(expectedOutput + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test for tee followed by uniq -c with valid option
     */
    @Test
    void teeFollowedByUniq_CountingFlag_CountsDuplicateLines() throws Exception {
        String inputContent = "hello\nworld\nhello\nhello\nworld";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String targetFile = Paths.get(TEST_DIR, "output.txt").toString();

        String command = String.format("echo \"%s\" | tee %s | uniq -c", inputContent, targetFile);
        shell.parseAndEvaluate(command, outputStream);

        String expectedOutput = "1 hello" + System.lineSeparator() + "1 world" + System.lineSeparator() + "2 hello" + System.lineSeparator() + "1 world";
        assertEquals(expectedOutput + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test for tee followed by uniq -d with valid option
     */
    @Test
    void teeFollowedByUniq_DuplicateFlag_PrintsOnlyDuplicateLines() throws Exception {
        String inputContent = "hello\nworld\nhello\nhello\nworld";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String targetFile = Paths.get(TEST_DIR, "output.txt").toString();

        String command = String.format("echo \"%s\" | tee %s | uniq -d", inputContent, targetFile);
        shell.parseAndEvaluate(command, outputStream);

        String expectedOutput = "hello";
        assertEquals(expectedOutput + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Test for tee followed by uniq -d with invalid option
     */
    @Test
    void teeFollowedByUniq_DWithInvalidOption_ThrowsException() {
        String inputContent = "unique\ntest\ntest";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String command = String.format("echo \"%s\" | tee output.txt | uniq -d -invalid", inputContent);

        Exception exception = assertThrows(UniqException.class, () -> {
            shell.parseAndEvaluate(command, outputStream);
        });
    }

    /**
     * Test for tee followed by wc with valid option -w
     */
    @Test
    void teeThenWc_CountWords_ShowsNumWords() throws Exception {
        String targetFile = Paths.get(TEST_DIR, "content.txt").toString();
        String content = "This is a test.\nWith two lines.";

        String teeWcCommand = String.format("echo \"%s\" | tee %s | wc -w", content.replace("\n", "\\n"), targetFile);
        shell.parseAndEvaluate(teeWcCommand, outputStream);

        String expectedOutput = "6";
        assertEquals(expectedOutput, outputStream.toString().trim());
    }
}