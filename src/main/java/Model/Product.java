package Model;

/**
 * Represents a product in the order management system.
 * This class contains product information including ID, name, price, and stock quantity.
 * 
 * @author StudentName
 * @version 1.0
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    /**
     * Default constructor for the Product class.
     */
    public Product() {}

    /**
     * Constructs a new Product without an ID (used for creating new products).
     * 
     * @param name the product's name
     * @param price the product's price
     * @param stock the product's available stock quantity
     */
    public Product(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Constructs a new Product with an ID (used when retrieving products from database).
     * 
     * @param id the product's unique identifier
     * @param name the product's name
     * @param price the product's price
     * @param stock the product's available stock quantity
     */
    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Gets the product's ID.
     * 
     * @return the product's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the product's ID.
     * 
     * @param id the product's ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the product's name.
     * 
     * @return the product's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product's name.
     * 
     * @param name the product's name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the product's price.
     * 
     * @return the product's price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product's price.
     * 
     * @param price the product's price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the product's available stock quantity.
     * 
     * @return the product's available stock quantity
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets the product's available stock quantity.
     * 
     * @param stock the product's available stock quantity to set
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Returns a string representation of the product.
     * 
     * @return a string representation of the product
     */
    @Override
    public String toString() {
        return name;
    }
}
