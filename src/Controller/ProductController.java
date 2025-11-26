package Controller;

import DAO.ProductDAO;
import Model.Product;

import java.time.LocalDate;
import java.util.List;

public class ProductController {
    private final ProductDAO productDAO;

    public ProductController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    // Create Product
    public boolean addProduct(String name, int category, double unitPrice, int inventoryQuantity, LocalDate expirationDate) {
        Product product = new Product(0, name, category, unitPrice, inventoryQuantity, expirationDate);
        return productDAO.create(product);
    }

    // Read Product
    public Product getProductById(int id) {
        return productDAO.read(id);
    }

    // Update Product
    public boolean updateProduct(int id, String name, int category, double unitPrice, int inventoryQuantity, LocalDate expirationDate) {
        Product product = new Product(id, name, category, unitPrice, inventoryQuantity, expirationDate);
        return productDAO.update(product);
    }

    // Delete Product by ID
    public boolean deleteProduct(int id) {
        return productDAO.delete(id);
    }

    // List all Product
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    // List low stock
    public List<Product> getLowStockProducts() {
        return productDAO.findLowStock();
    }

    // Total number of products
    public int getTotalProducts() {
        return productDAO.countProducts();
    }
    // Number of products with low stock
    public int getLowStockCount() {
        return productDAO.countLowStock();
    }
}
