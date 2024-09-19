package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MkdirArgsParserTest {
    private MkdirArgsParser parser;
    private static final String DIR1 = "dir1";
    private static final String DIR2 = "dir2";
    private static final String CHILD = "child";
    private static final String DIR1_CHILD = DIR1 + StringUtils.CHAR_FILE_SEP + CHILD;

    @BeforeEach
    public void setUp() {
        parser = new MkdirArgsParser();
    }

//    @Test
//    public void parse_nullArgs_shouldThrowException() {
//        assertThrows(InvalidArgsException.class, () -> parser.parse(null));
//    }

    @Test
    public void parse_singleArgAndNoParentFlag_shouldCorrectAssignment() {
        try {
            parser.parse(DIR1);
        } catch (InvalidArgsException e) {
            fail();
        }
        List<String> directories = parser.getDirectories();
        assertEquals(1, directories.size());
        assertEquals(DIR1, directories.get(0));
        assertFalse(parser.isCreateParent());
    }

    @Test
    public void parse_multipleArgsAndNoParentFlag_shouldCorrectAssignment() {
        try {
            parser.parse(DIR1, DIR2);
        } catch (InvalidArgsException e) {
            fail();
        }
        List<String> directories = parser.getDirectories();
        assertEquals(2, directories.size());
        assertTrue(directories.contains(DIR1));
        assertTrue(directories.contains(DIR2));
        assertFalse(parser.isCreateParent());
    }

    @Test
    public void parse_singleArgAndParentFlag_shouldCorrectAssignment() {
        try {
            parser.parse("-p", DIR1_CHILD);
        } catch (InvalidArgsException e) {
            fail();
        }
        List<String> directories = parser.getDirectories();
        assertEquals(1, directories.size());
        assertEquals(DIR1_CHILD, directories.get(0));
        assertTrue(parser.isCreateParent());
    }

    @Test
    public void parse_multipleArgsAndParentFlag_shouldCorrectAssignment() {
        try {
            parser.parse("-p", DIR1_CHILD, DIR2);
        } catch (InvalidArgsException e) {
            fail();
        }
        List<String> directories = parser.getDirectories();
        assertEquals(2, directories.size());
        assertTrue(directories.contains(DIR1_CHILD));
        assertTrue(directories.contains(DIR2));
        assertTrue(parser.isCreateParent());
    }

    // Additional tests for edge cases, invalid input, mutability, thread safety, etc.
}

