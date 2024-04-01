package io.muzoo.ssc.project.backend;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_user")
public class User {
    @Id
    @Column(unique = true)
    private String username;
    private String displayName;
    private String password;
    private String role;
}
