package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.LsException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

/**
 * This class contains unit tests for the LsApplication class.
 * It tests various functionalities of the LsApplication class, such as listing
 * folder contents,
 * sorting by extension, and handling different options.
 */
public class LsApplicationTest {
    private LsApplication lsApp;
    private static final String TEST_DIRECTORY = "ls_test_directory";
    private static final String TEST_FILE_1 = "ls_file1.txt";
    private static final String TEST_FILE_2 = "ls_file2.txt";
    private static final String TEST_FILE_3 = "ls_file3.png";
    private static final String TEST_FILE_4 = "ls_file4.docx";
    private static final String TEST_FILE_5 = "ls_file5.jpg";
    private static final String TEST_FOLDER_1 = "ls_folder1";
    private static final String TEST_FOLDER_2 = "ls_folder2";
    private static final String[] TEST_FILES_W_EXT = { TEST_FILE_1, TEST_FILE_2, TEST_FILE_3, TEST_FILE_4, TEST_FILE_5 };
    private static final String[] TEST_FOLDERS = { TEST_FOLDER_1, TEST_FOLDER_2 };
    private static final String FOLDER_1_FILE_1_TXT = "ls_folder1_file1.txt"; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String FOLDER_1_FILE_2_TXT = "ls_folder1_file2.txt"; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String FOLDER_1_FILE_3_TXT = "ls_folder1_file3.txt"; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String FOLDER_1_FILE_1_PNG = "ls_folder1_file1.png"; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String FOLDER_1_FILE_2_PNG = "ls_folder1_file2.png"; //NOPMD - suppressed LongVariable - Variable Name is More Readable
    private static final String FOLDER_1_FILE_3_PNG = "ls_folder1_file3.png"; //NOPMD - suppressed LongVariable - Variable Name is More Readable

    @BeforeAll
    public static void setUp() throws IOException {
        String testDirPath = Paths.get(Environment.currentDirectory).resolve(TEST_DIRECTORY).toString();
        File directory = new File(testDirPath);
        directory.mkdir();

        for (String fileName : TEST_FILES_W_EXT) {
            File file = new File(testDirPath + File.separator + fileName);
            file.createNewFile();
        }

        for (String folderName : TEST_FOLDERS) {
            File folder = new File(testDirPath + File.separator + folderName);
            folder.mkdir();

            for (int i = 1; i <= 3; i++) {
                String fileName = folderName + "_file" + i + ".txt";
                File file = new File(folder.getAbsolutePath() + File.separator + fileName);
                file.createNewFile();
            }

            for (int i = 1; i <= 3; i++) {
                String fileName = folderName + "_file" + i + ".png";
                File file = new File(folder.getAbsolutePath() + File.separator + fileName);
                file.createNewFile();
            }
        }
        Environment.currentDirectory = testDirPath;
    }

    @AfterAll
    public static void tearDown() {
        String testDirPath = Environment.currentDirectory;

        // delete test directory along with all its contents
        File directory = new File(testDirPath);
        deleteDirectory(directory);
        Environment.currentDirectory = System.getProperty("user.dir");
    }

    private static void deleteDirectory(File directory) {
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
     * Test case to verify the behavior of the listFolderContent method when no
     * options are provided.
     * It checks if the method correctly lists the content of the current folder.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the method
     */
    @Test
    public void listFolderContent_NoOptions_CurrentFolderContent() throws AbstractApplicationException {
        lsApp = new LsApplication();

        String expectedContent = TEST_FILE_1 + System.lineSeparator() +
                TEST_FILE_2 + System.lineSeparator() +
                TEST_FILE_3 + System.lineSeparator() +
                TEST_FILE_4 + System.lineSeparator() +
                TEST_FILE_5 + System.lineSeparator() +
                TEST_FOLDER_1 + System.lineSeparator() +
                TEST_FOLDER_2;

        String actualContent = lsApp.listFolderContent(false, false);

        assertEquals(expectedContent, actualContent);
    }

    /**
     * Test case for listing the content of the current folder recursively with the
     * recursive option.
     * It verifies that the expected content matches the actual content returned by
     * the lsApp.listFolderContent method.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the lsApp.listFolderContent method
     */
    @Test
    public void listFolderContent_RecursiveOption_CurrentFolderContentRecursively()
            throws AbstractApplicationException {
        lsApp = new LsApplication();

        String expectedContent = TEST_FILE_1 + System.lineSeparator() +
                TEST_FILE_2 + System.lineSeparator() +
                TEST_FILE_3 + System.lineSeparator() +
                TEST_FILE_4 + System.lineSeparator() +
                TEST_FILE_5 + System.lineSeparator() +
                TEST_FOLDER_1 + System.lineSeparator() +
                TEST_FOLDER_2 + System.lineSeparator() + System.lineSeparator() +
                TEST_FOLDER_1 + ":" + System.lineSeparator() +
                FOLDER_1_FILE_1_PNG + System.lineSeparator() +
                FOLDER_1_FILE_1_TXT + System.lineSeparator() +
                FOLDER_1_FILE_2_PNG + System.lineSeparator() +
                FOLDER_1_FILE_2_TXT + System.lineSeparator() +
                FOLDER_1_FILE_3_PNG + System.lineSeparator() +
                FOLDER_1_FILE_3_TXT + System.lineSeparator() + System.lineSeparator() +
                TEST_FOLDER_2 + ":" + System.lineSeparator() +
                "ls_folder2_file1.png" + System.lineSeparator() +
                "ls_folder2_file1.txt" + System.lineSeparator() +
                "ls_folder2_file2.png" + System.lineSeparator() +
                "ls_folder2_file2.txt" + System.lineSeparator() +
                "ls_folder2_file3.png" + System.lineSeparator() +
                "ls_folder2_file3.txt";

        String actualContent = lsApp.listFolderContent(true, false);

        assertEquals(expectedContent, actualContent);
    }

    /**
     * Test case for the listFolderContent method with the sortByExt option.
     * It verifies that the current folder's content is sorted by extension.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the application
     */
    @Test
    public void listFolderContent_SortByExtOption_CurrentFolderContentSortedByExtension()
            throws AbstractApplicationException {
        lsApp = new LsApplication();

        String expectedContent = TEST_FOLDER_1 + System.lineSeparator() +
                TEST_FOLDER_2 + System.lineSeparator() +
                TEST_FILE_4 + System.lineSeparator() +
                TEST_FILE_5 + System.lineSeparator() +
                TEST_FILE_3 + System.lineSeparator() +
                TEST_FILE_1 + System.lineSeparator() +
                TEST_FILE_2;

        String actualContent = lsApp.listFolderContent(false, true);

        assertEquals(expectedContent, actualContent);
    }

    /**
     * Test case for listing the folder content recursively and sorting by
     * extension.
     * It verifies that the current folder content is recursively sorted by
     * extension.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the application
     */
    @Test
    public void listFolderContent_RecursiveAndSortByExtOptions_CurrentFolderContentRecursivelySortedByExtension()
            throws AbstractApplicationException {
        lsApp = new LsApplication();

        String expectedContent = TEST_FOLDER_1 + System.lineSeparator() +
                TEST_FOLDER_2 + System.lineSeparator() +
                TEST_FILE_4 + System.lineSeparator() +
                TEST_FILE_5 + System.lineSeparator() +
                TEST_FILE_3 + System.lineSeparator() +
                TEST_FILE_1 + System.lineSeparator() +
                TEST_FILE_2 + System.lineSeparator() + System.lineSeparator() +
                TEST_FOLDER_1 + ":" + System.lineSeparator() +
                FOLDER_1_FILE_1_PNG + System.lineSeparator() +
                FOLDER_1_FILE_2_PNG + System.lineSeparator() +
                FOLDER_1_FILE_3_PNG + System.lineSeparator() +
                FOLDER_1_FILE_1_TXT + System.lineSeparator() +
                FOLDER_1_FILE_2_TXT + System.lineSeparator() +
                FOLDER_1_FILE_3_TXT + System.lineSeparator() + System.lineSeparator() +
                TEST_FOLDER_2 + ":" + System.lineSeparator() +
                "ls_folder2_file1.png" + System.lineSeparator() +
                "ls_folder2_file2.png" + System.lineSeparator() +
                "ls_folder2_file3.png" + System.lineSeparator() +
                "ls_folder2_file1.txt" + System.lineSeparator() +
                "ls_folder2_file2.txt" + System.lineSeparator() +
                "ls_folder2_file3.txt";

        String actualContent = lsApp.listFolderContent(true, true);

        assertEquals(expectedContent, actualContent);
    }

    /**
     * Test case for the listFolderContent method when listing the content of one
     * folder.
     * It verifies that the listFolderContent method returns the expected content
     * for the specified folder.
     *
     * @throws AbstractApplicationException if an error occurs during the execution
     *                                      of the test case
     */
    @Test
    public void listFolderContent_OneFolder_ListContentForOneFolder() throws AbstractApplicationException {
        lsApp = new LsApplication();

        String expectedContent = "ls_folder1:" + System.lineSeparator() +
                FOLDER_1_FILE_1_PNG + System.lineSeparator() +
                FOLDER_1_FILE_1_TXT + System.lineSeparator() +
                FOLDER_1_FILE_2_PNG + System.lineSeparator() +
                FOLDER_1_FILE_2_TXT + System.lineSeparator() +
                FOLDER_1_FILE_3_PNG + System.lineSeparator() +
                FOLDER_1_FILE_3_TXT;

        String actualContent = lsApp.listFolderContent(false, false, TEST_FOLDER_1);

        assertEquals(expectedContent, actualContent);
    }

    /**
     * Test case for listing the content of a folder recursively with the recursive
     * option enabled for one folder.
     *
     * @throws AbstractApplicationException if an error occurs while executing the
     *                                      application
     */
    @Test
    public void listFolderContent_RecursiveOptionOneFolder_ListContentRecursivelyForOneFolder()
            throws AbstractApplicationException {
        lsApp = new LsApplication();

//        folder1:
//folder1_file1.png
//folder1_file1.txt
//folder1_file2.png
//folder1_file2.txt
//folder1_file3.png
//folder1_file3.txt

        String expectedContent = "ls_folder1:" + System.lineSeparator() +
                FOLDER_1_FILE_1_PNG + System.lineSeparator() +
                FOLDER_1_FILE_1_TXT + System.lineSeparator() +
                FOLDER_1_FILE_2_PNG + System.lineSeparator() +
                FOLDER_1_FILE_2_TXT + System.lineSeparator() +
                FOLDER_1_FILE_3_PNG + System.lineSeparator() +
                FOLDER_1_FILE_3_TXT;

        String actualContent = lsApp.listFolderContent(true, false, TEST_FOLDER_1);

        assertEquals(expectedContent, actualContent);
    }

    // multiple folders recursive
    @Test
    public void listFolderContent_RecursiveOptionTwoLevels_ListContentRecursivelyForMultipleFolders()
            throws AbstractApplicationException {
        lsApp = new LsApplication();

        String expectedContent = TEST_FILE_1 + System.lineSeparator() +
                TEST_FILE_2 + System.lineSeparator() +
                TEST_FILE_3 + System.lineSeparator() +
                TEST_FILE_4 + System.lineSeparator() +
                TEST_FILE_5 + System.lineSeparator() +
                TEST_FOLDER_1 + System.lineSeparator() +
                TEST_FOLDER_2 + System.lineSeparator() + System.lineSeparator() +
                "ls_folder1:" + System.lineSeparator() +
                FOLDER_1_FILE_1_PNG + System.lineSeparator() +
                FOLDER_1_FILE_1_TXT + System.lineSeparator() +
                FOLDER_1_FILE_2_PNG + System.lineSeparator() +
                FOLDER_1_FILE_2_TXT + System.lineSeparator() +
                FOLDER_1_FILE_3_PNG + System.lineSeparator() +
                FOLDER_1_FILE_3_TXT + System.lineSeparator() + System.lineSeparator() +
                "ls_folder2:" + System.lineSeparator() +
                "ls_folder2_file1.png" + System.lineSeparator() +
                "ls_folder2_file1.txt" + System.lineSeparator() +
                "ls_folder2_file2.png" + System.lineSeparator() +
                "ls_folder2_file2.txt" + System.lineSeparator() +
                "ls_folder2_file3.png" + System.lineSeparator() +
                "ls_folder2_file3.txt";

        String actualContent = lsApp.listFolderContent(true, false);

        assertEquals(expectedContent, actualContent);

    }


    @Test
    public void run_EmptyArgs_ListContentForOneFolder() throws AbstractApplicationException {
        lsApp = new LsApplication();
        String[] args = {};
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        lsApp.run(args, System.in, stdout);

        String expectedContent = TEST_FILE_1 + System.lineSeparator() +
                TEST_FILE_2 + System.lineSeparator() +
                TEST_FILE_3 + System.lineSeparator() +
                TEST_FILE_4 + System.lineSeparator() +
                TEST_FILE_5 + System.lineSeparator() +
                TEST_FOLDER_1 + System.lineSeparator() +
                TEST_FOLDER_2 + System.lineSeparator();

        assertEquals(expectedContent, stdout.toString());
    }

    /**
     * Test case to verify that the `run` method of the `LsApplication` class throws
     * an `IllegalArgumentException`
     * when called with no arguments.
     */
    @Test
    public void run_NullArgs_ThrowsException() {
        lsApp = new LsApplication();
        String[] args = null;
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        Throwable exception = assertThrows(LsException.class, () -> {
            lsApp.run(args, System.in, stdout);
        });

        assertEquals("ls: " + ERR_NULL_ARGS, exception.getMessage());
    }

    /**
     * Test case to verify that an exception is thrown when an invalid file is
     * provided as an argument to the run method.
     *
     * @throws IllegalArgumentException if the file does not exist
     */
    @Test
    public void run_InvalidFile_ErrorMessage() throws AbstractApplicationException{
        lsApp = new LsApplication();
        String[] args = { "invalid_file" };
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

        // assert that result of ls is message 'ls: cannot access invalid_file: No such file or directory'
        String expectedOutput = "ls: cannot access 'invalid_file': No such file or directory" + System.lineSeparator();
        lsApp.run(args, System.in, stdout);
        assertEquals(expectedOutput, stdout.toString());
    }
}