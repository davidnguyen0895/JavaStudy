package spring.schedule.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.InsertScheduleEntity;
import spring.schedule.entity.Schedule;
import spring.schedule.entity.ScheduleRequest;

@Mapper
public interface SelectScheduleMapper{
	/**
	 *
	 * @return 全件検索結果
	 */
	List<Schedule> selectAll();
	/**
	 *
	 * @param scheduleSearchRequest
	 * @return IDで参照したスケジュールの情報
	 */
	Schedule selectById(ScheduleSearchRequest scheduleSearchRequest) ;
	/**
	 *日付で参照したスケジュールの情報
	 * @param scheduledate
	 * @return
	 */
	List<Schedule> selectAllByDate(String scheduledate);
	/**
	 *
	 * @param id
	 * @return
	 */
	Schedule selectAllById(Long id);
	/**
	 *
	 * @param schedule
	 * @return
	 */
	void insertNewSchedule(InsertScheduleEntity newSchedule);
}
