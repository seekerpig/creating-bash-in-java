package sg.edu.nus.comp.cs4218.impl.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ArgumentResolverTest {
    private final ArgumentResolver argumentResolver = new ArgumentResolver();

    /**
     * Test case to verify parsing of a single-quoted argument.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_SingleQuote_OutputMatchesExpected() {
        List<String> input = List.of("'Hello World'");
        List<String> expected = List.of("Hello World");
        assertDoesNotThrow(() -> {
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(expected, parsedArgsList);
        });
    }

    /**
     * Test case to verify parsing of a double-quoted argument.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_DoubleQuote_OutputMatchesExpected() {
        List<String> input = List.of("\"Hello World\"");
        List<String> expected = List.of("Hello World");
        assertDoesNotThrow(() -> {
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(expected, parsedArgsList);
        });
    }

    /**
     * Test case to verify parsing of a double-quoted argument containing backquotes.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_DoubleQuoteWithBackQuote_OutputMatchesExpected() {
        List<String> input = List.of("\"This is space: `echo \"\"`.\"");
        List<String> expected = List.of("This is space: .");
        assertDoesNotThrow(() -> {
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(expected, parsedArgsList);
        });
    }

    /**
     * Test case to verify parsing of a single-quoted argument containing a backquoted expression.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_SingleQuoteWithBackQuote_OutputMatchesExpected() {
        List<String> input = List.of("'Travel time Singapore -> Paris is 13h and 15`'");
        List<String> expected = List.of("Travel time Singapore -> Paris is 13h and 15`");
        assertDoesNotThrow(() -> {
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(expected, parsedArgsList);
        });
    }

    /**
     * Test case to verify parsing of a double-quoted argument containing a single-quoted string.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_DoubleQuoteWithSingleQuote_OutputMatchesExpected() {
        List<String> input = List.of("\"This is space:' '\".");
        List<String> expected = List.of("This is space:' '.");
        assertDoesNotThrow(() -> {
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(expected, parsedArgsList);
        });
    }

    /**
     * Test case to verify parsing of a single-quoted string within double quotes.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_SingleQuoteDisabledByDoubleQuote_OutputMatchesExpected() {
        List<String> input = List.of("\"'This is space `echo \" \"`'\"");
        List<String> expected = List.of("'This is space  '");
        assertDoesNotThrow(() -> {
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(expected, parsedArgsList);
        });
    }

    /**
     * Test case to verify parsing of a single-quoted string disabling double quotes and backquotes.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_SingleQuoteDisablesDoubleQuoteAndBackquote_OutputMatchesExpected() {
        List<String> input = List.of("'This is space `echo \" \"`'");
        List<String> expected = List.of("This is space `echo \" \"`");
        assertDoesNotThrow(() -> {
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(expected, parsedArgsList);
        });
    }

    /**
     * Test case to verify parsing of an unquoted asterisk as a wildcard.
     *
     * @throws Exception if an error occurs during parsing
     */
    @Test
    void parseArguments_UnquotedAsterisk_HandlesAsWildcard() throws Exception {
        List<String> input = List.of("file*.txt");
        List<String> resolvedArguments = argumentResolver.parseArguments(input);
        assertEquals(Collections.singletonList("file*.txt"), resolvedArguments);
    }

}
