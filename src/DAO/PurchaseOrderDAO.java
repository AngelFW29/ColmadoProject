package DAO;

import Model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDAO implements ICRUD<PurchaseOrder> {
    private final ConnectionMySQL CONNECTION;
    private final SupplierDAO supplierDAO;
    private final PurchaseOrderDetailsDAO detailDAO;

    public PurchaseOrderDAO() {
        this.CONNECTION = ConnectionMySQL.getInstance();
        this.supplierDAO = new SupplierDAO();
        this.detailDAO = new PurchaseOrderDetailsDAO();
    }

    @Override
    public boolean create(PurchaseOrder entity) {
        String query = "INSERT INTO PurchaseOrder(id_supplier, order_date, total, status) VALUES (?, ?, ?, ?)";

        try {
            int rows = CONNECTION.executeUpdate(query,
                    entity.getSupplier().getId(),
                    entity.getOrderDate(),
                    entity.getTotal(),
                    entity.getStatus().getDbValue()
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

                String statusStr = rs.getString("status");
                PurchaseOrderStatus status = PurchaseOrderStatus.PENDIENTE;
                for (PurchaseOrderStatus s : PurchaseOrderStatus.values()) {
                    if (s.getDbValue().equalsIgnoreCase(statusStr)) {
                        status = s;
                        break;
                    }
                }

                PurchaseOrder po = new PurchaseOrder(
                        rs.getInt("id_purchase_order"),
                        supplier,
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        status
                );
                po.setTotal(rs.getDouble("total"));

                List<PurchaseOrderDetails> details = detailDAO.findAllByOrderId(id);
                po.setDetails(details);

                return po;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar la orden: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<PurchaseOrder> findAll() {
        List<PurchaseOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseOrder";

        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar órdenes: " + e.getMessage(), e);
        }
        return orders;
    }

    private PurchaseOrder mapResultSet(ResultSet rs) throws SQLException {
        Supplier supplier = this.supplierDAO.read(rs.getInt("id_supplier"));

        String statusStr = rs.getString("status");
        PurchaseOrderStatus status = PurchaseOrderStatus.PENDIENTE;

        for (PurchaseOrderStatus s : PurchaseOrderStatus.values()) {
            if (s.getDbValue().equalsIgnoreCase(statusStr)) {
                status = s;
                break;
            }
        }

        PurchaseOrder po = new PurchaseOrder(
                rs.getInt("id_purchase_order"),
                supplier,
                rs.getTimestamp("order_date").toLocalDateTime(),
                status
        );

        po.setTotal(rs.getDouble("total"));

        return po;
    }

    public int getLastInsertedId() {
        String sql = "SELECT MAX(id_purchase_order) AS last_id FROM PurchaseOrder";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

    // firstStatsPanel PurchaseOrder
    public int countPurchaseOrder() {
        String sql = "SELECT COUNT(*) AS total FROM PurchaseOrder";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // secondStatsPanel PurchaseOrder
    public int pendingOrder() {
        String sql = "SELECT COUNT(*) AS total_Pendiente FROM PurchaseOrder WHERE status = 'Pendiente'";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total_Pendiente");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // thirdStatsPanel PurchaseOrder
    public int receivedOrder() {
        String sql = "SELECT COUNT(*) AS total_Recibido FROM PurchaseOrder WHERE status = 'Recibido'";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total_Recibido");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<PurchaseOrder> searchPurchaseOrder(String filter) {
        List<PurchaseOrder> orders = new ArrayList<>();
        String sql = """ 
                SELECT p.*, s.name AS supplier_name
                FROM PurchaseOrder p
                JOIN Person s ON p.id_supplier = s.id_person
                WHERE s.name LIKE ?
                   OR CAST(p.id_purchase_order AS CHAR) LIKE ?
                   OR p.status LIKE ?
                """;

        String text = "%" + filter + "%";

        try (ResultSet rs = CONNECTION.executeQuery(sql, text, text, text)) {
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return orders;
    }

}