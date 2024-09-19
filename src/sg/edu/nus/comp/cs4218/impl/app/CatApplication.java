package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.parser.CatArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IO_EXCEPTION;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CatApplication implements CatInterface {
    /**
     * Executes the Cat application with the given arguments, input stream, and output stream.
     *
     * @param args   The command-line arguments passed to the Cat application.
     * @param stdin  The input stream containing the data to be processed by the Cat application.
     * @param stdout The output stream where the result of the Cat application will be written.
     * @throws CatException If an error occurs during the execution of the Cat application.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {
        if (stdout == null) {
            throw new CatException(ErrorConstants.ERR_NO_OSTREAM);
        }

        CatArgsParser parser = new CatArgsParser();

        try {
            parser.parse(args);
            boolean isLineNumber = parser.isNumberLines();
            String[] files = parser.getFiles().toArray(new String[0]);
            List<String> fileList = parser.getFiles();

            String result;
            if (fileList.isEmpty() || (fileList.size() == 1 && "-".equals(fileList.get(0)))) {
                result = catStdin(isLineNumber, stdin);
            } else if (parser.getFiles().contains("-")) {
                result = catFileAndStdin(isLineNumber, stdin, files);
            } else {
                result = catFiles(isLineNumber, files);
            }
            
            stdout.write(result.getBytes(StandardCharsets.UTF_8));
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (Exception e) {
            throw new CatException(ErrorConstants.ERR_GENERAL + ": " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.

        }
    }

    /**
     * Returns string containing the content of the specified file
     *
     * @param isLineNumber Prefix lines with their corresponding line number starting from 1
     * @param fileName     Array of String of file names (not including "-" for reading from stdin)
     * @return
     * @throws Exception
     */
    @Override
    public String catFiles(Boolean isLineNumber, String... fileName) throws CatException { //NOPMD - suppressed ExcessiveMethodLength - the method doesn't seem long at all to me, it is very readable
        StringBuilder output = new StringBuilder();
        int lineNumber = 1;

        List<String> lines = new ArrayList<>();
        for (String file : fileName) {
            if ("-".equals(file)) {
                continue;
            }
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                throw new CatException(ERR_FILE_NOT_FOUND);
            }
            if (node.isDirectory()) {
                throw new CatException(ERR_IS_DIR);
            }
            if (!node.canRead()) {
                throw new CatException(ERR_NO_PERM);
            }
            InputStream input = null; //NOPMD - suppressed CloseResource - it is being closed below (using IOUtils.closeInputStream(input);)

            try {
                input = IOUtils.openInputStream(file);
            } catch (ShellException e) { //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.

                throw new CatException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
            try {
                lines.addAll(IOUtils.getLinesFromInputStream(input)); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            } catch (IOException e) {
                throw new CatException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
            try {
                IOUtils.closeInputStream(input); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            } catch (ShellException e) {
                throw new CatException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
        }
        
        for (int i = 0; i < lines.size(); i++) {
            if (isLineNumber) {
                output.append(lineNumber++).append(' ').append(lines.get(i));
            } else {
                output.append(lines.get(i));
            }
            if (i < lines.size() - 1) {
                output.append(STRING_NEWLINE);
            }
        }

        return output.toString();
    }

    /**
     * Returns string containing the content of the standard input
     *
     * @param isLineNumber Prefix lines with their corresponding line number starting from 1
     * @param stdin        InputStream containing arguments from Stdin
     * @return
     * @throws Exception
     */
    @Override
    public String catStdin(Boolean isLineNumber, InputStream stdin) throws CatException {
        if (stdin == null) {
            throw new CatException(ErrorConstants.ERR_NO_OSTREAM);
        }

        StringBuilder output = new StringBuilder();
        try {
            List<String> lines = IOUtils.getLinesFromInputStream(stdin);
            for (int i = 0; i < lines.size(); i++) {
                if (isLineNumber) {
                    output.append(i + 1).append(' ').append(lines.get(i)).append(STRING_NEWLINE);
                } else {
                    output.append(lines.get(i)).append(STRING_NEWLINE);
                }
            } //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        } catch (IOException e) {
            throw new CatException("cat: Error reading input"); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }

        if (output.length() > 0) {
            String lineSeparator = STRING_NEWLINE;
            output.delete(output.length() - lineSeparator.length(), output.length());
        }

        return output.toString();
    }

    /**
     * Returns string containing the content of the standard input and specified file
     *
     * @param isLineNumber Prefix lines with their corresponding line number starting from 1
     * @param stdin        InputStream containing arguments from Stdin
     * @param fileName     Array of String of file names (including "-" for reading from stdin)
     * @return
     * @throws Exception
     */
    @Override
    public String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws CatException {
        if (stdin == null) {
            throw new CatException(ErrorConstants.ERR_NO_OSTREAM);
        }

        StringBuilder output = new StringBuilder();

        output.append(catFiles(isLineNumber, fileName));

        int lineNumber = output.toString().split(STRING_NEWLINE).length;
        output.append(STRING_NEWLINE);

        if (isLineNumber) {
            String stdinOutput = catStdin(false, stdin);
            String[] stdinOutputLines = stdinOutput.split(STRING_NEWLINE);
            for (int i = 0; i < stdinOutputLines.length; i++) {
                output.append(lineNumber + i + 1).append(' ').append(stdinOutputLines[i]).append(STRING_NEWLINE);
            }
            if (output.length() > 0) {
                String lineSeparator = STRING_NEWLINE;
                output.delete(output.length() - lineSeparator.length(), output.length());
            }
        } else {
            output.append(catStdin(isLineNumber, stdin));
        }
        
        return output.toString();
    }
}
