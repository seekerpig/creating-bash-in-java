package sg.edu.nus.comp.cs4218.impl.integration.pipe;

import org.junit.jupiter.api.BeforeEach;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.*;

public class LsApplicationIT { //NOPMD - suppressed ClassNamingConventions - Name is logical and sensible
    private static ShellImpl shell;
    private ByteArrayOutputStream outputStream;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String SAMPLE_FOLDER = "tempFolder" + File.separator + "";
    private static final String SAMPLE_PATH = ROOT_PATH + File.separator + SAMPLE_FOLDER;
    public static final String FIRST_FOLDER = "firstFolder";
    public static final String FIRST_FOLDER_PATH = SAMPLE_PATH + FIRST_FOLDER;
    public static final String FIRST_FILE = "firstFile.txt";
    public static final String FIRST_FILE_PATH = FIRST_FOLDER_PATH + File.separator + FIRST_FILE;
    public static final String SECOND_FILE = "secondFile.txt";
    public static final String SECOND_FILE_PATH = SAMPLE_PATH + SECOND_FILE;
    public static final String[] LINES1 = {"line 1.1", "line 1.2"};
    public static final String[] LINES2 = {"line 2.1", "line 2.2"};
    public static final String SAMPLE_INPUT = "Input line 1" + StringUtils.STRING_NEWLINE + "Input line 2" + StringUtils.STRING_NEWLINE;
    public final InputStream inputStream = new ByteArrayInputStream(SAMPLE_INPUT.getBytes());

    public static void removeDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File currentFile : files) {
                removeDirectory(currentFile);
            }
        }
        directory.delete();
    }

    @BeforeAll
    static void setUp() throws IOException {
        shell = new ShellImpl();
        Environment.currentDirectory = SAMPLE_PATH;
        // remove existing directory
        removeDirectory(new File(SAMPLE_PATH));

        Files.createDirectories(Paths.get(SAMPLE_PATH));
        Files.deleteIfExists(Paths.get(FIRST_FOLDER_PATH));
        Files.createDirectories(Paths.get(FIRST_FOLDER_PATH));
        Files.deleteIfExists(Paths.get(FIRST_FILE_PATH));
        Files.createFile(Paths.get(FIRST_FILE_PATH));

        for (String line : LINES1) {
            Files.write(Paths.get(FIRST_FILE_PATH), (line + StringUtils.STRING_NEWLINE).getBytes(), APPEND);
        }

        Files.deleteIfExists(Paths.get(SECOND_FILE_PATH));
        Files.createFile(Paths.get(SECOND_FILE_PATH));

        for (String line : LINES2) {
            Files.write(Paths.get(SECOND_FILE_PATH), (line + StringUtils.STRING_NEWLINE).getBytes(), APPEND);
        }
    }

    @BeforeEach
    void setUpEach() throws IOException {
        outputStream = new ByteArrayOutputStream();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        removeDirectory(new File(SAMPLE_PATH));
    }


    @Test
    void ls_PipedToRm_DeleteFirstFileAndDisplayEmpty() throws Exception {
        String commandString = String.format("ls %s | rm %s", FIRST_FILE_PATH, FIRST_FILE_PATH);
        String expected = "";
        shell.parseAndEvaluate(commandString, outputStream);
        assertEquals(expected, outputStream.toString());
        File tempFile = new File(FIRST_FILE_PATH);
        assertFalse(tempFile.exists());
    }

    @Test
    void ls_PipedToUniqWithSingleFile_DisplayNothing() throws Exception {
        String commandString = String.format("ls %s | uniq -d", FIRST_FILE_PATH);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, outputStream);
        assertEquals("", outputStream.toString());
    }

    @Test
    void uniq_WithInvalidOptionPipedToLs_ThrowsUniqException() {
        String commandString = String.format("uniq -O | ls %s", FIRST_FOLDER, FIRST_FOLDER);
        assertThrows(UniqException.class, () -> shell.parseAndEvaluate(commandString, outputStream));
    }

    @Test
    void ls_PipedToSortWithNonexistentFile_DisplaysNoSuchFileError() throws Exception {
        String inputString = "ls nonexistent.txt | sort";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        command.evaluate(System.in, outputStream);
        String expected = "ls: cannot access 'nonexistent.txt': No such file or directory";
        assertEquals(expected + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    @Test
    void ls_WithInvalidOption_ThrowsLsException() throws Exception {
        String inputString = "ls -P | sort";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(LsException.class, () -> command.evaluate(System.in, outputStream));
    }

    @Test
    void ls_PipedToSortWithInvalidOption_ThrowsSortException() throws Exception {
        String inputString = "ls | sort -O";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(SortException.class, () -> command.evaluate(System.in, outputStream));
    }
}
