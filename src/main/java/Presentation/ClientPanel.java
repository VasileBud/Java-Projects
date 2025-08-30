package Presentation;

import Model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * The ClientPanel class is a Swing component that provides a graphical user interface
 * for displaying and managing a list of clients. It allows users to add, edit, and delete
 * client information, interact with a table of clients, and update client details.
 * 
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class ClientPanel extends JPanel {
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField addressField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JPanel tablePanel;
    private Client selectedClient;

    /**
     * Constructs a new ClientPanel with BorderLayout and initializes all components.
     */
    public ClientPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    /**
     * Initializes and sets up all UI components of the panel including the table, form fields and buttons.
     * Also configures the table selection listener to update form fields when a row is selected.
     */
    public void initComponents() {
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setPreferredSize(new Dimension(600, 200));

        tableModel = new DefaultTableModel();
        clientTable = new JTable(tableModel);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(clientTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

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
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

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

        clientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && clientTable.getSelectedRow() != -1) {
                int selectedRow = clientTable.getSelectedRow();
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                String name = tableModel.getValueAt(selectedRow, 1).toString();
                String email = tableModel.getValueAt(selectedRow, 2).toString();
                String address = tableModel.getValueAt(selectedRow, 3).toString();

                selectedClient = new Client(id, name, email, address);

                nameField.setText(name);
                emailField.setText(email);
                addressField.setText(address);
            }
        });
    }

    /**
     * Updates the table model with a new list of clients.
     *
     * @param clients List of Client objects to display in the table
     */
    public void updateTable(List<Client> clients) {
        tableModel = TableUtils.createTableModel(clients);
        clientTable.setModel(tableModel);
    }

    /**
     * Creates and returns a Client object from the form field values.
     * Validates that all fields are filled out.
     *
     * @return New Client object with form field values, or null if validation fails
     */
    public Client getClientFromFields() {
        String name = nameField.getText();
        String email = emailField.getText();
        String address = addressField.getText();

        if (name.isEmpty() || email.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (selectedClient != null) {
            return new Client(selectedClient.getId(), name, email, address);
        }
        return new Client(name, email, address);
    }

    /**
     * Gets the ID of the currently selected client in the table.
     *
     * @return ID of selected client, or -1 if no selection
     */
    public int getSelectedId() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1) {
            return Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        }
        return -1;
    }

    /**
     * Gets the Add button component.
     *
     * @return JButton for adding clients
     */
    public JButton getAddButton() {
        return addButton;
    }

    /**
     * Gets the Edit button component.
     *
     * @return JButton for editing clients
     */
    public JButton getEditButton() {
        return editButton;
    }

    /**
     * Gets the Delete button component.
     *
     * @return JButton for deleting clients
     */
    public JButton getDeleteButton() {
        return deleteButton;
    }
}