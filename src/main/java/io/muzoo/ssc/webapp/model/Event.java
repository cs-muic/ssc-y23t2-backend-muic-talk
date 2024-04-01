package io.muzoo.ssc.webapp.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Event {
    private int eventId;
    private String eventName;
    private LocalDateTime eventDateTime;

    // Constructor
    public Event(int eventId, String eventName, LocalDateTime eventDateTime) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDateTime = eventDateTime;
    }

    // Constructor when eventId is not provided
    public Event(String eventName, LocalDateTime eventDateTime) {
        this.eventName = eventName;
        this.eventDateTime = eventDateTime;
    }

    // Getters and setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
}
