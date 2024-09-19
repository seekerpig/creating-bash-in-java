package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    private final Properties systemProperties = System.getProperties();

    /**
     * Tests the fileSeparator method for a Windows OS environment. Verifies that the method
     * returns the correct file separator used by Windows operating systems.
     */
    @Test
    void fileSeparator_Windows_ShouldReturnValidFileSeparator() {
        systemProperties.setProperty("os.name", "Windows 11");
        assertEquals('\\' + File.separator, StringUtils.fileSeparator());
    }

    /**
     * Tests the fileSeparator method for a Linux OS environment. Verifies that the method
     * returns the correct file separator used by Linux operating systems.
     */
    @Test
    void fileSeparator_Linux_ShouldReturnValidFileSeparator() {
        systemProperties.setProperty("os.name", "Ubuntu");
        assertEquals(File.separator, StringUtils.fileSeparator());
    }

    /**
     * Tests the isBlank method with a null input. Verifies that the method returns true
     * indicating the input is considered blank.
     */
    @Test
    void isBlank_NullInput_ShouldReturnTrue() {
        assertTrue(StringUtils.isBlank(null));
    }

    /**
     * Tests the isBlank method with an empty string. Verifies that the method returns true
     * indicating the string is considered blank.
     */
    @Test
    void isBlank_EmptyString_ShouldReturnTrue() {
        assertTrue(StringUtils.isBlank(""));
    }

    /**
     * Tests the isBlank method with a long string of spaces. Verifies that the method returns true
     * indicating the string is considered blank.
     */
    @Test
    void isBlank_LongEmptyString_ShouldReturnTrue() {
        assertTrue(StringUtils.isBlank("     "));
    }

    /**
     * Tests the isBlank method with a valid string containing characters other than spaces.
     * Verifies that the method returns false indicating the string is not considered blank.
     */
    @Test
    void isBlank_ValidString_ShouldReturnFalse() {
        assertFalse(StringUtils.isBlank("hello world"));
    }

    /**
     * Tests the multiplyChar method with a character and a multiplier of 0. Verifies that
     * the method returns an empty string.
     */
    @Test
    void multiplyChar_NumIs0_ShouldReturnEmptyString() {
        assertTrue(StringUtils.multiplyChar('a', 0).isEmpty());
    }

    /**
     * Tests the multiplyChar method with a character and a positive multiplier. Verifies that
     * the method returns a string consisting of the character repeated according to the multiplier.
     */
    @Test
    void multiplyChar_NumIs5_ShouldReturnStringWithCorrectValues() {
        assertEquals("aaaaa", StringUtils.multiplyChar('a', 5));
    }

    /**
     * Tests the tokenize method with an empty string. Verifies that the method returns an empty array.
     */
    @Test
    void tokenize_StringEmpty_ShouldReturnEmptyArr() {
        assertEquals(Arrays.toString(new String[0]), Arrays.toString(StringUtils.tokenize("")));
    }

    /**
     * Tests the tokenize method with a valid string containing spaced characters. Verifies that
     * the method returns an array of strings, each containing a single character from the input string.
     */
    @Test
    void tokenize_ValidString_ShouldReturnEmptyArr() {
        String[] resultString = {"h", "e", "l", "l", "o"};
        assertArrayEquals(resultString, StringUtils.tokenize("h e l l o"));
    }

    /**
     * Tests the isNumber method with a valid number string. Verifies that the method returns true
     * indicating the string represents a valid number.
     */
    @Test
    void isNumber_isValidNumber_ShouldReturnTrue() {
        assertTrue(StringUtils.isNumber("10"));
    }

    /**
     * Tests the isNumber method with an invalid number string. Verifies that the method returns false
     * indicating the string does not represent a valid number.
     */
    @Test
    void isNumber_isNotValid_ShouldReturnFalse() {
        assertFalse(StringUtils.isNumber(""));
    }
}