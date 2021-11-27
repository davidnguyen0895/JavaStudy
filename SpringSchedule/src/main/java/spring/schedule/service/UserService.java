package spring.schedule.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import spring.schedule.entity.UserInfoEntity;
import spring.schedule.mapper.SelectUserMapper;

/**
 * rollbackOn = Exception.class : 例外が発生した場合，ロールバックする．
 * 
 * @author thinh カレンダー表示画面を作成するService
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

	@Override
	public UserInfoEntity loadUserByUsername(String userName) throws UsernameNotFoundException {
		UserInfoEntity userInfo = selectUserMapper.selectUserByUserName(userName);
		if (userInfo == null) {
			throw new UsernameNotFoundException("ユーザ名：" + userName + "が見つかりません．");
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		userInfo.setPass(encoder.encode(userInfo.getPassword()));

		// UserInfoEntityを返す
		return userInfo;
	}
}