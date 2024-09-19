package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MULTIPLE_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_INPUT;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_OUTPUT;

public class IORedirectionHandler {
    private final List<String> argsList;
    private final ArgumentResolver argumentResolver;
    private InputStream origInputStream;
    private OutputStream origOutputStream;
    private List<String> noRedirArgsList;
    private InputStream inputStream;
    private OutputStream outputStream;

    public IORedirectionHandler(List<String> argsList, InputStream origInputStream,
                                OutputStream origOutputStream, ArgumentResolver argumentResolver) {
        this.argsList = argsList;
        this.inputStream = origInputStream;
        this.origInputStream = origInputStream;
        this.outputStream = origOutputStream;
        this.origOutputStream = origOutputStream;
        this.argumentResolver = argumentResolver;
    }

    /*
     * It iterates over the list of arguments. 
     * If an argument is a redirection operator (< for input redirection, > for output redirection), it treats the next argument as a filename 
     * and opens an input or output stream for that file
     * If a redirection operator is found but the next argument is also a redirection operator, it throws a ShellException with a syntax error message.
     * If a redirection operator is found and the next argument resolves to more than one file (due to globbing or command substitution), it throws a ShellException with a syntax error message.
     * If a redirection operator is found and an input or output stream has already been opened (not the original stream), it throws a ShellException with a multiple streams error message.
     */

    public void extractRedirOptions() throws AbstractApplicationException, ShellException, FileNotFoundException { //NOPMD - suppressed ExcessiveMethodLength - Method needs to be long to abide by the method requirements
        if (argsList == null || argsList.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }

        noRedirArgsList = new LinkedList<>();

        // extract redirection operators (with their corresponding files) from argsList
        ListIterator<String> argsIterator = argsList.listIterator();
        String inputFile = null;
        String outputFile = null;
        while (argsIterator.hasNext()) {
            String arg = argsIterator.next();

            // leave the other args untouched
            if (!isRedirOperator(arg)) {
                noRedirArgsList.add(arg);
                continue;
            }

            // if there are still other similar redir operators after, continue

            // if current arg is < or >, fast-forward to the next arg to extract the specified file
            String file = argsIterator.next();

            if (isRedirOperator(file)) {
            }

            // handle quoting + globing + command substitution in file arg
            List<String> fileSegment = argumentResolver.resolveOneArgument(file);
            if (fileSegment.size() > 1) {
                // ambiguous redirect if file resolves to more than one parsed arg
                throw new ShellException(ERR_SYNTAX);
            }
            file = fileSegment.get(0);


            if (arg.equals(String.valueOf(CHAR_REDIR_INPUT))) {
                inputFile = file;
            } else if (arg.equals(String.valueOf(CHAR_REDIR_OUTPUT))) {
                outputFile = file;
            }
        }
        // if need to change streams, close existing streams, open new streams
        if (inputFile != null) {
            IOUtils.closeInputStream(inputStream);
            if (!inputStream.equals(origInputStream)) {
                throw new ShellException(ERR_MULTIPLE_STREAMS);
            }
            inputStream = IOUtils.openInputStream(inputFile);
            origInputStream = inputStream;
        }
        if (outputFile != null) {
            IOUtils.closeOutputStream(outputStream);
            if (!outputStream.equals(origOutputStream)) {
                throw new ShellException(ERR_MULTIPLE_STREAMS);
            }
            outputStream = IOUtils.openOutputStream(outputFile);
            origOutputStream = outputStream;
        }
    }

    // returns the list of arguments without the redirection options
    // "command", "arg1", ">", "output.txt" ===> "command", "arg1"
    public List<String> getNoRedirArgsList() {
        return noRedirArgsList;
    }

    // still returns System.in, because there's no input redirection in the command
    public InputStream getInputStream() {
        return inputStream;
    }

    // returns an OutputStream for the file "output.txt"
    public OutputStream getOutputStream() {
        return outputStream;
    }

    private boolean isRedirOperator(String str) {
        return str.equals(String.valueOf(CHAR_REDIR_INPUT)) || str.equals(String.valueOf(CHAR_REDIR_OUTPUT));
    }
}
