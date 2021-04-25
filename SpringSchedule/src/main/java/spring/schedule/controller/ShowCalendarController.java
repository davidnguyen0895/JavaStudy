package spring.schedule.controller;

import java.util.List;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring.schedule.entity.CalendarOutput;
import spring.schedule.entity.Schedule;
import spring.schedule.service.CalendarService;

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

	@RequestMapping
	public String today(Model model) {
		LocalDate today = new LocalDate();
		int year = today.getYear();
		int month = today.getMonthOfYear();
		return date(year, month, model);
	}

	@RequestMapping(value = "date")
	public String date(@RequestParam("year") int year,@RequestParam("month") int month, Model model) {
		//一日
		LocalDate firstDayOfMonth =new LocalDate(year, month, 1);
		//カレンダーを格納Model
		 CalendarOutput output = calendarService.getCalendarOutput(firstDayOfMonth);
		model.addAttribute("output", output);
		return "calendar";
	}

	@RequestMapping(value = "schedule")
	public String schedule(@RequestParam("scheduledate") String scheduledate, Model model) {
		List<Schedule> scheduleList = calendarService.selectAllByDate(scheduledate);
		model.addAttribute("scheduleList", scheduleList);/* Modelに格納してhtmlに渡す */
		return "showSchedule";
	}
}