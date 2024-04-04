package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.muzoo.ssc.project.backend.user.User;

@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // add default admin user and set its password to 123456
        User admin = userRepository.findFirstByUsername("admin");

        if  (admin == null){
            admin = new User();
            admin.setUsername("admin");
            admin.setDisplayName("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("USER");
            userRepository.save(admin);
        }

        User unknownUser = userRepository.findFirstByUsername("null");
        if  (unknownUser == null){
            unknownUser = new User();
            unknownUser.setUsername("null");
            unknownUser.setDisplayName("Unknown User");
            unknownUser.setPassword(passwordEncoder.encode("123"));
            unknownUser.setRole("NULL");
            userRepository.save(unknownUser);
        }
    }
}
