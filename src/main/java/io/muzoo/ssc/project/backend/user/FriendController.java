package io.muzoo.ssc.project.backend.user;

import io.muzoo.ssc.project.backend.*;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// You don't have to create a UserController or UserService object explicitly,
// Spring will do for youuu
@RestController
public class FriendController {

    // Overusing Autowiring or injections may create cyclic dependency
    // e.g. class A requires class B but class B also requires class A (loopy)
    @Autowired // Spring wires it for you (I think that means it will create object?)
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @PostMapping("/user/add")
    public SimpleResponseDTO sendFriendRequest(@RequestParam String username,
                           @RequestParam String userToAdd) {
        try {
            User toAdd = userRepository.findFirstByUsername(userToAdd);
            if (toAdd == null) throw new UserDoesNotExistException("The user you are trying to add does not exist.");
            Friend newFriend = new Friend();
            newFriend.setUser1(userRepository.findFirstByUsername(username));
            newFriend.setUser2(userRepository.findFirstByUsername(userToAdd));
            newFriend.setPending(true);
            friendRepository.save(newFriend);
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message(String.format("Friend Request sent successfully to %s.", userToAdd))
                    .build();
        }
        catch (UserDoesNotExistException e) {
            return SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/user/accept")
    public SimpleResponseDTO acceptFriendRequest(@RequestParam String username,
                                        @RequestParam String userToAdd) {
        // We know userToAdd is user1
        User toAdd = userRepository.findFirstByUsername(userToAdd);
        User user = userRepository.findFirstByUsername(username);
        Friend acceptRequest = friendRepository.findFirstByUser1AndUser2(toAdd, user);
        acceptRequest.setPending(false);
        friendRepository.save(acceptRequest);
        return SimpleResponseDTO
                .builder()
                .success(true)
                .message(String.format("Friend Request from %s has been accepted.", userToAdd))
                .build();
    }

    @PostMapping("/user/requests")
    public FriendDTO getFriendRequests(@RequestParam String username) {
        User user = userRepository.findFirstByUsername(username);
        List<Friend> requests = friendRepository.findAllByUser1OrUser2AndPending(user,user,true);
        JsonArrayBuilder friends = Json.createArrayBuilder();
        for (Friend req : requests) {
            friends.add(Json.createObjectBuilder()
                    .add("username", req.getUser1().getUsername())
            );
        }
        return FriendDTO
                .builder()
                .success(true)
                .friends(friends.build())
                .request(true)
                .build();
    }
}