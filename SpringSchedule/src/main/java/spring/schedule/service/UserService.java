package spring.schedule.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import spring.schedule.entity.UserInfoEntity;
import spring.schedule.repository.SelectUserMapper;

/**
 * rollbackOn = Exception.class : 例外が発生した�?�合，ロールバックする?�?
 *
 * @author thinh カレンダー表示画面を作�?�するService
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class UserService implements UserDetailsService {
	@Autowired
	private SelectUserMapper selectUserMapper;

	/**
	 *
	 * @return 全件検索結果
	 */
	public List<UserInfoEntity> selectAllUser() {
		return selectUserMapper.selectAllUser();
	}

	/**
	 * ユーザ名でユーザオブジェクトを生�??
	 */
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		UserInfoEntity user = selectUserMapper.selectUserByUserName(userName);
		if (user == null) {
			throw new UsernameNotFoundException("ユーザ名�?" + userName + "が見つかりません?�?");
		}
		// 権限�?�リス�?
		List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
		GrantedAuthority authority = new SimpleGrantedAuthority("USER");
		grantList.add(authority);

		// rawDataのパスワード�?�渡すことができな�?ので、暗号�?
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// UserDetailsはインタフェースなのでUserクラスのコンストラクタで生�?�したユーザオブジェクトをキャス�?
		UserDetails userDetails = (UserDetails) new User(user.getUsername(), encoder.encode(user.getPass()), grantList);

		return userDetails;
	}
}