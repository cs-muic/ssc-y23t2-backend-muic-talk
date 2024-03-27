package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.model.User;
import io.muzoo.ssc.webapp.service.FriendService;
import io.muzoo.ssc.webapp.service.SecurityService;
import io.muzoo.ssc.webapp.service.UserDoesNotExistException;
import io.muzoo.ssc.webapp.service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;

public class AddFriendsServlet extends HttpServlet implements Routable {
    private SecurityService securityService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request); // Done using session & cookies
        if (authorized) {
            // do MVC in here
            String username = (String) request.getSession().getAttribute("username");
            String friendUser = (String) request.getParameter("friendUser");
            System.out.println(friendUser);
            UserService userService = UserService.getInstance();
            FriendService friendService = FriendService.getInstance();

            if (friendUser.isEmpty()) {
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", "Please input the username that you would like to add.");
            }
            else {

                try {
                    User currentUser = userService.findByUsername(username);
                    User userToAdd = userService.findByUsername(friendUser);

                    if (StringUtils.equals(currentUser.getUsername(), userToAdd.getUsername())) {
                        request.getSession().setAttribute("hasError", true);
                        request.getSession().setAttribute("message", "You cannot add yourself!");
                    } else if (userToAdd == null){
                        request.getSession().setAttribute("hasError", true);
                        request.getSession().setAttribute("message", String.format("User %s does not exist.", friendUser));
                    }
                    else {
                        if (friendService.addFriend(currentUser.getUsername(), userToAdd.getUsername())) {
                            // go to user list page with success msg
                            request.getSession().setAttribute("hasError", false);
                            request.getSession().setAttribute("message", String.format("A friend request has been sent to %s!", userToAdd.getUsername()));
                        } else {
                            // go to user list page with error msg
                            request.getSession().setAttribute("hasError", true);
                            request.getSession().setAttribute("message", String.format("%s does not exist.", userToAdd.getUsername()));

                        }
                    }
                } catch (UserDoesNotExistException e) {
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", String.format("User %s does not exist.", friendUser));
                } catch (Exception e) {
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", String.format("User %s does not exist.", friendUser));
                }
            }
            response.sendRedirect("/"); // If login is wrong, redirect !
        } else {
            response.sendRedirect("/login"); // If login is wrong, redirect !
        }
    }

    @Override
    public String getMapping() { return "/user/add"; }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}

