import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class InventoryManager extends JFrame implements ActionListener {
    private JTable productTable;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;

    public InventoryManager() {
        setTitle("Inventory Manager");
        setSize(600, 400);
        setLayout(new BorderLayout());

        productTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        add(inputPanel, BorderLayout.SOUTH);

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        addButton = new JButton("Add Product");
        addButton.addActionListener(this);
        inputPanel.add(addButton);

        updateButton = new JButton("Update Product");
        updateButton.addActionListener(this);
        inputPanel.add(updateButton);

        deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(this);
        inputPanel.add(deleteButton);

        loadProducts();

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/products", "root", "");
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)");
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, quantity);
                stmt.executeUpdate();
                conn.close();
                loadProducts();
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                nameField.setText("");
                priceField.setText("");
                quantityField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding product.");
            }
        } else if (e.getSource() == updateButton) {
            int row = productTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to update.");
                return;
            }

            int id = (int) productTable.getValueAt(row, 0);
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/products", "root", "");
                PreparedStatement stmt = conn.prepareStatement("UPDATE products SET name=?, price=?, quantity=? WHERE id=?");
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, quantity);
                stmt.setInt(4, id);
                stmt.executeUpdate();
                conn.close();
                loadProducts();
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
                nameField.setText("");
                priceField.setText("");
                quantityField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating product.");
            }
        } else if (e.getSource() == deleteButton) {
            int row = productTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to delete.");
                return;
            }

            int id = (int) productTable.getValueAt(row, 0);

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/products", "root", "");
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id=?");
                stmt.setInt(1, id);
                stmt.executeUpdate();
                conn.close();
                loadProducts();
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting product.");
            }
        }
    }

    private void loadProducts() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/products", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            DefaultTableModel model = new DefaultTableModel(new Object[] { "ID", "Name", "Price", "Quantity" }, 0);
            while (rs.next()) {
                model.addRow(new Object[] { rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("quantity") });
            }
            productTable.setModel(model);
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.");
        }
    }

    public static void main(String[] args) {
        new InventoryManager();
    }
}
