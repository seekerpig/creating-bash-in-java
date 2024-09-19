package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class TeeArgsParser extends ArgsParser {
    private static final char LEGAL_FLAG = 'a';

    public TeeArgsParser() {
        super();
        legalFlags.add(LEGAL_FLAG);
    }

    /**
     * Checks if the '-a' flag is set, indicating that the tee command should append
     * the input to the given file(s) rather than overwriting them.
     *
     * @return true if the '-a' flag is present, false otherwise.
     */
    public boolean isAppend() {
        return flags.contains(LEGAL_FLAG);
    }

    /**
     * Retrieves the non-flag arguments.
     *
     * @return List of non-flag arguments (file names).
     */
    public List<String> getFiles() {
        return nonFlagArgs;
    }
}