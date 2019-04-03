package com.example.barvius.parsingxml.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationTagEntity {
    private String location;
    private String country;
    private String state;
    private String city;
    private double lat;
    private double lon;
}
