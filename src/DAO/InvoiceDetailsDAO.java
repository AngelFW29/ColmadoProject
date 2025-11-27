package DAO;

import Model.Invoice;
import Model.InvoiceDetails;
import Model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailsDAO implements ICRUD<InvoiceDetails> {

    private final ConnectionMySQL CONNECTION;
    private final ProductDAO productDAO;

    public InvoiceDetailsDAO() {
        this.CONNECTION = ConnectionMySQL.getInstance();
        this.productDAO = new ProductDAO();
    }

    @Override
    public boolean create(InvoiceDetails entity) {
        String query = "INSERT INTO InvoiceDetail (id_invoice, id_product, quantity, unit_price, sub_total) VALUES (?, ?, ?, ?, ?)";

        try {
            double subTotalCalculated = entity.getSubtTotal();

            int rows = CONNECTION.executeUpdate(query,
                    entity.getInvoice().getId(),
                    entity.getProduct().getId(),
                    entity.getQuantity(),
                    entity.getUnitPrice(),
                    subTotalCalculated
            );

            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el detalle de factura: " + e.getMessage(), e);
        }
    }

    @Override
    public InvoiceDetails read(int id) {
        String query = "SELECT * FROM InvoiceDetail WHERE id_invoice_detail = ?";

        try (ResultSet rs = CONNECTION.executeQuery(query, id)) {
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el detalle de factura: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public boolean update(InvoiceDetails entity) {
        String query = "UPDATE InvoiceDetail SET id_product = ?, quantity = ?, unit_price = ?, sub_total = ? WHERE id_invoice_detail = ?";

        try {
            double subTotalCalculated = entity.getSubtTotal();

            int rows = CONNECTION.executeUpdate(query,
                    entity.getProduct().getId(),
                    entity.getQuantity(),
                    entity.getUnitPrice(),
                    subTotalCalculated,
                    entity.getIdInvoiceDetails()
            );

            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el detalle de factura: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM InvoiceDetail WHERE id_invoice_detail = ?";

        try {
            int rows = CONNECTION.executeUpdate(query, id);
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el detalle de factura: " + e.getMessage(), e);
        }
    }

    @Override
    public List<InvoiceDetails> findAll() {
        List<InvoiceDetails> list = new ArrayList<>();
        String query = "SELECT * FROM InvoiceDetail";

        try (ResultSet rs = CONNECTION.executeQuery(query)) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los detalles de factura: " + e.getMessage(), e);
        }

        return list;
    }

    public List<InvoiceDetails> findAllByInvoiceId(int invoiceId) {
        List<InvoiceDetails> list = new ArrayList<>();
        String query = "SELECT * FROM InvoiceDetail WHERE id_invoice = ?";

        try (ResultSet rs = CONNECTION.executeQuery(query, invoiceId)) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener detalles de la factura #" + invoiceId + ": " + e.getMessage(), e);
        }

        return list;
    }

    private InvoiceDetails mapResultSetToEntity(ResultSet rs) throws SQLException {
        Product product = productDAO.read(rs.getInt("id_product"));

        InvoiceDetails detail = new InvoiceDetails(
                new Invoice(),
                product,
                rs.getInt("quantity"),
                rs.getDouble("unit_price")
        );

        detail.setIdInvoiceDetails(rs.getInt("id_invoice_detail"));
        detail.setProduct(product);

        detail.setSubtTotal(rs.getDouble("sub_total"));

        Invoice stubInvoice = new Invoice();
        stubInvoice.setId(rs.getInt("id_invoice"));
        detail.setInvoice(stubInvoice);

        return detail;
    }
}
