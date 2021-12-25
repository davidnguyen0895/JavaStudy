package spring.schedule.weather;

import java.util.HashMap;

import lombok.Data;

@Data
public class WeatherData {
	private HashMap<org.joda.time.LocalDate, String> iconMap;
	private HashMap<org.joda.time.LocalDate, String> descriptionMap;
}
