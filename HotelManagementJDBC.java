import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class HotelManagementJDBC {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String customerName;
    private String roomType;
    private String bookingTime;

    public HotelManagementJDBC(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
        this.bookingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }

    public void saveBooking() {
        String insertQuery = "INSERT INTO bookings (customer_name, room_type, booking_time) VALUES (?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                preparedStatement.setString(1, this.customerName);
                preparedStatement.setString(2, this.roomType);
                preparedStatement.setString(3, this.bookingTime);

                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println(rowsAffected > 0 ? "Booking saved successfully!" : "Failed to save booking.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Add the driver to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error while interacting with the database.");
            e.printStackTrace();
        }
    }

    public static void displayBookings() {
        String selectQuery = "SELECT * FROM bookings";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                System.out.println("Booking Details:");
                while (resultSet.next()) {
                    int id = resultSet.getInt("booking_id");
                    String name = resultSet.getString("customer_name");
                    String room = resultSet.getString("room_type");
                    String time = resultSet.getString("booking_time");
                    System.out.printf("ID: %d, Name: %s, Room Type: %s, Booking Time: %s%n", id, name, room, time);
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Add the driver to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error while interacting with the database.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Hotel Management System");
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Booking");
            System.out.println("2. Display Bookings");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter room type (e.g., Single, Double): ");
                    String roomType = scanner.nextLine();

                    HotelManagementJDBC booking = new HotelManagementJDBC(name, roomType);
                    booking.saveBooking();
                    break;
                case 2:
                    displayBookings();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
