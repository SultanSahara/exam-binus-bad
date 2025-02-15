package binusexam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthAccount {
    public static int loggedInUserId = -1;
    public static boolean registerUser(String name, String email, String password, String phone) {
        String checkEmailQuery = "SELECT email FROM users WHERE email = ?";
        String insertQuery = "INSERT INTO users (name, email, password, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkEmailQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Email already registered!");
                return false;
            }

            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.setString(4, phone);
            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Registration successful!");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static String login(String email, String password) {
        String userQuery = "SELECT id FROM users WHERE email = ? AND password = ?";
        String adminQuery = "SELECT id FROM admin WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userQuery);
             PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {

            userStmt.setString(1, email);
            userStmt.setString(2, password);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                loggedInUserId = userRs.getInt("id");
                return "USER";
            }

            adminStmt.setString(1, email);
            adminStmt.setString(2, password);
            ResultSet adminRs = adminStmt.executeQuery();
            if (adminRs.next()) {
                loggedInUserId = adminRs.getInt("id");
                return "ADMIN";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "INVALID";
    }

}

