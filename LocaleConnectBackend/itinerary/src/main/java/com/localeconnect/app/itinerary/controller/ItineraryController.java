package com.localeconnect.app.itinerary.controller;

import com.localeconnect.app.itinerary.dto.ItineraryAttendDTO;
import com.localeconnect.app.itinerary.dto.ItineraryDTO;
import com.localeconnect.app.itinerary.dto.ReviewDTO;
import com.localeconnect.app.itinerary.dto.Tag;
import com.localeconnect.app.itinerary.response_handler.ResponseHandler;
import com.localeconnect.app.itinerary.service.ItineraryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
@AllArgsConstructor
public class ItineraryController {
    private final ItineraryService itineraryService;

    @PostMapping("/create")
    public ResponseEntity<Object> createItinerary(@RequestBody @Valid ItineraryDTO itineraryDTO) {
        ItineraryDTO itinerary = itineraryService.createItinerary(itineraryDTO, itineraryDTO.getUserId());
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, itinerary, null);
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<Object> updateItinerary(@PathVariable("id") Long id, @RequestBody @Valid ItineraryDTO itineraryDTO) {
        ItineraryDTO itinerary = itineraryService.updateItinerary(itineraryDTO, id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, itinerary, null);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Object> deleteItinerary(@PathVariable("id") Long id) {
        itineraryService.deleteItinerary(id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, null, null);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getItineraryById(@PathVariable("id") Long id) {
        ItineraryDTO itinerary = itineraryService.getItineraryById(id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, itinerary, null);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllItineraries() {
        List<ItineraryDTO> itineraries = itineraryService.getAllItineraries();
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, itineraries, null);
    }

    @GetMapping(path = "/allByUser/{userId}")
    public ResponseEntity<Object> getUserItineraries(@PathVariable("userId") Long userId) {
        List<ItineraryDTO> itineraries = itineraryService.getAllItinerariesByUser(userId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, itineraries, null);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> searchItineraries(@RequestParam("name") String name) {
        List<ItineraryDTO> itineraries = itineraryService.searchByName(name);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, itineraries, null);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterItineraries(
            @RequestParam(value = "place", required = false) String place,
            @RequestParam(value = "tag", required = false) Tag tag,
            @RequestParam(value = "days", required = false) Integer days) {
        List<ItineraryDTO> itineraries = itineraryService.filter(place, tag, days);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, itineraries, null);
    }

    @PostMapping("/create-review")
    public ResponseEntity<Object> createReview(@RequestBody @Valid ReviewDTO reviewDto) {
        ReviewDTO review = itineraryService.createReview(reviewDto, reviewDto.getUserId(), reviewDto.getItineraryId());
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, review, null);
    }

    @PutMapping(path = "/update-review/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable("id") Long id, @RequestBody @Valid ReviewDTO reviewDTO) {
        ReviewDTO review = itineraryService.updateReview(reviewDTO, id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, review, null);
    }

    @DeleteMapping(path = "/delete-review/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable("id") Long id) {
        itineraryService.deleteReview(id);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, null, null);
    }

    @GetMapping(path = "/all-reviews/{itineraryId}")
    public ResponseEntity<Object> getAllReviews(@PathVariable("itineraryId") Long itineraryId) {
        List<ReviewDTO> reviews = itineraryService.getAllReviewsForItinerary(itineraryId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, reviews, null);
    }

    @PostMapping("/share/{itineraryId}")
    public ResponseEntity<Object> shareItinerary(@PathVariable("itineraryId") Long itineraryId,
                                                 @RequestParam("authorId") @Valid Long authorId) {
        String res = itineraryService.shareItinerary(itineraryId, authorId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, res, null);
    }
    @PostMapping("/{itineraryId}/rate/{userId}")
    public ResponseEntity<Object> rateItinerary(@PathVariable("itineraryId") Long itineraryId,
                                                 @PathVariable("userId") Long userId,
                                                 @RequestParam("rating") Double rating) {
        ItineraryDTO ratedItinerary = itineraryService.rateItinerary(itineraryId, userId, rating);

        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, ratedItinerary, null);
    }
    @GetMapping("/{itineraryId}/rating")
    public ResponseEntity<Object> getAverageRatingOfItinerary(@PathVariable("itineraryId") Long itineraryId) {
        double averageRating = itineraryService.getAverageRatingOfItinerary(itineraryId);

        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, averageRating, null);
    }

    @GetMapping("/{itineraryId}/rating-count")
    public ResponseEntity<Object> getRatingCountOfItinerary(@PathVariable("itineraryId") Long itineraryId) {
        int ratingCount = itineraryService.getRatingCountOfItinerary(itineraryId);

        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, ratingCount, null);
    }
    @PostMapping("/{id}/attend")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> attendItinerary(@PathVariable("id") Long itineraryId, @RequestBody @Valid ItineraryAttendDTO itineraryAttendDTO) {
        itineraryService.attendItinerary(itineraryId, itineraryAttendDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, "Traveller added to itinerary attendees successfully!", null);
    }

    @PostMapping("/{id}/unattend")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> unattendItinerary(@PathVariable("id") Long itineraryId, @RequestBody @Valid ItineraryAttendDTO itineraryAttendDTO) {
        itineraryService.unattendItinerary(itineraryId, itineraryAttendDTO);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, "Traveller removed from itinerary attendees successfully!", null);
    }


}
