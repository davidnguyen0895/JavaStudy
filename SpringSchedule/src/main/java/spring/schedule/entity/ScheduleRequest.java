package spring.schedule.entity;

import java.io.Serializable;
import lombok.Data;

/**
 *スケージュール情報 リクエストデータ
 * @author thinh
 *
 */
@Data
public class ScheduleRequest implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int userid;
	private String scheduledate;
	private String starttime;
	private String endtime;
	private String schedule;
	private String schedulememo;
}


