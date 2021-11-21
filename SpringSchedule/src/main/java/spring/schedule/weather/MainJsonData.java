package spring.schedule.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MainJsonData {
	@Getter(onMethod_ = { @JsonProperty("lat") })
	@Setter(onMethod_ = { @JsonProperty("lat") })
	private double lat;
	@Getter(onMethod_ = { @JsonProperty("lon") })
	@Setter(onMethod_ = { @JsonProperty("lon") })
	private double lon;
	@Getter(onMethod_ = { @JsonProperty("timezone") })
	@Setter(onMethod_ = { @JsonProperty("timezone") })
	private String timezone;
	@Getter(onMethod_ = { @JsonProperty("timezone_offset") })
	@Setter(onMethod_ = { @JsonProperty("timezone_offset") })
	private long timezoneOffset;
	@Getter(onMethod_ = { @JsonProperty("daily") })
	@Setter(onMethod_ = { @JsonProperty("daily") })
	private List<Daily> daily;
}
