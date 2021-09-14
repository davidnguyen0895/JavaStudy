package spring.schedule.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	List<ScheduleInfoEntity> selectByDate(LocalDate scheduledate);

	/**
	 * IDでスケージュール情報を検索する
	 * 
	 * @param id
	 * @return
	 */
	ScheduleInfoEntity selectById(Long id);

	/**
	 * IDでスケージュール情報を検索する
	 * 
	 * @param scheduleRequest
	 * @return
	 */
	ScheduleInfoEntity selectById(ScheduleRequest scheduleRequest);

	/**
	 * ユーザIDでスケージュール情報を検索する
	 *
	 * @param userId
	 * @return
	 */
	List<ScheduleInfoEntity> selectByUserId(Long userId, String scheduledate);

	/**
	 * スケジュール更新日を取得する．
	 *
	 * @return
	 */
	LocalDateTime selectUpdateDay(Long id);

	/**
	 * スケージュール情報を登録する
	 *
	 * @param newSchedule
	 */
	void insertNewSchedule(ScheduleInfoEntity newSchedule);

	/**
	 * スケージュール情報を更新する
	 *
	 * @param id
	 */
	void updateSchedule(ScheduleInfoEntity updateSchedule);

	/**
	 * IDでスケージュール情報を削除する．
	 *
	 * @param id
	 */
	void deleteSchedule(Long id);
}
