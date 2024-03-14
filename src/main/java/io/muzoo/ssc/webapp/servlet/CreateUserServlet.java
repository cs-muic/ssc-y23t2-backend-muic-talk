package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.service.SecurityService;
import io.muzoo.ssc.webapp.service.UserService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateUserServlet extends HttpServlet implements Routable {
    private SecurityService securityService;
    @Override
    public String getMapping() {
        return "/user/create";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            // Do MVC in here
            String username = (String) request.getSession().getAttribute("username");

            request.setAttribute("user", username);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/login.jsp");
            rd.include(request, response);

            rd.forward(request, response);

            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        } else {
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            String username = StringUtils.trim((String) request.getParameter("username"));
            String displayName = StringUtils.trim((String) request.getParameter("displayName"));
            String password = (String) request.getParameter("password");
            String cpassword = (String) request.getParameter("cpassword");

            UserService userService = UserService.getInstance();
            String errorMessage = null;

            System.out.println("Creating user");
            // Check if username is valid
            if (userService.findByUsername(username) != null) {
                errorMessage = String.format("Username %s has already been taken.", username);
            }

            // Username must start with 'u'
            else if (!username.startsWith("u")) {
                errorMessage = "Username must start with the letter 'u'.";
            }

            // Confirm password match
            else if (!StringUtils.equals(password,cpassword)) {
                errorMessage = "Confirmed password mismatches";
            }

            if(errorMessage != null){
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", errorMessage);

            } else {
                // Create a user
                try{
                    // If no error redirect
                    userService.createUser(username, password, displayName);
                    System.out.println("User created");
                    request.getSession().setAttribute("hasError", false);
                    request.getSession().setAttribute("message", String.format("User %s has been created successfully", username));
                    response.sendRedirect("/");

                } catch (Exception e){
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", e.getMessage());

                }
            }
            request.setAttribute("username",username);
            request.setAttribute("displayName",displayName);
            request.setAttribute("password",password);
            request.setAttribute("cpassword",cpassword);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/login.jsp");
            rd.include(request, response);

            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        } else {
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }
    }
}

