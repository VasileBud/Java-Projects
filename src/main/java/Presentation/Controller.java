package Presentation;

import BusinessLogic.ClientBLL;
import BusinessLogic.OrdersBLL;
import BusinessLogic.ProductBLL;
import DataAccess.BillDAO;
import Model.Bill;
import Model.Client;
import Model.Orders;
import Model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * The Controller class serves as the mediator between the user interface (View)
 * and the business logic layer (BLL). It handles initializing data, setting up
 * action listeners, and managing business processes related to clients, products,
 * and orders.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class Controller {
    private final View view;
    private final ClientBLL clientBLL;
    private final ProductBLL productBLL;
    private final OrdersBLL ordersBLL;

    /**
     * Constructor for the Controller class.
     * Initializes the view and business logic layers, sets up initial data and action listeners.
     *
     * @param view The main view of the application
     */
    public Controller(View view) {
        this.view = view;
        this.clientBLL = new ClientBLL();
        this.productBLL = new ProductBLL();
        this.ordersBLL = new OrdersBLL();

        initializeData();
        addActionListeners();
    }

    /**
     * Initializes the application data by updating all tables and combo boxes.
     */
    public void initializeData() {
        updateClientTable();
        updateProductTable();
        updateOrderCombos();
        updateOrdersTable();
    }

    /**
     * Sets up action listeners for all buttons in the application.
     * Maps user interface actions to their corresponding handler methods.
     */
    public void addActionListeners() {
        view.getClientPanel().getAddButton().addActionListener(e -> addClient());
        view.getClientPanel().getEditButton().addActionListener(e -> editClient());
        view.getClientPanel().getDeleteButton().addActionListener(e -> deleteClient());

        view.getProductPanel().getAddButton().addActionListener(e -> addProduct());
        view.getProductPanel().getEditButton().addActionListener(e -> editProduct());
        view.getProductPanel().getDeleteButton().addActionListener(e -> deleteProduct());

        view.getOrderPanel().getCreateOrderButton().addActionListener(e -> {
            createOrder();
            view.getOrderPanel().showDetailsPanel();
        });
        view.getOrderPanel().getViewOrdersButton().addActionListener(e -> {
            updateOrdersTable();
            view.getOrderPanel().showTablePanel();
        });
        view.getOrderPanel().getViewLogsButton().addActionListener(e -> {
            updateLogTable();
            view.getOrderPanel().showTablePanel();
        });
    }

    /**
     * Updates the bill log table with current data from the database.
     */
    public void updateLogTable() {
        List<Bill> bills = BillDAO.findAll();
        view.getOrderPanel().updateLogTable(bills);
    }

    /**
     * Updates the client table with current data from the database.
     */
    public void updateClientTable() {
        List<Client> clients = clientBLL.findAllClients();
        view.getClientPanel().updateTable(clients);
    }

    /**
     * Handles adding a new client to the system.
     * Validates and saves client data, then updates relevant UI components.
     */
    public void addClient() {
        Client client = view.getClientPanel().getClientFromFields();
        if (client != null) {
            clientBLL.insertClient(client);
            updateClientTable();
            updateOrderCombos();
        }
    }

    /**
     * Handles editing an existing client's information.
     * Updates client data and refreshes relevant UI components.
     */
    public void editClient() {
        Client client = view.getClientPanel().getClientFromFields();
        if (client != null) {
            clientBLL.updateClient(client);
            updateClientTable();
            updateOrderCombos();
        }
    }

    /**
     * Handles deleting a selected client from the system.
     * Shows confirmation dialog before deletion and updates UI accordingly.
     */
    public void deleteClient() {
        int selectedId = view.getClientPanel().getSelectedId();
        if (selectedId != -1) {
            Client client = clientBLL.findClientById(selectedId);
            int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to delete client: " + client.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                clientBLL.deleteClient(client);
                updateClientTable();
                updateOrderCombos();
            }
        } else {
            JOptionPane.showMessageDialog(view, "Please select a client to delete");
        }
    }

    /**
     * Updates the product table with current data from the database.
     */
    public void updateProductTable() {
        List<Product> products = productBLL.findAllProducts();
        view.getProductPanel().updateTable(products);
    }

    /**
     * Handles adding a new product to the system.
     * Validates and saves product data, then updates relevant UI components.
     */
    public void addProduct() {
        Product product = view.getProductPanel().getProductFromFields();
        if (product != null) {
            productBLL.insertProduct(product);
            updateProductTable();
            updateOrderCombos();
        }
    }

    /**
     * Handles editing an existing product's information.
     * Updates product data and refreshes relevant UI components.
     */
    public void editProduct() {
        Product product = view.getProductPanel().getProductFromFields();
        if (product != null) {
            productBLL.updateProduct(product);
            updateProductTable();
            updateOrderCombos();
        }
    }

    /**
     * Handles deleting a selected product from the system.
     * Shows a confirmation dialog before deletion and updates UI accordingly.
     */
    public void deleteProduct() {
        int selectedId = view.getProductPanel().getSelectedId();
        if (selectedId != -1) {
            Product product = productBLL.findProductById(selectedId);
            int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to delete product: " + product.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                productBLL.deleteProduct(product);
                updateProductTable();
                updateOrderCombos();
            }
        } else {
            JOptionPane.showMessageDialog(view, "Please select a product to delete");
        }
    }

    /**
     * Updates the client and product combo boxes with current data.
     */
    public void updateOrderCombos() {
        List<Client> clients = clientBLL.findAllClients();
        List<Product> products = productBLL.findAllProducts();
        view.getOrderPanel().updateClientComboBox(clients);
        view.getOrderPanel().updateProductComboBox(products);
    }

    /**
     * Updates the orders table with current data from the database.
     */
    public void updateOrdersTable() {
        List<Orders> orders = ordersBLL.findAllOrders();
        view.getOrderPanel().updateTable(orders);
    }

    /**
     * Handles creating a new order in the system.
     * Validates order data, checks stock availability, creates order, and generates a bill.
     */
    public void createOrder() {
        Client client = view.getOrderPanel().getSelectedClient();
        Product product = view.getOrderPanel().getSelectedProduct();
        int quantity = view.getOrderPanel().getQuantity();

        if (client == null || product == null) {
            JOptionPane.showMessageDialog(view, "Please select a client and a product");
            return;
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(view, "Please enter a valid quantity");
            return;
        }

        if (product.getStock() < quantity) {
            JOptionPane.showMessageDialog(view, "Not enough stock available for " + product.getName() + "\nAvailable: " + product.getStock() + ", Requested: " + quantity, "Under-stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Orders order = new Orders(0, client.getId(), product.getId(), quantity);
            ordersBLL.insert(order);

            updateProductTable();
            updateOrderCombos();
            updateOrdersTable();

            double totalPrice = product.getPrice() * quantity;
            view.getOrderPanel().displayBill(client.getName(), product.getName(), quantity, totalPrice);

            JOptionPane.showMessageDialog(view, "Order created successfully");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error creating order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
