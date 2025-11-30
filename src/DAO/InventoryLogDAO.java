package DAO;

import Model.InventoryLog;
import Model.MovementType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogDAO implements ICRUD<InventoryLog> {
    private final ConnectionMySQL CONNECTION;

    public InventoryLogDAO() {
        this.CONNECTION = ConnectionMySQL.getInstance();
    }

    @Override
    public boolean create(InventoryLog entity) {
        String query = "INSERT INTO InventoryLog(id_product, movement_type, quantity_change, movement_date) VALUES (?, ?, ?, ?)";

        try {
            int rows = CONNECTION.executeUpdate(query,
                    entity.getIdProduct(),
                    entity.getMovementType().name(),
                    entity.getQuantityChange(),
                    entity.getMovementDate()
            );
            return (rows > 0);
        } catch (SQLException e) {
            throw new RuntimeException("Error SQL al crear Log: " + e.getMessage(), e);
        }
    }

    @Override
    public InventoryLog read(int id) {
        String query = "SELECT * FROM InventoryLog WHERE id_log = ?";
        try (ResultSet rs = CONNECTION.executeQuery(query, id)) {
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error SQL al leer Log: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<InventoryLog> findAll() {
        List<InventoryLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM InventoryLog";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error SQL al listar Logs: " + e.getMessage(), e);
        }
        return logs;
    }

    private InventoryLog mapResultSet(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("movement_type");
        MovementType type;
        try {
            type = MovementType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            type = MovementType.valueOf(typeStr.substring(0, 1).toUpperCase() + typeStr.substring(1).toLowerCase());
        }

        return new InventoryLog(
                rs.getInt("id_log"),
                rs.getInt("id_product"),
                type,
                rs.getInt("quantity_change"),
                rs.getTimestamp("movement_date").toLocalDateTime()
        );
    }

    @Override
    public boolean update(InventoryLog entity) {
        String query = "UPDATE InventoryLog SET id_product = ?, movement_type = ?, quantity_change = ?, movement_date = ? WHERE id_log = ?";

        try {
            int rows = CONNECTION.executeUpdate(query,
                    entity.getIdProduct(),
                    entity.getMovementType().name(),
                    entity.getQuantityChange(),
                    entity.getMovementDate(),
                    entity.getIdLog()
            );
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el log de inventario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM InventoryLog WHERE id_log = ?";
        try {
            int rows = CONNECTION.executeUpdate(sql, id);
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar log: " + e.getMessage(), e);
        }
    }

    //  Stats Panels result
    public int countTotalMovements() {
        String sql = "SELECT COUNT(*) AS total FROM InventoryLog";
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countEntries() {
        String sql = """
                SELECT COUNT(*) AS entradas
                FROM InventoryLog
                WHERE movement_type = 'Compra'
                   OR (movement_type = 'Ajuste' AND quantity_change > 0)
                """;
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("entradas");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countExits() {
        String sql = """
                SELECT COUNT(*) AS salidas
                FROM InventoryLog
                WHERE movement_type = 'Venta'
                   OR movement_type = 'Perdida'
                   OR (movement_type = 'Ajuste' AND quantity_change < 0)
                """;
        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("salidas");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public List<InventoryLog> searchInventories(String filter) {
        List<InventoryLog> logs = new ArrayList<>();

        String sql = """ 
                SELECT * FROM InventoryLog 
                WHERE CAST(id_log AS CHAR) LIKE ?
                OR CAST(id_product AS CHAR) LIKE ?
                OR movement_type LIKE ?
                OR CAST(quantity_change AS CHAR) LIKE ?
                """;

        String text = "%" + filter + "%";

        try (ResultSet rs = CONNECTION.executeQuery(sql, text, text, text, text)) {
            while (rs.next()) {
                logs.add(mapResultSet(rs));
            }
        } catch (Exception ex) {
            System.err.println("Error en filtro de inventario: " + ex.getMessage());
            ex.printStackTrace();
        }
        return logs;
    }
}