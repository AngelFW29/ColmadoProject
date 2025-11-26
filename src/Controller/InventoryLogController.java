package Controller;

import DAO.InventoryLogDAO;
import Model.InventoryLog;
import Model.MovementType;

import java.time.LocalDateTime;
import java.util.List;

public class InventoryLogController {
    private final InventoryLogDAO inventoryLogDAO;

    public InventoryLogController(InventoryLogDAO inventoryLogDAO) {
        this.inventoryLogDAO = inventoryLogDAO;
    }

    public boolean addLog(int idProduct, MovementType typeMovement, int quantityChange) {
        InventoryLog log = new InventoryLog(
                0,
                idProduct,
                typeMovement,
                quantityChange,
                LocalDateTime.now()
        );
        return inventoryLogDAO.create(log);
    }

    public InventoryLog getLogById(int id) {
        return inventoryLogDAO.read(id);
    }

    public boolean updateLog(int idLog, int idProduct, MovementType typeMovement, int quantityChange, LocalDateTime movementDate) {
        InventoryLog log = new InventoryLog(
                idLog,
                idProduct,
                typeMovement,
                quantityChange,
                movementDate
        );
        return inventoryLogDAO.update(log);
    }

    public boolean deleteLog(int id) {
        return inventoryLogDAO.delete(id);
    }

    public List<InventoryLog> getAllLogs() {
        return inventoryLogDAO.findAll();
    }

    // RECOMENDACIÓN:
    // Seguramente necesitarás filtrar los movimientos por producto específico.
    // Si agregas un método 'findAllByProductId(int id)' en tu DAO, agrégalo aquí también.
}