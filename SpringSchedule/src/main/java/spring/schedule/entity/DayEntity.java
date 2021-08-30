package spring.schedule.entity;

import java.io.Serializable;
import org.joda.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * 日付情報を格納するためのEntity
 *
 * @author thinh
 *
 */
@Data
public class DayEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	private Long id;
	/**
	 * IDリスト
	 */
	private List<Long> idList;
	/**
	 * カレンダーの日付
	 */
	private LocalDate day;
	/**
	 * スケージュール内容リスト
	 */
	private List<ScheduleInfoEntity> scheduleList;
	/**
	 * 日付の年(カレンダー表示画面に戻る用)
	 */
	private int calendarYear;
	/**
	 * 日付の月(カレンダー表示画面に戻る用)
	 */
	private int calendarMonth;
	/**
	 * 操作名
	 */
	private String action;
}
