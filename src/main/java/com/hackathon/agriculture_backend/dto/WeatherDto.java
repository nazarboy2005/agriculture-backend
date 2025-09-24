package com.hackathon.agriculture_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDto {
    
    private Double tempC;
    private Double humidity;
    private Double rainfallMm;
    private Double forecastRainfallMm;
    private Boolean heatAlert;
    private String weatherDescription;
    private Double etc; // Evapotranspiration
    
    @JsonProperty("current")
    private CurrentWeather current;
    
    @JsonProperty("daily")
    private List<DailyWeather> daily;
    
    @JsonProperty("alerts")
    private List<WeatherAlert> alerts;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentWeather {
        private Double temp;
        private Double humidity;
        private Double uvi;
        private Double windSpeed;
        private Rain rain; // 1h rainfall in mm
        private Double snow; // 1h snowfall in mm
        private List<Weather> weather;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rain {
        @JsonProperty("1h")
        private Double oneHour;
        
        public Double getOneHour() {
            return oneHour;
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyWeather {
        private Long dt;
        private Temp temp;
        private Double humidity;
        private Double rain;
        private List<Weather> weather;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Temp {
        private Double day;
        private Double min;
        private Double max;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weather {
        private String main;
        private String description;
        private String icon;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherAlert {
        @JsonProperty("sender_name")
        private String senderName;
        private String event;
        private Long start;
        private Long end;
        private String description;
        private List<String> tags;
    }
}
