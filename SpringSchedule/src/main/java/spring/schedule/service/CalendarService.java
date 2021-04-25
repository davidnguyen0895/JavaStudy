package spring.schedule.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.joda.time.DateTimeConstants;
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

	public CalendarOutput getCalendarOutput(LocalDate firstDayOfMonth) {
		// 当月の最後の日
		LocalDate lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
		// カレンダーの一日目
		LocalDate firstDayOfCalendar = firstDayOfMonth.dayOfWeek().withMinimumValue();
		// カレンダーの最後の日
		LocalDate lastDayOfCalendar = lastDayOfMonth.dayOfWeek().withMaximumValue();
		// カレンダーを格納Model
		CalendarOutput output = new CalendarOutput();
		// 一週間の日付
		List<DayEntity> weekList = null;
		// 4週間の日付
		List<List<DayEntity>> calendar = new ArrayList<List<DayEntity>>();
		// スケジュール日付
		String scheduledate = "";
		int count = 0;
		//DayFormat
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd");

		while (true) {
			// カレンダーの一日目からカウントアップ
			LocalDate currentDay = firstDayOfCalendar.plusDays(count);
			scheduledate = currentDay.toString();
			// 現在の日付のスケジュールリストを取得
			List<Schedule> scheduleList = selectAllByDate(scheduledate);
			// 日付Model
			DayEntity day = new DayEntity();
			day.setDay(scheduledate);
			// スケジュールリストをfor文で，それぞれの日付のスケジュールを日付Modelに格納
			if(scheduleList != null) {
				for (Schedule schedule : scheduleList) {
					day.setSchedule(schedule.getSchedule());
					day.setScheduledate(dayFormat.format(schedule.getScheduledate()));
				}
			}
			if (currentDay.isAfter(lastDayOfCalendar)) {
				break;
			}
			if (weekList == null) {
				weekList = new ArrayList<DayEntity>();
				calendar.add(weekList);
			}
			weekList.add(day);

			int week = currentDay.getDayOfWeek();
			if (week == DateTimeConstants.SUNDAY) {
				weekList = null;
			}
			count++;
		}
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
	public List<Schedule> selectByDate(String scheduledate) {
		return selectScheduleMapper.selectByDate(scheduledate);
	}

	/**
	 *
	 * @param scheduledate
	 * @return
	 */
	public List<Schedule> selectAllByDate(String scheduledate) {
		return selectScheduleMapper.selectAllByDate(scheduledate);
	}
}