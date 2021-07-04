package spring.schedule.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

/**
 *スケージュール情報 リクエストデータを格納するためのEntity
 * @author thinh
 *
 */
@Data
public class ScheduleRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	private Long id;
	/**
	 *ユーザID
	 */
	private Long userid;
	/**
	 *スケジュール日付
	 */
	@NotNull(message="スケジュール日付を入力してください．")
	@DateTimeFormat (pattern = "yyyy/MM/dd")
	private LocalDate scheduledate;
	/**
	 *開始時間
	 */
	@NotNull(message="開始時間を入力してください．")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime starttime;
	/**
	 *終了時間
	 */
	@NotNull(message="終了時間を入力してください．")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime endtime;
	/**
	 *スケージュール内容
	 */
	@NotNull(message="スケジュール内容を入力してください．")
	@Size(max=100, message="スケジュールは100バイト以内です．")
	private String schedule;
	/**
	 *メモ
	 */
	@Size(max=100, message="メモは100バイト以内です．")
	private String schedulememo;
	/**
	 *
	 */
	private String version;
}


