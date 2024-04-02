package io.muzoo.ssc.project.backend.auth;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.util.AjaxUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @GetMapping("/api/test")
    public String test() {
        return "If this message is shown it meanss login is successful because we didnt set to permit this path";
    }

    @PostMapping("/api/login")
    public SimpleResponseDTO login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }
            request.login(username, password);
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("You are logged in successfully.")
                    .build();
        } catch (ServletException e) {
            return SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message("Invalid username or password.")
                    .build();
        }


    }

    @GetMapping("/api/logout")
    public SimpleResponseDTO logout(HttpServletRequest request) {
       try{
           request.logout();
           return SimpleResponseDTO
                   .builder()
                   .success(true)
                   .message("logout successful")
                   .build();
       } catch (ServletException e){
           return SimpleResponseDTO
                   .builder()
                   .success(false)
                   .message("logout unsuccessful")
                   .build();
       }

    }
}
