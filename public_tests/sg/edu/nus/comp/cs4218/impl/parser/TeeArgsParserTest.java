package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the TeeArgsParser class.
 * It tests the parsing of command-line arguments for the Tee command.
 */
class TeeArgsParserTest {
    private TeeArgsParser parser;
    private static final String FILE_1 = "file1.txt ";
    private static final String FILE_2 = "file2.txt ";

    @BeforeEach
    void setUp() {
        parser = new TeeArgsParser();
    }

    /**
     * Test case to verify that when no arguments are provided, the `parse` method
     * returns an empty file list.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_NoArguments_ReturnsEmptyFileList() throws InvalidArgsException {
        parser.parse();
        assertTrue(parser.getFiles().isEmpty(), "Expected no file arguments.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets the `isAppend`
     * flag to true
     * when only a valid flag ("-a") is provided as input.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_OnlyValidFlag_IsAppendReturnsTrue() throws InvalidArgsException {
        parser.parse("-a");
        assertTrue(parser.isAppend(), "Expected isAppend to return true.");
    }

    /**
     * Test case to verify that the `parse` method throws an `InvalidArgsException`
     * when an invalid flag is provided.
     *
     * @throws InvalidArgsException if an invalid flag is provided
     */
    @Test
    void parse_InvalidFlag_ThrowsInvalidArgsException() {
        InvalidArgsException thrown = assertThrows(InvalidArgsException.class, () -> parser.parse("-b"));
        assertTrue(thrown.getMessage().contains("illegal option"), "Expected exception for illegal option.");
    }

    /**
     * Test case to verify the behavior of the parse method when given a single file
     * name.
     * It checks if the file name is correctly added to the file list.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_SingleFileName_ReturnsFileNameInFileList() throws InvalidArgsException {
        parser.parse(FILE_1);
        assertEquals(1, parser.getFiles().size(), "Expected one file name in the list.");
        assertEquals(FILE_1, parser.getFiles().get(0), "Expected file name to match.");
    }

    /**
     * Test case to verify the behavior of the parse method when multiple file names
     * are provided.
     * It checks if the parse method correctly adds all the file names to the file
     * list.
     *
     * @throws InvalidArgsException if there is an error in parsing the file names.
     */
    @Test
    void parse_MultipleFileNames_ReturnsAllFileNamesInFileList() throws InvalidArgsException {
        parser.parse(FILE_1, FILE_2);
        assertEquals(2, parser.getFiles().size(), "Expected two file names in the list.");
        assertIterableEquals(List.of(FILE_1, FILE_2), parser.getFiles(), "Expected file names to match.");
    }

    /**
     * Test case to verify the behavior of the parse method when given a flag and
     * file names.
     * The method should set the append flag to true and return the list of file
     * names.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_FlagAndFileNames_IsAppendAndReturnsFileNames() throws InvalidArgsException {
        parser.parse("-a", FILE_1, FILE_2);
        assertTrue(parser.isAppend(), "Expected isAppend to return true with '-a' flag.");
        assertEquals(2, parser.getFiles().size(), "Expected two file names in the list.");
        assertIterableEquals(List.of(FILE_1, FILE_2), parser.getFiles(), "Expected file names to match.");
    }

    /**
     * Test case to verify that the `parse` method throws an `InvalidArgsException`
     * when multiple flags, including a valid flag, are provided as arguments.
     */
    @Test
    void parse_MultipleFlagsIncludingValid_ThrowsInvalidArgsException() {
        InvalidArgsException thrown = assertThrows(InvalidArgsException.class, () -> parser.parse("-a", "-b"));
        assertTrue(thrown.getMessage().contains("illegal option"), "Expected exception for illegal option.");
    }

    /**
     * Test case to verify the behavior of isAppend() method when no '-a' flag is
     * given.
     * It should return false.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void isAppend_NoFlagGiven_ReturnsFalse() throws InvalidArgsException {
        parser.parse(FILE_1);
        assertFalse(parser.isAppend(), "Expected isAppend to return false when no '-a' flag is given.");
    }

    /**
     * Test case to verify that the parse method does not throw an exception when an
     * empty string is passed as an argument.
     * It also checks if the empty string is treated as a file name by the parser.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_EmptyStringAsArgument_ThrowsNoException() throws InvalidArgsException {
        parser.parse("");
        assertTrue(parser.getFiles().contains(""), "Expected empty string to be treated as a file name.");
    }

    /**
     * Test case to verify that when a hyphen is passed as the file name argument to
     * the `parse` method,
     * it is treated as a valid file name and added to the list of files.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_HyphenAsFileName_TreatsHyphenAsFileName() throws InvalidArgsException {
        parser.parse("-");
        assertTrue(parser.getFiles().contains("-"), "Expected '-' to be treated as a file name.");
    }

    /**
     * Test case to verify the behavior of the parse method when an invalid flag is
     * provided along with file names.
     * It should throw an InvalidArgsException and ignore the files.
     */
    @Test
    void parse_InvalidFlagWithFileNames_ThrowsInvalidArgsExceptionAndIgnoresFiles() {
        InvalidArgsException thrown = assertThrows(InvalidArgsException.class,
                () -> parser.parse("-x", FILE_1, FILE_2));
        assertTrue(thrown.getMessage().contains("illegal option"), "Expected exception for illegal option.");
    }

    /**
     * Test case to verify the behavior of the `parse` method when multiple valid
     * flags are provided.
     * The test checks if the `isAppend` method returns true when multiple '-a'
     * flags are given.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_MultipleValidFlags_IsAppendReturnsTrue() throws InvalidArgsException {
        parser.parse("-a", "-a");
        assertTrue(parser.isAppend(), "Expected isAppend to return true when multiple '-a' flags are given.");
    }
}