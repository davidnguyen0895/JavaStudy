package spring.schedule.controller;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import spring.schedule.entity.CalendarOutput;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.Schedule;
import spring.schedule.service.CalendarService;
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
	 *カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;
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
		//一日
		LocalDate firstDayOfMonth =new LocalDate(year, month, 1);
		//当月の最後の日
		LocalDate lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
		//カレンダーの一日目
		LocalDate firstDayOfCalendar = firstDayOfMonth.dayOfWeek().withMinimumValue();
		//カレンダーの最後の日
		LocalDate lastDayOfCalendar = lastDayOfMonth.dayOfWeek().withMaximumValue();

		//カレンダーを格納Model
		CalendarOutput output = new CalendarOutput();
		//一週間の日付
		List<DayEntity> weekList = null;
		//4週間の日付
		List<List<DayEntity>> calendar = new ArrayList<List<DayEntity>>();
		//カレンダーを格納Model
		output = calendarService.setCalendar(output, firstDayOfMonth,
				lastDayOfMonth, firstDayOfCalendar,
				lastDayOfCalendar, calendar, weekList);
		model.addAttribute("output", output);
		return "calendar";
	}
	@RequestMapping(value = "schedule", method = RequestMethod.POST)
	public String schedule(String scheduledate, BindingResult result, Model model) {
		List<Schedule> schedule = scheduleService.selectAllByDate(scheduledate);
		model.addAttribute("selectAllByDateInfo", schedule);/* Modelに格納してhtmlに渡す */
		return "showSchedule";
	}
}
