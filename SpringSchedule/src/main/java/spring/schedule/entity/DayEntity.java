package spring.schedule.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class DayEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	private List<Long> idList;
	/**
	 *カレンダーの日付
	 */
	private String day;
	/**
	 *スケージュール内容
	 */
	private List<Schedule> scheduleList;
	/**
	 *
	 */
	private String scheduledate;
}
