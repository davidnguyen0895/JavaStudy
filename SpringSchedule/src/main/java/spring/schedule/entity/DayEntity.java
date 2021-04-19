package spring.schedule.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class DayEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String day;
	private String schedule;
}
