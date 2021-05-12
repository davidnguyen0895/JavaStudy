package spring.schedule.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.ScheduleInfoEntity;

/**
 * スケージュール情報を参照するためのMapper
 * @author thinh
 *
 */
@Mapper
public interface SelectScheduleMapper{
	/**
	 *
	 * @return 全件検索結果
	 */
	List<ScheduleInfoEntity> selectAll();
	/**
	 * IDでスケージュール情報を参照する画面
	 * @param scheduleSearchRequest
	 * @return IDで参照したスケジュールの情報
	 */
	ScheduleInfoEntity selectById(ScheduleSearchRequest scheduleSearchRequest) ;
	/**
	 *日付で参照したスケジュールの情報
	 * @param scheduledate
	 * @return
	 */
	List<ScheduleInfoEntity> selectAllByDate(String scheduledate);
	/**
	 * カレンダー表示画面
	 * @param id
	 * @return IDで参照したスケジュールの情報
	 */
	ScheduleInfoEntity selectAllById(Long id);
/**
 * スケージュール情報を登録する
 * @param newSchedule
 */
	void insertNewSchedule(ScheduleInfoEntity newSchedule);
	/**
	 * IDでスケージュール情報を削除する．
	 * @param id
	 */
	void deleteSchedule(Long id);
	/**
	 *更新
	 * @param id
	 */
	void updateSchedule(ScheduleInfoEntity updateSchedule);
}
