package spring.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import spring.schedule.constants.Constants;

/**
 *
 * @author thinh
 *スケジュール情報Controller
 */
@Controller
public class HomePageController {
	@GetMapping(value = "/index")
	public String displayHomePage(Model model) {
		return Constants.INDEX;
	}
}
