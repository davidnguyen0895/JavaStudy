package spring.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import spring.schedule.common.Ulitities;
import spring.schedule.constants.Constants;
import spring.schedule.entity.CalendarInfoEntity;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.entity.UserInfoEntity;
import spring.schedule.exception.ExclusiveException;
import spring.schedule.mapper.SelectScheduleMapper;
import spring.schedule.mapper.SelectUserMapper;
import spring.schedule.weather.WeatherUtilities;

/**
 * rollbackOn = Exception.class : 例外が発生した場合，ロールバックする．
 * 
 * @author thinh カレンダー表示画面を作成するService
 */
@Service
public class CalendarService {
	// 天気APIリクエストリンクのパラメータ
	private final String lat = "35.6895";
	private final String lon = "139.6917";
	private final String apiKey = "254ca69e129ef9485cb3df5f70b55caa";

	// カレンダーを何週目まで表示するか定義するための変数。
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
	 * @return カレンダーを表示するための情報
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
		// ユーザIDを取得
		//Long userId = getUserId(Ulitities.getLoginUserName());
		//ユーザリストを取得
		List<UserInfoEntity> userList = selectUserMapper.selectAllUser();
		//ユーザリストを格納
		calendarInfo.setUserList(userList);
		// 来月の日付を取得
		org.joda.time.LocalDate nextMonth = firstDayOfMonth.plusMonths(1);
		// 先月の日付を取得
		org.joda.time.LocalDate prevMonth = lastDayOfMonth.minusMonths(1);

		HashMap<org.joda.time.LocalDate, String> weatherMap = null;
		try {
			weatherMap = WeatherUtilities.createWeatherHashMap(lat, lon, apiKey);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		for (int week = 1; week <= weekNum; week++) {
			generateWeekList(firstDayOfCalendar, calendar, firstDayOfWeek, lastDayOfWeek, weatherMap);
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
		calendarInfo.setCurrentYear(LocalDate.now().getYear());
		// 今月を格納
		calendarInfo.setCurrentMonth(LocalDate.now().getMonthValue());

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
	 * @param weatherMap
	 */
	private void generateWeekList(org.joda.time.LocalDate firstDayOfCalendar, List<List<DayEntity>> calendar,
			int firstDayOfWeek, int lastDayOfWeek, HashMap<org.joda.time.LocalDate, String> weatherMap) {

		List<DayEntity> weekList = new ArrayList<DayEntity>();
		for (int i = firstDayOfWeek; i <= lastDayOfWeek; i++) {
			// 日付情報インストタンス
			DayEntity day = new DayEntity();
			// カレンダーの最初日付をその週の最初日付と最終日付でインクリメントする．
			org.joda.time.LocalDate scheduledate = firstDayOfCalendar.plusDays(i);

			// 日付を引数として、天気のアイコンを取得しDayEntityに格納する
			if (weatherMap.get(scheduledate) != null) {
				day.setWeatherIconLink(WeatherUtilities.getIconImage(weatherMap.get(scheduledate)));
			}

			// 日付データを日付情報インストタンスに格納する．
			day.setDay(scheduledate);
			// 当月の月を設定する
			day.setCalendarMonth(scheduledate.getMonthOfYear());
			// 日付データを用いてスケージュール情報リストを参照する．
			List<ScheduleInfoEntity> scheduleList = selectByDate(scheduledate.toString());
			// スケージュール情報リストを日付情報インストタンスに格納する．
			day.setScheduleList(scheduleList);
			// 日付情報インストタンスを週の日付リストに格納する．
			weekList.add(day);
		}
		calendar.add(weekList);
	}

	/**
	 * スケジュールを登録する。
	 * 
	 * @param scheduleRequest
	 */
	public void insertNewSchedule(ScheduleRequest scheduleRequest) {
		ScheduleInfoEntity schedule = createScheduleFromRequest(scheduleRequest, Constants.ACTION_REGIST);
		selectScheduleMapper.insertNewSchedule(schedule);
	}

	/**
	 * スケージュール情報を更新する。
	 * 
	 * @param scheduleRequest
	 * @throws ExclusiveException
	 */
	@Transactional(rollbackOn = ExclusiveException.class)
	public void updateSchedule(ScheduleRequest scheduleRequest) throws ExclusiveException {
		if (scheduleRequest.getUpdatedate() == null) {
			ScheduleInfoEntity updateSchedule = createScheduleFromRequest(scheduleRequest, Constants.ACTION_UPDATE);
			selectScheduleMapper.updateSchedule(updateSchedule);
		} else {
			ScheduleInfoEntity dbScheduleInfo = new ScheduleInfoEntity();
			dbScheduleInfo = selectScheduleMapper.selectScheduleUpdatedate(scheduleRequest.getId());
			LocalDateTime dbUpdateDate = dbScheduleInfo.getUpdatedate();
			// 楽観排他，更新日が異なる場合
			if (!scheduleRequest.getUpdatedate().equals(dbUpdateDate)) {
				ExclusiveException exclusiveException = new ExclusiveException();
				exclusiveException.setCalendarYear(scheduleRequest.getScheduledate().getYear());
				exclusiveException.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
				throw exclusiveException;
			}
			ScheduleInfoEntity updateSchedule = createScheduleFromRequest(scheduleRequest, Constants.ACTION_UPDATE);
			updateSchedule.setUpdatedate(LocalDateTime.now());
			selectScheduleMapper.updateSchedule(updateSchedule);
		}

	}

	/**
	 * ケージュール情報を削除する
	 * 
	 * @param id
	 */
	public void deleteSchedule(Long id) {
		selectScheduleMapper.deleteSchedule(id);
	}

	/**
	 * スケージュール情報リクエストデータを作成
	 * 
	 * @param 	scheduleRequest
	 * @return	スケージュール情報リクエストデータ
	 */
	public ScheduleInfoEntity createScheduleFromRequest(ScheduleRequest scheduleRequest, String action) {
		ScheduleInfoEntity schedule = new ScheduleInfoEntity();
		
		if(action.equals(Constants.ACTION_REGIST)){
			schedule.setId(selectScheduleMapper.selectLatestId());
		}else {
			schedule.setId(scheduleRequest.getId());
		}
		schedule.setUserid(getUserId(Ulitities.getLoginUserName()));
		schedule.setUsername(Ulitities.getLoginUserName());
		schedule.setScheduledate(scheduleRequest.getScheduledate());
		schedule.setStarttime(scheduleRequest.getStarttime());
		schedule.setEndtime(scheduleRequest.getEndtime());
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		schedule.setUpdatedate(scheduleRequest.getUpdatedate());
		return schedule;
	}

	/**
	 * ユーザIDを取得する．
	 * 
	 * @param userName
	 * @return ユーザID
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
	 * @return スケージュール情報
	 */
	public ScheduleInfoEntity selectById(Long id) {
		return selectScheduleMapper.selectById(id);
	}

	/**
	 *
	 * @return 全件検索結果
	 */
	public List<ScheduleInfoEntity> selectAll() {
		return selectScheduleMapper.selectAll();
	}

	/**
	 * 日付情報でスケージュール情報を取得。
	 * 
	 * @param scheduledate
	 * @return 日付情報でスケージュール情報
	 */
	public List<ScheduleInfoEntity> selectByDate(String scheduledate) {
		return selectScheduleMapper.selectByDate(scheduledate);
	}

	/**
	 * ユーザIDと日付でスケージュール情報を取得。
	 * 
	 * @param userId
	 * @param scheduledate
	 * @return ユーザIDと日付でスケージュール情報
	 */
	public List<ScheduleInfoEntity> selectByUserId(Long userId, String scheduledate) {
		return selectScheduleMapper.selectByUserId(userId, scheduledate);
	}

	/**
	 * ユーザ名でユーザIDを取得
	 * 
	 * @param userName
	 * @return ユーザ名でユーザID
	 */
	public UserInfoEntity selectUserId(String userName) {
		return selectUserMapper.selectUserId(userName);
	}

	/**
	 * スケジュール更新日を取得
	 * 
	 * @param id 
	 * @return スケジュール更新日
	 */
	public ScheduleInfoEntity selectScheduleUpdatedate(Long id) {
		return selectScheduleMapper.selectScheduleUpdatedate(id);
	}
}
