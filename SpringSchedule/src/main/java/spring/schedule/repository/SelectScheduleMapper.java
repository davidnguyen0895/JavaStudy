package spring.schedule.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.Schedule;

@Mapper
public interface SelectScheduleMapper{
/**
 *
 * @return
 */
	List<Schedule> selectAll();
	/**
	 *
	 * @param scheduleSearchRequest
	 * @return
	 */
	Schedule selectById(ScheduleSearchRequest scheduleSearchRequest) ;
	/**
	 *
	 * @param scheduleSearchRequest
	 * @return
	 */
	List<Schedule> selectByDate(String scheduledate) ;
}
