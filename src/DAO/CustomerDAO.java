package DAO;

import Model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements ICRUD<Customer> {

    private final ConnectionMySQL conexion;

    public CustomerDAO() {
        this.conexion = ConnectionMySQL.getInstance();
    }

    @Override
    public boolean create(Customer customer) {
        String sql = "INSERT INTO Person(name, address, phone, type_person, fiscal_identification) VALUES (?, ?, ?, ?, ?)";
        try {
            int rows = conexion.executeUpdate(sql,
                    customer.getName(),
                    customer.getAddress(),
                    customer.getPhone(),
                    customer.getTypePerson(),
                    customer.getFiscalIdentification()
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Customer read(int id) {
        String sql = "SELECT * FROM Person WHERE id_person = ? AND type_person = 'Cliente'";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Customer customer) {
        String sql = "UPDATE Person SET name = ?, address = ?, phone = ?, fiscal_identification = ? WHERE id_person = ? AND type_person = 'Cliente'";
        try {
            int rows = conexion.executeUpdate(sql,
                    customer.getName(),
                    customer.getAddress(),
                    customer.getPhone(),
                    customer.getFiscalIdentification(),
                    customer.getId()
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Person WHERE id_person = ? AND type_person = 'Cliente'";
        try {
            int rows = conexion.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Person WHERE type_person = 'Cliente'";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer findCustomerByName(String name) {
        String sql = "SELECT * FROM Person WHERE name = ? AND type_person = 'Cliente'";
        try (ResultSet rs = conexion.executeQuery(sql, name)) {
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id_person"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("fiscal_identification")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Customer mapResultSet(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id_person"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("fiscal_identification")
        );
    }

    public List<Customer> searchCustomers(String filter) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Person WHERE type_person='Cliente' AND (name LIKE ? OR fiscal_identification LIKE ?)";
        String text = "%" + filter + "%";
        try (ResultSet rs = conexion.executeQuery(sql, text, text)) {
            while (rs.next()) {
                customers.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar clientes: " + e.getMessage(), e);
        }
        return customers;
    }

    // firstStatsPanel Customer
    public int countCustomers() {
        String sql = "SELECT COUNT(*) AS total_cliente FROM Person WHERE type_person='Cliente'";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total_cliente");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // secondStatsPanel Customer
    public int newCustomers() {
        String sql = "SELECT COUNT(*) AS nuevo_cliente FROM Person WHERE type_person='Cliente' "; // AND DATE(created_at) = CURDATE() - Removido(no existe en DB)
        try (ResultSet rs = conexion.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("nuevo_cliente");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
