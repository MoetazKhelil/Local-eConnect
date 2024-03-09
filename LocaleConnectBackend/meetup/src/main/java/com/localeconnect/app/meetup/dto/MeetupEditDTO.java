package com.localeconnect.app.meetup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetupEditDTO {

    private String name;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double cost;
    private String location;
    private List<String> spokenLanguages= new ArrayList<>();
}


