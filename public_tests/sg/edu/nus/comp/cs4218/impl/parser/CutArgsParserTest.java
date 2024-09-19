package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the CutArgsParser class.
 * It tests the functionality of the CutArgsParser class by verifying the behavior of its methods.
 */
class CutArgsParserTest {

    private CutArgsParser parser;

    @BeforeEach
    void setUp() {
        parser = new CutArgsParser();
    }

    /**
     * Test case to verify that the `parse` method correctly sets the cut by character flag when a valid cut by character flag is provided.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_ValidCutByCharacterFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-c", "1-3", "file.txt"); //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords) //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression
        assertTrue(parser.isCutByCharacter(), "Expected cut by character flag to be set.");
        assertFalse(parser.isCutByByte(), "Expected cut by byte flag not to be set.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets the cut by byte flag when valid byte flag is provided.
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_ValidCutByByteFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-b", "2,4,6", "file.txt");
        assertTrue(parser.isCutByByte(), "Expected cut by byte flag to be set.");
        assertFalse(parser.isCutByCharacter(), "Expected cut by character flag not to be set.");
    }

    /**
     * Test case to verify that the parser throws an InvalidArgsException when an invalid flag is provided.
     * The test checks if the exception message contains the phrase "illegal option".
     */
    @Test
    void parse_InvalidFlag_ThrowsInvalidArgsException() {
        Exception exception = assertThrows(InvalidArgsException.class, () -> parser.parse("-x", "2,4,6", "file.txt"));
        assertTrue(exception.getMessage().contains("illegal option"), "Expected exception for invalid flag.");
    }

    /**
     * Test case to verify the correct parsing of valid list and files in the parse method.
     * It checks if the parser correctly parses the cut list and file names.
     *
     * @throws InvalidArgsException if the arguments provided are invalid
     */
    @Test
    void parse_ValidListAndFiles_ParsesCorrectly() throws InvalidArgsException {
        parser.parse("-c", "1-5", "file1.txt", "file2.txt");
        assertEquals(List.of("1-5"), parser.getCutList(), "Expected correct list parsing.");
        assertIterableEquals(List.of("file1.txt", "file2.txt"), parser.getFiles(), "Expected correct file names parsing.");
    }

    /**
     * Test case to verify that the `parse` method correctly handles the scenario when no files are specified and standard input is read.
     * It checks if the `parse` method is able to parse the given arguments ("-c", "1-3") and correctly sets the list of files to be empty.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_NoFiles_ReadsStandardInput() throws InvalidArgsException {
        parser.parse("-c", "1-3");
        assertTrue(parser.getFiles().isEmpty(), "Expected no file names when none are specified.");
    }

    /**
     * Test case to verify that the parser throws an InvalidArgsException when an invalid list format is provided.
     * The method asserts that the exception message contains the expected error message.
     */
    @Test
    void parse_InvalidListFormat_ThrowsInvalidArgsException() {
        Exception exception = assertThrows(InvalidArgsException.class, () -> parser.parse("-c", "invalid", "file.txt"));
        assertTrue(exception.getMessage().contains("No valid list of positions provided."), "Expected exception for invalid list format.");
    }

    /**
     * Test case to verify that the parser takes the first valid list when multiple lists are provided.
     *
     * @throws InvalidArgsException if the arguments are invalid
     */
    @Test
    void parse_MultipleLists_TakesFirstValidList() throws InvalidArgsException {
        parser.parse("-c", "1-3", "4-6", "file.txt");
        assertEquals(List.of("1-3"), parser.getCutList(), "Expected parser to take the first valid list.");
    }

    /**
     * Test case to verify the behavior of the `isCutByCharacter` method when no flag is given.
     * It should return false.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void isCutByCharacter_NoFlagGiven_ReturnsFalse() throws InvalidArgsException {
        parser.parse("1-3", "file.txt");
        assertFalse(parser.isCutByCharacter(), "Expected isCutByCharacter to return false when no flag is given.");
    }

    /**
     * Test case to verify the behavior of the `isCutByByte` method when no flag is given.
     * It should return false.
     *
     * @throws InvalidArgsException if there is an error parsing the arguments
     */
    @Test
    void isCutByByte_NoFlagGiven_ReturnsFalse() throws InvalidArgsException {
        parser.parse("2-4", "file.txt");
        assertFalse(parser.isCutByByte(), "Expected isCutByByte to return false when no flag is given.");
    }
}