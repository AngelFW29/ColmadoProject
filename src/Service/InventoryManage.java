package Service;

import DAO.ProductDAO;
import Model.Product;

import java.util.List;

public class InventoryManage implements IInventoryManage {
    private ProductDAO productDAO;

    public InventoryManage(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }


    @Override
    public void addProduct(Product product) {
        boolean success = productDAO.create(product);
        if (!success) {
            System.out.println("Error al agregar el producto: " + product.getName());
        }
    }

    @Override
    public void updateStock(Product product, int quantity) {
        product.setInventoryQuantity(product.getInventoryQuantity() + quantity);
        boolean success = productDAO.update(product);
        if (!success) {
            System.out.println("Error al actualizar el stock del producto: " + product.getName());
        }
    }

    @Override
    public void removeProduct(int productId) {
        boolean success = productDAO.delete(productId);
        if (!success) {
            System.out.println("Error al eliminar el producto con ID: " + productId);
        }
    }

    @Override
    public void checkLowStock(List<Product> products) {
        for (Product p : products) {
            if (p.getInventoryQuantity() < 5) { // define tu umbral de stock bajo
                System.out.println("Stock bajo: " + p.getName() + " (Cantidad: " + p.getInventoryQuantity() + ")");
            }
        }
    }
}

