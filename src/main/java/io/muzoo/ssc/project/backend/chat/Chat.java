package io.muzoo.ssc.project.backend.chat;

import io.muzoo.ssc.project.backend.group.Group;
import io.muzoo.ssc.project.backend.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "tbl_chat")
public class Chat {
    @Id
    private String messageId;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User sender;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    private String message;
    private Timestamp sent;
}
