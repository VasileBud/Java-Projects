package Presentation;

import javax.swing.*;

/**
 * The View class is a graphical user interface for managing clients, products, and orders.
 * It extends from JFrame and incorporates three main panels, each corresponding to specific functionalities:
 * client management, product management, and order management.
 * 
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class View extends JFrame {
    private JTabbedPane tabbedPane;
    private ClientPanel clientPanel;
    private ProductPanel productPanel;
    private OrderPanel orderPanel;

    /**
     * Constructor for the View class. Initializes the main window with title,
     * size, and position settings, then creates and displays all UI components.
     */
    public View() {
        setTitle("Orders Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    /**
     * Initializes and sets up all GUI components including the tabbed pane
     * and the three main panels (clients, products, orders).
     */
    public void initComponents() {
        tabbedPane = new JTabbedPane();

        clientPanel = new ClientPanel();
        productPanel = new ProductPanel();
        orderPanel = new OrderPanel();

        tabbedPane.addTab("Clients", clientPanel);
        tabbedPane.addTab("Products", productPanel);
        tabbedPane.addTab("Orders", orderPanel);

        getContentPane().add(tabbedPane);
    }

    /**
     * Returns the client management panel.
     *
     * @return ClientPanel instance for managing clients
     */
    public ClientPanel getClientPanel() {
        return clientPanel;
    }

    /**
     * Returns the product management panel.
     *
     * @return ProductPanel instance for managing products
     */
    public ProductPanel getProductPanel() {
        return productPanel;
    }

    /**
     * Returns the order management panel.
     *
     * @return OrderPanel instance for managing orders
     */
    public OrderPanel getOrderPanel() {
        return orderPanel;
    }

    /**
     * Main method to start the application. Sets the Nimbus look and feel
     * if available, creates the main view and controller.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        View view = new View();
        new Controller(view);
    }
}
