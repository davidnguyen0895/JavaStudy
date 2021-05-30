package spring.schedule.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import spring.schedule.entity.UserInfoEntity;

/**
 * スケージュール情報を参照するためのMapper
 * @author thinh
 *
 */
@Mapper
public interface SelectUserMapper{
	/**
	 *
	 * @return 全件検索結果
	 */
	List<UserInfoEntity> selectAllUser();
	/**
	 *
	 * @param userName
	 * @return
	 */
	UserInfoEntity selectUserId(String userName);
	/**
	 *
	 * @param userName
	 * @return
	 */
	UserInfoEntity selectUserByUserName(String userName);
}
