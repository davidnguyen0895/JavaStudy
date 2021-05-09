package spring.schedule.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spring.schedule.constants.ScheduleConstants;
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.InsertScheduleEntity;
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
	public List<ScheduleInfoEntity> selectAllByDate(String scheduledate) {
		return calendarService.selectAllByDate(scheduledate);
	}
	/**
	 * スケジュール登録フォーム画面を表示する．
	 * @param year
	 * @param month
	 * @param model
	 * @return スケージュール作成フォーム
	 */
	@RequestMapping(value="showScheduleForm", method=RequestMethod.GET)
	public String showNewScheduleForm(Model model) {
		//今月の年と月の値をdayEntityインスタンスに格納する．
		DayEntity dayEntity = new DayEntity();
		LocalDate today = new LocalDate();
		int year = today.getYear();
		int month = today.getMonthOfYear();
		dayEntity.setCalendarYear(year);
		dayEntity.setCalendarMonth(month);
		//modelにdayEntityとScheduleRequestのインスタンスを格納し，スケージュール作成フォームに送信する．
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", new ScheduleRequest());
		return ScheduleConstants.CREATE_SCHEDULE_FORM;
	}
	/**
	 *
	 * @param scheduleRequest
	 * @param model
	 * @return スケージュール情報を登録した内容を表示する画面を作成する．
	 * @throws ParseException
	 */
	@RequestMapping(value="createSchedule",method=RequestMethod.POST)
	public String createNewSchedule(@ModelAttribute ScheduleRequest scheduleRequest, Model model) throws ParseException {
		//入力フォーム画面で入力した値をDBに登録する．
		calendarService.insertNewSchedule(scheduleRequest);
		//日付情報インストタンス
		DayEntity dayEntity = new DayEntity();
		//日付データを用いてスケージュール情報リストを参照する．
		List<ScheduleInfoEntity> scheduleList = selectAllByDate(scheduleRequest.getScheduledate());
		//スケージュール情報リストからスケージュールIDをIDリストに格納
		for(ScheduleInfoEntity schedule : scheduleList) {
			dayEntity.setId(schedule.getId());
		}
		//新規登録のスケージュール情報を作成してscheduleインストタンスに格納する．
		InsertScheduleEntity schedule = new InsertScheduleEntity();
		schedule = CreateSchedule(scheduleRequest);
		schedule.setId(dayEntity.getId());
		//カレンダーに戻るボタンのための年と月の値を格納する．
		java.time.LocalDate calendarDate = java.time.LocalDate.ofInstant(schedule.getScheduledate().toInstant(), ZoneId.systemDefault());
		dayEntity.setCalendarYear(calendarDate.getYear());
		dayEntity.setCalendarMonth(calendarDate.getMonthValue());
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", schedule);
		return ScheduleConstants.CREATE_SCHEDULE_RESULT;
	}
	/**
	 * スケージュール情報を作成する．
	 * @param scheduleRequest
	 * @return スケージュール情報オブジェクト
	 * @throws ParseException
	 */
	private InsertScheduleEntity CreateSchedule(ScheduleRequest scheduleRequest) throws ParseException {
		InsertScheduleEntity schedule = new InsertScheduleEntity();
		java.sql.Time convertedStarttime = new java.sql.Time(ScheduleConstants.timeFormat.parse(scheduleRequest.getStarttime()).getTime());
		java.sql.Time convertedEndtime = new java.sql.Time(ScheduleConstants.timeFormat.parse(scheduleRequest.getEndtime()).getTime());
		java.util.Date convertedUtilScheduleDate = ScheduleConstants.dateFormat.parse(scheduleRequest.getScheduledate());
		String startTimeStamp = scheduleRequest.getScheduledate() + ScheduleConstants.SPACE + convertedStarttime.toString();
		String endTimeStamp = scheduleRequest.getScheduledate() + ScheduleConstants.SPACE + convertedEndtime.toString();
		Date resultStartTime = (Date) ScheduleConstants.timeStampFormat.parse(startTimeStamp);
		Date resultEndTime = (Date) ScheduleConstants.timeStampFormat.parse(endTimeStamp);
		schedule.setUserid(3);
		schedule.setScheduledate(convertedUtilScheduleDate);
		schedule.setStarttime(resultStartTime);
		schedule.setEndtime(resultEndTime);
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		return schedule;
	}
}