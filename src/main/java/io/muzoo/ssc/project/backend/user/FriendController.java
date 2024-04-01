package io.muzoo.ssc.project.backend.user;

import io.muzoo.ssc.project.backend.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public SimpleResponseDTO createUser(@RequestParam String username,
                           @RequestParam String userToAdd) {
        try {
            User toAdd = userRepository.findFirstByUsername(userToAdd);
            if (toAdd == null) throw new UserDoesNotExistException("The user you are trying to add does not exist.");
            Friend newFriend = new Friend();
            newFriend.setUser1(userRepository.findFirstByUsername(username));
            newFriend.setUser2(userRepository.findFirstByUsername(userToAdd));
            newFriend.setPending_user2(true);
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
}