package sg.edu.nus.comp.cs4218.impl.cmd;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.IORedirectionHandler;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

/**
 * A Call Command is a sub-command consisting of at least one non-keyword or quoted.
 * <p>
 * Command format: (<non-keyword> or <quoted>) *
 */
public class CallCommand implements Command {
    private final List<String> argsList;
    private final ApplicationRunner appRunner;
    private final ArgumentResolver argumentResolver;

    public CallCommand(List<String> argsList, ApplicationRunner appRunner, ArgumentResolver argumentResolver) {
        // Tokenized array of arguments
        this.argsList = argsList;
        // ApplicationRunner to run the application (each command has its own ApplicationRunner)
        this.appRunner = appRunner;
        this.argumentResolver = argumentResolver;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException, FileNotFoundException {
        if (argsList == null || argsList.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }

        // Handle IO redirection

        /* Description of IO redirection
         * Allows you to control where a command gets its input or sends its output.
         * Input redirection allows you to read input from a file instead of keyboard
         * Keyboard is the default input source
         * Example: cat < file.txt
         * Output redirection allows you to write output to a file instead of the screen
         * Screen is the default output destination
         * Example: cat file.txt > newfile.txt
         * Error redirection allows you to write error messages to a file instead of the screen
         * Screen is the default error destination
         * Example: cat file.txt 2 > errorfile.txt
         * Combination: mycommand < input.txt > output.txt
         */

        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, stdin, stdout, argumentResolver);
        redirHandler.extractRedirOptions();
        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        InputStream inputStream = redirHandler.getInputStream(); //NOPMD - suppressed CloseResource - Stream is properly closed.
        OutputStream outputStream = redirHandler.getOutputStream(); //NOPMD - suppressed CloseResource - Stream is properly closed.

        /*
         * In a traditional Unix-like shell, a command can only have one standard input stream and one standard output stream at a time.
         * If you try to set multiple input or output streams for a single command, it would typically result in an error or unexpected behavior.
         * For example, in the code you provided, if an input or output stream has already been set (i.e., it's not the original stream), and you try to set another one, it throws a ShellException with an error message indicating that multiple streams are not allowed.
         * However, it's worth noting that while a command can only have one standard input and one standard output, it can still read from and write to multiple files by opening them directly. This is different from input/output redirection, which affects the standard input and output streams.
         */

        // Handle quoting + globing + command substitution
        List<String> parsedArgsList = argumentResolver.parseArguments(noRedirArgsList);
        if (!parsedArgsList.isEmpty()) {
            String app = parsedArgsList.remove(0);
            // First parameter is command name - identifier
            // Second parameter is the list of arguments
            // The 0 in new String[0] is the initial size of the array. However, toArray will return an array that is big enough to hold all the elements in the list, regardless of the size of the array passed as an argument. 
            // So even though new String[0] creates an empty array, parsedArgsList.toArray(new String[0]) will return a String array with the same size as parsedArgsList.
            appRunner.runApp(app, parsedArgsList.toArray(new String[0]), inputStream, outputStream);
        }

        IOUtils.closeInputStream(inputStream);
        IOUtils.closeOutputStream(outputStream);
    }

    @Override
    public void terminate() {
        // Unused for now
    }

    public List<String> getArgsList() {
        return argsList;
    }
}
