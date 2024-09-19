package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MkdirApplicationIT { // NOPMD
    private ShellImpl shell;
    private static final String FOLDER1 = "folder1";
    private static final String FOLDER2 = "folder2";
    private static final String FOLDER3 = "folder3";
    private static final String SUBFOLDER = "subfolder";
    private static final String[] FOLDER_NAMES = {FOLDER1, FOLDER2, FOLDER3, FOLDER1 + File.separator + SUBFOLDER, FOLDER2 + File.separator + SUBFOLDER};
    private static final String IO_FAILURE_MSG = "IOException: ";
    private static final String ABSTRACT_EXC_MSG = "Abstract Application Exception: ";
    private static final String SHELL_EXC_MSG = "Shell Exception: ";

    @BeforeEach
    public void setUp() {
        shell = new ShellImpl();
        // cleanup
        for (String folderName : FOLDER_NAMES) {
            File folder = new File(folderName);
            if (folder.exists()) {
                folder.delete();
            }
        }
    }

    @AfterEach
    public void cleanup() {
        for (String directoryName : FOLDER_NAMES) {
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

    // run simple mkdir command, should create folder
    @Test
    public void run_simpleMkdirCommand_shouldCreateFolder() {
        // Setup
        String command = "mkdir folder1";
        String cwd = Environment.currentDirectory;
        File folder = new File(cwd + File.separator + FOLDER1);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(command, outputStream);
        } catch (IOException e) {
            fail(IO_FAILURE_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(ABSTRACT_EXC_MSG + e.getMessage());
        } catch (ShellException e) {
            fail(SHELL_EXC_MSG + e.getMessage());
        }

        assert folder.exists();
    }

    // run mkdir command with multiple folders, should create all folders
    @Test
    public void run_mkdirCommandWithMultipleFolders_shouldCreateAllFolders() {
        // Setup
        String command = "mkdir folder1 folder2 folder3";
        String cwd = Environment.currentDirectory;
        File folder1 = new File(cwd + File.separator + FOLDER1);
        File folder2 = new File(cwd + File.separator + FOLDER2);
        File folder3 = new File(cwd + File.separator + FOLDER3);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(command, outputStream);
        } catch (IOException e) {
            fail(IO_FAILURE_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(ABSTRACT_EXC_MSG + e.getMessage());
        } catch (ShellException e) {
            fail(SHELL_EXC_MSG + e.getMessage());
        }

        // Check
        assert folder1.exists();
        assert folder2.exists();
        assert folder3.exists();
    }

    // run mkdir command with -p option, should create parent folders
    @Test
    public void run_mkdirCommandWithPOption_shouldCreateParentFolders() {
        // Setup
        String command = "mkdir -p folder1" + File.separator + "subfolder folder2" + File.separator + SUBFOLDER;
        String cwd = Environment.currentDirectory;
        File folder1 = new File(cwd + File.separator + FOLDER1);
        File folder2 = new File(cwd + File.separator + FOLDER2);
        File subfolder1 = new File(cwd + File.separator + FOLDER1 + File.separator + SUBFOLDER);
        File subfolder2 = new File(cwd + File.separator + FOLDER2 + File.separator + SUBFOLDER);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(command, outputStream);
        } catch (IOException e) {
            fail(IO_FAILURE_MSG + e.getMessage());
        } catch (AbstractApplicationException e) {
            fail(ABSTRACT_EXC_MSG + e.getMessage());
        } catch (ShellException e) {
            fail(SHELL_EXC_MSG + e.getMessage());
        }

        // Check
        assert folder1.exists();
        assert folder2.exists();
        assert subfolder1.exists();
        assert subfolder2.exists();
    }

    // run mkdir command with -p option and without it, should create partially
    @Test
    public void run_mkdirCommandWithSubFolderAndWithoutP_shouldCreatePartially() {
        String command = "mkdir folder1 folder2" + File.separator + SUBFOLDER;
        String cwd = Environment.currentDirectory;
        File folder1 = new File(cwd + File.separator + FOLDER1);
        File folder2 = new File(cwd + File.separator + FOLDER2);
        File subfolder = new File(cwd + File.separator + FOLDER2 + File.separator + SUBFOLDER);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(command, outputStream);
            fail("Exception not thrown");
        } catch (AbstractApplicationException e) {
            assert folder1.exists();
            assert !folder2.exists();
            assert !subfolder.exists();
        } catch (ShellException e) {
            fail(SHELL_EXC_MSG + e.getMessage());
        } catch (IOException e) {
            fail(IO_FAILURE_MSG + e.getMessage());
        }
    }

// pairwise makedir with other apps
    @Test
    public void testMkdir_mkdirSeqEcho_shouldCreateAndEcho() { // mkdir ; echo
        String commandString = "mkdir folder1 ; echo 'hello world'";
        String expectResult = "hello world" + System.lineSeparator();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(commandString, outputStream);
            assertEquals(expectResult, outputStream.toString());
        } catch (AbstractApplicationException | ShellException | IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }

        String cwd = Environment.currentDirectory;
        File folder1 = new File(cwd + File.separator + FOLDER1);
        assert folder1.exists();
    }

    @Test
    public void testMkdir_mkdirSeqLs_shouldCreateAndLs() { // mkdir ; ls
        String commandString = "mkdir folder1 ; ls";
        String expectResult = FOLDER1;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(commandString, outputStream);
            // assert contains folder1
            assert outputStream.toString().contains(expectResult);
        } catch (AbstractApplicationException | ShellException | IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }

        String cwd = Environment.currentDirectory;
        File folder1 = new File(cwd + File.separator + FOLDER1);
        assert folder1.exists();
    }

    // negative test cases

    @Test
    public void testMkdir_mkdirSeqMkdir_shouldReturnMessage() { // no exception, only error message
        String commandString = "mkdir folder1 ; mkdir folder1";
        String expectResult = "mkdir: " + ErrorConstants.ERR_FOLDER_EXISTS + System.lineSeparator();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(commandString, outputStream);
            assertEquals(expectResult, outputStream.toString());
        } catch (AbstractApplicationException | ShellException | IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }

        String cwd = Environment.currentDirectory;
        File folder1 = new File(cwd + File.separator + FOLDER1);
        assert folder1.exists();
    }


}
