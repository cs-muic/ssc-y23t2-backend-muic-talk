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

public class UserEditServlet extends HttpServlet implements Routable {
    private SecurityService securityService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            String username = (String) request.getSession().getAttribute("username");
            UserService userService = UserService.getInstance();

            // Prefill the form
            User user = userService.findByUsername(username);
            request.setAttribute("users", user);
            request.setAttribute("username",user.getUsername());
            request.setAttribute("displayName", user.getDisplayName());
            // If not success, it will arrive here
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/settings.jsp");
            rd.include(request, response);

            // Removing attributes as soon as they are used is known as flash session
            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        } else {
            // Just add some extra precaution to delete these two attributes
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
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
    public String getMapping() { return "/user/edit"; }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}

