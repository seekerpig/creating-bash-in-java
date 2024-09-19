package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.ExitException;

/**
 * This class contains unit tests for the {@link ExitApplication} class.
 */
public class ExitApplicationTest {
    private ExitApplication exitApplication;

    @BeforeEach
    void setUp() {
        exitApplication = new ExitApplication();
    }

    /**
     * Test case to verify that the `run` method throws an `ExitException`.
     */
    @Test
    void run_Null_ShouldThrowExitException() {
        assertThrows(ExitException.class, () -> exitApplication.run(null, null, null));
    }

    /**
     * Test case to verify that calling the `terminateExecution` method of the
     * `ExitApplication` class
     * throws an `ExitException`.
     */
    @Test
    void terminateExecution_Terminate_ShouldThrowExitException() {
        assertThrows(ExitException.class, exitApplication::terminateExecution);
    }
}
