package spring.schedule.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import spring.schedule.entity.Schedule;
import spring.schedule.service.ScheduleService;

/**
 *
 * @author thinh
 *スケジュール情報Controller
 */
@Controller
public class SelectAllController {
	/**
	 * スケジュール情報Serivce
	 */
	@Autowired
	ScheduleService scheduleService;

	/**
	 * ユーザー情報一覧画面を表示
	 * @param model Model
	 * @return ユーザー情報一覧画面
	 */
	@GetMapping(value = "/selectAll")
	public String selectAll(Model model) {
		List<Schedule> scheduleList = new ArrayList<Schedule>();
		scheduleList = scheduleService.selectAll();
		model.addAttribute("scheduleList", scheduleList);
		return "selectAll";
	}
}
