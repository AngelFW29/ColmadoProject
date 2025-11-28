package DAO;

import Model.ProductCategory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryProductDAO implements ICRUD<ProductCategory> {
    private final ConnectionMySQL conexion;

    public CategoryProductDAO() {
        this.conexion = ConnectionMySQL.getInstance();
    }

    @Override
    public boolean create(ProductCategory category) throws RuntimeException {
        String query = "INSERT INTO Category (name) VALUES (?)";

        try {
            int rows = conexion.executeUpdate(query, category.getNameCategory());
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductCategory read(int id) throws RuntimeException {
        String sql = "SELECT * FROM Category WHERE id_category = ?";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return new ProductCategory(
                        rs.getInt("id_category"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la categoría: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(ProductCategory category) throws RuntimeException {
        String query = "UPDATE Category SET name = ? WHERE id_category = ?";

        try {
            int rows = conexion.executeUpdate(query,
                    category.getNameCategory(),
                    category.getIdCategory()
            );
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws RuntimeException {
        String sql = "DELETE FROM Category WHERE id_category = ?";
        try {
            int rows = conexion.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductCategory> findAll() throws RuntimeException {
        List<ProductCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category";

        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new ProductCategory(
                        rs.getInt("id_category"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las categorías: " + e.getMessage(), e);
        }
        return categories;
    }

    public ArrayList<String> findAllNames() throws RuntimeException {
        ArrayList<String> categoriesName = new ArrayList<>();
        String sql = "SELECT name FROM Category";

        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                categoriesName.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las categorías: " + e.getMessage(), e);
        }
        return categoriesName;
    }

    public ProductCategory searchByName(String name) throws RuntimeException {
        ProductCategory category = null;

        String sql = "SELECT * FROM Category where name = ?";

        try (ResultSet rs = conexion.executeQuery(sql, name)) {
            while (rs.next()) {
                category = new ProductCategory(
                        rs.getInt("id_category"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las categorías: " + e.getMessage(), e);
        }
        return category;
    }


}