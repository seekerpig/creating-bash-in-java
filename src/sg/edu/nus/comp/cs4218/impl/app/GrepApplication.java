package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplication implements GrepInterface { //NOPMD - suppressed GodClass - GrepApplication is a god class as it has many functionalities
    public static final String INVALID_PATTERN = "Invalid pattern syntax";
    public static final String EMPTY_PATTERN = "Pattern should not be empty.";
    public static final String IS_DIRECTORY = "Is a directory";
    public static final String NULL_POINTER = "Null Pointer Exception";
    public static final String STD_IN = "(standard input):";

    private static final int NUM_ARGUMENTS = 3;
    private static final char CASE_INSEN_IDENT = 'i';
    private static final char COUNT_IDENT = 'c';
    private static final char PREFIX_FN = 'H';
    private static final int CASE_INSEN_IDX = 0;
    private static final int COUNT_INDEX = 1;
    private static final int PREFIX_FN_IDX = 2;

    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, String... fileNames) throws AbstractApplicationException {
        if (fileNames == null || pattern == null) {
            throw new GrepException(NULL_POINTER);
        }
        if (pattern.isEmpty()) {
            throw new GrepException(EMPTY_PATTERN);
        }
        if (fileNames.length == 0) {
            throw new GrepException(ERR_NO_INPUT);
        }

        StringJoiner lineResults = new StringJoiner(STRING_NEWLINE);
        StringJoiner countResults = new StringJoiner(STRING_NEWLINE);

        grepResultsFromFiles(pattern, isCaseInsensitive, isPrefixFileName, lineResults, countResults, fileNames);

        String results = "";
        if (isCountLines) {
            results = countResults.toString() + STRING_NEWLINE;
        } else {
            if (!lineResults.toString().isEmpty()) {
                results = lineResults.toString() + STRING_NEWLINE;
            }
        }
        return results;
    }

    /**
     * Extract the lines and count number of lines for grep from files and insert them into
     * lineResults and countResults respectively.
     *
     * @param pattern           supplied by user
     * @param isCaseInsensitive supplied by user
     * @param lineResults       a StringJoiner of the grep line results
     * @param countResults      a StringJoiner of the grep line count results
     * @param fileNames         a String Array of file names supplied by user
     */
    private void grepResultsFromFiles(String pattern, Boolean isCaseInsensitive, //NOPMD - suppressed ExcessiveMethodLength - Method needs to be long to abide by the method requirements
                                      Boolean isPrefixFileName, StringJoiner lineResults, StringJoiner countResults, String... fileNames) throws AbstractApplicationException {
        int count;
        boolean isSingleFile = (fileNames.length == 1);
        for (String f : fileNames) {
            BufferedReader reader = null;
            try {
                String path = convertToAbsolutePath(f);
                File file = new File(path);
                if (!file.exists()) {
                    lineResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    countResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    continue;
                }
                if (file.isDirectory()) { // ignore if it's a directory
                    lineResults.add(f + ": " + IS_DIRECTORY);
                    countResults.add(f + ": " + IS_DIRECTORY);
                    continue;
                }
                reader = new BufferedReader(new FileReader(path));
                String line;
                Pattern compiledPattern;
                if (isCaseInsensitive) {
                    compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                } else {
                    compiledPattern = Pattern.compile(pattern);
                }
                count = 0;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = compiledPattern.matcher(line);
                    if (matcher.find()) { // match
                        if (isSingleFile && !isPrefixFileName) {
                            lineResults.add(line);
                        } else {
                            lineResults.add(f + ":" + line);
                        }
                        count++;
                    }
                }
                if (isSingleFile && !isPrefixFileName) {
                    countResults.add("" + count);
                } else {
                    countResults.add(f + ":" + count);
                }
                reader.close();
            } catch (PatternSyntaxException pse) {
                throw new IllegalArgumentException(pse);
            } catch (FileNotFoundException e) {
                throw new GrepException(ERR_FILE_NOT_FOUND); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
            } catch (IOException e) {
                throw new GrepException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new GrepException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
                    }
                }
            }
        }
    }

    @Override
    public String grepFromFileAndStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin, String... fileNames) throws AbstractApplicationException {
        if (fileNames == null || pattern == null) {
            throw new GrepException(NULL_POINTER);
        }
        if (pattern.isEmpty()) {
            throw new GrepException(EMPTY_PATTERN);
        }
        if (stdin == null) {
            throw new GrepException(ERR_NO_INPUT);
        }

        boolean isSingleFile = (fileNames.length == 1);
        StringBuilder stringBuilder = new StringBuilder();
        for (String f : fileNames) {
            String result;
            if ("-".equals(f)) {
                result = grepFromStdin(pattern, isCaseInsensitive, isCountLines, isPrefixFileName, stdin);
            } else {
                result = grepFromFiles(pattern, isCaseInsensitive, isCountLines, isPrefixFileName, f);
            }
            if (isSingleFile || isPrefixFileName) {
                stringBuilder.append(result);
            } else {
                // no filename from above, add file name if multiple files
                // append file name to each line
                String prefix = "-".equals(f) ? STD_IN : f + ":";
                List<String> lines = Arrays.stream(result.split(STRING_NEWLINE)).map(line -> prefix + line).collect(Collectors.toList());
                stringBuilder.append(String.join(STRING_NEWLINE, lines));
            }
            // if last line no newline, add newline
            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != STRING_NEWLINE.charAt(0)) {
                stringBuilder.append(STRING_NEWLINE);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Converts filename to absolute path, if initially was relative path
     *
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    private String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = Environment.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    /**
     * Converts path provided by user into path recognised by the system
     *
     * @param path supplied by user
     * @return a String of the converted path
     */
    private String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }

    @Override
    public String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin) throws AbstractApplicationException {  //NOPMD - suppressed ExcessiveMethodLength - Method needs to be long to abide by the method requirements
        if (pattern == null) {
            throw new GrepException(NULL_POINTER);
        }
        if (pattern.isEmpty()) {
            throw new GrepException(EMPTY_PATTERN);
        }
        if (stdin == null) {
            throw new GrepException(ERR_NO_INPUT);
        }

        int count = 0;
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            String line;
            Pattern compiledPattern;
            if (isCaseInsensitive) {
                compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            } else {
                compiledPattern = Pattern.compile(pattern);
            }
            while ((line = reader.readLine()) != null) {
                Matcher matcher = compiledPattern.matcher(line);
                if (matcher.find()) { // match
                    // add 'standard input' prefix if -H flag is set
                    if (isPrefixFileName) {
                        stringJoiner.add(STD_IN + line);
                    } else {
                        stringJoiner.add(line);
                    }
                    count++;
                }
            }
            reader.close();
        } catch (PatternSyntaxException pse) {
            throw (GrepException)new GrepException(INVALID_PATTERN).initCause(pse);
        } catch (NullPointerException npe) {
            throw new GrepException(ERR_FILE_NOT_FOUND); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        } catch (IOException e) {
            throw new GrepException(ERR_IO_EXCEPTION); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }

        String results = "";
        if (isCountLines) {
            if (isPrefixFileName) {
                results = STD_IN + count + STRING_NEWLINE;
            } else {
                results = count + STRING_NEWLINE;
            }
        } else {
            if (!stringJoiner.toString().isEmpty()) {
                results = stringJoiner.toString() + STRING_NEWLINE;
            }
        }
        return results;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            boolean[] grepFlags = new boolean[NUM_ARGUMENTS];
            ArrayList<String> inputFiles = new ArrayList<>();
            String pattern = getGrepArguments(args, grepFlags, inputFiles);
            String result = "";

            if (stdin == null && inputFiles.isEmpty()) {
                throw new Exception(ERR_NO_INPUT);
            }
            if (pattern == null) {
                throw new Exception(ERR_SYNTAX);
            }

            if (pattern.isEmpty()) {
                throw new Exception(EMPTY_PATTERN);
            } else {
                if (inputFiles.isEmpty()) {
                    result = grepFromStdin(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], grepFlags[PREFIX_FN_IDX], stdin);
                } else {
                    String[] inputFilesArray = new String[inputFiles.size()];
                    inputFilesArray = inputFiles.toArray(inputFilesArray);

                    if (inputFiles.contains("-")) {
                        result = grepFromFileAndStdin(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], grepFlags[PREFIX_FN_IDX], stdin, inputFilesArray);
                    } else {
                        result = grepFromFiles(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], grepFlags[PREFIX_FN_IDX], inputFilesArray);
                    }
                }
            }
            // if last line no newline, add newline
            if (!result.isEmpty() && !result.endsWith(STRING_NEWLINE)) {
                result += STRING_NEWLINE;
            }
            stdout.write(result.getBytes());
        } catch (GrepException grepException) {
            throw grepException;
        } catch (Exception e) {
            throw new GrepException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }
    }

    /**
     * Separates the arguments provided by user into the flags, pattern and input files.
     *
     * @param args       supplied by user
     * @param grepFlags  a bool array of possible flags in grep
     * @param inputFiles a ArrayList<String> of file names supplied by user
     * @return regex pattern supplied by user. An empty String if not supplied.
     */
    private String getGrepArguments(String[] args, boolean[] grepFlags, ArrayList<String> inputFiles) throws AbstractApplicationException {
        String pattern = null;
        boolean isFile = false; // files can only appear after pattern

        for (String s : args) {
            char[] arg = s.toCharArray();
            if (isFile) {
                inputFiles.add(s);
            } else {
                if (!s.isEmpty() && arg[0] == CHAR_FLAG_PREFIX) {
                    arg = Arrays.copyOfRange(arg, 1, arg.length);
                    for (char c : arg) {
                        switch (c) {
                            case CASE_INSEN_IDENT:
                                grepFlags[CASE_INSEN_IDX] = true;
                                break;
                            case COUNT_IDENT:
                                grepFlags[COUNT_INDEX] = true;
                                break;
                            case PREFIX_FN:
                                grepFlags[PREFIX_FN_IDX] = true;
                                break;
                            default:
                                throw new GrepException(ERR_SYNTAX);
                        }
                    }
                } else { // pattern must come before file names
                    pattern = s;
                    isFile = true; // next arg onwards will be file
                }
            }
        }
        return pattern;
    }
}
