package spring.schedule.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.Schedule;

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
	List<Schedule> selectAllByDate(String scheduledate) ;
}
