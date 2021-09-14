package spring.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
	 * IDでスケージュール情報を検索する画面
	 *
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/selectById")
	public String displaySelectById(Model model) {
		return Constants.RETURN_SELECT_BY_ID;
	}

	/**
	 * IDでスケージュール情報を検索する
	 *
	 * @param scheduleSearchRequest
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/selectById")
	public String selectById(@Validated @ModelAttribute ScheduleRequest scheduleSearchRequest, BindingResult result,
			Model model) {
		ScheduleInfoEntity schedule = calendarService.selectById(scheduleSearchRequest);
		model.addAttribute("selectByIdInfo", schedule);/* Modelに格納してhtmlに渡す */
		return Constants.RETURN_SELECT_BY_ID;
	}
}
