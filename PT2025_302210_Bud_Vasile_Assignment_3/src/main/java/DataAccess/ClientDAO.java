package DataAccess;

import Model.Client;

/**
 * Data Access Object (DAO) class for managing Client entities in the database.
 * Extends AbstractDAO to inherit basic CRUD operations for Client objects.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class ClientDAO extends AbstractDAO<Client>{
    public ClientDAO() {
        super();
    }
}
