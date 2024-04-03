package io.muzoo.ssc.project.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityService {

    private final UserRepository userRepository;

    @Autowired
    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAuthorized(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        return (username != null && userRepository.findFirstByUsername(username) != null);
    }

    public boolean authenticate(String username, String password) {
        User user = userRepository.findFirstByUsername(username);
        return (user != null && new BCryptPasswordEncoder().matches(password, user.getPassword()));
    }

    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }
}
