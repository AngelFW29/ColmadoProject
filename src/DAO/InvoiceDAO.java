package DAO;

import Model.Invoice;
import Model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO implements ICRUD<Invoice> {

    private final ConnectionMySQL conexion;
    private final InvoiceDetailsDAO invoiceDetailsDAO;

    public InvoiceDAO(InvoiceDetailsDAO invoiceDetailsDAO) {
        this.conexion = ConnectionMySQL.getInstance();
        this.invoiceDetailsDAO = invoiceDetailsDAO;
    }

    @Override
    public boolean create(Invoice invoice) {
        String sql = "INSERT INTO Invoice (created_at, id_person, total, payment_method) VALUES (?, ?, ?, ?)";
        try {
            int rows = conexion.executeUpdate(sql,
                    new java.sql.Timestamp(invoice.getDate().getTime()),
                    invoice.getCustomer().getId(),
                    invoice.getTotal(),
                    invoice.getPaymentMethod()
            );
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error creating invoice: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Invoice read(int id) {
        String sql = "SELECT * FROM Invoice WHERE id_invoice = ?";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id_person"));

                Invoice invoice = new Invoice(
                        rs.getInt("id_invoice"),
                        rs.getTimestamp("created_at"),
                        customer,
                        new ArrayList<>(),
                        rs.getDouble("total"),
                        rs.getString("payment_method")
                );

                try {
                    invoice.setItems(new ArrayList<>(invoiceDetailsDAO.findAllByInvoiceId(id)));
                } catch (Exception e) {
                    invoice.setItems(new ArrayList<>());
                }

                return invoice;
            }
        } catch (SQLException e) {
            System.out.println("Error reading invoice: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean update(Invoice invoice) {
        String sql = "UPDATE Invoice SET id_person = ?, total = ?, payment_method = ? WHERE id_invoice = ?";
        try {
            int rows = conexion.executeUpdate(sql,
                    invoice.getCustomer().getId(),
                    invoice.getTotal(),
                    invoice.getPaymentMethod(),
                    invoice.getIdInvoice()
            );
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating invoice: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Invoice WHERE id_invoice = ?";
        try {
            int rows = conexion.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting invoice: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id_person"));

                int invoiceId = rs.getInt("id_invoice");

                Invoice invoice = new Invoice(
                        invoiceId,
                        rs.getTimestamp("created_at"),
                        customer,
                        new ArrayList<>(),
                        rs.getDouble("total"),
                        rs.getString("payment_method")
                );

                invoice.setItems(new ArrayList<>(invoiceDetailsDAO.findAllByInvoiceId(invoiceId)));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving invoices: " + e.getMessage());
        }
        return invoices;
    }

    public double TodaySales() {
        String sql = "SELECT SUM(total) AS Ventas_hoy FROM Invoice WHERE DATE(created_at) = CURDATE()";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("Ventas_hoy");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }


}
