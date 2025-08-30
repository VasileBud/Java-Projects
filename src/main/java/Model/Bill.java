package Model;

/**
 * Represents an immutable bill in the order management system.
 * This record contains billing information including ID, order ID, client name,
 * product name, quantity, and total price. As a Java record, this class is
 * immutable by design, ensuring bills cannot be modified after creation.
 * 
 * Bills are generated for each order and stored in the Log table. They can
 * only be inserted and read, no updates are allowed.
 * 
 * @param id The unique identifier of the bill
 * @param orderId The ID of the order associated with this bill
 * @param clientName The name of the client who placed the order
 * @param productName The name of the product ordered
 * @param quantity The quantity of the product ordered
 * @param totalPrice The total price of the order
 * 
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public record Bill(
        int id,
        int orderId,
        String clientName,
        String productName,
        int quantity,
        double totalPrice
) {
}
