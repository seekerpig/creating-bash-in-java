package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.RmException;

public class RmApplicationTest {

    private File testFile1;
    private File testFile2;
    private File testFile3;
    private File testDirectory;
    private File emptyTestDir1;
    private File emptyTestDir2;
    private RmApplication rmApplication;
    private InputStream inputStream;
    private OutputStream outputStream;

    @BeforeEach
    public void setUp() throws IOException {
        rmApplication = new RmApplication();
        inputStream = null;
        outputStream = null;
        emptyTestDir1 = Files.createTempDirectory("emptyTestDir1").toFile();
        emptyTestDir2 = Files.createTempDirectory("emptyTestDir2").toFile();
        testDirectory = Files.createTempDirectory("testDir").toFile();
        testFile1 = new File(testDirectory, "test1.txt");
        assertTrue(testFile1.createNewFile());

        testFile2 = new File(testDirectory, "test2.txt");
        assertTrue(testFile2.createNewFile());

        testFile3 = new File(testDirectory, "test3.txt");
        assertTrue(testFile3.createNewFile());
    }

    @AfterEach
    public void tearDown() {
        testFile1.delete();
        testFile2.delete();
        testFile3.delete();
        deleteDirectory(testDirectory);
    }

    private void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

        directory.delete();
    }

    /**
     * Test case to verify that an exception is thrown when running the 'rm' command
     * with no arguments.
     *
     * Test Input:
     * - Call the 'run' method with an empty argument array.
     *
     * Expected Behavior:
     * - An RmException should be thrown indicating that no arguments were provided.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void run_noArgs_shouldThrow() throws Exception {
        assertThrows(RmException.class, () -> rmApplication.run(new String[] {}, inputStream, outputStream));
    }

    /**
     * Test case to verify that an exception is thrown when running the 'rm' command
     * with a null argument.
     *
     * Test Input:
     * - Call the 'run' method with a null argument
     *
     * Expected Behavior:
     * - An RmException should be thrown indicating that null argument was provided.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void run_argListContainsNull_ThrowsException() {
        assertThrows(RmException.class, () -> rmApplication.run(new String[] { null }, inputStream, outputStream));
    }

    /**
     * Unit test for the 'rm' command when no file or directory is specified.
     * This test verifies that an exception is thrown when running the 'rm' command
     * with no file or directory specified.
     * 
     * Test Input:
     * - Call the 'run' method with no file or directory specified
     *
     * Expected Behavior:
     * - An RmException should be thrown indicating that null argument was provided.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void run_noFileOrDirectorySpecified_shouldThrow() throws Exception {
        assertThrows(RmException.class, () -> rmApplication.run(new String[] { "-r" },
                inputStream, outputStream));
    }

    /**
     * Test case to verify that a file is deleted successfully.
     *
     * Test Input:
     * - Create a temporary file.
     * - Call the 'remove' method with deleteFile set to true.
     *
     * Expected Behavior:
     * - The file should no longer exist after the 'remove' operation.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteFile_shouldRemove() throws Exception {
        assertTrue(testFile1.exists());
        rmApplication.remove(false, false, testFile1.getAbsolutePath());
        assertFalse(testFile1.exists());
    }

    /**
     * Test case to verify that attempting to remove an invalid file results in
     * exception thrown.
     *
     * Test Input:
     * - Call the 'remove' method with the path to a nonexistent file.
     *
     * Expected Behavior:
     * - An exception should be thrown indicating that the file does not exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_invalidFile_shouldThrow() throws Exception {
        String invalidFilePath = "/path/to/nonexistent/file.txt";
        assertThrows(RmException.class, () -> rmApplication.remove(false, false, invalidFilePath));
    }

    /**
     * Test case to verify the removal of multiple files by the RmApplication class.
     * This test ensures that the remove method correctly removes
     * multiple files specified by their absolute file paths. The method is called
     * with three file paths representing
     * temporary files created for testing purposes. After the method call, it
     * asserts that each file has been
     * successfully removed from the file system.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    public void remove_deleteMultipleFiles_removeFiles() throws Exception {
        rmApplication.remove(false, false, testFile1.getAbsolutePath(), testFile2.getAbsolutePath(),
                testFile3.getAbsolutePath());

        assertFalse(testFile1.exists());
        assertFalse(testFile2.exists());
        assertFalse(testFile3.exists());
    }

    /**
     * Test case to verify that multiple files are removed successfully even if one
     * file is invalid.
     *
     * Test Input:
     * - Create multiple temporary files.
     * - Include one valid file and one invalid file in the list of files to remove.
     * - Call the 'remove' method with the list of files.
     *
     * Expected Behavior:
     * - The valid file should be removed successfully.
     * - An exception should be thrown for the invalid file, indicating that it does
     * not exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteMultipleFilesButOneFileIsInvalid_shouldRemoveValidFilesAndThrow() throws Exception {
        assertTrue(testFile1.exists());
        String invalidFilePath = "/path/to/nonexistent/file.txt";
        assertThrows(RmException.class,
                () -> rmApplication.remove(false, false, invalidFilePath, testFile1.getAbsolutePath()));
        assertFalse(testFile1.exists());
    }

    /**
     * Test case to verify that a directory is successfully deleted.
     *
     * Test Input:
     * - Create a temporary directory.
     * - Call the 'remove' method with the path to the created directory.
     *
     * Expected Behavior:
     * - The directory should be successfully deleted without throwing any
     * exceptions.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteDirectory_shouldRemove() throws Exception {
        assertTrue(testDirectory.exists());
        rmApplication.remove(false, true, testDirectory.getAbsolutePath());
        assertFalse(testDirectory.exists());

    }

    /**
     * Test case to verify that an exception is thrown when trying to delete a
     * directory
     * without using the recursive flag.
     *
     * Test Input:
     * - Create a temporary directory with files.
     * - Call the 'remove' method with the path to the directory and without the
     * recursive flag.
     *
     * Expected Behavior:
     * - An RmException should be thrown indicating that the directory is not empty
     * and the recursive flag is not set.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteDirectoryWithoutRecursiveFlag_shouldThrow() throws Exception {
        assertTrue(testDirectory.exists());
        assertThrows(RmException.class,
                () -> rmApplication.remove(false, false, testDirectory.getAbsolutePath()));
    }

    /**
     * Test case to verify that an exception is thrown when trying to delete a
     * non-existent directory.
     *
     * Test Input:
     * - Call the 'remove' method with a non-existent directory path.
     *
     * Expected Behavior:
     * - An ExitException should be thrown indicating that the directory does not
     * exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteDirectoryNotExist_shouldThrow() throws Exception {
        assertThrows(RmException.class, () -> rmApplication.remove(false, false, "dir_not_exist"));
    }

    /**
     * Test case to verify that an exception is thrown when trying to delete
     * multiple directories.
     *
     * Test Input:
     * - Create two temporary directories.
     * - Call the 'remove' method with the paths to both directories.
     *
     * Expected Behavior:
     * - An RmException should be thrown indicating that the directories are not
     * empty and the recursive flag is not set.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteMultipleDirectories_shouldThrow() throws Exception {
        assertTrue(testDirectory.exists());
        assertTrue(emptyTestDir1.exists());
        assertThrows(RmException.class,
                () -> rmApplication.remove(false, false, testDirectory.getAbsolutePath(),
                        emptyTestDir1.getAbsolutePath()));
        assertTrue(testDirectory.exists());
        assertTrue(emptyTestDir1.exists());
    }

    /**
     * Test case to verify that an empty directory is removed successfully when the
     * '-d' flag is set.
     *
     * Test Input:
     * - Create an empty directory.
     * - Call the 'remove' method with isEmptyFolder set to true and the directory
     * path.
     *
     * Expected Behavior:
     * - The empty directory should be removed successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteEmptyDirectoryWithEmptyFlag_shouldRemove() throws Exception {
        assertTrue(emptyTestDir1.exists());
        rmApplication.remove(true, false, emptyTestDir1.getAbsolutePath());
        assertFalse(emptyTestDir1.exists());
    }

    /**
     * Test case to verify that attempting to remove an empty directory without the
     * '-d' flag throws an exception.
     *
     * Test Input:
     * - Call the 'remove' method with isEmptyFolder set to false and the directory
     * path.
     *
     * Expected Behavior:
     * - An exception should be thrown, indicating that the directory cannot be
     * removed without the '-d' flag.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_deleteEmptyDirectoryWithoutEmptyFlag_shouldThrow() throws Exception {
        assertThrows(RmException.class,
                () -> rmApplication.remove(false, false, emptyTestDir1.getAbsolutePath()));
    }

    /**
     * Test case to verify that attempting to remove a non-empty directory with the
     * '-d' flag throws an exception.
     *
     * Test Input:
     * - Create a non-empty directory.
     * - Call the 'remove' method with isEmptyFolder set to true and the directory
     * path.
     *
     * Expected Behavior:
     * - An exception should be thrown, indicating that the directory is not empty
     * and cannot be removed with the '-d' flag.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_nonEmptyDirectoryWithEmptyFlag_shouldThrow() throws Exception {
        assertTrue(testDirectory.exists());
        assertThrows(RmException.class,
                () -> rmApplication.remove(true, false, testDirectory.getAbsolutePath()));
    }

    /**
     * Test case to verify that multiple empty directories can be removed
     * successfully with the '-d' flag.
     *
     * Test Input:
     * - Call the 'remove' method with isEmptyFolder set to true and the paths of
     * emptyTestDirectory1 and emptyTestDirectory2.
     *
     * Expected Behavior:
     * - All empty directories should be removed successfully without any
     * exceptions.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_multipleEmptyDirectoryWithEmptyFlag_shouldRemove() throws Exception {
        assertTrue(emptyTestDir1.exists());
        assertTrue(emptyTestDir2.exists());

        rmApplication.remove(true, false, emptyTestDir1.getAbsolutePath(), emptyTestDir2.getAbsolutePath());

        assertFalse(emptyTestDir1.exists());
        assertFalse(emptyTestDir2.exists());
    }

    /**
     * Test case to verify that a directory with files is deleted successfully.
     *
     * Test Input:
     * - Create a test directory with files.
     * - Call the 'remove' method with deleteDirectory set to true and force set to
     * true.
     *
     * Expected Behavior:
     * - The directory and its contents should no longer exist after the 'remove'
     * operation.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void remove_directoryWithFiles_removeDirectory() throws Exception {
        assertTrue(testDirectory.exists());
        assertTrue(testFile1.exists());
        assertTrue(testFile2.exists());
        rmApplication.remove(true, true, testDirectory.getAbsolutePath());
        assertFalse(testDirectory.exists());
        assertFalse(testFile1.exists());
        assertFalse(testFile2.exists());
    }

}
