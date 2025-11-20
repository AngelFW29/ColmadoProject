package Service;

import Model.Product;

import java.util.List;

public interface IInventoryManage {
    void addProduct(Product product);

    void updateStock(Product product, int quantity);

    void removeProduct(int productId);

    void checkLowStock(List<Product> products);
}

