package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.parser.WcArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.WcResult;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;


public class WcApplication implements WcInterface {

    private static final String NUMBER_FORMAT = "\t%d";
    private static final int LINES_INDEX = 0;
    private static final int WORDS_INDEX = 1;
    private static final int BYTES_INDEX = 2;
    List<WcResult> totalResults = new ArrayList<>();

    /**
     * Runs the wc application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is
     *               the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this
     *               InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this
     *               OutputStream.
     * @throws WcException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws WcException {
        // Format: wc [-clw] [FILES]
        if (stdout == null || args == null || stdin == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }

        WcArgsParser wcArgsParser = new WcArgsParser();
        try {
            wcArgsParser.parse(args);
        } catch (InvalidArgsException e) {
            throw new WcException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }
        String result;
        StringBuilder resultSb = new StringBuilder();
        try {
            if (wcArgsParser.getFileNames().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(countFromStdin(wcArgsParser.isByteCount(), wcArgsParser.isLineCount(),
                        wcArgsParser.isWordCount(), stdin));
                result = stringBuilder.toString();
            } else if (!wcArgsParser.getFileNames().contains("-")) { //NOPMD - suppressed ConfusingTenarary - Ternary operator is not confusing
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(countFromFiles(wcArgsParser.isByteCount(), wcArgsParser.isLineCount(),
                        wcArgsParser.isWordCount(), wcArgsParser.getFileNames().toArray(new String[0])));
                result = stringBuilder.toString();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(countFromFileAndStdin(wcArgsParser.isByteCount(), wcArgsParser.isLineCount(),
                        wcArgsParser.isWordCount(), stdin, wcArgsParser.getFileNames().toArray(new String[0])));
                result = stringBuilder.toString();
            }
            resultSb.append(result);
            resultSb.append(STRING_NEWLINE);
        } catch (Exception e) {
            // Will never happen
            throw new WcException(ERR_GENERAL); // NOPMD
        }
        try {
            stdout.write(resultSb.toString().getBytes());
        } catch (IOException e) {
            throw new WcException(ERR_WRITE_STREAM);// NOPMD
        }
    }

    /**
     * Returns string containing the number of lines, words, and bytes in input
     * files
     *
     * @param isBytes  Boolean option to count the number of Bytes
     * @param isLines  Boolean option to count the number of lines
     * @param isWords  Boolean option to count the number of words
     * @param fileName Array of String of file names
     * @throws Exception
     */
    @Override
    public String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords, // NOPMD
            String... fileName) throws AbstractApplicationException {
        WcResult res = new WcResult();
        if (fileName == null) {
            throw new WcException(ERR_NULL_ARGS);
        }
        List<String> result = new ArrayList<>();
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                throw new WcException(ERR_FILE_NOT_FOUND);
            }
            if (node.isDirectory()) {
                throw new WcException(ERR_IS_DIR);
            }
            if (!node.canRead()) {
                throw new WcException(ERR_NO_PERM);
            }

            InputStream input = null; // NOPMD - Resource is closed in subsequent lines
            try {
                input = IOUtils.openInputStream(file);
            } catch (ShellException e) {
                throw new WcException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
            }
            long[] count = getCountReport(input); // lines words bytes
            try {
                IOUtils.closeInputStream(input);
            } catch (ShellException e) {
                throw new WcException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
            }

            // Update total count
            totalLines += count[0];
            totalWords += count[1];
            totalBytes += count[2];

            // Format all output: " %7d %7d %7d %s"
            // Output in the following order: lines words bytes filename
            StringBuilder sb = new StringBuilder(); // NOPMD
            if (isLines) {
                sb.append(String.format(NUMBER_FORMAT, count[0]));
                res.setLines(count[LINES_INDEX]);
            }
            if (isWords) {
                sb.append(String.format(NUMBER_FORMAT, count[1]));
                res.setWords(count[WORDS_INDEX]);
            }
            if (isBytes) {
                sb.append(String.format(NUMBER_FORMAT, count[2]));
                res.setBytes(count[BYTES_INDEX]);
            }
            if (!isLines && !isWords && !isBytes) {
                res.setLines(count[LINES_INDEX]);
                res.setWords(count[WORDS_INDEX]);
                res.setBytes(count[BYTES_INDEX]);
                sb.append(String.format(NUMBER_FORMAT, count[0]));
                sb.append(String.format(NUMBER_FORMAT, count[1]));
                sb.append(String.format(NUMBER_FORMAT, count[2]));
            }
            totalResults.add(res);
            sb.append(String.format(" %s", file));
            result.add(sb.toString());
        }

        if (fileName.length > 1) {
            StringBuilder sb = new StringBuilder(); // NOPMD
            sb.append(String.format(NUMBER_FORMAT, totalLines)).append(String.format(NUMBER_FORMAT, totalWords)).append(String.format(NUMBER_FORMAT, totalBytes)).append(" total");
            result.add(sb.toString());
        }
        return String.join(STRING_NEWLINE, result);
    }

    /**
     * Returns string containing the number of lines, words, and bytes in standard
     * input
     *
     * @param isBytes Boolean option to count the number of Bytes
     * @param isLines Boolean option to count the number of lines
     * @param isWords Boolean option to count the number of words
     * @param stdin   InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String countFromStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
            InputStream stdin) throws AbstractApplicationException {
        if (stdin == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        long[] count = getCountReport(stdin); // lines words bytes;
        WcResult res = new WcResult();

        StringBuilder sb = new StringBuilder(); // NOPMD
        if (isLines) {
            sb.append(String.format(NUMBER_FORMAT, count[0]));
            res.setLines(count[LINES_INDEX]);
        }
        if (isWords) {
            sb.append(String.format(NUMBER_FORMAT, count[1]));
            res.setWords(count[WORDS_INDEX]);
        }
        if (isBytes) {
            sb.append(String.format(NUMBER_FORMAT, count[2]));
            res.setBytes(count[BYTES_INDEX]);
        }
        totalResults.add(res);
        return sb.toString();
    }

    @Override
    public String countFromFileAndStdin(Boolean isBytes, Boolean isLines, Boolean isWords, InputStream stdin,
            String... fileNames) throws AbstractApplicationException {
        List<String> total = new ArrayList<>();
        for(String fileName : fileNames){
            if("-".equals(fileName)){
                StringBuilder sb = new StringBuilder(); // NOPMD
                sb.append(countFromStdin(isBytes, isLines, isWords, stdin));
                if (stdin != null) {
                    sb.append(" -");
                }
                total.add(sb.toString());
            } else {
                StringBuilder sb = new StringBuilder(); // NOPMD
                sb.append(countFromFiles(isBytes, isLines, isWords, fileName));
                total.add(sb.toString());
            }
        }
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        for (WcResult result : totalResults) {
            if (isBytes) {
                totalBytes += result.getBytes();
            }
            if (isLines) {
                totalLines += result.getLines();
            }
            if (isWords) {
                totalWords += result.getWords();
            }
        }
        StringBuilder sb = new StringBuilder(); // NOPMD
        if (isLines) {
            sb.append(String.format(NUMBER_FORMAT, totalLines));
        }
        if (isWords) {
            sb.append(String.format(NUMBER_FORMAT, totalWords));
        }
        if (isBytes) {
            sb.append(String.format(NUMBER_FORMAT, totalBytes));
        }
        if(fileNames.length > 1 && stdin != null){
            sb.append(" total");
            total.add(sb.toString());
        }
        sb.append(STRING_NEWLINE);
        return String.join(STRING_NEWLINE, total);
    }

    /**
     * Returns array containing the number of lines, words, and bytes based on data
     * in InputStream.
     *
     * @param input An InputStream
     * @throws IOException
     */
    public long[] getCountReport(InputStream input) throws AbstractApplicationException {
        if (input == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        long[] result = new long[3]; // lines, words, bytes

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int inRead = 0;
        boolean inWord = false;
        try {
            while ((inRead = input.read(data, 0, data.length)) != -1) {
                for (int i = 0; i < inRead; ++i) {
                    if (Character.isWhitespace(data[i])) {
                        // Use <newline> character here. (Ref: UNIX)
                        if (data[i] == '\n') {
                            ++result[LINES_INDEX];
                        }
                        if (inWord) {
                            ++result[WORDS_INDEX];
                        }

                        inWord = false;
                    } else {
                        inWord = true;
                    }
                }
                result[BYTES_INDEX] += inRead;
                buffer.write(data, 0, inRead);
            }
            buffer.flush();
            if (inWord) {
                ++result[WORDS_INDEX]; // To handle last word
            }
        } catch (IOException e) {
            throw new SortException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }

        return result;
    }
}
