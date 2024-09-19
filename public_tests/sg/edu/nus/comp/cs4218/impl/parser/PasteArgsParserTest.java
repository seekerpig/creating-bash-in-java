package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasteArgsParserTest {

    private PasteArgsParser parser;
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.txt";

    @BeforeEach
    void setUp() {
        parser = new PasteArgsParser();
    }

    /**
     * Test case to verify that when no arguments are provided, the `parse` method
     * returns an empty file list and the `isSerial` flag is set to false.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments.
     */
    @Test
    void parse_NoFlagsNoFiles_FilesEmpty() throws InvalidArgsException {
        parser.parse();
        assertTrue(parser.getFiles().isEmpty());
        assertFalse(parser.isSerial());
    }

    /**
     * Test method to verify the behavior of parsing when only the serial flag ("-s") is provided.
     *
     * @throws InvalidArgsException if there is an error with the provided arguments
     */
    @Test
    void parse_OnlySerialFlag_AssertsTrue() throws InvalidArgsException {
        parser.parse("-s");
        assertTrue(parser.isSerial());
        assertTrue(parser.getFiles().isEmpty());
    }

    /**
     * Test method to verify the behavior of parsing when the serial flag ("-s") is provided along with file names.
     *
     * @throws InvalidArgsException if there is an error with the provided arguments
     */
    @Test
    void parse_SerialFlagWithFiles_ShowsBoth() throws InvalidArgsException {
        String[] args = {"-s", FILE_1, FILE_2};
        parser.parse(args);
        assertTrue(parser.isSerial());
        assertEquals(2, parser.getFiles().size());
        assertEquals(FILE_1, parser.getFiles().get(0));
        assertEquals(FILE_2, parser.getFiles().get(1));
    }

    /**
     * Test method to verify the behavior of parsing when no serial flag is provided but file names are provided.
     *
     * @throws InvalidArgsException if there is an error with the provided arguments
     */
    @Test
    void parse_NoSerialFlagWithFiles_ShowsFiles() throws InvalidArgsException {
        String[] args = {FILE_1, FILE_2};
        parser.parse(args);
        assertFalse(parser.isSerial());
        assertEquals(2, parser.getFiles().size());
        assertEquals(FILE_1, parser.getFiles().get(0));
        assertEquals(FILE_2, parser.getFiles().get(1));
    }

    /**
     * Test method to verify that providing an invalid flag throws an InvalidArgsException.
     * It checks that an exception is thrown and the correct error message is received.
     */
    @Test
    void parse_InvalidFlag_ThrowsException() {
        String[] args = {"-x", FILE_1};
        Exception exception = assertThrows(InvalidArgsException.class, () -> parser.parse(args));
        assertEquals("illegal option -- x", exception.getMessage());
    }

    /**
     * Test method to verify that providing multiple flags including the serial flag ("-s") throws an InvalidArgsException.
     * It checks that an exception is thrown and the correct error message is received.
     *
     * @throws InvalidArgsException if there is an error with the provided arguments
     */
    @Test
    void parse_MultipleFlagsIncludingSerial_CapturesInvalidFlag() throws InvalidArgsException {
        String[] args = {"-sx", FILE_1};
        Exception exception = assertThrows(InvalidArgsException.class, () -> parser.parse(args));
        assertEquals("illegal option -- x", exception.getMessage());
    }

    /**
     * Test method to verify the behavior of parsing when the serial flag ("-s") and non-flag arguments are provided in mixed order.
     * It ensures that the parser correctly handles the arguments and sets the serial flag accordingly.
     *
     * @throws InvalidArgsException if there is an error with the provided arguments
     */
    @Test
    void parse_SerialFlagAndNonFlagArgumentsMixedOrder_ParsesCorrectly() throws InvalidArgsException {
        String[] args = {FILE_1, "-s", FILE_2};
        parser.parse(args);
        assertTrue(parser.isSerial());
        assertEquals(2, parser.getFiles().size());
        assertEquals(FILE_1, parser.getFiles().get(0));
        assertEquals(FILE_2, parser.getFiles().get(1));
    }
}
