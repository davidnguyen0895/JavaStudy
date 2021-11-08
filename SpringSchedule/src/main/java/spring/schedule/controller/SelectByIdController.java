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

import spring.schedule.constants.Constants;
import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;
import spring.schedule.service.CalendarService;

/**
 *
 * @author thinh スケジュール情報Controller
 */
@Controller
public class SelectByIdController {
	/**
	 * スケジュール情報Serivce
	 */
	@Autowired
	CalendarService calendarService;

	/**
	 *
	 * @param model
	 * @return
	 */
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
	public String selectById(@Validated @ModelAttribute ScheduleRequest scheduleSearchRequest, BindingResult result,
			Model model) {
		ScheduleInfoEntity schedule = calendarService.selectById(scheduleSearchRequest);
		model.addAttribute("selectByIdInfo", schedule);/* Modelに格納してhtmlに渡す */
		return Constants.RETURN_SELECT_BY_ID;
	}
}