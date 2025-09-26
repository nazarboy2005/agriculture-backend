package com.hackathon.agriculture_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NasaEtDto {
    
    @JsonProperty("parameters")
    private Parameters parameters;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {
        @JsonProperty("ET0")
        private EtData et0;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EtData {
        private List<Double> data;
        private String units;
    }
}



