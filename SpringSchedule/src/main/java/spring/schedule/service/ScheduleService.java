package spring.schedule.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.schedule.dto.ScheduleSearchRequest;
import spring.schedule.entity.Schedule;
import spring.schedule.repository.SelectScheduleMapper;

@Service
@Transactional(rollbackOn = Exception.class)
public class ScheduleService {
	/**
	 * IDで検索するMapper
	 */
	@Autowired
	private SelectScheduleMapper selectScheduleMapper;

	/**
	 * ユーザ情報検索
	 * @param scheduleSearchRequest
	 * @return IDで検索結果
	 */
	public Schedule selectById(ScheduleSearchRequest scheduleSearchRequest) {
		return selectScheduleMapper.selectById(scheduleSearchRequest);
	}
	/**
	 *
	 * @return 全件検索結果
	 */
	public List<Schedule> selectAll(){
		return selectScheduleMapper.selectAll();
	}
	/**
	 *
	 * @param scheduleSearchRequest
	 * @return
	 */
	public List<Schedule> selectByDate(String scheduledate) {
		return selectScheduleMapper.selectByDate(scheduledate);
	}
}
