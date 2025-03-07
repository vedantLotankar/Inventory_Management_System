import java.sql.*;

public class DatabaseConnection {
    static final String URL = "jdbc:mysql://localhost:3306/INV_management";
    static final String USER = "root";
    static final String PASSWORD = "root";

    public static Connection connect() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered!");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to Database!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Connection failed. Check output console.");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Connection conn = connect();
        if (conn != null) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}