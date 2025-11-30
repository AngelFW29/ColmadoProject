package DAO;

import Model.Invoice;
import Model.Customer;
import Model.InvoiceDetails;
import Model.PaymentMethod;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO implements ICRUD<Invoice> {

    private final ConnectionMySQL CONNECTION;
    private final InvoiceDetailsDAO invoiceDetailsDAO;
    private final CustomerDAO customerDAO;

    public InvoiceDAO() {
        this.CONNECTION = ConnectionMySQL.getInstance();
        this.invoiceDetailsDAO = new InvoiceDetailsDAO();
        this.customerDAO = new CustomerDAO();
    }

    public InvoiceDAO(InvoiceDetailsDAO detailsDAO) {
        this.CONNECTION = ConnectionMySQL.getInstance();
        this.invoiceDetailsDAO = detailsDAO;
        this.customerDAO = new CustomerDAO();
    }

    @Override
    public boolean create(Invoice entity) {
        String query = "INSERT INTO Invoice(id_person, total, payment_method, created_at) VALUES (?, ?, ?, ?)";

        try {
            int rows = CONNECTION.executeUpdate(query,
                    entity.getCustomer().getId(),
                    entity.getTotal(),
                    entity.getPaymentMethod().getDisplayName(),
                    entity.getDateTime()
            );
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error creating invoice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Invoice read(int id) {
        String sql = "SELECT * FROM Invoice WHERE id_invoice = ?";
        try (ResultSet rs = CONNECTION.executeQuery(sql, id)) {
            if (rs.next()) {
                return mapResultSetToInvoice(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error reading invoice: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving invoices: " + e.getMessage());
        }
        return invoices;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        int customerId = rs.getInt("id_person");
        Customer customer = customerDAO.read(customerId);

        if (customer == null) {
            customer = new Customer();
            customer.setId(customerId);
            customer.setName("Cliente Eliminado");
        }

        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime dateTime = (ts != null) ? ts.toLocalDateTime() : null;

        String pmString = rs.getString("payment_method");
        PaymentMethod pm = PaymentMethod.CASH;
        if (pmString != null) {
            for (PaymentMethod m : PaymentMethod.values()) {
                if (m.getDisplayName().equalsIgnoreCase(pmString)) {
                    pm = m;
                    break;
                }
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
            List<InvoiceDetails> details = invoiceDetailsDAO.findAllByInvoiceId(invoice.getId());
            invoice.setItems(details);
        } catch (Exception e) {
            System.err.println("Error cargando detalles factura #" + invoice.getId());
        }

        return invoice;
    }

    public int getLastInsertedId() {
        String sql = "SELECT MAX(id_invoice) AS last_id FROM Invoice";
        try (java.sql.ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int countInvoices() {
        String sql = "SELECT COUNT(*) AS total FROM Invoice";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public int countInvoicesToday() {
        String sql = "SELECT COUNT(*) AS facturas_hoy FROM Invoice WHERE DATE(created_at) = CURDATE()";

        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("facturas_hoy");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;

    }

    public double getTodaySales() {
        String sql = "SELECT SUM(total) AS ventas_hoy FROM Invoice WHERE DATE(created_at) = CURDATE()";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) {
                double total = rs.getDouble("ventas_hoy");
                return rs.wasNull() ? 0.0 : total;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public List<Invoice> searchInvoice(String filter) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = """ 
                SELECT * FROM Invoice 
                WHERE CAST(id_invoice AS CHAR) LIKE ?
                OR CAST(id_person AS CHAR) LIKE ?
                OR payment_method LIKE ?
                """;

        String text = "%" + filter + "%";

        try (ResultSet rs = CONNECTION.executeQuery(sql, text, text, text)) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return invoices;
    }

    @Override
    public boolean update(Invoice invoice) { return false; }

    @Override
    public boolean delete(int id) {
        // Borrar factura (Cuidado: InvoiceDetail debería tener ON DELETE CASCADE en SQL)
        String sql = "DELETE FROM Invoice WHERE id_invoice = ?";
        try {
            int rows = CONNECTION.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting invoice: " + e.getMessage());
            return false;
        }
    }

}