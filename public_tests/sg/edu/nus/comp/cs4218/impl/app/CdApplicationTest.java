package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CdApplicationTest {

    private final CdApplication cdApplication = new CdApplication();

    @Test
    void run_nullArgs_shouldThrow() {
        assertThrows(CdException.class, () -> cdApplication.run(null, System.in, System.out));
    }

    @Test
    void run_tooManyArgs_shouldThrow() {
        assertThrows(CdException.class,
                () -> cdApplication.run(new String[] { "dir1", "dir2" }, System.in, System.out));
    }

    @Test
    void run_nonExistingDirectory_shouldThrow() {
        String nonExistingDir= "non-existing-directory";
        assertThrows(CdException.class,
                () -> cdApplication.run(new String[] { nonExistingDir }, System.in, System.out));
    }

    @Test
    void run_fileInsteadOfDirectory_shouldThrow() {
        String filePath = "file.txt";
        assertThrows(CdException.class, () -> cdApplication.run(new String[] { filePath }, System.in, System.out));
    }

}
