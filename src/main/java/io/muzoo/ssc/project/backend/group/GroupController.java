package io.muzoo.ssc.project.backend.group;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setCreated(Timestamp.valueOf(LocalDateTime.now()));
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
}
