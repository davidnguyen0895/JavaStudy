package spring.schedule.entity;
import java.io.Serializable;
import java.util.Date;
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
@Table(name="users")
public class UserInfoEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ユーザID
	 */
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	/**
	 * ユーザ名
	 */
	@Column(name="username")
	private String username;
	/**
	 * パスワード
	 */
	@Column(name="password")
	private String password;
	/**
	 * 登録日
	 */
	@JsonFormat(pattern = "yyyy/MM/dd", timezone = "Asia/Tokyo")
	@Column(name="create_date")
	private Date create_date;
	/**
	 * 更新日
	 */
	@JsonFormat(pattern = "yyyy/MM/dd", timezone = "Asia/Tokyo")
	@Column(name="update_date")
	private Date update_date;
}
