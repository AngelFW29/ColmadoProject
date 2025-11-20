package DAO;

import Model.Invoice;
import Model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO implements ICRUD<Invoice> {

    private ConnectionMySQL conexion;

    public InvoiceDAO(ConnectionMySQL conexion) {
        this.conexion = conexion;
    }

    @Override
    public boolean create(Invoice invoice) {
        String sql = "INSERT INTO Invoice(dateInvoice, idPerson, total, paymentMethod) VALUES (?, ?, ?, ?)";
        try {
            int rows = conexion.executeUpdate(sql,
                    new java.sql.Timestamp(invoice.getDate().getTime()),
                    invoice.getCustomer().getId(),
                    invoice.getTotal(),
                    invoice.getPaymentMethod()
            );
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Invoice read(int id) {
        String sql = "SELECT * FROM Invoice WHERE idInvoice = ?";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("idPerson"));

                return new Invoice(
                        rs.getInt("idInvoice"),
                        rs.getTimestamp("dateInvoice"),
                        customer,
                        new ArrayList<>(), // detalles opcionales
                        rs.getDouble("total"),
                        rs.getString("paymentMethod")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("idPerson"));

                Invoice invoice = new Invoice(
                        rs.getInt("idInvoice"),
                        rs.getTimestamp("dateInvoice"),
                        customer,
                        new ArrayList<>(), // detalles opcionales
                        rs.getDouble("total"),
                        rs.getString("paymentMethod")
                );
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    @Override
    public boolean update(Invoice entity) {
        // No implementado
        throw new UnsupportedOperationException("Update not supported for Invoice");
    }

    @Override
    public boolean delete(int id) {
        // No implementado
        throw new UnsupportedOperationException("Delete not supported for Invoice");
    }


}
