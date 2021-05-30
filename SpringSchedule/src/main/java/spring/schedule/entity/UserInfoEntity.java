package spring.schedule.entity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/*スケジュール情報Entity*/
@Entity
@Data
@Table(name="usertable")
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
	@Column(name="pass")
	private String pass;
	/**
	 * ROLL
	 */
	@Column(name="roll")
	private int roll;
}
