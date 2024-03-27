package io.muzoo.ssc.webapp.service;

import io.muzoo.ssc.webapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;

public class FriendService {

    private static final String INSERT_USER_SQL = "INSERT INTO tbl_friend (user_1, user_2, pending_user_2) VALUES (?, ?, 1);";

    private DatabaseConnectionService databaseConnectionService;

    private static UserService service;

    private FriendService() { }


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

    public void addFriend(String user_1, String user_2) throws SQLException, UserServiceException {
        try {
            Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL);
            ps.setString(1, user_1);
            ps.setString(2, user_2);
            ps.executeUpdate();
            // so need to be manually commit the change
            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UserDoesNotExistException(String.format("User %s does not exist.", user_2));
        } catch (Exception throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }
}
