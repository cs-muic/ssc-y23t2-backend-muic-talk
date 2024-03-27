package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.model.User;
import io.muzoo.ssc.webapp.service.SecurityService;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request); // Done using session & cookies
        if (authorized) {
            // do MVC in here
            String username = (String) request.getSession().getAttribute("username");
            UserService userService = UserService.getInstance();

            try {
                User currentUser = userService.findByUsername(username);
                User userToDelete = userService.findByUsername(request.getParameter("username"));
                if (StringUtils.equals(currentUser.getUsername(), userToDelete.getUsername())) {
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", "You cannot delete your own account.");
                }
                else {
                    if (userService.deleteUserByUsername(userToDelete.getUsername())) {
                        // go to user list page with success msg
                        request.getSession().setAttribute("hasError", false);
                        request.getSession().setAttribute("message", String.format("User %s has been deleted successfully.", userToDelete.getUsername()));
                    } else {
                        // go to user list page with error msg
                        request.getSession().setAttribute("hasError", true);
                        request.getSession().setAttribute("message", String.format("Unable to delete user %s.", userToDelete.getUsername()));

                    }
                }
            } catch (Exception e) {
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", String.format("Unable to delete user %s.", request.getParameter("username")));
            }

            response.sendRedirect("/"); // If login is wrong, redirect !
        } else {
            response.sendRedirect("/login"); // If login is wrong, redirect !
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request); // Done using session & cookies
        System.out.println(authorized);
        if (authorized) {
            String username = (String) request.getSession().getAttribute("username");
            String displayName =  StringUtils.trim(request.getParameter("displayName"));

            UserService userService = UserService.getInstance();
            User user = userService.findByUsername(username);

            String errorMessage = null;
            // check valid username
            if (user == null) {
                // Username taken
                errorMessage = String.format("Username %s does not exist.", username);
            }
            // check valid displayName
            else if (StringUtils.isBlank(displayName)) {
                // Show error msg invalid displayName
                errorMessage = "Display Name must not be empty.";
            }
            if (errorMessage != null) {
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", errorMessage);
            }
            else {
                // Update user's displayName
                try {
                    userService.updateUserByUsername(username, displayName);
                    request.getSession().setAttribute("hasError", false);
                    request.getSession().setAttribute("message", String.format("User %s's display name has been updated to %s!", username, displayName));
                    response.sendRedirect("/user/edit");
                    return;
                } catch (Exception e) {
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", e.getMessage());
                }
            }
            request.setAttribute("username", username);
            request.setAttribute("displayName", displayName);
            request.setAttribute("displayName", user.getDisplayName());

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/settings.jsp");
            rd.include(request, response);
            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        } else {
            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
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

