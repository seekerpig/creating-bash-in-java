package sg.edu.nus.comp.cs4218.impl.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

public class MvArgsParserTest {
    private MvArgsParser parser;

    @BeforeEach
    void setUp() {
        parser = new MvArgsParser();
    }

    /**
     * Test case to verify that the `parse` method correctly sets the overwrite flag.
     *
     * @throws InvalidArgsException if there is an error in parsing the arguments
     */
    @Test
    void parse_ValidOverwriteFlag_SetsCorrectFlag() throws InvalidArgsException {
        parser.parse("-n", "file.txt");
        assertFalse(parser.isOverwrite(), "Expected overwrite flag to be set.");
    }
}
