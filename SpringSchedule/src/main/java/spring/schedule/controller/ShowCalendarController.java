package spring.schedule.controller;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.schedule.entity.CalendarOutput;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.Schedule;
import spring.schedule.service.ScheduleService;

/**
 *
 * @author thinh
 *スケジュール情報Controller
 */
@Controller
@RequestMapping("calendar")
public class ShowCalendarController {
	/**
	 * スケジュール情報Serivce
	 */
	@Autowired
	ScheduleService scheduleService;

	@RequestMapping
	public String today(Model model) {
		LocalDate today = new LocalDate();
		int year = today.getYear();
		int month = today.getMonthOfYear();
		return month(year, month, model);
	}

	@RequestMapping(value = "month")
	public String month(@RequestParam("year") int year,@RequestParam("month") int month, Model model) {
		LocalDate firstDayOfMonth =new LocalDate(year, month, 1);
		LocalDate lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
		LocalDate firstDayOfCalendar = firstDayOfMonth.dayOfWeek().withMinimumValue();
		LocalDate lastDayOfCalendar = lastDayOfMonth.dayOfWeek().withMaximumValue();

		CalendarOutput output = new CalendarOutput();
		List<DayEntity> weekList = null;
		List<List<DayEntity>> calendar = new ArrayList<List<DayEntity>>();
		String scheduledate = "";
		int count = 0;

		while(true) {
			LocalDate currentDay = firstDayOfCalendar.plusDays(count);
			scheduledate = currentDay.toString();
			//現在の日付のスケジュールを取得
			List<Schedule> scheduleList = scheduleService.selectByDate(scheduledate);
			DayEntity day = new DayEntity();
			day.setDay(scheduledate.substring(8, 10));

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

		model.addAttribute("output", output);
		return "calendar";
	}
}
