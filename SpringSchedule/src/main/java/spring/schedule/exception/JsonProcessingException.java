package spring.schedule.exception;

import java.io.IOException;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Json変換処理の例外クラス
 * 
 * @author 2020007523
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JsonProcessingException extends IOException {
	private static final long serialVersionUID = 1L;
	/**
	 * 日付の年(カレンダー表示画面に戻る用)
	 */
	private int calendarYear;
	/**
	 * 日付の月(カレンダー表示画面に戻る用)
	 */
	private int calendarMonth;

	public JsonProcessingException() {
		super();
	}

	/**
	 * 
	 * @param message
	 */
	public JsonProcessingException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public JsonProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}