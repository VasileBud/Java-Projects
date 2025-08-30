package DataAccess;

import Model.Orders;

/**
 * Data Access Object (DAO) class for managing Orders entities in the database.
 * Extends AbstractDAO to inherit basic CRUD operations for Orders objects.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */

public class OrdersDAO extends AbstractDAO<Orders>{
    public OrdersDAO() {
        super();
    }
}
