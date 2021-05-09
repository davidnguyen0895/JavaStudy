package spring.schedule.entity;

import java.io.Serializable;
import org.joda.time.LocalDate;
import java.util.List;

/*import org.joda.time.LocalDate;*/

import lombok.Data;

@Data
public class DayEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
	 * IDリスト
	 */
	private List<Long> idList;
	/**
	 *カレンダーの日付
	 */
	private LocalDate day;
	/**
	 *スケージュール内容
	 */
	private List<Schedule> scheduleList;
	/**
	 *
	 */
	private int calendarYear;
	/**
	 *
	 */
	private int calendarMonth;
}
