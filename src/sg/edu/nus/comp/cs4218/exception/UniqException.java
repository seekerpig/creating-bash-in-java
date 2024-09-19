package sg.edu.nus.comp.cs4218.exception;

public class UniqException extends AbstractApplicationException {
        private static final long serialVersionUID = -742723164724927309L;

        public UniqException(String message) {
            super("uniq: " + message);
        }
}
