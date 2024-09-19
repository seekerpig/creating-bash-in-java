package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.MvException;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;

/**
 * This class contains unit tests for the MvApplication class.
 */
public class MvApplicationTest {

    private MvApplication mvApp;
    private Path tempDirectory;
    private File sourceFile;
    private File targetFile;
    private File destDirectory;
    private File noPermissionDir;

    @BeforeEach
    void setUp() throws IOException {
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
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDirectory)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void mvSrcFileToDestFile_WithoutOverwrite_FileIsMoved() throws AbstractApplicationException {
        String srcFilePath = sourceFile.getAbsolutePath();
        String destFilePath = destDirectory.getAbsolutePath();

        assertNotEquals(srcFilePath, destFilePath);

        // Move source file to destination file without overwrite
        assertDoesNotThrow(() -> mvApp.mvSrcFileToDestFile(false, srcFilePath, destFilePath));

        // Verify source file is moved
        assertFalse(sourceFile.exists());
        assertTrue(targetFile.exists());
    }

    @Test
    void testMvFilesToFolder_OverwriteNotAllowed_shouldThrow() throws IOException {
        String destDir = destDirectory.getAbsolutePath();
        String fileName = sourceFile.getAbsolutePath();

        File existingFile = new File(destDir, sourceFile.getName());
        assertTrue(existingFile.createNewFile());
        assertThrows(MvException.class, () -> mvApp.mvSrcFileToDestFile(false, fileName, destDir));
    }

    // ====================================================================================================================================

    /**
     * Test case for moving a source file to a destination file without overwriting.
     * The method moves the source file to the destination file and verifies that
     * the source file no longer exists
     * and the destination file exists after the move operation.
     *
     * @throws Exception if an error occurs during the test case
     */
    @Test
    void mvSrcFileToDestFile_WithoutOverwriting_FileIsMoved() throws Exception {
        String srcFile = sourceFile.getAbsolutePath();
        String destDir = destDirectory.getAbsolutePath();
        mvApp.mvSrcFileToDestFile(null, srcFile, destDir);
        assertTrue(new File(destDir, sourceFile.getName()).exists());
    }

    /**
     * Test case to verify that when the `mvSrcFileToDestFile` method is called with
     * the `overwrite` flag set to true,
     * the source file is overwritten by the destination file.
     *
     * @throws Exception if an error occurs during the test case execution
     */
    @Test
    void mvSrcFileToDestFile_WithOverwriting_FileIsOverwritten() throws Exception {
        String srcFile = sourceFile.getAbsolutePath();
        String destDir = destDirectory.getAbsolutePath();
        mvApp.mvSrcFileToDestFile(true, srcFile, destDir);
        assertTrue(new File(destDir, sourceFile.getName()).exists());
    }

    /**
     * Test case for the mvSrcFileToDestFile method when overwriting is not allowed
     * and the target file already exists.
     * It asserts that an exception is thrown.
     */
    @Test
    void mvSrcFileToDestFile_WithoutOverwriting_ThrowsExceptionIfTargetExists() throws IOException {
        String srcFile = sourceFile.getAbsolutePath();
        String destDir = destDirectory.getAbsolutePath();
        File existTargetFile = new File(destDir, sourceFile.getName());
        assertTrue(existTargetFile.createNewFile());
        assertThrows(MvException.class, () -> mvApp.mvSrcFileToDestFile(false, srcFile, destDir));
    }

    /**
     * Test case to verify that files are moved to a folder without overwriting
     * existing files.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void mvFilesToFolder_WithoutOverwriting_FilesAreMoved() throws Exception {
        File anotherSourceFile = tempDirectory.resolve("anotherSource.txt").toFile();
        assertTrue(anotherSourceFile.createNewFile());

        mvApp.mvFilesToFolder(false, destDirectory.getAbsolutePath(), sourceFile.getAbsolutePath(),
                anotherSourceFile.getAbsolutePath());
        assertFalse(sourceFile.exists());
        assertFalse(anotherSourceFile.exists());
        assertTrue(new File(destDirectory, sourceFile.getName()).exists());
        assertTrue(new File(destDirectory, anotherSourceFile.getName()).exists());
    }

    /**
     * Test case to test run method with empty arguments
     */
    @Test
    void run_EmptyArgs_ThrowsException() {
        assertThrows(MvException.class, () -> mvApp.run(new String[0], System.in, System.out));
    }

    /**
     * Test case to test run method with valid arguments
     */
    @Test
    void run_ValidArgs_NoException() {
        assertDoesNotThrow(() -> mvApp.run(new String[]{sourceFile.getAbsolutePath(), destDirectory.getAbsolutePath()},
                System.in, System.out));
    }

    /**
     * Test case to test run method with invalid arguments
     */
    @Test
    void run_InvalidArgs_ThrowsException() {
        assertThrows(MvException.class, () -> mvApp.run(new String[]{"-a"}, System.in, System.out));
    }
}
