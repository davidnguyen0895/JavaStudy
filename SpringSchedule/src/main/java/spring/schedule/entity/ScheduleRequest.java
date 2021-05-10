package spring.schedule.entity;

import java.io.Serializable;
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
	private String scheduledate;
	/**
	 *開始時間
	 */
	private String starttime;
	/**
	 *終了時間
	 */
	private String endtime;
	/**
	 *スケージュール内容
	 */
	private String schedule;
	/**
	 *メモ
	 */
	private String schedulememo;
}


