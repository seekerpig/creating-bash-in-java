package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SortInterface;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.parser.SortArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SortApplication implements SortInterface {

    /**
     * Runs the sort application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws SortException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws SortException {
        // Format: sort [-nrf] [FILES]
        if (stdout == null) {
            throw new SortException(ERR_NULL_STREAMS);
        }
        SortArgsParser sortArgsParser = new SortArgsParser();
        try {
            sortArgsParser.parse(args);
        } catch (InvalidArgsException e) {
            throw new SortException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
        StringBuilder output = new StringBuilder();
        try {
            if (sortArgsParser.getFileNames().isEmpty()) {
                output.append(sortFromStdin(sortArgsParser.isFirstWordNumber(), sortArgsParser.isReverseOrder(), sortArgsParser.isCaseIndependent(), stdin));
            } else {
                output.append(sortFromFiles(sortArgsParser.isFirstWordNumber(), sortArgsParser.isReverseOrder(), sortArgsParser.isCaseIndependent(), sortArgsParser.getFileNames().toArray(new String[0])));
            }
        } catch (Exception e) {
            throw new SortException(e.getMessage());//NOPMD
        }
        try {
            if (!output.toString().isEmpty()) {
                stdout.write(output.toString().getBytes());
                stdout.write(STRING_NEWLINE.getBytes());
            }
        } catch (IOException e) {
            throw new SortException(ERR_WRITE_STREAM);//NOPMD
        }
    }

    /**
     * Returns string containing the orders of the lines of the specified file
     *
     * @param isFirstWordNumber Boolean option to treat the first word of a line as a number
     * @param isReverseOrder    Boolean option to sort in reverse order
     * @param isCaseIndependent Boolean option to perform case-independent sorting
     * @param fileNames         Array of String of file names
     * @throws Exception
     */
    @Override
    public String sortFromFiles(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent,
                                String... fileNames) throws SortException {
        if (fileNames == null) {
            throw new SortException(ERR_NULL_ARGS);
        }
        List<String> lines = new ArrayList<>();
        for (String file : fileNames) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                throw new SortException(ERR_FILE_NOT_FOUND);
            }
            if (node.isDirectory()) {
                throw new SortException(ERR_IS_DIR);
            }
            if (!node.canRead()) {
                throw new SortException(ERR_NO_PERM);
            }
            InputStream input = null; //NOPMD - suppressed CloseResource - it is being closed below (using IOUtils.closeInputStream(input);)

            try {
                input = IOUtils.openInputStream(file);
            } catch (ShellException e) {
                throw new SortException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
            try {
                lines.addAll(IOUtils.getLinesFromInputStream(input));
            } catch (IOException e) {
                throw new SortException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace -We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
            try {
                IOUtils.closeInputStream(input);
            } catch (ShellException e) {
                throw new SortException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }

        }
        sortInputString(isFirstWordNumber, isReverseOrder, isCaseIndependent, lines);
        return String.join(STRING_NEWLINE, lines);
    }

    /**
     * Returns string containing the orders of the lines from the standard input
     *
     * @param isFirstWordNumber Boolean option to treat the first word of a line as a number
     * @param isReverseOrder    Boolean option to sort in reverse order
     * @param isCaseIndependent Boolean option to perform case-independent sorting
     * @param stdin             InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String sortFromStdin(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent,
                                InputStream stdin) throws SortException {
        if (stdin == null) {
            throw new SortException(ERR_NULL_STREAMS);
        }
        List<String> lines = null;
        try {
            lines = IOUtils.getLinesFromInputStream(stdin);
        } catch (Exception e) {
            throw new SortException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
        sortInputString(isFirstWordNumber, isReverseOrder, isCaseIndependent, lines);
        return String.join(STRING_NEWLINE, lines);
    }

    /**
     * Sorts the input ArrayList based on the given conditions. Invoking this function will mutate the ArrayList.
     *
     * @param isFirstWordNumber Boolean option to treat the first word of a line as a number
     * @param isReverseOrder    Boolean option to sort in reverse order
     * @param isCaseIndependent Boolean option to perform case-independent sorting
     * @param input             ArrayList of Strings of lines
     */
    private void sortInputString(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent,
                                 List<String> input) {
        Collections.sort(input, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                String temp1 = isCaseIndependent ? str1.toLowerCase() : str1.replaceAll("^\\s+", "");;//NOPMD
                String temp2 = isCaseIndependent ? str2.toLowerCase() : str2.replaceAll("^\\s+", "");;//NOPMD

                // Extract the first group of numbers if possible.
                if (isFirstWordNumber && !temp1.isEmpty() && !temp2.isEmpty()) {
                    String chunk1 = getChunk(temp1);//NOPMD
                    String chunk2 = getChunk(temp2);//NOPMD

                    // If both chunks can be represented as numbers, sort them numerically.
                    int result = 0;

                    if (chunk1.charAt(0) == '-' && Character.isDigit(chunk2.charAt(0))) {
                        result = -1;
                    } else if (chunk2.charAt(0) == '-' && Character.isDigit(chunk1.charAt(0))) {
                        result = 1;
                    } else if (chunk1.charAt(0) == '-' && chunk2.charAt(0) == '-') {
                        BigInteger num1 = new BigInteger(chunk1);
                        BigInteger num2 = new BigInteger(chunk2);
                        result =  num2.abs().compareTo(num1.abs());
                    } else if (Character.isDigit(chunk1.charAt(0)) && Character.isDigit(chunk2.charAt(0))) {
                        BigInteger num1 = new BigInteger(chunk1);
                        BigInteger num2 = new BigInteger(chunk2);
                        result = num1.compareTo(num2);
                    } else {
                        result = chunk1.compareTo(chunk2);
                    }
                    if (result != 0) {
                        return result;
                    }
                    return temp1.substring(chunk1.length()).compareTo(temp2.substring(chunk2.length()));
                }

                return temp1.compareTo(temp2);
            }
        });
        if (isReverseOrder) {
            Collections.reverse(input);
        }
    }

    /**
     * Extracts a chunk of numbers or non-numbers from str starting from index 0.
     *
     * @param str Input string to read from
     */
    private String getChunk(String str) {
        int startIndexLocal = 0;
        StringBuilder chunk = new StringBuilder();
        int strLen = str.length();
        if (str.charAt(0) == '-') {
            char chr = str.charAt(startIndexLocal);
            startIndexLocal++;
            chunk.append(chr);
        }
        char chr = str.charAt(startIndexLocal++);
        chunk.append(chr);
        final boolean extractDigit = Character.isDigit(chr);
        while (startIndexLocal < strLen) {
            chr = str.charAt(startIndexLocal++);
            if ((extractDigit && !Character.isDigit(chr)) || (!extractDigit && Character.isDigit(chr))) {
                break;
            }
            chunk.append(chr);
        }
        return chunk.toString();
    }
}
