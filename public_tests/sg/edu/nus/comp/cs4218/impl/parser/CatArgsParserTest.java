package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

/**
 * This class contains unit tests for the CatArgsParser class.
 * It tests the behavior of the CatArgsParser class in parsing command-line arguments for the "cat" command.
 */
public class CatArgsParserTest {
    private CatArgsParser parser;

    @BeforeEach
    void setUp() {
        parser = new CatArgsParser();
    }

    /**
     * Test case to verify the behavior of the isNumberLines() method when the flag is present.
     * It checks if the method returns true after parsing the given arguments and setting the flag.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void isNumberLines_WhenFlagIsPresent_ReturnsTrue() throws InvalidArgsException {
        String[] args = {"-n", "file1.txt"}; //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        parser.parse(args);
        assertTrue(parser.isNumberLines());
    }

    /**
     * Tests the behavior of the `isNumberLines` method when the flag is absent.
     * It verifies that the method returns false when the flag is not present.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void isNumberLines_WhenFlagIsAbsent_ReturnsFalse() throws InvalidArgsException {
        String[] args = {"file1.txt"};
        parser.parse(args);
        assertFalse(parser.isNumberLines());
    }

    /**
     * Test case to verify the behavior of the `getFiles` method when a single file is provided as an argument.
     * It checks if the method correctly returns a list containing the single file.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void getFiles_WhenSingleFile_ReturnsFileList() throws InvalidArgsException {
        String[] args = {"file1.txt"};
        parser.parse(args);
        assertEquals(List.of("file1.txt"), parser.getFiles());
    }

    /**
     * Test case to verify the behavior of the getFiles() method when multiple files are provided as arguments.
     * It checks if the method returns a list of files that were parsed by the parser.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void getFiles_WhenMultipleFiles_ReturnsFileList() throws InvalidArgsException {
        String[] args = {"file1.txt", "file2.txt"}; //NOPMD - suppressed AvoidDuplicateLiterals - For readability (it is not scalable to have global variables for all string literals - it only makes sense in certain contexts, for example: for keywords)
        parser.parse(args);
        assertEquals(List.of("file1.txt", "file2.txt"), parser.getFiles());
    }

    /**
     * Test case to verify that when no files are provided as arguments, the method returns an empty list.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void getFiles_WhenNoFiles_ReturnsEmptyList() throws InvalidArgsException {
        String[] args = {"-n"};
        parser.parse(args);
        assertTrue(parser.getFiles().isEmpty());
    }

    /**
     * Test case to verify that the `parse` method throws an `InvalidArgsException`
     * when an invalid flag is provided.
     */
    @Test
    void parse_WhenInvalidFlag_ThrowsInvalidArgsException() {
        String[] args = {"-x", "file1.txt"};
        assertThrows(InvalidArgsException.class, () -> parser.parse(args));
    }

    /**
     * Test case to verify the behavior of the `parse` method when there are no flags and no files provided.
     * It should return an empty list of files and `false` for the flag indicating whether to number the lines.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_WhenNoFlagsAndNoFiles_ReturnsEmptyListAndFalseForFlag() throws InvalidArgsException {
        String[] args = {};
        parser.parse(args);
        assertFalse(parser.isNumberLines());
        assertTrue(parser.getFiles().isEmpty());
    }

    /**
     * Test case to verify the behavior of the parse method when flags and files are mixed.
     * It checks if the method correctly parses the input arguments and sets the flag status and file list accordingly.
     *
     * @throws InvalidArgsException if the input arguments are invalid
     */
    @Test
    void parse_WhenFlagsAndFilesMixed_ReturnsCorrectFileListAndFlagStatus() throws InvalidArgsException {
        String[] args = {"-n", "file1.txt", "file2.txt"};
        parser.parse(args);
        assertTrue(parser.isNumberLines());
        assertEquals(List.of("file1.txt", "file2.txt"), parser.getFiles());
    }

    /**
     * Test case to verify the behavior of the `parse` method when flags are duplicated.
     * It checks if the method correctly handles duplicated flags and returns the correct flag status.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_WhenFlagsAreDuplicated_ReturnsCorrectFlagStatus() throws InvalidArgsException {
        String[] args = {"-n", "-n", "file1.txt"};
        parser.parse(args);
        assertTrue(parser.isNumberLines());
        assertEquals(List.of("file1.txt"), parser.getFiles());
    }
}
