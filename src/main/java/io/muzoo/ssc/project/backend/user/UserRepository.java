package io.muzoo.ssc.project.backend.user;

import io.muzoo.ssc.project.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findFirstByUsername(String username);
}
