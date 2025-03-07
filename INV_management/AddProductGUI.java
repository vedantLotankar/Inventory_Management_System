import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AddProductGUI {
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Add Product");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setBounds(10, 20, 100, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(150, 20, 165, 25);
        panel.add(nameText);

        JLabel stockLabel = new JLabel("Stock:");
        stockLabel.setBounds(10, 50, 100, 25);
        panel.add(stockLabel);

        JTextField stockText = new JTextField(20);
        stockText.setBounds(150, 50, 165, 25);
        panel.add(stockText);

        JLabel reorderLabel = new JLabel("Reorder Level:");
        reorderLabel.setBounds(10, 80, 100, 25);
        panel.add(reorderLabel);

        JTextField reorderText = new JTextField(20);
        reorderText.setBounds(150, 80, 165, 25);
        panel.add(reorderText);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(10, 110, 100, 25);
        panel.add(priceLabel);

        JTextField priceText = new JTextField(20);
        priceText.setBounds(150, 110, 165, 25);
        panel.add(priceText);

        JButton addButton = new JButton("Add Product");
        addButton.setBounds(150, 140, 150, 25);
        panel.add(addButton);

        addButton.addActionListener(e -> {
            try {
                String name = nameText.getText();
                int stock = Integer.parseInt(stockText.getText());
                int reorderLevel = Integer.parseInt(reorderText.getText());
                double price = Double.parseDouble(priceText.getText());

                System.out.println("Adding product: " + name + ", Stock: " + stock + ", Reorder Level: " + reorderLevel + ", Price: " + price);

                addProductToDatabase(name, stock, reorderLevel, price);
                JOptionPane.showMessageDialog(null, "Product Added Successfully!");
                updateTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers for stock, reorder level, and price.");
                ex.printStackTrace();
            }
        });

        // Table to display products
        tableModel = new DefaultTableModel(new String[]{"Name", "Stock", "Reorder Level", "Price"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 180, 560, 150);
        panel.add(scrollPane);

        // Load initial data
        updateTable();
    }

    private static void addProductToDatabase(String name, int stock, int reorderLevel, double price) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, stock_quantity, reorder_level, price) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setInt(2, stock);
            stmt.setInt(3, reorderLevel);
            stmt.setDouble(4, price);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new product was inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while adding product to database.");
            e.printStackTrace();
        }
    }

    private static void updateTable() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, stock_quantity, reorder_level, price FROM products")) {
            tableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                String name = rs.getString("name");
                int stock = rs.getInt("stock_quantity");
                int reorderLevel = rs.getInt("reorder_level");
                double price = rs.getDouble("price");
                tableModel.addRow(new Object[]{name, stock, reorderLevel, price});
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while fetching products from database.");
            e.printStackTrace();
        }
    }
}