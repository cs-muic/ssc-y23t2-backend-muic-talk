package io.muzoo.ssc.project.backend.group;

import io.muzoo.ssc.project.backend.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "my_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;


    @Setter
    @Getter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    // Add convenience methods to manage users

    public void addUser(User user) {
        this.users.add(user);
        user.getGroups().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getGroups().remove(this);
    }


    // Constructors, getters, and setters
}
