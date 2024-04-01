package io.muzoo.ssc.project.backend.whoami;

import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to rereive currentjly login uesr
 */

@RestController
public class WhoamiController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Make sure that all API path begins with /api This will be userful when we do prixy
     */
    @GetMapping("/api/whoami")
    public WhoamiDTO whoami() {
        // put try around the statemnt because we use nested dot notation which could raise a Null exception
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User){
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findFirstByUsername(user.getUsername());
                return WhoamiDTO.builder()
                        .loggedIn(true)
                        .name(u.getDisplayName())
                        .role(u.getRole())
                        .username(u.getUsername())
                        .build();
            }
        } catch (Exception e){
            // USER Is not logged in

        }
        return WhoamiDTO.builder()
                .loggedIn(false)
                .build();
    }
}
