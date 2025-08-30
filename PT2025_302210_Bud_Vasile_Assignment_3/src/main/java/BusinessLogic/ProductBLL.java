package BusinessLogic;

import DataAccess.ProductDAO;
import Model.Product;

import java.util.List;

/**
 * Business Logic Layer (BLL) class for managing the operations related to products.
 * This class provides methods to perform CRUD operations on Product objects
 * by interacting with the data access layer (ProductDAO).
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class ProductBLL {

    private final ProductDAO productDAO;

    public ProductBLL() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    public Product findProductById(int id) {
        return productDAO.findById(id);
    }

    public Product insertProduct(Product product) {
        return productDAO.insert(product);
    }

    public Product updateProduct(Product product) {
        return productDAO.update(product);
    }

    public Product deleteProduct(Product product) {
        return productDAO.delete(product);
    }
}
