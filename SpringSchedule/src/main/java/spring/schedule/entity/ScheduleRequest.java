package spring.schedule.entity;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
	private int userid;
	/**
	 *スケジュール日付
	 */
	@NotEmpty(message="スケジュール日付を入力してください．")
	//@Pattern(regexp = "^[0-9]{4}/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message="スケジュール日付が不正です．")
	private String scheduledate;
	/**
	 *開始時間
	 */
	@NotEmpty(message="開始時間を入力してください．")
	private String starttime;
	/**
	 *終了時間
	 */
	@NotEmpty(message="終了時間を入力してください．")
	private String endtime;
	/**
	 *スケージュール内容
	 */
	@NotEmpty(message="スケジュール内容を入力してください．")
	@Size(max=100, message="スケジュールは100バイト以内です．")
	private String schedule;
	/**
	 *メモ
	 */
	@Size(max=100, message="メモは100バイト以内です．")
	private String schedulememo;
}


