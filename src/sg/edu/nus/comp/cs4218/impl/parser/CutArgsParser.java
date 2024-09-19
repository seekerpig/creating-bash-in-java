package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CutArgsParser extends ArgsParser {

    private List<String> cutList; // To store the list of positions or ranges

    public CutArgsParser() {
        super();
        // Define legal flags for the cut command
        legalFlags.add('c');
        legalFlags.add('b');
        cutList = new ArrayList<>();
    }

    @Override
    public void parse(String... args) throws InvalidArgsException {
        super.parse(args);
        // After separating flags and non-flag arguments, parse the list
        for (String arg : nonFlagArgs) {
            if (arg.matches("\\d+(-\\d+)?(,\\d+(-\\d+)?)*")) { // Matches single numbers, ranges, or comma-separated lists thereof
                cutList = Arrays.asList(arg.split(","));
                nonFlagArgs.remove(arg);
                break; // Assumes the first valid list found is the intended cut list
            }
        }
        if (cutList.isEmpty()) {
            throw new InvalidArgsException("No valid list of positions provided.");
        }
    }

    public boolean isCutByCharacter() {
        return flags.contains('c');
    }

    public boolean isCutByByte() {
        return flags.contains('b');
    }

    public List<String> getCutList() {
        return cutList;
    }

    public List<String> getFiles() {
        return nonFlagArgs;
    }
}
