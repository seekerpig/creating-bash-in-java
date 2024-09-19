package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SequenceCommandTest {
    private CallCommand command1;
    private CallCommand command2;
    private CallCommand invalidCommand;
    private InputStream inputStream;
    private OutputStream outputStream;
    @BeforeEach
    void setUp() {
        ApplicationRunner appRunner = new ApplicationRunner();
        ArgumentResolver argumentResolver = new ArgumentResolver();
        inputStream = System.in;
        outputStream = new ByteArrayOutputStream();
        // setup call command
        List<String> argsList1 = new ArrayList<>(List.of("echo", "hello"));
        command1 = new CallCommand(argsList1, appRunner, argumentResolver);
        List<String> argsList2 = new ArrayList<>(List.of("echo", "world"));
        command2 = new CallCommand(argsList2, appRunner, argumentResolver);
        List<String> argsList3 = new ArrayList<>(List.of("dummy"));
        invalidCommand = new CallCommand(argsList3, appRunner, argumentResolver);
    }

    // test for getCommands
    @Test
    public void testGetCommands_withCommands_shouldReturnCommandList() {
        List<Command> commandList = new ArrayList<>();
        commandList.add(command1);
        SequenceCommand sequenceCommand = new SequenceCommand(commandList);

        assertEquals(commandList, sequenceCommand.getCommands());
    }

    @Test
    public void testEvaluate_withValidCommands_shouldReturnCorrectOutput() {
        // create a new sequence command
        List<Command> commandList = new ArrayList<>();
        commandList.add(command1);
        commandList.add(command2);
        SequenceCommand sequenceCommand = new SequenceCommand(commandList);

        assertDoesNotThrow(() -> sequenceCommand.evaluate(inputStream, outputStream));
        assertEquals("hello" + StringUtils.STRING_NEWLINE + "world" + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void testEvaluate_withInvalidCommand_shouldThrowException() {
        List<Command> commandList = new ArrayList<>();
        commandList.add(command1);
        commandList.add(invalidCommand);
        SequenceCommand sequenceCommand = new SequenceCommand(commandList);

        assertDoesNotThrow(() -> sequenceCommand.evaluate(inputStream, outputStream));
        assertEquals("hello" + StringUtils.STRING_NEWLINE + "shell: dummy: " + ErrorConstants.ERR_INVALID_APP + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    // invalid output stream should throw exception
//    @Test
//    public void testEvaluate_withInvalidOutputStream_shouldThrowException() {
//        List<Command> commandList = new ArrayList<>();
//        commandList.add(command1);
//        commandList.add(command2);
//        SequenceCommand sequenceCommand = new SequenceCommand(commandList);
//
//        OutputStream invalidOutputStream = new BufferedOutputStream(new ByteArrayOutputStream()) {
//            @Override
//            public void write(int b) throws IOException {
//                throw new IOException();
//            }
//        };
//
//        assertThrows(Exception.class, () -> sequenceCommand.evaluate(inputStream, invalidOutputStream));
//    }

    @Test
    public void testTerminate_withCommands_shouldNotThrowException() {
        List<Command> commandList = new ArrayList<>();
        commandList.add(command1);
        commandList.add(command2);
        SequenceCommand sequenceCommand = new SequenceCommand(commandList);

        assertDoesNotThrow(() -> sequenceCommand.terminate());
    }
}