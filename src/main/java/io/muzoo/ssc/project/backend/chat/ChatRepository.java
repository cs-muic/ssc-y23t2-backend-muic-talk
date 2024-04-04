package io.muzoo.ssc.project.backend.chat;

import io.muzoo.ssc.project.backend.group.Group;
import io.muzoo.ssc.project.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository  extends JpaRepository<Chat, Long> {
    List<Chat> findAllByGroupOrderBySent(Group groupId);

    List<Chat> findAllBySender(User user);
}
