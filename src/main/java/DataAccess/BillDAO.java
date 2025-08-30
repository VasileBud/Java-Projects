package DataAccess;

import Model.Bill;
import Connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) class for managing Bill entities in the database.
 * Provides methods for inserting a new record and retrieving all existing records
 * from the Log table in the database.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class BillDAO {
    protected static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());

    /**
     * Inserts a new bill record into the Log table in the database.
     *
     * @param bill the Bill object containing the order details to be inserted into the database
     * @return the same Bill object that was passed as input
     */
    public static Bill insert(Bill bill) {
        Connection connection = null;
        PreparedStatement statement = null;

        String query = "INSERT INTO Log (order_id, client_name, product_name, quantity, total_price) VALUES (?, ?, ?, ?, ?)";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);

            statement.setInt(1, bill.orderId());
            statement.setString(2, bill.clientName());
            statement.setString(3, bill.productName());
            statement.setInt(4, bill.quantity());
            statement.setDouble(5, bill.totalPrice());

            statement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return bill;
    }

    /**
     * Retrieves all records from the Log table in the database and maps them to a list of Bill objects.
     * Each record in the Log table corresponds to a Bill object containing details such as
     * bill ID, order ID, client name, product name, quantity, and total price.
     *
     * @return a list of Bill objects representing all records in the Log table.
     *         If no records are found, an empty list is returned.
     */
    public static List<Bill> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Bill> bills = new ArrayList<>();

        String query = "SELECT * FROM Log";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Bill bill = new Bill(
                        resultSet.getInt("id"),
                        resultSet.getInt("order_id"),
                        resultSet.getString("client_name"),
                        resultSet.getString("product_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("total_price")
                );
                bills.add(bill);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return bills;
    }
}
