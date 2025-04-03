import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class NewOrder {
    private JTextField productNameField; // Declare as instance variable
    private JTextField supplierNameField; // Declare as instance variable

    public NewOrder(Orders ordersInstance) { // Pass Orders instance to refresh data
        JFrame frame = new JFrame("Add New Order");
        frame.setSize(600, 500); // Frame size remains the same
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Product ID Section (Moved to the top)
        JPanel productIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel productIdLabel = new JLabel("Product ID:");
        JTextField productIdField = new JTextField(10); // Adjusted width to 10 characters
        productIdField.setMaximumSize(new Dimension(200, 25)); // Set maximum size for better appearance
        JButton checkProductButton = new JButton("✔"); // Small button to check product
        checkProductButton.setPreferredSize(new Dimension(40, 25)); // Small square button

        // Add action listener to the check button
        checkProductButton.addActionListener(e -> {
            String productId = productIdField.getText().trim();
            if (productId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Product ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check product in the products table
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement("SELECT name FROM products WHERE product_id = ?")) {
                stmt.setString(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Fill the Product Name field
                    productNameField.setText(rs.getString("name"));
                    supplierNameField.setText("N/A"); // No supplier name in the products table
                } else {
                    // Leave fields unchanged if product not found
                    JOptionPane.showMessageDialog(frame, "Product not found in the products table.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to check product. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        productIdPanel.add(productIdLabel);
        productIdPanel.add(productIdField);
        productIdPanel.add(checkProductButton);
        frame.add(productIdPanel);

        // Order Details Section
        JLabel orderDetailsLabel = new JLabel("Order Details");
        orderDetailsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(orderDetailsLabel);

        // Product Name
        JPanel productNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel productNameLabel = new JLabel("Product Name:");
        productNameField = new JTextField(20); // Adjusted width to 20 characters
        productNameField.setMaximumSize(new Dimension(300, 25)); // Set maximum size for better appearance
        productNamePanel.add(productNameLabel);
        productNamePanel.add(productNameField);
        frame.add(productNamePanel);

        // Supplier Name
        JPanel supplierNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel supplierNameLabel = new JLabel("Supplier Name:");
        supplierNameField = new JTextField(20); // Adjusted width to 20 characters
        supplierNameField.setMaximumSize(new Dimension(300, 25)); // Set maximum size for better appearance
        supplierNamePanel.add(supplierNameLabel);
        supplierNamePanel.add(supplierNameField);
        frame.add(supplierNamePanel);

        // Status
        JLabel statusLabel = new JLabel("Status:");
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton pendingButton = new JRadioButton("Pending", true);
        JRadioButton completedButton = new JRadioButton("Completed");
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(pendingButton);
        statusGroup.add(completedButton);
        statusPanel.add(statusLabel);
        statusPanel.add(pendingButton);
        statusPanel.add(completedButton);
        frame.add(statusPanel);

        // Order Items Section
        JLabel orderItemsLabel = new JLabel("Order Items");
        orderItemsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(orderItemsLabel);

        // Quantity Ordered
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel quantityLabel = new JLabel("Quantity Ordered:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        frame.add(quantityPanel);

        // Price per Unit
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel priceLabel = new JLabel("Price per Unit:");
        JTextField priceField = new JTextField(20); // Adjusted width to 20 characters
        priceField.setMaximumSize(new Dimension(300, 25)); // Set maximum size for better appearance
        pricePanel.add(priceLabel);
        pricePanel.add(priceField);
        frame.add(pricePanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        frame.add(buttonPanel);

        // Action listener for Submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Collect data from the form
                String productName = productNameField.getText();
                String supplierName = supplierNameField.getText();
                String status = pendingButton.isSelected() ? "Pending" : "Completed";
                String productId = productIdField.getText();
                int quantity = (int) quantitySpinner.getValue();
                String price = priceField.getText();

                // Validate inputs (basic validation)
                if (productName.isEmpty() || supplierName.isEmpty() || productId.isEmpty() || price.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert data into the database
                try (Connection conn = DatabaseConnection.connect()) {
                    if (conn == null) {
                        JOptionPane.showMessageDialog(frame, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check for primary key conflicts in the orders table
                    String checkOrderQuery = "SELECT COUNT(*) FROM orders WHERE product_name = ?";
                    try (PreparedStatement checkOrderStmt = conn.prepareStatement(checkOrderQuery)) {
                        checkOrderStmt.setString(1, productName);
                        ResultSet rs = checkOrderStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(frame, "Order for this product already exists. Please update the existing order.", "Integrity Issue", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    // Insert into orders table
                    String insertOrderQuery = "INSERT INTO orders (product_name, order_date, supplier_name, status) VALUES (?, NOW(), ?, ?)";
                    try (PreparedStatement insertOrderStmt = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                        insertOrderStmt.setString(1, productName);
                        insertOrderStmt.setString(2, supplierName);
                        insertOrderStmt.setString(3, status);
                        insertOrderStmt.executeUpdate();

                        // Get the generated order_id
                        ResultSet generatedKeys = insertOrderStmt.getGeneratedKeys();
                        int orderId = -1;
                        if (generatedKeys.next()) {
                            orderId = generatedKeys.getInt(1);
                        }
                        
                        // Insert into order_items table
                        String insertOrderItemQuery = "INSERT INTO order_items (order_id, product_id, product_name, quantity_ordered, price_per_unit) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement insertOrderItemStmt = conn.prepareStatement(insertOrderItemQuery)) {
                            insertOrderItemStmt.setInt(1, orderId);
                            insertOrderItemStmt.setString(2, productId);
                            insertOrderItemStmt.setString(3, productName);
                            insertOrderItemStmt.setInt(4, quantity);
                            insertOrderItemStmt.setBigDecimal(5, new java.math.BigDecimal(price));
                            insertOrderItemStmt.executeUpdate();
                        }
                    }

                    // Success message
                    JOptionPane.showMessageDialog(frame, "Order submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh table data in Orders.java
                    ordersInstance.refreshTableData();

                    // Close the form
                    frame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Failed to submit the order. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for Cancel button
        cancelButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }
}
