package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.model.User;
import io.muzoo.ssc.webapp.service.SecurityService;
import io.muzoo.ssc.webapp.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChangePasswordServlet extends HttpServlet implements Routable {

    private SecurityService securityService;

    @Override
    public String getMapping() { return "/user/password"; }

    @Override
    public void setSecurityService(SecurityService securityService) { this.securityService = securityService; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
//            String username = StringUtils.trim(request.getParameter("username")); // From query part
            String username = (String) request.getSession().getAttribute("username");
//            System.out.println(username);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            // Change password
            // Ensure that username does not obtain leading and trailing spaces
            String username = (String) request.getSession().getAttribute("username"); // From query part
            String oldPassword = request.getParameter("oldPassword");
            String password = request.getParameter("password");
            String cpassword = request.getParameter("confirmPassword");

            UserService userService = UserService.getInstance();
            User user = userService.findByUsername(username);

            String errorMsg = null;
            // Check if username exists
            if (user == null) { errorMsg = String.format("User %s does not exist.", username); }
            // Check if old password matches
            else if (!BCrypt.checkpw(oldPassword, user.getPassword())) { errorMsg = "Failed: Old password is incorrect."; }
            // Check if password valid
            else if (StringUtils.isBlank(password)) { errorMsg = "Failed: New Password cannot be blank."; }
            // Check if password is more than 5 characters
            else if (password.length() <= 5) { errorMsg = "Failed: New Password must be more than 5 characters."; }
            // Check if confirmed password is correct
            else if(!StringUtils.equals(password,cpassword)) { errorMsg = "Failed: Confirmed password mismatched"; }

            if (errorMsg != null) {
                request.getSession().setAttribute("hasError",true);
                request.getSession().setAttribute("message",errorMsg);
            } else {
                // Edit user
                try {
                    userService.changePassword(username, password);
                    // If no error redirect
                    request.getSession().setAttribute("hasError",false);
                    request.getSession().setAttribute("message",String.format("Password for user %s has been updated successfully",username));
                    response.sendRedirect("/");
                    return;
                } catch (Exception e) {
                    request.getSession().setAttribute("hasError",true);
                    request.getSession().setAttribute("message",e.getMessage());
                }
            }

            request.setAttribute("username", username);
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
}