package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;


// bug:
// ls * doesnt work on files

public class LsApplication implements LsInterface {

    private final static String PATH_CURR_DIR = STRING_CURR_DIR + CHAR_FILE_SEP;

    @Override
    public String listFolderContent(Boolean isRecursive, Boolean isSortByExt,
                                    String... folderName) throws AbstractApplicationException {
        if (folderName.length == 0 && !isRecursive) { // no args no recursive, just list cwd
            return listCwdContent(isSortByExt);
        }

        List<Path> paths;
        if (folderName.length == 0 && isRecursive) { // no args but recursive, list cwd and all children
            String[] directories = new String[1];
//            directories[0] = Environment.currentDirectory;
//            paths = resolvePaths(directories);
            paths = new ArrayList<>();
            paths.add(Paths.get(Environment.currentDirectory));
        } else {
            paths = resolvePaths(folderName);
        }

        return buildResult(paths, isRecursive, isSortByExt);
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException {
        if (args == null) {
            throw new LsException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new LsException(ERR_NO_OSTREAM);
        }

        LsArgsParser parser = new LsArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new LsException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }

        Boolean recursive = parser.isRecursive();
        Boolean sortByExt = parser.isSortByExt();
        String[] directories = parser.getDirectories()
                .toArray(new String[parser.getDirectories().size()]);
        String result = listFolderContent(recursive, sortByExt, directories);

        try {
            stdout.write(result.getBytes());
            stdout.write(StringUtils.STRING_NEWLINE.getBytes());
        } catch (Exception e) {
            throw new LsException(ERR_WRITE_STREAM); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }
    }

    /**
     * Lists only the current directory's content and RETURNS. This does not account for recursive
     * mode in cwd.
     *
     * @param isSortByExt
     * @return
     */
    private String listCwdContent(Boolean isSortByExt) throws AbstractApplicationException {
        String cwd = Environment.currentDirectory;
        try {
            return formatContents(getContents(Paths.get(cwd)), isSortByExt);
        } catch (InvalidDirectoryException e) {
            throw new LsException("Unexpected error occurred!"); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }
    }

    /**
     * Builds the resulting string to be written into the output stream.
     * <p>
     * NOTE: This is recursively called if user wants recursive mode.
     *
     * @param paths       - list of java.nio.Path objects to list
     * @param isRecursive - recursive mode, repeatedly ls the child directories
     * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ‘.’ (without quotes)). Files with no extension are sorted first.
     * @return String to be written to output stream.
     */
    private String buildResult(List<Path> paths, Boolean isRecursive, Boolean isSortByExt) { //NOPMD - suppressed ExcessiveMethodLength - Method needs to be long to abide by the method requirements
        StringBuilder result = new StringBuilder();
        for (Path path : paths) {
            // if file exists and file not directory, we append to result
            if (Files.exists(path) && !Files.isDirectory(path)) {
                result.append(path.getFileName().toString());
                result.append(StringUtils.STRING_NEWLINE);
            } else {
                try {
                    // chk directory
                    List<Path> contents = getContents(path);
                    String formatted = formatContents(contents, isSortByExt);
                    String relativePath = getRelativeToCwd(path).toString();
                    result.append(StringUtils.isBlank(relativePath) ? "" : relativePath + ":" + StringUtils.STRING_NEWLINE);
//                    result.append(StringUtils.STRING_NEWLINE);
                    result.append(formatted);

                    if (!formatted.isEmpty()) {
                        // Empty directories should not have an additional new line
                        result.append(StringUtils.STRING_NEWLINE);
                    }
                    result.append(StringUtils.STRING_NEWLINE);

                    // RECURSE!
                    if (isRecursive) {
                        // filter for directories in contents
                        contents.removeIf(p -> !Files.isDirectory(p));
                        result.append(buildResult(contents, isRecursive, isSortByExt));
                        // if buildResult returns empty, we don't want to append an additional new line
                        if (!contents.isEmpty()) {
                            result.append(StringUtils.STRING_NEWLINE + StringUtils.STRING_NEWLINE);
                        }
                    }
                } catch (InvalidDirectoryException e) {
                    // NOTE: This is pretty hackish IMO - we should find a way to change this
                    // If the user is in recursive mode, and if we resolve a file that isn't a directory
                    // we should not spew the error message.
                    //
                    // However the user might have written a command like `ls invalid1 valid1 -R`, what
                    // do we do then?
                    if (!isRecursive) {
                        result.append(e.getMessage());
                        result.append(StringUtils.STRING_NEWLINE);
                    }
                }
            }


        }

        return result.toString().trim();
    }

    /**
     * Formats the contents of a directory into a single string.
     *
     * @param contents    - list of items in a directory
     * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ‘.’ (without quotes)). Files with no extension are sorted first.
     * @return
     */
    private String formatContents(List<Path> contents, Boolean isSortByExt) {
        List<String> fileNames = new ArrayList<>();
        for (Path path : contents) {
            fileNames.add(path.getFileName().toString());
        }

        if (isSortByExt) {
            Collections.sort(fileNames, (a, b) -> {
                String extA = a.contains(".") ? a.substring(a.lastIndexOf(".") + 1) : "";
                String extB = b.contains(".") ? b.substring(b.lastIndexOf(".") + 1) : "";
                if (extA.equals(extB)) { // no or same extension
                    return a.compareTo(b);
                }
                return extA.compareTo(extB);
            });
        }

        StringBuilder result = new StringBuilder();
        for (String fileName : fileNames) {
            result.append(fileName);
            result.append(StringUtils.STRING_NEWLINE);
        }

        return result.toString().trim();
    }

    /**
     * Gets the contents in a single specified directory.
     *
     * @param directory
     * @return List of files + directories in the passed directory.
     */
    private List<Path> getContents(Path directory)
            throws InvalidDirectoryException {
        if (!Files.exists(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        if (!Files.isDirectory(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        List<Path> result = new ArrayList<>();
        File pwd = directory.toFile();
        for (File f : pwd.listFiles()) {
            if (!f.isHidden()) {
                result.add(f.toPath());
            }
        }

        Collections.sort(result);

        return result;
    }

    /**
     * Resolve all paths given as arguments into a list of Path objects for easy path management.
     *
     * @param directories
     * @return List of java.nio.Path objects
     */
    private List<Path> resolvePaths(String... directories) {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < directories.length; i++) {
            paths.add(resolvePath(directories[i]));
        }

        return paths;
    }

    /**
     * Converts a String into a java.nio.Path objects. Also resolves if the current path provided
     * is an absolute path.
     *
     * @param directory
     * @return
     */
    private Path resolvePath(String directory) {
        Path path = Paths.get(directory);

        boolean isAbsolute = path.isAbsolute();

        if (directory.charAt(0) == '/' || isAbsolute) {
            // This is an absolute path
            return Paths.get(directory).normalize();
        }

        return Paths.get(Environment.currentDirectory, directory).normalize();
    }

    /**
     * Converts a path to a relative path to the current directory.
     *
     * @param path
     * @return
     */
    private Path getRelativeToCwd(Path path) {
        return Paths.get(Environment.currentDirectory).relativize(path);
    }

    private class InvalidDirectoryException extends Exception {
        InvalidDirectoryException(String directory) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory));
        }

        InvalidDirectoryException(String directory, Throwable cause) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory),
                    cause);
        }
    }
}
