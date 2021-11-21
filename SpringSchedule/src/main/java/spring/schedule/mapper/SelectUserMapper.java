package spring.schedule.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import spring.schedule.entity.UserInfoEntity;

/**
 * スケージュール情報を参照するためのMapper
 * 
 * @author thinh
 *
 */
@Mapper
public interface SelectUserMapper {
	/**
	 * 
	 * @return 全件検索結果
	 */
	List<UserInfoEntity> selectAllUser();

	/**
	 * ユーザIDでユーザ情報を取得
	 * 
	 * @param userName
	 * @return
	 */
	UserInfoEntity selectUserId(String userName);

	/**
	 * ユーザ名でユーザ情報を取得
	 * 
	 * @param userName
	 * @return
	 */
	UserInfoEntity selectUserByUserName(String userName);
}