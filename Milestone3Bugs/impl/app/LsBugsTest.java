package impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LsBugsTest {
    Shell shell;
    ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() throws IOException {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();

        // create directories and files in the current working directory
        Path currentPath = Paths.get(Environment.currentDirectory);
        File dir1 = new File(currentPath.toString() + File.separator + "dir1");
        File dir2 = new File(currentPath.toString() + File.separator + "dir2");
        dir1.mkdir();
        dir2.mkdir();
        File file1 = new File(dir1.getAbsolutePath() + File.separator + "file1");
        file1.createNewFile();
        File file2 = new File(dir2.getAbsolutePath() + File.separator + "file2");
        file2.createNewFile();
    }

    @AfterEach
    public void tearDown() {
        // delete directories and files in the current working directory
        Path currentPath = Paths.get(Environment.currentDirectory);
        File dir1 = new File(currentPath.toString() + File.separator + "dir1");
        File dir2 = new File(currentPath.toString() + File.separator + "dir2");
        File file1 = new File(dir1.getAbsolutePath() + File.separator + "file1");
        File file2 = new File(dir2.getAbsolutePath() + File.separator + "file2");
        file1.delete();
        file2.delete();
        dir1.delete();
        dir2.delete();
    }

    // test ls */ should list directories and its files in the current working directory
    @Test
    public void ls_starSlash_ShouldListDirectoriesAndFiles() throws Exception {
         String command = "ls */";
         String expectedOutput = "dir1/:" + System.lineSeparator() + "file1" + System.lineSeparator() + System.lineSeparator()
                 + "dir2/:" + System.lineSeparator() + "file2" + System.lineSeparator();
        shell.parseAndEvaluate(command, outputStream);
        assertEquals(expectedOutput, outputStream.toString());
    }

    // test ls dir1/* should list files in dir1
    @Test
    public void ls_dirSlashStar_ShouldListFilesInDir() throws Exception {
         String command = "ls dir1/*";
         String expectedOutput = "dir1/file1" + System.lineSeparator();
         shell.parseAndEvaluate(command, outputStream);
         assertEquals(expectedOutput, outputStream.toString());
    }

}
