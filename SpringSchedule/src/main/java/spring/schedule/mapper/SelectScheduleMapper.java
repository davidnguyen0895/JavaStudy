package spring.schedule.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import spring.schedule.entity.ScheduleInfoEntity;
import spring.schedule.entity.ScheduleRequest;

/**
 * スケージュール情報を参照するためのMapper
 * 
 * @author thinh
 *
 */
@Mapper
public interface SelectScheduleMapper {
	/**
	 *
	 * @return 全件検索結果
	 */
	List<ScheduleInfoEntity> selectAll();

	/**
	 * 日付で参照したスケジュールの情報
	 * 
	 * @param scheduledate
	 * @return
	 */
	List<ScheduleInfoEntity> selectByDate(String scheduledate);

	/**
	 * カレンダー表示画面
	 * 
	 * @param id
	 * @return IDで参照したスケジュールの情報
	 */
	ScheduleInfoEntity selectById(Long id);

	/**
	 *
	 * @param scheduleRequest
	 * @return
	 */
	ScheduleInfoEntity selectById(ScheduleRequest scheduleRequest);

	/**
	 *
	 * @param userId
	 * @return
	 */
	List<ScheduleInfoEntity> selectByUserId(Long userId, String scheduledate);

	Long selectLatestId();

	/**
	 *
	 * @return
	 */
	ScheduleInfoEntity selectScheduleUpdatedate(Long id);

	/**
	 * スケージュール情報を登録する
	 * 
	 * @param newSchedule
	 */
	void insertNewSchedule(ScheduleInfoEntity newSchedule);

	/**
	 * 更新
	 * 
	 * @param updateSchedule
	 */
	void updateSchedule(ScheduleInfoEntity updateSchedule);

	/**
	 * IDでスケージュール情報を削除する．
	 * 
	 * @param id
	 */
	void deleteSchedule(Long id);
}