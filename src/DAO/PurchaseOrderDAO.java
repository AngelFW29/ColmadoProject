package DAO;

import Model.PurchaseOrder;
import Model.PurchaseOrderDetail; // <--- Importante
import Model.PurchaseOrderStatus;
import Model.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDAO implements ICRUD<PurchaseOrder> {
    private final ConnectionMySQL CONNECTION;
    private final SupplierDAO supplierDAO;
    private final PurchaseOrderDetailDAO detailDAO;

    public PurchaseOrderDAO(ConnectionMySQL CONNECTION) {
        this.CONNECTION = CONNECTION;
        this.supplierDAO = new SupplierDAO();
        this.detailDAO = new PurchaseOrderDetailDAO();
    }

    @Override
    public boolean create(PurchaseOrder entity) {
        // Este método guarda la cabecera. Para guardar los detalles,
        // normalmente lo manejarás desde el "Service" (Lógica de Negocio),
        // porque necesitas el ID generado de esta orden para guardar los productos.

        String query = "INSERT INTO PurchaseOrder(id_supplier, order_date, total, status) VALUES (?, ?, ?, ?)";

        try {
            int rows = CONNECTION.executeUpdate(query,
                    entity.getSupplier().getId(),
                    entity.getOrderDate(),
                    entity.getTotal(),
                    entity.getStatus().name()
            );
            return (rows > 0);
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la orden de compra: " + e.getMessage(), e);
        }
    }

    @Override
    public PurchaseOrder read(int id) {
        String query = "SELECT * FROM PurchaseOrder WHERE id_purchase_order = ?";

        try (ResultSet rs = CONNECTION.executeQuery(query, id)) {

            if (rs.next()) {
                Supplier supplier = this.supplierDAO.read(rs.getInt("id_supplier"));
                PurchaseOrderStatus status = PurchaseOrderStatus.valueOf(rs.getString("status"));

                PurchaseOrder po = new PurchaseOrder(
                        rs.getInt("id_purchase_order"),
                        supplier,
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        status
                );

                List<PurchaseOrderDetail> details = detailDAO.findAllByOrderId(id);
                po.setDetails(details);

                return po;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar la orden: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error de datos: El estado en la BD no coincide", e);
        }

        return null;
    }

    @Override
    public boolean update(PurchaseOrder entity) {
        String query = "UPDATE PurchaseOrder SET id_supplier = ?, order_date = ?, total = ?, status = ? WHERE id_purchase_order = ?";

        try {
            int rows = CONNECTION.executeUpdate(query,
                    entity.getSupplier().getId(),
                    entity.getOrderDate(),
                    entity.getTotal(),
                    entity.getStatus().name(),
                    entity.getIdPurchaseOrder()
            );
            return (rows > 0);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la orden: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM PurchaseOrder WHERE id_purchase_order = ?";
        try {
            int rows = CONNECTION.executeUpdate(query, id);
            return (rows > 0);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la orden: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PurchaseOrder> findAll() {
        List<PurchaseOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseOrder";

        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            while (rs.next()) {
                Supplier supplier = this.supplierDAO.read(rs.getInt("id_supplier"));
                PurchaseOrderStatus status = PurchaseOrderStatus.valueOf(rs.getString("status"));

                PurchaseOrder po = new PurchaseOrder(
                        rs.getInt("id_purchase_order"),
                        supplier,
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        status
                );

                po.setDetails(detailDAO.findAllByOrderId(po.getIdPurchaseOrder()));

                orders.add(po);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las órdenes: " + e.getMessage(), e);
        }
        return orders;
    }
}