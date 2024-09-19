package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.UniqInterface;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.parser.UniqArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IO_EXCEPTION;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class UniqApplication implements UniqInterface { //NOPMD - suppressed GodClass - class is not overly complex - it follows single responsibility principle and implements correct functionality in a modular fashion
    private static final String EMPTY_STRING = "";
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws UniqException {
        if (stdout == null) {
            throw new UniqException(ErrorConstants.ERR_NO_OSTREAM);
        }

        UniqArgsParser uniqArgsParser = new UniqArgsParser();
        try {
            uniqArgsParser.parse(args);
        } catch (InvalidArgsException e) {
            throw new UniqException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }

        StringBuilder output = new StringBuilder();
        try {
            if ((uniqArgsParser.getInputFileName() != null) && (!uniqArgsParser.getInputFileName().equals("-"))) {
                output.append(uniqFromFile(uniqArgsParser.isCountFlag(), uniqArgsParser.isDuplicateFlag(), uniqArgsParser.isAllDuplicateFlag(), uniqArgsParser.getInputFileName(), uniqArgsParser.getOutputFileName()));
            } else {
                output.append(uniqFromStdin(uniqArgsParser.isCountFlag(), uniqArgsParser.isDuplicateFlag(), uniqArgsParser.isAllDuplicateFlag(), stdin, uniqArgsParser.getOutputFileName()));
            }
        } catch (Exception e) {
            throw new UniqException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
        
        String result = output.toString();

        if (uniqArgsParser.getOutputFileName() != null && !uniqArgsParser.getOutputFileName().isEmpty()) {
            try {
                Files.write(Paths.get(uniqArgsParser.getOutputFileName()), result.getBytes(StandardCharsets.UTF_8));
                stdout.write(System.lineSeparator().getBytes());
            } catch (Exception e) {
                throw new UniqException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
        } else {
            try {
                stdout.write(result.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new UniqException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
        }
    }

    /**
     * Filters adjacent matching lines from INPUT_FILE or standard input and writes to an OUTPUT_FILE or to standard output.
     *
     * @param isCount        Boolean option to prefix lines by the number of occurrences of adjacent duplicate lines
     * @param isRepeated     Boolean option to print only duplicate lines, one for each group
     * @param isAllRepeated  Boolean option to print all duplicate lines (takes precedence if isRepeated is set to true)
     * @param inputFileName  of path to input file
     * @param outputFileName of path to output file (if any)
     * @throws Exception
     */
    public String uniqFromFile(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, String inputFileName, String outputFileName) throws UniqException {
        if (inputFileName == null || inputFileName.isEmpty()) {
            throw new UniqException("Input file name cannot be null or empty.");
        }

        List<String> lines = new ArrayList<>();

        File node = IOUtils.resolveFilePath(inputFileName).toFile();
        if (!node.exists()) {
            throw new UniqException(ERR_FILE_NOT_FOUND);
        }
        if (node.isDirectory()) {
            throw new UniqException(ERR_IS_DIR);
        }
        if (!node.canRead()) {
            throw new UniqException(ERR_NO_PERM);
        }
        InputStream input = null; //NOPMD - suppressed CloseResource - it is being closed below (using IOUtils.closeInputStream(input);)

        try {
            input = IOUtils.openInputStream(inputFileName);
        } catch (ShellException e) {
            throw new UniqException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
        try {
            lines.addAll(IOUtils.getLinesFromInputStream(input));
        } catch (IOException e) {
            throw new UniqException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
        try {
            IOUtils.closeInputStream(input);
        } catch (ShellException e) {
            throw new UniqException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }

        return uniqInputString(isCount, isRepeated, isAllRepeated, lines);
    }

    /**
     * Filters adjacent matching lines from INPUT_FILE or standard input and writes to an OUTPUT_FILE or to standard output.
     *
     * @param isCount       Boolean option to prefix lines by the number of occurrences of adjacent duplicate lines
     * @param isRepeated    Boolean option to print only duplicate lines, one for each group
     * @param isAllRepeated Boolean option to print all duplicate lines (takes precedence if isRepeated is set to true)
     * @param stdin         InputStream containing arguments from Stdin
     * @param outputFileName of path to output file (if any)
     * @throws Exception
     */
    public String uniqFromStdin(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, InputStream stdin, String outputFileName) throws UniqException {
        if (stdin == null) {
            throw new UniqException(ERR_NULL_STREAMS);
        }
        List<String> lines = null;
        try {
            lines = IOUtils.getLinesFromInputStream(stdin);
        } catch (Exception e) {
            throw new UniqException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
        
        return uniqInputString(isCount, isRepeated, isAllRepeated, lines);
    }

    private void appendLine(StringBuilder output, Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, String line, int count) {
        if (isAllRepeated) {
            if (count > 1) {
                for (int i = 0; i < count; i++) {
                    if (isCount) {
                        output.append(count).append(' ').append(line).append(System.lineSeparator());
                    } else {
                        output.append(line).append(System.lineSeparator());
                    }
                }
            }
        } else if (isRepeated) {
            if (count > 1) {
                if (isCount) {
                    output.append(count).append(' ').append(line).append(System.lineSeparator());
                } else {
                    output.append(line).append(System.lineSeparator());
                }
            }
        } else if (isCount) {
            output.append(count).append(' ').append(line).append(System.lineSeparator());
        } else {
            output.append(line).append(System.lineSeparator());
        }
    }

    public String uniqInputString(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, List<String> lines) {
        StringBuilder output = new StringBuilder();
        String previousLine = null;
        int count = 0;
        
        if (lines.isEmpty()) {
            return System.lineSeparator();
        }

        for (String currentLine : lines) {
            if (currentLine.equals(previousLine)) {
                count++;
            } else {
                if (previousLine != null) {
                    appendLine(output, isCount, isRepeated, isAllRepeated, previousLine, count);
                }
                previousLine = currentLine;
                count = 1;
            }
        }

        if (previousLine != null) {
            appendLine(output, isCount, isRepeated, isAllRepeated, previousLine, count);
        }
        
        return output.toString();
    }
}