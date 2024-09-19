package sg.edu.nus.comp.cs4218.impl.integration.pipe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class CatApplicationIT { //NOPMD - suppressed ClassNamingConventions - Class name is valid and sensible
    private ShellImpl shell;
    private ByteArrayOutputStream outputStream;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String TEST_DIR = Paths.get(TEMP_DIR, "catIntegrationTest").toString();
    private static final String FIRST_FILE = "first.txt";
    private static final String SECOND_FILE = "second.txt";
    private static final String THIRD_FILE = "third.txt";
    private static final String FOURTH_FILE = "fourth.txt";
    private static final String FIFTH_FILE = "fifth.txt";
    private static final String OUTPUT_FILE_1 = "output1.txt";
    private static final String OUTPUT_FILE_2 = "output2.txt";
    private static final String OUTPUT_FILE_3 = "output3.txt";
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

        String data = String.join(StringUtils.STRING_NEWLINE, "apple", "Banana", "123", "23", "!@#", "Cherry", "ORANGE"); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        String data2 = String.join(StringUtils.STRING_NEWLINE, "lemon"); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        String data3 = String.join(StringUtils.STRING_NEWLINE, "Hello World", "Hello World", "Alice", "Alice", "Bob", "Alice", "Bob"); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)

        Files.write(Paths.get(TEST_DIR, FIRST_FILE), "Some content in file 1.".getBytes());
        Files.write(Paths.get(TEST_DIR, SECOND_FILE), "Other content in file 2".getBytes());
        Files.write(Paths.get(TEST_DIR, THIRD_FILE), data.getBytes());
        Files.write(Paths.get(TEST_DIR, FOURTH_FILE), data2.getBytes());
        Files.write(Paths.get(TEST_DIR, FIFTH_FILE), data3.getBytes());
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

    @Test
    void cat_PipedToCut_FirstTwoCharactersDisplayed() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String command = String.format("cat %s | cut -c 1,2", firstFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "So" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void cut_PipedToCat_FirstTwoCharactersDisplayed() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String command = String.format("cut -c 1,2 %s | cat", firstFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "So" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void cat_WithLineNumberOptionPipedToCut_DisplayLineNumberAndFirstEightChars() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String command = String.format("cat -n %s | cut -c 1-8", firstFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "1 Some c" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void cut_WithRangePipedToCat_DisplayFirstEightCharacters() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String command = String.format("cut -c 1-8 %s | cat", firstFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "Some con" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void cat_WithLineNumberOptionForMultipleFilesPipedToCut_DisplayLineNumbersAndFirstTenBytes() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String command = String.format("cat -n %s %s | cut -b 1-10", firstFile, secondFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "1 Some con" + StringUtils.STRING_NEWLINE + "2 Other co" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void cut_WithByteRangeForMultipleFilesPipedToCatWithLineNumbers_DisplayFirstTenBytesWithLineNumbers() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String command = String.format("cut -b 1-10 %s %s | cat -n", firstFile, secondFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "1 Some conte" + StringUtils.STRING_NEWLINE + "2 Other cont" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }
    
    @Test
    void cat_CatPipedToCutWithRange_FirstFileContentCut() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String command = String.format("cat %s | cut -c 1-4 - %s", firstFile, secondFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "Othe" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void cut_CutPipedToCatWithLineNumbers_MultipleFilesContentNumbered() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String command = String.format("cut -c 1-4 %s | cat -n - %s", firstFile, secondFile);
        shell.parseAndEvaluate(command, outputStream);
        String expectedOutput = "1 Other content in file 2" + StringUtils.STRING_NEWLINE + "2 Some" + StringUtils.STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void cat_InvalidOptionWithPipe_ThrowsCatException() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String command = String.format("cat %s -o | cut -c 1,2", firstFile, secondFile);
        assertThrows(CatException.class, () -> shell.parseAndEvaluate(command, outputStream));
    }

    @Test
    void cut_InvalidCutSyntax_ThrowsCutException() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String command = String.format("cat -n %s | cut 1,2", firstFile, secondFile);
        assertThrows(CutException.class, () -> shell.parseAndEvaluate(command, outputStream));
    }

    @Test
    void cut_CutWithInvalidFileSyntax_ThrowsCutException() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String command = String.format("cat -n %s | cut -c 1,2 file2.txt", firstFile);
        assertThrows(CutException.class, () -> shell.parseAndEvaluate(command, outputStream));
    }

    @Test
    void cat_CatNonexistentFile_ThrowsCatException() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String command = String.format("cat -n nonexistent.txt | cut -c 1,2 %s", firstFile);
        assertThrows(CatException.class, () -> shell.parseAndEvaluate(command, outputStream));
    }
    
    @Test
    void cat_MultipleFilesPipedToSort_SortedContentReturned() throws Exception {
        String firstFile = Paths.get(TEST_DIR, THIRD_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, FOURTH_FILE).toString();
        String commandString = String.format("cat %s %s | sort", firstFile, secondFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "!@#", "123", "23", "Banana", "Cherry", "ORANGE", "apple", "lemon") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void sort_MultipleFilesPipedToCat_SortedContentReturned() throws Exception {
        String firstFile = Paths.get(TEST_DIR, THIRD_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, FOURTH_FILE).toString();
        String commandString = String.format("sort %s %s | cat", firstFile, secondFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "!@#", "123", "23", "Banana", "Cherry", "ORANGE", "apple", "lemon") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void sort_MultipleFilesPipedToCatTripleN_NestedNumberingApplied() throws Exception {
        String firstFile = Paths.get(TEST_DIR, THIRD_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, FOURTH_FILE).toString();
        String commandString = String.format("sort %s %s | cat -n | cat -n | cat -n", firstFile, secondFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "1 1 1 !@#", "2 2 2 123", "3 3 3 23", "4 4 4 Banana", "5 5 5 Cherry", "6 6 6 ORANGE", "7 7 7 apple", "8 8 8 lemon") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void sort_MultipleFilesPipedToCatNThenSort_SortedNumberedContentReturned() throws Exception {
        String firstFile = Paths.get(TEST_DIR, THIRD_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, FOURTH_FILE).toString();
        String thirdFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String commandString = String.format("sort %s %s | cat -n | sort", firstFile, secondFile, thirdFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "1 !@#", "2 123", "3 23", "4 Banana", "5 Cherry", "6 ORANGE", "7 apple", "8 lemon") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void cat_MultipleFilesPipedToSortReverse_ReversedSortedContentReturned() throws Exception {
        String firstFile = Paths.get(TEST_DIR, THIRD_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, FOURTH_FILE).toString();
        String commandString = String.format("cat %s %s | sort -r", firstFile, secondFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "lemon", "apple", "ORANGE", "Cherry", "Banana", "23", "123", "!@#") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void sort_InvalidOptionWithMultipleFiles_ThrowsSortException() {
        String firstFile = Paths.get(TEST_DIR, THIRD_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, FOURTH_FILE).toString();
        String commandString = String.format("sort -z %s %s | cat", firstFile, secondFile);
        assertThrows(SortException.class, () -> shell.parseAndEvaluate(commandString, outputStream));
    }

    @Test
    void cat_PipeTeeAndCatWithFiles_CopiesFirstToFileSecondAndDisplaysThird() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String thirdFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String commandString = String.format("cat %s | tee %s | cat %s", firstFile, secondFile, thirdFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "Some content in file 1.") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void cat_PipeTeeWithFiles_DisplaysFirstAndCopiesToSecond() throws Exception {
        String firstFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, FIRST_FILE).toString();
        String commandString = String.format("cat %s | tee %s", firstFile, secondFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "Other content in file 2") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void cat_InvalidOptionWithTee_ThrowsCatException() throws Exception {
        String firstFile = Paths.get(TEST_DIR, OUTPUT_FILE_1).toString();
        String secondFile = Paths.get(TEST_DIR, OUTPUT_FILE_2).toString();
        String thirdFile = Paths.get(TEST_DIR, OUTPUT_FILE_3).toString();
        String commandString = String.format("cat -pp %s | tee -a %s", firstFile, secondFile, thirdFile);
        assertThrows(CatException.class, () -> shell.parseAndEvaluate(commandString, outputStream));
    }
    
    @Test
    void cat_InvalidOptionWithUniq_ThrowsCatException() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIFTH_FILE).toString();
        String commandString = String.format("cat -pp %s | uniq", firstFile);
        assertThrows(CatException.class, () -> shell.parseAndEvaluate(commandString, outputStream));
    }

    @Test
    void uniq_InvalidOptionPipedToCat_ThrowsUniqException() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIFTH_FILE).toString();
        String commandString = String.format("uniq -r | cat -pp %s", firstFile);
        assertThrows(UniqException.class, () -> shell.parseAndEvaluate(commandString, outputStream));
    }

    @Test
    void cat_PipedToUniq_DisplaysUniqueLines() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIFTH_FILE).toString();
        String commandString = String.format("cat %s | uniq", firstFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "Hello World", "Alice", "Bob", "Alice", "Bob") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void cat_PipedToUniqWithDuplicateLines_DisplaysDuplicatesOnly() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIFTH_FILE).toString();
        String commandString = String.format("cat %s | uniq -D", firstFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "Hello World", "Hello World", "Alice", "Alice") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void cat_PipedToUniqWithDuplicateFlag_DisplaysFirstOccurrenceOfDuplicates() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIFTH_FILE).toString();
        String commandString = String.format("cat %s | uniq -d", firstFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "Hello World", "Alice") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void cat_WithNumberOptionPipedToUniqWithCount_DisplaysLineNumbersAndCounts() throws Exception {
        String firstFile = Paths.get(TEST_DIR, FIFTH_FILE).toString();
        String secondFile = Paths.get(TEST_DIR, SECOND_FILE).toString();
        String commandString = String.format("cat -n %s %s | uniq -c", firstFile, secondFile);
        String expected = String.join(StringUtils.STRING_NEWLINE, "1 1 Hello World", "1 2 Hello World", "1 3 Alice",
                                        "1 4 Alice", "1 5 Bob", "1 6 Alice", "1 7 Bob", "1 8 Other content in file 2") + StringUtils.STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
    }
}
