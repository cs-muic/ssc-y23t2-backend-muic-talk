package io.muzoo.ssc.project.backend.whoami;

import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//controller to retrieve currently logged in user
@RestController
public class WhoamiController {
    @Autowired
    private UserRepository userRepository;
    // make sure all API begins with /api. All for when doing proxy
    @GetMapping ("/api/whoami")
    public WhoamiDTO whoami(){
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(principal != null && principal instanceof org.springframework.security.core.userdetails.User){
                // user is logged in

                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findFirstByUsername(user.getUsername());
                return WhoamiDTO.builder()
                        .loggedIn(true)
                        .name(u.getName())
                        .role(u.getRole())
                        .username(u.getUsername())
                        .build();

            }

        } catch(Exception e){
            // user is not logged in
        }
        return WhoamiDTO.builder()
                .loggedIn(false)
                .build();
    }
}
