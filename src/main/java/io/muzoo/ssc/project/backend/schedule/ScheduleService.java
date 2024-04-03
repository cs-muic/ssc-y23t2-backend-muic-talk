package io.muzoo.ssc.project.backend.schedule;

import io.muzoo.ssc.project.backend.schedule.Event;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

//    @Value("${jdbc.url}")
//    private String JDBC_URL;
//
//    @Value("${jdbc.username}")
//    private String JDBC_USERNAME;
//
//    @Value("${jdbc.password}")
//    private String JDBC_PASSWORD;

    private static final String JDBC_URL = "jdbc:mariadb://127.0.0.1:13306/login_webapp";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "securedpassword";

    public boolean addEvent(String tableName, String eventName, LocalDateTime eventDateTime) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            System.out.println("connection success, addEvent");
            String sql = "INSERT INTO " + tableName + " (event_name, event_datetime) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, eventName);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(eventDateTime));
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getUserSchedule(String tableName) {
        List<Event> userSchedule = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            System.out.println("connection success, getUserSchedule");
            String sql = "SELECT * FROM " + tableName;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int eventId = resultSet.getInt("event_id");
                String eventName = resultSet.getString("event_name");
                LocalDateTime eventDateTime = resultSet.getTimestamp("event_datetime").toLocalDateTime();
                userSchedule.add(new Event(eventId, eventName, eventDateTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userSchedule;
    }

    public boolean deleteEvent(String tableName, int eventId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            String sql = "DELETE FROM " + tableName + " WHERE event_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, eventId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
