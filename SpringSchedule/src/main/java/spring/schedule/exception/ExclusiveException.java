package spring.schedule.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 楽観的排他の例外クラス
 * 
 * @author 2020007523
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExclusiveException extends RollBackException {
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
	 * 楽観的排他の例外(オーバライド)
	 */
	public ExclusiveException() {
		super();
	}

	/**
	 * 楽観的排他の例外(オーバライド)
	 * 
	 * @param message
	 */
	public ExclusiveException(String message) {
		super(message);
	}

	/**
	 * 楽観的排他の例外(オーバライド)
	 * 
	 * @param message
	 * @param cause
	 */
	public ExclusiveException(String message, Throwable cause) {
		super(message, cause);
	}
}