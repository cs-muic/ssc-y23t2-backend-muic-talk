package io.muzoo.ssc.project.backend;
import java.util.HashSet;

//import io.muzoo.ssc.project.backend.group.Group;
import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "tbl_user")
public class User {
    @Id
    @Column(unique = true)
    private String username;
    @Name("displayName")
    private String displayName;
    private String password;
    private String role;
}
