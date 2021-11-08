package spring.schedule.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import spring.schedule.constants.Constants;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.service.CalendarService;

/**
 *
 * @author thinh スケジュール情報Controller
 */
@Controller
public class SelectAllController {
	/**
	 * スケジュール情報Serivce
	 */
	@Autowired
	CalendarService calendarService;

	/**
	 * ユーザー情報一覧画面を表示
	 * 
	 * @param model Model
	 * @return ユーザー情報一覧画面
	 */
	@GetMapping(value = "/selectAll")
	public String selectAll(Model model) {
		List<ScheduleInfoEntity> scheduleList = new ArrayList<ScheduleInfoEntity>();
		scheduleList = calendarService.selectAll();
		model.addAttribute("scheduleList", scheduleList);
		return Constants.RETURN_SELECT_ALL;
	}
}