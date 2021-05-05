package spring.schedule.controller;

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
 * @author thinh スケジュール情報Controller
 */
@Controller
@RequestMapping("calendar")
public class ShowCalendarController {
	/**
	 * カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;

	/**
	 * 本日の日付を取得
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping
	public String today(Model model) {
		LocalDate today = new LocalDate();
		int year = today.getYear();
		int month = today.getMonthOfYear();
		return date(year, month, model);
	}

	/**
	 *
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "date")
	public String date(@RequestParam("year") int year, @RequestParam("month") int month, Model model) {
		// カレンダーを格納Model
		CalendarOutput output = calendarService.getCalendarOutput(year, month);
		model.addAttribute("output", output);
		return "calendar";
	}

	@RequestMapping(value = "schedule")
	public String schedule(@RequestParam("id") Long id, Model model) {
		Schedule scheduleList = calendarService.selectAllById(id);
		model.addAttribute("scheduleList", scheduleList);
		//Modelに格納してhtmlに渡す
		return "showSchedule";
	}

}