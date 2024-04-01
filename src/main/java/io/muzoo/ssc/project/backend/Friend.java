package io.muzoo.ssc.project.backend;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_friend")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user1")
    private User user1;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user2")
    private User user2;
    private boolean pending_user2;
}
