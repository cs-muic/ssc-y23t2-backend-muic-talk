package io.muzoo.ssc.webapp.service;

import io.muzoo.ssc.webapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;

public class UserService {

    private static final String INSERT_USER_SQL = "INSERT INTO tbl_user (username, display_name, password, role) VALUES (?, ?, ?, ?);";
    private static final String SELECT_USER_SQL = "SELECT * FROM tbl_user WHERE username = ?";
    private static final String SELECT_ALL_USER_SQL = "SELECT * FROM tbl_user";
    private static final String DELETE_USER_SQL = "DELETE FROM tbl_user WHERE username =?";
    private static final String UPDATE_USER_SQL = "UPDATE tbl_user SET display_name = ? WHERE username = ?";
    private static final String UPDATE_USER_PASSWORD_SQL = "UPDATE tbl_user SET password = ? WHERE username = ?";

    private DatabaseConnectionService databaseConnectionService;

    private static UserService service;

    private UserService() {

    }

    public static UserService getInstance() {
        if (service == null) {
            service = new UserService();
            service.setDatabaseConnectionService(DatabaseConnectionService.getInstance());
        }
        return service;
    }

    public void setDatabaseConnectionService(DatabaseConnectionService databaseConnectionService) {
        this.databaseConnectionService = databaseConnectionService;
    }

    // create new user
    public void createUser(String username, String password, String displayName, String role) throws SQLException, UserServiceException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL);
            ps.setString(1, username);
            ps.setString(2, displayName);
            ps.setString(3, hashedPassword);
            ps.setString(4, role);
            ps.executeUpdate();
            // so need to be manually commit the change
            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UsernameNotUniqueException(String.format("Username %s has already been taken.", username));
        } catch (Exception throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }

    // find user by name
    public User findByUsername(String username) {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_USER_SQL);
        ) {
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return new User(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("display_name"),
                    resultSet.getString("role")
            );
        } catch (Exception throwables) {
            return null;
        }
    }

    /**
     * list all users in the database
     * @return list of users, never return null
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USER_SQL);
        ) {
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                users.add(
                        new User(
                                resultSet.getLong("id"),
                                resultSet.getString("username"),
                                resultSet.getString("password"),
                                resultSet.getString("display_name"),
                                resultSet.getString("role")
                        ));
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return users;
    }

    /**
     * delete user by user id
     * @param username
     * @return true if successful
     */
    public boolean deleteUserByUsername(String username) {
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_USER_SQL);
        ) {
            ps.setString(1, username);
            int deleteCount = ps.executeUpdate();
            connection.commit();
            return deleteCount > 0;
        } catch (SQLException throwables) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Users can only change their display name when updating profile
     * @param username
     * @param displayName
     */
    public void updateUserByUsername(String username, String displayName) throws UserServiceException {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL);
        ) {
            ps.setString(1, displayName);
            // setting display name column 3
            ps.setString(2, username);
            ps.executeUpdate();
            // so need to be manually commit the change
            connection.commit();
        } catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Change password method is separated from update user method because user normally
     * never change password and update profile at the same time
     * @param newPassword
     */
    public void changePassword(String username, String newPassword)  throws UserServiceException {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_PASSWORD_SQL);
        ) {
            ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            ps.setString(2, username);
            ps.executeUpdate();
            // so need to be manually commit the change
            connection.commit();
        } catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) throws UserServiceException {
//        UserService userService = UserService.getInstance();
//        try {
//            userService.createUser("u6480952", "Ling", "Kanladaporn", "Student");
//
//        } catch(UserServiceException | SQLException e ) {
//            e.printStackTrace();
//        }
//
//    }

}
