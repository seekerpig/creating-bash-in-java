package sg.edu.nus.comp.cs4218.impl.util;

public class WcResult { //NOPMD - Required to construct result
    private long lines;
    private long words;
    private long bytes;


    public WcResult() {
        /**
         * Used to initialize the result of wc command
         */
    }

    // Getters and setters
    public long getLines() {
        return lines;
    }

    public void setLines(long lines) {
        this.lines = lines;
    }

    public long getWords() {
        return words;
    }

    public void setWords(long words) {
        this.words = words;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }
    @Override
    public String toString() {
        return String.format("Lines: %d, Words: %d, Bytes: %d", lines, words, bytes);
    }

}
