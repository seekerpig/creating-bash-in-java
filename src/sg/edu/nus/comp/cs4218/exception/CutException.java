package sg.edu.nus.comp.cs4218.exception;

public class CutException extends AbstractApplicationException {

    private static final long serialVersionUID = -334567890123456789L; // Example serialVersionUID

    public CutException(String message) {
        super("cut: " + message); // Prefixing the message with "cut: " to specify the command causing the exception
    }
}
