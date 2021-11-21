package spring.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import spring.schedule.common.Ulitities;
import spring.schedule.constants.Constants;

/**
 *
 * @author thinh スケジュール情報Controller
 */
@Slf4j
@Controller
public class HomePageController {
	@RequestMapping("/index")
	private String init(Model model) {
		log.info(Ulitities.getLoginUserName() + "さんがログインしました。");
		model.addAttribute("username", Ulitities.getLoginUserName());
		return Constants.RETURN_INDEX;
	}
}