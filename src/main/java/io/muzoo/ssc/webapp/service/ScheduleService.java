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

    // Method to retrieve a user's schedule from the database
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
