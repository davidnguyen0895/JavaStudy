package spring.schedule.controller;

import java.text.ParseException;
import java.time.LocalDate;
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
import spring.schedule.constants.Constants;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.service.CalendarService;

/**
 *
 * @author thinh スケジュール情報Controller
 */
@RequestMapping(value="schedule")
@Controller
public class CreateScheduleController {
	/**
	 * カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;
	/**
	 *
	 * @param scheduledate
	 * @return 日付で参照したスケージュール情報
	 */
	public List<ScheduleInfoEntity> selectAllByDate(LocalDate scheduledate) {
		return calendarService.selectByDate(scheduledate);
	}
	/**
	 *	スケジュール情報を削除するメソッド
	 * @param id
	 * @return
	 */
	@RequestMapping(value="deleteSchedule",method=RequestMethod.GET)
	public String deleteSchedule(@RequestParam("id") Long id) {
		calendarService.deleteSchedule(id);
		return Constants.REDIRECT_DISPLAY_CALENDAR;
	}
	/**
	 *
	 * @param scheduleRequest
	 * @param schedule
	 * @return
	 * @throws ParseException
	 */
	private ScheduleInfoEntity createSchedule(ScheduleRequest scheduleRequest){
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
	 * スケージュール情報を登録フォーム
	 * @param model
	 * @param today
	 * @return
	 */
	@RequestMapping(value="showScheduleForm", method=RequestMethod.GET)
	public String showNewScheduleForm(Model model) {
		//今月の年と月の値をdayEntityインスタンスに格納する．
		DayEntity dayEntity = createDayEntityObject(Constants.TODAY);
		dayEntity.setCalendarYear(Constants.TODAY.getYear());
		dayEntity.setCalendarMonth(Constants.TODAY.getMonthOfYear());
		//modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", new ScheduleRequest());
		return Constants.RETURN_CREATE_SCHEDULE_FORM;
	}
	/**
	 * スケジュール情報を登録するメソッド
	 * @param scheduleRequest
	 * @param model
	 * @return スケジュール情報詳細画面
	 * @throws ParseException
	 */
	@RequestMapping(value="createSchedule",method=RequestMethod.POST)
	public String createNewScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model){
		//日付情報インストタンス
		DayEntity dayEntity = createDayEntityObject(Constants.TODAY);
		//入力チェック
		if(result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for(ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("validationError", errorList);
			//入力した情報を残す
			model.addAttribute("schedule", scheduleRequest);
			return Constants.RETURN_CREATE_SCHEDULE_FORM;
		}
		//入力フォーム画面で入力した値をDBに登録する．
		calendarService.insertNewSchedule(scheduleRequest);
		ScheduleInfoEntity schedule = createSchedule(scheduleRequest);
		//日付データを用いてスケージュール情報リストを参照する．
		List<ScheduleInfoEntity> scheduleList = selectAllByDate(scheduleRequest.getScheduledate());
		//新規登録のスケージュール情報を作成してscheduleインストタンスに格納する．
		for(ScheduleInfoEntity scheduleObj : scheduleList) {
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
	 *	スケジュール情報を更新するフォーム
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value="showUpdateScheduleForm", method=RequestMethod.GET)
	public String showUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest, Model model){
		//今月の年と月の値をdayEntityインスタンスに格納する．
		DayEntity dayEntity = createDayEntityObject(Constants.TODAY);
		//modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", scheduleRequest);
		return Constants.RETURN_UPDATE_SCHEDULE_FORM;
	}
	/**
	 *	スケジュール情報を更新するメソッド
	 * @param scheduleRequest
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value="updateSchedule",method=RequestMethod.POST)
	public String createUpdateScheduleForm(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model){
		DayEntity dayEntity = createDayEntityObject(Constants.TODAY);
		//入力チェック
		if(result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for(ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("validationError", errorList);
			//入力した情報を残す
			model.addAttribute("schedule", scheduleRequest);
			return Constants.RETURN_UPDATE_SCHEDULE_FORM;
		}
		//入力フォーム画面で入力した値をDBに登録する．
		if( calendarService.updateSchedule(scheduleRequest) == false) {
			List<String> errorList = new ArrayList<String>();
			errorList.add("【更新失敗】他のユーザが利用しています．");
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("validationError", errorList);
			//入力した情報を残す
			model.addAttribute("schedule", scheduleRequest);
			return Constants.RETURN_UPDATE_SCHEDULE_FORM;
		}
		//更新したスケジュール情報を取得
		ScheduleInfoEntity updateSchedule = calendarService.selectById(scheduleRequest.getId());
		//カレンダーに戻るボタンのための年と月の値を格納する．
		LocalDate calendarDate = updateSchedule.getScheduledate();
		dayEntity.setCalendarYear(calendarDate.getYear());
		dayEntity.setCalendarMonth(calendarDate.getMonthValue());
		dayEntity.setAction(Constants.ACTION_UPDATE);
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", updateSchedule);
		return Constants.RETURN_SHOW_SCHEDULE_DETAIL;
	}
	/**
	 *	今日の日付オブジェクトを作成
	 * @param today
	 * @return
	 */
	private DayEntity createDayEntityObject(org.joda.time.LocalDate today) {
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(today.getYear());
		dayEntity.setCalendarMonth(today.getMonthOfYear());
		return dayEntity;
	}
}