import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class LoginGUI {
    private static JTextField usernameField;
    private static JPasswordField passwordField;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);
    
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);
    
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
    
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);
    
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);
    
        JButton createAccountButton = new JButton("Create Account");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(createAccountButton, gbc);
    
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });
    }

    private static void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, password FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String storedPassword = rs.getString("password");

                if (storedPassword.equals(password)) { // Replace with password hashing check
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    // Open InventoryAdminPage instead of ProductManagementGUI
                    SwingUtilities.invokeLater(() -> new AdminFrame().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid password.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createAccount() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Replace with hashed password
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                createProductTableForUser(conn, userId);
            }

            JOptionPane.showMessageDialog(null, "Account created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createProductTableForUser(Connection conn, int userId) throws SQLException {
        String tableName = "products_user_" + userId;
        String createTableSQL = "CREATE TABLE " + tableName + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "stock_quantity INT NOT NULL, "
                + "reorder_level INT NOT NULL, "
                + "price DECIMAL(10, 2) NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        }
    }
}