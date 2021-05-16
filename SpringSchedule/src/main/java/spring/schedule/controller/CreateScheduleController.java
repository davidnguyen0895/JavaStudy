package spring.schedule.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
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
	//日付変換定数 : yyyy/MM/dd
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	//日付変換定数 : hh:mm
	private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
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
	public List<ScheduleInfoEntity> selectAllByDate(String scheduledate) {
		return calendarService.selectAllByDate(scheduledate);
	}
	/**
	 * スケージュール情報を作成する．
	 * @param scheduleRequest
	 * @return スケージュール情報オブジェクト
	 * @throws ParseException
	 */
	private ScheduleInfoEntity CreateSchedule(ScheduleRequest scheduleRequest, ScheduleInfoEntity schedule) throws ParseException {
		java.sql.Time convertedStarttime = new java.sql.Time(timeFormat.parse(scheduleRequest.getStarttime()).getTime());
		java.sql.Time convertedEndtime = new java.sql.Time(timeFormat.parse(scheduleRequest.getEndtime()).getTime());
		java.util.Date convertedUtilScheduleDate = dateFormat.parse(scheduleRequest.getScheduledate());
		schedule.setScheduledate(convertedUtilScheduleDate);
		schedule.setStarttime(convertedStarttime);
		schedule.setEndtime(convertedEndtime);
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		return schedule;
	}
	/**
	 * スケジュール登録フォーム画面を表示する．
	 * @param year
	 * @param month
	 * @param model
	 * @return スケージュール作成フォーム
	 */
	@RequestMapping(value="showScheduleForm", method=RequestMethod.GET)
	public String showNewScheduleForm(Model model, LocalDate today) {
		//今月の年と月の値をdayEntityインスタンスに格納する．
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(today.getYear());
		dayEntity.setCalendarMonth(today.getMonthOfYear());
		//modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", new ScheduleRequest());
		return Constants.CREATE_SCHEDULE_FORM;
	}
	/**
	 * スケジュール情報を登録するメソッド
	 * @param scheduleRequest
	 * @param model
	 * @return スケジュール情報詳細画面
	 * @throws ParseException
	 */
	@RequestMapping(value="createSchedule",method=RequestMethod.POST)
	public String createNewSchedule(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			BindingResult result, Model model, LocalDate today) throws ParseException {
		ScheduleInfoEntity schedule = new ScheduleInfoEntity();
		//日付情報インストタンス
		DayEntity dayEntity = new DayEntity();
		//入力チェック
		if(result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for(ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			//カレンダーに戻るための日付情報
			dayEntity.setCalendarYear(today.getYear());
			dayEntity.setCalendarMonth(today.getMonthOfYear());
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("validationError", errorList);
			//入力した情報を残す
			model.addAttribute("schedule", scheduleRequest);
			return Constants.CREATE_SCHEDULE_FORM;
		}
		else {//入力エラーがない場合
			//入力フォーム画面で入力した値をDBに登録する．
			calendarService.insertNewSchedule(scheduleRequest);
			//日付データを用いてスケージュール情報リストを参照する．
			List<ScheduleInfoEntity> scheduleList = selectAllByDate(scheduleRequest.getScheduledate());
			//新規登録のスケージュール情報を作成してscheduleインストタンスに格納する．
			for(ScheduleInfoEntity scheduleObj : scheduleList) {
				schedule.setId(scheduleObj.getId());
				schedule.setUserid(scheduleObj.getUserid());
			}
			CreateSchedule(scheduleRequest, schedule);
			//カレンダーに戻るボタンのための年と月の値を格納する．
			LocalDate calendarDate = DateTimeFormat.forPattern("yyyy/MM/dd").parseLocalDate(scheduleRequest.getScheduledate());
			dayEntity.setCalendarYear(calendarDate.getYear());
			dayEntity.setCalendarMonth(calendarDate.getMonthOfYear());
			dayEntity.setAction(Constants.ACTION_REGIST);
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("schedule", schedule);
			return Constants.SHOW_SCHEDULE_DETAIL;
		}
	}
	/**
	 *	スケジュール情報を更新するフォーム
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value="showUpdateScheduleForm", method=RequestMethod.GET)
	public String showUpdateScheduleForm(@RequestParam("id") Long id,
			@RequestParam("userid") int userid,
			@RequestParam("scheduledate") String scheduledate,
			@RequestParam("starttime") String starttime,
			@RequestParam("endtime") String endtime,
			@RequestParam("schedule") String schedule,
			@RequestParam("schedulememo") String schedulememo,
			LocalDate today, Model model) throws ParseException {
		//今月の年と月の値をdayEntityインスタンスに格納する．
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(today.getYear());
		dayEntity.setCalendarMonth(today.getMonthOfYear());
		//更新情報のリクエストデータ
		ScheduleRequest updateSchedule = new ScheduleRequest();
		updateSchedule.setId(id);
		updateSchedule.setUserid(userid);
		updateSchedule.setScheduledate(scheduledate);
		updateSchedule.setStarttime(starttime);
		updateSchedule.setEndtime(endtime);
		updateSchedule.setSchedule(schedule);
		updateSchedule.setSchedulememo(schedulememo);
		//modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", updateSchedule);
		return Constants.UPDATE_SCHEDULE_FORM;
	}
	/**
	 *	スケジュール情報を更新するメソッド
	 * @param scheduleRequest
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value="updateSchedule",method=RequestMethod.POST)
	public String updateSchedule(@Validated @ModelAttribute ScheduleRequest scheduleRequest,
			LocalDate today, BindingResult result, Model model) throws ParseException {
		DayEntity dayEntity = new DayEntity();
		//日付情報インストタンス
		ScheduleInfoEntity updateSchedule = new ScheduleInfoEntity();
		//入力チェック
		if(result.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for(ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			dayEntity.setCalendarYear(today.getYear());
			dayEntity.setCalendarMonth(today.getMonthOfYear());
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("validationError", errorList);
			//入力した情報を残す
			model.addAttribute("schedule", scheduleRequest);
			return Constants.UPDATE_SCHEDULE_FORM;
		}else {
			//入力フォーム画面で入力した値をDBに登録する．
			calendarService.updateSchedule(scheduleRequest);
			updateSchedule.setId(scheduleRequest.getId());
			updateSchedule.setUserid(scheduleRequest.getUserid());
			//更新したスケジュール情報を取得
			CreateSchedule(scheduleRequest, updateSchedule);
			//カレンダーに戻るボタンのための年と月の値を格納する．
			LocalDate calendarDate = DateTimeFormat.forPattern("yyyy/MM/dd").parseLocalDate(scheduleRequest.getScheduledate());
			dayEntity.setCalendarYear(calendarDate.getYear());
			dayEntity.setCalendarMonth(calendarDate.getMonthOfYear());
			dayEntity.setAction(Constants.ACTION_UPDATE);
			model.addAttribute("dayEntity", dayEntity);
			model.addAttribute("schedule", updateSchedule);
			return Constants.SHOW_SCHEDULE_DETAIL;
		}
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
}