package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.EchoException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class EchoApplicationTest {
   private EchoApplication echoApplication;
    @BeforeEach
    void setUp() throws IOException {
        echoApplication = new EchoApplication();
    }

    @Test
    public void constructResult_MultipleArguments_OutputMatchesExpected() throws AbstractApplicationException {
        String[] args = {"Hello World"};
        String result = echoApplication.constructResult(args);
        assertEquals("Hello World" + System.lineSeparator(), result);
    }
    @Test
    public void constructResult_OneArgument_OutputMatchesExpected() throws AbstractApplicationException {
        String testWord = "Hello";
        String[] args = {testWord};
        String result = echoApplication.constructResult(args);
        assertEquals(testWord + System.lineSeparator(), result);
    }
    @Test
    public void constructResult_NumberInputArgument_OutputMatchesExpected() throws AbstractApplicationException {
        String testNumbers = "1234567890";
        String[] args = {testNumbers};
        String result = echoApplication.constructResult(args);
        assertEquals(testNumbers + System.lineSeparator(), result);
    }

    @Test
    public void constructResult_SpaceInputArgument_OutputMatchesExpected() throws AbstractApplicationException {
        String testNumbers = " ";
        String[] args = {testNumbers};
        String result = echoApplication.constructResult(args);
        assertEquals(testNumbers + System.lineSeparator(), result);
    }

    @Test
    public void constructResult_NumberAndWordsInputArgument_OutputMatchesExpected() throws AbstractApplicationException {
        String testNumbers = "hello 123";
        String[] args = {testNumbers};
        String result = echoApplication.constructResult(args);
        assertEquals(testNumbers + System.lineSeparator(), result);
    }

    @Test
    public void constructResult_EmptyInputArgument_OutputMatchesExpected() throws AbstractApplicationException {
        String[] args = {};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        echoApplication.run(args, null, outputStream);
        assertEquals(System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void constructResult_SymbolInputArgument_OutputMatchesExpected() throws AbstractApplicationException {
        String symbols = "!@#$%^&*()_+";
        String[] args = {symbols};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        echoApplication.run(args, null, outputStream);
        assertEquals(symbols + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void run_ValidArguments_OutputToOutputStream() throws AbstractApplicationException {
        String[] args = {"Hello", "World"};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        echoApplication.run(args, null, outputStream);
        assertEquals("Hello World" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void run_NullOutputStream_ThrowsEchoException() throws AbstractApplicationException {
        String[] args = {"Hello", "World"};
        assertThrows(EchoException.class, () -> echoApplication.run(args, null, null));
    }

    @Test
    public void run_NoArguments_ThrowsEchoException() throws AbstractApplicationException {
        String[] args = {};
        assertThrows(EchoException.class, () -> echoApplication.run(args, null, null));
    }

}
