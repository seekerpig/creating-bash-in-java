package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

/**
 * CatArgsParser extends ArgsParser to specifically handle parsing of arguments for the cat command.
 */
public class CatArgsParser extends ArgsParser {

    private static final char CHAR_N = 'n';

    /**
     * Constructor for CatArgsParser.
     */
    public CatArgsParser() {
        super();
        this.legalFlags.add(CHAR_N);
    }

    /**
     * Retrieves the list of file names passed.
     *
     * @return List of file names
     */
    public List<String> getFiles() {
        return nonFlagArgs;
    }

    /**
     * Checks if the -n flag is present among the parsed flags.
     *
     * @return true if the -n flag is present, false otherwise.
     */
    public boolean isNumberLines() {
        return this.flags.contains(CHAR_N);
    }
}
