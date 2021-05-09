package spring.schedule.controller;

import org.joda.time.LocalDate;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.schedule.constants.ScheduleConstants;
import spring.schedule.entity.CalendarInfoEntity;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.service.CalendarService;

/**
 *	カレンダー表示コントローラー
 * @author thinh スケジュール情報Controller
 */
@RequestMapping(value="displayCalendar")
@Controller
public class DisplayCalendarController {
	/**
	 * カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;

	/**
	 * 本日の日付を取得
	 * org.joda.time.LocalDate
	 * @param model
	 * @return 本日の日付
	 */
	@RequestMapping
	public String today(Model model) {
		LocalDate today = new LocalDate();
		int year = today.getYear();
		int month = today.getMonthOfYear();
		return date(year, month, model);
	}

	/**
	 * カレンダー表示するための情報を格納して画面に渡す．
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "date")
	public String date(@RequestParam("year") int year, @RequestParam("month") int month, Model model) {
		// カレンダーを格納するインストタンス
		CalendarInfoEntity calendarInfo = calendarService.generateCalendarInfo(year, month);
		model.addAttribute("calendarInfo", calendarInfo);
		return ScheduleConstants.DISPLAY_CALENDAR;
	}
	/**
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "schedule")
	public String schedule(@RequestParam("id") Long id, Model model) {
		//リクエストデータのID情報を用いてスケージュール情報を参照する．
		ScheduleInfoEntity schedule = calendarService.selectAllById(id);
		//カレンダー表示画面に戻るための年と月のデータをdayEntityインストタンスに格納する．
		java.time.LocalDate calendarDate = java.time.LocalDate.ofInstant(schedule.getScheduledate().toInstant(), ZoneId.systemDefault());
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(calendarDate.getYear());
		dayEntity.setCalendarMonth(calendarDate.getMonthValue());
		//modelに格納してhtmlに渡す
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", schedule);
		return ScheduleConstants.SHOW_SCHEDULE_DETAIL;
	}
}