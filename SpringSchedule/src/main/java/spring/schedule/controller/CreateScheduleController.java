package spring.schedule.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.schedule.constants.Constants;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.exception.ExclusiveException;
import spring.schedule.service.CalendarService;

/**
 * スケージュール情報コントローラー
 *
 * @author thinh スケジュール情報Controller
 */
@RequestMapping(value = "schedule")
@Controller
public class CreateScheduleController {

	@Autowired
	CalendarService calendarService;

	/**
	 * スケージュール情報を格納する
	 *
	 * @param scheduleRequest
	 * @return schedule
	 */
	private ScheduleInfoEntity createSchedule(ScheduleRequest scheduleRequest) {
		ScheduleInfoEntity schedule = new ScheduleInfoEntity();
		schedule.setScheduledate(scheduleRequest.getScheduledate());
		schedule.setStarttime(scheduleRequest.getStarttime());
		schedule.setEndtime(scheduleRequest.getEndtime());
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		schedule.setUpdateday(scheduleRequest.getUpdateday());
		return schedule;
	}

	/**
	 * スケジュール情報を削除するメソッド
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "deleteSchedule")
	public String deleteSchedule(@RequestParam("id") Long id) {
		calendarService.deleteSchedule(id);
		return Constants.REDIRECT_DISPLAY_CALENDAR;
	}

	/**
	 * スケージュール情報を登録フォーム
	 *
	 * @param model
	 * @return
	 */
	@GetMapping(value = "showScheduleForm")
	public String showNewScheduleForm(Model model) {
		// 今月の年と月の値をdayEntityインスタンスに格納する．
		ScheduleRequest scheduleRequest = new ScheduleRequest();
		DayEntity dayEntity = createDayEntityObject(java.time.LocalDate.now());
		// modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute(Constants.ATTRIBUTE_DAYENTITY, dayEntity);
		model.addAttribute(Constants.ATTRIBUTE_SCHEDULE, scheduleRequest);
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
	@GetMapping(value = "createSchedule")
	public String createNewScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model) {
		// 日付情報インストタンス
		DayEntity dayEntity = createDayEntityObject(java.time.LocalDate.now());
		// 入力チェック
		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute(Constants.ATTRIBUTE_DAYENTITY, dayEntity);
			model.addAttribute("validationError", errorList);
			// 入力した情報を残す
			model.addAttribute(Constants.ATTRIBUTE_SCHEDULE, scheduleRequest);
			return Constants.RETURN_CREATE_SCHEDULE_FORM;
		}
		// 入力フォーム画面で入力した値をDBに登録する．
		calendarService.insertNewSchedule(scheduleRequest);
		ScheduleInfoEntity schedule = createSchedule(scheduleRequest);
		// 日付データを用いてスケージュール情報リストを参照する．
		List<ScheduleInfoEntity> scheduleList = calendarService.selectByDate(scheduleRequest.getScheduledate());
		// 新規登録のスケージュール情報を作成してscheduleインストタンスに格納する．
		for (ScheduleInfoEntity scheduleObj : scheduleList) {
			schedule.setId(scheduleObj.getId());
			schedule.setUserid(scheduleObj.getUserid());
		}
		dayEntity.setCalendarYear(schedule.getScheduledate().getYear());
		dayEntity.setCalendarMonth(schedule.getScheduledate().getMonthValue());
		dayEntity.setAction(Constants.ACTION_REGIST);
		model.addAttribute(Constants.ATTRIBUTE_DAYENTITY, dayEntity);
		model.addAttribute(Constants.ATTRIBUTE_SCHEDULE, schedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}

	/**
	 * スケジュール情報を更新するフォーム
	 *
	 * @param scheduleRequest
	 * @param model
	 * @return
	 */
	@GetMapping(value = "showUpdateScheduleForm")
	public String showUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest, Model model) {
		// 今月の年と月の値をdayEntityインスタンスに格納する．
		DayEntity dayEntity = createDayEntityObject(scheduleRequest.getScheduledate());
		dayEntity.setCalendarYear(scheduleRequest.getScheduledate().getYear());
		dayEntity.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
		// modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute(Constants.ATTRIBUTE_DAYENTITY, dayEntity);
		model.addAttribute(Constants.ATTRIBUTE_SCHEDULE, scheduleRequest);
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
	 */
	@PostMapping(value = "updateSchedule")
	public String updateSchedule(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model) throws ExclusiveException {
		DayEntity dayEntity = createDayEntityObject(scheduleRequest.getScheduledate());
		dayEntity.setCalendarYear(scheduleRequest.getScheduledate().getYear());
		dayEntity.setCalendarMonth(scheduleRequest.getScheduledate().getMonthValue());
		// 入力チェック
		if (result.hasErrors()) {
			List<String> errorList = new ArrayList<>();
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute(Constants.ATTRIBUTE_DAYENTITY, dayEntity);
			model.addAttribute("validationError", errorList);
			// 入力した情報を残す
			model.addAttribute(Constants.ATTRIBUTE_SCHEDULE, scheduleRequest);
			return Constants.RETURN_UPDATE_SCHEDULE_FORM;
		}
		calendarService.updateSchedule(scheduleRequest);
		// 更新したスケジュール情報を取得
		ScheduleInfoEntity updateSchedule = calendarService.selectById(scheduleRequest.getId());
		// カレンダーに戻るボタンのための年と月の値を格納する．
		dayEntity.setAction(Constants.ACTION_UPDATE);
		model.addAttribute(Constants.ATTRIBUTE_DAYENTITY, dayEntity);
		model.addAttribute(Constants.ATTRIBUTE_SCHEDULE, updateSchedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}

	/**
	 * 今日の日付オブジェクトを作成
	 *
	 * @param date
	 * @return
	 */
	private DayEntity createDayEntityObject(java.time.LocalDate date) {
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(date.getYear());
		dayEntity.setCalendarMonth(date.getMonthValue());
		return dayEntity;
	}

	/**
	 * 悲観排他例外ハンドラ
	 *
	 * @param ex
	 * @param scheduleRequest
	 * @param model
	 * @return
	 */
	@ExceptionHandler(ExclusiveException.class)
	public String exclusiveExceptionHandler(ExclusiveException ex, Model model) {
		// カレンダーに戻るボタン用の日付
		DayEntity dayEntity = createDayEntityObject(LocalDate.now());
		model.addAttribute(Constants.ATTRIBUTE_DAYENTITY, dayEntity);
		model.addAttribute("errorMessage", ex.getMessage());
		return Constants.RETURN_ERROR;
	}
}