package com.garanin.weather.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherModel {
    @JsonIgnore
    private String coord;
    @JsonIgnore
    private String weather;
    @JsonIgnore
    private String base;

    private Main main;
    @JsonIgnore
    private String visibility;
    @JsonIgnore
    private String wind;
    @JsonIgnore
    private String clouds;
    @JsonIgnore
    private String dt;
    @JsonIgnore
    private String sys;
    @JsonIgnore
    private String timezone;
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String name;
    @JsonIgnore
    private String cod;
}