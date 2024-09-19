package impl.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.app.CdApplication;

public class CdBugs {
    @Test
    void bugReport18() throws AbstractApplicationException {
        CdApplication cdApplication = new CdApplication();
        String[] args = {"~/"};
        cdApplication.run(args, System.in, System.out);
        assertEquals(System.getProperty("user.home"), System.getProperty("user.dir"));
    }

}
