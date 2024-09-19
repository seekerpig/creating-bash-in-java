package sg.edu.nus.comp.cs4218.impl.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

public class RmArgsParserTest {
    private RmArgsParser parser;

    @BeforeEach
    void setUp() {
        parser = new RmArgsParser();
    }

    /**
     * Test case to verify that the `parse` method correctly sets the recursive flag.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_ValidRecursiveFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-r", "file.txt");
        assertTrue(parser.isRecursive(), "Expected recursive flag to be set.");
        assertFalse(parser.isEmptyDirectory(), "Expected empty directory flag to be not set.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets the empty directory flag.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_ValidEmptyDirectoryFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-d", "file.txt");
        assertTrue(parser.isEmptyDirectory(), "Expected empty directory flag to be set.");
        assertFalse(parser.isRecursive(), "Expected recursive flag to be not set.");
    }

    /**
     * Test case to verify that the `parse` method correctly sets no flags when none is given.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_NoFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("", "file.txt");
        assertFalse(parser.isEmptyDirectory(), "Expected empty directory flag to be not set.");
        assertFalse(parser.isRecursive(), "Expected recursive flag to be not set.");
    }
}
