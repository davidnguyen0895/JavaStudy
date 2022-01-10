package spring.schedule.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.schedule.common.Ulitities;
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

	@Autowired
	HttpSession session;

	String userName = Constants.EMPTY;

	/**
	 * 本日の日付を取得 org.joda.time.LocalDate
	 * 
	 * @param model
	 * @return 本日の日付
	 */
	@RequestMapping
	public String today(Model model) {
		org.joda.time.LocalDate today = new org.joda.time.LocalDate();
		return createCalendar(today.getYear(), today.getMonthOfYear(), model);
	}

	/**
	 * カレンダー表示するための情報を格納して画面に渡す．
	 * 
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "createCalendar")
	public String createCalendar(@RequestParam("year") int year, @RequestParam("month") int month, Model model) {
		if (this.session.getAttribute("userName") != null) {
			this.userName = String.valueOf(this.session.getAttribute("userName"));
		}
		this.session.setAttribute("currentYear", year);
		this.session.setAttribute("currentMonth", month);
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(year);
		dayEntity.setCalendarMonth(month);
		model.addAttribute("dayEntity", dayEntity);
		// カレンダーを格納するインストタンス
		CalendarInfoEntity calendarInfo = this.calendarService.generateCalendarInfo(year, month, this.userName);
		model.addAttribute("calendarInfo", calendarInfo);
		return Constants.RETURN_DISPLAY_CALENDAR;
	}

	/**
	 * セッションに変数を格納する。
	 * 
	 * @param displayMode
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "setSessionAttribute")
	public String setSessionAttribute(@RequestParam("displayMode") String displayMode, Model model) {
		if (displayMode.equals("loginUser")) {
			this.session.setAttribute("userName", Ulitities.getLoginUserName());
			this.userName = String.valueOf(this.session.getAttribute("userName"));
		}
		if (displayMode.equals("allUser")) {
			this.session.setAttribute("userName", Constants.EMPTY);
			this.userName = Constants.EMPTY;
		}
		int currentYear = Integer.parseInt(String.valueOf(this.session.getAttribute("currentYear")));
		int currentMonth = Integer.parseInt(String.valueOf(this.session.getAttribute("currentMonth")));
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(currentYear);
		dayEntity.setCalendarMonth(currentMonth);
		model.addAttribute("dayEntity", dayEntity);

		CalendarInfoEntity calendarInfo = this.calendarService.generateCalendarInfo(
				Integer.parseInt(String.valueOf(this.session.getAttribute("currentYear"))),
				Integer.parseInt(String.valueOf(this.session.getAttribute("currentMonth"))), this.userName);
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
	@SuppressWarnings("boxing")
	@RequestMapping(value = "schedule")
	public String showScheduleDetail(@RequestParam("id") Long id, Model model) {
		// リクエストデータのID情報を用いてスケージュール情報を参照する．
		ScheduleInfoEntity schedule = this.calendarService.selectById(id);
		// カレンダー表示画面に戻るための年と月のデータをdayEntityインストタンスに格納する．
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(schedule.getScheduledate().getYear());
		dayEntity.setCalendarMonth(schedule.getScheduledate().getMonthValue());
		dayEntity.setAction(Constants.ACTION_SEARCH);
		// 操作制限：自分のスケジュール以外は更新不可。
		if (!schedule.getUsername().equals(Ulitities.getLoginUserName())) {
			schedule.setChangeAllowedFlg(false);
		} else {
			schedule.setChangeAllowedFlg(true);
		}
		// modelに格納してhtmlに渡す
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", schedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}
}