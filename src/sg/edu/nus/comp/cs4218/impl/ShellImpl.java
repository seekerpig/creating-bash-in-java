package sg.edu.nus.comp.cs4218.impl;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ShellImpl implements Shell {

    /**
     * Main method for the Shell Interpreter program.
     *
     * @param args List of strings arguments, unused.
     */
    public static void main(String... args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Shell shell = new ShellImpl();
        String currentDirectory;
        String commandString;
        boolean exitSuccess = true;

        try {
            do {
                currentDirectory = Environment.currentDirectory;

                System.out.print(currentDirectory + "> ");

                commandString = reader.readLine();

                if (!StringUtils.isBlank(commandString)) {
                    shell.parseAndEvaluate(commandString, System.out);
                }
            }
            while (commandString != null);

        } catch (IOException exception) {
            System.out.println("IOException: " + exception.getMessage());
            exitSuccess = false;
        } catch (ExitException exception) {
            System.out.println("ExitException: " + exception.getMessage());
            if (!exception.getMessage().endsWith("success")) {
                exitSuccess = false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exitSuccess = false;
        } finally {
            try {
                reader.close();

                if (exitSuccess) {
                    System.exit(0); // System executed successfully
                }
            } catch (IOException exception) {
                System.out.println("IOException: " + exception.getMessage());
                System.exit(1); // To indicate execution error in Exit Status
            }
        }
    }

    @Override
    public void parseAndEvaluate(String commandString, OutputStream stdout)
            throws AbstractApplicationException, ShellException, FileNotFoundException {
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
    }
}
