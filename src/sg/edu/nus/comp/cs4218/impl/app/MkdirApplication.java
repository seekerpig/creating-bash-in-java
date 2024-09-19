package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.MkdirInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.MkdirException;
import sg.edu.nus.comp.cs4218.impl.parser.MkdirArgsParser;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

// TODO: adding of directory with same name should/should not throw exception

public class MkdirApplication implements MkdirInterface {
    private boolean createParent;

    /**
     * Runs the mkdir application with the specified arguments.
     *
     * @param args   Array of arguments for the application.
     * @param stdin  An InputStream.
     * @param stdout An OutputStream.
     *
     * @throws AbstractApplicationException If invalid input is encountered.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (args == null) {
            throw new MkdirException(ERR_NULL_ARGS);
        }
        if (args.length == 0) {
            throw new MkdirException(ERR_NO_ARGS);
        }

        MkdirArgsParser parser = new MkdirArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new MkdirException(e.getMessage()); //NOPMD - suppressed PreserveStackTrace - Stack trace not preserved as exception is thrown
        }

        this.createParent = parser.isCreateParent();
        createFolder(parser.getDirectories().toArray(new String[0]));
    }

    public void createFolder(String... folderNames) throws AbstractApplicationException {
        if (folderNames == null) {
            throw new MkdirException(ERR_NULL_ARGS);
        }
        if (folderNames.length == 0) {
            throw new MkdirException(ERR_NO_ARGS);
        }
        MkdirException err = null;
        for (String folderName : folderNames) {
            if (folderName == null) {
                throw new MkdirException(ERR_NULL_ARGS);
            }
            if (folderName.isEmpty()) {
                throw new MkdirException(ERR_NO_ARGS);
            }

            String cwd = Environment.currentDirectory;
            File folder = new File(cwd, folderName);
            if (folder.exists() && err == null) {
                err = new MkdirException(ERR_FOLDER_EXISTS);
            }
            boolean folderCreated;
            if (createParent) {
                folderCreated = folder.mkdirs();
            } else {
                folderCreated = folder.mkdir();
            }

            if (folderCreated) {
//                System.out.println("Folder created successfully.");
            } else if (err == null) {
                err = new MkdirException(ERR_CREATE_FOLDER);
            }
        }
        if (err != null) {
            throw err;
        }
    }

}
