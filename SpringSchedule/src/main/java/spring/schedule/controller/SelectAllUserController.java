package spring.schedule.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import spring.schedule.constants.Constants;
import spring.schedule.entity.UserInfoEntity;
import spring.schedule.service.UserService;

/**
 *
 * @author thinh ユーザ情報Controller
 */
@Controller
public class SelectAllUserController {
	/**
	 * スケジュール情報Serivce
	 */
	@Autowired
	UserService userService;

	/**
	 * ユーザー情報一覧画面を表示
	 *
	 * @param model Model
	 * @return ユーザー情報一覧画面
	 */
	@GetMapping(value = "/selectAllUser")
	public String selectAllUser(Model model) {
		List<UserInfoEntity> userList = new ArrayList<UserInfoEntity>();
		userList = userService.selectAllUser();
		model.addAttribute("userList", userList);
		return Constants.RETURN_SELECT_ALL_USER;
	}
}
