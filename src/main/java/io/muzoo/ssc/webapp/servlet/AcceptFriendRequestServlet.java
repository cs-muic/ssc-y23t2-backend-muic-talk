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

public class AcceptFriendRequestServlet extends HttpServlet implements Routable {
    private SecurityService securityService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request); // Done using session & cookies
        if (authorized) {
            // do MVC in here
            String username = (String) request.getSession().getAttribute("username");
            String requestUser = request.getParameter("requestUser");
            System.out.println(requestUser);
            UserService userService = UserService.getInstance();
            FriendService friendService = FriendService.getInstance();

                try {
                    User currentUser = userService.findByUsername(username);
                    User userToAccept = userService.findByUsername(requestUser);

                    if (friendService.acceptFriendRequest( userToAccept.getUsername(), currentUser.getUsername())) {
                            // go to user list page with success msg
                        request.getSession().setAttribute("hasError", false);
                        request.getSession().setAttribute("message", String.format("Friend request from %s has been accepted!", userToAccept.getUsername()));
                    } else {
                        // go to user list page with error msg
                        request.getSession().setAttribute("hasError", true);
                        request.getSession().setAttribute("message", "Unable to accept friend request");
                    }
                } catch (Exception e) {
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", "Unable to accept friend request.");
                }

            response.sendRedirect("/"); // If login is wrong, redirect !
        } else {
            response.sendRedirect("/login"); // If login is wrong, redirect !
        }
    }

    @Override
    public String getMapping() { return "/user/add/accept"; }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}

