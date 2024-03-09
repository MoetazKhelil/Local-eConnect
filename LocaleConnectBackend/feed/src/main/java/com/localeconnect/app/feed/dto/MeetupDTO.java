package com.localeconnect.app.feed.dto;


//import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetupDTO {

    public interface CreateValidation {}

    public interface UpdateValidation{}

    private Long id;

    @NotNull(message = "This is a required field")
    private Long creatorId;

    @NotBlank(message = "This is a required field")
    private String name;

    @NotBlank(message = "This is a required field")
    private String description;

    @NotNull(message = "This is a required field")
    private Date date;

    @NotBlank(message = "This is a required field")
    private String startTime;

    @NotBlank(message = "This is a required field")
    private String endTime;

    @NotNull(message = "This is a required field")
    private Double cost;

    @NotBlank(message = "This is a required field")
    private String location;

    private List<String> spokenLanguages= new ArrayList<>();

    private List<Long> meetupAttendees = new ArrayList<>();
}
