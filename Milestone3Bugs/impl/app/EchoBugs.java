package impl.app;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.app.EchoApplication;

public class EchoBugs {
    private ShellImpl shell;
    @BeforeEach
    void setUp() throws IOException {
        shell = new ShellImpl();
    }

    @Test
    void bugReport13() {
        String command = "echo */.";
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            shell.parseAndEvaluate(command, outputStream);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
