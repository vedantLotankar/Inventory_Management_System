import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryManagementGUI {
    private JFrame frame;
    private JTextField nameText, categoryText, stockText, expiryText, supplierText, reorderText, priceText;
    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedProductId = -1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryManagementGUI::new);
    }

    public InventoryManagementGUI() {
        frame = new JFrame("Inventory Management");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        nameText = new JTextField(10);
        categoryText = new JTextField(10);
        stockText = new JTextField(5);
        expiryText = new JTextField(10); // Format: YYYY-MM-DD
        supplierText = new JTextField(10);
        reorderText = new JTextField(5);
        priceText = new JTextField(7);

        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(nameText);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryText);
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(stockText);
        inputPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
        inputPanel.add(expiryText);
        inputPanel.add(new JLabel("Supplier:"));
        inputPanel.add(supplierText);
        inputPanel.add(new JLabel("Reorder Level:"));
        inputPanel.add(reorderText);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceText);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Stock", "Expiry", "Supplier", "Reorder", "Price"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        loadProducts();

        addButton.addActionListener(e -> {
            String name = nameText.getText().trim();
            String category = categoryText.getText().trim();
            String stock = stockText.getText().trim();
            String expiry = expiryText.getText().trim();
            String supplier = supplierText.getText().trim();
            String reorder = reorderText.getText().trim();
            String price = priceText.getText().trim();

            if (!name.isEmpty() && !stock.isEmpty() && !price.isEmpty()) {
                addProduct(name, category, Integer.parseInt(stock), expiry, supplier, Integer.parseInt(reorder), Double.parseDouble(price));
                loadProducts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(frame, "Product Name, Stock, and Price are required!");
            }
        });

        updateButton.addActionListener(e -> {
            if (selectedProductId != -1) {
                String name = nameText.getText().trim();
                String category = categoryText.getText().trim();
                int stock = Integer.parseInt(stockText.getText().trim());
                String expiry = expiryText.getText().trim();
                String supplier = supplierText.getText().trim();
                int reorder = Integer.parseInt(reorderText.getText().trim());
                double price = Double.parseDouble(priceText.getText().trim());

                updateProduct(selectedProductId, name, category, stock, expiry, supplier, reorder, price);
                loadProducts();
                clearFields();
                selectedProductId = -1;
            } else {
                JOptionPane.showMessageDialog(frame, "Select a product to update.");
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedProductId != -1) {
                deleteProduct(selectedProductId);
                loadProducts();
                clearFields();
                selectedProductId = -1;
            } else {
                JOptionPane.showMessageDialog(frame, "Select a product to delete.");
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedProductId = (int) tableModel.getValueAt(selectedRow, 0);
                    nameText.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    categoryText.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    stockText.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    expiryText.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    supplierText.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    reorderText.setText(tableModel.getValueAt(selectedRow, 6).toString());
                    priceText.setText(tableModel.getValueAt(selectedRow, 7).toString());
                }
            }
        });

        frame.setVisible(true);
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("category"), 
                    rs.getInt("stock_quantity"), rs.getDate("expiry_date"), 
                    rs.getString("supplier"), rs.getInt("reorder_level"), rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct(String name, String category, int stock, String expiry, String supplier, int reorder, double price) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, category, stock_quantity, expiry_date, supplier, reorder_level, price) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setInt(3, stock);
            stmt.setString(4, expiry);
            stmt.setString(5, supplier);
            stmt.setInt(6, reorder);
            stmt.setDouble(7, price);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProduct(int id, String name, String category, int stock, String expiry, String supplier, int reorder, double price) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("UPDATE products SET name = ?, category = ?, stock_quantity = ?, expiry_date = ?, supplier = ?, reorder_level = ?, price = ? WHERE id = ?")) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setInt(3, stock);
            stmt.setString(4, expiry);
            stmt.setString(5, supplier);
            stmt.setInt(6, reorder);
            stmt.setDouble(7, price);
            stmt.setInt(8, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct(int id) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameText.setText(""); categoryText.setText(""); stockText.setText(""); expiryText.setText("");
        supplierText.setText(""); reorderText.setText(""); priceText.setText("");
    }
}
