package spring.schedule.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import spring.schedule.constants.Constants;
import spring.schedule.entity.CalendarInfoEntity;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.entity.UserInfoEntity;
import spring.schedule.exception.ExclusiveException;
import spring.schedule.repository.SelectScheduleMapper;
import spring.schedule.repository.SelectUserMapper;

/**
 * rollbackOn = Exception.class : 例外が発生した場合，ロールバックする．
 *
 * @author thinh カレンダー表示画面を作成するService
 */
@Service
public class CalendarService {
	// 週
	private final int weekNum = 6;
	/**
	 * スケージュール情報を参照するためのMapperインストタンス
	 */
	@Autowired
	private SelectScheduleMapper selectScheduleMapper;
	/**
	 * ユーザ情報を参照するためのMapperインストタンス
	 */
	@Autowired
	private SelectUserMapper selectUserMapper;

	/**
	 * カレンダーを表示するための情報を作成するメソッド．
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public CalendarInfoEntity generateCalendarInfo(int year, int month) {
		int firstDayOfWeek = 0;
		int lastDayOfWeek = 6;
		// カレンダー情報を格納Model
		CalendarInfoEntity calendarInfo = new CalendarInfoEntity();
		List<List<DayEntity>> calendar = new ArrayList<List<DayEntity>>();
		// 当月の１日
		org.joda.time.LocalDate firstDayOfMonth = new org.joda.time.LocalDate(year, month, 1);
		// 当月の最後の日
		org.joda.time.LocalDate lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
		// カレンダーの一日目
		// minusDays(1)による最初の日付を日曜日に指定する．
		org.joda.time.LocalDate firstDayOfCalendar = firstDayOfMonth.dayOfWeek().withMinimumValue().minusDays(1);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Principalからログインユーザの情報を取得
		String userName = auth.getName();
		// ユーザIDを取得
		Long userId = getUserId(userName);
		// 来月の日付を取得
		org.joda.time.LocalDate nextMonth = firstDayOfMonth.plusMonths(1);
		// 先月の日付を取得
		org.joda.time.LocalDate prevMonth = lastDayOfMonth.minusMonths(1);

		for (int week = 1; week <= weekNum; week++) {
			generateWeekList(firstDayOfCalendar, calendar, firstDayOfWeek, lastDayOfWeek, userId);
			firstDayOfWeek = firstDayOfWeek + 7;
			lastDayOfWeek = lastDayOfWeek + 7;
		}
		// それぞれの週の日付を作成し，リストに格納する．
		// calendarInfoインストタンスに2重リストcalendarを格納する．
		calendarInfo.setCalendar(calendar);
		// 月の１日を格納する．
		calendarInfo.setFirstDayOfMonth(firstDayOfMonth);
		// 来月の年を格納する．
		calendarInfo.setYearOfNextMonth(nextMonth.getYear());
		// 来月の月を格納する．
		calendarInfo.setMonthOfNextMonth(nextMonth.getMonthOfYear());
		// 先月の年を格納する．
		calendarInfo.setYearOfPrevMonth(prevMonth.getYear());
		// 先月の月を格納する．
		calendarInfo.setMonthOfPrevMonth(prevMonth.getMonthOfYear());
		// 今年を格納
		calendarInfo.setCurrentYear(Constants.TODAY.getYear());
		// 今月を格納
		calendarInfo.setCurrentMonth(Constants.TODAY.getMonthValue());

		// カレンダー情報を返す
		return calendarInfo;
	}

	/**
	 * １週間の日付リストを作成する．
	 *
	 * @param firstDayOfCalendar
	 * @param calendar
	 * @param firstDayOfWeek
	 * @param lastDayOfWeek
	 * @param userId
	 */
	private void generateWeekList(org.joda.time.LocalDate firstDayOfCalendar, List<List<DayEntity>> calendar,
			int firstDayOfWeek, int lastDayOfWeek, Long userId) {
		List<DayEntity> weekList = new ArrayList<DayEntity>();
		for (int i = firstDayOfWeek; i <= lastDayOfWeek; i++) {
			// IDリストインストタンス
			List<Long> idList = new ArrayList<Long>();
			// 日付情報インストタンス
			DayEntity day = new DayEntity();
			// カレンダーの最初日付をその週の最初日付と最終日付でインクリメントする．
			org.joda.time.LocalDate scheduledate = firstDayOfCalendar.plusDays(i);
			// 日付データを日付情報インストタンスに格納する．
			day.setDay(scheduledate);
			// 当月の月を格納する
			day.setCalendarMonth(scheduledate.getMonthOfYear());
			// 当月の年を格納する
			day.setCalendarYear(scheduledate.getYear());
			// 日付データを用いてスケージュール情報リストを参照する．
			List<ScheduleInfoEntity> scheduleList = selectByUserId(userId, scheduledate.toString());
			// スケージュール情報リストを日付情報インストタンスに格納する．
			day.setScheduleList(scheduleList);
			// スケージュール情報リストからスケージュールIDをIDリストに格納
			for (ScheduleInfoEntity schedule : scheduleList) {
				idList.add(schedule.getId());
			}
			// IDリストを日付情報インストタンスに格納する．
			day.setIdList(idList);
			// 日付情報インストタンスを週の日付リストに格納する．
			weekList.add(day);
		}
		calendar.add(weekList);
	}

	/**
	 * 新規登録スケジュール情報をDBに登録する
	 *
	 * @param scheduleRequest
	 * @throws ParseException
	 */
	public void insertNewSchedule(ScheduleRequest scheduleRequest) {
		ScheduleInfoEntity schedule = createSchedule(scheduleRequest);
		selectScheduleMapper.insertNewSchedule(schedule);
	}

	/**
	 * スケージュール情報を更新
	 *
	 * @param scheduleRequest
	 * @throws ExclusiveException
	 */
	@Transactional(rollbackOn = ExclusiveException.class)
	public void updateSchedule(ScheduleRequest scheduleRequest) throws ExclusiveException {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		String screenVersion = scheduleRequest.getVersion();
		String dbVersion = selectScheduleVersion(scheduleRequest.getId());
		// 楽観排他，DB更新日がNULLではない+バージョンが異なる場合
		if (!screenVersion.equals(dbVersion)) {
			throw new ExclusiveException("他のユーザが更新しています．カレンダーに戻して，もう一度更新して下さい．");
		}
		ScheduleInfoEntity updateSchedule = createSchedule(scheduleRequest);
		updateSchedule.setVersion(LocalDateTime.now().format(dateFormat));
		selectScheduleMapper.updateSchedule(updateSchedule);
	}

	/**
	 * スケージュール情報を格納する．
	 * @param scheduleRequest
	 * @return
	 */
	private ScheduleInfoEntity createSchedule(ScheduleRequest scheduleRequest) {
		ScheduleInfoEntity schedule = new ScheduleInfoEntity();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// ログインユーザの情報を取得
		String userName = auth.getName();
		schedule.setId(scheduleRequest.getId());
		schedule.setUserid(getUserId(userName));
		schedule.setScheduledate(scheduleRequest.getScheduledate());
		schedule.setStarttime(scheduleRequest.getStarttime());
		schedule.setEndtime(scheduleRequest.getEndtime());
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		schedule.setVersion(scheduleRequest.getVersion());
		return schedule;
	}

	/**
	 * ユーザIDを取得する．
	 *
	 * @param userName
	 * @return
	 */
	public Long getUserId(String userName) {
		UserInfoEntity userInfo = selectUserMapper.selectUserId(userName);
		return userInfo.getId();
	}

	// ～～～～～～～～～～Mapperを呼び出すメソッド～～～～～～～～～～
	/**
	 * ID情報でDB参照する．
	 *
	 * @param scheduleSearchRequest
	 * @return IDで検索結果
	 */
	public ScheduleInfoEntity selectById(ScheduleRequest scheduleSearchRequest) {
		return selectScheduleMapper.selectById(scheduleSearchRequest);
	}

	/**
	 * スケージュール情報をIDで取得
	 *
	 * @param id
	 * @return
	 */
	public ScheduleInfoEntity selectById(Long id) {
		return selectScheduleMapper.selectById(id);
	}

	/**
	 * 全件検索
	 * @return 全件検索結果
	 */
	public List<ScheduleInfoEntity> selectAll() {
		return selectScheduleMapper.selectAll();
	}

	/**
	 * 日付情報でスケージュール情報を参照する．
	 *
	 * @param scheduledate
	 * @return
	 */
	public List<ScheduleInfoEntity> selectByDate(LocalDate scheduledate) {
		return selectScheduleMapper.selectByDate(scheduledate);
	}

	/**
	 * ユーザIDと日付でスケージュール情報を取得する
	 *
	 * @param userId
	 * @param scheduledate
	 * @return
	 */
	public List<ScheduleInfoEntity> selectByUserId(Long userId, String scheduledate) {
		return selectScheduleMapper.selectByUserId(userId, scheduledate);
	}

	/**
	 * ユーザ名でユーザIDを取得
	 *
	 * @param userName
	 * @return
	 */
	public UserInfoEntity selectUserId(String userName) {
		return selectUserMapper.selectUserId(userName);
	}

	/**
	 * スケジュール更新日を取得
	 *
	 * @param id
	 * @return
	 */
	public String selectScheduleVersion(Long id) {
		return selectScheduleMapper.selectScheduleVersion(id);
	}

	/**
	 * IDでスケージュール情報を削除する
	 *
	 * @param id
	 */
	public void deleteSchedule(Long id) {
		selectScheduleMapper.deleteSchedule(id);
	}
}