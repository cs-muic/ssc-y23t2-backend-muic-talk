package io.muzoo.ssc.project.backend.schedule;

import io.muzoo.ssc.project.backend.user.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(SecurityService securityService, ScheduleService scheduleService) {
        this.securityService = securityService;
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public String showSchedule(HttpServletRequest request, Model model) {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            System.out.println("Successfully authenticated in ScheduleController showSchedule");
            String username = (String) request.getSession().getAttribute("username");

            // Retrieve the user's schedule from the database
            List<Event> userSchedule = scheduleService.getUserSchedule("user_schedule_" + username.toLowerCase());
            System.out.println(userSchedule);

            // Pass the user's schedule to the frontend
            model.addAttribute("userSchedule", userSchedule);

            return "schedule";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping
    public String addEvent(HttpServletRequest request,
                           @RequestParam String eventName,
                           @RequestParam String eventDate,
                           @RequestParam String eventTime) {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            System.out.println("success, doPost-ScheduleController");
            String username = (String) request.getSession().getAttribute("username");

            if (eventName != null && eventDate != null && eventTime != null) {
                try {
                    // Parse eventDate and eventTime to create a single LocalDateTime object
                    LocalDate parsedEventDate = LocalDate.parse(eventDate);
                    LocalTime parsedEventTime = LocalTime.parse(eventTime);
                    LocalDateTime eventDateTime = LocalDateTime.of(parsedEventDate, parsedEventTime);

                    // Store the event in the database
                    boolean success = scheduleService.addEvent("user_schedule_" + username.toLowerCase(), eventName, eventDateTime);

                    if (success) {
                        // Redirect to the schedule page
                        return "redirect:/schedule";
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

        // If execution reaches here, there was an error, so redirect to the schedule page
        return "redirect:/schedule";
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(HttpServletRequest request, @PathVariable int eventId) {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            System.out.println("Success, doDelete-ScheduleController");
            String username = (String) request.getSession().getAttribute("username");

            // Delete the event from the user's schedule
            scheduleService.deleteEvent("user_schedule_" + username.toLowerCase(), eventId);
        }
        // Note: You might want to handle the response accordingly, such as returning a ResponseEntity.
    }
}
