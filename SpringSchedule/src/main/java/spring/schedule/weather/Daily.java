package spring.schedule.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Daily {
	@Getter(onMethod_ = { @JsonProperty("dt") })
	@Setter(onMethod_ = { @JsonProperty("dt") })
	private long dt;
	@Getter(onMethod_ = { @JsonProperty("sunrise") })
	@Setter(onMethod_ = { @JsonProperty("sunrise") })
	private long sunrise;
	@Getter(onMethod_ = { @JsonProperty("sunset") })
	@Setter(onMethod_ = { @JsonProperty("sunset") })
	private long sunset;
	@Getter(onMethod_ = { @JsonProperty("moonrise") })
	@Setter(onMethod_ = { @JsonProperty("moonrise") })
	private long moonrise;
	@Getter(onMethod_ = { @JsonProperty("moonset") })
	@Setter(onMethod_ = { @JsonProperty("moonset") })
	private long moonset;
	@Getter(onMethod_ = { @JsonProperty("moon_phase") })
	@Setter(onMethod_ = { @JsonProperty("moon_phase") })
	private double moonPhase;
	@Getter(onMethod_ = { @JsonProperty("temp") })
	@Setter(onMethod_ = { @JsonProperty("temp") })
	private Temp temp;
	@Getter(onMethod_ = { @JsonProperty("feels_like") })
	@Setter(onMethod_ = { @JsonProperty("feels_like") })
	private FeelsLike feelsLike;
	@Getter(onMethod_ = { @JsonProperty("pressure") })
	@Setter(onMethod_ = { @JsonProperty("pressure") })
	private long pressure;
	@Getter(onMethod_ = { @JsonProperty("humidity") })
	@Setter(onMethod_ = { @JsonProperty("humidity") })
	private long humidity;
	@Getter(onMethod_ = { @JsonProperty("dew_point") })
	@Setter(onMethod_ = { @JsonProperty("dew_point") })
	private double dewPoint;
	@Getter(onMethod_ = { @JsonProperty("wind_speed") })
	@Setter(onMethod_ = { @JsonProperty("wind_speed") })
	private double windSpeed;
	@Getter(onMethod_ = { @JsonProperty("wind_deg") })
	@Setter(onMethod_ = { @JsonProperty("wind_deg") })
	private long windDeg;
	@Getter(onMethod_ = { @JsonProperty("wind_gust") })
	@Setter(onMethod_ = { @JsonProperty("wind_gust") })
	private double windGust;
	@Getter(onMethod_ = { @JsonProperty("weather") })
	@Setter(onMethod_ = { @JsonProperty("weather") })
	private List<Weather> weather;
	@Getter(onMethod_ = { @JsonProperty("clouds") })
	@Setter(onMethod_ = { @JsonProperty("clouds") })
	private long clouds;
	@Getter(onMethod_ = { @JsonProperty("pop") })
	@Setter(onMethod_ = { @JsonProperty("pop") })
	private double pop;
	@Getter(onMethod_ = { @JsonProperty("uvi") })
	@Setter(onMethod_ = { @JsonProperty("uvi") })
	private double uvi;
	@Getter(onMethod_ = { @JsonProperty("rain") })
	@Setter(onMethod_ = { @JsonProperty("rain") })
	private Double rain;
}
