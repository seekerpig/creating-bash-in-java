package sg.edu.nus.comp.cs4218.exception;

public class TeeException extends AbstractApplicationException {

    private static final long serialVersionUID = -1234567890123456789L; // Unique ID for serialization

    public TeeException(String message) {
        super("tee: " + message); // Prefix the message with the application name for clarity
    }
}