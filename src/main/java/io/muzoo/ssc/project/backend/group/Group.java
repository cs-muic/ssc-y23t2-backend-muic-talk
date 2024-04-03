package io.muzoo.ssc.project.backend.group;

import io.muzoo.ssc.project.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "tbl_groups")
public class Group {

    @Id
    private String id;
    private String name;
    private Timestamp created;

    @Setter
    @Getter
    @ManyToMany(mappedBy="groups", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    // Add convenience methods to manage users

    public void addUser(User user) {
        this.users.add(user);
        user.getGroups().add(this);
    }
}
