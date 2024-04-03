package io.muzoo.ssc.project.backend.group;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.user.User;
import io.muzoo.ssc.project.backend.user.UserRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user/groups/create")
    public SimpleResponseDTO createGroup(
            @RequestParam String username,
            @RequestParam String name) {
        User user = userRepository.findFirstByUsername(username);
        Timestamp created = Timestamp.valueOf(LocalDateTime.now());
        System.out.println(created);
        Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setCreated(created);
        newGroup.setId(DigestUtils.sha256Hex(created.toString()));
        newGroup.addUser(user);
        groupRepository.save(newGroup);
        return SimpleResponseDTO
                .builder()
                .success(true)
                .message("Group has been created.")
                .build();
    }

    @PostMapping("/user/groups/addUser")
    public ResponseEntity<?> addUserToGroup(@RequestParam Long groupId, @RequestParam Long userId) {
        boolean result = groupService.addUserToGroup(groupId, userId);
        if(result) {
            return ResponseEntity.ok().body("User added to group successfully");
        }
        return ResponseEntity.badRequest().body("Failed to add user to group");
    }

    @PostMapping("/user/groups")
    public GroupDTO getGroups(@RequestParam String username) {
        User user = userRepository.findFirstByUsername(username);
        Set<Group> groupSet = user.getGroups();
        JsonArrayBuilder groups = Json.createArrayBuilder();
        for (Group group : groupSet) {
            groups.add(Json.createObjectBuilder()
                            .add("groupId", group.getId())
                           .add("name", group.getName()));
        }
        return GroupDTO
                .builder()
                .success(true)
                .groups(groups.build())
                .build();
    }

    @PostMapping("/user/groups/leave")
    public SimpleResponseDTO leaveGroup(@RequestParam String username,
                               @RequestParam String groupId) {
        User user = userRepository.findFirstByUsername(username);
        Group group = groupRepository.findById(groupId);
        if (user.getGroups().contains(group)) {
            // Leave group
            user.getGroups().remove(group);
            userRepository.save(user);
            group.getUsers().remove(user);
            groupRepository.save(group);
            System.out.println(group.getUsers());
            if (group.getUsers().isEmpty())
                groupRepository.delete(group);
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("Successfully left group.")
                    .build();
        }
        return SimpleResponseDTO
                .builder()
                .success(false)
                .message("User is not in this group!!")
                .build();
    }
}
