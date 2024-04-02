package io.muzoo.ssc.project.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>{
    List<Friend> findAllByUser1(User user1);

    List<Friend> findAllByUser1OrUser2AndPending(User user1, User user2, boolean pending);
}
