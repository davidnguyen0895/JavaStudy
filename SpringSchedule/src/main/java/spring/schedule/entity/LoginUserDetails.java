package spring.schedule.entity;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
//equals()とhashCode()を生成するが親クラスのメソッドは呼び出さない
@EqualsAndHashCode(callSuper=false)
public class LoginUserDetails extends User {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//employeeテーブルから取得したオブジェクトを格納
	private final UserInfoEntity user;

	//認証処理
	public LoginUserDetails(UserInfoEntity user, String role) {
		//employeeテーブルの名前とパスワードでログイン認証を行う
		super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(role));
		this.user = user;
	}
}
