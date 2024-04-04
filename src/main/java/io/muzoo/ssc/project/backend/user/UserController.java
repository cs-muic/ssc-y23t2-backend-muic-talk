package io.muzoo.ssc.project.backend.user;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.chat.Chat;
import io.muzoo.ssc.project.backend.chat.ChatRepository;
import io.muzoo.ssc.project.backend.friend.Friend;
import io.muzoo.ssc.project.backend.friend.FriendRepository;
import io.muzoo.ssc.project.backend.group.Group;
import io.muzoo.ssc.project.backend.group.GroupRepository;
import io.muzoo.ssc.project.backend.schedule.Event;
import io.muzoo.ssc.project.backend.schedule.EventController;
import io.muzoo.ssc.project.backend.schedule.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;

// You don't have to create a UserController or UserService object explicitly,
// Spring will do for youuu
@RestController
public class UserController {

    // Overusing Autowiring or injections may create cyclic dependency
    // e.g. class A requires class B but class B also requires class A (loopy)
    @Autowired // Spring wires it for you (I think that means it will create object?)
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private GroupRepository groupRepository;

    @PostMapping("/user/create")
    public SimpleResponseDTO createUser(@RequestParam String username,
                             @RequestParam String displayName,
                             @RequestParam String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setDisplayName(displayName);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole("USER");
        try {
            if (userRepository.findFirstByUsername(username) == null) {
                userRepository.save(newUser);
                System.out.println("Success");
                return SimpleResponseDTO
                        .builder()
                        .success(true)
                        .message("User created successfully.")
                        .build();
            } else throw new UsernameNotUniqueException("Username exists.");
        }
        catch (UsernameNotUniqueException e) {
            return SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/user/displayName")
    public SimpleResponseDTO changeDisplayName(@RequestParam String username,
                                        @RequestParam String displayName) {
        User user = userRepository.findFirstByUsername(username);
        user.setDisplayName(displayName);
        userRepository.save(user);
        return SimpleResponseDTO
                .builder()
                .success(true)
                .message(String.format("Display name successfully updated to %s.", displayName))
                .build();
    }

    @PostMapping("/user/password")
    public SimpleResponseDTO changePassword(@RequestParam String username,
                                               @RequestParam String oldPassword,
                                               @RequestParam String newPassword) {
        User user = userRepository.findFirstByUsername(username);
        if (BCrypt.checkpw(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("Password has been successfully updated.")
                    .build();
        } else return SimpleResponseDTO
                .builder()
                .success(false)
                .message("Incorrect password")
                .build();


    }

    @PostMapping("/user/password/verify")
    public SimpleResponseDTO verifyPassword(@RequestParam String username,
                                            @RequestParam String password) {
        User user = userRepository.findFirstByUsername(username);
        if (BCrypt.checkpw(password, user.getPassword())) {
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("Password has been successfully verified.")
                    .build();
        } else return SimpleResponseDTO
                .builder()
                .success(false)
                .message("Incorrect password")
                .build();


    }

    @PostMapping("/user/delete")
    public SimpleResponseDTO deleteUser(@RequestParam String username,
                                            @RequestParam String password) {
        User user = userRepository.findFirstByUsername(username);
        List<Friend> friendList = friendRepository.findAllByUser1OrUser2(user, user);
        List<Event> events = eventRepository.findAllByUser(user);
        List<Chat> chats = chatRepository.findAllBySender(user);
        Set<Group> groups = user.getGroups();
        if (BCrypt.checkpw(password, user.getPassword())) {
            friendRepository.deleteAll(friendList);
            eventRepository.deleteAll(events);
            User nullUser = userRepository.findFirstByUsername("null");
            for (Chat chat : chats) {
                chat.setSender(nullUser);
            }
            chatRepository.saveAll(chats);
            for (Group group : groups) {
                group.getUsers().remove(user);
            }
            groupRepository.saveAll(groups);
            userRepository.delete(user);
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("User has been deleted.")
                    .build();
        } else return SimpleResponseDTO
                .builder()
                .success(false)
                .message("Failed to delete account.")
                .build();
    }
}