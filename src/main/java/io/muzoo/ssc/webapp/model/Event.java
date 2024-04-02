package io.muzoo.ssc.webapp.model;

import java.time.LocalDateTime;

public class Event {

    private int id;
    private String name;
    private LocalDateTime dateTime;

    public Event(int id, String name, LocalDateTime dateTime) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
