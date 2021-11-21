package spring.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spring.schedule.entity.CalendarInfoEntity;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.entity.UserInfoEntity;
import spring.schedule.exception.ExclusiveException;
import spring.schedule.repository.SelectScheduleMapper;
import spring.schedule.repository.SelectUserMapper;
import spring.schedule.weather.Daily;
import spring.schedule.weather.MainJsonData;
import spring.schedule.weather.Weather;
import spring.schedule.weather.WeatherUtilities;

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
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public CalendarInfoEntity generateCalendarInfo(int year, int month){

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

		HashMap<org.joda.time.LocalDate, String> weatherMap = null;
		try {
			weatherMap = createWeatherHashMap();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		for (int week = 1; week <= weekNum; week++) {
			generateWeekList(firstDayOfCalendar, calendar, firstDayOfWeek, lastDayOfWeek, userId, weatherMap);
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
	 * 
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	private HashMap<org.joda.time.LocalDate, String> createWeatherHashMap()
			throws JsonMappingException, JsonProcessingException {
		List<org.joda.time.LocalDate> targetWeatherDateList = new ArrayList<org.joda.time.LocalDate>();
		List<String> iconList = new ArrayList<String>();

		// dailyリストを取得
		List<Daily> daily = new ObjectMapper()
				.readValue(WeatherUtilities.getHTTPData(WeatherUtilities.getApiRequest()), MainJsonData.class)
				.getDaily();

		for (Daily dailyData : daily) {

			targetWeatherDateList
					.add(org.joda.time.LocalDate.parse(WeatherUtilities.convertUnixTimeToDate(dailyData.getDt()),
							DateTimeFormat.forPattern("yyyy/MM/dd")));

			List<Weather> weather = dailyData.getWeather();
			for (Weather weatherData : weather) {
				iconList.add(weatherData.getIcon());
			}
		}

		HashMap<org.joda.time.LocalDate, String> weatherMap = new HashMap<>();

		for (int i = 0; i < targetWeatherDateList.size(); i++) {
			weatherMap.put(targetWeatherDateList.get(i), iconList.get(i));
		}
		return weatherMap;
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
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	private void generateWeekList(org.joda.time.LocalDate firstDayOfCalendar, List<List<DayEntity>> calendar,
			int firstDayOfWeek, int lastDayOfWeek, Long userId, HashMap<org.joda.time.LocalDate, String> weatherMap){

		List<DayEntity> weekList = new ArrayList<DayEntity>();
		for (int i = firstDayOfWeek; i <= lastDayOfWeek; i++) {
			// IDリストインストタンス
			List<Long> idList = new ArrayList<Long>();
			// 日付情報インストタンス
			DayEntity day = new DayEntity();
			// カレンダーの最初日付をその週の最初日付と最終日付でインクリメントする．
			org.joda.time.LocalDate scheduledate = firstDayOfCalendar.plusDays(i);

			// 天気のアイコンを取得
			if (weatherMap.get(scheduledate) != null) {
				day.setWeatherIconLink(WeatherUtilities.getIconImage(weatherMap.get(scheduledate)));
			}

			// 日付データを日付情報インストタンスに格納する．
			day.setDay(scheduledate);
			// 当月の月を設定する
			day.setCalendarMonth(scheduledate.getMonthOfYear());
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
	 * スケジュールを登録する。
	 * 
	 * @param scheduleRequest
	 */
	public void insertNewSchedule(ScheduleRequest scheduleRequest) {
		ScheduleInfoEntity schedule = createScheduleFromRequest(scheduleRequest);
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
		LocalDateTime screenUpdateDate = scheduleRequest.getUpdatedate();
		if (screenUpdateDate == null) {
			ScheduleInfoEntity updateSchedule = createScheduleFromRequest(scheduleRequest);
			selectScheduleMapper.updateSchedule(updateSchedule);
		} else {
			ScheduleInfoEntity dbScheduleInfo = new ScheduleInfoEntity();
			dbScheduleInfo = selectScheduleMapper.selectScheduleUpdatedate(scheduleRequest.getId());
			LocalDateTime dbUpdateDate = dbScheduleInfo.getUpdatedate();
			// 楽観排他，更新日が異なる場合
			if (!screenUpdateDate.equals(dbUpdateDate)) {
				ExclusiveException exclusiveException = new ExclusiveException();
				exclusiveException.setCalendarYear(scheduleRequest.getScheduledate().getYear());
				exclusiveException.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
				// メッセージ情報を代入
				// logBackXML設定
				// logファイル分ける
				throw exclusiveException;
			}
			ScheduleInfoEntity updateSchedule = createScheduleFromRequest(scheduleRequest);
			updateSchedule.setUpdatedate(LocalDateTime.now());
			selectScheduleMapper.updateSchedule(updateSchedule);
		}

	}

	/**
	 * ケージュール情報を削除する
	 * 
	 * @param scheduleSearchRequest
	 */
	public void deleteSchedule(Long id) {
		selectScheduleMapper.deleteSchedule(id);
	}

	/**
	 * スケージュール情報リクエストデータ
	 * 
	 * @param scheduleRequest
	 * @return
	 */
	public ScheduleInfoEntity createScheduleFromRequest(ScheduleRequest scheduleRequest) {
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
		schedule.setUpdatedate(scheduleRequest.getUpdatedate());
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
	 *
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
	public ScheduleInfoEntity selectScheduleUpdatedate(Long id) {
		return selectScheduleMapper.selectScheduleUpdatedate(id);
	}
}
