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
public class LocationModel {
    private String name;
    @JsonIgnore
    private String local_names;
    private double lat;
    private double lon;
    private String country;
    private String state;
}