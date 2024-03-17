package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User admin = userRepository.findFirstByUsername("admin");
        if(admin == null){
            admin = new User();
            admin.setUsername("admin");
            admin.setName("BigBoss");
            admin.setPassword(passwordEncoder.encode("1111"));
            admin.setRole("USER");
            userRepository.save(admin);
        }
    }

}
