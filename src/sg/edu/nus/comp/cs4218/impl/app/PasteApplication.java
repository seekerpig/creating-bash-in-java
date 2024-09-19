package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.parser.PasteArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PasteApplication implements PasteInterface { //NOPMD - suppressed GodClass - Only implemented methods given by interface
    private static final char TAB_CHAR = '\t';
    private static final String NEWLINE_CHAR = System.lineSeparator();

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws PasteException {
        if (stdin == null) {
            throw new PasteException("No InputStream provided");
        }
        if (stdout == null) {
            throw new PasteException("No OutputStream provided");
        }

        PasteArgsParser parser = new PasteArgsParser();
        try {
            parser.parse(args);
        } catch (Exception e) {
            throw new PasteException("Error parsing arguments: " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Exceptions are thrown
        }

        boolean isSerial = parser.isSerial();
        String[] fileNames = parser.getFiles().toArray(new String[0]);

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stdout));
        try {
            boolean mergeWithStdin = Arrays.asList(fileNames).contains("-");
            String result;
            boolean onlyStdin = fileNames.length == 0 || (fileNames.length == 1 && fileNames[0].equals("-"));

            if (onlyStdin) {
                result = mergeStdin(isSerial, stdin);
            } else if (fileNames.length == 0 || mergeWithStdin) {
                result = mergeWithStdin ? mergeFileAndStdin(isSerial, stdin, fileNames) : mergeStdin(isSerial, stdin);
            } else {
                result = mergeFile(isSerial, fileNames);
            }

            if (!result.isEmpty()) {
                result += System.lineSeparator();
            }
            writer.write(result);
        } catch (Exception e) {
            throw new PasteException("Error merging: " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Exceptions are thrown
        } finally {
            writer.flush();
        }
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) Stdin arguments. If only one Stdin
     * arg is specified, echo back the Stdin.
     *
     * @param isSerial Paste one file at a time instead of in parallel
     * @param stdin    InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String mergeStdin(Boolean isSerial, InputStream stdin) throws PasteException {
        if (stdin == null) {
            throw new PasteException("InputStream cannot be null");
        }

        try {
            List<String> lines = IOUtils.getLinesFromInputStream(stdin);
            return processLines(lines, isSerial);
        } catch (Exception e) {
            throw new PasteException("Error while reading from stdin" + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Following implementation of existing exceptions
        }
    }

    private String processLines(List<String> lines, Boolean isSerial) {
        if (isSerial == null || !isSerial) {
            return String.join(NEWLINE_CHAR, lines);
        } else {
            return String.join(String.valueOf(TAB_CHAR), lines);
        }
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files. If only one file is
     * specified, echo back the file content.
     *
     * @param isSerial  Paste one file at a time instead of in parallel
     * @param fileNames Array of file names to be read and merged (not including "-" for reading from stdin)
     * @throws Exception
     */
    public String mergeFile(Boolean isSerial, String... fileNames) throws PasteException {
        if (fileNames == null || fileNames.length == 0) {
            throw new PasteException("No input files provided");
        }

        if (isSerial) {
            return mergeFilesSerially(fileNames);
        } else {
            return mergeFilesInParallel(fileNames);
        }
    }

    private String mergeFilesSerially(String... fileNames) throws PasteException {
        StringBuilder output = new StringBuilder();
        for (String fileName : fileNames) {
            if (fileName == null) {
                continue;
            }
            List<String> lines = readFileLines(fileName);
            for (String line : lines) {
                output.append(line).append('\t');
            }
            if (!lines.isEmpty()) {
                output.setLength(output.length() - 1); // Remove last tab
            }
            output.append(System.lineSeparator());
        }
        return output.toString().trim(); // Remove last newline
    }

    private String mergeFilesInParallel(String... fileNames) throws PasteException {
        StringBuilder output = new StringBuilder();
        List<List<String>> allLines = new ArrayList<>();
        int maxLines = 0;

        for (String fileName : fileNames) {
            if (fileName == null) {
                continue;
            }
            List<String> lines = readFileLines(fileName);
            maxLines = Math.max(maxLines, lines.size());
            allLines.add(lines);
        }

        for (int i = 0; i < maxLines; i++) {
            for (int j = 0; j < allLines.size(); j++) {
                List<String> lines = allLines.get(j);
                if (i < lines.size()) {
                    output.append(lines.get(i));
                }
                if (j < allLines.size() - 1) {
                    output.append('\t');
                }
            }
            output.append(System.lineSeparator());
        }
        return output.toString().trim(); // Remove last newline
    }

    private List<String> readFileLines(String fileName) throws PasteException {
        try {
            InputStream inputStream = IOUtils.openInputStream(fileName); //NOPMD - suppressed CloseResource - Already closed below
            List<String> lines = IOUtils.getLinesFromInputStream(inputStream);
            IOUtils.closeInputStream(inputStream);
            return lines;
        } catch (Exception e) {
            throw new PasteException("Error reading file: " + fileName); //NOPMD - suppressed PreserveStackTrace - Following implementation of existing exceptions
        }
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files and Stdin arguments.
     *
     * @param isSerial  Paste one file at a time instead of in parallel
     * @param stdin     InputStream containing arguments from Stdin
     * @param fileNames Array of file names to be read and merged (including "-" for reading from stdin)
     * @throws Exception
     */
//    public String mergeFileAndStdin(Boolean isSerial, InputStream stdin, String... fileNames) throws PasteException {
//        if (stdin == null) {
//            throw new PasteException("Stdin cannot be null");
//        }
//        if (fileNames == null || fileNames.length == 0) {
//            throw new PasteException("File names cannot be null or empty");
//        }
//        StringBuilder output = new StringBuilder();
//        List<List<String>> allLines = new ArrayList<>();
//        int maxLines = 0;
//        List<String> stdinLines;
//        try {
//            stdinLines = IOUtils.getLinesFromInputStream(stdin);
//        } catch (IOException e) {
//            throw new PasteException("Error reading from stdin: " + e.getMessage());
//        }
//        try {
//            for (String fileName : fileNames) {
//                List<String> lines;
//                if ("-".equals(fileName)) {
//                    lines = new ArrayList<>(stdinLines);
//                } else {
//                    if (fileName == null) {
//                        continue;
//                    }
//                    try {
//                        InputStream inputStream = IOUtils.openInputStream(fileName);
//                        lines = IOUtils.getLinesFromInputStream(inputStream);
//                        IOUtils.closeInputStream(inputStream);
//                    } catch (Exception e) {
//                        throw new PasteException("Error reading file: " + fileName);
//                    }
//                }
//                maxLines = Math.max(maxLines, lines.size());
//                allLines.add(lines);
//            }
//            if (isSerial) {
//                for (List<String> lines : allLines) {
//                    output.append(String.join("\t", lines)).append(System.lineSeparator());
//                }
//            } else {
//                // Parallel mode concatenation
//                for (int i = 0; i < maxLines; i++) {
//                    for (List<String> lines : allLines) {
//                        if (lines.size() > i) {
//                            output.append(lines.get(i));
//                        }
//                        output.append('\t');
//                    }
//                    output.setLength(output.length() - 1);
//                    output.append(System.lineSeparator());
//                }
//            }
//            int endIndex = output.length();
//            while (endIndex > 0 && (output.charAt(endIndex - 1) == '\n' || output.charAt(endIndex - 1) == '\r' || output.charAt(endIndex - 1) == '\t')) {
//                endIndex--;
//            }
//            output.setLength(endIndex);
//        } catch (Exception e) {
//            throw new PasteException("Error while merging files and stdin: " + e.getMessage());
//        }
//        return output.toString();
//    }
    public String mergeFileAndStdin(Boolean isSerial, InputStream stdin, String... fileNames) throws PasteException {
        if (stdin == null) {
            throw new PasteException("Stdin cannot be null");
        }
        if (fileNames == null || fileNames.length == 0) {
            throw new PasteException("File names cannot be null or empty");
        }
        StringBuilder output = new StringBuilder();
        List<List<String>> allLines = readAllLines(stdin, fileNames);
        concatenateLines(isSerial, allLines, output);
        trimTrailingCharacters(output);
        return output.toString();
    }

    private List<List<String>> readAllLines(InputStream stdin, String... fileNames) throws PasteException {
        List<String> stdinLines = readLinesFromInputStream(stdin);
        List<List<String>> allLines = new ArrayList<>();






        for (String fileName : fileNames) {
            List<String> lines = readLinesFromFile(fileName, stdinLines);
            allLines.add(lines);











        }
        return allLines;
    }

    private List<String> readLinesFromFile(String fileName, List<String> stdinLines) throws PasteException {


















        if ("-".equals(fileName)) {
            return new ArrayList<>(stdinLines);
        } else if (fileName != null) {
            try {
                InputStream inputStream = IOUtils.openInputStream(fileName); //NOPMD - suppressed CloseResource - Already closed below
                List<String> lines = IOUtils.getLinesFromInputStream(inputStream);
                IOUtils.closeInputStream(inputStream);
                return lines;
            } catch (Exception e) {
                throw new PasteException("Error reading file: " + fileName); //NOPMD - suppressed PreserveStackTrace - Following implementation of existing exceptions
            }
        }
        return new ArrayList<>();
    }

    private List<String> readLinesFromInputStream(InputStream stdin) throws PasteException {
        try {
            return IOUtils.getLinesFromInputStream(stdin);
        } catch (IOException e) {
            throw new PasteException("Error reading from stdin: " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Following implementation of existing exceptions
        }
    }

    private void concatenateLines(Boolean isSerial, List<List<String>> allLines, StringBuilder output) {
        if (isSerial) {
            allLines.forEach(lines -> output.append(String.join("\t", lines)).append(System.lineSeparator()));
        } else {
            int maxLines = allLines.stream().mapToInt(List::size).max().orElse(0);
            for (int i = 0; i < maxLines; i++) {
                final int index = i;
                allLines.forEach(lines -> {
                    if (index < lines.size()) {
                        output.append(lines.get(index));
                    }
                    output.append('\t');
                });
                output.setLength(output.length() - 1);


                output.append(System.lineSeparator());
            }
        }
    }

    private void trimTrailingCharacters(StringBuilder output) {
        int endIndex = output.length();
        while (endIndex > 0 && (output.charAt(endIndex - 1) == '\n' || output.charAt(endIndex - 1) == '\r' || output.charAt(endIndex - 1) == '\t')) {
            endIndex--;
        }
        output.setLength(endIndex);
    }


}