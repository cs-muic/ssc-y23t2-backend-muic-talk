package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.model.User;
import io.muzoo.ssc.webapp.service.SecurityService;
import io.muzoo.ssc.webapp.service.ScheduleService;
import io.muzoo.ssc.webapp.service.UserService;
import io.muzoo.ssc.webapp.model.Event;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.*;

import static io.muzoo.ssc.webapp.service.ScheduleService.*;


public class ScheduleServlet extends HttpServlet implements Routable {
    private SecurityService securityService;
    private ScheduleService scheduleService;
    private List<Event> events;

    public ScheduleServlet() {
        events = new ArrayList<>();
    }


    @Override
    public String getMapping() {
        return "/schedule";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            String username = (String) request.getSession().getAttribute("username");
            UserService userService = UserService.getInstance();

            User user = userService.findByUsername(username);

            // Retrieve the user's schedule from the database
            List<Event> userSchedule = scheduleService.getUserSchedule(user.getId());

            // Store the user's schedule in memory
            events.clear();
            events.addAll(userSchedule);

            // Pass the user's schedule to the frontend
            request.setAttribute("userSchedule", userSchedule);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/schedule.jsp");
            rd.include(request, response);

            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            String username = (String) request.getSession().getAttribute("username");
            UserService userService = UserService.getInstance();

            User user = userService.findByUsername(username);

            String eventName = request.getParameter("eventName");
            String eventDateStr = request.getParameter("eventDate");
            String eventTimeStr = request.getParameter("eventTime");

            if (eventName != null && eventDateStr != null && eventTimeStr != null) {
                try {
                    // Parse eventDate and eventTime to create a single LocalDateTime object
                    LocalDate eventDate = LocalDate.parse(eventDateStr);
                    LocalTime eventTime = LocalTime.parse(eventTimeStr);
                    LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

                    // Store the event in memory
                    events.add(new Event(eventName, eventDateTime));

                    // Store the event in the database
                    boolean success = scheduleService.addEvent(user.getId(), eventName, eventDateTime);

                    if (success) {
                        // Redirect to the schedule page
                        response.sendRedirect(request.getContextPath() + "/schedule");
                        return;
                    } else {
                        // Handle error
                        request.getSession().setAttribute("hasError", true);
                        request.getSession().setAttribute("message", "Failed to add event.");
                    }
                } catch (DateTimeParseException e) {
                    // Handle invalid date or time format
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", "Invalid date or time format.");
                }
            } else {
                // Handle missing parameters
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", "Please fill out all fields.");
            }
        }

        // If execution reaches here, there was an error, so redirect back to the schedule page
        response.sendRedirect(request.getContextPath() + "/schedule");
    }

    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize scheduleService here
        this.scheduleService = new ScheduleService();
        this.scheduleService.setJdbcUrl(JDBC_URL);
        this.scheduleService.setJdbcUsername(JDBC_USERNAME);
        this.scheduleService.setJdbcPassword(JDBC_PASSWORD);
    }


}
