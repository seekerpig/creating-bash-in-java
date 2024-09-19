package impl.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.app.MvApplication;

public class MvBugs {
    @TempDir
    Path tempDir;
    private ByteArrayOutputStream outputStream;
    private ShellImpl shell;

    private MvApplication mvApp;
    private Path tempDirectory;
    private File sourceFile;
    private File targetFile;
    private File destDirectory;
    private File noPermissionDir;

    @BeforeEach
    public void setUp() throws IOException {
        mvApp = new MvApplication();
        tempDirectory = Files.createTempDirectory("tempDirForMv");

        sourceFile = tempDirectory.resolve("source.txt").toFile();
        assertTrue(sourceFile.createNewFile());

        targetFile = tempDirectory.resolve("target.txt").toFile();
        assertTrue(targetFile.createNewFile());

        destDirectory = tempDirectory.resolve("destDir").toFile();
        assertTrue(destDirectory.mkdir());

        File dupeDirectory = tempDirectory.resolve("dupeDir").toFile();
        assertTrue(dupeDirectory.mkdir());

        // Create and write content to a.txt and b.txt
        Files.write(tempDirectory.resolve("a.txt"), "a".getBytes());
        Files.write(tempDirectory.resolve("b.txt"), "b".getBytes());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDirectory)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void bugReport8() throws Exception {
        String sourcePath = destDirectory.getPath();
        String[] args = { sourcePath, sourcePath };
        assertThrows(MvException.class, () -> mvApp.run(args, System.in, outputStream));
    }

    @Test
    void bugReport9() throws Exception {
        Path sourcePath = tempDirectory.resolve("a.txt");
        Path targetPath = tempDirectory.resolve("b.txt");
        String[] args = { sourcePath.toString(), targetPath.toString() };
        mvApp.run(args, System.in, outputStream);
        String contentB = Files.readString(targetPath);
        assertEquals("a", contentB);
    }

    //If b is not an existing file or directory: The file a.txt will be renamed to b. After the operation, there will no longer be an a.txt in the current directory, and instead, there will be a file named b.
    @Test
    void bugReport10_case1() throws Exception {
        Path sourcePath = tempDirectory.resolve("a.txt");
        Path targetDir = tempDirectory.resolve("b");
        Files.createDirectories(targetDir);
        String[] moveArgs = { sourcePath.toString(), targetDir.toString() };
        mvApp.run(moveArgs, System.in, outputStream);
        assertTrue(Files.notExists(sourcePath));
        assertTrue(Files.exists(targetDir.resolve("a.txt")));
    }

    @Test
    void bugReport10_case2() throws Exception {
        // Arrange
        Path sourcePath = tempDirectory.resolve("aaa.txt");
        Path targetDir = tempDirectory.resolve("b");
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve("aaa.txt");
        Files.createFile(sourcePath);
        String[] moveArgs = { sourcePath.toString(), targetDir.toString() };
        mvApp.run(moveArgs, System.in, outputStream);
        assertFalse(Files.exists(sourcePath));
        assertTrue(Files.exists(targetFile));
    }

    @Test
    void bugReport10_case3() throws Exception {
        Path sourcePath = tempDirectory.resolve("a.txt");
        Path targetFile = tempDirectory.resolve("b");
        Files.createFile(sourcePath);
        Files.createFile(targetFile);
        String[] moveArgs = { sourcePath.toString(), targetFile.toString() };
        mvApp.run(moveArgs, System.in, outputStream);
        assertTrue(Files.notExists(sourcePath));
        assertEquals(Files.readAllLines(targetFile), Files.readAllLines(sourcePath));
    }


}
