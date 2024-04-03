package io.muzoo.ssc.project.backend.schedule;

import io.muzoo.ssc.project.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "tbl_event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private Timestamp dateTime;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;
}
