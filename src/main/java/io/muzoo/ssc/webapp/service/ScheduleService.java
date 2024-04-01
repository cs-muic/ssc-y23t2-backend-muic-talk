package io.muzoo.ssc.webapp.service;

import io.muzoo.ssc.webapp.model.Event;

import java.sql.*;
import java.time.*;
import java.util.*;

public class ScheduleService {


    public static String JDBC_URL = "jdbc:mariadb://localhost:13306/muic_talk_webapp";
    public static String JDBC_USERNAME = "ssc";
    public static String JDBC_PASSWORD = "hardpass";

    // Method to add an event to the database
    public boolean addEvent(int userId, String eventName, LocalDateTime eventDateTime) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            String sql = "INSERT INTO events (user_id, event_name, event_datetime) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, eventName);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(eventDateTime));
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to retrieve a user's schedule from the database
    public List<Event> getUserSchedule(long userId) {
        List<Event> userSchedule = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM events WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
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


    public void setJdbcUrl(String jdbcUrl) {
        this.JDBC_URL = jdbcUrl;
    }

    public void setJdbcUsername(String jdbcUsername) {
        this.JDBC_USERNAME = jdbcUsername;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.JDBC_PASSWORD = jdbcPassword;
    }

}
