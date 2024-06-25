package managers;

import models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    private static final String INSERT_USER_SQL = "INSERT INTO users (login, password) VALUES (?, ?)";
    private static final String SELECT_USER_SQL = "SELECT * FROM users WHERE login = ? AND password = ?";
    private static final String SELECT_USER_ID_SQL = "SELECT id FROM users WHERE login = ?";

    private static final UserManager INSTANCE = new UserManager();

    private UserManager() {}

    public boolean register(User user)  {
        String hashedPassword = hashPassword(user.getPassword());

        try (Connection connection = ConnectionManager.open();
             PreparedStatement stmt = connection.prepareStatement(INSERT_USER_SQL)) {
            stmt.setString(1, user.getLogin());
            stmt.setString(2, hashedPassword);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean authenticate(User user) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement stmt = connection.prepareStatement(SELECT_USER_SQL)) {
            String hashedPassword = hashPassword(user.getPassword());
            stmt.setString(1, user.getLogin());
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getUserIdByLogin(String login) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ID_SQL)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String hashPassword(String password)  {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-224");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static UserManager getInstance() {
        return INSTANCE;
    }
}
