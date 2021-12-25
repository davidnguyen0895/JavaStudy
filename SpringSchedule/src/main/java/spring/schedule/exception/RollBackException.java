package spring.schedule.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ロールバック例外クラス
 * 
 * @author 2020007523
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RollBackException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 日付の年(カレンダー表示画面に戻る用)
	 */
	private int calendarYear;
	/**
	 * 日付の月(カレンダー表示画面に戻る用)
	 */
	private int calendarMonth;

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
