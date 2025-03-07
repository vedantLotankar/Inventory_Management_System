import javax.swing.*;
import java.awt.*;

public class InventoryGUI {
    public static void main(String[] args) {
        // Create Main Frame
        JFrame frame = new JFrame("Smart Inventory & Expiry Management");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        // Show Frame
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel label = new JLabel("Welcome to Inventory Management System");
        label.setBounds(100, 50, 400, 30);
        panel.add(label);

        JButton addButton = new JButton("Add Product");
        addButton.setBounds(200, 100, 150, 30);
        panel.add(addButton);
    }
}
