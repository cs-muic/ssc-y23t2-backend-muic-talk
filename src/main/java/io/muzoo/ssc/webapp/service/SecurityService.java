package io.muzoo.ssc.webapp.service;

import io.muzoo.ssc.webapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SecurityService {

    private UserService userService;
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /*
    By calling this method and providing the session object and the username,
    you can store the username in the session,
    making it accessible throughout the user's session for authentication and other purposes.
     */
//    public void setSessionUsername(HttpSession session, String username) {
//        session.setAttribute("username", username);
//    }

    public boolean isAuthorized(HttpServletRequest request) {
        String username = (String) request.getSession()
                .getAttribute("username");
        return (username != null && userService.findByUsername(username) != null);
    }

    public boolean authenticate(String username, String password, HttpServletRequest request) {
        User user = userService.findByUsername(username);
        if (user != null && BCrypt.checkpw(password,user.getPassword())) {
            request.getSession().setAttribute("username", username);
            return true;
        } else {
            return false;
        }
    }

    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }

}
