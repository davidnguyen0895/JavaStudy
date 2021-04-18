package spring.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.Schedule;
import spring.schedule.service.ScheduleService;

/**
 *
 * @author thinh
 *スケジュール情報Controller
 */
@Controller
public class SelectByIdController {
	/**
	 * スケジュール情報Serivce
	 */
	@Autowired
	ScheduleService scheduleService;

	@GetMapping(value = "/selectById")
	public String displaySelectById(Model model) {
		return "selectById";
	}

	/**
	 *
	 * @param scheduleSearchRequest
	 * @param result
	 * @param model
	 * @return スケジュール情報一覧画面
	 */
	@RequestMapping(value = "/selectById", method = RequestMethod.POST)
	public String selectById(@Validated @ModelAttribute ScheduleSearchRequest scheduleSearchRequest,
			BindingResult result, Model model) {
		Schedule schedule = scheduleService.selectById(scheduleSearchRequest);
		model.addAttribute("selectByIdInfo", schedule);/* Modelに格納してhtmlに渡す */
		return "selectById";
	}
}
