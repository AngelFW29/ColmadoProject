package DAO;

import Model.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO implements ICRUD<Supplier> {

    private final ConnectionMySQL conexion;

    public SupplierDAO() {
        this.conexion = ConnectionMySQL.getInstance();
    }

    @Override
    public boolean create(Supplier supplier) {
        String sql = "INSERT INTO Person(name, address, phone, type_person, fiscal_identification) VALUES (?, ?, ?, ?, ?)";

        try {
            int rows = conexion.executeUpdate(sql,
                    supplier.getName(),
                    supplier.getAddress(),
                    supplier.getPhone(),
                    "Proveedor",
                    supplier.getFiscalIdentification()
            );
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el proveedor: " + e.getMessage(), e);
        }
    }

    @Override
    public Supplier read(int id) {
        String sql = "SELECT * FROM Person WHERE id_person = ? AND type_person = 'Proveedor'";

        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el proveedor: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(Supplier supplier) {
        String sql = "UPDATE Person SET name = ?, address = ?, phone = ?, fiscal_identification = ? WHERE id_person = ? AND type_person = 'Proveedor'";

        try {
            int rows = conexion.executeUpdate(sql,
                    supplier.getName(),
                    supplier.getAddress(),
                    supplier.getPhone(),
                    supplier.getFiscalIdentification(),
                    supplier.getId()
            );
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el proveedor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Person WHERE id_person = ? AND type_person = 'Proveedor'";

        try {
            int rows = conexion.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el proveedor (verifique que no tenga compras asociadas): " + e.getMessage(), e);
        }
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Person WHERE type_person = 'Proveedor'";

        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los proveedores: " + e.getMessage(), e);
        }
        return suppliers;
    }

    private Supplier mapResultSet(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("id_person"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("fiscal_identification")
        );
    }

    public List<Supplier> searchSuppliers(String filter) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Person WHERE type_person='Proveedor' AND (name LIKE ? OR fiscal_identification LIKE ?)";
        String text = "%" + filter + "%";
        try (ResultSet rs = conexion.executeQuery(sql, text, text)) {
            while (rs.next()) suppliers.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedores: " + e.getMessage(), e);
        }
        return suppliers;
    }

    // firstStatsPanel Supplier
    public int countSuppliers() {
        String sql = "SELECT COUNT(*) AS total_proveedores FROM Person WHERE type_person='Proveedor'";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total_proveedores");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // secondStatsPanel Supplier
    public int newSuppliers() {
        String sql = "SELECT COUNT(*) AS nuevo_proveedor FROM Person WHERE type_person='Proveedor'"; // AND DATE(created_at) = CURDATE() - Removido(no existe en DB)
        try (ResultSet rs = conexion.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("nuevo_proveedor");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


}