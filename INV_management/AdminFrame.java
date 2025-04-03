import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryDropdown;
    private JButton addButton, editButton, deleteButton, reportButton, settingsButton;

    public AdminFrame() {
        setTitle("Inventory Management - Admin Panel");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel (Search & Filters)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        searchField = new JTextField(20);
        categoryDropdown = new JComboBox<>(new String[]{"All Categories", "Electronics", "Furniture", "Clothing"});
        JButton searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(categoryDropdown);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel (Table)
        String[] columnNames = {"Product ID", "User ID", "Name", "Stock Quantity", "Reorder Level", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Bottom Panel (Buttons)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Inventory Button
        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.addActionListener(e -> {
            // Open ProductManagementGUI without disposing of the current frame
            new ProductManagementGUI().setVisible(true);
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(inventoryButton, gbc);

        // Orders Button
        JButton ordersButton = new JButton("Orders");
        ordersButton.addActionListener(e -> {
            // Open Orders without disposing of the current frame
            new Orders().setVisible(true);
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        bottomPanel.add(ordersButton, gbc);

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Double the width of the Orders button
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(exitButton, gbc);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data from the database
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT product_id, user_id, name, stock_quantity, reorder_level, price FROM products")) {

            // Clear existing rows
            tableModel.setRowCount(0);

            // Populate table with data from the database
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getInt("stock_quantity"),
                    rs.getInt("reorder_level"),
                    rs.getBigDecimal("price").toString()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data from database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
