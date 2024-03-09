package com.localeconnect.app.meetup.controller;

import com.localeconnect.app.meetup.dto.MeetupAttendDTO;
import com.localeconnect.app.meetup.dto.MeetupDTO;
import com.localeconnect.app.meetup.dto.MeetupEditDTO;
import com.localeconnect.app.meetup.response_handler.ResponseHandler;
import com.localeconnect.app.meetup.service.MeetupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/meetup")
public class MeetupController {

    private final MeetupService meetupService;

    @PostMapping("/create")
    public ResponseEntity<Object> createMeetup(@RequestBody @Valid MeetupDTO meetupDTO) {
        MeetupDTO createdMeetupDTO = meetupService.createMeetup(meetupDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.CREATED, createdMeetupDTO, null);
    }
    @GetMapping("/all")
    public ResponseEntity<Object> getAllMeetups() {
        List<MeetupDTO> meetupDTOS = meetupService.getAllMeetups();
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, meetupDTOS, null);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getMeetupById(@PathVariable("id") Long id) {
        MeetupDTO meetupDTO = meetupService.getMeetupById(id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, meetupDTO, null);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateMeetup(@PathVariable("id") Long id, @RequestBody MeetupEditDTO meetupEditDTO) {
        MeetupDTO updatedMeetup = meetupService.updateMeetup(meetupEditDTO, id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, updatedMeetup, null);
    }

    @PostMapping("/{id}/attend")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> attendMeetup(@PathVariable("id") Long meetupId, @RequestBody @Valid MeetupAttendDTO meetupAttendDTO) {
        meetupService.attendMeetup(meetupId, meetupAttendDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, "Traveller added to meetup attendees successfully!", null);
    }

    @PostMapping("/{id}/unattend")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> unattendMeetup(@PathVariable("id") Long meetupId, @RequestBody @Valid MeetupAttendDTO meetupAttendDTO) {
        meetupService.unattendMeetup(meetupId, meetupAttendDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, "Traveller removed from meetup attendees successfully!", null);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Object> deleteMeetupById(@PathVariable("id") Long id) {
        MeetupDTO deletedMeetupDTO = meetupService.deleteMeetupById(id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, deletedMeetupDTO, null);
    }
    @GetMapping(path = "/allByUser/{userId}")
    public ResponseEntity<Object> getMeetupsByUserId(@PathVariable("userId") Long userId) {
        List<MeetupDTO> meetups = meetupService.getMeetupsByUserId(userId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, meetups, null);
    }
    @PostMapping("/share/{meetupId}")
    public ResponseEntity<Object> shareMeetup(@PathVariable("meetupId") Long meetupId,
                                              @RequestParam("authorId") @Valid Long authorId) {
        String res = meetupService.shareMeetup(meetupId, authorId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, res, null);
    }

    @PostMapping("/{meetupId}/rate/{userId}")
    public ResponseEntity<Object> rateMeetup(@PathVariable("meetupId") Long meetupId,
                                           @PathVariable("userId") Long userId,
                                           @RequestParam("rating") Double rating) {
        MeetupDTO ratedMeetup = meetupService.rateMeetup(meetupId, userId, rating);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, ratedMeetup, null);
    }

    @GetMapping("/{meetupId}/rating")
    public ResponseEntity<Object> getAverageRatingOfMeetup(@PathVariable("meetupId") Long meetupId) {
        double averageRating = meetupService.getAverageRatingOfMeetup(meetupId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, averageRating, null);
    }

    @GetMapping("/{meetupId}/rating-count")
    public ResponseEntity<Object> getRatingCountOfItinerary(@PathVariable("meetupId") Long meetupId) {
        int ratingCount = meetupService.getRatingCountOfMeetup(meetupId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, ratingCount, null);
    }
    @GetMapping(path = "/search")
    public ResponseEntity<Object> searchMeetup(@RequestParam("name") String name) {
        List<MeetupDTO> meetups = meetupService.searchByName(name);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, meetups, null);
    }

}
