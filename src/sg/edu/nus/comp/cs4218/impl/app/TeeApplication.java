package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.TeeInterface;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.parser.TeeArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TeeApplication implements TeeInterface {

    /**
     * Executes the Tee command with the given arguments, input stream, and output stream.
     *
     * @param args   The command arguments.
     * @param stdin  The input stream.
     * @param stdout The output stream.
     * @throws TeeException If there is an error executing the Tee command.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws TeeException {
        if (stdin == null) {
            throw new TeeException(ErrorConstants.ERR_NO_ISTREAM);
        }
        if (stdout == null) {
            throw new TeeException(ErrorConstants.ERR_NO_OSTREAM);
        }

        TeeArgsParser parser = new TeeArgsParser();
        try {
            parser.parse(args);
        } catch (Exception e) {
            throw new TeeException("Error parsing arguments: " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Following implementation of existing exceptions
        }

        String result;

        boolean isAppend = parser.isAppend();
        String[] fileNames = parser.getFiles().toArray(new String[0]);

        try {
            result = teeFromStdin(isAppend, stdin, fileNames) + System.lineSeparator();
            stdout.write(result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TeeException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Following implementation of existing exceptions
        }
    }

    /**
     * Reads from standard input and write to both the standard output and files
     *
     * @param isAppend Boolean option to append the standard input to the contents of the input files
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of String of file names
     * @return
     * @throws Exception
     */
    @Override
    public String teeFromStdin(Boolean isAppend, InputStream stdin, String... fileName) throws TeeException {
        if (stdin == null) {
            throw new TeeException(ErrorConstants.ERR_NO_ISTREAM);
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stdin))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }

            String lineSeparator = System.lineSeparator();
            output.delete(output.length() - lineSeparator.length(), output.length());
            writeToFiles(output.toString(), isAppend, fileName);
        } catch (IOException e) {
            throw new TeeException("Failed to read from stdin or write to file: " + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Following implementation of existing exceptions
        }
        return output.toString();
    }

    /**
     * Writes the given content to the specified files.
     *
     * @param content   The content to be written to the files.
     * @param isAppend  Specifies whether the content should be appended to the existing file content.
     * @param fileNames The names of the files to write the content to.
     * @throws IOException If an I/O error occurs while writing to the files.
     */
    private void writeToFiles(String content, Boolean isAppend, String... fileNames) throws IOException {
        for (String fileName : fileNames) {
            File file = new File(fileName);

            if (!file.isAbsolute()) {
                file = new File(Environment.currentDirectory, fileName);
            }

            boolean shouldPrependLine = false;

            if (isAppend && file.exists() && file.length() > 0) {
                shouldPrependLine = !endsWithNewLine(file);
            }

            try (FileWriter fileWriter = new FileWriter(file, isAppend)) {
                if (shouldPrependLine) {
                    fileWriter.write(System.lineSeparator());
                }
                fileWriter.write(content);
            }
        }
    }

    /**
     * Checks if the file ends with a newline character.
     *
     * @param file The file to check.
     * @return true if the file ends with a newline, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    private boolean endsWithNewLine(File file) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            long fileLength = file.length() - 1;
            if (fileLength < 0) {
                return false; // File is empty
            }

            randomAccessFile.seek(fileLength); // Go to the end of the file
            int readByte = randomAccessFile.readByte();
            return readByte == '\n' || readByte == '\r'; // Check if the last byte is a newline character
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }
}