package spring.schedule.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class FeelsLike {
    @lombok.Getter(onMethod_ = {@JsonProperty("day")})
    @lombok.Setter(onMethod_ = {@JsonProperty("day")})
    private double day;
    @lombok.Getter(onMethod_ = {@JsonProperty("night")})
    @lombok.Setter(onMethod_ = {@JsonProperty("night")})
    private double night;
    @lombok.Getter(onMethod_ = {@JsonProperty("eve")})
    @lombok.Setter(onMethod_ = {@JsonProperty("eve")})
    private double eve;
    @lombok.Getter(onMethod_ = {@JsonProperty("morn")})
    @lombok.Setter(onMethod_ = {@JsonProperty("morn")})
    private double morn;
}
