package Model;

/**
 * Represents a client in the order management system.
 * This class contains client information including ID, name, email, and address.
 * 
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class Client {
    private int id;
    private String name;
    private String email;
    private String address;

    /**
     * Default constructor for the Client class.
     */
    public Client() {
    }

    /**
     * Constructs a new Client without an ID (used for creating new clients).
     * 
     * @param name the client's name
     * @param email the client's email address
     * @param address the client's physical address
     */
    public Client(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    /**
     * Constructs a new Client with an ID (used when retrieving clients from database).
     * 
     * @param id the client's unique identifier
     * @param name the client's name
     * @param email the client's email address
     * @param address the client's physical address
     */
    public Client(int id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    /**
     * Gets the client's ID.
     * 
     * @return the client's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the client's ID.
     * 
     * @param id the client's ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the client's name.
     * 
     * @return the client's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the client's name.
     *
     * @param name the name to set for the client
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the client.
     *
     * @return the client's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the client's email address.
     *
     * @param email the email address to set for the client
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the client's physical address.
     *
     * @return the client's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Updates the client's physical address.
     *
     * @param address the new address to set for the client
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns a string representation of the client.
     *
     * @return a string representation of the client
     */
    @Override
    public String toString() {
        return name;
    }
}
