package spring.schedule.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Weather {
	@Getter(onMethod_ = { @JsonProperty("id") })
	@Setter(onMethod_ = { @JsonProperty("id") })
	private long id;
	@Getter(onMethod_ = { @JsonProperty("main") })
	@Setter(onMethod_ = { @JsonProperty("main") })
	private String main;
	@Getter(onMethod_ = { @JsonProperty("description") })
	@Setter(onMethod_ = { @JsonProperty("description") })
	private String description;
	@Getter(onMethod_ = { @JsonProperty("icon") })
	@Setter(onMethod_ = { @JsonProperty("icon") })
	private String icon;
}
