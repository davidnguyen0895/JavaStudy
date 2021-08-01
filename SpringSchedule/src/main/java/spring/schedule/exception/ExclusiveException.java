package spring.schedule.exception;

/**
 *
 * @author thinh
 *
 */
public class ExclusiveException extends Exception{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
    public ExclusiveException() {
        super();
    }
    /**
     *
     * @param message
     */
    public ExclusiveException(String message) {
        super(message);
    }
    /**
     *
     * @param message
     * @param cause
     */
    public ExclusiveException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     *
     * @param cause
     */
    public ExclusiveException(Throwable cause) {
        super(cause);
    }
}
