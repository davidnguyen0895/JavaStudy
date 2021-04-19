package spring.schedule.service;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.schedule.entity.CalendarOutput;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.Schedule;

@Service
@Transactional(rollbackOn = Exception.class)
public class CalendarService {
	/**
	 * スケジュール情報取得Serivce
	 */
	@Autowired
	ScheduleService scheduleService;

	public CalendarOutput setCalendar(CalendarOutput output, LocalDate firstDayOfMonth, LocalDate lastDayOfMonth,
			LocalDate firstDayOfCalendar, LocalDate lastDayOfCalendar,
			List<List<DayEntity>> calendar, List<DayEntity> weekList){
		//スケジュール日付
		String scheduledate = "";
		int count = 0;

		while(true) {
			//カレンダーの一日目からカウントアップ
			LocalDate currentDay = firstDayOfCalendar.plusDays(count);
			//Stringに変換
			scheduledate = currentDay.toString();
			//現在の日付のスケジュールリストを取得
			List<Schedule> scheduleList = scheduleService.selectByDate(scheduledate);
			//日付Model
			DayEntity day = new DayEntity();
			//YYYY/MM/DDのDDを格納
			day.setDay(scheduledate);
			//スケジュールリストをfor文で，それぞれの日付のスケジュールを日付Modelに格納
			if(scheduleList != null) {
				for(Schedule schedule : scheduleList) {
					day.setSchedule(schedule.getSchedule());
				}
			}
			if(currentDay.isAfter(lastDayOfCalendar)) {
				break;
			}
			if(weekList == null) {
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
}
