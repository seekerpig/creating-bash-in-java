package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * This class contains unit tests for the SortArgsParser class.
 * It tests the behavior of the SortArgsParser class when parsing command-line
 * arguments.
 */
public class SortArgsParserTest {
    private SortArgsParser parser;

    @BeforeEach
    void setUp() {
        parser = new SortArgsParser();
    }

    /**
     * Test isFirstWordNumber returns true when the -n flag is present.
     *
     * @throws InvalidArgsException
     */
    @Test
    public void isFirstWordNumber_WhenFlagIsPresent_ReturnsTrue() throws InvalidArgsException {
        String[] args = { "-n" };
        parser.parse(args);
        assertTrue(parser.isFirstWordNumber());
    }

    /**
     * Test isFirstWordNumber returns false when the -n flag is absent.
     *
     * @throws InvalidArgsException
     */
    @Test
    public void isFirstWordNumber_WhenFlagIsAbsent_ReturnsFalse() throws InvalidArgsException {
        parser.parse(new String[] {});
        assertFalse(parser.isFirstWordNumber());
    }

    /**
     * Test isReverseOrder returns true when the -r flag is present.
     *
     * @throws InvalidArgsException
     */
    @Test
    public void isReverseOrder_WhenFlagIsPresent_ReturnsTrue() throws InvalidArgsException {
        parser.parse(new String[] { "-r" });
        assertTrue(parser.isReverseOrder());
    }

    /**
     * Test isReverseOrder returns false when the -r flag is absent.
     */
    @Test
    public void isReverseOrder_WhenFlagIsAbsent_ReturnsFalse() throws InvalidArgsException {
        parser.parse(new String[] {});
        assertFalse(parser.isReverseOrder());
    }

    /**
     * Test isCaseIndependent returns true when the -f flag is present.
     */
    @Test
    public void isCaseIndependent_WhenFlagIsPresent_ReturnsTrue() throws InvalidArgsException {
        SortArgsParser parser = new SortArgsParser();
        parser.parse(new String[] { "-f" });
        assertTrue(parser.isCaseIndependent());
    }

    /**
     * Test isCaseIndependent returns false when the -f flag is absent.
     */
    @Test
    public void isCaseIndependent_WhenFlagIsAbsent_ReturnsFalse() throws InvalidArgsException {
        parser.parse(new String[] {});
        assertFalse(parser.isCaseIndependent());
    }

    /**
     * This method tests the "no flags" scenario of the SortArgsParser class.
     *
     * @throws InvalidArgsException
     */
    @Test
    void parse_NoFlags_ReturnsFalseForFlagsAndExpectedFiles() throws InvalidArgsException {
        String[] args = { "file1.txt", "file2.txt" }; //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        parser.parse(args);
        assertFalse(parser.isFirstWordNumber());
        assertFalse(parser.isReverseOrder());
        assertFalse(parser.isCaseIndependent());
        assertEquals(List.of("file1.txt", "file2.txt"), parser.getFileNames());
    }

    /**
     * This method tests the "all flags separately" scenario of the SortArgsParser
     * class.
     *
     * @throws InvalidArgsException
     */
    @Test
    void parse_AllFlagsSeparately_ReturnsTrueForFlagsAndExpectedFile() throws InvalidArgsException {
        String[] args = { "-n", "-r", "-f", "file1.txt" };
        parser.parse(args);
        assertTrue(parser.isFirstWordNumber());
        assertTrue(parser.isReverseOrder());
        assertTrue(parser.isCaseIndependent());
        assertEquals(List.of("file1.txt"), parser.getFileNames());
    }

    /**
     * This method tests the "all flags together" scenario of the SortArgsParser
     * class.
     *
     * @throws InvalidArgsException
     */
    @Test
    void parse_AllFlagsTogether_ReturnsTrueForFlagsAndExpectedFile() throws InvalidArgsException {
        String[] args = { "-nrf", "file1.txt" };
        parser.parse(args);
        assertTrue(parser.isFirstWordNumber());
        assertTrue(parser.isReverseOrder());
        assertTrue(parser.isCaseIndependent());
        assertEquals(List.of("file1.txt"), parser.getFileNames());
    }

    /**
     * Test case to verify the behavior of the parse method when given flags but no
     * files.
     * It checks if the parse method correctly sets the flags and returns an empty
     * file list.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_FlagsButNoFiles_ReturnsTrueForFlagsAndEmptyFileList() throws InvalidArgsException {
        String[] args = { "-n", "-r", "-f" };
        parser.parse(args);
        assertTrue(parser.isFirstWordNumber());
        assertTrue(parser.isReverseOrder());
        assertTrue(parser.isCaseIndependent());
        assertTrue(parser.getFileNames().isEmpty());
    }

    /**
     * Test case to verify the behavior of the parse method when given different
     * order flags but no files.
     * It should return true for the flags -r, -f, and -n, and an empty file list.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_DifferentOrderFlagsButNoFiles_ReturnsTrueForFlagsAndEmptyFileList() throws InvalidArgsException {
        String[] args = { "-r", "-f", "-n" };
        parser.parse(args);
        assertTrue(parser.isFirstWordNumber());
        assertTrue(parser.isReverseOrder());
        assertTrue(parser.isCaseIndependent());
        assertTrue(parser.getFileNames().isEmpty());
    }

    /**
     * Test case to verify the behavior of the parse method when there are no flags
     * and no files provided.
     * It should return false for all flags and an empty file list.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_NoFlagsAndNoFiles_ReturnsFalseForFlagsAndEmptyFileList() throws InvalidArgsException {
        String[] args = {};
        parser.parse(args);
        assertFalse(parser.isFirstWordNumber());
        assertFalse(parser.isReverseOrder());
        assertFalse(parser.isCaseIndependent());
        assertTrue(parser.getFileNames().isEmpty());
    }

    /**
     * Test case to verify that the parser throws an InvalidArgsException when an
     * invalid flag is provided.
     *
     * @throws InvalidArgsException if the parser fails to handle the invalid flag.
     */
    @Test
    void parse_InvalidFlag_ThrowsInvalidArgsException() throws InvalidArgsException {
        String[] args = { "-x", "file1.txt" };
        assertThrows(InvalidArgsException.class, () -> parser.parse(args));
    }

    /**
     * Test case to verify the behavior of the parse method when given an invalid
     * option convention.
     * It should return false for isFirstWordNumber.
     *
     * @throws InvalidArgsException if there is an error in the arguments
     */
    @Test
    void parse_InvalidOptionConvention_ReturnsFalseForIsFirstWordNumber() throws InvalidArgsException {
        String[] args = { "n", "file1.txt" };
        parser.parse(args);
        assertFalse(parser.isFirstWordNumber());
    }
}