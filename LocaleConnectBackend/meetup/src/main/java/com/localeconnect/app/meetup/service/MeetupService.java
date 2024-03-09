package com.localeconnect.app.meetup.service;

import com.localeconnect.app.meetup.config.MeetupRabbitConfig;
import com.localeconnect.app.meetup.dto.*;
import com.localeconnect.app.meetup.exceptions.LogicException;
import com.localeconnect.app.meetup.exceptions.ResourceNotFoundException;
import com.localeconnect.app.meetup.exceptions.ValidationException;
import com.localeconnect.app.meetup.mapper.MeetupMapper;
import com.localeconnect.app.meetup.model.Meetup;
import com.localeconnect.app.meetup.rabbit.RabbitMQMessageProducer;
import com.localeconnect.app.meetup.repository.MeetupRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
@Transactional
public class MeetupService {

    private final MeetupRepository meetupRepository;
    private final MeetupMapper meetupMapper;
    private final WebClient webClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public MeetupDTO createMeetup(MeetupDTO meetupDTO) {
        Meetup meetup = meetupMapper.toEntity(meetupDTO);

        // check creatorId is valid
        if (!checkUserId(meetup.getCreatorId()))
            throw new ResourceNotFoundException("No User Found with id: " + meetup.getCreatorId() + "!");

        // Save meetup in DATABASE
        Meetup createdMeetup = meetupRepository.save(meetup);
        // return saved meetup
        return meetupMapper.toDomain(createdMeetup);
    }

    public List<MeetupDTO> getAllMeetupsByCreator(Long userId) {

        if (!checkUserId(userId))
            throw new ResourceNotFoundException("User with id " + userId + " does not exist!");

        Optional<List<Meetup>> meetups = meetupRepository.findByCreatorId(userId);
        return meetups.map(meetupList -> meetupList.stream().map(meetupMapper::toDomain).collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    public MeetupDTO updateMeetup(MeetupEditDTO meetupEditDTO, Long meetupId) {

        Optional<Meetup> optional = meetupRepository.findById(meetupId);
        if (optional.isEmpty())
            throw new ResourceNotFoundException("No Meetup Found with id: " + meetupId + "!");

        Meetup actualMeetup = optional.get();
        if (meetupEditDTO.getName() != null)
            actualMeetup.setName(meetupEditDTO.getName());

        if (meetupEditDTO.getDescription() != null)
            actualMeetup.setDescription(meetupEditDTO.getDescription());

        if (meetupEditDTO.getCost() != null)
            actualMeetup.setCost(meetupEditDTO.getCost());

        if (meetupEditDTO.getDate() != null)
            actualMeetup.setDate(meetupEditDTO.getDate());

        if (meetupEditDTO.getStartTime() != null)
            actualMeetup.setStartTime(meetupEditDTO.getStartTime());

        if (meetupEditDTO.getEndTime() != null)
            actualMeetup.setEndTime(meetupEditDTO.getEndTime());

        if (meetupEditDTO.getLocation() != null)
            actualMeetup.setLocation(meetupEditDTO.getLocation());

        if (meetupEditDTO.getSpokenLanguages() != null)
            actualMeetup.setSpokenLanguages(meetupEditDTO.getSpokenLanguages());

        meetupRepository.save(actualMeetup);

        List<Long> attendees = actualMeetup.getMeetupAttendees();
        for (Long att : attendees
        ) {
            NotificationDTO newNotification = new NotificationDTO();
            newNotification.setTitle("New Meetup Notification");
            newNotification.setMessage("Meetup " + meetupId + " Got Updated!");
            newNotification.setSentAt(LocalDateTime.now());
            newNotification.setReceiverID(att);
            newNotification.setSenderID(actualMeetup.getCreatorId());
            rabbitMQMessageProducer.publish(newNotification, MeetupRabbitConfig.EXCHANGE, MeetupRabbitConfig.ROUTING_KEY);
        }
        return meetupMapper.toDomain(actualMeetup);
    }


    public void attendMeetup(Long meetupId, MeetupAttendDTO meetupAttendDTO) {

        Optional<Meetup> meetup = meetupRepository.findById(meetupId);
        if (meetup.isEmpty())
            throw new ResourceNotFoundException("No Meetup Found with id: " + meetupId + "!");

        Long travellerId = meetupAttendDTO.getTravellerId();

        if (!checkUserId(travellerId))
            throw new ResourceNotFoundException("No User Found with id: " + travellerId + "!");

        Meetup actualMeetup = meetup.get();

        if (actualMeetup.getMeetupAttendees().contains(travellerId))
            throw new LogicException("Traveller is ALREADY in meetup attendees!");

        actualMeetup.getMeetupAttendees().add(travellerId);
        meetupRepository.save(actualMeetup);
    }

    public void unattendMeetup(Long meetupId, MeetupAttendDTO meetupAttendDTO) {

        Optional<Meetup> meetup = meetupRepository.findById(meetupId);
        if (meetup.isEmpty())
            throw new ResourceNotFoundException("No Meetup Found with id: " + meetupId + "!");

        Long travellerId = meetupAttendDTO.getTravellerId();

        if (!checkUserId(travellerId))
            throw new ResourceNotFoundException("No User Found with id: " + travellerId + "!");

        Meetup actualMeetup = meetup.get();
        if (!actualMeetup.getMeetupAttendees().contains(travellerId))
            throw new LogicException("Traveller is NOT in meetup attendees!");

        actualMeetup.getMeetupAttendees().remove(travellerId);
        meetupRepository.save(actualMeetup);
    }

    public List<MeetupDTO> getAllMeetups() {
        List<Meetup> meetups = meetupRepository.findAll();
        return meetups.stream().map(
                        meetupMapper::toDomain)
                .collect(Collectors.toList());
    }

    public MeetupDTO getMeetupById(Long id) {
        Optional<Meetup> optional = meetupRepository.findById(id);
        if (optional.isEmpty())
            throw new ResourceNotFoundException("No Meetup Found with id: " + id + "!");

        return meetupMapper.toDomain(optional.get());
    }

    public List<MeetupDTO> getMeetupsByUserId(Long userId) {
        if (!this.checkUserId(userId))
            throw new ValidationException("Register to create a Meetup");
        Optional<List<Meetup>> meetups = meetupRepository.findByCreatorId(userId);
        return meetups.map(tripList -> tripList.stream().map(meetupMapper::toDomain).collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    public MeetupDTO deleteMeetupById(Long id) {
        Optional<Meetup> optional = meetupRepository.findById(id);
        if (optional.isEmpty())
            throw new ResourceNotFoundException("No Meetup Found with id: " + id + "!");

        Meetup actualMeetup = optional.get();

        List<Long> attendees = actualMeetup.getMeetupAttendees();
        for (Long att : attendees
        ) {
            NotificationDTO newNotification = new NotificationDTO();
            newNotification.setTitle("New Meetup Notification");
            newNotification.setMessage("Meetup " + actualMeetup.getId() + " Got Deleted!");
            newNotification.setSentAt(LocalDateTime.now());
            newNotification.setReceiverID(att);
            newNotification.setSenderID(actualMeetup.getCreatorId());
            rabbitMQMessageProducer.publish(newNotification, MeetupRabbitConfig.EXCHANGE, MeetupRabbitConfig.ROUTING_KEY);
        }

        meetupRepository.deleteById(id);
        return meetupMapper.toDomain(optional.get());
    }

    public MeetupDTO rateMeetup(Long meetupId, Long userId, Double rating) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ResourceNotFoundException("Meetup with id " + meetupId + " does not exist"));

        if (!checkUserId(userId))
            throw new ResourceNotFoundException("User with id " + userId + " does not exist");

        meetup.addRating(rating);
        meetup.calcAverageRating();

        meetupRepository.save(meetup);
        return meetupMapper.toDomain(meetup);
    }

    public double getAverageRatingOfMeetup(Long meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ResourceNotFoundException("Meetup with id " + meetupId + " does not exist"));

        MeetupDTO ratedMeetupDTO = meetupMapper.toDomain(meetup);

        return ratedMeetupDTO.getAverageRating();
    }

    public int getRatingCountOfMeetup(Long meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ResourceNotFoundException("Meetup with id " + meetupId + " does not exist"));

        MeetupDTO ratedMeetupDTO = meetupMapper.toDomain(meetup);

        return ratedMeetupDTO.getRatingsCount();
    }

    public String shareMeetup(Long meetupId, Long authorId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ResourceNotFoundException("Meetup not found with id: " + meetupId));

        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("User with id " + authorId + " does not exist!");

        MeetupDTO shareDTO = meetupMapper.toDomain(meetup);

        return postToFeed(shareDTO, authorId);
    }

    public List<MeetupDTO> searchByName(String name) {

        List<Meetup> meetups = meetupRepository.findAllByNameIgnoreCaseLike(name)
                .orElseThrow(() -> new ResourceNotFoundException("Meetup not found with name: " + name));
        return meetups.stream().map(meetupMapper::toDomain).collect(Collectors.toList());
    }

    private String postToFeed(MeetupDTO meetupShareDTO, Long authorId) {
        String url = UriComponentsBuilder
                .fromUriString("http://feed-service:8081/api/feed/share-meetup")
                .queryParam("authorId", authorId)
                .toUriString();

        ShareMeetupResponseDTO res = webClient.post()
                .uri(url)
                .bodyValue(meetupShareDTO)
                .retrieve()
                .bodyToMono(ShareMeetupResponseDTO.class)
                .block();

        return res.getData();
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
