package spring.schedule.entity;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDate;

import lombok.Data;

@Data
public class CalendarOutput implements Serializable{
	/**
	 *４週
	 */
	private List<List<DayEntity>> calendar;

	/**
	 * 1週間の日付
	 */
	private List<DayEntity> weekList;

	private LocalDate firstDayOfMonth;

	private int yearOfNextMonth;

	private int monthOfNextMonth;

	private int yearOfPrevMonth;

	private int monthOfPrevMonth;

}
