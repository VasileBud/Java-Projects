package BusinessLogic;

import DataAccess.ClientDAO;
import Model.Client;

import java.util.List;

/**
 * Business Logic Layer (BLL) class for managing the operations related to clients.
 * This class provides methods to perform CRUD operations on Client objects
 * by interacting with the data access layer (ClientDAO).
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class ClientBLL {

    private final ClientDAO clientDAO;

    public ClientBLL() {
        this.clientDAO = new ClientDAO();
    }

    public List<Client> findAllClients() {
        return clientDAO.findAll();
    }

    public Client findClientById(int id) {
        return clientDAO.findById(id);
    }

    public Client insertClient(Client client) {
        return clientDAO.insert(client);
    }

    public Client updateClient(Client client) {
        return clientDAO.update(client);
    }

    public Client deleteClient(Client client) {
        return clientDAO.delete(client);
    }

}
