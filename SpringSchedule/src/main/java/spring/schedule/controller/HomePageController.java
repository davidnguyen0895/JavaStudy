package spring.schedule.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import spring.schedule.constants.Constants;

/**
 *
 * @author thinh
 *スケジュール情報Controller
 */
@Controller
public class HomePageController {
	@RequestMapping("/index")
	private String init(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//Principalからログインユーザの情報を取得
		String userName = auth.getName();
		model.addAttribute("username", userName);
		return Constants.RETURN_INDEX;
	}
}
