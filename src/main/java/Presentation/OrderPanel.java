package Presentation;

import Model.Bill;
import Model.Client;
import Model.Orders;
import Model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * GUI panel for managing orders in an order management system.
 * Provides functionality for creating orders, viewing order history and logs.
 * Contains form fields for client/product selection and quantity input,
 * along with panels for displaying order details and data tables.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class OrderPanel extends JPanel {
    private JComboBox<Client> clientComboBox;
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;
    private JButton createOrderButton;
    private JButton viewOrdersButton;
    private JButton viewLogsButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea billArea;

    private JPanel tablePanel;
    private JPanel detailsPanel;
    private CardLayout contentLayout;
    private JPanel contentPanel;

    /**
     * Constructor for OrderPanel that sets up the layout and initializes components.
     */
    private Controller controller;

    public OrderPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Initializes and configures all UI components of the order panel including form fields,
     * buttons, tables and layout managers.
     */
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Client:"), gbc);

        gbc.gridx = 1;
        clientComboBox = new JComboBox<>();
        formPanel.add(clientComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Product:"), gbc);

        gbc.gridx = 1;
        productComboBox = new JComboBox<>();
        formPanel.add(productComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        quantityField = new JTextField(10);
        formPanel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        createOrderButton = new JButton("Create Order");
        viewOrdersButton = new JButton("View Orders");
        viewLogsButton = new JButton("View Logs");
        buttonPanel.add(createOrderButton);
        buttonPanel.add(viewOrdersButton);
        buttonPanel.add(viewLogsButton);
        formPanel.add(buttonPanel, gbc);

        billArea = new JTextArea(20, 40);
        billArea.setEditable(false);
        JScrollPane invoiceScrollPane = new JScrollPane(billArea);
        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.add(invoiceScrollPane, BorderLayout.CENTER);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Orders"));

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.add(detailsPanel, "Order Details");
        contentPanel.add(tablePanel, "orders");

        add(formPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        createOrderButton.addActionListener(e -> showDetailsPanel());
        viewOrdersButton.addActionListener(e -> showTablePanel());
        viewLogsButton.addActionListener(e -> showTablePanel());

        showDetailsPanel();
    }

    /**
     * Updates the client combo box with a new list of clients.
     *
     * @param clients List of Client objects to populate the combo box
     */
    public void updateClientComboBox(List<Client> clients) {
        clientComboBox.removeAllItems();
        clients.stream().forEach(c -> clientComboBox.addItem(c));
    }

    /**
     * Updates the product combo box with a new list of products.
     *
     * @param products List of Product objects to populate the combo box
     */
    public void updateProductComboBox(List<Product> products) {
        productComboBox.removeAllItems();
        products.forEach(p -> productComboBox.addItem(p));
    }

    /**
     * Gets the currently selected client from the combo box.
     *
     * @return Selected Client object
     */
    public Client getSelectedClient() {
        return (Client) clientComboBox.getSelectedItem();
    }

    /**
     * Gets the currently selected product from the combo box.
     *
     * @return Selected Product object
     */
    public Product getSelectedProduct() {
        return (Product) productComboBox.getSelectedItem();
    }

    /**
     * Gets the quantity entered in the quantity field.
     *
     * @return Quantity as integer, or -1 if invalid input
     */
    public int getQuantity() {
        try {
            return Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * @return Reference to the Create Order button
     */
    public JButton getCreateOrderButton() {
        return createOrderButton;
    }

    /**
     * @return Reference to the View Orders button
     */
    public JButton getViewOrdersButton() {
        return viewOrdersButton;
    }

    /**
     * @return Reference to the View Logs button
     */
    public JButton getViewLogsButton() {
        return viewLogsButton;
    }

    /**
     * Updates the orders table with new data.
     *
     * @param orders List of Orders to display in the table
     */
    public void updateTable(List<Orders> orders) {
        tableModel = TableUtils.createTableModel(orders);
        table.setModel(tableModel);
    }

    /**
     * Updates the log table with billing data.
     *
     * @param bills List of Bills to display in the table
     */
    public void updateLogTable(List<Bill> bills) {
        tableModel = TableUtils.createTableModel(bills);
        table.setModel(tableModel);
    }

    /**
     * Shows the order details panel in the card layout.
     */
    public void showDetailsPanel() {
        contentLayout.show(contentPanel, "Order Details");
    }

    /**
     * Shows the orders table panel in the card layout.
     */
    public void showTablePanel() {
        contentLayout.show(contentPanel, "orders");
    }

    /**
     * Displays an order bill with order details.
     *
     * @param clientName  Name of the client
     * @param productName Name of the product
     * @param quantity    Quantity ordered
     * @param totalPrice  Total price of the order
     */
    public void displayBill(String clientName, String productName, int quantity, double totalPrice) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== BILL ==========\n");
        sb.append("Client: ").append(clientName).append("\n");
        sb.append("Product: ").append(productName).append("\n");
        sb.append("Quantity: ").append(quantity).append("\n");
        sb.append("Total Price: ").append(String.format("%.2f", totalPrice)).append(" RON\n");
        sb.append("==========================");

        billArea.setText(sb.toString());
        showDetailsPanel();
    }
}