package com.durga.miniproject;

import java.sql.*;
import java.util.Scanner;

public class BookingSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/bus_booking_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Root@25";
    private static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Add Bus");
                System.out.println("2. Book Seats");
                System.out.println("3. View Buses");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        addBus(scanner);
                        break;
                    case 2:
                        bookSeats(scanner);
                        break;
                    case 3:
                        viewBuses();
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addBus(Scanner scanner) throws SQLException {
        System.out.print("Enter bus name: ");
        String busName = scanner.next();
        System.out.print("Enter source: ");
        String source = scanner.next();
        System.out.print("Enter destination: ");
        String destination = scanner.next();
        System.out.print("Enter total seats: ");
        int totalSeats = scanner.nextInt();

        String query = "INSERT INTO buses (bus_name, source, destination, total_seats) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, busName);
        pstmt.setString(2, source);
        pstmt.setString(3, destination);
        pstmt.setInt(4, totalSeats);
        pstmt.executeUpdate();
        System.out.println("Bus added successfully.");
    }

    private static void bookSeats(Scanner scanner) throws SQLException {
        System.out.print("Enter bus ID: ");
        int busId = scanner.nextInt();
        System.out.print("Enter passenger name: ");
        String passengerName = scanner.next();
        System.out.print("Enter number of seats to book: ");
        int seatsBooked = scanner.nextInt();

        String query = "INSERT INTO bookings (bus_id, passenger_name, seats_booked) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, busId);
        pstmt.setString(2, passengerName);
        pstmt.setInt(3, seatsBooked);
        pstmt.executeUpdate();
        System.out.println("Seats booked successfully.");
    }

    private static void viewBuses() throws SQLException {
        String query = "SELECT * FROM buses";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            System.out.println("Bus ID: " + rs.getInt("bus_id"));
            System.out.println("Bus Name: " + rs.getString("bus_name"));
            System.out.println("Source: " + rs.getString("source"));
            System.out.println("Destination: " + rs.getString("destination"));
            System.out.println("Total Seats: " + rs.getInt("total_seats"));
            System.out.println("---------------------------");
        }
    }
}
