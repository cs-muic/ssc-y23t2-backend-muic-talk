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
    public String getMapping() { return "/user/edit"; }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}

