import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*; // Import for database operations

public class Orders extends JFrame {
    // Table models for orders and order_items
    DefaultTableModel orderModel, orderItemsModel;
    JTable table;
    JComboBox<String> tableSelector;
    JTextField searchField;
    JRadioButton searchById, searchByName;
    TableRowSorter<DefaultTableModel> sorter;
    JRadioButton sortAsc, sortDesc; // Radio buttons for sorting
    ButtonGroup sortGroup; // Group for sorting radio buttons

    public Orders() {
        setTitle("Orders Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add components or functionality here
        JLabel label = new JLabel("Orders Management GUI");
        add(label);

        // Create main frame
        JFrame frame = new JFrame("Inventory Management - Order Placement");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520); // 65% left, 35% right

        // -------- Left Panel --------
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Dropdown to switch between tables
        String[] tables = {"Orders", "Order Items"};
        tableSelector = new JComboBox<>(tables);
        tableSelector.addActionListener(new TableSwitcher());

        // Create table models
        createTableModels();
        table = new JTable(orderModel);
        sorter = new TableRowSorter<>(orderModel);
        table.setRowSorter(sorter);

        // Add table and dropdown to left panel
        leftPanel.add(tableSelector, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // -------- Right Panel --------
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Feature (Moved to Right Panel)
        JLabel searchLabel = new JLabel("Search Orders/Items:");
        searchField = new JTextField(40); // ✅ Set size to 40 width
        searchField.setMaximumSize(new Dimension(400, 25)); // ✅ Max size for consistency

        searchById = new JRadioButton("By ID", true);
        searchByName = new JRadioButton("By Name");

        ButtonGroup searchGroup = new ButtonGroup();
        searchGroup.add(searchById);
        searchGroup.add(searchByName);

        // Search and Reset buttons
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset"); // ✅ New Reset Button

        searchButton.addActionListener(new SearchAction());
        resetButton.addActionListener(new ResetAction()); // ✅ Reset functionality

        // Sorting Feature
        JLabel sortLabel = new JLabel("Sort by Date:");
        sortAsc = new JRadioButton("Ascending", true);
        sortDesc = new JRadioButton("Descending");

        sortGroup = new ButtonGroup();
        sortGroup.add(sortAsc);
        sortGroup.add(sortDesc);

        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(new SortAction()); // Add sorting functionality

        // Search panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton); // ✅ Add Reset button beside Search button

        // Sorting panel
        JPanel sortPanel = new JPanel();
        sortPanel.add(sortLabel);
        sortPanel.add(sortAsc);
        sortPanel.add(sortDesc);
        sortPanel.add(sortButton);

        // Add search components to right panel
        rightPanel.add(searchLabel);
        rightPanel.add(searchField);
        rightPanel.add(searchById);
        rightPanel.add(searchByName);
        rightPanel.add(buttonPanel); // ✅ Add button panel with search & reset
        rightPanel.add(Box.createVerticalStrut(20)); // Space after search box

        // Add sorting components to right panel
        rightPanel.add(sortPanel); // Add sorting panel to the right panel

        // Add Order and Update/Delete Order buttons
        JButton addOrderButton = new JButton("Add Order");
        JButton updateDeleteOrderButton = new JButton("Update/Delete Order");

        // Set buttons to have the same size and match the width of the right panel
        Dimension buttonSize = new Dimension(400, 40); // Width matches the right panel, height is 40
        addOrderButton.setMaximumSize(buttonSize);
        updateDeleteOrderButton.setMaximumSize(buttonSize);

        // Action listener for Add Order button
        addOrderButton.addActionListener(e -> {
            // Open the NewOrder form and pass the current Orders instance
            SwingUtilities.invokeLater(() -> new NewOrder(this));
        });

        // Action listener for Update/Delete Order button
        updateDeleteOrderButton.addActionListener(e -> {
            // Ensure a row is selected
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an order to update/delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pass the selected order data to UpdateDeleteOrder
            int modelRow = table.convertRowIndexToModel(selectedRow); // Convert view index to model index
            DefaultTableModel currentModel = (DefaultTableModel) table.getModel();
            Object[] rowData = new Object[currentModel.getColumnCount()];
            for (int i = 0; i < currentModel.getColumnCount(); i++) {
                rowData[i] = currentModel.getValueAt(modelRow, i);
            }

            // Open the UpdateDeleteOrder form with the selected row data and Orders instance
            SwingUtilities.invokeLater(() -> new UpdateDeleteOrder(rowData, this));
        });

        // Add buttons to a panel
        JPanel buttonPanelBottom = new JPanel();
        buttonPanelBottom.setLayout(new BoxLayout(buttonPanelBottom, BoxLayout.Y_AXIS));
        buttonPanelBottom.add(Box.createVerticalStrut(10)); // Add spacing
        buttonPanelBottom.add(addOrderButton);
        buttonPanelBottom.add(Box.createVerticalStrut(10)); // Add spacing
        buttonPanelBottom.add(updateDeleteOrderButton);

        // Add buttons to the right panel
        rightPanel.add(Box.createVerticalStrut(20)); // Space before buttons
        rightPanel.add(buttonPanelBottom);

        // Add panels to split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // Add split pane to frame
        frame.add(splitPane);
        frame.setVisible(true);
    }

    // Method to create sample data for tables
    private void createTableModels() {
        // Orders Table
        String[] orderColumns = {"Order ID", "Product Name", "Order Date", "Supplier Name", "Status"};
        orderModel = new DefaultTableModel(orderColumns, 0); // Initialize with column names only

        // Fetch data for Orders table
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT order_id, product_name, order_date, supplier_name, status FROM orders")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("product_name"),
                    rs.getTimestamp("order_date"),
                    rs.getString("supplier_name"),
                    rs.getString("status") // Ensure "status" is fetched as a String
                };
                orderModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Order Items Table
        String[] orderItemColumns = {"Order ID", "Product ID", "Product Name", "Quantity Ordered", "Price per Unit", "Total Price"};
        orderItemsModel = new DefaultTableModel(orderItemColumns, 0); // Initialize with column names only

        // Fetch data for Order Items table
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT order_id, product_id, product_name, quantity_ordered, price_per_unit, total_price FROM order_items")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity_ordered"),
                    rs.getBigDecimal("price_per_unit"),
                    rs.getBigDecimal("total_price")
                };
                orderItemsModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to refresh table data
    public void refreshTableData() {
        // Clear existing data
        orderModel.setRowCount(0);
        orderItemsModel.setRowCount(0);

        // Reload data for Orders table
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT order_id, product_name, order_date, supplier_name, status FROM orders")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("product_name"),
                    rs.getTimestamp("order_date"),
                    rs.getString("supplier_name"),
                    rs.getString("status") // Ensure "status" is fetched as a String
                };
                orderModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Reload data for Order Items table
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT order_id, product_id, product_name, quantity_ordered, price_per_unit, total_price FROM order_items")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity_ordered"),
                    rs.getBigDecimal("price_per_unit"),
                    rs.getBigDecimal("total_price")
                };
                orderItemsModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ActionListener for table switching
    private class TableSwitcher implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedTable = (String) tableSelector.getSelectedItem();
            if (selectedTable.equals("Orders")) {
                table.setModel(orderModel);
                sorter = new TableRowSorter<>(orderModel);
            } else if (selectedTable.equals("Order Items")) {
                table.setModel(orderItemsModel);
                sorter = new TableRowSorter<>(orderItemsModel);
            }
            table.setRowSorter(sorter); // Update sorter after switching
        }
    }

    // ActionListener for search functionality
    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                sorter.setRowFilter(null); // Reset filter to show all rows
                return;
            }

            int searchColumn = searchById.isSelected() ? 0 : 1; // 0 for ID, 1 for Name
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, searchColumn)); // Case-insensitive filtering
        }
    }

    // ActionListener for reset functionality
    private class ResetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchField.setText(""); // Clear search field
            sorter.setRowFilter(null); // Reset filter to show all rows
        }
    }

    // ActionListener for sorting functionality
    private class SortAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sorter.setSortKeys(null); // Clear existing sort keys
            int sortColumn = 2; // Column index for "Order Date"
            SortOrder sortOrder = sortAsc.isSelected() ? SortOrder.ASCENDING : SortOrder.DESCENDING;
            sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(sortColumn, sortOrder)));
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Orders::new); // Updated to use Orders class
    }
}
