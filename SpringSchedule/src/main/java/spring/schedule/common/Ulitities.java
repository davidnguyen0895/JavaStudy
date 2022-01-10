package spring.schedule.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Ulitities {
	/**
	 * ログインユーザ名を取得
	 * 
	 * @return
	 */
	public static String getLoginUserName() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}
}
