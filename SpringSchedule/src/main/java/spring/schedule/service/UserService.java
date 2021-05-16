package spring.schedule.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.schedule.entity.UserInfoEntity;
import spring.schedule.repository.SelectUserMapper;

/**
 * rollbackOn = Exception.class : 例外が発生した場合，ロールバックする．
 * @author thinh
 * カレンダー表示画面を作成するService
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class UserService {
	@Autowired
	private SelectUserMapper selectUserMapper;
	/**
	 *
	 * @return 全件検索結果
	 */
	public List<UserInfoEntity> selectAllUser() {
		return selectUserMapper.selectAllUser();
	}
}