package Model;

/**
 * Represents an order in the order management system.
 * This class contains order information including ID, client ID, product ID, and quantity.
 * 
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class Orders {
    private int id;
    private int clientId;
    private int productId;
    private int quantity;

    /**
     * Default constructor for the Orders class.
     */
    public Orders() {}

    /**
     * Constructs a new Order without an ID (used for creating new orders).
     * 
     * @param clientId the ID of the client placing the order
     * @param productId the ID of the product being ordered
     * @param quantity the quantity of the product being ordered
     */
    public Orders(int clientId, int productId, int quantity) {
        this.clientId = clientId;
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * Constructs a new Order with an ID (used when retrieving orders from database).
     * 
     * @param id the order's unique identifier
     * @param clientId the ID of the client placing the order
     * @param productId the ID of the product being ordered
     * @param quantity the quantity of the product being ordered
     */
    public Orders(int id, int clientId, int productId, int quantity) {
        this.id = id;
        this.clientId = clientId;
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * Gets the order's ID.
     * 
     * @return the order's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the order's ID.
     * 
     * @param id the order's ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the client who placed the order.
     * 
     * @return the client's ID
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Gets the ID of the product that was ordered.
     * 
     * @return the product's ID
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Gets the quantity of the product that was ordered.
     * 
     * @return the quantity ordered
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the ID of the client who placed the order.
     *
     * @param clientId the client's ID to set
     */
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Sets the ID of the product associated with the order.
     *
     * @param productId the ID of the product to set
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * Sets the quantity of the product in the order.
     *
     * @param quantity the quantity of the product to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns a string representation of the order.
     * 
     * @return a string representation of the order
     */
    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }

}
