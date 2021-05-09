package spring.schedule.entity;

import java.io.Serializable;
import java.util.Date;
import java.sql.Time;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
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
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	/**
	 * ユーザID
	 */
	@Column(name="userid")
	private int userid;
	/**
	 * スケジュール日付
	 */
	@JsonFormat(pattern = "yyyy/MM/dd", timezone = "Asia/Tokyo")
	@Column(name="scheduledate")
	private Date scheduledate;
	/**
	 * 開始時間
	 */
	@Column(name="starttime")
	private Time starttime;
	/**
	 * 終了時間
	 */
	@Column(name="endtime")
	private Time endtime;
	/**
	 * スケジュール内容
	 */
	@Column(name="schedule")
	private String schedule;
	/**
	 * メモ
	 */
	@Column(name="schedulememo")
	private String schedulememo;
}
