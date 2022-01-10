package spring.schedule.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import spring.schedule.common.Ulitities;
import spring.schedule.constants.Constants;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.exception.ExclusiveException;
import spring.schedule.exception.RollBackException;
import spring.schedule.service.CalendarService;

/**
 *
 * @author thinh スケジュール情報Controller
 */
@RequestMapping(value = "schedule")
@Controller
public class CreateScheduleController {
	/**
	 * カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;

	/**
	 * スケージュール情報を登録フォーム
	 * @param year
	 * @param month
	 * @param model
	 * @return RETURN_CREATE_SCHEDULE_FORM
	 */
	@RequestMapping(value = "showNewScheduleForm", method = RequestMethod.GET)
	public static String showNewScheduleForm(@RequestParam("year") int year, @RequestParam("month") int month,
			Model model) {
		// 今月の年と月の値をdayEntityインスタンスに格納する．
		ScheduleRequest scheduleRequest = new ScheduleRequest();
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(year);
		dayEntity.setCalendarMonth(month);
		// modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", scheduleRequest);
		return Constants.RETURN_CREATE_SCHEDULE_FORM;
	}

	/**
	 * スケジュール情報を登録するメソッド
	 * 
	 * @param scheduleRequest
	 * @param result
	 * @param model
	 * @return
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "createSchedule", method = RequestMethod.POST)
	public String createNewScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model) {
		// 日付情報インストタンス
		DayEntity dayEntity = createDayEntityObject(LocalDate.now());
		// 入力チェック
		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("validationError", errorList);
			// 入力した情報を残す
			model.addAttribute("schedule", scheduleRequest);
			return Constants.RETURN_CREATE_SCHEDULE_FORM;
		}
		// 入力フォーム画面で入力した値をDBに登録する．
		scheduleRequest.setUpdatedate(LocalDateTime.now());
		this.calendarService.insertNewSchedule(scheduleRequest);
		ScheduleInfoEntity schedule = this.calendarService.createScheduleFromRequest(scheduleRequest,
				Constants.ACTION_REGIST);
		// 日付データを用いてスケージュール情報リストを参照する．
		dayEntity.setCalendarYear(schedule.getScheduledate().getYear());
		dayEntity.setCalendarMonth(schedule.getScheduledate().getMonthValue());
		dayEntity.setAction(Constants.ACTION_REGIST);
		// 操作制限：自分のスケジュール以外は更新不可。
		if (!schedule.getUsername().equals(Ulitities.getLoginUserName())) {
			schedule.setChangeAllowedFlg(false);
		} else {
			schedule.setChangeAllowedFlg(true);
		}
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", schedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}

	/**
	 * スケジュール情報を更新するフォーム
	 * 
	 * @param scheduleRequest
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "showUpdateScheduleForm", method = RequestMethod.GET)
	public static String showUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			Model model) {
		// 今月の年と月の値をdayEntityインスタンスに格納する．
		DayEntity dayEntity = createDayEntityObject(scheduleRequest.getScheduledate());
		dayEntity.setCalendarYear(scheduleRequest.getScheduledate().getYear());
		dayEntity.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
		// modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", scheduleRequest);
		return Constants.RETURN_UPDATE_SCHEDULE_FORM;
	}

	/**
	 * スケジュール情報を更新するメソッド
	 * 
	 * @param scheduleRequest
	 * @param result
	 * @param model
	 * @return
	 * @throws ExclusiveException
	 * @throws RollBackException
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "updateSchedule", method = RequestMethod.POST)
	public String createUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model) throws ExclusiveException, RollBackException {
		DayEntity dayEntity = createDayEntityObject(scheduleRequest.getScheduledate());
		dayEntity.setCalendarYear(scheduleRequest.getScheduledate().getYear());
		dayEntity.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
		// 入力チェック
		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("validationError", errorList);
			// 入力した情報を残す
			model.addAttribute("schedule", scheduleRequest);
			return Constants.RETURN_UPDATE_SCHEDULE_FORM;
		}
		// 入力フォーム画面で入力した値をDBに登録する．
		this.calendarService.updateSchedule(scheduleRequest);
		// 更新したスケジュール情報を取得
		ScheduleInfoEntity updateSchedule = this.calendarService.selectById(scheduleRequest.getId());
		// カレンダーに戻るボタンのための年と月の値を格納する．
		dayEntity.setAction(Constants.ACTION_UPDATE);
		// 操作制限：自分のスケジュール以外は更新不可。
		if (!updateSchedule.getUsername().equals(Ulitities.getLoginUserName())) {
			updateSchedule.setChangeAllowedFlg(false);
		} else {
			updateSchedule.setChangeAllowedFlg(true);
		}
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", updateSchedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}

	/**
	 * スケジュール情報を削除するメソッド
	 * 
	 * @param scheduleRequest
	 * @param model
	 * @return 実行結果
	 */
	@RequestMapping(value = "deleteSchedule", method = RequestMethod.GET)
	public String deleteSchedule(@Validated @ModelAttribute ScheduleRequest scheduleRequest, Model model) {
		DayEntity dayEntity = createDayEntityObject(scheduleRequest.getScheduledate());
		dayEntity.setCalendarYear(scheduleRequest.getScheduledate().getYear());
		dayEntity.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
		ScheduleInfoEntity scheduleInfo = this.calendarService.selectById(scheduleRequest.getId());

		if (scheduleInfo == null) {
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute(Constants.ERROR_MESSAGE, "スケジュールが既に削除されています。");
			return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
		}
		this.calendarService.deleteSchedule(scheduleInfo.getId());
		return Constants.REDIRECT_DISPLAY_CALENDAR;
	}

	/**
	 * 今日の日付オブジェクトを作成
	 * 
	 * @param today
	 * @return
	 */
	private static DayEntity createDayEntityObject(java.time.LocalDate date) {
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(date.getYear());
		dayEntity.setCalendarMonth(date.getMonthValue());
		return dayEntity;
	}
}