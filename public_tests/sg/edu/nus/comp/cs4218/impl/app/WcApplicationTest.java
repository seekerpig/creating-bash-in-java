package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcApplicationTest {
    private WcApplication wcApp;
    private static String singleLFileName = "wc_test_single_line.txt";
    private static String multiLFileName = "wc_test_multi_line.txt";
    private static String emptyFileName = "empty_file.txt";

    private static File testSingleLine = new File(singleLFileName);
    private static File testMultiLine = new File(multiLFileName);
    private static File testEmptyFile = new File(emptyFileName);

    private static BufferedWriter writer;
    private final String initEnv = Environment.currentDirectory;

    @BeforeEach
    void setUp() throws Exception {
        wcApp = new WcApplication();
        Path currPath = Paths.get(initEnv);
        Path subFolderPath = currPath.resolve("testResources");
        Files.createDirectories(subFolderPath);

        Path pathToFile = subFolderPath.resolve(testSingleLine.getName());
        Files.writeString(pathToFile, "This is a test sentence.");

        Path pathToFile2 = subFolderPath.resolve(testMultiLine.getName());
        Files.writeString(pathToFile2, "This is the first line.\nThis is the second line.\nThis is the third line.");

        Path pathToFile3 = subFolderPath.resolve(testEmptyFile.getName());
        Files.writeString(pathToFile3, "");
    }

    @AfterEach
    void tearDown() throws Exception {
        Path subFolderPath = Paths.get(initEnv).resolve("testResources");
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

    @Test
    void run_NullArgs_ShouldThrow() {
        assertThrows(WcException.class, () -> {
            wcApp.run(null, System.in, System.out);
        });
    }

    @Test
    void countFromFiles_NonExistentFile_ShouldThrow() {
        String noExistFile = "non_existent_file.txt";
        assertThrows(WcException.class, () -> {
            wcApp.countFromFiles(true, true, true, noExistFile);
        });
    }

    @Test
    void countFromFiles_NullFile_ShouldThrow() {
        assertThrows(WcException.class, () -> {
            wcApp.countFromFiles(true, true, true, null);
        });
    }

    @Test
    void countFromFiles_NoFileProvided_ShouldThrow() {
        assertThrows(WcException.class, () -> {
            wcApp.countFromFiles(true, true, true, "");
        });
    }

    @Test
    void countFromFiles_Directory_ShouldThrow() {
        String directoryPath = "src";
        assertThrows(WcException.class, () -> {
            wcApp.countFromFiles(true, true, true, directoryPath);
        });
    }

    @Test
    void countFromFiles_EmptyFile_ShouldCount() throws AbstractApplicationException {
        StringBuilder stringBuilder = new StringBuilder(64);
        stringBuilder.append("\t0\t0\t0 testResources/empty_file.txt");
        assertEquals(stringBuilder.toString(), wcApp.countFromFiles(true, true, true, "testResources/empty_file.txt"));
    }

    @Test
    void countFromFiles_MultiLineFile_ShouldCount() throws AbstractApplicationException {
        StringBuilder stringBuilder = new StringBuilder(64);
        stringBuilder.append("\t2\t15\t72 testResources/wc_test_multi_line.txt");
        assertEquals(stringBuilder.toString(), wcApp.countFromFiles(true, true, true, "testResources/wc_test_multi_line.txt"));
    }

    @Test
    void countFromFiles_SingleLineFile_ShouldCount() throws AbstractApplicationException {
        StringBuilder stringBuilder = new StringBuilder(64);
        stringBuilder.append("\t0\t5\t24 testResources/wc_test_single_line.txt");
        assertEquals(stringBuilder.toString(), wcApp.countFromFiles(true, true, true, "testResources/wc_test_single_line.txt"));
    }

    @Test
    void countFromFiles_NullStdin_ShouldThrow() {
        assertThrows(WcException.class, () -> {
            wcApp.countFromStdin(true, true, true, null);
        });
    }
}