package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.parser.CutArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IO_EXCEPTION;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CutApplication implements CutInterface { //NOPMD - suppressed GodClass - class is not overly complex - it follows single responsibility principle and implements correct functionality in a modular fashion
    /**
     * Executes the Cut application with the given arguments, input stream, and output stream.
     *
     * @param args   The arguments passed to the Cut application.
     * @param stdin  The input stream containing the input data.
     * @param stdout The output stream to write the result to.
     * @throws AbstractApplicationException If an error occurs during the execution of the Cut application.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (args == null) {
            throw new CutException(ErrorConstants.ERR_NULL_ARGS);
        }
        if (stdin == null) {
            throw new CutException(ErrorConstants.ERR_NO_ISTREAM);
        }
        if (stdout == null) {
            throw new CutException(ErrorConstants.ERR_NO_OSTREAM);
        }

        CutArgsParser parser = new CutArgsParser();
        try {
            parser.parse(args);
        } catch (Exception e) {
            throw new CutException(ErrorConstants.ERR_INVALID_FLAG + ": " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }

        boolean isCharPo = parser.isCutByCharacter();
        boolean isBytePo = parser.isCutByByte();
        List<int[]> ranges = parseRanges(parser.getCutList());
        List<String> fileNames = parser.getFiles();

        if ((isCharPo && isBytePo) || (!isCharPo && !isBytePo)) {
            throw new CutException(ErrorConstants.ERR_INVALID_FLAG);
        }

        String result;
        if (fileNames.isEmpty() || (fileNames.size() == 1 && "-".equals(fileNames.get(0)))) {
            result = cutFromStdin(isCharPo, isBytePo, ranges, stdin);
        } else {
            result = cutFromFiles(isCharPo, isBytePo, ranges, fileNames.toArray(new String[0]));
        }

        try {
            stdout.write(result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new CutException(ErrorConstants.ERR_WRITE_STREAM + ": " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
    }

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges   List of 2-element arrays containing the start and end indices for cut.
     *                 For instance, cutting on the first column would be represented using a [1,1] array.
     * @param fileNames Array of String of file names
     * @return
     * @throws Exception
     */
    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, String... fileNames) throws CutException {
        StringBuilder result = new StringBuilder();

        List<String> lines = new ArrayList<>();
        for (String file : fileNames) {
            if ("-".equals(file)) {
                continue;
            }
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                throw new CutException(ERR_FILE_NOT_FOUND);
            }
            if (node.isDirectory()) {
                throw new CutException(ERR_IS_DIR);
            }
            if (!node.canRead()) {
                throw new CutException(ERR_NO_PERM);
            }
            InputStream input = null; //NOPMD - suppressed CloseResource - it is being closed below (using IOUtils.closeInputStream(input);)

            try {
                input = IOUtils.openInputStream(file);
            } catch (ShellException e) {
                throw new CutException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
            try {
                lines.addAll(IOUtils.getLinesFromInputStream(input));
            } catch (IOException e) {
                throw new CutException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
            try {
                IOUtils.closeInputStream(input);
            } catch (ShellException e) {
                throw new CutException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
        }

        result.append(cutFromStream(isCharPo, isBytePo, ranges, lines));
        
        return result.toString();
    }

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges   List of 2-element arrays containing the start and end indices for cut.
     *                 For instance, cutting on the first column would be represented using a [1,1] array.
     * @param stdin    InputStream containing arguments from Stdin
     * @return
     * @throws Exception
     */
    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, InputStream stdin) throws AbstractApplicationException {
        try {
            validateRanges(ranges);
        } catch (IllegalArgumentException e) {
            throw new CutException("Invalid range provided."); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }

        List<String> lines = null;
        try {
            lines = IOUtils.getLinesFromInputStream(stdin);
        } catch (Exception e) {
            throw new CutException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }

        try {
            return cutFromStream(isCharPo, isBytePo, ranges, lines);
        } catch (Exception e) {
            throw new CutException(ErrorConstants.ERR_READING_FILE + ": stdin"); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
        }
    }

    /**
     * Reads input from a stream or file and performs cuts based on the specified ranges.
     *
     * @param isCharPo   a boolean indicating whether the ranges are character positions
     * @param isBytePo   a boolean indicating whether the ranges are byte positions
     * @param ranges     a list of integer arrays representing the start and end positions for each cut
     * @param lines      list of lines (string type)
     * @return a string containing the cuts made from the input
     */
    private String cutFromStream(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, List<String> lines) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < lines.size(); i++) {
            StringBuilder lineOutput = new StringBuilder();
            for (int[] range : ranges) {
                int start = Math.max(0, range[0] - 1);
                int end = range[1];
                if (isCharPo && start < lines.get(i).length()) {
                    end = Math.min(lines.get(i).length(), end);
                    lineOutput.append(lines.get(i), start, end);
                } else if (isBytePo) {
                    byte[] lineBytes = lines.get(i).getBytes(StandardCharsets.UTF_8);
                    if (start < lineBytes.length) {
                        end = Math.min(lineBytes.length, end);
                        lineOutput.append(new String(lineBytes, start, end - start, StandardCharsets.UTF_8));
                    }
                }
            }
            output.append(lineOutput).append(STRING_NEWLINE);
        }

        return output.toString();
    }

    /**
     * Parses the given list of cut ranges and returns a list of integer arrays representing the ranges.
     *
     * @param cutList the list of cut ranges to be parsed
     * @return a list of integer arrays representing the parsed ranges
     * @throws CutException if the cutList is null or empty, or if an invalid range is encountered
     */
    private List<int[]> parseRanges(List<String> cutList) throws CutException {
        if (cutList == null || cutList.isEmpty()) {
            throw new CutException(ErrorConstants.ERR_NO_ARGS);
        }
        List<int[]> ranges = new ArrayList<>();
        for (String part : cutList) {
            try {
                if (part.contains("-")) {
                    String[] tokens = part.split("-");
                    int start = Integer.parseInt(tokens[0]);
                    int end = Integer.parseInt(tokens[1]);
                    ranges.add(new int[]{start, end});
                } else {
                    int number = Integer.parseInt(part);
                    ranges.add(new int[]{number, number});
                }
            } catch (NumberFormatException e) {
                throw new CutException(ErrorConstants.ERR_INVALID_FLAG + ": " + part); //NOPMD - suppressed PreserveStackTrace - We are following the implementation of the existing exceptions, hence this warning is ignored.
            }
        }
        return ranges;
    }

    /**
     * Validates the given list of ranges.
     *
     * @param ranges the list of ranges to be validated
     * @throws IllegalArgumentException if any of the ranges is invalid
     */
    private void validateRanges(List<int[]> ranges) throws IllegalArgumentException {
        for (int[] range : ranges) {
            if (range[0] < 1 || range[1] < range[0]) {
                throw new IllegalArgumentException("Invalid range: " + Arrays.toString(range));
            }
        }
    }
}


