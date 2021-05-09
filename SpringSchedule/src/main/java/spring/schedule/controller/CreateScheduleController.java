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
import spring.schedule.entity.DayEntity;
import spring.schedule.entity.InsertScheduleEntity;
import spring.schedule.entity.Schedule;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.service.CalendarService;

/**
 *
 * @author thinh スケジュール情報Controller
 */
@RequestMapping(value="schedule")
@Controller
public class CreateScheduleController {
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
	private final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	/**
	 * カレンダー作成Service
	 */
	@Autowired
	CalendarService calendarService;
	/**
	 *
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@RequestMapping(value="showScheduleForm", method=RequestMethod.GET)
	public String showNewScheduleForm(Model model) {
		DayEntity dayEntity = new DayEntity();
		LocalDate today = new LocalDate();
		int year = today.getYear();
		int month = today.getMonthOfYear();
		dayEntity.setCalendarYear(year);
		dayEntity.setCalendarMonth(month);
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", new ScheduleRequest());
		return "createScheduleForm";
	}
	/**
	 *
	 * @param scheduleRequest
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value="createSchedule",method=RequestMethod.POST)
	public String createNewSchedule(@ModelAttribute ScheduleRequest scheduleRequest, Model model) throws ParseException {
		calendarService.insertNewSchedule(scheduleRequest);
		//日付Model
		DayEntity dayEntity = new DayEntity();
		//スケージュールリスト
		List<Schedule> scheduleList = selectAllByDate(scheduleRequest.getScheduledate());
		//スケージュールがあった日付のスケージュールIDをIDリストに格納
		for(Schedule schedule : scheduleList) {
			dayEntity.setId(schedule.getId());
		}
		//カレンダーに戻る処理
		InsertScheduleEntity schedule = new InsertScheduleEntity();
		schedule = CreateSchedule(scheduleRequest);
		schedule.setId(dayEntity.getId());
		java.time.LocalDate calendarDate = java.time.LocalDate.ofInstant(schedule.getScheduledate().toInstant(), ZoneId.systemDefault());
		dayEntity.setCalendarYear(calendarDate.getYear());
		dayEntity.setCalendarMonth(calendarDate.getMonthValue());
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute("schedule", schedule);
		return "createScheduleResult";
	}
	/**
	 *
	 * @param scheduleRequest
	 * @return
	 * @throws ParseException
	 */
	private InsertScheduleEntity CreateSchedule(ScheduleRequest scheduleRequest) throws ParseException {
		InsertScheduleEntity schedule = new InsertScheduleEntity();
		java.sql.Time convertedStarttime = new java.sql.Time(timeFormat.parse(scheduleRequest.getStarttime()).getTime());
		java.sql.Time convertedEndtime = new java.sql.Time(timeFormat.parse(scheduleRequest.getEndtime()).getTime());
		java.util.Date convertedUtilScheduleDate = dateFormat.parse(scheduleRequest.getScheduledate());
		String stringStartTime = convertedStarttime.toString();
		String stringEndTime = convertedEndtime.toString();
		String startTimeStamp = scheduleRequest.getScheduledate() + " " + stringStartTime;
		String endTimeStamp = scheduleRequest.getScheduledate() + " " + stringEndTime;
		Date resultStartTime = (Date) timeStampFormat.parse(startTimeStamp);
		Date resultEndTime = (Date) timeStampFormat.parse(endTimeStamp);
		schedule.setUserid(3);
		schedule.setScheduledate(convertedUtilScheduleDate);
		schedule.setStarttime(resultStartTime);
		schedule.setEndtime(resultEndTime);
		schedule.setSchedule(scheduleRequest.getSchedule());
		schedule.setSchedulememo(scheduleRequest.getSchedulememo());
		return schedule;
	}
	/**
	 *
	 * @param scheduledate
	 * @return
	 */
	public List<Schedule> selectAllByDate(String scheduledate) {
		return calendarService.selectAllByDate(scheduledate);
		}
}