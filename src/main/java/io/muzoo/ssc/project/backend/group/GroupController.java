package io.muzoo.ssc.project.backend.group;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/api/groups/create")
    public Group createGroup(@RequestBody GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO.getName());
    }

    @PostMapping("/api/groups/addUser")
    public ResponseEntity<?> addUserToGroup(@RequestParam Long groupId, @RequestParam Long userId) {
        boolean result = groupService.addUserToGroup(groupId, userId);
        if(result) {
            return ResponseEntity.ok().body("User added to group successfully");
        }
        return ResponseEntity.badRequest().body("Failed to add user to group");
    }
}
