package Presentation;

import Model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for product management operations.
 * This panel provides a user interface for adding, editing, deleting and viewing products.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class ProductPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JPanel tablePanel;
    private Product selectedProduct;

    /**
     * Creates a new ProductPanel instance
     */
    public ProductPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    /**
     * Initializes the UI components
     */
    private void initComponents() {
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setPreferredSize(new Dimension(600, 200));

        tableModel = new DefaultTableModel();
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(productTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        priceField = new JTextField(20);
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Stock:"), gbc);

        gbc.gridx = 1;
        stockField = new JTextField(20);
        formPanel.add(stockField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int selectedRow = productTable.getSelectedRow();
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                String name = tableModel.getValueAt(selectedRow, 1).toString();
                double price = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
                int stock = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

                selectedProduct = new Product(id, name, price, stock);

                nameField.setText(name);
                priceField.setText(String.valueOf(price));
                stockField.setText(String.valueOf(stock));
            }
        });
    }

    /**
     * Updates table with given products list
     */
    public void updateTable(List<Product> products) {
        tableModel = TableUtils.createTableModel(products);
        productTable.setModel(tableModel);
    }

    /**
     * Creates Product from input fields
     *
     * @return Product object or null if invalid input
     */
    public Product getProductFromFields() {
        String name = nameField.getText();
        String priceStr = priceField.getText();
        String stockStr = stockField.getText();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            if (selectedProduct != null) {
                return new Product(selectedProduct.getId(), name, price, stock);
            }
            return new Product(name, price, stock);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numeric values", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Gets selected product ID from table
     *
     * @return selected ID or -1 if none selected
     */
    public int getSelectedId() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            return Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        }
        return -1;
    }

    /**
     * Gets Add button
     */
    public JButton getAddButton() {
        return addButton;
    }

    /**
     * Gets Edit button
     */
    public JButton getEditButton() {
        return editButton;
    }

    /**
     * Gets Delete button
     */
    public JButton getDeleteButton() {
        return deleteButton;
    }
}