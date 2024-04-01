package io.muzoo.ssc.project.backend.user;

import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import jakarta.servlet.ServletException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// You don't have to create a UserController or UserService object explicitly,
// Spring will do for youuu
@RestController
public class UserController {

    // Overusing Autowiring or injections may create cyclic dependency
    // e.g. class A requires class B but class B also requires class A (loopy)
    @Autowired // Spring wires it for you (I think that means it will create object?)
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
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
}