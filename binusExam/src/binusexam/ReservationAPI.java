package binusexam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationAPI {

    public static boolean bookRoom(int userId, int roomId, Date checkIn, Date checkOut) {
        String insertReservation = "INSERT INTO reservations (user_id, room_id, check_in, check_out, status) VALUES (?, ?, ?, ?, 'booked')";
        String updateRoomAvailability = "UPDATE rooms SET is_available = 0 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertReservation);
             PreparedStatement updateStmt = conn.prepareStatement(updateRoomAvailability)) {

            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, roomId);
            insertStmt.setDate(3, new java.sql.Date(checkIn.getTime()));
            insertStmt.setDate(4, new java.sql.Date(checkOut.getTime()));
            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted > 0) {
                updateStmt.setInt(1, roomId);
                updateStmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean cancelReservation(int reservationId, int roomId) {
        String deleteReservation = "DELETE FROM reservations WHERE id = ?";
        String updateRoomAvailability = "UPDATE rooms SET is_available = 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteReservation);
             PreparedStatement updateStmt = conn.prepareStatement(updateRoomAvailability)) {

            deleteStmt.setInt(1, reservationId);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                updateStmt.setInt(1, roomId);
                updateStmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static List<String> getReservationsByUserId(int userId) {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT r.id, rm.room_number, r.check_in, r.check_out, r.status " +
                     "FROM reservations r JOIN rooms rm ON r.room_id = rm.id " +
                     "WHERE r.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservations.add("Reservation ID: " + rs.getInt("id") +
                        " | Room: " + rs.getString("room_number") +
                        " | Check-In: " + rs.getDate("check_in") +
                        " | Check-Out: " + rs.getDate("check_out") +
                        " | Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public static List<String> getAllReservations() {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT r.id, u.name AS user_name, rm.room_number, r.check_in, r.check_out, r.status " +
                     "FROM reservations r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN rooms rm ON r.room_id = rm.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                reservations.add("Reservation ID: " + rs.getInt("id") +
                        " | User: " + rs.getString("user_name") +
                        " | Room: " + rs.getString("room_number") +
                        " | Check-In: " + rs.getDate("check_in") +
                        " | Check-Out: " + rs.getDate("check_out") +
                        " | Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public static void main(String[] args) {
    System.out.println("\n Booking Room...");
    boolean booked = ReservationAPI.bookRoom(1, 2, new Date(), new Date());
    System.out.println(booked ? "Room booked successfully!" : "Booking failed!");

    System.out.println("\n Cancelling Reservation...");
    boolean cancelled = ReservationAPI.cancelReservation(3, 2);
    System.out.println(cancelled ? "Reservation cancelled successfully!" : "Cancellation failed!");

    System.out.println("\n Fetching User Reservations:");
    for (String res : ReservationAPI.getReservationsByUserId(1)) {
        System.out.println(res);
    }

    System.out.println("\n Fetching All Reservations (Admin View):");
    for (String res : ReservationAPI.getAllReservations()) {
        System.out.println(res);
    }
}
}

