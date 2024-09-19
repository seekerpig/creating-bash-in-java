package impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.app.CatApplication;
import sg.edu.nus.comp.prof.tdd.testutils.TestEnvironmentUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.prof.tdd.testutils.TestStringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.prof.tdd.testutils.TestStringUtils.STRING_NEWLINE;

public class CatBugsTest {
    private ShellImpl shell;
    private static final String TEMP = "e.txt";
    private static final String DIR = "dir";
    public static final String ERR_IS_DIR = String.format("cat: %s: Is a directory", DIR);
    private static final String TEXT_ONE = "Test line 1" + STRING_NEWLINE + "Test line 2" + STRING_NEWLINE +
            "Test line 3";
    private static final Deque<Path> files = new ArrayDeque<>();
    public static final String ERR_NO_SUCH_FILE = "cat: %s: No such file or directory";
    private static Path TEMP_PATH;
    private static Path DIR_PATH;

    private CatApplication catApplication;

    @BeforeEach
    void setUp() {
        shell = new ShellImpl();
        catApplication = new CatApplication();
    }

    @BeforeAll
    static void createTemp() throws IOException, NoSuchFieldException, IllegalAccessException {
        String initialDir = TestEnvironmentUtil.getCurrentDirectory();
        TEMP_PATH = Paths.get(initialDir, TEMP);
        DIR_PATH = Paths.get(TestEnvironmentUtil.getCurrentDirectory(), TEMP + CHAR_FILE_SEP + DIR);
        Files.createDirectory(TEMP_PATH);
        Files.createDirectory(DIR_PATH);
    }

    @AfterAll
    static void deleteFiles() throws IOException {
        for (Path file : files) {
            Files.deleteIfExists(file);
        }
        Files.deleteIfExists(DIR_PATH);
        Files.delete(TEMP_PATH);
    }

    private void createFile(String name, String text) throws IOException {
        Path path = TEMP_PATH.resolve(name);
        Files.createFile(path);
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
        files.push(path);
    }

    private String[] toArgs(String flag, String... files) {
        List<String> args = new ArrayList<>();
        if (!flag.isEmpty()) {
            args.add("-" + flag);
        }
        for (String file : files) {
            if (file.equals("-")) {
                args.add(file);
            } else {
                args.add(Paths.get(TEMP, file).toString());
            }
        }
        return args.toArray(new String[0]);
    }

    //catStdin cases
    @Test
    void cat_ConcatenateFilesInWildcardDirectory_DisplaysCorrectOutput() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String command = String.format("cat *.txt");
        createFile("someFile", TEXT_ONE);
        String expectedOutput = "Test line 1" + STRING_NEWLINE + "Test line 2" + STRING_NEWLINE +  "Test line 3";

        shell.parseAndEvaluate(command, outputStream);

        assertEquals(expectedOutput, outputStream.toString());
    }
}
