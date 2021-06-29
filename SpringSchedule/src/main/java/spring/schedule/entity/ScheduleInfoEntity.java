package spring.schedule.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/*スケジュール情報Entity*/
@Entity
@Data
@Table(name="schedule")
public class ScheduleInfoEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	@Id
	//@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	/**
	 * ユーザID
	 */
	//@Column(name="userid")
	private Long userid;
	/**
	 * スケジュール日付
	 */
	//@Column(name="scheduledate")
	//@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate scheduledate;
	/**
	 * 開始時間
	 */
	//@Column(name="starttime")
	//@DateTimeFormat(pattern = "HH:mm")
	private LocalTime starttime;
	/**
	 * 終了時間
	 */
	//@DateTimeFormat(pattern = "HH:mm")
	//@Column(name="endtime")
	private LocalTime endtime;
	/**
	 * スケジュール内容
	 */
	//@Column(name="schedule")
	private String schedule;
	/**
	 * メモ
	 */
	//@Column(name="schedulememo")
	private String schedulememo;
}
