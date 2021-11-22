package spring.schedule.entity;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDate;

import lombok.Data;

/**
 * カレンダー情報を格納するクラス
 * 
 * @author thinh
 *
 */
@Data
public class CalendarInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * x週間の日付リスト
	 */
	private List<List<DayEntity>> calendar;
	/**
	 * 月の最初の日付（1日）
	 */
	private LocalDate firstDayOfMonth;
	/**
	 * 来月の年
	 */
	private int yearOfNextMonth;
	/**
	 * 来月の月
	 */
	private int monthOfNextMonth;
	/**
	 * 先月の年
	 */
	private int yearOfPrevMonth;
	/**
	 * 先月の月
	 */
	private int monthOfPrevMonth;
	/**
	 * 現在の年
	 */
	private int currentYear;
	/**
	 * 現在の月
	 */
	private int currentMonth;
	/**
	 * //ユーザリスト
	 */
	private List<UserInfoEntity> userList;
}