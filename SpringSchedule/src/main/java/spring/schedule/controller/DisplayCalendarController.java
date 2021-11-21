package spring.schedule.controller;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.schedule.constants.Constants;
import spring.schedule.entity.CalendarInfoEntity;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.service.CalendarService;

/**
 * カレンダー表示コントローラー
 * 
 * @author thinh スケジュール情報Controller
 */
@RequestMapping(value = "displayCalendar")
@Controller
public class DisplayCalendarController {
	/**
	 * カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;

	/**
	 * 本日の日付を取得 org.joda.time.LocalDate
	 * 
	 * @param model
	 * @return 本日の日付
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping
	public String today(Model model) throws JSONException, IOException {
		org.joda.time.LocalDate today = new org.joda.time.LocalDate();
		return date(today.getYear(), today.getMonthOfYear(), model);
	}

	/**
	 * カレンダー表示するための情報を格納して画面に渡す．
	 * 
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping(value = "date")
	public String date(@RequestParam("year") int year, @RequestParam("month") int month, Model model)
			throws JSONException, IOException {
		// カレンダーを格納するインストタンス
		CalendarInfoEntity calendarInfo = calendarService.generateCalendarInfo(year, month);
		model.addAttribute("calendarInfo", calendarInfo);
		return Constants.RETURN_DISPLAY_CALENDAR;
	}

	/**
	 * スケージュール詳細を表示する。
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "schedule")
	public String showScheduleDetail(@RequestParam("id") Long id, Model model) {
		// リクエストデータのID情報を用いてスケージュール情報を参照する．
		ScheduleInfoEntity schedule = calendarService.selectById(id);
		// カレンダー表示画面に戻るための年と月のデータをdayEntityインストタンスに格納する．
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(schedule.getScheduledate().getYear());
		dayEntity.setCalendarMonth(schedule.getScheduledate().getMonthValue());
		dayEntity.setAction(Constants.ACTION_SEARCH);
		// modelに格納してhtmlに渡す
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", schedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}
}