package spring.schedule.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class DayEntity implements Serializable{
	private Long id;
	private String day;
	private String schedule;
}
