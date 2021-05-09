package spring.schedule.service;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.CalendarOutput;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.InsertScheduleEntity;
import spring.schedule.entity.Schedule;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.repository.SelectScheduleMapper;

@Service
@Transactional(rollbackOn = Exception.class)
public class CalendarService {
	//
	private final SimpleDateFormat dateFormat_mmddyyyy = new SimpleDateFormat("yyyy/MM/dd");
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
	private final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	/**
	 * Mapper
	 */
	@Autowired
	private SelectScheduleMapper selectScheduleMapper;

	/**
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public CalendarOutput getCalendarOutput(int year, int month) {
		//一日
		LocalDate firstDayOfMonth =new LocalDate(year, month, 1);
		// 当月の最後の日
		LocalDate lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
		// カレンダーの一日目
		LocalDate firstDayOfCalendar = firstDayOfMonth.dayOfWeek().withMinimumValue().minusDays(1);
		// カレンダーの最後の日
		/*
		 * LocalDate lastDayOfCalendar =
		 * lastDayOfMonth.dayOfWeek().withMaximumValue().minusDays(1);
		 */
		// カレンダーを格納Model
		CalendarOutput output = new CalendarOutput();
		// 5週間の日付
		List<DayEntity> firstWeekList = new ArrayList<DayEntity>();
		List<DayEntity> secondWeekList = new ArrayList<DayEntity>();
		List<DayEntity> thirdWeekList = new ArrayList<DayEntity>();
		List<DayEntity> fourthWeekList = new ArrayList<DayEntity>();
		List<DayEntity> fifthWeekList = new ArrayList<DayEntity>();
		List<DayEntity> sixthWeekList = new ArrayList<DayEntity>();
		List<List<DayEntity>> calendar = new ArrayList<List<DayEntity>>();

		generateWeekList(0, 6, firstDayOfCalendar, firstWeekList, output);
		generateWeekList(7, 13, firstDayOfCalendar, secondWeekList, output);
		generateWeekList(14, 20, firstDayOfCalendar, thirdWeekList, output);
		generateWeekList(21, 27, firstDayOfCalendar, fourthWeekList, output);
		generateWeekList(28, 34, firstDayOfCalendar, fifthWeekList, output);
		generateWeekList(35, 41, firstDayOfCalendar, sixthWeekList, output);

		calendar.add(firstWeekList);
		calendar.add(secondWeekList);
		calendar.add(thirdWeekList);
		calendar.add(fourthWeekList);
		calendar.add(fifthWeekList);
		calendar.add(sixthWeekList);

		LocalDate nextMonth = firstDayOfMonth.plusMonths(1);
		LocalDate prevMonth = lastDayOfMonth.minusMonths(1);
		output.setCalendar(calendar);
		output.setFirstDayOfMonth(firstDayOfMonth);
		output.setYearOfNextMonth(nextMonth.getYear());
		output.setMonthOfNextMonth(nextMonth.getMonthOfYear());
		output.setYearOfPrevMonth(prevMonth.getYear());
		output.setMonthOfPrevMonth(prevMonth.getMonthOfYear());
		return output;
	}

	/**
	 *
	 * @param mondayCount
	 * @param sundayCount
	 * @param firstDayOfCalendar
	 * @param weekList
	 */
	private void generateWeekList(int firstDay, int lastDay, LocalDate firstDayOfCalendar,
			List<DayEntity> weekList, CalendarOutput output) {
		for(int i = firstDay; i <= lastDay; i++) {
			//IDリスト
			List<Long> idList = new ArrayList<Long>();
			//日付Model
			DayEntity day = new DayEntity();
			LocalDate scheduledate = firstDayOfCalendar.plusDays(i);
			//カレンダー表示のための日付
			day.setDay(scheduledate);
			//スケージュールリスト
			List<Schedule> scheduleList = selectAllByDate(scheduledate.toString());
			day.setScheduleList(scheduleList);
			//スケージュールがあった日付のスケージュールIDをIDリストに格納
			for(Schedule schedule : scheduleList) {
				idList.add(schedule.getId());
			}
			day.setIdList(idList);
			//日付リストに格納
			weekList.add(day);
		}
	}
	/**
	 * ユーザ情報検索
	 *
	 * @param scheduleSearchRequest
	 * @return IDで検索結果
	 */
	public Schedule selectById(ScheduleSearchRequest scheduleSearchRequest) {
		return selectScheduleMapper.selectById(scheduleSearchRequest);
	}

	/**
	 *
	 * @return 全件検索結果
	 */
	public List<Schedule> selectAll() {
		return selectScheduleMapper.selectAll();
	}
	/**
	 *
	 * @param scheduledate
	 * @return
	 */
	public List<Schedule> selectAllByDate(String scheduledate) {
		return selectScheduleMapper.selectAllByDate(scheduledate);
		}
	/**
	 *
	 * @param id
	 * @return
	 */
	public Schedule selectAllById(Long id) {
		return selectScheduleMapper.selectAllById(id);
	}
	/**
	 *	スケージュール情報新規登録
	 * @param schedule スケージュール情報
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
		java.sql.Time convertedStarttime = new java.sql.Time(timeFormat.parse(scheduleRequest.getStarttime()).getTime());
		java.sql.Time convertedEndtime = new java.sql.Time(timeFormat.parse(scheduleRequest.getEndtime()).getTime());
		java.util.Date convertedUtilScheduleDate = dateFormat_mmddyyyy.parse(scheduleRequest.getScheduledate());
		java.sql.Date convertedSqlScheduleDate = new java.sql.Date(convertedUtilScheduleDate.getTime());
		String stringStartTime = convertedStarttime.toString();
		String stringEndTime = convertedEndtime.toString();
		String startTimeStamp = scheduleRequest.getScheduledate() + " " + stringStartTime;
		String endTimeStamp = scheduleRequest.getScheduledate() + " " + stringEndTime;
		Date resultStartTime = (Date) timeStampFormat.parse(startTimeStamp);
		Date resultEndTime = (Date) timeStampFormat.parse(endTimeStamp);
		schedule.setUserid(3);
		schedule.setScheduledate(convertedSqlScheduleDate);
		schedule.setStarttime(resultStartTime);
		schedule.setEndtime(resultEndTime);
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		return schedule;
	}
}