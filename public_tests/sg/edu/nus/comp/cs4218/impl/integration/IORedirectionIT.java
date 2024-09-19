package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

// TODO: test for input files with input redirection together
public class IORedirectionIT { // NOPMD

    Shell shell;
    ByteArrayOutputStream outputStream;

    private static final String INPUT_FILE_1 = "ioit_input.txt";
    private static final String INPUT_FILE_2 = "ioit_input1.txt";
    private static final String INPUT_FILE_3 = "ioit_input2.txt";
    private static final String OUTPUT_FILE_1 = "ioit_output.txt";
    private static final String OUTPUT_FILE_2 = "ioit_output2.txt";
    private static final String[] FILE_NAMES = {INPUT_FILE_1, INPUT_FILE_2, INPUT_FILE_3, OUTPUT_FILE_1, OUTPUT_FILE_2};

    private static final String STD_INPUT = "(standard input):";
    private static final String CONTENT_STR_1 = "hello";
    private static final String CONTENT_STR_2 = "world";
    private static final String EXC_MSG = "Failed to run command: ";
    private static final String FILE_FAIL_MSG = "Failed to read file: ";


    @BeforeEach
    public void setUp() {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() {
        // cleanup
        for (String fileName : FILE_NAMES) {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(fileName);
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    fail("Failed to delete file: " + e.getMessage());
                }
            }
        }
    }

    private void createFile(String fileName, String content) {
        Path currentPath = Paths.get(Environment.currentDirectory);
        Path filePath = currentPath.resolve(fileName);
        try {
            Files.write(filePath, content.getBytes());
        } catch (IOException e) {
            fail("Failed to create file: " + e.getMessage());
        }
    }


    // test input redirection
    @Test
    public void wc_WithInputRedirection_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1);
        String command = "wc < ioit_input.txt";
        // tab separated
        String expectedOutput = "\t0\t1\t5" + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput, outputStream.toString());
    }

    // sort input redir
    @Test
    public void sort_WithInputRedirection_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_1, "c" + System.lineSeparator() + "b" + System.lineSeparator() + "a");
        String command = "sort < ioit_input.txt";
        String expectedOutput = "a" + System.lineSeparator() + "b" + System.lineSeparator() + "c" + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void cat_WithInputRedirection_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1);
        String command = "cat < ioit_input.txt";
        String expectedOutput = CONTENT_STR_1 + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput, outputStream.toString());
    }

    // paste input redir
    @Test
    public void paste_WithInputRedirection_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_2, "a" + System.lineSeparator() + "b" + System.lineSeparator() + "c");
        createFile(INPUT_FILE_3, "1" + System.lineSeparator() + "2" + System.lineSeparator() + "3");
        String command = "paste ioit_input1.txt - < ioit_input2.txt";
        String expectedOutput = "a\t1" + System.lineSeparator() + "b\t2" + System.lineSeparator() + "c\t3" + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput, outputStream.toString());
    }

    // cut input redir
    @Test
    public void cut_WithInputRedirection_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1);
        String command = "cut -c 1 < ioit_input.txt";
        String expectedOutput = "h" + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput, outputStream.toString());
    }

    // TODO: tee creating undeleted ioit_output.txt
    @Test
    public void tee_WithInputRedirection_ShouldReturnCorrectOutput() {
        outputStream = new ByteArrayOutputStream();
        createFile(INPUT_FILE_1, CONTENT_STR_1);
        String command = "tee ioit_output.txt < ioit_input.txt";
        String expectedOutput = CONTENT_STR_1;
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput + StringUtils.STRING_NEWLINE, outputStream.toString());
        // check if the file is created and contains the correct output
        try {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(OUTPUT_FILE_1);
            String content = Files.readString(filePath);
            assertEquals(expectedOutput, content);
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

    // grep input redir
    @Test
    public void grep_WithInputRedirection_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1 + System.lineSeparator() + CONTENT_STR_2);
        String command = "grep -H hello < ioit_input.txt";
        String expectedOutput = STD_INPUT + CONTENT_STR_1 + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput, outputStream.toString());
    }


    // test output redirection
    // echo, wc, sort, cat, paste, cut, tee, grep

    @Test
    public void echo_WithOutputRedirection_ShouldCreateFileWithCorrectOutput() {
        String command = "echo hello > ioit_output.txt";
        String expectedOutput = CONTENT_STR_1 + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, System.out);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        // check if the file is created and contains the correct output
        try {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(OUTPUT_FILE_1);
            String content = Files.readString(filePath);
            assert (content.equals(expectedOutput));
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

    @Test
    public void wc_WithOutputRedirection_ShouldCreateFileWithCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1);
        String command = "wc ioit_input.txt > ioit_output.txt";
        String expectedOutput = "\t0\t1\t5 ioit_input.txt" + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, System.out);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        // check if the file is created and contains the correct output
        try {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(OUTPUT_FILE_1);
            String content = Files.readString(filePath);
            assertEquals(expectedOutput, content);
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

    @Test
    public void sort_WithOutputRedirection_ShouldCreateFileWithCorrectOutput() {
        createFile(INPUT_FILE_1, "c" + System.lineSeparator() + "b" + System.lineSeparator() + "a");
        String command = "sort ioit_input.txt > ioit_output.txt";
        String expectedOutput = "a" + System.lineSeparator() + "b" + System.lineSeparator() + "c" + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, System.out);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        // check if the file is created and contains the correct output
        try {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(OUTPUT_FILE_1);
            String content = Files.readString(filePath);
            assertEquals(expectedOutput, content);
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

//    @Test
//    public void cat_WithOutputRedirection_ShouldCreateFileWithCorrectOutput() {
//        // create ioit_input.txt file
//        try {
//            Files.writeString(Paths.get(INPUT_FILE_1), CONTENT_STR_1);
//        } catch (Exception e) {
//            fail("Failed to create file: " + e.getMessage());
//        }
//        String command = "cat ioit_input.txt > ioit_output.txt";
//        String expectedOutput = CONTENT_STR_1;
//        try {
//            shell.parseAndEvaluate(command, System.out);
//        } catch (Exception e) {
//            fail(EXC_MSG + e.getMessage());
//        }
//        // check if the file is created and contains the correct output
//        try {
//            String content = Files.readString(Paths.get(OUTPUT_FILE_1));
//            assert (content.equals(expectedOutput));
//        } catch (Exception e) {
//            fail(FILE_FAIL_MSG + e.getMessage());
//        }
//    }

    @Test
    public void cut_WithOutputRedirection_ShouldCreateFileWithCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1);
        String command = "cut -c 1 ioit_input.txt > ioit_output.txt";
        String expectedOutput = "h" + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, System.out);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        // check if the file is created and contains the correct output
        try {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(OUTPUT_FILE_1);
            String content = Files.readString(filePath);
            assertEquals(expectedOutput, content);
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

//    @Test
//    public void tee_WithOutputRedirection_ShouldCreateFileWithCorrectOutput() {
//        try {
//            Files.writeString(Paths.get(INPUT_FILE_1), CONTENT_STR_1);
//        } catch (Exception e) {
//            fail("Failed to create file: " + e.getMessage());
//        }
//        String command = "tee ioit_input.txt > ioit_output.txt";
//        String expectedOutput = CONTENT_STR_1;
//        try {
//            shell.parseAndEvaluate(command, System.out);
//        } catch (Exception e) {
//            fail(EXC_MSG + e.getMessage());
//        }
//        // check if the file is created and contains the correct output
//        try {
//            String content = Files.readString(Paths.get(OUTPUT_FILE_1));
//            assert (content.equals(expectedOutput));
//        } catch (Exception e) {
//            fail(FILE_FAIL_MSG + e.getMessage());
//        }
//    }

    @Test
    public void grep_WithOutputRedirection_ShouldCreateFileWithCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1 + System.lineSeparator() + CONTENT_STR_2);
        String command = "grep hello ioit_input.txt > ioit_output.txt";
        String expectedOutput = CONTENT_STR_1 + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, System.out);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        // check if the file is created and contains the correct output
        try {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(OUTPUT_FILE_1);
            String content = Files.readString(filePath);
            assert (content.equals(expectedOutput));
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

    // test both input and output redirection


    // test corner cases, double input redirection, etc
    // double input redir
    @Test
    public void cat_WithDoubleInputRedirection_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_2, CONTENT_STR_1);
        createFile(INPUT_FILE_3, CONTENT_STR_2);
        String command = "cat < ioit_input1.txt < ioit_input2.txt";
        String expectedOutput = CONTENT_STR_2 + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        assertEquals(expectedOutput, outputStream.toString());
    }

    // double output redir

    // TODO: double output redir creates extra file
    @Test
    public void echo_WithDoubleOutputRedirection_ShouldThrowException() {
        String command = "echo hello > ioit_output.txt > ioit_output2.txt";
        String expectedOutput = CONTENT_STR_1 + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, System.out);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        // check if the file is created and contains the correct output
        try {
            String cwd = Environment.currentDirectory;
            String content = Files.readString(Paths.get(cwd).resolve(OUTPUT_FILE_2));
            assertEquals(expectedOutput, content);
            // assert ioit_output.txt is not created
            assertFalse(Files.exists(Paths.get(cwd).resolve(OUTPUT_FILE_1)));
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

    // output redir before input redir
    @Test
    public void grep_WithOutputRedirBeforeInputRedir_ShouldReturnCorrectOutput() {
        createFile(INPUT_FILE_1, CONTENT_STR_1 + System.lineSeparator() + CONTENT_STR_2);
        String command = "grep hello > ioit_output.txt < ioit_input.txt";
        String expectedOutput = CONTENT_STR_1 + System.lineSeparator();
        try {
            shell.parseAndEvaluate(command, System.out);
        } catch (Exception e) {
            fail(EXC_MSG + e.getMessage());
        }
        // check if the file is created and contains the correct output
        try {
            Path filePath = Paths.get(Environment.currentDirectory).resolve(OUTPUT_FILE_1);
            String content = Files.readString(filePath);
            assertEquals(expectedOutput, content);
        } catch (Exception e) {
            fail(FILE_FAIL_MSG + e.getMessage());
        }
    }

}
