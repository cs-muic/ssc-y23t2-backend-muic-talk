package io.muzoo.ssc.project.backend.schedule;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.user.User;
import io.muzoo.ssc.project.backend.user.UserRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
public class EventController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    @PostMapping("/api/user/events")
    public EventDTO getSchedule(@RequestParam String username) {
        User user = userRepository.findFirstByUsername(username);
        List<Event> events = eventRepository.findAllByUser(user);
        JsonArrayBuilder schedule = Json.createArrayBuilder();
        for (Event event : events) {
            schedule.add(Json.createObjectBuilder()
                    .add("id", String.valueOf(event.getId()))
                    .add("name", event.getName())
                    .add("date", event.getDateTime().toString())
            );
        }
        return EventDTO
                .builder()
                .success(true)
                .events(schedule.build())
                .build();
    }

    @PostMapping("/api/user/events/new")
    public SimpleResponseDTO addEvent(@RequestParam String username,
                                      @RequestParam String eventName,
                                      @RequestParam String eventDate,
                                      @RequestParam String eventTime) {
        if (eventName != null && eventDate != null && eventTime != null) {
            try {
                User user = userRepository.findFirstByUsername(username);
                // Parse eventDate and eventTime to create a single LocalDateTime object
                LocalDate parsedEventDate = LocalDate.parse(eventDate);
                LocalTime parsedEventTime = LocalTime.parse(eventTime);
                LocalDateTime eventDateTime = LocalDateTime.of(parsedEventDate, parsedEventTime);

                Timestamp time = Timestamp.valueOf(eventDateTime);

                Event newEvent = new Event();
                newEvent.setUser(user);
                newEvent.setDateTime(time);
                newEvent.setName(eventName);
                eventRepository.save(newEvent);
                // Store the event in the database
                return SimpleResponseDTO
                        .builder()
                        .success(true)
                        .message("Event created")
                        .build();
            } catch (DateTimeParseException e) {
                // Handle invalid date or time format
                return SimpleResponseDTO
                        .builder()
                        .success(false)
                        .message("Invalid date or time format.")
                        .build();
            }
        } else {
            return SimpleResponseDTO
                .builder()
                .success(false)
                .message("Please fill in all fields!")
                .build();
        }
    }

    @PostMapping("/api/user/events/delete")
    public SimpleResponseDTO deleteEvent(@RequestParam String username,
                                         @RequestParam String eventId) {
        User user = userRepository.findFirstByUsername(username);
        System.out.println(eventId);
        Optional<Event> eventOptional = eventRepository.findById(Long.parseLong(eventId));
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getUser() == user) {
                System.out.println(event.getId() + ", " + event.getName());
                eventRepository.delete(event);
                return SimpleResponseDTO
                        .builder()
                        .success(true)
                        .message("Event has been deleted.")
                        .build();
            }
            else return SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message("This event does not belong to you.")
                    .build();
        }
        else return SimpleResponseDTO
                .builder()
                .success(false)
                .message("This event does not belong to you.")
                .build();
    }
}
