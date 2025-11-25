package DAO;

import Model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDAO implements ICRUD<Product> {

    private final ConnectionMySQL conexion;

    public ProductDAO() {
        this.conexion = ConnectionMySQL.getInstance();
    }

    @Override
    public boolean create(Product product) {
        String sql = "INSERT INTO Product(name,  id_category, unit_price, current_stock, expiration_date) VALUES (?, ?, ?, ?, ?)";
        try {
            int rows = conexion.executeUpdate(sql,
                    product.getName(),
                    product.getCategory(),
                    product.getUnitPrice(),
                    product.getInventoryQuantity(),
                    new java.sql.Date(product.getExpirationDate().getTime())
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Product read(int id) {
        String sql = "SELECT * FROM Product WHERE id_product = ?";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return new Product(
                        rs.getInt("id_product"),
                        rs.getString("name"),
                        rs.getInt(" id_category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("current_stock"),
                        rs.getDate("expiration_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE Product SET name = ?,  id_category = ?, unit_price = ?, current_stock = ?, expiration_date = ? WHERE id_product = ?";
        try {
            int rows = conexion.executeUpdate(sql,
                    product.getName(),
                    product.getCategory(),
                    product.getUnitPrice(),
                    product.getInventoryQuantity(),
                    new Date(product.getExpirationDate().getTime()),
                    product.getId()
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Product WHERE id_product = ?";
        try {
            int rows = conexion.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id_product"),
                        rs.getString("name"),
                        rs.getInt(" id_category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("current_stock"),
                        rs.getDate("expiration_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> findLowStock() {
        List<Product> lowStockProducts = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE current_stock < 5";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                lowStockProducts.add(new Product(
                        rs.getInt("id_product"),
                        rs.getString("name"),
                        rs.getInt(" id_category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("current_stock"),
                        rs.getDate("expiration_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockProducts;
    }
}
