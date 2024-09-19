package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.MkdirException;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MkdirApplicationTest {
    private MkdirApplication application;
    private static final String FOLDER1 = "folder1";
    private static final String FOLDER2 = "folder2";
    private static final String FOLDER3 = "folder3";
    private static final String SUBFOLDER = "subfolder";
    @BeforeEach
    public void setUp() {
        application = new MkdirApplication();
    }

    @AfterEach
    public void cleanup() {
        List<String> dirToDelete = Arrays.asList(FOLDER1, FOLDER2, FOLDER3, FOLDER1 + File.separator + SUBFOLDER, FOLDER2 + File.separator + SUBFOLDER);

        for (String directoryName : dirToDelete) {
            String cwd = Environment.currentDirectory;
            File directory = new File(cwd + File.separator + directoryName);
            if (directory.exists() && directory.isDirectory()) {
                deleteDirectory(directory);
            }
        }
    }

    private void deleteDirectory(File directory) {
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


    private void assertAllFoldersExist(String... folderNames) {
        String cwd = Environment.currentDirectory;
        for (String folderName : folderNames) {
            File folder = new File(cwd + File.separator + folderName);
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
        }
    }

    /**
     * Test case to verify that running the application with null arguments throws a {@link MkdirException}.
     */
    @Test
    public void run_nullArgs_shouldThrowException() {
        assertThrows(MkdirException.class, () -> application.run(null, System.in, System.out));
    }

    /**
     * Test case to verify that running the application with empty arguments throws a {@link MkdirException}.
     */
    @Test
    public void run_emptyArgs_shouldThrowException() {
        assertThrows(MkdirException.class, () -> application.run(new String[0], System.in, System.out));
    }

    /**
     * Test case to verify that running the application with null standard output stream throws a {@link MkdirException}.
     */

    /**
     * Test case to verify that running the application with a single argument creates the corresponding folder.
     */
    @Test
    public void run_singleArg_shouldCreateFolder() {
        String[] args = {FOLDER1};
        assertDoesNotThrow(() -> application.run(args, System.in, System.out));
        assertAllFoldersExist(args);
    }

    /**
     * Test case to verify that running the application with multiple arguments creates the corresponding folders.
     */
    @Test
    public void run_multipleArgs_shouldCreateFolders() {
        String[] args = {FOLDER1, FOLDER2, FOLDER3};
        assertDoesNotThrow(() -> application.run(args, System.in, System.out));
        String[] expectedFolders = {FOLDER1, FOLDER2, FOLDER3};
        assertAllFoldersExist(expectedFolders);
    }

    /**
     * Test case to verify that running the application with multiple arguments and the parent flag set to true
     * creates the corresponding folders and their parent directories if necessary.
     */
    @Test
    public void run_multipleArgsAndParentTrue_shouldCreateFolders() {
        String[] args = {"-p", FOLDER1, FOLDER2 + File.separator + SUBFOLDER, FOLDER3};
        assertDoesNotThrow(() -> application.run(args, System.in, System.out));
        String[] expectedFolders = {FOLDER1, FOLDER2, FOLDER2 + File.separator + SUBFOLDER, FOLDER3};
        assertAllFoldersExist(expectedFolders);
    }

    /**
     * Test case to verify that running the application with multiple arguments and the parent flag set to false
     * creates folders partially and throws a {@link MkdirException} for folders whose parent directories don't exist.
     */
    @Test
    public void run_multipleArgsAndParentFalse_shouldCreatePartially() {
        String[] args = {FOLDER1, FOLDER2 + File.separator + SUBFOLDER, FOLDER3};
        assertThrows(MkdirException.class, () -> application.run(args, System.in, System.out));

        // ensure only folder1 and folder3 are created
        String[] expectedFolders = {FOLDER1, FOLDER3};
        assertAllFoldersExist(expectedFolders);
    }

    /**
     * Test case to verify that creating a folder with a single argument and the parent flag set to false
     * successfully creates the folder.
     */
    @Test
    public void createFolder_singleArgAndParentFalse_shouldCreate() {
        // array of folder names
        String[] folderNames = {FOLDER1};

        assertDoesNotThrow(() -> application.createFolder(folderNames));

        assertAllFoldersExist(folderNames);
    }

    @Test
    public void createFolder_singleArgAndParentFalse_shouldFail() {
        String[] folderNames = {FOLDER1+File.separator+SUBFOLDER};
        assertThrows(MkdirException.class, () -> application.createFolder(folderNames));
    }

//    @Test
//    public void createFolder_singleArgNoSlashParentTrue_shouldCreate() {
//        List<String> folderNames = Collections.singletonList(FOLDER1);
//
//        assertDoesNotThrow(() -> application.createFolder(true, folderNames));
//
//        assertAllFoldersExist(folderNames);
//    }

//    @Test
//    public void createFolder_singleArgSlashParentTrue_shouldCreate() {
//        List<String> folderNames = Collections.singletonList(FOLDER1 + File.separator + SUBFOLDER);
//
//        assertDoesNotThrow(() -> application.createFolder(true, folderNames));
//
//        assertAllFoldersExist(folderNames);
//    }

    @Test
    public void createFolder_multipleArgsAndParentFalse_shouldCreatePartially() {
        String[] folderNames = {FOLDER1, FOLDER2 + File.separator + SUBFOLDER, FOLDER3};

        assertThrows(MkdirException.class, () -> application.createFolder(folderNames));

        // ensure only folder1 and folder3 are created
        String[] expectedFolders = {FOLDER1, FOLDER3};
        assertAllFoldersExist(expectedFolders);
    }

    @Test
    public void createFolder_null_shouldThrowException() {
        String[] folderNames = {FOLDER1, null, ""};

        assertThrows(MkdirException.class, () -> application.createFolder(folderNames));
    }

}