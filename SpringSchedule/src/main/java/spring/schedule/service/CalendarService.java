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

import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.CalendarInfoEntity;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.entity.UserInfoEntity;
import spring.schedule.repository.SelectScheduleMapper;
import spring.schedule.repository.SelectUserMapper;

/**
 * rollbackOn = Exception.class : 例外が発生した場合，ロールバックする．
 * @author thinh
 * カレンダー表示画面を作成するService
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class CalendarService {
	//週
	private final int weekNum = 6;
	/**
	 * スケージュール情報を参照するためのMapperインストタンス
	 */
	@Autowired
	private SelectScheduleMapper selectScheduleMapper;
	/**
	 *
	 */
	@Autowired
	private SelectUserMapper selectUserMapper;

	/**
	 * カレンダーを表示するための情報を作成するメソッド．
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
		//当月の１日
		org.joda.time.LocalDate firstDayOfMonth =new org.joda.time.LocalDate(year, month, 1);
		// 当月の最後の日
		org.joda.time.LocalDate lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
		// カレンダーの一日目
		//minusDays(1)による最初の日付を日曜日に指定する．
		org.joda.time.LocalDate firstDayOfCalendar = firstDayOfMonth.dayOfWeek().withMinimumValue().minusDays(1);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//Principalからログインユーザの情報を取得
		String userName = auth.getName();
		//ユーザIDを取得
		Long userId = getUserId(userName);
		//来月の日付を取得
		org.joda.time.LocalDate nextMonth = firstDayOfMonth.plusMonths(1);
		//先月の日付を取得
		org.joda.time.LocalDate prevMonth = lastDayOfMonth.minusMonths(1);

		for(int week = 1; week <= weekNum; week++) {
			generateWeekList(firstDayOfCalendar, calendar, firstDayOfWeek, lastDayOfWeek, userId);
			firstDayOfWeek = firstDayOfWeek + 7;
			lastDayOfWeek = lastDayOfWeek + 7;
		}
		//それぞれの週の日付を作成し，リストに格納する．
		//calendarInfoインストタンスに2重リストcalendarを格納する．
		calendarInfo.setCalendar(calendar);
		//月の１日を格納する．
		calendarInfo.setFirstDayOfMonth(firstDayOfMonth);
		//来月の年を格納する．
		calendarInfo.setYearOfNextMonth(nextMonth.getYear());
		//来月の月を格納する．
		calendarInfo.setMonthOfNextMonth(nextMonth.getMonthOfYear());
		//先月の年を格納する．
		calendarInfo.setYearOfPrevMonth(prevMonth.getYear());
		//先月の月を格納する．
		calendarInfo.setMonthOfPrevMonth(prevMonth.getMonthOfYear());
		//カレンダー情報を返す
		return calendarInfo;
	}
	/**
	 *
	 * @param firstDayOfCalendar
	 * @param calendar
	 * @param week
	 * @param monday
	 * @param sunday
	 */
	private void generateWeekList(org.joda.time.LocalDate firstDayOfCalendar, List<List<DayEntity>> calendar,
			int firstDayOfWeek, int lastDayOfWeek, Long userId) {
		List<DayEntity> weekList = new ArrayList<DayEntity>();
		for(int i = firstDayOfWeek; i <= lastDayOfWeek; i++) {
			//IDリストインストタンス
			List<Long> idList = new ArrayList<Long>();
			//日付情報インストタンス
			DayEntity day = new DayEntity();
			//カレンダーの最初日付をその週の最初日付と最終日付でインクリメントする．
			org.joda.time.LocalDate scheduledate = firstDayOfCalendar.plusDays(i);
			//日付データを日付情報インストタンスに格納する．
			day.setDay(scheduledate);
			//当月の月を設定する
			day.setCalendarMonth(scheduledate.getMonthOfYear());
			//日付データを用いてスケージュール情報リストを参照する．
			List<ScheduleInfoEntity> scheduleList = selectAllByUserId(userId, scheduledate.toString());
			//スケージュール情報リストを日付情報インストタンスに格納する．
			day.setScheduleList(scheduleList);
			//スケージュール情報リストからスケージュールIDをIDリストに格納
			for(ScheduleInfoEntity schedule : scheduleList) {
				idList.add(schedule.getId());
			}
			//IDリストを日付情報インストタンスに格納する．
			day.setIdList(idList);
			//日付情報インストタンスを週の日付リストに格納する．
			weekList.add(day);
		}
		calendar.add(weekList);
	}
	/**
	 * 新規登録スケジュール情報をDBに登録するメソッド．
	 * @param scheduleRequest
	 * @throws ParseException
	 */
	public void insertNewSchedule(ScheduleRequest scheduleRequest){
		ScheduleInfoEntity schedule = createSchedule(scheduleRequest);
		selectScheduleMapper.insertNewSchedule(schedule);
	}
	/**
	 * 更新
	 * @param scheduleRequest
	 * @throws ParseException
	 */
	public boolean updateSchedule(ScheduleRequest scheduleRequest){
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		String updateVersion = LocalDateTime.now().format(dateFormat);
		String firstVersion = scheduleRequest.getVersion();
		String secondVersion = selectScheduleVersion(scheduleRequest.getId());

		if(secondVersion != null) {
			if(firstVersion == null) {
				firstVersion = "9999";
			}
			if(!isValidScheduleVersion(firstVersion, secondVersion)) {
				return false;
			}
		}

		scheduleRequest.setVersion(updateVersion);
		ScheduleInfoEntity updateSchedule = createSchedule(scheduleRequest);
		selectScheduleMapper.updateSchedule(updateSchedule);
		return true;
	}
	/**
	 * @param scheduleRequest スケージュール情報リクエストデータ
	 * @return
	 * @throws ParseException
	 */
	private ScheduleInfoEntity createSchedule(ScheduleRequest scheduleRequest){
		ScheduleInfoEntity schedule = new ScheduleInfoEntity();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//ログインユーザの情報を取得
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
	 *	ユーザID
	 * @param userName
	 * @return
	 */
	public Long getUserId(String userName) {
		UserInfoEntity userInfo = selectUserMapper.selectUserId(userName);
		return userInfo.getId();
	}
	//～～～～～～～～～～Mapperを呼び出すメソッド～～～～～～～～～～
	/**
	 * ID情報でDB参照する．
	 *
	 * @param scheduleSearchRequest
	 * @return IDで検索結果
	 */
	public ScheduleInfoEntity selectById(ScheduleSearchRequest scheduleSearchRequest) {
		return selectScheduleMapper.selectById(scheduleSearchRequest);
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
	 * @param scheduledate
	 * @return
	 */
	public List<ScheduleInfoEntity> selectAllByDate(LocalDate scheduledate) {
		return selectScheduleMapper.selectAllByDate(scheduledate);
	}
	/**
	 *
	 * @param userId
	 * @return
	 */
	public List<ScheduleInfoEntity> selectAllByUserId(Long userId, String scheduledate){
		return selectScheduleMapper.selectAllByUserId(userId, scheduledate);
	}
	/**
	 *
	 * @param userName
	 * @return
	 */
	public UserInfoEntity selectUserId(String userName) {
		return selectUserMapper.selectUserId(userName);
	}
	/**
	 * ID情報でDBを参照する
	 * @param id
	 * @return
	 */
	public ScheduleInfoEntity selectAllById(Long id) {
		return selectScheduleMapper.selectAllById(id);
	}
	/**
	 *	IDで削除
	 * @param id
	 */
	public void deleteSchedule(Long id) {
		selectScheduleMapper.deleteSchedule(id);
	}
	/**
	 *
	 * @param scheduleDate
	 * @return
	 */
	public String selectScheduleVersion(Long id) {
		return selectScheduleMapper.selectScheduleVersion(id);
	}
	/**
	 *
	 * @param id
	 * @param version
	 */
	public void updateScheduleVersion(Long id, String version) {
		selectScheduleMapper.updateScheduleVersion(id, version);
	}
	/**
	 *
	 * @param currentVersion
	 * @param oldVersion
	 * @return
	 */
	private boolean isValidScheduleVersion(String currentVersion, String oldVersion) {
		if(!currentVersion.equals(oldVersion)) {
			return false;
		}else {
			return true;
		}
	}
}