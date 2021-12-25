package spring.schedule.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

/*スケジュール情報Entity*/
@Entity
@Data
@Table(name = "usertable")
public class UserInfoEntity implements UserDetails {
	private static final long serialVersionUID = 1L;
	/**
	 * ユーザID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * ユーザ名
	 */
	private String user;
	/**
	 * パスワード
	 */
	private String pass;
	/**
	 * ROLL
	 */
	private int role;

	// 権限をCollectionで返す。
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList(String.valueOf(this.role));
	}

	@Override
	public String getPassword() {
		return this.pass;
	}

	@Override
	public String getUsername() {
		return this.user;
	}

	@Override
	// アカウントが有効期限内であるか
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	// アカウントがロックされていないか
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	// 資格情報が有効期限内であるか
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	// 有効なアカウントであるか
	public boolean isEnabled() {
		return true;
	}
}