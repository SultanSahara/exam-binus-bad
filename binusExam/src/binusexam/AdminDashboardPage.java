package binusexam;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminDashboardPage {
    private JFrame frame;
    private JTable roomsTable;
    private DefaultTableModel roomsModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterDropdown;
    private DefaultTableModel reservationsModel;
    private JTable reservationsTable;


    public AdminDashboardPage() {
        frame = new JFrame("Admin Dashboard - View Rooms");
        frame.setSize(750, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel searchLabel = new JLabel("Search Room:");
        searchLabel.setBounds(20, 10, 100, 25);
        frame.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(130, 10, 150, 25);
        frame.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(290, 10, 100, 25);
        frame.add(searchButton);

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setBounds(20, 50, 100, 25);
        frame.add(filterLabel);

        String[] statuses = {"All", "Available", "Occupied"};
        statusFilterDropdown = new JComboBox<>(statuses);
        statusFilterDropdown.setBounds(130, 50, 150, 25);
        frame.add(statusFilterDropdown);

        JButton filterButton = new JButton("Filter");
        filterButton.setBounds(290, 50, 100, 25);
        frame.add(filterButton);

        JLabel roomsLabel = new JLabel("All Rooms:");
        roomsLabel.setBounds(20, 90, 200, 25);
        frame.add(roomsLabel);

        roomsModel = new DefaultTableModel(new String[]{"Room Number", "Type", "Price", "Status"}, 0);
        roomsTable = new JTable(roomsModel);
        JScrollPane roomsScrollPane = new JScrollPane(roomsTable);
        roomsScrollPane.setBounds(20, 120, 700, 300);
        frame.add(roomsScrollPane);
        
        JButton createRoomButton = new JButton("Create Room");
        createRoomButton.setBounds(20, 430, 150, 30);
        frame.add(createRoomButton);
        
        JButton editRoomButton = new JButton("Edit Room");
        editRoomButton.setBounds(180, 430, 150, 30);
        frame.add(editRoomButton);

        JButton deleteRoomButton = new JButton("Delete Room");
        deleteRoomButton.setBounds(340, 430, 150, 30);
        frame.add(deleteRoomButton);
        
        JLabel reservationsLabel = new JLabel("All Reservations:");
        reservationsLabel.setBounds(20, 500, 200, 25);
        frame.add(reservationsLabel);
        
        JLabel searchReservationLabel = new JLabel("Search Reservation:");
        searchReservationLabel.setBounds(20, 475, 150, 25); 
        frame.add(searchReservationLabel);

        JTextField searchReservationField = new JTextField();
        searchReservationField.setBounds(180, 475, 200, 25);
        frame.add(searchReservationField);

        JButton searchReservationButton = new JButton("Search");
        searchReservationButton.setBounds(390, 475, 100, 25);
        frame.add(searchReservationButton);

        reservationsModel = new DefaultTableModel(new String[]{"Reservation ID", "User", "Room", "Check-In", "Check-Out", "Status"}, 0);
        reservationsTable = new JTable(reservationsModel);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        reservationsScrollPane.setBounds(20, 535, 700, 150);
        frame.add(reservationsScrollPane);

        JButton cancelReservationButton = new JButton("Cancel Reservation");
        cancelReservationButton.setBounds(20, 695, 180, 30);
        frame.add(cancelReservationButton);

        loadReservations(""); 
        searchReservationButton.addActionListener(e -> {
        String searchQuery = searchReservationField.getText().trim();
        loadReservations(searchQuery);
        });

        cancelReservationButton.addActionListener(e -> cancelSelectedReservation());

        loadRooms();
        createRoomButton.addActionListener(e -> openCreateRoomDialog());
        deleteRoomButton.addActionListener(e -> deleteSelectedRoom());
        editRoomButton.addActionListener(e -> openEditRoomDialog());
        searchButton.addActionListener(e -> loadRooms());
        filterButton.addActionListener(e -> loadRooms());

        frame.setVisible(true);
    }

    private void loadRooms() {
        roomsModel.setRowCount(0);

        String searchText = searchField.getText().trim().toLowerCase();
        String selectedStatus = (String) statusFilterDropdown.getSelectedItem();

        List<String> rooms = RoomAPI.getAllRooms(null);

        for (String room : rooms) {
            String[] roomData = room.split(" \\| ");
            String roomNumber = roomData[0].split(": ")[1];
            String type = roomData[1].split(": ")[1];
            String price = roomData[2].split(": ")[1];
            String status = roomData[3].split(": ")[1];

            if (!searchText.isEmpty() && !roomNumber.toLowerCase().contains(searchText)) {
                continue;
            }

            if (!selectedStatus.equals("All") && !status.equalsIgnoreCase(selectedStatus)) {
                continue;
            }

            roomsModel.addRow(new Object[]{roomNumber, type, price, status});
        }
    }
    
    private void loadReservations(String searchQuery) {
        reservationsModel.setRowCount(0);

        List<String> reservations = ReservationAPI.getAllReservations();

        for (String res : reservations) {
            if (searchQuery.isEmpty() || res.toLowerCase().contains(searchQuery.toLowerCase())) {
                String[] resData = res.split(" \\| ");
                reservationsModel.addRow(new Object[]{
                        resData[0].split(": ")[1],  
                        resData[1].split(": ")[1],  
                        resData[2].split(": ")[1],  
                        resData[3].split(": ")[1],  
                        resData[4].split(": ")[1],  
                        resData[5].split(": ")[1]   
                });
            }
        }
    }

    private void openCreateRoomDialog() {
        JDialog createDialog = new JDialog(frame, "Create Room", true);
        createDialog.setSize(350, 250);
        createDialog.setLayout(null);

        JLabel roomNumberLabel = new JLabel("Room Number:");
        roomNumberLabel.setBounds(20, 20, 100, 25);
        createDialog.add(roomNumberLabel);

        JTextField roomNumberField = new JTextField();
        roomNumberField.setBounds(140, 20, 150, 25);
        createDialog.add(roomNumberField);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(20, 60, 100, 25);
        createDialog.add(typeLabel);

        JComboBox<String> typeDropdown = new JComboBox<>(new String[]{"Standard", "Deluxe", "Executive", "Presidential"});
        typeDropdown.setBounds(140, 60, 150, 25);
        createDialog.add(typeDropdown);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(20, 100, 100, 25);
        createDialog.add(priceLabel);

        JTextField priceField = new JTextField();
        priceField.setBounds(140, 100, 150, 25);
        createDialog.add(priceField);

        JButton saveButton = new JButton("Create");
        saveButton.setBounds(90, 150, 80, 30);
        createDialog.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(180, 150, 80, 30);
        createDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            String roomNumber = roomNumberField.getText().trim();
            String type = (String) typeDropdown.getSelectedItem();
            String priceText = priceField.getText().trim();

            if (roomNumber.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(createDialog, "Fields cannot be empty.");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);

                boolean success = RoomAPI.createRoom(roomNumber, type, price);
                if (success) {
                    JOptionPane.showMessageDialog(createDialog, "Room created successfully!");
                    createDialog.dispose();
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(createDialog, "Room creation failed.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(createDialog, "Price must be a number.");
            }
        });

        cancelButton.addActionListener(e -> createDialog.dispose());

        createDialog.setVisible(true);
    }
    
    private void openEditRoomDialog() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to edit.");
            return;
        }

        String roomNumber = (String) roomsModel.getValueAt(selectedRow, 0);
        String type = (String) roomsModel.getValueAt(selectedRow, 1);
        String price = (String) roomsModel.getValueAt(selectedRow, 2);

        int roomId = RoomAPI.getRoomIdByNumber(roomNumber);
        if (roomId == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Room ID not found.");
            return;
        }

        JDialog editDialog = new JDialog(frame, "Edit Room", true);
        editDialog.setSize(350, 250);
        editDialog.setLayout(null);

        JLabel roomNumberLabel = new JLabel("Room Number:");
        roomNumberLabel.setBounds(20, 20, 100, 25);
        editDialog.add(roomNumberLabel);

        JTextField roomNumberField = new JTextField(roomNumber);
        roomNumberField.setBounds(140, 20, 150, 25);
        editDialog.add(roomNumberField);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(20, 60, 100, 25);
        editDialog.add(typeLabel);

        JComboBox<String> typeDropdown = new JComboBox<>(new String[]{"Standard", "Deluxe", "Executive", "Presidential"});
        typeDropdown.setSelectedItem(type);
        typeDropdown.setBounds(140, 60, 150, 25);
        editDialog.add(typeDropdown);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(20, 100, 100, 25);
        editDialog.add(priceLabel);

        JTextField priceField = new JTextField(price);
        priceField.setBounds(140, 100, 150, 25);
        editDialog.add(priceField);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(90, 150, 80, 30);
        editDialog.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(180, 150, 80, 30);
        editDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            String newRoomNumber = roomNumberField.getText().trim();
            String newType = (String) typeDropdown.getSelectedItem();
            String newPrice = priceField.getText().trim();

            if (newRoomNumber.isEmpty() || newPrice.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog, "Fields cannot be empty.");
                return;
            }

            try {
                double newPriceValue = Double.parseDouble(newPrice);

                boolean success = RoomAPI.updateRoomDetails(roomId, newRoomNumber, newType, newPriceValue);
                if (success) {
                    JOptionPane.showMessageDialog(editDialog, "Room updated successfully!");
                    editDialog.dispose();
                    loadRooms(); 
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Update failed.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Price must be a number.");
            }
        });

        cancelButton.addActionListener(e -> editDialog.dispose());

        editDialog.setVisible(true);
    }

    private void cancelSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a reservation to cancel.");
            return;
        }

        int reservationId = Integer.parseInt((String) reservationsModel.getValueAt(selectedRow, 0));
        String roomNumber = (String) reservationsModel.getValueAt(selectedRow, 2);

        int roomId = RoomAPI.getRoomIdByNumber(roomNumber);
        if (roomId == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Room ID not found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel this reservation?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = ReservationAPI.cancelReservation(reservationId, roomId);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Reservation cancelled successfully!");
                loadReservations("");
                loadRooms();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to cancel reservation.");
            }
        }
    }

    private void deleteSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to delete.");
            return;
        }

        String roomNumber = (String) roomsModel.getValueAt(selectedRow, 0);
        int roomId = RoomAPI.getRoomIdByNumber(roomNumber);

        if (roomId == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Room ID not found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this room?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = RoomAPI.deleteRoom(roomId);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Room deleted successfully!");
                loadRooms();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete room.");
            }
        }
    }

}