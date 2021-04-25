package spring.schedule.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class DayEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	private Long id;
	/**
	 *カレンダーの日付
	 */
	private String day;
	/**
	 *スケージュール内容
	 */
	private String schedule;
	/**
	 *スケージュールの日付
	 */
	private String scheduledate;
}
