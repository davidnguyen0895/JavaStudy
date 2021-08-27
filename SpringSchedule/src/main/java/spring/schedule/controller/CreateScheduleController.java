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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
		schedule.setVersion(scheduleRequest.getVersion());
		return schedule;
	}

	/**
	 * スケジュール情報を削除するメソッド
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "deleteSchedule", method = RequestMethod.GET)
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
	@RequestMapping(value = "showScheduleForm", method = RequestMethod.GET)
	public String showNewScheduleForm(Model model) {
		// 今月の年と月の値をdayEntityインスタンスに格納する．
		ScheduleRequest scheduleRequest = new ScheduleRequest();
		DayEntity dayEntity = createDayEntityObject(Constants.TODAY);
		dayEntity.setCalendarYear(Constants.TODAY.getYear());
		dayEntity.setCalendarMonth(Constants.TODAY.getMonthValue());
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
		DayEntity dayEntity = createDayEntityObject(Constants.TODAY);
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
	 */
	@RequestMapping(value = "updateSchedule", method = RequestMethod.POST)
	public String createUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model) throws ExclusiveException {
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
		calendarService.updateSchedule(scheduleRequest);
		// 更新したスケジュール情報を取得
		ScheduleInfoEntity updateSchedule = calendarService.selectById(scheduleRequest.getId());
		// カレンダーに戻るボタンのための年と月の値を格納する．
		dayEntity.setAction(Constants.ACTION_UPDATE);
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", updateSchedule);
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
		DayEntity dayEntity = createDayEntityObject(LocalDate.now());
		List<String> errorList = new ArrayList<String>();
		errorList.add(ex.getMessage());
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("validationError", errorList);
		return Constants.RETURN_ERROR;
	}
}