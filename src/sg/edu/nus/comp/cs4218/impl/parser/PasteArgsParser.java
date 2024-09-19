package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.List;

/**
 * PasteParser class is responsible for parsing arguments input to the Paste application.
 */
public class PasteArgsParser extends ArgsParser {

    private static final char SERIAL_FLAG = 's';

    public PasteArgsParser() {
        super();
        legalFlags.add(SERIAL_FLAG); // The only legal flag for paste is 's'
    }

    /**
     * Checks if the serial flag is present in the parsed arguments.
     *
     * @return true if the serial flag is present, false otherwise.
     */
    public boolean isSerial() {
        return flags.contains(SERIAL_FLAG);
    }

    /**
     * Retrieves the list of non-flag arguments, which should correspond to file names.
     *
     * @return A list of strings representing file names.
     */
    public List<String> getFiles() {
        return nonFlagArgs;
    }

    @Override
    public void parse(String... args) throws InvalidArgsException {
        super.parse(args); // Call to superclass method to populate flags and nonFlagArgs
    }
}
