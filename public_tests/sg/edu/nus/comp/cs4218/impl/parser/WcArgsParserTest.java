package sg.edu.nus.comp.cs4218.impl.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.WcException;

/**
 * This class contains unit tests for the WcArgsParser class.
 * It tests the functionality of the WcArgsParser class by verifying the behavior of its methods.
 */
public class WcArgsParserTest {

    private WcArgsParser parser;
    private static String testFileName = "file.txt";

    @BeforeEach
    void setUp() {
        parser = new WcArgsParser();
    }

    /**
     * Test case to verify that the `parse` method correctly sets the line count flag when a valid word count by line flag is provided.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_ValidLineCountFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-l", testFileName);
        assertTrue(parser.isLineCount(), "Expected wc by line flag to be set.");
        assertFalse(parser.isByteCount(), "Expected wc by byte flag not to be set.");
        assertFalse(parser.isWordCount(), "Expected wc by word flag not to be set.");
    }
    /**
     * Test case to verify that the `parse` method correctly sets the count by byte flag when valid byte flag is provided.
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_ValidCutByByteFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-c", testFileName);
        assertTrue(parser.isByteCount(), "Expected wc by byte flag to be set.");
        assertFalse(parser.isLineCount(), "Expected wc by line flag to be not set.");
        assertFalse(parser.isWordCount(), "Expected wc by word flag not to be not set.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets the count by word flag when valid word flag is provided.
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_ValidCutByWordFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-w", testFileName);
        assertTrue(parser.isWordCount(), "Expected wc by word flag to be set.");
        assertFalse(parser.isLineCount(), "Expected wc by line flag to be not set.");
        assertFalse(parser.isByteCount(), "Expected wc by byte flag to be not set.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets the count by word, line and byte flag when valid flags are provided.
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_ValidAllFlags_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-lcw", testFileName);
        assertTrue(parser.isWordCount(), "Expected wc by word flag to be set.");
        assertTrue(parser.isLineCount(), "Expected wc by line flag to be set.");
        assertTrue(parser.isByteCount(), "Expected wc by byte flag to be set.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets the count by word, line and byte flag when valid flags are provided.
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_InvalidFlag_ThrowsInvalidArgsException() throws InvalidArgsException {
        assertThrows(InvalidArgsException.class, () -> parser.parse("-h", "file.txt"));
    }

    /**
     * Test case to verify that the `parse` method correctly sets when no flags are provided.
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_NoFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("", testFileName);
        assertTrue(parser.isWordCount(), "Expected wc by word flag to be set.");
        assertTrue(parser.isLineCount(), "Expected wc by line flag to be set.");
        assertTrue(parser.isByteCount(), "Expected wc by byte flag to be set.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets when no flags are provided.
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_FileNameGivenAsFlag_ThrowsInvalidArgsException() throws InvalidArgsException {
        assertThrows(InvalidArgsException.class, () -> parser.parse("-l " + testFileName, testFileName));
    }

}
