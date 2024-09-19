package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class RmArgsParser extends ArgsParser {
    private final static char CHAR_RECURSIVE = 'r';
    private final static char CHAR_EMPTY_DIR = 'd';

    public RmArgsParser() {
        super();
        legalFlags.add(CHAR_RECURSIVE);
        legalFlags.add(CHAR_EMPTY_DIR);
    }

    public Boolean isRecursive() {
        return flags.contains(CHAR_RECURSIVE);
    }

    public Boolean isEmptyDirectory() {
        return flags.contains(CHAR_EMPTY_DIR);
    }

    public String[] getFileNames() {
        return nonFlagArgs.toArray(new String[0]);
    }

    public String[] getFlags() {
        return flags.toArray(new String[0]);
    }

    public List<String> getNonFlagArgs() {
        return nonFlagArgs;
    }
}
