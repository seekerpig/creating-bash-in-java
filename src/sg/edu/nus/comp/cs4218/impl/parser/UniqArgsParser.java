package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class UniqArgsParser extends ArgsParser {
    public static final char FLAG_IS_COUNT = 'c';
    public static final char FLAG_IS_DUPLICATE = 'd';
    public static final char FLAG_IS_ALL_DUPLICATE = 'D'; //NOPMD - suppressed LongVariable - any further reduction would diminish the meaning of the variable. Current definition is clear and concise
    private final static int INDEX_INPUT_FILE = 0;
    private final static int INDEX_OUTPUT_FILE = 1;

    public UniqArgsParser() {
        super();
        legalFlags.add(FLAG_IS_COUNT);
        legalFlags.add(FLAG_IS_DUPLICATE);
        legalFlags.add(FLAG_IS_ALL_DUPLICATE);
    }

    public Boolean isCountFlag() {
        return flags.contains(FLAG_IS_COUNT);
    }

    public Boolean isDuplicateFlag() {
        return flags.contains(FLAG_IS_DUPLICATE);
    }

    public boolean isAllDuplicateFlag() {
        return flags.contains(FLAG_IS_ALL_DUPLICATE);
    }

    public List<String> getFileNames() {
        return nonFlagArgs;
    }

    public String getInputFileName() {
        if (nonFlagArgs.size() > 0) {
            return nonFlagArgs.get(INDEX_INPUT_FILE);
        }
        return null;
    }

    public String getOutputFileName() {
        if (nonFlagArgs.size() > 1) {
            return nonFlagArgs.get(INDEX_OUTPUT_FILE);
        }
        return null;
    }
}
