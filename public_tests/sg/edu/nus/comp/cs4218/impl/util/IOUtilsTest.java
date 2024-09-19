package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_CLOSING_STREAMS;

class IOUtilsTest {

    private String currentDirectory = Environment.currentDirectory;
    private File someFile;

    /**
     * Sets up the test environment before each test case. This includes setting the current directory
     * and creating a new file named "someFile.txt" within that directory.
     *
     * @throws IOException If an I/O error occurs
     */
    @BeforeEach
    void setUp() throws IOException {
        currentDirectory = Environment.currentDirectory;

        Path path = Paths.get(currentDirectory, "someFile.txt");

        someFile = new File(path.toString());
        someFile.createNewFile();

    }

    /**
     * Cleans up the test environment after each test case. This includes deleting the "someFile.txt"
     * file that was created during setup.
     *
     * @throws IOException If an I/O error occurs
     */
    @AfterEach
    void deleteTestResources() throws IOException {
        someFile.delete();
    }


    /**
     * Tests attempting to open an input stream for an invalid file. Verifies that a ShellException
     * is thrown to indicate the file could not be found or accessed.
     */
    @Test
    void openInputStream_invalidFile_ShouldThrowShellException() {
        assertThrows(ShellException.class, () -> IOUtils.openInputStream(currentDirectory + File.separator + "randomFile.txt"));
    }

    /**
     * Tests opening an input stream for a valid file. Verifies that an InputStream object is returned
     * and can be closed without issues.
     *
     * @throws ShellException If a shell utility exception occurs
     * @throws IOException    If an I/O error occurs
     */
    @Test
    void openInputStream_invalidFile_ShouldReturnInputStream() throws ShellException, IOException {
        InputStream inputStream = IOUtils.openInputStream(someFile.getPath()); //NOPMD - suppressed CloseResource - Stream is properly closed.
        assertInstanceOf(InputStream.class, inputStream);
        inputStream.close();
    }

    /**
     * Tests attempting to open an output stream with an invalid file path. Verifies that a
     * FileNotFoundException is thrown.
     */
    @Test
    void openOutputStream_invalidFile_ShouldThrowFileNotFoundException() {
        assertThrows(FileNotFoundException.class, () -> IOUtils.openOutputStream(""));
    }

    /**
     * Tests opening an output stream for a valid file. Verifies that an OutputStream object is
     * returned and can be closed without issues.
     *
     * @throws IOException    If an I/O error occurs
     * @throws ShellException If a shell utility exception occurs
     */
    @Test
    void openOutputStream_validFile_ShouldReturnOutputStream() throws IOException, ShellException {
        OutputStream outputStream = IOUtils.openOutputStream(someFile.getAbsolutePath()); //NOPMD - suppressed CloseResource - Stream is properly closed.
        assertInstanceOf(OutputStream.class, outputStream);
        outputStream.close();
    }

    /**
     * Tests closing an input stream when an IOException is thrown during the close operation.
     * Verifies that a ShellException is thrown as a result.
     *
     * @throws IOException If an I/O error occurs during setup
     */
    @Test
    void closeInputStream_GivenInvalidInput_ThrowsException() throws IOException {
        InputStream mockedInputStream = mock(InputStream.class); //NOPMD - suppressed CloseResource - Stream is not real, its mocked and closed.
        doThrow(IOException.class).when(mockedInputStream).close();

        assertThrows(ShellException.class, () -> IOUtils.closeInputStream(mockedInputStream));

        verify(mockedInputStream).close();
    }

    /**
     * Tests closing an input stream under normal conditions. Verifies that no exception is thrown
     * during the close operation.
     */
    @Test
    void closeInputStream_GivenValidInput_DoNotThrowException() {
        InputStream inputStream = mock(InputStream.class); //NOPMD - suppressed CloseResource - Stream is not real, its mocked and closed.

        assertDoesNotThrow(() -> IOUtils.closeInputStream(inputStream));
    }

    /**
     * Tests closing an output stream under normal conditions. Verifies that no exception is thrown
     * during the close operation.
     *
     * @throws IOException If an I/O error occurs during setup
     */
    @Test
    void closeOutputStream_GivenValidOutput_DoesNotThrowException() throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> IOUtils.closeOutputStream(outputStream));
        outputStream.close();
    }

    /**
     * Tests closing an output stream when an IOException is thrown during the close operation.
     * Verifies that a ShellException is thrown as a result.
     *
     * @throws IOException If an I/O error occurs during setup
     */
    @Test
    void closeOutputStream_GivenOutputStreamThrowsIOException_ShouldThrowShellException() throws IOException {
        OutputStream outputStream = mock(OutputStream.class); //NOPMD - suppressed CloseResource - Stream is not real, its mocked and closed.
        doThrow(IOException.class).when(outputStream).close();

        assertThrows(ShellException.class, () -> IOUtils.closeOutputStream(outputStream));

        verify(outputStream).close();
    }

    /**
     * Tests resolving a file path given a file name. Verifies that the method returns the correct
     * path to the file within the current directory.
     *
     * @throws IOException If an I/O error occurs
     */
    @Test
    void resolveFilePath_GivenFileName_ShouldReturnValidPath() throws IOException {
        assertEquals(Paths.get(currentDirectory + File.separator + "someFile.txt"), IOUtils.resolveFilePath("someFile.txt"));
    }

    /**
     * Tests getting lines from an input stream that contains a single line of text. Verifies that
     * the method returns a list containing the correct line of text.
     *
     * @throws IOException If an I/O error occurs
     */
    @Test
    void getLinesFromInputStream_GivenInputStreamWithValidInputs_ShouldReturnValidString() throws IOException {
        String text = "abc";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());

        List<String> result = IOUtils.getLinesFromInputStream(inputStream);
        List<String> expectedResult = List.of("abc");
        assertEquals(expectedResult.toString(), result.toString());
        inputStream.close();
    }

    /**
     * Tests getting lines from an input stream that contains multiple lines of text separated by
     * system line separators. Verifies that the method returns a list containing the correct lines
     * of text.
     *
     * @throws IOException If an I/O error occurs
     */
    @Test
    void getLinesFromInputStream_GivenInputStreamWithMultipleInputs_ShouldReturnValidString() throws IOException {
        String text = "Hello" + System.lineSeparator() + "World";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());

        List<String> result = IOUtils.getLinesFromInputStream(inputStream);
        List<String> expectedResult = List.of("Hello", "World");
        assertEquals(expectedResult.toString(), result.toString());
        inputStream.close();
    }
}