package DAO;

import Model.Supplier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO implements ICRUD<Supplier> {

    private ConnectionMySQL conexion;

    public SupplierDAO(ConnectionMySQL conexion) {
        this.conexion = conexion;
    }

    @Override
    public boolean create(Supplier supplier) {
        String sql = "INSERT INTO Person(name, address, phone, typePerson, fiscalIdentification) VALUES (?, ?, ?, ?, ?)";
        try {
            int rows = conexion.executeUpdate(sql,
                    supplier.getName(),
                    supplier.getAddress(),
                    supplier.getPhone(),
                    supplier.getTypePerson(),
                    supplier.getFiscalIdentification()
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Supplier read(int id) {
        String sql = "SELECT * FROM Person WHERE idPerson = ? AND typePerson = 'Proveedor'";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return new Supplier(
                        rs.getInt("idPerson"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("fiscalIdentification")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Supplier supplier) {
        String sql = "UPDATE Person SET name = ?, address = ?, phone = ?, fiscalIdentification = ? WHERE idPerson = ? AND typePerson = 'Proveedor'";
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
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Person WHERE idPerson = ? AND typePerson = 'Proveedor'";
        try {
            int rows = conexion.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Person WHERE typePerson = 'Proveedor'";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(new Supplier(
                        rs.getInt("idPerson"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("fiscalIdentification")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }
}
