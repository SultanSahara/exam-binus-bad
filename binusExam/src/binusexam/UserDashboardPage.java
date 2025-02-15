package binusexam;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;

public class UserDashboardPage {
    private int userId;
    private JFrame frame;
    private JTable roomsTable;
    private JTable reservationsTable;
    private JComboBox<String> roomTypeDropdown;
    private DefaultTableModel roomsModel;
    private DefaultTableModel reservationsModel;
    private HashMap<String, Integer> roomIdMap = new HashMap<>();
    private JComboBox<String> checkInDay, checkInMonth, checkInYear;
    private JComboBox<String> checkOutDay, checkOutMonth, checkOutYear;
    private JTextField searchField;


    public UserDashboardPage(int userId) {
        this.userId = userId;
        frame = new JFrame("User Dashboard");
        frame.setSize(750, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel searchLabel = new JLabel("Search Room:");
        searchLabel.setBounds(20, 10, 100, 25);
        frame.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(140, 10, 200, 25);
        frame.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(350, 10, 100, 25);
        frame.add(searchButton);

        searchButton.addActionListener(e -> loadRooms());

        JLabel roomTypeLabel = new JLabel("Filter by Room Type:");
        roomTypeLabel.setBounds(20, 50, 150, 25);
        frame.add(roomTypeLabel);

        String[] roomTypes = {"All", "Standard", "Deluxe", "Executive", "Presidential"};
        roomTypeDropdown = new JComboBox<>(roomTypes);
        roomTypeDropdown.setBounds(140, 50, 200, 25);
        frame.add(roomTypeDropdown);

        JButton filterButton = new JButton("Filter");
        filterButton.setBounds(350, 50, 100, 25);
        frame.add(filterButton);

        JLabel roomsLabel = new JLabel("Available Rooms:");
        roomsLabel.setBounds(20, 80, 200, 25);
        frame.add(roomsLabel);

        roomsModel = new DefaultTableModel(new String[]{"Room Number", "Type", "Price"}, 0);
        roomsTable = new JTable(roomsModel);
        JScrollPane roomsScrollPane = new JScrollPane(roomsTable);
        roomsScrollPane.setBounds(20, 110, 680, 150); 
        frame.add(roomsScrollPane);

        JLabel checkInLabel = new JLabel("Check-in Date:");
        checkInLabel.setBounds(20, 270, 100, 25);
        frame.add(checkInLabel);

        checkInDay = new JComboBox<>(generateNumbers(1, 31));
        checkInDay.setBounds(130, 270, 50, 25);
        frame.add(checkInDay);

        checkInMonth = new JComboBox<>(generateNumbers(1, 12));
        checkInMonth.setBounds(190, 270, 50, 25);
        frame.add(checkInMonth);

        checkInYear = new JComboBox<>(generateNumbers(2025, 2030));
        checkInYear.setBounds(250, 270, 70, 25);
        frame.add(checkInYear);

        JLabel checkOutLabel = new JLabel("Check-out Date:");
        checkOutLabel.setBounds(20, 300, 100, 25);
        frame.add(checkOutLabel);

        checkOutDay = new JComboBox<>(generateNumbers(1, 31));
        checkOutDay.setBounds(130, 300, 50, 25);
        frame.add(checkOutDay);

        checkOutMonth = new JComboBox<>(generateNumbers(1, 12));
        checkOutMonth.setBounds(190, 300, 50, 25);
        frame.add(checkOutMonth);

        checkOutYear = new JComboBox<>(generateNumbers(2025, 2030));
        checkOutYear.setBounds(250, 300, 70, 25);
        frame.add(checkOutYear);

        JButton bookButton = new JButton("Book Room");
        bookButton.setBounds(350, 285, 150, 30);
        frame.add(bookButton);

        JLabel reservationsLabel = new JLabel("Your Reservations:");
        reservationsLabel.setBounds(20, 340, 200, 25);
        frame.add(reservationsLabel);

        reservationsModel = new DefaultTableModel(new String[]{"Reservation ID", "Room", "Check-In", "Check-Out", "Status"}, 0);
        reservationsTable = new JTable(reservationsModel);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        reservationsScrollPane.setBounds(20, 370, 680, 100);
        frame.add(reservationsScrollPane);

        JButton cancelButton = new JButton("Cancel Reservation");
        cancelButton.setBounds(20, 480, 180, 30);
        frame.add(cancelButton);

        filterButton.addActionListener(e -> loadRooms());
        bookButton.addActionListener(e -> bookSelectedRoom());
        cancelButton.addActionListener(e -> cancelSelectedReservation());

        loadRooms();
        loadUserReservations();

        frame.setVisible(true);
    }

    private String[] generateNumbers(int start, int end) {
        String[] numbers = new String[end - start + 1];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = String.valueOf(start + i);
        }
        return numbers;
    }

    private void loadRooms() {
        roomsModel.setRowCount(0);
        roomIdMap.clear();

        String selectedType = (String) roomTypeDropdown.getSelectedItem();
        String searchText = searchField.getText().trim().toLowerCase();

        List<String> rooms = selectedType.equals("All") ? RoomAPI.getAvailableRooms(null) : RoomAPI.getAvailableRooms(Arrays.asList(selectedType));

        for (String room : rooms) {
            String[] roomData = room.split(" \\| "); 
            try {
                int roomId = Integer.parseInt(roomData[0].split(": ")[1]); 
                String roomNumber = roomData[1].split(": ")[1];
                String type = roomData[2].split(": ")[1];
                String price = roomData[3].split(": ")[1];

                if (!searchText.isEmpty() && !roomNumber.toLowerCase().contains(searchText)) {
                    continue; 
                }

                roomIdMap.put(roomNumber, roomId); 
                roomsModel.addRow(new Object[]{roomNumber, type, price});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUserReservations() {
        reservationsModel.setRowCount(0);
        List<String> reservations = ReservationAPI.getReservationsByUserId(userId);

        for (String res : reservations) {
            String[] resData = res.split(" \\| ");
            reservationsModel.addRow(new Object[]{
                resData[0].split(": ")[1], 
                resData[1].split(": ")[1], 
                resData[2].split(": ")[1], 
                resData[3].split(": ")[1], 
                resData[4].split(": ")[1]
            });
        }
    }

    private void bookSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to book.");
            return;
        }

        int checkInDayInt = Integer.parseInt((String) checkInDay.getSelectedItem());
        int checkInMonthInt = Integer.parseInt((String) checkInMonth.getSelectedItem()) - 1;
        int checkInYearInt = Integer.parseInt((String) checkInYear.getSelectedItem());

        int checkOutDayInt = Integer.parseInt((String) checkOutDay.getSelectedItem());
        int checkOutMonthInt = Integer.parseInt((String) checkOutMonth.getSelectedItem()) - 1;
        int checkOutYearInt = Integer.parseInt((String) checkOutYear.getSelectedItem());

        Calendar checkInCalendar = Calendar.getInstance();
        checkInCalendar.set(checkInYearInt, checkInMonthInt, checkInDayInt);
        Date checkInDate = checkInCalendar.getTime();

        Calendar checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.set(checkOutYearInt, checkOutMonthInt, checkOutDayInt);
        Date checkOutDate = checkOutCalendar.getTime();

        if (checkOutDate.before(checkInDate)) {
            JOptionPane.showMessageDialog(frame, "Check-out date must be after check-in date.");
            return;
        }

        String roomNumber = (String) roomsModel.getValueAt(selectedRow, 0);
        int roomId = roomIdMap.get(roomNumber); 

        boolean success = ReservationAPI.bookRoom(userId, roomId, checkInDate, checkOutDate);
        JOptionPane.showMessageDialog(frame, success ? "Room booked successfully!" : "Booking failed!");

        loadRooms();
        loadUserReservations();
    }

    private void cancelSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a reservation to cancel.");
            return;
        }

        int reservationId = Integer.parseInt((String) reservationsModel.getValueAt(selectedRow, 0));
        String roomNumber = (String) reservationsModel.getValueAt(selectedRow, 1);

        int roomId = RoomAPI.getRoomIdByNumber(roomNumber);
        if (roomId == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Room ID not found for " + roomNumber);
            return;
        }

        boolean success = ReservationAPI.cancelReservation(reservationId, roomId);
        JOptionPane.showMessageDialog(frame, success ? "Reservation cancelled!" : "Failed to cancel reservation.");

        loadRooms();
        loadUserReservations();
    }



}
