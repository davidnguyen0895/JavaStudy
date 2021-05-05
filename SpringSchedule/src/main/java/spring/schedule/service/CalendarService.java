package spring.schedule.service;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.CalendarOutput;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.Schedule;
import spring.schedule.repository.SelectScheduleMapper;

@Service
@Transactional(rollbackOn = Exception.class)
public class CalendarService {
	/**
	 * Mapper
	 */
	@Autowired
	private SelectScheduleMapper selectScheduleMapper;

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

		//DateTimeFormatter
		/* DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd"); */

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
			String scheduledate = firstDayOfCalendar.plusDays(i).toString().replace("-", "/");
			//スケージュールデータを取得するための日付
			day.setScheduledate(scheduledate);
			//カレンダー表示のための日付
			day.setDay(generateCalendarDate(scheduledate));
			//スケージュールリスト
			List<Schedule> scheduleList = selectAllByDate(scheduledate);
			day.setScheduleList(scheduleList);
			//スケージュールがあった日付のスケージュールIDをIDリストに格納
			for(Schedule schedule : scheduleList) {
				idList.add(schedule.getId());
				day.setIdList(idList);
			}
			//日付リストに格納
			weekList.add(day);
		}
	}
	/**
	 *
	 * @param scheduledate
	 * @return
	 */
	private String generateCalendarDate(String scheduledate) {
		String result = "";
		if(scheduledate.substring(8, 9).equals("0")) {
			result = scheduledate.substring(9,10);
			return result;
		}else {
			result = scheduledate.substring(8,10);
			return result;
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
		return selectScheduleMapper.selectAllByDate(scheduledate); }
	/**
	 *
	 * @param id
	 * @return
	 */
	public Schedule selectAllById(Long id) {
		return selectScheduleMapper.selectAllById(id);
	}
}