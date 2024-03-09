package com.localeconnect.app.trip.controller;

import com.localeconnect.app.trip.dto.TripAttendDTO;
import com.localeconnect.app.trip.dto.TripDTO;
import com.localeconnect.app.trip.dto.TripReviewDTO;
import com.localeconnect.app.trip.response_handler.ResponseHandler;
import com.localeconnect.app.trip.service.TripService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/trip")
public class TripController {
    private final TripService tripService;

    @PostMapping("/create")
    public ResponseEntity<Object> createTrip(@RequestBody @Valid TripDTO tripDTO) {
        TripDTO trip = tripService.createTrip(tripDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, trip, null);
    }
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTrips() {
        List<TripDTO> trips = tripService.getAllTrips();
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, trips, null);
    }

    @GetMapping("/{trip_id}")
    public ResponseEntity<Object> getTripById(@PathVariable ("trip_id") Long tripId) {
        TripDTO trip = tripService.getById(tripId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, trip, null);
    }
    @GetMapping(path = "/allByLocalguide/{localguideId}")
    public ResponseEntity<Object> getLocalguideTrips(@PathVariable("localguideId") Long localguideId) {
        List<TripDTO> trips = tripService.getAllTripsByLocalguide(localguideId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, trips, null);
    }

    @PutMapping("/update/{trip_id}")
    public ResponseEntity<Object> updateTrip(@PathVariable ("trip_id") Long tripId,
                                             @RequestBody TripDTO tripDTO) {
        TripDTO trip = tripService.updateTrip(tripId, tripDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, trip,null);
    }
    @DeleteMapping("/delete/{trip_id}")
    public ResponseEntity<Object> deleteTrip(@PathVariable ("trip_id") Long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, null,null);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchTrip(@RequestParam("name") String tripName) {
        TripDTO trip = tripService.searchTrip(tripName);
        return ResponseHandler.generateResponse("success!", HttpStatus.OK, trip, null);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterTrip(@RequestParam(value = "destination", required = false) String destination,
                                             @RequestParam(value = "traveltime", required = false) Double traveltime,
                                             @RequestParam(value = "language", required = false) List<String> languages) {
        List<TripDTO> tripsFiltered = tripService.filter(destination, traveltime, languages);
        return ResponseHandler.generateResponse("success!", HttpStatus.OK, tripsFiltered, null);
    }
    @PostMapping("/create-review")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createReview(@RequestBody @Valid TripReviewDTO tripReviewDto) {
        TripReviewDTO review = tripService.createReview(tripReviewDto, tripReviewDto.getUserId(), tripReviewDto.getTripId());
        return ResponseHandler.generateResponse("success!", HttpStatus.OK, review, null);
    }
    @PutMapping(path = "/update-review/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable("id") Long id, @RequestBody @Valid TripReviewDTO tripReviewDTO) {
        TripReviewDTO review = tripService.updateReview(tripReviewDTO, id);
        return ResponseHandler.generateResponse("success!", HttpStatus.OK, review, null);
    }

    @DeleteMapping(path = "/delete-review/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable("id") Long id) {
        tripService.deleteReview(id);
        return ResponseHandler.generateResponse("success!", HttpStatus.OK, null, null);
    }

    @GetMapping(path = "/all-reviews/{tripId}")
    public ResponseEntity<Object> getAllReviews(@PathVariable("tripId") Long tripId) {
        List<TripReviewDTO> reviews = tripService.getAllReviewsForTrip(tripId);
        return ResponseHandler.generateResponse("success!", HttpStatus.OK, reviews, null);
    }
    @PostMapping("/share/{tripId}")
    public ResponseEntity<Object> shareTrip(@PathVariable("tripId") Long tripId,
                                            @RequestParam("authorId") @Valid Long authorId) {
        String res = tripService.shareTrip(tripId, authorId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, res, null);
    }

    @PostMapping("/{tripId}/rate/{userId}")
    public ResponseEntity<Object> rateTrip(@PathVariable("tripId") Long tripId,
                                           @PathVariable("userId") Long userId,
                                           @RequestParam("rating") Double rating) {
        TripDTO ratedTrip = tripService.rateTrip(tripId, userId, rating);
        return ResponseHandler.generateResponse("Successfully rated the trip!", HttpStatus.OK, ratedTrip, null);
    }
    @GetMapping("/{tripId}/rating")
    public ResponseEntity<Object> getAverageRatingOfTrip(@PathVariable("tripId") Long tripId) {
        double averageRating = tripService.getAverageRatingOfTrip(tripId);

        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, averageRating, null);
    }

    @GetMapping("/{tripId}/rating-count")
    public ResponseEntity<Object> getRatingCountOfTrip(@PathVariable("tripId") Long tripId) {
        int ratingCount = tripService.getRatingCountOfTrip(tripId);

        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, ratingCount, null);
    }
    @PostMapping("/{id}/attend")
    public ResponseEntity<Object> attendTrip(@PathVariable("id") Long tripId, @RequestBody @Valid TripAttendDTO tripAttendDTO) {
        tripService.attendTrip(tripId, tripAttendDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, "Traveller added to trip attendees successfully!", null);
    }

    @PostMapping("/{id}/unattend")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> unattendTrip(@PathVariable("id") Long tripId, @RequestBody @Valid TripAttendDTO tripAttendDTO) {
        tripService.unattendTrip(tripId, tripAttendDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, "Traveller removed from trip attendees successfully!", null);
    }


}
