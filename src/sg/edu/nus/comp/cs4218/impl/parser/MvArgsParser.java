package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;
import java.util.Set;

public class MvArgsParser extends ArgsParser {
    private final static char NOT_OVERWRITE = 'n';

    public MvArgsParser() {
        super();
        legalFlags.add(NOT_OVERWRITE);
    }

    public String getDestinationFolder() {
        return nonFlagArgs.get(nonFlagArgs.size() - 1);
    }
    public List<String> getNonFlagArgs() {
        return nonFlagArgs;
    }
    public Set<Character> getFlagArgs() {
        return flags;
    }

    public boolean hasMultipleSources() {
        return nonFlagArgs.size() > 2;
    }

    public List<String> getFilesToMove() {
        return nonFlagArgs.subList(0, nonFlagArgs.size() - 1);
    }

    public String getSingleFileToMove() {
        return nonFlagArgs.get(0);
    }

    public boolean isOverwrite() {
        return !legalFlags.contains(NOT_OVERWRITE);
    }

}
