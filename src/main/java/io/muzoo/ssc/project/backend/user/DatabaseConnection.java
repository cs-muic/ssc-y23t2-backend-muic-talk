package io.muzoo.ssc.project.backend.user;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:mariadb://127.0.0.1:13306/login_webapp";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "securedpassword";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
    }
}
