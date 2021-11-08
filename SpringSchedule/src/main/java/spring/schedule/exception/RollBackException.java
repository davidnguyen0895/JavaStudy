package spring.schedule.exception;

/**
 * ロールバック例外クラス
 * 
 * @author 2020007523
 *
 */
public class RollBackException extends Exception {
	/**
	 *
	 */
	public RollBackException() {
		super();
	}

	/**
	 *
	 * @param message
	 */
	public RollBackException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public RollBackException(String message, Throwable cause) {
		super(message, cause);
	}
}
