package io.muzoo.ssc.webapp.service;

import io.muzoo.ssc.webapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;

public class FriendService {

    private static final String INSERT_USER_SQL = "INSERT INTO tbl_friend (user_1, user_2, pending_user_2) VALUES (?, ?, 1);";
    private static final String SELECT_ALL_ADDED_FRIENDS_SQL = "SELECT * FROM tbl_friend WHERE user_1 = ? OR user_2 = ? AND pending_user_2 = 0;";
    private static final String SELECT_ALL_PENDING_FRIENDS_SQL = "SELECT * FROM tbl_friend WHERE user_2 = ? AND pending_user_2 = 1;";

    private static final String UPDATE_PENDING_FRIEND_REQUEST_SQL = "UPDATE tbl_friend SET pending_user_2 = 0 WHERE user_1 = ? AND user_2 = ?;";

    private DatabaseConnectionService databaseConnectionService;

    private static FriendService service;

    private FriendService() { }


    public static FriendService getInstance() {
        if (service == null) {
            service = new FriendService();
            service.setDatabaseConnectionService(DatabaseConnectionService.getInstance());
        }
        return service;
    }

    public void setDatabaseConnectionService(DatabaseConnectionService databaseConnectionService) {
        this.databaseConnectionService = databaseConnectionService;
    }

    public boolean addFriend(String user_1, String user_2) throws SQLException, UserServiceException {
        try {
            Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL);
            ps.setString(1, user_1);
            ps.setString(2, user_2);
            ps.executeUpdate();
            // so need to be manually commit the change
            connection.commit();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UserDoesNotExistException(String.format("User %s does not exist.", user_2));
        } catch (Exception throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }

    public List<User> findAllAddedFriends(String username) {
        List<User> friends = new ArrayList<>();
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_ADDED_FRIENDS_SQL);
        ) {
            ps.setString(1, username);
            ps.setString(2, username);
            ResultSet resultSet = ps.executeQuery();
            UserService userService = UserService.getInstance();

            while (resultSet.next()) {
                System.out.println("Friend found!!");
                if (!resultSet.getBoolean("pending_user_2")) {
                    if (!Objects.equals(resultSet.getString("user_1"), username)) {
                        // Should never happen !!
                        friends.add(userService.findByUsername(resultSet.getString("user_1")));
                    }
                    else {
                        friends.add(userService.findByUsername(resultSet.getString("user_2")));
                    }
                }
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return friends;
    }

    public List<User> findAllFriendRequests(String username) {
        List<User> requests = new ArrayList<>();
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_PENDING_FRIENDS_SQL);
        ) {
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            UserService userService = UserService.getInstance();

            while (resultSet.next()) {
                System.out.println("Friend found!!");
                if (resultSet.getBoolean("pending_user_2")) {
                    requests.add(userService.findByUsername(resultSet.getString("user_1")));
                }
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return requests;
    }

    public boolean acceptFriendRequest(String user_1, String user_2) throws UserServiceException {
        try {
            Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_PENDING_FRIEND_REQUEST_SQL);
            ps.setString(1, user_1);
            ps.setString(2, user_2);

            ps.executeUpdate();
            // so need to be manually commit the change
            connection.commit();
            return true;
        } catch (Exception throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }
}