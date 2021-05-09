package spring.schedule.service;

import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.schedule.constants.Constants;
import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.CalendarInfoEntity;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.InsertScheduleEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.repository.SelectScheduleMapper;

/**
 * rollbackOn = Exception.class : 例外が発生した場合，ロールバックする．
 * @author thinh
 * カレンダー表示画面を作成するService
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class CalendarService {
	/**
	 * スケージュール情報を参照するためのMapperインストタンス
	 */
	@Autowired
	private SelectScheduleMapper selectScheduleMapper;

	/**
	 * カレンダーを表示するための情報を作成するメソッド．
	 * @param year
	 * @param month
	 * @return
	 */
	public CalendarInfoEntity generateCalendarInfo(int year, int month) {
		//当月の１日
		LocalDate firstDayOfMonth =new LocalDate(year, month, 1);
		// 当月の最後の日
		LocalDate lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
		// カレンダーの一日目
		//minusDays(1)による最初の日付を日曜日に指定する．
		LocalDate firstDayOfCalendar = firstDayOfMonth.dayOfWeek().withMinimumValue().minusDays(1);
		// カレンダー情報を格納Model
		CalendarInfoEntity calendarInfo = new CalendarInfoEntity();
		// 6週間分の日付リスト
		List<DayEntity> firstWeekList = new ArrayList<DayEntity>();
		List<DayEntity> secondWeekList = new ArrayList<DayEntity>();
		List<DayEntity> thirdWeekList = new ArrayList<DayEntity>();
		List<DayEntity> fourthWeekList = new ArrayList<DayEntity>();
		List<DayEntity> fifthWeekList = new ArrayList<DayEntity>();
		List<DayEntity> sixthWeekList = new ArrayList<DayEntity>();
		//6週間分の日付リストを格納するための2重リスト
		List<List<DayEntity>> calendar = new ArrayList<List<DayEntity>>();
		//それぞれの週の日付を作成し，リストに格納する．
		generateWeekList(0, 6, firstDayOfCalendar, firstWeekList);
		generateWeekList(7, 13, firstDayOfCalendar, secondWeekList);
		generateWeekList(14, 20, firstDayOfCalendar, thirdWeekList);
		generateWeekList(21, 27, firstDayOfCalendar, fourthWeekList);
		generateWeekList(28, 34, firstDayOfCalendar, fifthWeekList);
		generateWeekList(35, 41, firstDayOfCalendar, sixthWeekList);
		//それぞれの週の日付リストデータを2重リストcalendarに格納する．
		calendar.add(firstWeekList);
		calendar.add(secondWeekList);
		calendar.add(thirdWeekList);
		calendar.add(fourthWeekList);
		calendar.add(fifthWeekList);
		calendar.add(sixthWeekList);
		//来月の日付を取得
		LocalDate nextMonth = firstDayOfMonth.plusMonths(1);
		//先月の日付を取得
		LocalDate prevMonth = lastDayOfMonth.minusMonths(1);
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
	 * @param firstDay その週の最初の日
	 * @param lastDay その週の最終の日
	 * @param firstDayOfCalendar カレンダーの最初の日付
	 * @param weekList 週の日付リスト
	 */
	private void generateWeekList(int firstDay, int lastDay, LocalDate firstDayOfCalendar,
			List<DayEntity> weekList) {
		//その週の最初日付から最終日付までループで処理する．
		for(int i = firstDay; i <= lastDay; i++) {
			//IDリストインストタンス
			List<Long> idList = new ArrayList<Long>();
			//日付情報インストタンス
			DayEntity day = new DayEntity();
			//カレンダーの最初日付をその週の最初日付と最終日付でインクリメントする．
			LocalDate scheduledate = firstDayOfCalendar.plusDays(i);
			//日付データを日付情報インストタンスに格納する．
			day.setDay(scheduledate);
			//日付データを用いてスケージュール情報リストを参照する．
			List<ScheduleInfoEntity> scheduleList = selectAllByDate(scheduledate.toString());
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
	}
	/**
	 * 新規登録スケジュール情報をDBに登録するメソッド．
	 * @param scheduleRequest
	 * @throws ParseException
	 */
	public void insertNewSchedule(ScheduleRequest scheduleRequest) throws ParseException {
		InsertScheduleEntity schedule = new InsertScheduleEntity();
		schedule = CreateSchedule(scheduleRequest);
		selectScheduleMapper.insertNewSchedule(schedule);
	}
	/**
	 * @param scheduleRequest スケージュール情報リクエストデータ
	 * @return
	 * @throws ParseException
	 */
	private InsertScheduleEntity CreateSchedule(ScheduleRequest scheduleRequest) throws ParseException {
		InsertScheduleEntity schedule = new InsertScheduleEntity();
		String startTimeStr = scheduleRequest.getStarttime();
		java.sql.Time convertedStarttime = new java.sql.Time(Constants.timeFormat.parse(startTimeStr).getTime());
		String endTimeStr = scheduleRequest.getEndtime();
		java.sql.Time convertedEndtime = new java.sql.Time(Constants.timeFormat.parse(endTimeStr).getTime());
		String scheduleDateStr = scheduleRequest.getScheduledate();
		java.util.Date convertedUtilScheduleDate = Constants.dateFormat.parse(scheduleDateStr);
		java.sql.Date convertedSqlScheduleDate = new java.sql.Date(convertedUtilScheduleDate.getTime());
		String startTimeStamp = scheduleRequest.getScheduledate() + Constants.SPACE + convertedStarttime.toString();
		String endTimeStamp = scheduleRequest.getScheduledate() + Constants.SPACE + convertedEndtime.toString();
		Date resultStartTime = (Date) Constants.timeStampFormat.parse(startTimeStamp);
		Date resultEndTime = (Date) Constants.timeStampFormat.parse(endTimeStamp);
		schedule.setUserid(3);
		schedule.setScheduledate(convertedSqlScheduleDate);
		schedule.setStarttime(resultStartTime);
		schedule.setEndtime(resultEndTime);
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		return schedule;
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
	 * 日付情報でDBを参照する．
	 * @param scheduledate
	 * @return
	 */
	public List<ScheduleInfoEntity> selectAllByDate(String scheduledate) {
		return selectScheduleMapper.selectAllByDate(scheduledate);
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
	 *
	 * @param id
	 */
	public void deleteSchedule(Long id) {
		selectScheduleMapper.deleteSchedule(id);
	}
}