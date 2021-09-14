package spring.schedule.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	/**
	 * ユーザID
	 */
	private Long userid;
	/**
	 * スケジュール日付
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate scheduledate;
	/**
	 * 開始時間
	 */
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime starttime;
	/**
	 * 終了時間
	 */
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime endtime;
	/**
	 * スケジュール内容
	 */
	private String schedule;
	/**
	 * メモ
	 */
	private String schedulememo;
	/**
	 * スケジュール更新日
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime updateday;
}
