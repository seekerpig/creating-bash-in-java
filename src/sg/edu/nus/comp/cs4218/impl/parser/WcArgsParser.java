package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class WcArgsParser extends ArgsParser {
    public static final char IS_BYTE_COUNT = 'c';

    public static final char IS_LINE_COUNT = 'l';

    public static final char IS_WORD_COUNT = 'w';

    public WcArgsParser() {
        super();

        legalFlags.add(IS_BYTE_COUNT);
        legalFlags.add(IS_LINE_COUNT);
        legalFlags.add(IS_WORD_COUNT);
    }

    public Boolean isByteCount() {
        return flags.isEmpty() || flags.contains(IS_BYTE_COUNT);
    }

    public Boolean isLineCount() {
        return flags.isEmpty() || flags.contains(IS_LINE_COUNT);
    }

    public Boolean isWordCount() {
        return flags.isEmpty() || flags.contains(IS_WORD_COUNT);
    }

    public List<String> getFileNames() {
        return nonFlagArgs;
    }
}
