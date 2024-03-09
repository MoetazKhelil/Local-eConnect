package com.localeconnect.app.trip.service;

import com.localeconnect.app.trip.config.RabbitConfig;
import com.localeconnect.app.trip.dto.*;
import com.localeconnect.app.trip.exceptions.ResourceNotFoundException;
import com.localeconnect.app.trip.exceptions.ValidationException;
import com.localeconnect.app.trip.exceptions.LogicException;
import com.localeconnect.app.trip.mapper.TripMapper;
import com.localeconnect.app.trip.mapper.TripReviewMapper;
import com.localeconnect.app.trip.model.Trip;
import com.localeconnect.app.trip.model.TripReview;
import com.localeconnect.app.trip.rabbit.RabbitMQMessageProducer;
import com.localeconnect.app.trip.repository.TripRepository;
import com.localeconnect.app.trip.repository.TripReviewRepository;
import com.localeconnect.app.trip.repository.TripSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final TripReviewMapper tripReviewMapper;
    private final WebClient webClient;
    private final TripReviewRepository tripReviewRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public TripDTO createTrip(TripDTO tripDTO) {
        Trip trip = tripMapper.toEntity(tripDTO);

        if (trip == null) {
            throw new ResourceNotFoundException("Trip data is invalid");
        }

        if (!this.checkUserId(tripDTO.getLocalguideId()))
            throw new ValidationException("Register as a Localguide to create a Trip");

        if (tripRepository.findByLocalguideIdAndName(tripDTO.getLocalguideId(), tripDTO.getName()).isPresent())
            throw new LogicException("This user already created this trip.");

        List<String> images = tripDTO.getImageUrls();

        if (!images.isEmpty()) {

            GCPResponseDTO gcpResponse = saveImageToGCP(tripDTO.getImageUrls().get(0));
            String imageUrl = gcpResponse.getData();
            trip.setImageUrls(List.of(imageUrl));

        } else {
            trip.setImageUrls(new ArrayList<>());
        }

        tripRepository.save(trip);

        return tripMapper.toDomain(trip);
    }

    public List<TripDTO> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toDomain)
                .collect(Collectors.toList());
    }

    public TripDTO getById(Long tripId) {
        Trip optionalTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("A trip with the id " + tripId + " does not exist!"));

        return tripMapper.toDomain(optionalTrip);
    }

    public List<TripDTO> getAllTripsByLocalguide(Long localguideId) {
        if (!this.checkUserId(localguideId))
            throw new ValidationException("Register as Localguide to create a Trip");

        Optional<List<Trip>> trips = tripRepository.findByLocalguideId(localguideId);
        return trips.map(tripList -> tripList.stream().map(tripMapper::toDomain).collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    public TripDTO updateTrip(Long tripId, TripDTO tripDTO) {
        Trip tripToUpdate = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("A trip with the id " + tripId + " does not exist!"));

        tripToUpdate.setId(tripId);
        tripMapper.updateTripFromDto(tripDTO, tripToUpdate);
        //send notifications to all travelers
        List<Long> travelers = tripToUpdate.getTravelers();
        for (Long traveler : travelers) {
            NotificationDTO newNotification = new NotificationDTO();
            newNotification.setTitle("New Notification");
            newNotification.setMessage("Trip " + tripId + " Got Updated!");
            newNotification.setSentAt(LocalDateTime.now());
            newNotification.setReceiverID(traveler);
            newNotification.setSenderID(tripToUpdate.getLocalguideId());
        }
        tripRepository.save(tripToUpdate);
        return tripMapper.toDomain(tripToUpdate);
    }

    public void deleteTrip(Long tripId) {
        Trip tripToDelete = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("A trip with the id " + tripId + " does not exist!"));

        //send notifications to all travelers
        List<Long> travelers = tripToDelete.getTripAttendees();
        for (Long traveler : travelers) {
            NotificationDTO newNotification = new NotificationDTO();
            newNotification.setTitle("New Trip Notification");
            newNotification.setMessage("Trip " + tripId + " Got Deleted!");
            newNotification.setSentAt(LocalDateTime.now());
            newNotification.setReceiverID(traveler);
            newNotification.setSenderID(tripToDelete.getLocalguideId());
            rabbitMQMessageProducer.publish(newNotification, RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY);
        }
        tripRepository.delete(tripToDelete);
    }

    public TripDTO searchTrip(String tripName) {
        Trip trip = tripRepository.findByName(tripName)
                .orElseThrow(() -> new ResourceNotFoundException("A trip with the name " + tripName + " does not exist!"));

        return tripMapper.toDomain(trip);
    }

    public List<TripDTO> filter(String destination, Double traveltime, List<String> languages) {
        if (destination == null && traveltime <= 0 && languages == null)
            return null;

        Specification<Trip> specif = Specification.where(TripSpecification.getDestination(destination))
                .and(TripSpecification.maxDuration(traveltime)).and(TripSpecification.hasLanguages(languages));
        List<Trip> trips = tripRepository.findAll(specif);
        return trips.stream().map(tripMapper::toDomain).collect(Collectors.toList());
    }

    public TripReviewDTO createReview(TripReviewDTO tripReviewDTO, Long userId, Long tripId) {
        if (!this.checkUserId(userId))
            throw new ValidationException("Register to create a Review");
        if (tripId == null || !tripRepository.existsById(tripId))
            throw new ResourceNotFoundException("Trip with this ID does not exist");
        TripReview tripReview = tripReviewMapper.toEntity(tripReviewDTO);
        tripReview.setTripId(tripId);
        tripReview.setUserId(userId);
        tripReview.setTimestamp(LocalDateTime.now());
        tripReview = tripReviewRepository.save(tripReview);
        return tripReviewMapper.toDomain(tripReview);
    }

    public TripReviewDTO updateReview(TripReviewDTO tripReviewDTO, Long id) {
        TripReview existingTripReview = tripReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        if (!this.checkUserId(tripReviewDTO.getUserId())) {
            throw new ValidationException("Only registered users can edit their reviews");
        }

        if (!existingTripReview.getUserId().equals(tripReviewDTO.getUserId())) {
            throw new ValidationException("Users can only edit their own reviews");
        }

        TripReview reviewToUpdate = tripReviewMapper.toEntity(tripReviewDTO);
        reviewToUpdate.setTripReviewId(id);
        reviewToUpdate.setTimestamp(LocalDateTime.now());
        tripReviewMapper.updateTripReviewFromDto(tripReviewDTO, reviewToUpdate);
        TripReview updatedReview = tripReviewRepository.save(reviewToUpdate);
        return tripReviewMapper.toDomain(updatedReview);
    }

    public void deleteReview(Long id) {
        TripReview review = tripReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        if (!this.checkUserId(review.getUserId())) {
            throw new ValidationException("Only registered users can delete their reviews");
        }

        tripReviewRepository.delete(review);
    }

    public List<TripReviewDTO> getAllReviewsForTrip(Long tripId) {
        List<TripReview> reviews = tripReviewRepository.findByTripId(tripId);

        return reviews.stream().map(
                        tripReviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    public String shareTrip(Long tripId, Long authorId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("User with id " + authorId + " does not exist!");

        TripDTO shareDTO = tripMapper.toDomain(trip);

        return postToFeed(shareDTO, authorId);
    }

    public TripDTO rateTrip(Long tripId, Long userId, Double rating) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " does not exist"));

        if (!checkUserId(userId))
            throw new ResourceNotFoundException("User with id " + userId + " does not exist");

        trip.addRating(rating);
        trip.calcAverageRating();

        tripRepository.save(trip);
        return tripMapper.toDomain(trip);
    }

    public double getAverageRatingOfTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " does not exist"));

        TripDTO ratedTripDTO = tripMapper.toDomain(trip);

        return ratedTripDTO.getAverageRating();
    }

    public int getRatingCountOfTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " does not exist"));

        TripDTO ratedTripDTO = tripMapper.toDomain(trip);

        return ratedTripDTO.getRatingsCount();
    }

    public void attendTrip(Long tripId, TripAttendDTO tripAttendDTO) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        if (trip.isEmpty())
            throw new ResourceNotFoundException("No Trip Found with id: " + tripId + "!");

        Long travellerId = tripAttendDTO.getTravellerId();

        if (!checkUserId(travellerId))
            throw new ResourceNotFoundException("No User Found with id: " + travellerId + "!");

        Trip actualTrip = trip.get();

        if (actualTrip.getTripAttendees().contains(travellerId))
            throw new LogicException("Traveller is ALREADY in trip attendees!");

        actualTrip.getTripAttendees().add(travellerId);
        tripRepository.save(actualTrip);
    }

    public void unattendTrip(Long tripId, TripAttendDTO tripAttendDTO) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        if (trip.isEmpty())
            throw new ResourceNotFoundException("No Trip Found with id: " + tripId + "!");

        Long travellerId = tripAttendDTO.getTravellerId();

        if (!checkUserId(travellerId))
            throw new ResourceNotFoundException("No User Found with id: " + travellerId + "!");

        Trip actualTrip = trip.get();
        if (!actualTrip.getTripAttendees().contains(travellerId))
            throw new LogicException("Traveller is NOT in trip attendees!");

        actualTrip.getTripAttendees().remove(travellerId);
        tripRepository.save(actualTrip);
    }

    //Webclient calls
    private String postToFeed(TripDTO tripShareDTO, Long authorId) {
        String url = UriComponentsBuilder
                .fromUriString("http://feed-service:8081/api/feed/share-trip")
                .queryParam("authorId", authorId)
                .toUriString();

        ShareTripResponseDTO res = webClient.post()
                .uri(url)
                .bodyValue(tripShareDTO)
                .retrieve()
                .bodyToMono(ShareTripResponseDTO.class)
                .block();

        return res.getData();
    }

    private GCPResponseDTO saveImageToGCP(String image) {
        ResponseEntity<GCPResponseDTO> responseEntity = webClient.post()
                .uri("http://gcp-service:5005/api/gcp/?filename=trip")
                .bodyValue(image)
                .retrieve()
                .toEntity(GCPResponseDTO.class)
                .block();

        return responseEntity.getBody();
    }

    private boolean checkUserId(Long userId) {
        System.out.println(userId);
        CheckUserExistsResponseDTO res = this.webClient.get()
                .uri("http://user-service:8084/api/user/auth/exists/{userId}", userId)
                .retrieve().bodyToMono(CheckUserExistsResponseDTO.class).block();

        Boolean check = res.getData();
        return check != null && check;
    }

}
