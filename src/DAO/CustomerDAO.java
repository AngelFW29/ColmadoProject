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
        String sql = "INSERT INTO Person(name, address, phone, typePerson, fiscalIdentification) VALUES (?, ?, ?, ?, ?)";
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
        String sql = "SELECT * FROM Person WHERE idPerson = ? AND typePerson = 'Cliente'";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return new Customer(
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
    public boolean update(Customer customer) {
        String sql = "UPDATE Person SET name = ?, address = ?, phone = ?, fiscalIdentification = ? WHERE idPerson = ? AND typePerson = 'Cliente'";
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
        String sql = "DELETE FROM Person WHERE idPerson = ? AND typePerson = 'Cliente'";
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
        String sql = "SELECT * FROM Person WHERE typePerson = 'Cliente'";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
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
        return customers;
    }
}
