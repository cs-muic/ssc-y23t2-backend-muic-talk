package io.muzoo.ssc.project.backend.schedule;

import io.muzoo.ssc.project.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByUser(User user);
}
