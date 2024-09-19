package sg.edu.nus.comp.cs4218.impl.app;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MISSING_ARG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MISSING_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

public class RmApplication implements RmInterface {
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        RmArgsParser parser = new RmArgsParser();
        if (args == null || args.length == 0 || Arrays.asList(args).contains(null)) {
            throw new RmException(ERR_NULL_ARGS);
        }

        boolean recursive, removeEmptyDirs;

        try {
            parser.parse(args);

        } catch (Exception e) {
            throw new RmException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }
        if (parser.getFileNames().length == 0) {
            throw new RmException(ERR_NULL_ARGS); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }


        recursive = parser.isRecursive();
        removeEmptyDirs = parser.isEmptyDirectory();

        try {
            remove(removeEmptyDirs, recursive, parser.getFileNames());
        } catch (RmException e) {
            throw new RmException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * Runs the rm application.
     *
     * @param isEmptyFolder Boplean indicating if the folder is empty.
     * @param isRecursive   Boolean indicating if the operation is recursive.
     * @param fileNames     Array of file names to be removed.
     * @throws RmException If an error occurs during the removal process such as :
     *                     - File not found
     *                     - Directory not empty
     *                     - Directory not found
     *                     - File is a directory
     *                     - File is a directory and recursive is not set
     *                     - File is a directory and empty folder is not set
     */
    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileNames) //NOPMD - suppressed MethodLength - Method length is fine
            throws AbstractApplicationException {
        List<File> validFiles = new ArrayList<>();
        List<String> invalidFiles = new ArrayList<>();

        // First, iterate over all files to check their existence
        for (String fileName : fileNames) {
            Path srcFullPath = IOUtils.resolveFilePath(fileName);
            File file = new File(srcFullPath.toString());
            if (file.exists()) {
                validFiles.add(file); // Add valid files to the list
            } else {
                invalidFiles.add(fileName); // Add invalid file names to the list
            }
        }

        // Now, iterate over the valid files and perform the deletion operation
        for (File file : validFiles) {
            try {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files == null || files.length == 0) {
                        if (!isEmptyFolder) { //NOPMD - suppressed ConfusingTenarary - Ternary operator is not confusing
                            if (isRecursive) {
                                deleteDirectory(file);
                            } else {
                                throw new RmException("Empty Flag not given");
                            }
                        } else {
                            if (!file.delete()) {
                                throw new RmException("Failed to delete directory: " + file.getAbsolutePath());
                            }
                        }
                    } else {
                        if (isRecursive) {
                            deleteDirectory(file);
                        } else if (isEmptyFolder) {
                            // Check if the folder is empty, if so, remove it
                            if (files.length == 0) {
                                if (!file.delete()) {
                                    throw new RmException("Failed to delete directory: " + file.getAbsolutePath());
                                }
                            } else {
                                throw new RmException("Directory not empty: " + file.getAbsolutePath());
                            }
                        } else {
                            throw new RmException(
                                    "Directory not empty (use -r option to remove recursively): "
                                            + file.getAbsolutePath());
                        }
                    }
                } else {
                    if (!file.delete()) {
                        throw new RmException("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            } catch (RmException e) {
                throw new RmException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
            }
        }
        // If there are invalid files, throw an exception
        if (!invalidFiles.isEmpty()) {
            throw new RmException("Files not found: " + String.join(", ", invalidFiles));
        }
    }

}
