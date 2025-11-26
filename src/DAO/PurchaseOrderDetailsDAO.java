package DAO;

import Model.Product;
import Model.PurchaseOrder;
import Model.PurchaseOrderDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDetailsDAO implements ICRUD<PurchaseOrderDetails> {

    private final ConnectionMySQL CONNECTION;
    private final ProductDAO productDAO;

    public PurchaseOrderDetailsDAO() {
        this.CONNECTION = ConnectionMySQL.getInstance();
        this.productDAO = new ProductDAO();
    }

    @Override
    public boolean create(PurchaseOrderDetails entity) {
        String query = "INSERT INTO PurchaseOrderDetail(id_purchase_order, id_product, quantity, unit_cost, sub_total) VALUES (?, ?, ?, ?, ?)";

        try {
            double subTotalCalculated = entity.getSubTotal();

            int rows = CONNECTION.executeUpdate(query,
                    entity.getPurchaseOrder().getIdPurchaseOrder(),
                    entity.getProduct().getId(),
                    entity.getQuantity(),
                    entity.getUnitCost(),
                    subTotalCalculated
            );

            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar el detalle de compra: " + e.getMessage(), e);
        }
    }

    @Override
    public PurchaseOrderDetails read(int id) {
        String query = "SELECT * FROM PurchaseOrderDetail WHERE id_purchase_detail = ?";

        try (ResultSet rs = CONNECTION.executeQuery(query, id)) {
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el detalle de compra: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(PurchaseOrderDetails entity) {
        String query = "UPDATE PurchaseOrderDetail SET id_product = ?, quantity = ?, unit_cost = ?, sub_total = ? WHERE id_purchase_detail = ?";

        try {
            double subTotalCalculated = entity.getQuantity() * entity.getUnitCost();

            int rows = CONNECTION.executeUpdate(query,
                    entity.getProduct().getId(),
                    entity.getQuantity(),
                    entity.getUnitCost(),
                    subTotalCalculated,
                    entity.getIdPurchaseOrderDetail()
            );

            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el detalle de compra: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM PurchaseOrderDetail WHERE id_purchase_detail = ?";
        try {
            int rows = CONNECTION.executeUpdate(query, id);
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el detalle de compra: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PurchaseOrderDetails> findAll() {
        List<PurchaseOrderDetails> details = new ArrayList<>();
        String query = "SELECT * FROM PurchaseOrderDetail";

        try (ResultSet rs = CONNECTION.executeQuery(query)) {
            while (rs.next()) {
                details.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los detalles: " + e.getMessage(), e);
        }
        return details;
    }

    public List<PurchaseOrderDetails> findAllByOrderId(int orderId) {
        List<PurchaseOrderDetails> details = new ArrayList<>();
        String query = "SELECT * FROM PurchaseOrderDetail WHERE id_purchase_order = ?";

        try (ResultSet rs = CONNECTION.executeQuery(query, orderId)) {
            while (rs.next()) {
                details.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los detalles de la orden #" + orderId + ": " + e.getMessage(), e);
        }
        return details;
    }

    private PurchaseOrderDetails mapResultSetToEntity(ResultSet rs) throws SQLException {
        Product product = productDAO.read(rs.getInt("id_product"));

        PurchaseOrderDetails detail = new PurchaseOrderDetails(
                product,
                rs.getInt("quantity"),
                rs.getDouble("unit_cost")
        );

        detail.setIdPurchaseOrderDetail(rs.getInt("id_purchase_detail"));

        // Asignamos una referencia "Stub" (parcial) a la orden padre
        // para evitar un bucle infinito de lectura (Orden -> Detalles -> Orden...)
        PurchaseOrder stubOrder = new PurchaseOrder();
        stubOrder.setIdPurchaseOrder(rs.getInt("id_purchase_order"));
        detail.setPurchaseOrder(stubOrder);

        return detail;
    }
}