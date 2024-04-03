package io.muzoo.ssc.project.backend.group;

import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);
        return groupRepository.save(group);
    }


    @Transactional
    public boolean addUserToGroup(Long groupId, Long userId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (groupOpt.isPresent() && userOpt.isPresent()) {
            Group group = groupOpt.get();
            User user = userOpt.get();

            // Check if the user is already in the group to prevent duplicate associations.
            if (!group.getUsers().contains(user)) {
                // Add the user to the group's set of users.
                group.getUsers().add(user);

                // Also, add the group to the user's set of groups.
                // This ensures the relationship is managed correctly from both sides.
                user.getGroups().add(group);

                // Save the updated group entity.
                // Depending on your JPA provider's cascading settings, this might also persist
                // the changes to the user entity. However, explicitly saving both can be clearer
                // and ensures that changes are persisted even with different cascading settings.
                groupRepository.save(group);
                userRepository.save(user);

                return true;
            } else {
                // The user is already in the group, so there's nothing to update.
                return false;
            }
        }
        return false;
    }
}
