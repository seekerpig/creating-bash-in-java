package sg.edu.nus.comp.cs4218.impl.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IORedirectionHandlerTest {

    private static final String INPUT_FILE_1 = "iohandler_input1.txt";
    private static final String INPUT_FILE_2 = "iohandler_input2.txt";
    private static final String INPUT_FILE_3 = "iohandler_input3.txt";
    private static final String OUTPUT_FILE = "iohandler_output.txt";
    private static final String[] FILE_NAMES = {INPUT_FILE_1, INPUT_FILE_2, INPUT_FILE_3, OUTPUT_FILE};
    private static final String CMD = "cmd";
    private static final String EXC_MSG = "Exception thrown: ";
    private static InputStream inputStream;
    private static OutputStream outputStream;

    @AfterEach
    void tearDown() throws IOException {
        for (String fileName : FILE_NAMES) {
            Path currentPath = Paths.get(Environment.currentDirectory);
            Path filePath = currentPath.resolve(fileName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                fail("Failed to delete file: " + e.getMessage());
            }
        }
    }

    /**
     * Test case to verify that calling {@link IORedirectionHandler#getInputStream()} when no input redirection is specified
     * should return the {@link System#in} input stream.
     */
    @Test
    public void testGetInputStream_NoRedir_ShouldReturnSystemIn() {
        // Prepare test data
        List<String> argsList = Arrays.asList(CMD, "arg1", "arg2");
        inputStream = System.in;
        outputStream = System.out;
        ArgumentResolver argumentResolver = mock(ArgumentResolver.class);

        // Create instance of IORedirectionHandler
        IORedirectionHandler handler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        // Verify input stream
        assertEquals(inputStream, handler.getInputStream());
    }

    /**
     * Test case to verify that calling {@link IORedirectionHandler#getOutputStream()} when no output redirection is specified
     * should return the {@link System#out} output stream.
     */
    @Test
    @DisabledOnOs(OS.WINDOWS) // Bugged on Windows
    public void testGetOutputStream_NoRedir_ShouldReturnSystemOut() {
        // Prepare test data
        List<String> argsList = Arrays.asList(CMD, "arg1", "arg2");
        inputStream = System.in;
        outputStream = System.out;
        ArgumentResolver argumentResolver = mock(ArgumentResolver.class);

        // Create instance of IORedirectionHandler
        IORedirectionHandler handler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        // Verify output stream
        assertEquals(outputStream, handler.getOutputStream());
    }


    /**
     * Test case to verify that calling {@link IORedirectionHandler#extractRedirOptions()} when no input/output redirection is specified
     * should not change the input and output streams.
     */
    @Test
    public void testExtractRedirOptions_noRedir_shouldHaveFullRedirArgsListAndUnchangedInputOutput() {
        // Prepare test data
        List<String> argsList = Arrays.asList(CMD, "arg1", "arg2");
        inputStream = System.in;
        outputStream = System.out;
        ArgumentResolver argumentResolver = mock(ArgumentResolver.class);

        // Create instance of IORedirectionHandler
        IORedirectionHandler handler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        // Call method to extract redirection options
        try {
            handler.extractRedirOptions();
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail(EXC_MSG + e.getMessage());
        }

        // Verify no redirection
        assertEquals(argsList, handler.getNoRedirArgsList());
        assertEquals(inputStream, handler.getInputStream());
        assertEquals(outputStream, handler.getOutputStream());
    }

    /**
     * Test case to verify that calling {@link IORedirectionHandler#extractRedirOptions()} when input redirection is specified
     * should change the input stream to the contents of the specified input file.
     *
     * @throws FileNotFoundException        if the input file is not found
     * @throws AbstractApplicationException if an abstract application error occurs
     * @throws ShellException               if a shell-related error occurs
     */
    @Test
    @DisabledOnOs(OS.WINDOWS) // Bugged on Windows
    public void testExtractRedirOptions_withInputRedir_shouldChangeInput() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // Prepare test data
        String inputFileContent = "content from iohandler_input.txt";
        String origStreamContent = "content from original stream";
        List<String> argsList = Arrays.asList(CMD, "<", INPUT_FILE_1);
        inputStream = new ByteArrayInputStream(origStreamContent.getBytes());
        outputStream = new ByteArrayOutputStream();
        createFile(INPUT_FILE_1, inputFileContent);
        ArgumentResolver argumentResolver = mock(ArgumentResolver.class);
        when(argumentResolver.resolveOneArgument(INPUT_FILE_1)).thenReturn(Arrays.asList(INPUT_FILE_1));

        IORedirectionHandler handler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        try {
            handler.extractRedirOptions();
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail(EXC_MSG + e.getMessage());
        }

        // check contents of input stream
        byte[] buffer = new byte[1024];
        int length;
        try {
            length = handler.getInputStream().read(buffer);
            assertEquals(inputFileContent, new String(buffer, 0, length));
        } catch (IOException e) {
            fail("Failed to read from input stream: " + e.getMessage());
        }
    }

    // test two input redirection, should only get last input redirection
    @Test
    @DisabledOnOs(OS.WINDOWS) // Bugged on Windows
    public void testExtractRedirOptions_twoInputRedirection_shouldChangeInputWithLast() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // Prepare test data
        List<String> argsList = Arrays.asList(CMD, "<", INPUT_FILE_2, "<", INPUT_FILE_3);
        inputStream = System.in;
        createFile(INPUT_FILE_2, "content from iohandler_input1.txt");
        createFile(INPUT_FILE_3, "content from iohandler_input2.txt");

        outputStream = new ByteArrayOutputStream();
        ArgumentResolver argumentResolver = mock(ArgumentResolver.class);
        when(argumentResolver.resolveOneArgument(INPUT_FILE_2)).thenReturn(Arrays.asList(INPUT_FILE_2));
        when(argumentResolver.resolveOneArgument(INPUT_FILE_3)).thenReturn(Arrays.asList(INPUT_FILE_3));

        // Create instance of IORedirectionHandler
        IORedirectionHandler handler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        // Call method to extract redirection options
        try {
            handler.extractRedirOptions();
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail(EXC_MSG + e.getMessage());
        }

        // Verify input stream redirection by reading content from input stream
        byte[] buffer = new byte[1024];
        int length;
        try {
            length = handler.getInputStream().read(buffer);
            assertEquals("content from iohandler_input2.txt", new String(buffer, 0, length));
        } catch (IOException e) {
            fail("Failed to read from input stream: " + e.getMessage());
        }
        assertEquals(outputStream, handler.getOutputStream());
    }

    // test output redirection, should write to file
    @DisabledOnOs(OS.WINDOWS) // Bugged on Windows
    @Test
    public void testExtractRedirOptions_withOutputRedir_ShouldChangeOutput() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // Prepare test data
        List<String> argsList = Arrays.asList(CMD, ">", OUTPUT_FILE);
        inputStream = System.in;
        createFile(OUTPUT_FILE, "");
        outputStream = new ByteArrayOutputStream();
        ArgumentResolver argumentResolver = mock(ArgumentResolver.class);
        when(argumentResolver.resolveOneArgument(OUTPUT_FILE)).thenReturn(Arrays.asList(OUTPUT_FILE));

        // Create instance of IORedirectionHandler
        IORedirectionHandler handler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        // Call method to extract redirection options
        try {
            handler.extractRedirOptions();
        } catch (AbstractApplicationException | ShellException | FileNotFoundException e) {
            fail(EXC_MSG + e.getMessage());
        }

        String outputContent = "content to be written to iohandler_output2.txt";
        try {
            handler.getOutputStream().write(outputContent.getBytes());
        } catch (IOException e) {
            fail("Failed to write to output stream: " + e.getMessage());
        }
        // read contents of iohandler_output2.txt at correct path, Environment.currentDirectory + File.separator + OUTPUT_FILE
        Path currentPath = Paths.get(Environment.currentDirectory);
        Path filePath = currentPath.resolve(OUTPUT_FILE);
        String fileContent;
        try {
            fileContent = Files.readString(filePath);
            assertEquals(outputContent, fileContent);
        } catch (IOException e) {
            fail("Failed to read from file: " + e.getMessage());
        }
        assertEquals(inputStream, handler.getInputStream());
    }

    // test arglist is null
    @Test
    public void testExtractRedirOptions_nullArgsList_shouldThrowException() {
        List<String> args = null;
        ArgumentResolver resolver = new ArgumentResolver();
        IORedirectionHandler handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        Throwable thrown = assertThrows(ShellException.class, () -> {
            handler.extractRedirOptions();
        });
        assertEquals("shell: Invalid syntax", thrown.getMessage());
    }

    // test arglist is empty
    @Test
    public void testExtractRedirOptions_EmptyArgsList_shouldThrowException() {
        List<String> args = Arrays.asList();
        ArgumentResolver resolver = new ArgumentResolver();
        IORedirectionHandler handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        Throwable thrown = assertThrows(ShellException.class, () -> {
            handler.extractRedirOptions();
        });
        assertEquals("shell: Invalid syntax", thrown.getMessage());
    }


    // Method to programmatically create a file with given name and content
    private void createFile(String fileName, String content) {
        Path currentPath = Paths.get(Environment.currentDirectory);
        Path filePath = currentPath.resolve(fileName);
        try {
            Files.write(filePath, content.getBytes());
        } catch (IOException e) {
            fail("Failed to create file: " + e.getMessage());
        }
    }
}