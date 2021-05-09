package spring.schedule.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class ScheduleSearchRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
}
