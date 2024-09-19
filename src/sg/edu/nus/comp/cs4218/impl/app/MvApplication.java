package sg.edu.nus.comp.cs4218.impl.app;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;

public class MvApplication implements MvInterface {
    private static final String EMPTY_STRING = "";

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        MvArgsParser parser = new MvArgsParser();
        try {
            parser.parse(args);
        } catch (Exception e) {
            throw new MvException(e.getMessage()); // NOPMD - suppressed PreserveStackTrace - Stack trace not preserved
                                                   // as exception is thrown
        }

        if (parser.getNonFlagArgs().isEmpty()) {
            throw new MvException("No arguments provided."); // NOPMD - suppressed PreserveStackTrace - Stack trace not
                                                             // preserved as exception is thrown
        }

        if (!parser.getFlagArgs().isEmpty() && parser.getNonFlagArgs().size() < 2) {
            throw new MvException("Invalid number of arguments. Expected at least two arguments."); // NOPMD -
                                                                                                    // suppressed
                                                                                                    // PreserveStackTrace
                                                                                                    // - Stack trace not
                                                                                                    // preserved as
                                                                                                    // exception is
                                                                                                    // thrown
        }

        try {
            // Determine which function to call based on the presence of a wildcard
            if (parser.hasMultipleSources()) {
                List<String> filesToMove = parser.getFilesToMove();
                String[] filesArray = filesToMove.toArray(new String[0]);
                mvFilesToFolder(parser.isOverwrite(), parser.getDestinationFolder(), filesArray);
            } else {
                mvSrcFileToDestFile(parser.isOverwrite(), parser.getSingleFileToMove(), parser.getDestinationFolder());
            }
        } catch (MvException e) {
            throw new MvException(e.getMessage()); // NOPMD - suppressed PreserveStackTrace - Stack trace not preserved
                                                   // as exception is thrown
        }
    }

    @Override
    public String mvSrcFileToDestFile(Boolean isOverwrite, String srcFile, String destFile)
            throws AbstractApplicationException {
        try {
            Path srcFullPath = IOUtils.resolveFilePath(srcFile);
            Path destFullPath = IOUtils.resolveFilePath(destFile);
            Path destFilePath = destFullPath.resolve(srcFullPath.getFileName());
            if (srcFullPath.equals(destFullPath)) {
                throw new MvException("rename " + destFilePath.getFileName() + " to " + destFilePath.getFileName()
                        + ": Invalid argument");
            }

            if (Files.isDirectory(destFullPath)) {
                if (Files.exists(destFilePath) && !isOverwrite) {
                    throw new MvException("Destination file already exists and overwrite is not allowed.");
                }
                Files.move(srcFullPath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                if (!Files.exists(destFullPath)) { // NOPMD - suppressed ConfusingTenarary - Ternary operator is not
                                                   // confusing
                    try {
                        Files.move(srcFullPath, destFullPath);
                    } catch (IOException e) {
                        throw new MvException(e.getMessage()); // NOPMD - suppressed PreserveStackTrace - Stack trace
                                                               // not preserved as exception is thrown
                    }
                } else {

                    boolean destOwnerWritable = Files.isWritable(destFilePath);
                    if (!isOverwrite) { // NOPMD - suppressed ConfusingTenarary - Ternary operator
                                        // is not confusing
                        Files.move(srcFullPath, destFullPath, StandardCopyOption.REPLACE_EXISTING);
                        return EMPTY_STRING;
                    } else if (!destOwnerWritable) {
                        throw new MvException(String.format(ERR_NO_PERM, destFullPath.toString()));
                    }
                    if (!isOverwrite && Files.exists(destFullPath)) {

                        return EMPTY_STRING;
                    } else {

                        Files.move(srcFullPath, destFullPath);
                        return EMPTY_STRING;
                    }
                }
            }
        } catch (IOException e) {
            throw new MvException(e.getMessage()); // NOPMD - suppressed PreserveStackTrace - Stack trace not preserved
                                                   // as exception is thrown
        }
        return EMPTY_STRING;
    }

    @Override
    public String mvFilesToFolder(Boolean isOverwrite, String destFolder, String... fileNames)
            throws AbstractApplicationException {
        Path dstFullPath = IOUtils.resolveFilePath(destFolder);
        File destinationFolder = new File(dstFullPath.toString());
        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            throw new MvException("Destination folder does not exist: " + destFolder); // NOPMD - suppressed
                                                                                       // PreserveStackTrace - Stack
                                                                                       // trace not preserved as
                                                                                       // exception is thrown
        }

        for (String fileName : fileNames) {
            Path srcFullPath = IOUtils.resolveFilePath(fileName);
            File sourceFile = new File(srcFullPath.toString());
            File destinationFile = new File(destFolder, sourceFile.getName());
            Path destFullPath = IOUtils.resolveFilePath(destinationFile.toString());

            if (!sourceFile.exists()) {
                throw new MvException("Source file does not exist: " + fileName); // NOPMD - suppressed
                                                                                  // PreserveStackTrace - Stack trace
                                                                                  // not preserved as exception is
                                                                                  // thrown
            }
            if (Files.exists(destFullPath) && isOverwrite) {
                continue;
            }

            try {
                Files.move(srcFullPath, dstFullPath.resolve(sourceFile.getName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new MvException(e.getMessage()); // NOPMD - suppressed PreserveStackTrace - Stack trace not
                                                       // preserved as exception is thrown
            }
        }

        return EMPTY_STRING;
    }
}
