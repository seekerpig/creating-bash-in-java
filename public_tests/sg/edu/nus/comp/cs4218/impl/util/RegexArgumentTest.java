package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RegexArgumentTest {

    private RegexArgument regexArgument;

    @BeforeEach
    void setUp() {
        regexArgument = new RegexArgument();
    }

    @AfterEach
    void tearDown() throws IOException {
        Path subFolderPath = Paths.get(Environment.currentDirectory).resolve("testResources"); //NOPMD - suppressed AvoidDuplicateLiterals - Readability instead of using a global variable
        deleteDirectoryRecursively(subFolderPath);
    }

    /**
     * Deletes the given directory recursively.
     *
     * @param path the path to the directory to be deleted
     * @throws IOException if an I/O error occurs
     */
    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    /**
     * Generates a random string of lowercase letters of the specified length.
     *
     * @param length the length of the string to generate
     * @return a string containing random lowercase letters
     */
    private String generateRandomContent(int length) {
        int leftLimit = 97;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Tests appending a lowercase letter to the RegexArgument.
     * Verifies the character is appended correctly and the regex string is updated.
     */
    @Test
    void append_lowercaseLetter_ShouldContainAppendedChar() {
        regexArgument.append('a');
        assertEquals("a", regexArgument.toString());
        assertEquals(Pattern.quote("a"), regexArgument.getRegexString());
    }

    /**
     * Tests appending an uppercase letter to the RegexArgument.
     * Verifies the character is appended correctly and the regex string is updated.
     */
    @Test
    void append_uppercaseLetter_ShouldContainAppendedChar() {
        regexArgument.append('A');
        assertEquals("A", regexArgument.toString());
        assertEquals(Pattern.quote("A"), regexArgument.getRegexString());
    }

    /**
     * Tests appending a number to the RegexArgument.
     * Verifies the character is appended correctly and the regex string is updated.
     */
    @Test
    void append_Number_ShouldContainAppendedChar() {
        regexArgument.append('0');
        assertEquals("0", regexArgument.toString());
        assertEquals(Pattern.quote("0"), regexArgument.getRegexString());
    }

    /**
     * Tests appending multiple values to the RegexArgument.
     * Verifies the characters are appended correctly and the regex string is updated.
     */
    @Test
    void append_MultipleValues_ShouldContainAppendedChar() {
        regexArgument.append('A');
        regexArgument.append('z');
        regexArgument.append('0');
        assertEquals("Az0", regexArgument.toString());
        assertEquals((Pattern.quote("A") + Pattern.quote("z") + Pattern.quote("0")), regexArgument.getRegexString());
    }

    /**
     * Tests appending an asterisk to the RegexArgument.
     * Verifies the asterisk is appended correctly, the regex string is updated, and regex check passes.
     */
    @Test
    void appendAsterisk_called_ShouldContainAsterisk() {
        regexArgument.appendAsterisk();
        assertEquals("*", regexArgument.toString());
        assertEquals("[^" + StringUtils.fileSeparator() + "]*", regexArgument.getRegexString());
        assertTrue(regexArgument.checkRegex());
    }

    /**
     * Tests merging another RegexArgument with a false regex flag into this RegexArgument.
     * Verifies the merged values are correct and the regex check fails.
     */
    @Test
    void merge_withRegexThatAreFalse_shouldContainCorrectValues() {
        String currentDir = Environment.currentDirectory;
        RegexArgument appendArgument = new RegexArgument("someFile", currentDir, false); //NOPMD - suppressed AvoidDuplicateLiterals - Readability instead of using a global variable
        regexArgument.merge(appendArgument);

        assertEquals(appendArgument.toString(), regexArgument.toString());
        assertEquals(appendArgument.getRegexString(), regexArgument.getRegexString());
        assertFalse(regexArgument.checkRegex());
    }

    /**
     * Tests merging with a RegexArgument that has a true regex flag. Verifies that
     * the original RegexArgument contains the correct values after the merge.
     */
    @Test
    void merge_withRegexThatAreTrue_shouldContainCorrectValues() {
        String currentDir = Environment.currentDirectory;
        RegexArgument appendArgument = new RegexArgument("someFile", currentDir, true);
        regexArgument.merge(appendArgument);

        assertEquals(appendArgument.toString(), regexArgument.toString());
        assertEquals(appendArgument.getRegexString(), regexArgument.getRegexString());
        assertTrue(regexArgument.checkRegex());
    }

    /**
     * Tests merging with a plain string. Verifies that the RegexArgument
     * correctly contains the merged string and its corresponding regex representation.
     */
    @Test
    void merge_withString_ShouldContainCorrectValues() {
        regexArgument.merge("hello"); //NOPMD - suppressed AvoidDuplicateLiterals - Readability instead of using a global variable
        assertEquals("hello", regexArgument.toString());
        assertEquals(Pattern.quote("hello"), regexArgument.getRegexString());
    }

    /**
     * Tests merging with a string that has a space prefix. Verifies that the RegexArgument
     * correctly includes the prefixed space in both its string and regex representation.
     */
    @Test
    void merge_withStringWithSpacePrefixed_ShouldContainCorrectValues() {
        regexArgument.merge(" hello");
        assertEquals(" hello", regexArgument.toString());
        assertEquals(Pattern.quote(" hello"), regexArgument.getRegexString());
    }

    /**
     * Tests the behavior of merging with a string using the built-in constructor.
     * Verifies that the RegexArgument correctly initializes with the provided string and its regex representation.
     */
    @Test
    void merge_withStringAndBuiltInConstructor_ShouldContainCorrectValues() {
        regexArgument = new RegexArgument("hello");
        assertEquals("hello", regexArgument.toString());
        assertEquals(Pattern.quote("hello"), regexArgument.getRegexString());
    }

    /**
     * Tests globbing files with a non-regex pattern. Verifies that only the plain text string item is returned.
     */
    @Test
    void globFiles_NonRegex_ShouldReturnOnlyThePlainTextStringItem() {
        String currentDir = Environment.currentDirectory;

        regexArgument = new RegexArgument(currentDir, "test", false);
        assertEquals(List.of("test"), regexArgument.globFiles());
    }

    /**
     * Tests globbing files with a regex pattern that matches existing files. Verifies that all matching files are returned.
     */
    @Test
    void globFiles_RegexIsTrueAndThereAreMatchingItems_ShouldReturnAllMatchingFiles() throws IOException {
        for (int i = 0; i < 5; i++) {
            String fileName = "testFile" + i + ".txt"; //NOPMD - suppressed AvoidDuplicateLiterals - Readability instead of using a global variable

            Path currPath = Paths.get(Environment.currentDirectory);
            Path subFolderPath = currPath.resolve("testResources");
            Files.createDirectories(subFolderPath);
            Path pathToFile = subFolderPath.resolve(fileName);


            String content = generateRandomContent(15); //
            Files.writeString(pathToFile, content);

            // System.out.println(Files.readString(pathToFile));
        }

        regexArgument = new RegexArgument("testFile*", "testResources" + File.separator + "testFile", true);
        assertEquals(List.of("testResources" + File.separator + "testFile0.txt",
                "testResources" + File.separator + "testFile1.txt",
                "testResources" + File.separator + "testFile2.txt",
                "testResources" + File.separator + "testFile3.txt",
                "testResources" + File.separator + "testFile4.txt"), regexArgument.globFiles());
    }

    /**
     * Tests globbing files with a regex pattern that does not match any existing files.
     * Verifies that no files are returned when there are no matching items.
     */
    @Test
    void globFiles_RegexIsTrueAndThereAreNoMatchingItems_ShouldReturnNoMatchingFiles() throws IOException {
        for (int i = 0; i < 5; i++) {
            String fileName = "testFile" + i + ".txt";

            Path currPath = Paths.get(Environment.currentDirectory);
            Path subFolderPath = currPath.resolve("testResources");
            Files.createDirectories(subFolderPath);
            Path pathToFile = subFolderPath.resolve(fileName);


            String content = generateRandomContent(15); //
            Files.writeString(pathToFile, content);

            // System.out.println(Files.readString(pathToFile));
        }

        regexArgument = new RegexArgument("testFile10.txt", "testResources" + File.separator + "testFile10.txt", true); //NOPMD - suppressed AvoidDuplicateLiterals - Readability instead of using a global variable
        assertEquals(List.of("testResources" + File.separator + "testFile10.txt"), regexArgument.globFiles());
    }

    /**
     * Tests globbing files with a regex pattern and absolute path that matches existing files.
     * Verifies that all matching files are returned with their absolute paths.
     */
    @Test
    void globFiles_RegexIsTrueAndThereAreMatchingItemsAndAbsolutePath_ShouldReturnMatchingFiles() throws IOException {
        Path currPath = Paths.get(Environment.currentDirectory);
        Path subFolderPath = currPath.resolve("testResources");
        Files.createDirectories(subFolderPath);

        for (int i = 0; i < 5; i++) {
            String fileName = "testFile" + i + ".txt";

            Path pathToFile = subFolderPath.resolve(fileName);

            String content = generateRandomContent(15); //
            Files.writeString(pathToFile, content);

            // System.out.println(Files.readString(pathToFile));
        }

        regexArgument = new RegexArgument("testFile*", subFolderPath.toAbsolutePath().toString() + File.separator + "testFile10.txt", true);
        assertEquals(List.of(subFolderPath.toAbsolutePath().toString() + File.separator + "testFile0.txt",
                subFolderPath.toAbsolutePath().toString() + File.separator + "testFile1.txt",
                subFolderPath.toAbsolutePath().toString() + File.separator + "testFile2.txt",
                subFolderPath.toAbsolutePath().toString() + File.separator + "testFile3.txt",
                subFolderPath.toAbsolutePath().toString() + File.separator + "testFile4.txt"), regexArgument.globFiles());

    }

    /**
     * Tests checking the regex flag when it is set to true. Verifies that the method returns true.
     */
    @Test
    void checkRegex_True_ReturnTrue() {
        String currentDir = Environment.currentDirectory;
        RegexArgument argument = new RegexArgument("someFile", currentDir, true);
        assertTrue(argument.checkRegex());
    }

    /**
     * Tests checking the regex flag when it is set to false. Verifies that the method returns false.
     */
    @Test
    void checkRegex_False_ReturnFalse() {
        String currentDir = Environment.currentDirectory;
        RegexArgument argument = new RegexArgument("someFile", currentDir, false);
        assertFalse(argument.checkRegex());
    }

    /**
     * Tests the isEmpty method on a newly constructed RegexArgument. Verifies that it returns true.
     */
    @Test
    void isEmpty_EmptyConstructed_ReturnTrue() {
        assertTrue(regexArgument.isEmpty());
    }

    /**
     * Tests the isEmpty method on a RegexArgument constructed with a non-empty string.
     * Verifies that it returns false.
     */
    @Test
    void isEmpty_ConstructedWithString_ReturnTrue() {
        regexArgument = new RegexArgument("hello");
        assertFalse(regexArgument.isEmpty());
    }

    /**
     * Tests the toString method on an empty RegexArgument. Verifies that it returns an empty string.
     */
    @Test
    void toString_empty_shouldReturnEmptyString() {
        assertEquals("", regexArgument.toString());
    }

    /**
     * Tests the toString method on a RegexArgument merged with a non-empty string.
     * Verifies that it returns the non-empty string.
     */
    @Test
    void toString_nonEmptyString_shouldNonEmptyString() {
        regexArgument.merge("hello");
        assertEquals("hello", regexArgument.toString());
    }

    /**
     * Tests the getRegexString method on an empty RegexArgument.
     * Verifies that it returns an empty regex string.
     */
    @Test
    void getRegexString_EmptyString_shouldNonEmptyRegexString() {
        assertEquals("", regexArgument.getRegexString());
    }

    /**
     * Tests the getRegexString method on a RegexArgument merged with a non-empty string.
     * Verifies that it returns a non-empty regex string corresponding to the string.
     */
    @Test
    void getRegexString_nonEmptyString_shouldNonEmptyRegexString() {
        regexArgument.merge("hello");
        assertEquals(Pattern.quote("hello"), regexArgument.getRegexString());
    }
}