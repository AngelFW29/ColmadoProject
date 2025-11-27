package DAO;

import Model.Invoice;
import Model.Customer;
import Model.PaymentMethod;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
            Timestamp sqlDate = Timestamp.valueOf(invoice.getDateTime());

            String paymentMethodStr = invoice.getPaymentMethod().name();

            int rows = conexion.executeUpdate(sql,
                    sqlDate,
                    invoice.getCustomer().getId(),
                    invoice.getTotal(),
                    paymentMethodStr
            );
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error creating invoice: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Invoice read(int id) {
        String sql = "SELECT * FROM Invoice WHERE id_invoice = ?";
        try (ResultSet rs = conexion.executeQuery(sql, id)) {
            if (rs.next()) {
                return mapResultSetToInvoice(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error reading invoice: " + e.getMessage());
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
                    invoice.getPaymentMethod().name(),
                    invoice.getId()
            );
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating invoice: " + e.getMessage());
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
            System.err.println("Error deleting invoice: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving invoices: " + e.getMessage());
        }
        return invoices;
    }

    public double getTodaySales() {
        String sql = "SELECT SUM(total) AS Ventas_hoy FROM Invoice WHERE DATE(created_at) = CURDATE()";
        try (ResultSet rs = conexion.executeQuery(sql)) {
            if (rs.next()) {
                double total = rs.getDouble("Ventas_hoy");
                return rs.wasNull() ? 0.0 : total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id_person"));

        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime dateTime = (ts != null) ? ts.toLocalDateTime() : null;

        String pmString = rs.getString("payment_method");
        PaymentMethod pm = PaymentMethod.CASH;
        if (pmString != null) {
            try {
                pm = PaymentMethod.valueOf(pmString);
            } catch (IllegalArgumentException e) {
                System.err.println("Método de pago desconocido en BD: " + pmString);
            }
        }

        Invoice invoice = new Invoice(
                rs.getInt("id_invoice"),
                dateTime,
                customer,
                new ArrayList<>(),
                rs.getDouble("total"),
                pm
        );

        try {
            if (invoiceDetailsDAO != null) {
                invoice.setItems(invoiceDetailsDAO.findAllByInvoiceId(invoice.getId()));
            }
        } catch (Exception e) {
            System.err.println("Error cargando detalles para factura " + invoice.getId());
        }

        return invoice;
    }
}