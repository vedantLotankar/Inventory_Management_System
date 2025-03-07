import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/INV_management"; // Database URL
        String user = "root";
        String password = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL Driver
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL Database Successfully!");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
