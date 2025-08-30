package DataAccess;

import Model.Product;

/**
 * Data Access Object (DAO) class for managing Product entities in the database.
 * Extends AbstractDAO to inherit basic CRUD operations for Product objects.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class ProductDAO extends AbstractDAO<Product>{
    public ProductDAO() {
        super();
    }
}
