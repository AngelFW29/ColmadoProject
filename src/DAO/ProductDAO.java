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
        String sql = "INSERT INTO Product(name, categoria, unitPrice, stock, expirationDate) VALUES (?, ?, ?, ?, ?)";
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
        String sql = "SELECT * FROM Product WHERE idProduct = ?";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return new Product(
                        rs.getInt("idProduct"),
                        rs.getString("name"),
                        rs.getInt("categoria"),
                        rs.getDouble("unitPrice"),
                        rs.getInt("stock"),
                        rs.getDate("expirationDate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE Product SET name = ?, categoria = ?, unitPrice = ?, stock = ?, expirationDate = ? WHERE idProduct = ?";
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
        String sql = "DELETE FROM Product WHERE idProduct = ?";
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
                        rs.getInt("idProduct"),
                        rs.getString("name"),
                        rs.getInt("categoria"),
                        rs.getDouble("unitPrice"),
                        rs.getInt("stock"),
                        rs.getDate("expirationDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> findLowStock() {
        List<Product> lowStockProducts = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE stock < 5";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                lowStockProducts.add(new Product(
                        rs.getInt("idProduct"),
                        rs.getString("name"),
                        rs.getInt("categoria"),
                        rs.getDouble("unitPrice"),
                        rs.getInt("stock"),
                        rs.getDate("expirationDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockProducts;
    }
}
