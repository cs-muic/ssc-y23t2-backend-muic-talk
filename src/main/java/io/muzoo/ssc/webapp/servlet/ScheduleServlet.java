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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.time.*;

import static io.muzoo.ssc.webapp.service.ScheduleService.*;


public class ScheduleServlet extends HttpServlet implements Routable {
    private SecurityService securityService;
    private ScheduleService scheduleService;

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
            System.out.println("Successfully authenticated in ScheduleServlet doGet");
            String username = (String) request.getSession().getAttribute("username");

            // Retrieve the user's schedule from the database
            List<Event> userSchedule = scheduleService.getUserSchedule("user_schedule_" + username.toLowerCase());
            System.out.println(userSchedule);

            // Pass the user's schedule to the frontend
            request.setAttribute("userSchedule", userSchedule);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/schedule.jsp");
            rd.forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }


//    // doGet method
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        boolean authorized = securityService.isAuthorized(request);
//        if (authorized) {
//            System.out.println("Successfully authenticated in ScheduleServlet doGet");
//            String username = (String) request.getSession().getAttribute("username");
//
//            // Retrieve the user's schedule from the database
//            List<Event> userSchedule = scheduleService.getUserSchedule("user_schedule_" + username.toLowerCase());
//
//            // Log the retrieved events
//            System.out.println("Retrieved event from user_schedule_" + username.toLowerCase() + ": " + userSchedule);
//            // Log the retrieved events
//            System.out.println("Retrieved events: " + userSchedule);
//
//            // Pass the user's schedule to the frontend
//            request.setAttribute("userSchedule", userSchedule);
//
//            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/schedule.jsp");
//            rd.forward(request, response);
//        } else {
//            response.sendRedirect(request.getContextPath() + "/login");
//        }
//    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            System.out.println("success, doPost-ScheduleServlet");
            String username = (String) request.getSession().getAttribute("username");

            String eventName = request.getParameter("eventName");
            String eventDateStr = request.getParameter("eventDate");
            String eventTimeStr = request.getParameter("eventTime");

            if (eventName != null && eventDateStr != null && eventTimeStr != null) {
                try {
                    // Parse eventDate and eventTime to create a single LocalDateTime object
                    LocalDate eventDate = LocalDate.parse(eventDateStr);
                    LocalTime eventTime = LocalTime.parse(eventTimeStr);
                    LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

                    // Store the event in the database
                    boolean success = scheduleService.addEvent("user_schedule_" + username.toLowerCase(), eventName, eventDateTime);

                    if (success) {
                        // Fetch the updated schedule from the database
                        List<Event> userSchedule = scheduleService.getUserSchedule("user_schedule_" + username.toLowerCase());
                        // Pass the updated schedule to the frontend
                        request.setAttribute("userSchedule", userSchedule);
                        // Forward to the schedule page
                        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/schedule.jsp");
                        rd.forward(request, response);
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

        // If execution reaches here, there was an error, so forward to the schedule page without a redirect
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/schedule.jsp");
        rd.forward(request, response);
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

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            System.out.println("Success, doDelete-ScheduleServlet");
            String username = (String) request.getSession().getAttribute("username");

            // Retrieve the eventId parameter from the request
            String eventIdParameter = request.getParameter("eventId");
            System.out.println(eventIdParameter);

            if (eventIdParameter != null && !eventIdParameter.isEmpty()) {
                try {
                    int eventId = Integer.parseInt(eventIdParameter);

                    // Delete the event from the user's schedule
                    boolean success = scheduleService.deleteEvent("user_schedule_" + username.toLowerCase(), eventId);

                    if (success) {
                        // Send a success response
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        // Send an error response
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid eventId parameter
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                // eventId parameter is missing
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // Unauthorized request
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
