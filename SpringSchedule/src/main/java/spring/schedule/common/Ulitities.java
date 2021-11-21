package spring.schedule.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Ulitities {
	/**
	 * ログインユーザ名を取得
	 * @return
	 */
	public static String getLoginUserName() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}
	/**
	 * 本日の日付を取得
	 * @return
	 */
	public static String getStrDateNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
}
