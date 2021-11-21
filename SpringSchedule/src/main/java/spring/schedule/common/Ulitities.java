package spring.schedule.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ulitities {

	/**
	 * 
	 * @return
	 */
	public static String getStrDateNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
