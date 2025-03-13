import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductManagementGUI {
    private static DefaultTableModel tableModel;
    private static JComboBox<String> productComboBox;
    private static JTextField updateStockText;
    private static JTextField updateReorderText;
    private static JTextField updatePriceText;
    private static JTable deleteTable;
    private static int userId;

    public static void setUserId(int userId) {
        ProductManagementGUI.userId = userId;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Product Management");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem addProductMenuItem = new JMenuItem("AddProduct");
        JMenuItem updateProductMenuItem = new JMenuItem("Update");
        JMenuItem deleteProductMenuItem = new JMenuItem("Delete");

        menu.add(addProductMenuItem);
        menu.add(updateProductMenuItem);
        menu.add(deleteProductMenuItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new CardLayout());
        JPanel addProductPanel = createAddProductPanel();
        JPanel updateProductPanel = createUpdateProductPanel();
        JPanel deleteProductPanel = createDeleteProductPanel();

        mainPanel.add(addProductPanel, "AddProduct");
        mainPanel.add(updateProductPanel, "Update");
        mainPanel.add(deleteProductPanel, "Delete");

        addProductMenuItem.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, "AddProduct");
        });

        updateProductMenuItem.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, "Update");
            loadProductNames();
        });

        deleteProductMenuItem.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, "Delete");
            updateDeleteTable();
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JPanel createAddProductPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

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

        return panel;
    }

    private static JPanel createUpdateProductPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel selectProductLabel = new JLabel("Select Product:");
        selectProductLabel.setBounds(10, 20, 100, 25);
        panel.add(selectProductLabel);

        productComboBox = new JComboBox<>();
        productComboBox.setBounds(150, 20, 165, 25);
        panel.add(productComboBox);

        JLabel updateStockLabel = new JLabel("Stock:");
        updateStockLabel.setBounds(10, 50, 100, 25);
        panel.add(updateStockLabel);

        updateStockText = new JTextField(20);
        updateStockText.setBounds(150, 50, 165, 25);
        panel.add(updateStockText);

        JLabel updateReorderLabel = new JLabel("Reorder Level:");
        updateReorderLabel.setBounds(10, 80, 100, 25);
        panel.add(updateReorderLabel);

        updateReorderText = new JTextField(20);
        updateReorderText.setBounds(150, 80, 165, 25);
        panel.add(updateReorderText);

        JLabel updatePriceLabel = new JLabel("Price:");
        updatePriceLabel.setBounds(10, 110, 100, 25);
        panel.add(updatePriceLabel);

        updatePriceText = new JTextField(20);
        updatePriceText.setBounds(150, 110, 165, 25);
        panel.add(updatePriceText);

        JButton updateButton = new JButton("Update Product");
        updateButton.setBounds(150, 140, 150, 25);
        panel.add(updateButton);

        updateButton.addActionListener(e -> {
            try {
                String selectedProduct = (String) productComboBox.getSelectedItem();
                int stock = Integer.parseInt(updateStockText.getText());
                int reorderLevel = Integer.parseInt(updateReorderText.getText());
                double price = Double.parseDouble(updatePriceText.getText());

                System.out.println("Updating product: " + selectedProduct + ", Stock: " + stock + ", Reorder Level: " + reorderLevel + ", Price: " + price);

                updateProductInDatabase(selectedProduct, stock, reorderLevel, price);
                JOptionPane.showMessageDialog(null, "Product Updated Successfully!");
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

        return panel;
    }

    private static JPanel createDeleteProductPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Table to display products
        DefaultTableModel deleteTableModel = new DefaultTableModel(new String[]{"Name", "Stock", "Reorder Level", "Price"}, 0);
        deleteTable = new JTable(deleteTableModel);
        JScrollPane scrollPane = new JScrollPane(deleteTable);
        scrollPane.setBounds(10, 20, 560, 150);
        panel.add(scrollPane);

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.setBounds(220, 180, 150, 25);
        panel.add(deleteButton);

        deleteButton.addActionListener(e -> {
            int selectedRow = deleteTable.getSelectedRow();
            if (selectedRow != -1) {
                String selectedProduct = (String) deleteTable.getValueAt(selectedRow, 0);
                deleteProductFromDatabase(selectedProduct);
                JOptionPane.showMessageDialog(null, "Product Deleted Successfully!");
                updateDeleteTable();
                updateTable();
            } else {
                JOptionPane.showMessageDialog(null, "Please select a product to delete.");
            }
        });

        return panel;
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

    private static void updateProductInDatabase(String name, int stock, int reorderLevel, double price) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("UPDATE products SET stock_quantity = ?, reorder_level = ?, price = ? WHERE name = ?")) {
            stmt.setInt(1, stock);
            stmt.setInt(2, reorderLevel);
            stmt.setDouble(3, price);
            stmt.setString(4, name);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Product updated successfully!");
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while updating product in database.");
            e.printStackTrace();
        }
    }

    private static void deleteProductFromDatabase(String name) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE name = ?")) {
            stmt.setString(1, name);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Product deleted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while deleting product from database.");
            e.printStackTrace();
        }
    }

    private static void updateTable() {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT name, stock_quantity, reorder_level, price FROM products WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                int stock = rs.getInt("stock_quantity");
                int reorderLevel = rs.getInt("reorder_level");
                double price = rs.getDouble("price");
                tableModel.addRow(new Object[]{name, stock, reorderLevel, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateDeleteTable() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, stock_quantity, reorder_level, price FROM products")) {
            DefaultTableModel deleteTableModel = (DefaultTableModel) deleteTable.getModel();
            deleteTableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                String name = rs.getString("name");
                int stock = rs.getInt("stock_quantity");
                int reorderLevel = rs.getInt("reorder_level");
                double price = rs.getDouble("price");
                deleteTableModel.addRow(new Object[]{name, stock, reorderLevel, price});
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while fetching products from database.");
            e.printStackTrace();
        }
    }

    private static void loadProductNames() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM products")) {
            productComboBox.removeAllItems();
            while (rs.next()) {
                productComboBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while fetching product names from database.");
            e.printStackTrace();
        }
    }
}