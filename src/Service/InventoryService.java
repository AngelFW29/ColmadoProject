package Service;

import DAO.ProductDAO;
import Model.Product;

import java.util.List;

public class InventoryService {
    private final ProductDAO PRODUCT_DAO;

    public InventoryService(ProductDAO productDAO) {
        this.PRODUCT_DAO = productDAO;
    }


    public void addProduct(Product product) {
        boolean success = PRODUCT_DAO.create(product);
        if (!success) {
            System.out.println("Error al agregar el producto: " + product.getName());
        }
    }

    public void updateStock(Product product, int quantity) {
        product.setInventoryQuantity(product.getInventoryQuantity() + quantity);
        boolean success = PRODUCT_DAO.update(product);
        if (!success) {
            System.out.println("Error al actualizar el stock del producto: " + product.getName());
        }
    }

    public void removeProduct(int productId) {
        boolean success = PRODUCT_DAO.delete(productId);
        if (!success) {
            System.out.println("Error al eliminar el producto con ID: " + productId);
        }
    }

    public void checkLowStock(List<Product> products) {
        for (Product p : products) {
            if (p.getInventoryQuantity() < 5) {
                System.out.println("Stock bajo: " + p.getName() + " (Cantidad: " + p.getInventoryQuantity() + ")");
            }
        }
    }
}

