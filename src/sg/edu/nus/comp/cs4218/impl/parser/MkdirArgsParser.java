package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class MkdirArgsParser extends ArgsParser {
    private final static char FLAG_CREATE_PARENT = 'p';  //NOPMD - suppressed LongVariable - Variable Name is More Readable

    public MkdirArgsParser() {
        super();
        legalFlags.add(FLAG_CREATE_PARENT);
    }

    public Boolean isCreateParent() {
        return flags.contains(FLAG_CREATE_PARENT);
    }

    public List<String> getDirectories() {
        return nonFlagArgs;
    }
}