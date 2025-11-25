package DAO;

import Model.InventoryLog;
import Model.ProductCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogDAO implements ICRUD<InventoryLog> {
    private final ConnectionMySQL CONNECTION;

    InventoryLogDAO(){
        this.CONNECTION = ConnectionMySQL.getInstance();
    }


    @Override
    public boolean create(InventoryLog entity) throws RuntimeException{
        String query = "INSERT INTO InventoryLog(id_product, movement_type, quantity_change, movement_date) VALUES (?, ?, ?, ?)";

        try{
            int rows = CONNECTION.executeUpdate(query,
                    entity.getIdProduct(),
                    entity.getTypeMovement(),
                    entity.getQuantityChange(),
                    entity.getMovementDate()
            );

            return (rows > 0);
        }catch(SQLException e){
            throw new RuntimeException("Error al crear el registro: " + e.getMessage(), e);
        }
    }

    @Override
    public InventoryLog read(int id) {
        String query = "SELECT * FROM InventoryLog WHERE id_log = ?";

        try (ResultSet rs = CONNECTION.executeQuery(query, id)) {

            if(rs.next()){
                return new InventoryLog(
                        rs.getInt("id_log"),
                        rs.getInt("id_product"),
                        rs.getString("movement_type"),
                        rs.getInt("quantity_change"),
                        rs.getTimestamp("movement_date").toLocalDateTime()
                );
            }

        }catch (SQLException e){
            throw new RuntimeException("Error al consultar el registro: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public boolean update(InventoryLog entity) {
        String query = "UPDATE InventoryLog SET id_product = ?, movement_type = ?, quantity_change = ?, movement_date = ? WHERE id_log = ?";
        try{
            int rows = CONNECTION.executeUpdate(query,
                    entity.getIdProduct(),
                    entity.getTypeMovement(),
                    entity.getQuantityChange(),
                    entity.getMovementDate(),
                    entity.getIdLog()
            );

            return (rows > 0);
        }catch(SQLException e){
            throw new RuntimeException("Error al actualizar el registro: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM InventoryLog WHERE id_log = ?";
        try{
            int rows = CONNECTION.executeUpdate(query, id);

            return (rows > 0);
        }catch(SQLException e){
            throw new RuntimeException("Error al eliminar el registro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<InventoryLog> findAll() {
        List<InventoryLog> inventories = new ArrayList<>();
        String sql = "SELECT * FROM InventoryLog";

        try (ResultSet rs = CONNECTION.executeQuery(sql)) {
            while (rs.next()) {
                inventories.add(new InventoryLog(
                        rs.getInt("id_log"),
                        rs.getInt("id_product"),
                        rs.getString("movement_type"),
                        rs.getInt("quantity_change"),
                        rs.getTimestamp("movement_date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las categorías: " + e.getMessage(), e);
        }
        return inventories;
    }
}
