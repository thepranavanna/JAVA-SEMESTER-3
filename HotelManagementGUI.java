import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class HotelManagementGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private JTextField customerNameField;
    private JTextField roomTypeField;
    private JTextArea outputArea;

    public HotelManagementGUI() {
        // Set up the main GUI frame
        setTitle("Hotel Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Booking"));

        inputPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField();
        inputPanel.add(customerNameField);

        inputPanel.add(new JLabel("Room Type:"));
        roomTypeField = new JTextField();
        inputPanel.add(roomTypeField);

        JButton addButton = new JButton("Add Booking");
        inputPanel.add(addButton);

        JButton displayButton = new JButton("Display Bookings");
        inputPanel.add(displayButton);

        // Add input panel to frame
        add(inputPanel, BorderLayout.NORTH);

        // Create output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> addBooking());
        displayButton.addActionListener(e -> displayBookings());
    }

    private void addBooking() {
        String customerName = customerNameField.getText();
        String roomType = roomTypeField.getText();
        String bookingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        if (customerName.isEmpty() || roomType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String insertQuery = "INSERT INTO bookings (customer_name, room_type, booking_time) VALUES (?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                preparedStatement.setString(1, customerName);
                preparedStatement.setString(2, roomType);
                preparedStatement.setString(3, bookingTime);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Booking saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save booking.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error while interacting with the database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayBookings() {
        String selectQuery = "SELECT * FROM bookings";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                StringBuilder bookingDetails = new StringBuilder("Booking Details:\n");
                while (resultSet.next()) {
                    int id = resultSet.getInt("booking_id");
                    String name = resultSet.getString("customer_name");
                    String room = resultSet.getString("room_type");
                    String time = resultSet.getString("booking_time");

                    bookingDetails.append(String.format("ID: %d, Name: %s, Room Type: %s, Booking Time: %s%n", id, name, room, time));
                }

                outputArea.setText(bookingDetails.toString());
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error while interacting with the database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelManagementGUI app = new HotelManagementGUI();
            app.setVisible(true);
        });
    }
}
