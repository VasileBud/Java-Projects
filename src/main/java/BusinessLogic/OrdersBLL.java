package BusinessLogic;

import DataAccess.BillDAO;
import DataAccess.ClientDAO;
import DataAccess.OrdersDAO;
import DataAccess.ProductDAO;
import Model.Bill;
import Model.Client;
import Model.Orders;
import Model.Product;

import java.util.List;

/**
 * The OrdersBLL class provides business logic to manage orders in the application.
 * It acts as an intermediary between the data access layer and the user interface.
 * This class ensures proper validation, update of related entities, and creation of associated records.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */

public class OrdersBLL {
    private final OrdersDAO ordersDAO;
    private final ProductDAO productDAO;
    private final BillDAO billDAO;
    private final ClientDAO clientDAO;

    public OrdersBLL() {
        this.ordersDAO = new OrdersDAO();
        this.productDAO = new ProductDAO();
        this.billDAO = new BillDAO();
        this.clientDAO = new ClientDAO();
    }

    /**
     * Inserts a new order into the database after performing the necessary validations and updates.
     * This method ensures that the product stock is enough before proceeding with the order creation.
     * Additionally, it calculates the total cost of the order and creates an associated bill.
     *
     * @param order the order to be inserted, containing details such as product ID, client ID, and quantity
     * @return the inserted order, including the unique identifier assigned by the database
     */
    public Orders insert(Orders order) {

        Product product = productDAO.findById(order.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product does not exist!");
        }

        if (product.getStock() < order.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        product.setStock(product.getStock() - order.getQuantity());
        productDAO.update(product);

        Orders insertedOrder = ordersDAO.insert(order);
        if (insertedOrder == null) {
            throw new RuntimeException("Order could not be inserted.");
        }

        Client client = clientDAO.findById(order.getClientId());
        String clientName = client != null ? client.getName() : "Unknown client";

        double total = product.getPrice() * order.getQuantity();
        Bill bill = new Bill(0, insertedOrder.getId(), clientName, product.getName(), order.getQuantity(), total);
        billDAO.insert(bill);

        return insertedOrder;
    }

    public List<Orders> findAllOrders() {
        return ordersDAO.findAll();
    }
}