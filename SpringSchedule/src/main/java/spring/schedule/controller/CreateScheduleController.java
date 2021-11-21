package spring.schedule.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	private static final Logger logger = LoggerFactory.getLogger(CreateScheduleController.class);
	/**
	 * カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;

	/**
	 * スケジュール情報を削除するメソッド
	 * 
	 * @param id
	 * @return
	 */

	@RequestMapping(value = "deleteSchedule", method = RequestMethod.GET)
	public String deleteSchedule(@Validated @ModelAttribute ScheduleRequest scheduleRequest, Model model) {
		DayEntity dayEntity = createDayEntityObject(scheduleRequest.getScheduledate());
		dayEntity.setCalendarYear(scheduleRequest.getScheduledate().getYear());
		dayEntity.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
		ScheduleInfoEntity scheduleInfo = calendarService.selectById(scheduleRequest.getId());

		if (scheduleInfo == null) {
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute(Constants.ERROR_MESSAGE, "スケジュールが既に削除されています。");
			return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
		}
		calendarService.deleteSchedule(scheduleInfo.getId());
		return Constants.REDIRECT_DISPLAY_CALENDAR;
	}

	/**
	 * スケージュール情報を登録フォーム
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "showScheduleForm", method = RequestMethod.GET)
	public String showNewScheduleForm(Model model) {
		// 今月の年と月の値をdayEntityインスタンスに格納する．
		ScheduleRequest scheduleRequest = new ScheduleRequest();
		DayEntity dayEntity = createDayEntityObject(LocalDate.now());
		dayEntity.setCalendarYear(LocalDate.now().getYear());
		dayEntity.setCalendarMonth(LocalDate.now().getMonthValue());
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
	@RequestMapping(value = "createSchedule", method = RequestMethod.POST)
	public String createNewScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model) {
		// 日付情報インストタンス
		DayEntity dayEntity = createDayEntityObject(LocalDate.now());
		// 入力チェック
		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
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
		calendarService.insertNewSchedule(scheduleRequest);
		ScheduleInfoEntity schedule = calendarService.createScheduleFromRequest(scheduleRequest);
		// 日付データを用いてスケージュール情報リストを参照する．
		dayEntity.setCalendarYear(schedule.getScheduledate().getYear());
		dayEntity.setCalendarMonth(schedule.getScheduledate().getMonthValue());
		dayEntity.setAction(Constants.ACTION_REGIST);
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
	public String showUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest, Model model) {
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
	@RequestMapping(value = "updateSchedule", method = RequestMethod.POST)
	public String createUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model) throws ExclusiveException, RollBackException {
		DayEntity dayEntity = createDayEntityObject(scheduleRequest.getScheduledate());
		dayEntity.setCalendarYear(scheduleRequest.getScheduledate().getYear());
		dayEntity.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
		// 入力チェック
		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
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
		calendarService.updateSchedule(scheduleRequest);
		// 更新したスケジュール情報を取得
		ScheduleInfoEntity updateSchedule = calendarService.selectById(scheduleRequest.getId());

		StringBuilder updateScheduleInfo = new StringBuilder();
		updateScheduleInfo.append("ID : " + updateSchedule.getId());
		updateScheduleInfo.append("日付 : " + updateSchedule.getScheduledate());
		updateScheduleInfo.append("開始時間 : " + updateSchedule.getStarttime());
		updateScheduleInfo.append("終了時間 : " + updateSchedule.getId());
		updateScheduleInfo.append("内容 : " + updateSchedule.getSchedule());
		updateScheduleInfo.append("メモ : " + updateSchedule.getSchedulememo());

		logger.info(updateSchedule.toString());

		// カレンダーに戻るボタンのための年と月の値を格納する．
		dayEntity.setAction(Constants.ACTION_UPDATE);
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", updateSchedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}

	/**
	 * 今日の日付オブジェクトを作成
	 * 
	 * @param today
	 * @return
	 */
	private DayEntity createDayEntityObject(java.time.LocalDate date) {
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(date.getYear());
		dayEntity.setCalendarMonth(date.getMonthValue());
		return dayEntity;
	}
}