package binusexam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections; 

public class RoomAPI {
public static List<String> getAvailableRooms(List<String> types) {
    List<String> rooms = new ArrayList<>();
    String sql = "SELECT id, room_number, type, price FROM rooms WHERE is_available = TRUE";

    if (types != null && !types.isEmpty()) {
        String placeholders = String.join(", ", Collections.nCopies(types.size(), "?"));
        sql += " AND type IN (" + placeholders + ")";
    }

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        if (types != null && !types.isEmpty()) {
            for (int i = 0; i < types.size(); i++) {
                pstmt.setString(i + 1, types.get(i));
            }
        }

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int roomId = rs.getInt("id");
            String roomNumber = rs.getString("room_number");
            String type = rs.getString("type");
            double price = rs.getDouble("price");

            rooms.add("ID: " + roomId + " | Room: " + roomNumber + " | Type: " + type + " | Price: " + price);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rooms;
}

    public static List<String> getAllRooms(List<String> types) {
        List<String> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";

        if (types != null && !types.isEmpty()) {
            String placeholders = types.stream().map(t -> "?").collect(Collectors.joining(", "));
            sql += " WHERE type IN (" + placeholders + ")";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (types != null && !types.isEmpty()) {
                for (int i = 0; i < types.size(); i++) {
                    pstmt.setString(i + 1, types.get(i));
                }
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String status = rs.getBoolean("is_available") ? "Available" : "Occupied";
                rooms.add("Room: " + rs.getString("room_number") +
                          " | Type: " + rs.getString("type") +
                          " | Price: " + rs.getDouble("price") +
                          " | Status: " + status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    public static String getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getBoolean("is_available") ? "Available" : "Occupied";
                return "Room: " + rs.getString("room_number") +
                       " | Type: " + rs.getString("type") +
                       " | Price: " + rs.getDouble("price") +
                       " | Status: " + status;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Room not found!";
    }
    
    public static int getRoomIdByNumber(String roomNumber) {
        String query = "SELECT id FROM rooms WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean updateRoomAvailability(int roomId, boolean isAvailable) {
        String sql = "UPDATE rooms SET is_available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, roomId);
            int rowsUpdated = pstmt.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateRoomDetails(int roomId, String newRoomNumber, String newType, double newPrice) {
        String sql = "UPDATE rooms SET room_number = ?, type = ?, price = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRoomNumber);
            pstmt.setString(2, newType);
            pstmt.setDouble(3, newPrice);
            pstmt.setInt(4, roomId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createRoom(String roomNumber, String type, double price) {
        String sql = "INSERT INTO rooms (room_number, type, price, is_available) VALUES (?, ?, ?, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, price);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

