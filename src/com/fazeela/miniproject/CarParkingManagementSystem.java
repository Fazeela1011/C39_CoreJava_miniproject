package com.fazeela.miniproject;

import java.sql.*;
import java.util.Scanner;

public class CarParkingManagementSystem {

    private static final String URL = "jdbc:mysql://localhost:3306/parking_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Root@25"; 

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void displaySlots() throws SQLException {
        Connection conn = getConnection();
        String query = "SELECT * FROM ParkingSlots LIMIT 100"; // Fetch up to 100 slots
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        
        System.out.println("Displaying up to 100 slots:");
        while (rs.next()) {
            int slotId = rs.getInt("slot_id");
            String location = rs.getString("location");
            String status = rs.getString("status");
            System.out.println("Slot ID: " + slotId + ", Location: " + location + ", Status: " + status);
        }
        conn.close();
    }

    public boolean isSlotAvailable(int slotId) throws SQLException {
        Connection conn = getConnection();
        String query = "SELECT status FROM ParkingSlots WHERE slot_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, slotId);
        ResultSet rs = pstmt.executeQuery();
        boolean available = false;
        if (rs.next()) {
            available = "Available".equals(rs.getString("status"));
        }
        conn.close();
        return available;
    }

    public void bookSlot(int slotId, String carNumber, Timestamp startTime, Timestamp endTime, String holderName, String address, String verificationId) throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);

        try {
            String updateSlot = "UPDATE ParkingSlots SET status = 'Occupied' WHERE slot_id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(updateSlot);
            pstmt1.setInt(1, slotId);
            pstmt1.executeUpdate();

            String insertBooking = "INSERT INTO Bookings (slot_id, car_number, start_time, end_time, holder_name, address, verification_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt2 = conn.prepareStatement(insertBooking);
            pstmt2.setInt(1, slotId);
            pstmt2.setString(2, carNumber);
            pstmt2.setTimestamp(3, startTime);
            pstmt2.setTimestamp(4, endTime);
            pstmt2.setString(5, holderName);
            pstmt2.setString(6, address);
            pstmt2.setString(7, verificationId);
            pstmt2.executeUpdate();

            conn.commit();
            System.out.println("Slot booked successfully.");
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("Failed to book the slot.");
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public void viewBookings() throws SQLException {
        Connection conn = getConnection();
        String query = "SELECT * FROM Bookings";
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("Booking ID: " + rs.getInt("booking_id"));
            System.out.println("Slot ID: " + rs.getInt("slot_id"));
            System.out.println("Car Number: " + rs.getString("car_number"));
            System.out.println("Start Time: " + rs.getTimestamp("start_time"));
            System.out.println("End Time: " + rs.getTimestamp("end_time"));
            System.out.println("Holder Name: " + rs.getString("holder_name"));
            System.out.println("Address: " + rs.getString("address"));
            System.out.println("Verification ID: " + rs.getString("verification_id"));
            System.out.println("-----------------------------");
        }
        conn.close();
    }

    public static void main(String[] args) {
        CarParkingManagementSystem system = new CarParkingManagementSystem();
        Scanner scanner = new Scanner(System.in);
        try {
            system.displaySlots(); // Display up to 100 slots
            
            System.out.print("Choose a slot ID from the displayed slots: ");
            int slotId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            if (system.isSlotAvailable(slotId)) {
                System.out.print("Enter car number: ");
                String carNumber = scanner.nextLine();
                System.out.print("Enter your name: ");
                String holderName = scanner.nextLine();
                System.out.print("Enter your address: ");
                String address = scanner.nextLine();
                System.out.print("Enter your verification ID: ");
                String verificationId = scanner.nextLine();
                Timestamp startTime = new Timestamp(System.currentTimeMillis());
                Timestamp endTime = new Timestamp(System.currentTimeMillis() + 3600000); // 1 hour later

                system.bookSlot(slotId, carNumber, startTime, endTime, holderName, address, verificationId);
            } else {
                System.out.println("Slot is not available.");
            }

            system.viewBookings();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}

